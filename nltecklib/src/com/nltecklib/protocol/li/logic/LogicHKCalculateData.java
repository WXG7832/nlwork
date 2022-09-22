package com.nltecklib.protocol.li.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.AdcChunk;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年4月21日 上午11:09:13 类说明
 */
public class LogicHKCalculateData extends Data implements Configable, Queryable, Responsable {
   
	private static final int  KB_FACTOR = 7;
	private static final int  SUB_KB_FACTOR = 2;
	
	private CalWorkMode workMode = CalWorkMode.SLEEP;
	private long     programValRange; // 程控值
	
	private long     programValRead; //一级程控值
	private long     programValRead2; //二级程控值
	private long     programValRead3; //三级程控值

	private double calculateDot; // 计量点
	private double programK1; // 1级程控K值
	private double programB1; // 1级程控K值
	private double programK2; // 2级程控K值
	private double programB2; // 2级程控K值
	private double programK3; // 3级程控K值
	private double programB3; // 3级程控K值
	
	private double adcK; // ADC K值
	private double adcB; // ADC b值
	
	
	
	private List<AdcChunk> chunks = new ArrayList<AdcChunk>();
	

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
		data.add((byte) (isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(chnIndex,Data.getDriverChnCount()) : chnIndex));
		
		//工位
		data.add((byte) workMode.ordinal());
		//程控值最大量程
		data.addAll(Arrays.asList(ProtocolUtil.split(programValRange, 2, true)));
		//计量点
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(calculateDot * 100), 3, true)));
		//程控KB值
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programK1 * Math.pow(10, KB_FACTOR)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programB1 * Math.pow(10, KB_FACTOR)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programK2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programB2 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programK3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(programB3 * Math.pow(10, SUB_KB_FACTOR)), 2, true)));
		
		// 一级程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programValRead, 2, true)));
		// 二级程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programValRead2, 2, true)));
		// 三级程控值
		data.addAll(Arrays.asList(ProtocolUtil.split(programValRead3, 2, true)));
		// adc
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(adcK * Math.pow(10, KB_FACTOR)), 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(adcB * Math.pow(10, KB_FACTOR)), 4, true)));
		
		//adc采样
		data.add((byte) chunks.size());
		
		for(int n = 0 ; n < chunks.size() ; n++) {
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chunks.get(n).primitiveAdc * 100), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chunks.get(n).finalAdc * 100), 3, true)));
		}
		
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0 ;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex =  isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(val,Data.getDriverChnCount()) : val;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= WorkMode.values().length) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = CalWorkMode.values()[flag];
		programValRange = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		calculateDot = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
		index += 3;
		//解码程控KB值
		programK1 = (double)ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, KB_FACTOR);
		index += 4;
		programB1 = (double)ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, KB_FACTOR);
		index += 4;
		
		programK2 = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / Math.pow(10, SUB_KB_FACTOR);
		index += 2;
		programB2 = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / Math.pow(10, SUB_KB_FACTOR);
		index += 2;
		programK3 = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / Math.pow(10, SUB_KB_FACTOR);
		index += 2;
		programB3 = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / Math.pow(10, SUB_KB_FACTOR);
		index += 2;
		
		//一二三级程控值
		programValRead = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programValRead2 = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programValRead3 = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		//adc kb
		adcK = (double)ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, KB_FACTOR);
		index += 4;
		adcB = (double)ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, KB_FACTOR);
		index += 4;
		
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int n = 0 ; n < count ; n++) {
			
			double primitiveADC = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			double finalADC = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			
			AdcChunk chunk = new AdcChunk();
			chunk.primitiveAdc = primitiveADC;
			chunk.finalAdc     = finalADC;
			
			chunks.add(chunk);
			
		}

	}

	@Override
	public Code getCode() {

		return LogicCode.HKCalculateCode;
	}

	public CalWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalWorkMode workMode) {
		this.workMode = workMode;
	}

	public long getProgramValRange() {
		return programValRange;
	}

	public void setProgramValRange(long programValRange) {
		this.programValRange = programValRange;
	}

	
	public long getProgramValRead() {
		return programValRead;
	}

	public void setProgramValRead(long programValRead) {
		this.programValRead = programValRead;
	}

	public long getProgramValRead2() {
		return programValRead2;
	}

	public void setProgramValRead2(long programValRead2) {
		this.programValRead2 = programValRead2;
	}

	public long getProgramValRead3() {
		return programValRead3;
	}

	public void setProgramValRead3(long programValRead3) {
		this.programValRead3 = programValRead3;
	}
	
	public double getCalculateDot() {
		return calculateDot;
	}

	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}

	public double getProgramK1() {
		return programK1;
	}

	public void setProgramK1(double programK1) {
		this.programK1 = programK1;
	}

	public double getProgramB1() {
		return programB1;
	}

	public void setProgramB1(double programB1) {
		this.programB1 = programB1;
	}

	public double getProgramK2() {
		return programK2;
	}

	public void setProgramK2(double programK2) {
		this.programK2 = programK2;
	}

	public double getProgramB2() {
		return programB2;
	}

	public void setProgramB2(double programB2) {
		this.programB2 = programB2;
	}

	public double getProgramK3() {
		return programK3;
	}

	public void setProgramK3(double programK3) {
		this.programK3 = programK3;
	}

	public double getProgramB3() {
		return programB3;
	}

	public void setProgramB3(double programB3) {
		this.programB3 = programB3;
	}

	public double getAdcK() {
		return adcK;
	}

	public void setAdcK(double adcK) {
		this.adcK = adcK;
	}

	public double getAdcB() {
		return adcB;
	}

	public void setAdcB(double adcB) {
		this.adcB = adcB;
	}

	public List<AdcChunk> getChunks() {
		return chunks;
	}

	public void setChunks(List<AdcChunk> chunks) {
		this.chunks = chunks;
	}
	

}
