package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 保护或获取保护方案
 * @author Administrator
 *
 */
public class SaveParamData extends Data implements Configable ,Queryable ,  Responsable {
    
	private String name = "";
	private boolean defaultPlan; //复测方案,注意此处为了兼容旧版本，故不再修改属性名为retest;实际true就是代表复测保护方案
	private WorkType  workType = WorkType.AG; //测试类型
	private static final int  NAME_BYTES = 50 ; //固定50个字节
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {
		
		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		//data.add((byte) chnIndex);
		byte[] array = null;
		try {
			array = name.getBytes("utf-8");
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
		//测试类型
		data.add((byte) workType.ordinal());
		//是否复测保护方案
		data.add((byte) (defaultPlan ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		//chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		byte[] nameBytes = new byte[NAME_BYTES];
        int strLen = 0;
        for(int i = index ; i < index + NAME_BYTES ; i++) {
        	
            if(data.get(i) == 0) {
        		
        		break;
        	}
        	nameBytes[i - index] = data.get(i);
        	strLen++;
        }
        try {
        	
        	name = new String(Arrays.copyOfRange(nameBytes, 0, strLen),"utf-8");
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			throw new RuntimeException("decode parameter name error :" + e.getMessage());
		}
        index += NAME_BYTES;
        //测试类型
        int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
        if(flag > WorkType.values().length - 1) {
        	
        	throw new RuntimeException("error work type code :" +  flag);
        }
        workType = WorkType.values()[flag];
        //默认保护方案
        defaultPlan = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SaveParamCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public boolean isDefaultPlan() {
		return defaultPlan;
	}

	public void setDefaultPlan(boolean defaultPlan) {
		this.defaultPlan = defaultPlan;
	}

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	
	public boolean  isRetest() {
		
		return this.chnIndex == 1;
	}
	
	public void setRetest(boolean retest) {
		
		this.chnIndex  = retest ? 0x01 : 0x00;
	}
	
 
	@Override
	public String toString() {
		return "SaveParamData [name=" + name + ", retest=" + defaultPlan + ", workType=" + workType + "]";
	}

	/**
	 *  0代表AG , 1代表IC；用于查询指定类型的保护方案
	 */
	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
