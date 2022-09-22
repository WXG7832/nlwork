package com.nlteck.fireware;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.nlteck.modbus.constant.Address.DialogType;
import com.nlteck.modbus.constant.Address.Light;
import com.nlteck.modbus.constant.Environment.CalMode;
import com.nlteck.modbus.constant.Environment.CalType;
import com.nlteck.modbus.constant.Environment.LightState;
import com.nlteck.modbus.constant.Environment.ScreenButton;
import com.nlteck.modbus.constant.Environment.TestState;
import com.nlteck.modbus.model.DataDot;
import com.nlteck.modbus.service.ActionService;
import com.nlteck.modbus.service.ScreenService;
import com.nlteck.modbus.table.BaseTableViewerProvider;
import com.nlteck.modbus.table.MeterDataTableViewerProvider.MeterData;
import com.nlteck.modbus.table.ShowTableViewer;
import com.nlteck.model.Channel;
import com.nlteck.model.TestDot;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.utils.LogUtil;

public class Screen {

	public static final int CONNECT_TIMEOUT = 3000;
	public static final int DEC = 3;
	private CalibrateCore core;
	private boolean use;
	private boolean result = true;
	private ScreenService screenService;
	private Socket socket;
	private String ip;
	private int port;
	private Map<Integer, TestState> lastTestStateMap = new ConcurrentHashMap<>();
	private CalType calType;
	public static boolean isLongPress = false;
	public Logger logger;

	private boolean deviceConnect = false;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public ScreenService getScreenService() {
		return screenService;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

//	/**
//	 * 设置ip并重连
//	 * 
//	 * @param ip
//	 * @throws Exception
//	 */
//
//	public void reConnect() throws Exception {
//
//		if (!use) {
//			return;
//		}
//		if (socket != null && !socket.isClosed()) {
//			socket.close();
//		}
//		connect();
//	}

	public Screen(CalibrateCore core) {
		logger = LogUtil.getLogger("screen");
		this.core = core;
		// 上一个通道数据初始化
		core.getDeviceCore().channelMap.entrySet().stream()
				.forEach(x -> lastTestStateMap.put(x.getValue().getDeviceChnIndex(), TestState.NONE));
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}
	
	private Thread thread=null;
	
	public void runStartConnect() {
		if(thread!=null&&!thread.isAlive()) {
			return ;
		}
		thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startConnect();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public void startConnect() {
		
		while(true) {
			try {
				connect();
				break;
			}catch(Exception ex) {
				logger.error("screen connect error:"+ex.getMessage(),ex);
			}finally {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			init();
		} catch (Exception ex) {
			// TODO Auto-generated catch bloc
			logger.error("screen init error:"+ex.getMessage(),ex);
		}
		
	}
	
	private void connect() throws IOException {
		
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}

		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, port), CONNECT_TIMEOUT);
	}
	
	
	
	

