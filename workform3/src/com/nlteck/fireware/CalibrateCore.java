package com.nlteck.fireware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.nlteck.base.BaseCfgManager;
import com.nlteck.base.BaseCfgManager.CalBoardParam;
import com.nlteck.base.BaseCfgManager.CommType;
import com.nlteck.base.BaseCfgManager.IOParam;
import com.nlteck.base.BaseCfgManager.Network;
import com.nlteck.base.BaseCfgManager.Port;
import com.nlteck.base.BaseCfgManager.RelayBoardParam;
import com.nlteck.base.CalCfgManager;
import com.nlteck.base.Env;
import com.nlteck.base.I18N;
import com.nlteck.controller.GpioPowerController;
import com.nlteck.controller.PowerController;
import com.nlteck.controller.PowerController.PowerListener;
import com.nlteck.model.CalBoardChannel;
import com.nlteck.model.Channel;
import com.nlteck.service.CalibrateService;
import com.nlteck.service.ChnMapService;
import com.nlteck.service.DiskService;
import com.nlteck.service.NetworkService;
import com.nlteck.service.VirtualService;
import com.nltecklib.device.KeySight34461A;
import com.nltecklib.device.Meter;
import com.nltecklib.io.broadcast.BroadcastManager;
import com.nltecklib.io.broadcast.DeviceType;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.MBWorkform.MBDeviceBaseInfoData;
import com.nltecklib.protocol.li.PCWorkform.BaseCfgData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoConfigData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoQueryData;
import com.nltecklib.protocol.li.PCWorkform.ConnectDeviceData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.cal.VoltageBaseData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbBaseInfoData;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.CoreA7EnvUtil;
import com.nltecklib.utils.IOUtil;
import com.nltecklib.utils.LogUtil;
import com.rm5248.serial.SerialPort;

/**
 * 校准主控
 * 
 * @author guofang_ma
 *
 */
public class CalibrateCore {

	private boolean pcConnected;
	private static BaseCfgManager baseCfg; // 配置环境
	private CalCfgManager calCfg;// 校准配置
	private Logger logger;

	private NetworkService networkService;
	private CalibrateService calibrateService;
	private VirtualService virtualService;
	private DiskService diskService;
	private PowerController powerController;
	private ChnMapService chnMapService;

	private Map<Integer, CalBoard> calBoardMap = new HashMap<>();

	private List<RelayBoard> relayBoards = new ArrayList<>();

	private List<Meter> meters = new ArrayList<>();
	private DeviceCore deviceCore;
	private Screen screen;

	private final static String VERSION = "V3.0.13.20220906";

	private Map<Meter, MeterParam> meterParamMap = new HashMap<>();

	
	public int meterRelayIndex=-1;
	
	public static class MeterParam {
		public int lastCalIndex = -1;// 上一个校准板，-1表示空
	}

	private Map<Integer, SerialPort> serialMap = new HashMap<>();

