package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.CapacityUnit;
import com.nltecklib.protocol.lab.main.MainEnvironment.CurrentUnit;
import com.nltecklib.protocol.lab.main.MainEnvironment.EnergyUnit;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.PowerUnit;
import com.nltecklib.protocol.lab.main.MainEnvironment.ResisterUnit;
import com.nltecklib.protocol.lab.main.MainEnvironment.TimeUnit;
import com.nltecklib.protocol.lab.main.MainEnvironment.VoltageUnit;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年8月9日 下午1:42:41
* 主控单位管理
*/
public class UnitManageData extends Data implements Configable, Queryable, Responsable {
    
	private VoltageUnit voltageUnit = VoltageUnit.V;
	private CurrentUnit currentUnit = CurrentUnit.A;
	private CapacityUnit capacityUnit = CapacityUnit.Ah;
	private EnergyUnit energyUnit = EnergyUnit.Wh;
	private PowerUnit  powerUnit  = PowerUnit.W;
	private ResisterUnit  resisterUnit = ResisterUnit.R;
	private TimeUnit    timeUnit = TimeUnit.s;
	
	@Override
	public boolean supportMain() {
		
		return true;
	}

	@Override
	public boolean supportChannel() {
		
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) voltageUnit.ordinal());
		data.add((byte) currentUnit.ordinal());
		data.add((byte) capacityUnit.ordinal());
		data.add((byte) energyUnit.ordinal());
		data.add((byte) powerUnit.ordinal());
		data.add((byte) resisterUnit.ordinal());
		data.add((byte) timeUnit.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > VoltageUnit.values().length - 1) {
			
			throw new RuntimeException("error voltage unit code:" + code);
		}
		voltageUnit = VoltageUnit.values()[code];
		//电流单位
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > CurrentUnit.values().length - 1) {
			
			throw new RuntimeException("error current unit code:" + code);
		}
		currentUnit = CurrentUnit.values()[code];
		//容量单位
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > CapacityUnit.values().length - 1) {
			
			throw new RuntimeException("error capacity unit code:" + code);
		}
		capacityUnit = CapacityUnit.values()[code];
		//能量单位
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > EnergyUnit.values().length - 1) {
			
			throw new RuntimeException("error energy unit code:" + code);
		}
		energyUnit = EnergyUnit.values()[code];
		//功率单位
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > PowerUnit.values().length - 1) {
			
			throw new RuntimeException("error power unit code:" + code);
		}
		powerUnit = PowerUnit.values()[code];
		//电阻单位
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > ResisterUnit.values().length - 1) {
			
			throw new RuntimeException("error resister unit code:" + code);
		}
		resisterUnit = ResisterUnit.values()[code];
		//时间单位
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(code > TimeUnit.values().length - 1) {
			
			throw new RuntimeException("error time unit code:" + code);
		}
		timeUnit = TimeUnit.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UnitManageCode;
	}

	public VoltageUnit getVoltageUnit() {
		return voltageUnit;
	}

	public void setVoltageUnit(VoltageUnit voltageUnit) {
		this.voltageUnit = voltageUnit;
	}

	public CurrentUnit getCurrentUnit() {
		return currentUnit;
	}

	public void setCurrentUnit(CurrentUnit currentUnit) {
		this.currentUnit = currentUnit;
	}

	public CapacityUnit getCapacityUnit() {
		return capacityUnit;
	}

	public void setCapacityUnit(CapacityUnit capacityUnit) {
		this.capacityUnit = capacityUnit;
	}

	public EnergyUnit getEnergyUnit() {
		return energyUnit;
	}

	public void setEnergyUnit(EnergyUnit energyUnit) {
		this.energyUnit = energyUnit;
	}

	public PowerUnit getPowerUnit() {
		return powerUnit;
	}

	public void setPowerUnit(PowerUnit powerUnit) {
		this.powerUnit = powerUnit;
	}

	public ResisterUnit getResisterUnit() {
		return resisterUnit;
	}

	public void setResisterUnit(ResisterUnit resisterUnit) {
		this.resisterUnit = resisterUnit;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	@Override
	public String toString() {
		return "UnitManageData [voltageUnit=" + voltageUnit + ", currentUnit=" + currentUnit + ", capacityUnit="
				+ capacityUnit + ", energyUnit=" + energyUnit + ", powerUnit=" + powerUnit + ", resisterUnit="
				+ resisterUnit + ", timeUnit=" + timeUnit + "]";
	}
	
	

}
