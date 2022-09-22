package com.nltecklib.io;

/**
 * 纽联IO数据包工厂
 * @author Administrator
 *
 */
public interface NlteckPackageFactory {
     
	/**
	 * 将字节流数据解析成数据包
	 * @param data
	 * @return
	 */
	public  NlteckIOPackage decode(byte[] data);
	
	/**
	 * 将数据包对象编码成字节流
	 * @param pack
	 * @return
	 */
	public byte[]   encode(NlteckIOPackage  pack) ;
	
	
	/**
	 * 获取最小可解析的数据长度
	 * @return
	 */
	public int  getMinDecodeLen();
	
	
	/**
	 * 获取完整的数据包长度;注意此时传入的data长度必须满足大于getMinDecodeLen()返回的值
	 */
	public int getPackLen(byte[] data);
	
	/**
	 * 寻找协议头的位置
	 * @param data
	 * @return
	 */
	public int findHeadPos(byte[] data);
	
}
