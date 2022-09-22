package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 用于同步等待压力判断
 * @Desc：   
 * @author：LLC   
 * @Date：2022年2月17日 下午10:37:41   
 * @version
 */
public class SyncPressureParamData  extends Data implements Configable, Responsable {

	/** 步次压力是否到达*/
	private boolean pressureComplete;
	/** 同步等待超时时间 */
	private int timeout;
	
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
		
		data.add((byte)(pressureComplete ? 0x01 : 0x00));
				
		data.addAll(Arrays.asList(ProtocolUtil.split(timeout, 2, true)));
		 
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//工步压力是否到达
		pressureComplete  = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		
		timeout = (int)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SyncPressureParamCode;
	}

	public boolean isPressureComplete() {
		return pressureComplete;
	}

	public void setPressureComplete(boolean pressureComplete) {
		this.pressureComplete = pressureComplete;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
