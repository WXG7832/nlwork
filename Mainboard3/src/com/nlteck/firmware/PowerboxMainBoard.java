package com.nlteck.firmware;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Env;
import com.nlteck.Environment;
import com.nlteck.Environment.ChargeType;
import com.nlteck.Environment.ControlUnitListener;
import com.nlteck.RunningLamp;
import com.nlteck.constant.Constant.DeviceType;
import com.nlteck.constant.Constant.Window;
import com.nlteck.controller.PCController;
import com.nlteck.exception.ScreenException;
import com.nlteck.firmware.PowerController.PowerListener;
import com.nlteck.i18n.I18N;
import com.nlteck.screen.ScreenImpl;
import com.nlteck.service.PCNetworkService;
import com.nlteck.service.STMDeviceController;
import com.nlteck.service.StartupCfgManager;
import com.nlteck.service.StartupCfgManager.AlarmInfo;
import com.nlteck.service.StartupCfgManager.PortInfo;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.service.accessory.GroupPowerManager;
import com.nlteck.service.accessory.MonitorFanManager;
import com.nlteck.service.accessory.MonitorMechanismManager;
import com.nlteck.service.accessory.OMRTemperatureManager;
import com.nlteck.service.accessory.ScreenController;
import com.nlteck.service.accessory.SerialDoorAlertManager;
import com.nlteck.service.accessory.SerialProbeManager;
import com.nlteck.service.accessory.SerialSmogAlertManager;
import com.nlteck.service.accessory.VirtualDoorAlertManager;
import com.nlteck.service.accessory.VirtualFanManager;
import com.nlteck.service.accessory.VirtualGroupPowerManager;
import com.nlteck.service.accessory.VirtualMechanismManager;
import com.nlteck.service.accessory.VirtualProbeManager;
import com.nlteck.service.accessory.VirtualSmogAlertManager;
import com.nlteck.service.accessory.VirtualTemperatureManager;
import com.nlteck.service.accessory.controller.StateLightController;
import com.nlteck.service.accessory.manager.AlertManager;
import com.nlteck.service.alert.DeviceAudioLightAlarmController;
import com.nlteck.service.alert.VirtualAudioLightAlarmController;

import com.nlteck.util.CommonUtil;
import com.nlteck.util.SerialUtil;
import com.nltecklib.protocol.lab.Data.Generation;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.FourLightStateData;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlarmType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.check2.Check2Environment.CheckWorkMode;
import com.nltecklib.protocol.li.check2.Check2StartupData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.li.main.StartupData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.rm5248.serial.SerialPort;

/**
 * 主控板
 * 
 * @author Administrator
 * 
 */
public class PowerboxMainBoard extends MainBoard {

	/**
	 * 通信口定义
	 */
	public final static String LOGIC_PORT1 = "/dev/ttyS0";
	public final static String LOGIC_PORT2 = "/dev/ttyS1";
	public final static String CHECK_PORT1 = "/dev/ttyS2";
	public final static String CHECK_PORT2 = "/dev/ttyS3";
	public final static String CONTROLLER_PORT = "/dev/ttyS4";
	public final static String SPARE_PORT = "/dev/ttyS5";

	public static final int BAUD_RATE = 19200; // 通信波特率

	/**
	 * GPIO串口引脚定义
	 */
	public final static int LOGIC_PIN1 = 23;
	public final static int LOGIC_PIN2 = 27;
	public final static int CHECK_PIN1 = 67;
	public final static int CHECK_PIN2 = 8;
	public final static int CONTROLLER_PIN = 76;
	public final static int SPARE_PIN = 74;

	public static final double MIX_CHARGE_CURRENT = 24; // 24mA
	public static final double MAX_DEVICE_VOLTAGE = 5500; // 5v
	public static final double MAX_DEVICE_CURRENT = 15000; // 12A
	public static final double MIX_DISCHARGE_VOLTAGE = 2000; // 最低放电电压2v
	public static final double MIX_POLE_VOLTAGE = -1000; // 最小极性界定值
	public static final double MIX_RUNNING_CURRENT = 20; // 最小允许运行电流20mA,低于该值报警

	// 产品充电类型
	public static final ChargeType CHARGE_TYPE = ChargeType.A15;

	// 分区状态
	private Map<ControlUnit, ControlUnitListener> unitListeners = new HashMap<ControlUnit, ControlUnitListener>();

