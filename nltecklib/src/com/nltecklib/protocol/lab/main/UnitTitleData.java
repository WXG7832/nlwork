package com.nltecklib.protocol.lab.main;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年10月18日 下午2:26:01
* 单元号标题配置和读取
*/
public class UnitTitleData extends Data implements Configable, Queryable, Responsable {
   
	private int unitIndex;
	
	
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
		
		
		data.add((byte) unitIndex);

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
        unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.UnitTitleCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	@Override
	public String toString() {
		return "UnitTitleData [unitIndex=" + unitIndex + "]";
	}

	
    
	
	

}