	/**
	 * 主程序入口点
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CalibrateCore calibrateCore = new CalibrateCore();
		calibrateCore.init();
	}

	/**
	 * 初始化
	 */
	public void init() {

		logger = LogUtil.getLogger("mainboard");
		// 打印版本号，防止升级没成功
		logger.info("Version = " + VERSION);

		// 判断是否重复打开
		if (Env.isLinuxEnvironment()) {

			if (Env.isAnotherProcessExists("workform.jar")) {

				logger.info("process is existed ,exit system!");
				System.exit(0);
				return;
			}
		}

		try {

			// arm锁住供电
			try {
				initPowerController();
			} catch (Exception ex) {

				// 不提示错误，如果是虚拟机必然报错
			}

			networkService = new NetworkService(this);
			logger.info("load base.xml");
			baseCfg = new BaseCfgManager();
			baseCfg.loadDocument();
			I18N.init(CalibrateCore.class.getClassLoader()
					.getResourceAsStream(String.format("language/%s.xml", baseCfg.i18n.language)));
			networkService.pushLog(I18N.getVal(I18N.AppStart), false);
			networkService.pushLog(I18N.getVal(I18N.LoadingConfigFile), false);

			Entity.setPrintProtocol(baseCfg.protocol.print);

			// 初始化协议参数
			Data.setReverseDriverChnIndex(baseCfg.protocol.chnOrderResverse);// 通道反序，针对校准板
			Data.setResistanceResolution(baseCfg.protocol.resistanceResolution);// 电阻系数
			Data.setVoltageResolution(baseCfg.protocol.voltageResolution);// 电压
			Data.setCurrentResolution(baseCfg.protocol.currentResolution);// 电流
			Data.setProgramKResolution(baseCfg.protocol.programKResolution);// 程控K
			Data.setProgramBResolution(baseCfg.protocol.programBResolution);// 程控B
			Data.setAdcKResolution(baseCfg.protocol.adcKResolution);// adcK
			Data.setAdcBResolution(baseCfg.protocol.adcBResolution);// adcB
			Data.setModuleCount(baseCfg.protocol.moduleCount); // 设置模片数量
			com.nltecklib.protocol.power.Data.setModuleCount(baseCfg.protocol.moduleCount);
			// 通道数
			Data.setDriverChnCount(baseCfg.calChnCount);
			com.nltecklib.protocol.power.Data.setDriverChnCount(baseCfg.calChnCount);
			if(baseCfg.base.driverChnCount>16) {
				Data.setUseHugeDriverChnCount(true);
				com.nltecklib.protocol.power.Data.setDriverChnCount(baseCfg.calChnCount);
			}
			
			if(baseCfg.protocol.isUseHugeModuleCount) {
				
				Data.setUseHugeModuleCount(true);
				com.nltecklib.protocol.power.Data.setUseHugeModuleCount(true);
			}
			

			// if (!baseCfg.virtual.use) {
			// // arm锁住供电
			// initPowerController();
			// }

			networkService.pushLog(I18N.getVal(I18N.LoadingCalibrateInfo), false);
			logger.info("load calibrate infomation");
			diskService = new DiskService(this);// 初始化硬盘服务
			calCfg = new CalCfgManager(this);// 初始化校准配置

			if (!baseCfg.virtual.use) {

				networkService.pushLog(I18N.getVal(I18N.InitSerialPorts), false);
				// 初始化串口
				for (Port pt : baseCfg.ports) {
					logger.info("init serial port " + pt.name);
					SerialPort port = IOUtil.openPort(pt.name, pt.baudrate);
					serialMap.put(pt.index, port);
				}
			}

			// 初始化设备主控
			// Network net = baseCfg.networks.get(baseCfg.device.commIndex);
			deviceCore = baseCfg.virtual.use ? new VirtualDeviceCore(this) : new ARMDeviceCore(this);

			chnMapService = new ChnMapService();

			for (IOParam iop : baseCfg.meters) {
				// 初始化万用表
				logger.info("init meter " + iop.index);
				if (iop.commType != CommType.NETWORK) {
					throw new Exception(I18N.getVal(I18N.CommTypeNotSupport, iop.commType));
				}
				Meter meter = baseCfg.virtual.use ? new VirtualMeter(iop.index, this) : new KeySight34461A(iop.index);
				meter.setUse(!iop.disabled);
				meter.setIpAddress(baseCfg.networks.get(iop.commIndex).ip);
				meters.add(meter);
				meterParamMap.put(meter, new MeterParam());

			}

			// 初始化校准板
			networkService.pushLog(I18N.getVal(I18N.InitCalBoards), false);
			for (CalBoardParam param : baseCfg.calboards) {
				logger.info("init calboards " + param.index);
				if (param.commType != CommType.SERIAL) {
					throw new Exception(I18N.getVal(I18N.CommTypeNotSupport, param.commType));
				}
				CalBoard calBoard = baseCfg.virtual.use ? new VirtualCalBoard(this, param.index)
						: new STMCalBoard(this, param.index, serialMap.get(param.commIndex));
				calBoard.setMeter(meters.stream().filter(x -> x.getIndex() == param.meterIndex).findAny().get());
				calBoard.setDisabled(param.disabled);
				calBoardMap.put(calBoard.getIndex(), calBoard);
			}

			// 初始化继电器板VirtualRelayBoard.java

			if (baseCfg.useRelayBoard) {

				for (RelayBoardParam param : baseCfg.relayBoards) {
					logger.info("init relayboards " + param.index);
					if (param.commType != CommType.SERIAL) {
						throw new Exception(I18N.getVal(I18N.CommTypeNotSupport, param.commType));
					}
					RelayBoard relayBoard = baseCfg.virtual.use
							? new VirtualRelayBoard(this, serialMap.get(param.commIndex))
							: new SerialRelayBoard(this,serialMap.get(param.commIndex));
					relayBoard.setDisabled(param.disabled);
					relayBoards.add(relayBoard);
				}
			}

			calibrateService = new CalibrateService(this);

			if (baseCfg.virtual.use) {
				virtualService = new VirtualService(this);
			}

			screen = new Screen(this);

			if (!baseCfg.screen.disabled) {

				networkService.pushLog(I18N.getVal(I18N.InitScreens), false);
				logger.info("init screen");
				if (baseCfg.screen.commType != CommType.NETWORK) {
					throw new Exception(I18N.getVal(I18N.CommTypeNotSupport, baseCfg.screen.commType));
				}
				Network netScreen = baseCfg.networks.get(baseCfg.screen.commIndex);
				screen.setUse(true);
				screen.setIp(netScreen.ip);
				screen.setPort(netScreen.port);
				screen.runStartConnect();
			}

		} catch (Exception e) {

			logger.error(e.getMessage(), e);
			networkService.pushLog(I18N.getVal(I18N.InitFailed, e.getMessage()), true);

		} finally {

			try {

				networkService.pushLog(I18N.getVal(I18N.InitNetWork), false);
				// 网络初始化
				networkService.init(new NetworkListener() {

					@Override
					public void send(IoSession session, Object message) {

						// if (!(((Decorator) message).getDestData() instanceof MainHeartBeatData)) {
						//// || ((Decorator) message).getDestData() instanceof UploadTestDotData
						//// || ((Decorator) message).getDestData() instanceof LivePushData)) {
						//// System.out.println("PC-->" + message.toString());
						//// System.out.println("PC-->" +((Decorator)
						// message).getClass().getSimpleName()+","+((Decorator)
						// message).getDestData().getClass().getSimpleName());
						//
						//// }
						// System.out.println("PC-->" + ((Decorator) message).getClass().getSimpleName()
						// + ","
						// + ((Decorator) message).getDestData().getClass().getSimpleName());
						// }
					}

					@Override
					public void receive(IoSession session, Object message) {

						try {
							Decorator decorator = (Decorator) message;
							if (decorator != null) {
								ResponseDecorator response = networkService.getPcController().process(decorator);
								if (response != null) {
									networkService.pushSendQueue(response);
								}
							}
						} catch (Exception e) {
							getLogger().error("receive error:" + e.getMessage(), e);
						}

						// if (!(((Decorator) message).getDestData() instanceof MainHeartBeatData)) {
						//// || ((Decorator) message).getDestData() instanceof UploadTestDotData
						//// || ((Decorator) message).getDestData() instanceof LivePushData) ) {
						//// System.out.println("PC<--" + message.toString());
						//// }
						// System.out.println("PC<--" + ((Decorator) message).getClass().getSimpleName()
						// + ","
						// + ((Decorator) message).getDestData().getClass().getSimpleName());
						// }

						// networkService.getRecvQueue().put(decorator);

						// if (decorator != null) {
						//
						// if (decorator instanceof ResponseDecorator) {
						//
						// ResponseDecorator response = (ResponseDecorator) decorator;
						//
						// if (response.getDestData() instanceof LogPushData) {
						// // dataRecvIndex++;
						//
						// } else if (response.getDestData() instanceof LivePushData) {
						// // dataRecvIndex++;
						//
						// } else {
						// // 回复命令
						// networkService.getResponseMap().put(decorator.getDestData().getCodeKey(),
						// (ResponseDecorator) decorator);
						// }
						//
						// } else {
						//
						// try {
						// ResponseDecorator response =
						// networkService.getPcController().process(decorator);
						// if (response != null) {
						// networkService.pushSendQueue(response);
						// }
						// } catch (Exception e) {
						// getLogger().error(e.getMessage(), e);
						// }
						// }
						// }

					}

					@Override
					public void connected(IoSession session) {
						pcConnected = true;
						logger.info("connected");

						// try {
						// networkService.pushLog(I18N.getVal(I18N.ConnectingDevice));
						// deviceCore.connect();
						// // 协议配置
						// Data.setLogicDriverCount(deviceCore.getLogicDriverCount());
						// Data.setDriverChnCount(deviceCore.getDriverChnCount());
						//
						// networkService.pushLog(I18N.getVal(I18N.ConnectingMeters));
						// for (Meter meter : meters) {
						// if (meter.isUse()) {
						// meter.connect();
						// }
						// }
						// networkService.pushLog(I18N.getVal(I18N.InitDevice));
						// deviceCore.init();// 初始化设备主控，通道对象
						//
						// if (baseCfg.virtual.use) {
						// virtualService.virtualBinding();
						// }
						// networkService.pushLog(I18N.getVal(I18N.InitDeviceSuccess));
						//
						// } catch (Exception e) {
						// logger.error(e.getMessage(), e);
						// networkService.pushLog(I18N.getVal(I18N.InitDeviceFailed, e.getMessage()));
						// }
					}

					@Override
					public void disconnected(IoSession session) {
						pcConnected = false;
						logger.info("disconnected");
						// 上位机连接断开，停止校准
						if (getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
								.count() == 0) {
							return;
						}

						if (getDeviceCore().getChannelMap().entrySet().stream()
								.filter(x -> x.getValue().isSelected() && x.getValue().isReady()).count() == 0) {

							return;
						}

						getDeviceCore().getChannelMap().entrySet().stream()
								.filter(x -> x.getValue().isSelected() && x.getValue().isReady()).forEach(x -> {
									x.getValue().setReady(false);
									/* getNetworkService().pushChnData(Arrays.asList(x.getValue())); */
								});

						// 清空选中通道
						getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
								.forEach(x -> x.getValue().setSelected(false));

					}

					@Override
					public void exception(IoSession session, Throwable cause) {
						// TODO Auto-generated method stub
						logger.error("mina error:" + cause.getMessage(), cause);
					}

					@Override
					public void idled(IoSession session) {
						// TODO Auto-generated method stub
						logger.info("pc idled");
						if (!baseCfg.debug) {
							logger.info("pc connect cut off");
							networkService.disconnect();
						}
					}

				});
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("app start success");
			logger.info(Env.getLocalIP());

			// Thread broadCastThread = new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// try {
			// new BroadcastManager().createBroadcaseResponsor(DeviceType.CALIBRATE_BOX,
			// Env.getLocalIP(),
			// Env.isLinuxEnvironment() ? Env.getMac() : Env.getWindowsMACAddress());
			// } catch (Exception e) {
			// logger.error("broadcast start error:", e);
			// }
			// }
			// });
			// broadCastThread.setDaemon(true);
			// broadCastThread.start();
			// logger.info("broadcast start");
		}
	}

	public boolean isPcConnected() {
		return pcConnected;
	}

	public Screen getScreen() {
		return screen;
	}

	public PowerController getPowerController() {
		return powerController;
	}

	public Map<Meter, MeterParam> getMeterParamMap() {
		return meterParamMap;
	}

	public VirtualService getVirtualService() {
		return virtualService;
	}

	public static BaseCfgManager getBaseCfg() {
		return baseCfg;
	}

	public CalCfgManager getCalCfg() {
		return calCfg;
	}

	public Logger getLogger() {
		return logger;
	}

	public NetworkService getNetworkService() {
		return networkService;
	}

	public CalibrateService getCalibrateService() {
		return calibrateService;
	}

	public DiskService getDiskService() {
		return diskService;
	}

	public Map<Integer, CalBoard> getCalBoardMap() {
		return calBoardMap;
	}

	
	

	public List<RelayBoard> getRelayBoards() {
		return relayBoards;
	}

	public void setRelayBoards(List<RelayBoard> relayBoards) {
		this.relayBoards = relayBoards;
	}

	public List<Meter> getMeters() {
		return meters;
	}

	public DeviceCore getDeviceCore() {
		return deviceCore;
	}

	public Map<Integer, SerialPort> getSerialMap() {
		return serialMap;
	}

	/**
	 * 主控基本配置设置
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void cfgBaseCfg(BaseCfgData data) throws Exception {

		baseCfg.networks.get(baseCfg.device.commIndex).ip = data.getDeviceIp();
		deviceCore.setIp(data.getDeviceIp());

		try {
			deviceCore.connect();
		} catch (Exception ex) {

			throw new Exception("连接设备主控(" + data.getDeviceIp() + ")失败!");

		}
		deviceCore.startInit();

		for (IOParam iop : baseCfg.meters) {

			if (data.getMeterIps().size() > iop.index) {

				baseCfg.networks.get(baseCfg.meters.get(iop.index).commIndex).ip = data.getMeterIps().get(iop.index);
				meters.get(iop.index).setIpAddress(data.getMeterIps().get(iop.index));
			}

			if (iop.disabled) {
				continue;
			}

			try {
				meters.get(iop.index).connect();
			} catch (Exception e) {
				throw new Exception(
						I18N.getVal(I18N.ConnectFail, I18N.getVal(I18N.Meter, iop.index + 1)) + ":" + e.getMessage());
			}
		}

		/*
		 * if (!data.getScreenIp().equals("0.0.0.0") &&
		 * !data.getScreenIp().equals(baseCfg.networks.get(baseCfg.screen.commIndex).ip)
		 * ) { baseCfg.networks.get(baseCfg.screen.commIndex).ip = data.getScreenIp();
		 * screen.setIp(data.getScreenIp());
		 * 
		 * // 液晶屏ip改变重连 if (!baseCfg.screen.disabled) { try { screen.runStartConnect();
		 * } catch (Exception e) { throw new Exception(I18N.getVal(I18N.ConnectFail,
		 * I18N.getVal(I18N.Screen)) + ":" + e.getMessage()); } } }
		 */

		baseCfg.flush();

	}

