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
* @version 创建时间：2020年6月11日 下午10:06:10
* 用于测试名的配置和查询
*/
public class TestNameData extends Data implements Configable, Queryable, Responsable {
   
	
	private String testName;
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
	
		try {
			byte[] array = testName.getBytes("utf-8");
			data.add((byte) array.length);
			data.addAll(ProtocolUtil.convertArrayToList(array));
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
        int len = ProtocolUtil.getUnsignedByte(data.get(index++));
        try {
			testName = new String(ProtocolUtil.convertListToArray(encodeData.subList(index, index + len)),"utf-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.TestnameCode;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	

}
