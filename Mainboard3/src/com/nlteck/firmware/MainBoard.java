package com.nlteck.firmware;

import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.ParameterName;
import com.nlteck.RunningLamp;
import com.nlteck.constant.Constant;
import com.nlteck.controller.Controller;
import com.nlteck.firmware.ControlUnit.Statistics;
import com.nlteck.i18n.I18N;
import com.nlteck.service.AbsLogicBoardService;
import com.nlteck.service.CoreService;
import com.nlteck.service.DataManager;
import com.nlteck.service.STMDeviceController;
import com.nlteck.service.StartupCfgManager;
import com.nlteck.service.UpgradeManager;
import com.nlteck.service.accessory.DoorAlertManager;
import com.nlteck.service.accessory.FanManager;
import com.nlteck.service.accessory.MechanismManager;
import com.nlteck.service.accessory.PowerManager;
import com.nlteck.service.accessory.ProbeManager;
import com.nlteck.service.accessory.SmogAlertManager;
import com.nlteck.service.accessory.TemperatureManager;
import com.nlteck.service.accessory.controller.StateLightController;
import com.nlteck.service.alert.AudioLightAlarmController;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.FileUtil;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.BeepAlertData;
import com.nltecklib.protocol.li.main.ControlUnitData;
import com.nltecklib.protocol.li.main.DriverChnIndexDefineData;
import com.nltecklib.protocol.li.main.EnergySaveData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.CoreData;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.OfflineRunningData;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.TempData;
import com.nltecklib.protocol.li.main.UnitChannelSwitchData;
import com.nltecklib.protocol.li.main.VoiceAlertData;
import com.rm5248.serial.SerialPort;

/**
 * 动力电池主控 3代平台
 * @author wavy_zheng
 * 2022年1月12日
 *
 */
public abstract class MainBoard {

	public final static int LOGIC_COUNT = 1; // 逻辑板数量
	public final static int CHECK_COUNT = 1; // 回检板数量
	public final static int CAL_COUNT = 1; // 校准板数量

	public static int printDebugLog = 0; // 通信数据打印;不启用，会占用IO口，使性能大幅度下降；故删除掉配置
	public static final boolean specialProcessOffset = true; // 特殊处理电压电流
	public static final double maxPrecideOffset = 10.0; // 最大精度偏差值
	public static final double maxOffsetUpper = 50; // 最大偏差报警

	
	protected State state = State.NORMAL; // 设备状态
	protected boolean readChnState; // 是否已经读到掉电保存的数据?
    


	public final static String PROCEDURE_PATH = "./config/main/procedure.xml";
	public final static String RUNTIME_PATH = "./config/struct/runtime.xml";
	public final static String OFFLINE_PATH = "./config/struct/offline.xml";
	public final static String CFG_PATH = "./config/cfg.xml";
	public final static String PARAMETER_PATH = "./config/parameter.xml";
	public final static String TEMP_PATH = "./config/tempControl.xml";
	public final static String OFFLINE_RUNNING_PATH = "./config/offlineRunning.xml";
	public final static String BEEP_ALERT_PATH = "./config/beep.xml";
    
	//上个版本:"2.0.33.20210225"
	public final static String VERSION = "3.0.49.20220810"; // 软件版本号

	protected LinkedBlockingQueue<Decorator> sendQueue = new LinkedBlockingQueue<Decorator>();
	protected LinkedBlockingQueue<Decorator> recvQueue = new LinkedBlockingQueue<Decorator>();
	// 离线或报警日志缓存队列，在PC确认回复后再发送下一条
	protected Queue<Decorator> alertBuff = new ConcurrentLinkedQueue<Decorator>();

	//protected List<LogicBoard> logicBoards = new ArrayList<LogicBoard>(LOGIC_COUNT);
	//protected List<CheckBoard> checkBoards = new ArrayList<CheckBoard>(CHECK_COUNT);
	
	protected List<DriverBoard> driverBoards = new ArrayList<>();

	protected double temperature; // 机柜温度
	protected AlertCode alertCode = AlertCode.NORMAL;
    