	private void init() throws Exception {

		
		screenService = new ScreenService(socket);

		// 1.设置监测心跳
		screenService.sendHeartData();

		screenService.triggerActionListener(new ActionService() {

			@Override
			public void stopWork() throws Exception {
				logger.info("user operator: cal stop");
//				for (Integer calIndex : core.getCalBoardMap().keySet()) {
//					CalBoard cb = core.getCalBoardMap().get(calIndex);
//					cb.setWork(false);
//				}

//				core.getDeviceCore().getChannelMap().entrySet().stream()
//						.filter(x -> core.getDeviceCore().isLogicUse(x.getValue().getUnitIndex()))
//						.forEach(e -> e.getValue().setReady(false));

				Thread.sleep(2000);

//				core.getDeviceCore().getChannelMap().entrySet().stream()
//						.filter(x -> core.getDeviceCore().isLogicUse(x.getValue().getUnitIndex()))
//						.forEach(e -> core.getScreen().setState(e.getValue()));
//				core.getScreen()
//						.updateAllChannel(core.getDeviceCore().channelMap.entrySet().stream()
//								.filter(x -> core.getDeviceCore().isLogicUse(x.getValue().getUnitIndex()))
//								.map(x -> x.getValue()).collect(Collectors.toList()));
			}

			@Override
			public void setKeySightIp(String ip) throws Exception {
				logger.info("user operator: setKeySightIp:" + ip);

				try {

					if (!core.getMeters().get(0).getIpAddress().equals(ip)) {

						CalibrateCore.getBaseCfg().networks
								.get(CalibrateCore.getBaseCfg().meters.get(0).commIndex).ip = ip;
						CalibrateCore.getBaseCfg().flush();
						core.getMeters().get(0).setIpAddress(ip);
						core.getMeters().get(0).connect();// 重连
					}
					screenService.openKeySightIPDialog(true);

				} catch (Exception e) {
					logger.error("setKeySightIp error:" + e.getMessage(), e);
					screenService.openMessageDialog("ip设置错误：" + e.getMessage(), DialogType.MESSAGE_EDIT);
				}
			}

			@Override
			public void setDeviceCoreIp(String ip) throws Exception {
				logger.info("user operator: setDeviceCoreIp:" + ip);
				// 设置成功或失败的提示框
				try {

					if (!CalibrateCore.getBaseCfg().networks.get(CalibrateCore.getBaseCfg().device.commIndex).ip
							.equals(ip)) {
						CalibrateCore.getBaseCfg().networks.get(CalibrateCore.getBaseCfg().device.commIndex).ip = ip;
						core.getDeviceCore().connect();
						core.getDeviceCore().startInit();
					}

					screenService.openDeviceCoreIPDialog(true);
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("setDeviceCoreIp error:" + e.getMessage(), e);
					screenService.openMessageDialog("ip设置错误：" + e.getMessage(), DialogType.MESSAGE_EDIT);
				}
			}

			@Override
			public void setCalCoreIp(String ip) throws Exception {
				// TODO Auto-generated method stub
				logger.info("user operator: setCalCoreIp:" + ip);

				try {
					throw new Exception("功能未实现");
//					screenService.openCalCoreIPDialog(true);
				} catch (Exception e) {
					logger.error("setCalCoreIp error:" + e.getMessage(), e);
					screenService.openMessageDialog("ip设置错误：" + e.getMessage(), DialogType.MESSAGE_EDIT);
				}

			}

			@Override
			public void openDockMode() throws Exception {
				logger.info("user operator: openDockMode");
				try {
					core.getCalibrateService().cfgModeSwitch(CalibrateCoreWorkMode.MATCH);
				} catch (Exception e) {
					logger.error("match error:" + e.getMessage(), e);
					screenService.openMessageDialog("对接失败:" + e.getMessage(), DialogType.MESSAGE_EDIT);
				}

			}

			@Override
			public void measure() throws Exception {
				logger.info("user operator: measure");
				// 对接的全部选中
//				core.getDeviceCore().channelMap.entrySet().stream()
//						.forEach(x -> x.getValue().setReady(x.getValue().getBindingCalBoardChannel() != null));

				for (int calIndex : core.getCalBoardMap().keySet()) {
					if (core.getCalBoardMap().get(calIndex).isWork()) {

						screenService.setScreenButtonState(false, ScreenButton.MEASURE);
						screenService.openMessageDialog("正在校准中", DialogType.MESSAGE_EDIT);
						return;
					}
				}

				calType = CalType.MEASURE;
//				core.getDeviceCore().cfgStartup(State.CAL);// 进入校准模式

				for (int calIndex : core.getCalBoardMap().keySet()) {
					core.getCalBoardMap().get(calIndex).startCalculate();
				}

			}

			@Override
			public void listenChannel(int channelIndex) throws Exception {

				int chnIndex = channelIndex - 1;// 获取到的是从1开始

				logger.info("user operator: listenChannel " + (chnIndex + 1) + ",isLongPress=" + isLongPress);

				Channel chn = core.getDeviceCore().getChannelMap().get(chnIndex);

				if (isLongPress) {
					isLongPress = false;
					// 弹出计量报表

					if (chn.getChnState() != CalState.NONE) {
						result = true;
						screenService.setMeasureTablePositionTitle(
								String.format("%d-%d", chn.getDriverIndex() + 1, chn.getChnIndex() + 1));
						ShowTableViewer tableViewer = new ShowTableViewer() {

							@Override
							public void setData(BaseTableViewerProvider provider) throws Exception {
								// TODO Auto-generated method stub

								List<TestDot> dots = chn.getMeasureDots().stream()
										.filter(x -> x.testResult != null && x.mode != DriverEnvironment.CalMode.CV)
										.collect(Collectors.toList());

								for (TestDot testDot : dots) {
									if (!result) {
										break;
									}

									try {

										int driverIndex = chn.getDriverIndex() + 1;
										int channelIndex = chn.getChnIndex() + 1;
										CalMode calMode = null;

										switch (testDot.mode) {
										case CC:
											calMode = CalMode.CC;
											break;
										case CV:
											calMode = CalMode.CV;
											break;
										case DC:
											calMode = CalMode.DC;
											break;
										case SLEEP:
											calMode = CalMode.SLEEP;
											break;
										default:
											break;
										}

										String pole = testDot.pole == DriverEnvironment.Pole.POSITIVE ? "+" : "-";
										double meterDot = testDot.programVal;
										double value = mathRound(testDot.meterVal, 3);
										double valueOffset = mathRound(Math.abs(testDot.meterVal - testDot.programVal),
												3);
										double adc = mathRound(testDot.adc, 3);
										double adcOffset = mathRound(Math.abs(testDot.adc - testDot.programVal), 3);
										String errorMsg = testDot.info == null ? "" : testDot.info;
										String pass = testDot.testResult == null ? "null" : testDot.testResult.name();

										provider.refreshData(new MeterData(calMode, pole, (int) meterDot, value,
												valueOffset, adc, adcOffset, errorMsg, pass));

									} catch (Exception e) {
										logger.error(e.getMessage(), e);
										break;
									}
								}
							}
						};

						screenService.setMeterDataTableViewer(tableViewer);
					}

				} else {
					// 选择通道
					if (chn.getBindingCalBoardChannel() != null
							&& (chn.getChnState() != CalState.CALIBRATE && chn.getChnState() != CalState.CALCULATE)) {

						chn.setReady(!chn.isReady());
						setState(chn);
					}
				}

			}

			@Override
			public void exitMeasureTable() throws Exception {
				logger.info("user operator: exitMeasureTable");
				result = false;

			}

			@Override
			public void connectDevice() throws Exception {

				logger.info("user operator: connectDevice");

				try {

					if (!deviceConnect) {
						
						try {
						core.getDeviceCore().connect();
						}catch(Exception ex) {
							throw new Exception("设备主控连接失败:"+ex.getMessage());
						}
						core.getDeviceCore().startInit();

						screenService.setSignLight(Light.CONNECT, LightState.ON);
						screenService.setSignLight(Light.CALBOARDTEMP1, LightState.ON);
						screenService.setSignLight(Light.CALBOARDTEMP2, LightState.ON);

						if (core.getMeters().get(0).isUse()) {
							try {
							core.getMeters().get(0).connect();
							}catch(Exception ex) {
								throw new Exception("万用表连接失败:"+ex.getMessage());
							}
							screenService.setSignLight(Light.KEYSIGHT, LightState.ON);
						}

						core.getDeviceCore().startInit();// 初始化设备主控，通道对象

						if (CalibrateCore.getBaseCfg().virtual.use) {
							core.getVirtualService().virtualBinding();
						}

						deviceConnect = true;
					} else {

						screenService.setSignLight(Light.CONNECT, LightState.OFF);
						screenService.setSignLight(Light.CALBOARDTEMP1, LightState.OFF);
						screenService.setSignLight(Light.CALBOARDTEMP2, LightState.OFF);

						if (core.getMeters().get(0).isUse()) {
							screenService.setSignLight(Light.KEYSIGHT, LightState.OFF);
						}

						deviceConnect = false;
					}

				} catch (Exception e) {
					logger.error("connectDevice error:" + e.getMessage(), e);
					screenService.openMessageDialog("连接失败" + e.getMessage(), DialogType.MESSAGE_EDIT);
				}

			}

			@Override
			public void calibrate() throws Exception {
				logger.info("user operator: calibrate");

				for (int calIndex : core.getCalBoardMap().keySet()) {
					if (core.getCalBoardMap().get(calIndex).isWork()) {

						screenService.setScreenButtonState(false, ScreenButton.CAL);
						screenService.openMessageDialog("正在计量中", DialogType.MESSAGE_EDIT);
						return;
					}
				}
				calType = CalType.CAL;

				for (int calIndex : core.getCalBoardMap().keySet()) {
					core.getCalBoardMap().get(calIndex).startCalibrate();
				}

			}

			@Override
			public void closeMasterSwitch() throws Exception {
				// TODO Auto-generated method stub
				logger.info("user operator: closeMasterSwitch");
				try {
					core.getCalibrateService().cfgModeSwitch(CalibrateCoreWorkMode.NONE);
				} catch (Exception e) {
					logger.error("closeMasterSwitch error:" + e.getMessage(), e);
					screenService.openMessageDialog("退出工作模式失败：" + e.getMessage(), DialogType.MESSAGE_EDIT);

				}
			}

			@Override
			public void openCalMode() throws Exception {
				logger.info("user operator: openCalMode");
				try {
					core.getCalibrateService().cfgModeSwitch(CalibrateCoreWorkMode.CAL);
				} catch (Exception e) {
					logger.error("openCalMode enter:" + e.getMessage(), e);
					screenService.openMessageDialog("进入工作模式失败：" + e.getMessage(), DialogType.MESSAGE_EDIT);

				}
			}

			@Override
			public void keepPress() throws Exception {
				logger.info("user operator: keepPress");
				isLongPress = true;
			}

			@Override
			public void listenDriver(int driverIndex) throws Exception {

				int index = driverIndex;
				logger.info("user operator: listenDriver " + (index + 1));

				// 选择当前板中已经对接好的通道
				List<Channel> driverChannels = core.getDeviceCore().channelMap.entrySet().stream()
						.filter(x -> x.getKey() >= index * core.getDeviceCore().getDriverChnCount()
								&& x.getKey() < (index + 1) * core.getDeviceCore().getDriverChnCount())
						.map(x -> x.getValue()).filter(x -> x.getBindingCalBoardChannel() != null)
						.collect(Collectors.toList());

				// 如果对接好的通道全部选中，则反选，否则全选
				if (driverChannels.stream().allMatch(x -> x.isReady())) {// 已经全选
					driverChannels.stream().forEach(x -> x.setReady(false));
				} else {
					driverChannels.stream().forEach(x -> x.setReady(true));
				}
//				driverChannels.stream().forEach(x -> setState(x));

//				core.getScreen()
//						.updateAllChannel(core.getDeviceCore().getChannelMap().entrySet().stream()
//								.filter(x -> core.getDeviceCore().isLogicUse(x.getValue().getUnitIndex()))
//								.map(x -> x.getValue()).collect(Collectors.toList()));
			}
		});

		// 3.启动监听器
		screenService.init();
//		screenService.setCurrentDate(new Date());
	}

