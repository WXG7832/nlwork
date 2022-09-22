package com.nltecklib.protocol.lab.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.Recover;
import com.nltecklib.protocol.util.ProtocolUtil;

public class DeviceExceptionData extends Data implements Configable,Queryable,Responsable {

	private Recover powOffRecover = Recover.AUTO; //¶Ïµç»Ö¸´
	private Recover netOffRecover = Recover.AUTO; //¶ÏÍø»Ö¸´
	private Recover calOffRecover = Recover.AUTO; //Ð£×¼»Ö¸´
	
	
	@Override
	public void encode() {
		
		data.add((byte)powOffRecover.ordinal());
		data.add((byte)netOffRecover.ordinal());
		data.add((byte)calOffRecover.ordinal());
		
	}
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;

		powOffRecover = Recover.values()[ProtocolUtil.getUnsignedByte(data.get(index++))];
		netOffRecover = Recover.values()[ProtocolUtil.getUnsignedByte(data.get(index++))];
		calOffRecover = Recover.values()[ProtocolUtil.getUnsignedByte(data.get(index++))];
		
	}
	
	@Override
	public Code getCode() {
		return MainCode.DeviceExceptionCode;
	}
	
	
	public Recover getPowOffRecover() {
		return powOffRecover;
	}

	public void setPowOffRecover(Recover powOffRecover) {
		this.powOffRecover = powOffRecover;
	}

	public Recover getNetOffRecover() {
		return netOffRecover;
	}

	public void setNetOffRecover(Recover netOffRecover) {
		this.netOffRecover = netOffRecover;
	}

	public Recover getCalOffRecover() {
		return calOffRecover;
	}

	public void setCalOffRecover(Recover calOffRecover) {
		this.calOffRecover = calOffRecover;
	}


	@Override
	public String toString() {
		return "DeviceExceptionData [powOffRecover=" + powOffRecover + ", netOffRecover=" + netOffRecover
				+ ", calOffRecover=" + calOffRecover + "]";
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	
}
