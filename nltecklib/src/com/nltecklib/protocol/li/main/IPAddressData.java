package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class IPAddressData extends Data implements Configable, Responsable {

	private int addr1 = 192;
	private int addr2 = 168;
	private int addr3 = 1;
	private int addr4 = 127;

	@Override
	public boolean supportUnit() {

		return false;
	}

	public IPAddressData() {

	}

	public IPAddressData(String ip) {
            
		SetIpAddress(ip);
	}

	@Override
	public void encode() {

		data.add((byte) addr1);
		data.add((byte) addr2);
		data.add((byte) addr3);
		data.add((byte) addr4);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub
		data = encodeData;
		int index = 0;
		addr1 = ProtocolUtil.getUnsignedByte(data.get(index++));
		addr2 = ProtocolUtil.getUnsignedByte(data.get(index++));
		addr3 = ProtocolUtil.getUnsignedByte(data.get(index++));
		addr4 = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.IPAddressCode;
	}

	public String getIpAddress() {

		return addr1 + "." + addr2 + "." + addr3 + "." + addr4;
	}

	public void SetIpAddress(String ip) {

		String[] secs = ip.split("\\.");
		for (int n = 0; n < secs.length; n++) {

			int val = Integer.parseInt(secs[n]);
			switch (n) {

			case 0:
				addr1 = val;
				break;
			case 1:
				addr2 = val;
				break;
			case 2:
				addr3 = val;
				break;
			case 3:
				addr4 = val;
				break;
			default:
				break;

			}
		}
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