	protected boolean checking = true; //系统是否正在自检
	protected boolean initOk = false; // 系统是否初始化成功
	
	protected ScheduledExecutorService  heartbeatThread; //心跳工作线程

	protected CoreData coreData = new CoreData(); // 核心板状态

	protected EnergySaveData energySaveData = new EnergySaveData(); // 节能方案
	protected TempData tempData = new TempData(); // 温控方案
	protected VoiceAlertData beep = new VoiceAlertData(); // 蜂鸣控制
//	protected OfflineRunningData offlineRunningData = new OfflineRunningData(); // 离线运行配置
	protected Date offlineRunningStartTime = null; // 离线运行开始时间
	
	protected Date startupDatetime; //系统最近一次启动时间
	protected long runMiliseconds; //系统累计运行时间
	protected Date factoryDatetime; //出厂时间

	// 启动配置管理器
	public static StartupCfgManager startupCfg;

	protected RunningLamp lamp;
	protected GpioPowerController powerController;
	protected boolean offline = true; // 网络被切断
	protected boolean poweroff; // 设备已发生断电
	protected boolean shutdown; // 设备已被切断
	
	// 控制单元分区集合
	protected List<ControlUnit> controls = new ArrayList<ControlUnit>();

	protected long alertSendDataIndex; // 报警或离线数据推送序号
	protected long alertRecvDataIndex; // 报警或离线数据推送回复序号

	protected Map<String, SerialPort> portMap = new HashMap<String, SerialPort>();
	
	protected Date  heartbeatTime; //上次心跳时间;主控通过数据推送实现心跳；在校准时则不判断心跳
	private final static int  HEARTBEAT_TIMEOUT = 5000; //心跳间隔

	/**
	 * 保护参数
	 */
	protected DataManager dataManager; // 离线数据管理器
	protected PowerManager powerManager; // 电源管理器
	protected FanManager fanManager; // 风机管理器
	protected UpgradeManager upgradeManager = new UpgradeManager(); // 升级管理器
	protected SmogAlertManager smogManager; // 烟雾报警器
	protected DoorAlertManager doorManager; // 门近报警器

	protected STMDeviceController deviceController; // 控制板
	protected TemperatureManager tempManager; // 温控板
	protected AudioLightAlarmController audioLightAlarmController; // 三色灯
	protected MechanismManager mechanismManager; // 机械控制
	protected ProbeManager probeManager; // 温度探头管理器
	protected List<StateLightController> stateLightControllers = new ArrayList<StateLightController>(); // 极性灯
	// 流程模式
	protected ProcedureMode procedureMode = ProcedureMode.DEVICE;
	protected ParameterName tempParameterNameCfg; // 临时下发的配置保护方案
	//
	
	protected boolean pressureOk;  //夹具是否压紧?

	protected ScheduledExecutorService executor;
	
	protected DriverChnIndexDefineData dcdd; //驱动板通道定义
	
	protected int     syncIndex;

	/**
	 * 初始化主控
	 */
	public abstract void init(String[] args);

	/**
	 * 回复基本网络命令
	 */
	
	/**
	 * 初始化控制器
	 * @author  wavy_zheng
	 * 2020年11月13日
	 */
	protected void initController() {
		
		
		
	
	}

	

	public State getState() {
		return state;
	}


