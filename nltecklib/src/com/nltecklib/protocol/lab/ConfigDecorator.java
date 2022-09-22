package com.nltecklib.protocol.lab;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Orient;
import com.nltecklib.protocol.util.ProtocolUtil;


public class ConfigDecorator implements Decorator,Comparable {
    
	private Data  destData;
	
	public ConfigDecorator(Data destData){
		
        if(!(destData instanceof Configable) ){
			
			throw new RuntimeException("data code (func code:" +destData.getCode() +")is not suitalbe for config");
		}
		this.destData = destData;
		this.destData.setOrient(Orient.CONFIG);
		
	}
	
	@Override
	public void encode() {
		
		destData.clear();
		if (destData.supportMain()) {
			
			//pc和上位机通信必须带主控地址
			destData.getEncodeData().add((byte)destData.getMainIndex());
		}
		if(destData.supportChannel()) {
			
			destData.getEncodeData().add((byte)destData.getChnIndex());
		}
        destData.encode();
      
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		if (destData.supportMain()) {
			
			destData.setMainIndex(ProtocolUtil.getUnsignedByte(encodeData.get(index++)));
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
	public int compareTo(Object obj) {
		int order = 0 , orderCompare = 0;
		if(obj instanceof QueryDecorator){
			
			   orderCompare = 1;
		}else if(obj instanceof ConfigDecorator){
			
		}else{
			
			throw new RuntimeException("发送队列只能装入配置和查询命令");
		}
		return order - orderCompare; //数字越小指令优先级越高
	}

	@Override
	public Orient getOrient() {
		// TODO Auto-generated method stub
		return Orient.CONFIG;
	}
    
	@Override
	public String toString() {
		
		return destData.toString();
	}
	

	
	
	

}
