package com.nlteck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.data.DataProviderService;
import com.nlteck.util.CommonUtil;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.logic.LogicEnvironment.StepAndLoop;
import com.nltecklib.protocol.li.logic2.Logic2SyncStepSkipData;
import com.nltecklib.protocol.li.main.AllowStepSkipData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnOpt;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.utils.LogUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年12月21日 下午4:34:30 流程同步控制服务
 */
public class SyncStepControlService {

	private Logger logger;
	private MainBoard mainboard;
	private Map<ControlUnit, Boolean> syncMap = new HashMap<>(); // 是否已同步?
	private Map<ControlUnit, Long> syncPressCompleteTOMap = new ConcurrentHashMap<ControlUnit, Long>();
	

	public SyncStepControlService(MainBoard mainboard) {

		logger = LogUtil.getLogger("log/syncStepControlService.log");
		this.mainboard = mainboard;
	}

	public void resetSync(ControlUnit cu) {

		syncMap.put(cu, false);
	}

	public void setSync(ControlUnit cu, Boolean sync) {

		syncMap.put(cu, sync);
	}

	public Boolean getSync(ControlUnit cu) {

		return syncMap.get(cu) == null ? false : syncMap.get(cu);
	}

	/**
	 * 找到同步步次
	 * 
	 * @author wavy_zheng 2020年12月22日
	 * @param procedure
	 * @param channels
	 * @return
	 */
	private Step findSyncStepFrom(ProcedureData procedure, List<Channel> channels) {

		for (Channel chn : channels) {

			if (procedure.getStep(chn.getStepIndex() - 1).overMode == OverMode.PAUSE) {

				Step step = procedure.getStep(chn.getStepIndex() - 1);
				step.loopIndex = chn.getLoopIndex();
				return step;
			}
		}

		return null;

	}

