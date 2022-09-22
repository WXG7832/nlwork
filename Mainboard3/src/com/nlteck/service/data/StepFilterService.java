package com.nlteck.service.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月16日 下午3:12:40 步次数据过滤,管理步次跳转,插入逻辑板跳步次的转点关键数据！
 */
public class StepFilterService implements DataFilterService {

	private Logger logger;

	public StepFilterService() {

		try {
			logger = LogUtil.createLog("log/stepFilterService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<ChnDataPack> filterRawDatas(Channel channel, List<ChnDataPack> rawDatas) {

		List<ChnDataPack> insertList = new ArrayList<>();
		List<ChnDataPack> copyList = new ArrayList<>();
		copyList.addAll(rawDatas);
		for (int n = 0; n < rawDatas.size(); n++) {

			ChnDataPack rawData = rawDatas.get(n);

			if (channel.getState() == MainEnvironment.ChnState.RUN) {

				// 流程运行结束
				if (rawData.getState() == DriverEnvironment.ChnState.COMPLETE
						&& MainBoard.startupCfg.getProcedureSupportInfo().supportImportantData) {

					ChnDataPack nodeData = createNodeData(channel, rawData);
					if (nodeData != null) {

						int insertIndex = copyList.indexOf(rawData);
						copyList.add(insertIndex, nodeData);

					}

				} else if (rawData.getState() == ChnState.RUNNING
						&& (rawData.getStepIndex() == 0 || rawData.getLoopIndex() == 0)) {

					channel.log("0-0 step:" + rawData);
					channel.setSleepZeroCount(channel.getSleepZeroCount() + 1);
					if (channel.getSleepZeroCount() > 10) {

						try {
							Context.getCoreService().executeChannelsAlertInLogic(
									AlertCode.LOGIC, I18N.getVal(I18N.StepSkipError, channel.getStepIndex(),
											rawData.getStepIndex(), channel.getLoopIndex(), rawData.getLoopIndex()),
									channel);
						} catch (AlertException e) {

							channel.log(CommonUtil.getThrowableException(e));
							e.printStackTrace();

						}
					}

					continue;

				} else if ((rawData.getStepIndex() != channel.getStepIndex()
						|| rawData.getLoopIndex() != channel.getLoopIndex())) {

					channel.log("step index change:" + rawData);

					if (rawData.getLoopIndex() > 0 && rawData.getStepIndex() > 0) {

						channel.log("set lead sync count = 2");
						channel.setLeadSyncCount(2); // 转步后，可以同步

						// 记录最初要插入的位置
						int origIndex = copyList.indexOf(rawData);

						ChnDataPack skipData = null;
						if (MainBoard.startupCfg.getProcedureSupportInfo().supportImportantData) {
                             
							int  index = 1;
							do {
								skipData = createSkipData(channel, rawData, index);
								if (skipData != null) {
	
									int insertIndex = copyList.indexOf(rawData);
									if (insertIndex != -1) {
	
										channel.log("insert " + index  +" skip data:" + skipData);
										copyList.add(insertIndex, skipData);
	
									}
									index++;
									// 最多处理2次
									/*skipData = createSkipData(channel, rawData, 2);
									if (skipData != null) {
	
										insertIndex = copyList.indexOf(rawData);
										if (insertIndex != -1) {
	
											channel.log("insert second skip data:" + skipData);
											copyList.add(insertIndex, skipData);
	
										}
									}*/
									
									if(index > 10) {
										
										//防止主控进入死循环跳步次;
										break;
									}
	
								}
							} while(skipData != null);
						}

						if (rawData.getStepElapsedTime() > 0) {

							// 加入逻辑板给的第1个数据时间不是0开始，强行将其改成第1个点
							// 容量和时间置0
							// rawData.setStepElapsedTime(0);
							// rawData.setCapacity(0);

						}
						if (skipData == null) { // 确认没有发生跳步次后才记录上步次最后一个关键点数据
							if ((rawData.getStepIndex() > 1 && rawData.getLoopIndex() > 0)
									|| (rawData.getStepIndex() == 1 && rawData.getLoopIndex() > 1)) {
								// 塞入上个步次的最后一条关键点数据

								if (MainBoard.startupCfg.getProcedureSupportInfo().supportImportantData) {
									ChnDataPack nodeData = createNodeData(channel, rawData);
									if (nodeData != null && origIndex != -1) {

										copyList.add(origIndex, nodeData);
									}
								}

							}
						}

					}

				}

			}

		}

		return copyList;
	}

	/**
	 * 因逻辑板直接跳转 -> 跳转步次数据
	 * 
	 * @author wavy_zheng 2021年2月17日
	 * @param channel
	 * @param rawData
	 * @param stepCount
	 *            生成后几个步次
	 * @return
	 */
	private ChnDataPack createSkipData(Channel channel, ChnDataPack rawData, int stepCount) {

		Step nextStep = null;

		for (int n = 0; n < stepCount; n++) {

			int stepIndex = nextStep == null ? channel.getStepIndex() : nextStep.getStepIndex();
			int loopIndex = nextStep == null ? channel.getLoopIndex() : nextStep.getLoopIndex();
			
			nextStep = DataProviderService.nextStepFrom(channel.getCurrentProcedure(), stepIndex, loopIndex);
			
		}

		if (nextStep == null) {

			channel.log("channel stepindex = " + channel.getStepIndex() + ",channel loop = " + channel.getLoopIndex()
					+ ",can not get next step,alert!");
			return null;
		}
		
		channel.log("channel next step :" + nextStep.loopIndex + "-" + nextStep.stepIndex);

		rawData.setImportant(true); // 关键点,新步次的第一个数据点
		ChnDataPack skipData = null;
		
		

		if (nextStep.stepIndex > 0 && nextStep.loopIndex > 0 && (nextStep.stepIndex != rawData.getStepIndex() || nextStep.loopIndex != rawData.getLoopIndex())) {

			// 已经发生了跳转步次,开始生成各个跳转步次的转点关键数据
			try {
				skipData = (ChnDataPack) rawData.clone();

				skipData.setStepIndex(nextStep.stepIndex);
				skipData.setLoopIndex(nextStep.loopIndex);
				skipData.setWorkMode(DriverEnvironment.WorkMode.values()[nextStep.workMode.ordinal()]);
				if (nextStep.workMode == WorkMode.CCC) {

					if (nextStep.overThreshold > 0 && nextStep.overThreshold > rawData.getVoltage()) {
						skipData.setVoltage(nextStep.overThreshold + new Random().nextDouble() / 2); // 模拟电压超限
					} else {

						skipData.setVoltage(rawData.getVoltage());
					}
					skipData.setCurrent(CommonUtil.produceRandomNumberInRange(nextStep.specialCurrent, 0.1));
				} else if (nextStep.workMode == WorkMode.CVC || nextStep.workMode == WorkMode.CC_CV) {

					skipData.setVoltage(CommonUtil.produceRandomNumberInRange(nextStep.specialVoltage, 0.1)); // 模拟电压恒压
					if (nextStep.overThreshold > 0 ) {
						skipData.setCurrent(nextStep.overThreshold - new Random().nextDouble() / 2);
					} else {

						skipData.setCurrent(rawData.getCurrent());
					}
				} else if (nextStep.workMode == WorkMode.CCD) {

					if (nextStep.overThreshold > 0 && nextStep.overThreshold > rawData.getVoltage()) {
						skipData.setVoltage(nextStep.overThreshold - new Random().nextDouble() / 2); // 模拟电压超限
					} else {

						skipData.setVoltage(rawData.getVoltage());
					}
					skipData.setCurrent(CommonUtil.produceRandomNumberInRange(nextStep.specialCurrent, 0.1));
				} else if (nextStep.workMode == WorkMode.SLEEP) {

				} else {

					return null;

				}
				if (channel.getState() == MainEnvironment.ChnState.RUN) {

					skipData.setCapacity(0);
					skipData.setStepElapsedTime(0);
					skipData.setAlertCode(DriverEnvironment.AlertCode.NORMAL);
					skipData.setState(ChnState.RUNNING);
					skipData.setImportant(true);

				}

				skipData.setBackupVoltage(rawData.getBackupVoltage());
				skipData.setPowerVoltage(rawData.getPowerVoltage());

			} catch (CloneNotSupportedException e) {

				e.printStackTrace();
			}

		}

		return skipData;
	}

	/**
	 * 创建关键结点数据
	 * 
	 * @author wavy_zheng 2021年2月4日
	 * @param channel
	 * @param rawData
	 * @return
	 */
	private ChnDataPack createNodeData(Channel channel, ChnDataPack rawData) {

		ChnDataPack lastData = new ChnDataPack();
		lastData.setVoltage(rawData.getAlertVolt());

		if (lastData.getVoltage() == 0) {

			lastData.setVoltage(channel.getVoltage());
		}
		if (channel.getWorkMode() == WorkMode.SLEEP) {

			lastData.setCurrent(0);
		} else {
			lastData.setCurrent(rawData.getAlertCurrent());
		}

		lastData.setBackupVoltage(rawData.getBackupVoltage());
		lastData.setPowerVoltage(rawData.getPowerVoltage());

		lastData.setStepElapsedTime(
				rawData.getAlertTime() == 0 ? channel.getStepElapseMiliseconds() + 2500 : rawData.getAlertTime());
		// double capacity = rawData.getAlertCapacity();
		// if(capacity < channel.getStepCapacity()) {

		double capacity = channel.getStepCapacity() + DataProviderService.getDeltaCapacity(
				rawData.getAlertCurrent() == 0 ? channel.getCurrent() : rawData.getAlertCurrent(),
				lastData.getStepElapsedTime() - channel.getStepElapseMiliseconds());
		// }
		// 容量
		lastData.setCapacity(capacity);

		lastData.setState(rawData.getState() == ChnState.COMPLETE ? ChnState.RUNNING : rawData.getState());
		if (channel.getWorkMode() == WorkMode.SYNC) {

			lastData.setWorkMode(DriverEnvironment.WorkMode.SLEEP);
		} else {

			lastData.setWorkMode(DriverEnvironment.WorkMode.values()[channel.getWorkMode().ordinal()]);
		}
		lastData.setLoopIndex(channel.getLoopIndex());
		lastData.setStepIndex(channel.getStepIndex());
		lastData.setImportant(true);
		lastData.setChnIndex(rawData.getChnIndex());
		//

		channel.log("rawData = " + rawData.toString());
		// channel.log("last data:" + lastData);

		if (channel.getLastRawData() != null) {
			if (channel.getLastRawData().getStepIndex() == lastData.getStepIndex()
					&& channel.getLastRawData().getLoopIndex() == lastData.getLoopIndex()) {

				// 如果出现重复的数据，则不添加结点数据
				if (channel.getLastRawData().getStepElapsedTime() != lastData.getStepElapsedTime()) {

					Step step = channel.getProcedureStep(lastData.getStepIndex());
					if (step != null && step.workMode == WorkMode.SLEEP
							&& ((lastData.getStepElapsedTime() > step.overTime * 1000 && step.overTime > 0)
									|| lastData.getStepElapsedTime() == 0)) {

						lastData.setStepElapsedTime(step.overTime * 1000);
					}

					return lastData;
				} else {

					channel.log("same data , not append node,set important node");

					ChannelData skipData = channel.getRuntimeCaches().get(channel.getRuntimeCaches().size() - 1);
					skipData.setImportantData(true);
					channel.log("last skip data " + skipData);
				}

			}
		}

		return null;

	}

	@Override
	public List<ChannelData> filterChannelDatas(Channel channel, List<ChannelData> channelDatas) {

		/*
		 * for (int n = 0; n < channelDatas.size(); n++) {
		 * 
		 * ChannelData channelData = channelDatas.get(n); // 先判断步次跳转是否正确（即往后跳转） if
		 * (channelData.getStepIndex() < channel.getStepIndex() &&
		 * channelData.getLoopIndex() == channel.getLoopIndex()) {
		 * 
		 * try { Context.getCoreService().executeChannelsAlertInLogic( AlertCode.LOGIC,
		 * I18N.getVal(I18N.StepSkipError, channel.getStepIndex(),
		 * channelData.getStepIndex(), channel.getLoopIndex(),
		 * channelData.getLoopIndex()), channel); } catch (AlertException e) {
		 * 
		 * channel.log(CommonUtil.getThrowableException(e)); e.printStackTrace();
		 * 
		 * }
		 * 
		 * continue; }
		 * 
		 * }
		 */
		return channelDatas;
	}

}
