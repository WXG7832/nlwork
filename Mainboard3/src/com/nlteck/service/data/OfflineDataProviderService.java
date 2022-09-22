package com.nlteck.service.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.PCNetworkService;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.OfflineRunningData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年12月15日 下午6:08:29 离线运行数据工厂提供组件
 */
public class OfflineDataProviderService extends DataProviderService {

	private Logger logger;

	// 最大离线缓存队列长度
	public final static int MAX_OFFLINE_BUFF_SIZE = 50;

	private Date runningStateDate; // 运行时间

	private boolean timeout; // 离线运行时间到

	public OfflineDataProviderService(MainBoard mb) {
		super(mb);

		runningStateDate = new Date();
		// 配置过滤器
		filterChain.add(new DataProcessService());
		filterChain.add(new StepFilterService());
		filterChain.add(new OperationFilterService());
		filterChain.add(new ProtectionFilterService());
		try {
			logger = LogUtil.createLog("log/servie/offlineDataProvider.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void provideChannelData(Channel chn) {

		// 从模型缓存区取出采集的源数据
		List<ChannelData> channelDatas = provideCoreChannelData(chn);

		OfflineRunningData ord = Context.getCoreService().getOfflineRunningCfg();
		// 将打包好的数据推送到模型缓存区
		if (!timeout && chn.getRuntimeCaches().size() >= Channel.MAX_RUNNING_CACHE_SIZE || (ord.getMaxRunningTime() > 0
				&& new Date().getTime() - runningStateDate.getTime() >= ord.getMaxRunningTime() * 60 * 1000)) {

			// 暂停设备
			if (chn.getState() == ChnState.RUN) {
				
				logger.info("chn" + (chn.getDeviceChnIndex() + 1) + " runtime buff is full or running max time out ,pause the control unit");
				try {
					Context.getCoreService().emergencyPause(chn.getControlUnit());
				} catch (AlertException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			timeout = true;

		}
		for(ChannelData chnData : channelDatas) {
			
			if(chnData.getState() == ChnState.RUN) {
				
				chn.getRuntimeCaches().add(chnData);
			} else {
				
				chn.setUdtData(chnData); //放入待测数据
			}
		}
		

	}

	@Override
	public void pushRuntimeCacheToPC(int logicIndex) {

		LogicBoard logic = null ;//mainboard.getLogicBoards().get(logicIndex);

		OfflineRunningData ord = Context.getCoreService().getOfflineRunningCfg();

		List<ChannelData> list = new ArrayList<ChannelData>();

		for (DriverBoard db : logic.getDrivers()) {

			for (int n = 0; n < db.getChannelCount(); n++) {

				Channel channel = db.getChannel(n);
				synchronized (channel.getRuntimeCaches()) {

					if (channel.getRuntimeCaches().size() > 1) {

						list.addAll(channel.getRuntimeCaches().subList(0, channel.getRuntimeCaches().size() - 1));
						channel.getRuntimeCaches().subList(0, channel.getRuntimeCaches().size() - 1).clear(); // 清除打包上传缓存

						if (ord.isForbid()) {

							continue; // 禁用离线运行
						}
						for (ChannelData chnData : list) {

							boolean needSave = false;
							if (chnData.getState() == ChnState.RUN) {
								if (channel.getLastSaveOfflineData() == null) {

									// 离线缓存的第一个数据点
									needSave = true;
								} else {

									if (chnData.getDate().getTime()
											- channel.getLastSaveOfflineData().getDate().getTime() >= ord.getSaveTime()
													* 1000) {

										// 保存时间点
										needSave = true;

									} else if (ord.getSaveVoltOffset() > 0 && Math.abs(
											chnData.getVoltage() - channel.getLastSaveOfflineData().getVoltage()) >= ord
													.getSaveVoltOffset()) {

										needSave = true;
									} else if (ord.getSaveCurrOffset() > 0 && Math.abs(
											chnData.getCurrent() - channel.getLastSaveOfflineData().getCurrent()) >= ord
													.getSaveCurrOffset()) {

										needSave = true;
									}

								}
								if (needSave) {
                                    
									channel.log("save offline data" + chnData);
									channel.setLastSaveOfflineData(chnData);
									channel.getOfflineCaches().add(chnData);
									if(channel.getOfflineCaches().size() >= MAX_OFFLINE_BUFF_SIZE) {
										
										channel.log("write offline data to flash");
										Context.getFileSaveService().writeOfflineToDisk(channel);
									}
								}

							}
						} // create offline data
					}

				}
			}
		}

	}

}
