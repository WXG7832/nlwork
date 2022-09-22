package com.nlteck.listener;

import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.ChannelDO;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月31日 下午9:11:21
* 类说明
*/
public interface ChnInfoShowListener {
    

	/**
	 * 普通测试项信息显示
	 * @author  wavy_zheng
	 * 2022年3月31日
	 * @param channel
	 */
	public void onTestItemInfo(ChannelDO channel);
	
	
	
	
}
