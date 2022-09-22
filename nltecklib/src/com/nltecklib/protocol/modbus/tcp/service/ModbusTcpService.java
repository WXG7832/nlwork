package com.nltecklib.protocol.modbus.tcp.service;

import com.nltecklib.protocol.modbus.tcp.constant.Environment.SendType;
import com.nltecklib.protocol.modbus.tcp.data.Data;
import com.nltecklib.protocol.modbus.tcp.data.ReadCoils;
import com.nltecklib.protocol.modbus.tcp.data.ReadHoldRegister;
import com.nltecklib.protocol.modbus.tcp.data.WriteSingleCoil;
import com.nltecklib.protocol.modbus.tcp.data.WriteMultiRegister;
import com.nltecklib.protocol.modbus.tcp.data.WriteSingleRegister;


public class ModbusTcpService extends BaseService{

	/***  基础服务****/

	/**
	 * 批量读取LB值
	 * 应用场景：开关状态读取
	 * @param address
	 * @param count
	 * @param message 功能说明
	 * @return
	 * @throws Exception 
	 */
	public boolean readCoils(int address,int count,String message) throws Exception {
		Data data = new ReadCoils(address, 1);
		return isOn(data,message);
	}
	
	/**
	 * 批量读取LW地址值
	 * 应用场景：多状态值、电压电流、读取属于字节的内容
	 * @param address
	 * @param count
	 * @param message 功能说明
	 * @return
	 * @throws Exception 
	 */
	public String readHoldRegister(int address,int count,String message) throws Exception {
		Data data = new ReadHoldRegister(address,count);		
	    return getText(data,message).trim();
	}

	/**
	 * Ascii 转 字符串
	 * 设置ip地址
	 * @param address
	 * @param count
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public String readHoldRegisterAscii(int address,int count,String message) throws Exception {
		Data data = new ReadHoldRegister(address,count);		
	    return getAsciiToString(data,message).trim();
	}
	
	/**
	  *   单个位的写入
	  *   应用场景：对 单控制状态的切换
	 * @param address
	 * @param value
	 * @param message 功能说明
	 * @throws Exception 
	 */
	public void WriteSingleCoil(int address,boolean value,String message) throws Exception{
		Data data = new WriteSingleCoil(value,address);
		sendProtocol(data,message);
	}
	
	/**
	 * 写入多个字节的数据
	 * 应用场景： 后台时间同步液晶屏、写入电压电流数据
	 * @param address
	 * @param count
	 * @param value
	 * @param type
	 * @param message 功能说明
	 * @throws Exception 
	 */
	public void WriteMultiRegister(int address,int count,Object value,SendType type,String message) throws Exception{
		Data data = new WriteMultiRegister(address,count, value,type);
		setText(data,message);
	}
	
	/**
	 * 写入单字节的数据
	 * 应用场景：多状态控件切换
	 * @param address
	 * @param value
	 * @param message 功能说明
	 * @throws Exception 
	 */
	public void WriteSingleRegister(int address,int value,String message) throws Exception{
		Data data = new WriteSingleRegister(address,value);
		sendProtocol(data,message);
	}

	
}