	/**
	 * 检测流程的合法性
	 * 
	 * @param pd
	 * @return
	 */
	public void checkProcedureValid(ProcedureData pd) throws AlertException {

		if (pd.getLoopCount() > 0 && pd.getLoopSt() > pd.getLoopEd()) {

			throw new AlertException(AlertCode.LOGIC,
					"流程循环起始(" + pd.getLoopSt() + ") 不能大于 流程结束(" + pd.getLoopEd() + ")");
		}
		for (int i = 0; i < pd.getStepCount(); i++) {

			Step step = pd.getStep(i);

			if (step.workMode == WorkMode.CCC) {

				if (i < pd.getStepCount() - 1 && pd.getStep(i + 1).workMode == WorkMode.CVC) {

					if (step.specialCurrent == 0 || step.specialCurrent > MainBoard.startupCfg.getMaxDeviceCurrent()) {

						throw new AlertException(AlertCode.LOGIC,
								"在CC-CV模式程控电流值必须在范围(0-" + MainBoard.startupCfg.getMaxDeviceCurrent() + ")");
					}
					if (pd.getLoopCount() > 0 && pd.getLoopEd() == i + 1) {

						throw new AlertException(AlertCode.LOGIC, "在CC-CV模式下CC步次不能设置为循环结束");
					}
					// cc-cv特殊步次
					if (step.overThreshold != step.specialVoltage
							|| pd.getStep(i + 1).specialVoltage != step.overThreshold) {

						System.out.println(" step.overThreashold = " + step.overThreshold + ",step.specialVoltage = "
								+ step.specialVoltage);
						System.out.println(" cv special voltage = " + pd.getStep(i + 1).specialVoltage
								+ ",step.overThreashold = " + step.overThreshold);

						throw new AlertException(AlertCode.LOGIC, "在cc-cv模式下程控电压必须等于结束电压");
					}
					if (step.overThreshold > MainBoard.startupCfg.getMaxDeviceVoltage()) {

						throw new AlertException(AlertCode.LOGIC,
								"在cc-cv模式下结束电压必须在范围(0-" + MainBoard.startupCfg.getMaxDeviceVoltage() + ") ");
					}
					// if (pd.getStep(i + 1).overThreashold < getMixChargeCurrent()) {
					//
					// throw new RuntimeException("over current threashold must greater than minumun
					// charge current("
					// + getMixChargeCurrent() + ") in cc-cv mode ");
					// }
					// if (step.overCapacity != 0 || pd.getStep(i + 1).overCapacity != 0) {
					// throw new RuntimeException("overCapacity can not be set in cc-cv mode ");
					// }
					// if (step.overTime != 0 || pd.getStep(i + 1).overTime != 0) {
					// throw new RuntimeException("overTime can not be set in cc-cv mode ");
					// }
					// if (step.deltaVoltage != 0 || pd.getStep(i + 1).deltaVoltage != 0) {
					// throw new RuntimeException("deltaV can not be set in cc-cv mode ");
					// }
					i++;

				} else {

					// 单独CC步次
					if (step.overCapacity == 0 && step.overThreshold == 0 && step.overTime == 0
							&& step.deltaVoltage == 0) {

						throw new AlertException(AlertCode.LOGIC, "在cc模式下至少要有1个结束条件");
					}
					
					if (step.specialCurrent == 0 || step.specialCurrent > MainBoard.startupCfg.getMaxDeviceCurrent()) {

						throw new AlertException(AlertCode.LOGIC,
								"在cc模式下恒流值必须在范围(0-" + MainBoard.startupCfg.getMaxDeviceCurrent() + ")内");
					}

				}
			} else if (step.workMode == WorkMode.CVC) {

				// cv不能单独配置，必须和CC一起出现
				throw new AlertException(AlertCode.LOGIC, "cv流程步次不能单独配置");
			} else if (step.workMode == WorkMode.CCD) {

				if (step.specialCurrent == 0 || step.specialCurrent > MainBoard.startupCfg.getMaxDeviceCurrent()) {

					throw new AlertException(AlertCode.LOGIC,
							"在dc模式下恒流值必须在范围(0-" + MainBoard.startupCfg.getMaxDeviceCurrent() + ")内");
				}
				if (step.overThreshold == 0 && step.overTime == 0 && step.overCapacity == 0
						&& step.deltaVoltage == 0) {

					throw new AlertException(AlertCode.LOGIC, "在dc模式下至少要有1个结束条件");
				}
				if (step.overThreshold > 0 && step.overThreshold < MainBoard.startupCfg.getMinDischargeVoltage()) {

					throw new AlertException(AlertCode.LOGIC, "dc模式结束电压值" + step.overThreshold + "必须大于等于设备放电下限电压值"
							+ MainBoard.startupCfg.getMinDischargeVoltage() + " mv");
				}

			} else if (step.workMode == WorkMode.SLEEP) {

				// 休眠步次
				if (step.overCapacity > 0 || step.overThreshold > 0 || step.deltaVoltage > 0) {

					throw new AlertException(AlertCode.LOGIC, "休眠模式下只有时间能作为结束条件");
				}
				if (step.overTime == 0) {

					throw new AlertException(AlertCode.LOGIC, "休眠模式下必须要设置结束时间");
				}
			}
		}

	}

	

