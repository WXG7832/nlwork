package com.nlteck.service.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.ParameterName;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.service.StartupCfgManager.RangeSection;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.check2.Check2Environment.AlertCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CCVProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverResumeData.ResumeUnit;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年12月10日 下午8:20:06 通道保护过滤
 */
public class ProtectionFilterService implements DataFilterService {

	private Logger logger;
	private final static int CONTINUE_POLE_COUNT = 3; // 最小连续反极性报警的次数
	private final static int CONTINUE_OVERVOLT_COUNT = 3; // 关闭通道后连续3次都超压
	private final static int MONITOR_OVERVOLT_TIME = 20000; // 20s
	private static final double MIN_TOUCH_CURRENT = 1000; // 最小接触电阻计算电流阀值

	private static final double MAX_VOLTAGE_CONTINUE_COUNT = 4; // 连续多少次电压变化
	private static final double MAX_VOLTAGE_CONTINUE_VAL = 5; // 连续电压变化累计阀值

	private static final int MAX_STEP_PICK_COUNT = 10; // 最大跳转步次后采集次数，超该次数仍未跳转的触发保护

	private static final int DEFAULT_SLOPE_TIME = 300; // 斜率默认时间
	private static final double DEFAULT_SLOPE_LOWER = 0.5; // 斜率默认最小值
	private static final double DEFAULT_RESISTER_LOWER = 800; // 默认最小接触电阻

	private static final double DEFAULT_MIN_VOLTAGE_OFFSET = 1500; // 最小压差保护阀值
	public static final int TOUCH_VOLT_PICK_COUNT = 5; // 待测压差保护采集个数
	public static final int TOUCH_VOLT_PICK_ALERT_COUNT = 4; // 待测压差保护采集个数偏差阀值
	private static final int MAX_CONST_OFFSET_COUNT = 5; // 连续恒定值偏差个数

	public static final double CC_CV_VOLT_RANGE = 5.0; // 恒压范围偏差

	private Map<Channel, List<ChnData>> exceptionCache = new HashMap<>();

	public ProtectionFilterService() {

		try {
			logger = LogUtil.createLog("log/protectionFilterService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 处理满足转步次条件后逻辑板仍没有发生转步次的异常情况
	 * 
	 * @author wavy_zheng 2021年2月15日
	 * @param channel
	 * @param channelDatas
	 */
	private boolean processOverStepChange(Channel channel, ChannelData channelData) {

		boolean lastStep = false;
		if (channelData.getState() != ChnState.RUN) {

			return lastStep; // 未运行则忽略
		}
		Step step = channel.getProcedureStep(channelData.getStepIndex());
		if (step == null) {

			return lastStep;
		}
		Step nextStep = channel.getProcedureStep(channelData.getStepIndex() + 1);

		switch (step.getWorkMode()) {

		case SLEEP:
			if (channelData.getTimeStepSpend() >= step.overTime + 10) {

				channel.appendOverStepCaches(channelData);
				lastStep = true;
			}
			break;
		case CCC:

			if (nextStep != null && nextStep.getWorkMode() == WorkMode.CVC) {

				return lastStep; // 不处理cc-cv转步情况
			}
			if (step.overTime > 0 && channelData.getTimeStepSpend() >= step.overTime) {

				lastStep = true;
				if (step.isTimeProtect()) {

					// 超时保护
					try {
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TIME_OVER,
								I18N.getVal(I18N.LogicOverTime, channelData.getTimeStepSpend(), step.overTime),
								channel);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						Context.getPcNetworkService().pushSendQueue(e);
						logger.error(CommonUtil.getThrowableException(e));
					}

					return lastStep;

				}

				channel.appendOverStepCaches(channelData);
			}
			if (channelData.getVoltage() >= step.overThreshold) {

				lastStep = true;
				channel.appendOverStepCaches(channelData);
			} else {

				channel.clearOverStepCaches();
			}
			if (channelData.getDeviceVoltage() >= step.overThreshold) {

				channel.appendOverStepSubcaches(channelData);
			} else {

				channel.clearOverStepSubcaches();
			}

			if (step.overCapacity > 0 && channelData.getVoltage() >= step.overCapacity) {

				lastStep = true;
				channel.appendOverStepCaches(channelData);
			}

			break;
		case CVC:
		case CC_CV:
			if (step.overTime > 0 && channelData.getTimeStepSpend() >= step.overTime) {

				lastStep = true;
				if (step.isTimeProtect()) {

					// 超时保护
					try {
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TIME_OVER,
								I18N.getVal(I18N.LogicOverTime, channelData.getTimeStepSpend(), step.overTime),
								channel);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						Context.getPcNetworkService().pushSendQueue(e);
						logger.error(CommonUtil.getThrowableException(e));
					}

					return lastStep;

				}
				channel.appendOverStepCaches(channelData);
			}
			if (channelData.getCurrent() <= step.overThreshold) {

				lastStep = true;
				channel.appendOverStepCaches(channelData);
			} else {

				channel.clearOverStepCaches();
			}
			if (step.overCapacity > 0 && channelData.getVoltage() >= step.overCapacity) {

				lastStep = true;
				channel.appendOverStepCaches(channelData);
			}
			break;
		case CCD:
			if (step.overTime > 0 && channelData.getTimeStepSpend() >= step.overTime) {

				lastStep = true;
				if (step.isTimeProtect()) {

					// 超时保护
					try {
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TIME_OVER,
								I18N.getVal(I18N.LogicOverTime, channelData.getTimeStepSpend(), step.overTime),
								channel);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						Context.getPcNetworkService().pushSendQueue(e);
						logger.error(CommonUtil.getThrowableException(e));
					}

					return lastStep;

				}
				channel.appendOverStepCaches(channelData);
			}
			if (channelData.getVoltage() <= step.overThreshold) {

				lastStep = true;
				channel.appendOverStepCaches(channelData);
			} else {

				channel.clearOverStepCaches();
			}
			if (channelData.getDeviceVoltage() <= step.overThreshold) {

				channel.appendOverStepSubcaches(channelData);
			} else {

				channel.clearOverStepSubcaches();
			}

			break;

		}

		if (channel.getOverStepCaches().size() > MAX_STEP_PICK_COUNT) {

			try {

				if (step.workMode != WorkMode.SLEEP) {

					channel.log("over step voltage : " + channelData.toString());
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.DEVICE_ERROR,
							I18N.getVal(I18N.StepChangeExcept), channel);

					Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
							I18N.getVal(I18N.ChnStepChangeException, channel.getDeviceChnIndex() + 1), false);

				} else {
					channel.clearOverStepCaches(); // 先清空休眠数据，开始恢复

					DriverOperateData operate = new DriverOperateData();
					operate.setDriverIndex(channel.getDriverBoard().getDriverIndex());
					short optFlag = (short) (0x01 << channel.getChnIndex());
					operate.setOptFlag(optFlag);
					operate.setOpen(false);
					Context.getDriverboardService().writeOperate(operate);

					CommonUtil.sleep(100);

					if (nextStep != null) {
						// 直接恢复到下一步
						DriverResumeData resume = new DriverResumeData();

						// channel.skipStep(stepIndex, loopIndex);
						ResumeUnit unit = new ResumeUnit();
						resume.setDriverIndex(channel.getDriverBoard().getDriverIndex());

						unit.chnIndex = channel.getChnIndex();
						unit.loopIndex = channel.getLoopIndex();
						unit.stepIndex = nextStep.getStepIndex();
						unit.miliseconds = 0;
						unit.capacity = channel.getStepCapacity();

						List<ResumeUnit> units = new ArrayList<>();
						units.add(unit);
						resume.setUnits(units);

						Context.getDriverboardService().writeResume(resume);

						channel.log("because over time sleep , resume next step :" + nextStep);

						// 重新打开通道
						operate.setDriverIndex(channel.getDriverBoard().getDriverIndex());
						optFlag = (short) (0x01 << channel.getChnIndex());
						operate.setOptFlag(optFlag);
						operate.setOpen(true);

						Context.getDriverboardService().writeOperate(operate);

					} else {

						channel.complete();
					}

				}
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
		}

