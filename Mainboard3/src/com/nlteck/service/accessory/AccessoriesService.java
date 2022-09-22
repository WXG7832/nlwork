package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.constant.Constant.DeviceType;
import com.nlteck.constant.Constant.Window;
import com.nlteck.exception.ScreenException;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.screen.ScreenImpl;
import com.nlteck.service.accessory.controller.BeepController;
import com.nlteck.service.accessory.controller.StateLightController;
import com.nlteck.service.accessory.controller.TripleColorLightController;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.accessory.PowerSwitchData;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.TempBoardType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.WorkState;
import com.nltecklib.protocol.li.accessory.HeartBeatData;
import com.nltecklib.protocol.li.main.EnergySaveData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.FanMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.PowerMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.TempMeterMonitorData;
import com.nltecklib.protocol.li.main.TempData;
import com.nltecklib.utils.BaseUtil;
import com.rm5248.serial.SerialPort;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月23日 下午6:11:20 包含了大量的外围设备服务
 */
public class AccessoriesService {

	private MainBoard mainboard;

	private FanManager fanManager;

	private PowerManager powerManager;

	private TemperatureManager temperatureManager; // 温控管理器

	private MechanismManager mechanismManager; // 机械控制

	private ProbeManager probeManager; // 探头管理

	private ScreenController screenController; // 液晶屏

	private DoorAlertManager doorManager; // 门近开关

	private SmogAlertManager smogManager; // 烟雾报警器

	private StateLightController stateLightController; // 状态极性灯管理器

	private TripleColorLightController tripleColorLightController; // 三色灯控制器

	private BeepController beepController; // 蜂鸣报警器

	private AbsControlboardService controlboard; // 控制板

	private Logger logger;

	private ScheduledExecutorService executor = null;

	private EnergySaveData energySaveData = new EnergySaveData(); // 节能方案

	private AbsPingController pingController; // 针床控制器

	private int tickCount;