	/**
	 * 查询基本配置
	 * 
	 * @param data
	 */
	public void qryBaseCfg(BaseCfgData data) {
		data.setDeviceIp(baseCfg.networks.get(baseCfg.device.commIndex).ip);

		data.getMeterIps().clear();
		for (IOParam iop : baseCfg.meters) {
			data.getMeterIps().add(baseCfg.networks.get(iop.commIndex).ip);
		}
		data.setScreenIp(baseCfg.networks.get(baseCfg.screen.commIndex).ip);
	}

	// public Driverboard findDriver(int unitIndex, int driverIndex) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	/**
	 * 初始化
	 * 
	 * @throws AlertException
	 */
	public void initPowerController() throws Exception {

		// 初始化跑马灯
		RunningLamp lamp = new RunningLamp(new GPIO(GpioPowerController.LAMP_CONTROL_PIN));
		lamp.start();
		// 初始化控制器
		logger.info("init gpio control");
		powerController = new GpioPowerController(new GPIO(GpioPowerController.POWER_CONTROL_PIN),
				new GPIO(GpioPowerController.POWER_CHCEK_PIN));
		powerController.addPowerListener(new PowerListener() {

			@Override
			public void powerOff() {

				handlePoweroff();
			}

		});

	}

	/**
	 * 将对主控板进行断电后的数据保护和断网处理
	 * 
	 * @throws AlertException
	 */
	public void handlePoweroff() {

		logger.info("start to cut off power");

		// 停止所有校准
		for (int calIndex : calBoardMap.keySet()) {
			calBoardMap.get(calIndex).setWork(false);
		}

		// 等待文件保存结束
		networkService.pushLog(I18N.getVal(I18N.CorePowerOff, 60), false);

		if (powerController != null) {
			powerController.powerOff();
		}
	}

