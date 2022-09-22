package com.nltecklib.protocol.modbus.tcp.constant;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.modbus.tcp.util.CommonUtil;
import com.nltecklib.protocol.modbus.tcp.util.ProtocolUtil;



public class Environment {
	
	public static final String TRANSACTION_ID = String.format("%04x", 0);//事务标识
	
	public static final String PROTOCOL_ID = String.format("%04x", 0); //TCP协议标识符

	public static final int  PROTOCOL_DATA_LENGTH = 4;
	
	
	//传输类型
	public enum SendType {
		/**
		 * 数字
		 */
		INT,
		/**
		 * unicode码
		 */
		UNICODE,
		/**
		 * ASCII码
		 */
		ASCII;
	}
	

	/***
	 * TCP协议头部标识
	 * @return
	 */
	public static String getHeadSign() {
		return TRANSACTION_ID + PROTOCOL_ID;
	}
	
	
	
	/**
	 * 获取数据区长度
	 * @param hex
	 * @return
	 */
	public static int getHeadLength() {
		return getHeadSign().length() + PROTOCOL_DATA_LENGTH;
	}

	
	
	/**
	 *   预计目标长度
	 * @param list 
	 * @param hex
	 * @return
	 * @throws ScreenException 
	 */
	public static int getProtocolLength(List<Byte> list) throws Exception {
		
		if(list == null || list.size() == 0) {
			throw new Exception("当前协议内容为空");	
		}
        
		int start = getHeadSign().length()/2;
		
		String hexStr = ProtocolUtil.binaryToHexString(Arrays.asList(new Byte[] {list.get(start),list.get(start+1)}));
		
		return getHeadLength() / 2 + Integer.parseInt(hexStr,16);// + hex.substring(head , head + PROTOCOL_DATA_LENGTH).length();
	}
	


	/**
	 * 获取数据区
	 * @param hex
	 * @return
	 */
	public static String  getData(String hex) {
		
		if(CommonUtil.isEmpty(hex) || hex.length() <= getHeadSign().length()) {
			System.out.println(String.format("数据区内容为空 ! 协议内容：%s", hex));
			return "";
		}
		return hex.substring(getHeadLength(), hex.length());
	}



	/**
	 * 获取数据区长度 字节
	 * @return
	 */
	public static int getHeadByteLength() {
		return getHeadLength() / 2;
	}
	
}
