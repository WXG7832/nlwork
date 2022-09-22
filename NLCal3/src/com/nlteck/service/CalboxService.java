package com.nlteck.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.eclipse.swt.widgets.Display;

import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.firmware.WorkBench.CalType;
import com.nlteck.model.BaseCfg.MeterFilterParams;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.model.TestLog;
import com.nlteck.service.CalboxService.CalboxListener;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.PCWorkform.BaseCfgData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoConfigData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoQueryData;
import com.nltecklib.protocol.li.PCWorkform.BindCalBoardData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardTestModeData;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalMatchValueData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempQueryDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibrateTerminalData;
import com.nltecklib.protocol.li.PCWorkform.CalibrateTerminalData.CalibrateTerminal;
import com.nltecklib.protocol.li.PCWorkform.ChnSelectData;
import com.nltecklib.protocol.li.PCWorkform.ConnectCalboardData;
import com.nltecklib.protocol.li.PCWorkform.ConnectDeviceData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.DriverBindData;
import com.nltecklib.protocol.li.PCWorkform.DriverModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.HeartbeatData;
import com.nltecklib.protocol.li.PCWorkform.IPCfgData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogDebugPushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.li.PCWorkform.MatchStateData;
import com.nltecklib.protocol.li.PCWorkform.MeterConnectData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.ModuleSwitchData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfTestInfoData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.TestMode;
import com.nltecklib.protocol.li.PCWorkform.ReadMeterData;
import com.nltecklib.protocol.li.PCWorkform.RelayControlExDebugData;
import com.nltecklib.protocol.li.PCWorkform.RelaySwitchDebugData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.PCWorkform.SwitchMeterData;
import com.nltecklib.protocol.li.PCWorkform.TestModeData;
import com.nltecklib.protocol.li.PCWorkform.TimeData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.RelayControlExData;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData.AdcData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.utils.LogUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月13日 上午10:27:25 校准箱服务
 */
public class CalboxService {

	private List<CalboxListener> listeners = new ArrayList<CalboxListener>();

	/**
	 * 对接状态
	 * 
	 * @author wavy_zheng 2021年1月13日
	 *
	 */
	public enum MatchState {

		NONE, MATCHING, MATCHED;
	}

	public interface CalboxListener {

		/**
		 * 网络连接
		 * 
		 * @author wavy_zheng 2021年1月13日
		 * @param calbox
		 */
		public void connected(CalBox calbox);

		/**
		 * 网络断开
		 * 
		 * @author wavy_zheng 2021年1月13日
		 * @param calbox
		 */
		public void disconnected(CalBox calbox);

		/**
		 * 
		 * @author wavy_zheng 2021年1月13日
		 * @param operation
		 *            true正在对接，false对接完成
		 */
		public void join(CalBox calbox, boolean operation);

		/**
		 * 
		 * @author wavy_zheng 2021年1月13日
		 * @param enter
		 *            true已进入校准;false退出校准
		 */
		public void calibration(CalBox calbox, boolean enter);

		/**
		 * 已从下面接收到对接电压
		 * 
		 * @author wavy_zheng 2021年1月19日
		 * @param calbox
		 * @param cmvd
		 */
		public void joinVoltage(CalBox calbox, List<ChannelDO> channels);

		/**
		 * 从校准箱获取到通道状态
		 * 
		 * @author wavy_zheng 2021年1月19日
		 * @param calbox
		 * @param data
		 */
		public void onRecvChnState(CalBox calbox, List<ChannelDO> channels);

		/**
		 * 通道收到数据
		 * 
		 * @author wavy_zheng 2021年1月19日
		 * @param calbox
		 * @param channel
		 */
		public void onRecvChnData(CalBox calbox, ChannelDO channel);

		/**
		 * 日志
		 * 
		 * @author wavy_zheng 2021年1月19日
		 * @param calbox
		 * @param log
		 */
		public void onRecvLog(CalBox calbox, TestLog log);

		/**
		 * 通道开始测试
		 * 
		 * @author wavy_zheng 2021年2月19日
		 * @param calbox
		 * @param channel
		 */
		public void onStartTest(CalBox calbox, ChannelDO channel);

	}

	private Logger logger;

	private final static int TIME_OUT = 5000;

	public CalboxService() {

		logger = LogUtil.getLogger("log/calboxService.log");
	}

	/**
	 * 调试连接校准箱
	 * 
	 * @author wavy_zheng 2022年2月7日
	 * @param calbox
	 * @return
	 * @throws Exception
	 */
	public boolean connectBoxDebug(CalBox calbox) {

		if (calbox.getConnector() == null) {

			initNetwork(calbox); // 初始化网络
		}
		if (calbox.getConnector().isConnected()) {

			return true;
		}
		if (!calbox.getConnector().connect(calbox.getIp(), CalBox.PORT)) {

			return false;
		}

		return true;

	}

