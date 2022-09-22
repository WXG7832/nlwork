package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 离线运行
 * @author Administrator
 *
 */
public class OfflineRunningData extends Data implements Configable, Queryable, Responsable {
     
	private boolean      forbid =  true; //禁用离线运行?
	private int          saveTime; //离线数据保存最大间隔时间
	private int          saveVoltOffset; //离线数据保存最大偏差电压值,mV
	private int          saveCurrOffset; //离线数据保存最大偏差电流值,mA
	private int          maxRunningTime = 5;  //默认最大运行时间，单位min;0表示运行到流程结束
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void encode() {
		
		//开关状态
		data.add((byte)(forbid ? 0 : 1));
		//离线时间
		data.addAll(Arrays.asList(ProtocolUtil.split((long)saveTime, 2, true)));
		//电压偏差保存阀值
		data.add((byte) saveVoltOffset);
		//电流偏差保存阀值
		data.add((byte) saveCurrOffset);
		//最大运行时间
		data.add((byte)maxRunningTime);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > 1) {
			
			throw new RuntimeException("error switch state code: " + code);
		}
		forbid = code == 0;
		//离线时间
		saveTime = (int)ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		saveVoltOffset = ProtocolUtil.getUnsignedByte(data.get(index++));
		saveCurrOffset = ProtocolUtil.getUnsignedByte(data.get(index++));
		maxRunningTime = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		
		return MainCode.OfflineRunningCode;
	}

	

	public boolean isForbid() {
		return forbid;
	}


	public void setForbid(boolean forbid) {
		this.forbid = forbid;
	}


	public int getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(int saveTime) {
		this.saveTime = saveTime;
	}

	public int getSaveVoltOffset() {
		return saveVoltOffset;
	}

	public void setSaveVoltOffset(int saveVoltOffset) {
		this.saveVoltOffset = saveVoltOffset;
	}

	public int getSaveCurrOffset() {
		return saveCurrOffset;
	}

	public void setSaveCurrOffset(int saveCurrOffset) {
		this.saveCurrOffset = saveCurrOffset;
	}


	public int getMaxRunningTime() {
		return maxRunningTime;
	}


	public void setMaxRunningTime(int maxRunningTime) {
		this.maxRunningTime = maxRunningTime;
	}


	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
