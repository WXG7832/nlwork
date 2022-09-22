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

public class CylinderPressureProtectData extends Data implements Configable,Queryable,Responsable{

	private double pressureUpper;
	private double pressureLower;

	@Override
	public void encode() {

		data.addAll(Arrays.asList(ProtocolUtil.split((long)(pressureLower * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(pressureUpper * 10), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
	    data = encodeData;
	    pressureLower = ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10.0;
		index += 2;
		pressureUpper = ProtocolUtil.compose(data.subList(index, index+2).toArray(new Byte[0]), true) / 10.0;
		index += 2;
	}

	@Override
	public Code getCode() {
		
		return MainCode.CylinderPressureCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}
	
	public double getPressureUpper() {
		return pressureUpper;
	}

	public void setPressureUpper(double pressureUpper) {
		this.pressureUpper = pressureUpper;
	}

	public double getPressureLower() {
		return pressureLower;
	}

	public void setPressureLower(double pressureLower) {
		this.pressureLower = pressureLower;
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
		} else if (obj instanceof CylinderPressureProtectData){
			
			CylinderPressureProtectData pd = (CylinderPressureProtectData) obj;
			if (pd.getPressureUpper() == this.getPressureUpper()
					&& pd.getPressureLower() == this.getPressureLower()) {
				
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "CylinderPressureProtectData [pressureUpper=" + pressureUpper + ", pressureLower=" + pressureLower + "]";
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
