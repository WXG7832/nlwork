package com.nlteck.service.accessory;

import com.nltecklib.protocol.lab.accessory.IPC_PowerOptData;

/**
* @author  wavy_zheng
* @version 创建时间：2022年8月4日 上午8:47:13
* 用于实验室控制板服务组件
*/
public abstract class AbsControlboardService {
  
	/**
	 * 打开电源
	 * @author  wavy_zheng
	 * 2022年8月4日
	 * @param on
	 * @throws Exception
	 */
	public abstract void power(boolean on) throws Exception;
	
	/**
	 * 切断电源
	 */
	public abstract void cutoffPower() throws Exception;
	
	
	
	/**
	 * 修改控制板IP地址
	 * @author  wavy_zheng
	 * 2022年6月15日
	 * @param ip
	 * @throws Exception
	 */
	public abstract void changeIp(String ip) throws Exception;
	
	
	
	/**
	 * 心跳
	 * @author  wavy_zheng
	 * 2022年6月15日
	 * @throws Exception
	 * @return  返回电源检测信息
	 */
	public abstract IPC_PowerOptData heartbeat() throws Exception;
	
}
