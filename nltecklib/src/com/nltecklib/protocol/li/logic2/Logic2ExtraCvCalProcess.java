package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月26日 下午5:09:24
* 类说明
*/
public class Logic2ExtraCvCalProcess extends Data implements Configable, Queryable, Responsable {
    
	
	private Pole pole = Pole.NORMAL;
	private long programV;
	private long programI;
	private List<Double> adcs = new ArrayList<>(); //采集的ADC集合
	
	
	
	
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
		// 通道序号
		data.add((byte) (isReverseDriverChnIndex()
				? ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: chnIndex));
		data.add((byte) pole.ordinal());
		// 程控电压
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programV), 2, true)));
		// 程控电流
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (programI), 3, true)));
		data.add((byte) adcs.size());
		
		for(Double adc : adcs) {
			
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (adc * 100), 3, true)));
			
		}
		
        
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		int val = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = isReverseDriverChnIndex() ? ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount())
				: val;
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		
		// 程控电压
		programV = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		programI = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
		index += 3;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			double adc = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 100;
			index += 3;
			adcs.add(adc);
		}
		
		
	}
	
	
    
	

	@Override
	public String toString() {
		return "Logic2ExtraCvCalProcess [pole=" + pole + ", programV=" + programV + ", programI=" + programI + ", adcs="
				+ adcs + "]";
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return Logic2Code.ExtraCvCalProcessCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public long getProgramV() {
		return programV;
	}

	public void setProgramV(long programV) {
		this.programV = programV;
	}

	public long getProgramI() {
		return programI;
	}

	public void setProgramI(long programI) {
		this.programI = programI;
	}

	public List<Double> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<Double> adcs) {
		this.adcs = adcs;
	}
	
	

}
