package com.nltecklib.protocol.li.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatLine;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMeterCurrent;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.OverTempFlag;
import com.nltecklib.protocol.util.ProtocolUtil;


public class TempQueryData extends Data implements Queryable, Responsable {
    
	private double mainTemp; //主要温度
	private double subTemp;  //辅助温度
	private OverTempFlag   overTempFlag = OverTempFlag.NORMAL;
	private HeatMode       heatMode     = HeatMode.ST;
	private HeatLine       heatLine     = HeatLine.NORMAL;
	private HeatMeterCurrent    heatMeterCurrent     = HeatMeterCurrent.NORMAL;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(mainTemp), 2,true)));
        data.addAll(Arrays.asList(ProtocolUtil.split((long)(subTemp), 2,true)));
        data.add((byte) overTempFlag.ordinal());
        data.add((byte) heatMode.ordinal());
        data.add((byte) heatLine.ordinal());
        data.add((byte) heatMeterCurrent.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		 int index = 0;
         data = encodeData;
         driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
         //main temp
         mainTemp  = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
 		 index += 2;
 		 //sub temp
 		 subTemp  = (double) ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10;
		 index += 2;
		 //flag
		 int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		 if(code > OverTempFlag.values().length - 1) {
			 
			 throw new RuntimeException("error over temp flag : " + code);
		 }
		 overTempFlag = OverTempFlag.values()[code];
		 //heat mode
		 code = ProtocolUtil.getUnsignedByte(data.get(index++));
		 if(code > HeatMode.values().length - 1) {
			 
			 throw new RuntimeException("error heat mode code : " + code);
		 }
		 heatMode = HeatMode.values()[code];
		 
		 //heat line
		 code = ProtocolUtil.getUnsignedByte(data.get(index++));
		 if(code > HeatLine.values().length - 1) {
			 
			 throw new RuntimeException("error heat line code : " + code);
		 }
		 heatLine = HeatLine.values()[code];
		 
		 //heat meter current
		 code = ProtocolUtil.getUnsignedByte(data.get(index++));
		 if(code > HeatMeterCurrent.values().length - 1) {
			 
			 throw new RuntimeException("error heat meter current code : " + code);
		 }
		 heatMeterCurrent = HeatMeterCurrent.values()[code];
		 
		 

	}

	@Override
	public Code getCode() {
		
		return AccessoryCode.TempQueryCode;
	}

	public double getMainTemp() {
		return mainTemp;
	}

	public void setMainTemp(double mainTemp) {
		this.mainTemp = mainTemp;
	}

	public double getSubTemp() {
		return subTemp;
	}

	public void setSubTemp(double subTemp) {
		this.subTemp = subTemp;
	}

	public OverTempFlag getOverTempFlag() {
		return overTempFlag;
	}

	@Override
	public String toString() {
		return "TempQueryData [mainTemp=" + mainTemp + ", subTemp=" + subTemp + ", overTempFlag=" + overTempFlag + "]";
	}

	public HeatMode getHeatMode() {
		return heatMode;
	}

	public HeatLine getHeatLine() {
		return heatLine;
	}

	public HeatMeterCurrent getHeatMeterCurrent() {
		return heatMeterCurrent;
	}

	public void setOverTempFlag(OverTempFlag overTempFlag) {
		this.overTempFlag = overTempFlag;
	}

	public void setHeatMode(HeatMode heatMode) {
		this.heatMode = heatMode;
	}

	public void setHeatLine(HeatLine heatLine) {
		this.heatLine = heatLine;
	}

	public void setHeatMeterCurrent(HeatMeterCurrent heatMeterCurrent) {
		this.heatMeterCurrent = heatMeterCurrent;
	}

	
	
	

}
