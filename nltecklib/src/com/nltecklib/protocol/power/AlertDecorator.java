package com.nltecklib.protocol.power;

import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.Environment.Orient;
import com.nltecklib.protocol.util.ProtocolUtil;


public class AlertDecorator implements Decorator{
    
	private Data  destData;
	
	public AlertDecorator(Data destData){
		
		if(!(destData instanceof Alertable)){
			
			throw new RuntimeException("func code:" + destData.getCode() +" do not support alert protocol");
		}
		
		this.destData = destData;
		this.destData.setOrient(Orient.ALERT);
	}
	
	@Override
	public void encode() {
		
		destData.clear();
        if (destData.supportDriver()) {
			
			//pc和上位机通信必须带主控地址
			destData.getEncodeData().add((byte)destData.getDriverIndex());
		}
		if(destData.supportChannel()) {
			
			destData.getEncodeData().add((byte)destData.getChnIndex());
		}
		destData.encode();
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		if (destData.supportDriver()) {
			
			destData.setDriverIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}
		if(destData.supportChannel()) {
			
			destData.setChnIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
		}
        destData.decode(encodeData.subList(index, encodeData.size()));
	}

	@Override
	public Code getCode() {
		
		return destData.getCode();
	}

	public Data getDestData() {
		return destData;
	}

	@Override
	public Orient getOrient() {
		
		return Orient.ALERT;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  destData.toString();
	}
	
	

}
