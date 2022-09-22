package com.nltecklib.protocol.lab.screen;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年9月2日 下午7:10:43
* 通信状态
*/
public class CommunicationData extends Data implements Configable, Queryable, Responsable {
   
	private boolean communicationOk;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		data.add((byte) (communicationOk ? 0x01 : 0x02));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		communicationOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.CommunicateCode;
	}

	public boolean isCommunicationOk() {
		return communicationOk;
	}

	public void setCommunicationOk(boolean communicationOk) {
		this.communicationOk = communicationOk;
	}

	@Override
	public String toString() {
		return "CommunicationData [communicationOk=" + communicationOk + "]";
	}
	
	

}