	/**
	 * 设备发生超温故障，紧急暂停流程并切断加热管
	 * 
	 * @throws AlertException
	 */
	public void emergencyShutdownHeat() throws AlertException {

		emergencyPauseDevice();
		// 关闭加热管
		if (tempManager != null) {

			tempManager.power(PowerState.OFF);
		}
		if (getFanManager() != null) {
			// 关闭风机
			getFanManager().powerTurboFan(PowerState.OFF);
		}
	}

	/**
	 * 紧急暂停设备
	 * 
	 * @throws AlertException
	 */
	public void emergencyPauseDevice() throws AlertException {

		for (int n = 0; n < controls.size(); n++) {

			controls.get(n).emergencyPause();
		}
	}

	/**
	 * 设备发生损伤性故障，紧急暂停流程并切断电源
	 * 
	 * @throws IOException
	 */
	public void emergencyShutdownDevice() throws AlertException {

		if (powerManager.getPowerSwitchState() == PowerState.ON) {

			shutdown = true;
			/**
			 * 紧急暂停设备
			 */
			emergencyPauseDevice();
			/**
			 * 关闭总电源
			 */
			powerManager.power(PowerState.OFF);
		}

	}

	public int getTotalChnCount() {

		int total = 0;
		for (DriverBoard db : driverBoards) {

			total +=  startupCfg.getDriverChnCount();
		}
		return total;
	}

	public STMDeviceController getDeviceController() {
		return deviceController;
	}

	public void setDeviceController(STMDeviceController deviceController) {
		this.deviceController = deviceController;
	}

	public boolean isOffline() {
		return offline;
	}
	
	public void setOffline(boolean offline) {
		
		this.offline = offline;
	}
	
	

	public void setPoweroff(boolean poweroff) {
		this.poweroff = poweroff;
	}

