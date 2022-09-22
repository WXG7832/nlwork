package com.nltecklib.protocol.fins;

import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Command;
import com.nltecklib.protocol.fins.Environment.DataUnit;
import com.nltecklib.protocol.fins.Environment.Error;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.fins.Environment.Result;

public abstract class Data implements NlteckIOPackage{
	
	protected int address;
	protected Area area = Area.DM;
	protected List<Byte> data = new LinkedList<Byte>();
	protected int datalength;
	protected Orient orient = Orient.READ;
	protected boolean isBit;
	protected Error error = Error.NO;
	protected Command command = Command.DATA;
	protected int     offset; //数据存储偏移地址
	protected Result result;
	protected DataUnit  dataUnit = DataUnit.BYTE; //数据操作单元
	
	
	
	
	public void encode() {
		
		
		
	}

	public abstract void decode(List<Byte> encodeData); // 解码

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<Byte> getData() {
		return data;
	}

	public void setData(List<Byte> data) {
		this.data = data;
	}

	public Orient getOrient() {
		return orient;
	}

	public void setOrient(Orient orient) {
		this.orient = orient;
	}

	public boolean isBit() {
		return isBit;
	}

	public void setBit(boolean isBit) {
		this.isBit = isBit;
	}
	
	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public int getDatalength() {
		return datalength;
	}

	public void setDatalength(int datalength) {
		this.datalength = datalength;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	
	/**
	 * 将int转换成byte[],低位在前,如1234 转换成	{-46,4,0,0}
	 * @param number
	 * @return
	 */
	public byte[] intToByteArray(int number) {   
		return new byte[] {   
				(byte) (number & 0xFF),   
				(byte) ((number >> 8) & 0xFF),      
				(byte) ((number >> 16) & 0xFF),      
				(byte) ((number >> 24) & 0xFF),   
		};   
	}
	

	/**
	 * 将float转换成byte[],低位在前,如1.5 转换成
	 * @param number
	 * @return
	 */
	public byte[] floatToByteArray(float number) {   
		int value = Float.floatToIntBits(number);
		return new byte[] {   
				(byte) (value & 0xFF),   
				(byte) ((value >> 8) & 0xFF),      
				(byte) ((value >> 16) & 0xFF),      
				(byte) ((value >> 24) & 0xFF),   
		};   
	}
}
