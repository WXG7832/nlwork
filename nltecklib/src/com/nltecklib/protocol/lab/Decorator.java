package com.nltecklib.protocol.lab;

import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Orient;


public interface Decorator extends NlteckIOPackage {
     
	  /**
	   *  获取装饰的对象
	   * @return
	   */
	  Data getDestData();
	  /**
	   * 编码
	   */
	  void encode();
	  /**
	   * 解码
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