	public AccessoriesService(MainBoard mainboard) {

		this.mainboard = mainboard;

		try {
			logger = LogUtil.createLog("log/accessoriesService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化所有配件
	 * 
	 * @author wavy_zheng 2020年11月23日
	 */
	public void init(boolean virtual) throws AlertException {

		if (!MainBoard.startupCfg.isUseVirtualData()) {

			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			if (MainBoard.startupCfg.getControlInfo().use) {

				if (MainBoard.startupCfg.getControlInfo().ip != null) {

					controlboard = new NetworkControlboardService(MainBoard.startupCfg.getControlInfo().ip);
				}

				executor.scheduleWithFixedDelay(new Runnable() {

					@Override
					public void run() {

						try {
							heartbeat();
						} catch (AlertException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}, 100, 10000, TimeUnit.MILLISECONDS);
			}

		}

		// 日志清理程序
		ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();
		executor2.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {

				try {
					double useRate = Environment.detectDiskUsedRate();
					Environment.infoLogger.info("disk use rate : " + useRate + "%");
					if (useRate >= 60) {

						Environment.clearLogs();
						Environment.infoLogger.info("disk is full, clear all logs");

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}, 1, 30, TimeUnit.MINUTES);

		// 故障自动恢复
		if (MainBoard.startupCfg.getControlInfo().resetPower) {
			ScheduledExecutorService executor3 = Executors.newSingleThreadScheduledExecutor();
			executor3.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {

						Environment.infoLogger.info("start to scan driver state");
						boolean driverLoss = false;

						for (DriverBoard db : mainboard.listDrivers()) {

							if (db.getTemp1() == 254.0 || db.getTemp2() == 254.0) {

								driverLoss = true;
								boolean stopPick = false;
								if (Context.getPowerProvider() != null) {

									Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
											I18N.getVal(I18N.DriverRest, db.getDriverIndex() + 1));

									if (mainboard.getState() == State.FORMATION) {

										Environment.infoLogger.info("pause device for reset");

										Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
												I18N.getVal(I18N.PowerResetPauseDevice, db.getDriverIndex() + 1));

										Context.getCoreService().stopWork();
										Context.getAccessoriesService().stopWork();
										stopPick = true;
										// 等待设备状态变更
										CommonUtil.sleep(3000);

										for (ControlUnit cu : mainboard.getControls()) {

											cu.poweroffPause(); // 直接暂停
										}

									}

									Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
											I18N.getVal(I18N.PowerResetingDevice));
									boolean resetOk = Context.getPowerProvider().resetPower(mainboard);

									if (stopPick) {

										Context.getCoreService().startWork();
										Context.getAccessoriesService().startWork();
										CommonUtil.sleep(3000);

									}
									if (resetOk) {
										if (mainboard.getState() == State.PAUSE) {

											Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
													I18N.getVal(I18N.PowerResetResumeDevice));

											for (ControlUnit cu : mainboard.getControls()) {
												if (cu.getState() == State.PAUSE) {
													Context.getCoreService().executeProcedure(cu, State.FORMATION);
												}
											}

										}
									}

								}

								break;

							}

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}, 10, 10, TimeUnit.SECONDS);
		}

		/*
		 * ScheduledExecutorService executor4 =
		 * Executors.newSingleThreadScheduledExecutor();
		 * executor4.scheduleWithFixedDelay(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * Environment.infoLogger.info("start to scan powers state"); if
		 * (Context.getAccessoriesService().getPowerManager() != null &&
		 * Context.getAccessoriesService().getPowerManager().getRecovertyCount() < 5) {
		 * 
		 * int recoveryCount =
		 * Context.getAccessoriesService().getPowerManager().getRecovertyCount(); if
		 * (mainboard.getState() != State.FORMATION) {
		 * 
		 * boolean anyPowerFault = false; List<PowerGroup> groups =
		 * Context.getAccessoriesService().getPowerManager().getGroups(); for
		 * (PowerGroup group : groups) {
		 * 
		 * for (InverterPower inverter : group.powers) {
		 * 
		 * if (inverter.ws == WorkState.FAULT) {
		 * 
		 * anyPowerFault = true; break; } } } if (anyPowerFault) {
		 * 
		 * Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
		 * I18N.getVal(I18N.PowerFaultRecovery));
		 * Environment.infoLogger.info("start to recover powers"); try {
		 * Context.getAccessoriesService().getPowerManager().power(PowerState.OFF);
		 * CommonUtil.sleep(10000);
		 * Context.getAccessoriesService().getPowerManager().power(PowerState.ON);
		 * 
		 * } catch (AlertException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * Context.getAccessoriesService().getPowerManager().setRecovertyCount(++
		 * recoveryCount); } else {
		 * 
		 * // 置位 Context.getAccessoriesService().getPowerManager().setRecovertyCount(0);
		 * }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * }, 1, 1, TimeUnit.MINUTES);
		 */

		try {
			initScreen();
			initPowers(virtual);
			initFans(virtual);

			initMechanism(virtual);
			initProbes(virtual);
			initTempController(virtual);
			initBeeps(virtual);
			initAudioLighters(virtual);
			initStateIndicators(virtual);
			initTripleColorLights(virtual);
			initSomgs(virtual);
			initPingController(virtual);

			// 读取节能方案
			this.energySaveData = Context.getFileSaveService().readEnergySaveFile();

			logger.info("start accessories monitor");
			startWork();

		} catch (Exception ex) {

			logger.info(CommonUtil.getThrowableException(ex));

			throw new AlertException(AlertCode.INIT, I18N.getVal(I18N.InitAccessoriesFail, ex.getMessage()));
		}

	}

	/**
	 * 初始化门近管理器
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	private void initDoors() throws AlertException {

		Environment.infoLogger.info("init door alert manager!");
		if (!MainBoard.startupCfg.isUseVirtualData()) {

			doorManager = new SerialDoorAlertManager(mainboard);

		} else {

			doorManager = new VirtualDoorAlertManager(mainboard);
		}

	}

	/**
	 * 初始化烟雾报警器
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	private void initSomgs(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getSmogAlertManagerInfo().use) {
			Environment.infoLogger.info("init smog alert manager!");
			if (!virtual) {

				smogManager = new SerialSmogAlertManager(mainboard);

			} else {

				smogManager = new VirtualSmogAlertManager(mainboard);
			}

		}

	}

	private void initPingController(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getPingController().enable) {
			Environment.infoLogger.info("init ping controller!");
			if (!virtual) {

				pingController = new SerialPingController(mainboard);
			} else {

				pingController = new VirtualPingController(mainboard);
			}
		}

	}

	private void initMechanism(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getMechanismManagerInfo().use) {

			logger.info("init mechanism manager!");
			if (!virtual) {
				mechanismManager = new MonitorMechanismManager(mainboard);
			} else {

				mechanismManager = new VirtualMechanismManager(mainboard);
			}

		}

	}

	/**
	 * 初始化逆变电源
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	private void initPowers(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getPowerManagerInfo().use) {
			logger.info("init power manager");

			if (virtual) {

				powerManager = new VirtualGroupPowerManager(mainboard);

			} else {

				powerManager = new GroupPowerManager(mainboard);

			}
			// 打开电源总开关
			powerManager.power(PowerState.ON);
			logger.info("power on ");

			if (screenController != null) {

				try {
					screenController.showStartupInfo(true, I18N.getVal(I18N.InitPowersSuccess));
				} catch (ScreenException e) {

					e.printStackTrace();
					throw new AlertException(AlertCode.INIT, e.getMessage());
				}
			}
			Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
					I18N.getVal(I18N.InitPowersSuccess));

		}

	}

	/**
	 * 初始化风机
	 * 
	 * @author wavy_zheng 2020年11月23日
	 * @throws Exception
	 */
	private void initFans(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getFanManagerInfo().use) {
			Environment.infoLogger.info("init fan manager");
			if (virtual) {

				fanManager = new VirtualFanManager(mainboard);
			} else {

				fanManager = new MonitorFanManager(mainboard);

			}

			Environment.infoLogger.info("start all cool fans");
			try {

				// 初始化打开所有散热风机
				if (screenController != null) {
					screenController.showStartupInfo(true, I18N.getVal(I18N.startupFans));
				}

				fanManager.fan(0, Direction.IN, PowerState.ON, 0);
				Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
						I18N.getVal(I18N.OpenFansSuccess));
			} catch (Exception ae) {

				Environment.errLogger.error(CommonUtil.getThrowableException(ae));
			}

		}

	}