	public void showTestDot(TestDot dot) throws Exception {
		if (!use) {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				// 4.当前4个校准板的校准结果，主界面显示
				String name = String.format("%d-%d", 
						dot.channel.getDriverIndex() + 1, dot.channel.getDriverChnIndex() + 1);
				CalType calType = dot.testType == TestType.Cal ? CalType.CAL : CalType.MEASURE;// 校准类型

				StringBuffer calMode = new StringBuffer();
				calMode.append(dot.mode.name());
				calMode.append(dot.pole == DriverEnvironment.Pole.POSITIVE ? "+" : "-");

				if (dot.testType == TestType.Cal
						&& (dot.mode == DriverEnvironment.CalMode.CC || dot.mode == DriverEnvironment.CalMode.DC)) {
					calMode.append(dot.precision + "");
				}

				long time = 0;
				if (dot.channel.getStartDate() != null) {
					if (dot.channel.getEndDate() != null) {
						time = ((dot.channel.getEndDate().getTime() - dot.channel.getStartDate().getTime()) / 1000);
					} else {
						time = ((new Date().getTime() - dot.channel.getStartDate().getTime()) / 1000);
					}
				}

				Double[] doubles = null;

				if (calType == CalType.MEASURE) {
					doubles = new Double[] { mathRound(dot.programVal, DEC), mathRound(dot.adc, DEC),
							mathRound(dot.meterVal, DEC) };
				} else {
					doubles = new Double[] { mathRound(dot.programVal, DEC), mathRound(dot.adc, DEC),
							mathRound(dot.meterVal, DEC) };
				}
				try {

					System.out.println("calType=" + calType + ",name=" + name + ",calMode=" + calMode.toString()
							+ ",doubles=" + Arrays.asList(doubles) + ",time=" + time + ",calIndex="
							+ (dot.channel.getBindingCalBoardChannel().getBoardIndex() + 1));

					screenService.showRealDataDotDetail(
							new DataDot(calType, name, calMode.toString(), dot.programVal, dot.meterVal, dot.adc, time),
							dot.channel.getBindingCalBoardChannel().getBoardIndex() + 1);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						screenService.openMessageDialog(e.getMessage(), DialogType.MESSAGE_EDIT);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();

	}

	private TestState getTestState(Channel channel) {
		TestState state = TestState.NONE;

		if (channel.isReady()
				&& (channel.getChnState() != CalState.CALCULATE && channel.getChnState() != CalState.CALIBRATE)) {// 准备的优先
			state = TestState.ALREADY;

		} else if (channel.getBindingCalBoardChannel() != null) {// 对接

			if (core.getCalBoardMap().get(channel.getBindingCalBoardChannel().getBoardIndex()).work) {
				switch (channel.getChnState()) {
				case CALCULATE:
				case CALIBRATE:
					state = TestState.RUNNING;
					break;
				case CALCULATE_PASS:
				case CALIBRATE_PASS:
					state = TestState.PASS;
					break;
				case CALIBRATE_FAIL:
				case CALCULATE_FAIL:
					state = TestState.FAIL;
					break;
				case NONE:
					state = TestState.NONE;
					break;
				case READY:
					state = TestState.ALREADY;
					break;
				}
			} else {

				state = TestState.DOCKING;
			}

		} else {
			switch (channel.getChnState()) {
			case CALCULATE:
			case CALIBRATE:
				state = TestState.RUNNING;
				break;
			case CALCULATE_PASS:
				state = TestState.PASS;
				break;
			case CALIBRATE_FAIL:
			case CALCULATE_FAIL:
				state = TestState.FAIL;
				break;
			case CALIBRATE_PASS:
				state = TestState.PASS;
				break;
			case NONE:
				state = TestState.NONE;
				break;
			case READY:
				state = TestState.ALREADY;
				break;
			}
		}
		lastTestStateMap.put(channel.getDeviceChnIndex(), state);
		return state;
	}

//	private void asynSetState(Channel channel) {
//		TestState state = TestState.NONE;
//
//		if (channel.isReady()
//				&& (channel.getChnState() != CalState.CALCULATE && channel.getChnState() != CalState.CALIBRATE)) {// 准备的优先
//			state = TestState.ALREADY;
//
//		} else if (channel.getBindingCalBoardChannel() != null) {// 对接
//
//			if (core.getCalBoardMap().get(channel.getBindingCalBoardChannel().getBoardIndex()).work) {
//				switch (channel.getChnState()) {
//				case CALCULATE:
//				case CALIBRATE:
//					state = TestState.RUNNING;
//					break;
//				case CALCULATE_PASS:
//				case CALIBRATE_PASS:
//					state = TestState.PASS;
//					break;
//				case CALIBRATE_FAIL:
//				case CALCULATE_FAIL:
//					state = TestState.FAIL;
//					break;
//				case NONE:
//					state = TestState.NONE;
//					break;
//				case READY:
//					state = TestState.ALREADY;
//					break;
//				}
//			} else {
//
//				state = TestState.DOCKING;
//			}
//
//		} else {
//			switch (channel.getChnState()) {
//			case CALCULATE:
//			case CALIBRATE:
//				state = TestState.RUNNING;
//				break;
//			case CALCULATE_PASS:
//				state = TestState.PASS;
//				break;
//			case CALIBRATE_FAIL:
//			case CALCULATE_FAIL:
//				state = TestState.FAIL;
//				break;
//			case CALIBRATE_PASS:
//				state = TestState.PASS;
//				break;
//			case NONE:
//				state = TestState.NONE;
//				break;
//			case READY:
//				state = TestState.ALREADY;
//				break;
//			}
//		}
//		// 颜色改变的通道上报
//		if (lastTestStateMap.get(channel.getDeviceChnIndex()) != state) {
//
//			try {
//				screenService.setChannelState(channel.getDeviceChnIndex(), state);
//				lastTestStateMap.put(channel.getDeviceChnIndex(), state);
//			} catch (Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//
//		}
//	}

	public void setState(Channel channel) {
		if (!use) {
			return;
		}
//		new Thread(() -> {
//			asynSetState(channel);
//		}).start();
//		getTestState(channel);
		try {
			screenService.setChannelState(channel.getDeviceChnIndex(), getTestState(channel));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		asynSetState(channel);
	}

	public static double mathRound(double val, int dec) {
		double pow = Math.pow(10, dec);
		return (double) Math.round(val * pow) / pow;
	}

	public void updateAllChannel(List<Channel> chns) {
		if (!use) {
			return;
		}
		try {
			List<com.nlteck.modbus.model.Channel> screenChns = chns.stream()
					.map(x -> new com.nlteck.modbus.model.Channel(x.getDeviceChnIndex(), getTestState(x)))
					.collect(Collectors.toList());
			screenService.setBatchChannelState(screenChns.subList(0, screenChns.size() / 2));
			screenService.setBatchChannelState(screenChns.subList(screenChns.size() / 2, screenChns.size()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
