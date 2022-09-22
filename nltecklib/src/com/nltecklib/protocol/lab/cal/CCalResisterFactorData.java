package com.nltecklib.protocol.lab.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;



public class CCalResisterFactorData extends Data implements Configable ,Queryable, Responsable {

	public final static int EXP = 7; // 小数位精度
	//private int precisionLevel;   精度通过通道号传输
	private double resisterFactor;
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;//这里代表精度等级
	}

	@Override
	public void encode() {

		//data.add((byte)precisionLevel);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resisterFactor * Math.pow(10, EXP)), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		//precisionLevel=ProtocolUtil.getUnsignedByte(data.get(index++));
		resisterFactor = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, EXP);
		index += 4;
	}

	@Override
	public Code getCode() {

		return CalCode.RESISTER;
	}


	public int getPrecisionLevel() {
		return chnIndex;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.chnIndex = precisionLevel;
	}

	public void setResisterFactor(double resisterFactor) {
		this.resisterFactor = resisterFactor;
	}

	public double getResisterFactor() {
		return resisterFactor;
	}

	@Override
	public String toString() {
		return "CalResisterFactorData [resisterFactor=" + resisterFactor + "]";
	}

	

}