		return lastStep;
		/*
		 * else if (channel.getOverStepSubcaches().size() > MAX_STEP_PICK_COUNT * 5) {
		 * 
		 * try { if (step.workMode != WorkMode.SLEEP) {
		 * Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.
		 * AlertCode.DEVICE_ERROR, I18N.getVal(I18N.StepChangeExcept), channel);
		 * 
		 * Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
		 * I18N.getVal(I18N.ChnStepChangeException, channel.getDeviceChnIndex() + 1),
		 * false);
		 * 
		 * } } catch (AlertException e) {
		 * Context.getPcNetworkService().pushSendQueue(e);
		 * logger.error(CommonUtil.getThrowableException(e)); } }
		 */

	}

	/**
	 * 处理超压报警
	 * 
	 * @author wavy_zheng 2021年1月26日
	 * @param channel
	 * @param chnDatas
	 */
	public void processOverVoltage(Channel channel, List<ChannelData> chnDatas) {

		if (channel.getState() != ChnState.ALERT && channel.getState() != ChnState.CLOSE) {
			for (ChannelData chnData : chnDatas) {

				DeviceProtectData dpd = channel.getControlUnit().getDpd();
				if (dpd.getDeviceVoltUpper() > 0 && chnData.getDeviceVoltage() > dpd.getDeviceVoltUpper()) {

					// 关闭通道
					try {
						chnData.setImportantData(true);
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_UPPER, I18N
								.getVal(I18N.CoreDeviceOverVolt, chnData.getDeviceVoltage(), dpd.getDeviceVoltUpper()),
								channel);
					} catch (AlertException e) {

						e.printStackTrace();
					}

					break;
				}
			}
		}

		if (channel.getAlertCode() != MainEnvironment.AlertCode.DEVICE_ERROR && channel.getState() != ChnState.CLOSE) {

			for (ChannelData chnData : chnDatas) {

				if (chnData.getDeviceVoltage() > channel.getControlUnit().getDpd().getDeviceVoltUpper()) {

					channel.appendOverVoltData(chnData);
				} else {

					channel.clearOverVoltCache();
				}
			}
		}

		if (!channel.getOverVoltCaches().isEmpty()) {

			System.out.println("overVoltCaches:" + channel.getOverVoltCaches().size());

			if (channel.getOverVoltCaches().size() >= CONTINUE_OVERVOLT_COUNT) {

				channel.clearOverVoltCache();

				logger.info("over volt count >= " + CONTINUE_OVERVOLT_COUNT + ",over voltage alert");

				// 触发报警
				try {

					channel.alert(MainEnvironment.AlertCode.DEVICE_ERROR,
							I18N.getVal(I18N.ChnOverVoltException, channel.getDeviceChnIndex() + 1));
					Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
							I18N.getVal(I18N.ChnOverVoltException, channel.getDeviceChnIndex() + 1), false);
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * 处理一般保护,必配
	 * 
	 * @author wavy_zheng 2021年1月26日
	 * @param channel
	 * @param channelDatas
	 */
	private void processDeviceProtect(Channel channel, ChannelData channelData) {

		if (channel.getState() == ChnState.ALERT) {

			return;
		}

		DeviceProtectData dpd = channel.getControlUnit().getDpd();
		if (dpd == null) {

			return;
		}

		double batVoltUpper = dpd.getBatVoltUpper();
		double batCurrUpper = dpd.getCurrUpper();
		double capacityCoefficien = dpd.getCapacityCoefficien();

		ProcedureData procedure = channel.getCurrentProcedure();

		// 处理容量系数
		if (procedure != null && channel.getState() != ChnState.ALERT && channelData.getState() == ChnState.RUN) {

			String procedureName = procedure.getName();
			// ATL客户流程名以-分割，第2个段表示容量大小,mAh
			String[] secs = procedureName.split("-");
			if (secs.length > 2) {

				int capacity = Integer.parseInt(secs[1]);
				if (capacityCoefficien > 0) {

					if (channelData.getAccumulateCapacity() > capacity * capacityCoefficien) {

						// 触发容量系数保护
						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(
									MainEnvironment.AlertCode.CAPACITY_UPPER, I18N.getVal(I18N.CapacityCoefficien,
											channelData.getAccumulateCapacity(), (double) capacity, capacityCoefficien),
									channel);
						} catch (AlertException e) {

							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}

					}
				}
			}
		}

		// 主控检测电池超压保护
		if (channelData.getVoltage() > batVoltUpper && batVoltUpper > 0) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_UPPER,
						I18N.getVal(I18N.CoreBatOverVolt, channelData.getVoltage(), batVoltUpper), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
		}

		// 主控检测到电池超流
		if (channelData.getCurrent() > batCurrUpper && batCurrUpper > 0) {

			channel.log("over current alert:" + channelData.toString());

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CUR_UPPER,
						I18N.getVal(I18N.CoreBatOverCurr, channelData.getCurrent(), batCurrUpper), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
		}

	}

	/**
	 * 处理休眠保护
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelData
	 */
	private void processSleepProtection(Channel channel, ChannelData channelData) {

		if (channelData.getState() != ChnState.RUN) {

			return;
		}
		if (channelData.getWorkMode() != WorkMode.SLEEP) {

			return;
		}

		if (channelData.getStepIndex() == 0 || channelData.getLoopIndex() == 0) {

			channel.setSleepZeroCount(channel.getSleepZeroCount() + 1);

			// 步次时间保护
			Step step = channel.getProcedureStep(channel.getStepIndex());
			if (step != null && step.timeProtect && step.overTime > 0
					&& channelData.getTimeStepSpend() + 5 > step.overTime) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService()
							.executeChannelsAlertInLogic(
									MainEnvironment.AlertCode.TIME_OVER, I18N.getVal(I18N.StepTimeout,
											channel.getStepIndex(), channelData.getTimeStepSpend() + 5, step.overTime),
									channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;
			} else if (channel.getSleepZeroCount() > 10) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(
							MainEnvironment.AlertCode.LOGIC, I18N.getVal(I18N.StepSkipError, channel.getStepIndex(),
									channelData.getStepIndex(), channel.getLoopIndex(), channelData.getLoopIndex()),
							channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}

			}

		}

		SlpProtectData protect = null;
		Step step = channel.getProcedureStep(channelData.getStepIndex());

		if (step == null || step.protection == null) {
			ParameterName pn = channel.getControlUnit().getCurrentPn();
			if (pn == null) {

				return;
			}
			protect = pn.getSlpProtect();
		} else {

			protect = (SlpProtectData) step.protection;
		}

		if (protect.getVoltOffset() > 0) {

			if (channel.getRuntimeCaches().size() > 0) {

				ChannelData preData = channel.getRuntimeCaches().get(0);
				// 同个步次两次数据超差
				if (preData.getStepIndex() == channelData.getStepIndex()
						&& preData.getLoopIndex() == channelData.getLoopIndex()) {

					if (Math.abs(channelData.getVoltage() - preData.getVoltage()) > protect.getVoltOffset()) {
						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.SleepProtect, channelData.getVoltage(), preData.getVoltage()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;
					}

				}
			}

		}

	}

	private boolean isCvInCcCvMode(Channel channel, ChannelData channelData) {

		Step step = channel.getProcedureStep(channelData.getStepIndex());

		if (step == null) {

			return false;
		}
		// 检测电压是否在恒定范围以内
		if (channelData.getVoltage() - step.specialVoltage < -CC_CV_VOLT_RANGE) {

			return false;
		}

		RangeSection rs = DataProcessService.findSectionByCurrent(channelData.getCurrent());

		if (rs == null) {

			return false;
		}
		// 检测电流是否下降到恒定值一个精度以下
		if (channelData.getCurrent() > step.specialCurrent - rs.precision || channelData.getCurrent() == 0) {

			return false;
		}

		return true;

	}

	/**
	 * 处理cccv保护，将保护拆分成cc和cv两部分进行分别保护
	 * 
	 * @author wavy_zheng 2022年3月1日
	 * @param channel
	 * @param channelData
	 */
	private void processCCCVProtection(Channel channel, ChannelData channelData) {

		if (isCvInCcCvMode(channel, channelData)) {

			processCVProtection(channel, channelData);

		} else {

			processCCProtection(channel, channelData);
		}
	}

	/**
	 * CC保护处理
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelData
	 */
	private void processCCProtection(Channel channel, ChannelData channelData) {

		if (channelData.getState() != ChnState.RUN || channelData.getStepIndex() == 0) {

			return;
		}
		if (channelData.getWorkMode() != WorkMode.CCC && channelData.getWorkMode() != WorkMode.CC_CV) {

			return;
		}

		if (channelData.getWorkMode() == WorkMode.CC_CV) {

			// 判断是否在cc模式
			ProcedureData procedure = channel.getControlUnit().getProcedure();
			Step step = procedure.getStep(channelData.getStepIndex() - 1);
			if (DataProcessService.isCvInCcCv(step, channel, channelData)) {

				return;
			}

		}

		Step step = channel.getControlUnit().getProcedureStep(channelData.getStepIndex());
		CCProtectData protect = null;

		if (step == null || step.protection == null) {
			ParameterName pn = channel.getControlUnit().getCurrentPn();
			if (pn == null) {

				return;
			}
			protect = pn.getCcProtect();
		} else {

			if (step.protection instanceof CCVProtectData) {

				protect = ((CCVProtectData) step.protection).getCcProtect();
			} else {

				protect = (CCProtectData) step.protection;
			}

		}

		// 检查电压上限保护值
		if (protect.getVoltUpper() > 0 && channelData.getVoltage() > protect.getVoltUpper()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_UPPER,
						I18N.getVal(I18N.CCVoltUpper, channelData.getVoltage(), protect.getVoltUpper()), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;

		}
		// 检查电压下限保护值
		if (protect.getVoltLower() > 0 && channelData.getVoltage() < protect.getVoltLower()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_LOWER,
						I18N.getVal(I18N.CCVoltLower, channelData.getVoltage(), protect.getVoltLower()), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}

		if (channel.getRuntimeCaches().size() > 0) {

			ChannelData preData = channel.getRuntimeCaches().get(0);

			step = channel.getProcedureStep(channelData.getStepIndex());
			if (step == null) {

				return;
			}
			if (channelData.getStepIndex() != preData.getStepIndex()) {

				return;
			}

			// 电流超差保护
			if (protect.getCurrOffsetVal() > 0 || protect.getCurrOffsetPercent() > 0) {

				double offset = protect.getCurrOffsetPercent() * 0.01 * channelData.getCurrent() >= protect
						.getCurrOffsetVal() ? protect.getCurrOffsetPercent() * 0.01 * channelData.getCurrent()
								: protect.getCurrOffsetVal();

				if (Math.abs(channelData.getCurrent() - step.specialCurrent) > offset
						&& preData.getState() == ChnState.RUN && preData.getWorkMode() == WorkMode.CCC
						&& Math.abs(preData.getCurrent() - step.specialCurrent) > offset) {

					try {
						channelData.setImportantData(true);
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CURR_WAVE,
								I18N.getVal(I18N.CCCurOffset, step.specialCurrent, channelData.getCurrent()), channel);
					} catch (AlertException e) {
						Context.getPcNetworkService().pushSendQueue(e);
						logger.error(CommonUtil.getThrowableException(e));
					}
					return;
				}
			}

			// 电压超差保护2000mAh以下
			if (channelData.getAccumulateCapacity() < 2000
					&& (protect.getVoltWaveValUnder2000() > 0 || protect.getVoltWaveValUnder2000() > 0)) {

				double offset = protect.getVoltWaveValUnder2000() >= protect.getVoltWavePercentUnder2000() * 0.01
						* channelData.getVoltage() ? protect.getVoltWaveValUnder2000()
								: protect.getVoltWavePercentUnder2000() * 0.01 * channelData.getVoltage();

				if (preData.getState() == ChnState.RUN && preData.getStepIndex() == channelData.getStepIndex()
						&& preData.getLoopIndex() == channelData.getLoopIndex()) {

					if (offset > 0 && Math.abs(channelData.getVoltage() - preData.getVoltage()) > offset) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.CCVoltOffset2000d, channelData.getVoltage(), offset), channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;

					}
				}
			} else if (channelData.getAccumulateCapacity() >= 2000
					&& (protect.getVoltWaveValAbove2000() > 0 || protect.getVoltWaveValAbove2000() > 0)) {

				double offset = protect.getVoltWaveValAbove2000() >= protect.getVoltWavePercentAbove2000() * 0.01
						* channelData.getVoltage() ? protect.getVoltWaveValAbove2000()
								: protect.getVoltWavePercentAbove2000() * 0.01 * channelData.getVoltage();
				if (preData.getState() == ChnState.RUN && preData.getStepIndex() == channelData.getStepIndex()
						&& preData.getLoopIndex() == channelData.getLoopIndex()) {
					if (offset > 0 && Math.abs(channelData.getVoltage() - preData.getVoltage()) > offset) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.CCVoltOffset2000u, channelData.getVoltage(), offset), channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;
					}
				}
			}
		}
		if (!MainBoard.startupCfg.isDisableDefaultProtection()) {
			if (protect.getVoltAscUnitSeconds() == 0) {

				protect.setVoltAscUnitSeconds(DEFAULT_SLOPE_TIME);
			}
			if (protect.getVoltAscValLower() == 0) {

				protect.setVoltAscValLower(DEFAULT_SLOPE_LOWER);
			}
		}

		// 斜率保护
		if (protect.getVoltAscUnitSeconds() > 0
				&& (protect.getVoltAscValUpper() > 0 || protect.getVoltAscValLower() > 0)) {

			if (channel.getSlopeCaches().isEmpty()) {

				channel.appendSlopeCaches(channelData);

			} else {

				ChannelData slopeData = channel.getSlopeCaches().get(channel.getSlopeCaches().size() - 1);

				if (slopeData.getStepIndex() != channelData.getStepIndex()
						|| slopeData.getLoopIndex() != channelData.getLoopIndex()) {

					// 不再同个步次的斜率保护则不保护
					channel.clearSlopeCaches();
					channel.appendSlopeCaches(channelData);

				} else {

					// 上升时间差
					long seconds = channelData.getTimeStepSpend() - slopeData.getTimeStepSpend();

					if (protect.getVoltAscValLower() > 0 && seconds >= protect.getVoltAscUnitSeconds()
							&& channelData.getVoltage() - slopeData.getVoltage() < protect.getVoltAscValLower()) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.CCSlopeLower, seconds, slopeData.getVoltage(),
											channelData.getVoltage()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;
					}

					if (protect.getVoltAscValUpper() > 0
							&& channelData.getVoltage() - slopeData.getVoltage() > protect.getVoltAscValUpper()) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.CCSlopeUpper, seconds, slopeData.getVoltage(),
											channelData.getVoltage()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;

					}

					if (seconds >= protect.getVoltAscUnitSeconds()) {

						channel.clearSlopeCaches();
						channel.appendSlopeCaches(channelData);

					}

				}

			}

		}

		// 容量保护
		if (protect.getCapacityUpper() > 0 && channelData.getCapacity() >= protect.getCapacityUpper()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CAPACITY_UPPER,
						I18N.getVal(I18N.CCCapacityUpper, channelData.getCapacity(), protect.getCapacityUpper()),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;

		}
		// 时间保护
		if (protect.getMinuteUpper() > 0 && channelData.getTimeStepSpend() > protect.getMinuteUpper() * 60) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TIME_OVER,
						I18N.getVal(I18N.CCTimeUpper, channelData.getTimeStepSpend(), protect.getMinuteUpper() * 60),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}

		// 检查连续电压下降个数
		if (protect.getVoltDescCount() > 0 && protect.getVoltDescVal() > 0) {

			if (!channel.getRuntimeCaches().isEmpty()) {
				ChannelData preData = channel.getRuntimeCaches().get(0);
				if (preData.getStepIndex() == channelData.getStepIndex()
						&& preData.getLoopIndex() == channelData.getLoopIndex() && preData.getState() == ChnState.RUN) {

					if (channelData.getVoltage() - preData.getVoltage() <= -protect.getVoltDescVal()) {

						if (channel.getExceptionCaches().isEmpty()) {

							channel.appendExceptionData(preData);
						}
						channel.appendExceptionData(channelData);
					} else {

						channel.clearExceptionCaches(); // 清空缓存
					}

					if (channel.getExceptionCaches().size() >= protect.getVoltDescCount() + 1) {

						StringBuffer info = new StringBuffer(I18N.getVal(I18N.CCVoltDesc));
						for (int n = 0; n < channel.getExceptionCaches().size(); n++) {

							info.append(CommonUtil.formatNumber(channel.getExceptionCaches().get(n).getVoltage(), 1)
									+ "mV->");

						}
						info = new StringBuffer(info.substring(0, info.length() - 2));
						info.append("]");

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									info.toString(), channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}

						channel.clearExceptionCaches(); // 清空缓存
						return;
					}

				}
			}

		}

	}

	/**
	 * 处理其他保护
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelData
	 */
	private void processOtherProtection(Channel channel, ChannelData channelData) {

		if (channel.getState() != ChnState.RUN) {

			return;
		}

		// 接触电阻
		CheckVoltProtectData protect = channel.getControlUnit().getTouch();

		if (protect != null) {

			if (protect.getResisterOffset() > 0 && channelData.getCurrent() >= MIN_TOUCH_CURRENT
					&& channelData.getDeviceVoltage() > 0 && channelData.getPowerVoltage() > 0) {

				double r = 0;
				if (MainBoard.startupCfg.getPingController().enable) {
					// 计算探针内阻
					r = Math.abs(channelData.getDeviceVoltage() - channelData.getVoltage())
							/ (channelData.getCurrent() / 1000);

				} else {

					// 算全回路内阻
					r = Math.abs(channelData.getDeviceVoltage() - channelData.getPowerVoltage())
							/ (channelData.getCurrent() / 1000);
				}

				if (r > protect.getResisterOffset() && channel.isResisterOffsetAlert()) {

					channelData.setImportantData(true);
					try {
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TOUCH,
								I18N.getVal(I18N.ResisterOffset, r, protect.getResisterOffset()), channel);
					} catch (AlertException e) {
						Context.getPcNetworkService().pushSendQueue(e);
						logger.error(CommonUtil.getThrowableException(e));
					}

				} else if (r > protect.getResisterOffset()) {

					channel.setResisterOffsetAlert(true); // 第一次电阻值偏差不产生报警，但记录
				} else {

					channel.setResisterOffsetAlert(false); // 消除上次的电阻偏差报警
				}

			}

			if (protect.getVoltOffset() > 0) {

				if (Math.abs(channelData.getVoltage() - channelData.getDeviceVoltage()) > protect.getVoltOffset()) {
					// 主备压差保护
					if (channel.getRuntimeCaches().size() > 0) {

						ChannelData preData = channel.getRuntimeCaches().get(0);
						if(Math.abs(preData.getVoltage() - preData.getDeviceVoltage()) > protect.getVoltOffset()) {
							
							channelData.setImportantData(true);
							try {
								Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TOUCH,
										I18N.getVal(I18N.VoltageOffset, channelData.getVoltage(), channelData.getDeviceVoltage()
												, protect.getVoltOffset()), channel);
							} catch (AlertException e) {
								Context.getPcNetworkService().pushSendQueue(e);
								logger.error(CommonUtil.getThrowableException(e));
							}
						}
					}
				}
			}

		}
		if (channelData.getWorkMode() != WorkMode.SLEEP && channelData.getWorkMode() != WorkMode.SYNC) {
			// 电流过低保护
			if (channelData.getCurrent() <= MainBoard.startupCfg.getMinRunningCurrent()) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService()
							.executeChannelsAlertInLogic(
									MainEnvironment.AlertCode.CUR_LOWER, I18N.getVal(I18N.CurrLowerProtect,
											channelData.getCurrent(), MainBoard.startupCfg.getMinRunningCurrent()),
									channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;
			}
		}

		// 步次时间保护
		Step step = channel.getProcedureStep(channelData.getStepIndex());
		if (step != null && step.timeProtect && step.overTime > 0 && channelData.getTimeStepSpend() > step.overTime) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService()
						.executeChannelsAlertInLogic(
								MainEnvironment.AlertCode.TIME_OVER, I18N.getVal(I18N.StepTimeout,
										channel.getStepIndex(), channelData.getTimeStepSpend(), step.overTime),
								channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}

		// 超温保护

	}

	/**
	 * 处理首步次CC保护
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelData
	 */
	private void processFirstCCProtection(Channel channel, ChannelData channelData) {

		if (channelData.getState() != ChnState.RUN) {

			return;
		}
		if (channelData.getWorkMode() != WorkMode.CCC) {

			return;
		}

		if (channel.getFirstCCStepIndex() != channelData.getStepIndex()) {

			return;
		}
		ParameterName pn = channel.getControlUnit().getCurrentPn();
		if (pn == null) {

			return;
		}
		FirstCCProtectData tpd = pn.getFirstCCProtect();

		if (tpd.isNeedCheck()
				&& (channelData.getVoltage() < tpd.getVoltLower() || channelData.getVoltage() > tpd.getVoltUpper())
				&& channelData.getTimeStepSpend() > tpd.getTimeOut()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
						I18N.getVal(I18N.FirstCCProtect, channelData.getTimeStepSpend(), channelData.getVoltage(),
								tpd.getVoltLower(), tpd.getVoltUpper()),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}

			return;
		}

	}

	/**
	 * 处理DC保护
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelData
	 */
	private void processDCProtection(Channel channel, ChannelData channelData) {

		if (channelData.getState() != ChnState.RUN || channelData.getStepIndex() == 0) {

			return;
		}
		if (channelData.getWorkMode() != WorkMode.CCD) {

			return;
		}

		Step step = channel.getProcedureStep(channelData.getStepIndex());

		DCProtectData protect = null;
		if (step == null || step.protection == null) {
			ParameterName pn = channel.getControlUnit().getCurrentPn();
			if (pn == null) {

				return;
			}
			protect = pn.getDcProtect();
		} else {

			protect = (DCProtectData) step.protection;
		}

		// 检查电压上限保护值
		if (protect.getVoltUpper() > 0 && channelData.getVoltage() > protect.getVoltUpper()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_UPPER,
						I18N.getVal(I18N.DCVoltUpper, channelData.getVoltage(), protect.getVoltUpper()), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}
		// 检查电压下限保护值
		if (protect.getVoltLower() > 0 && channelData.getVoltage() < protect.getVoltLower()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_LOWER,
						I18N.getVal(I18N.DCVoltLower, channelData.getVoltage(), protect.getVoltLower()), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}

		if (!channel.getRuntimeCaches().isEmpty()) {

			ChannelData preData = channel.getRuntimeCaches().get(0);
			if (preData.getStepIndex() == channelData.getStepIndex()
					&& preData.getLoopIndex() == channelData.getLoopIndex() && preData.getState() == ChnState.RUN) {

				step = channel.getProcedureStep(channelData.getStepIndex());
				if (step == null) {

					return;
				}
				// 电流超差保护
				if (protect.getCurrOffsetVal() > 0 || protect.getCurrOffsetPercent() > 0) {

					double offset = protect.getCurrOffsetPercent() * 0.01 * channelData.getCurrent() >= protect
							.getCurrOffsetVal() ? protect.getCurrOffsetPercent() * 0.01 * channelData.getCurrent()
									: protect.getCurrOffsetVal();

					if (Math.abs(channelData.getCurrent() - step.specialCurrent) > offset
							&& Math.abs(preData.getCurrent() - step.specialCurrent) > offset) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CURR_WAVE,
									I18N.getVal(I18N.DCCurOffset, step.specialCurrent, channelData.getCurrent()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;
					}
				}
				// 电压超差保护2000mAh以下
				if (channelData.getAccumulateCapacity() < 2000
						&& (protect.getVoltWaveValUnder2000() > 0 || protect.getVoltWaveValUnder2000() > 0)) {

					double offset = protect.getVoltWaveValUnder2000() >= protect.getVoltWavePercentUnder2000() * 0.01
							* channelData.getVoltage() ? protect.getVoltWaveValUnder2000()
									: protect.getVoltWavePercentUnder2000() * 0.01 * channelData.getVoltage();

					if (preData.getState() == ChnState.RUN && preData.getStepIndex() == channelData.getStepIndex()
							&& preData.getLoopIndex() == channelData.getLoopIndex()) {
						if (Math.abs(channelData.getVoltage() - preData.getVoltage()) > offset) {

							try {
								channelData.setImportantData(true);
								Context.getCoreService().executeChannelsAlertInLogic(
										MainEnvironment.AlertCode.VOLT_WAVE, I18N.getVal(I18N.DCVoltOffset2000d,
												channelData.getVoltage(), preData.getVoltage()),
										channel);
							} catch (AlertException e) {
								Context.getPcNetworkService().pushSendQueue(e);
								logger.error(CommonUtil.getThrowableException(e));
							}
							return;
						}
					}
				} else if (channelData.getAccumulateCapacity() >= 2000
						&& (protect.getVoltWaveValAbove2000() > 0 || protect.getVoltWaveValAbove2000() > 0)) {

					double offset = protect.getVoltWaveValAbove2000() >= protect.getVoltWavePercentAbove2000() * 0.01
							* channelData.getVoltage() ? protect.getVoltWaveValAbove2000()
									: protect.getVoltWavePercentAbove2000() * 0.01 * channelData.getVoltage();
					if (preData.getState() == ChnState.RUN && preData.getStepIndex() == channelData.getStepIndex()
							&& preData.getLoopIndex() == channelData.getLoopIndex()) {
						if (Math.abs(channelData.getVoltage() - preData.getVoltage()) > offset) {

							try {
								channelData.setImportantData(true);
								Context.getCoreService().executeChannelsAlertInLogic(
										MainEnvironment.AlertCode.VOLT_WAVE, I18N.getVal(I18N.DCVoltOffset2000u,
												channelData.getVoltage(), preData.getVoltage()),
										channel);
							} catch (AlertException e) {
								Context.getPcNetworkService().pushSendQueue(e);
								logger.error(CommonUtil.getThrowableException(e));
							}
							return;
						}
					}
				}

				// 检查连续电压上升个数
				if (protect.getVoltAscVal() > 0 && protect.getVoltAscCount() > 0) {

					if (preData.getStepIndex() == channelData.getStepIndex()
							&& preData.getLoopIndex() == channelData.getLoopIndex()) {
						if (channelData.getVoltage() - preData.getVoltage() >= protect.getVoltAscVal()) {

							if (channel.getExceptionCaches().isEmpty()) {

								channel.appendExceptionData(preData);
							}
							channel.appendExceptionData(channelData);
						} else {

							channel.clearExceptionCaches(); // 清空缓存
						}
					}

					if (channel.getExceptionCaches().size() >= protect.getVoltAscCount() + 1) {

						StringBuffer info = new StringBuffer(I18N.getVal(I18N.DCVoltAsc));
						for (int n = 0; n < channel.getExceptionCaches().size(); n++) {

							info.append(CommonUtil.formatNumber(channel.getExceptionCaches().get(n).getVoltage(), 1)
									+ "mV->");

						}
						info = new StringBuffer(info.substring(0, info.length() - 2));
						info.append("]");

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									info.toString(), channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}

						channel.clearExceptionCaches(); // 清空缓存
						return;
					}

				}

			}
		}

		// 设置默认斜率保护值

		if (!MainBoard.startupCfg.isDisableDefaultProtection()) {
			if (protect.getVoltDescUnitSeconds() == 0) {

				protect.setVoltDescUnitSeconds(DEFAULT_SLOPE_TIME);
			}
			if (protect.getVoltDescValLower() == 0) {

				protect.setVoltDescValLower(DEFAULT_SLOPE_LOWER);
			}
		}

		// 斜率保护
		if (protect.getVoltDescUnitSeconds() > 0
				&& (protect.getVoltDescValUpper() > 0 || protect.getVoltDescValLower() > 0)) {

			if (channel.getSlopeCaches().isEmpty()) {

				channel.appendSlopeCaches(channelData);

			} else {

				ChannelData slopeData = channel.getSlopeCaches().get(channel.getSlopeCaches().size() - 1);

				if (slopeData.getStepIndex() != channelData.getStepIndex()
						|| slopeData.getLoopIndex() != channelData.getLoopIndex()) {

					// 不再同个步次的斜率保护则不保护
					channel.clearSlopeCaches();
					channel.appendSlopeCaches(channelData);

				} else {

					// 下降时间差
					long seconds = channelData.getTimeStepSpend() - slopeData.getTimeStepSpend();

					if (protect.getVoltDescValLower() > 0 && seconds >= protect.getVoltDescUnitSeconds()
							&& slopeData.getVoltage() - channelData.getVoltage() < protect.getVoltDescValLower()) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.DCSlopeLower, seconds, slopeData.getVoltage(),
											channelData.getVoltage()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;
					}

					if (protect.getVoltDescValUpper() > 0
							&& slopeData.getVoltage() - channelData.getVoltage() > protect.getVoltDescValUpper()) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.DCSlopeUpper, seconds, slopeData.getVoltage(),
											channelData.getVoltage()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}
						return;

					}

					if (seconds >= protect.getVoltDescUnitSeconds()) {

						channel.clearSlopeCaches();
						channel.appendSlopeCaches(channelData);

					}

				}
			}

		}

		// 容量保护
		if (protect.getCapacityUpper() > 0 && channelData.getCapacity() >= protect.getCapacityUpper()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CAPACITY_UPPER,
						I18N.getVal(I18N.DCCapacityUpper, channelData.getCapacity(), protect.getCapacityUpper()),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;

		}
		// 时间保护
		if (protect.getMinuteUpper() > 0 && channelData.getTimeStepSpend() > protect.getMinuteUpper() * 60) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CAPACITY_UPPER,
						I18N.getVal(I18N.DCTimeUpper, channelData.getTimeStepSpend(), protect.getMinuteUpper() * 60),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}

	}

	/**
	 * 处理CV保护
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelData
	 */
	private void processCVProtection(Channel channel, ChannelData channelData) {

		if (channelData.getState() != ChnState.RUN || channelData.getStepIndex() == 0) {

			return;
		}
		if (channelData.getWorkMode() != WorkMode.CVC && channelData.getWorkMode() != WorkMode.CC_CV) {

			return;
		}
		if (channelData.getWorkMode() == WorkMode.CC_CV) {

			// 判断是否在cc模式
			ProcedureData procedure = channel.getControlUnit().getProcedure();
			Step step = procedure.getStep(channelData.getStepIndex() - 1);
			if (!DataProcessService.isCvInCcCv(step, channel, channelData)) {

				return;
			}

		}
		Step step = channel.getProcedureStep(channelData.getStepIndex());
		CVProtectData protect = null;
		if (step == null || step.protection == null) {
			ParameterName pn = channel.getControlUnit().getCurrentPn();
			if (pn == null) {

				return;
			}
			protect = pn.getCvProtect();
		} else {

			if (step.protection instanceof CCVProtectData) {

				protect = ((CCVProtectData) step.protection).getCvProtct();
			} else {

				protect = (CVProtectData) step.protection;
			}

		}

		if (protect.getCurrUpper() > 0 && channelData.getCurrent() > protect.getCurrUpper()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CUR_UPPER,
						I18N.getVal(I18N.CVCurUpper, channelData.getCurrent(), protect.getCurrUpper()), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}

			return;
		}
		// 检查电流下限保护值
		if (protect.getCurrLower() > 0 && channelData.getCurrent() < protect.getCurrLower()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CUR_UPPER,
						I18N.getVal(I18N.CVCurLower, channelData.getCurrent(), protect.getCurrLower()), channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}

			return;

		}

		// if (channel.isCvInCcCvMode(channelData)) { //
		// 保证cv步次已经真正进入cv，有时候cv恢复时实际可能在cc充电模式下
		// 电压超差波动保护
		if (!channel.getRuntimeCaches().isEmpty()) {
			ChannelData preData = channel.getRuntimeCaches().get(0);
			if (preData.getState() == ChnState.RUN && preData.getStepIndex() == channelData.getStepIndex()
					&& preData.getLoopIndex() == channelData.getLoopIndex()) {

				step = channel.getProcedureStep(channelData.getStepIndex());
				if (step == null) {

					return;
				}

				if (protect.getVoltOffsetPercent() > 0 || protect.getVoltOffsetVal() > 0) {
					double offsetVoltage = protect.getVoltOffsetPercent() * 0.01 * channelData.getVoltage();
					double offset = offsetVoltage >= protect.getVoltOffsetVal() ? offsetVoltage
							: protect.getVoltOffsetVal();
					// 检查电压波动
					if (offset > 0 && Math.abs(channelData.getVoltage() - step.specialVoltage) > offset) {

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
									I18N.getVal(I18N.CVVoltOffset, step.specialVoltage, channelData.getVoltage()),
									channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}

						return;
					}
				}

				// 检查连续电流上升个数
				if (protect.getCurrAscCount() > 0 && protect.getCurrAscVal() > 0) {

					if (channelData.getCurrent() - preData.getCurrent() >= protect.getCurrAscVal()) {

						channel.appendExceptionData(channelData);
					} else {

						channel.clearExceptionCaches(); // 清空缓存
					}

					if (channel.getExceptionCaches().size() > protect.getCurrAscCount()) {

						StringBuffer info = new StringBuffer(I18N.getVal(I18N.CVCurAsc));
						for (int n = 0; n < channel.getExceptionCaches().size(); n++) {

							info.append(CommonUtil.formatNumber(channel.getExceptionCaches().get(n).getCurrent(), 1)
									+ "mA->");

						}
						info.substring(0, info.length() - 2);
						info.append("]");

						try {
							channelData.setImportantData(true);
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CURR_WAVE,
									info.toString(), channel);
						} catch (AlertException e) {
							Context.getPcNetworkService().pushSendQueue(e);
							logger.error(CommonUtil.getThrowableException(e));
						}

						channel.clearExceptionCaches(); // 清空缓存
						return;
					}

				}

			} // loop == loop , step == step
		}
		// }

		// 容量保护
		if (protect.getCapacityUpper() > 0 && channelData.getCapacity() >= protect.getCapacityUpper()) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CAPACITY_UPPER,
						I18N.getVal(I18N.CVCapacityUpper, channelData.getCapacity(), protect.getCapacityUpper()),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;

		}
		// 时间保护
		if (protect.getMinuteUpper() > 0 && channelData.getTimeStepSpend() > protect.getMinuteUpper() * 60) {

			try {
				channelData.setImportantData(true);
				Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.TIME_OVER,
						I18N.getVal(I18N.CVTimeUpper, channelData.getTimeStepSpend(), protect.getMinuteUpper() * 60),
						channel);
			} catch (AlertException e) {
				Context.getPcNetworkService().pushSendQueue(e);
				logger.error(CommonUtil.getThrowableException(e));
			}
			return;
		}

	}

	/**
	 * 处理接触电阻智能保护
	 * 
	 * @author wavy_zheng 2022年5月4日
	 * @param channel
	 * @param channelData
	 */
	private void processSmartResisterProtection(Channel channel, ChannelData channelData) {

		if (!MainBoard.startupCfg.getSmartProtects().enalbe || !MainBoard.startupCfg.getSmartProtects().touch.enable) {

			return;
		}

	}

	/**
	 * 启用压差保护
	 * 
	 * @author wavy_zheng 2021年4月21日
	 * @param channel
	 * @param channelData
	 * @return
	 */
	private void processVoltageOffsetProtection(Channel channel, ChannelData channelData) {

		double startVoltOffset = MainBoard.startupCfg.getRange().voltageStartOffset;
		if (startVoltOffset == 0 || MainBoard.startupCfg.getProductType() == ProductType.POWERBOX) {

			startVoltOffset = 200; // 直接使用最大值
		}
		if (channelData.getVoltage() < DEFAULT_MIN_VOLTAGE_OFFSET) {

			return;
		}
		MainBoard mb = channel.getControlUnit().getMainBoard();

		if (MainBoard.startupCfg.getProductType() == ProductType.POWERBOX && !mb.isPressureOk()) {

			return;
		}
		if (channelData.getPowerVoltage() < 1 || channelData.getVoltage() < 1 || channelData.getDeviceVoltage() < 1) {

			return;
		}

		if (channelData.getState() == ChnState.UDT || channelData.getState() == ChnState.ALERT
		/* || channelData.getState() == ChnState.PAUSE */ || channelData.getState() == ChnState.STOP) {

			// if (!channel.getRuntimeCaches().isEmpty()) {

			channel.appendTouchData(channelData);
			if (channel.getTouchData().size() > TOUCH_VOLT_PICK_COUNT) {

				channel.getTouchData().subList(0, 1).clear(); // 删除第一个
			}
			ChannelData alertData = null;
			if ((alertData = isVoltageOffsetAlert(channel)) != null && channelData.getState() != ChnState.ALERT) {

				channel.log("voltage offset protect:" + channel.getTouchData().toString());
				channelData.setImportantData(true);
				// 记录压差保护数据
				channel.alert(MainEnvironment.AlertCode.VOLT_WAVE,
						I18N.getVal(I18N.StartVoltOffset, alertData.getVoltage(), alertData.getDeviceVoltage(),
								alertData.getPowerVoltage(), startVoltOffset));
			}
			// }

		}

	}

	/**
	 * 内置重要的保护，此报警无法通过配置文件屏蔽
	 * 
	 * @author wavy_zheng 2021年2月2日
	 * @param channel
	 * @param channelData
	 */
	private void processImportantProtection(Channel channel, ChannelData channelData) {

		// 处理压差保护
		processVoltageOffsetProtection(channel, channelData);

		if (channel.getState() != ChnState.RUN) {

			return;
		}

		// 运行过程中清空缓存
		if (!channel.getTouchData().isEmpty()) {

			channel.clearTouchData();
		}

	}

	/**
	 * 处理流程首尾保护
	 * 
	 * @author wavy_zheng 2021年1月27日
	 * @param channel
	 * @param channelDatas
	 */
	private void processStartEndProtection(Channel channel, ChannelData channelData) {


		double voltageStLower = 0, voltageStUpper = 0, voltageEdLower = 0, voltageEdUpper = 0, capacityEdLower = 0,
				capacityEdUpper = 0;

		StartEndCheckData sec = channel.getControlUnit().getSec();
		if (sec == null) {

			return;
		}

		voltageStLower = sec.getStartVoltageLower();
		voltageStUpper = sec.getStartVoltageUpper();

		voltageEdLower = sec.getEndVoltageLower();
		voltageEdUpper = sec.getEndVoltageUpper();

		capacityEdLower = sec.getEndCapacityLower();
		capacityEdUpper = sec.getEndCapacityUpper();

		if (channel.getState() == ChnState.RUN &&
				channelData.getStepIndex() == 1 && channelData.getLoopIndex() == 1) {
			// 流程启动保护

			if (voltageStLower > 0 && channelData.getVoltage() < voltageStLower) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_LOWER,
							I18N.getVal(I18N.StartVoltLower, channelData.getVoltage(), voltageStLower), channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;

			} else if (voltageStUpper > 0 && channelData.getVoltage() > voltageStUpper) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_UPPER,
							I18N.getVal(I18N.StartVoltUpper, channelData.getVoltage(), voltageStUpper), channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;
			}

		}

		// 结束判断
		if (channel.getState() == ChnState.COMPLETE ) {

			if (voltageEdLower > 0 && channelData.getVoltage() < voltageEdLower) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_LOWER,
							I18N.getVal(I18N.EndVoltLower, channelData.getVoltage(), voltageEdLower), channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;

			} else if (voltageEdUpper > 0 && channelData.getVoltage() > voltageEdUpper) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_UPPER,
							I18N.getVal(I18N.EndVoltUpper, channelData.getVoltage(), voltageEdUpper), channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;
			}

			// 结束容量判断
			if (capacityEdLower > 0 && channelData.getAccumulateCapacity() < capacityEdLower) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CAPACITY_UPPER,
							I18N.getVal(I18N.EndCapacityLower, channelData.getAccumulateCapacity(), capacityEdLower),
							channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;

			} else if (capacityEdUpper > 0 && channelData.getAccumulateCapacity() > capacityEdUpper) {

				try {
					channelData.setImportantData(true);
					Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CAPACITY_UPPER,
							I18N.getVal(I18N.EndCapacityUpper, channelData.getAccumulateCapacity(), capacityEdUpper),
							channel);
				} catch (AlertException e) {
					Context.getPcNetworkService().pushSendQueue(e);
					logger.error(CommonUtil.getThrowableException(e));
				}
				return;

			}

		}

	}

	/**
	 * 处理极性反接报警
	 * 
	 * @author wavy_zheng 2021年1月26日
	 * @param channel
	 * @param rawDatas
	 */
	private void processReversePole(Channel channel, List<ChnDataPack> rawDatas) {

		if (channel.getState() == ChnState.ALERT || channel.getState() == ChnState.CLOSE
				|| channel.getState() == ChnState.NONE) {

			return;
		}
		for (ChnDataPack chnData : rawDatas) {

			if (chnData.getState() == DriverEnvironment.ChnState.EXCEPT
					&& chnData.getAlertCode() == DriverEnvironment.AlertCode.POLE_REVERSE) {

				channel.setMonitorPoleCount(channel.getMonitorPoleCount() + 1);
				if (channel.getMonitorPoleCount() >= CONTINUE_POLE_COUNT) {

					channel.alert(MainEnvironment.AlertCode.POLE_REVERSE, I18N.getVal(I18N.LogicPoleReverse));
					channel.setMonitorPoleCount(0);
				}

			} else if (chnData.getState() != DriverEnvironment.ChnState.EXCEPT) {

				channel.setMonitorPoleCount(0);
			}
		}

	}

	/**
	 * 关闭通道
	 * 
	 * @author wavy_zheng 2021年1月26日
	 * @param channel
	 * @throws AlertException
	 */
	/*
	 * public void closeChannelByOverVoltage(Channel channel) throws AlertException
	 * {
	 * 
	 * logger.info("close channel " + channel.getDeviceChnIndex() +
	 * " because over voltage"); // 关闭通道
	 * Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.
	 * AlertCode.VOLT_UPPER, I18N.CheckVoltageOver, channel); //
	 * Context.getCoreService().executeChannelsProcedureInLogic(ChnOpt.STOP, //
	 * channel); // 通知回检板 CheckBoard cb =
	 * channel.getLogic().getMainBoard().getCheckBoards().get(channel.getLogicIndex(
	 * )); Check2ConfirmCloseData cccd = new Check2ConfirmCloseData();
	 * cccd.setUnitIndex(channel.getLogicIndex()); int chnIndex =
	 * Context.getChannelIndexService().getActualLogicChnIndexBy(cb.getCheckIndex(),
	 * channel.getLogicChnIndex()); cccd.setChnIndex(chnIndex);
	 * 
	 * cccd.setClosed(true);
	 * Context.getCheckboardService().writeConfirmCloseData(cccd);
	 * 
	 * }
	 */

	@Override
	public List<ChnDataPack> filterRawDatas(Channel channel, List<ChnDataPack> rawDatas) {

		if (channel.getAlertCode() != MainEnvironment.AlertCode.DEVICE_ERROR && channel.getState() != ChnState.CLOSE) {

			if (channel.getCheckChnData() != null
					&& channel.getCheckChnData().getChnState() == Check2Environment.ChnState.EXCEPT
					&& channel.getCheckChnData().getAlertCode() == AlertCode.DEV_VOLT_OVER) {

				// 超压
				DeviceProtectData dpd = channel.getControlUnit().getDpd();

				/*
				 * if (!channel.isCheckboardMonitor()) {
				 * 
				 * logger.info("checkboard start to  monitor over voltage");
				 * channel.setCheckboardMonitor(true);
				 * 
				 * try { closeChannelByOverVoltage(channel);
				 * channel.setCheckMonitorVoltSt(System.currentTimeMillis()); // 设置监视点 } catch
				 * (AlertException e) {
				 * 
				 * logger.error(CommonUtil.getThrowableException(e));
				 * 
				 * }
				 * 
				 * } else {
				 */

				logger.info("check board response second over voltage exception ,trigger alert");
				// 超压报警
				channel.setCheckboardMonitor(false);
				channel.alert(MainEnvironment.AlertCode.DEVICE_ERROR, I18N.getVal(I18N.CheckDeviceOverVolt,
						channel.getCheckChnData().getAlertVoltage(), dpd.getDeviceVoltUpper()));

				try {
					Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
							I18N.getVal(I18N.DeviceError), false);
				} catch (AlertException e) {

					e.printStackTrace();
				}

				// }

			}

		}

		if (channel.getState() != ChnState.ALERT && channel.getState() != ChnState.CLOSE) {
			for (ChnDataPack chnData : rawDatas) {

				if (chnData.getState() == DriverEnvironment.ChnState.EXCEPT) {

					if (chnData.getAlertCode() == DriverEnvironment.AlertCode.OVER_VOLT) {

						DeviceProtectData dpd = channel.getControlUnit().getDpd();

						// 超压保护
						channel.alert(MainEnvironment.AlertCode.VOLT_UPPER,
								I18N.getVal(I18N.LogicBatOverVolt, chnData.getAlertVolt(), dpd.getBatVoltUpper()));

					} else if (chnData.getAlertCode() == DriverEnvironment.AlertCode.OVER_CURR) {

						DeviceProtectData dpd = channel.getControlUnit().getDpd();

						System.out.println("alert data:" + chnData);

						channel.alert(MainEnvironment.AlertCode.CUR_UPPER,
								I18N.getVal(I18N.LogicBatOverCurr, chnData.getAlertCurrent(), dpd.getCurrUpper()));
					} else if (chnData.getAlertCode() == DriverEnvironment.AlertCode.OVER_TIME) {

						Step step = channel.getProcedureStep(channel.getStepIndex());

						if (step != null) {
							// 步次超时报警
							channel.alert(MainEnvironment.AlertCode.TIME_OVER,
									I18N.getVal(I18N.LogicOverTime, chnData.getAlertTime() / 1000, step.overTime));
						}
					} else {

						if (chnData.getAlertCode() != DriverEnvironment.AlertCode.POLE_REVERSE
								&& channel.getState() != ChnState.NONE) {

							// 反接专门由processReversePole处理
							// 逻辑板异常
							channel.alert(MainEnvironment.AlertCode.TIME_OVER, I18N.getVal(I18N.LogicUnknownExcept,
									chnData.getAlertCode() == null ? -1 : chnData.getAlertCode().ordinal()));
						}
					}

					// 为了防止逻辑板没有关闭，主控再次进行关闭

				}
			}
		}

		// 主控处理反极性保护
		processReversePole(channel, rawDatas);

		for (ChnDataPack rawData : rawDatas) {
			if (isLogicStopSelf(channel, rawData)) {

				channel.log("channel exception terminal :" + rawData.toString());
				channel.alert(MainEnvironment.AlertCode.TIME_OVER,
						I18N.getVal(I18N.LogicChnSelfStop, rawData.getState().name()));
			}
			/**
			 * 因派能经常出现此保护，先关闭切断逆变
			 */

			if (isLogicNotClosedNormally(channel, rawData)) {

				// 通道异常有电流
			}

		}

		return rawDatas;
	}

	/**
	 * 需要在过滤前就要处理报警的保护
	 * 
	 * @author wavy_zheng 2022年8月6日
	 * @param channel
	 * @param channelDatas
	 */
	public void processFirstProtections(Channel channel, List<ChannelData> channelDatas) {

		if (channelDatas.size() > 0) {

			// 压差保护只处理最后一条
			processImportantProtection(channel, channelDatas.get(channelDatas.size() - 1));
		}

		// 主控检查超压保护
		processOverVoltage(channel, channelDatas);

	}

	@Override
	public List<ChannelData> filterChannelDatas(Channel channel, List<ChannelData> channelDatas) {

		// 重要保护

		/**
		 * 移植到数据处理过滤器
		 */
		/*
		 * if (channelDatas.size() > 0) {
		 * 
		 * // 压差保护只处理最后一条 processImportantProtection(channel,
		 * channelDatas.get(channelDatas.size() - 1)); }
		 * 
		 * // 主控检查超压保护 processOverVoltage(channel, channelDatas);
		 */

		if (MainBoard.startupCfg.isUseAlert()) {

			for (int n = 0; n < channelDatas.size(); n++) {

				ChannelData chnData = channelDatas.get(n);
				// if (channel.getStepIndex() == chnData.getStepIndex()
				// && channel.getLoopIndex() == chnData.getStepIndex()) {

				if (channel.getState() == ChnState.ALERT) {
					chnData.setState(ChnState.ALERT);
				}

				// 流程首尾保护
				processStartEndProtection(channel, chnData);

				boolean lastStep = false;
				if (n == channelDatas.size() - 1) {

					if (MainBoard.startupCfg.isUseStepChangeProtect()) {
						// 转步次保护
						lastStep = processOverStepChange(channel, chnData);
					}
				}

				/**
				 * 去除，由电流超差和电压超差保护 注意，最后一个数据无论如何都要加入保护域
				 */
				if (channel.getLeadStepCount() > 0 && !lastStep) {

					continue;
				}

				// 设备一级保护
				processDeviceProtect(channel, chnData);

				// CC保护
				processCCProtection(channel, chnData);

				// CV保护
				processCVProtection(channel, chnData);

				// dc保护
				processDCProtection(channel, chnData);

				// 休眠保护
				processSleepProtection(channel, chnData);

				// 其他保护
				processOtherProtection(channel, chnData);

			}

			// }
		}

		return channelDatas;
	}

	/**
	 * 判断逻辑板自行发生了停止或复位
	 * 
	 * @author wavy_zheng 2021年3月3日
	 * @param channel
	 * @param chnData
	 * @return
	 */
	private boolean isLogicStopSelf(Channel channel, ChnDataPack chnData) {

		if (channel.getState() == ChnState.RUN) {

			if (chnData.getState() != DriverEnvironment.ChnState.RUNNING
					&& chnData.getState() != DriverEnvironment.ChnState.COMPLETE
					&& chnData.getState() != DriverEnvironment.ChnState.STOP) {

				// 逻辑板发生了正常停止以外的状态
				return true;
			}

		}

		return false;
	}

	/**
	 * 逻辑板未正常关闭
	 * 
	 * @author wavy_zheng 2021年4月7日
	 * @param channel
	 * @param chnData
	 * @return
	 */
	private boolean isLogicNotClosedNormally(Channel channel, ChnDataPack chnData) {

		if (channel.getControlUnit().getState() == State.FORMATION && channel.getState() == ChnState.ALERT) {

			boolean except = false;
			if ((chnData.getState() == DriverEnvironment.ChnState.EXCEPT
					|| chnData.getState() == DriverEnvironment.ChnState.STOP)
					&& chnData.getCurrent() > MainBoard.startupCfg.getRange().disableCurrentLine * 3) {

				except = true;
			}

			if (chnData.getState() == DriverEnvironment.ChnState.RUNNING && chnData.getCurrent() > 0) {

				except = true;
			}

			if (except) {
				if (channel.getNotClosedRawData().size() <= 20) {

					try {
						channel.appendNotClosedRawData(chnData);
						channel.log("alert or close state have current:" + chnData);
					} catch (AlertException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (channel.getNotClosedRawData().size() == 20) {

						// 尝试再次关闭
						try {
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CURR_WAVE,
									null, channel);
						} catch (AlertException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else {

					channel.log("alert or close state have current:" + chnData);
					chnData.setImportant(true);
					channel.clearNotClosedRawData();
					try {
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.DEVICE_ERROR,
								I18N.getVal(I18N.ChnCurrentException, channel.getDeviceChnIndex() + 1,
										chnData.getCurrent()),
								channel);
					} catch (AlertException e1) {

						e1.printStackTrace();
					}

					try {
						Context.getAlertManager().handle(MainEnvironment.AlertCode.DEVICE_ERROR,
								I18N.getVal(I18N.ChnCurrentException, channel.getDeviceChnIndex() + 1,
										chnData.getCurrent()),
								false);
					} catch (AlertException e) {

						e.printStackTrace();
					}

					return true;
				}
			} else {

				channel.clearNotClosedRawData();
			}

		} else {

			channel.clearNotClosedRawData();

		}
		return false;
	}

	/**
	 * 恒流恒压保护
	 * 
	 * @author wavy_zheng 2021年1月16日
	 * @param channel
	 * @param channleData
	 */
	private void checkConstProtection(Channel channel, ChannelData channelData) {

		if (channel.getState() != ChnState.RUN) {

			return;
		}
		Step step = channel.getProcedureStep(channelData.getStepIndex());
		if (step == null) {

			return;
		}
		if (step.workMode == WorkMode.CCC || step.workMode == WorkMode.CCD) {

			// 获取精度范围
			RangeSection rs = DataFilterService.findSectionByCurrent(step.specialCurrent);
			if (rs != null) {

				// 恒流保护
				if (Math.abs(channelData.getCurrent() - step.specialCurrent) > rs.precision * 3) {

					channel.appendConstData(channelData);
					if (channel.getConstData().size() >= MAX_CONST_OFFSET_COUNT) {
						try {
							Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.CURR_WAVE,
									I18N.getVal(I18N.CurrentConstProtection, channelData.getCurrent(),
											step.getSpecialCurrent()),
									channel);
						} catch (AlertException e) {

							e.printStackTrace();
						}
					} else {

					}
				} else {

					channel.clearConstCache(); // 需要连续指定次数偏离恒流范围
				}
			}

		} else if (step.workMode == WorkMode.CVC) {

			if (Math.abs(
					channelData.getVoltage() - step.specialVoltage) > MainBoard.startupCfg.getRange().voltagePrecision
							* 3) {

				channel.appendConstData(channelData);
				if (channel.getConstData().size() >= MAX_CONST_OFFSET_COUNT) {
					try {
						Context.getCoreService().executeChannelsAlertInLogic(MainEnvironment.AlertCode.VOLT_WAVE,
								I18N.getVal(I18N.VoltageConstProtection, channelData.getVoltage(), step.specialVoltage),
								channel);
					} catch (AlertException e) {

						e.printStackTrace();
					}
				} else {

					channel.clearConstCache(); // 需要连续指定次数偏离恒流范围
				}

			}
		}

	}

	/**
	 * 是否触发压差保护
	 * 
	 * @author wavy_zheng 2021年4月13日
	 * @param channel
	 * @return null没有触发压差保护
	 */
	public static ChannelData isVoltageOffsetAlert(Channel channel) {

		double startVoltOffset = MainBoard.startupCfg.getRange().voltageStartOffset;
		if (startVoltOffset == 0 || MainBoard.startupCfg.getProductType() == ProductType.POWERBOX) {

			startVoltOffset = 200;
		}
		ChannelData alertData = null;
		int touchCount = 0;
		for (int n = 0; n < channel.getTouchData().size(); n++) {

			ChannelData touchData = channel.getTouchData().get(n);
			if (Math.abs(touchData.getVoltage() - touchData.getDeviceVoltage()) >= startVoltOffset
					|| Math.abs(touchData.getVoltage() - touchData.getPowerVoltage()) >= startVoltOffset
					|| Math.abs(touchData.getPowerVoltage() - touchData.getDeviceVoltage()) >= startVoltOffset) {

				touchCount++;
				alertData = touchData;

			}

		}
		if (touchCount >= TOUCH_VOLT_PICK_ALERT_COUNT) {

			return alertData;
		} else {

			return null;
		}

	}

}
