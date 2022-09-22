package com.nlteck.service.data.virtual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.RangeSection;
import com.nlteck.service.data.DataProcessService;
import com.nlteck.service.data.DataProviderService;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.driver.DriverEnvironment.AlertCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.ChnState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPickupData;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.protocol.power.driver.DriverPoleData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverResumeData.ResumeUnit;
import com.nltecklib.protocol.power.driver.DriverStepData;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年1月17日 下午5:01:24 驱动板数据虚拟提供器
 */
public class VirtualDriverChnDataProvider {

	private final static double STANDARD_CAPACITY = 5000; // 假设电芯是标准容量5000mAh
	private final static double MIN_VOLTAGE = 3000; // 标准放电容量的最小电压
	private final static double MAX_VOLTAGE = 4400; // 标准充电容量的最大电压

	private final static double BAT_DROP_OFFSET = 0.02; // 充放电反向电压跌落比例
	private final static double CC_CV_CAP_RATE = 0.90; // cc-cv容量比例

	private final static int PRODUCT_COUNT_PER_HOUR = 3600; // 每个小时产生多少个数据
	private final static double VOLTAGE_PRECISION = 0.0003; // 万分之3的精度偏差
	private final static double CURRENT_PRECISION = 0.0002; // 万分之2的精度偏差

	private final static double CV_RANGE_OFFSET = 5.0; // 恒压范围

	private List<VirtualDriver> drivers = new ArrayList<>();

	public VirtualDriverChnDataProvider() {

		// 自动初始化数据提供器
		for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

			VirtualDriver driver = new VirtualDriver(n);
			drivers.add(driver);

		}
		// 默认启动采集
		for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