	/**
	 * 连接校准箱
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @throws Exception
	 */
	public boolean connect(CalBox calbox) throws Exception {

		if (calbox.getConnector() == null) {

			initNetwork(calbox); // 初始化网络
		}
		if (calbox.getConnector().isConnected()) {

			return true;
		}
		if (!calbox.getConnector().connect(calbox.getIp(), CalBox.PORT)) {

			return false;
		}

		// 配置基本校准箱信息
		configBaseInfo(calbox);

		// 获取校准箱基础信息
		queryInitInfo(calbox);

		// 将校准箱信息保存到数据库
		queryBoxInfo(calbox);

		// 更新到数据库
		WorkBench.getDatabaseManager().updateBox(calbox, null, null, calbox.getMeterIps(), calbox.getScreenIp(), null);

		// 刷新通道信息。重新连接网络后，可能内存通道状态没及时更新
		WorkBench.getDatabaseManager().refreshChannels(calbox.getDevice());

		return true;

	}

	/**
	 * 断开校准箱
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 */
	public void disconnect(CalBox calbox) {

		if (calbox.getConnector() != null) {

			if (calbox.getConnector().isConnected()) {

				calbox.getConnector().disconnect();
				calbox.setConnector(null);
				CommonUtil.sleep(300);
			}
		}
	}

	/**
	 * 初始化网络
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 */
	public void initNetwork(CalBox calbox) {

		calbox.setConnector(new NetworkConnector(new Entity(), false));
		calbox.getConnector().setConnectTimeOut(5000);
		calbox.getConnector().setNetworkListener(new NetworkListener() {

			@Override
			public void send(IoSession session, Object message) {

			}

			@Override
			public void receive(IoSession session, Object message) {

				Decorator decorator = (Decorator) message;
				if (decorator instanceof ResponseDecorator) {
					// 处理接收消息
					processResponseMessage(calbox, (ResponseDecorator) decorator);
				} else if (decorator instanceof AlertDecorator) {

					processPushMessage(calbox, (AlertDecorator) decorator);
				}

			}

			@Override
			public void connected(IoSession session) {

				if (calbox.getHeartThread() == null) {
					ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
					executor.scheduleAtFixedRate(new Runnable() {

						@Override
						public void run() {

							try {
								heartbeat(calbox);
								calbox.setBeatCount(0);
							} catch (Exception e) {

								calbox.setBeatCount(calbox.getBeatCount() + 1);
							}
							if (calbox.getBeatCount() >= 3) {

								Display.getDefault().syncExec(new Runnable() {

									@Override
									public void run() {

										logger.info("cut off network because of heartbeat!!!");
										triggerConnectListener(false, calbox);

									}

								});
								calbox.setBeatCount(0);

							}

						}

					}, 100, 5000, TimeUnit.MILLISECONDS);
					calbox.setHeartThread(executor);

				}
			}

			@Override
			public void disconnected(IoSession session) {

				if (calbox.getHeartThread() != null) {
					calbox.getHeartThread().shutdown();
					calbox.setHeartThread(null);
					logger.info("disconnect box , shutdown heart beat!");

					if (calbox.getDevice() != null) {
						calbox.getDevice().setMode(CalibrateCoreWorkMode.NONE); // 自动退出校准模式
					}
				}

			}

			@Override
			public void exception(IoSession session, Throwable cause) {

				logger.error(CommonUtil.getThrowableException(cause));
				disconnect(calbox);
				triggerConnectListener(false, calbox); // 通知窗口网络异常

			}

			@Override
			public void idled(IoSession session) {

			}

		});

	}

	/**
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param data
	 */
	public void processResponseMessage(CalBox calbox, ResponseDecorator response) {

		logger.info("收到校准箱回复:" + response);
		calbox.getReceiveBufferMap().put(response.getDestData().getCodeKey(), response);

	}

	public static int convertToDeviceIndex(Device device, int unitIndex, int chnIndexInLogic) {

		return unitIndex * device.getDriverNumInLogic() * device.getChnNumInDriver() + chnIndexInLogic;

	}

