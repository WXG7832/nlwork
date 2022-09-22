package com.nlteck.service.data;

import java.util.List;

import com.nlteck.firmware.Channel;
import com.nlteck.firmware.MainBoard;
import com.nlteck.service.StartupCfgManager.Range;
import com.nlteck.service.StartupCfgManager.RangeSection;
import com.nltecklib.protocol.li.logic2.Logic2PickupData.ChnData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.power.driver.DriverPickupData.ChnDataPack;


/**
* @author  wavy_zheng
* @version 创建时间：2020年11月16日 上午11:12:32
* 数据过滤接口
*/
public interface DataFilterService {
   
	/**
	 * 过滤采集数据
	 * @author  wavy_zheng
	 * 2020年11月16日
	 */
	public List<ChnDataPack>  filterRawDatas(Channel channel , List<ChnDataPack> rawDatas);  
	
	
	/**
	 * 过滤包装好的数据
	 * @author  wavy_zheng
	 * 2020年11月16日
	 * @param channelDatas
	 * @return
	 */
	public List<ChannelData>  filterChannelDatas(Channel channel ,List<ChannelData> channelDatas);  
	
	
	/**
	 * 根据电流匹配精度范围
	 * @author  wavy_zheng
	 * 2021年1月16日
	 * @param current
	 * @return
	 */
	public static RangeSection findSectionByCurrent(double current) {

		Range range = MainBoard.startupCfg.getRange();
		for (RangeSection section : range.sections) {

			if (current >= section.lower && current < section.upper) {

				return section;

			}
		}
		return null;

	}
}