			startWork(n);
			;
		}

	}

	/**
	 * 模拟产生CC数据
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param channel
	 * @return
	 */
	private ChnDataPack produceCCData(VirtualChannel channel) {

		DriverStepData procedure = channel.getProcedure();

		Step step = procedure.getSteps().get(channel.getStepIndex() - 1);
		// 电压每次刷新增长比例
		double voltSpan = (MAX_VOLTAGE - MIN_VOLTAGE) * CC_CV_CAP_RATE * step.specialCurrent
				/ (STANDARD_CAPACITY * PRODUCT_COUNT_PER_HOUR);
		double current = CommonUtil.produceRandomNumberInRange(step.specialCurrent,
				step.specialCurrent * CURRENT_PRECISION);

		voltSpan = CommonUtil.produceRandomNumberInRange(voltSpan, voltSpan * VOLTAGE_PRECISION);
		System.out.println(voltSpan);

		double voltage = channel.getVoltage() + voltSpan;

		if (channel.getIndex() == 0 && channel.getDriver().getDriverIndex() == 0) {

			// 模拟电压连续下降
			voltage = channel.getVoltage() - 5;
		} else if (channel.getIndex() == 1 && channel.getDriver().getDriverIndex() == 0) {

			// 模拟电流变为5mA以下，触发恒流保护
			current = 4.01;
		}

		refreshChannel(channel, voltage, current);

		ChnDataPack chnData = new ChnDataPack();
		chnData.setChnIndex(channel.getIndex());
		chnData.setAlertCode(AlertCode.NORMAL);
		chnData.setAlertCurrent(0);
		chnData.setAlertVolt(0);
		chnData.setCapacity(channel.getCapacity());
		chnData.setCurrent(current);
		chnData.setVoltage(voltage);
		chnData.setLoopIndex(channel.getLoopIndex());
		chnData.setStepIndex(channel.getStepIndex());
		chnData.setStepElapsedTime(channel.getMiliSeconds());
		chnData.setWorkMode(WorkMode.values()[channel.getWorkMode().ordinal()]);
		chnData.setState(ChnState.RUNNING);

		return chnData;
	}

	/**
	 * 刷新通道数据
	 * 
	 * @author wavy_zheng 2020年11月19日
	 * @param channel
	 * @param voltage
	 * @param current
	 */
	private void refreshChannel(VirtualChannel channel, double voltage, double current) {

		long now = System.currentTimeMillis();
		long time = now - channel.getLastProductTime();
		double capacity = DataProviderService.getDeltaCapacity(current, time);
		channel.setCapacity(channel.getCapacity() + capacity);
		channel.setMiliSeconds(channel.getMiliSeconds() + time);
		channel.setVoltage(voltage);
		channel.setCurrent(current);
		channel.setLastProductTime(now);
	}

	/**
	 * 生成休眠数据
	 * 
	 * @author wavy_zheng 2020年11月19日
	 * @param channel
	 * @return
	 */
	private ChnDataPack produceSleepData(VirtualChannel channel) {

		double voltage = channel.getVoltage();

		refreshChannel(channel, voltage, 0);

		double offset = new Random().nextInt(100);
		if (new Random().nextBoolean()) {

			offset = -offset;
		}

		ChnDataPack chnData = new ChnDataPack();
		chnData.setChnIndex(channel.getIndex());
		chnData.setAlertCode(AlertCode.NORMAL);
		chnData.setAlertCurrent(0);
		chnData.setAlertVolt(0);
		chnData.setCapacity(0);
		chnData.setCurrent(0);
		chnData.setVoltage(voltage /* + offset */); // 模拟休眠电压抖动
		chnData.setLoopIndex(channel.getLoopIndex());
		chnData.setStepIndex(channel.getStepIndex());
		chnData.setStepElapsedTime(channel.getMiliSeconds());
		chnData.setWorkMode(channel.getWorkMode());
		chnData.setState(ChnState.RUNNING);

		return chnData;
	}

	private ChnDataPack produceUDTData(VirtualChannel channel) {

		ChnDataPack chnData = new ChnDataPack();
		chnData.setChnIndex(channel.getIndex());
		chnData.setState(channel.getState());

		chnData.setAlertCode(AlertCode.NORMAL);

		chnData.setAlertCurrent(0);
		chnData.setAlertVolt(0);
		chnData.setCapacity(0);
		chnData.setCurrent(0);
		chnData.setVoltage(channel.getVoltage());
		chnData.setLoopIndex(0);
		chnData.setStepIndex(0);
		chnData.setStepElapsedTime(0);
		chnData.setWorkMode(WorkMode.SLEEP);

		return chnData;

	}

	/**
	 * 模拟产生DC数据
	 * 
	 * @author wavy_zheng 2020年11月19日
	 * @param channel
	 * @return
	 */
	private ChnDataPack produceDCData(VirtualChannel channel) {

		DriverStepData procedure = channel.getProcedure();

		Step step = procedure.getSteps().get(channel.getStepIndex() - 1);
		// 电压每次刷新递减比例
		double voltSpan = (MAX_VOLTAGE - MIN_VOLTAGE) * step.specialCurrent
				/ (STANDARD_CAPACITY * PRODUCT_COUNT_PER_HOUR);
		double current = CommonUtil.produceRandomNumberInRange(step.specialCurrent,
				step.specialCurrent * CURRENT_PRECISION);

		voltSpan = CommonUtil.produceRandomNumberInRange(voltSpan, voltSpan * VOLTAGE_PRECISION);

		double voltage = channel.getVoltage() - voltSpan;

		if (channel.getIndex() == 0) {

			// 模拟电压连续上升
			// voltage = channel.getVoltage() + 30;

		}

		refreshChannel(channel, voltage, current);

		ChnDataPack chnData = new ChnDataPack();
		chnData.setChnIndex(channel.getIndex());
		chnData.setAlertCode(AlertCode.NORMAL);
		chnData.setAlertCurrent(0);
		chnData.setAlertVolt(0);
		chnData.setCapacity(channel.getCapacity());
		chnData.setCurrent(current);

		chnData.setVoltage(voltage);
		chnData.setLoopIndex(channel.getLoopIndex());
		chnData.setStepIndex(channel.getStepIndex());
		chnData.setStepElapsedTime(channel.getMiliSeconds());
		chnData.setWorkMode(channel.getWorkMode());
		chnData.setState(ChnState.RUNNING);

		return chnData;
	}

	/**
	 * 模拟产生CV数据
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param channel
	 * @return
	 */
	private ChnDataPack produceCVData(VirtualChannel channel) {

		Step step = channel.getProcedure().getSteps().get(channel.getStepIndex() - 1);

		double current = channel.getCurrent() <= 0 ? step.specialCurrent : channel.getCurrent();
		channel.setCurrent(current);
		
		double filterRange = 30.0;
		RangeSection rs = DataProcessService.findSectionByCurrent(step.specialCurrent);
		if(rs != null) {
			
			filterRange = rs.currentFilterRange;
		}
		
        if(Math.abs(current - step.specialCurrent) < filterRange) {
			
			//模拟CV电流第一次大幅度下降
        	current = current * 0.9777;
		} else if (current > step.specialCurrent / 2) {
            
			current = current * 0.9977;
		} else if (channel.getCurrent() <= step.specialCurrent / 2 && channel.getCurrent() > step.specialCurrent / 4) {

			double rate = 0.9997;
//			current = channel.getCurrent() * rate + channel.getCurrent() * (1 - rate)
//					* (step.specialCurrent - channel.getCurrent()) / step.specialCurrent;
			current =  current * rate;
			
		} else {

			current = current * 0.99
					+ current * 0.01 * (step.specialCurrent - current) / step.specialCurrent;
		}
		
		
		

//		if (channel.getCurrent() > step.specialCurrent / 2) {
//
//			current = channel.getCurrent() * CC_CV_CAP_RATE;
//		} else if (channel.getCurrent() <= step.specialCurrent / 2 && channel.getCurrent() > step.specialCurrent / 4) {
//
//			double rate = CC_CV_CAP_RATE + (1 - CC_CV_CAP_RATE) / 2;
//			current = channel.getCurrent() * rate + channel.getCurrent() * (1 - rate)
//					* (step.specialCurrent - channel.getCurrent()) / step.specialCurrent;
//		} else {
//
//			current = channel.getCurrent() * 0.99
//					+ channel.getCurrent() * 0.01 * (step.specialCurrent - channel.getCurrent()) / step.specialCurrent;
//		}
//
//		current = CommonUtil.produceRandomNumberInRange(current, CURRENT_PRECISION);

		double voltage = CommonUtil.produceRandomNumberInRange(step.specialVoltage, VOLTAGE_PRECISION);

		refreshChannel(channel, voltage, current);

		/*if (channel.getDriver().getDriverIndex() == 0 && channel.getIndex() == 3) {

			// 模拟电流抖动（往上抖）
			if (channel.getTickCount() % 15 == 0) {

				current = current * 1.2;
			}

		}*/

		ChnDataPack chnData = new ChnDataPack();
		chnData.setChnIndex(channel.getIndex());
		chnData.setAlertCode(AlertCode.NORMAL);
		chnData.setAlertCurrent(0);
		chnData.setAlertVolt(0);
		chnData.setCapacity(channel.getCapacity());
		chnData.setCurrent(current);
		chnData.setVoltage(voltage);
		chnData.setLoopIndex(channel.getLoopIndex());
		chnData.setStepIndex(channel.getStepIndex());
		chnData.setStepElapsedTime(channel.getMiliSeconds());
		chnData.setWorkMode(channel.getWorkMode());
		chnData.setState(ChnState.RUNNING);

		return chnData;
	}

	/**
	 * 过滤数据，判断是否转步
	 * 
	 * @author wavy_zheng 2020年11月19日
	 * @param channel
	 * @param chnData
	 */
	private void controlStep(VirtualChannel channel, ChnDataPack chnData) {

		// 判断是否完结
		Step step = channel.getProcedure().getSteps().get(chnData.getStepIndex() - 1);
		Step nextStep = nextStepFrom(channel.getProcedure(), chnData.getStepIndex(), chnData.getLoopIndex());
		boolean skipNextStep = false;

		double voltage = chnData.getVoltage();

		if (chnData.getWorkMode() == WorkMode.CC) {

			if (voltage >= step.overThreshold && step.overThreshold > 0) {

				skipNextStep = true;

			}
			// if (nextStep.workMode == MainEnvironment.WorkMode.SLEEP) {
			//
			// // 模拟电芯充电完成时瞬间跌落
			// voltage = voltage * 0.99;
			// }

		} else if (chnData.getWorkMode() == WorkMode.DC) {

			if (voltage <= step.overThreshold && step.overThreshold > 0) {

				skipNextStep = true;
			}
			// if (nextStep.workMode == MainEnvironment.WorkMode.SLEEP) {
			//
			// // 模拟电芯放电完成时瞬间反弹
			// voltage = voltage * 1.01;
			// }

		} else if (chnData.getWorkMode() == WorkMode.CV || chnData.getWorkMode() == WorkMode.CC_CV) {

			if (chnData.getCurrent() <= step.overThreshold && step.overThreshold > 0) {

				skipNextStep = true; // 电流低于阀值
			}
			// if (nextStep.workMode == MainEnvironment.WorkMode.SLEEP) {
			//
			// // 模拟电芯充电完成时瞬间跌落
			// voltage = voltage * 0.99;
			// }
		}

		// 是否以时间结束
		if (chnData.getStepElapsedTime() / 1000 - step.overTime >= 0 && step.overTime > 0) {

			if (chnData.getWorkMode() == WorkMode.SLEEP && channel.getDriver().getDriverIndex() == 1) {

				System.out.println(chnData.getStepElapsedTime() + "ms");
				if (chnData.getStepElapsedTime() / 1000 - step.overTime >= 2) {

					skipNextStep = true;
				}

			} else {

				skipNextStep = true;
			}

		}

		// 容量是否达到转步条件
		if (chnData.getCapacity() - step.overCapacity >= 0 && step.overCapacity > 0) {

			skipNextStep = true;
		}

		// △v超限?
		if (chnData.getVoltage() - channel.getStartVoltage() >= step.deltaVoltage && step.deltaVoltage > 0) {

			skipNextStep = true;
		}

		if (step.overMode == OverMode.PROCEED) { // 转步模式为继续才会自动跳转

			if (skipNextStep) {

				if (nextStep == null) {

					// 流程结束，直接关闭通道
					channel.close(false);
					return;
				}

				// 模拟节点数据
				chnData.setAlertVolt(voltage);

				chnData.setAlertCurrent(chnData.getCurrent());

				chnData.setAlertTime(chnData.getStepElapsedTime());

				if (nextStep.workMode != MainEnvironment.WorkMode.SLEEP) { // 模拟跳步次

					boolean skipAfterNext = false;
					if (nextStep.workMode == MainEnvironment.WorkMode.CCC) {

						if (voltage >= nextStep.overThreshold && step.overThreshold > 0) {

							skipAfterNext = true;
						}
					} else if (nextStep.workMode == MainEnvironment.WorkMode.CC_CV
							|| nextStep.workMode == MainEnvironment.WorkMode.CCD) {

						if (voltage <= nextStep.overThreshold && step.overThreshold > 0) {

							skipAfterNext = true;
						}
					}

					if (skipAfterNext) {
						// 检测是否跳步次
						Step afterNextStep = nextStepFrom(channel.getProcedure(), nextStep.getStepIndex(),
								nextStep.getLoopIndex());
						if (afterNextStep != null) {

							nextStep = afterNextStep;
						} else {

							// 流程结束，直接关闭通道
							channel.close(false);
						}
					}
				}

				chnData.setStepIndex(nextStep.stepIndex);
				chnData.setLoopIndex(nextStep.loopIndex);
				chnData.setCapacity(0);
				chnData.setStepElapsedTime(0);
				chnData.setWorkMode(WorkMode.values()[nextStep.workMode.ordinal()]);
				if (nextStep.workMode == MainEnvironment.WorkMode.CCC
						|| nextStep.workMode == MainEnvironment.WorkMode.CC_CV
						|| nextStep.workMode == MainEnvironment.WorkMode.CCD) {

					chnData.setCurrent(nextStep.specialCurrent);
				} else if (nextStep.workMode == MainEnvironment.WorkMode.SLEEP) {

					chnData.setCurrent(0);

				}

				// 发生了跳转
				channel.setCapacity(0);
				channel.setStepIndex(nextStep.stepIndex);
				channel.setLoopIndex(nextStep.loopIndex);
				channel.setMiliSeconds(0);

				channel.setWorkMode(WorkMode.values()[nextStep.workMode.ordinal()]);

			}
		}

	}

	/**
	 * 寻找当前步次的下一个步次对象
	 * 
	 * @author wavy_zheng 2020年11月19日
	 * @param procedure
	 * @param stepIndex
	 * @param loopIndex
	 * @return
	 */
	private Step nextStepFrom(DriverStepData procedure, int stepIndex, int loopIndex) {

		if (loopIndex > procedure.getLoopCount() + 1 && loopIndex > 1) {

			return null;
		}

		Step nextStep = null;

		if (procedure.getLoopCount() > 0 && procedure.getLoopSt() > 0 && procedure.getLoopEd() > 0) {

			// 处理循环步次
			if (stepIndex == procedure.getLoopEd() && loopIndex <= procedure.getLoopCount()) {
				// 跳转循环

				int nextStepIndex = procedure.getLoopSt();

				nextStep = procedure.getSteps().get(nextStepIndex - 1);
				nextStep.loopIndex = loopIndex + 1;
				return nextStep;
			}

		}
		if (stepIndex + 1 > procedure.getSteps().size()) {

			return null;
		}
		nextStep = procedure.getSteps().get(stepIndex);
		nextStep.loopIndex = loopIndex;
		return nextStep;

	}

	/**
	 * 停止流程执行
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param logic
	 */
	public void stopWork(int driverIndex) {

		VirtualDriver driver = drivers.get(driverIndex);
		if (driver.getExecutor() != null) {

			driver.getExecutor().shutdownNow();
			driver.setExecutor(null);

		}
	}

	/**
	 * 开始工作
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param logic
	 */
	public void startWork(int driverIndex) {

		VirtualDriver driver = drivers.get(driverIndex);

		if (driver.getExecutor() == null) {

			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {

					for (VirtualChannel channel : driver.getChannels()) {

						try {
							ChnDataPack chnData = null;

							if (channel.getState() == ChnState.RUNNING) {
								chnData = produceData(channel);
								if (chnData != null) {
									channel.pushData(chnData);
								}
							} else {
								chnData = produceUDTData(channel);
								channel.pushData(chnData);
							}
							channel.setTickCount(channel.getTickCount() + 1);

							// 判断是否超压
							if (chnData != null) {
								if (chnData.getVoltage() + new Random().nextDouble() >= driver.getProtect()
										.getDeviceVoltUpper()) {

									channel.setState(ChnState.EXCEPT);
									channel.setAlertCode(AlertCode.DEVICE_VOLT_UPPER); // 超压报警
								}
							}

						} catch (Exception ex) {

							ex.printStackTrace();
						}

					}

				}

			}, 100, 1000, TimeUnit.MILLISECONDS);
			driver.setExecutor(executor);

		}

	}

	/**
	 * 提供采样虚拟数据
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @return
	 */
	public ChnDataPack produceData(VirtualChannel channel) {

		ChnDataPack chnData = null;
		if (channel.getState() == ChnState.RUNNING) {

			Step step = null;
			if (channel.getStepIndex() > 0) {

				step = channel.getProcedure().getSteps().get(channel.getStepIndex() - 1);
			}
			if (step == null) {

				return null;
			}

			switch (step.workMode) {

			case CCC:
				chnData = produceCCData(channel);
				break;
			case CVC:
				chnData = produceCVData(channel);
				break;
			case CCD:
				chnData = produceDCData(channel);
				break;
			case CC_CV:
				chnData = produceCCVData(channel);
				break;
			case SLEEP:
				chnData = produceSleepData(channel);
				break;

			}

			// 判定流程步次转换
			controlStep(channel, chnData);

		} else {

			chnData = produceUDTData(channel);

		}

		return chnData;
	}

	private ChnDataPack produceCCVData(VirtualChannel channel) {

		Step step = channel.getProcedure().getSteps().get(channel.getStepIndex() - 1);

		double voltage = channel.getVoltage();
		if (voltage - step.specialVoltage < -CV_RANGE_OFFSET) {

			// CC
			return produceCCData(channel);

		} else {

			return produceCVData(channel);
		}

	}

	public DriverPickupData pickupDriver(int driverIndex) {

		DriverPickupData pickup = new DriverPickupData();

		for (int n = 0; n < drivers.get(driverIndex).getChannels().size(); n++) {

			VirtualChannel channel = drivers.get(driverIndex).getChannels().get(n);
			pickup.getPacks().addAll(channel.getDatas());
			channel.clearData();
		}
		pickup.setDriverStateOk(true);
		pickup.setDriverMode(drivers.get(driverIndex).getMode());
		pickup.setTemperature1(21);
		pickup.setTemperature2(22);
		pickup.setDriverIndex(driverIndex);

		return pickup;

	}

	public void writePole(int driverIndex, DriverPoleData pole) {

		drivers.get(driverIndex).setPole(pole.getPole());
	}

	public DriverInfoData readDriverInfo(int driverIndex) {

		return drivers.get(driverIndex).getDriverInfo();
	}

	public void writeDriverProtect(DriverProtectData protect) {

		drivers.get(protect.getDriverIndex()).setProtect(protect);
	}

	public void writeSteps(DriverStepData stepData) {

		drivers.get(stepData.getDriverIndex()).setProcedure(stepData);

	}

	public void writeMode(DriverModeData mode) {

		drivers.get(mode.getDriverIndex()).setMode(mode.getMode());
	}

	public void writeResume(DriverResumeData resume) {

		VirtualDriver driver = drivers.get(resume.getDriverIndex());
		if (driver != null) {

			for (int n = 0; n < resume.getUnits().size(); n++) {

				ResumeUnit unit = resume.getUnits().get(n);

				if (driver.getChannels().get(unit.chnIndex).getProcedure() != null) {
					driver.getChannels().get(unit.chnIndex).setResumeUnit(unit);
				}

			}

		}

	}

	public void writeOperate(DriverOperateData operate) throws AlertException {

		VirtualDriver driver = drivers.get(operate.getDriverIndex());
		if (driver != null) {

			for (int n = 0; n < driver.getChannels().size(); n++) {

				if ((operate.getOptFlag() & 0x01 << n) > 0) {

					if (operate.isOpen()) {
						if (!driver.getChannels().get(n).open()) {

							throw new AlertException(MainEnvironment.AlertCode.LOGIC, "打开通道" + (n + 1) + "失败");

						}
					} else {

						driver.getChannels().get(n).close(true);
					}

				}

			}

		}
	}

}
