package com.nltecklib.protocol.li.workform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年8月21日 下午2:06:48
* 类说明
*/
public class ExResisterFactorData extends Data implements Queryable, Responsable {
    
//	private final static int EXP = 8; // 小数位精度
	private double  resisterFactor; //电阻系数
	
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resisterFactor * Math.pow(10, getResistanceResolution())), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		resisterFactor = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, getResistanceResolution());
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return WorkformCode.ExResisterFactorCode;
	}

	public int getLevel() {
		return chnIndex;
	}

	public void setLevel(int level) {
		this.chnIndex = level;
	}

	public double getResisterFactor() {
		return resisterFactor;
	}

	public void setResisterFactor(double resisterFactor) {
		this.resisterFactor = resisterFactor;
	}
	
	

}
