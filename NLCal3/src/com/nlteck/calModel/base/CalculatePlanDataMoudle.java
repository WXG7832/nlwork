package com.nlteck.calModel.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nlteck.calModel.base.CalCfgManager.CalculatePlanMoudle;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculatePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  w_xg
* 单膜片计量方案
*/
public class CalculatePlanDataMoudle extends Data implements Configable, Queryable, Responsable {
    
	private double maxMeterOffset;
	private double maxAdcOffset;
	private double maxAdcOffsetCheck;
	private double maxAdcOffsetCV2;
	
	private double minCalculateCurrent;
	private double maxCalculateCurrent;
	private double minCalculateVoltage;
	private double maxCalculateVoltage;
	public  int moudleIndex;
	
	
	private List<CalculatePlanMoudle> modes = new ArrayList<>();
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

	
	
	public void setModes(List<CalculatePlanMoudle> modes) {
		this.modes = modes;
	}

	


	public double getMaxAdcOffsetCV2() {
		return maxAdcOffsetCV2;
	}

	public void setMaxAdcOffsetCV2(double maxAdcOffsetCV2) {
		this.maxAdcOffsetCV2 = maxAdcOffsetCV2;
	}

	public double getMaxAdcOffsetCheck() {
		return maxAdcOffsetCheck;
	}

	public void setMaxAdcOffsetCheck(double maxAdcOffsetCheck) {
		this.maxAdcOffsetCheck = maxAdcOffsetCheck;
	}

	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (maxMeterOffset * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (maxAdcOffset * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (minCalculateCurrent * 100), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (maxCalculateCurrent * 100), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (minCalculateVoltage * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (maxCalculateVoltage * 10), 3, true)));
		data.add((byte) modes.size());
		for(CalculatePlanMoudle mode : modes) {
			
			data.add((byte) mode.mode.ordinal());
			data.add((byte) mode.pole.ordinal());
			data.add((byte) (mode.disabled ? 0x00 : 0x01));
			data.add((byte) mode.dots.size());
			for(Double val : mode.dots) {
				
				data.addAll(Arrays.asList(ProtocolUtil.split((long) (val * 10), 3, true)));
			}
			
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		maxMeterOffset = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
        index += 2;
        maxAdcOffset = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
        index += 2;
        minCalculateCurrent = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
        index += 3;
        maxCalculateCurrent = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
        index += 3;
        minCalculateVoltage = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
        index += 3;
        maxCalculateVoltage = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
        index += 3;
        int count = ProtocolUtil.getUnsignedByte(data.get(index++));
        for(int n = 0 ; n < count ; n++) {
        	
        	CalculatePlanMoudle pm = new CalculatePlanMoudle();
        	int code = ProtocolUtil.getUnsignedByte(data.get(index++));
        	if(code > CalMode.values().length - 1) {
        		
        		throw new RuntimeException("invalid mode code : " + code);
        	}
        	pm.mode = CalMode.values()[code];
        	
        	code = ProtocolUtil.getUnsignedByte(data.get(index++));
        	if(code > Pole.values().length - 1) {
        		
        		throw new RuntimeException("invalid pole code : " + code);
        	}
        	pm.pole = Pole.values()[code];
        	
        	pm.disabled =  ProtocolUtil.getUnsignedByte(data.get(index++)) == 0 ;
        	
        	int dotCount = ProtocolUtil.getUnsignedByte(data.get(index++));
        	
        	for(int i = 0 ; i < dotCount ; i++) {
        		
        		 pm.dots.add((double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10);
        	     index += 3;
        	}
        	
        	addCalculateMode(pm);
        }
        
	}
	
	public void  addCalculateMode(CalculatePlanMoudle  mode ) {
		
		modes.add(mode);
	}
	
	

	public double getMaxMeterOffset() {
		return maxMeterOffset;
	}

	public void setMaxMeterOffset(double maxMeterOffset) {
		this.maxMeterOffset = maxMeterOffset;
	}

	public double getMaxAdcOffset() {
		return maxAdcOffset;
	}

	public void setMaxAdcOffset(double maxAdcOffset) {
		this.maxAdcOffset = maxAdcOffset;
	}

	public double getMinCalculateCurrent() {
		return minCalculateCurrent;
	}

	public void setMinCalculateCurrent(double minCalculateCurrent) {
		this.minCalculateCurrent = minCalculateCurrent;
	}

	public double getMaxCalculateCurrent() {
		return maxCalculateCurrent;
	}

	public void setMaxCalculateCurrent(double maxCalculateCurrent) {
		this.maxCalculateCurrent = maxCalculateCurrent;
	}

	public double getMinCalculateVoltage() {
		return minCalculateVoltage;
	}

	public void setMinCalculateVoltage(double minCalculateVoltage) {
		this.minCalculateVoltage = minCalculateVoltage;
	}

	public double getMaxCalculateVoltage() {
		return maxCalculateVoltage;
	}

	public void setMaxCalculateVoltage(double maxCalculateVoltage) {
		this.maxCalculateVoltage = maxCalculateVoltage;
	}

	public List<CalculatePlanMoudle> getModes() {
		return modes;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.CalculatePlanCode;
	}

	@Override
	public String toString() {
		return "CalculatePlanData [maxMeterOffset=" + maxMeterOffset + ", maxAdcOffset=" + maxAdcOffset
				+ ", minCalculateCurrent=" + minCalculateCurrent + ", maxCalculateCurrent=" + maxCalculateCurrent
				+ ", minCalculateVoltage=" + minCalculateVoltage + ", maxCalculateVoltage=" + maxCalculateVoltage
				+ ", modes=" + modes + "]";
	}

}

