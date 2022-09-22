package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlertState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 请使用MechanismStateQueryData，弃用
 * @author Administrator
 *
 */
@Deprecated
public class PressureTempStateQueryData extends Data implements Queryable, Responsable {
    
	private List<AlertState> tcStates = new ArrayList<AlertState>();
	private List<Double>     tcReads  = new ArrayList<Double>();
	private AlertState       pressureState = AlertState.NORMAL;
	private AlertState       connectState = AlertState.NORMAL;
	
	@Override
	public boolean supportUnit() {
		
		return false;
	}

	@Override
	public boolean supportDriver() {
		
		return true;
	}

	@Override
	public boolean supportChannel() {
		
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		//温度
		data.add((byte) tcStates.size());
		//
		for(int n = 0 ; n < tcStates.size() ; n++) {
			
			data.add((byte) (tcStates.get(n).ordinal()));
			data.add((byte) (double)tcReads.get(n));
		}
		//气压状态
		data.add((byte) pressureState.ordinal());
		//气压串口连接状态
		data.add((byte) connectState.ordinal());
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
         int index = 0;
         data = encodeData;
         driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
         int count = ProtocolUtil.getUnsignedByte(data.get(index++));
         for(int n = 0 ; n < count ; n++) {
        	 
        	 int code = ProtocolUtil.getUnsignedByte(data.get(index++));
        	 if(code > AlertState.values().length - 1) {
        		 
        		 throw new RuntimeException("error tc state code : " + code);
        	 }
        	 tcStates.add(AlertState.values()[code]);
        	 tcReads.add((double) ProtocolUtil.getUnsignedByte(data.get(index++)));
         }
         //气压状态
         int code = ProtocolUtil.getUnsignedByte(data.get(index++));
    	 if(code > AlertState.values().length - 1) {
    		 
    		 throw new RuntimeException("error pressure state code : " + code);
    	 }
         pressureState = AlertState.values()[code];
         //气压串口连接状态
         code = ProtocolUtil.getUnsignedByte(data.get(index++));
    	 if(code > AlertState.values().length - 1) {
    		 
    		 throw new RuntimeException("error connect state code : " + code);
    	 }
         connectState = AlertState.values()[code];
         
	}

	@Override
	public Code getCode() {
		
		return null;
	}

	public List<AlertState> getTcStates() {
		return tcStates;
	}

	public void setTcStates(List<AlertState> tcStates) {
		this.tcStates = tcStates;
	}

	public List<Double> getTcReads() {
		return tcReads;
	}

	public void setTcReads(List<Double> tcReads) {
		this.tcReads = tcReads;
	}

	public AlertState getPressureState() {
		return pressureState;
	}

	public void setPressureState(AlertState pressureState) {
		this.pressureState = pressureState;
	}

	public AlertState getConnectState() {
		return connectState;
	}

	public void setConnectState(AlertState connectState) {
		this.connectState = connectState;
	}
	
	

}
