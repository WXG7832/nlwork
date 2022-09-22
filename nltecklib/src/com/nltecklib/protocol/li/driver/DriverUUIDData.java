package com.nltecklib.protocol.li.driver;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.IDType;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.DriverEnvironment.UuidType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年5月8日 下午6:29:31
* 类说明
*/
public class DriverUUIDData extends Data implements Configable, Queryable, Responsable {
   
	private static final int UUID_LENGTH = 16;
	private String     uuid;
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
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
		
		data.add((byte) driverIndex);
		
         if(chnIndex > UuidType.values().length - 1) {
			
			throw new RuntimeException("error id uuid type code :" + chnIndex);
		}
		data.add((byte) chnIndex); //id类型
		
         if(uuid.length()  != UUID_LENGTH * 2) {
			
			throw new RuntimeException("error uuid :" + uuid);
		}
		int index= 0;
		for (int i = 0; i < UUID_LENGTH; i++) {
			
		    data.add((byte) Integer.parseInt(uuid.substring(index, index = index + 2), 16));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if(chnIndex > UuidType.values().length - 1) {
			
			throw new RuntimeException("error uuid type code :" + chnIndex);
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < UUID_LENGTH; i++) {
			String string = Integer.toHexString(ProtocolUtil.getUnsignedByte(data.get(index++)));
			if (string.length() < 2) {
				string = "0" + string;
			}
			stringBuilder.append(string);
		}
		uuid = stringBuilder.toString();

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverCode.CALIBRATE_UUID_CODE;
	}

    

	public UuidType getUuidType() {
		return UuidType.values()[chnIndex];
	}

	public void setUuidType(UuidType uuidType) {
		this.chnIndex = uuidType.ordinal();
	}


	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "DriverUUIDData [uuidType=" + chnIndex + ", uuid=" + uuid + "]";
	}

	
	

}
