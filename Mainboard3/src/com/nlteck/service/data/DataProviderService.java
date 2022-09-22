package com.nlteck.service.data;
/**
* @author  wavy_zheng
* @version 创建时间：2020年11月14日 下午4:25:12
* 数据处理服务,将源数据进行处理打包发送给网络服务
*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.firmware.SmartPickupData;
import com.nlteck.service.StartupCfgManager;
import com.nlteck.service.StartupCfgManager.DriverboardType;
import com.nlteck.util.ArithUtil;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.logic2.Logic2Environment.ChnState;
import com.nltecklib.protocol.li.logic2.Logic2PickupData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.OfflinePickupData;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;
import com.nltecklib.utils.BaseUtil;

public abstract class DataProviderService {

	public static final int MAX_PUSH_DATA_COUNT = 1200; // 单次推送最多数据包个数

	protected MainBoard mainboard;

	// 数据过滤链
	protected List<DataFilterService> filterChain = new ArrayList<>();

	protected DataProviderService(MainBoard mb) {

		this.mainboard = mb;
	}

	/**
	 * 将数据打包转化为可推送给上位机的数据包
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param chn
	 */
	public abstract void provideChannelData(Channel chn);

	/**
	 * 将队列中的元素一次性全部取出
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param queue
	 * @return
	 */
	public <E> List<E> fetchAllDataFromQueue(Queue<E> queue) {

		List<E> list = new ArrayList<>();
		for (Iterator<E> it = queue.iterator(); it.hasNext();) {

			list.add(it.next());
		}
		return list;
	}

	/**
	 * 设备未运行的通道状态处理
	 */
	private void processWaitState(Channel channel, ChnDataPack rawChnData) {

		if (channel.getState() == MainEnvironment.ChnState.RUN
				|| channel.getState() == MainEnvironment.ChnState.PAUSE) {

			return; // 不处理运行状态
		}

		if (channel.getControlUnit().getState() == State.FORMATION
				|| channel.getControlUnit().getState() == State.PAUSE) {

			return; // 在设备分区运行或暂停时不处理通道
		}

		if (channel.getState() == MainEnvironment.ChnState.NONE) {

			if (rawChnData.getState() != DriverEnvironment.ChnState.NONE) {

				channel.setState(MainEnvironment.ChnState.UDT);

			}

		} else {

			if (channel.getState() != MainEnvironment.ChnState.CLOSE) {
				if (rawChnData.getState() == DriverEnvironment.ChnState.NONE) {

					// 变为无电池
					channel.setState(MainEnvironment.ChnState.NONE);
					channel.setAlertCode(AlertCode.NORMAL);
					channel.clearTouchData(); // 清除压差保护缓存
					channel.setVoltageOffsetProtectionData(null); // 消除压差保护

				} else if (rawChnData.getState() == DriverEnvironment.ChnState.UDT) {

					if ((channel.getState() != MainEnvironment.ChnState.STOP
							&& channel.getState() != MainEnvironment.ChnState.COMPLETE
							&& channel.getState() != MainEnvironment.ChnState.CLOSE
							&& channel.getState() != MainEnvironment.ChnState.ALERT
							&& channel.getState() != MainEnvironment.ChnState.UDT)) {

						// 变为有电池
						channel.setState(MainEnvironment.ChnState.UDT);

					}

				}
			}

		}

	}

	public ProtectionFilterService getProtectService() {

		for (DataFilterService service : filterChain) {

			if (service instanceof ProtectionFilterService) {

				return (ProtectionFilterService) service;
			}
		}

		return null;
	}

	public DataProcessService getDataProcessService() {

		for (DataFilterService service : filterChain) {

			if (service instanceof DataProcessService) {

				return (DataProcessService) service;
			}
		}

		return null;
	}

	/**
	 * 转化为上位机数据包
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param pickupData
	 * @return
	 */
	protected ChannelData produceChannelData(Channel channel, ChnDataPack rawData) {

		processWaitState(channel, rawData);

		ChannelData chnData = new ChannelData();
		chnData.putData("channel", channel);

		if (rawData.getState() == DriverEnvironment.ChnState.COMPLETE) {

			channel.complete();
		} else if (rawData.getStepIndex() != channel.getStepIndex()
				|| rawData.getLoopIndex() != channel.getLoopIndex()) {

			if (rawData.getStepIndex() > 0 && rawData.getLoopIndex() > 0) {

				channel.skipStep(rawData.getStepIndex(), rawData.getLoopIndex());
			}
			
		}

		chnData.setUnitIndex(0);
		chnData.setState(channel.getState());
		chnData.setStepIndex(channel.getState() == MainEnvironment.ChnState.ALERT ? 254
				: (channel.getState() == MainEnvironment.ChnState.PAUSE ? channel.getStepIndex()
						: rawData.getStepIndex()));
		chnData.setLoopIndex(
				channel.getState() == MainEnvironment.ChnState.PAUSE ? channel.getLoopIndex() : rawData.getLoopIndex());

		Date now = new Date();
		
		if (rawData.getState() == DriverEnvironment.ChnState.RUNNING && MainBoard.startupCfg.isUseSTM32Time()) {
             
			chnData.setDate(new Date(channel.getOptStartTime().getTime() + channel.getOptElapseMiliseconds()));
            //channel.log("date=" + BaseUtil.formatDate("yyyy/MM/dd HH:mm:ss.SSS", chnData.getDate()) + " , " + channel.getOptElapseMiliseconds() + "ms");
			
			
		} else {

			chnData.setDate(now);

		}
		// 模式

		chnData.setWorkMode(DataProviderService.convertFrom(rawData.getWorkMode()) /* channel.getWorkMode() */);

		chnData.setVoltage(rawData.getVoltage());
		chnData.setCurrent(chnData.getWorkMode() == WorkMode.SLEEP ? 0 : rawData.getCurrent());
		chnData.setImportantData(rawData.isImportant());
		chnData.setChannelIndex(channel.getDeviceChnIndex());

		// 计算能量
		if (/* rawData.getCurrent() > 0 || */ rawData.getState() == DriverEnvironment.ChnState.RUNNING) {

			Step step = channel.getProcedureStep(rawData.getStepIndex());
			boolean changeStepTime = false;
		
			if (rawData.getStepIndex() > 0 && rawData.getLoopIndex() > 0) {

				if (channel.getStepLastMiliseconds() == 0) {

					if (MainBoard.startupCfg.isUseSTM32Time()) {

						channel.setStepLastMiliseconds(rawData.getStepElapsedTime());
					} else {
						channel.setStepLastMiliseconds(now.getTime());
					}
					
					if (MainBoard.startupCfg.isUseSTM32Time()) {
						
						channel.log("skip rawData.getStepElapsedTime() = " + rawData.getStepElapsedTime());
					}		

				}
                
				long deltaMiliseconds = (MainBoard.startupCfg.isUseSTM32Time() ? rawData.getStepElapsedTime()
						: now.getTime()) - channel.getStepLastMiliseconds();
				
				
				if (deltaMiliseconds < 0) {
                     
					channel.log("deltaMiliseconds < 0 :" + deltaMiliseconds);
					channel.log("channel.getStepLastMiliseconds() = " + channel.getStepLastMiliseconds());
					deltaMiliseconds = 0;

				}

				if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

					// 实验室驱动板采用容量累计方式
					channel.setStepCapacity(
							channel.getStepCapacity() + getDeltaCapacity(rawData.getCurrent(), deltaMiliseconds));
				} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
					// 生产驱动板直接采用下位机提供的容量值
					channel.setStepCapacity(rawData.getCapacity());
				}

				channel.setStepEnergy(channel.getStepEnergy()
						+ getDeltaEnergy(chnData.getVoltage(), chnData.getCurrent(), deltaMiliseconds));

				channel.setStepElapseMiliseconds(channel.getStepElapseMiliseconds() + deltaMiliseconds);
				channel.setTotalMiliseconds(channel.getTotalMiliseconds() + deltaMiliseconds);

				channel.setStepLastMiliseconds(
						MainBoard.startupCfg.isUseSTM32Time() ? rawData.getStepElapsedTime() : now.getTime());

				// 更新操作开始的累计时间
				channel.setOptElapseMiliseconds(channel.getOptElapseMiliseconds() + deltaMiliseconds);

			}
			if (channel.getState() != MainEnvironment.ChnState.ALERT
					&& channel.getState() != MainEnvironment.ChnState.CLOSE) {

				// 当逻辑板通道运行状态时，主控强行修改为运行状态
				channel.setState(MainEnvironment.ChnState.RUN);

			}

		} else {

			channel.setStepLastMiliseconds(0);

		}

		// 步次容量
		chnData.setCapacity(channel.getStepCapacity());

		// 步次能量
		chnData.setEnergy(channel.getStepEnergy());

		// 步次时间
		chnData.setTimeStepSpend(Math.round((double) channel.getStepElapseMiliseconds() / 1000));

		// 累积容量
		chnData.setAccumulateCapacity(channel.getAccumulateCapacity() + channel.getStepCapacity());
		// 累计能量
		chnData.setAccumulateEnergy(channel.getAccumulateEnergy() + channel.getStepEnergy());

		chnData.setTimeTotalSpend(Math.round((double) channel.getTotalMiliseconds() / 1000));

		chnData.setDeviceVoltage(rawData.getBackupVoltage());
		chnData.setPowerVoltage(rawData.getPowerVoltage());
		
		//标记上一次采样时间
		channel.setLastPickupTime(chnData.getDate());

		/**
		 * 温度采集
		 */
		if (Context.getAccessoriesService().getProbeManager() != null) {

			double temp = Context.getAccessoriesService().getProbeManager().getProbeValue(channel.getDeviceChnIndex());
			if (temp != -1) {

				chnData.setTemp(temp);
			}

			if (MainBoard.startupCfg.getProtocol().useFrameTempPick) {

				chnData.setFrameTemps(Context.getAccessoriesService().getProbeManager().getFrameTemps());
			}
		} else if (MainBoard.startupCfg.getDriversCfg().useTempPick
				|| Context.getAccessoriesService().getPingController() != null) {

			// 直接采集驱动板内部通道温度
			chnData.setTemp(channel.getTemperature());

		}

		return chnData;

	}

	private SmartPickupData createSmartDataFromPickupData(ChannelData chnData) {

		SmartPickupData spd = new SmartPickupData();
		spd.stepIndex = chnData.getStepIndex();
		spd.loopIndex = chnData.getLoopIndex();
		spd.voltage = chnData.getVoltage();
		spd.current = chnData.getCurrent();
		spd.workMode = chnData.getWorkMode();
		spd.seconds = (int) chnData.getTimeStepSpend();

		return spd;

	}

	public void appendFilter(DataFilterService filter) {

		filterChain.add(filter);
	}

	public void clearFilters() {

		filterChain.clear();
	}

	/**
	 * 计算能量累计
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param voltage
	 * @param current
	 * @param timeElapsed
	 * @return
	 */
	public static double getDeltaEnergy(double voltage, double current, long timeElapsed) {

		double deltaTime = ArithUtil.div(timeElapsed, 1000 * 3600);
		return ArithUtil.div(ArithUtil.mul(ArithUtil.mul(current, voltage), deltaTime), 1000);
	}

	/**
	 * 累计容量
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param current
	 * @param timeElapsed
	 * @return
	 */
	public static double getDeltaCapacity(double current, long timeElapsed) {

		double deltaTime = ArithUtil.div(timeElapsed, 1000 * 3600);
		return ArithUtil.mul(current, deltaTime);

	}

	/**
	 * 获取流程的下一个步次
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param procedure
	 * @param stepIndex
	 * @param loopIndex
	 * @return
	 */
	public static Step nextStepFrom(ProcedureData procedure, int stepIndex, int loopIndex) {

		if (loopIndex > procedure.getLoopCount() + 1 && loopIndex > 1) {

			return null;
		}

		Step nextStep = null;

		if (procedure.getLoopCount() > 0 && procedure.getLoopSt() > 0 && procedure.getLoopEd() > 0) {

			// 处理循环步次
			if (stepIndex == procedure.getLoopEd() && loopIndex <= procedure.getLoopCount()) {
				// 跳转循环

				int nextStepIndex = procedure.getLoopSt();

				nextStep = procedure.getStep(nextStepIndex - 1);
				nextStep.loopIndex = loopIndex + 1;
				return nextStep;
			}

		}
		if (stepIndex + 1 > procedure.getStepCount()) {

			return null;
		}
		nextStep = procedure.getStep(stepIndex);
		nextStep.loopIndex = loopIndex;
		return nextStep;

	}

	/**
	 * 工作模式类型转换
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param workMode
	 * @return
	 */
	public static WorkMode convertFrom(Logic2Environment.WorkMode workMode) {

		return WorkMode.values()[workMode.ordinal()];

	}

	public static WorkMode convertFrom(DriverEnvironment.WorkMode workMode) {

		return WorkMode.values()[workMode.ordinal()];

	}

	/**
	 * 工作模式类型转换
	 * 
	 * @author wavy_zheng 2020年11月16日
	 * @param workMode
	 * @return
	 */
	public static Logic2Environment.WorkMode convertFrom(WorkMode workMode) {

		return Logic2Environment.WorkMode.values()[workMode.ordinal()];

	}

	/**
	 * 将1个驱动板打包好的数据推送给PC
	 * 
	 * @author wavy_zheng 2020年11月26日
	 */
	public abstract void pushRuntimeCacheToPC(int driverIndex);

	protected List<ChannelData> provideCoreChannelData(Channel chn) {

		// 从模型缓存区取出采集的源数据

		List<ChannelData> channelDatas = new ArrayList<ChannelData>();
		// synchronized (chn) { // 锁住缓存，防止被修改
		for (DataFilterService filter : filterChain) {
			List<ChnDataPack> rawDatas = filter.filterRawDatas(chn, chn.getRawCaches());
			chn.setRawCaches(rawDatas);
		}
		if (!chn.getRawCaches().isEmpty()) {
			chn.setLastRawData(chn.getRawCaches().get(chn.getRawCaches().size() - 1));
		}

		for (int n = 0; n < chn.getRawCaches().size(); n++) {

			ChnDataPack rawData = chn.getRawCaches().get(n);
			ChannelData produceData = produceChannelData(chn, rawData);
			if (produceData != null) {

				channelDatas.add(produceData);
			}

		}
		chn.getRawCaches().clear(); // 清空
		// }
		for (DataFilterService filter : filterChain) {

			channelDatas = filter.filterChannelDatas(chn, channelDatas); // 过滤包装好的数据

		}
		// 最后更新上一次通道状态
		chn.setPreState(chn.getState());
		// 记录上一次的电压电流
		if (!channelDatas.isEmpty()) {
			chn.setVoltage(channelDatas.get(channelDatas.size() - 1).getVoltage());
			chn.setCurrent(channelDatas.get(channelDatas.size() - 1).getCurrent());
		}
		// 如果当前步次是同步模式，则做标记
		if (chn.isSynMode() && chn.getLeadSyncCount() >= 0) {

			for (ChannelData chnData : channelDatas) {

				chnData.setWorkMode(WorkMode.SYNC);
			}
		}

		// 计数--
		chn.minusLeadCount();

		return channelDatas;

	}

	/**
	 * 将所有的离线缓存数据推送给电脑
	 * 
	 * @author wavy_zheng 2020年12月16日
	 * @param cu
	 */
	public void pushAllOfflineDataToPc(ControlUnit cu) {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				pushAllOfflineDataToPc(unit);
			}
		} else {

			for (LogicBoard lb : cu.getLogics()) {

				OfflinePickupData opd = new OfflinePickupData();
				opd.setUnitIndex(lb.getLogicIndex());
				List<ChannelData> list = fetchAllLogicOfflineData(lb);
				if (!list.isEmpty()) {

					do {

						if (list.size() > MAX_PUSH_DATA_COUNT) {
							opd.setChnDataList(list.subList(0, MAX_PUSH_DATA_COUNT));
							Context.getPcNetworkService().pushSendQueue(new AlertDecorator(opd));
							list.subList(0, MAX_PUSH_DATA_COUNT).clear();
						} else {

							opd.setChnDataList(list);
							Context.getPcNetworkService().pushSendQueue(new AlertDecorator(opd));
							list.clear();
						}

					} while (!list.isEmpty());
				}

			}

		}

	}

	/**
	 * 取出所有离线运行缓存数据
	 * 
	 * @author wavy_zheng 2020年12月16日
	 * @param lb
	 * @return
	 */
	private List<ChannelData> fetchAllLogicOfflineData(LogicBoard lb) {

		List<ChannelData> list = new ArrayList<>();
		for (Channel chn : lb.getChannels()) {

			list.addAll(Context.getFileSaveService().readOfflineFromDisk(chn));
			list.addAll(chn.getOfflineCaches());
			chn.getOfflineCaches().clear(); // 清空缓存队列
		}

		return list;

	}

}
