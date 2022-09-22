package com.nltecklib.protocol.modbus.tcp.service;

import java.io.IOException;
import java.util.List;
import com.nltecklib.protocol.modbus.tcp.ModbusTcpMaster;
import com.nltecklib.protocol.modbus.tcp.data.Data;
import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;


public abstract class BaseService {

	private ModbusTcpMaster server;
	
	public void setModbusTcpMaster(ModbusTcpMaster server) {
		this.server = server;	
	}
	

	/**
	 * 从hmi获得文本信息
	 * @param data
	 * @param message 异常信息
	 * @return
	 * @throws Exception 
	 */
	protected String getText(Data data,String message) throws Exception{
		try {
			String hex = server.send(data);
			//System.out.println(hex);
			String result = (String) data.decoder(hex);
			if (result == null ) {
				throw new Exception(String.format("%s decoding error ", message));
			}
			return Integer.valueOf(result,16) + "";
		} catch (IOException e) {
			throw new Exception(String.format("%s,io exception", message));
		}catch (InterruptedException e) {
			throw new Exception(String.format("%s,interrupted exception",message));
		}
	}
	
	protected String getAsciiToString(Data data,String message) throws Exception{
		try {
			String hex = server.send(data);
			//System.out.println(hex);
			String result = (String) data.decoder(hex);
			if (result == null ) {
				throw new Exception(String.format("%s decoding error ", message));
			}
			return ConvertUtils.asciiToString(result);
		} catch (IOException e) {
			throw new Exception(String.format("%s,io exception", message));
		}catch (InterruptedException e) {
			throw new Exception(String.format("%s,interrupted exception",message));
		}
	}
	
	/**
	 * 发送信息给hmi
	 * @param data
	 * @param message
	 * @return
	 * @throws Exception 
	 */
	protected void setText(Data data,String message) throws Exception{

		try {
			String hex = server.send(data);
			boolean b = (boolean) data.decoder(hex);
			if (!b) {
				throw new Exception(String.format("%s decoding error ", message));
			}
		}catch (IOException e) {
			throw new Exception(String.format("%s,io exception", message));
		}catch (InterruptedException e) {
			throw new Exception(String.format("%s,interrupted exception",message));
		}

	}
	/**
	 * 发送信息给hmi
	 * @param data
	 * @param message
	 * @return
	 * @throws Exception 
	 */
	protected void sendProtocol(Data data,String message) throws Exception{

		try {
			String hex = server.send(data);
			//System.out.println(hex);
			boolean b = (boolean) data.decoder(hex);
			if (!b) {
				throw new Exception(String.format("%s decoding error ", message));
			}
		}catch (IOException e) {
			throw new Exception(String.format("%s io exception", message));
		}catch (InterruptedException e) {
			throw new Exception(String.format("%s interrupted exception",message));
		}
		
	}
	
	
	/**
	 * 从hmi获取信息判断是否开启
	 * @param data
	 * @param message 异常信息
	 * @return
	 * @throws Exception 
	 */
	protected boolean isOn(Data data,String message) throws Exception {
		try {
			String hex = server.send(data);
			@SuppressWarnings("unchecked")
			List<Integer> results = (List<Integer>) data.decoder(hex);
			if (results == null || results .size() == 0 ) {
				throw new Exception(String.format("%s decoding error ", message));
			}
			if (results.get(0) == 1) {
				return true;
			}
		}catch (IOException e) {
			throw new Exception(String.format("%s io exception", message));
		}catch (InterruptedException e) {
			throw new Exception(String.format("%s interrupted exception",message));
		}
		return false;
	}
	
}
