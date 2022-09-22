package com.nlteck.firmware;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment.ControlUnitListener;
import com.nlteck.ParameterName;
import com.nlteck.i18n.I18N;
import com.nlteck.listener.LogicListener;
import com.nlteck.service.DataManager;
import com.nlteck.service.FileSaveService;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.logic.LogicEnvironment.StepAndLoop;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectData;
import com.nltecklib.protocol.li.logic2.Logic2PoleData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.ExChnsOperateData;
import com.nltecklib.protocol.li.main.JsonProcedureExData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.li.main.StartupData;

/**
 * 实际控制分区
 * 
 * @author Administrator
 *
 */
public class ControlUnit {

	private int index;

	/****************************** 实际内部控制单元 ************************************/
	private List<LogicBoard> logics = new ArrayList<LogicBoard>();
	private List<CheckBoard> checks = new ArrayList<CheckBoard>();
	private List<DriverBoard> drivers = new ArrayList<DriverBoard>(); // 当分区控制最小单元为驱动板时生效
	/********************************************************************************/

	private DataManager offlineManager; // 离线数据管理器
	private boolean tempAlert; // 是否处于温度超限报警
	private boolean syncPause; // 是否处于同步温度暂停状态

	private AlertCode alertCode = AlertCode.NORMAL; // 当前分区报警码
	private Map<LogicBoard, LogicListener> listeners = new HashMap<LogicBoard, LogicListener>();

	// 流程
	private ProcedureData procedure;
	
	// AG分区保护方案名
	private ParameterName pnAG = new ParameterName(WorkType.AG);
	private ParameterName pnAGRetest = new ParameterName(WorkType.AG , true);
	// IC分区保护方案
	private ParameterName pnIC = new ParameterName(WorkType.IC);
	private ParameterName pnICRetest = new ParameterName(WorkType.IC , true);
	
	//超压保护
	private DeviceProtectData  dpd = new DeviceProtectData();	
	//极性保护
	private PoleData           pole = new PoleData();
	//接触保护
	private CheckVoltProtectData  touch = new CheckVoltProtectData();
	//首尾保护
	private StartEndCheckData   sec = new StartEndCheckData();
	

	private String testName = ""; // 测试名
	private State state = State.NORMAL;
	private MainBoard mainBoard; // 主控板
	private List<ControlUnitListener> unitListeners = new ArrayList<ControlUnitListener>();
	private State operateState = State.NORMAL; // 操作意向
	private Date opertateDate; // 操作时间

	// 包含的逻辑板序号集合
	private List<Integer> logicIndexs = new ArrayList<Integer>();

	// 同步步次
	private Step syncStep; // 当前需要同步的流程步次

	// 定期轮询查询分区状态线程
	private ScheduledExecutorService scheduleExecutor;

	private boolean pressureComplete;
	//压力到达超时时间，超时则转下一步
	private int pressureCompleteTimeout;
	
	private boolean  pressureChanged; //是否压力发生了变更?
	
	/**
	 * 智能保护采集样本
	 */
	private List<SmartPickupData> sampleCaches = Collections.synchronizedList(new LinkedList<SmartPickupData>()); //智能保护需要采集的电压缓存


	private Logger logger;

	/**
	 * 操作对象
	 * 
	 * @author Administrator
	 *
	 */
	public static class OperateObj {

		public Channel chn;

	}

	/**
	 * 统计数据
	 * 
	 * @return
	 */
	public static class Statistics {

		public int charge;
		public int discharge;
		public int sleep;
		public int alert;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<LogicBoard> getLogics() {
		return logics;
	}

	public void setLogics(List<LogicBoard> logics) {
		this.logics = logics;
	}

	public List<CheckBoard> getChecks() {
		return checks;
	}

	public void setChecks(List<CheckBoard> checks) {
		this.checks = checks;
	}

	public int getChannelCount() {

		return drivers.size() * MainBoard.startupCfg.getDriverChnCount();
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
		System.out.println("set test name = " + testName);
	}

