package com.nltecklib.protocol.li.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check.CheckEnvironment.CalDot;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class CheckWriteCalFlashData extends Data implements Configable, Queryable, Responsable {

	public static final int MODE_CHECK_DOT_COUNT = 32; // 每个模式固定校准点数
	public static final int MODE_CHECK_DOT_BYTES = 32; // 每个校准点固定字节数

	private List<CalDot> cvpDots = new ArrayList<CalDot>(MODE_CHECK_DOT_COUNT);
	private List<CalDot> cvnDots = new ArrayList<CalDot>(MODE_CHECK_DOT_COUNT);
	private static final int ADC_BIT_COUNT = 2;
	private static final int K_BIT_COUNT = 7;
	private static final int B_BIT_COUNT = 6;

	
	private static final int LEFT_BYTE_COUNT = 21;
	
	
	public CheckWriteCalFlashData() {
		
	
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		
		if(isReverseDriverChnIndex()) {
			
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}
			
		data.add((byte) chnIndex);

		// 回检校准点
		for (int i = 0; i < MODE_CHECK_DOT_COUNT; i++) {

			CalDot dot = new CalDot();
			if (i < cvpDots.size()) {
				dot = cvpDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, ADC_BIT_COUNT)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, K_BIT_COUNT)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, B_BIT_COUNT)), 4, true)));

			for (int j = 0; j < LEFT_BYTE_COUNT; j++) {
				data.add((byte) 0);
			}
		}

		// 回检校准点
		for (int i = 0; i < MODE_CHECK_DOT_COUNT; i++) {

			CalDot dot = new CalDot();
			if (i < cvnDots.size()) {
				dot = cvnDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * Math.pow(10, ADC_BIT_COUNT)), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, K_BIT_COUNT)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, B_BIT_COUNT)), 4, true)));

			for (int j = 0; j < LEFT_BYTE_COUNT; j++) {
				data.add((byte) 0);
			}
		}

	}

	private boolean isDotEmpty(CalDot dot) {

		if (dot.adc == 0 &&  dot.adcK == 0 && dot.adcB == 0 ) {

			return true;
		}

		return false;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(isReverseDriverChnIndex()) {
			
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
					
		}
		
		for (int i = 0; i < MODE_CHECK_DOT_COUNT; i++) {
           
			CalDot dot = new CalDot();
			
			dot.adc   = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / Math.pow(10, ADC_BIT_COUNT);
			index += 3;
			dot.adcK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, K_BIT_COUNT);
			index += 4;
			dot.adcB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, B_BIT_COUNT);
			index += 4;
			
			cvpDots.add(dot);
			
			index += LEFT_BYTE_COUNT;
		}
		for (int i = 0; i < MODE_CHECK_DOT_COUNT; i++) {
	           
			CalDot dot = new CalDot();
			
			dot.adc   = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / Math.pow(10, ADC_BIT_COUNT);
			index += 3;
			dot.adcK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, K_BIT_COUNT);
			index += 4;
			dot.adcB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, B_BIT_COUNT);
			index += 4;

			cvnDots.add(dot);
			
			index += LEFT_BYTE_COUNT;
			
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.WriteCalFlashCode;
	}

	public List<CalDot> getCvpDots() {
		return cvpDots;
	}

	public void setCvpDots(List<CalDot> cvpDots) {
		this.cvpDots = cvpDots;
	}

	public List<CalDot> getCvnDots() {
		return cvnDots;
	}

	public void setCvnDots(List<CalDot> cvnDots) {
		this.cvnDots = cvnDots;
	}

}
