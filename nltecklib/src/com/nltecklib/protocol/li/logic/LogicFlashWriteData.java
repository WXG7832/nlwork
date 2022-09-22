package com.nltecklib.protocol.li.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.CalDot;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;


public class LogicFlashWriteData extends Data implements Configable,Queryable,Responsable {

	private int chnIndex;
	

	public final static int MODE_DOT_COUNT = 30;
	public final static int MODE_DOT_LEFT_BYTES = 8;

	private List<CalDot> ccDots = new ArrayList<CalDot>();
	private List<CalDot> cvpDots = new ArrayList<CalDot>();
	private List<CalDot> cvnDots = new ArrayList<CalDot>();
	private List<CalDot> dcDots = new ArrayList<CalDot>();

	public LogicFlashWriteData() {
		
		
	}
	
	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	public void appendCCDot(CalDot dot) {

		ccDots.add(dot);
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add(isReverseDriverChnIndex() ? (byte)ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount()) :  (byte)chnIndex);
		// cc30个校准点
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			CalDot dot = new CalDot();
			if (i < ccDots.size()) {
				dot = ccDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long)dot.da , 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, 7)), 4, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			CalDot dot = new CalDot();
			if (i < cvpDots.size()) {
				dot = cvpDots.get(i);
			}
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) dot.da , 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, 7)), 4, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			CalDot dot = new CalDot();
			if (i < cvnDots.size()) {
				dot = cvnDots.get(i);
			}
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long)  dot.da , 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, 7)), 4, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES; j++) {
				data.add((byte) 0);
			}
		}
		for (int i = 0; i < MODE_DOT_COUNT; i++) {

			CalDot dot = new CalDot();
			if (i < dcDots.size()) {
				dot = dcDots.get(i);
			}
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot.adc * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long)  dot.da , 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.meter * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programK * Math.pow(10, 7)), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (dot.programB * Math.pow(10, 7)), 4, true)));
			for (int j = 0; j < MODE_DOT_LEFT_BYTES ; j++) {
				data.add((byte) 0);
			}
		}

	}
	
	private boolean isDotEmpty(CalDot dot) {
		
		if(dot.adc == 0 && dot.meter == 0 && dot.adcK == 0 && dot.adcB == 0 &&
				dot.programB == 0 && dot.programK == 0) {
			
			return true;
		}
		
		return false;
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//通道号
		
		chnIndex =  ProtocolUtil.getUnsignedByte(data.get(index++));
		if(isReverseDriverChnIndex()) {
			
			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}
		
		
		// cc30个校准点
		for (int i = 0; i < MODE_DOT_COUNT; i++) {
           
			CalDot dot = new CalDot();
			dot.adc   = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.adcB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.da  = ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+3).toArray(new Byte[0]), true) /100;
			index += 3;
			
			dot.programK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.programB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
		
			ccDots.add(dot);
			
			index += MODE_DOT_LEFT_BYTES;
		}
		for (int i = 0; i < MODE_DOT_COUNT; i++) {
	           
			CalDot dot = new CalDot();
			
			dot.adc   = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.adcB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.da  = ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+3).toArray(new Byte[0]), true) /100;
			index += 3;
			dot.programK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.programB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			
				
			cvpDots.add(dot);
			
			index += MODE_DOT_LEFT_BYTES;
		}
		for (int i = 0; i < MODE_DOT_COUNT; i++) {
	           
			CalDot dot = new CalDot();
			dot.adc   = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.adcB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.da  = ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+3).toArray(new Byte[0]), true) /100;
			index += 3;
			dot.programK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.programB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			
			cvnDots.add(dot);
			
			index += MODE_DOT_LEFT_BYTES;
		}
		for (int i = 0; i < MODE_DOT_COUNT; i++) {
	           
			CalDot dot = new CalDot();
			dot.adc   = (double)ProtocolUtil.compose(data.subList(index, index+3).toArray(new Byte[0]), true) / 100;
			index += 3;
			dot.adcK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.adcB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.da  = ProtocolUtil.composeSpecialMinus(data.subList(index, index+2).toArray(new Byte[0]), true);
			index += 2;
			dot.meter  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+3).toArray(new Byte[0]), true) /100;
			index += 3;
			dot.programK  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			dot.programB  = (double)ProtocolUtil.composeSpecialMinus(data.subList(index, index+4).toArray(new Byte[0]), true) / Math.pow(10, 7);
			index += 4;
			
			dcDots.add(dot);
			
			index += MODE_DOT_LEFT_BYTES;
		}
	}

	@Override
	public Code getCode() {

		return LogicCode.WriteCalFlashCode;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	public List<CalDot> getCcDots() {
		return ccDots;
	}

	public void setCcDots(List<CalDot> ccDots) {
		this.ccDots = ccDots;
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

	public List<CalDot> getDcDots() {
		return dcDots;
	}

	public void setDcDots(List<CalDot> dcDots) {
		this.dcDots = dcDots;
	}

	@Override
	public boolean supportChannel() {
		
		return true;
	}
	
	

}
