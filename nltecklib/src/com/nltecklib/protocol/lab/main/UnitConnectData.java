package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月17日 上午9:05:07
* 对多个单元进行连接或断开网络操作
*/
public class UnitConnectData extends Data implements Configable,  Responsable {
    
	private List<Integer> chnList = new ArrayList<>();
	
	private boolean connect;
	
	private boolean force; //是否强制操作
	
	
	@Override
	public boolean supportMain() {
		
		return false;
	}

	@Override
	public boolean supportChannel() {

		return false;
	}

	@Override
	public void encode() {
		
          
		data.add((byte) chnList.size());
		for(int n = 0 ; n < chnList.size() ; n++) {
			
			int val = (int)chnList.get(n);
			data.add((byte) val);
		}
		data.add((byte) (connect ? 0x01 : 0x00));
		data.add((byte) (force ? 0x01 : 0x00));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for(int n = 0 ; n < count ; n++) {
			
			chnList.add(ProtocolUtil.getUnsignedByte(data.get(index++)));
		}
		connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		force   = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UnitConnectCode;
	}
	
	
	public void appendChnIndex(int chnIndex) {
		
		chnList.add(chnIndex);
		
	}
	
	public void clearChnIndexs() {
		
		chnList.clear();
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}


	public List<Integer> getChnList() {
		return chnList;
	}

	@Override
	public String toString() {
		return "UnitConnectData [chnList=" + chnList + ", connect=" + connect + ", force=" + force + "]";
	}

	
	
	
	

}
