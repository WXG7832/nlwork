package com.nltecklib.io.mina;
/**
* @author  wavy_zheng
* @version 创建时间：2020年10月31日 下午1:18:15
*  网口收发调试日志消息接口；
*  现实此接口可以截取网络收发日志
*/
public interface DebugDataListener {
    
	
	/**
	 * 网络端口中有编码下发的数据
	 * @author  wavy_zheng
	 * 2020年10月31日
	 * @param data
	 */
	public void  onEncode(byte[] data);
	
	
	/**
	 * 网络端口中有解码接收的数据
	 * @author  wavy_zheng
	 * 2020年10月31日
	 * @param data
	 */
	public void onDecode(byte[] data);
	
}
