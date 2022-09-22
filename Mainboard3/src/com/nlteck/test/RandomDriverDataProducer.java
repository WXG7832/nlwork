package com.nlteck.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.nlteck.firmware.MainBoard;
import com.nlteck.produce.ChnDataProducer;
import com.nlteck.produce.LogicDriverDataProducer;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.logic.LogicEnvironment;
import com.nltecklib.protocol.li.logic.LogicEnvironment.AlertCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.ChnState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.PickupState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.nltecklib.protocol.li.logic.LogicPickupData.ChnData;
import com.nltecklib.protocol.li.logic.LogicStateData;

/**
 * 产生单板虚拟数据
 * 
 * @author Administrator
 *
 */
public class RandomDriverDataProducer implements LogicDriverDataProducer {

	private Map<Integer, ChnDataProducer> producers = new HashMap<Integer, ChnDataProducer>();
	private LogicEnvironment.WorkMode workMode = LogicEnvironment.WorkMode.UDT;
	private Map<Integer, LogicEnvironment.ChnState> states = new HashMap<Integer, LogicEnvironment.ChnState>();
	private int unitIndex;
	private int driverIndex;
	private boolean running;
	private int pickCount;
	private LogicState state = LogicState.UDT; // 分区状态

	private Map<Integer, Double> proceduceVoltMap = new HashMap<Integer, Double>();

	// 模拟驱动板温度
	private double mainTemp;
	private double subTemp;

	public RandomDriverDataProducer(int unitIndex, int driverIndex) {

		for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

			states.put(i, ChnState.UDT/* unitIndex == 0 ? ChnState.UDT : ChnState.NONE */);

		}

