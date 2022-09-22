package com.nltecklib.protocol.li.cal;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
* @author  wavy_zheng
* @version 创建时间：2020年7月29日 下午4:16:13
* 增强型读取多档量程校准板电阻系数
*/
public class ExCalResisterFactorData extends Data implements Queryable, Responsable {
    
//	private final static int EXP = 8; // 小数位精度
	private double resisterFactor;  //电阻系数
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;  //板号
	}
    /**
     * 用于标识精度量程档位
     */
	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setResisterFactor(double resisterFactor) {
		this.resisterFactor = resisterFactor;
	}

	@Override
	public void encode() {
		data.add((byte) driverIndex);
		data.add((byte)chnIndex); //此处代表档位
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (resisterFactor * Math.pow(10, getResistanceResolution())), 4, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++)); //档位
		resisterFactor = (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
				/ Math.pow(10, getResistanceResolution());
		index += 4;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalCode.EXRESISTER;
	}

	public double getResisterFactor() {
		return resisterFactor;
	}
	
	public int  getRangeLevel() {
		
		return chnIndex;
	}
	
	public void setRangeLevel(int level) {
		
		this.chnIndex = level;
	}
	
	

//	public void setResisterFactor(double resisterFactor) {
//		this.resisterFactor = resisterFactor;
//	}

	@Override
	public String toString() {
		return "ExCalResisterFactorData [resisterFactor=" + resisterFactor + ", rangeLevel=" + chnIndex + "]";
	}
	
	

}
