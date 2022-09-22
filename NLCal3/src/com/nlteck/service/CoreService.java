package com.nlteck.service;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.nlteck.calModel.base.CalculateException;
import com.nlteck.calModel.base.I18N;
import com.nlteck.calModel.base.DelayConfig.DetailConfig;
import com.nlteck.calModel.model.ABSCalMainBoard;
import com.nlteck.calModel.model.ABSLogicMianBoard;
import com.nlteck.calModel.model.NetworkCalMianBoard;
import com.nlteck.calModel.model.NetworkLogicMianBoard;
import com.nlteck.calModel.model.VirtureCalMainBoard;
import com.nlteck.calModel.model.VirtureLogicMainBoard;
import com.nlteck.calModel.model.VirtureMeter;
import com.nlteck.dialog.Test;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.BaseCfg.TestName;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.StableDataDO;
import com.nlteck.model.StablePlan.StableStep;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nlteck.model.TestItemDataDO;
import com.nlteck.model.TestLog;
import com.nlteck.report.CalibrateReport;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.PCWorkform.CalBoardTestModeData;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData.DriverCheckInfoData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.li.PCWorkform.ReadMeterData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.PCWorkform.SwitchMeterData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.modbus.tcp.util.CommonUtil;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData.CalParamData;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CheckResult;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.utils.LogUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年3月27日 下午9:44:11 核心测试服务 为了提高编程效率，降低复杂度.
 *          将不再使用主控进行核心测试，主控只作为命令的转发 该服务将代替原来的主控进行核心逻辑测试
 */
public class CoreService {
	private Logger logger;
	public ABSCalMainBoard absCalMainBoard;
	public ABSLogicMianBoard absLogicMainBoard;
	public Meter absMeter;

	public CoreService() {

		logger = LogUtil.getLogger("coreService");
	}

	/**
	 * 复位测试项目
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param channel
	 */
	public void resetChnTestItems(ChannelDO channel) {

		logger.info("reset test items");
		for (RunMode rm : WorkBench.baseCfg.runModes) {

			if (rm != RunMode.Cal && rm != RunMode.StableTest) {

				channel.putTestItems(rm, WorkBench.baseCfg.testItemMap.get(rm));

			}

		}

		channel.clearStableDatas();
		channel.clearDebugDatas();

	}

	/**
	 * 一般性测试
	 * 
	 * @author wavy_zheng 2022年3月29日
	 * @param channel
	 * @param rm
	 * @throws Exception
	 */
	private void executeCommonTest(ChannelDO channel, RunMode rm) throws Exception {

		for (TestItemDataDO item : channel.getTestItemsBy(rm)) {

			executeItem(channel, item);

		}

	}