	/**
	 * 初始化所有探测器
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws Exception
	 */
	private void initProbes(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getProbeManagerInfo().use) {
			// 探头初始化
			logger.info("init probe manager!");
			if (!virtual) {
				probeManager = new SerialProbeManager(mainboard);
			} else {

				probeManager = new VirtualProbeManager(mainboard);
			}
		}

	}

	/**
	 * 初始化所有状态指示器
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws Exception
	 */
	public void initStateIndicators(boolean virtual) throws Exception {

		if (MainBoard.startupCfg.getStateLightControllerInfo().use) {

			logger.info("init state light controller");
			stateLightController = new StateLightController(mainboard, virtual);
			// 根据当前极性灯点亮,等核心部件初始化后点亮

		}
	}

	/**
	 * 初始化三色灯
	 * 
	 * @author wavy_zheng 2021年2月1日
	 * @param virtual
	 * @throws Exception
	 */
	public void initTripleColorLights(boolean virtual) throws Exception {

		if (MainBoard.startupCfg.getColorLightManagerInfo().use) {

			tripleColorLightController = new TripleColorLightController(virtual);

		}

	}

	/**
	 * 初始化所有声光报警装置
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws Exception
	 */
	private void initAudioLighters(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getColorLightManagerInfo().use) {
			// 探头初始化
			logger.info("init color light manager!");
			if (!virtual) {
				// colorLightManager = new SerialColorLightManager();
			} else {

				// colorLightManager = new VirtualColorLightManager();
			}
		}

	}

	private void initBeeps(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getBeepAlertManagerInfo().use) {
			// 探头初始化
			logger.info("init beep manager!");
			beepController = new BeepController(virtual);

		}

	}

	private void initTempController(boolean virtual) throws AlertException {

		if (MainBoard.startupCfg.getOMRManagerInfo().use) {
			// 温控表初始化
			try {

				logger.info("init temperature meter");
				if (!virtual) {

					temperatureManager = new OMRTemperatureManager(mainboard);

				} else {

					temperatureManager = new VirtualTemperatureManager(mainboard);
				}

				// 读取温控参数
				TempData tempData = Context.getFileSaveService().readTempControlFile();

				// 初始化
				temperatureManager.writeTemperature(tempData.getTempConstant());
				temperatureManager.writeTempLower(tempData.getLower());
				temperatureManager.writeTempUpper(tempData.getUpper());
				temperatureManager.setSyncTempOpen(tempData.isSyncTempOpen());
				temperatureManager.setFanManager(fanManager);

				if (tempData.isSyncTempOpen() && tempData.isTempControlOpen()) {

					logger.info("power on temperature control system!");
					// 此时设备仍在运行，自动启动温控
					try {
						if (fanManager != null) {
							fanManager.powerTurboFan(PowerState.ON);
						}
						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
								I18N.getVal(I18N.StartTempControlSystem));
						temperatureManager.power(PowerState.ON);
						Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
								I18N.getVal(I18N.InitTempControlSuccess));

					} catch (AlertException ex) {

						Context.getPcNetworkService().pushSendQueue(ex);
					}
				}

				if (screenController != null) {

					screenController.showStartupInfo(true, I18N.getVal(I18N.InitTempControlSuccess));

				}

			} catch (Exception ae) {

				logger.error(CommonUtil.getThrowableException(ae));
				throw new AlertException(AlertCode.INIT, ae.getMessage());

			}

		}

	}

	/**
	 * 初始化液晶屏
	 * 
	 * @author wavy_zheng 2020年11月23日
	 * @throws Exception
	 */
	private void initScreen() throws Exception {

		if (MainBoard.startupCfg.getScreenInfo().use) {

			Environment.infoLogger.info("init screen ");
			screenController = new ScreenController(mainboard, new ScreenImpl(
					Context.getPortManager().getPortByName(MainBoard.startupCfg.getScreenInfo().portName)));
			if (MainBoard.startupCfg.getDriverCount() == 16 && MainBoard.startupCfg.getDriverChnCount() == 16) {
				screenController.setDeviceType(DeviceType.DRIVE16_16);
			} else if (MainBoard.startupCfg.getDriverCount() == 32 && MainBoard.startupCfg.getDeviceChnCount() == 8) {
				System.out.println("load driver 32 , chn 8");
				screenController.setDeviceType(DeviceType.DRIVE32_8);
			} else {

				screenController.setDeviceType(DeviceType.DRIVE32_16);
			}
			screenController.switchScreen(Window.LOADING);
			Environment.infoLogger.info("switch to loading screen ");
			screenController.showStartupInfo(true, I18N.getVal(I18N.StartingSystem));
			Environment.infoLogger.info("reset progress 0");
			screenController.showProgress(0);
			Environment.infoLogger.info("init screen controller success : " + screenController);
			Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
					I18N.getVal(I18N.InitScreenSuccess));

		}
	}

	public FanManager getFanManager() {
		return fanManager;
	}

	public PowerManager getPowerManager() {
		return powerManager;
	}

	public TemperatureManager getTemperatureManager() {
		return temperatureManager;
	}

	public MechanismManager getMechanismManager() {
		return mechanismManager;
	}

	public ProbeManager getProbeManager() {
		return probeManager;
	}

	public ScreenController getScreenController() {
		return screenController;
	}

	public DoorAlertManager getDoorManager() {
		return doorManager;
	}

	public SmogAlertManager getSmogManager() {
		return smogManager;
	}

	public StateLightController getStateLightController() {
		return stateLightController;
	}

	public TripleColorLightController getTripleColorLightController() {
		return tripleColorLightController;
	}

	public BeepController getBeepController() {
		return beepController;
	}

	public void startWork() {

		if (executor == null) {

			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {

					try {

						if (temperatureManager != null && temperatureManager.isMonitor()) {
							temperatureManager.monitorTemperature();
						}
						if (powerManager != null && powerManager.isMonitor()) {

							powerManager.monitorPowers();
							powerManager.flushAllPowersRunMiliseconds(4000);
						}
						if (fanManager != null && fanManager.isMonitor()) {

							fanManager.monitorFans();
							fanManager.flushAllFansRunMiliseconds(4000);
						}

						if (++tickCount % 15 == 0) {

							// 每分钟保存一次配件寿命
							Context.getFileSaveService().writeLifeFile();

						}

					} catch (Exception ex) {

						ex.printStackTrace();
						logger.error(BaseUtil.getThrowableException(ex));

					} catch (Throwable t) {

						t.printStackTrace();
						logger.error(BaseUtil.getThrowableException(t));
					}

				}

			}, 1000, 4000, TimeUnit.MILLISECONDS);

		}
	}

	private void calculateRunMiliseconds(int milisecondSpan) {

	}

	public void stopWork() {

		if (executor != null) {

			executor.shutdown();
			executor = null;
		}

	}

	public List<FanMonitorData> pickupFanMonitorData() {

		List<FanMonitorData> list = new ArrayList<>();
		if (getFanManager() == null || !getFanManager().isUse()) {

			return list;
		}

		FanManager fanManager = Context.getAccessoriesService().getFanManager();
		if (fanManager != null) {

			for (int n = 0; n < fanManager.getHeatFanCount(); n++) {
				Fan fan = fanManager.getHeatFanByIndex(n);
				FanMonitorData data = new FanMonitorData();
				data.setIndex(n);
				data.setFanType(FanType.COOL);
				data.setNormal(fan.ws == WorkState.NORMAL);
				data.setWorking(fan.ps == PowerState.ON);
				data.setRunMiliseconds(0);
				data.setSpeed(fanManager.getFanSpeed());
				data.setRunMiliseconds(fan.getRunMiliseconds());
				list.add(data);
			}

			for (int n = 0; n < fanManager.getTurboFanCount(); n++) {

				Fan fan = fanManager.getTurboFanByIndex(n);
				FanMonitorData data = new FanMonitorData();
				data.setIndex(n);
				data.setFanType(FanType.TURBO);
				data.setNormal(fan.ws == WorkState.NORMAL);
				data.setWorking(fan.ps == PowerState.ON);
				data.setRunMiliseconds(0);
				data.setSpeed(fanManager.getFanSpeed());
				data.setRunMiliseconds(fan.getRunMiliseconds());
				list.add(data);
			}

		}

		return list;
	}

	/**
	 * 采集电源监控数据
	 * 
	 * @author wavy_zheng 2020年12月14日
	 * @return
	 */
	public List<PowerMonitorData> pickPowerMonitorData() {

		List<PowerMonitorData> list = new ArrayList<>();
		if (getPowerManager() == null || !getPowerManager().isUse()) {

			return list;
		}
		PowerManager gpm = (PowerManager) Context.getAccessoriesService().getPowerManager();
		if (gpm != null) {

			int index = 0;
			for (PowerGroup pg : gpm.getGroups()) {

				for (int n = 0; n < pg.getPowerCount(); n++) {

					InverterPower ip = pg.getPowerByIndex(n);
					PowerMonitorData pmd = new PowerMonitorData();
					pmd.setPowerType(PowerType.CHARGE);
					pmd.setIndex(index++);
					pmd.setNormal(ip.getWs() == WorkState.NORMAL);
					pmd.setWorking(ip.getPs() == PowerState.ON);
					pmd.setUse(true);
					pmd.setFaultInfo(ip.getFault());
					pmd.setRunMiliseconds(ip.getRunMiliseconds());
					list.add(pmd);
				}
			}

			for (int n = 0; n < gpm.getAuxiliaryPowerCount(); n++) {

				AuxiliaryPower ap = gpm.findAuxiliaryPowerByIndex(n);
				PowerMonitorData pmd = new PowerMonitorData();
				pmd.setPowerType(PowerType.AUXILIARY);
				pmd.setIndex(index++);
				pmd.setNormal(ap.getWs() == WorkState.NORMAL);
				pmd.setWorking(ap.getPs() == PowerState.ON);
				pmd.setUse(true);
				pmd.setRunMiliseconds(ap.getRunMiliseconds());
				list.add(pmd);

			}

		}

		return list;
	}

	/**
	 * 采集温控表监控数据
	 * 
	 * @author wavy_zheng 2020年12月14日
	 * @return
	 */
	public List<TempMeterMonitorData> pickTempMeterMonitorData() {

		List<TempMeterMonitorData> list = new ArrayList<>();
		if (Context.getAccessoriesService().getTemperatureManager() == null
				|| !Context.getAccessoriesService().getTemperatureManager().isUse()) {

			return list;
		}
		TempMeter meter = Context.getAccessoriesService().getTemperatureManager().getTempMeter();
		if (meter != null) {
			TempMeterMonitorData tmmd = new TempMeterMonitorData();
			tmmd.setOpen(meter.getPs() == PowerState.ON);
			tmmd.setOverTempAlert(meter.getOverFlag() == OverTempFlag.ALERT);
			tmmd.setIndex(meter.getIndex());
			tmmd.setTemperature(meter.getTemperature());
			tmmd.setUse(meter.isUse());
			tmmd.setNormal(meter.getWs() == WorkState.NORMAL);
			tmmd.setMeterType(TempBoardType.OMR);
			list.add(tmmd);
		}

		// 副表
		TempMeter protectMeter = Context.getAccessoriesService().getTemperatureManager().getProtectedTempMeter();
		if (protectMeter != null) {

			TempMeterMonitorData tmmd = new TempMeterMonitorData();
			tmmd.setOpen(protectMeter.getPs() == PowerState.ON);
			tmmd.setOverTempAlert(protectMeter.getOverFlag() == OverTempFlag.ALERT);
			tmmd.setIndex(protectMeter.getIndex());
			tmmd.setTemperature(protectMeter.getTemperature());
			tmmd.setUse(protectMeter.isUse());
			tmmd.setNormal(protectMeter.getWs() == WorkState.NORMAL);
			tmmd.setMeterType(TempBoardType.OMR_PROTECT);
			list.add(tmmd);
		}

		return list;

	}

	public EnergySaveData getEnergySaveData() {
		return energySaveData;
	}

	public void setEnergySaveData(EnergySaveData energySaveData) throws AlertException {
		this.energySaveData = energySaveData;

		// 写入文件
		Context.getFileSaveService().writeEnergySaveFile(energySaveData);
	}

	public void heartbeat() throws AlertException {

		if (controlboard == null) {

			String portName = MainBoard.startupCfg.getControlInfo().portName;
			SerialPort serialPort = Context.getPortManager().getPortByName(portName);
			ResponseDecorator response;
			try {
				response = SerialUtil.sendAndRecvImmediate(ProtocolType.ACCESSORY, serialPort,
						new QueryDecorator(new HeartBeatData()), 2000);
			} catch (IOException e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.CommError) + ":" + e.getMessage());
			}
		} else {
			
			try {
				controlboard.heartbeat();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC,e.getMessage());
			}
		}

	}

	public AbsPingController getPingController() {
		return pingController;
	}

	public AbsControlboardService getControlboard() {
		return controlboard;
	}
	
	

}