	public void qryBaseInfoQuery(BaseInfoQueryData data) throws Exception {

		MbBaseInfoData mbData = deviceCore.getBaseInfoData();
		if (mbData == null) {
			throw new Exception(I18N.getVal(I18N.GetDeviceInfoError));
		}
		data.setDeviceMac("11:12:13:14:15:F1");
		logger.info("deviceMac=" + data.getDeviceMac());
		data.setLogicCount(1);
		data.setLogicState((byte) 0x01);
		// data.setCheckCount(mbData.getCheckCount());
		// data.setCheckState(mbData.getCheckState());
		data.setDeviceDriverCount(mbData.getDriverCount());
		data.setDeviceDriverChnCount(CalibrateCore.getBaseCfg().base.driverChnCount);
		if (Env.isLinuxEnvironment()) {
			data.setCalBoxMac(Env.getMac());

		} else {
			data.setCalBoxMac(Env.getWindowsMACAddress());

		}
		logger.info("calBoxMac=" + data.getCalBoxMac());
		data.setCalCount(baseCfg.calboards.size());
		byte flag = 0;
		for (CalBoardParam iop : baseCfg.calboards) {
			if (!iop.disabled) {
				flag |= 1 << iop.index;
			}
		}
		data.setCalState(flag);
		data.setCalChnCount(baseCfg.calChnCount);
	}

	public void cfgBaseInfoConfig(BaseInfoConfigData data) throws Exception {

		// baseCfg.calChnCount = data.getCalChnCount();

		for (int i = 0; i < baseCfg.calboards.size(); i++) {
			baseCfg.calboards.get(i).disabled = (data.getCalState() >> i & 0x01) == 0;
			calBoardMap.get(i).disabled = (data.getCalState() >> i & 0x01) == 0;
		}
		baseCfg.flush();
	}

	public ChnMapService getChnMapService() {
		return chnMapService;
	}

	public CalBoardChannel findCalboardChnByDeviceChnIndex(int deviceChnIndex) {

		for (int i = 0; i < baseCfg.calboards.size(); i++) {

			if (baseCfg.calboards.get(i).disabled) {

				continue;
			}

			CalBoardChannel cbd = calBoardMap.get(i).findCalboardChnByIndex(deviceChnIndex);
			if (cbd != null) {

				return cbd;
			}
		}

		return null;

	}

}