	/**
	 * 启动
	 * 
	 * @author wavy_zheng 2022年3月29日
	 * @param channel
	 * @throws SQLException
	 */
	public void executeTest(ChannelDO channel) throws Exception {

		resetChnTestItems(channel);

//		executeStartCheck(channel.getDevice());

		try {
			for (RunMode rm : WorkBench.baseCfg.runModes) {

				// 设置正在测试的运行模式
				channel.setRunningMode(rm);
//				resetChnTestItems(channel);

				long tick = System.currentTimeMillis();
				channel.setStartTestTick(tick);
				try {

					if (rm == RunMode.Cal) {
						ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
						scheduledExecutorService.schedule(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
								try {
									executeCalTest(channel, true);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									scheduledExecutorService.shutdown();
								}
							}
						}, 0, TimeUnit.MINUTES);

					} else if (rm == RunMode.StableTest) {

						executeStableTest(channel);

					} else {

						executeCommonTest(channel, rm);
					}
				} catch (Exception e) {

					e.printStackTrace();

					throw e;
				}
			}

		} finally {

			channel.setRunningMode(null);
			channel.setReadyCommonTest(false);
		}

	}

	public void appendCalLog(ChannelDO channelDO,String content) {
		TestLog testLog=new TestLog(channelDO.getChnIndex(), "info", content, new Date());
		channelDO.appendTestLog(testLog);
	}
	
	public void calculate(ChannelDO channel) throws Exception {

		try {
			channel.setInfo("");// 信息重置
			channel.setLastTestDot(null);// 作为第一个点和KB值计算判断依据
			channel.getMeasureDotList().clear();
			channel.setMeasureDotList(WorkBench.calCfgManager.initCalculate(channel));
			channel.setCalculateIndex(0);

			channel.setStartTime(new Date());
			channel.setEndTime(null);
			channel.setState(CalState.CALCULATE);

			boolean useVirture = false;

			if (useVirture) {
				absCalMainBoard = new VirtureCalMainBoard();
				absLogicMainBoard = new VirtureLogicMainBoard();
				absMeter = new VirtureMeter(0);
			} else {
				absCalMainBoard = new NetworkCalMianBoard(channel.getDevice().getCalBoxList().get(0));
				absLogicMainBoard = new NetworkLogicMianBoard();
			}

			// 通道下发校准状态
			WorkBench.getBoxService().configBaseInfo(channel.getDevice().getCalBoxList().get(0));
			absCalMainBoard.changeModel(channel.getDevice().getCalBoxList(), CalibrateCoreWorkMode.CAL);

			for (TestDot dot : channel.getMeasureDotList()) {

				try {
					executeCalculateTestCore(dot, WorkBench.baseCfgManager.base.useRecal, false);
				} catch (CalculateException ex) {

					throw ex;

				}
			}
			channel.setState(CalState.CALCULATE_PASS);
		} catch (Exception e) {
			channel.setInfo(e.getMessage() + "");
			channel.setState(CalState.CALCULATE_FAIL);
			throw e;
		} finally {

			channel.setReady(false);
			// System.out.println("channel.getLastTestDot()="+channel.getLastTestDot());
			channel.setEndTime(new Date());

			if (channel.getLastTestDot() != null) {
				try {
					executeTestCloseAll(channel.getLastTestDot());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					channel.setLastTestDot(null);
				}
			}

		}
	}

	private void executeCalTest(ChannelDO channelDO, boolean retest) throws Exception {
		try {
			channelDO.setInfo("");
			// 作为第一个点和KB值计算判断依据
			channelDO.setLastTestDot(null);
			channelDO.getCalDotList().clear();
			channelDO.getMeasureDotList().clear();
			channelDO.setCalDotList(WorkBench.calCfgManager.initCalibrate(channelDO));// 初始化校准点
			channelDO.setMeasureDotList(WorkBench.calCfgManager.initCalculate(channelDO));// 初始化计量点
			appendCalLog(channelDO, "");
			if (!retest) {
				channelDO.setInfo(I18N.getVal(I18N.StartCalibrate));
				channelDO.setStartTime(new Date());
			}

			channelDO.setEndTime(null);
			channelDO.setState(CalState.CALIBRATE);
			boolean useVirture = WorkBench.baseCfgManager.virtual.use;

			if (useVirture) {
				absCalMainBoard = new VirtureCalMainBoard();
				absLogicMainBoard = new VirtureLogicMainBoard();
				absMeter = new VirtureMeter(0);
			} else {
				absCalMainBoard = new NetworkCalMianBoard(channelDO.getDevice().getCalBoxList().get(0));
				absLogicMainBoard = new NetworkLogicMianBoard();
			}

			// 通道下发校准状态
			WorkBench.getBoxService().configBaseInfo(channelDO.getDevice().getCalBoxList().get(0));
			absCalMainBoard.changeModel(channelDO.getDevice().getCalBoxList(), CalibrateCoreWorkMode.CAL);

			int moduleIndex = 0;
			for (TestDot dot : channelDO.getCalDotList()) {
				executeTestCore(dot);
				moduleIndex = dot.moduleIndex;
			}

			absLogicMainBoard.writeFlash(channelDO, moduleIndex);

			try {
				if (WorkBench.calCfgManager.calibratePlanData.isNeedCalculateAfterCalibrate()) {

					for (TestDot dot : channelDO.getMeasureDotList()) {

						try {
							executeCalculateTestCore(dot, WorkBench.baseCfgManager.base.useRecal, false);
						} catch (CalculateException ex) {

							if (WorkBench.baseCfgManager.kbAdjust.enable) {

								// 开始记录
								// triggerDebugLog(dot.getChannelDO(), "开始记录该失败计量点准备调整B值");

							} else {

								throw ex;
							}

						}

					}
					if (WorkBench.baseCfgManager.kbAdjust.enable) {

						for (int n = 0; n < WorkBench.baseCfgManager.kbAdjust.count; n++) {

							List<TestDot> failDots = absCalMainBoard.fetchAllFailDot(channelDO);
							if (failDots.isEmpty()) {

								break;
							}
							// 调整B值，并写入调整后的程控B值；注意，只写入主模片系数
							absCalMainBoard.adjustBValues(channelDO, failDots);

							for (TestDot dot : failDots) {

								try {
									executeTestCore(dot);
								} catch (CalculateException ex) {

									if (n < WorkBench.baseCfgManager.kbAdjust.count - 1) {
										// 开始记录
										// triggerDebugLog(dot.getChannelDO(), "开始记录该失败计量点准备调整B值");
									} else {

										throw ex;
									}

								}

							}

						}

					}

				}
			} catch (Exception ex) {

				if (!retest && WorkBench.baseCfgManager.base.useRecal) {

					// triggerErrorLog(channelDO, "计量失败,开始进行通道复测...");
					try {
						executeCalTest(channelDO, true);
					} catch (Exception e1) {

						e1.printStackTrace();
						// 不再抛出
					}

					return;

				} else {

					throw ex;
				}

			}

			channelDO.setState(CalState.CALIBRATE_PASS);
			System.out.println("++++++++++++++" + channelDO.getState() + "++++++++++++++++++");
		} catch (Exception e) {
			e.printStackTrace();
			channelDO.setInfo(e.getMessage() + "");
			channelDO.setState(CalState.CALIBRATE_FAIL);
			throw e;
		} finally {

			// if (retest) {
			//
			// // 避免重复
			// return;
			// }
			channelDO.setReady(false);
			channelDO.setEndTime(new Date());

			if (channelDO.getLastTestDot() != null) {
				try {
					executeTestCloseAll(channelDO.getLastTestDot());
				} catch (Exception e) {

				} finally {
					channelDO.setLastTestDot(null);
				}
			}
		}
	}

	private void executeTestCore(TestDot dot) throws Exception {
		executeTestCore(dot, WorkBench.baseCfgManager.base.useRecal, false);
	}

	/**
	 * 校准核心逻辑
	 *
	 * @param dot
	 * @param retest
	 *            是否在失败后启用单点继续校准？
	 * @param needOpen
	 *            是否需要打开膜片?
	 *
	 * @throws Exception
	 *
	 */
	private void executeCalculateTestCore(TestDot dot, boolean retest, boolean needOpen) throws Exception {
		// 获取延时
		DetailConfig detailConfig = WorkBench.calCfgManager.delayConfig.findDelay(dot);

		// 是否上报数据点
		boolean uploadData = true;
		try {
			// 模式切换且不是第一个点，需要关闭膜片，DC高精也需要关闭
			// 关闭膜片
			absCalMainBoard.switchModule(dot.getChannelDO().getDevice().getCalBoxList(),
					dot.getChannelDO().getDeviceChnIndex(), false);
			absCalMainBoard.TestSleep(detailConfig.moduleCloseDelay);
			// 下发校准板
			absCalMainBoard.sendCalMeasure(dot);

			// 下发逻辑板计量
			absCalMainBoard.sendLogicMeasure(dot);
			System.out.println("下发计量点" + dot.toString());

			// 打开膜片
			absCalMainBoard.switchModule(dot.getChannelDO().getDevice().getCalBoxList(),
					dot.getChannelDO().getDeviceChnIndex(), true);

			absCalMainBoard.TestSleep(detailConfig.modeChangeDelay);


			// 读取计量ADC
			absLogicMainBoard.gatherCalculateADC(dot);
			System.out.println("读取计量点" + dot.toString());

			logger.info("read meter sleep :" + detailConfig.readMeterDelay + "ms");
			// 读表延时
			absCalMainBoard.TestSleep(detailConfig.readMeterDelay);

			// 读取万用表
		
			absCalMainBoard.gatherMeter(dot, detailConfig);

			// 比较校准万用表偏差

			double adcOffSet = 0;
			double meterOffSet = 0;
			switch (dot.getMode()) {
			case CC:
			case DC:
				// 电流偏差从档位取
				meterOffSet = WorkBench.calCfgManager.rangeCurrentPrecisionData.getRanges().stream()
						.filter(x -> x.level == dot.getPrecision()).findAny().get().maxMeterOffset;
				adcOffSet = WorkBench.calCfgManager.rangeCurrentPrecisionData.getRanges().stream()
						.filter(x -> x.level == dot.getPrecision()).findAny().get().maxAdcOffset;
				break;
			case CV:
				// 电压偏差从计量计划取
				meterOffSet = WorkBench.calCfgManager.calculatePlanData.getMaxMeterOffset();
				adcOffSet = WorkBench.calCfgManager.calculatePlanData.getMaxAdcOffset();
				break;
			default:
				break;
			}

			System.out.println("计量点" + dot.toString());

			if (Math.abs(dot.getMeterVal() - dot.getProgramVal()) > meterOffSet) {
				throw new CalculateException(
						I18N.getVal(I18N.MeasureActualValOffsetOver, dot.getMeterVal() - dot.getProgramVal()));
			}

			// 处理adc偏差
			absCalMainBoard.adjustAdcOffset(true, dot, false);
			if (Math.abs(dot.getAdc() - dot.getProgramVal()) > adcOffSet) {
				throw new Exception(I18N.getVal(I18N.MeasureAdcOffsetOver, dot.getAdc() - dot.getProgramVal()));
			}

			if (dot.getMode() == DriverEnvironment.CalMode.CV) {
				if (WorkBench.baseCfgManager.base.calCheckBoard) {// 回检板启用

					// 处理adc偏差
					absCalMainBoard.adjustAdcOffset(false, dot, false);
					System.out.println("checkAdc:" + dot.getCheckAdc());
					if (Math.abs(dot.getCheckAdc() - dot.getProgramVal()) > WorkBench.calCfgManager.calculatePlanData
							.getMaxAdcOffsetCheck()) {

						throw new Exception(
								I18N.getVal(I18N.CheckBoardAdcOffsetOver, dot.getCheckAdc() - dot.getProgramVal()));
					}

				}
				// CV2
				if (!WorkBench.baseCfgManager.base.ignoreCV2) {

					// 处理adc偏差
					absCalMainBoard.adjustAdcOffset(true, dot, true);
					if (Math.abs(dot.checkAdc2 - dot.getProgramVal()) > WorkBench.calCfgManager.calculatePlanData
							.getMaxAdcOffsetCV2()) {

						throw new Exception(
								I18N.getVal(I18N.MeasureAdcOffsetOver, dot.checkAdc2 - dot.getProgramVal()));
					}

				}
			}

			dot.testResult = TestResult.Success;
			System.out.println(dot.testResult);
		} catch (Exception e) {
			dot.testResult = TestResult.Fail;
			dot.setInfo(e.getMessage() + "");
			e.printStackTrace();
			throw e;

		} finally {
			UploadTestDot uploadTestDot=new UploadTestDot();
			uploadTestDot.adc=dot.getAdc();
			uploadTestDot.adcK=dot.getAdck();
			uploadTestDot.adcB=dot.getAdcb();	
			uploadTestDot.adc2=dot.checkAdcK;
			uploadTestDot.adcK2=dot.checkAdcK2;
			uploadTestDot.adcB2=dot.checkAdcB2;
			uploadTestDot.checkAdc=dot.checkAdc2;
			uploadTestDot.checkAdcK=dot.checkAdcK2;
			uploadTestDot.checkAdcB=dot.checkAdcB2;
			uploadTestDot.chnIndex=dot.getChnIndex();
			uploadTestDot.meterVal=dot.getMeterVal();
			uploadTestDot.moduleIndex=dot.moduleIndex;
			uploadTestDot.mode=com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode.values()[dot.getMode().ordinal()];
			uploadTestDot.pole=com.nltecklib.protocol.li.main.PoleData.Pole.values()[dot.getPole().ordinal()];
			uploadTestDot.precision=dot.getPrecision();
			uploadTestDot.programVal=dot.getProgramVal();
			uploadTestDot.programK=dot.getProgramk();
			uploadTestDot.programB=dot.getProgramb();
			uploadTestDot.testType=dot.getTestType();
			uploadTestDot.success=dot.testResult==TestResult.Success?true:false;
			uploadTestDot.info=dot.getInfo();
			
			dot.getChannelDO().appendDebugData(uploadTestDot);
			dot.getChannelDO().setLastTestDot(dot);
			if (uploadData) {
				dot.time = new Date();
			}
		}

	}

	/**
	 * jiaozhun
	 * 
	 * @param dot
	 * @param retest
	 * @param needOpen
	 * @throws Exception
	 */
	private void executeTestCore(TestDot dot, boolean retest, boolean needOpen) throws Exception {
		
		
		
//		ResistanceModeRelayDebugData query=new ResistanceModeRelayDebugData();
//		query.setDriverIndex(0);
//		query.setRelayIndex((byte) 0);
//		query.setWorkPattern(WorkPattern.values()[dot.getMode().ordinal()]);
//		query.setRange(dot.getPrecision());
//
//		double resistivity = WorkBench.calboxService.queryResistances(dot.getChannelDO().getDevice().getCalBoxList().get(0), query).getResistance();
//		
//		System.out.println(resistivity+"===================");
		// 第一个测试点
		boolean isFirstDot = dot.getChannelDO().getLastTestDot() == null;
		boolean isModeChange = !dot.sameTestMode(dot.getChannelDO().getLastTestDot());
		// 获取延时
		DetailConfig detailConfig = WorkBench.calCfgManager.delayConfig.findDelay(dot);
		// 是否上报数据点
		boolean uploadData = true;
		try {
			// 模式切换且不是第一个点，需要关闭膜片，DC高精也需要关闭
			if (!isFirstDot && (isModeChange || detailConfig.dotClose)) {
				// 关闭膜片
				absCalMainBoard.switchModule(dot.getChannelDO().getDevice().getCalBoxList(),
						dot.getChannelDO().getDeviceChnIndex(), false);
				absCalMainBoard.TestSleep(detailConfig.moduleCloseDelay);
				if (dot.moduleIndex != dot.getChannelDO().getLastTestDot().moduleIndex) {

					logger.info("change module " + dot.getChannelDO().getLastTestDot().moduleIndex + " -> "
							+ dot.moduleIndex);
					absLogicMainBoard.writeFlash(dot.getChannelDO(), dot.getChannelDO().getLastTestDot().moduleIndex);
					// 清除校准参数
					// dot.channel.getCalDots().clear();

					CommonUtil.sleep(1000);
				}

				// if (dot.moduleIndex != 0 && dot.getTestType() == TestType.Cal) {
				// if (dot.getMode() == DriverEnvironment.CalMode.CC
				// || dot.getMode() == DriverEnvironment.CalMode.DC) {
				// dot.getChannelDO().setInfo(
				// "设置主膜片：" + I18N.getVal(I18N.CfgLogicBoardCalibrate, dot.getMode(),
				// dot.getPole()));
				// long da = 10000;
				// int range = dot.getPrecision();
				// if (dot.combine) {
				//
				// range = absCalMainBoard.getRangeForMeterVal(dot.mainMeter);
				// dot.getChannelDO().setInfo("找到" + dot.mainMeter + "的精度档位:" + range);
				// da = absCalMainBoard.getDAFromMeter(dot.getChannelDO(), 0, dot.getMode(),
				// range,
				// dot.mainMeter);
				// dot.getChannelDO().setInfo("计算得到DA:" + da);
				// }
				//
				// dot.getChannelDO().setInfo("主模片:" +
				// I18N.getVal(I18N.CfgLogicBoardCalibrateDebug, dot.getMode(),
				// dot.getPole(), range, 60000, da));
				//
				// LogicCalibrate2DebugData calibrate = new LogicCalibrate2DebugData();
				// calibrate.setChnIndex(dot.getChannelDO().getChnIndex());
				// calibrate.setModuleIndex(0);
				// calibrate.setWorkMode(dot.getMode());
				// calibrate.setPole(dot.getPole());
				// calibrate.setPrecision(range);
				// calibrate.setProgramV(60000);
				// calibrate.setProgramI(da);
				//
				// List<DriverCalibrateData.AdcData> adcs = new ArrayList<>();
				// for (int i = 0; i < WorkBench.calCfgManager.steadyCfgData.getSampleCount();
				// i++) {
				// adcs.add(new DriverCalibrateData.AdcData());
				// }
				// calibrate.setAdcs(adcs);
				//
				// for (CalBox calBox : dot.getChannelDO().getDevice().getCalBoxList()) {
				// WorkBench.boxService.cfgCalibrate(calBox, calibrate);
				// }
				//
				// CommonUtil.sleep(200);
				// }
				// }
			}

			if (!WorkBench.baseCfgManager.downFirstDotOnly || isModeChange || isFirstDot) {
				// 下发校准板

				absCalMainBoard.sendCalibrateDot(dot, true);

			}

			// 下发逻辑板校准
			absCalMainBoard.sendLogicDot(dot);

			if (isFirstDot || isModeChange || detailConfig.dotClose || needOpen) {
				// 打开膜片
				absCalMainBoard.switchModule(dot.getChannelDO().getDevice().getCalBoxList(),
						dot.getChannelDO().getDeviceChnIndex(), true);

				if (isFirstDot || isModeChange) {
					absCalMainBoard.TestSleep(detailConfig.modeChangeDelay);

				} else if (detailConfig.dotClose) {
					absCalMainBoard.TestSleep(detailConfig.moduleOpenDelay);
				}
				

			} else {
				// 程控设置等待
				if (dot.getMode() == DriverEnvironment.CalMode.CV && !WorkBench.baseCfgManager.base.ignoreCV2) {
					absCalMainBoard.TestSleep(detailConfig.programSetDelayCV2);
				} else {
					absCalMainBoard.TestSleep(detailConfig.programSetDelay);
				}

			}

			// 读取校准adc
			absLogicMainBoard.gatherCalibrateADC(dot);

			if (WorkBench.calCfgManager.calibratePlanData.isNeedValidate() && dot.getTestType() == TestType.Cal) {
				// 比较校准ADC偏差
				if (dot.getAdc() < 0) {
					throw new Exception(I18N.getVal(I18N.AdcOffsetOver, dot.getAdc(), dot.minAdc, dot.maxAdc));
				}
			}
			logger.info("read meter sleep :" + detailConfig.readMeterDelay + "ms");
			// 读表延时
			absCalMainBoard.TestSleep(detailConfig.readMeterDelay);

			// 读取万用表

			absCalMainBoard.gatherMeter(dot, detailConfig);

			// 比较校准万用表偏差
			if (WorkBench.calCfgManager.calibratePlanData.isNeedValidate() && dot.getTestType() == TestType.Cal) {
				if (dot.getMeterVal() < dot.minMeter || dot.getMeterVal() > dot.maxMeter) {

					throw new Exception(
							I18N.getVal(I18N.ActualValOffsetOver, dot.getMeterVal(), dot.minMeter, dot.maxMeter));
				}
			}

			// 计算KB值
			if (dot.getChannelDO().getLastTestDot() != null) {
				// 上一个点
				TestDot lastDot = dot.getChannelDO().getLastTestDot();
				// 与上一个点模式相同
				if (dot.sameTestMode(lastDot)) {
					// 尝试除以0
					if (dot.getMeterVal() == lastDot.getMeterVal()) {
						throw new Exception(I18N.getVal(I18N.MeterDiv0));
					}

					dot.setProgramk((dot.getProgramVal() - lastDot.getProgramVal())
							/ (dot.getMeterVal() - lastDot.getMeterVal()));
					dot.setProgramb(
							(dot.getMeterVal() * lastDot.getProgramVal() - lastDot.getMeterVal() * dot.getProgramVal())
									/ (dot.getMeterVal() - lastDot.getMeterVal()));

					// 尝试除以0
					if (dot.getAdc() == lastDot.getAdc()) {

						throw new Exception(I18N.getVal(I18N.AdcDiv0));

					}

					dot.setAdck((dot.getMeterVal() - lastDot.getMeterVal()) / (dot.getAdc() - lastDot.getAdc()));
					dot.setAdcb((dot.getAdc() * lastDot.getMeterVal() - lastDot.getAdc() * dot.getMeterVal())
							/ (dot.getAdc() - lastDot.getAdc()));

					// 比较逻辑板KB
					if (WorkBench.calCfgManager.calibratePlanData.isNeedValidate()) {

						try {
							if (dot.getProgramk() < dot.minProgramK || dot.getProgramk() > dot.maxProgramK) {

								// throw new Exception(I18N.getVal(I18N.ProgramKOffsetOver,dot.getProgramk()));
							}
							if (dot.getProgramb() < dot.minProgramB || dot.getProgramb() > dot.maxProgramB) {
								throw new Exception(I18N.getVal(I18N.ProgramBOffsetOver, dot.getProgramb()));
							}

							if (dot.getAdck() < dot.minAdcK || dot.getAdck() > dot.maxAdcK) {
								throw new Exception(I18N.getVal(I18N.AdcKOffsetOver, dot.getAdck()));
							}
							if (dot.getAdcb() < dot.minAdcB || dot.getAdcb() > dot.maxAdcB) {
								throw new Exception(I18N.getVal(I18N.AdcBOffsetOver, dot.getAdcb()));
							}

						} catch (Exception ex) {

							logger.info("catch exception :" + ex.getMessage());

							throw ex;

						}
					}
					if (dot.getMode() == DriverEnvironment.CalMode.CV && WorkBench.baseCfgManager.base.calCheckBoard) {

						// 回检板
						// 尝试除以0
						if (dot.getCheckAdc() == lastDot.getCheckAdc()) {

							System.out.println("dot.checkAdc = " + dot.getCheckAdc() + ",lastDot.getCheckAdc() = "
									+ lastDot.getCheckAdc());
							throw new Exception(I18N.getVal(I18N.CheckAdcDiv0));

						}
						dot.setCheckAdck((dot.getMeterVal() - lastDot.getMeterVal())
								/ (dot.getCheckAdc() - lastDot.getCheckAdc()));
						dot.setCheckAdcb(
								(dot.getCheckAdc() * lastDot.getMeterVal() - lastDot.getCheckAdc() * dot.getMeterVal())
										/ (dot.getCheckAdc() - lastDot.getCheckAdc()));

						// 校准回检ADC2
						if (!WorkBench.baseCfgManager.base.ignoreCV2) {

							// 尝试除以0
							if (dot.checkAdc2 == lastDot.checkAdc2) {
								throw new Exception(I18N.getVal(I18N.Cv2AdcDiv0));

							}
							dot.checkAdcK2 = (dot.getMeterVal() - lastDot.getMeterVal())
									/ (dot.checkAdc2 - lastDot.checkAdc2);
							dot.checkAdcB2 = (dot.checkAdc2 * lastDot.getMeterVal()
									- lastDot.checkAdc2 * dot.getMeterVal()) / (dot.checkAdc2 - lastDot.checkAdc2);
						}

						// 比较
						if (WorkBench.calCfgManager.calibratePlanData.isNeedValidate()) {
							if (dot.getCheckAdck() < dot.minCheckAdcK || dot.getCheckAdck() > dot.maxCheckAdcK) {
								throw new Exception(I18N.getVal(I18N.CheckBoardAdcKOffsetOver, dot.checkAdcK));

							}
							if (dot.getCheckAdcb() < dot.minCheckAdcB || dot.getCheckAdcb() > dot.maxCheckAdcB) {
								throw new Exception(I18N.getVal(I18N.CheckBoardAdcBOffsetOver, dot.checkAdcB));

							}
						}

					}
				}

			}

			dot.testResult = TestResult.Success;
			System.out.println(dot.testResult);
		} catch (Exception e) {
			dot.testResult = TestResult.Fail;
			dot.setInfo(e.getMessage() + "");
			e.printStackTrace();
			throw e;

		} finally {
			UploadTestDot uploadTestDot=new UploadTestDot();
			uploadTestDot.adc=dot.getAdc();
			uploadTestDot.adcK=dot.getAdck();
			uploadTestDot.adcB=dot.getAdcb();	
			uploadTestDot.adc2=dot.checkAdcK;
			uploadTestDot.adcK2=dot.checkAdcK2;
			uploadTestDot.adcB2=dot.checkAdcB2;
			uploadTestDot.checkAdc=dot.checkAdc2;
			uploadTestDot.checkAdcK=dot.checkAdcK2;
			uploadTestDot.checkAdcB=dot.checkAdcB2;
			uploadTestDot.chnIndex=dot.getChnIndex();
			uploadTestDot.meterVal=dot.getMeterVal();
			uploadTestDot.moduleIndex=dot.moduleIndex;
			uploadTestDot.mode=com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode.values()[dot.getMode().ordinal()];
			uploadTestDot.pole=com.nltecklib.protocol.li.main.PoleData.Pole.values()[dot.getPole().ordinal()];
			uploadTestDot.precision=dot.getPrecision();
			uploadTestDot.programVal=dot.getProgramVal();
			uploadTestDot.programK=dot.getProgramk();
			uploadTestDot.programB=dot.getProgramb();
			uploadTestDot.testType=dot.getTestType();
			uploadTestDot.success=dot.testResult==TestResult.Success?true:false;
			uploadTestDot.info=dot.getInfo();
			
			dot.getChannelDO().appendDebugData(uploadTestDot);
			dot.getChannelDO().setLastTestDot(dot);
			if (uploadData) {
				dot.time = new Date();
			}
		}

	}

	private void executeTestCloseAll(TestDot dot) throws Exception {
		if (WorkBench.baseCfgManager.base.stopMode == 0) {
			absCalMainBoard.switchModule(dot.getChannelDO().getDevice().getCalBoxList(),
					dot.getChannelDO().getDeviceChnIndex(), false);// 关闭膜片
			absCalMainBoard.sendCalibrateDot(dot, false);
		}

	}

	/**
	 * 开始稳定度测试
	 * 
	 * @author wavy_zheng 2022年3月29日
	 * @param channel
	 * @throws Exception
	 */
	private void executeStableTest(ChannelDO channel) throws Exception {

		resetChnTestItems(channel);

		if (channel.getWorkMode() != CalibrateCoreWorkMode.CAL) {

			channel.appendLog(RunMode.StableTest,
					new TestLog(channel.getDeviceChnIndex(), "DEBUG", "正在进入校准模式", new Date()));
			// 进校准模式
			for (CalBox box : channel.getDevice().getCalBoxList()) {
				WorkBench.getBoxService().changeDriverWorkMode(box, channel.getDriverIndex(),
						CalibrateCoreWorkMode.CAL);

			}
			int driverIndex = channel.getDriverIndex();

			for (int n = channel.getDevice().getChnNumInDriver() * driverIndex; n < (driverIndex + 1)
					* channel.getDevice().getChnNumInDriver(); n++) {

				channel.getDevice().getChannels().get(n).setWorkMode(CalibrateCoreWorkMode.CAL);

			}
		}

		try {

			int index = 0;
			for (int i = 0; i < WorkBench.stablePlan.getSteps().size(); i++) {

				if (channel.getRunningMode() == null) {

					throw new Exception("用户强制停止了测试!");
				}

				StableStep step = WorkBench.stablePlan.getSteps().get(i);

				// 进入计量模式
				logger.info("cfg calculate:" + step.destVal);

				for (CalBox box : channel.getDevice().getCalBoxList()) {

					CalCalculate2DebugData calculate = new CalCalculate2DebugData();
					calculate.setDriverIndex(channel.getDriverIndex());
					calculate.setChnIndex(channel.getDeviceChnIndex());
					calculate.setCalculateDot(step.destVal);
					calculate.setPole(PoleData.Pole.NORMAL);
					calculate.setWorkMode(CalEnvironment.WorkMode.valueOf(step.mode.name()));
					WorkBench.getBoxService().cfgCalboardCalculate(box, calculate);

					TestLog log = new TestLog(channel.getDeviceChnIndex(), "DEBUG",
							"配置校准板计量点" + step.destVal + ",极性: + " + ",模式:" + step.mode.name(), new Date());
					channel.appendLog(RunMode.StableTest, log);

				}
				TestLog logSlp = new TestLog(channel.getDeviceChnIndex(), "DEBUG", "延时500ms", new Date());
				channel.appendLog(RunMode.StableTest, logSlp);
				// 延时500ms
				CommonUtil.sleep(500);

				logger.info("cfg driver calculate:" + step.destVal);
				for (CalBox box : channel.getDevice().getCalBoxList()) {

					LogicCalculate2DebugData lcal = new LogicCalculate2DebugData();
					lcal.setDriverIndex(channel.getDriverIndex());
					lcal.setChnIndex(channel.getChnIndex());
					lcal.setCalculateDot(step.destVal);
					lcal.setPole(Pole.POSITIVE);
					lcal.setMode(step.mode);
					WorkBench.getBoxService().cfgCalculate(box, lcal);

					TestLog log = new TestLog(channel.getDeviceChnIndex(), "DEBUG",
							"配置通道" + (channel.getDeviceChnIndex() + 1) + "计量点" + step.destVal + ",极性:+ ,模式:"
									+ step.mode.name(),
							new Date());
					channel.appendLog(RunMode.StableTest, log);

				}

				// 延时500ms
				CommonUtil.sleep(500);

				logger.info("start to enable module");
				channel.appendLog(RunMode.StableTest,
						new TestLog(channel.getDeviceChnIndex(), "DEBUG", "正在使能通道", new Date()));
				for (CalBox box : channel.getDevice().getCalBoxList()) {

					WorkBench.getBoxService().cfgModuleSwitch(box, channel.getDeviceChnIndex(), true);
				}
				channel.appendLog(RunMode.StableTest, new TestLog(channel.getDeviceChnIndex(), "DEBUG",
						"延时" + WorkBench.stablePlan.getOpenModuleDelay() + "ms", new Date()));

				CommonUtil.sleep(WorkBench.stablePlan.getOpenModuleDelay());

				try {
					for (int n = 0; n < step.pickCount; n++) {

						if (channel.getRunningMode() == null) {

							throw new Exception("用户强制停止了测试!");
						}

						StableDataDO stable = new StableDataDO();
						stable.setIndex(index++);
						stable.setCalculateDot(step.destVal);
						stable.setMode(step.mode.name());
						stable.setPole("+");

						try {
							executeStableItem(channel, stable, n, step);
							stable.setResult("ok");
						} catch (Exception ex) {

							stable.setResult("ng");
							channel.appendTestLog(
									new TestLog(channel.getDeviceChnIndex(), "error", ex.getMessage(), new Date()));
							throw ex;

						}
						stable.setDate(new Date());
						channel.appendStableData(stable);
						// 通知界面更新数据
						channel.triggerStableDataChange(stable);

						CommonUtil.sleep(step.pickInterval);
					}
				} finally {

					// 关闭使能
					for (CalBox box : channel.getDevice().getCalBoxList()) {

						channel.appendLog(RunMode.StableTest,
								new TestLog(channel.getDeviceChnIndex(), "DEBUG", "正在关闭模片使能", new Date()));
						WorkBench.getBoxService().cfgModuleSwitch(box, channel.getDeviceChnIndex(), false);

						channel.appendLog(RunMode.StableTest, new TestLog(channel.getDeviceChnIndex(), "DEBUG",
								"延时" + WorkBench.stablePlan.getCloseModuleDelay() + "ms", new Date()));
						CommonUtil.sleep(WorkBench.stablePlan.getCloseModuleDelay());

						channel.appendLog(RunMode.StableTest,
								new TestLog(channel.getDeviceChnIndex(), "DEBUG", "正在关闭校准板", new Date()));

						CalCalculate2DebugData calculate = new CalCalculate2DebugData();
						calculate.setDriverIndex(channel.getDriverIndex());
						calculate.setChnIndex(channel.getDeviceChnIndex());
						calculate.setCalculateDot(step.destVal);
						calculate.setPole(PoleData.Pole.NORMAL);
						calculate.setWorkMode(WorkMode.SLEEP);
						calculate.setWorkState(WorkState.UNWORK);

						WorkBench.getBoxService().cfgCalboardCalculate(box, calculate);
					}
				}

			}

		} finally {

		}

	}

	private void executeStableItem(ChannelDO channel, StableDataDO stable, int index, StableStep step)
			throws Exception {

		logger.info("read driver calculate:" + stable.getCalculateDot());

		int readSteadyIndex = 0;

		do {
			for (CalBox box : channel.getDevice().getCalBoxList()) {

				LogicCalculate2DebugData response = WorkBench.getBoxService().readCalculate(box,
						channel.getDeviceChnIndex());
				if (response.getAdcDatas().size() < 2) {

					throw new Exception("adc计量数据太少,至少要2个以上");
				}

				double sum = 0;

				for (ReadonlyAdcData adc : response.getAdcDatas()) {

					for (int n = 0; n < Data.getModuleCount(); n++) {

						sum += adc.adcList.get(n).finalAdc;

					}
				}
				// 平均值
				double avg = sum / response.getAdcDatas().size();

				double tmp1 = 0;
				for (ReadonlyAdcData adc : response.getAdcDatas()) {

					double finalAdc = 0;
					for (int n = 0; n < Data.getModuleCount(); n++) {

						finalAdc += adc.adcList.get(n).finalAdc;

					}

					tmp1 += Math.pow(finalAdc - avg, 2);

				}
				double sigma1 = tmp1 / (response.getAdcDatas().size() - 1);
				// 标准差
				sigma1 = sigma1 >= 0 ? Math.sqrt(sigma1) : 0;

				TestLog log = new TestLog(channel.getDeviceChnIndex(), "DEBUG",
						"读取通道" + (channel.getDeviceChnIndex() + 1) + "计量点" + stable.getCalculateDot() + "ADC平均值" + avg
								+ ",标准差:" + sigma1,
						new Date());
				channel.appendLog(RunMode.StableTest, log);

				if (step.steadySigmaOffset > 0) {

					if (sigma1 <= step.steadySigmaOffset) {

						stable.setAdc(avg);
						break;
					}
				} else {

					stable.setAdc(avg);
					break;
				}

				// 延时1000ms
				CommonUtil.sleep((int) step.steadyReadInterval);

			}

		} while (++readSteadyIndex < step.maxSteadyReadCount);

		if (readSteadyIndex < step.maxSteadyReadCount) {

			// 已读到稳定ADC
			if (Math.abs(stable.getAdc() - stable.getCalculateDot()) > step.maxAdcOffset) {

				throw new Exception("adc:" + stable.getAdc() + "与计量点" + stable.getCalculateDot() + "偏差过大");
			}

			CommonUtil.sleep(100);
			// 读取万用表
			channel.appendLog(RunMode.StableTest,
					new TestLog(channel.getDeviceChnIndex(), "DEBUG", "读取万用表", new Date()));

			for (CalBox box : channel.getDevice().getCalBoxList()) {

				ReadMeterData smd = new ReadMeterData();
				smd.setDriverIndex(0);
				smd.setChnIndex(channel.getDeviceChnIndex());
				smd = WorkBench.getBoxService().readMeterDebugData(box, smd);

				if (CalMode.valueOf(stable.getMode()) == CalMode.CC
						|| CalMode.valueOf(stable.getMode()) == CalMode.DC) {

					// 附带参数
					int level = box.getCurrentRangeLevel(stable.getCalculateDot());
					if (level == -1) {

						throw new Exception("无法获取电流" + stable.getCalculateDot() + "mA的档位量程");
					}
					double factor = box.findResisterFactor(level,
							CalMode.valueOf(stable.getMode()) == CalMode.CC ? WorkPattern.CC : WorkPattern.DC);
					if (factor == 0) {

						throw new Exception("无法获取电流" + stable.getCalculateDot() + "mA的电阻系数");
					}
					channel.appendLog(RunMode.StableTest, new TestLog(channel.getDeviceChnIndex(), "DEBUG",
							"电阻系数:" + factor + ",表值:" + smd.getReadVal(), new Date()));

					stable.setMeter(factor * smd.getReadVal());

				} else {

					stable.setMeter(smd.getReadVal());

				}
			}

			if (Math.abs(stable.getMeter() - stable.getCalculateDot()) > step.maxMeterOffset) {

				throw new Exception("表值:" + stable.getMeter() + "与计量点" + stable.getCalculateDot() + "偏差过大");
			}

		} else {

			throw new Exception("读取ADC稳定超时");
		}
	}

	private void executeItem(ChannelDO channel, TestItemDataDO item) throws Exception {

		long tick = System.currentTimeMillis();
		item.setState("testing");

		try {

			if (channel.getRunningMode() == null) {

				throw new Exception("用户强制停止了测试!");
			}
			switch (item.getName()) {

			case ENTER_CAL:
				executeEnterCal(channel, item);
				break;
			case ENTER_NORMAL:
				executeEnterNormal(channel, item);
				break;
			case SLEEP:
				executeSleep(channel, item);
				break;
			case WRITE_FLASH:
				executeWriteFlash(channel, item);
				break;
			case CALBOARD_VOLT_TEST:
			case CALBOARD_SHORT_POSTIVE:
			case CALBOARD_SHORT_NEGTIVE:
				executeCalboardTestMode(channel, item);
				break;
			case CALBOARD_CC:
			case CALBOARD_CV:
			case CALBOARD_DC:
			case CALBOARD_SLEEP:
				executeCalboardCalMode(channel, item);
				break;
			case SWITCH_METER:
				executeSwitchMeter(channel, item);
				break;
			case READ_METER:
				executeReadMeter(channel, item);
				break;
			case FLASH_CHECK:
				executeFlashCheck(channel, item);
				break;
			case AD_PICK_CHECK:
				executeADCheck(channel, item);
				break;
			case SRAM_CHECK:
				executeSramCheck(channel, item);
				break;
			case CAL_CHECK:
				executeCalCheck(channel, item);
				break;
			case BACKUP_CHECK:
				executeBackupCheck(channel, item);
				break;

			case MAIN_BACK_COMPARAM:
				executeMainBackupVoltComprare(channel, item);
				break;
			case BACK_POWER_COMPARAM:
				executeBackupPowerVoltComprare(channel, item);
				break;
			case MAIN_POWER_COMPARAM:
				executeMainPowerVoltComprare(channel, item);
				break;
			case PROCEDURE:
				break;

			case VIRTUAL_VOLTAGE_SCAN:
				executeVirtualVoltScan(channel, item);
				break;
			case DEVICE_SELFCHECK:
				executeReadCheck(channel, item);
				break;
			default:
				throw new Exception("未知测试项:" + item.getName().toString());

			}

			item.setState("ok");
		} catch (Exception ex) {

			item.setInfo(ex.getMessage());
			item.setState("ng");

		} finally {

			item.setMilisecs(System.currentTimeMillis() - tick);
		}

	}

	/**
	 * 切表
	 * 
	 * @author wavy_zheng 2022年3月29日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeSwitchMeter(ChannelDO channel, TestItemDataDO item) throws Exception {

		for (CalBox box : channel.getDevice().getCalBoxList()) {

			SwitchMeterData smd = new SwitchMeterData();
			smd.setDriverIndex(0);
			smd.setChnIndex(channel.getDeviceChnIndex());
			smd.setConnect(true);

			WorkBench.getBoxService().cfgMeterChange(box, smd);
		}

	}

	/**
	 * 检查通道flash
	 * 
	 * @author wavy_zheng 2022年3月31日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeFlashCheck(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getSelftCheck() == null) {

			throw new Exception("没有查到该通道的自检信息");
		}
		if (channel.getSelftCheck().driverFlash != CheckResult.NORMAL) {

			throw new Exception("flash故障:" + channel.getSelftCheck().driverFlash);
		}

	}

	private void executeSramCheck(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getSelftCheck() == null) {

			throw new Exception("没有查到该通道的自检信息");
		}
		if (channel.getSelftCheck().driverSram != CheckResult.NORMAL) {

			throw new Exception("sram故障:" + channel.getSelftCheck().driverSram);
		}

	}

	private void executeCalCheck(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getSelftCheck() == null) {

			throw new Exception("没有查到该通道的自检信息");
		}
		if (channel.getSelftCheck().calParam != CheckResult.NORMAL) {

			throw new Exception("校准系数故障:" + channel.getSelftCheck().calParam);
		}

	}

	/**
	 * 执行流程下发
	 * 
	 * @author wavy_zheng 2022年4月6日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeProcedureItem(ChannelDO channel, TestItemDataDO item) throws Exception {

		String[] params = item.getParam().toString().split(";");
		if (params.length != 2) {

			throw new Exception("流程未配置参数模式和恒定值(模式;恒定值)");
		}

	}

	/**
	 * 虚压扫描
	 * 
	 * @author wavy_zheng 2022年3月31日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeVirtualVoltScan(ChannelDO channel, TestItemDataDO item) throws Exception {

		// 扫描同个驱动板相邻通道的电压情况
		if (channel.getChannelData() == null) {

			throw new Exception("没有查到该通道的采集信息");
		}
		for (int n = channel.getDriverIndex()
				* channel.getDevice().getChnNumInDriver(); n < (channel.getDriverIndex() + 1)
						* channel.getDevice().getChnNumInDriver(); n++) {

			ChannelDO chn = channel.getDevice().getChannels().get(n);
			if (chn != channel) {

				if (chn.getChannelData() == null) {

					throw new Exception("相邻通道" + (chn.getDeviceChnIndex() + 1) + "没有查到该通道的采集信息");

				}
				if (chn.getChannelData().getVoltage() > item.getUpper() && item.getUpper() > 0) {

					throw new Exception("相邻通道" + (chn.getDeviceChnIndex() + 1) + "虚电压:"
							+ chn.getChannelData().getVoltage() + " > " + item.getUpper());
				}

				if (chn.getChannelData().getVoltage() < item.getLower() && item.getLower() > 0) {

					throw new Exception("相邻通道" + (chn.getDeviceChnIndex() + 1) + "虚电压:"
							+ chn.getChannelData().getVoltage() + " < " + item.getLower());
				}

			}

		}

	}

	/**
	 * 主备份电压测试
	 * 
	 * @author wavy_zheng 2022年3月31日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeMainBackupVoltComprare(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getChannelData() == null) {

			throw new Exception("没有查到该通道的采集信息");
		}
		item.setTestVal(Math.abs(channel.getChannelData().getVoltage() - channel.getChannelData().getDeviceVoltage()));
		if (item.getUpper() > 0
				&& Math.abs(channel.getChannelData().getVoltage() - channel.getChannelData().getDeviceVoltage()) > item
						.getUpper()) {

			throw new Exception("主电压:" + channel.getChannelData().getVoltage() + ",备份电压:"
					+ channel.getChannelData().getDeviceVoltage());
		}

		if (item.getLower() > 0
				&& Math.abs(channel.getChannelData().getVoltage() - channel.getChannelData().getDeviceVoltage()) < item
						.getLower()) {

			throw new Exception("主电压:" + channel.getChannelData().getVoltage() + ",备份电压:"
					+ channel.getChannelData().getDeviceVoltage());
		}
	}

	private void executeBackupPowerVoltComprare(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getChannelData() == null) {

			throw new Exception("没有查到该通道的采集信息");
		}
		item.setTestVal(
				Math.abs(channel.getChannelData().getPowerVoltage() - channel.getChannelData().getDeviceVoltage()));
		if (item.getUpper() > 0 && Math
				.abs(channel.getChannelData().getPowerVoltage() - channel.getChannelData().getDeviceVoltage()) > item
						.getUpper()) {

			throw new Exception("功率电压:" + channel.getChannelData().getPowerVoltage() + ",备份电压:"
					+ channel.getChannelData().getDeviceVoltage());
		}

		if (item.getLower() > 0 && Math
				.abs(channel.getChannelData().getPowerVoltage() - channel.getChannelData().getDeviceVoltage()) < item
						.getLower()) {

			throw new Exception("功率电压:" + channel.getChannelData().getPowerVoltage() + ",备份电压:"
					+ channel.getChannelData().getDeviceVoltage());
		}
	}

	private void executeMainPowerVoltComprare(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getChannelData() == null) {

			throw new Exception("没有查到该通道的采集信息");
		}
		item.setTestVal(Math.abs(channel.getChannelData().getPowerVoltage() - channel.getChannelData().getVoltage()));
		if (item.getUpper() > 0
				&& Math.abs(channel.getChannelData().getPowerVoltage() - channel.getChannelData().getVoltage()) > item
						.getUpper()) {

			throw new Exception("功率电压:" + channel.getChannelData().getPowerVoltage() + ",备份电压:"
					+ channel.getChannelData().getVoltage());
		}

		if (item.getLower() > 0 && Math
				.abs(channel.getChannelData().getPowerVoltage() - channel.getChannelData().getDeviceVoltage()) < item
						.getLower()) {

			throw new Exception("功率电压:" + channel.getChannelData().getPowerVoltage() + ",备份电压:"
					+ channel.getChannelData().getDeviceVoltage());
		}
	}

	private void executeBackupCheck(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getSelftCheck() == null) {

			throw new Exception("没有查到该通道的自检信息");
		}
		if (channel.getSelftCheck().checkboard != CheckResult.NORMAL) {

			throw new Exception("备份板故障:" + channel.getSelftCheck().checkboard);
		}

	}

	private void executeADCheck(ChannelDO channel, TestItemDataDO item) throws Exception {

		if (channel.getSelftCheck() == null) {

			throw new Exception("没有查到该通道的自检信息");
		}
		if (channel.getSelftCheck().adPick != CheckResult.NORMAL) {

			throw new Exception("AD采集板故障:" + channel.getSelftCheck().adPick);
		}

	}

	/**
	 * 读表
	 * 
	 * @author wavy_zheng 2022年3月29日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeReadMeter(ChannelDO channel, TestItemDataDO item) throws Exception {

		for (CalBox box : channel.getDevice().getCalBoxList()) {

			ReadMeterData smd = new ReadMeterData();
			smd.setDriverIndex(0);
			smd.setChnIndex(channel.getDeviceChnIndex());
			smd = WorkBench.getBoxService().readMeterDebugData(box, smd);

			if (item.getCalMode() == CalMode.CC || item.getCalMode() == CalMode.DC) {

				// 附带参数
				double current = Double.parseDouble(item.getParam().toString());
				int level = box.getCurrentRangeLevel(current);
				if (level == -1) {

					throw new Exception("无法获取电流" + current + "mA的档位量程");
				}
				double factor = box.findResisterFactor(level,
						item.getCalMode() == CalMode.CC ? WorkPattern.CC : WorkPattern.DC);
				if (factor == 0) {

					throw new Exception("无法获取电流" + current + "mA的电阻系数");
				}
				item.setInfo("电阻系数:" + factor + ",表值:" + smd.getReadVal());
				item.setTestVal(factor * smd.getReadVal());

			} else {

				item.setTestVal(smd.getReadVal());

			}

			if (item.getLower() > 0) {

				if (item.getTestVal() < item.getLower()) {

					throw new Exception("小于下限值");
				}
			}

			if (item.getUpper() > 0) {

				if (item.getTestVal() > item.getLower()) {

					throw new Exception("大于上限值");
				}
			}

		}

	}

	/**
	 * 控制校准板的校准模式
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeCalboardCalMode(ChannelDO channel, TestItemDataDO item) throws Exception {

		for (CalBox box : channel.getDevice().getCalBoxList()) {

			CalCalculate2DebugData data = new CalCalculate2DebugData();
			data.setCalculateDot(1000);
			data.setDriverIndex(0); // 通道映射后，校准板号通过通道号计算出；此处直接给0
			data.setChnIndex(channel.getDeviceChnIndex()); // 通过通道序号自动映射校准板号
			data.setPole(PoleData.Pole.NORMAL);
			data.setWorkState(WorkState.WORK);
			if (item.getName().equals(TestName.CALBOARD_CC)) {

				data.setWorkMode(WorkMode.CC);
			} else if (item.getName().equals(TestName.CALBOARD_CV)) {

				data.setWorkMode(WorkMode.CV);
			} else if (item.getName().equals(TestName.CALBOARD_DC)) {

				data.setWorkMode(WorkMode.DC);
			} else if (item.getName().equals(TestName.CALBOARD_SLEEP)) {

				data.setWorkState(WorkState.UNWORK);
				data.setWorkMode(WorkMode.SLEEP);
			}

			WorkBench.getBoxService().cfgCalboardCalculate(box, data);
		}
	}

	private void executeCalboardTestMode(ChannelDO channel, TestItemDataDO item) throws Exception {

		int driverIndex = channel.getDeviceChnIndex() / channel.getDevice().getChnNumInDriver();

		CalEnvironment.TestType type = null;
		if (item.getName() == TestName.CALBOARD_VOLT_TEST) {

			type = CalEnvironment.TestType.VOL_COMPARE;
		} else if (item.getName() == TestName.CALBOARD_SHORT_POSTIVE) {

			type = CalEnvironment.TestType.POSITIVE_SHORT_CIRCUIT;
		} else if (item.getName() == TestName.CALBOARD_SHORT_NEGTIVE) {

			type = CalEnvironment.TestType.NEGATIVE_SHORT_CIRCUIT;
		}

		for (CalBox box : channel.getDevice().getCalBoxList()) {

			CalBoardTestModeData data = new CalBoardTestModeData();
			data.setDriverIndex(driverIndex);
			data.setTestType(type);

			WorkBench.getBoxService().cfgCalboardTestMode(box, data);

			// 设置状态
			box.getCalBoardList().get(driverIndex).setTestType(type);

		}

	}

	/**
	 * 读取自检数据
	 * 
	 * @author wavy_zheng 2022年3月31日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeReadCheck(ChannelDO channel, TestItemDataDO item) throws Exception {

		Device device = channel.getDevice();
		for (CalBox box : device.getCalBoxList()) {

			DeviceSelfCheckData data = new DeviceSelfCheckData();
			StringBuffer info = new StringBuffer();
			DeviceSelfCheckData result = (DeviceSelfCheckData) WorkBench.getBoxService().querySelfCheckInfo(box);
			if (result == null) {

				throw new Exception(info.toString());
			}
			for (DriverCheckInfoData check : result.getDriverCheckInfoDataList()) {

				for (int n = check.driverIndex * device.getChnNumInDriver(); n < (check.driverIndex + 1)
						* device.getChnNumInDriver(); n++) {

					device.getChannels().get(n).setSelftCheck(check);
				}

			}

		}

	}

	/**
	 * 进入校准模式
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeEnterCal(ChannelDO channel, TestItemDataDO item) throws Exception {

		int driverIndex = channel.getDeviceChnIndex() / channel.getDevice().getChnNumInDriver();

		for (CalBox box : channel.getDevice().getCalBoxList()) {

			WorkBench.getBoxService().changeDriverWorkMode(box, driverIndex, CalibrateCoreWorkMode.CAL);

		}
		for (int n = channel.getDevice().getChnNumInDriver() * driverIndex; n < (driverIndex + 1)
				* channel.getDevice().getChnNumInDriver(); n++) {

			channel.getDevice().getChannels().get(n).setWorkMode(CalibrateCoreWorkMode.CAL);

		}

	}

	/**
	 * 写入当前调试页面的系数
	 * 
	 * @author wavy_zheng 2022年2月19日
	 * @throws Exception
	 */
	private LogicFlashWrite2DebugData createFlashData(ChannelDO channel, List<UploadTestDot> debugDatas)
			throws Exception {

		// 驱动板flash
		Map<DriverEnvironment.CalMode, List<CalParamData>> dotMap = new HashMap<>();

		List<CalParamData> checkList = new ArrayList<>();
		List<CalParamData> check2List = new ArrayList<>();

		int currentModuleIndex = -1;

		for (int n = 0; n < debugDatas.size(); n++) {

			UploadTestDot calDot = debugDatas.get(n);

			if (calDot.testType != TestType.Cal) {

				continue;
			}

			if (currentModuleIndex == -1) {

				currentModuleIndex = calDot.moduleIndex;
			}
			if (currentModuleIndex != calDot.moduleIndex || n == debugDatas.size() - 1) {

				List<CalParamData> cvParams = dotMap.get(DriverEnvironment.CalMode.CV);
				List<CalParamData> dcParams = dotMap.get(DriverEnvironment.CalMode.DC);
				List<CalParamData> ccParams = dotMap.get(DriverEnvironment.CalMode.CC);
				if (cvParams == null) {

					cvParams = new ArrayList<>();
				}
				if (ccParams == null) {

					ccParams = new ArrayList<>();
				}
				if (dcParams == null) {

					dcParams = new ArrayList<>();
				}

				if (!checkList.isEmpty()) {

					cvParams.addAll(checkList);
				}
				if (!check2List.isEmpty()) {

					cvParams.addAll(check2List);
				}
				int cvTotalCount = cvParams.size();

				final TestLog log = new TestLog(channel.getDeviceChnIndex(), "DEBUG",
						"共写入cc" + ccParams.size() + ",cv " + (cvTotalCount - checkList.size() - check2List.size())
								+ ",dc" + dcParams.size() + ",cv1 " + checkList.size() + ",cv2 " + check2List.size(),
						new Date());
				// 生成日志
				channel.appendLog(channel.getRunningMode(), log);

				// 写入或者
				LogicFlashWrite2DebugData flash = new LogicFlashWrite2DebugData();

				flash.setChnIndex(channel.getChnIndex());
				flash.setModuleIndex(currentModuleIndex); // 选择模片写入
				flash.setKb_dotMap(dotMap);

				if (!ccParams.isEmpty()) {
					System.out.println("first cc adc :" + ccParams.get(0).adc);
				}

				flash.setCv1DotCount(checkList.size());
				flash.setCv2DotCount(check2List.size());

				// 清除
				dotMap.clear();

				return flash;

			}

			if (calDot.adcK != 0) {
				CalParamData cd = new CalParamData();
				cd.calMode = CalMode.values()[calDot.mode.ordinal()];
				cd.pole = Pole.values()[calDot.pole.ordinal()];
				cd.meter = calDot.meterVal;
				cd.adc = calDot.adc;
				cd.adcK = calDot.adcK;
				cd.adcB = calDot.adcB;
				cd.da = (int) calDot.programVal;
				cd.programK = calDot.programK;
				cd.programB = calDot.programB;
				cd.range = calDot.precision;

				if (dotMap.containsKey(cd.calMode)) {

					dotMap.get(cd.calMode).add(cd);

				} else {

					List<CalParamData> list = new ArrayList<>();
					list.add(cd);
					dotMap.put(cd.calMode, list);
				}

				if (calDot.mode == Logic2Environment.CalMode.CV) {

					if (calDot.checkAdc != 0) {

						CalParamData back1 = new CalParamData();
						back1.calMode = CalMode.values()[calDot.mode.ordinal()];
						back1.pole = Pole.values()[calDot.pole.ordinal()];
						back1.meter = calDot.meterVal;
						back1.adc = calDot.checkAdc;
						back1.adcK = calDot.checkAdcK;
						back1.adcB = calDot.checkAdcB;
						back1.da = (int) calDot.programVal;
						back1.programK = calDot.programK;
						back1.programB = calDot.programB;
						back1.range = calDot.precision;
						checkList.add(back1);
					}

					if (calDot.adc2 != 0) {

						CalParamData back2 = new CalParamData();
						back2.calMode = CalMode.values()[calDot.mode.ordinal()];
						back2.pole = Pole.values()[calDot.pole.ordinal()];
						back2.meter = calDot.meterVal;
						back2.adc = calDot.adc2;
						back2.adcK = calDot.adcK2;
						back2.adcB = calDot.adcB2;
						back2.da = (int) calDot.programVal;
						back2.programK = calDot.programK;
						back2.programB = calDot.programB;
						back2.range = calDot.precision;
						check2List.add(back2);

					}

				}
			}
		}

		return null;

	}

	private void executeWriteFlash(ChannelDO channel, TestItemDataDO item) throws Exception {

		// 加载flash文件
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(item.getParam().toString()));

		List<UploadTestDot> datas = CalibrateReport.importCalSheet(wb, channel.getDeviceChnIndex());

		LogicFlashWrite2DebugData flash = createFlashData(channel, datas);

		if (flash == null) {

			throw new Exception("生成flash数据失败");
		}

		for (CalBox box : channel.getDevice().getCalBoxList()) {
			WorkBench.getBoxService().cfgFlash(box, flash);

		}

	}

	private void executeEnterNormal(ChannelDO channel, TestItemDataDO item) throws Exception {

		int driverIndex = channel.getDeviceChnIndex() / channel.getDevice().getChnNumInDriver();

		for (CalBox box : channel.getDevice().getCalBoxList()) {

			WorkBench.getBoxService().changeDriverWorkMode(box, driverIndex, CalibrateCoreWorkMode.NONE);

		}

		for (int n = channel.getDevice().getChnNumInDriver() * driverIndex; n < (driverIndex + 1)
				* channel.getDevice().getChnNumInDriver(); n++) {

			channel.getDevice().getChannels().get(n).setWorkMode(CalibrateCoreWorkMode.NONE);

		}

	}

	/**
	 * 执行休眠
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param channel
	 * @param item
	 * @throws Exception
	 */
	private void executeSleep(ChannelDO channel, TestItemDataDO item) throws Exception {

		CommonUtil.sleep(Integer.parseInt(item.getParam().toString()));
		item.setTestVal(Double.parseDouble(item.getParam().toString()));
	}

	/**
	 * 重新加载测试项目
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param channel
	 * @param runMode
	 * @throws SQLException
	 */
	public void loadChnTestItems(ChannelDO channel, RunMode runMode) throws SQLException {

		if (runMode == null) {

			for (RunMode rm : WorkBench.baseCfg.runModes) {

				loadChnTestItems(channel, rm);
			}

		} else {

			if (runMode != RunMode.Cal && runMode != RunMode.StableTest) {

				List<TestItemDataDO> items = WorkBench.dataManager.listTestItems(channel, runMode);

				if (!items.isEmpty()) {
					channel.putTestItems(runMode, items);
				} else {
					// 数据库中无保存数据，直接加载配置文件测试项目
					channel.putTestItems(runMode, WorkBench.baseCfg.testItemMap.get(runMode));
				}
			} else if (runMode == RunMode.Cal) {

				channel.clearDebugDatas();

			} else if (runMode == RunMode.StableTest) {

				channel.clearStableDatas();

			}
			channel.clearLog(runMode);

		}

	}

	/**
	 * 
	 * @author wavy_zheng 2022年3月29日
	 */
	private void executeStartCheck(Device device) throws Exception {

		// 检查是否加载配置文件
		for (CalBox box : device.getCalBoxList()) {

			logger.info("check range current file");
			if (box.getRangeCurrentPrecision() == null) {

				logger.info("load range current file");
				box.loadRangeCurrentPrecision();
			}
			logger.info("check resistor factors");
			if (box.getResisterFactors().isEmpty()) {

				List<CalResistanceDebugData> resisters = new ArrayList<>();

				for (RangeCurrentPrecision range : box.getRangeCurrentPrecision().getRanges()) {

					CalResistanceDebugData crd = new CalResistanceDebugData();
					crd.setDriverIndex(0); // 默认读第一块校准板的电阻系数，程序认为两块校准必须有一致的电阻系数
					crd.setRange(range.level);
					crd.setWorkPattern(WorkPattern.CC);
					crd = WorkBench.getBoxService().readNewResistanceDebug(box, crd);

					logger.info("read resistor factors, cc ," + range.level + ",resister:" + crd.getResistance());
					resisters.add(crd);

					crd.setWorkPattern(WorkPattern.DC);
					WorkBench.getBoxService().readNewResistanceDebug(box, crd);

					logger.info("read resistor factors, dc ," + range.level + ",resister:" + crd.getResistance());

					resisters.add(crd);

				}

				box.setResisterFactors(resisters);

			}

		}

	}

}
