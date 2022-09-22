package com.nltecklib.protocol.li.check2;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.check2.Check2Environment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月19日 下午2:36:16 回检超压后续处理协议
 */
public class Check2OverVoltProcessData extends Data implements Configable, Responsable {

	private int checkCount = 3; // 检测电压连续上升个数
	private int checkSeconds = 10; // 检测用时，单位s，0表示检测到退出模式
	private int checkTimeSpan = 2000; //默认检测间隔时间

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) checkCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) checkSeconds, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) checkTimeSpan, 2, true)));
        
	}

	@Override
	public void decode(List<Byte> encodeData) {
       
		int index = 0 ;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		checkCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		checkSeconds = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		checkTimeSpan = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Check2Code.OverVoltCode;
	}


	public int getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

	public int getCheckSeconds() {
		return checkSeconds;
	}

	public void setCheckSeconds(int checkSeconds) {
		this.checkSeconds = checkSeconds;
	}

	public int getCheckTimeSpan() {
		return checkTimeSpan;
	}

	public void setCheckTimeSpan(int checkTimeSpan) {
		this.checkTimeSpan = checkTimeSpan;
	}
	
	
	

}
