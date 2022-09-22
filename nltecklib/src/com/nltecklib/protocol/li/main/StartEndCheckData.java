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

public class StartEndCheckData extends Data implements Configable, Responsable, Queryable , Cloneable {
    
	private double startVoltageLower;
	private double startVoltageUpper;
	private double endVoltageLower;
	private double endVoltageUpper;
	private double endCapacityLower;
	private double endCapacityUpper;
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (startVoltageLower * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (startVoltageUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (endVoltageLower * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (endVoltageUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (endCapacityLower * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (endCapacityUpper * 10), 3, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		startVoltageLower = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		startVoltageUpper = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		endVoltageLower = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		endVoltageUpper = ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		endCapacityLower = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
		index += 3;
		endCapacityUpper = ProtocolUtil.compose(encodeData.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
		index += 3;

	}

	@Override
	public Code getCode() {
		
		return MainCode.StartEndCheckCode;
	}

	public double getStartVoltageLower() {
		return startVoltageLower;
	}

	public void setStartVoltageLower(double startVoltageLower) {
		this.startVoltageLower = startVoltageLower;
	}

	public double getStartVoltageUpper() {
		return startVoltageUpper;
	}

	public void setStartVoltageUpper(double startVoltageUpper) {
		this.startVoltageUpper = startVoltageUpper;
	}

	public double getEndVoltageLower() {
		return endVoltageLower;
	}

	public void setEndVoltageLower(double endVoltageLower) {
		this.endVoltageLower = endVoltageLower;
	}

	public double getEndVoltageUpper() {
		return endVoltageUpper;
	}

	public void setEndVoltageUpper(double endVoltageUpper) {
		this.endVoltageUpper = endVoltageUpper;
	}

	public double getEndCapacityLower() {
		return endCapacityLower;
	}

	public void setEndCapacityLower(double endCapacityLower) {
		this.endCapacityLower = endCapacityLower;
	}

	public double getEndCapacityUpper() {
		return endCapacityUpper;
	}

	public void setEndCapacityUpper(double endCapacityUpper) {
		this.endCapacityUpper = endCapacityUpper;
	}

	@Override
	public String toString() {
		return "StartEndCheckData [startVoltageLower=" + startVoltageLower + ", startVoltageUpper=" + startVoltageUpper
				+ ", endVoltageLower=" + endVoltageLower + ", endVoltageUpper=" + endVoltageUpper
				+ ", endCapacityLower=" + endCapacityLower + ", endCapacityUpper=" + endCapacityUpper + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			
			return false;
		} else if (obj instanceof StartEndCheckData){
			
			StartEndCheckData secd = (StartEndCheckData) obj;
			if (secd.getUnitIndex() == this.unitIndex 
					&& secd.getStartVoltageLower() == this.startVoltageLower
					&& secd.getStartVoltageUpper() == this.startVoltageUpper
					&& secd.getEndCapacityLower() == this.endCapacityLower
					&& secd.getEndCapacityUpper() == this.endCapacityUpper
					&& secd.getEndVoltageLower() == this.endVoltageLower
					&& secd.getEndVoltageUpper() == this.endVoltageUpper) {
				
				return true;
			}
		}
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
