package com.nlteck.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.nlteck.base.BaseCfgManager.AdcAdjust;
import com.nlteck.base.BaseCfgManager.CalculateValidate;
import com.nlteck.base.BaseCfgManager.EquipType;
import com.nlteck.base.BaseCfgManager.MeterPart;
import com.nlteck.base.CalculateException;
import com.nlteck.base.I18N;
import com.nlteck.fireware.CalBoard;
import com.nlteck.fireware.CalibrateCore;
import com.nlteck.model.CalBoardChannel;
import com.nlteck.model.Channel;
import com.nlteck.model.DelayConfig.DetailConfig;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.MBWorkform.MBCalMatchData;
import com.nltecklib.protocol.li.MBWorkform.MBCheckCalculateData;
import com.nltecklib.protocol.li.MBWorkform.MBCheckCalibrateData;
import com.nltecklib.protocol.li.MBWorkform.MBCheckFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCalculateData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCalibrateData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCheckFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBSelfCheckData;
import com.nltecklib.protocol.li.MBWorkform.MBSelfTestInfoData;
import com.nltecklib.protocol.li.PCWorkform.BindCalBoardData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardTestModeData;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempQueryDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.ChnSelectData;
import com.nltecklib.protocol.li.PCWorkform.ConnectDeviceData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData.DriverCheckInfoData;
import com.nltecklib.protocol.li.PCWorkform.DriverModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.MatchStateData;
import com.nltecklib.protocol.li.PCWorkform.MeterConnectData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.ModuleSwitchData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfTestInfoData;
import com.nltecklib.protocol.li.PCWorkform.ReadMeterData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculateDotData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.li.PCWorkform.RequestCalculateData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.PCWorkform.SwitchMeterData;
import com.nltecklib.protocol.li.PCWorkform.TestModeData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.ResistanceModeData;
import com.nltecklib.protocol.li.cal.ResistanceModeRelayData;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.check2.Check2Environment.AdcGroup;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.check2.Check2Environment.Work;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData.AdcData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalculateAdcGroup;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalibrateAdcGroup;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData.CalDot;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment;
import com.nltecklib.protocol.power.calBox.calBox_device.MbCalibrateChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbDriverModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbSelfCheckData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData.CalParamData;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.utils.LogUtil;

/**
 * 校准服务
 * 
 * @author guofang_ma
 *
 */
public class CalibrateService {

	private CalibrateCore core;

	private Logger logger;

	public CalibrateService(CalibrateCore core) {
		this.core = core;

		logger = LogUtil.getLogger("calibrateService");
	}

	/**
	 * 上报生产日志
	 * 
	 * @param channel
	 * @param log
	 */
	private void triggerLog(Channel channel, String log) {

		core.getNetworkService().pushLog(channel, log, false);
	}

	/**
	 * 上报生产错误日志
	 * 
	 * @param channel
	 * @param log
	 */
	private void triggerErrorLog(Channel channel, String log) {

		core.getNetworkService().pushLog(channel, log, true);
	}

	/**
	 * 上报调试日志
	 * 
	 * @param channel
	 * @param log
	 */
	private void triggerDebugLog(Channel channel, String log) {

		core.getNetworkService().pushDebugLog(channel, log, false);
	}

	/**
	 * 上报错误调试日志
	 * 
	 * @param channel
	 * @param log
	 */
	private void triggerDebugErrorLog(Channel channel, String log) {

		core.getNetworkService().pushDebugLog(channel, log, true);
	}

	/**
	 * 上报通道状态
	 * 
	 * @param channel
	 */
	private void triggerChnData(Channel channel) {

		core.getNetworkService().pushChnData(Arrays.asList(channel));
		core.getScreen().setState(channel);

	}

	/**
	 * 通道校准
	 * 
	 * @param channel
	 * @param retest
	 *            复测，计量失败时重新校准通道并计量
	 * @throws Exception
	 * @
	 */
	public void calibrate(Channel channel, boolean retest) throws Exception {

		// 初始化校准计量点
		int relayBlockIndex = channel.getBindingCalBoardChannel().getBoardIndex();
		int cchIndex = channel.getBindingCalBoardChannel().getChnIndex();
		try {
			channel.setInfo("");// 信息重置
			channel.setLastTestDot(null);// 作为第一个点和KB值计算判断依据
			channel.initCalibrate();// 初始化校准点
			channel.initCalculate();// 初始化计量点
			channel.setCalculateIndex(0);

			core.getNetworkService().clearTestDot(channel, TestType.Measure);// 通知上位机清除计量数据
			core.getNetworkService().clearTestDot(channel, TestType.Cal);// 通知上位机清除校准数据
			if (!retest) {

				triggerLog(channel, I18N.getVal(I18N.StartCalibrate));

				channel.setStartDate(new Date());
			}
			channel.setEndDate(null);
			channel.setChnState(CalState.CALIBRATE);

			// xiao_wang 打开继电器板直到通道校准计量完毕或报错 0表示关闭通道

			if (CalibrateCore.getBaseCfg().useRelayBoard) {

				core.getRelayBoards().get(0).cfgCalRelaySwitch(relayBlockIndex, cchIndex + 1);
				Thread.sleep(1000);
				System.out.println("=========funny========" + relayBlockIndex + "====" + (cchIndex + 1));

			}

			triggerChnData(channel);
			int moduleIndex = 0;
			for (TestDot dot : channel.getCalDots()) {

				test(dot);
				moduleIndex = dot.moudleIndex;

			}

			writeFlash(channel, moduleIndex);

			try {
				if (core.getCalCfg().calibratePlanData.isNeedCalculateAfterCalibrate()) {

					for (TestDot dot : channel.getMeasureDots()) {

						try {
							test(dot);
						} catch (CalculateException ex) {

							if (CalibrateCore.getBaseCfg().kbAdjust.enable) {

								// 开始记录
								triggerDebugLog(dot.channel, "开始记录该失败计量点准备调整B值");

							} else {

								throw ex;
							}

						}

					}
					if (CalibrateCore.getBaseCfg().kbAdjust.enable) {

						for (int n = 0; n < CalibrateCore.getBaseCfg().kbAdjust.count; n++) {

							List<TestDot> failDots = fetchAllFailDot(channel);
							if (failDots.isEmpty()) {

								break;
							}
							// 调整B值，并写入调整后的程控B值；注意，只写入主模片系数
							adjustBValues(channel, failDots);

							for (TestDot dot : failDots) {

								try {
									test(dot);
								} catch (CalculateException ex) {

									if (n < CalibrateCore.getBaseCfg().kbAdjust.count - 1) {
										// 开始记录
										triggerDebugLog(dot.channel, "开始记录该失败计量点准备调整B值");
									} else {

										throw ex;
									}

								}

							}

						}

					}

				}
			} catch (Exception ex) {

				if (!retest && CalibrateCore.getBaseCfg().base.useRecal) {

					triggerErrorLog(channel, "计量失败,开始进行通道复测...");
					try {
						calibrate(channel, true);
					} catch (Exception e1) {

						e1.printStackTrace();
						// 不再抛出
					}

					return;

				} else {

					throw ex;
				}

			}

			channel.setChnState(CalState.CALIBRATE_PASS);

		} catch (Exception e) {

			channel.setInfo(e.getMessage() + "");
			channel.setChnState(CalState.CALIBRATE_FAIL);
			triggerErrorLog(channel, e.getMessage() + "");
			triggerCoreFailLog(channel, e);
			throw e;
		} finally {

			if (retest) {

				// 避免重复
				return;
			}

			channel.setReady(false);
			channel.setEndDate(new Date());

			triggerLog(channel, channel.getChnState().getDescribe());
			triggerChnData(channel);
			core.getDiskService().pushChannel(channel);

			// =================================================================================
			if (CalibrateCore.getBaseCfg().useRelayBoard) {

				core.getRelayBoards().get(0).cfgCalRelaySwitch(relayBlockIndex, 0);
				SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				System.out.println(dFormat.format(new Date()) + " ===================== close relay!");
			}

			if (channel.getLastTestDot() != null) {
				try {
					closeAll(channel.getLastTestDot());
				} catch (Exception e) {
					core.getLogger().error("closeAll error:" + e.getMessage(), e);
				} finally {
					channel.setLastTestDot(null);
					triggerDebugLog(channel, channel.getChnState().toString());
				}
			}

		}
	}

	/**
	 * 写入核心板日志
	 * 
	 * @param channel
	 * @param info
	 */
	private void triggerCoreLog(Channel channel, String info) {
		core.getLogger()
				.info(String.format("chn[%d-%d]-%s", channel.getDriverIndex() + 1, channel.getChnIndex() + 1, info));
	}

	/**
	 * 写入核心板error日志
	 * 
	 * @param channel
	 * @param e
	 */
	private void triggerCoreFailLog(Channel channel, Throwable e) {
		core.getLogger().error(
				String.format("chn[%d-%d]-%s", channel.getDriverIndex() + 1, channel.getChnIndex() + 1, e.getMessage()),
				e);
	}

	/**
	 * 通道计量
	 * 
	 * @param channel
	 * @throws Exception
	 * @
	 */
	public void calculate(Channel channel) throws Exception {
		int relayBlockIndex = channel.getBindingCalBoardChannel().getBoardIndex();
		int cchIndex = channel.getBindingCalBoardChannel().getChnIndex();
		try {
			channel.setInfo("");// 信息重置
			channel.setLastTestDot(null);// 作为第一个点和KB值计算判断依据
			channel.initCalculate();// 初始化计量点
			channel.setCalculateIndex(0);
			core.getNetworkService().clearTestDot(channel, TestType.Measure);// 通知上位机清除计量数据

			triggerLog(channel, I18N.getVal(I18N.StartCalculate));
			triggerCoreLog(channel, "start calculate");
			channel.setStartDate(new Date());
			channel.setEndDate(null);
			channel.setChnState(CalState.CALCULATE);

			// xiao_wang 打开继电器板直到通道校准计量完毕或报错
			if (CalibrateCore.getBaseCfg().useRelayBoard) {

				core.getRelayBoards().get(0).cfgCalRelaySwitch(relayBlockIndex, cchIndex + 1);
			}
			triggerChnData(channel);
			for (TestDot dot : channel.getMeasureDots()) {

				try {
					test(dot);
				} catch (CalculateException ex) {

					if (CalibrateCore.getBaseCfg().kbAdjust.enable) {

						// 开始记录
						triggerDebugLog(dot.channel, "开始记录该失败计量点准备调整B值");

					} else {

						throw ex;
					}

				}

			}
			if (CalibrateCore.getBaseCfg().kbAdjust.enable) {

				for (int n = 0; n < CalibrateCore.getBaseCfg().kbAdjust.count; n++) {

					List<TestDot> failDots = fetchAllFailDot(channel);
					if (failDots.isEmpty()) {

						break;
					}
					// 调整B值，并写入调整后的程控B值；注意，只写入主模片系数
					adjustBValues(channel, failDots);

					for (TestDot dot : failDots) {

						try {
							test(dot);
						} catch (CalculateException ex) {

							if (n < CalibrateCore.getBaseCfg().kbAdjust.count - 1) {
								// 开始记录
								triggerDebugLog(dot.channel, "开始记录该失败计量点准备调整B值");
							} else {

								throw ex;
							}

						}

					}

				}

			}

			channel.setChnState(CalState.CALCULATE_PASS);
		} catch (Exception e) {
			channel.setInfo(e.getMessage() + "");
			channel.setChnState(CalState.CALCULATE_FAIL);
			triggerErrorLog(channel, e.getMessage() + "");
			triggerCoreFailLog(channel, e);
			throw e;
		} finally {

			channel.setReady(false);
			// System.out.println("channel.getLastTestDot()="+channel.getLastTestDot());
			channel.setEndDate(new Date());

			triggerLog(channel, channel.getChnState().getDescribe());
			triggerChnData(channel);
			core.getDiskService().pushChannel(channel);

			if (CalibrateCore.getBaseCfg().useRelayBoard) {

				core.getRelayBoards().get(0).cfgCalRelaySwitch(relayBlockIndex, 0);
			}

			if (channel.getLastTestDot() != null) {
				try {
					closeAll(channel.getLastTestDot());
				} catch (Exception e) {
					core.getLogger().error("closeAll error:" + e.getMessage(), e);
				} finally {
					channel.setLastTestDot(null);
					triggerDebugLog(channel, channel.getChnState().toString());
				}
			}

		}
	}

	/**
	 * 调整错误计量点的B值,暂时定为主模片
	 * 
	 * @author wavy_zheng 2022年4月29日
	 * @param failDots
	 * @throws Exception
	 */
	public void adjustBValues(Channel channel, List<TestDot> failDots) throws Exception {

		List<TestDot> dots = channel.getCalDots().stream().filter(x -> x.moudleIndex == 0).collect(Collectors.toList());
		Map<TestDot, TestDot> map = new HashMap<>();
		for (int n = 0; n < failDots.size(); n++) {

			TestDot fail = failDots.get(n);
			List<TestDot> fetchs = new ArrayList<>();
			for (TestDot dot : dots) {

				if (dot.mode == fail.mode && dot.precision == fail.precision) {

					fetchs.add(dot);
				}

			}

			if (!fetchs.isEmpty()) {

				boolean fetchVal = false;
				for (int i = 0; i < fetchs.size(); i++) {

					TestDot secDot = fetchs.get(i);

					if (fail.meterVal <= secDot.meterVal) {

						TestDot adjustDot = map.get(secDot);
						fetchVal = true;
						// 同一段偏差更大的计量点获得B值调整机会
						if (adjustDot == null || Math.abs(fail.meterVal - fail.programVal) > Math
								.abs(adjustDot.meterVal - adjustDot.programVal)) {
							map.put(secDot, fail);

						}
						break;

					}

				}
				if (!fetchVal) {

					// 取第最后1个值
					map.put(fetchs.get(fetchs.size() - 1), fail);
				}

			}

		}

		// 开始调整B值
		for (Iterator<TestDot> it = map.keySet().iterator(); it.hasNext();) {

			TestDot key = it.next();
			TestDot val = map.get(key);
			double offset = val.meterVal - val.programVal;
			triggerDebugLog(channel,
					"计量点:" + val.programVal + ",模式:" + val.mode + ",实际值:" + val.meterVal + ",偏差:" + offset);
			double B = key.programB - key.programK * offset;
			triggerDebugLog(channel,
					"开始调整" + key.meterVal + "段B值:" + key.programB + " - " + key.programK + " * " + offset + " = " + B);
			triggerDebugLog(channel, key.meterVal + "段B值:" + key.programB + " -> " + B);
			key.programB = B;

		}

		if (!map.isEmpty()) {

			triggerDebugLog(channel, "开始重新写入调整后的KB校准系数,请稍后...");
			writeFlash(channel, 0);

		}

	}

