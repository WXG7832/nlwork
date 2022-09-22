package com.nltecklib.protocol.fuel;

import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;

/**
 * 燃料电池装饰器接口，继承了纽联IO数据包
 * 
 * @author caichao_tang
 *
 */
public interface Decorator extends NlteckIOPackage {

	/**
	 * 获取装饰的对象
	 * 
	 * @return 被装饰的协议数据
	 */
	Data getDestData();

	/**
	 * 编码
	 */
	void encode();

	/**
	 * 解码
	 * 
	 * @param encodeData
	 */
	void decode(List<Byte> encodeData);

	/**
	 * 功能码
	 */
	Code getCode();

	/**
	 * 数据区类型
	 */
	Orient getOrient();

}
