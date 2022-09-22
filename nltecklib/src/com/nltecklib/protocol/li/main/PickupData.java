package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.Context;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class PickupData extends Data implements Alertable,Responsable{

	private List<ChannelData> chnDatas = new ArrayList<ChannelData>();
	private int    chnCount; // 通道数
	private int    loopIndex;
	private Date   date;    //采集当前时间
	private double temp; // 当前温度.单位0.1℃
	private State state = State.NORMAL;

	
	public PickupData(){	
	   
	}
	
	@Override
	public String toString() {
		return "PickupData [chnDatas=" + chnDatas + 
				", chnCount=" + chnCount + ", loopIndex=" + loopIndex + ", date=" + date + ", temp="
				+ temp + ", state=" + state + "]";
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex); // 查询，回复都要分区序号
		data.add((byte)state.ordinal());
		//绝对时间
		Calendar cal = Calendar.getInstance();
		
		
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)chnDatas.size(), 2, true)));
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long)(temp * 10), 2, true)));

		if (chnDatas.size() != chnCount) {

			throw new RuntimeException("chnCount != chnDatas.size()");
		}

		for (int i = 0; i < chnDatas.size(); i++) {

			ChannelData chnData = chnDatas.get(i);
			if (chnData == null) {

				throw new RuntimeException("chn data must not be null");
			}
			data.add((byte) chnData.getChannelIndex()); //分区内通道序号
			data.add((byte)chnData.getState().ordinal());
			data.add((byte)(chnData.isImportantData() ? 1 : 0)); //是否重要转点数据
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getVoltage() * Math.pow(10,Data.getVoltageResolution())), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getCurrent() * Math.pow(10,Data.getCurrentResolution())), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getCapacity() * Math.pow(10,Data.getCapacityResolution())), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getEnergy() * Math.pow(10,Data.getEnergyResolution())), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getAccumulateCapacity() * Math.pow(10,Data.getCapacityResolution())), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getAccumulateEnergy() * Math.pow(10,Data.getEnergyResolution())), 4, true)));
			//备份电池电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getDeviceVoltage() * 10), 2, true)));
			//备份功率电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getPowerVoltage() * 10), 2, true)));
			//流程流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getTimeTotalSpend(), 4, true)));
			//步次流逝时间
			data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getTimeStepSpend(), 3, true)));
			//实时时间
			cal.setTime(chnData.getDate() == null ? new Date() : chnData.getDate());
			
			int year = cal.get(Calendar.YEAR)-2000;
			int month = cal.get(Calendar.MONTH)+1;
			int date = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			
			data.add((byte)year);
			data.add((byte)month);
			data.add((byte)date);
			data.add((byte)hour);
			data.add((byte)min);
			data.add((byte)sec);
			//步次循环号
			data.add((byte)chnData.getLoopIndex());
			//步次序号
			data.add((byte)chnData.getStepIndex());
			//工作模式
			data.add(chnData.getWorkMode() == null ? (byte)0xff : (byte)chnData.getWorkMode().ordinal());
			
			//通道温度
			if(Data.isUseChnTemperature()) {
				
				data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getTemp() * 10), 2, true)));
			}
			if(Data.isUseFrameTemperature()) {
				//料框温度个数
				data.add((byte)chnData.getFrameTemps().size());
				
				//4个料框温度 新浦蜂巢
				for(int n = 0; n < chnData.getFrameTemps().size(); n++) {

					data.addAll(Arrays.asList(ProtocolUtil.split(
							(long)(chnData.getFrameTemps().get(n) * 10), 2, true)));
				}
			}
			//报警代码
			data.add((byte)chnData.getAlertCode().ordinal());	
			//报警电压
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getAlertVoltage() * Math.pow(10,Data.getVoltageResolution())), 2, true)));
			//报警电流
			data.addAll(Arrays.asList(ProtocolUtil.split((long)(chnData.getAlertCurrent() * Math.pow(10,Data.getCurrentResolution())), 3, true)));

		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int stateIndex = ProtocolUtil.getUnsignedByte(data.get(index));
		if(stateIndex > State.values().length - 1) {
			
			throw new RuntimeException("invalid unit state:" + stateIndex);
		}	
		state = State.values()[data.get(index++)];
		
		
		//解析时间
		Calendar cal = Calendar.getInstance();
		
		
		
		chnCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		temp = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;

		chnDatas.clear();
		// 解析通道数据
		for (int i = 0; i < chnCount; i++) {

			ChannelData chnData = new ChannelData();
			//分区通道序号
			chnData.setChannelIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
			 if (data.get(index) > ChnState.values().length - 1){
				
				throw new RuntimeException("error channel state code :" + data.get(index));
			}
			chnData.setState(ChnState.values()[data.get(index++)]);
			//转点标记
			int importantData = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(importantData > 1) {
				
				throw new RuntimeException("important code is error: " + importantData);
			}
			chnData.setImportantData(importantData == 1); //设置标记位
			
			//通道电压
			long val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setVoltage((double) val / Math.pow(10, Data.getVoltageResolution()));
			//通道电流
			val = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			chnData.setCurrent((double) val / Math.pow(10, Data.getCurrentResolution()));
			//通道容量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
	        chnData.setCapacity((double) val / Math.pow(10, Data.getCapacityResolution()));
	        //通道能量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
	        chnData.setEnergy((double) val / Math.pow(10, Data.getEnergyResolution()));
	        //通道累计容量
			val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
	        chnData.setAccumulateCapacity((double) val / Math.pow(10, Data.getCapacityResolution()));
	        //通道累计能量
	        val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
	        chnData.setAccumulateEnergy((double) val / Math.pow(10, Data.getEnergyResolution()));
	        
	        //备份电池电压
	        val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setDeviceVoltage((double) val / Math.pow(10, Data.getVoltageResolution()));
			//备份功率电压
			 val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			 index += 2;
			 chnData.setPowerVoltage((double) val / Math.pow(10,Data.getVoltageResolution()));
			//流程流逝时间
			 val = ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
			index += 4;
			chnData.setTimeTotalSpend(val);
	        //步次流逝时间
	        val = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			chnData.setTimeStepSpend((int)val);
			//实时时间
			int year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
			int month = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
			int day  = ProtocolUtil.getUnsignedByte(data.get(index++));
			int hour = ProtocolUtil.getUnsignedByte(data.get(index++));
			int min = ProtocolUtil.getUnsignedByte(data.get(index++));
			int sec = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			if(month > 11 ) {
				
				throw new RuntimeException("invalid month format:" + month);
			}
	        if(day > 31 ) {
				
				throw new RuntimeException("invalid day format:" + day);
			}
	        if(hour > 24 ) {
				
				throw new RuntimeException("invalid hour format:" + hour);
			}
	        if(min > 59) {
	        	
	        	throw new RuntimeException("invalid minute format:" + min);
	        }
	        if(sec > 59) {
	        	
	        	throw new RuntimeException("invalid second format:" + sec);
	        }
			
			
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH , month);
			cal.set(Calendar.DATE , day);
			cal.set(Calendar.HOUR_OF_DAY , hour);
			cal.set(Calendar.MINUTE , min);
			cal.set(Calendar.SECOND , sec);
			cal.set(Calendar.MILLISECOND, 0);
			
			chnData.setDate(cal.getTime());
			//步次循环号
			int loop = ProtocolUtil.getUnsignedByte(data.get(index++));
			chnData.setLoopIndex(loop);
			//步次序号
			val = ProtocolUtil.getUnsignedByte(data.get(index++));
			chnData.setStepIndex((int) val); //步次序号
			//工作模式
			int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(mode < WorkMode.values().length){
			    chnData.setWorkMode(WorkMode.values()[mode]);
			}else if(chnData.getState() == ChnState.RUN) {
				
				throw new RuntimeException("running mode is exception:" + mode);
				
			}
			//通道温度
			if(Data.isUseChnTemperature()) {
				
				 double chntemp = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
				 index += 2;
				 chnData.setTemp(chntemp);

			}
			//4个料框温度  新浦蜂巢
			if(Data.isUseFrameTemperature()) {
				
				int frameTempCount = ProtocolUtil.getUnsignedByte(data.get(index++));
				
				List<Double> frameTemps = new ArrayList<Double>();
 				for(int n = 0; n < frameTempCount; n++) {
 					 double chntemp = (double)ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
 					 index += 2;
 					 frameTemps.add(chntemp);
				}
				chnData.setFrameTemps(frameTemps);
			}
			
			
			//报警码
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if(code < AlertCode.values().length) {
				
				chnData.setAlertCode(AlertCode.values()[code]);
			}else {
				
				throw new RuntimeException("error alert code :" + code);
			}
			//报警电压
			val = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			chnData.setAlertVoltage((double)val / Math.pow(10,Data.getVoltageResolution()));
			//报警电流
			val = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
			index += 3;
			chnData.setAlertCurrent((double)val / Math.pow(10,Data.getCurrentResolution()));
	        
	        
			//公用参数
			
			if(!Data.isUseChnTemperature()) {
			   chnData.setTemp(temp);
			} 
			
			
			chnDatas.add(chnData);

		}

	}

	@Override
	public Code getCode() {
		return MainCode.PickupCode;
	}

	public List<ChannelData> getChnDatas() {
		return chnDatas;
	}

	public void setChnDatas(List<ChannelData> chnDatas) {
		this.chnDatas = chnDatas;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int chnIndex) {
		this.unitIndex = chnIndex;
	}

	
	public int getChnCount() {
		return chnCount;
	}

	public void setChnCount(int chnCount) {
		this.chnCount = chnCount;
	}

	public int getLoopIndex() {
		return loopIndex;
	}

	public void setLoopIndex(int loopIndex) {
		this.loopIndex = loopIndex;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		return false;
	}
	
	

}
