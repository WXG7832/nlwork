package com.nltecklib.protocol.li.MBWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.IDType;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年12月31日 上午11:01:18 类说明
 */
public class MBUuidData extends Data implements Configable, Queryable, Responsable {

	private static final int UUID_LENGTH = 16;
	private String uuid;

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
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
	
	

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);

		if (chnIndex > IDType.values().length - 1) {

			throw new RuntimeException("error id type code :" + chnIndex);
		}
		data.add((byte) chnIndex); // id类型
		if (uuid.length()  != UUID_LENGTH * 2) {

			throw new RuntimeException("error uuid :" + uuid);
		}
		for (int i = 0; i < UUID_LENGTH; i++) {

			data.add((byte) Integer.parseInt(uuid.substring(i*2, i *2+ 2), 16));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

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
		return MBWorkformCode.UUIDCode;
	}

	public void setIDType(IDType type) {

		this.chnIndex = type.ordinal();
	}

	public IDType getIDType() {

		return IDType.values()[chnIndex];
	}

	@Override
	public String toString() {
		return "MBUuidData [uuid=" + uuid + "]";
	}

	
	

}
