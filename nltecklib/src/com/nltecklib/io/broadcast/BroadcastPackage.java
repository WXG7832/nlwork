package com.nltecklib.io.broadcast;
/**
* @author  wavy_zheng
* @version 创建时间：2020年11月11日 上午10:34:06
* 广播包结构
*/
public class BroadcastPackage {
    
	public String  ipAddress; //网络地址
	public DeviceType  deviceType; //在线设备类型
	public String   identity; //身份信息,配合OA系统，预留
	public Orient   orient; //请求类型
	@Override
	public String toString() {
		return "BroadcastPackage [ipAddress=" + ipAddress + ", deviceType=" + deviceType + ", identity=" + identity
				+ ", orient=" + orient + "]";
	}
	
	
}
