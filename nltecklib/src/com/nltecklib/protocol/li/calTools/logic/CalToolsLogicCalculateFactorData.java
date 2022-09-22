package com.nltecklib.protocol.li.calTools.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.CalDot;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.Pole;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class CalToolsLogicCalculateFactorData extends Data implements Configable,Queryable, Responsable{

	private Map<Pole, List<CalDot>> calDotMap = new HashMap<Pole, List<CalDot>>();
	
	public CalToolsLogicCalculateFactorData() {
		calDotMap.put(Pole.POSITIVE,new ArrayList<CalDot>());
		calDotMap.put(Pole.NEGATIVE,new ArrayList<CalDot>());
	}
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) chnIndex);
 
		List<CalDot> positiveDots = calDotMap.get(Pole.POSITIVE);
		data.add((byte) positiveDots.size());

		for(int i = 0;i < positiveDots.size();i++) {
			CalDot  dot = positiveDots.get(i);
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.adc * Math.pow(10,currentResolution)), 3, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, programKResolution)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, programBResolution)), 4, true)));
		}

		
		
		List<CalDot> negativeDots = calDotMap.get(Pole.NEGATIVE);
		data.add((byte) negativeDots.size());
		
		
		for(int i = 0;i < negativeDots.size();i++) {
			CalDot  dot = negativeDots.get(i);
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.adc * Math.pow(10, currentResolution)), 3, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.adcK * Math.pow(10, programKResolution)), 4, true)));
			data.addAll(Arrays.asList(
					ProtocolUtil.splitSpecialMinus((long) (dot.adcB * Math.pow(10, programBResolution)), 4, true)));
		}


		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		/** 正级 */
		
		int positiveCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(positiveCount > 0) {
			
			List<CalDot> pCalDots = new ArrayList<CalDot>(positiveCount);
			
			for(int i = 0;i < positiveCount;i++) {
				
				CalDot dot = new CalDot();

				dot.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / Math.pow(10, currentResolution);
				index += 3;
				dot.adcK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, programKResolution);
				index += 4;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, programBResolution);
				index += 4;
				pCalDots.add(dot);
			}
			
			calDotMap.put(Pole.POSITIVE, pCalDots);
		}
		/** 负级 */
		
		int negativeCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		if(negativeCount > 0) {
			
			List<CalDot> nCalDots = new ArrayList<CalDot>(negativeCount);
			
			for(int i = 0;i < negativeCount;i++) {
				
				CalDot dot = new CalDot();

				dot.adc = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]),
						true) / Math.pow(10, currentResolution);
				index += 3;
				dot.adcK = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, programKResolution);
				index += 4;
				dot.adcB = (double) ProtocolUtil
						.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true)
						/ Math.pow(10, programBResolution);
				index += 4;
				nCalDots.add(dot);
			}
			
			calDotMap.put(Pole.NEGATIVE, nCalDots);
		}
		
		
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CalToolsLogicCode.CalFactorCode;
	}
	
	public Map<Pole, List<CalDot>> getCalDotMap() {
		return calDotMap;
	}

	public void setCalDotMap(Map<Pole, List<CalDot>> calDotMap) {
		this.calDotMap = calDotMap;
	}

	
	@Override
	public String toString() {
		return "CalToolsCheckCalculateFactorData [calDotMap.size=" + calDotMap.size() + "]";
	}


}
