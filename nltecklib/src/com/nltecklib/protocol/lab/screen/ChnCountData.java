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
* @version 创建时间：2021年9月2日 下午4:39:14
* 通道设定
*/
public class ChnCountData extends Data implements Configable, Queryable, Responsable {
   
	private int chnCount;
	
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
		
		data.add((byte) chnCount);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		chnCount = ProtocolUtil.getUnsignedByte(data.get(0));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.ChnCountCode;
	}

	public int getChnCount() {
		return chnCount;
	}

	public void setChnCount(int chnCount) {
		this.chnCount = chnCount;
	}

	@Override
	public String toString() {
		return "ChnCountData [chnCount=" + chnCount + "]";
	}
	
	

}
