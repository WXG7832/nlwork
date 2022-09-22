package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.ErrCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 查询通道校准数据
 * @author Administrator
 *
 */
public class CalQueryChnADCData extends Data implements Queryable,Responsable{

	private ErrCode errCode = ErrCode.NORMAL;
	
	private List<Double> primitiveADCs=new ArrayList<Double>();
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}


	public ErrCode getErrCode() {
		return errCode;
	}

	public void setErrCode(ErrCode errCode) {
		this.errCode = errCode;
	}

	public List<Double> getPrimitiveADCs() {
		return primitiveADCs;
	}


	public void setPrimitiveADCs(List<Double> primitiveADCs) {
		this.primitiveADCs = primitiveADCs;
	}


	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		//errCode
		data.add((byte) errCode.ordinal());
		
		data.add((byte)primitiveADCs.size());
		
		for (double adc : primitiveADCs) {
			// ADC原始值
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 1000), 3, true)));
			
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		//errCode
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= ErrCode.values().length) {

			throw new RuntimeException("the errCode value is error:" + flag);
		}
		errCode = ErrCode.values()[flag];
		
		int length= ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int i=0;i<length;i++) {

			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 1000;
			index += 3;
			primitiveADCs.add(adc);
		}
		// primitiveADC
	}

	@Override
	public Code getCode() {
		return MainCode.CalQueryChnADCCode;
	}


	@Override
	public String toString() {
		return "CalQueryChnADCData [errCode=" + errCode + ", primitiveADCs=" + primitiveADCs + "]";
	}

	

}
