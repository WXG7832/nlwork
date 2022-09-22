package com.nltecklib.protocol.li.test.driver;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.DriverTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月24日 上午11:47:45
* 类说明
*/
public class DTPowerConfigData extends Data implements Configable, Queryable, Responsable {
   
	private double  posCurUpper;
	private double  posCurLower;
	private double  negCurUpper;
	private double  negCurLower;
	private double  inverterCurUpper;
	private double  inverterCurLower;
	
	
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
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(posCurUpper * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(posCurLower * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(negCurUpper * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(negCurLower * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(inverterCurUpper * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(inverterCurLower * 10), 3, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		posCurUpper =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;
	    posCurLower =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;
	    negCurUpper =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;
	    negCurLower =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;
	    inverterCurUpper =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;
	    inverterCurLower =  (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10 ;
	    index += 3;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverTestCode.PowerConfig;
	}

	public double getPosCurUpper() {
		return posCurUpper;
	}

	public void setPosCurUpper(double posCurUpper) {
		this.posCurUpper = posCurUpper;
	}

	public double getPosCurLower() {
		return posCurLower;
	}

	public void setPosCurLower(double posCurLower) {
		this.posCurLower = posCurLower;
	}

	public double getNegCurUpper() {
		return negCurUpper;
	}

	public void setNegCurUpper(double negCurUpper) {
		this.negCurUpper = negCurUpper;
	}

	public double getNegCurLower() {
		return negCurLower;
	}

	public void setNegCurLower(double negCurLower) {
		this.negCurLower = negCurLower;
	}

	public double getInverterCurUpper() {
		return inverterCurUpper;
	}

	public void setInverterCurUpper(double inverterCurUpper) {
		this.inverterCurUpper = inverterCurUpper;
	}

	public double getInverterCurLower() {
		return inverterCurLower;
	}

	public void setInverterCurLower(double inverterCurLower) {
		this.inverterCurLower = inverterCurLower;
	}
	
	

}
