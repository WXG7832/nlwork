package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.constant.Constant.Window;
import com.nlteck.exception.ScreenException;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.DriverBoard.ChangeResult;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.firmware.STMDriverBoard;
import com.nlteck.firmware.SmartPickupData;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.DriverInfo;
import com.nlteck.service.StartupCfgManager.DriverboardType;
import com.nlteck.service.accessory.ScreenController;
import com.nlteck.service.data.ProtectionFilterService;
import com.nlteck.service.data.virtual.VirtualDriver;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.FileUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.IDType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ValveState;
import com.nltecklib.protocol.li.accessory.ColorLightData.LightColor;
import com.nltecklib.protocol.li.accessory.FourLightStateData;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.check2.Check2Environment.CalDot;
import com.nltecklib.protocol.li.check2.Check2Environment.CheckWorkMode;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.check2.Check2FaultCheckData;
import com.nltecklib.protocol.li.check2.Check2PoleData;
import com.nltecklib.protocol.li.check2.Check2ProgramStateData;
import com.nltecklib.protocol.li.check2.Check2SoftversionData;
import com.nltecklib.protocol.li.check2.Check2StartupData;
import com.nltecklib.protocol.li.check2.Check2UUIDData;
import com.nltecklib.protocol.li.check2.Check2VoltProtectData;
import com.nltecklib.protocol.li.check2.Check2WriteCalFlashData;

import com.nltecklib.protocol.li.logic2.Logic2CheckFlashWriteData;
import com.nltecklib.protocol.li.logic2.Logic2ChnOptData;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectData;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectExData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.DriverFaultInfo;
import com.nltecklib.protocol.li.logic2.Logic2Environment.LogicState;
import com.nltecklib.protocol.li.logic2.Logic2Environment.OptMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.SwitchState;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.logic2.Logic2FaultCheckData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData;
import com.nltecklib.protocol.li.logic2.Logic2PoleData;
import com.nltecklib.protocol.li.logic2.Logic2PoleExData;
import com.nltecklib.protocol.li.logic2.Logic2ProcedureData;
import com.nltecklib.protocol.li.logic2.Logic2ProgramStateData;
import com.nltecklib.protocol.li.logic2.Logic2SoftversionData;
import com.nltecklib.protocol.li.logic2.Logic2StartupInitData;
import com.nltecklib.protocol.li.logic2.Logic2StartupInitData.ChnInitData;
import com.nltecklib.protocol.li.logic2.Logic2StateData;
import com.nltecklib.protocol.li.logic2.Logic2UUIDData;
import com.nltecklib.protocol.li.main.ControlUnitData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.CheckMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnOpt;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.ControlUnitMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.CoreData;
import com.nltecklib.protocol.li.main.MainEnvironment.DriverMonitorState;
import com.nltecklib.protocol.li.main.MainEnvironment.LogicMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.OfflineRunningData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.StartupData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPoleData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverResumeData.ResumeUnit;
import com.nltecklib.protocol.power.driver.DriverStepData;
import com.nltecklib.protocol.power.driver.DriverUpgradeData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月13日 下午4:16:15 主控核心服务组件
 */
public class CoreService {

	private MainBoard mainboard;
	private ScheduledExecutorService executor;
	private Logger logger;
	private OfflineRunningData offlineRunningCfg; // 离线运行配置
	private ScheduledExecutorService pickupExecutor; // 采集线程池

