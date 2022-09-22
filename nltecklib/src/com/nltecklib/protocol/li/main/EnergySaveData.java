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
 * 节能协议
 * @author Administrator
 *
 */
public class EnergySaveData extends Data implements Configable, Responsable, Queryable {
    
	private boolean useSmartPower;
	private boolean useSmartFan;
	private int     maxPowerWaitMin;
	private int     maxTempControlWaitMin;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) (useSmartPower ? 0x01 : 0x00));
		data.add((byte) (useSmartFan ? 0x01 : 0x00));
        data.addAll(Arrays.asList(ProtocolUtil.split(maxPowerWaitMin, 2, true)));
        data.addAll(Arrays.asList(ProtocolUtil.split(maxTempControlWaitMin, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		useSmartPower = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		useSmartFan   = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		maxPowerWaitMin = (int)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		maxTempControlWaitMin = (int)ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		
		return MainCode.EnergyCode;
	}

	public boolean isUseSmartPower() {
		return useSmartPower;
	}

	public void setUseSmartPower(boolean useSmartPower) {
		this.useSmartPower = useSmartPower;
	}

	public boolean isUseSmartFan() {
		return useSmartFan;
	}

	public void setUseSmartFan(boolean useSmartFan) {
		this.useSmartFan = useSmartFan;
	}

	public int getMaxPowerWaitMin() {
		return maxPowerWaitMin;
	}

	public void setMaxPowerWaitMin(int maxPowerWaitMin) {
		this.maxPowerWaitMin = maxPowerWaitMin;
	}

	public int getMaxTempControlWaitMin() {
		return maxTempControlWaitMin;
	}

	public void setMaxTempControlWaitMin(int maxTempControlWaitMin) {
		this.maxTempControlWaitMin = maxTempControlWaitMin;
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
