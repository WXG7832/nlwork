package com.nltecklib.protocol.fins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Command;
import com.nltecklib.protocol.fins.Environment.DataUnit;
import com.nltecklib.protocol.fins.Environment.Error;
import com.nltecklib.protocol.fins.Environment.Orient;
import com.nltecklib.protocol.fins.Environment.Result;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年7月31日 下午4:37:31
* 类说明
*/
public abstract class FinsProtocol implements NlteckIOPackage {
     
	
	protected static final Byte[] HEAD = new Byte[] { 0x46, 0x49, 0x4E, 0x53 };
	protected Area area = Area.DM; //寄存器类型
	protected int address; // 地址
	protected int offset; // 在地址基础上偏移位置
	protected List<Byte> data = new ArrayList<Byte>(); // 数据区
	protected Orient orient = Orient.READ; // 读还是写?
	protected Error error = Error.NO; // 错误码
	protected Command command = Command.DATA; // 指令类型
	protected Result result = Result.OK;
	protected DataUnit dataUnit = DataUnit.BYTE;
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address = address;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
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
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public DataUnit getDataUnit() {
		return dataUnit;
	}
	public void setDataUnit(DataUnit dataUnit) {
		this.dataUnit = dataUnit;
	}
	
	public void addData(int value) {
		
		if(dataUnit == DataUnit.BYTE) {
		   data.addAll( Arrays.asList(ProtocolUtil.split(value, 2,  true /*area == Area.DM*/)));
		} else if(dataUnit == DataUnit.BIT) {
			
			data.add((byte) value);
		}
	}
	
	public void clearData() {
		
		data.clear();
	}

}