	/**
	 * 获取计量失败的点
	 * 
	 * @author wavy_zheng 2022年4月29日
	 * @param channel
	 * @return
	 */
	public List<TestDot> fetchAllFailDot(Channel channel) {

		List<TestDot> list = new ArrayList<>();
		for (TestDot dot : channel.getMeasureDots()) {

			if (dot.testResult == TestResult.Fail) {

				list.add(dot);
			}

		}

		return list;

	}

	public void closeAll(TestDot dot) throws Exception {

		if (CalibrateCore.getBaseCfg().base.stopMode == 0) {
			triggerCoreLog(dot.channel, "close mode");
			switchDiap(dot, false);// 关闭膜片
			triggerCoreLog(dot.channel, "cal sleep");
			if (dot.testType == TestType.Cal) {
				setCalCalibrate(dot, false);// 校准板sleep
			} else {
				setCalMeasure(dot, false);
			}
			triggerCoreLog(dot.channel, "logic sleep");
			setLogicProgram(dot, false);// 逻辑板sleep

		}

	}

	private void writeFlash(Channel channel, int moduleIndex) throws Exception {

		// 驱动板flash
		Map<DriverEnvironment.CalMode, List<CalParamData>> dotMap = new HashMap<>();

		List<CalParamData> checkList = new ArrayList<>();
		List<CalParamData> check2List = new ArrayList<>();
		for (TestDot calDot : channel.getCalDots()) {
			if (calDot.adcK != 0 && calDot.moudleIndex == moduleIndex) {
				CalParamData cd = new CalParamData();
				cd.calMode = calDot.mode;
				cd.pole = calDot.pole;
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

				if (calDot.mode == DriverEnvironment.CalMode.CV) {

					if (calDot.checkAdc != 0) {

						CalParamData back1 = new CalParamData();
						back1.calMode = calDot.mode;
						back1.pole = calDot.pole;
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

					if (calDot.checkAdc2 != 0) {

						CalParamData back2 = new CalParamData();
						back2.calMode = calDot.mode;
						back2.pole = calDot.pole;
						back2.meter = calDot.meterVal;
						back2.adc = calDot.checkAdc2;
						back2.adcK = calDot.checkAdcK2;
						back2.adcB = calDot.checkAdcB2;
						back2.da = (int) calDot.programVal;
						back2.programK = calDot.programK;
						back2.programB = calDot.programB;
						back2.range = calDot.precision;
						check2List.add(back2);

					}

				}
			}
		}

		triggerLog(channel, I18N.getVal(I18N.WriteLogicFlash));
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

		triggerLog(channel, "共写入cc" + ccParams.size() + ",cv " + (cvTotalCount - checkList.size() - check2List.size())
				+ ",dc" + dcParams.size() + ",cv1 " + checkList.size() + ",cv2 " + check2List.size());
		MbFlashParamData flash = new MbFlashParamData();
		flash.setDriverIndex(channel.getDriverIndex());
		flash.setChnIndex(channel.getChnIndex());
		flash.setModuleIndex(moduleIndex); // 选择模片写入
		flash.setKb_dotMap(dotMap);

		if (!ccParams.isEmpty()) {
			System.out.println("first cc adc :" + ccParams.get(0).adc);
		}

		flash.setCv1DotCount(checkList.size());
		flash.setCv2DotCount(check2List.size());
		channel.getDeviceCore().cfgFlash(flash);
	}

	private void checkWork(TestDot dot, boolean needCheck) {
		if (needCheck) {
			if (!dot.channel.isReady()) {
				throw new RuntimeException(I18N.getVal(I18N.UserStoped));
			}

			// if (CalibrateCore.getBaseCfg().base.calibrateTerminal ==
			// CalibrateTerminal.PC) {
			// if (!core.isPcConnected()) {
			// throw new RuntimeException(I18N.getVal(I18N.PCNotConnected));
			// }
			// }

			// if
			// (!core.getCalBoardMap().get(dot.channel.getBindingCalBoardChannel().getBoardIndex()).isWork())
			// {
			// throw new RuntimeException(I18N.getVal(I18N.UserStoped));
			// }
		}
	}

	private void sleep(TestDot dot, int delay) {
		if (delay == 0) {
			return;
		}
		triggerDebugLog(dot.channel, I18N.getVal(I18N.Delay, delay));
		triggerCoreLog(dot.channel, "sleep " + delay + "ms");
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void test(TestDot dot) throws Exception {

		test(dot, CalibrateCore.getBaseCfg().base.useRecal, false);
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
	 * @
	 */
	public void test(TestDot dot, boolean retest, boolean needOpen) throws Exception {

		checkWork(dot, true);
		// 开始测试
		triggerDebugLog(dot.channel, I18N.getVal(I18N.StartTestDot, dot.getDescription()));
		// 通道第一个点
		boolean isFirstDot = dot.channel.getLastTestDot() == null;
		// 模式切换
		boolean isModeChange = !dot.sameMode(dot.channel.getLastTestDot());
		// 获取延时
		DetailConfig detailConfig = core.getCalCfg().delayConfig.findDelay(dot);

		if (dot.testType == TestType.Measure && CalibrateCore.getBaseCfg().base.measureNeedClose) {

			isModeChange = true; // 动力电池每次计量都需要重新打开膜片
		}

		boolean uploadData = true;// 是否上报数据点

		try {
			// 模式切换且不是第一个点，需要关闭膜片，DC高精也需要关闭
			if (!isFirstDot && (isModeChange || detailConfig.dotClose)) {

				// 关闭膜片
				switchDiap(dot, false);
				sleep(dot, detailConfig.moduleCloseDelay);

				if (dot.moudleIndex != dot.channel.getLastTestDot().moudleIndex) {

					triggerDebugLog(dot.channel,
							"change module " + dot.channel.getLastTestDot().moudleIndex + " -> " + dot.moudleIndex);
					writeFlash(dot.channel, dot.channel.getLastTestDot().moudleIndex);
					// 清除校准参数
					// dot.channel.getCalDots().clear();

					CommonUtil.sleep(1000);
				}

				if (dot.moudleIndex != 0 && dot.testType == TestType.Cal) {

					if (dot.mode == DriverEnvironment.CalMode.CC || dot.mode == DriverEnvironment.CalMode.DC) {

						triggerLog(dot.channel,
								"设置主模片:" + I18N.getVal(I18N.CfgLogicBoardCalibrate, dot.mode, dot.pole));

						long da = 10000;
						int range = dot.precision;
						if (dot.combine) {

							range = getRangeForMeterVal(dot.mainMeter);
							triggerDebugLog(dot.channel, "找到" + dot.mainMeter + "的精度档位:" + range);

							da = getDAFromMeter(dot.channel, 0, dot.mode, range, dot.mainMeter);
							// triggerDebugLog(dot.channel, "组合模式校准，主膜片表值:" + dot.mainMeter + ",通过" +
							// dot.programK + " * "
							// + dot.mainMeter + " + " + dot.programB + " = " + da);
							triggerDebugLog(dot.channel, "计算得到DA:" + da);
						}

						triggerDebugLog(dot.channel, "主模片:"
								+ I18N.getVal(I18N.CfgLogicBoardCalibrateDebug, dot.mode, dot.pole, range, 60000, da));

						MbCalibrateChnData calibrate = new MbCalibrateChnData();
						calibrate.setDriverIndex(dot.channel.getDriverIndex());
						calibrate.setChnIndex(dot.channel.getChnIndex());
						calibrate.setModuleIndex(0);
						calibrate.setMode(dot.mode);
						calibrate.setPole(dot.pole);
						calibrate.setRange(range);
						calibrate.setVoltageDA((int) 60000);
						calibrate.setCurrentDA((int) da);

						List<DriverCalibrateData.AdcData> adcs = new ArrayList<>();
						for (int i = 0; i < core.getCalCfg().steadyCfgData.getSampleCount(); i++) {
							adcs.add(new DriverCalibrateData.AdcData());
						}
						calibrate.setAdcDatas(adcs);

						dot.channel.getDeviceCore().cfgCalibrate(calibrate);

						CommonUtil.sleep(200);

					}

				}
			}

			if (!CalibrateCore.getBaseCfg().downFirstDotOnly || isModeChange || isFirstDot) {

				// 下发校准板
				if (dot.testType == TestType.Cal) {
					setCalCalibrate(dot, true);
				} else {
					setCalMeasure(dot, true);
				}

			}

			if (dot.testType == TestType.Cal) {
				// 下发逻辑板校准
				setLogicProgram(dot, true);
			} else {
				// 下发逻辑板计量
				setLogicMeasure(dot);
				System.out.println("=========="+dot.getDescription());
			}

			if (isFirstDot || isModeChange || detailConfig.dotClose || needOpen) {
				// 打开膜片
				switchDiap(dot, true);

				if (isFirstDot || isModeChange) {
					sleep(dot, detailConfig.modeChangeDelay);

				} else if (detailConfig.dotClose) {
					sleep(dot, detailConfig.moduleOpenDelay);
				}

			} else {
				// 程控设置等待
				if (dot.mode == DriverEnvironment.CalMode.CV && !CalibrateCore.getBaseCfg().base.ignoreCV2) {

					sleep(dot, detailConfig.programSetDelayCV2);
				} else {
					sleep(dot, detailConfig.programSetDelay);
				}

			}

			if (dot.testType == TestType.Cal) {
				// 读取校准adc
				getLogicAdc(dot);
			} else {
				// 读取计量adc
				getLogicFinalAdc(dot);
			}

			if (core.getCalCfg().calibratePlanData.isNeedValidate() && dot.testType == TestType.Cal) {
				// 比较校准adc偏差
				if (dot.adc < dot.minAdc || dot.adc > dot.maxAdc) {

					if (retest) {

						// 关闭膜片
						switchDiap(dot, false);
						setCalCalibrate(dot, false);

						test(dot, false, true);
						uploadData = false; // 不重复上报
						return;
					} else {

						throw new Exception(I18N.getVal(I18N.AdcOffsetOver, dot.adc, dot.minAdc, dot.maxAdc));
					}
				}
			}

			logger.info("read meter sleep :" + detailConfig.readMeterDelay + "ms");
			// 读表延时
			sleep(dot, detailConfig.readMeterDelay);

			// 读取万用表
			getMeter(dot, detailConfig);

			// 比较校准万用表偏差
			if (core.getCalCfg().calibratePlanData.isNeedValidate() && dot.testType == TestType.Cal) {
				if (dot.meterVal < dot.minMeter || dot.meterVal > dot.maxMeter) {

					if (retest) {

						// 关闭膜片
						switchDiap(dot, false);
						setCalCalibrate(dot, false);

						test(dot, false, true);
						uploadData = false; // 不重复上报
						return;
					} else {

						throw new Exception(
								I18N.getVal(I18N.ActualValOffsetOver, dot.meterVal, dot.minMeter, dot.maxMeter));

					}
				}
			}

			if (dot.testType == TestType.Cal) {
				// 计算KB值
				if (dot.channel.getLastTestDot() != null) {
					// 上一个点
					TestDot lastDot = dot.channel.getLastTestDot();
					// 与上一个点模式相同
					if (dot.sameMode(lastDot)) {
						// 尝试除以0
						if (dot.meterVal == lastDot.meterVal) {
							throw new Exception(I18N.getVal(I18N.MeterDiv0));
						}

						dot.programK = (dot.programVal - lastDot.programVal) / (dot.meterVal - lastDot.meterVal);
						dot.programB = (dot.meterVal * lastDot.programVal - lastDot.meterVal * dot.programVal)
								/ (dot.meterVal - lastDot.meterVal);

						// 尝试除以0
						if (dot.adc == lastDot.adc) {

							if (retest) {

								// 关闭膜片
								switchDiap(dot, false);
								setCalCalibrate(dot, false);

								test(dot, false, true);
								uploadData = false; // 不重复上报
								return;
							} else {

								throw new Exception(I18N.getVal(I18N.AdcDiv0));

							}
						}

						dot.adcK = (dot.meterVal - lastDot.meterVal) / (dot.adc - lastDot.adc);
						dot.adcB = (dot.adc * lastDot.meterVal - lastDot.adc * dot.meterVal) / (dot.adc - lastDot.adc);

						// 比较逻辑板KB
						if (core.getCalCfg().calibratePlanData.isNeedValidate()) {

							try {
								if (dot.programK < dot.minProgramK || dot.programK > dot.maxProgramK) {

									throw new Exception(I18N.getVal(I18N.ProgramKOffsetOver, dot.programK));
								}
								if (dot.programB < dot.minProgramB || dot.programB > dot.maxProgramB) {
									throw new Exception(I18N.getVal(I18N.ProgramBOffsetOver, dot.programB));
								}

								if (dot.adcK < dot.minAdcK || dot.adcK > dot.maxAdcK) {
									throw new Exception(I18N.getVal(I18N.AdcKOffsetOver, dot.adcK));
								}
								if (dot.adcB < dot.minAdcB || dot.adcB > dot.maxAdcB) {
									throw new Exception(I18N.getVal(I18N.AdcBOffsetOver, dot.adcB));
								}

							} catch (Exception ex) {

								logger.info("catch exception :" + ex.getMessage());
								if (retest) {

									// 关闭膜片
									switchDiap(dot, false);
									setCalCalibrate(dot, false);

									test(dot, false, true);
									uploadData = false; // 不重复上报
									return;
								} else {

									throw ex;
								}

							}
						}
						if (dot.mode == DriverEnvironment.CalMode.CV && CalibrateCore.getBaseCfg().base.calCheckBoard) {

							// 回检板
							// 尝试除以0
							if (dot.checkAdc == lastDot.checkAdc) {

								System.out.println(
										"dot.checkAdc = " + dot.checkAdc + ",lastDot.checkAdc = " + lastDot.checkAdc);
								throw new Exception(I18N.getVal(I18N.CheckAdcDiv0));

							}
							dot.checkAdcK = (dot.meterVal - lastDot.meterVal) / (dot.checkAdc - lastDot.checkAdc);
							dot.checkAdcB = (dot.checkAdc * lastDot.meterVal - lastDot.checkAdc * dot.meterVal)
									/ (dot.checkAdc - lastDot.checkAdc);
							// 比较
							if (core.getCalCfg().calibratePlanData.isNeedValidate()) {
								if (dot.checkAdcK < dot.minCheckAdcK || dot.checkAdcK > dot.maxCheckAdcK) {
									throw new Exception(I18N.getVal(I18N.CheckBoardAdcKOffsetOver, dot.checkAdcK));

								}
								if (dot.checkAdcB < dot.minCheckAdcB || dot.checkAdcB > dot.maxCheckAdcB) {
									throw new Exception(I18N.getVal(I18N.CheckBoardAdcBOffsetOver, dot.checkAdcB));

								}
							}

							// 校准回检ADC2
							if (!CalibrateCore.getBaseCfg().base.ignoreCV2) {
								// 校准异常两校准点回检ADC一致
								if (CalibrateCore.getBaseCfg().base.checkKBUseBackKB
										&& dot.checkAdc2 == lastDot.checkAdc2) {
									dot.checkAdcK2 = dot.checkAdcK;
									dot.checkAdcB2 = dot.checkAdcB;

									System.out.println("check adc ======== modify");
								} else {
									// 尝试除以0
									if (dot.checkAdc2 == lastDot.checkAdc2) {
										throw new Exception(I18N.getVal(I18N.Cv2AdcDiv0));

									}
									dot.checkAdcK2 = (dot.meterVal - lastDot.meterVal)
											/ (dot.checkAdc2 - lastDot.checkAdc2);
									dot.checkAdcB2 = (dot.checkAdc2 * lastDot.meterVal
											- lastDot.checkAdc2 * dot.meterVal) / (dot.checkAdc2 - lastDot.checkAdc2);

									// 比较
									if (core.getCalCfg().calibratePlanData.isNeedValidate()) {
										if (dot.checkAdcK2 < dot.minCheckAdcK || dot.checkAdcK2 > dot.maxCheckAdcK) {
											throw new Exception(
													I18N.getVal(I18N.CheckBoardAdcKOffsetOver, dot.checkAdcK2));

										}
										if (dot.checkAdcB2 < dot.minCheckAdcB || dot.checkAdcB2 > dot.maxCheckAdcB) {
											throw new Exception(
													I18N.getVal(I18N.CheckBoardAdcBOffsetOver, dot.checkAdcB2));

										}
									}

								}

							}

						}
					}

				}
			} else {

				double adcOffSet = 0;
				double meterOffSet = 0;
				switch (dot.mode) {
				case CC:
				case DC:

					if (CalibrateCore.getBaseCfg().calculateValidates.isEmpty()) {

						// 电流偏差从档位取
						meterOffSet = core.getCalCfg().rangeCurrentPrecisionData.getRanges().stream()
								.filter(x -> x.level == dot.precision).findAny().get().maxMeterOffset;
						adcOffSet = core.getCalCfg().rangeCurrentPrecisionData.getRanges().stream()
								.filter(x -> x.level == dot.precision).findAny().get().maxAdcOffset;

					} else {

						CalculateValidate validate = CalibrateCore.getBaseCfg().calculateValidates.stream()
								.filter(x -> x.min <= dot.programVal && x.max > dot.programVal).findAny().get();

						meterOffSet = validate.meterOffset;
						adcOffSet = validate.adcOffset;
					}
					break;
				case CV:
					// 电压偏差从计量计划取
					meterOffSet = core.getCalCfg().calculatePlanData.getMaxMeterOffset();
					adcOffSet = core.getCalCfg().calculatePlanData.getMaxAdcOffset();
					break;
				}

				if (Math.abs(dot.meterVal - dot.programVal) > meterOffSet) {
					throw new CalculateException(
							I18N.getVal(I18N.MeasureActualValOffsetOver, dot.meterVal - dot.programVal));
				}

				// 处理adc偏差
				adjustAdcOffset(true, dot, false);
				if (Math.abs(dot.adc - dot.programVal) > adcOffSet) {
					throw new Exception(I18N.getVal(I18N.MeasureAdcOffsetOver, dot.adc - dot.programVal));
				}

				if (dot.mode == DriverEnvironment.CalMode.CV) {
					if (CalibrateCore.getBaseCfg().base.calCheckBoard) {// 回检板启用

						// 处理adc偏差
						adjustAdcOffset(false, dot, false);
						if (Math.abs(dot.checkAdc - dot.programVal) > core.getCalCfg().calculatePlanData
								.getMaxAdcOffsetCheck()) {

							throw new Exception(
									I18N.getVal(I18N.CheckBoardAdcOffsetOver, dot.checkAdc - dot.programVal));
						}

					}
					// CV2
					if (!CalibrateCore.getBaseCfg().base.ignoreCV2) {

						if (CalibrateCore.getBaseCfg().base.checkKBUseBackKB) {
							dot.checkAdc2 = dot.checkAdc;
							// System.out.println("========do nothing========");
						} else {
							// 处理adc偏差
							adjustAdcOffset(true, dot, true);
							if (Math.abs(dot.checkAdc2 - dot.programVal) > core.getCalCfg().calculatePlanData
									.getMaxAdcOffsetCV2()) {

								throw new Exception(
										I18N.getVal(I18N.MeasureAdcOffsetOver, dot.checkAdc2 - dot.programVal));
							}

						}

					}
				}
			}
			dot.testResult = TestResult.Success;

		} catch (Exception e) {
			dot.testResult = TestResult.Fail;
			dot.info = e.getMessage() + "";
			System.out.println(e.getMessage());
			e.printStackTrace();
			
			throw e;

		} finally {

			dot.channel.setLastTestDot(dot);
			if (uploadData) {
				dot.time = new Date();
				triggerCoreLog(dot.channel, "final " + dot.getDescription());
				core.getNetworkService().appendTestDot(dot);
				core.getScreen().showTestDot(dot);
				// dot.channel.setLastTestDot(dot);
				// triggerChnData(dot.channel);
				// dot.channel.setCurrentTestDot(null);
				// core.getScreen().showTestDot(CV2dot);
				// triggerChnData(CV2dot.channel);

				triggerCoreLog(dot.channel, "============================================================");
			}

		}

	}

	/**
	 * 读取计量点稳定度数据
	 * 
	 * @author wavy_zheng 2022年2月6日
	 * @param dot
	 * @throws Exception
	 */
	private void getLogicFinalAdc(TestDot dot) throws Exception {
		checkWork(dot, true);

		triggerCoreLog(dot.channel, "get logic final adc");
		triggerLog(dot.channel, I18N.getVal(I18N.QryLogicBoardFinalAdc));
		int retryCount = dot.mode == DriverEnvironment.CalMode.CV ? core.getCalCfg().steadyCfgData.getAdcReadCountCV()
				: core.getCalCfg().steadyCfgData.getAdcReadCount();

		for (int i = 0; i < retryCount; i++) {
			checkWork(dot, true);
			MbMeasureChnData data = dot.channel.getDeviceCore().qryCalculate(dot.channel.getDriverIndex(),
					dot.channel.getChnIndex());

			List<Double> adcs = new ArrayList<>();

			/**
			 * 把所有模片的真实值累加即最终真实值
			 */
			List<Double> adc1s = new ArrayList<>();
			List<Double> adc2s = new ArrayList<>();

			for (ReadonlyAdcData rad : data.getAdcDatas()) {

				double val = 0;
				for (int n = 0; n < Data.getModuleCount(); n++) {

					val += rad.adcList.get(n).finalAdc;

				}
				adcs.add(val);
				adc1s.add(rad.finalBackAdc1);
				adc2s.add(rad.finalBackAdc2);

			}

			try {
				dot.adc = calculateStable(dot, adcs, core.getCalCfg().steadyCfgData.getMaxSigma());
				if (CalibrateCore.getBaseCfg().base.calCheckBoard && dot.mode == DriverEnvironment.CalMode.CV) {
					dot.checkAdc = calculateStable(dot, adc1s, core.getCalCfg().steadyCfgData.getMaxSigmabackup1());
					if (!CalibrateCore.getBaseCfg().base.ignoreCV2) {
						if (CalibrateCore.getBaseCfg().base.checkKBUseBackKB) {
							dot.checkAdc2 = calculateStable(dot, adc1s,
									core.getCalCfg().steadyCfgData.getMaxSigmabackup2());
						} else {

							dot.checkAdc2 = calculateStable(dot, adc2s,
									core.getCalCfg().steadyCfgData.getMaxSigmabackup2());
						}

					}
				}

				dot.programK = data.getProgramKReadonly();
				dot.programB = data.getProgramBReadonly();
				dot.adcK = data.getAdcKReadonly();
				dot.adcB = data.getAdcBReadonly();
				break;
			} catch (Exception e) {
				if (i == retryCount - 1) {

					if (dot.adc > 0 && dot.checkAdc > 0) {

						throw new Exception(I18N.getVal(I18N.CheckBoard) + dot.mode.name() + e.getMessage());
					}

					throw new Exception(I18N.getVal(I18N.LogicBoard) + dot.mode.name() + e.getMessage());
				} else {
					sleep(dot,
							dot.mode == DriverEnvironment.CalMode.CV
									? core.getCalCfg().steadyCfgData.getAdcRetryDelayCV()
									: core.getCalCfg().steadyCfgData.getAdcRetryDelay());
				}
			}
		}

	}

	/**
	 * 配置校准板校准点
	 * 
	 * @author wavy_zheng 2022年2月6日
	 * @param dot
	 * @param open
	 */
	private void setCalMeasure(TestDot dot, boolean open) {

		checkWork(dot, open);

		CalMode tempMode = open ? Logic2Environment.CalMode.values()[dot.mode.ordinal()] : CalMode.SLEEP;

		WorkMode mode = null;
		switch (tempMode) {
		case CC:
			mode = WorkMode.CC;
			break;
		case CV:
			mode = WorkMode.CV;
			break;
		case DC:
			mode = WorkMode.DC;
			break;
		case SLEEP:
			mode = WorkMode.SLEEP;
			break;
		}

		triggerCoreLog(dot.channel, "set cal measure" + ",open=" + open + ",mode=" + mode + ",pole=" + dot.pole
				+ ",precision=" + dot.precision + ",programVal=" + dot.programVal);

		triggerDebugLog(dot.channel, I18N.getVal(I18N.CfgCalBoardMeasure));

		CalBoardChannel cch = dot.channel.getBindingCalBoardChannel();

		int chnIndex = cch.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			chnIndex = core.getChnMapService().mapChnIndex(cch.getBoardIndex(), chnIndex);
		}

		core.getCalBoardMap().get(cch.getBoardIndex()).cfgCalculate2(chnIndex, open ? WorkState.WORK : WorkState.UNWORK,
				mode, CalEnvironment.Pole.values()[(dot.pole.ordinal())], dot.programVal, dot.precision);
	}

	private void setLogicMeasure(TestDot dot) throws Exception {
		checkWork(dot, true);

		triggerCoreLog(dot.channel,
				"set logic measure" + ",mode=" + dot.mode + ",pole=" + dot.pole + ",checkDot=" + dot.programVal);

		triggerLog(dot.channel, I18N.getVal(I18N.CfgLogicBoardMeasure, dot.mode, dot.pole, dot.programVal));

		MbMeasureChnData measure = new MbMeasureChnData();
		measure.setDriverIndex(dot.channel.getDriverIndex());
		measure.setChnIndex(dot.channel.getChnIndex());
		measure.setModuleIndex(-1); // 计量不选择模片序号
		measure.setMode(dot.mode);
		measure.setPole(dot.pole);
		measure.setCalculateDot(dot.programVal);

		double programVal = 0;
		switch (dot.mode) {
		case CC:
		case DC:
			programVal = core.getCalCfg().calibratePlanData.getMaxProgramV();
			break;
		case CV:
			programVal = core.getCalCfg().calibratePlanData.getMaxProgramI();
			break;
		default:
			break;
		}

		measure.setProgramDot(programVal);

		List<ReadonlyAdcData> groups = new ArrayList<>();
		for (int i = 0; i < core.getCalCfg().steadyCfgData.getSampleCount(); i++) {
			groups.add(new ReadonlyAdcData());
		}
		measure.setAdcDatas(groups);

		dot.channel.getDeviceCore().cfgCalculate(measure);

	}

	/**
	 * 配置校准板校准点
	 * 
	 * @author wavy_zheng 2022年2月6日
	 * @param dot
	 * @param open
	 */
	private void setCalCalibrate(TestDot dot, boolean open) {

		checkWork(dot, open);

		CalMode tempMode = open ? CalMode.values()[dot.mode.ordinal()] : CalMode.SLEEP;

		int programV = 0;
		int programI = 0;

		switch (tempMode) {
		case CC:
		case DC:
			programV = (int) core.getCalCfg().calibratePlanData.getMaxProgramV();
			programI = (int) dot.programVal;
			break;
		case CV:
		case CV2:
			programV = (int) dot.programVal;
			programI = (int) core.getCalCfg().calibratePlanData.getMaxProgramI();
			break;
		default:
			break;
		}

		CalBoardChannel cch = dot.channel.getBindingCalBoardChannel();

		WorkMode mode = null;
		switch (tempMode) {
		case CC:
			mode = WorkMode.CC;
			break;
		case CV:
			mode = WorkMode.CV;
			break;
		case DC:
			mode = WorkMode.DC;
			break;
		case SLEEP:
			mode = WorkMode.SLEEP;
			break;
		}

		triggerDebugLog(dot.channel, I18N.getVal(I18N.CfgCalBoardCalibrateDebug, cch.getBoardIndex(), cch.getChnIndex(),
				I18N.getVal(open ? I18N.On : I18N.Off), mode, dot.pole, dot.precision, programV, programI));

		triggerCoreLog(dot.channel,
				"set cal program boardIndex=" + cch.getBoardIndex() + ",chnIndex=" + cch.getChnIndex() + ",module="
						+ dot.moudleIndex + ",open=" + open + ",mode=" + mode + ",pole=" + dot.pole + ",precision="
						+ dot.precision + ",programV=" + programV + ",programI=" + programI);

		int chnIndex = cch.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			chnIndex = core.getChnMapService().mapChnIndex(cch.getBoardIndex(), chnIndex);
		}

		core.getCalBoardMap().get(cch.getBoardIndex()).cfgCalibrate2(chnIndex, open ? WorkState.WORK : WorkState.UNWORK,
				mode, dot.precision, PoleData.Pole.values()[dot.pole.ordinal()], programV, programI);
	}

	private void setLogicProgram(TestDot dot, boolean open) throws Exception {

		checkWork(dot, open);

		DriverEnvironment.CalMode mode = open ? dot.mode : DriverEnvironment.CalMode.SLEEP;

		long programV = 0;
		long programI = 0;

		switch (mode) {
		case CC:
		case DC:
			programV = core.getCalCfg().calibratePlanData.getMaxProgramV();
			programI = (long) dot.programVal;
			break;
		case CV:
			programV = (long) dot.programVal;
			programI = core.getCalCfg().calibratePlanData.getMaxProgramI();
			break;
		default:
			break;
		}

		MbCalibrateChnData calibrate = new MbCalibrateChnData();
		calibrate.setDriverIndex(dot.channel.getDriverIndex());
		calibrate.setChnIndex(dot.channel.getChnIndex());
		calibrate.setModuleIndex(dot.moudleIndex);
		calibrate.setMode(mode);
		calibrate.setPole(dot.pole);
		calibrate.setRange(dot.precision);
		calibrate.setVoltageDA((int) programV);
		calibrate.setCurrentDA((int) programI);

		List<DriverCalibrateData.AdcData> adcs = new ArrayList<>();
		for (int i = 0; i < core.getCalCfg().steadyCfgData.getSampleCount(); i++) {
			adcs.add(new DriverCalibrateData.AdcData());
		}
		calibrate.setAdcDatas(adcs);

		triggerCoreLog(dot.channel, "set logic program" + ",mode=" + mode + ",pole=" + dot.pole + ",precision="
				+ dot.precision + ",programV=" + programV + ",programI=" + programI);

		triggerLog(dot.channel, I18N.getVal(I18N.CfgLogicBoardCalibrate, mode, dot.pole));

		triggerDebugLog(dot.channel,
				I18N.getVal(I18N.CfgLogicBoardCalibrateDebug, mode, dot.pole, dot.precision, programV, programI));

		calibrate.setModuleIndex(dot.moudleIndex);
		calibrate.setVoltageDA((int) programV);

		dot.channel.getDeviceCore().cfgCalibrate(calibrate);
	}

	private void switchDiap(TestDot dot, boolean open) throws Exception {
		checkWork(dot, open);

		triggerCoreLog(dot.channel, (open ? "open" : "close") + " diap");
		triggerDebugLog(dot.channel, I18N.getVal(open ? I18N.On : I18N.Off) + I18N.getVal(I18N.Diaphragm));

		dot.channel.getDeviceCore().cfgModuleSwitch(dot.channel.getDriverIndex(), dot.channel.getChnIndex(), open);
	}

	private void getLogicAdc(TestDot dot) throws Exception {
		checkWork(dot, true);

		triggerCoreLog(dot.channel, "get logic adc");
		triggerLog(dot.channel, I18N.getVal(I18N.QryLogicBoardAdc));
		int retryCount = core.getCalCfg().steadyCfgData.getAdcReadCount();

		for (int i = 0; i < retryCount; i++) {
			checkWork(dot, true);
			MbCalibrateChnData data = dot.channel.getDeviceCore().qryCalibrate(dot.channel.getDriverIndex(),
					dot.channel.getChnIndex());

			List<Double> adcs = data.getAdcDatas().stream().map(x -> x.mainAdc).collect(Collectors.toList());
			try {
				System.out.println("mainAdcs:" + adcs);
				dot.adc = calculateStable(dot, adcs, core.getCalCfg().steadyCfgData.getMaxSigma());
				System.out.println("avg dot.adc = " + dot.adc);
				if (dot.mode == DriverEnvironment.CalMode.CV) {

					// 需要查询back1Adc稳定度
					adcs = data.getAdcDatas().stream().map(x -> x.backAdc1).collect(Collectors.toList());
					System.out.println("checkAdcs:" + adcs);
					try {
						dot.checkAdc = calculateStable(dot, adcs, core.getCalCfg().steadyCfgData.getMaxSigmabackup1());
						System.out.println("dot.checkAdc = " + dot.checkAdc);

					} catch (Exception e) {

						throw new Exception("回检ADC1 " + dot.mode.name() + e.getMessage());

					}
					if (!CalibrateCore.getBaseCfg().base.ignoreCV2) {

						System.out.println("start cal check adc2");
						if (CalibrateCore.getBaseCfg().base.checkKBUseBackKB) {
							adcs = data.getAdcDatas().stream().map(x -> x.backAdc1).collect(Collectors.toList());
						} else {

							adcs = data.getAdcDatas().stream().map(x -> x.backAdc2).collect(Collectors.toList());
						}
						System.out.println("check2Adcs:" + adcs);

						try {
							dot.checkAdc2 = calculateStable(dot, adcs,
									core.getCalCfg().steadyCfgData.getMaxSigmabackup2());
							System.out.println("dot.checkAdc2 = " + dot.checkAdc2);

						} catch (Exception e) {

							throw new Exception("回检2ADC " + dot.mode.name() + e.getMessage());

						}

					}

				}
				break;

			} catch (Exception e) {
				if (i == retryCount - 1) {
					throw new Exception("adc " + dot.mode.name() + e.getMessage());
				} else {
					triggerCoreFailLog(dot.channel, e);
					sleep(dot, core.getCalCfg().steadyCfgData.getAdcRetryDelay());
				}
			}

		}

	}

	private double calculateStable(TestDot dot, List<Double> adcs, double maxSigma) throws Exception {

		if (adcs.size() - core.getCalCfg().steadyCfgData.getTrailCount() < 2) {
			throw new Exception(I18N.getVal(I18N.AdcCountNotEnough, adcs.size()));
		}

		adcs.sort(null);

		int cut = core.getCalCfg().steadyCfgData.getTrailCount() / 2;
		List<Double> tempAdcs = adcs.subList(cut, adcs.size() - cut);

		// 总和
		double sum = 0;
		for (double val : tempAdcs) {
			sum += val;
		}
		// 平均数
		double avg = sum / tempAdcs.size();

		double sum2 = 0;
		for (double val : tempAdcs) {
			sum2 += Math.pow(val - avg, 2);
		}
		// 样本方差
		double sigma2 = sum2 / (tempAdcs.size() - 1);
		// 样本标准差
		double sigma = sigma2 >= 0 ? Math.sqrt(sigma2) : 0;

		triggerCoreLog(dot.channel, "avg=" + avg + ", sigma=" + sigma + ", sigma2=" + sigma2 + ",adcs=" + adcs);
		triggerDebugLog(dot.channel, "avg=" + avg + ", sigma=" + sigma + ", sigma2=" + sigma2 + ",adcs=" + adcs);

		if (avg <= 0) {
			throw new Exception(I18N.getVal(I18N.AdcIsZero));
		}

		if (sigma < maxSigma) {
			return avg;
		}

		throw new Exception(I18N.getVal(I18N.AdcNotStable));
	}

	private void getMeter(TestDot dot, DetailConfig detailConfig) throws Exception {
		checkWork(dot, true);

		// 切表
		int calIndex = dot.channel.getBindingCalBoardChannel().getBoardIndex();
		Meter meter = core.getCalBoardMap().get(calIndex).getMeter();
		logger.info(detailConfig);
		synchronized (meter) {

			// ============== // 读表延时
			if (!CalibrateCore.getBaseCfg().readMeasureMeter.enable) {
				
				if (core.getMeterParamMap().get(meter).lastCalIndex != calIndex) {
					// 表和当前板没有连接
					if (core.getMeterParamMap().get(meter).lastCalIndex != -1) {
						// 表当前有别的板连接，断开上一个板

						triggerCoreLog(dot.channel, String.format("turn off calboard %d  meter switch ",
								core.getMeterParamMap().get(meter).lastCalIndex + 1));
						triggerDebugLog(dot.channel, String.format("turn off calboard %d  meter switch ",
								core.getMeterParamMap().get(meter).lastCalIndex + 1));
						
						Thread.sleep(1000);
						core.getCalBoardMap().get(core.getMeterParamMap().get(meter).lastCalIndex).cfgRelayControl(false);

						logger.info(
								"cutoff old calboard index for meter :" + core.getMeterParamMap().get(meter).lastCalIndex
										+ ",delay = " + detailConfig.turnOffMeterDelay + "ms");

						sleep(dot, detailConfig.turnOffMeterDelay);
						// 清空表连接
						core.getMeterParamMap().get(meter).lastCalIndex = -1;
					}

					logger.info("connect calboard index for meter:" + calIndex);
					// 连接当前板
					triggerCoreLog(dot.channel, String.format("turn on calboard %d  meter switch ", calIndex + 1));
					triggerDebugLog(dot.channel, String.format("turn on calboard %d  meter switch ", calIndex + 1));

					core.getCalBoardMap().get(calIndex).cfgRelayControl(true);
					// 表连接标志
					core.getMeterParamMap().get(meter).lastCalIndex = calIndex;

					sleep(dot, detailConfig.turnOnMeterDelay);
				}
				sleep(dot, detailConfig.readMeterDelay);

				double readVal = meter.ReadSingleClearBuffer();

				if (dot.mode == DriverEnvironment.CalMode.CC || dot.mode == DriverEnvironment.CalMode.DC) {

					// double resistivity = getResistivity(calIndex, dot.precision);
					double resistivity = getResistanceEx(calIndex, WorkPattern.values()[dot.mode.ordinal()],
							dot.precision);
					dot.meterVal = readVal * resistivity;

					triggerCoreLog(dot.channel,
							String.format("meterval : %f * %f = %f", readVal, resistivity, dot.meterVal));
					triggerDebugLog(dot.channel,
							String.format("meterval : %f * %f = %f", readVal, resistivity, dot.meterVal));

					if (dot.combine && dot.moudleIndex != 0) {

						triggerDebugLog(dot.channel,
								dot.meterVal + " - " + dot.mainMeter + " = " + (dot.meterVal - dot.mainMeter));
						dot.meterVal = dot.meterVal - dot.mainMeter;
					}

				} else {
					dot.meterVal = readVal;
					triggerCoreLog(dot.channel, String.format("meterval : %f ", dot.meterVal));
					triggerDebugLog(dot.channel, String.format("meterval : %f ", dot.meterVal));

				}

				// triggerLog(dot.channel, "meter=" + dot.meterVal);
				// triggerCoreLog(dot.channel, "meter=" + dot.meterVal);
			} else {
				readBigCurr(meter,calIndex,dot,detailConfig);
			}

	
		}
	}

	private void readBigCurr(Meter meter,int calIndex,TestDot dot, DetailConfig detailConfig) throws IOException, InterruptedException {
		
		if (dot.testType == TestType.Cal || dot.mode == DriverEnvironment.CalMode.CV) {
			
			// 读表延时
			sleep(dot, detailConfig.readMeterDelay);
			
			double readVal = meter.ReadSingleClearBuffer();

			if (dot.mode == DriverEnvironment.CalMode.CC || dot.mode == DriverEnvironment.CalMode.DC) {

				double resistivity = getResistanceEx2(calIndex, 0, WorkPattern.values()[dot.mode.ordinal()],
						dot.precision);
				
				
				dot.meterVal = readVal * resistivity;

				triggerCoreLog(dot.channel,
						String.format("meterval : %f * %f = %f", readVal, resistivity, dot.meterVal));
				triggerDebugLog(dot.channel,
						String.format("meterval : %f * %f = %f", readVal, resistivity, dot.meterVal));


			} else {
				dot.meterVal = readVal;
				triggerCoreLog(dot.channel, String.format("meterval : %f ", dot.meterVal));
				triggerDebugLog(dot.channel, String.format("meterval : %f ", dot.meterVal));

			}
		} else {
			
			/** 表值 合集 */
			List<Double> meterList = new ArrayList<>();
			// 获取读表次数
			int count = getReadMeterCount(CalibrateCore.getBaseCfg().readMeasureMeter.meterParts,
					dot.programVal);
			System.out.println("Read meter count: " + count);
			
			triggerDebugLog(dot.channel, "Read meter count: " + count);
			//count<2表示小电流读一次表
			if(count<2) {
				
				double resistivity = getResistanceEx2(calIndex, 0,
						WorkPattern.values()[dot.mode.ordinal()], dot.precision);
				
				sleep(dot, detailConfig.meterRelayDelay);
				
				double meterVal=meter.ReadSingleClearBuffer() ;
				
				
				sleep(dot, detailConfig.meterRelayDelay);
				System.out.println(String.format("===this is " + 0 + " meterval : %f ", meterVal));
				triggerDebugLog(dot.channel,
						"===this is " + 0 + " meterval : "+ meterVal + " * " + resistivity +" = "+meterVal*resistivity);
				
				meterList.add(meterVal* resistivity);
				
				
			}else {
				//关闭所有万用表继电器
//				for (int relayIndex = 0; relayIndex < count; relayIndex++) {
//					core.getCalBoardMap().get(calIndex).cfgRelayControl2(relayIndex, false);
//					triggerDebugLog(dot.channel, "close meter relay"+relayIndex+"delay");
//					sleep(dot, detailConfig.meterRelayDelay);
//				}
				
				for (int relayIndex = 0; relayIndex < count; relayIndex++) {
					//读取电阻系数
					double resistivity = getResistanceEx2(calIndex, relayIndex,
							WorkPattern.values()[dot.mode.ordinal()], dot.precision);
					
					
					//打开表继电器
					core.getCalBoardMap().get(calIndex).cfgRelayControl2(relayIndex, true);
					triggerDebugLog(dot.channel, "open meter relay delay");
					
					
					sleep(dot, detailConfig.meterRelayDelay);
					
					double meterVal=meter.ReadSingleClearBuffer() ;
					meterList.add(meterVal* resistivity);
					
					triggerDebugLog(dot.channel, "close meter relay delay");
					sleep(dot, detailConfig.meterRelayDelay);
					core.getCalBoardMap().get(calIndex).cfgRelayControl2(relayIndex, false);
					sleep(dot, detailConfig.meterRelayDelay);
					
					System.out.println(String.format("===this is " + relayIndex + " meterval : %f ", meterVal));
					triggerDebugLog(dot.channel,
							"===this is " + relayIndex + " meterval : "+ meterVal + " * " + resistivity +" = "+meterVal*resistivity);
				}
				
			}
			
			
			double meterVal = 0;
			for (Double meterValue : meterList) {
				meterVal += meterValue;
			}
			dot.meterVal = meterVal;
			triggerDebugLog(dot.channel, String.format("==final meterval : %f ", dot.meterVal));
			System.out.println("=============meterValue======="+dot.meterVal);
		}

	
	}

	private int getReadMeterCount(List<MeterPart> meterParts, double programVal) {
		List<MeterPart> sort = meterParts.stream().filter(x -> x.threashold <= programVal)
				.sorted(Comparator.comparing(MeterPart::getThreashold)).collect(Collectors.toList());
		if (sort.size() == 0) {
			return 0;
		}
		return sort.get(sort.size() - 1).useNum;
	}

	private double getResistivity(int calIndex, int precision) {

		return core.getCalBoardMap().get(calIndex).getResistivity(precision);
	}

	public double getResistanceEx2(int calIndex, int meterRelayIndex, WorkPattern workPattern, int range) {
		ResistanceModeRelayData rm = (ResistanceModeRelayData) core.getCalBoardMap().get(calIndex)
				.qryResistanceModeData(meterRelayIndex, workPattern, range);
		return rm.getResistance();
	}

	private double getResistanceEx(int calIndex, WorkPattern wp, int range) {

		ResistanceModeData rm = (ResistanceModeData) core.getCalBoardMap().get(calIndex).qryResistanceModeData(wp,
				range);
		return rm.getResistance();
	}

	private void checkDot(TestDot dot) {
		// TODO Auto-generated method stub
		switch (dot.testType) {
		case Cal:

			break;
		case Measure:
			break;
		default:
			break;
		}
	}

	/**
	 * 选中并校准/停止通道
	 * 
	 * @param data
	 * @
	 */
	// public void cfgCalibrate(CalibrateData data) {
	//
	// int
	// deviceDriverIndex=data.getUnitIndex()*CalibrateCore.getBaseCfg().base.logicDriverCount+
	// data.getDriverIndex();
	//
	// //for(int )
	//
	// Driverboard driverboard = core.findDriver(data.getUnitIndex(),
	// data.getDriverIndex());
	//
	// if (data.isCalibrate()) {// 校准
	//
	// if (driverboard.isWork()) {
	// throw new Exception(String.format("驱动板%d正在工作中", driverboard.getIndex() + 1));
	// }
	//
	// if (data.getChnFlag() == 0) {
	// return;
	// }
	//
	// for (int i = 0; i < driverboard.getChannels().size(); i++) {
	//
	// if ((data.getChnFlag() >> i & 0x01) > 0) {
	//
	// driverboard.getChannels().get(i).setChnState(CalState.READY);
	// }
	// }
	//
	// new Thread(() -> {
	//
	// driverboard.setWork(true);
	//
	// // 对接
	//
	// // 校准
	// driverboard.getChannels().forEach(x -> {
	// if (x.getChnState() == CalState.READY) {
	//
	// try {
	// x.setChnState(CalState.CALIBRATE);
	// calibrate(x);
	// x.setChnState(CalState.CALIBRATE_PASS);
	// } catch (Exception e) {
	// x.setChnState(CalState.CALCULATE_FAIL);
	// TODO1: handle exception
	// }
	// }
	// });
	// driverboard.setWork(false);
	//
	// }).start();
	//
	// } else {// 停止
	//
	// if (!driverboard.isWork()) {
	// return;
	// }
	//
	// if (data.getChnFlag() == 0) {
	// return;
	// }
	//
	// for (int i = 0; i <= driverboard.getChannels().size(); i++) {
	//
	// if ((data.getChnFlag() >> i & 0x01) > 0) {
	// if (driverboard.getChannels().get(i).getChnState() == CalState.READY
	// || driverboard.getChannels().get(i).getChnState() == CalState.CALIBRATE) {//
	// 如果正在校准或准备状态，清空
	// driverboard.getChannels().get(i).setChnState(CalState.NONE);
	// }
	// }
	// }
	//
	// // 没有通道在准备或校准
	//// if (driverboard.getChannels().stream()
	//// .filter(x -> x.getChnState() == CalState.READY || x.getChnState() ==
	// CalState.CALIBRATE).findAny()
	//// .equals(Optional.empty())) {
	//// driverboard.setWork(false);
	//// }
	// }
	// }

	/**
	 * 选中并计量/停止通道
	 * 
	 * @param data
	 * @
	 */
	// public void cfgCalculate(CalculateData data) {
	//
	// Driverboard driverboard = core.findDriver(data.getUnitIndex(),
	// data.getDriverIndex());
	//
	// if (data.isCalculate()) {// 计量
	//
	// if (driverboard.isWork()) {
	// throw new Exception(String.format("驱动板%d正在工作中", driverboard.getIndex() + 1));
	// }
	//
	// if (data.getChnFlag() == 0) {
	// return;
	// }
	//
	// for (int i = 0; i < driverboard.getChannels().size(); i++) {
	//
	// if ((data.getChnFlag() >> i & 0x01) > 0) {
	//
	// driverboard.getChannels().get(i).setChnState(CalState.READY);
	// }
	// }
	//
	// new Thread(() -> {
	//
	// driverboard.setWork(true);
	// // 对接
	//
	// // 校准
	// driverboard.getChannels().forEach(x -> {
	// if (x.getChnState() == CalState.READY) {
	//
	// try {
	// x.setChnState(CalState.CALCULATE);
	// calculate(x);
	// x.setChnState(CalState.CALCULATE_PASS);
	// } catch (Exception e) {
	// x.setChnState(CalState.CALCULATE_FAIL);
	// // TOD1O: handle exception
	// }
	// }
	// });
	// driverboard.setWork(false);
	//
	// }).start();
	//
	// } else {// 停止
	//
	// if (!driverboard.isWork()) {
	// return;
	// }
	//
	// if (data.getChnFlag() == 0) {
	// return;
	// }
	//
	// for (int i = 0; i <= driverboard.getChannels().size(); i++) {
	//
	// if ((data.getChnFlag() >> i & 0x01) > 0) {
	// if (driverboard.getChannels().get(i).getChnState() == CalState.READY
	// || driverboard.getChannels().get(i).getChnState() == CalState.CALCULATE) {//
	// 如果正在校准或准备状态，清空
	// driverboard.getChannels().get(i).setChnState(CalState.NONE);
	// }
	// }
	// }
	//
	// // 没有通道在准备或计量
	//// if (driverboard.getChannels().stream()
	//// .filter(x -> x.getChnState() == CalState.READY || x.getChnState() ==
	// CalState.CALCULATE).findAny()
	//// .equals(Optional.empty())) {
	//// driverboard.setWork(false);
	//// }
	// }
	// }

	// /**
	// * 切换识别模式
	// *
	// * @param data
	// * @
	// */
	// public void cfgModeSwitch(ModeSwitchData data) {
	//
	// switch (data.getMode()) {
	// case CAL:
	// if (matchWork) {
	// throw new Exception(I18N.getVal(I18N.MatchBusy));
	// }
	// core.getDeviceCore().cfgStartup(State.CAL);// 校准
	// break;
	// case MATCH:
	// if (matchWork) {
	// throw new Exception(I18N.getVal(I18N.MatchBusy));
	// }
	// core.getDeviceCore().cfgStartup(State.JOIN);// 识别
	// startMatch();// 开始识别
	// break;
	// case NONE:
	// stopMatch();// 停止识别
	// core.getDeviceCore().cfgStartup(State.NORMAL);
	// break;
	// }
	// calibrateCoreWorkMode = data.getMode();
	// }

	/**
	 * 单驱动板切换工作模式
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param driverIndex
	 * @param mode
	 * @throws Exception
	 */
	public void cfgDriverModeSwitch(DriverModeSwitchData data) throws Exception {

		MbDriverModeChangeData dmcd = new MbDriverModeChangeData();

		dmcd.setDriverIndex(data.getDriverIndex());

		switch (data.getMode()) {
		case CAL:

			dmcd.setMode(CalBoxDeviceEnvironment.WorkMode.CAL);

			break;
		case MATCH:

			dmcd.setMode(CalBoxDeviceEnvironment.WorkMode.JOINT);
			startMatch();// 开始识别
			break;
		case NONE:
			stopMatch();// 停止识别
			dmcd.setMode(CalBoxDeviceEnvironment.WorkMode.NORMAL);
			break;
		}
		core.getDeviceCore().cfgDriverModeChange(dmcd);

	}

	/**
	 * 切换工作模式
	 * 
	 * @param mode
	 * @throws Exception
	 * @
	 */
	public void cfgModeSwitch(CalibrateCoreWorkMode mode) throws Exception {

		MbModeChangeData modeChangeData = new MbModeChangeData();
		switch (mode) {
		case CAL:
			if (matchWork) {
				throw new Exception(I18N.getVal(I18N.MatchBusy));
			}

			modeChangeData.setMode(CalBoxDeviceEnvironment.WorkMode.CAL);

			break;
		case MATCH:
			if (matchWork) {
				throw new Exception(I18N.getVal(I18N.MatchBusy));
			}
			modeChangeData.setMode(CalBoxDeviceEnvironment.WorkMode.JOINT);
			startMatch();// 开始识别
			break;
		case NONE:
			stopMatch();// 停止识别
			modeChangeData.setMode(CalBoxDeviceEnvironment.WorkMode.NORMAL);
			break;
		}
		core.getDeviceCore().cfgModeChange(modeChangeData);// 校准
		calibrateCoreWorkMode = mode;
	}

	private CalibrateCoreWorkMode calibrateCoreWorkMode = CalibrateCoreWorkMode.NONE;

	public CalibrateCoreWorkMode getCalibrateCoreWorkMode() {
		return calibrateCoreWorkMode;
	}

	private boolean matchWork;
	private Thread matchThread;

	public void stopMatch() {
		matchWork = false;
	}

	/**
	 * 设置基准电压
	 * 
	 * @author wavy_zheng 2021年7月19日
	 * @param calboardIndex
	 * @param baseVolt
	 *            0表示关闭
	 */
	private void writeBaseVoltage(int calboardIndex, double baseVolt) {

		CalBoard calboard = core.getCalBoardMap().get(calboardIndex);
		calboard.cfgVoltBase(calboardIndex, baseVolt == 0 ? WorkState.UNWORK : WorkState.WORK, baseVolt);

	}

	/**
	 * 对接单通道
	 * 
	 * @author wavy_zheng 2021年7月19日
	 * @param chnIndexInLogic
	 * @return true对接成功， false对接失败
	 */
	private boolean matchChannel(int time) {

		int needMatchCount = 0;

		for (int key : core.getCalBoardMap().keySet()) {
			CalBoard calBoard = core.getCalBoardMap().get(key);// 校准板同时发
			if (calBoard.isDisabled()) {
				continue;
			}
			// 基准电压
			double baseVolt = CalibrateCore.getBaseCfg().match.matchCals.get(key).get(time);
			// 下发基准电压
			writeBaseVoltage(key, baseVolt);

			needMatchCount++;
		}
		int findCount = 0;
		int matchCountIndex = 0; // 寻址次数
		// 开始查询ADC，采用连续多次采集来判断

		List<Channel> matchChnList = new ArrayList<>();
		do {

			CommonUtil.sleep(CalibrateCore.getBaseCfg().match.matchDelay);

			MbMatchAdcData response = core.getDeviceCore().qryCalMatch();
			core.getLogger().info("MbMatchAdcData=" + response);
			core.getNetworkService().pushCalMatchVolt(response);

			for (int key : core.getCalBoardMap().keySet()) {

				CalBoard calBoard = core.getCalBoardMap().get(key);// 校准板同时发
				if (calBoard.isDisabled()) {
					continue;
				}
				// 基准电压
				double baseVolt = CalibrateCore.getBaseCfg().match.matchCals.get(key).get(time);

				for (DriverMatchAdcData.AdcData adc : response.getAdcList()) {

					Channel chn = core.getDeviceCore().getChannelMap().get(adc.chnIndex);
					chn.getMatchVolt()[time] = adc.adc;

					if (Math.abs(adc.adc - baseVolt) <= CalibrateCore.getBaseCfg().match.voltOffset) {

						matchCountIndex++;
						matchChnList.add(chn);
					}

				}

			}

		} while (findCount++ < 3 && matchCountIndex < needMatchCount);

		return false;
	}

	/**
	 * 增加对接成功率
	 * 
	 * @author wavy_zheng 2021年7月19日
	 */
	public void startMatchEx() {

		if (matchWork) {
			return;
		}
		matchThread = new Thread(new Runnable() {
			public void run() {

				matchWork = true;
				int currentCalChn = -1;
				boolean matchSuccess = false;
				// 初始化
				for (Iterator<Integer> it = core.getDeviceCore().getChannelMap().keySet().iterator(); it.hasNext();) {

					Integer key = it.next();
					core.getDeviceCore().getChannelMap().get(key).setBindingCalBoardChannel(null);
					core.getDeviceCore().getChannelMap().get(key)
							.setMatchVolt(new double[CalibrateCore.getBaseCfg().match.time]);
				}

				int needMatchCount = 0;
				if (CalibrateCore.getBaseCfg().base.equipType == EquipType.PowerCab) {
					needMatchCount = CalibrateCore.getBaseCfg().calChnCount;
				} else {
					needMatchCount = 1;// 首个通道对接
				}

			}
		});

	}

	public void startMatch() {
		if (matchWork) {
			return;
		}
		matchThread = new Thread(new Runnable() {
			public void run() {

				matchWork = true;
				int currentCalChn = -1;
				boolean matchSuccess = false;

				try {

					if (CalibrateCore.getBaseCfg().match.staticMaxVolt > 0) {
						// 清空校准板状态
						for (int key : core.getCalBoardMap().keySet()) {
							CalBoard calBoard = core.getCalBoardMap().get(key);// 校准板同时发
							if (calBoard.isDisabled()) {
								continue;
							}
							calBoard.cfgVoltBase(0, WorkState.UNWORK, 0);
						}

						// 延时
						Thread.sleep(CalibrateCore.getBaseCfg().match.matchDelay);
						// 检测静态采集电压

						MbMatchAdcData response = core.getDeviceCore().qryCalMatch();
						core.getLogger().info("mbCalMatchData=" + response);
						core.getNetworkService().pushCalMatchVolt(response);

						List<DriverMatchAdcData.AdcData> adcData = response.getAdcList().stream()
								.filter(x -> x.adc > CalibrateCore.getBaseCfg().match.staticMaxVolt)
								.collect(Collectors.toList());

						if (adcData.size() > 0) {
							// 静态超压报警
							adcData.stream().forEach(x -> x.chnIndex += 1);
							throw new Exception(I18N.getVal(I18N.LogicChnMatchVoltOver, 0, adcData.toString()));
						}
					}

					core.getDeviceCore().getChannelMap().entrySet().stream().forEach(x -> {
						x.getValue().setBindingCalBoardChannel(null);
						x.getValue().setMatchVolt(new double[CalibrateCore.getBaseCfg().match.time]);
					});

					int matchCount = 0;
					if (CalibrateCore.getBaseCfg().base.equipType == EquipType.PowerCab) {
						matchCount = CalibrateCore.getBaseCfg().calChnCount;
					} else {
						matchCount = 1;// 首个通道对接
					}

					for (int calChnIndex = 0; calChnIndex < matchCount; calChnIndex++) {// 通道轮询
						currentCalChn = calChnIndex;
						for (int time = 0; time < CalibrateCore.getBaseCfg().match.time; time++) {

							for (int key : core.getCalBoardMap().keySet()) {
								CalBoard calBoard = core.getCalBoardMap().get(key);// 校准板同时发
								if (calBoard.isDisabled()) {
									continue;
								}
								core.getLogger().info("calboard setVoltBase chn=" + calChnIndex + ",matchVolt="
										+ CalibrateCore.getBaseCfg().match.matchCals.get(key).get(time));
								calBoard.cfgVoltBase(calChnIndex, WorkState.WORK,
										CalibrateCore.getBaseCfg().match.matchCals.get(key).get(time));

							}

							Thread.sleep(CalibrateCore.getBaseCfg().match.matchDelay);

							MbMatchAdcData response = core.getDeviceCore().qryCalMatch();
							core.getLogger().info("mbCalMatchData=" + response);
							core.getNetworkService().pushCalMatchVolt(response);

							for (DriverMatchAdcData.AdcData adc : response.getAdcList()) {
								core.getDeviceCore().getChannelMap().get(adc.chnIndex).getMatchVolt()[time] = adc.adc;
							}

						}

						/*
						 * for (int unitIndex = 0; unitIndex < core.getDeviceCore().getLogicCount();
						 * unitIndex++) { if (!core.getDeviceCore().isLogicUse(unitIndex)) { continue; }
						 * 
						 * for (int driverIndex = 0; driverIndex < core.getDeviceCore()
						 * .getLogicDriverCount(); driverIndex++) {
						 * 
						 * CalBoard bingCalBoard = null;// 针对整板对接
						 * 
						 * for (int chnIndex = 0; chnIndex < core.getDeviceCore() .getDriverChnCount();
						 * chnIndex++) { Channel channel = core.getDeviceCore().getChannelMap()
						 * .get((unitIndex * core.getDeviceCore().getLogicDriverCount() + driverIndex)
						 * core.getDeviceCore().getDriverChnCount() + chnIndex);
						 * 
						 * if (chnIndex == 0 || CalibrateCore.getBaseCfg().base.equipType ==
						 * EquipType.PowerCab) {
						 * 
						 * boolean match = false; int matchCalIndex = -1;
						 * 
						 * for (int ci = 0; ci < CalibrateCore.getBaseCfg().calboards.size(); ci++) {
						 * 
						 * if (CalibrateCore.getBaseCfg().calboards.get(ci).disabled) { continue; }
						 * 
						 * match = true; for (int i = 0; i < CalibrateCore.getBaseCfg().match.time; i++)
						 * { // System.out.println(channel.getChnIndex() + "channel.getMatchVolt()[" //
						 * + i // + "] =" + channel.getMatchVolt()[i]);
						 * 
						 * if (Math.abs(channel.getMatchVolt()[i] -
						 * CalibrateCore.getBaseCfg().match.matchCals.get(ci).get( i)) >
						 * CalibrateCore.getBaseCfg().match.voltOffset) {
						 * 
						 * // if (channel // .getMatchVolt()[i] <
						 * CalibrateCore.getBaseCfg().match.matchCals // .get(ci).get(i).adcMin // ||
						 * channel.getMatchVolt()[i] > CalibrateCore //
						 * .getBaseCfg().match.matchCals.get(ci).get(i).adcMax) { // 如果范围超过则未对接 match =
						 * false; break; } } if (match) { matchCalIndex = ci; break; } }
						 * 
						 * if (match) { bingCalBoard = core.getCalBoardMap().get(matchCalIndex);// 获取校准板
						 * channel.setBindingCalBoardChannel(
						 * bingCalBoard.getCalBoardChannels().get(calChnIndex)); } } else { if
						 * (bingCalBoard != null) { // 如果第一个通道连上了 channel.setBindingCalBoardChannel(
						 * bingCalBoard.getCalBoardChannels().get(chnIndex));// 自动 } } } // channel
						 * 
						 * // 上报对接情况
						 * 
						 * } // board } // unit
						 * 
						 */
						core.getNetworkService().pushChnData(core.getDeviceCore().getChannelMap().entrySet().stream()
								.map(e -> e.getValue()).collect(Collectors.toList()));

						core.getScreen().updateAllChannel(core.getDeviceCore().getChannelMap().entrySet().stream()
								.map(x -> x.getValue()).collect(Collectors.toList()));
					}

					matchSuccess = true;

				} catch (Exception e) {
					core.getLogger().error(e.getMessage(), e);
					core.getNetworkService().pushLog(I18N.getVal(I18N.MatchFailed, e.getMessage()), true);
				} finally {
					if (currentCalChn != -1) {
						for (int key : core.getCalBoardMap().keySet()) {
							CalBoard calBoard = core.getCalBoardMap().get(key);// 最后关闭所有校准板
							if (calBoard.isDisabled()) {
								continue;
							}
							try {
								calBoard.cfgVoltBase(currentCalChn, WorkState.UNWORK, 0);
							} catch (Exception e) {
								core.getLogger().error(e.getMessage(), e);
							}
						}
					}
					matchWork = false;

					// 上报对接完成
					MatchStateData matchStateData = new MatchStateData();
					matchStateData.setSuccess(matchSuccess);
					core.getNetworkService().pushSendQueue(new AlertDecorator(matchStateData));
				}
			}
		});
		matchThread.setDaemon(true);
		matchThread.start();
	}

	/**
	 * 连接万用表
	 * 
	 * @param data
	 * @throws Exception
	 * @
	 */
	public void cfgMeterConnect(MeterConnectData data) throws Exception {

		for (Meter meter : core.getMeters()) {
			if (data.isConnected()) {
				meter.connect();
			} else {
				meter.disconnect();
			}
		}

	}

	/**
	 * 驱动板绑定
	 * 
	 * @param data
	 */
	// public void cfgDriverBind(DriverBindData data) {
	//
	// CalBoard calboard = core.getCalBoardMap().get(data.getCalboardIndex());
	// Driverboard driverboard =
	// core.getLogicboards().get(data.getUnitIndex()).getDriverboards()
	// .get(data.getDriverIndex());
	//
	// if (data.isBind()) {// 绑定
	//
	// if (calboard.getDriverboard() == driverboard) {// 校准板已经被绑定
	//
	// return;
	// }
	// if (calboard.getDriverboard() != null) {
	// driverboard.setCalboard(null);
	// calboard.setDriverboard(null);
	// }
	//
	// driverboard.setCalboard(calboard);
	// calboard.setDriverboard(driverboard);
	//
	// } else {
	//
	// driverboard.setCalboard(null);
	// calboard.setDriverboard(null);
	// }
	// }

	/**
	 * 膜片开关
	 * 
	 * @param data
	 * @throws Exception
	 * @
	 */
	public void cfgModuleSwitch(ModuleSwitchData data) throws Exception {

		int driverIndex = data.getChnIndex() / CalibrateCore.getBaseCfg().base.driverChnCount;
		int chnIndexInDriver = data.getChnIndex() % CalibrateCore.getBaseCfg().base.driverChnCount;

		core.getDeviceCore().cfgModuleSwitch(driverIndex, chnIndexInDriver, data.open);
	}

	/**
	 * 逻辑板校准flash
	 * 
	 * @param data
	 * @
	 */
	public void cfgLogicFlashWrite(LogicFlashWriteData data) {

		// core.getDeviceCore().cfgFlash(data.getUnitIndex(), data.getChnIndex(),
		// data.getDots());

	}

	public void qryModeSwitch(ModeSwitchData data) {

		MbModeChangeData modeChangeData = core.getDeviceCore().qryModeChange();
		CalibrateCoreWorkMode mode = CalibrateCoreWorkMode.NONE;
		switch (modeChangeData.getMode()) {
		case CAL:
			mode = CalibrateCoreWorkMode.CAL;
		case JOINT:
			mode = CalibrateCoreWorkMode.MATCH;
			break;
		}
		data.setMode(mode);// 在对接模式中
	}

	public void qryMeterConnect(MeterConnectData data) {
		boolean connected = core.getMeters().get(0).isConnected();
		data.setConnected(connected);

	}

	public void qryModuleSwitch(ModuleSwitchData data) {
		boolean open = core.getDeviceCore().qryModuleSwitch(data.getUnitIndex(), data.getChnIndex());
		data.setOpen(open);

	}

	public void qryLogicFlashWrite(LogicFlashWriteData data) {

		MbFlashParamData response = core.getDeviceCore().qryLogicFlash(data.getUnitIndex(), data.getChnIndex());

	}

	public void qryRequestCalculate(RequestCalculateData data) {

		core.getDeviceCore().getChannelMap().forEach((k, v) -> {

			CalculateDotData dot = v.getCalculateDotData();
			if (dot != null) {
				data.appendDotData(dot);
			}

		});

	}

	public void cfgTestMode(TestModeData data) throws Exception {

		switch (data.getTestMode()) {
		case ClearChnReadyFlag:

			// 清除通道选中标志
			core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.forEach(x -> x.getValue().setSelected(false));

			break;
		case EnterCalModeAndStartCal:
			System.out.println("channls size: " + core.getDeviceCore().getChannelMap().size());
			if (core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.count() == 0) {
				// 没有通道选中
				throw new Exception(I18N.getVal(I18N.NoChannelSelected));
			}

			if (calibrateCoreWorkMode != CalibrateCoreWorkMode.CAL) {
				throw new Exception(I18N.getVal(I18N.NotInCalMode));
			}
			// 选择可校准通道
			if (core.getDeviceCore().getChannelMap().entrySet().stream()
					.filter(x -> x.getValue().isSelected() && x.getValue().getBindingCalBoardChannel() != null && !core
							.getCalBoardMap().get(x.getValue().getBindingCalBoardChannel().getBoardIndex()).isWork())
					.count() == 0) {
				throw new Exception(I18N.getVal(I18N.NoCanCalibrateChannelSelected));
			}

			core.getDeviceCore().getChannelMap().entrySet().stream()
					.filter(x -> x.getValue().isSelected() && x.getValue().getBindingCalBoardChannel() != null && !core
							.getCalBoardMap().get(x.getValue().getBindingCalBoardChannel().getBoardIndex()).isWork())
					.forEach(x -> {
						x.getValue().setReady(true);
						core.getNetworkService().pushChnData(Arrays.asList(x.getValue()));
					});

			// 清空选中通道
			core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.forEach(x -> x.getValue().setSelected(false));

			for (int calIndex : core.getCalBoardMap().keySet()) {
				CalBoard calBoard = core.getCalBoardMap().get(calIndex);
				calBoard.startCalibrate();

			}

			break;
		case EnterCalModeAndStartCheck:
			if (core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.count() == 0) {
				// 没有通道选中
				throw new Exception(I18N.getVal(I18N.NoChannelSelected));
			}

			if (calibrateCoreWorkMode != CalibrateCoreWorkMode.CAL) {
				throw new Exception(I18N.getVal(I18N.NotInCalMode));
			}
			// 选择可计量通道
			if (core.getDeviceCore().getChannelMap().entrySet().stream()
					.filter(x -> x.getValue().isSelected() && x.getValue().getBindingCalBoardChannel() != null && !core
							.getCalBoardMap().get(x.getValue().getBindingCalBoardChannel().getBoardIndex()).isWork())
					.count() == 0) {
				throw new Exception(I18N.getVal(I18N.NoCanMeasureChannelSelected));
			}

			core.getDeviceCore().getChannelMap().entrySet().stream()
					.filter(x -> x.getValue().isSelected() && x.getValue().getBindingCalBoardChannel() != null && !core
							.getCalBoardMap().get(x.getValue().getBindingCalBoardChannel().getBoardIndex()).isWork())
					.forEach(x -> {
						x.getValue().setReady(true);
						core.getNetworkService().pushChnData(Arrays.asList(x.getValue()));
					});

			// 清空选中通道
			core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.forEach(x -> x.getValue().setSelected(false));

			for (int calIndex : core.getCalBoardMap().keySet()) {
				CalBoard calBoard = core.getCalBoardMap().get(calIndex);
				calBoard.startCalculate();

			}

			break;
		case StopTestAndExitCalMode:

			if (core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.count() == 0) {
				// 没有通道选中
				throw new Exception(I18N.getVal(I18N.NoChannelSelected));
			}

			if (core.getDeviceCore().getChannelMap().entrySet().stream()
					.filter(x -> x.getValue().isSelected() && x.getValue().isReady()).count() == 0) {
				// 没有选择可停止通道
				throw new Exception(I18N.getVal(I18N.NoCanStopChannelSelected));
			}

			core.getDeviceCore().getChannelMap().entrySet().stream()
					.filter(x -> x.getValue().isSelected() && x.getValue().isReady()).forEach(x -> {
						x.getValue().setReady(false);
						core.getNetworkService().pushChnData(Arrays.asList(x.getValue()));
					});

			// 清空选中通道
			core.getDeviceCore().getChannelMap().entrySet().stream().filter(x -> x.getValue().isSelected())
					.forEach(x -> x.getValue().setSelected(false));

			break;
		}
	}

	/**
	 * 前提是已经清空ready
	 * 
	 * @param data
	 */
	public void cfgChnSelect(ChnSelectData data) {

		int startChn = data.getDriverIndex() * core.getDeviceCore().getDriverChnCount();

		System.out.println(data.getChnFlag());

		for (int i = 0; i < core.getDeviceCore().getDriverChnCount(); i++) {

			if ((data.getChnFlag() >> i & 0x01) > 0) {

				Channel channel = core.getDeviceCore().getChannelMap().get(startChn + i);

				if (channel.getBindingCalBoardChannel() != null) {
					channel.setSelected(true);
				}
			}
		}
	}

	public void cfgLogicFlashWriteDebug(LogicFlashWrite2DebugData data) throws Exception {

		MbFlashParamData save = new MbFlashParamData();
		save.setDriverIndex(data.getChnIndex() / CalibrateCore.getBaseCfg().base.driverChnCount);
		save.setChnIndex(data.getChnIndex() % CalibrateCore.getBaseCfg().base.driverChnCount);
		save.setCv1DotCount(data.getCv1DotCount());
		save.setCv2DotCount(data.getCv2DotCount());
		save.setModuleIndex(data.getModuleIndex());
		save.setKb_dotMap(data.getKb_dotMap());

		System.out.println("write module " + data.getModuleIndex() + " flash mode size: " + data.getKb_dotMap().size());
		core.getDeviceCore().cfgFlash(save);

	}

	public void cfgLogicCalibrateDebug(LogicCalibrate2DebugData data) throws Exception {

		List<DriverCalibrateData.AdcData> groups = new ArrayList<>();
		for (int i = 0; i < core.getCalCfg().steadyCfgData.getSampleCount(); i++) {
			groups.add(new DriverCalibrateData.AdcData());
		}
		MbCalibrateChnData cal = new MbCalibrateChnData();
		cal.setDriverIndex(data.getChnIndex() / core.getDeviceCore().getDriverChnCount());
		cal.setChnIndex(data.getChnIndex() % core.getDeviceCore().getDriverChnCount());
		cal.setModuleIndex(data.getModuleIndex());
		cal.setPole(DriverEnvironment.Pole.values()[data.getPole().ordinal()]);
		cal.setMode(DriverEnvironment.CalMode.values()[data.getWorkMode().ordinal()]);
		cal.setVoltageDA((int) data.getProgramV());
		cal.setCurrentDA((int) data.getProgramI());
		cal.setRange(data.getPrecision());
		cal.setAdcDatas(groups);

		core.getDeviceCore().cfgCalibrate(cal);
	}

	public void cfgLogicCalculateDebug(LogicCalculate2DebugData data) throws Exception {

		MbMeasureChnData measure = new MbMeasureChnData();
		measure.setDriverIndex(data.getChnIndex() / core.getDeviceCore().getDriverChnCount());
		measure.setChnIndex(data.getChnIndex() % core.getDeviceCore().getDriverChnCount());
		measure.setPole(DriverEnvironment.Pole.values()[data.getPole().ordinal()]);
		measure.setMode(DriverEnvironment.CalMode.values()[data.getMode().ordinal()]);
		measure.setCalculateDot(data.getCalculateDot());
		measure.setModuleIndex(data.getModuleIndex()); // 计量默认全选模片
		measure.setProgramDot(data.getProgramDot());

		System.out.println("set module index " + data.getModuleIndex());
		System.out.println("set program Val :" + data.getProgramDot());

		
		
		// if (measure.getMode() == DriverEnvironment.CalMode.CV) {
		//
		// measure.setProgramDot(core.getCalCfg().calculatePlanData.getMaxCalculateCurrent());
		// } else {
		//
		// measure.setProgramDot(core.getCalCfg().calculatePlanData.getMaxCalculateVoltage());
		// }
		measure.setAdcDatas(data.getAdcDatas());

		core.getDeviceCore().cfgCalculate(measure);

	}

	public void qryLogicCalibrateDebug(LogicCalibrate2DebugData data) {

		int driverIndex = data.getChnIndex() / CalibrateCore.getBaseCfg().base.driverChnCount;
		int chnIndex = data.getChnIndex() % CalibrateCore.getBaseCfg().base.driverChnCount;

		MbCalibrateChnData response = core.getDeviceCore().qryCalibrate(driverIndex, chnIndex);
		data.setPole(response.getPole());
		data.setWorkMode(response.getMode());
		data.setModuleIndex(response.getModuleIndex());
		data.setPrecision(response.getRange());
		data.setProgramV(response.getVoltageDA());
		data.setProgramI(response.getCurrentDA());
		data.setAdcs(response.getAdcDatas());

		System.out.println(response);

	}

	public void qryLogicCalculateDebug(LogicCalculate2DebugData data) {

		int driverIndex = data.getChnIndex() / CalibrateCore.getBaseCfg().base.driverChnCount;
		int chnIndex = data.getChnIndex() % CalibrateCore.getBaseCfg().base.driverChnCount;

		MbMeasureChnData response = core.getDeviceCore().qryCalculate(driverIndex, chnIndex);
		System.out.println(response.toString());
		logger.info("read mb measure:" + response);

		data.setPole(response.getPole());
		data.setMode(response.getMode());
		data.setCalculateDot(response.getCalculateDot());
		data.setModuleIndex(response.getModuleIndex());
		data.setProgramKReadonly(response.getProgramKReadonly());
		data.setProgramBReadonly(response.getProgramBReadonly());
		data.setAdcKReadonly(response.getAdcKReadonly());
		data.setAdcBReadonly(response.getAdcBReadonly());
		data.setBackAdcKReadonly1(response.getBackAdcKReadonly1());
		data.setBackAdcBReadonly1(response.getBackAdcBReadonly1());
		data.setBackAdcKReadonly2(response.getBackAdcKReadonly2());
		data.setBackAdcBReadonly2(response.getBackAdcBReadonly2());
		data.setProgramDot((long) response.getProgramDot());
		data.setAdcDatas(response.getAdcDatas());

		for (int n = 0; n < data.getAdcDatas().size(); n++) {
			logger.info("get adcList size "+ response.getAdcDatas().get(n).adcList.size());

			logger.info(n + " : main adc" + response.getAdcDatas().get(n).adcList.get(0));
			logger.info(n + " : sub2 adc" + response.getAdcDatas().get(n).adcList.get(1));
		}

	}

	public void cfgBindCalBoard(BindCalBoardData data) throws Exception {

		int unitIndex = data.getUnitIndex();
		int driverIndex = data.getDriverIndex();
		int calIndex = data.getCalIndex();
		System.out.println(data.isBind());

		if (core.getCalBoardMap().get(calIndex).isDisabled()) {
			throw new Exception(I18N.getVal(I18N.CalBoardNotUse, calIndex + 1));
		}

		if (core.getCalBoardMap().get(calIndex).isWork()) {
			throw new Exception(I18N.getVal(I18N.CalBoardIsWorking, calIndex + 1));
		}

		int endChnIndex = driverIndex * core.getDeviceCore().getDriverChnCount()
				+ CalibrateCore.getBaseCfg().calChnCount - 1;

		int totalCount = core.getDeviceCore().getDriverCount() * core.getDeviceCore().getDriverChnCount();

		if (endChnIndex + 1 > totalCount) {

			throw new Exception("校准板通道数" + CalibrateCore.getBaseCfg().calChnCount + "绑定驱动板" + (driverIndex + 1)
					+ "导致越界(" + endChnIndex + " ->" + totalCount + ")");
		}

		int startChnIndex = driverIndex * core.getDeviceCore().getDriverChnCount();
		for (int i = 0; i < CalibrateCore.getBaseCfg().calChnCount; i++) {
			Channel channel = core.getDeviceCore().getChannelMap().get(startChnIndex + i);
			channel.setBindingCalBoardChannel(
					data.isBind() ? core.getCalBoardMap().get(calIndex).getCalBoardChannels().get(i) : null);
		}

		core.getNetworkService().pushChnData(core.getDeviceCore().getChannelMap().entrySet().stream()
				.map(x -> x.getValue()).collect(Collectors.toList()));

		/*
		 * for (int i = 0; i < core.getDeviceCore().getDriverChnCount(); i++) { Channel
		 * channel = core.getDeviceCore().getChannelMap() .get((unitIndex *
		 * core.getDeviceCore().getLogicDriverCount() + driverIndex)
		 * core.getDeviceCore().getDriverChnCount() + i);
		 * channel.setBindingCalBoardChannel( data.isBind() ?
		 * core.getCalBoardMap().get(calIndex).getCalBoardChannels().get(i) : null); }
		 * 
		 * core.getNetworkService()
		 * .pushChnData(core.getDeviceCore().getChannelMap().entrySet().stream()
		 * .filter(x -> core.getDeviceCore().isLogicUse(x.getValue().getUnitIndex()))
		 * .map(x -> x.getValue()).collect(Collectors.toList())); core.getScreen()
		 * .updateAllChannel(core.getDeviceCore().getChannelMap().entrySet().stream()
		 * .filter(x -> core.getDeviceCore().isLogicUse(x.getValue().getUnitIndex()))
		 * .map(x -> x.getValue()).collect(Collectors.toList()));
		 */
	}

	public void cfgPCSelfCheck(PCSelfCheckData data) {
		// TODO Auto-generated method stub
		// core.getDeviceCore().cfgSelfCheck(data.getState(), data.getDate());
	}

	public void qryPCSelfTestInfo(PCSelfTestInfoData data) {
		// MBSelfTestInfoData response = core.getDeviceCore().qrySelfTestInfo();
		// data.setSelfTestInfo(response.getSelfTestInfo());
	}

	/**
	 * 查询自检信息
	 * 
	 * @author wavy_zheng 2022年3月31日
	 * @param selfcheck
	 */
	public void qryDeviceSelfCheck(DeviceSelfCheckData selfcheck) {

		MbSelfCheckData mbSelf = core.getDeviceCore().qrySelfCheck();

		List<DriverCheckInfoData> list = new ArrayList<>();
		for (DriverCheckData data : mbSelf.getCheckDataList()) {

			DriverCheckInfoData check = new DriverCheckInfoData();
			check.adPick = data.getAdPick();
			check.calParam = data.getCalParam();
			check.checkboard = data.getCheckboard();
			check.driverFlash = data.getDriverFlash();
			check.driverIndex = data.getDriverIndex();
			check.driverSram = data.getDriverSram();
			check.powerOk = data.isPowerOk();
			check.tempPick = data.getTempPick();

			list.add(check);

		}
		selfcheck.setDriverCheckInfoDataList(list);

	}

	public void stopTest() {
		// TODO Auto-generated method stub
		for (int calBoardIndex : core.getCalBoardMap().keySet()) {
			core.getCalBoardMap().get(calBoardIndex).setWork(false);
		}
	}

	/**
	 * 处理ADC偏差
	 * 
	 * @param calMode
	 * @param offset
	 * @return 处理后的偏差值
	 */
	private void adjustAdcOffset(boolean logic, TestDot dot, boolean backCheck2) {

		if (!CalibrateCore.getBaseCfg().adjustParam.use) {
			return;
		}

		double offset = (logic ? dot.adc : (backCheck2 ? dot.checkAdc2 : dot.checkAdc)) - dot.programVal;

		// triggerDebugLog(dot.channel,
		// String.format("%s%s adc调整前=%f", logic ? "逻辑板" : "回检板", dot.mode, (logic ?
		// dot.adc : dot.checkAdc)));

		AdcAdjust adcAdjust = findAdjust(logic, dot.mode, dot.pole, dot.precision);
		if (adcAdjust != null) {

			if (Math.abs(offset) > adcAdjust.threshold) {

				offset = offset / adcAdjust.div;
				if (logic) {

					dot.adc = dot.programVal + offset; // 求出校正后的ADC
				} else {

					if (!backCheck2) {
						dot.checkAdc = dot.programVal + offset; // 求出校正后的ADC
					} else {

						dot.checkAdc2 = dot.programVal + offset; // 求出校正后的ADC
					}
				}
			}

		}

		triggerDebugLog(dot.channel, String.format("%s%s adc调整后=%f", logic ? "逻辑板" : (backCheck2 ? "回检板2" : "回检板1"),
				dot.mode, (logic ? dot.adc : (backCheck2 ? dot.checkAdc2 : dot.checkAdc))));

	}

	private AdcAdjust findAdjust(boolean logic, DriverEnvironment.CalMode calMode, DriverEnvironment.Pole pole,
			int level) {

		Optional<AdcAdjust> adjust = CalibrateCore.getBaseCfg().adjustParam.adcAdjusts.stream()
				.filter(x -> x.logic == logic && x.mode == calMode && x.pole == pole && x.level == level).findAny();

		if (!adjust.equals(Optional.empty())) {
			return adjust.get();
		}
		return null;

	}

	/**
	 * 动态切表,在通道映射下可以根据通道序号自动找到校准板
	 * 
	 * @author wavy_zheng 2022年3月29日
	 * @param data
	 */
	public void cfgCalboardMeterSwitch(SwitchMeterData data) {

		int chnIndex = data.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			CalBoardChannel calChn = core.findCalboardChnByDeviceChnIndex(chnIndex);
			// 根据通道序号，换算出校准板号
			if (calChn != null) {

				data.setDriverIndex(calChn.getBoardIndex());
			}
			chnIndex = core.getChnMapService().mapChnIndex(data.getDriverIndex(), chnIndex);

		}
		Meter meter = core.getCalBoardMap().get(data.getDriverIndex()).getMeter();

		synchronized (meter) {

			if (data.isConnect()) {

				// 断开另外一个校准板
				if (core.getMeterParamMap().get(meter).lastCalIndex != data.getDriverIndex()
						&& core.getMeterParamMap().get(meter).lastCalIndex != -1) {

					core.getCalBoardMap().get(core.getMeterParamMap().get(meter).lastCalIndex).cfgRelayControl(false);
				}
				cfgRelayMeterDebug(data);
				// 表连接标志
				core.getMeterParamMap().get(meter).lastCalIndex = data.getDriverIndex();

			} else {

				cfgRelayMeterDebug(data);
				core.getMeterParamMap().get(meter).lastCalIndex = -1;
			}

		}

	}