	/**
	 * 处理实时推送的消息
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param data
	 */
	public void processPushMessage(CalBox calbox, AlertDecorator decorator) {

		Data data = decorator.getDestData();
		if (data instanceof LivePushData) {

			List<ChannelDO> channels = calbox.getDevice().getChannels();
			// 状态推送
			List<ChannelDO> changeChns = new ArrayList<>();
			LivePushData lpd = (LivePushData) data;
			for (PushData pd : lpd.getPushDatas()) {

				System.out.println("live push state:" + pd.calState);

				if (pd.matched) {

					System.out.println("matched:" + pd.chnIndex);
				}

				int deviceChnIndex = pd.unitIndex * calbox.getDevice().getDriverNumInLogic()
						* calbox.getDevice().getChnNumInDriver() + pd.chnIndex;

				if ((channels.get(deviceChnIndex).getState() == CalState.CALCULATE
						|| channels.get(deviceChnIndex).getState() == CalState.CALIBRATE)
						&& (pd.calState == CalState.CALCULATE_PASS || pd.calState == CalState.CALCULATE_FAIL
								|| pd.calState == CalState.CALIBRATE_PASS || pd.calState == CalState.CALIBRATE_FAIL)) {

					// 保存通道状态
					try {

						channels.get(deviceChnIndex).setEndTime(new Date());
						channels.get(deviceChnIndex).setState(pd.calState);
						// logger.info("===========保存通道测试结果，通道号：" + (deviceChnIndex + 1) + ",结果：" +
						// pd.calState);
						WorkBench.getDatabaseManager().saveChannel(channels.get(deviceChnIndex));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// logger.info("===========初始化设置通道结果，通道号：" + (deviceChnIndex + 1) + ",结果：" +
				// pd.calState);

				if (pd.calState == CalState.NONE && channels.get(deviceChnIndex).getResult() > 0) {
					// System.out.println("通道号：" + (deviceChnIndex + 1) + ",结果：" +
					// channels.get(deviceChnIndex).getResult());
					if (channels.get(deviceChnIndex).getResult() == 2) {
						pd.calState = CalState.CALIBRATE_PASS;

					} else {
						pd.calState = channels.get(deviceChnIndex).getCalType() == CalType.MEASURE
								? CalState.CALCULATE_FAIL
								: CalState.CALIBRATE_FAIL;

					}
				}
				channels.get(deviceChnIndex).setState(pd.calState);
				channels.get(deviceChnIndex).setConnectCalboard(pd.matched);
				changeChns.add(channels.get(deviceChnIndex));

			}
			triggerRecvState(calbox, changeChns);

		} else if (data instanceof UploadTestDotData) {

			List<ChannelDO> channels = calbox.getDevice().getChannels();

			// 实时测试点数据推送
			System.out.println("recv test dot:" + data);
			UploadTestDotData testDot = (UploadTestDotData) data;
			UploadTestDot dot = testDot.getDot();
			if (dot != null) {
				int deviceChnIndex = convertToDeviceIndex(calbox.getDevice(), dot.unitIndex, dot.chnIndex);
				System.out.println("deviceChnIndex = " + deviceChnIndex);

				ChannelDO channel = channels.get(deviceChnIndex);
				if (dot.testType == TestType.Measure) {

					MeasureDotDO mdd = new MeasureDotDO();
					if (dot.clear) {

						channel.clearTestDots();
						/**
						 * 重测不应该清除调试数据
						 */
						// channel.clearDebugDatas();
						try {
							WorkBench.getDatabaseManager().clearMeasureDots(channel);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						triggerTestStart(calbox, channel);

						return;
					}

					mdd.setCalculateDot(dot.programVal);
					mdd.setFinalAdc(dot.adc);
					mdd.setMeterVal(dot.meterVal);
					mdd.setPole(dot.pole == null ? "" : (dot.pole == Pole.NORMAL ? "+" : "-"));
					mdd.setMode(dot.mode == null ? CalMode.SLEEP.name() : dot.mode.name());
					mdd.setLevel(dot.precision);
					mdd.setIndex(channel.getMeasures().size());
					mdd.setResult(dot.success ? "pass" : "fail");
					mdd.setInfo(dot.info);
					mdd.setChannel(channel);

					/*
					 * xingguo_wang
					 */
					if (WorkBench.baseCfg.meterValueFilter.use) {

						for (MeterFilterParams meterFilterParams : WorkBench.baseCfg.meterValueFilter.meterFilterParams) {
							if (meterFilterParams.mode.equals(dot.mode) && meterFilterParams.pole.equals(dot.pole)
									&& meterFilterParams.level == dot.precision) {
								// 偏差在threshold到maxRange区间进行修正
								double realOffset = dot.meterVal - dot.programVal;
								if (Math.abs(realOffset) > meterFilterParams.threshold&& Math.abs(realOffset) < meterFilterParams.maxRange) {
									double afterOffset = realOffset * meterFilterParams.threshold / meterFilterParams.maxRange;
									dot.meterVal = dot.programVal + afterOffset;
									mdd.setMeterVal(dot.meterVal);
									if (WorkBench.baseCfg.meterValueFilter.show) {
										mdd.setInfo("realMeter:" + (dot.programVal + realOffset) + " realOffset:"
												+ realOffset);
										dot.info = "realMeter:" + (dot.programVal + realOffset) + " realOffset:"
												+ realOffset;
									}
								}
								break;
							}
						}
					}

					MeasureDotDO findDot = null;
					if ((findDot = channel.findSameDot(mdd)) != null) {

						findDot.setFinalAdc(mdd.getFinalAdc());
						findDot.setMeterVal(mdd.getMeterVal());
						findDot.setResult(mdd.getResult());
						findDot.setInfo(mdd.getInfo());
						findDot.setChannel(channel);

						// 直接更新旧的点
						try {
							WorkBench.getDatabaseManager().updateMeasureDot(findDot);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {

						try {
							WorkBench.getDatabaseManager().saveMeasureDot(mdd);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						channel.appendTestDot(mdd);
					}

				}
				channel.setPole(dot.pole);
				channel.setAdc(dot.adc);
				channel.setMode(dot.mode);
				channel.setProgrameVal(dot.programVal);
				channel.setMeterVal(dot.meterVal);
				channel.setInfo(dot.info);
				channel.setPos(dot.pos);
				channel.setRange(dot.range);
				channel.setPrecision(dot.precision);
				channel.setSeconds(dot.seconds);
				channel.setCalType(CalType.values()[dot.testType.ordinal()]);

				if (!dot.clear) {
					channel.appendDebugData(dot);
				} else {

					triggerTestStart(calbox, channel);
				}

				if (channel.getState() == CalState.READY) {

					channel.setState(dot.testType == TestType.Measure ? CalState.CALCULATE : CalState.CALIBRATE);
					// 更新通道转改

				}
				triggerRecvData(calbox, channel);
			}

		} else if (data instanceof LogPushData) {

			// 实时生产日志
			LogPushData lpd = (LogPushData) data;
			int unitIndex = data.getUnitIndex();
			Device device = calbox.getDevice();

			if (device == null) {

				return;
			}

//			for (PushLog pl : lpd.getLogs()) {
//
//				TestLog tl = new TestLog();
//				tl.setContent(pl.log);
//				tl.setDate(pl.date);
//				tl.setDevice(device);
//				tl.setLevel(pl.error ? "error" : "info");
//
//				if (pl.chnIndexInLogic != 0xff) {
//					tl.setDeviceChnIndex(convertToDeviceIndex(device, unitIndex, pl.chnIndexInLogic));
//					// logger.info("product log:" + unitIndex + "-" + pl.chnIndexInLogic + "=" +
//					// tl.getDeviceChnIndex());
//					device.getChannels().get(tl.getDeviceChnIndex()).appendTestLog(tl);
//				} else {
//
//					tl.setDeviceChnIndex(-1);
//					calbox.getDevice().getTestLogs().add(tl);
//				}
//				triggerLogData(calbox, tl);
//
//			}

		} else if (data instanceof LogDebugPushData) {

			// System.out.println(data);
			// 调试日志
			LogDebugPushData ldpd = (LogDebugPushData) data;
			TestLog log = new TestLog();
			log.setContent(ldpd.getLog());
			log.setDate(new Date());
			log.setDevice(calbox.getDevice());
			log.setLevel("debug");
			if (ldpd.getChnIndex() == 0xff) {

				log.setDeviceChnIndex(-1);
				calbox.getDevice().getTestLogs().add(log);
			} else {

				int deviceChnIndex = convertToDeviceIndex(calbox.getDevice(), ldpd.getUnitIndex(), ldpd.getChnIndex());
				log.setDeviceChnIndex(deviceChnIndex);
				// logger.info("debug log:" + ldpd.getUnitIndex() + "-" + ldpd.getChnIndex() +
				// "=" + deviceChnIndex);
				calbox.getDevice().getChannels().get(deviceChnIndex).appendTestLog(log);
			}

			triggerLogData(calbox, log);

		} else if (data instanceof MatchStateData) {

			// 对接标志
			MatchStateData match = (MatchStateData) data;
			calbox.setMatchState(MatchState.MATCHED); // 对接完成
			triggerJoinListener(calbox, true); // 通知对接完成

		} else if (data instanceof CalMatchValueData) {

			CalMatchValueData cmvd = (CalMatchValueData) data;

			List<ChannelDO> chns = new ArrayList<>();
			List<ChannelDO> channels = calbox.getDevice().getChannels();
			for (AdcData ad : cmvd.getAdcList()) {

				int deviceChnIndex = convertToDeviceIndex(calbox.getDevice(), cmvd.getUnitIndex(), ad.chnIndex);
				channels.get(deviceChnIndex).setConnectVoltage(ad.adc);
				chns.add(channels.get(deviceChnIndex));

			}
			System.out.println(cmvd);
			triggerRecvVoltage(calbox, chns);

		}

		// 回复给校准箱
		data.setResult(Result.valueOf(data.getCode(), 1));
		calbox.getConnector().send(new ResponseDecorator(data, false)); // 回复

	}

	/**
	 * 给指定的校准箱发送命令
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param cfg
	 * @param timeOut
	 * @throws Exception
	 */
	private ResponseDecorator configCommand(CalBox calbox, Decorator cfg, int timeOut) throws Exception {

		logger.info("下发校准箱命令:" + cfg);

		// 确保唯一
		calbox.getReceiveBufferMap().remove(cfg.getDestData().getCodeKey());
		// 发送
		calbox.getConnector().send(cfg);

		ResponseDecorator responseDecorator = (ResponseDecorator) calbox.findResponseBy(cfg.getDestData().getCodeKey(),
				timeOut);
		if (responseDecorator == null) {

			throw new Exception("校准主控通信超时!");
		}
		if (responseDecorator.getDestData().getResult().getCode() != Result.SUCCESS) {

			throw new Exception(
					"错误码:" + responseDecorator.getDestData().getResult() + "\n" + responseDecorator.getInfo());
		}

		return (ResponseDecorator) responseDecorator;

	}

	/**
	 * 通过校准箱连接或断开设备主控
	 * 
	 * @author wavy_zheng 2022年2月7日
	 * @param calbox
	 * @throws Exception
	 */
	public void connectDevice(CalBox calbox, String ip, boolean connect) throws Exception {

		ConnectDeviceData bcd = new ConnectDeviceData();
		bcd.setDeviceIp(ip);
		bcd.setConnect(connect);

		logger.info("connect device info:" + bcd);

		configCommand(calbox, new ConfigDecorator(bcd), TIME_OUT);

	}

	/**
	 * 通知校准箱连接校准板
	 * 
	 * @author wavy_zheng 2022年2月7日
	 * @param calbox
	 * @param connect
	 * @throws Exception
	 */
	public void connectCalboard(CalBox calbox, int calboardIndex, boolean connect) throws Exception {

		ConnectCalboardData bcd = new ConnectCalboardData();
		bcd.setDriverIndex(calboardIndex);
		bcd.setConnect(connect);

		logger.info("connect calboard info:" + bcd);

		configCommand(calbox, new ConfigDecorator(bcd), TIME_OUT);
	}

	/**
	 * 配置设备基础信息
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @throws Exception
	 */
	public void configBaseInfo(CalBox calbox) throws Exception {

		BaseCfgData bcd = new BaseCfgData();
		bcd.setDeviceIp(calbox.getDevice().getIp());

		List<String> ips = new ArrayList<>();
		for (String ip : calbox.getMeterIps()) {

			if (CommonUtil.checkIP(ip)) {

				ips.add(ip);
			}
		}

		bcd.setMeterIps(ips);
		bcd.setScreenIp(calbox.getScreenIp() == null ? "0.0.0.0" : calbox.getScreenIp());

		logger.info("config box info:" + bcd);

		configCommand(calbox, new ConfigDecorator(bcd), TIME_OUT);

	}

	/**
	 * 配置校准板开关状态
	 * 
	 * @param calbox
	 * @param state
	 * @throws Exception
	 */
	public void configCalBoardState(CalBox calbox, byte state) throws Exception {

		BaseInfoConfigData baseInfoConfigData = new BaseInfoConfigData();
		baseInfoConfigData.setCalState(state);
		baseInfoConfigData.setCalCount(calbox.getCalBoardList().size());
		baseInfoConfigData.setCalChnCount(calbox.getCalBoardList().get(0).getChannelCount());

		configCommand(calbox, new ConfigDecorator(baseInfoConfigData), TIME_OUT);

	}

	public void heartbeat(CalBox calbox) throws Exception {

		logger.info("heart beat!!!");
		HeartbeatData hb = new HeartbeatData();
		configCommand(calbox, new ConfigDecorator(hb), TIME_OUT);

	}

	/**
	 * 配置设备计量方案
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param device
	 *            设备对象
	 * @throws Exception
	 */
	public void configCalculatePlan(Device device) throws Exception {

		CalculatePlanData plan = device.getCalculatePlan();
		if (plan == null) {

			throw new Exception("设备未绑定计量方案");
		}
		for (CalBox calbox : device.getCalBoxList()) {
			configCommand(calbox, new ConfigDecorator(plan), TIME_OUT);
		}

	}

	/**
	 * 读取校准箱内的计量方案
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @throws Exception
	 */
	public void readCalculatePlan(CalBox calbox) throws Exception {

		ResponseDecorator response = configCommand(calbox, new QueryDecorator(new CalculatePlanData()), TIME_OUT);
		calbox.setCalculatePlan((CalculatePlanData) response.getDestData());

	}

	/**
	 * 切换模式
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param mode
	 * @throws Exception
	 */
	public void changeWorkMode(CalBox calbox, CalibrateCoreWorkMode mode) throws Exception {

		ModeSwitchData msd = new ModeSwitchData();
		msd.setMode(mode);
		configCommand(calbox, new ConfigDecorator(msd), TIME_OUT);

	}

	/**
	 * 单板切换模式
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param calbox
	 * @param driverIndex
	 * @param mode
	 * @throws Exception
	 */
	public void changeDriverWorkMode(CalBox calbox, int driverIndex, CalibrateCoreWorkMode mode) throws Exception {

		DriverModeSwitchData msd = new DriverModeSwitchData();
		msd.setDriverIndex(driverIndex);
		msd.setMode(mode);
		configCommand(calbox, new ConfigDecorator(msd), TIME_OUT);

	}

	public void cfgModuleSwitch(CalBox calbox, int chnIndex, boolean enable) throws Exception {

		ModuleSwitchData data = new ModuleSwitchData();
		data.setUnitIndex(0);
		data.setChnIndex(chnIndex);
		data.setOpen(enable);
		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgCalibrate(CalBox calbox, LogicCalibrate2DebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgFlash(CalBox calbox, LogicFlashWrite2DebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgCalculate(CalBox calbox, LogicCalculate2DebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);
	}

	public void cfgResistance(CalBox calbox, CalResistanceDebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgCalboardCalibrate(CalBox calbox, CalCalibrate2DebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgTempControl(CalBox calbox, CalTempControlDebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);
	}

	public void cfgCalboardCalculate(CalBox calbox, CalCalculate2DebugData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgCalboardTestMode(CalBox calbox, CalBoardTestModeData data) throws Exception {

		configCommand(calbox, new ConfigDecorator(data), TIME_OUT);

	}

	public void cfgMeterChange(CalBox calbox, CalRelayControlDebugData relay) throws Exception {

		configCommand(calbox, new ConfigDecorator(relay), TIME_OUT);

	}

	public void cfgMeterChange(CalBox calbox, SwitchMeterData relay) throws Exception {

		configCommand(calbox, new ConfigDecorator(relay), TIME_OUT);

	}

	public CalTempQueryDebugData readTempQueryDebug(CalBox calbox, int driverIndex) throws Exception {

		CalTempQueryDebugData data = new CalTempQueryDebugData();
		data.setDriverIndex(driverIndex);
		ResponseDecorator response = configCommand(calbox, new QueryDecorator(data), TIME_OUT);
		return (CalTempQueryDebugData) response.getDestData();
	}

	public CalResistanceDebugData readNewResistanceDebug(CalBox calbox, CalResistanceDebugData query) throws Exception {

		ResponseDecorator response = configCommand(calbox,
				new QueryDecorator(query, (byte) query.getWorkPattern().ordinal(), (byte) query.getRange()), TIME_OUT);
		return (CalResistanceDebugData) response.getDestData();
	}

	public ReadMeterData readMeterDebugData(CalBox box, ReadMeterData rmd) throws Exception {

		ResponseDecorator response = configCommand(box, new QueryDecorator(rmd), TIME_OUT);
		return (ReadMeterData) response.getDestData();
	}

	public LogicCalculate2DebugData readCalculate(CalBox calbox, int chnIndex) throws Exception {

		LogicCalculate2DebugData data = new LogicCalculate2DebugData();
		data.setUnitIndex(0);
		data.setChnIndex(chnIndex);

		ResponseDecorator response = configCommand(calbox, new QueryDecorator(data), TIME_OUT);
		if (response.getDestData().getResult() != DefaultResult.SUCCESS) {

			throw new Exception("返回结果码:" + response.getDestData().getResult());
		}

		return (LogicCalculate2DebugData) response.getDestData();

	}

	public LogicCalibrate2DebugData readCalibrate(CalBox calbox, int chnIndex) throws Exception {

		LogicCalibrate2DebugData data = new LogicCalibrate2DebugData();
		data.setUnitIndex(0);
		data.setChnIndex(chnIndex);

		ResponseDecorator response = configCommand(calbox, new QueryDecorator(data), TIME_OUT);
		if (response.getDestData().getResult() != DefaultResult.SUCCESS) {

			throw new Exception("返回结果码:" + response.getDestData().getResult());
		}

		return (LogicCalibrate2DebugData) response.getDestData();

	}

	/**
	 * 读取校准箱工作模式
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @throws Exception
	 */
	public void readWorkMode(CalBox calbox) throws Exception {

		ResponseDecorator response = configCommand(calbox, new QueryDecorator(new ModeSwitchData()), TIME_OUT);
		calbox.setMode(((ModeSwitchData) response.getDestData()).getMode());

	}

	/**
	 * 连接万用表
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param index
	 * @param connect
	 * @throws Exception
	 */
	public void connectMeter(CalBox calbox, int index, boolean connect) throws Exception {

		MeterConnectData mcd = new MeterConnectData();
		mcd.setDriverIndex(index);
		mcd.setConnected(connect);
		configCommand(calbox, new ConfigDecorator(mcd), TIME_OUT);
	}

	/**
	 * 选择通道
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param unitIndex
	 * @param driverIndex
	 * @param selectFlag
	 * @throws Exception
	 */
	public void selectCalibrateChns(CalBox calbox, int unitIndex, int driverIndex, short selectFlag) throws Exception {

		ChnSelectData csd = new ChnSelectData();
		csd.setUnitIndex(unitIndex);
		csd.setDriverIndex(driverIndex);
		csd.setChnFlag(selectFlag);
		configCommand(calbox, new ConfigDecorator(csd), TIME_OUT);

	}

	/**
	 * 操作通道进行相应操作
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param tm
	 * @throws Exception
	 */
	public void operateChannels(CalBox calbox, TestMode tm) throws Exception {

		TestModeData tmd = new TestModeData();
		tmd.setTestMode(tm);
		configCommand(calbox, new ConfigDecorator(tmd), TIME_OUT);

	}

	/**
	 * 时间校准
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @throws Exception
	 */
	public void configTime(CalBox calbox) throws Exception {

		TimeData td = new TimeData();
		td.setTime(new Date());
		configCommand(calbox, new ConfigDecorator(td), TIME_OUT);
	}

	/**
	 * 修改校准箱Ip
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param ip
	 * @throws Exception
	 */
	public void changeCalboxIp(CalBox calbox, String ip) throws Exception {

		IPCfgData ipData = new IPCfgData();
		ipData.setDeviceIp(ip);
		configCommand(calbox, new ConfigDecorator(ipData), TIME_OUT);
	}

	/**
	 * 修改校准方式
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param terminal
	 * @throws Exception
	 */
	public void changeTerminalDevice(CalBox calbox, CalibrateTerminal terminal) throws Exception {

		CalibrateTerminalData ctd = new CalibrateTerminalData();
		ctd.setCalibrateTerminal(terminal);
		configCommand(calbox, new ConfigDecorator(ctd), TIME_OUT);
	}

	/**
	 * 执行校准步骤
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @param calbox
	 * @param calType
	 * @throws Exception
	 */
	public void executeTest(CalBox calbox, TestMode mode) throws Exception {

		TestModeData tmd = new TestModeData();
		tmd = new TestModeData();
		tmd.setTestMode(mode);
		configCommand(calbox, new ConfigDecorator(tmd), TIME_OUT);

	}

	/**
	 * 下发选中的通道
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @param calbox
	 * @throws Exception
	 * @param select
	 *            true 选中所有选中的通道 ; false将所有测试或准备的通道选中
	 */
	public void selectChns(CalBox calbox, LogicBoard lb, boolean select) throws Exception {
		TestModeData tmd = new TestModeData();
		tmd.setTestMode(TestMode.ClearChnReadyFlag);
		configCommand(calbox, new ConfigDecorator(tmd), TIME_OUT);
		CommonUtil.sleep(10);

		int driverChnCount = calbox.getDevice().getChnNumInDriver();
		int driverCount = calbox.getDevice().getDriverNumInLogic();
		if(driverChnCount>16) {
			Data.setUseHugeDriverChnCount(true);		
		}
		for (int n = 0; n < driverCount; n++) {

			ChnSelectData csd = new ChnSelectData();
			csd.setUnitIndex(lb.getLogicIndex());
			csd.setDriverIndex(n);
			int flag = 0;
			for (int m = 0; m < driverChnCount; m++) {

				ChannelDO channel = lb.getChannels().get(n * driverChnCount + m);
				if (select) {
					if (channel.isSelected()) {

						flag = (int) (flag | 0x01 << m);
					}
				} else {

					if (channel.getState() == CalState.READY || channel.getState() == CalState.CALIBRATE
							|| channel.getState() == CalState.CALCULATE) {

						flag = (int) (flag | 0x01 << m);
					}

				}

			}
			csd.setChnFlag(flag);
			if (flag != 0) {

				System.out.println("flag:" + flag);
				System.out.println(csd);
				configCommand(calbox, new ConfigDecorator(csd), TIME_OUT);

			}

		}

	}

	/**
	 * 将校准板绑定到驱动板上
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @param calbox
	 * @param unitIndex
	 * @param driverIndex
	 * @param calIndex
	 * @throws Exception
	 */
	public void bindCalboardToDriver(CalBox calbox, int unitIndex, int driverIndex, int calIndex, boolean bind)
			throws Exception {

		BindCalBoardData bcbd = new BindCalBoardData();
		bcbd.setUnitIndex(unitIndex);
		bcbd.setDriverIndex(driverIndex);
		bcbd.setCalIndex(calIndex);
		bcbd.setBind(bind);

		configCommand(calbox, new ConfigDecorator(bcbd), TIME_OUT);

		if (bind) {
			calbox.setBindFlag( (calbox.getBindFlag() | 0x01 << calIndex));
		} else {

			calbox.setBindFlag( (calbox.getBindFlag() & ~(0x01 << calIndex)));
		}

	}

	/**
	 * 查询校准箱信息
	 * 
	 * @author wavy_zheng 2021年1月20日
	 * @param calbox
	 * @throws Exception
	 */
	public void queryBoxInfo(CalBox calbox) throws Exception {

		BaseCfgData bcd = new BaseCfgData();
		ResponseDecorator response = configCommand(calbox, new QueryDecorator(bcd), TIME_OUT);
		bcd = (BaseCfgData) response.getDestData();

		logger.info(bcd);
		List<String> meterIps = new ArrayList<>();
		for (String ip : bcd.getMeterIps()) {

			if (CommonUtil.checkIP(ip)) {

				meterIps.add(ip);
			}

		}
		calbox.setMeterIps(meterIps);
		calbox.setScreenIp(bcd.getScreenIp());

	}

	/**
	 * 查询设备自检信息
	 * 
	 * @author wavy_zheng 2021年2月9日
	 * @param calbox
	 * @return
	 * @throws Exception
	 */
	public DeviceSelfCheckData querySelfCheckInfo(CalBox calbox) throws Exception {

		DeviceSelfCheckData pstid = new DeviceSelfCheckData();
		ResponseDecorator response = configCommand(calbox, new QueryDecorator(pstid), TIME_OUT * 7);
		pstid = (DeviceSelfCheckData) response.getDestData();
		logger.info(pstid);
		return pstid;

	}

	/**
	 * 获取初始化信息
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @throws Exception
	 */
	public void queryInitInfo(CalBox calbox) throws Exception {

		BaseInfoQueryData biqd = new BaseInfoQueryData();
		ResponseDecorator response = configCommand(calbox, new QueryDecorator(new BaseInfoQueryData()), TIME_OUT);
		biqd = (BaseInfoQueryData) response.getDestData();

		calbox.setMac(biqd.getCalBoxMac());
		calbox.getDevice().setMac(biqd.getDeviceMac());
		// calbox.setCalBoardCount(biqd.getCalCount());

		if (biqd.getCalCount() > 0) {

			List<CalBoard> calBoardList = new ArrayList<>();
			for (int i = 0; i < biqd.getCalCount(); i++) {

				boolean open = (biqd.getCalState() & 0x01 << i) > 0;
				CalBoard board = new CalBoard(calbox, i, open);
				calBoardList.add(board);

			}
			calbox.setCalBoardList(calBoardList);
		}

		if (calbox.getDevice().getLogicNum() != biqd.getLogicCount()) {

			throw new Exception("校准箱逻辑板数:" + biqd.getLogicCount() + "与软件配置" + calbox.getDevice().getLogicNum() + "不一致");
		}
		if (calbox.getDevice().getDriverNumInLogic() != biqd.getDeviceDriverCount()) {

			throw new Exception("校准箱驱动板板数:" + biqd.getDeviceDriverCount() + "与软件配置"
					+ calbox.getDevice().getDriverNumInLogic() + "不一致");

		}
		if (calbox.getDevice().getChnNumInDriver() != biqd.getDeviceDriverChnCount()) {

			throw new Exception("校准箱驱动板通道数:" + biqd.getDeviceDriverChnCount() + "与软件配置"
					+ calbox.getDevice().getChnNumInDriver() + "不一致");
		}

	}

	/**
	 * 手动绑定或解绑
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param calbox
	 * @param unitIndex
	 * @param driverIndex
	 * @param calIndex
	 * @param bind
	 * @throws Exception
	 */
	public void bindCalToDriver(CalBox calbox, int unitIndex, int driverIndex, int calIndex, boolean bind)
			throws Exception {

		DriverBindData dbd = new DriverBindData();
		dbd.setUnitIndex(unitIndex);
		dbd.setDriverIndex(driverIndex);
		dbd.setBind(bind);
		dbd.setCalboardIndex(calIndex);
		configCommand(calbox, new ConfigDecorator(dbd), TIME_OUT);

	}

	public void addListener(CalboxListener listener) {

		listeners.add(listener);
	}

	public void removeListener(CalboxListener listener) {

		listeners.remove(listener);
	}

	public void clearListners() {

		listeners.clear();
	}

	public void triggerConnectListener(boolean connect, CalBox box) {

		for (CalboxListener listener : listeners) {

			if (connect) {
				listener.connected(box);
			} else {
				listener.disconnected(box);
			}
		}

	}

	public void triggerJoinListener(CalBox calbox, boolean ok) {

		for (CalboxListener listener : listeners) {

			listener.join(calbox, ok);

		}
	}

	public void triggerCalibrateListener(CalBox box, boolean calibrateOk) {

		for (CalboxListener listener : listeners) {

			listener.join(box, calibrateOk);
		}
	}

	public void triggerRecvState(CalBox box, List<ChannelDO> channels) {

		for (CalboxListener listener : listeners) {

			listener.onRecvChnState(box, channels);
		}
	}

	public void triggerRecvVoltage(CalBox box, List<ChannelDO> channels) {

		for (CalboxListener listener : listeners) {

			listener.onRecvChnState(box, channels);
		}
	}

	public void triggerRecvData(CalBox box, ChannelDO channel) {

		for (CalboxListener listener : listeners) {

			listener.onRecvChnData(box, channel);
		}
	}

	public void triggerTestStart(CalBox box, ChannelDO channel) {

		for (CalboxListener listener : listeners) {

			listener.onStartTest(box, channel);
		}
	}

	public void triggerLogData(CalBox box, TestLog log) {

		for (CalboxListener listener : listeners) {

			listener.onRecvLog(box, log);
		}
	}

	public LogicFlashWrite2DebugData readFlash(CalBox calBox, LogicFlashWrite2DebugData data) throws Exception {
		int moduleIndex=0;
		ResponseDecorator responseDecorator=configCommand(calBox, new QueryDecorator(data,(byte)moduleIndex), TIME_OUT);
		return (LogicFlashWrite2DebugData) responseDecorator.getDestData();
	}

	public void cfgRelaySwitch(CalBox calBox, RelaySwitchDebugData data) throws Exception {
		ResponseDecorator responseDecorator = configCommand(calBox, new ConfigDecorator(data), TIME_OUT);
		System.out.println("config relaySwitch " +responseDecorator.getResult());
	}

	public void cfgMeterRelaySwitch(CalBox calBox, RelayControlExDebugData data) throws Exception {
		ResponseDecorator responseDecorator = configCommand(calBox, new ConfigDecorator(data), TIME_OUT);
		System.out.println("config relayMeterSwitch " +responseDecorator.getResult());
	}
	
	public ResistanceModeRelayDebugData queryResistances(CalBox calBox, ResistanceModeRelayDebugData data) throws Exception {
		WorkPattern workPattern=data.getWorkPattern();
		byte relayIndex=data.getRelayIndex();
		int range=data.getRange();
		
		ResponseDecorator responseDecorator = configCommand(calBox, new QueryDecorator(data,relayIndex,(byte)workPattern.ordinal(),(byte)range), TIME_OUT);
		return (ResistanceModeRelayDebugData) responseDecorator.getDestData();
	}
	public void cfgResistances(CalBox calBox, ResistanceModeRelayDebugData data) throws Exception {
		
		ResponseDecorator responseDecorator = configCommand(calBox, new ConfigDecorator(data), TIME_OUT);
		
	}
	
}
