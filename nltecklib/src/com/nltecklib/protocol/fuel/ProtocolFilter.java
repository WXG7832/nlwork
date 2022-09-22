package com.nltecklib.protocol.fuel;

import java.util.List;

/**
 * 协议过滤器接口
 * @author Administrator
 *
 */
public interface ProtocolFilter {
    
	/**
	 * 在编码前处理
	 */
	public void beforeEncode(Data data);
	
	
	/**
	 * 在编码后运行
	 */
	public void afterEncode(List<Byte> encodeData);
	
	
	/**
	 * 在解码前运行
	 */
	public void beforeDecode(List<Byte> decodeData);
	
	/**
	 * 在解码后运行
	 */
	public void afterDecode(Data data);
	
}
