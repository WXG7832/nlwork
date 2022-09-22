package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 液晶屏通讯状态显示
 * @Desc：   
 * @author：LLC   
 * @Date：2021年10月19日 上午12:28:39   
 * @version
 */
public class ScreenCommStateData  extends Data implements Configable, Responsable {
   
	private static final int IP_ADDR_BYTE_LENGTH = 4;
	
	//液晶屏地址
	private int screenIndex;
	
	private List<UnitStateM> unitStates = new ArrayList<UnitStateM>();
	
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) screenIndex);
		data.add((byte) unitStates.size());
		for(int n = 0 ; n < unitStates.size() ; n++) {
			
			data.add((byte) (unitStates.get(n).connect ? 0x01 : 0x00));
			
			for(int j = 0; j < IP_ADDR_BYTE_LENGTH ; j++) {
				data.add(unitStates.get(n).ipAddress[j]);
			}
			
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
	    data = encodeData;
	    int index = 0;
	    screenIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    int count = ProtocolUtil.getUnsignedByte(data.get(index++));
	    
	    unitStates.clear();
	    
	    for(int n = 0 ; n < count ; n++) {
	    	
	    	UnitStateM unitStateM = new UnitStateM();
	    	unitStateM.connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
	    	
	    	for(int j = 0; j < IP_ADDR_BYTE_LENGTH ; j++) {
	    		unitStateM.ipAddress[j] = data.get(index++);
			}
	    	
	    	unitStates.add(unitStateM);
	    }

	}

	@Override
	public Code getCode() {
		return AccessoryCode.ScreenCommStateCode;
	}


	/**
	 * 单元状态模型
	 */
	public static class UnitStateM{
	
		public boolean connect = false;
		
		public byte[]  ipAddress = new byte[IP_ADDR_BYTE_LENGTH];
		
		public void setIpAddress(String ip) {
			
			String[] secs = ip.split("\\.");
			for(int n = 0 ; n < ipAddress.length ; n++) {
				
				ipAddress[n] = (byte) Integer.parseInt(secs[n]);
			}
			
		}
		
		
		public String getIpAddress() {
			
			StringBuffer info = new StringBuffer();
	         for(int n = 0 ; n < ipAddress.length ; n++) {
				
				 info.append(ProtocolUtil.getUnsignedByte(ipAddress[n]));
				 if(n < ipAddress.length - 1) {
					 
					 info.append(".");
				 }
			}
	         
	         return info.toString();
			
		}
	
	}


	public int getScreenIndex() {
		return screenIndex;
	}

	public void setScreenIndex(int screenIndex) {
		this.screenIndex = screenIndex;
	}

	public List<UnitStateM> getUnitStates() {
		return unitStates;
	}

	public void setUnitStates(List<UnitStateM> unitStates) {
		this.unitStates = unitStates;
	}

}
