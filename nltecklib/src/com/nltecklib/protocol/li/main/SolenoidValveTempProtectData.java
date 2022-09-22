package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class SolenoidValveTempProtectData extends Data implements Configable,Queryable,Responsable{
    
	private double temperatureUpper;

	@Override
	public void encode() {

        data.add((byte) temperatureUpper);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
	    data = encodeData;
	    temperatureUpper = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		
		return MainCode.TrayTempUpperCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}
	
	public double getTemperatureUpper() {
		return temperatureUpper;
	}

	public void setTemperatureUpper(double temperatureUpper) {
		this.temperatureUpper = temperatureUpper;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof SolenoidValveTempProtectData){
			
			SolenoidValveTempProtectData pd = (SolenoidValveTempProtectData) obj;
			if (pd.getTemperatureUpper() == this.getTemperatureUpper()) {
				
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "SolenoidValveTempProtectData [temperatureUpper=" + temperatureUpper + "]";
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
