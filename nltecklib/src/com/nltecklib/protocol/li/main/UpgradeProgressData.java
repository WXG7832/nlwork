package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.UpgradeType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 升级进度上报
 * @author Administrator
 *
 */
public class UpgradeProgressData extends Data implements Alertable, Responsable {
   
	private UpgradeType  upgradeType = UpgradeType.Core; //升级类型
	private int          pos;
	private int          range;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) upgradeType.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.split((long)pos, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)range, 2, true)));
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
	    data = encodeData;
	    unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    int code = ProtocolUtil.getUnsignedByte(data.get(index++));
	    if(code > UpgradeType.values().length - 1) {
	    	
	    	throw new RuntimeException("error upgrade type code :" + code);
	    }
	    upgradeType = UpgradeType.values()[code];
		//总字节数
		pos = (int) ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
		//包总数
		range = (int) ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		index += 2;
	

	}

	@Override
	public Code getCode() {
		
		return MainCode.UpgradeProgressCode;
	}



	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(UpgradeType type) {
		this.upgradeType = type;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