	public ControlUnit(MainBoard mb, int index) {

		if (index == -1 || index == 0xff) {

			index = 0;
		}
		this.index = index;
		this.mainBoard = mb;
		this.pnAG = new ParameterName(WorkType.AG);
		this.pnIC = new ParameterName(WorkType.IC);
		try {
			logger = LogUtil.createLog("log/controlunit" + index + ".log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static WorkType getWorkTypeByName(String name) {

		if (CommonUtil.isNullOrEmpty(name)) {

			return WorkType.AG;
		}
		String[] secs = name.split("\\-");
		return secs[0].equals("IC") ? WorkType.IC : WorkType.AG;

	}

	/**
	 * 控制分区初始化
	 * 
	 * @throws AlertException
	 */
	public void init() throws AlertException {

		loadAllParamsFromFile();

	}

	public void saveAllProtections() throws AlertException {

		// ConfigManager.writeProtectFile("config");
	}

	/**
	 * 从硬盘加载所有配置文件
	 * 
	 * @throws AlertException
	 */
	public void loadAllParamsFromFile() {
		
		//如无流程，使用默认保护配置
		//加载超压保护
		File file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/device.xml");
        if (file.exists()) {
            
			try {
			    dpd = Context.getFileSaveService().readDeviceProtectFile(file);
			} catch (AlertException ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		}
        
      //加载极性保护
        file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/pole.xml");
        if (file.exists()) {
            
			try {
			    pole = Context.getFileSaveService().readPoleFile(file);
			} catch (AlertException ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		}
		
		

		file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/procedure.xml");
		if (file.exists()) {

			try {
				JsonProcedureExData jsonPd = FileSaveService.readProcedureExFile(file.getPath());
				procedure = jsonPd.getProcedureData();
				if(jsonPd.getDeviceProtect() != null) {
					
				   setDpd(jsonPd.getDeviceProtect());
				}
                if(jsonPd.getCheckProtect() != null) {
					
					setTouch(jsonPd.getCheckProtect());
				}
                if(jsonPd.getFirstEndProtect() != null) {
					
					setSec(jsonPd.getFirstEndProtect());
				}
                System.out.println(jsonPd);

			} catch (Exception ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(new AlertException(AlertCode.LOGIC,ex.getMessage()));
			}
		} 
		
			
		/*file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/protection_AG.xml");
		if (file.exists()) {

			try {
				pnAG = FileSaveService.readProtectFile(file.getPath());
			} catch (AlertException ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		} else {

			pnAG = new ParameterName(WorkType.AG);
			pnAG.getCheckVoltProtect()
					.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
		}

		logger.info("load ag protection:" + pnAG.getName());
		
		
		file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/protection_AG_retest.xml");
		if (file.exists()) {

			try {
				pnAGRetest = FileSaveService.readProtectFile(file.getPath());
			} catch (AlertException ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		} else {

			pnAGRetest = new ParameterName(WorkType.AG,true);
			pnAGRetest.getCheckVoltProtect()
					.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
		}

		logger.info("load ag retest protection:" + pnAGRetest.getName());
		

		file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/protection_IC.xml");
		if (file.exists()) {
            
			try {
			    pnIC = FileSaveService.readProtectFile(file.getPath());
			} catch (AlertException ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		} else {

			pnIC = new ParameterName(WorkType.IC);
			pnIC.getCheckVoltProtect()
					.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
		}

		logger.info("load ic protection:" + pnIC.getName());
		
		file = new File(
				"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/protection_IC_retest.xml");
		if (file.exists()) {
            
			try {
			    pnICRetest = FileSaveService.readProtectFile(file.getPath());
			} catch (AlertException ex) {

				ex.printStackTrace();
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		} else {

			pnICRetest = new ParameterName(WorkType.IC);
			pnICRetest.getCheckVoltProtect()
					.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
		}

		logger.info("load ic retest protection:" + pnICRetest.getName());*/
		
		

	}

	public ProcedureData getProcedure() {
		return procedure;
	}

	public void setProcedure(ProcedureData procedure) {
		this.procedure = procedure;
	}

	public MainBoard getMainBoard() {
		return mainBoard;
	}

	public Step getProcedureStep(int stepIndex) {

		if (procedure == null) {
			return null;
		}
		if(stepIndex < 1 || stepIndex > procedure.getStepCount()) {
			
			return null;
		}
		return procedure.getStep(stepIndex - 1);
	}

	public int getProcedureStepCount() {

		if (procedure == null) {
			return 0;
		}
		return procedure.getStepCount();
	}

	/**
	 * 获取状态
	 * 
	 * @return
	 */
	public State getState() {

		return state;
	}

	public List<Integer> getLogicIndexs() {
		return logicIndexs;
	}

	public void setLogicIndexs(List<Integer> logicIndexs) {
		this.logicIndexs = logicIndexs;
	}

	/**
	 * 启动前的检测
	 * 
	 * @return
	 * @throws AlertException
	 */
	private void startupCheck() throws AlertException {

		if (getProcedure() == null) {

			// throw new AlertException(AlertCode.INIT, "启动分区" + (index + 1) + "无流程");
			throw new AlertException(AlertCode.INIT, I18N.getVal(I18N.StartUnitNoProcess, index + 1));
		}
		if (getProcedure().getStepCount() == 0) {

			// throw new AlertException(AlertCode.INIT, "启动分区" + (index + 1) + "流程0步次");
			throw new AlertException(AlertCode.INIT, I18N.getVal(I18N.StartUnitProcessSize, index + 1, 0));
		}

		// 如果是同步模式,则开始记录同步步次
		if (getProcedure().getStepMode() == StepMode.SYNC) {

			if (getState() != State.PAUSE) {

				syncStep = null; // 清空步次
				setSyncStep(findNextSyncStep()); // 设置当前分区的将要同步的流程步次
			}

		}

	}

	/**
	 * 刷新分区状态
	 */
	public void refreshState() {

	}

	public void stimulatePowerOff() {

		// 模拟断电
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				// 模拟断电
				Context.getCoreService().powerOff();

			}

		}, new Date(new Date().getTime() + 30000));
	}

	public ProcedureMode getProcedureMode() {

		return mainBoard.getProcedureMode();
	}

	public void executeChannel(Channel chn, State optState) {

		switch (optState) {

		case STARTUP:
			// 启动
			chn.startupStep();
			break;
		case FORMATION:
			// 恢复
			chn.resumeStep(true);
			break;
		case PAUSE:
			chn.pauseStep();
			break;
		case STOP:
			chn.stopStep();
			break;
		default:
			break;
		}

	}

	public ParameterName getPnAG() {
		return pnAG;
	}

	public void setPnAG(ParameterName pn) {
		this.pnAG = pn;
	}

	public ParameterName getPnIC() {
		return pnIC;
	}

	public void setPnIC(ParameterName pnIC) {
		this.pnIC = pnIC;
	}

	public void addControlUnitListener(ControlUnitListener listener) {

		unitListeners.add(listener);
	}

	public void rmvControlUnitListener(ControlUnitListener listener) {

		unitListeners.remove(listener);

	}

	public State getOperateState() {
		return operateState;
	}

	public void setOperateState(State operateState) {
		this.operateState = operateState;
	}

	

	public boolean isTempAlert() {
		return tempAlert;
	}

	public void setTempAlert(boolean tempAlert) {
		this.tempAlert = tempAlert;
	}

	public boolean isSyncPause() {
		return syncPause;
	}

	public void setSyncPause(boolean syncPause) {
		this.syncPause = syncPause;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isBoardConnected(StringBuffer err) {

		for (LogicBoard lb : logics) {

			if (MainBoard.startupCfg.getLogicInfo(lb.getLogicIndex()).use && !lb.isBoardConnected()) {

				// err.append("逻辑板" + (lb.getLogicIndex() + 1) + "已通信中断,请检查!");
				err.append(I18N.getVal(I18N.LogicBoardCommunicateInterrupted_PleaseCheck, lb.getLogicIndex() + 1));
				return false;
			}
		}
		/*for (CheckBoard check : checks) {

			if (MainBoard.startupCfg.getCheckInfo(check.getCheckIndex()).use && !check.isBoardConnected()) {

				// err.append("回检板" + (check.getCheckIndex() + 1) + "已通信中断,请检查!");
				err.append(I18N.getVal(I18N.CheckBoardCommunicateInterrupted_PleaseCheck, check.getCheckIndex() + 1));
				return false;
			}
		}*/

		return true;

	}

	// public void saveChnOfflineData(Channel chn , boolean append) {
	//
	// if (!append) {
	// offlineManager = new DataManager("./struct/offline.xml");
	// }
	// offlineManager.saveChnOfflineData(chn, chn.getOfflineCaches());
	// offlineManager.saveFile();
	// }

	public AlertCode getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(AlertCode alertCode) {
		this.alertCode = alertCode;
	}

	private Step findNextSyncStep() {

		Step nextStep = syncStep;
		if (nextStep == null) {

			nextStep = procedure.getStep(0); // 第1步次
			nextStep.loopIndex = 1;
			if (nextStep.workMode != WorkMode.SLEEP) {

				return nextStep;
			}
		}

		// 寻找下一个同步步次!
		do {

			StepAndLoop slp = LogicBoard.skipNextStep(procedure, nextStep.stepIndex, nextStep.loopIndex);
			if (slp.nextStep > procedure.getStepCount()) {

				setSyncStep(null);
				return null;
			}
			nextStep = procedure.getStep(slp.nextStep - 1);
			nextStep.loopIndex = slp.nextLoop;

		} while (nextStep != null && nextStep.workMode == WorkMode.SLEEP);

		return nextStep;

	}

	/**
	 * 监视整区是否都已经进入同步状态，如果满足条件则给每个同步状态的通道发送跳转指令
	 * 
	 * @return
	 */
	public synchronized void updateSyncWaitState() {

		if (state != State.FORMATION) {
			return;
		}
		if (getProcedure() != null && getProcedure().getStepMode() == StepMode.ASYNC) {
			return;
		}
		if (syncStep == null) {

			return;
		}

		List<Channel> channels = listAllChannels(ChnState.PAUSE);

		if (!channels.isEmpty()) {

			return;
		}
		// 所有的运行通道
		channels = listAllChannels(ChnState.RUN);

		for (Channel chn : channels) {

			if (!chn.isSyncWaitForSkip()) {

				return; // 未满足分区内所有通道同步请求
			}

		}

		logger.info("all controlunit " + getIndex() + " synchronized!");

		Step nextStep = findNextSyncStep();

		logger.info("ok,skip next sync step " + nextStep);
		setSyncStep(nextStep);
		// 取消同步状态，开始转步次
		for (Channel chn : channels) {

			chn.setSyncWaitForSkip(false);

		}

	}

	/**
	 * 紧急暂停流程
	 * 
	 * @return
	 * @throws AlertException
	 */
	public void emergencyPause() throws AlertException {

		if (state == State.FORMATION) {

			clearAllOperations(); // 清空当前所有的操作
			StartupData sd = new StartupData();
			sd.setState(State.PAUSE);

		}

	}

	/**
	 * 清除所有正在执行的操作
	 */
	public void clearAllOperations() {

		for (Channel chn : listAllChannels(null)) {

			chn.clearOperates();

		}

	}


	/**
	 * 以驱动板作为最小控制单元创建的分区
	 * 
	 * @author wavy_zheng 2020年7月14日
	 * @param mb
	 * @param unitIndex
	 * @param boards
	 * @return
	 */
	public static ControlUnit createUnit(MainBoard mb, int unitIndex, DriverBoard... boards) {

		ControlUnit unit = new ControlUnit(mb, unitIndex);
		unit.drivers = Arrays.asList(boards);
		return unit;

	}

	public void stopMonitorUnitState() {

		if (scheduleExecutor != null) {

			scheduleExecutor.shutdown();
			scheduleExecutor = null;
		}
	}

	public boolean containsDriver(DriverBoard db) {

		return drivers.contains(db);
	}

	/**
	 * 在设备意外断电情况下对所有通道进行暂停操作
	 */
	public void poweroffPause() {

		// 直接修改状态
		List<Channel> list = listAllChannels(ChnState.RUN);
		for (Channel chn : list) {

			chn.setState(ChnState.PAUSE);
		}
		if (state == State.FORMATION) {

			state = State.PAUSE;
		}
	}

	/**
	 * 例举该分区下所有的通道
	 * 
	 * @author wavy_zheng 2020年7月15日
	 * @param chnState
	 *            null 例举所有通道 ；否则例举符合通道状态的通道
	 * @return
	 */
	public List<Channel> listAllChannels(ChnState chnState) {

		List<Channel> list = new ArrayList<>();
		for (int n = 0; n < drivers.size(); n++) {

			for (int i = 0; i < drivers.get(n).getChannelCount(); i++) {

				if (chnState == null || chnState == drivers.get(n).getChannel(i).getState()) {
					list.add(drivers.get(n).getChannel(i));
				}
			}
		}
		return list;
	}

	/**
	 * 无任何运行缓存数据了?
	 * 
	 * @author wavy_zheng 2020年12月10日
	 * @return
	 */
	public boolean isAllChnRuntimeCachesOver() {

		List<Channel> chns = listAllChannels(null);
		for (Channel chn : chns) {

			synchronized (chn.getRuntimeCaches()) { // 防止历遍时被修改
				for (ChannelData chnData : chn.getRuntimeCaches()) {

					if (chnData.getState() == ChnState.RUN) { // 还有运行数据

						return false;
					}

				}
			}

		}

		return true;
	}

	public List<DriverBoard> getDrivers() {
		return drivers;
	}
	
	public void writeProcedureExFile(JsonProcedureExData procedure) throws Exception {
		
		if (procedure != null) {

			this.setProcedure(procedure.getProcedureData());
			if(procedure.getDeviceProtect() != null) {
			    this.setDpd(procedure.getDeviceProtect());
			}
			
			this.setSec(procedure.getFirstEndProtect());
			this.setTouch(procedure.getCheckProtect());
			
			Context.getFileSaveService().writeProcedureExFile(new File(
					"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/procedure.xml"),
					procedure);
		}
		
	}

	public void writeProcedureFile(ProcedureData procedure) throws AlertException {

		if (procedure != null) {
			this.procedure = procedure;
			Context.getFileSaveService().writeProcedureFile(new File(
					"./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index) + "/procedure.xml"),
					procedure);
		}
	}

	public void writeProtectionFile(ParameterName pn) throws AlertException {
       
		ParameterName pnWrite = null;
		if(pn == null) {
			
			return;
		}
		if(pn.getWorkType() == WorkType.AG) {
			
			if(pn.isDefaultPlan()) {
				
				pnWrite = this.pnAGRetest;
			} else {
				
				pnWrite = this.pnAG;
			}
		} else {
			
            if(pn.isDefaultPlan()) {
				
				pnWrite = this.pnICRetest;
			} else {
				
				pnWrite = this.pnIC;
			}
			
		}
		
		pnWrite.setName(pn.getName());
		pnWrite.setWorkType(pn.getWorkType());
		pnWrite.setDefaultPlan(pn.isDefaultPlan());
		if (pn.getDeviceProtect() != null) {

			pnWrite.setDeviceProtect(pn.getDeviceProtect());
		}
		if (pn.getCcProtect() != null) {

			pnWrite.setCcProtect(pn.getCcProtect());
		}
		if (pn.getCvProtect() != null) {

			pnWrite.setCvProtect(pn.getCvProtect());
		}
		if (pn.getDcProtect() != null) {

			pnWrite.setDcProtect(pn.getDcProtect());
		}
		if (pn.getPoleProtect() != null) {

			pnWrite.setPoleProtect(pn.getPoleProtect());
		}
		if (pn.getFirstCCProtect() != null) {

			pnWrite.setFirstCCProtect(pn.getFirstCCProtect());
		}
		if (pn.getCheckVoltProtect() != null) {

			pnWrite.setCheckVoltProtect(pn.getCheckVoltProtect());
		}
		if (pn.getSlpProtect() != null) {

			pnWrite.setSlpProtect(pn.getSlpProtect());
		}
		if (pn.getStartEndCheckProtect() != null) {

			pnWrite.setStartEndCheckProtect(pn.getStartEndCheckProtect());
		}
		Context.getFileSaveService().writeProtectFile(
				new File("./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index)
						+ "/protection_" + pnWrite.getWorkType().name() + (pnWrite.isDefaultPlan() ? "_retest" : "") +  ".xml"),
				pnWrite);
		
		/*ParameterName pnWrite = pn.getWorkType() == WorkType.AG ? this.pnAG : this.pnIC;
		if (pn != null) {

			if (pnWrite == null) {

				if (pn.getWorkType() == WorkType.AG) {

					this.pnAG = pn;
				} else {

					this.pnIC = pn;
				}
				pnWrite = pn;
			} else {
				pnWrite.setName(pn.getName());
				pnWrite.setWorkType(pn.getWorkType());
				pnWrite.setDefaultPlan(pn.isDefaultPlan());
				if (pn.getDeviceProtect() != null) {

					pnWrite.setDeviceProtect(pn.getDeviceProtect());
				}
				if (pn.getCcProtect() != null) {

					pnWrite.setCcProtect(pn.getCcProtect());
				}
				if (pn.getCvProtect() != null) {

					pnWrite.setCvProtect(pn.getCvProtect());
				}
				if (pn.getDcProtect() != null) {

					pnWrite.setDcProtect(pn.getDcProtect());
				}
				if (pn.getPoleProtect() != null) {

					pnWrite.setPoleProtect(pn.getPoleProtect());
				}
				if (pn.getFirstCCProtect() != null) {

					pnWrite.setFirstCCProtect(pn.getFirstCCProtect());
				}
				if (pn.getCheckVoltProtect() != null) {

					pnWrite.setCheckVoltProtect(pn.getCheckVoltProtect());
				}
				if (pn.getSlpProtect() != null) {

					pnWrite.setSlpProtect(pn.getSlpProtect());
				}
				if (pn.getStartEndCheckProtect() != null) {

					pnWrite.setStartEndCheckProtect(pn.getStartEndCheckProtect());
				}

			}
			Context.getFileSaveService().writeProtectFile(
					new File("./config/struct/" + (mainBoard.getControlUnitCount() == 1 ? "device" : index)
							+ "/protection_" + pnWrite.getWorkType().name() + ".xml"),
					pnWrite);

		}*/
	}

	public void writeTestNameFile(String testName) throws AlertException {

		this.testName = testName;
		Context.getFileSaveService().writeStructFileTestName();

	}

	public Step getSyncStep() {
		return syncStep;
	}

	public void setSyncStep(Step syncStep) {
		this.syncStep = syncStep;
	}

	public void log(String msg) {

		logger.info(msg);
	}

	public void clearNetworkState() {

		for (LogicBoard lb : logics) {

			lb.revcPushResponseData();
		}

	}

	public Date getOpertateDate() {
		return opertateDate;
	}

	public void setOpertateDate(Date opertateDate) {
		this.opertateDate = opertateDate;
	}

	public boolean isAgWorkType() {

		if (procedure == null) {

			return true;
		}
       
		if(!Data.isUseProcedureWorkType()) {
			String wt = procedure.getName().split("\\-")[0];
			return !wt.equalsIgnoreCase("IC");
		} else {
			
			return procedure.getWorkType() == WorkType.AG;
		}

	}
	
	public boolean isRetest() {
		
		if (procedure == null) {

			return false;
		}
		String[] secs = procedure.getName().split("\\-");
		if(secs.length < 2) {
			
			return false;
		}
		
		return secs[secs.length - 2].equalsIgnoreCase("R");

		
	}
	

	/**
	 * 获取当前的保护方案
	 * 
	 * @author wavy_zheng 2020年12月29日
	 * @return
	 */
	public ParameterName getCurrentPn() {
        
		boolean ag = isAgWorkType();
		boolean retest = isRetest();
		
		if(ag) {
			
			return pnAG;
			//return retest ? pnAGRetest : pnAG;
		} else {
			
			//return retest ? pnICRetest : pnIC;
			return pnIC;
		}
	}

	/**
	 * 写入必要保护系数
	 * 
	 * @author wavy_zheng 2021年1月9日
	 * @throws AlertException
	 */
	public void writeBaseProtections() throws AlertException {
       
		logger.info("write base protections:" + this.dpd + "," + this.pole);
		try {
		   Context.getCoreService().writeVoltageProtection(this, this.dpd);
		} finally {
			
			Context.getCoreService().writePoleProtection(this, this.pole);
		}
			
		
	}

	public DeviceProtectData getDpd() {
		return dpd;
	}

	public void setDpd(DeviceProtectData dpd) {
		this.dpd = dpd;
	}

	public PoleData getPole() {
		return pole;
	}

	public void setPole(PoleData pole) {
		
		this.pole = pole;
	}

	public boolean isPressureComplete() {
		return pressureComplete;
	}

	public void setPressureComplete(boolean pressureComplete) {
		this.pressureComplete = pressureComplete;
	}
	public void setPressureCompleteParam(boolean pressureComplete, int timeout) {
		this.pressureComplete = pressureComplete;
		this.pressureCompleteTimeout = timeout;
	}

	public int getPressureCompleteTimeout() {
		return pressureCompleteTimeout;
	}

	public void setPressureCompleteTimeout(int pressureCompleteTimeout) {
		this.pressureCompleteTimeout = pressureCompleteTimeout;
	}

	public boolean isPressureChanged() {
		return pressureChanged;
	}

	public void setPressureChanged(boolean pressureChanged) {
		this.pressureChanged = pressureChanged;
	}
	
	public void appendSample(SmartPickupData pickupData) {
		
		this.sampleCaches.add(pickupData);
	}
	
	public void clearSample() {
		
		this.sampleCaches.clear();
	}
	
	
	public List<SmartPickupData> getSamples() {
		
		return this.sampleCaches;
	}

	public CheckVoltProtectData getTouch() {
		return touch;
	}

	public void setTouch(CheckVoltProtectData touch) {
		this.touch = touch;
	}

	public StartEndCheckData getSec() {
		return sec;
	}

	public void setSec(StartEndCheckData sec) {
		this.sec = sec;
	}
	
	
	
	
	
}