		this.unitIndex = unitIndex;
		this.driverIndex = driverIndex;
		updateProducers(0xffff, WorkMode.UDT, 3200, 500, 0);
	}

	/**
	 * 获取当前的实际电压
	 * 
	 * @param chnIndexInLogic
	 * @return
	 */
	// public double getBaseVoltage(int chnIndexInLogic) {
	//
	// return producers.get(chnIndexInLogic).getProduceVoltage();
	// }
	/**
	 * 获取当前的实际电流
	 * 
	 * @param chnIndexInLogic
	 * @return
	 */
	// public double getBaseCurrent(int chnIndexInLogic) {
	//
	// return producers.get(chnIndexInLogic).getProduceCurrent();
	// }

	public void switchChnsState(int chnSelectFlag, boolean close) {

		for (int chnIndex = 0; chnIndex < MainBoard.startupCfg.getDriverChnCount(); chnIndex++) {

			if ((chnSelectFlag & (0x01 << chnIndex)) > 0 && states.get(chnIndex) != ChnState.NONE) {

				states.put(chnIndex, close ? ChnState.CLOSE : ChnState.UDT);
			}
		}
	}

	/**
	 * 关闭通道
	 * 
	 * @param chnIndex
	 */
	public void stopChn(int chnSelectFlag) {

		for (int chnIndex = 0; chnIndex < MainBoard.startupCfg.getDriverChnCount(); chnIndex++) {

			if ((chnSelectFlag & (0x01 << chnIndex)) > 0 && states.get(chnIndex) != ChnState.CLOSE
					&& states.get(chnIndex) != ChnState.NONE) {

				states.put(chnIndex, ChnState.STOP);
				producers.get(chnIndex).setClose(true); // 关闭通道
			}
		}
	}

	public boolean isRunning() {

		for (int n = 0; n < states.size(); n++) {

			if (states.get(n) == ChnState.RUNNING) {

				return true;
			}
		}

		return false;
	}

	public void resumeChn(int chnSelectFlag, WorkMode workMode, double voltage, double current, double threshold) {

		System.out.println("逻辑板区号" + unitIndex + ",逻辑板启动板号:" + driverIndex + ",启动flag:"
				+ String.format("0x%x", chnSelectFlag) + ",workMode = " + workMode);
		// 更新步次
		updateProducers(chnSelectFlag, workMode, voltage, current, threshold);
		// 启动
		for (int chnIndex = 0; chnIndex < MainBoard.startupCfg.getDriverChnCount(); chnIndex++) {

			if ((chnSelectFlag & (0x01 << chnIndex)) > 0 && states.get(chnIndex) != ChnState.CLOSE
					&& states.get(chnIndex) != ChnState.NONE) {

				states.put(chnIndex, ChnState.RUNNING);
				producers.get(chnIndex).setClose(false); // 打开通道

			}
		}
	}

	/**
	 * 修改分区状态
	 * 
	 * @param lsd
	 */
	public void changeState(LogicStateData lsd) {

		state = lsd.getStartupState();
	}

	public void operate(LogicEnvironment.LogicState ss, WorkMode workMode, double voltage, double current,
			double threshold) {

		if (ss == LogicState.WORK) {

			resumeChn(0xffff, workMode, voltage, current, threshold);
			running = true;
		} else if (ss == LogicState.MAINTAIN || ss == LogicState.UDT) {

			stopChn(0xffff); // 模拟关闭通道
			running = false;

		}
	}

	public synchronized void updateProducers(int chnSelectFlag, WorkMode workMode, double voltage, double current,
			double overThreshold) {

		// 默认通道关闭
		for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

			if ((chnSelectFlag & (0x01 << i)) > 0) {

				ChnDataProducer p = null;

				Double procedureVoltage = getProduceVolt(i);
				if (procedureVoltage == null) {

					procedureVoltage = 3700.0;
				}
				if (workMode == LogicEnvironment.WorkMode.CC) {

					p = new CCDataProducer(procedureVoltage, overThreshold, current);
				} else if (workMode == LogicEnvironment.WorkMode.CC_CV) {

					p = new CCVDataProducer(procedureVoltage, voltage, current, overThreshold);
				} else if (workMode == LogicEnvironment.WorkMode.DC) {

					p = new DCDataProducer(procedureVoltage, overThreshold, current);
				} else {

					p = new SleepDataProducer(procedureVoltage);
				}

				producers.put(i, p);
			}

		}

	}

	@Override
	public synchronized LogicPickupData pickup() {

		List<ChnData> list = new ArrayList<ChnData>();
		LogicPickupData pickup = new LogicPickupData();

		boolean allChannelOver = true;
		for (int i = 0; i < producers.size(); i++) {

			ChnData cd = pickup.new ChnData();
			producers.get(i).produceData();
			cd.setVoltage(producers.get(i).getProduceVoltage());
			cd.setPowerVoltage(producers.get(i).getProduceVoltage() + new Random().nextDouble());
			cd.setCurrent(producers.get(i).getProduceCurrent());
			cd.setAlertCurrent(producers.get(i).getAlertCurrent());
			cd.setAlertVolt(producers.get(i).getAlertVoltage());

			if (i == 0 && driverIndex == 0 && unitIndex == 0) {

				cd.setCurrent(producers.get(i).getProduceCurrent() / 2);
				cd.setAlertCurrent(producers.get(i).getAlertCurrent() / 2);
				// cd.setState(ChnState.NONE);
			}

			WorkMode workMode = null;
			if (producers.get(i) instanceof CCDataProducer) {

				workMode = WorkMode.CC;

			} else if (producers.get(i) instanceof CCVDataProducer) {

				workMode = WorkMode.CC_CV;
				if(i == 2) {
					
					cd.setCurrent(90);
					cd.setVoltage(3750);
					producers.get(i).setClose(true);
				}

			} else if (producers.get(i) instanceof DCDataProducer) {

				workMode = WorkMode.DC;

			} else {

				workMode = WorkMode.UDT;
				// 模拟反极性
				// if(index++ > 10) {
				//
				// cd.setState(ChnState.EXCEPT);
				// cd.setAlertCode(AlertCode.POLE_REVERSE);
				//
				// }
			}
			cd.setWorkMode(workMode);

			if (states.get(i) == ChnState.RUNNING) {

				if (producers.get(i).isClose()) {

					// 测试完毕
					cd.setState(ChnState.COMPLETE);
					cd.setAlertVolt(cd.getVoltage());
					cd.setAlertCurrent(cd.getCurrent());
					if(i == 2) {
						
						producers.put(i, new SleepDataProducer(cd.getVoltage()));
					}

				} else {

					if (cd.getCurrent() > 0) {
                        
						if (i == 3 && driverIndex == 0 && unitIndex == 0 ) {
							
							cd.setState(ChnState.COMPLETE);
							cd.setCurrent(90);
							
						} else {
						
//						if (i == 2 && driverIndex == 0 && unitIndex == 0 && cd.getCurrent() == 90) {
//
//							cd.setState(ChnState.COMPLETE); // 测试
//						} else {
							cd.setState(ChnState.RUNNING); // 启动步次
//						}
						}
					} else {
						cd.setState(ChnState.UDT); // 清除上一步次状态
					}

					allChannelOver = false;

				}
			} else if (states.get(i) == ChnState.STOP) {

				if (producers.get(i).isClose()) {
					// 测试完毕
					cd.setState(ChnState.STOP);
				} else {

					cd.setState(ChnState.RUNNING);
					allChannelOver = false;
				}
			}
			if (cd.getState() != ChnState.COMPLETE && cd.getState() != ChnState.STOP
					&& cd.getState() != ChnState.EXCEPT) {

				cd.setState(states.get(i));
				cd.setAlertCode(AlertCode.NORMAL);
				cd.setAlertCurrent(0);
				cd.setAlertVolt(0);

			}

			proceduceVoltMap.put(i, cd.getVoltage());

			list.add(cd);

		}
		pickup.setChnDatas(list);
		pickup.setDriverIndex(this.driverIndex);
		pickup.setUnitIndex(this.unitIndex);
		pickup.setOrient(Orient.RESPONSE);
		pickup.setResult(DefaultResult.SUCCESS);
		pickup.setTemperature1((int) mainTemp);
		pickup.setTemperature2((int) subTemp);
		pickup.setDriverPickupState(isRunning() && !allChannelOver ? PickupState.RUNNING : PickupState.UDT);
		pickup.setLogicPickupState(isRunning() && !allChannelOver ? PickupState.RUNNING : PickupState.UDT);

		CommonUtil.sleep(50); // 模拟延时

		return pickup;
	}

	public double getMainTemp() {
		return mainTemp;
	}

	public void setMainTemp(double mainTemp) {
		this.mainTemp = mainTemp;
	}

	public double getSubTemp() {
		return subTemp;
	}

	public void setSubTemp(double subTemp) {
		this.subTemp = subTemp;
	}

	public void updateProduceVolt(int chnIndexInDriver, double produceVoltage) {

		proceduceVoltMap.put(chnIndexInDriver, produceVoltage);
	}

	public Double getProduceVolt(int chnIndexInDriver) {

		return proceduceVoltMap.get(chnIndexInDriver);
	}

	public void clearProduceVoltage() {

		proceduceVoltMap.clear();
	}

}
