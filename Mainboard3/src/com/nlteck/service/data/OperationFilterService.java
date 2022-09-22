package com.nlteck.service.data;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月17日 上午10:38:58 用于管理用户操作服务
 */
public class OperationFilterService implements DataFilterService {

	// private MainBoard mainboard;
	private Logger logger;

	public OperationFilterService() {

		// this.mainboard = mainboard;
		try {
			logger = LogUtil.createLog("log/service/operation.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<ChnDataPack> filterRawDatas(Channel channel, List<ChnDataPack> rawDatas) {
       
		
		State optState = channel.peekOperate();
		if (optState != null) {
			for (ChnDataPack rawData : rawDatas) {
                
				
				if (optState == State.STARTUP || optState == State.FORMATION) {

					// 启动操作监测
					checkStartOrResume(channel, rawData);
				} else if (optState == State.STOP || optState == State.PAUSE) {

					checkStopOrPause(channel, rawData);
				}

			}
		}

		return rawDatas;
	}

	@Override
	public List<ChannelData> filterChannelDatas(Channel channel, List<ChannelData> channelDatas) {

		return channelDatas;
	}

	/**
	 * 监测通道是否正常关闭
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param channel
	 * @param channelData
	 * @return
	 */
	private void checkStopOrPause(Channel channel, ChnDataPack rawData) {

		if (rawData.getState() == DriverEnvironment.ChnState.STOP 
				|| rawData.getStepIndex() == 0) {

			if (channel.peekOperate() == State.STOP) {

				channel.stop();// 停止测试
			} else if(channel.peekOperate() == State.PAUSE){

				channel.pause();
			}
		}

	}

	/**
	 * 监测通道是否正常启动
	 * 
	 * @author wavy_zheng 2020年11月17日
	 * @param channel
	 * @param channelData
	 * @return
	 */
	private void checkStartOrResume(Channel channel, ChnDataPack rawData) {

		Date now = new Date();
		long deltMiliseconds = now.getTime() - channel.getOptStartTime().getTime();
         
		channel.log("channel opt start miliseconds = " + channel.getOptStartTime().getTime());
		channel.log("attempt to " + channel.peekOperate() + " channel,attempt :" + channel.getAttemptOpenCount() + ","
				+ rawData.toString());
		if (rawData.getState() == DriverEnvironment.ChnState.RUNNING) {
            
			
			if (channel.peekOperate() == State.STARTUP) {

				channel.startup();
			} else if (channel.peekOperate() == State.FORMATION) {

				channel.resume(rawData);
			}
			channel.log("start up success,channel state = " + channel.getState());
			
			
		} else {
            
			channel.log("deltMiliseconds = " + deltMiliseconds + ",attemp open count = " + channel.getAttemptOpenCount());
			
			if(rawData.getState() == DriverEnvironment.ChnState.COMPLETE) {
				
				channel.log("channel complete, just over it ");
				channel.setState(ChnState.RUN);
				channel.complete();
				return;
			}
			
			if (deltMiliseconds > MainBoard.startupCfg.getMaxStartupTimeout()
					&& channel.getAttemptOpenCount() >= Channel.MAX_ATTEMPT_OPEN_COUNT) {

				channel.log(channel.peekOperate() + " channel failed");

				try {
					if (channel.peekOperate() == State.STARTUP) {

						// 异常步次的通道报警
						Context.getCoreService().executeChannelsAlertInLogic(AlertCode.LOGIC,
								I18N.getVal(I18N.StartAlarm_ChnStartTimeout), channel);

					} else if(channel.peekOperate() == State.FORMATION){

						Context.getCoreService().executeChannelsAlertInLogic(AlertCode.LOGIC,
								I18N.getVal(I18N.RecoveryAlarm_ChnRecoveryTimeout), channel);
					}
				} catch (AlertException ex) {
                     
					ex.printStackTrace();
					channel.log(CommonUtil.getThrowableException(ex));
				} finally {
					
					State pollState = channel.pollOperate();
					channel.log("poll state = " + pollState);
				}

			} else {

				// 启动计数+1
				channel.setAttemptOpenCount(channel.getAttemptOpenCount() + 1);
			}
		}

	}

}
