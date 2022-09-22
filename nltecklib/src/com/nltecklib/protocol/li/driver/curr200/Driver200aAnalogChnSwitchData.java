package com.nltecklib.protocol.li.driver.curr200;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* Copyright: Copyright (c) 2021 NiuLian
* 
* @Description: 多膜片模拟量通道切换 0x29 支持配置
* @version: v1.0.0
* @author: admin
* @date: 2021年11月4日 10:01:26 
*
 */
public class Driver200aAnalogChnSwitchData  extends Data implements Configable, Queryable, Responsable{

	//信号量通道号
	private int analogChnIndex;
	
	private List<ChnParam> chnParams;
	
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

	/**
	 * 返回通道参数
	 */
	public static class ChnParam{

		private int loop;//循环号
		private int step;//步次号
		private WorkMode workMode;//工作模式
		private long stepTime;//步次时间 ms
		private short highPrecision;//精度x个档
		
		
		public short getHighPrecision() {
			return highPrecision;
		}

		public void setHighPrecision(short highPrecision) {
			this.highPrecision = highPrecision;
		}

		public int getLoop() {
			return loop;
		}

		public void setLoop(int loop) {
			this.loop = loop;
		}

		public int getStep() {
			return step;
		}

		public void setStep(int step) {
			this.step = step;
		}

		
		public WorkMode getWorkMode() {
			return workMode;
		}

		public void setWorkMode(WorkMode workMode) {
			this.workMode = workMode;
		}
		public long getStepTime() {
			return stepTime;
		}

		public void setStepTime(long stepTime) {
			this.stepTime = stepTime;
		}
	}
	
	
	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) analogChnIndex);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		
//		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
//		
//		//信号量通道号
//		analogChnIndex = data.get(index++);
		
		int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++));
	    
	    if(chnCount == 0) {return;}
	    
	    for(int num = 0 ;num < chnCount ;num++) {

	    	ChnParam chnParam = new ChnParam();
	    	
	    	chnParam.setLoop(ProtocolUtil.getUnsignedByte(data.get(index++)));
	    	chnParam.setStep(ProtocolUtil.getUnsignedByte(data.get(index++)));
	    	
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			
			if(code > WorkMode.values().length - 1) {
				
				throw new RuntimeException("error work mode code : " + code);
			}
			chnParam.setWorkMode(WorkMode.values()[code]);
			
			chnParam.setHighPrecision((short)ProtocolUtil.getUnsignedByte(data.get(index++)));//2021-11-03添加精度
			
	    	chnParam.setStepTime(ProtocolUtil.compose(encodeData.subList(index, index+4).toArray(new Byte[0]), true));
	    	index += 4;
	    	
	    	chnParams.add(chnParam);
	    }
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.Driver200aAnalogChnSwitchCode;
	}

	public int getAnalogChnIndex() {
		return analogChnIndex;
	}

	public void setAnalogChnIndex(int analogChnIndex) {
		this.analogChnIndex = analogChnIndex;
	}
	
	public List<ChnParam> getChnParams() {
		return chnParams;
	}

	public void setChnParams(List<ChnParam> chnParams) {
		this.chnParams = chnParams;
	}


}
