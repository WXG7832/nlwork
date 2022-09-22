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
* @version 创建时间：2021年9月2日 下午4:36:27
* 类说明
*/
public class UnitData extends Data implements Configable, Queryable, Responsable {
   
	private int unitIndex;
	
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
		
		data.add((byte) unitIndex);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(0));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ScreenCode.UnitCode;
	}
	
	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	@Override
	public String toString() {
		return "UnitData [unitIndex=" + unitIndex + "]";
	}
	
	

}
