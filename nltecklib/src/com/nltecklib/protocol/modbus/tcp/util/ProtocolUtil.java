package com.nltecklib.protocol.modbus.tcp.util;

import java.util.List;

import com.nltecklib.protocol.modbus.tcp.constant.Environment;
import com.nltecklib.protocol.modbus.tcp.data.Data;

public class ProtocolUtil {

	
	public static int calUnicodeLength(String str) {
		
		if(CommonUtil.isEmpty(str)){return 0;}
		
		return ConvertUtils.stringToUnicode(str).replaceAll(" ","").length() / 2;
	}
	
    public static  String binaryToHexString(List<Byte> list) {
    	return ConvertUtils.binaryToHexString(list.toArray(new Byte[list.size()])).toUpperCase()
		.replaceAll(" ", "").trim();
    } 
    
    public static  String intToHexString(int data,int length) {
    	
    	String hex = Integer.toHexString(data);
    	
    	int offset = length - hex.length();
    	
    	if(offset > 0) {
    	   for(int num = 0 ; offset > num ; num++) {
    		   hex = "0" + hex;
    	   }	
    	}
    	return hex;
    }
 
	
	/***
	 *  校验 ModbusTcp 协议头是否合法
	 * @param hex
	 * @return
	 */
	public static boolean  validHead(String hex) {
		return hex.startsWith(Environment.getHeadSign());
	}

	public static boolean  validSlaveID(String hex) throws Exception{

		hex = hex.substring(Environment.getHeadLength(),hex.length());
		
		if(hex.startsWith(Data.SLAVE_ID)){	
			return true;
		}
		
		return false;
	}
	
	
	public static String validProtocol(String hex) throws Exception {
		
		if(hex.length() < Environment.getHeadLength()){
			throw new Exception(String.format("协议长度存在问题,hex: %s ", hex));
		}
		
		if(!ProtocolUtil.validHead(hex)) {
			throw new Exception(String.format("协议头不匹配,hex: %s ", hex));
		}
		
		if(!ProtocolUtil.validSlaveID(hex)) {
			throw new Exception(String.format("不是以01开始的异常指令,hex : %s ", hex));
		}
		
		return hex;
		
	}
}
