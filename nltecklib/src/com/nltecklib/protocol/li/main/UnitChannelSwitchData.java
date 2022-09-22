package com.nltecklib.protocol.li.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class UnitChannelSwitchData extends Data implements Configable,Queryable,Responsable{
   
	private List<Byte>  channelStates = new ArrayList<Byte>();
	
	public static final int CHN_STATE_OPEN = 0x01;
	public static final int CHN_STATE_CLOSE = 0x00;
	

	@Override
	public String toString() {
		return "ChannelSwitchData [channelStates=" + Entity.printList(channelStates)+ "]";
	}



	public void   init(int count , boolean close){
		
		  if(count % 8 != 0  || count < 8){
			  
			  throw new RuntimeException("count is not % 8!");
		  }
		  Byte b = new Byte((byte) (close ? 0x00 : 0xff));
		  for(int i = 0 ; i < count / 8 ; i++){
			  
			   channelStates.add(b);
		  }
	}
	
	public void   setState(int index , boolean close){
		
		//1 byte 占据 8位通道
		
		int n = index / 8 ;
		//第一个字节为
		int val = channelStates.get(n) & 0x0ff;
		
		int m = index % 8 ;
		
		if(close){
			 val = val & ~(0x01 << m);
			
		}else{
		     val = val | (0x01 << m);
		}
		channelStates.set(n, (byte)val);
	}
	
	

	@Override
	public void encode() {

    	 data.add((byte)unitIndex); 
    	 //低位在前，高位在后
    	 for(int i = 0 ; i < channelStates.size() ; i++) {
    		 
    		 data.add(channelStates.get(i));
    	 }
    	
         

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
	    unitIndex = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
	    //低位在前，高位在后
	    for(int i = index ; i < encodeData.size() ; i++) {
	    	
	    	channelStates.add(encodeData.get(i));
	    }
	  
		
	}

	@Override
	public Code getCode() {
		
		return MainCode.LogicChnSwitchCode;
	}



	public int getUnitIndex() {
		return unitIndex;
	}



	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}



	public List<Byte> getChannelStates() {
		return channelStates;
	}

	public void setChannelStates(List<Byte> channelStates) {
		this.channelStates = channelStates;
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