	public void cfgCalboardTestMode(CalBoardTestModeData data) {

		int chnIndex = data.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			CalBoardChannel calChn = core.findCalboardChnByDeviceChnIndex(chnIndex);
			// 根据通道序号，换算出校准板号
			if (calChn != null) {

				data.setDriverIndex(calChn.getBoardIndex());
			}
			chnIndex = core.getChnMapService().mapChnIndex(data.getDriverIndex(), chnIndex);

		}
		core.getCalBoardMap().get(data.getDriverIndex()).cfgTestMode(chnIndex, data.getTestType());

	}

	public void cfgCalibrate2Debug(CalCalibrate2DebugData data) {

		int chnIndex = data.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			CalBoardChannel calChn = core.findCalboardChnByDeviceChnIndex(chnIndex);
			// 根据通道序号，换算出校准板号
			if (calChn != null) {

				data.setDriverIndex(calChn.getBoardIndex());
			}
			chnIndex = core.getChnMapService().mapChnIndex(data.getDriverIndex(), chnIndex);

		}

		core.getCalBoardMap().get(data.getDriverIndex()).cfgCalibrate2(chnIndex, data.getWorkState(),
				data.getWorkMode(), data.getPrecision(), data.getPole(), data.getProgramV(), data.getProgramI());
	}

	public void cfgCalculate2Debug(CalCalculate2DebugData data) {

		int chnIndex = data.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			// 通过设备通道序号查找到校准板上绑定的通道对象
			CalBoardChannel calChn = core.findCalboardChnByDeviceChnIndex(chnIndex);
			// 根据通道序号，换算出校准板号
			if (calChn != null) {

				data.setDriverIndex(calChn.getBoardIndex());
			}
			chnIndex = core.getChnMapService().mapChnIndex(data.getDriverIndex(), chnIndex);
		}

		core.getCalBoardMap().get(data.getDriverIndex()).cfgCalculate2(chnIndex, data.getWorkState(),
				CalEnvironment.WorkMode.values()[data.getWorkMode().ordinal()],
				CalEnvironment.Pole.values()[data.getPole().ordinal()], data.getCalculateDot(), data.getPrecision());
	}

	public void connectDevice(ConnectDeviceData data) throws Exception {

		if (data.isConnect()) {

			core.getDeviceCore().setIp(data.getDeviceIp());
			core.getDeviceCore().connect();

		} else {

			core.getDeviceCore().disConnect();
		}

	}

	/**
	 * 万用表切表
	 * 
	 * @author wavy_zheng 2022年2月8日
	 * @param data
	 */
	public void cfgRelayControlDebug(CalRelayControlDebugData data) {

		core.getCalBoardMap().get(data.getDriverIndex()).cfgRelayControl(data.isConnected());
	}

	public void cfgRelayMeterDebug(SwitchMeterData data) {

		core.getCalBoardMap().get(data.getDriverIndex()).cfgRelayControl(data.isConnect());
	}

	public void qryRelayControlDebug(CalRelayControlDebugData data) {
		boolean open = core.getCalBoardMap().get(data.getDriverIndex()).qryRelayControl();
		data.setConnected(open);
	}

	public void qryMeterRead(ReadMeterData data) throws Exception {

		int chnIndex = data.getChnIndex();
		if (core.getChnMapService().isEnable()) {

			// 通过设备通道序号查找到校准板上绑定的通道对象
			CalBoardChannel calChn = core.findCalboardChnByDeviceChnIndex(chnIndex);
			// 根据通道序号，换算出校准板号
			if (calChn != null) {

				data.setDriverIndex(calChn.getBoardIndex());
			}

		}

		Meter meter = core.getCalBoardMap().get(data.getDriverIndex()).getMeter();
		if (meter == null) {

			throw new Exception("丢失万用表" + data.getDriverIndex());
		}
		double readVal = meter.ReadSingleClearBuffer();
		data.setReadVal(readVal);

	}

	/**
	 * 查询新版电阻系数
	 * 
	 * @author wavy_zheng 2022年2月8日
	 * @param data
	 */
	public void qryResistanceDebug(CalResistanceDebugData data) {

		/*
		 * int chnIndex = data.getDriverIndex(); if(core.getChnMapService().isEnable())
		 * {
		 * 
		 * //通过设备通道序号查找到校准板上绑定的通道对象 CalBoardChannel calChn =
		 * core.findCalboardChnByDeviceChnIndex(chnIndex); //根据通道序号，换算出校准板号 if(calChn !=
		 * null) {
		 * 
		 * data.setDriverIndex(calChn.getBoardIndex()); } }
		 */

		ResistanceModeData rmd = core.getCalBoardMap().get(data.getDriverIndex())
				.qryResistanceModeData(data.getWorkPattern(), data.getRange());
		data.setResistance(rmd.getResistance());
	}

	public void qryTemperatureDebug(CalTempQueryDebugData data) {

		OverTempAlertData otad = core.getCalBoardMap().get(data.getDriverIndex()).qryOverTempatureData();
		data.setBackupTemp(otad.getBackupTemp());
		data.setMainTemp(otad.getMainTemp());
		data.setConstantTempAlert(otad.getConstantTempAlert());
		data.setDeviationAlert(otad.getDeviationAlert());
		data.setElecTemp1(otad.getElecTemp1());
		data.setElecTemp2(otad.getElecTemp2());
		data.setElecTempAlert(otad.getElecTempAlert());
		data.setFanAlert(otad.getFanAlert());
		data.setOverTempAlert(otad.getOverTempAlert());

	}

	public void cfgResistanceDebug(CalResistanceDebugData data) {

		ResistanceModeData rmd = new ResistanceModeData();
		rmd.setDriverIndex(data.getDriverIndex());
		rmd.setResistance(data.getResistance());
		rmd.setWorkPattern(data.getWorkPattern());
		rmd.setRange(data.getRange());
		core.getCalBoardMap().get(data.getDriverIndex()).cfgResistanceModeData(rmd);
	}

	
	public void cfgRelayResistanceDebug(ResistanceModeRelayDebugData data) {

		ResistanceModeRelayData rmd = new ResistanceModeRelayData();
		rmd.setDriverIndex(data.getDriverIndex());
		rmd.setRelayIndex(data.getRelayIndex());
		rmd.setResistance(data.getResistance());
		rmd.setWorkPattern(data.getWorkPattern());
		rmd.setRange(data.getRange());
		core.getCalBoardMap().get(data.getDriverIndex()).cfgRelayResistanceData(rmd);
	}
	
	public void cfgTempControl(int driverIndex, double temp, boolean open) {

		core.getCalBoardMap().get(driverIndex).cfgTempControlData(temp, open);

	}

	/**
	 * 根据实际电流值计算值精度范围（档位）
	 * 
	 * @author wavy_zheng 2022年5月4日
	 * @param meterVal
	 * @return
	 */
	public int getRangeForMeterVal(double meterVal) {

		Optional<RangeCurrentPrecision> a = core.getCalCfg().rangeCurrentPrecisionData.getRanges().stream()
				.filter(x -> meterVal > x.min && meterVal <= x.max).findAny();

		if (a.equals(Optional.empty())) {

			return 0;
		}

		// 临界档位往高精度
		List<RangeCurrentPrecision> ranges = core.getCalCfg().rangeCurrentPrecisionData.getRanges();

		int precision = ranges.get(ranges.size() - 1).level;// 默认最高精度

		for (RangeCurrentPrecision range : ranges) {
			if (meterVal > range.min) {
				precision = range.level;
				break;
			}
		}

		return precision;

	}

	public long getDAFromMeter(Channel channel, int moduleIndex, DriverEnvironment.CalMode mode, int range,
			double meterVal) {

		List<TestDot> list = channel.getCalDots().stream()
				.filter(x -> x.moudleIndex == moduleIndex && x.mode == mode && x.precision == range)
				.collect(Collectors.toList());

		triggerDebugLog(channel, "主模片mode = " + mode + ",range=" + range + ",共找到" + list.size() + "个校准点");

		if (list.isEmpty()) {

			return 0;
		}

		for (int n = 0; n < list.size(); n++) {

			TestDot dot = list.get(n);
			if (meterVal <= dot.meterVal) {

				if (dot.programK == 0) {

					continue;
				}

				triggerDebugLog(channel, "匹配到校准点 " + dot.programVal + "[实际值:" + dot.meterVal + "],程控K=" + dot.programK
						+ ",程控B = " + dot.programB);

				triggerDebugLog(channel, "DA = " + meterVal + " * " + dot.programK + " + " + dot.programB);
				return (long) (meterVal * dot.programK + dot.programB);

			}
		}

		TestDot dot = list.get(list.size() - 1);

		return (long) (meterVal * dot.programK + dot.programB);

	}

	/**
	 * 上位机 查询flash
	 * 
	 * @param data
	 */
	public LogicFlashWrite2DebugData qryLogicFlashWriteDebug(LogicFlashWrite2DebugData data) {
		MbFlashParamData result = core.getDeviceCore().qryFlash(0, data.getChnIndex());
		LogicFlashWrite2DebugData response = new LogicFlashWrite2DebugData();
		response.setModuleIndex(result.getModuleIndex());
		response.setUnitIndex(data.getUnitIndex());
		response.setChnIndex(data.getChnIndex());
		response.setCv1DotCount(result.getCv1DotCount());
		response.setCv2DotCount(result.getCv2DotCount());
		response.setKb_dotMap(result.getKb_dotMap());
		//
		return response;
	}

}
