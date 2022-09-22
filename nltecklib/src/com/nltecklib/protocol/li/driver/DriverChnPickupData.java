package com.nltecklib.protocol.li.driver;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 通道采集
 * @author admin
 */
public class DriverChnPickupData extends Data  implements Configable, Responsable{
    
	private boolean OPEN;//通道开关


	private PickupItem pickupItem = PickupItem.VOLTAGE;
	
	private List<ChnParam> chnParams;
	
	
	
	public DriverChnPickupData(){
		
		chnParams = new ArrayList<ChnParam>();
	}
	
	
	public enum PickupItem{
		VOLTAGE,
		CURRENT,
		/** 备份电压AD3 */
		BACK_VOLT_AD3,
	}
	 
	
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
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte) chnIndex);
   	    data.add((byte) (OPEN ? 0x01 : 0x00));
   	    data.add((byte) pickupItem.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
	    data = encodeData;
	    
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
			
	    	chnParam.setStepTime(ProtocolUtil.compose(encodeData.subList(index, index+4).toArray(new Byte[0]), true));
	    	index += 4;
	    	
	    	//chnParam.setHighPrecision((short)ProtocolUtil.getUnsignedByte(data.get(index++)));//2021-11-03添加精度
	    	
	    	chnParams.add(chnParam);
	    }
		
	}

	@Override
	public Code getCode() {
		return DriverCode.ChnPickupDataCode;
	}



	public PickupItem getPickupItem() {
		return pickupItem;
	}

	public void setPickupItem(PickupItem pickupItem) {
		this.pickupItem = pickupItem;
	}


	public List<ChnParam> getChnParams() {
		return chnParams;
	}

	public void setChnParams(List<ChnParam> chnParams) {
		this.chnParams = chnParams;
	}
	
	
	public boolean isOPEN() {
		return OPEN;
	}

	public void setOPEN(boolean OPEN) {
		this.OPEN = OPEN;
	}

	/**
	 * 返回通道参数
	 */
	public static class ChnParam{

		private int loop;//循环号
		private int step;//步次号
		private WorkMode workMode;//工作模式
		private long stepTime;//步次时间 ms
		//private short highPrecision;//精度x个档
		
//		public short getHighPrecision() {
//			return highPrecision;
//		}
//
//		public void setHighPrecision(short highPrecision) {
//			this.highPrecision = highPrecision;
//		}
		
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
	
}
