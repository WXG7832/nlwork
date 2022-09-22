package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 流程测试名
 * @author Administrator
 *
 */
public class TestNameData extends Data implements Configable,Queryable,Responsable {
   
	
	private String  testName; //流程测试名
	private static final int  NAME_BYTES = 50 ; //固定50个字节
	
	public String getTestName() {
		return testName.trim();
	}

	public void setTestName(String testName) {
		this.testName = testName.trim();
	}

	@Override
	public void encode() {
		
		data.add((byte)unitIndex);
		byte[] array = null;
		try {
			array = testName.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0 ; i < NAME_BYTES ; i++) {
			
			  if(i < array.length) {
				  
				  data.add(array[i]);
			  }else {
				  
				  data.add((byte) 0);
			  }
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
        unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
        byte[] nameBytes = new byte[NAME_BYTES];
        for(int i = index ; i < index + NAME_BYTES ; i++) {
        	
        	nameBytes[i - index] = data.get(i);
        }
        try {
        	
			testName = new String(nameBytes,"utf-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
        
	}

	@Override
	public Code getCode() {
		
		return MainCode.TestNameCode;
	}

	@Override
	public boolean supportUnit() {
		
		return true;
	}

	@Override
	public String toString() {
		return "TestNameData [testName=" + testName + "]";
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
