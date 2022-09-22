package com.nltecklib.protocol.lab.mbcal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月29日 下午9:22:35
* 类说明
*/
public class MbCalResisterFactorData extends Data implements Configable, Queryable, Responsable {
    
	public final static int EXP = 7; // 小数位精度
	//private int precisionLevel;   精度通过通道号传输
	private double resisterFactor;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true; //代表精度等级
	}

	@Override
	public void encode() {
		
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
	
	public int getPrecisionLevel() {
		return chnIndex;
	}

	public void setPrecisionLevel(int precisionLevel) {
		this.chnIndex = precisionLevel;
	}
	
	public double getResisterFactor() {
		return resisterFactor;
	}

	public void setResisterFactor(double resisterFactor) {
		this.resisterFactor = resisterFactor;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MbCalCode.RESISTER;
	}

	@Override
	public String toString() {
		return "MbCalResisterFactorData [resisterFactor=" + resisterFactor + "]";
	}
	
	

}
