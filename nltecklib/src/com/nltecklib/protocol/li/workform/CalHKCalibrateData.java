package com.nltecklib.protocol.li.workform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CalWorkMode;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.CalBoardState;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;



/**
* @author  wavy_zheng
* @version 创建时间：2020年4月23日 上午10:42:57
* 类说明
*/
public class CalHKCalibrateData extends Data implements Configable, Queryable, Responsable {
    
	private int      range = 0; //精度档位
	private CalWorkMode workMode = CalWorkMode.SLEEP;
	private CalBoardState  calState = CalBoardState.UNREADY;
	private long     programV1;  //1级程控电压
	private long     programI1;  //1级程控电流
	private long     programV2;  //2级程控电压
	private long     programI2;  //2级程控电流
	private long     programV3;  //3级程控电压
	private long     programI3;  //3级程控电流
	private List<Double>  adcList = new ArrayList<Double>(); //adc采集集合
	
	
	
	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public boolean supportDriver() {
		
		return false;
	}

	@Override
	public boolean supportChannel() {
		
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) chnIndex);
		//工位
		data.add((byte) workMode.ordinal());
		//档位
		data.add((byte) range);
		//校准板状态
		data.add((byte) calState.ordinal());
		//1级程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split(programV1, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split(programI1, 2, true)));
		//2级DA
		data.add((byte) programV2);
		data.add((byte) programI2);
		//3级DA
		data.add((byte) programV3);
		data.add((byte) programI3);
		
		
		data.add((byte) adcList.size());
		for(int n = 0 ; n < adcList.size() ; n++) {
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(adcList.get(n) * 100)
					, 3, true)));
			
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		
		data = encodeData;
		int index = 0 ;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = val;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= WorkMode.values().length) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = CalWorkMode.values()[flag];
		range    = ProtocolUtil.getUnsignedByte(data.get(index++));
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		//校准板状态
		if (flag >= CalBoardState.values().length) {

			throw new RuntimeException("the cal board state value is error:" + flag);
		}
		calState = CalBoardState.values()[flag];
		
		// 1级DA
		programV1 = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI1 = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	    // 2级DA
		programV2 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		programI2 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		// 3级DA
		programV3 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		programI3 =  ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			double adc = (double)ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			adcList.add(adc);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return WorkformCode.HKCalibrateCode;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public CalWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalWorkMode workMode) {
		this.workMode = workMode;
	}

	public long getProgramV1() {
		return programV1;
	}

	public void setProgramV1(long programV1) {
		this.programV1 = programV1;
	}

	public long getProgramI1() {
		return programI1;
	}

	public void setProgramI1(long programI1) {
		this.programI1 = programI1;
	}

	public long getProgramV2() {
		return programV2;
	}

	public void setProgramV2(long programV2) {
		this.programV2 = programV2;
	}

	public long getProgramI2() {
		return programI2;
	}

	public void setProgramI2(long programI2) {
		this.programI2 = programI2;
	}

	public long getProgramV3() {
		return programV3;
	}

	public void setProgramV3(long programV3) {
		this.programV3 = programV3;
	}

	public long getProgramI3() {
		return programI3;
	}

	public void setProgramI3(long programI3) {
		this.programI3 = programI3;
	}

	public List<Double> getAdcList() {
		return adcList;
	}

	public void setAdcList(List<Double> adcList) {
		this.adcList = adcList;
	}

	public CalBoardState getCalState() {
		return calState;
	}

	public void setCalState(CalBoardState calState) {
		this.calState = calState;
	}
	
	
	

}