	public CoreService(MainBoard mainboard) {

		this.mainboard = mainboard;
		try {
			logger = LogUtil.createLog("log/coreService.log");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 初始化分区
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	private void createAndInitControlUnit() throws AlertException {

		Context.getFileSaveService().readStructFile();
		ScreenController screenController = Context.getAccessoriesService().getScreenController();
		if (screenController != null) {

			try {
				screenController.showStartupInfo(true, I18N.getVal(I18N.InitControlunitSuccess));
			} catch (ScreenException e) {

				e.printStackTrace();
			}
		}
		// 亮灯
		if (Context.getAccessoriesService().getStateLightController() != null) {

			for (ControlUnit cu : mainboard.getControls()) {

				Pole pole = cu.getPole().getPole();
				logger.info("light pole light:" + pole);
				// 点亮
				Context.getAccessoriesService().getStateLightController().light(cu.getIndex(),
						pole == Pole.NORMAL ? LightColor.GREEN : LightColor.YELLOW, false);
			}
		}

	}

	/**
	 * 创建并初始化所有核心部件
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	private void createAndInitCoreFirms() throws AlertException {

		// 初始化逻辑板（采集单元）
		try {
			for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

				ScreenController screenController = Context.getAccessoriesService().getScreenController();
				if (screenController != null) {

					screenController.showStartupInfo(true, I18N.getVal(I18N.InitDriverboard, n + 1));

				}

				DriverBoard db = new DriverBoard(n, mainboard);

				// 初始化驱动板
				db.init();

				// 添加到主控
				mainboard.appendDriver(db);

			}

		} catch (Exception ex) {

			ex.printStackTrace();
			throw new AlertException(AlertCode.INIT, ex.getMessage());
		}

	}

	/**
	 * 初始化核心部件
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	public void init() throws AlertException {

		Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL, I18N.getVal(I18N.SelfCheckingCores));
		offlineRunningCfg = Context.getFileSaveService().readOfflineRunningFile();

		logger.info("start init dcdd");
		
		if(MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {
			
			logger.info("wait 10s for network device initialize");
			CommonUtil.sleep(10000);
		}
		

		// 初始化通道定义
		try {
			mainboard.setDcdd(Context.getFileSaveService().readDriverChnIndexDefineFile());
		} catch (AlertException ae) {

			// logger.info(CommonUtil.getThrowableException(ae));
		}
		logger.info("createAndInitCoreFirms()");
		createAndInitCoreFirms();
		logger.info("createAndInitControlUnit()");
		createAndInitControlUnit();

		// 读取通道运行状态
		for (ControlUnit cu : mainboard.getControls()) {

			Context.getFileSaveService().readUnitRuntimeState(cu);

			// 读取分区通道存储数据

			List<Channel> channels = cu.listAllChannels(null);
			for (Channel chn : channels) {

				List<ChannelData> offlineDatas = Context.getFileSaveService().readOfflineFromDisk(chn);
				if (chn.getState() == ChnState.PAUSE || chn.getState() == ChnState.RUN) {

					chn.getOfflineCaches().addAll(offlineDatas); // 加入离线缓存
				}

			}

		}

		for (ControlUnit cu : mainboard.getControls()) {

			if (cu.getState() == State.FORMATION) {

				cu.getMainBoard().setState(State.FORMATION);
				break;
			} else if (cu.getState() == State.PAUSE) {

				cu.getMainBoard().setState(State.PAUSE);
				break;
			} else if (cu.getState() == State.STOP) {
				cu.getMainBoard().setState(State.STOP);
				break;
			}

		}
		// 维修模式
		if (Context.getFileSaveService().isMaintainFileExists()) {

			mainboard.setState(State.MAINTAIN);
		}

		logger.info("read switch state");
		// 读取通道开关状态
		Context.getFileSaveService().readChnSwitchState();

		if (!MainBoard.startupCfg.isUseDebug()) {

			logger.info("write base protecions to units");
             
			CommonUtil.sleep(500);
			// 写入保护系数
			for (ControlUnit cu : mainboard.getControls()) {
				cu.writeBaseProtections();
			}

		}

		/*
		 * if (mainboard.getProcedureMode() == ProcedureMode.DRIVER) {
		 * 
		 * // 默认回检5v超压配置 // 如果是驱动板独立模式，将禁用回检板超压保护功能 for (CheckBoard cb :
		 * mainboard.getCheckBoards()) {
		 * 
		 * if (cb.isUse()) { Check2VoltProtectData cvpd = new Check2VoltProtectData();
		 * cvpd.setUnitIndex(cb.getCheckIndex()); cvpd.setOverThreashold(5000); // 默认5v
		 * 
		 * try { Context.getCheckboardService().writeVoltageProtection(cvpd);
		 * logger.info( "write check board " + (cb.getCheckIndex() + 1) +
		 * " default 5000 mv voltage protect"); } catch (AlertException ex) {
		 * 
		 * throw new AlertException(AlertCode.LOGIC, "回检板" + (cb.getCheckIndex() + 1) +
		 * "配置超压保护失败:" + ex.getMessage()); } } }
		 * 
		 * }
		 */

		// logger.info("read life file");
		// 读取配件寿命
		// Context.getFileSaveService().readLifeFile();

	}

	private List<Integer> getThreadDriverList(int threadIndex) {

		int driverCount = mainboard.getDriverBoards().size();
		int threadCount = 4;
		if (driverCount < 4) {

			threadCount = driverCount;
		}
		int threadDriverCount = driverCount / threadCount;

		List<Integer> list = new ArrayList<>();
		if (threadIndex < threadCount - 1) {

			for (int n = threadIndex * threadDriverCount; n < (threadIndex + 1) * threadDriverCount; n++) {

				list.add(n);
			}

		} else {

			for (int n = threadIndex * threadDriverCount; n < driverCount; n++) {

				list.add(n);
			}

		}

		return list;

	}

	/**
	 * 主控开始工作
	 * 
	 * @author wavy_zheng 2020年11月13日
	 */
	public void startWork() {

		logger.info("start work");

		if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
			for (DriverBoard db : mainboard.getDriverBoards()) {

				Context.getDriverboardService().startWork(db.getDriverIndex());
			}
		} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

			// 采用线程池采集
			if (pickupExecutor != null) {

				return;

			}
			pickupExecutor = Executors.newScheduledThreadPool(4);
			int driverCount = mainboard.getDriverBoards().size();
			int threadCount = 4;
			if (driverCount < 4) {

				threadCount = driverCount;
			}

			for (int n = 0; n < threadCount; n++) {

				final int threadIndex = n;
				pickupExecutor.scheduleWithFixedDelay(new Runnable() {

					@Override
					public void run() {

						List<Integer> driverIndexs = getThreadDriverList(threadIndex);
						for (Integer index : driverIndexs) {

							Context.getDriverboardService().pickupDriverData(index);
						}

					}

				}, 100, 500, TimeUnit.MILLISECONDS);
			}

		}
		startMonitor();

	}

	public void stopWork() {

		logger.info("stop work");
		if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
			for (DriverBoard db : mainboard.getDriverBoards()) {

				Context.getDriverboardService().stopWork(db.getDriverIndex());
			}
		} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {
			
			if(pickupExecutor != null) {
				
				CommonUtil.exitThread(pickupExecutor, 2000);
				pickupExecutor = null;
			}
			
		}
		stopMonitor();

	}

	/**
	 * 给分区或整机配置电压保护,写入逻辑板
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param cu
	 * @param dpd
	 * @throws AlertException
	 */
	public void writeVoltageProtection(ControlUnit cu, DeviceProtectData dpd) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				writeVoltageProtection(unit, dpd);
			}

		} else {

			for (DriverBoard db : cu.getDrivers()) {

				if (db.isUse()) {

					DriverProtectData ddpd = new DriverProtectData();
					ddpd.setDriverIndex(db.getLoadDriverIndex());
					ddpd.setDeviceVoltUpper(dpd.getDeviceVoltUpper());
					ddpd.setChnVoltUpper(dpd.getBatVoltUpper());
					ddpd.setChnCurrUpper(dpd.getCurrUpper());
					ddpd.setChnVoltLower(1000); // 默认放电最低电压
					logger.info("write logic device protect:" + ddpd);
					try {
						Context.getDriverboardService().writeBaseProtect(ddpd);
					} catch (AlertException ex) {

						throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.DriverBaseProtectProtectException,
								db.getDriverIndex() + 1, ex.getMessage()));
					}

				}
			}

		}

	}

	/**
	 * 配置分区或设备极性
	 * 
	 * @author wavy_zheng 2020年11月30日
	 * @param cu
	 * @param pole
	 * @throws AlertException
	 */
	public void writePoleProtection(ControlUnit cu, PoleData pole) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				writePoleProtection(unit, pole);
			}

		} else {

			for (DriverBoard db : cu.getDrivers()) {

				if (db.isUse()) {
					DriverPoleData ppd = new DriverPoleData();
					ppd.setDriverIndex(db.getDriverIndex());
					ppd.setVoltageBound(pole.getPoleDefine());
					ppd.setPole(DriverEnvironment.Pole.values()[pole.getPole().ordinal()]);

					logger.info("write pole:" + ppd);
					try {
						Context.getDriverboardService().writePole(ppd);
					} catch (AlertException ex) {

						throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.DriverPoleProtectProtectException,
								db.getDriverIndex() + 1, ex.getMessage()));
					}
				}

			}

		}
	}

	/**
	 * 给分区或整机配置流程
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param cu
	 *            null表示整机（所有分区）配置流程
	 * @param procedure
	 *            下发的流程
	 * @throws AlertException
	 */
	public void writeProcedure(ControlUnit cu, ProcedureData procedure) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				writeProcedure(unit, procedure);
			}
		} else {

			Map<String, List<DriverBoard>> map = getDriverByPortMap(cu);
			// 转化流程

			final CountDownLatch latch = new CountDownLatch(map.size());
			final List<AlertException> exceptions = new ArrayList<>();
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {

				new Thread(new Runnable() {

					@Override
					public void run() {

						List<DriverBoard> drivers = map.get(it.next());

						try {
							for (DriverBoard db : drivers) {

								DriverStepData dsp = convertProcedureFrom(procedure);
								dsp.setDriverIndex(db.getDriverIndex()); // 别忘了设置驱动板地址
								Context.getDriverboardService().writeSteps(dsp);
							}
						} catch (AlertException ex) {

							exceptions.add(ex);
							ex.printStackTrace();
							// Context.getPcNetworkService().pushSendQueue(ex);

						} finally {

							latch.countDown();
						}

					}

				}).start();

			}

			try {
				latch.await();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			if (!exceptions.isEmpty()) {

				throw exceptions.get(0);
			}

			cu.setProcedure(procedure); // 设置流程

		}

	}

	/**
	 * 根据当前用户选择的通道创建工步
	 * 
	 * @author wavy_zheng 2022年7月14日
	 * @param channels
	 *            当channels为空时，表示对整柜通道进行流程下发
	 * @return
	 */
	private Map<DriverBoard, List<DriverStepData>> createDriverStepData(List<Channel> channels) {

		Map<DriverBoard, List<Channel>> driverChnMap = null;

		if (channels.isEmpty()) {

			driverChnMap = new HashMap<>();
			for (DriverBoard driver : mainboard.listDrivers()) {

				driverChnMap.put(driver, driver.getChannels());
			}

		} else {

			driverChnMap = classifyChannelsInDriver(channels);
		}

		// 找到同个流程同步次的工步
		for (Iterator<DriverBoard> it = driverChnMap.keySet().iterator(); it.hasNext();) {

			DriverBoard driver = it.next();

		}

		return null;
	}

	/**
	 * 将上位机-主控流程协议转为主控-逻辑板流程协议; 且设置流程每个步次的暂停或继续标志位，被设置暂停标志位的步次必须由主控发送跳转步次命令
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param procedure
	 * @return
	 */
	private static DriverStepData convertProcedureFrom(ProcedureData procedure) {

		DriverStepData lp = new DriverStepData();

		lp.setLoopCount(procedure.getLoopCount());
		lp.setLoopEd(procedure.getLoopEd());
		lp.setLoopSt(procedure.getLoopSt());

		for (int n = 0; n < procedure.getStepCount(); n++) {

			Step step = procedure.getStep(n);
			step.setOverMode(OverMode.PROCEED);

			if (step.workMode == WorkMode.CCD) {

				step.specialVoltage = 5500; // 默认放电DA
			}

			lp.getSteps().add(step);
		}

		return lp;

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

	/**
	 * 监视设备状态变化
	 * 
	 * @author wavy_zheng 2020年12月10日
	 */
	private void monitorMainboardState() {

		boolean anyUnitRun = false;
		boolean anyUnitPause = false;
		boolean anyUnitStop = false;
		boolean anyCal = false;
		for (ControlUnit unit : mainboard.getControls()) {

			if (unit.getState() == State.FORMATION) {

				anyUnitRun = true;
			} else if (unit.getState() == State.PAUSE) {

				anyUnitPause = true;
			} else if (unit.getState() == State.STOP) {

				anyUnitStop = true;

			} else if (unit.getState() == State.CAL) {

				anyCal = true;
			}

		}

		if (anyUnitRun) {

			if (mainboard.getState() != State.FORMATION) {

				mainboard.setState(State.FORMATION);

				// 状态变更为运行
				System.out.println("change device state :" + mainboard.getState());
				try {
					Context.getAlertManager().handle(AlertCode.NORMAL, "", false);
				} catch (AlertException e) {

					e.printStackTrace();
				}

				// 进入驱动板界面
				if (Context.getAccessoriesService().getScreenController() != null) {

					try {
						Context.getAccessoriesService().getScreenController().switchScreen(Window.DRIVING);
					} catch (ScreenException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (Context.getAccessoriesService().getPingController() != null) {

					FourLightStateData light = new FourLightStateData();
					light.setBlinkState(0xffff);
					light.setBuzzerState(0);
					light.setLightState(0x04); // 绿灯
					try {
						Context.getAccessoriesService().getPingController().writeFourLightState(light);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		} else if (anyUnitPause) {

			if (mainboard.getState() != State.PAUSE) {

				mainboard.setState(State.PAUSE);
				// 设备运行灯为待机
				try {
					Context.getAlertManager().handle(AlertCode.NORMAL, "", false);
				} catch (AlertException e) {

					e.printStackTrace();
				}

				if (Context.getAccessoriesService().getPingController() != null) {

					FourLightStateData light = new FourLightStateData();
					light.setBlinkState(0xffff);
					light.setBuzzerState(0);
					light.setLightState(0x02);
					try {
						Context.getAccessoriesService().getPingController().writeFourLightState(light);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		} else if (anyUnitStop) {

			if (mainboard.getState() == State.FORMATION || mainboard.getState() == State.PAUSE) {

				try {
					exitWorkMode(null);
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mainboard.setState(State.STOP);
				// 设备运行灯为待机
				try {
					Context.getAlertManager().handle(AlertCode.NORMAL, "", false);
				} catch (AlertException e) {

					e.printStackTrace();
				}

				if (Context.getAccessoriesService().getPingController() != null) {

					FourLightStateData light = new FourLightStateData();
					light.setBlinkState(0xffff);
					light.setBuzzerState(0);
					light.setLightState(0x02);
					try {
						Context.getAccessoriesService().getPingController().writeFourLightState(light);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		} else if (anyCal) {

			if (mainboard.getState() != State.CAL) {
				mainboard.setState(State.CAL);

				if (Context.getAccessoriesService().getPingController() != null) {

					FourLightStateData light = new FourLightStateData();
					light.setBlinkState(0xffff);
					light.setBuzzerState(0);
					light.setLightState(0x01);
					try {
						Context.getAccessoriesService().getPingController().writeFourLightState(light);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		} else {

			if (mainboard.getState() == State.FORMATION || mainboard.getState() == State.PAUSE) {

				mainboard.setState(State.COMPLETE);
				try {
					exitWorkMode(null);
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 设备运行灯为待机
				try {
					Context.getAlertManager().handle(AlertCode.NORMAL, "", false);
				} catch (AlertException e) {

					e.printStackTrace();
				}
				// 进入功能界面
				if (Context.getAccessoriesService().getScreenController() != null) {

					try {
						Context.getAccessoriesService().getScreenController().switchScreen(Window.FUNC);
					} catch (ScreenException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (Context.getAccessoriesService().getPingController() != null) {

					FourLightStateData light = new FourLightStateData();
					light.setBlinkState(0xffff);
					light.setBuzzerState(0);
					light.setLightState(0x02);
					try {
						Context.getAccessoriesService().getPingController().writeFourLightState(light);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} else {

				if (mainboard.getState() != State.UPGRADE && mainboard.getState() != State.MAINTAIN) {

					if (mainboard.getState() != State.NORMAL) {
						mainboard.setState(State.NORMAL);

						if (Context.getAccessoriesService().getPingController() != null) {

							FourLightStateData light = new FourLightStateData();
							light.setBlinkState(0xffff);
							light.setBuzzerState(0);
							light.setLightState(0x02);
							try {
								Context.getAccessoriesService().getPingController().writeFourLightState(light);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}

		}
	}

	/**
	 * 监视驱动板,逻辑板的状态
	 * 
	 * @author wavy_zheng 2020年12月9日
	 * @throws AlertException
	 */
	private void monitorUnitState(ControlUnit cu) throws AlertException {

		boolean isAnyChnRun = false;
		boolean isAnyChnPause = false;
		boolean isAnyChnStop = false;

		for (DriverBoard db : cu.getDrivers()) {

			if (db.isAnyChnRunning()) {

				isAnyChnRun = true;
			} else if (db.isAnyChnPaused()) {

				isAnyChnPause = true;
			} else if (db.isAnyChnStoped()) {

				isAnyChnStop = true;
			}

		}
		if (isAnyChnRun) {

			cu.setState(State.FORMATION); // 运行状态
			if (cu.getOperateState() == State.FORMATION || cu.getOperateState() == State.STARTUP) {
				cu.setOperateState(State.NORMAL); // 清空操作
			}
		} else if (isAnyChnPause) {

			if (cu.getState() != State.PAUSE) {

				// 退出工作模式
				exitWorkMode(cu);
				cu.setState(State.PAUSE);
				if (cu.getOperateState() == State.PAUSE) {
					cu.setOperateState(State.NORMAL); // 清空操作
				}

			}

		} else if (isAnyChnStop) {

			if (cu.isAllChnRuntimeCachesOver() && cu.getState() != State.STOP) { // 缓存数据是否全部清空

				cu.setState(State.STOP);

				try {
					exitWorkMode(cu); // 退出工作
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (cu.getOperateState() == State.STOP) {
					cu.setOperateState(State.NORMAL); // 清空操作
				}
			}
		} else {

			if (cu.getState() == State.FORMATION || cu.getState() == State.PAUSE) {

				if (cu.isAllChnRuntimeCachesOver()) { // 缓存数据是否全部清空
					cu.setState(State.COMPLETE);
					try {
						exitWorkMode(cu);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {

				if (cu.getOperateState() == State.STARTUP || cu.getOperateState() == State.FORMATION) {

					// 检测是否启动超时
					if (new Date().getTime() - cu.getOpertateDate().getTime() >= MainBoard.startupCfg
							.getMaxStartupTimeout()) {

						// 启动超时
						cu.setState(State.COMPLETE);
						Context.getPcNetworkService().pushSendQueue(cu.getIndex(), -1, AlertCode.LOGIC,
								(mainboard.getControlUnitCount() == 1 ? "设备" : "分区" + (cu.getIndex() + 1)) + "启动超时");
						cu.setState(State.NORMAL); // 清空操作
						cu.setOperateState(State.NORMAL);
						try {
							exitWorkMode(cu);
						} catch (AlertException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

				// state = State.NORMAL;
			}
		}

	}

	/**
	 * 开始监视设备状态变化
	 * 
	 * @author wavy_zheng 2020年7月14日
	 */
	private void startMonitor() {

		if (executor == null) {

			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {
						
						//System.out.println("monitor unit state!!!");
						// 监视分区状态
						for (ControlUnit cu : mainboard.getControls()) {
							monitorUnitState(cu);
						}
						// 监视设备状态
						monitorMainboardState();
						// 监视分区同步

						Context.getSyncStepControlService().monitorSyncSteps(null);
						// 统计累计运行时间
						calculateRunMiliseconds(2000);

					} catch (Exception ex) {

						ex.printStackTrace();
						logger.error(CommonUtil.getThrowableException(ex));
					} catch (Throwable t) {

						t.printStackTrace();
						logger.error(CommonUtil.getThrowableException(t));
					}
				}

			}, 1, 2, TimeUnit.SECONDS);

		}

	}

	public void exitWorkMode(ControlUnit cu) throws AlertException {

		if (cu == null) {
			// 通知机械抬起
			if (mainboard.getMechanismManager() != null && !mainboard.getMechanismManager().isAllCylinderOpen()) {

				Context.getPcNetworkService()
						.pushSendQueue(new AlertException(AlertCode.NORMAL, I18N.getVal(I18N.CylinderOpening)));
				mainboard.getMechanismManager().writeValve(0, ValveState.OPEN);
			}

			System.out.println("exit device work mode");

			// 风机转为低速
			if (Context.getAccessoriesService().getFanManager() != null) {

				Context.getAccessoriesService().getFanManager().fan(0, Direction.IN, PowerState.ON, 0);
			}

		} else {

			logger.info("exit unit " + (cu.getIndex() + 1) + " workmode");

			// 退出分区
			for (DriverBoard db : cu.getDrivers()) {

				if (db.isUse()) {
					if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
						DriverModeData dwmd = new DriverModeData();
						dwmd.setDriverIndex(db.getDriverIndex());
						dwmd.setMode(DriverMode.NORMAL);
						Context.getDriverboardService().writeWorkMode(dwmd);

						logger.info("exit driver " + (db.getDriverIndex() + 1) + " workmode");
					}
					db.setState(LogicState.UDT);
				}
			}

		}
	}

	/**
	 * 逻辑板,回检板进入工作模式
	 * 
	 * @author wavy_zheng 2020年12月11日
	 * @param lb
	 * @throws AlertException
	 */
	public void enterWorkMode(DriverBoard db) throws AlertException {

		if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
			DriverModeData dmd = new DriverModeData();
			dmd.setDriverIndex(db.getDriverIndex());
			dmd.setMode(DriverMode.WORK);
			Context.getDriverboardService().writeWorkMode(dmd);
		}

	}

	/**
	 * 让主控进入工作模式
	 * 
	 * @author wavy_zheng 2020年7月14日
	 * @throws AlertException
	 */
	public void enterWorkMode() throws AlertException {

		System.out.println("enter device work mode");

		// // 检查机械气缸是否闭合
		if (mainboard.getMechanismManager() != null) {

			if (!mainboard.getMechanismManager().isException() && mainboard.getMechanismManager().isAllCylinderOpen()) {

				Context.getPcNetworkService()
						.pushSendQueue(new AlertException(AlertCode.NORMAL, I18N.getVal(I18N.CylinderClosing)));
				mainboard.getMechanismManager().writeValve(0, ValveState.CLOSE); // 闭合气缸
				if (mainboard.getPowerManager().getPowerSwitchState() == PowerState.ON) {

					CommonUtil.sleep(6000);
				}
			}
		}

		// 针床控制器
		if (Context.getAccessoriesService().getPingController() != null) {

			// 检查针床
			try {

				// Context.getPcNetworkService()
				// .pushSendQueue(new AlertException(AlertCode.NORMAL,
				// I18N.getVal(I18N.CylinderChecking)));
				// Context.getAccessoriesService().getPingController().checkPingState();

			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, e.getMessage());
			}

		}

		if (mainboard.getControlUnitCount() == 1) {

			if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
				// 重新下发超压保护
				ControlUnit mcu = mainboard.getControlUnitByIndex(0);

				Context.getCoreService().writeVoltageProtection(mcu, mcu.getDpd());
			}
		}

		// 检测温度是否满足启动条件
		if (Context.getAccessoriesService().getTemperatureManager() != null) {
			Context.getAccessoriesService().getTemperatureManager().checkStartup();
		}
		if (Context.getAccessoriesService().getPowerManager() != null) {
			// 防止电源在启动时被待机时长超时关闭
			Context.getAccessoriesService().getPowerManager().clearWaitSeconds();
			if (Context.getAccessoriesService().getPowerManager().getPowerSwitchState() == PowerState.OFF) {

				mainboard.setShutdown(false);
				// 打开总电源
				Context.getAccessoriesService().getPowerManager().power(PowerState.ON);
				Context.getPcNetworkService()
						.pushSendQueue(new AlertException(AlertCode.NORMAL, I18N.getVal(I18N.WaitForPowerOn)));
				// 打开回检板电源
				CommonUtil.sleep(8000);
			}

			// 整机单元时需要检测电源
			Context.getAccessoriesService().getPowerManager().checkPowers();

		}

		if (Context.getAccessoriesService().getFanManager() != null) {
			// 清空缓存
			Context.getAccessoriesService().getFanManager().clearTempCache();

			// 检测风机
			Context.getAccessoriesService().getFanManager().canStartup();

			// 打开风机
			Context.getAccessoriesService().getFanManager().openAllCoolFans();

		}

		// 消除报警
		if (mainboard.getAlertCode() != AlertCode.NORMAL) {

			Context.getAlertManager().handle(null, "", true);
		}

	}

	/**
	 * 根据用户命令创建分区
	 * 
	 * @author wavy_zheng 2020年11月14日
	 * @param data
	 * @throws AlertException
	 */
	public void createControlUnits(ControlUnitData data) throws AlertException {

		// 先停止主控工作
		stopWork();
		// 清空当前分区
		mainboard.getControls().clear();

		if (data.getMode() == ProcedureMode.DEVICE) {

			ControlUnit cu = ControlUnit.createUnit(mainboard, 0);
			mainboard.getControls().add(cu);
			cu.getDrivers().addAll(mainboard.listDrivers());

		} else if (data.getMode() == ProcedureMode.DRIVER) {

			Map<Byte, List<DriverBoard>> map = new HashMap<>();

			List<DriverBoard> drivers = mainboard.listDrivers();
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
			for (byte n = 1; n <= map.size(); n++) {

				// System.out.println(map.get(n));
				ControlUnit cu = ControlUnit.createUnit(mainboard, n - 1, map.get(n).toArray(new DriverBoard[0]));
				mainboard.getControls().add(cu);
			}

		}

		mainboard.setProcedureMode(data.getMode());

		// 生成配置文件

		// 删除所有旧配置
		File dir = new File("./config/");
		File[] files = dir.listFiles();
		for (int n = 0; n < files.length; n++) {

			if (files[n].isDirectory()) {

				continue;

			}
			if ("cfg.xml".equals(files[n].getName()) || "switch.xml".equals(files[n].getName())
					|| "tempControl.xml".equals(files[n].getName())) {

				continue;
			}
			files[n].delete(); // delete file
		}
		// 重新生成struct.xml配置文件
		Context.getFileSaveService().writeStructFile();

		// 重新加载流程
		for (ControlUnit cu : mainboard.getControls()) {

			try {
				cu.init();
				cu.writeBaseProtections();
			} catch (AlertException ex) {

				logger.info(CommonUtil.getThrowableException(ex));
				Context.getPcNetworkService().pushSendQueue(ex);
			}
		}

		// 重新开始工作
		startWork();

	}

	/**
	 * 紧急修复分区流程
	 * 
	 * @author wavy_zheng 2021年4月22日
	 * @param cu
	 * @throws AlertException
	 */
	public void recovery(ControlUnit cu) throws AlertException {

		/*
		 * if (cu.getState() != State.COMPLETE && cu.getState() != State.STOP &&
		 * cu.getState() != State.NORMAL) {
		 * 
		 * throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.CanNotRecovery));
		 * }
		 */

		List<Channel> channels = cu.listAllChannels(null);

		boolean recover = false;
		for (Channel channel : channels) {

			if (channel.getState() == ChnState.ALERT || channel.getState() == ChnState.STOP) {

				channel.setState(ChnState.PAUSE);
				recover = true;
			} else if (channel.getState() == ChnState.UDT) {

				channel.setState(ChnState.PAUSE);
				channel.setStepIndex(1);
				channel.setLoopIndex(1);
				Step step = channel.getProcedureStep(1);
				if (step != null) {
					channel.setWorkMode(step.workMode);
				}
				recover = true;
			}

		}
		if (recover) {

			cu.setState(State.PAUSE);

			// 写入备份路径，防止意外
			Context.getFileSaveService().writeUnitRuntimeState(null, "");

		} else {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.CanNotRecovery));
		}

	}

	/**
	 * 重置当前设备
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void reset(ControlUnit cu) throws IOException, InterruptedException {

		if (mainboard.getState() == State.FORMATION) {

			return;
		}

		// 删除离线数据
		FileUtil.removeAllFiles(FileSaveService.OFFLINE_DIR_PATH);
		// 删除通道状态
		String path = cu.getMainBoard().getControlUnitCount() == 1 ? FileSaveService.STRUCT_FILE_PATH + "/device"
				: FileSaveService.STRUCT_FILE_PATH + "/" + cu.getIndex();
		path += "/runtime.xml";
		File runtimeFile = new File(path);
		if (runtimeFile.exists()) {

			runtimeFile.delete();
		}

		// 删除struct文件夹
		// FileUtil.removeAllFiles("./config/struct");
		// 删除struct.xml
		new File("./config/struct.xml").delete();

		new File("./config/life.xml").delete();
		// 删除offline文件夹
		FileUtil.removeAllFiles("./offline");

		if (Environment.isLinuxEnvironment()) {
			// 重启设备
			Environment.executeSysCmd(new String[] { "reboot" });
		}
	}

	/**
	 * 修复各通道启动状态
	 * 
	 * @author wavy_zheng 2021年4月14日
	 * @param lb
	 * @return
	 */
	private Logic2StartupInitData createLogicRecoveryInitStartupData(LogicBoard lb) {

		Logic2StartupInitData data = new Logic2StartupInitData();
		data.setUnitIndex(lb.getLogicIndex());
		List<ChnInitData> list = new ArrayList<>();
		for (DriverBoard driver : lb.getDrivers()) {

			for (int n = 0; n < driver.getChannelCount(); n++) {

				Channel chn = driver.getChannel(n);
				if (chn.getState() == ChnState.ALERT || chn.getState() == ChnState.STOP) {

					ChnInitData cid = new ChnInitData();
					cid.index = Context.getChannelIndexService().getActualLogicChnIndexBy(lb.getLogicIndex(), n);
					cid.loopIndex = chn.getLoopIndex();
					cid.stepIndex = chn.getStepIndex();
					cid.stepTime = chn.getStepElapseMiliseconds();
					cid.totalCapacity = chn.getStepCapacity();

					list.add(cid);
				}

			}

		}
		logger.info("recovery chn init:" + list);
		data.setChnInitDatas(list);
		return data;
	}

	private void writeDriversResumeData(int driverIndex, List<Channel> channelsInDriver, boolean resumeAlert)
			throws AlertException {

		DriverResumeData dsd = new DriverResumeData();
		dsd.setDriverIndex(driverIndex);
		for (int n = 0; n < channelsInDriver.size(); n++) {

			Channel chn = channelsInDriver.get(n);

			chn.log("create startup init Data");
			if (/* chn.getState() == ChnState.UDT || */ chn.getState() == ChnState.PAUSE
					|| (resumeAlert && chn.getState() == ChnState.ALERT)) {

				ResumeUnit unit = new ResumeUnit();
				unit.chnIndex = chn.getChnIndex();
				unit.capacity = chn.getStepCapacity();
				unit.miliseconds = chn.getStepElapseMiliseconds();
				unit.loopIndex = chn.getLoopIndex();
				unit.stepIndex = chn.getStepIndex();
				dsd.getUnits().add(unit);
			}

		}

		if (!dsd.getUnits().isEmpty()) {
			// 发送
			Context.getDriverboardService().writeResume(dsd);
		}

	}

	/**
	 * 为分区多个驱动板下发恢复初始步次信息
	 * 
	 * @author wavy_zheng 2021年6月24日
	 * @param cu
	 * @param resumeAlert
	 * @return
	 * @throws AlertException
	 */
	private void writeDriversResumeData(DriverBoard db, boolean resumeAlert) throws AlertException {

		writeDriversResumeData(db.getDriverIndex(), db.getChannels(), resumeAlert);

	}

	/**
	 * 创建分区逻辑板启动初始化数据
	 * 
	 * @author wavy_zheng 2020年12月8日
	 * @param cu
	 * @return
	 */
	private Logic2StartupInitData createLogicInitStartupData(LogicBoard lb, boolean resumeAlert) {

		Logic2StartupInitData data = new Logic2StartupInitData();
		data.setUnitIndex(lb.getLogicIndex());
		List<ChnInitData> list = new ArrayList<>();
		for (DriverBoard driver : lb.getDrivers()) {

			for (int n = 0; n < driver.getChannelCount(); n++) {

				Channel chn = driver.getChannel(n);
				if (chn.isStartuped()) { // 无启动过的情况不下发
					chn.log("create startup init Data");
					if (chn.getState() == ChnState.UDT || chn.getState() == ChnState.PAUSE
							|| (resumeAlert && chn.getState() == ChnState.ALERT)) {

						ChnInitData cid = new ChnInitData();
						// cid.index =
						// Context.getChannelIndexService().getActualLogicChnIndexBy(lb.getLogicIndex(),
						// chn.getLogicChnIndex());
						cid.loopIndex = chn.getLoopIndex();
						cid.stepIndex = chn.getStepIndex();
						cid.stepTime = chn.getStepElapseMiliseconds();
						cid.totalCapacity = chn.getStepCapacity();

						list.add(cid);
					}
				} else {

					chn.clearData(); // 清空数据，准备单点启动
					chn.setStepIndex(1);
					chn.setLoopIndex(1);
				}

			}

		}
		logger.info("resume chn init:" + list);
		data.setChnInitDatas(list);
		return data;

	}

	/**
	 * 逻辑板所有通道都能准备启动?用于同步模式
	 * 
	 * @author wavy_zheng 2020年12月8日
	 * @param lb
	 * @return
	 */
	private List<Channel> getAllChnReady(LogicBoard lb) {

		List<Channel> channels = new ArrayList<>();
		for (DriverBoard driver : lb.getDrivers()) {

			for (int n = 0; n < driver.getChannelCount(); n++) {

				if (driver.getChannel(n).getState() == ChnState.UDT
						|| driver.getChannel(n).getState() == ChnState.PAUSE) {

					channels.add(driver.getChannel(n));
				}
			}
		}

		return channels;
	}

	/**
	 * 复位通道，准备测试
	 * 
	 * @author wavy_zheng 2020年12月11日
	 * @param cu
	 */
	public void resetAllChannels(ControlUnit cu) {

		List<Channel> channels = cu.listAllChannels(null);
		for (Channel chn : channels) {

			chn.reset();
		}
	}

	/**
	 * 修复流程，应用紧急情况，保证数据能正常上传
	 * 
	 * @author wavy_zheng 2021年4月14日
	 * @param cu
	 * @throws AlertException
	 */
	/*
	 * public void recoveryProcedure(ControlUnit cu) throws AlertException {
	 * 
	 * if (mainboard.getState() == State.MAINTAIN) {
	 * 
	 * throw new AlertException(AlertCode.LOGIC,
	 * I18N.getVal(I18N.MaintainNotExecute)); }
	 * 
	 * if (mainboard.getProcedure().getStepMode() == StepMode.SYNC) {
	 * 
	 * throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.SyncNotSupport));
	 * }
	 * 
	 * // 将风机调到最大 if (Context.getAccessoriesService().getFanManager() != null) {
	 * Context.getAccessoriesService().getFanManager().fan(0, Direction.IN,
	 * PowerState.ON, 2); }
	 * 
	 * Map<String, List<DriverBoard>> map = getDriverByPortMap(cu); final
	 * CountDownLatch latch = new CountDownLatch(map.size()); final
	 * List<AlertException> exceptions = new ArrayList<>(); //并行发送协议 for
	 * (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
	 * 
	 * new Thread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * 
	 * List<DriverBoard> drivers = map.get(it.next());
	 * 
	 * try { for (DriverBoard db : drivers) {
	 * 
	 * //下发流程 DriverStepData dsp = convertProcedureFrom(cu.getProcedure());
	 * dsp.setDriverIndex(db.getDriverIndex()); // 别忘了设置驱动板地址
	 * Context.getDriverboardService().writeSteps(dsp);
	 * 
	 * //下发接续信息 writeDriversResumeData(db, resumeAlert);
	 * 
	 * } } catch (AlertException ex) {
	 * 
	 * exceptions.add(ex); ex.printStackTrace();
	 * //Context.getPcNetworkService().pushSendQueue(ex);
	 * 
	 * 
	 * } finally {
	 * 
	 * latch.countDown(); }
	 * 
	 * }
	 * 
	 * }).start();
	 * 
	 * } boolean allChnOperate = false; for (DriverBoard db : cu.getDrivers()) {
	 * 
	 * if (!db.isUse()) {
	 * 
	 * continue; } List<Channel> channels = new ArrayList<>();
	 * 
	 * DriverStepData lpd = convertProcedureFrom(cu.getProcedure());
	 * lpd.setDriverIndex(db.getDriverIndex()); // 写入流程
	 * Context.getDriverboardService().writeSteps(lpd);
	 * 
	 * // 重新将分区内每个步次的恢复信息写入逻辑板
	 * logger.info("write control unit resume info to logic " + (lb.getLogicIndex()
	 * + 1)); Context.getLogicboardService().writeProceedStates(
	 * createLogicRecoveryInitStartupData(lb));
	 * 
	 * // 让对应的逻辑板，回检板进入工作模式 enterWorkMode(lb.getLogicIndex());
	 * 
	 * // 假设可以整区启动 allChnOperate = true; // 如果分区内有锁定通道，或者有压差保护,不能使用整区启动 for (Channel
	 * channel : lb.getChannels()) {
	 * 
	 * if (channel.getState() == ChnState.STOP || channel.getState() ==
	 * ChnState.ALERT) {
	 * 
	 * if (channel.getState() == ChnState.ALERT &&
	 * ProtectionFilterService.isVoltageOffsetAlert(channel) != null) {
	 * 
	 * allChnOperate = false; continue; // 不启动已经压差保护的通道 } channels.add(channel); }
	 * else {
	 * 
	 * allChnOperate = false; }
	 * 
	 * }
	 * 
	 * if (allChnOperate) {
	 * 
	 * logger.info("recovery all channels"); channels = lb.getChannels(); // 启动或关闭
	 * Context.getLogicboardService().operateChns(lb.getLogicIndex(), OptMode.SYNC,
	 * SwitchState.ON, null); } else {
	 * 
	 * logger.info("recovery selected channels:" + channels); if
	 * (!channels.isEmpty()) {
	 * Context.getLogicboardService().operateChns(lb.getLogicIndex(), OptMode.ASYNC,
	 * SwitchState.ON, channels); } }
	 * 
	 * // 变更模型数据 for (Channel channel : channels) {
	 * 
	 * channel.recoveryStep(); }
	 * 
	 * }
	 * 
	 * // 设置操作标志 cu.setOperateState(MainEnvironment.State.FORMATION);
	 * cu.setOpertateDate(new Date());
	 * 
	 * }
	 */

	/**
	 * 检查驱动板超温254的情况，这种情况必须要进行电源复位才可恢复
	 * 
	 * @author wavy_zheng 2021年6月11日
	 * @param lb
	 */
	private void checkDriverLoss(ControlUnit cu) throws AlertException {

		for (int n = 0; n < cu.getLogics().size(); n++) {

			for (DriverBoard db : cu.getLogics().get(n).getDrivers()) {

				if (db.getTemp1() == 254.0 || db.getTemp2() == 254.0) {

					int logicIndex = cu.getLogics().get(n).getLogicIndex();

					if (Context.getPowerProvider() != null) {

						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
								I18N.getVal(I18N.DriverRest, logicIndex + 1));

					}
					break;

				}

			}

		}

	}

	private static void appendChannelInDriverMode(Map<LogicBoard, List<Channel>> operateChns, Channel channel) {

		// if (operateChns.containsKey(channel.getLogic())) {
		//
		// operateChns.get(channel.getLogic()).add(channel);
		// } else {
		//
		// List<Channel> list = new ArrayList<>();
		// list.add(channel);
		// operateChns.put(channel.getLogic(), list);
		// }

	}

	/**
	 * 特殊启动单板单流程
	 * 
	 * @author wavy_zheng 2021年6月24日
	 * @param cu
	 * @param optState
	 * @param resumeAlert
	 * @throws AlertException
	 */
	/*
	 * public void executeDriverProcedure(ControlUnit cu, State optState, boolean
	 * resumeAlert) throws AlertException {
	 * 
	 * Map<LogicBoard, List<Channel>> operateChns = new HashMap<>(); // 能操作启动的通道
	 * SwitchState ss = optState == State.FORMATION || optState == State.STARTUP ?
	 * SwitchState.ON : SwitchState.OFF; if (ss == SwitchState.ON) {
	 * 
	 * Logic2ProcedureData lpd = convertProcedureFrom(cu.getProcedure()); for
	 * (DriverBoard db : cu.getDrivers()) {
	 * 
	 * // lpd.setUnitIndex(db.getLogicBoard().getLogicIndex());
	 * lpd.setDriverIndex(db.getDriverIndex()); // 写入流程 try {
	 * Context.getLogicboardService().writeProcedure(lpd); } catch (AlertException
	 * ex) {
	 * 
	 * throw new AlertException(ex.getAlertCode(), "驱动板" +
	 * (db.getDeviceDriverIndex() + 1) + "下发流程错误:" + ex.getMessage()); }
	 * 
	 * } if (optState == State.FORMATION) {
	 * 
	 * writeDriversResumeData(cu, false);
	 * 
	 * } // 进入工作模式 enterWorkMode(cu);
	 * 
	 * }
	 * 
	 * for (Channel channel : cu.listAllChannels(null)) {
	 * 
	 * if (ss == SwitchState.ON) { if (channel.getState() != ChnState.NONE &&
	 * channel.getState() != ChnState.RUN && channel.getState() != ChnState.CLOSE) {
	 * 
	 * if (optState == State.STARTUP) {
	 * 
	 * if (channel.getState() == ChnState.ALERT &&
	 * ProtectionFilterService.isVoltageOffsetAlert(channel) != null) {
	 * 
	 * channel.log("resume fail voltage offset:" + channel.getTouchData());
	 * 
	 * } else {
	 * 
	 * appendChannelInDriverMode(operateChns, channel); channel.reset();
	 * 
	 * } } else {
	 * 
	 * // 注意恢复时，已完成的通道不用恢复 if (channel.getState() != ChnState.COMPLETE &&
	 * channel.getState() != ChnState.UDT && channel.getState() != ChnState.STOP &&
	 * channel.getState() != ChnState.ALERT) {
	 * 
	 * appendChannelInDriverMode(operateChns, channel); channel.reset(); }
	 * 
	 * }
	 * 
	 * } } else {
	 * 
	 * appendChannelInDriverMode(operateChns, channel); }
	 * 
	 * }
	 * 
	 * logger.info(ss + " selected channels:" + operateChns); // 开始启动
	 * 
	 * if (!operateChns.isEmpty()) {
	 * 
	 * CountDownLatch latch = new CountDownLatch(operateChns.size());
	 * ExecutorService executor = Executors.newCachedThreadPool(); for
	 * (Iterator<LogicBoard> it = operateChns.keySet().iterator(); it.hasNext();) {
	 * 
	 * final LogicBoard lb = it.next(); executor.execute(new Runnable() {
	 * 
	 * @Override public void run() {
	 * 
	 * try {
	 * 
	 * Context.getLogicboardService().operateChns(lb.getLogicIndex(), OptMode.ASYNC,
	 * ss, operateChns.get(lb));
	 * 
	 * // 更改通道状态 for (Channel channel : operateChns.get(lb)) {
	 * 
	 * switch (optState) {
	 * 
	 * case STARTUP: channel.startupStep(); break; case FORMATION:
	 * channel.resumeStep(false); break; case PAUSE: channel.pauseStep(); break;
	 * case STOP: channel.stopStep(); break; case ALERT: break;
	 * 
	 * } }
	 * 
	 * } catch (AlertException ex) {
	 * 
	 * Context.getPcNetworkService().pushSendQueue( new
	 * AlertException(ex.getAlertCode(), (ss == SwitchState.ON ? "打开" : "关闭") +
	 * "逻辑板" + (lb.getLogicIndex() + 1) + "通道错误:" + ex.getMessage()));
	 * 
	 * } finally {
	 * 
	 * latch.countDown(); }
	 * 
	 * }
	 * 
	 * }); }
	 * 
	 * try { latch.await(); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * }
	 */

	/**
	 * 特殊启动驱动板； 逻辑板有问题的通道即不启动，做保护处理
	 * 
	 * @author wavy_zheng 2021年6月5日
	 * @param lb
	 * @param optState
	 * @throws AlertException
	 */
	public void executeDriverProcedureEx(DriverBoard db, State optState, boolean resumeAlert) throws AlertException {

		SwitchState ss = optState == State.FORMATION || optState == State.STARTUP ? SwitchState.ON : SwitchState.OFF;
		List<Channel> operateChns = new ArrayList<>(); // 能操作启动的通道

		if (ss == SwitchState.ON) {

			DriverStepData lpd = convertProcedureFrom(db.getControlUnit().getProcedure());
			lpd.setDriverIndex(db.getDriverIndex());
			lpd.setChnFlag((short) 0xffff); // 选择所有通道
			// 写入流程
			logger.info("writeSteps driver " + db.getDriverIndex() + ", size = " + lpd.getSteps().size());
			Context.getDriverboardService().writeSteps(lpd);

			if (optState == State.FORMATION) {

				// 重新将分区内每个步次的恢复信息写入逻辑板
				logger.info("write control unit resume info to driver " + (db.getDriverIndex() + 1));
				writeDriversResumeData(db, resumeAlert);
			}
			// 让对应的驱动板进入工作模式
			enterWorkMode(db);

			// 如果分区内有锁定通道，或者有压差保护,不能使用整区启动
			for (Channel channel : db.getChannels()) {

				if (channel.getState() != ChnState.NONE && channel.getState() != ChnState.RUN
						&& channel.getState() != ChnState.CLOSE) {

					if (optState == State.STARTUP) {

						if (channel.getState() == ChnState.ALERT
								&& ProtectionFilterService.isVoltageOffsetAlert(channel) != null) {

							channel.log("resume fail voltage offset:" + channel.getTouchData());

						} else {

							operateChns.add(channel);
						}
					} else {

						// 注意恢复时，已完成的通道不用恢复
						if (channel.getState() != ChnState.COMPLETE && channel.getState() != ChnState.UDT
								&& channel.getState() != ChnState.STOP && channel.getState() != ChnState.ALERT
								&& !channel.isSyncSteping()) {

							operateChns.add(channel);
						}

					}

				}

			}

		} else {

			// 全部关闭
			operateChns.addAll(db.getChannels());

		}

		// 如果是启动，则需要先复位
		if (optState == State.STARTUP) {

			if (MainBoard.startupCfg.isUseVirtualData()) {
				// stimulatePowerOff(60); //模拟断电
			}
			for (Channel chn : operateChns) {

				chn.reset(); // 复位到启动状态
			}
			CommonUtil.sleep(200);

		}

		if (!operateChns.isEmpty()) {

			logger.info(ss + " selected channels:" + operateChns);
			DriverOperateData operate = new DriverOperateData();
			short optFlag = 0;
			operate.setDriverIndex(db.getDriverIndex());
			for (Channel chn : operateChns) {

				optFlag |= 0x01 << chn.getChnIndex();
			}
			operate.setOptFlag(optFlag);
			operate.setOpen(ss == SwitchState.ON);

			Context.getDriverboardService().writeOperate(operate);

		}

		// 变更模型数据
		for (Channel channel : operateChns) {

			switch (optState) {

			case STARTUP:
				channel.startupStep();
				break;
			case FORMATION:
				channel.resumeStep(false);
				break;
			case PAUSE:
				channel.pauseStep();
				break;
			case STOP:
				channel.stopStep();
				break;
			case ALERT:
				break;

			}

		}

	}

	/**
	 * 整区启动流程
	 * 
	 * @author wavy_zheng 2020年11月14日
	 * @param cu
	 *            null表示所有分区，即整机操作
	 * @param optState
	 *            操作类型
	 * @throws AlertException
	 * 
	 */
	public void executeProcedure(ControlUnit cu, State optState) throws AlertException {

		logger.info(optState + " control unit:" + cu.getIndex());
		System.out.println(optState + " control unit:" + cu.getIndex());
		if (optState != State.STARTUP && optState != State.FORMATION && optState != State.PAUSE
				&& optState != State.STOP && optState != State.ALERT) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.OperationError, optState));
		}

		if (mainboard.getState() == State.MAINTAIN) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.MaintainNotExecute));
		}

		SwitchState ss = optState == State.FORMATION || optState == State.STARTUP ? SwitchState.ON : SwitchState.OFF;
		if (ss == SwitchState.ON && (cu.getProcedure() == null || cu.getProcedure().getStepCount() == 0)) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ProcedureNotExist));
		}
		if (Context.getAccessoriesService().getPowerManager() != null) {
			Context.getAccessoriesService().getPowerManager().setRecovertyCount(0);
		}

		if (ss == SwitchState.ON) {

			enterWorkMode(); // 设备进入工作模式

			// 将风机调到最大
			if (Context.getAccessoriesService().getFanManager() != null) {
				Context.getAccessoriesService().getFanManager().fan(0, Direction.IN, PowerState.ON, 2);
			}

		}

		Context.getSyncStepControlService().resetSync(cu);

		if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {

			final Map<String, List<DriverBoard>> downloadDrivers = getDriverByPortMap(cu);

			if (!downloadDrivers.isEmpty()) {

				final CountDownLatch latch = new CountDownLatch(downloadDrivers.size());
				final List<AlertException> exceptions = new ArrayList<>();
				for (Iterator<String> it = downloadDrivers.keySet().iterator(); it.hasNext();) {

					final String portName = it.next();
					new Thread(new Runnable() {

						@Override
						public void run() {

							List<DriverBoard> drivers = downloadDrivers.get(portName);

							try {
								for (DriverBoard db : drivers) {

									if (!db.isUse()) {

										continue;
									}

									executeDriverProcedureEx(db, optState, false);

								}
							} catch (AlertException ex) {

								logger.error(CommonUtil.getThrowableException(ex));
								exceptions.add(ex);
							}

							finally {

								latch.countDown();

							}

						}

					}).start();

				}

				// 等待分区操作完成
				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!exceptions.isEmpty()) {

					throw exceptions.get(0);
				}

			}
		} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

			for (DriverBoard db : cu.getDrivers()) {

				if (!db.isUse()) {

					continue;
				}

				executeDriverProcedureEx(db, optState, false);

			}

		}

		// 设置操作标志
		cu.setOperateState(optState);
		cu.setOpertateDate(new Date());

	}

	/**
	 * 按照逻辑板通道进行划分
	 * 
	 * @author wavy_zheng 2020年11月14日
	 * @param channels
	 * @return
	 * @throws AlertException
	 */
	private Map<Integer, List<Channel>> classifyChannelsInLogic(List<Channel> channels) throws AlertException {

		Map<Integer, List<Channel>> map = new HashMap<>();
		ControlUnit unit = null;
		for (Channel channel : channels) {

			if (unit == null) {

				unit = channel.getControlUnit();
			}
			if (channel.getControlUnit() != unit) {

				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ChnNotAllInUnit));
			}

			// if (!map.containsKey(channel.getLogicIndex())) {
			//
			// List<Channel> list = new ArrayList<>();
			// list.add(channel);
			// map.put(channel.getLogicIndex(), list);
			// } else {
			//
			// map.get(channel.getLogicIndex()).add(channel);
			// }

		}

		return map;

	}

	/**
	 * 紧急关闭逆变电源并暂停整机流程，属于设备中最高级别的保护
	 * 
	 * @author wavy_zheng 2020年11月27日
	 * @throws AlertException
	 */
	public void emergencyShutdownDevice() throws AlertException {

		// if (Context.getAccessoriesService().getPowerManager().getPowerSwitchState()
		// == PowerState.ON) {

		logger.info("emergencyShutdownDevice");

		// 暂停设备流程
		try {
			emergencyPause(null);
		} catch (AlertException ex) {

			logger.error(CommonUtil.getThrowableException(ex));

		} finally {

			// 关闭电源
			Context.getAccessoriesService().getPowerManager().power(PowerState.OFF);

		}
		// }

	}

	/**
	 * 设备断电紧急处理
	 * 
	 * @author wavy_zheng 2020年12月16日
	 */
	public void powerOff() {

		logger.info("handle power off");

		if (Context.getPowerProvider() != null) {

			logger.info("cut off power");
			Context.getPowerProvider().powerOff(); // 拉低电平，进入掉电模式
		}

		// 写入所有ID
		// try {
		// Context.getFileSaveService().writeIdentityFile();
		// } catch (AlertException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// 停止采集
		Context.getCoreService().stopWork();
		Context.getAccessoriesService().stopWork();
		// 检测到断电
		if (mainboard.isInitOk()) { // 读到设备状态后,否则将不做写入处理

			if (mainboard.getState() == State.FORMATION || mainboard.getState() == State.PAUSE) {

				if (mainboard.getState() == State.FORMATION) {

					Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
							I18N.getVal(I18N.PowerOffPauseDevice));

				}
				Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
						I18N.getVal(I18N.SavePowerOffStateData));
				// 保存离线状态

				for (ControlUnit cu : mainboard.getControls()) {

					if (cu.getState() == State.FORMATION || cu.getState() == State.PAUSE) {

						cu.poweroffPause();
						try {
							Context.getFileSaveService().writeUnitRuntimeState(cu, null);// 写入通道状态
							Context.getFileSaveService().writeOfflineToDisk(cu); // 写入缓存数据

						} catch (AlertException e) {

							Context.getPcNetworkService().pushSendQueue(e);
						}

					}
				}

			}
		}

		if (mainboard.getState() == State.FORMATION) {

			mainboard.setState(State.PAUSE);
		}
		// 关闭设备电源
		mainboard.setPoweroff(true);

		if (Context.getAlertManager() != null) {

			logger.info("alert power off");
			Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.OFFPOWER, I18N.getVal(I18N.PowerOffState));
			// getAlertManager().handle(AlertCode.OFFPOWER, "主控板将在30s后断电,系统进入断电待机状态",
			// false);
		}

		CommonUtil.sleep(3000);
		logger.info("disconnect network");
		Context.getPcNetworkService().closeService();
	}

	/**
	 * 紧急暂停分区或设备
	 * 
	 * @author wavy_zheng 2020年11月27日
	 * @param cu
	 *            null会暂停所有分区
	 */
	public void emergencyPause(ControlUnit cu) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				emergencyPause(unit); // 递归
			}

		} else if (cu.getState() == State.FORMATION) {

			cu.clearAllOperations(); // 清空当前所有的操作
			StartupData sd = new StartupData();
			sd.setState(State.PAUSE);
			executeProcedure(cu, State.PAUSE);

		}

	}

	/**
	 * 处理设备报警
	 * 
	 * @author wavy_zheng 2020年11月27日
	 * @param code
	 *            报警码
	 * @param message
	 *            报警消息
	 * @param cancel
	 *            true表示取消该类型的报警;false产生该类型的报警
	 * @throws AlertException
	 */
	public void handleDeviceAlert(AlertCode code, String message, boolean cancel) throws AlertException {

		if (mainboard.isPoweroff()) {
			// 设备处于掉电或初始化状态，不响应任何报警
			return;
		}

	}

	/**
	 * 多个或1个通道报警
	 * 
	 * @author wavy_zheng 2020年12月28日
	 * @param channels
	 * @param alertCode
	 * @param alertInfo
	 * @throws AlertException
	 */
	public void executeChannelsAlertInLogic(AlertCode alertCode, String alertInfo, Channel... channels)
			throws AlertException {

		// 变更模型数据
		for (Channel channel : channels) {

			if (channel.getAlertCode() != MainEnvironment.AlertCode.DEVICE_ERROR
					&& alertCode == MainEnvironment.AlertCode.DEVICE_ERROR) {

				channel.setAlertCode(AlertCode.DEVICE_ERROR);
				channel.alert(AlertCode.DEVICE_ERROR, alertInfo);
			}
			if (channel.getState() == ChnState.RUN || channel.getState() == ChnState.UDT
					|| channel.getState() == ChnState.PAUSE || channel.getState() == ChnState.COMPLETE) {

				channel.alert(alertCode, alertInfo);
			}
		}

		final Map<DriverBoard, List<Channel>> driverChnMap = classifyChannelsInDriver(Arrays.asList(channels));
		if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {

			final Map<String, List<DriverBoard>> portMap = getDriverByPortMap(driverChnMap.keySet());

			final CountDownLatch latch = new CountDownLatch(portMap.size());
			final List<AlertException> exceptions = new ArrayList<>();
			for (Iterator<String> it = portMap.keySet().iterator(); it.hasNext();) {

				final String portName = it.next();
				new Thread(new Runnable() {

					@Override
					public void run() {

						List<DriverBoard> driverList = portMap.get(portName);

						try {
							for (DriverBoard db : driverList) {

								DriverOperateData operate = new DriverOperateData();
								operate.setDriverIndex(db.getDriverIndex());
								short flag = 0;
								for (Channel channel : driverChnMap.get(db)) {

									flag |= 0x01 << channel.getChnIndex();
								}
								operate.setOptFlag(flag);
								operate.setOpen(false);

								Context.getDriverboardService().writeOperate(operate);

							}

						} catch (AlertException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							exceptions.add(e);
						} finally {

							latch.countDown();
						}

					}

				}).start();

			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!exceptions.isEmpty()) {

				throw exceptions.get(0);
			}

		} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

			for (Iterator<DriverBoard> it = driverChnMap.keySet().iterator(); it.hasNext();) {

				DriverBoard db = it.next();
				DriverOperateData operate = new DriverOperateData();
				operate.setDriverIndex(db.getDriverIndex());
				short flag = 0;
				for (Channel channel : driverChnMap.get(db)) {

					flag |= 0x01 << channel.getChnIndex();
				}
				operate.setOptFlag(flag);
				operate.setOpen(false);

				Context.getDriverboardService().writeOperate(operate);

			}

		}

	}

	/**
	 * 创建单通道初始状态对象
	 * 
	 * @author wavy_zheng 2021年6月5日
	 * @param channel
	 * @return
	 */
	private Logic2StartupInitData createStartupInitData(Channel channel) {

		Logic2StartupInitData initData = new Logic2StartupInitData();

		ChnInitData cid = new ChnInitData();
		// cid.index = channel.getLogicChnIndex();
		cid.loopIndex = channel.getLoopIndex();
		cid.stepIndex = channel.getStepIndex();
		cid.stepTime = channel.getStepElapseMiliseconds();
		cid.totalCapacity = channel.getStepCapacity();

		List<ChnInitData> list = new ArrayList<>();
		list.add(cid);

		channel.log("add resume init info: step " + cid.stepIndex + ",time " + cid.stepTime + ",capacity "
				+ cid.totalCapacity);

		// initData.setUnitIndex(channel.getLogicIndex());
		initData.setChnInitDatas(list);

		return initData;

	}

	/**
	 * 扫描驱动板中通道中同个流程同个工步的通道
	 * 
	 * @author wavy_zheng 2022年7月14日
	 * @param channels
	 *            注意此时channels必须是驱动板通道集合
	 * @param reload
	 *            true:所有通道重新下发第一步工步 ; false 可以恢复则从当前工步恢复
	 * @return
	 */
	private Map<Step, List<Channel>> classifyStepInDriver(List<Channel> channels, boolean reload) {

		Map<Step, List<Channel>> map = new HashMap<>();

		for (Channel chn : channels) {

			Step step = null;
			if (reload || chn.getStepIndex() == 0) {

				step = chn.getProcedureStep(1);
			} else {
				step = chn.getProcedureStep(chn.getStepIndex());
			}
			if (step != null) {

				if (map.containsKey(step)) {

					map.get(step).add(chn);
				} else {

					List<Channel> list = new ArrayList<>();
					list.add(chn);
					map.put(step, list);

				}

			}

		}

		return map;

	}

	/**
	 * 对要操作的通道进行分类
	 * 
	 * @author wavy_zheng 2022年1月19日
	 * @param channels
	 * @return
	 */
	private Map<DriverBoard, List<Channel>> classifyChannelsInDriver(List<Channel> channels) {

		Map<DriverBoard, List<Channel>> map = new HashMap<>();

		for (Channel chn : channels) {

			if (map.containsKey(chn.getDriverBoard())) {

				map.get(chn.getDriverBoard()).add(chn);

			} else {

				List<Channel> list = new ArrayList<>();
				list.add(chn);
				map.put(chn.getDriverBoard(), list);
			}
		}

		return map;
	}

	/**
	 * 多通道操作流程
	 * 
	 * @author wavy_zheng 2020年11月14日
	 * @param channels
	 *            1个逻辑板内的通道集合
	 * @param optState
	 * @throws AlertException
	 */
	public void executeChannelsProcedure(ChnOpt optState, Channel... channels) throws AlertException {

		List<Channel> channelStart = new ArrayList<>();
		for (Channel chn : channels) {

			if (chn.getState() == ChnState.ALERT && ProtectionFilterService.isVoltageOffsetAlert(chn) != null) {

				chn.log("voltage offset alert, can not result or start");
			} else {

				channelStart.add(chn);
				chn.log("start operate :" + optState);
			}

		}

		final Map<DriverBoard, List<Channel>> map = classifyChannelsInDriver(channelStart);

		if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
			final Map<String, List<DriverBoard>> downloadDrivers = getDriverByPortMap(map.keySet());

			final CountDownLatch latch = new CountDownLatch(downloadDrivers.size());
			final List<AlertException> exceptions = new ArrayList<>();

			for (Iterator<String> it = downloadDrivers.keySet().iterator(); it.hasNext();) {

				final String portName = it.next();
				new Thread(new Runnable() {

					@Override
					public void run() {

						List<DriverBoard> drivers = downloadDrivers.get(portName);
						SwitchState ss = optState == ChnOpt.RESUME || optState == ChnOpt.TEST ? SwitchState.ON
								: SwitchState.OFF;
						try {
							for (DriverBoard db : drivers) {

								if (!db.isUse()) {

									continue;
								}

								short optFlag = 0;
								for (Channel chn : map.get(db)) {

									optFlag |= 0x01 << chn.getChnIndex();
								}

								if (ss == SwitchState.ON) {

									// 加入逻辑板已退出工作模式，则需要重新进入工作模式，并下发流程
									if (db.getDriverMode() == DriverMode.NORMAL) {

										System.out.println("write driver " + db.getDriverIndex() + " procedure ");
										// 下发流程
										DriverStepData lpd = convertProcedureFrom(db.getControlUnit().getProcedure());
										lpd.setDriverIndex(db.getDriverIndex());
										lpd.setChnFlag(optFlag);
										// 写入流程
										Context.getDriverboardService().writeSteps(lpd);

									}

									if (optState == ChnOpt.RESUME) {

										// 写入接续信息
										if (map.containsKey(db) && !map.get(db).isEmpty()) {

											writeDriversResumeData(db.getDriverIndex(), map.get(db), true);
										}

									}
									if (db.getDriverMode() == DriverMode.NORMAL) {
										System.out.println("driver " + db.getDriverIndex() + " enter workmode ");
										enterWorkMode(db);

									}

								}

								// 关闭或打开通道
								if (map.containsKey(db) && !map.get(db).isEmpty()) {

									DriverOperateData operate = new DriverOperateData();
									operate.setDriverIndex(db.getDriverIndex());
									operate.setOptFlag(optFlag);
									operate.setOpen(ss == SwitchState.ON);
									Context.getDriverboardService().writeOperate(operate);
									CommonUtil.sleep(200);
								}

							}
						} catch (AlertException ex) {

							logger.error(CommonUtil.getThrowableException(ex));
							exceptions.add(ex);
						}

						finally {

							latch.countDown();

						}

					}

				}).start();

			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

			SwitchState ss = optState == ChnOpt.RESUME || optState == ChnOpt.TEST ? SwitchState.ON : SwitchState.OFF;

			for (Iterator<DriverBoard> it = map.keySet().iterator(); it.hasNext();) {

				DriverBoard db = it.next();
				if (!db.isUse()) {

					continue;
				}

				short optFlag = 0;
				for (Channel chn : map.get(db)) {

					optFlag |= 0x01 << chn.getChnIndex();
				}

				if (ss == SwitchState.ON) {

					if (optState == ChnOpt.RESUME) {

						// 写入接续信息
						if (map.containsKey(db) && !map.get(db).isEmpty()) {

							writeDriversResumeData(db.getDriverIndex(), map.get(db), true);
						}

					} else {

						System.out.println("write driver " + db.getDriverIndex() + " procedure ");
						// 下发流程
						DriverStepData lpd = convertProcedureFrom(db.getControlUnit().getProcedure());
						lpd.setDriverIndex(db.getDriverIndex());
						lpd.setChnFlag(optFlag);
						// 写入流程
						Context.getDriverboardService().writeSteps(lpd);

					}

				}

				// 关闭或打开通道
				if (map.containsKey(db) && !map.get(db).isEmpty()) {

					DriverOperateData operate = new DriverOperateData();
					operate.setDriverIndex(db.getDriverIndex());
					operate.setOptFlag(optFlag);
					operate.setOpen(ss == SwitchState.ON);
					Context.getDriverboardService().writeOperate(operate);
					CommonUtil.sleep(200);
				}

			}

		}

		// 变更模型数据
		for (Channel channel : channels) {

			switch (optState) {

			case TEST:
				channel.startupStep();
				break;
			case RESUME:
				channel.resumeStep(true);
				break;
			case PAUSE:
				channel.pauseStep();
				break;
			case STOP:
				channel.stopStep();
				break;

			}

		}
	}

	/**
	 * 切换工作模式，不包括启动
	 * 
	 * @author wavy_zheng 2020年12月9日
	 * @param startup
	 * @throws AlertException
	 */
	public void changeWorkMode(StartupData startup) throws AlertException {

		if (startup.getState() == State.UPGRADE) {

			if (mainboard.getState() == State.UPGRADE) {

				logger.info("mainboard is already in upgrade");
				return; // 已经进入
			}

			if (mainboard.getState() == State.FORMATION /* || mainboard.getState() == State.UPGRADE */
					|| mainboard.getState() == State.CAL || mainboard.getState() == State.JOIN) {

				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeFailWithFormation));
			}

			if (mainboard.getState() == State.PAUSE) {
				// 保存当前设备状态
				logger.info("save paused unit states to file");
				Context.getFileSaveService().writeUnitRuntimeState(null, null);
			}

			// 写入备份路径，防止意外
			Context.getFileSaveService().writeUnitRuntimeState(null, "");

			logger.info("stop pickup work");
			stopWork();
			mainboard.setState(startup.getState());

		} else {

			// 打开电源和风机
			if (Context.getAccessoriesService().getPowerManager() != null) {

				if (Context.getAccessoriesService().getPowerManager().getPowerSwitchState() == PowerState.OFF) {
					System.out.println("open inverter");
					Context.getAccessoriesService().getPowerManager().power(PowerState.ON);
					CommonUtil.sleep(300);
				}
			}
			if (Context.getAccessoriesService().getFanManager() != null) {

				Context.getAccessoriesService().getFanManager().fan(0, Direction.IN, PowerState.ON, 2);
			}

			if (startup.getState() == State.CAL || startup.getState() == State.JOIN) {

				stopWork();
				// Context.getAccessoriesService().stopWork();
			} else if (startup.getState() == State.NORMAL) {

				if (mainboard.getState() != State.NORMAL) {
					Context.getFileSaveService().writeMaintainFile(false);
					if (!MainBoard.startupCfg.isUseDebug()) {

						// 清除缓存
						/*
						 * for(int n = 0 ; n < mainboard.getTotalChnCount() ; n++) {
						 * 
						 * mainboard.getChannelByChnIndex(n).getRawCaches().clear();
						 * mainboard.getChannelByChnIndex(n).getRuntimeCaches().clear(); }
						 */

						startWork();

					}
				}
			} else if (startup.getState() == State.MAINTAIN) {

				// 维修模式
				// stopWork();
				Context.getFileSaveService().writeMaintainFile(true);

			}

			if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
				final Map<String, List<DriverBoard>> map = getDriverByPortMap(mainboard.getDriverBoards());
				final List<AlertException> exceptions = new ArrayList<>();
				final CountDownLatch latch = new CountDownLatch(map.size());
				for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {

					final String portName = it.next();
					new Thread(new Runnable() {

						@Override
						public void run() {

							List<DriverBoard> driverList = map.get(portName);

							try {

								for (DriverBoard db : driverList) {

									if (!db.isUse()) {

										continue;
									}
									DriverModeData lsd = new DriverModeData();
									lsd.setDriverIndex(db.getDriverIndex());
									boolean change = false;
									switch (startup.getState()) {

									case CAL:

										lsd.setMode(DriverMode.CAL);
										change = true;
										break;
									case NORMAL:

										if (mainboard.getState() != State.NORMAL) {

											lsd.setMode(DriverMode.NORMAL);
											change = true;

										}

										break;
									case JOIN:
										lsd.setMode(DriverMode.JOINT);
										change = true;
										break;

									}

									if (change) {

										Context.getDriverboardService().writeWorkMode(lsd);

									}

								}

							} catch (AlertException ex) {

								ex.printStackTrace();
								exceptions.add(ex);

							} finally {

								latch.countDown();
							}

						}

					}).start();

				}

				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!exceptions.isEmpty()) {

					throw exceptions.get(0);
				}
			} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

				for (DriverBoard db : mainboard.getDriverBoards()) {

					if (!db.isUse()) {

						continue;
					}
					DriverModeData lsd = new DriverModeData();
					lsd.setDriverIndex(db.getDriverIndex());
					boolean change = false;
					switch (startup.getState()) {

					case CAL:

						lsd.setMode(DriverMode.CAL);
						change = true;
						break;
					case NORMAL:

						if (mainboard.getState() != State.NORMAL) {

							lsd.setMode(DriverMode.NORMAL);
							change = true;

						}

						break;
					case JOIN:
						lsd.setMode(DriverMode.JOINT);
						change = true;
						break;

					}

					if (change) {

						Context.getDriverboardService().writeWorkMode(lsd);

					}

				}

			}

			mainboard.setState(startup.getState());

		}

	}

	/**
	 * 收集主控运行状态
	 * 
	 * @author wavy_zheng 2020年12月10日
	 * @return
	 * @throws AlertException
	 */
	public CoreData pickCoreData() throws AlertException {

		CoreData coreData = Environment.getCoreData();
		coreData.setStartupDatetime(mainboard.getStartupDatetime());
		coreData.setFactoryDatetime(mainboard.getFactoryDatetime());
		coreData.setState(mainboard.getState()); // 运行状态
		coreData.setNormal(!mainboard.isChecking());
		coreData.setRunMiliseconds(mainboard.getRunMiliseconds());
		coreData.setVersion(MainBoard.VERSION);

		return coreData;
	}

	/**
	 * 采集通道监控信息
	 * 
	 * @author wavy_zheng 2021年1月16日
	 * @return
	 * @throws AlertException
	 */
	public List<ChannelMonitorData> pickupChannelMonitorData() throws AlertException {

		List<ChannelMonitorData> list = new ArrayList<>();
		for (int n = 0; n < MainBoard.startupCfg.getDeviceChnCount(); n++) {

			ChannelMonitorData cmd = new ChannelMonitorData();
			cmd.setIndex(n);
			Channel channel = mainboard.getChannelByChnIndex(n);
			cmd.setLogicFlashOk(channel.isLogicCalFlashOk());
			cmd.setCheckFlashOk(channel.isCheckCalFlashOk());
			cmd.setState(channel.getState());
			cmd.setRunMiliseconds(channel.getRunMiliseconds());
			list.add(cmd);
		}

		return list;
	}

	// public List<ChannelMonitorData> pickupChannelMonitorData() throws
	// AlertException {
	//
	// List<ChannelMonitorData> list = new ArrayList<ChannelMonitorData>();
	//
	// for (int n = 0; n < mainboard.getTotalChnCount(); n++) {
	// Channel channel = mainboard.getChannelByChnIndex(n);
	//
	// ChannelMonitorData cmd = new ChannelMonitorData();
	// cmd.setIndex(n);
	// cmd.setLogicFlashOk(channel.isLogicCalFlashOk());
	// cmd.setCheckFlashOk(channel.isCheckCalFlashOk());
	// cmd.setRunMiliseconds(channel.getRunMiliseconds());
	// cmd.setState(channel.getState());
	// list.add(cmd);
	// }
	//
	// return list;
	//
	// }

	public List<DriverMonitorState> pickupDriverMonitorData() throws AlertException {
		List<DriverMonitorState> list = new ArrayList<>();

		for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

			DriverBoard db = mainboard.getDriverByIndex(n);
			DriverMonitorState dms = new DriverMonitorState();

			dms.setDriverIndex(n);
			dms.setState(db.getState());
			dms.setDriverMode(db.getDriverMode());
			dms.setSendPickCount(db.getSendPickupCount());

			// System.out.println("db.getRecvPickupCount() = " + db.getRecvPickupCount());
			dms.setRecvPickCount(db.getRecvPickupCount());
			dms.setCheckBurnOk(db.isCheckProgramBurnOk());
			dms.setCheckFlashOk(db.isCheckDriverFlashOk());
			dms.setCheckSoftVersion(db.getCheckSoftversion());
			dms.setCheckUuid(db.getCheckUuid());

			dms.setLogicBurnOk(db.isLogicProgramBurnOk());
			dms.setLogicFlashOk(db.isLogicDriverFlashOk());
			dms.setLogicSoftversion(db.getLogicSoftversion());
			dms.setLogicUuid(db.getLogicUuid());
			dms.setUse(db.isUse());
			dms.setResistorTemp1(db.getTemp1());
			dms.setResistorTemp2(db.getTemp2());

			dms.setDriverCheckData(db.getDriverCheckData());
			dms.setTempSoftVersion(db.getTempSoftversion());
			dms.setPickSoftVersion(db.getPickSoftversion());
			dms.setMaxCurrentSupport(db.getMaxCurrentSupport());

			list.add(dms);

		}

		return list;

	}

	/**
	 * 收集分区运行状态
	 * 
	 * @author wavy_zheng 2020年12月10日
	 * @return
	 */
	public List<ControlUnitMonitorData> pickUnitMonitorData() {

		List<ControlUnitMonitorData> list = new ArrayList<>();

		for (ControlUnit cu : mainboard.getControls()) {
			ControlUnitMonitorData cumd = new ControlUnitMonitorData();
			cumd.setState(cu.getState());
			cumd.setProcedure(cu.getProcedure() != null);
			cumd.setProtection(cu.getPnAG() != null || cu.getPnIC() != null);
			list.add(cumd);
		}
		return list;
	}

	public OfflineRunningData getOfflineRunningCfg() {
		return offlineRunningCfg;
	}

	public void setOfflineRunningCfg(OfflineRunningData offlineRunningCfg) throws AlertException {
		this.offlineRunningCfg = offlineRunningCfg;
		Context.getFileSaveService().writeOfflineRunningFile(offlineRunningCfg);
	}

	/**
	 * 模拟设备断电
	 * 
	 * @author wavy_zheng 2020年12月16日
	 * @param seconds
	 *            多少秒后断电
	 */
	public void stimulatePowerOff(int seconds) {

		// 模拟断电
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				// 模拟断电
				Context.getCoreService().powerOff();

			}

		}, new Date(new Date().getTime() + seconds * 1000));
	}

	/**
	 * 开始同步跳转
	 * 
	 * @author wavy_zheng 2020年12月21日
	 * @param cu
	 */
	public void allowSyncStepSkip(ControlUnit cu) {

	}

	/**
	 * 写入驱动板的校准系数
	 * 
	 * @author wavy_zheng 2021年1月11日
	 * @param db
	 * @throws AlertException
	 */
	private void changeDriverboardParameters(DriverBoard db, int driverIndex) {

		logger.info("change driver " + db.getDeviceDriverIndex() + "cal flash to checkboard");

		// try {
		for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {
			logger.info("write chnIndex " + i + " cal flash to checkboard");

			int chnIndex = driverIndex * MainBoard.startupCfg.getDriverChnCount() + i;

			chnIndex = Context.getChannelIndexService().getActualLogicChnIndexBy(chnIndex, chnIndex);
			//
			// Logic2CheckFlashWriteData flash = Context.getLogicboardService()
			// .readCheckFlash(db.getLogicBoard().getLogicIndex(), chnIndex);
			// if (flash != null) {
			//
			// Check2WriteCalFlashData cwcfd = new Check2WriteCalFlashData();
			// cwcfd.setUnitIndex(db.getLogicBoard().getLogicIndex());
			// cwcfd.setChnIndex(chnIndex);
			// Map<String, List<CalDot>> map = new HashMap<>();
			// map.put(Check2WriteCalFlashData.unionKey(VoltMode.Backup, Pole.NORMAL),
			// flash.getBackupPosDots());
			// map.put(Check2WriteCalFlashData.unionKey(VoltMode.Backup, Pole.REVERSE),
			// flash.getBackupNegDots());
			// map.put(Check2WriteCalFlashData.unionKey(VoltMode.Power, Pole.NORMAL),
			// flash.getPowerPosDots());
			// map.put(Check2WriteCalFlashData.unionKey(VoltMode.Power, Pole.REVERSE),
			// flash.getPowerNegDots());
			// cwcfd.setCalDotMap(map);
			// Context.getCheckboardService().writeCalibrateFlashData(cwcfd);
			//
			// }

		}
		db.setChangeResult(ChangeResult.OK);
		// } catch (AlertException ex) {
		//
		// logger.error(CommonUtil.getThrowableException(ex));
		// db.setChangeResult(ChangeResult.NG);
		//
		// }

		// finally {

		db.setChangeTime(new Date());
		// }

	}

	/**
	 * 开始自检
	 * 
	 * @author wavy_zheng 2021年1月16日
	 */
	public void startSelfChecking() {

		stopWork();

		mainboard.setChecking(true); // 进入自检标志

		Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL, I18N.getVal(I18N.SelfCheckingFirms));
		logger.info("start self checking...");

		for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

			if (mainboard.getDriverByIndex(n).isUse()) {
				try {
					DriverCheckData dcd = Context.getDriverboardService().readDriverSelfCheckInfo(n);
					mainboard.getDriverByIndex(n).setDriverCheckData(dcd);
				} catch (AlertException e) {

					if (e.getMessage().contains("failed code")) {

						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.COMM_ERROR,
								I18N.getVal(I18N.DriverSelfCheckingErrCode, n + 1, e.getMessage()));

					} else {

						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.COMM_ERROR,
								I18N.getVal(I18N.DriverSelfCheckingTimeout, n + 1));
					}

					e.printStackTrace();
				}
				// 查询软件版本
				logger.info("read driver " + (n + 1) + " softversion");
				try {
					DriverInfoData did = Context.getDriverboardService().readDriverSoftInfo(n);
					mainboard.getDriverByIndex(n).setLogicSoftversion(did.getDriverSoftVersion());
					mainboard.getDriverByIndex(n).setCheckSoftversion(did.getCheckSoftVersion());
					mainboard.getDriverByIndex(n).setPickSoftversion(did.getPickSoftVersion());
					mainboard.getDriverByIndex(n).setTempSoftversion(did.getTempSoftVersion());
					mainboard.getDriverByIndex(n).setMaxCurrentSupport((int) did.getMaxCurrent());

					logger.info("driver " + (n + 1) + "driver soft:" + did.getDriverSoftVersion());
					logger.info("driver " + (n + 1) + "check soft:" + did.getCheckSoftVersion());
					logger.info("driver " + (n + 1) + "pickup soft:" + did.getPickSoftVersion());
					logger.info("driver " + (n + 1) + "max support current:" + did.getMaxCurrent() + "A");

				} catch (AlertException e) {

					if (e.getMessage().contains("failed code")) {
						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.COMM_ERROR,
								I18N.getVal(I18N.DriverSelfCheckingVersionErrCode, n + 1, e.getMessage()));

					} else {

						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.COMM_ERROR,
								I18N.getVal(I18N.DriverSelfCheckingVersionTimeout, n + 1));
					}

					e.printStackTrace();
				}

			}
		}

		mainboard.setChecking(false); // 自检完成
		Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
				I18N.getVal(I18N.SelfCheckingFirmsOver));

		startWork();

	}

	/**
	 * 计算设备核心配件和其它配件的累计时长
	 * 
	 * @author wavy_zheng 2021年1月29日
	 */
	private void calculateRunMiliseconds(int milisecondsSpan) {

		// 核心板累计时长
		mainboard.setRunMiliseconds(mainboard.getRunMiliseconds() + milisecondsSpan);

	}

	public Map<String, List<DriverBoard>> getDriverByPortMap(Collection<DriverBoard> drivers) {

		Map<String, List<DriverBoard>> map = new HashMap<>();

		for (Iterator<DriverBoard> it = drivers.iterator(); it.hasNext();) {

			DriverBoard driver = it.next();

			DriverInfo di = MainBoard.startupCfg.getDriverInfo(driver.getDriverIndex());
			if (!map.containsKey(di.portName)) {

				List<DriverBoard> list = new ArrayList<>();
				list.add(driver);
				map.put(di.portName, list);

			} else {

				map.get(di.portName).add(driver);
			}
		}

		return map;

	}

	/**
	 * 通过串口映射驱动板集合
	 * 
	 * @author wavy_zheng 2022年1月18日
	 * @param cu
	 * @return
	 */
	public Map<String, List<DriverBoard>> getDriverByPortMap(ControlUnit cu) {

		return getDriverByPortMap(cu.getDrivers());

	}

	/**
	 * 切换单板工作模式
	 * 
	 * @author wavy_zheng 2022年1月22日
	 * @param db
	 * @param mode
	 * @throws AlertException
	 */
	public void changeWorkMode(DriverBoard db, DriverMode mode) throws AlertException {

		DriverModeData data = new DriverModeData();
		data.setDriverIndex(db.getDriverIndex());
		data.setMode(mode);

		System.out.println("write work mode:" + data + ",driverIndex = " + db.getDriverIndex());
		Context.getDriverboardService().writeWorkMode(data);

	}

	/**
	 * 单块驱动板进行升级
	 * 
	 * @author wavy_zheng 2022年1月22日
	 * @param driverIndex
	 * @param mode
	 * @throws AlertException
	 */
	public void upgradeSingleDriver(int driverIndex, UpgradeType type, String fileName) throws AlertException {

		DriverModeData dmd = new DriverModeData();
		dmd.setDriverIndex(driverIndex);
		dmd.setMode(DriverMode.NORMAL);
		Context.getDriverboardService().writeWorkMode(dmd);
		CommonUtil.sleep(100);

		// 模式转换
		dmd = new DriverModeData();
		dmd.setDriverIndex(driverIndex);
		switch (type) {

		case LogicDriver:
			dmd.setMode(DriverMode.DRIVER_UPGRADE);
			break;
		case CheckDriver:
			dmd.setMode(DriverMode.CHECK_UPGRADE);
			break;
		case PickupDriver:
			dmd.setMode(DriverMode.PICK_UPGRADE);
			break;
		case TempDriver:
			dmd.setMode(DriverMode.TEMP_UPGRADE);
			break;
		default:
			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.UpgradeFailWithFormation));

		}
		Context.getDriverboardService().writeWorkMode(dmd);

		// 开始进行数据包传送
		DriverUpgradeData upgrade = new DriverUpgradeData();
		upgrade.setDriverIndex(driverIndex);
		Context.getUpgradeManager().upgradeDriverProgram(mainboard.getDriverByIndex(driverIndex), fileName, type);

	}

	/**
	 * 根据收集过来的数据进行统计，算出样本数据，以备下次测试使用
	 * 
	 * @author wavy_zheng 2022年5月16日
	 */
	public void createSmartSamples(ControlUnit cu) {

		SmartPickupData baseDot = findBaseSmartPickupData(cu);

		for (Channel channel : cu.listAllChannels(null)) {

			if (channel.getState() != ChnState.RUN) {

				// 将多余的前缀数据去除
				int index = -1;
				for (int n = 0; n < channel.getSmartCaches().size(); n++) {

					if (channel.getSmartCaches().get(n).voltage < baseDot.voltage) {

						index = n;
					} else {

						break;
					}

				}
				if (index != -1) {

					// 清除前缀，这些数据不能作为基准采样数据计算
					channel.getSmartCaches().subList(0, index).clear();
				}

			}

		}

		// 开始计算样本值

		for (Channel channel : cu.listAllChannels(null)) {

			if (channel.getState() != ChnState.RUN) {

			}

		}
	}

	/**
	 * 找寻基准起始电压；所有数据推算是基于基点时间上开始的
	 * 
	 * @author wavy_zheng 2022年5月16日
	 * @param cu
	 * @return
	 */
	private SmartPickupData findBaseSmartPickupData(ControlUnit cu) {

		/**
		 * 找出第一个采集电压最大值
		 */
		SmartPickupData maxFirstDot = new SmartPickupData();
		for (Channel channel : cu.listAllChannels(null)) {

			if (channel.getState() != ChnState.RUN && !channel.getSmartCaches().isEmpty()) {

				if (channel.getSmartCaches().get(0).voltage > maxFirstDot.voltage) {

					maxFirstDot = channel.getSmartCaches().get(0);
				}

			}

		}

		return maxFirstDot;

	}

}
