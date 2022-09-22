package com.nltecklib.protocol.fuel;

import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;

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
		destData.encode();
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		destData.decode(encodeData);
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
