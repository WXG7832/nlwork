package com.nlteck.listener;

import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.StableDataDO;
import com.nlteck.model.TestItemDataDO;
import com.nlteck.model.TestLog;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月30日 下午8:03:17
* 监视着通道数据变更
*/
public interface ChnDataChangeLisener {
   
	/**
	 * 一般测试数据变更
	 * @author  wavy_zheng
	 * 2022年3月30日
	 * @param rm
	 * @param item
	 */
	public void onItemChange(RunMode rm , TestItemDataDO item);
	
	
	/**
	 * 稳定度数据变更
	 * @author  wavy_zheng
	 * 2022年3月30日
	 * @param data
	 */
	public void onStableDataChange(StableDataDO data);
	
	/**
	 * 校准数据变更
	 * @author  wavy_zheng
	 * 2022年3月30日
	 * @param data
	 */
	public void onCalDataChange(UploadTestDotData data);
	
	/**
	 * 接收到日志
	 * @author  wavy_zheng
	 * 2022年3月31日
	 * @param log
	 */
	public void onRecvLog(TestLog log);
	
}
