package com.nltecklib.protocol.lab.accessory;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * ≈∑ƒ∑»›≥¨Œ¬≈‰÷√
 * @author Administrator
 *
 */
public class OMRTempUpperData extends Data implements Configable,Queryable,Responsable {

	//Œ¬øÿ±Ìµÿ÷∑
	//private int omrIndex;// π”√∞Â∫≈
	//Œ¬∂»…œœﬁ
	private double tempUpper;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void encode() {
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)tempUpper, 2, true)));		
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
		tempUpper = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;		
	}
	
	@Override
	public Code getCode() {
		return AccessoryCode.ORMMeterTempUpperCode;
	}
	
	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getOmrIndex() {
		return mainIndex;
	}

	public void setOmrIndex(int omrIndex) {
		this.mainIndex = omrIndex;
	}

	public double getTempUpper() {
		return tempUpper;
	}

	public void setTempUpper(double tempUpper) {
		this.tempUpper = tempUpper;
	}

	@Override
	public String toString() {
		return "OMRTempUpperData [tempUpper=" + tempUpper + "]";
	}
	
	
}