	/**
	 * 验证是否所有步次都处于同步状态
	 * 
	 * @author wavy_zheng 2020年12月22日
	 * @param syncStep
	 * @param channels
	 */
	private boolean validateSyncSteps(Step syncStep, List<Channel> channels) {

		for (Channel chn : channels) {

			if (chn.getStepIndex() != syncStep.getStepIndex()) {

				// 异常步次的通道报警

				// 同步异常，有两个不同的休眠步次发生
				try {
					Context.getCoreService().executeChannelsAlertInLogic(AlertCode.LOGIC,
							I18N.getVal(I18N.ChnSyncStepErr, chn.getStepIndex(), syncStep.getStepIndex()), chn);
					Context.getCoreService().emergencyPause(chn.getControlUnit());
				} catch (AlertException e) {

					chn.log(CommonUtil.getThrowableException(e));
					e.printStackTrace();
				}

				return false;

			}

			if (chn.getLoopIndex() != syncStep.getLoopIndex()) {

				// 同步异常，有两个不同的休眠循环号发生
				try {
					// 异常步次的通道报警
					Context.getCoreService().executeChannelsAlertInLogic(AlertCode.LOGIC,
							I18N.getVal(I18N.ChnSyncLoopErr, chn.getLoopIndex(), syncStep.getLoopIndex()), chn);
					Context.getCoreService().emergencyPause(chn.getControlUnit());
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return false;
			}
		}

		return true;
	}

	
	/**
	 * 处理流程同步步次以及变压处理
	 * 
	 * @author wavy_zheng 2021年8月24日
	 * @param cu
	 * @throws AlertException
	 */
	public void monitorSyncSteps(ControlUnit cu) throws AlertException {


		if(!mainboard.isInitOk() || mainboard.isOffline()){
			return;
		}
		
		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				monitorSyncSteps(unit);
			}

		} else {

			if (cu.getProcedure() == null) {

				return;
			}
			if (mainboard.getState() == State.PAUSE) {
				
				
				//检查是否同步模式
				List<Channel> channels = cu.listAllChannels(ChnState.PAUSE);
				List<Channel> syncResumeChannels = new ArrayList<>(); // 将执行同步恢复的通道
				
				for (Channel channel : channels) {

					if(channel.getState() == ChnState.PAUSE && channel.isSynMode()) {
					    syncResumeChannels.add(channel);
					}
					
				}
				if(channels.size() > syncResumeChannels.size()) {
					
//					logger.info("paused channel size = " + channels.size() + ",synced channel size = " 
//					             + syncResumeChannels.size() +
//							     ",can not resume synced channels");
					return;
				}
				
				boolean canResume = false;
				
				if(cu.isPressureChanged()) {
					
					//如果是变压工步,判断压力是否变更完毕?
					if(cu.isPressureComplete()) {
						
						canResume = true;
					} else if(cu.getPressureCompleteTimeout() > 0){
						
						logger.info("change pressure time out :" + cu.getPressureCompleteTimeout() + ",alert all channels");
						
						Context.getCoreService().executeChannelsAlertInLogic(AlertCode.PRESSURE, I18N.getVal(I18N.PressureChangeTimeOutException, cu.getPressureCompleteTimeout()),
								syncResumeChannels.toArray(new Channel[0]));
						
						cu.getMainBoard().pushSendQueue(0xff, -1, AlertCode.PRESSURE,
								I18N.getVal(I18N.PressureChangeTimeOutException, cu.getPressureCompleteTimeout()));
					}
					
				} else {
					//除压力变更外的其他同步直接恢复，无需等上位机通道
					canResume = true;
				}
				
				
				if (!syncResumeChannels.isEmpty() && canResume 
						) {
					
					logger.info("resume all sync channels:" + syncResumeChannels.size());
					//暂缓2s恢复同步通道
					CommonUtil.sleep(2000);
					// 将需要同步变压的通道
					Context.getCoreService().executeChannelsProcedure(ChnOpt.RESUME,
							syncResumeChannels.toArray(new Channel[0]));
					
					for(Channel chn : syncResumeChannels) {
						
						chn.setSynMode(false); //退出同步
					}
				}
				

			} else if (cu.getState() == State.FORMATION) {

				List<Channel> channels = cu.listAllChannels(ChnState.RUN);
				List<Channel> syncChannels = new ArrayList<Channel>(); // 将执行同步的步次
               
				//复位信号
				cu.setPressureComplete(false);
				cu.setPressureChanged(false);
				
				for (Channel channel : channels) {
                    
					if(channel.isSynMode()) {
						
						continue; //已经同步的通道无需同步
					}
					
					if(channel.getLeadSyncCount() == 0) {
						
						continue; //已经同步过了，无需重复同步
					}
					
					if (stepPressureChanged(cu.getProcedure(), channel.getStepIndex(), channel.getLoopIndex())) {
                       
						logger.info("previous step change pressure:" + channel.getStepIndex() + "chn:" + (channel.getDeviceChnIndex() + 1));
						syncChannels.add(channel);
						// 将当前工作模式转为同步
						channel.setSynMode(true);
						channel.setLeadSyncCount(0);//禁止再次同步，每个步次只同步一次
						cu.setPressureChanged(true); //表示这是个压力变更同步，需要等待上位机信号才能恢复
						
						
					} else if (stepPausedOverMode(cu.getProcedure(), channel.getStepIndex(), channel.getLoopIndex())) {
						
						logger.info(" OverMode Pause step :" + channel.getStepIndex() + "chn:" + (channel.getDeviceChnIndex() + 1) + "");
						syncChannels.add(channel);
						// 将当前工作模式转为同步
						channel.setSynMode(true);
						channel.setLeadSyncCount(0); //禁止再次同步，每个步次只同步一次
						
					}
					

				}
				
				if (!syncChannels.isEmpty()) {
					
					logger.info("syncChannels size = " + syncChannels.size());
					
					// 将需要同步变压的通道暂停
					Context.getCoreService().executeChannelsProcedure(ChnOpt.PAUSE,
							syncChannels.toArray(new Channel[0]));
					
					
				}

			}

		}

	}

	public void monitorSyncSteps2(ControlUnit cu) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				monitorSyncSteps(unit);
			}

		} else {

			if (cu.getProcedure() == null) {

				return;
			}
			if (mainboard.getState() == State.PAUSE) {
				
				
				//检查是否同步模式
				List<Channel> channels = cu.listAllChannels(ChnState.PAUSE);
				List<Channel> syncResumeChannels = new ArrayList<>(); // 将执行同步恢复的通道
				
				for (Channel channel : channels) {
					
					if(channel.getState() == ChnState.PAUSE && channel.isSynMode()) {
						
						if(channel.getLeadSyncCount() > 0 ) {
					
							channel.decreaseSyncCount();
						} else {
						    syncResumeChannels.add(channel);
						    
						}
						
					}
					
				}
				
				
				if (!syncResumeChannels.isEmpty()) {
					// 将需要同步变压的通道暂停
					Context.getCoreService().executeChannelsProcedure(ChnOpt.RESUME,
							syncResumeChannels.toArray(new Channel[0]));
					
				}
				

			} else if (cu.getState() == State.FORMATION) {

				List<Channel> channels = cu.listAllChannels(ChnState.RUN);
				List<Channel> syncChannels = new ArrayList<>(); // 将执行同步的步次
				
				for (Channel channel : channels) {
                    
					if(channel.isSynMode()) {
						
						continue;
					}
					
					if (stepPressureChanged(cu.getProcedure(), channel.getStepIndex(), channel.getLoopIndex())) {
                       
						System.out.println("previous step change pressure:" + channel.getStepIndex());
						syncChannels.add(channel);
						// 将当前工作模式转为同步
						channel.setSynMode(true);
						channel.setLeadSyncCount(1);
					} else if (stepPausedOverMode(cu.getProcedure(), channel.getStepIndex(), channel.getLoopIndex())) {
						
						syncChannels.add(channel);
						// 将当前工作模式转为同步
						channel.setSynMode(true);
						channel.setLeadSyncCount(1);
					}
					

				}
				
				logger.info("syncChannels size = " + syncChannels.size());
				
				if (!syncChannels.isEmpty()) {
					
					// 将需要同步变压的通道暂停
					Context.getCoreService().executeChannelsProcedure(ChnOpt.PAUSE,
							syncChannels.toArray(new Channel[0]));
				}

			}

		}

	}
	
	/**
	 * 监视同步步次跳转
	 * 
	 * @author wavy_zheng 2020年12月16日
	 */
	public void monitorSyncStepSkip(ControlUnit cu) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				monitorSyncStepSkip(unit);
			}

		} else {

			if (cu.getProcedure() == null || cu.getState() != State.FORMATION) {

				return;
			}

			List<Channel> channels = cu.listAllChannels(ChnState.RUN);

			Step syncStep = findSyncStepFrom(cu.getProcedure(), channels);
			if (syncStep == null) {

				if (getSync(cu)) {
					resetSync(cu); // 所有同步步次均已完成跳转,复位同步标记
				}
				return;
			}

			if (getSync(cu)) { // 已执行同步命令

				return;
			}

			for (Channel chn : channels) {

				// 同步一定发生在休眠步次
				if (chn.getWorkMode() != WorkMode.SLEEP) {

					return;
				}

			}

			if (!validateSyncSteps(syncStep, channels)) {

				return; // 验证失败
			}

			// 获取下一步
			Step nextStep = DataProviderService.nextStepFrom(cu.getProcedure(), syncStep.getStepIndex(),
					syncStep.getLoopIndex());
			if (nextStep == null) {

				return;
			}

			setSync(cu, true);
			// 查询压力有无发生变更
			/*
			 * if (nextStep.pressure > 0 && nextStep.pressure != syncStep.pressure) {
			 * 
			 * logger.info("pressure change :" + syncStep.pressure + " -> " +
			 * nextStep.pressure); Context.getPcNetworkService().pushSendQueue(-1, -1,
			 * AlertCode.NORMAL, I18N.getVal(I18N.PressureChange, syncStep.pressure,
			 * nextStep.pressure)); // 主动上报给上位机，提示上位机进行变压操作 AllowStepSkipData askd = new
			 * AllowStepSkipData(); askd.setUnitIndex(cu.getIndex());
			 * askd.setAllowedStepIndex(nextStep.stepIndex);
			 * Context.getPcNetworkService().pushSendQueue(new AlertDecorator(askd));
			 * 
			 * } else {
			 */

			logger.info("send skip sync step command ");
			// 无压力变更情况直接转步次
			skipNextSyncStep(cu);

			// }

		}

	}

	public void skipNextSyncStep(ControlUnit cu) throws AlertException {

		// 无压力变更情况直接转步次
		for (LogicBoard lb : cu.getLogics()) {
			Logic2SyncStepSkipData lssd = new Logic2SyncStepSkipData();
			lssd.setUnitIndex(lb.getLogicIndex());
			//Context.getLogicboardService().writeSyncStepSkip(lssd);
		}
	}

	private boolean stepPausedOverMode(ProcedureData procedure, int stepIndex, int loopIndex) {

		// 第1步不判断变压
		if ((stepIndex < 2 && loopIndex == 1) || stepIndex >= procedure.getStepCount()) {

			return false;
		}
		Step step = procedure.getStep(stepIndex - 1);
		return step.overMode == OverMode.PAUSE;

	}

	/**
	 * 当前步次压力发生了变更?
	 * 
	 * @author wavy_zheng 2021年8月24日
	 * @param procedure
	 * @param stepIndex
	 * @return
	 */
	private boolean stepPressureChanged(ProcedureData procedure, int stepIndex, int loopIndex) {

		// 第1步不判断变压
		if ((stepIndex < 2 && loopIndex == 1) || stepIndex > procedure.getStepCount()) { 

			return false;
		}
		
		Step step = procedure.getStep(stepIndex - 1);
        
		
		StepAndLoop sal = LogicBoard.skipPreviousStep(procedure, stepIndex, loopIndex);
		
		if (sal.nextStep > 0 && sal.nextStep <= procedure.getStepCount()) {

			Step previousStep = procedure.getStep(sal.nextStep - 1);
			if (previousStep.pressure > 0 && step.pressure > 0 && previousStep.pressure != step.pressure) {

				return true; // 压力发生了变更
			}

		}
		return false;

	}

	public List<Channel> listAllPauseStepChannels(ControlUnit cu) {

		List<Channel> channels = new ArrayList<>();
		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				listAllPauseStepChannels(unit);
			}

		} else {

			for (Channel channel : cu.listAllChannels(ChnState.RUN)) {
				Step step = cu.getProcedureStep(channel.getStepIndex());
				Step preStep = cu.getProcedureStep(channel.getStepIndex() - 1);

				if (step != null) {
					if (preStep != null) {

					}
					if (step.overMode == OverMode.PAUSE) {

					}
				}
			}

		}

		return channels;
	}

}
