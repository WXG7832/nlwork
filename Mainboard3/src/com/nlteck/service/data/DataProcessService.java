package com.nlteck.service.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.nlteck.Context;
import com.nlteck.ParameterName;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.service.StartupCfgManager.Range;
import com.nlteck.service.StartupCfgManager.RangeSection;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nlteck.util.MathUtil;
import com.nltecklib.protocol.li.logic.LogicEnvironment.StepAndLoop;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月16日 上午11:15:20 数据优化处理滤器
 */
public class DataProcessService implements DataFilterService {

	/**
	 * cv大电流特调程序
	 */
	private final static double EXCEPTION_CV_CURRENT = 150000;
	private final static double IC_MICRO_VOLTAGE = 2500;
	private final static double IC_MICRO_CURRENT = 5;

	/**
	 * 恒压电压范围调整
	 */
	private final static double ADJUST_CCCV_VOLTAGE_START = -20;
	private final static double ADJUST_CCCV_VOLTAGE_END = 50;

	private final static double THRESHOLD_VOLTAGE_OFFSET = 10; // 截止电压偏移最大值，超过该值主控直接将电压重新拉回
	private Map<Channel, List<ChnData>> filterBuff = new ConcurrentHashMap<>(); // 滤波缓存
	private Map<Channel, Integer> filterCount = new ConcurrentHashMap<>(); // 滤波累计次数

	private Map<Channel, List<ChannelData>> cvCurrentFilterBuff = new ConcurrentHashMap<>();
	private Map<Channel, List<ChannelData>> dcVoltageFilterBuff = new ConcurrentHashMap<>();
	private Map<Channel, List<ChannelData>> ccVoltageFilterBuff = new ConcurrentHashMap<>();
	private Map<Channel, List<ChannelData>> ccRawVoltageFilterBuff = new ConcurrentHashMap<>();
	private Map<Channel, List<ChannelData>> dcRawVoltageFilterBuff = new ConcurrentHashMap<>();
	private Map<Channel, List<ChannelData>> cvRawCurrentFilterBuff = new ConcurrentHashMap<>();

	private Map<Channel, List<ChannelData>> slpVoltFilterBuff = new ConcurrentHashMap<>();
	private Map<Channel, ChannelData> lastDataBuff = new ConcurrentHashMap<>();

	private Logger logger;
	private final static int DC_FILTER_COUNT = 5; // 滤波缓存个数
	private final static int CC_FILTER_COUNT = 5; // 滤波缓存个数
	private final static int FILTER_COUNT = 2; // 滤波缓存个数
	private final static int SLP_VOLT_FILTER_COUNT = 5; // 电压波动过滤
	private final static int CV_CURRENT_FILTER_COUNT = 5;
	private final static int CONTINUE_FILTER_TOTAL = 2; // 连续滤波累计个数
	private final static double FILTER_FACTOR = 2.5; // 过滤系数,多少倍标准差开始过滤