	/**
	 * 初始化主控板，这是主控板启动后第一个执行的方法 参数1：
	 * 启动代码bit为1打开，0关闭；从低到高分别表示逻辑板1，逻辑板2，回检板1，回检板2，控制板(校准板) 参数2： 所有附属板采集时间间隔，单位ms
	 * 参数3： 0表示使用真实数据 ;1启用虚拟数据 参数4：0表示不启用常规报警（由主控产生的报警），1表示启用所有报警
	 * 
	 * @throws Exception
	 */
	@Override
	public void init(String[] args) {

		// 判断是否重复打开
		if (Environment.isLinuxEnvironment()) {

			if (Environment.isAnotherProcessExists("mainboard.jar")) {

				Environment.infoLogger.info("process is existed ,exit system!");
				System.exit(0);
				return;
			}
		}
		
		Environment.infoLogger.info("version:" + MainBoard.VERSION);
		Environment.infoLogger.info("local ip:" + Env.getLocalIP());
		
		Environment.infoLogger.info("pull up power level");
		/**
		 * 无论虚拟机还是实际都直接拉高电平，防止复位；
		 */
		try {
			GpioPowerController powerProvider = new GpioPowerController(new GPIO(GpioPowerController.POWER_CONTROL_PIN),
					new GPIO(GpioPowerController.POWER_CHCEK_PIN));
			
			powerProvider.addPowerListener(new PowerListener() {
				
				@Override
				public void powerOff() {
					
					if(Context.getCoreService() != null) {
						
						Context.getCoreService().powerOff();
					} else {
						
						powerProvider.powerOff();
					}
					
					if(Context.getAccessoriesService() != null &&
							Context.getAccessoriesService().getControlboard() != null) {
						
						Environment.infoLogger.info("cut off controlboard power!");
						try {
							Context.getAccessoriesService().getControlboard().cutoffPower();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Environment.errLogger.error(CommonUtil.getThrowableException(e));
						}
					}
					
				}
			});
			/**
			 * 设置主控电源控制器
			 */
			Context.setPowerProvider(powerProvider);
			
			
			
		} catch (Exception ex) {
			
			
			
		}
		
		
		
		
		long st = System.currentTimeMillis();
		try {

			Environment.infoLogger.info("load config/cfg.xml,startup mainboard! ");
			startupCfg = new StartupCfgManager(CFG_PATH);
             
			// 初始化I18N
			I18N.init();
			
			// 初始化协议配置
			Data.setVoltageResolution(startupCfg.getProtocol().voltageResolution);// 电压小数位
			Data.setCurrentResolution(startupCfg.getProtocol().currentResolution);// 电流小数位
			Data.setCapacityResolution(startupCfg.getProtocol().capacityResolution);// 容量小数位
			Data.setEnergyResolution(startupCfg.getProtocol().energyResolution);// 能量小数位
			Data.setDriverChnCount(startupCfg.getDriverChnCount());// 驱动板通道数
			//Data.setLogicDriverCount(startupCfg.getLogicDriverCount());// 逻辑板包含驱动板数
			Data.setUseLogicPowerVoltage(startupCfg.getProtocol().useLogicPowerVoltage);// 是否启用逻辑板功率电压
			Data.setReverseDriverChnIndex(startupCfg.isUseReverseChnIndex());// 是否启用通道反序
			Data.setDoubleResolutionSupport(startupCfg.getDoublePrecision().use);// 是否启用多精度
			Data.setProgramKResolution(startupCfg.getKbDecimal().programK); //程控K值小数位
			Data.setProgramBResolution(startupCfg.getKbDecimal().programB); //程控B值小数位
			Data.setAdcKResolution(startupCfg.getKbDecimal().adcK); //adc K值小数位
			Data.setAdcBResolution(startupCfg.getKbDecimal().adcB); //adc b值小数位
			Data.setNewPickupProtocol(true); //采用新采集协议07
			Data.setUseChnTemperature(startupCfg.getProtocol().useChnTempPick); //采集通道温度
			
			if(startupCfg.getDriverChnCount() > 16) {
				Data.setUseHugeDriverChnCount(true);
				com.nltecklib.protocol.power.Data.setUseHugeDriverChnCount(true);
			}
			
			//xingguo_w
			if(startupCfg.getProtocol().moduleCount > 8) {
				Data.setUseHugeModuleCount(true);
				com.nltecklib.protocol.power.Data.setUseHugeModuleCount(true);
			}
			
			
			/**
			 * 实验室协议配置
			 */
			com.nltecklib.protocol.lab.Data.setGeneration(Generation.ND2); //实验室启用2代协议
			com.nltecklib.protocol.lab.Data.setUsePickupCapacity(true);
			com.nltecklib.protocol.lab.Data.setUseChnResponse(true);
			com.nltecklib.protocol.lab.Data.setUseTotalMiliseconds(true);
			com.nltecklib.protocol.lab.Data.setUseAndStepCondition(true);
			
			com.nltecklib.protocol.lab.Data.setReverseDriverChnIndex(startupCfg.isUseReverseChnIndex());
			com.nltecklib.protocol.lab.Data.setVoltageResolution(startupCfg.getProtocol().voltageResolution);
			com.nltecklib.protocol.lab.Data.setCurrentResolution(startupCfg.getProtocol().currentResolution);
			com.nltecklib.protocol.lab.Data.setCapacityResolution(startupCfg.getProtocol().capacityResolution);
			com.nltecklib.protocol.lab.Data.setEnergyResolution(startupCfg.getProtocol().energyResolution);// 能量小数位
			
			Data.setUseFrameTemperature(startupCfg.getProtocol().useFrameTempPick);//采集料框温度
			
			Data.setUseMainStepRecordTime(startupCfg.getProtocol().useStepRecord);
			Data.setUseMainStepVariable(startupCfg.getProtocol().useAirPressure);
			Data.setUseMainStepVarWaitTime(startupCfg.getProtocol().useAirPressure);
		    //需要使用流程类型判断当前需使用的保护
            Data.setUseProcedureWorkType(startupCfg.getProtocol().useProcedureWorkType);
			
			Data.setModuleCount(startupCfg.getProtocol().moduleCount); //设置模片数量
			com.nltecklib.protocol.power.Data.setModuleCount(startupCfg.getProtocol().moduleCount); //两套协议都得设置模片数量
			// 创建所有服务组件
			Context.initServices(this, startupCfg.isUseVirtualData());

			// 读取出厂设置
			if (!Context.getFileSaveService().readFactoryFile()) {

				factoryDatetime = new Date(); // 第一次上电
				Context.getFileSaveService().writeFactoryFile();
			}

			// 初始化流程模式
			procedureMode = startupCfg.getProcedureMode();
			// 清空保护缓存
			clearTempCfg();
			// 初始化配件
			Context.getAccessoriesService().init(startupCfg.isUseVirtualData());
			ScreenController screenController = Context.getAccessoriesService().getScreenController();
			if (screenController != null) {

				screenController.showProgress(40);
			}
			// 初始化核心组件
			Context.getCoreService().init();
			if (screenController != null) {
				screenController.showProgress(80);
			}

			if (screenController != null) {

				screenController.showStartupInfo(true, I18N.getVal(I18N.InitSystemSuccess));
				screenController.showProgress(100);
				CommonUtil.sleep(1000);
				screenController.showMainScreen();
				screenController.startPickup();
			}
			
			

		} catch (AlertException e) {
             
			
			Environment.errLogger.error(CommonUtil.getThrowableException(e));
			state = State.ALERT;
			Context.getPcNetworkService().pushSendQueue(e); // 发送给PC，设备初始化失败
			ScreenController screenController = Context.getAccessoriesService().getScreenController();
			if (screenController != null) {
				try {
					screenController.showStartupInfo(false, e.getMessage());
				} catch (ScreenException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			try {
				Context.getAlertManager().handle(AlertCode.INIT, e.getMessage(), false);
			} catch (AlertException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (ScreenException e) {
             
			
			e.printStackTrace();
			Environment.errLogger.error(CommonUtil.getThrowableException(e));
			state = State.ALERT;
			Context.getPcNetworkService()
					.pushSendQueue(new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ScreenCommError))); // 发送给PC，设备初始化失败

		} catch (Exception e) {
			
			state = State.ALERT;
			Environment.errLogger.info(CommonUtil.getThrowableException(e));
			ScreenController screenController = Context.getAccessoriesService().getScreenController();
			if (screenController != null) {
				try {
					screenController.showStartupInfo(false, I18N.getVal(I18N.InitSystemFail, e.getMessage()));
				} catch (ScreenException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			Context.getPcNetworkService().pushSendQueue(
					new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.InitSystemFail, e.getMessage()))); // 发送给PC，设备初始化失败
		} finally {

			ScreenController screenController = Context.getAccessoriesService().getScreenController();

			// 无论任何错误都初始化网络，防止主控退出
			// 初始化网络组件服务
			try {
				Context.getPcNetworkService().initNetwork();
				Context.getWorkformNetworkService().initNetwork();
				
				
			} catch (Exception e1) {

				e1.printStackTrace();
				initOk = false;
				state = State.ALERT;
			}
			if (screenController != null) {

				try {
					screenController.showStartupInfo(true, I18N.getVal(I18N.InitNetworkSuccess));
				} catch (ScreenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("init network success!");

			
			if (!startupCfg.isUseDebug()) {
				CommonUtil.sleep(2000);

				// 开始启动工作
				Context.getCoreService().startWork();
			} else {
				
				Environment.infoLogger.info("can not start work because of initOK = " + initOk + ",isUseDebug=" + startupCfg.isUseDebug());
			}
            
			if(state != State.ALERT) {
			    // 设备状态未变为报警状态， 初始化成功
			   initOk = true;
			}
			

			Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL,
					I18N.getVal(I18N.InitSystemSuccess));
			
			//System.out.println(I18N.getVal(I18N.CheckDeviceOverVolt,1.2f,100.1f ));

			if (initOk) {

				try {
					Context.getAlertManager().handle(AlertCode.NORMAL, "", false);

					// 初始化各种灯，状态灯，三色灯等
					if(Context.getAccessoriesService().getPingController() != null) {
						 /**
				         * 初始亮4色灯
				         */
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
					
					//初始化心跳
					createHeartbeatThread();

					

				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Environment.infoLogger.info("init device success!");
			}

			startupDatetime = new Date(); // 记录最近一次启动时间

		}

	}

	public static void main(String[] args) {

		PowerboxMainBoard mb = new PowerboxMainBoard();
		mb.init(args);
		// 采集主控状态
		mb.createCoreDataPickThread(5000);

	}

	/**
	 * 初始化
	 * 
	 * @throws AlertException
	 */
	public void initPowerController() throws AlertException {

		// 初始化跑马灯
		RunningLamp lamp = new RunningLamp(new GPIO(GpioPowerController.LAMP_CONTROL_PIN));
		lamp.start();
		// 初始化控制器
		Environment.infoLogger.info("init gpio control");
		powerController = new GpioPowerController(new GPIO(GpioPowerController.POWER_CONTROL_PIN),
				new GPIO(GpioPowerController.POWER_CHCEK_PIN));
		powerController.addPowerListener(new PowerListener() {

			@Override
			public void powerOff() {

				try {
					handlePoweroff();
				} catch (AlertException e) {

					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * 将对主控板进行断电后的数据保护和断网处理
	 * 
	 * @throws AlertException
	 */
	public void handlePoweroff() throws AlertException {

		Environment.infoLogger.info("start to cut off power");

		if (powerController != null) {
			powerController.powerOff();
		}

		// 停止采集
		Context.getCoreService().stopWork();
		// 检测到断电
		if (readChnState) { // 读到设备状态后,否则将不做写入处理

			if (getState() == State.FORMATION || getState() == State.PAUSE) {

				if (getState() == State.FORMATION) {

					Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL, "检测到发生断电，开始暂停流程!");

				}
				Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.NORMAL, "开始保存" + "断电前的数据和状态");
				// 保存离线状态

				for (ControlUnit cu : controls) {

					if (cu.getState() == State.FORMATION || cu.getState() == State.PAUSE) {

						cu.poweroffPause();
						// try {
						// cu.writeRuntimeFile();
						// } catch (AlertException e) {
						//
						// Context.getPcNetworkService().pushSendQueue(e);
						// }

					}
				}

			}
		}

		if (getState() == State.FORMATION) {

			setState(State.PAUSE);
		}
		// 关闭设备电源
		poweroff = true;

		if (Context.getAlertManager() != null) {

			Environment.infoLogger.info("alert power off");
			Context.getPcNetworkService().pushSendQueue(0xff, -1, AlertCode.OFFPOWER, "主控板约在60s后断电,系统进入断电待机状态");
			Context.getAlertManager().handle(AlertCode.OFFPOWER, "主控板将在30s后断电,系统进入断电待机状态", false);
		}

		CommonUtil.sleep(3000);
		Environment.infoLogger.info("disconnect network");
		Context.getPcNetworkService().closeService();

	}

	/**
	 * 初始化所有板的基础参数
	 */
	private void initAllBoardBaseConfig() {

		/*
		 * if (pn == null) {
		 * 
		 * // 默认配置 DeviceProtectData dpd = new DeviceProtectData();
		 * dpd.setDeviceVoltUpper(MainBoard.startupCfg.getMaxDeviceVoltage());
		 * dpd.setBatVoltUpper(MainBoard.startupCfg.getMaxDeviceVoltage() - 100);
		 * dpd.setCurrUpper(MainBoard.startupCfg.getMaxDeviceCurrent());
		 * writeBoardDeviceProtect(dpd);
		 * 
		 * PoleData pd = new PoleData(); pd.setPole(Pole.NORMAL); pd.setPoleDefine(100);
		 * writeBoardPoleProtect(pd);
		 * 
		 * } else {
		 * 
		 * writeBoardDeviceProtect(pn.getDeviceProtect());
		 * writeBoardPoleProtect(pn.getPoleProtect());
		 * 
		 * }
		 */
	}

}
