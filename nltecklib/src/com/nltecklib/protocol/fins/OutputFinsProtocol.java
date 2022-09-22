package com.nltecklib.protocol.fins;

import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Command;
import com.nltecklib.protocol.fins.Environment.DataUnit;
import com.nltecklib.protocol.fins.Environment.Orient;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年7月31日 下午2:55:59 类说明
 */
public class OutputFinsProtocol extends FinsProtocol implements NlteckIOPackage,Configable,Queryable{


	
	/**
	 * 禁止直接创建
	 */
	private OutputFinsProtocol() {
		
		
	}
	

	public static byte getLastIpAddr(String ip) {

		String[] secs = ip.split("\\.");
		if (secs.length != 4) {

			throw new RuntimeException("error ip address:" + ip);
		}
		return (byte) Integer.parseInt(secs[3]);

	}
	/**
	 * 握手协议
	 * @author  wavy_zheng
	 * 2020年7月31日
	 * @param localIp
	 * @return
	 */
	public static OutputFinsProtocol createHandShakeProtocol() {
		
		OutputFinsProtocol protocol = new OutputFinsProtocol();
		protocol.command = Command.HANDSHAKE;
		return protocol;
	}
	
	/**
	 * 创建一个输出（写入）FINS协议包
	 * @author  wavy_zheng
	 * 2020年7月31日
	 * @param area  寄存器类型
	 * @param du  控制单元
	 * @return
	 */
	public static OutputFinsProtocol createOutputProtocol(Area area , DataUnit du ) {
		
		OutputFinsProtocol protocol = new OutputFinsProtocol();
		protocol.area = area;
		protocol.dataUnit = du;
		
		return protocol;
		
	}
	
	public void  writeData( int address , int offset , List<Integer> outputData) {
		
		this.address = address;
		this.offset = offset;
		for(int n = 0 ; n < outputData.size();n++) {
			
			addData(outputData.get(n));
		}
		this.orient = Orient.WRITE;
	}
	
	public void readData(int address , int offset ,int readLen) {
		
		this.address = address;
		this.offset = offset;
		this.orient = Orient.READ;
		data.clear();
	
		for(int n = 0 ; n < (dataUnit == DataUnit.BYTE ? readLen * 2 : readLen) ; n++) {
		   data.add((byte) 0);
		}
	}

}