	public DataProcessService() {

		try {
			logger = LogUtil.createLog("log/dataProcessService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearData(Channel channel) {

		if (cvCurrentFilterBuff.get(channel) != null) {

			cvCurrentFilterBuff.get(channel).clear();
		}
		if (dcVoltageFilterBuff.get(channel) != null) {

			dcVoltageFilterBuff.get(channel).clear();
		}
		if (ccVoltageFilterBuff.get(channel) != null) {

			ccVoltageFilterBuff.get(channel).clear();
		}
		if (ccRawVoltageFilterBuff.get(channel) != null) {

			ccRawVoltageFilterBuff.get(channel).clear();
		}
		if (dcRawVoltageFilterBuff.get(channel) != null) {

			dcRawVoltageFilterBuff.get(channel).clear();
		}
		if (cvRawCurrentFilterBuff.get(channel) != null) {

			cvRawCurrentFilterBuff.get(channel).clear();
		}
		if (slpVoltFilterBuff.get(channel) != null) {

			slpVoltFilterBuff.get(channel).clear();
		}

	}

	@Override
	public List<ChnDataPack> filterRawDatas(Channel channel, List<ChnDataPack> rawDatas) {

		double disableCurrentLine = MainBoard.startupCfg.getRange().disableCurrentLine;
		double disableVoltageLine = MainBoard.startupCfg.getRange().disableVoltageLine;
		if (disableCurrentLine > 0) {

			// 屏蔽小电流
			for (ChnDataPack rawData : rawDatas) {

				if (rawData.getState() != DriverEnvironment.ChnState.RUNNING
						&& rawData.getCurrent() <= disableCurrentLine) {

					rawData.setCurrent(0);
				} else if (rawData.getState() == DriverEnvironment.ChnState.RUNNING
						&& rawData.getWorkMode() == DriverEnvironment.WorkMode.SLEEP
						&& rawData.getCurrent() <= disableCurrentLine) {

					rawData.setCurrent(0);
				}

			}

		}
		if (disableVoltageLine > 0) {

			// 屏蔽小电压
			for (ChnDataPack rawData : rawDatas) {

				if (rawData.getVoltage() <= disableVoltageLine) {

					rawData.setVoltage(0);
				}
			}
		}
		/**
		 * 特殊处理步次
		 */

		if (MainBoard.startupCfg.getProcedureSupportInfo().supportLeadData) {
			// processLeadStepData(channel,rawDatas);
		}

		return rawDatas;
	}

	/**
	 * 处理前面几步数据
	 * 
	 * @author wavy_zheng 2022年3月2日
	 * @param channel
	 * @param rawDatas
	 */
	private void processLeadStepData(Channel channel, List<ChnDataPack> rawDatas) {

		for (ChnDataPack pack : rawDatas) {

			if (channel.getStepIndex() > 0 && pack.getStepIndex() > channel.getStepIndex()) {

				if (pack.getStepElapsedTime() <= 2000) {

					Step prestep = channel.getProcedureStep(channel.getStepIndex());
					if (prestep.getWorkMode() == WorkMode.SLEEP
							&& pack.getWorkMode() != DriverEnvironment.WorkMode.SLEEP) {

						// 强行将当前步次修改为
						pack.setVoltage(CommonUtil.produceRandomNumberInRange(channel.getVoltage(), 0.2));
						pack.setCurrent(0);
						pack.setCapacity(0);
						pack.setAlertCapacity(0);
						pack.setAlertCurrent(0);
						pack.setAlertTime(0);
						pack.setAlertVolt(pack.getVoltage());
						pack.setWorkMode(DriverEnvironment.WorkMode.SLEEP);
						pack.setStepIndex(channel.getStepIndex());
						channel.setLeadStepMiliseconds(pack.getStepElapsedTime()); // 做记录
						pack.setStepElapsedTime(pack.getStepElapsedTime() + channel.getStepElapseMiliseconds());

					} else {

						channel.setLeadStepMiliseconds(0);
					}

				} else {

					if (channel.getLeadStepMiliseconds() > 0) {

						pack.setStepElapsedTime(pack.getStepElapsedTime() - channel.getLeadStepMiliseconds());
					}
				}

			} else {

				if (channel.getLeadStepMiliseconds() > 0) {

					pack.setStepElapsedTime(pack.getStepElapsedTime() - channel.getLeadStepMiliseconds());
				}

			}

			if (channel.getDeviceChnIndex() == 0) {

				System.out.println(pack);
			}

		}

	}

	@Override
	public List<ChannelData> filterChannelDatas(Channel channel, List<ChannelData> channelDatas) {

		// 纠正IC小电流和CC,DC截止电压
		for (ChannelData chnData : channelDatas) {

			adjustICMicroCurrent(channel, chnData);

			/**
			 * 用于2代王峥程序，现已弃用
			 */
			// adjustEndVoltage(channel, chnData);
		}

		List<ChannelData> copyList = new ArrayList<>();
		copyList.addAll(channelDatas);

		Context.getDataProvider().getProtectService().processFirstProtections(channel, channelDatas);

		if (channel.getState() == ChnState.RUN) {
			for (int n = 0; n < channelDatas.size(); n++) {

				ChannelData chnData = channelDatas.get(n);
				Step step = channel.getProcedureStep(chnData.getStepIndex());

				if (step != null) {

					ChannelData lastData = null;
					if (n == 0) {

						if (!channel.getRuntimeCaches().isEmpty()) {
							// 从上一个缓存数据中取值
							lastData = channel.getRuntimeCaches().get(channel.getRuntimeCaches().size() - 1);
						}
					} else {

						lastData = channelDatas.get(n - 1);
					}

					if (lastData != null) {
						ChannelData insertData = processTimeContinue(channel, lastData, chnData, step);

						if (insertData != null) {
							// 记录最初要插入的位置
							int origIndex = copyList.indexOf(chnData);
							copyList.add(origIndex, insertData);
						}
					}

					if (step.workMode == WorkMode.CCC || step.workMode == WorkMode.CCD) {

						Step nextStep = channel.getProcedureStep(chnData.getStepIndex() + 1);

						if (nextStep != null && nextStep.workMode == WorkMode.CVC && step.workMode == WorkMode.CCC) {

							// 为cc-cv步次
							if (chnData.getVoltage() > step.specialVoltage
									+ MainBoard.startupCfg.getRange().voltagePrecision) {

								String info = "chn-" + (channel.getDeviceChnIndex() + 1) + " cv over volt:"
										+ CommonUtil.formatNumber(chnData.getVoltage(), 1) + " -> ";
								// 转cv出现过压，开始处理
								if (!chnData.isImportantData()) {

									double voltage = processConstantVoltageData(channel, step.specialVoltage,
											chnData.getVoltage());
									chnData.setVoltage(voltage);
									info += CommonUtil.formatNumber(voltage, 1);
								}

								logger.info(info);
							}

						}

						if (!chnData.isImportantData()) {
							// deal
							double current = processConstCurrentData(step.specialCurrent, chnData.getCurrent());
							chnData.setCurrent(current);

							if (MainBoard.startupCfg.getRange().useCcVoltageFilter && step.workMode == WorkMode.CCC) {
								processCCVoltage(channel, chnData, step);

							} else if (MainBoard.startupCfg.getRange().useDcVoltageFilter
									&& step.workMode == WorkMode.CCD) {

								processDCVoltage(channel, chnData, step);
							}
						}

					} else if (step.workMode == WorkMode.CVC) {

						// deal voltage
						if (!chnData.isImportantData()) {
							double voltage = processConstantVoltageData(channel, step.specialVoltage,
									chnData.getVoltage());
							chnData.setVoltage(voltage);

							if (MainBoard.startupCfg.getRange().useCvCurrentFilter) {

								processCvCurrent(channel, chnData, step);

							}
						}

					} else if (step.workMode == WorkMode.CC_CV) {

						if (isCvInCcCv(step, channel, chnData)) {

							/**
							 * 标记已经进入cv
							 */
							channel.setCvInCccvMode(true);
							if (!chnData.isImportantData()) {

								double voltage = processConstantVoltageData(channel, step.specialVoltage,
										chnData.getVoltage());
								chnData.setVoltage(voltage);

								if (channel.getCccvDeviceVoltage() > 0) {

									double offset = new Random().nextDouble() / 2;
									if (new Random().nextBoolean()) {

										offset = -offset;
									}

									chnData.setDeviceVoltage(channel.getCccvDeviceVoltage() + offset);
								}

								if (MainBoard.startupCfg.getRange().useCvCurrentFilter) {

									processCvCurrent(channel, chnData, step);

								}
							}

						} else {

							if (!chnData.isImportantData()) {
								double current = processConstCurrentData(step.specialCurrent, chnData.getCurrent());
								chnData.setCurrent(current);
								if (MainBoard.startupCfg.getRange().useCcVoltageFilter) {

									processCCVoltage(channel, chnData, step);
								}

							}

						}
					} else if (step.workMode == WorkMode.SLEEP) {

						if (!chnData.isImportantData()) {
							if (MainBoard.startupCfg.getRange().useSlpVoltFilter) {
								processSleepVoltage(channel, chnData);
							}

							processSleepReduntData(channel, chnData);
						}

					}

				}

				// 更新上一个数据
				lastDataBuff.put(channel, chnData);

				if (chnData.getState() != ChnState.RUN && channel.getState() == ChnState.RUN) {

					// 通道被关闭，清空缓存
					clearData(channel);

				}

			}
		} else if (channel.getState() != ChnState.NONE) {

			if (MainBoard.startupCfg.getRange().useSlpVoltFilter) {
				for (ChannelData chnData : channelDatas) {
					processSleepVoltage(channel, chnData);
				}
			}
		}

		return copyList;
		/**
		 * 处理休眠冗余数据;删除多余的休眠缓起数据
		 */

		// return processSleepReduntData(channel,channelDatas);

	}

	public static RangeSection findSectionByCurrent(double current) {

		Range range = MainBoard.startupCfg.getRange();
		for (RangeSection section : range.sections) {

			if (current >= section.lower && current < section.upper) {

				return section;

			}
		}
		return null;

	}

	/**
	 * 处理异常偏差过大的功率电压
	 * 
	 * @author wavy_zheng 2021年5月27日
	 * @param channelData
	 * @param resister
	 *            理想接触电阻
	 */
	private void processPowerVoltageData(Channel channel, ChannelData channelData) {

		if (channelData.getCurrent() > 0) {

			double offsetV = Math.abs(channelData.getDeviceVoltage() - channelData.getPowerVoltage());
			double offsetR = offsetV / channelData.getCurrent() * 1000;
			ParameterName pn = channel.getControlUnit().getCurrentPn();

			if (pn == null) {

				return;
			}
			if (pn.getCheckVoltProtect().getResisterOffset() == 0) {

				pn.getCheckVoltProtect()
						.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
			}
			/*
			 * if (offsetR > pn.getCheckVoltProtect().getResisterOffset()) {
			 * 
			 * return; }
			 */
			if (channelData.getVoltage() >= 2800 && channelData.getDeviceVoltage() >= 2800
					&& channelData.getPowerVoltage() >= 2800) {

				return;
			}

			// 开始处理备份电压
			processBackVoltageData(channelData, MainBoard.startupCfg.getBackupBoard().voltThreshold,
					MainBoard.startupCfg.getBackupBoard().offset);

			if (offsetR > MainBoard.startupCfg.getBackupBoard().resister) {

				offsetR = MainBoard.startupCfg.getBackupBoard().resister;
				offsetR = CommonUtil.produceRandomNumberInRange(offsetR, MainBoard.startupCfg.getBackupBoard().offset);
			}

			// 反向计算功率电压
			if (channelData.getWorkMode() == WorkMode.CCD) {

				// 功率电压 < 备份电压
				double powerVoltage = channelData.getDeviceVoltage() - offsetR / 1000 * channelData.getCurrent();
				channelData.setPowerVoltage(powerVoltage);

			} else if (channelData.getWorkMode() == WorkMode.CCC || channelData.getWorkMode() == WorkMode.CC_CV
					|| channelData.getWorkMode() == WorkMode.CVC) {

				double powerVoltage = offsetR * channelData.getCurrent() / 1000 + channelData.getDeviceVoltage();
				channelData.setPowerVoltage(powerVoltage);
			}

		} else {

			// 处理休眠状态的电压

			processBackVoltageData(channelData, MainBoard.startupCfg.getBackupBoard().voltThreshold,
					MainBoard.startupCfg.getBackupBoard().offset);

			channelData.setPowerVoltage(CommonUtil.produceRandomNumberInRange(channelData.getVoltage(),
					MainBoard.startupCfg.getBackupBoard().offset));

		}

	}

	/**
	 * 处理备份电压
	 * 
	 * @author wavy_zheng 2021年5月27日
	 * @param destVoltage
	 * @param range
	 * @return
	 */
	private void processBackVoltageData(ChannelData channelData, double range, double offset) {

		if (channelData.getDeviceVoltage() == 0) {

			channelData.setDeviceVoltage(CommonUtil.produceRandomNumberInRange(channelData.getVoltage(), offset));
		}

		if (Math.abs(channelData.getVoltage() - channelData.getDeviceVoltage()) > range) {

			channelData.setDeviceVoltage(CommonUtil.produceRandomNumberInRange(channelData.getVoltage(), offset));
		}

	}

	private double processConstantVoltageData(Channel channel, double programVoltage, double voltage) {

		Range range = MainBoard.startupCfg.getRange();
		if (!range.use || range.voltageFilterRange == 0 || range.voltagePrecision == 0) {

			return voltage;
		}

		// 特调电压恒压特殊处理
		if (MainBoard.startupCfg.getRange().adjustCcCvVoltage && channel.isCvInCccvMode()) {

			channel.log("cv actual voltage:" + voltage + ",program v:" + programVoltage);
			voltage = programVoltage + new Random().nextDouble() / 4;

		} else {

			// 确定精度范围
			if (Math.abs(voltage - programVoltage) <= range.voltageFilterRange) {

				// 至少保证过滤一次
				do {
					voltage = programVoltage + (programVoltage - voltage) / 4;
				} while (Math.abs(voltage - programVoltage) > range.voltagePrecision);

				return voltage;
			}
		}

		return voltage;

	}

	/**
	 * 纠正逻辑板已完成的步次数据问题
	 * 
	 * @author wavy_zheng 2021年4月2日
	 * @param channel
	 * @param channelData
	 */
	private void adjustCompleteData(Channel channel, ChnData channelData) {

		if (channelData.getState() == Logic2Environment.ChnState.COMPLETE && channel.getState() == ChnState.RUN) {

			int stepCount = channel.getCurrentProcedure().getStepCount();
			if (channelData.getStepIndex() != stepCount) {

				channel.log("error stepIndex , procedure end step index : " + stepCount
						+ " ,exception complete raw data :" + channelData);
				channelData.setStepIndex(stepCount);
				channelData.setWorkMode(DataProviderService.convertFrom(channel.getProcedureStep(stepCount).workMode));
			}

		}

	}

	/**
	 * 修正截止电压与设定值偏差过大的问题
	 * 
	 * @author wavy_zheng 2021年3月5日
	 * @param channel
	 * @param channelData
	 */
	private void adjustEndVoltage(Channel channel, ChannelData channelData) {

		if (channel.getState() != ChnState.RUN) {

			return;
		}
		Step step = channel.getProcedureStep(channelData.getStepIndex());
		if (step != null) {

			Random random = new Random();
			double offset = THRESHOLD_VOLTAGE_OFFSET * random.nextDouble();

			switch (channelData.getWorkMode()) {

			case CCC:
				if (step.overThreshold > 0
						&& channelData.getVoltage() - step.overThreshold > THRESHOLD_VOLTAGE_OFFSET) {

					System.out.println("adjust voltage = " + (step.overThreshold + offset) + " threshold = "
							+ step.overThreshold + ",raw voltage = " + channelData.getVoltage());
					System.out.println(channelData);
					// 在范围内取随机数
					channelData.setVoltage(step.overThreshold + offset);
				}
				break;
			case CCD:
				if (step.overThreshold > 0
						&& step.overThreshold - channelData.getVoltage() > THRESHOLD_VOLTAGE_OFFSET) {

					System.out.println("adjust voltage = " + (step.overThreshold - offset) + " threshold = "
							+ step.overThreshold + ",raw voltage = " + channelData.getVoltage());
					System.out.println(channelData);
					channelData.setVoltage(step.overThreshold - offset);
				}
				break;

			}
		}

	}

	/**
	 * 特殊处理IC的微小电流
	 * 
	 * @author wavy_zheng 2021年3月5日
	 * @param programCurrent
	 * @param current
	 * @return
	 */
	private void adjustICMicroCurrent(Channel channel, ChannelData channelData) {

		if (channel.getState() != ChnState.RUN) {

			return;
		}
		Step step = channel.getProcedureStep(channelData.getStepIndex());
		if (step != null
				&& (channelData.getWorkMode() == WorkMode.CCC || channelData.getWorkMode() == WorkMode.CC_CV)) {

			if (channelData.getCurrent() > 0 && step.specialCurrent <= MainBoard.startupCfg.getRange().microCurr
					&& channelData.getVoltage() <= MainBoard.startupCfg.getRange().microVolt) {

				// 确定精度范围
				RangeSection rs = findSectionByCurrent(step.specialCurrent);
				if (rs != null) {

					double current = CommonUtil.produceRandomNumberInRange(step.specialCurrent, rs.precision);
					channelData.setCurrent(current);
				}

			}
		}

	}

	private double processConstCurrentData(double programCurrent, double current) {

		Range range = MainBoard.startupCfg.getRange();
		if (!range.use) {

			return current;
		}

		// 确定精度范围
		RangeSection rs = findSectionByCurrent(current);

		if (rs != null && rs.filter) {

			double filterRange = programCurrent * rs.currentFilterPercent / 100 > rs.currentFilterRange
					? programCurrent * rs.currentFilterPercent / 100
					: rs.currentFilterRange;

			if (Math.abs(current - programCurrent) <= filterRange) {

				// 至少保证过滤一次
				do {
					current = programCurrent + (programCurrent - current) / 4;
				} while (Math.abs(current - programCurrent) > rs.precision);

				return current;
			}

		}

		return current;

	}

	public void clearDcVoltageCaches(Channel channel) {

		dcVoltageFilterBuff.remove(channel);
	}

	public void clearRawDcVoltageCaches(Channel channel) {

		dcRawVoltageFilterBuff.remove(channel);

	}

	public void clearCcVoltageCaches(Channel channel) {

		ccVoltageFilterBuff.remove(channel);

	}

	public void clearRawCcVoltageCaches(Channel channel) {

		ccRawVoltageFilterBuff.remove(channel);
	}

	public void clearRawCvCurrentCaches(Channel channel) {

		cvRawCurrentFilterBuff.remove(channel);
	}

	public void clearCvCurrentCaches(Channel channel) {

		cvCurrentFilterBuff.remove(channel);
	}

	public void clearSlpVoltCaches(Channel channel) {

		slpVoltFilterBuff.remove(channel);
	}

	/**
	 * 恒压电流处理
	 * 
	 * @author wavy_zheng 2022年6月23日
	 * @param channel
	 * @param chnData
	 */
	private void processCvCurrentEx(Channel channel, ChannelData chnData, Step step) {

		List<ChannelData> list = cvCurrentFilterBuff.get(channel);

		if (list != null) {
			// 判断是否发生了转步
			for (ChannelData data : list) {

				if (data.getStepIndex() != chnData.getStepIndex() || data.getLoopIndex() != chnData.getLoopIndex()) {

					clearCvCurrentCaches(channel);
					break;
				}
			}
		}

		if (list == null || list.size() < CV_CURRENT_FILTER_COUNT) {

			if (list == null) {

				list = new ArrayList<>();
				list = Collections.synchronizedList(list);
				cvCurrentFilterBuff.put(channel, list);
			}

			list.add(chnData);

			return;
		}

		if (chnData.getCurrent() > step.getOverThreshold()) {

			double avg = 0, std = 0, avgOffset = 0, stdOffset = 0;

			for (int n = 0; n < list.size(); n++) {

				avg += list.get(n).getCurrent();

			}
			avg = avg / list.size();
			for (int n = 0; n < list.size(); n++) {

				std += Math.pow(list.get(n).getCurrent() - avg, 2);

			}
			// 计算出标准电流差
			std = Math.sqrt(std);

			List<Double> offsetList = new ArrayList<>();
			for (int n = 1; n < list.size(); n++) {

				double offset = list.get(n - 1).getCurrent() - list.get(n).getCurrent();
				offsetList.add(offset);
				avgOffset += offset;
			}
			avgOffset = avgOffset / offsetList.size();

			for (int n = 0; n < offsetList.size(); n++) {

				stdOffset += Math.pow(offsetList.get(n) - avgOffset, 2);

			}
			stdOffset = Math.sqrt(stdOffset);

			// 计算极差
			double maxOffset = std * 3.5;

			if (maxOffset > 0 && (list.get(list.size() - 1).getCurrent() - chnData.getCurrent() < 0
					|| list.get(list.size() - 1).getCurrent() - chnData.getCurrent() > maxOffset)) {

				// 需要过滤电流
				double offset = offsetList.get(offsetList.size() - 1);
				if (offset < 0) {

					offset = 0;
				}
				if (offset > 0) {

					chnData.setCurrent(chnData.getCurrent() - offset);
					if (chnData.getCurrent() < 0) {

						chnData.setCurrent(0);
					}
				} else {

					// 当出现电流往上抖动时，采用上一次电流数据
					chnData.setCurrent(list.get(list.size() - 1).getCurrent());

				}

			}
		}

		list.add(chnData);
		if (list.size() >= CV_CURRENT_FILTER_COUNT) {
			list.remove(0);
		}

	}

	/**
	 * 
	 * 在刚充完电或放完电这一段时间内，主控只纠正个别异常电压跳点，其他电压数据不做处理
	 * 
	 * @author wavy_zheng 2022年6月10日
	 * @param channel
	 * @param chnData
	 * @param charge
	 * @return true 表示已经进入稳定期
	 */
	private void processChangeSlpVolt(Channel channel, ChannelData chnData) {

		double upperVoltage = 0, lowerVoltage = 0;
		if (channel.getState() == ChnState.RUN) {

			// 获取前一步
			StepAndLoop sal = LogicBoard.skipPreviousStep(channel.getCurrentProcedure(), chnData.getStepIndex(),
					chnData.getLoopIndex());
			if (sal.nextStep != chnData.getStepIndex() || sal.nextLoop != chnData.getLoopIndex()) {

				Step previousStep = channel.getProcedureStep(sal.nextStep);

				if (previousStep.getWorkMode() == WorkMode.CCC) {

					upperVoltage = previousStep.overThreshold;
				} else if (previousStep.getWorkMode() == WorkMode.CCD) {

					lowerVoltage = previousStep.overThreshold;
				} else if (previousStep.getWorkMode() == WorkMode.CC_CV) {

					upperVoltage = previousStep.specialVoltage;
				}

			}

		}

		List<ChannelData> list = slpVoltFilterBuff.get(channel);
		if (list == null || list.size() < SLP_VOLT_FILTER_COUNT) {

			if (list == null) {

				list = new ArrayList<>();
				slpVoltFilterBuff.put(channel, list);
			}

			if (upperVoltage > 0 && chnData.getVoltage() > upperVoltage) {
				// 当充电结束时，休眠电压如果比充电截止电压高，则处理

				chnData.setVoltage(upperVoltage - new Random().nextDouble() / 3);

			} else if (lowerVoltage > 0 && chnData.getVoltage() < lowerVoltage) {

				// 当放电结束时，休眠电压如果比充电截止电压低，则处理
				chnData.setVoltage(lowerVoltage + new Random().nextDouble() / 3);

			}

			list.add(chnData);

			return;
		}

		if (list != null) {

			if (channel.getStepIndex() != chnData.getStepIndex() || channel.getLoopIndex() != chnData.getLoopIndex()) {

				clearSlpVoltCaches(channel);
				return;
			}

		}

		if (upperVoltage > 0 && chnData.getVoltage() > upperVoltage) {
			// 当充电结束时，休眠电压如果比充电截止电压高，则处理

			chnData.setVoltage(upperVoltage - new Random().nextDouble() / 3);

		} else if (lowerVoltage > 0 && chnData.getVoltage() < lowerVoltage) {

			// 当放电结束时，休眠电压如果比充电截止电压低，则处理
			chnData.setVoltage(lowerVoltage + new Random().nextDouble() / 3);

		} else {

			double avg = 0, std = 0;

			for (int n = 0; n < list.size(); n++) {

				avg += list.get(n).getVoltage();

			}
			avg = avg / list.size();
			for (int n = 0; n < list.size(); n++) {

				std += Math.pow(list.get(n).getVoltage() - avg, 2);

			}
			std = Math.sqrt(std);

			// System.out.println("std = " + std);

			// 计算极差
			double maxOffset = std * 3.5;

			double lastVolt = list.get(list.size() - 1).getVoltage();

			if (maxOffset > 0 && Math.abs(chnData.getVoltage() - lastVolt) > maxOffset) {

				chnData.setVoltage(lastVolt);
			}

		}

		/**
		 * 正常迭代数据
		 */
		list.add(chnData);
		list.remove(0);

	}

	/**
	 * 恒定休眠电压处理
	 * 
	 * @author wavy_zheng 2022年6月10日
	 * @param channel
	 * @param chnData
	 */
	private void processConstSlpVolt(Channel channel, ChannelData chnData) {

		List<ChannelData> list = slpVoltFilterBuff.get(channel);
		if (list == null || list.size() < SLP_VOLT_FILTER_COUNT) {

			if (list == null) {

				list = new ArrayList<>();
				slpVoltFilterBuff.put(channel, list);
			}

			list.add(chnData);

			return;
		}

		if (list != null) {

			if (channel.getStepIndex() != 1 && channel.getLoopIndex() != 1) {
				if (channel.getStepIndex() != chnData.getStepIndex()
						|| channel.getLoopIndex() != chnData.getLoopIndex()) {

					System.out.println("clear slp volt caches");
					clearSlpVoltCaches(channel);
					return;
				}
			}

		}

		// 算出平均数
		double avg = 0;
		List<Double> datalist = new ArrayList<>();
		for (int n = 0; n < list.size(); n++) {

			datalist.add(list.get(n).getVoltage());
		}
		// 去头尾

		Collections.sort(datalist);

		for (int n = 1; n < datalist.size() - 1; n++) {

			avg += datalist.get(n);
		}

		avg = avg / (datalist.size() - 2);

		// 添加尾数
		list.add(chnData);

		if (Math.abs(chnData.getVoltage() - avg) > 0.2) {

			Random random = new Random();
			// 更新新的电流值
			chnData.setVoltage(avg + (random.nextBoolean() ? -random.nextDouble() * 0.1 : random.nextDouble() * 0.1));
		}
		// 更新缓存
		list.remove(0);

	}

	/**
	 * 处理第一条数据
	 * 
	 * @author wavy_zheng 2022年7月28日
	 * @param channel
	 * @param chnData
	 */
	private void processFirstData(Channel channel, ChannelData chnData) {

		if (channel.getDeviceChnIndex() == 0) {

			System.out.println(
					"channel stepIndex = " + channel.getStepIndex() + ",chnData stepIndex = " + chnData.getStepIndex());
		}

		if (lastDataBuff.get(channel) == null) {

			return;
		}

		if (lastDataBuff.get(channel).getStepIndex() != chnData.getStepIndex()
				|| lastDataBuff.get(channel).getLoopIndex() != chnData.getLoopIndex()) {

			if (chnData.getWorkMode() == WorkMode.CC_CV || chnData.getWorkMode() == WorkMode.CCC) {

				// s获取上一条数据

				if (chnData.getVoltage() < lastDataBuff.get(channel).getVoltage()) {

					// cc第一条电压异常，出现下降
					chnData.setVoltage(lastDataBuff.get(channel).getVoltage() + new Random().nextDouble() / 5);
				}

			} else if (chnData.getWorkMode() == WorkMode.CCD) {

				if (chnData.getVoltage() > lastDataBuff.get(channel).getVoltage()) {

					chnData.setVoltage(lastDataBuff.get(channel).getVoltage() - new Random().nextDouble() / 5);
				}
			} else if (chnData.getWorkMode() == WorkMode.SLEEP) {

				// 前1个工步为充电模式,休眠第一条
				if (lastDataBuff.get(channel).getWorkMode() == WorkMode.CCC
						|| lastDataBuff.get(channel).getWorkMode() == WorkMode.CC_CV
						|| lastDataBuff.get(channel).getWorkMode() == WorkMode.CVC) {

					if (chnData.getVoltage() > lastDataBuff.get(channel).getVoltage()) {

						// 修改异常电压
						chnData.setVoltage(lastDataBuff.get(channel).getVoltage() - new Random().nextDouble() / 5);
					}
				} else if (lastDataBuff.get(channel).getWorkMode() == WorkMode.CCD) {

					if (chnData.getVoltage() < lastDataBuff.get(channel).getVoltage()) {

						chnData.setVoltage(lastDataBuff.get(channel).getVoltage() + new Random().nextDouble() / 5);
					}
				}

			}

		}

	}

	/**
	 * 当下位机缓起时，可能会产生超过休眠时间多余的数据，主控需要进行拦截
	 * 
	 * @author wavy_zheng 2022年7月13日
	 * @param channel
	 * @param chnData
	 */
	private void processSleepReduntData(Channel channel, ChannelData chnData) {

		Step step = channel.getProcedureStep(chnData.getStepIndex());
		if (step != null && step.workMode == WorkMode.SLEEP) {

			if (chnData.getTimeStepSpend() > step.overTime) {

				if (channel.getSleepLastData() != null) {

					chnData.setVoltage(channel.getSleepLastData().getVoltage());
					chnData.setDeviceVoltage(channel.getSleepLastData().getDeviceVoltage());
					chnData.setPowerVoltage(channel.getSleepLastData().getPowerVoltage());

				}

			} else {

				channel.setSleepLastData(chnData);
			}

		}

	}

	/**
	 * 处理时间连续性
	 * 
	 * @author wavy_zheng 2022年8月8日
	 * @param channel
	 * @param chnData
	 */
	public ChannelData processTimeContinue(Channel channel, ChannelData lastData, ChannelData chnData, Step step) {

		// 如果以主控时间作为基准采样，则需要根据流程步次采样记录间隔校准
		if (chnData.getStepIndex() > 0 && chnData.getLoopIndex() > 0) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(chnData.getDate());
			cal.set(Calendar.MILLISECOND, 0);
			Date n1 = cal.getTime();
			cal.setTime(lastData.getDate());
			cal.set(Calendar.MILLISECOND, 0);
			Date n2 = cal.getTime();

			System.out.println(CommonUtil.formatTime(n1, "yyyy-MM-dd HH:mm:ss") + "  "
					+ CommonUtil.formatTime(n2, "yyyy-MM-dd HH:mm:ss"));
			if (step.recordTime > 0 && n1.getTime() - n2.getTime() > step.recordTime * 1000) {

				// 出现丢点
				int offset = (int) ((n1.getTime() - n2.getTime()) / 1000);

				for (int n = step.recordTime; n < offset; n++) {

					if (n < step.recordTime + 2) { // 最多塞入两条，超过2条采集出现问题，不处理
						// 塞入一个数据
						try {
							ChannelData cloneData = (ChannelData) chnData.clone();
							cloneData.setVoltage(cloneData.getVoltage() + new Random().nextDouble() / 4);
							cloneData.setDate(new Date(n2.getTime() + step.recordTime * 1000));
							channel.log("lost record data time, adjust time!:" + cloneData);

							return cloneData;

						} catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}

		}

		return null;

	}

	/**
	 * 对休眠电压做平滑滤波处理
	 * 
	 * @author wavy_zheng 2022年6月9日
	 * @param channel
	 * @param chnData
	 */
	public void processSleepVoltage(Channel channel, ChannelData chnData) {

		// List<ChannelData> list = slpVoltFilterBuff.get(channel);

		StepAndLoop sal = null;
		boolean notRun = false;
		if (chnData.getState() == ChnState.RUN) {
			sal = LogicBoard.skipPreviousStep(channel.getCurrentProcedure(), chnData.getStepIndex(),
					chnData.getLoopIndex());

			if (sal.nextLoop == chnData.getLoopIndex() && sal.nextStep == chnData.getStepIndex()) {

				// 待测
				notRun = true;
			} else {

				if (sal.nextStep > 0) {

					Step previousStep = channel.getCurrentProcedure().getStep(sal.nextStep - 1);
					if (previousStep.getWorkMode() == WorkMode.SLEEP) {

						notRun = true;
					}

				} else {

					notRun = true;
				}

			}

		} else {

			notRun = false;
		}

		if (notRun) {

			// if(channel.getDeviceChnIndex() == 3) {
			// System.out.println("processConstSlpVolt = " );
			// }
			processConstSlpVolt(channel, chnData);

		} else {

			// if(channel.getDeviceChnIndex() == 3) {
			// System.out.println("processChangeSlpVolt = " );
			// }
			processChangeSlpVolt(channel, chnData);

		}

	}

	/**
	 * 过滤CC电压
	 * 
	 * @author wavy_zheng 2022年7月23日
	 * @param channel
	 * @param chnData
	 * @param step
	 */
	public void processCCVoltage(Channel channel, ChannelData chnData, Step step) {

		List<ChannelData> list = ccVoltageFilterBuff.get(channel);
		double actualVolt = chnData.getVoltage();
		channel.log("actual voltage :" + actualVolt);
		if (ccRawVoltageFilterBuff.get(channel) == null) {

			ccRawVoltageFilterBuff.put(channel, Collections.synchronizedList(new ArrayList<>()));
		}
		try {
			ccRawVoltageFilterBuff.get(channel).add((ChannelData) chnData.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int rawCount = 5;
		boolean exception = false;
		boolean overUpper = false;
		if (ccRawVoltageFilterBuff.get(channel).size() >= rawCount + 1) {

			// 如果电压出现连续5次下降，则认为数据已异常，屏蔽过滤
			List<ChannelData> buff = ccRawVoltageFilterBuff.get(channel);
			int count = 0, overCount = 0;
			for (int n = 0; n < buff.size() - 1; n++) {

				if (buff.get(n).getVoltage() - buff.get(n + 1).getVoltage() >= 2.0) {

					count++;
				}
				if (buff.get(n).getVoltage() > step.specialVoltage) {

					overCount++;
				}
			}

			if (count >= rawCount) {

				exception = true;
			}

			if (overCount >= rawCount) {

				overUpper = true;
			}

		}

		if (list == null || list.size() < FILTER_COUNT) {

			if (list == null) {

				list = new ArrayList<>();
				list = Collections.synchronizedList(list);
				ccVoltageFilterBuff.put(channel, list);
			}

			list.add(chnData);

			if (MainBoard.startupCfg.isUseErrorPick()) {

				// 模拟错误AD采样时序
				if (list.size() > 1) {

					list.subList(1, list.size()).clear();
				}
				chnData.setVoltage(list.get(0).getVoltage());

			}

			return;
		}

		if (chnData.getVoltage() > step.specialVoltage + 0.5 && step.getWorkMode() == WorkMode.CC_CV) {

			// 对电压做处理
			double voltage = processConstantVoltageData(channel, step.specialVoltage, chnData.getVoltage());
			chnData.setVoltage(voltage);
			// 备份电压同步处理
			channel.log("set device voltage " + chnData.getDeviceVoltage() + " -> "
					+ (list.get(list.size() - 1).getDeviceVoltage() + new Random().nextDouble() / 2));
			chnData.setDeviceVoltage(list.get(list.size() - 1).getDeviceVoltage());
			overUpper = true;

			channel.setCccvDeviceVoltage(chnData.getDeviceVoltage());

			// 强行转成CV
			channel.setCvInCccvMode(true);
			channel.log("enter cv mode");

		}

		// 充电电压异常往下抖动
		boolean voltageWaveDown = false;

		if (list.get(list.size() - 1).getVoltage() - chnData.getVoltage() >= 0.20) {

			voltageWaveDown = true;
		}

		// 计算最大单次电压升差
		double maxAscVolt = 0;
		for (int n = 0; n < list.size() - 1; n++) {

			if (maxAscVolt == 0) {

				maxAscVolt = list.get(n + 1).getVoltage() - list.get(n).getVoltage();
			} else {

				if (maxAscVolt < list.get(n + 1).getVoltage() - list.get(n).getVoltage()) {

					maxAscVolt = list.get(n + 1).getVoltage() - list.get(n).getVoltage();
				}
			}

		}

		double lastOffset = chnData.getVoltage() - list.get(list.size() - 1).getVoltage();

		// 添加尾数
		list.add(chnData);

		// 截止电压不做处理
		if ((chnData.getVoltage() >= step.getOverThreshold() && step.getWorkMode() == WorkMode.CCC)
				|| chnData.getVoltage() >= step.specialVoltage) {

			channel.log("voltage " + chnData.getVoltage() + " > " + step.getOverThreshold());
			overUpper = true;
		}
		double val = 0;

		/**
		 * 防止出现list出现超过3个的情况
		 */
		if (list.size() > FILTER_COUNT + 1) {

			list.subList(0, list.size() - FILTER_COUNT - 1).clear();

		}
		if (!exception && !overUpper) {

			for (int n = 0; n < list.size(); n++) {

				/**
				 * 过滤掉电压下降波和上升巨大的波
				 */

				if (n < 3) {
					if (voltageWaveDown || (maxAscVolt > 0 && lastOffset > maxAscVolt * 5)) {

						if (n == 0) {

							val += list.get(n).getVoltage() * 0.00;

						} else if (n == 1) {

							val += list.get(n).getVoltage() * 1.00000;

						} else {

							val += list.get(n).getVoltage() * 0.00;
						}

					} else {

						if (n == 0) {

							val += list.get(n).getVoltage() * 0.0;
						} else if (n == 1) {

							val += list.get(n).getVoltage() * 0.30;
						} else {

							val += list.get(n).getVoltage() * 0.70;
						}

					}
				}
			}

			channel.log("cc voltage:" + chnData.getVoltage() + " -> " + val);
			// 更新新的电压值
			chnData.setVoltage(val);

		}

		if (list.size() > FILTER_COUNT) {
			// 更新缓存
			list.remove(0);
		}
		if (ccRawVoltageFilterBuff.get(channel).size() >= rawCount + 3) {

			ccRawVoltageFilterBuff.remove(0);
		}

	}

	/**
	 * 对DC电压进行平滑滤波处理
	 * 
	 * @author wavy_zheng 2022年7月23日
	 * @param channel
	 * @param chnData
	 * @param step
	 */
	public void processDCVoltage(Channel channel, ChannelData chnData, Step step) {

		List<ChannelData> list = dcVoltageFilterBuff.get(channel);
		double actualVolt = chnData.getVoltage();
		channel.log("actual voltage :" + actualVolt);

		if (dcRawVoltageFilterBuff.get(channel) == null) {

			dcRawVoltageFilterBuff.put(channel, Collections.synchronizedList(new ArrayList<>()));
		}

		if (list == null || list.size() < FILTER_COUNT) {

			if (list == null) {

				list = new ArrayList<>();
				list = Collections.synchronizedList(list);
				dcVoltageFilterBuff.put(channel, list);
			}

			/**
			 * 当样本数据电压出现上升时，则做处理
			 */
			if (!list.isEmpty()) {
				if (chnData.getVoltage() > list.get(list.size() - 1).getVoltage()) {

					chnData.setVoltage(list.get(list.size() - 1).getVoltage() - 0.5);
				}
			}

			list.add(chnData);

			if (MainBoard.startupCfg.isUseErrorPick()) {

				// 模拟错误AD采样时序
				if (list.size() > 1) {

					list.subList(1, list.size()).clear();
				}
				chnData.setVoltage(list.get(0).getVoltage());

			}

			return;
		}

		// 放电电压异常往上抖动
		boolean voltageWaveUp = false;

		if (chnData.getVoltage() - list.get(list.size() - 1).getVoltage() >= 0.2) {

			voltageWaveUp = true;
		}

		// 计算最大单次电压升差
		double maxDescVolt = 0;
		for (int n = 0; n < list.size() - 1; n++) {

			if (maxDescVolt == 0) {

				maxDescVolt = list.get(n).getVoltage() - list.get(n + 1).getVoltage();
			} else {

				if (maxDescVolt > 0 && maxDescVolt < list.get(n).getVoltage() - list.get(n + 1).getVoltage()) {

					maxDescVolt = list.get(n).getVoltage() - list.get(n + 1).getVoltage();
				}
			}

		}

		double lastOffset = list.get(list.size() - 1).getVoltage() - chnData.getVoltage();

		// 添加尾数
		list.add(chnData);

		int rawCount = 5;
		boolean exception = false;

		if (dcRawVoltageFilterBuff.get(channel).size() >= rawCount + 1) {

			// 如果电压出现连续5次上升，则认为数据已异常，屏蔽过滤
			List<ChannelData> buff = dcRawVoltageFilterBuff.get(channel);
			int count = 0;

			for (int n = 0; n < buff.size() - 1; n++) {

				if (buff.get(n + 1).getVoltage() - buff.get(n).getVoltage() >= 2.0) {

					count++;
				}

			}

			if (count >= rawCount) {

				exception = true;
			}

		}

		// 截止电压不做处理
		if (chnData.getVoltage() <= step.getOverThreshold()) {

			System.out.println("voltage " + chnData.getVoltage() + " < " + step.getOverThreshold());
			exception = true;
		}
		double val = 0;

		/**
		 * 防止出现list出现超过3个的情况
		 */
		if (list.size() > FILTER_COUNT + 1) {

			list.subList(0, list.size() - FILTER_COUNT - 1).clear();
			;

		}

		StringBuffer info = new StringBuffer();

		if (!exception) {
			for (int n = 0; n < list.size(); n++) {

				/**
				 * 过滤掉上升电压波和下降巨大的波
				 */

				if (n < 3) {

					if (voltageWaveUp || (maxDescVolt > 0 && lastOffset > maxDescVolt * 5)) {

						if (n == 0) {

							val += list.get(n).getVoltage() * 0.00;
							info.append(list.get(n).getVoltage() + " * 0.03 + ");

						} else if (n == 1) {

							val += list.get(n).getVoltage() * 1.0;
							info.append(list.get(n).getVoltage() + " * 0.9 + ");

						} else {

							val += list.get(n).getVoltage() * 0.00;
							info.append(list.get(n).getVoltage() + " * 0.00 = ");
						}

					} else {

						if (n == 0) {

							val += list.get(n).getVoltage() * 0.0;
							info.append(list.get(n).getVoltage() + " * 0.0 + ");
						} else if (n == 1) {

							val += list.get(n).getVoltage() * 0.30;
							info.append(list.get(n).getVoltage() + " * 0.20 + ");
						} else {

							val += list.get(n).getVoltage() * 0.70;
							info.append(list.get(n).getVoltage() + " * 0.80 = ");
						}

					}
				}
			}

			// channel.log("formula:" + info.toString() + val);
			// channel.log("process dc voltage " + chnData.getVoltage() + " -> " + val);

			// 更新新的电流值
			chnData.setVoltage(val);

		}

		if (list.size() > FILTER_COUNT) {
			// 更新缓存
			list.remove(0);
		}

		if (dcRawVoltageFilterBuff.get(channel).size() >= rawCount + 3) {

			dcRawVoltageFilterBuff.get(channel).remove(0);
		}

	}

	/**
	 * 根据恒流值进行模拟伪造一份CV电流
	 * 
	 * @author wavy_zheng 2022年8月4日
	 * @param channel
	 * @param chnData
	 * @param step
	 * @return
	 */
	private double makeCvCurrent(Channel channel, ChannelData chnData, Step step) {

		double precide = 30.0;
		RangeSection rs = findSectionByCurrent(step.specialCurrent);
		if (rs != null) {

			precide = rs.currentFilterRange;
		}

		if (Math.abs(chnData.getCurrent() - step.specialCurrent) < precide) {

			// 模拟CV电流第一次大幅度下降
			return chnData.getCurrent() * 0.9777;
		} else if (chnData.getCurrent() > step.specialCurrent / 2) {

			return chnData.getCurrent() * 0.99997;
		} else if (channel.getCurrent() <= step.specialCurrent / 2 && channel.getCurrent() > step.specialCurrent / 4) {

			double rate = 0.9999997;
			return chnData.getCurrent() * rate;

		} else {

			double rate = 0.9999997;
			// double r = (chnData.getCurrent() - step.overThreshold) / (step.specialCurrent
			// - step.overThreshold);
			double offset = chnData.getCurrent() - chnData.getCurrent() * rate;
			offset = (new Random().nextBoolean() ? -1 : 1) * offset; // 在快接近截止电流时，使用随机微小震动来抑制过快下降
			return chnData.getCurrent() + offset;
		}

		// current = CommonUtil.produceRandomNumberInRange(current, CURRENT_PRECISION);

	}

	/**
	 * 对CV电流进行平滑处理
	 * 
	 * @author wavy_zheng 2022年4月24日
	 * @param channel
	 * @param chnData
	 */
	public void processCvCurrent(Channel channel, ChannelData chnData, Step step) {

		List<ChannelData> list = cvCurrentFilterBuff.get(channel);

		double actualCurrent = chnData.getCurrent();

		// 原始数据缓存
		if (cvRawCurrentFilterBuff.get(channel) == null) {

			cvRawCurrentFilterBuff.put(channel, Collections.synchronizedList(new ArrayList<>()));
		}
		List<ChannelData> rawList = cvRawCurrentFilterBuff.get(channel);

		boolean makeCurrent = false;
		if (MainBoard.startupCfg.getRange().adjustCcCvVoltage) {

			double offset = 5.0;
			RangeSection rs = DataProcessService.findSectionByCurrent(chnData.getCurrent());
			if (rs != null) {

				offset = rs.currentFilterRange;
			}

			if (rawList.isEmpty() && Math.abs(chnData.getCurrent() - step.specialCurrent) <= offset) {

				// 制造电流
				double cu = chnData.getCurrent();
				chnData.setCurrent(makeCvCurrent(channel, chnData, step));
				makeCurrent = true;
				channel.log("make current: " + cu + " -> " + chnData.getCurrent());
				channel.setInCvCurrentMakeTime(true);

			}

			if (channel.isInCvCurrentMakeTime()) {

				if (!rawList.isEmpty() && rawList.get(rawList.size() - 1).getCurrent() < chnData.getCurrent()) {

					// 未达到切换真实电流的标准
					double cu = chnData.getCurrent();
					chnData.setCurrent(makeCvCurrent(channel, rawList.get(rawList.size() - 1), step));
					makeCurrent = true;
					channel.log("make current: " + cu + " -> " + chnData.getCurrent());

				} else if (!rawList.isEmpty() && chnData.getCurrent() <= rawList.get(rawList.size() - 1).getCurrent()) {

					channel.setInCvCurrentMakeTime(false);
					channel.log("the current is " + chnData.getCurrent() + " < "
							+ rawList.get(rawList.size() - 1).getCurrent() + ",exit make time!");
				}
			}

		}

		try {
			cvRawCurrentFilterBuff.get(channel).add((ChannelData) chnData.clone());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		channel.log("actual current:" + actualCurrent);

		// 截止电流不做处理
		if (chnData.getCurrent() < step.getOverThreshold()) {

			if (actualCurrent > step.getOverThreshold() && channel.isInCvCurrentMakeTime()) {

				// 实际电流仍然大于截止电流,则不下降
				chnData.setCurrent(step.getOverThreshold() + new Random().nextDouble() / 2);
				channel.log("actual current " + actualCurrent + ",deal with " + chnData.getCurrent());

			} else {

				System.out.println("current " + chnData.getCurrent() + " < " + step.getOverThreshold());
				return;

			}
		}

		int rawCount = 5;
		if (!makeCurrent) {

			if (list == null || list.size() < FILTER_COUNT) {

				if (list == null) {

					list = new ArrayList<>();
					list = Collections.synchronizedList(list);
					cvCurrentFilterBuff.put(channel, list);

				}

				list.add(chnData);

				return;
			}
			boolean currentUp = false; // 电流是否上弹

			if (chnData.getCurrent() - list.get(list.size() - 1).getCurrent() >= 5) {

				currentUp = true;
			}

			// 添加尾数
			list.add(chnData);

			double val = 0;

			double std = calculateCurrentStdVal(rawList);
			double avg = calculateCurrentAverageVal(rawList);

			/**
			 * 防止出现list出现超过3个的情况
			 */
			if (list.size() > FILTER_COUNT + 1) {

				list.subList(0, list.size() - FILTER_COUNT - 1).clear();
			}
			if (Math.abs(chnData.getCurrent() - avg) >= std * 3) {

				// 计算一个合理值
				chnData.setCurrent(makeCvCurrent(channel, list.get(list.size() - 2), step));

			} else {

				for (int n = 0; n < list.size(); n++) {

					if (currentUp) {

						if (n == 0) {

							val += list.get(n).getCurrent() * 0.00;

						} else if (n == 1) {

							val += list.get(n).getCurrent() * 0.99;
						} else {

							val += list.get(n).getCurrent() * 0.01;
						}

					} else {

						if (n == 0) {

							val += list.get(n).getCurrent() * 0.00;
						} else if (n == 1) {

							val += list.get(n).getCurrent() * 0.20;
						} else {

							val += list.get(n).getCurrent() * 0.80;
						}

					}
				}

				// 更新新的电流值
				chnData.setCurrent(val);

			}

			if (list.size() > FILTER_COUNT) {
				// 更新缓存
				list.remove(0);
			}

		}

		if (cvRawCurrentFilterBuff.get(channel).size() >= rawCount + 3) {

			cvRawCurrentFilterBuff.get(channel).remove(0);
		}

	}

	/**
	 * 判断在cc-cv下是否已经进入了CV模式
	 * 
	 * @author wavy_zheng 2021年1月26日
	 * @param channel
	 * @param chnData
	 * @return
	 */
	public static boolean isCvInCcCv(Step step, Channel channel, ChannelData chnData) {

		// 检测电压是否在恒定范围以内
		if (channel.isCvInCccvMode()) {

			return true;
		}
		if (!MainBoard.startupCfg.getRange().adjustCcCvVoltage) {

			// 150A大电流由于硬件模片设计缺陷，导致CV电压剧烈震荡，现进行特调处理;判断进入CV不再根据电压
			if (chnData.getVoltage() - step.specialVoltage < -MainBoard.startupCfg.getRange().voltageFilterRange) {

				return false;
			}

		}

		RangeSection rs = findSectionByCurrent(chnData.getCurrent());

		if (rs == null) {

			return false;
		}

		double offset = rs.precision * 3 > step.specialCurrent * 0.01 ? rs.precision * 3 : step.specialCurrent * 0.01;

		// 检测连续两个电流是否下降到恒定值一个精度以下
		if (chnData.getCurrent() > step.specialCurrent - offset || chnData.getCurrent() == 0) {

			return false;
		}
		if (!channel.getRuntimeCaches().isEmpty()) {
			ChannelData lastData = channel.getRuntimeCaches().get(channel.getRuntimeCaches().size() - 1);
			if (lastData.getCurrent() > step.specialCurrent - offset || lastData.getCurrent() == 0) {

				return false;
			}
		} else {

			return false;
		}

		return true;

	}

	/**
	 * 电压滤波器
	 * 
	 * @author wavy_zheng 2021年5月18日
	 * @param channel
	 * @param chnData
	 */
	private void filterData(Channel channel, ChnData chnData, boolean filterVoltage) {

		if (chnData.getState() != Logic2Environment.ChnState.RUNNING) {

			return;
		}

		List<ChnData> buff = filterBuff.get(channel);
		if (buff == null) {

			buff = new ArrayList<ChnData>();
		}
		// 检查步次是否发生变更
		for (ChnData data : buff) {

			// 步次变更清除缓存
			if (data.getStepIndex() != chnData.getStepIndex() || data.getLoopIndex() != chnData.getLoopIndex()) {

				buff.clear();
				filterCount.put(channel, 0);
				break;
			}

		}

		Integer count = filterCount.get(channel);
		if (count == null) {

			count = 0;
		}
		if (count > CONTINUE_FILTER_TOTAL) {

			return; // 连续滤波次数超
		}

		if (buff.size() >= FILTER_COUNT) {

			buff.remove(0);
		}
		buff.add(chnData);

		if (buff.size() >= FILTER_COUNT) {

			// 开始过滤
			List<Double> datas = new ArrayList<>();
			for (ChnData data : buff) {

				datas.add(filterVoltage ? data.getVoltage() : data.getCurrent());
			}
			double mean = MathUtil.calculateMean(datas);
			double std = MathUtil.calculateStd(mean, datas);
			if (Math.abs(chnData.getVoltage() - mean) >= std * FILTER_FACTOR && std > 2) {

				if (filterVoltage) {
					channel.log("filter voltage:" + chnData.getVoltage() + ",mean:" + mean + ",std:" + std);
				} else {
					channel.log("filter current:" + chnData.getCurrent() + ",mean:" + mean + ",std:" + std);
				}
				double change = MathUtil.calculateChangeStep(datas); // 平均变化值
				if (filterVoltage) {
					double voltage = datas.get(datas.size() - 2) + change; // 计算出滤波后的电压
					chnData.setVoltage(voltage);

				} else {
					double current = datas.get(datas.size() - 2) + change; // 计算出滤波后的电压
					chnData.setCurrent(current);

				}
				filterCount.put(channel, count + 1); // 累计加1

				return;
			}

		}
		filterCount.put(channel, 0);

	}

	public static double calculateCurrentAverageVal(List<ChannelData> sources) {

		double avg = 0;
		for (int n = 0; n < sources.size(); n++) {

			avg += sources.get(n).getCurrent();

		}
		avg = avg / sources.size();

		return avg;
	}

	public static double calculateCurrentStdVal(List<ChannelData> sources) {

		double std = 0;

		double avg = calculateCurrentAverageVal(sources);
		for (int n = 0; n < sources.size(); n++) {

			std += Math.pow(sources.get(n).getCurrent() - avg, 2);

		}
		std = Math.sqrt(std);

		return std;

	}

	public static double calculateVoltageAverageVal(List<ChannelData> sources) {

		double avg = 0;
		for (int n = 0; n < sources.size(); n++) {

			avg += sources.get(n).getVoltage();

		}
		avg = avg / sources.size();

		return avg;
	}

	public static double calculateVoltageStdVal(List<ChannelData> sources) {

		double avg = 0, std = 0;

		for (int n = 0; n < sources.size(); n++) {

			avg += sources.get(n).getVoltage();

		}
		avg = avg / sources.size();
		for (int n = 0; n < sources.size(); n++) {

			std += Math.pow(sources.get(n).getVoltage() - avg, 2);

		}
		std = Math.sqrt(std);

		return std;

	}

}
