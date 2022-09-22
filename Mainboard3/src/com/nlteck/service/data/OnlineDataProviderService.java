package com.nlteck.service.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.nlteck.Context;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.PCNetworkService;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.PickupData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月16日 上午10:46:31 网络正常在线的数据工厂
 */
public class OnlineDataProviderService extends DataProviderService {

	private Logger logger;
	public final static int MAX_SEND_RECV_COUNT = 5; // 发送接收最大差异数，用于判断设备是否掉线

	public OnlineDataProviderService(MainBoard mb) {
		super(mb);

		// 配置过滤器
		filterChain.add(new DataProcessService());
		filterChain.add(new StepFilterService());
		filterChain.add(new OperationFilterService());
		filterChain.add(new ProtectionFilterService());

		try {
			logger = LogUtil.createLog("log/service/onlineDataProvider.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	@Override
	public void provideChannelData(Channel chn) {

		// 从模型缓存区取出采集的源数据
		List<ChannelData> channelDatas = provideCoreChannelData(chn);

		// 将打包好的数据推送到模型缓存区
		if (chn.getRuntimeCaches().size() >= Channel.MAX_RUNNING_CACHE_SIZE) {

			logger.info("chn" + (chn.getDeviceChnIndex() + 1) + " runtime buff is full");
			chn.log(chn.getRuntimeCaches().toString());
			if (!mainboard.isOffline()) {
				Context.getPcNetworkService().cutoff();
			}
		}
		for(ChannelData chnData : channelDatas) {
			
			if(chnData.getState() == ChnState.RUN) {
				
				chn.getRuntimeCaches().add(chnData); //加入运行缓存区
			} else {
				
				//System.out.println("set udt data:" + chnData);
				chn.setUdtData(chnData); //放入待测数据
			}

		}
		

	}

	@Override
	public void pushRuntimeCacheToPC(int driverIndex) {

		// 每次推送后至少保留一个缓存数据
		PickupData pd = new PickupData();
		pd.setDate(new Date());
		pd.setResult(DefaultResult.SUCCESS);

		pd.setState(mainboard.getState());
		pd.setLoopIndex(0);

		DriverBoard driver = mainboard.getDriverByIndex(driverIndex);

		List<ChannelData> list = new ArrayList<ChannelData>();

		for (int n = 0; n < driver.getChannelCount(); n++) {

			Channel channel = driver.getChannel(n);
			synchronized (channel.getRuntimeCaches()) {

				if (channel.getRuntimeCaches().size() > 1) {

					list.addAll(channel.getRuntimeCaches().subList(0, channel.getRuntimeCaches().size() - 1));
					channel.getRuntimeCaches().subList(0, channel.getRuntimeCaches().size() - 1).clear(); // 清除打包上传缓存

				}

			}
		}

		pd.setChnCount(list.size());
		pd.setTemp(0); // 电源柜没有此项
		pd.setChnDatas(list); // 设置采集数据集
		pd.setUnitIndex(0);

		// 将采集到的数据推送给电脑
		PCNetworkService pcService = (PCNetworkService) Context.getPcNetworkService();
		pcService.pushDataToPc(pd);

		

	}

}