	public boolean isPoweroff() {
		return poweroff;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setState(State state) {
		this.state = state;
	}


	public ProcedureData getProcedure() {

		ProcedureData pd = null;
		for (ControlUnit cu : controls) {

			if (pd == null) {

				pd = cu.getProcedure();
			} else {

				if (cu.getProcedure() != null && pd != cu.getProcedure()) {

					return null; // 分区流程不同
				}
			}
		}

		return pd;

	}


	public ControlUnit getControlUnitByIndex(int index) {

		if (index == 0xff || index == -1) {

			if (controls.size() == 1) {
				return controls.get(0);
			} else {

				return null;
			}
		} else {

			return controls.get(index);
		}
	}

	public List<ControlUnit> getControls() {
		return controls;
	}

	public void setControls(List<ControlUnit> controls) {
		this.controls = controls;
	}

	public TemperatureManager getTempManager() {

		return tempManager;
	}

	public static String getHostIP() {

		String hostIp = null;
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			InetAddress ia = null;
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration<InetAddress> ias = ni.getInetAddresses();
				while (ias.hasMoreElements()) {
					ia = ias.nextElement();
					if (ia instanceof Inet6Address) {
						continue;// skip ipv6
					}
					String ip = ia.getHostAddress();
					if (!"127.0.0.1".equals(ip)) {
						hostIp = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {

			e.printStackTrace();
		}
		return hostIp;

	}

	public AlertCode getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(AlertCode alertCode) {
		this.alertCode = alertCode;
	}


	public AudioLightAlarmController getAudioLightAlarmController() {
		return audioLightAlarmController;
	}

	public List<StateLightController> getStateLightControllers() {
		return stateLightControllers;
	}

	public boolean isInitOk() {
		return initOk;
	}

	/**
	 * 获取主控核心状态数据
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public CoreData pickCoreData() throws AlertException {

		CoreData coreData = Environment.getCoreData();
		coreData.setNormal(initOk);
		coreData.setStartupDatetime(startupDatetime == null ? new Date() : startupDatetime);
		coreData.setFactoryDatetime(factoryDatetime == null ? new Date() : factoryDatetime);
		coreData.setRunMiliseconds(runMiliseconds);
		
		return coreData;
	}

	protected void createCoreDataPickThread(int pickupTime) {

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				try {
					coreData = pickCoreData();
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}, 10000, pickupTime, TimeUnit.MILLISECONDS);

	}

	public CoreData getCoreData() {
		return coreData;
	}

	public EnergySaveData getEnergySaveData() {
		return energySaveData;
	}

	public void setEnergySaveData(EnergySaveData energySaveData) throws AlertException {
		this.energySaveData = energySaveData;
		Context.getFileSaveService().writeEnergySaveFile(energySaveData);
		
	}

	public Channel getChannelByChnIndex(int chnIndex) {

		int driverIndex = chnIndex / MainBoard.startupCfg.getDriverChnCount();
		int chnIndexInDriver = chnIndex % MainBoard.startupCfg.getDriverChnCount();
		return driverBoards.get(driverIndex).getChannel(chnIndexInDriver);

	}

	public PowerManager getPowerManager() {
		return powerManager;
	}

	public FanManager getFanManager() {
		return fanManager;
	}


	

	

	public List<Constant.State> getAllChannelStStates() {

		List<Constant.State> states = new ArrayList<Constant.State>();
		for (int n = 0; n < getTotalChnCount(); n++) {

			Channel channel = getChannelByChnIndex(n);
			if (channel.getState() == ChnState.CLOSE) {

				states.add(Constant.State.LOCK);
			} else if (channel.getState() == ChnState.ALERT) {

				states.add(Constant.State.ALERT);
			} else if (channel.getState() == ChnState.RUN) {

				if (channel.getWorkMode() == WorkMode.CCC || channel.getWorkMode() == WorkMode.CVC) {
					states.add(Constant.State.CHARGE);
				} else if (channel.getWorkMode() == WorkMode.CCD) {
					states.add(Constant.State.DISCHARGE);
				}
			} else if (channel.getState() == ChnState.NONE) {

				states.add(Constant.State.NONE);
			} else {

				states.add(Constant.State.SLEEP);
			}
		}
		return states;

	}

	/**
	 * 获取驱动板统计状态数据
	 * 
	 * @param driverIndex
	 * @return
	 */
	public Constant.State getDriverStStateByIndex(int driverIndex) {

		DriverBoard db = getDriverByIndex(driverIndex);
		Constant.State state = Constant.State.SLEEP;
		Map<Constant.State, Integer> map = new HashMap<Constant.State, Integer>();
		for (int n = 0; n < db.getChannelCount(); n++) {

			Channel channel = db.getChannel(n);
			if (channel.getState() == ChnState.NONE) {

				if (map.get(Constant.State.NONE) == null) {

					map.put(Constant.State.NONE, 1);
				} else {

					map.put(Constant.State.NONE, map.get(Constant.State.NONE) + 1);
				}
			} else if (channel.getState() == ChnState.ALERT) {

				if (map.get(Constant.State.ALERT) == null) {

					map.put(Constant.State.ALERT, 1);
				} else {

					map.put(Constant.State.ALERT, map.get(Constant.State.ALERT) + 1);
				}
			} else if (channel.getState() == ChnState.RUN) {

				if (channel.getWorkMode() == WorkMode.CCC || channel.getWorkMode() == WorkMode.CVC
						|| channel.getWorkMode() == WorkMode.CC_CV) {

					if (map.get(Constant.State.CHARGE) == null) {

						map.put(Constant.State.CHARGE, 1);
					} else {

						map.put(Constant.State.CHARGE, map.get(Constant.State.CHARGE) + 1);
					}
				} else if (channel.getWorkMode() == WorkMode.CCD) {

					if (map.get(Constant.State.DISCHARGE) == null) {

						map.put(Constant.State.DISCHARGE, 1);
					} else {

						map.put(Constant.State.DISCHARGE, map.get(Constant.State.DISCHARGE) + 1);
					}
				}

			} else if (channel.getState() == ChnState.CLOSE) {

				if (map.get(Constant.State.LOCK) == null) {

					map.put(Constant.State.LOCK, 1);
				} else {

					map.put(Constant.State.LOCK, map.get(Constant.State.LOCK) + 1);
				}
			}

		}

		int count = db.getChannelCount();
		if (map.get(Constant.State.LOCK) != null) {

			if (map.get(Constant.State.LOCK) == db.getChannelCount()) {
				return Constant.State.LOCK;
			}
			count -= map.get(Constant.State.LOCK); // 减去锁定的通道数
		}
		if (map.get(Constant.State.CHARGE) != null && map.get(Constant.State.CHARGE) > count * 0.7) {

			return Constant.State.CHARGE;
		}
		if (map.get(Constant.State.DISCHARGE) != null && map.get(Constant.State.DISCHARGE) > count * 0.7) {

			return Constant.State.DISCHARGE;
		}
		if (map.get(Constant.State.NONE) != null && map.get(Constant.State.NONE) > count * 0.7) {

			return Constant.State.NONE;
		}

		return Constant.State.SLEEP;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public Map<String, SerialPort> getPortMap() {
		return portMap;
	}

	public MechanismManager getMechanismManager() {
		return mechanismManager;
	}

	public ProcedureMode getProcedureMode() {
		return procedureMode;
	}

	public void setProcedureMode(ProcedureMode procedureMode) {
		this.procedureMode = procedureMode;
	}

	public DriverBoard getDriverByIndex(int driverIndexInDevice) {

		return this.driverBoards.get(driverIndexInDevice);
	}

	public void clearTempCfg() {

		tempParameterNameCfg = new ParameterName(WorkType.AG);
		tempParameterNameCfg.setCcProtect(null);
		tempParameterNameCfg.setCheckVoltProtect(null);
		tempParameterNameCfg.setCvProtect(null);
		tempParameterNameCfg.setDcProtect(null);
		tempParameterNameCfg.setDeviceProtect(null);
		tempParameterNameCfg.setFirstCCProtect(null);
		tempParameterNameCfg.setPoleProtect(null);
		tempParameterNameCfg.setSlpProtect(null);
		tempParameterNameCfg.setStartEndCheckProtect(null);

	}
	
	public void setTempParameterNameCfg(ParameterName pn) {
		
		tempParameterNameCfg = pn;
	}
	
	

	public ParameterName getTempParameterNameCfg() {
		return tempParameterNameCfg;
	}

	public int getControlUnitCount() {

		return controls.size();
	}

//	public OfflineRunningData getOfflineRunningData() {
//		return offlineRunningData;
//	}

	public synchronized Date getOfflineRunningStartTime() {
		return offlineRunningStartTime;
	}

	public synchronized void resetOfflineRunningStartTime() {

		offlineRunningStartTime = null;
	}

	public synchronized void flagOfflineRunningStartTime() {

		offlineRunningStartTime = new Date();
	}

	/**
	 * 重置当前设备
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void reset() throws IOException, InterruptedException {

		if (state == State.FORMATION) {

			return;
		}
		// 删除struct文件夹
		FileUtil.removeAllFiles("./config/struct");
		// 删除struct.xml
		new File("./config/struct.xml").delete();
		// 删除offline文件夹
		FileUtil.removeAllFiles("./offline");

		if (Environment.isLinuxEnvironment()) {
			// 重启设备
			Environment.executeSysCmd(new String[] { "reboot" });
		}
	}

	public ProbeManager getProbeManager() {
		return probeManager;
	}

	public void setProbeManager(ProbeManager probeManager) {
		this.probeManager = probeManager;
	}

	public boolean isReadChnState() {
		return readChnState;
	}

	public SmogAlertManager getSmogManager() {
		return smogManager;
	}

	public DoorAlertManager getDoorManager() {
		return doorManager;
	}

	/**
	 * 停止监视设备状态
	 * 
	 * @author wavy_zheng 2020年7月14日
	 */
	private void stopMonitor() {

		if (executor != null) {

			System.out.println("stop monitor device state");
			executor.shutdown();
			executor = null;
		}
	}

	public void parseUnitControls(ControlUnitData data) {

		for (ControlUnit cu : controls) {

			// 停止状态采集线程
			cu.stopMonitorUnitState();
		}
		controls.clear();
		if (data.getMode() == ProcedureMode.DEVICE) {

			ControlUnit cu = ControlUnit.createUnit(this, 0);
			controls.add(cu);

		}  else if (data.getMode() == ProcedureMode.DRIVER) {

			Map<Byte, List<DriverBoard>> map = new HashMap<>();

			List<DriverBoard> drivers = listDrivers();
			for (int n = 0; n < data.getControls().size(); n++) {

				byte v = data.getControls().get(n);
				if (map.containsKey(v)) {

					map.get(v).add(drivers.get(n));

				} else {

					List<DriverBoard> list = new ArrayList<>();
					list.add(drivers.get(n));
					map.put(v, list);
				}

			}
			for (int n = 1; n <= map.size(); n++) {

				ControlUnit cu = ControlUnit.createUnit(this, n - 1, map.get(n).toArray(new DriverBoard[0]));
				controls.add(cu);
			}

		}

		setProcedureMode(data.getMode());
	}

	public List<DriverBoard> listDrivers() {

		return driverBoards;
	}
	
	public void appendDriver(DriverBoard db) {
		
		this.driverBoards.add(db);
	}

	/**
	 * 获取统计数据
	 * 
	 * @return
	 */
	public Statistics getStatistics() {

		Statistics st = new Statistics();
		for (int n = 0; n < getTotalChnCount(); n++) {

			Channel chn = getChannelByChnIndex(n);
			if (chn.getState() == ChnState.RUN) {

				if (chn.getWorkMode() == WorkMode.CCC || chn.getWorkMode() == WorkMode.CVC) {

					st.charge++;
				} else if (chn.getWorkMode() == WorkMode.CCD) {

					st.discharge++;
				} else if (chn.getWorkMode() == WorkMode.SLEEP) {

					st.sleep++;
				}
			} else if (chn.getState() == ChnState.ALERT) {

				st.alert++;
			}
		}

		return st;
	}

	public void switchChannelState(UnitChannelSwitchData ucsd) {

		List<Byte> states = ucsd.getChannelStates();

		for (int n = 0; n < MainBoard.startupCfg.getDeviceChnCount(); n++) {

			// 每个字节代表8个通道

			Channel chn = getChannelByChnIndex(n);
			if ((states.get(n / 8) & (0x01 << (n % 8))) == 0) {

				// 关闭通道
				chn.setState(ChnState.CLOSE);

			} else {

				if (chn.getState() == ChnState.CLOSE) {
					chn.setState(ChnState.UDT);
				}

			}
		}

	}
	
	/**
	 * 写入通道开关状态文件
	 * @author  wavy_zheng
	 * 2020年11月12日
	 * @throws AlertException
	 */
	public void writeChnSwitchFile() throws AlertException {
		
		Context.getFileSaveService().writeChannelSwitchState();
		
	}
	
	public void writeTempControlFile(TempData tempData) throws AlertException {
		
		Context.getFileSaveService().writeTempControlFile(tempData);
		
	}
	
	
	public void writeOfflineFile(OfflineRunningData offline) throws AlertException {
		
		Context.getFileSaveService().writeOfflineRunningFile(offline);
		
	}
   
	/**
	 * 获取温度控制方案
	 * @author  wavy_zheng
	 * 2020年11月13日
	 * @return
	 */
	public TempData getTempData() {
		return tempData;
	}
    
	/**
	 * 获取蜂鸣控制方案
	 * @author  wavy_zheng
	 * 2020年11月13日
	 * @return
	 */
	public VoiceAlertData getBeep() {
		return beep;
	}

	public RunningLamp getLamp() {
		return lamp;
	}
	
	public void pushSendQueue(AlertException exception) {
		
		Context.getPcNetworkService().pushSendQueue(exception);
	}
	
	public void pushSendQueue(Decorator dec) {
		
		Context.getPcNetworkService().pushSendQueue(dec);
	}
	
	public void pushSendQueue(int unitIndex, int deviceChnIndex, AlertCode code, String content) {
		
		Context.getPcNetworkService().pushSendQueue(unitIndex, deviceChnIndex, code, content);
	}

	public Date getStartupDatetime() {
		return startupDatetime;
	}

	public void setStartupDatetime(Date startupDatetime) {
		this.startupDatetime = startupDatetime;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	public Date getFactoryDatetime() {
		return factoryDatetime;
	}

	public void setFactoryDatetime(Date factoryDatetime) {
		this.factoryDatetime = factoryDatetime;
	}

	public boolean isChecking() {
		return checking;
	}

	public void setChecking(boolean checking) {
		this.checking = checking;
	}

	public boolean isPressureOk() {
		return pressureOk;
	}

	public void setPressureOk(boolean pressureOk) {
		this.pressureOk = pressureOk;
	}

	public DriverChnIndexDefineData getDcdd() {
		return dcdd;
	}

	public void setDcdd(DriverChnIndexDefineData dcdd) {
		this.dcdd = dcdd;
	}

	public int getSyncIndex() {
		return syncIndex;
	}

	public void setSyncIndex(int syncIndex) {
		this.syncIndex = syncIndex;
	}

    public void decreaseSynIndex() {
    	
    	this.syncIndex--;
    }

	public List<DriverBoard> getDriverBoards() {
		return driverBoards;
	}

	public Date getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(Date heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}
	
	/**
	 * 切断网络
	 * @author  wavy_zheng
	 * 2022年7月24日
	 */
	public void disconnect() {
		
		setOffline(true); // 掉线状态
		Context.getPcNetworkService().stopPushWork(); //停止推送工作
		processOfflineSaveWork(); //处理离线工作
		Context.getPcNetworkService().cutoff();
		
	}
	
	/**
	 * 处理掉线数据保存工作
	 * @author  wavy_zheng
	 * 2022年7月24日
	 */
	public void processOfflineSaveWork() {
		
		if (Context.getCoreService().getOfflineRunningCfg().isForbid()) {// 禁用离线运行
            
			//暂停整机流程
			if(getState() == State.FORMATION) {
				
				
				Environment.infoLogger.info("pause mainboard");
				try {
					Context.getCoreService().emergencyPause(null);
				} catch (AlertException e) {
					
					e.printStackTrace();
				}
			}
			
			// 进入掉线状态
			try {
				Context.getAlertManager().handle(AlertCode.OFFLINE, I18N.getVal(I18N.NetworkOff), false);
			} catch (AlertException e) {

				e.printStackTrace();
			}
		} else {
            
			
			 //进入离线运行模式
			Context.getPcNetworkService().pushSendQueue(-1, -1, AlertCode.NORMAL, I18N.getVal(I18N.DeviceEnterOfflineRun));
		}
		
	}
	
	
	
	/**
	 * 创建心跳工作线程
	 * @author  wavy_zheng
	 * 2022年7月24日
	 */
	public void createHeartbeatThread() {
		
		if(heartbeatThread != null) {
			
			return;
		}
		
		heartbeatThread = Executors.newSingleThreadScheduledExecutor();
		heartbeatThread.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				
				if(isOffline() || state == State.CAL) {
					
					heartbeatTime = null;
					return;
				}
				if(heartbeatTime == null) {
					
					heartbeatTime = new Date();
					return;
				}
				
				long timespan = new Date().getTime() - heartbeatTime.getTime();
				if(timespan >= HEARTBEAT_TIMEOUT * 5 ) {
					
					Environment.infoLogger.info("heart beat time out, cut off network!");
					
					//网络连接超时，中断网络
					disconnect();
					
				}
			}
			
			
		}, 100, HEARTBEAT_TIMEOUT , TimeUnit.MILLISECONDS);
		
	}
	

}
