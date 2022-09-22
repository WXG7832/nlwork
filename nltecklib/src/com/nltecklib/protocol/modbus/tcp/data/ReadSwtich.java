package com.nltecklib.protocol.modbus.tcp.data;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.modbus.tcp.constant.Environment;
import com.nltecklib.protocol.modbus.tcp.util.ByteUtils;
import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;

/**
 * 
 * @ClassName ReadCoil
 * @Description ∂¡ø™πÿ ‰»Î
 */
public class ReadSwtich implements Data{
	
	private Integer address;
	private Integer count;
	
	public ReadSwtich() {
		super();
	}

	public ReadSwtich(Integer address, Integer count) {
		super();
		this.address = address;
		this.count = count;
	}

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public List<Integer> decoder(String hex) {
		List<Integer> results = new ArrayList<>();
		hex = hex.substring(Environment.getHeadLength(),hex.length());
		String func = hex.substring(2, 4);
		if (func.equals(Fun.READ_SWTICH.getCode())) {
			int byteLength = Integer.parseInt(hex.substring(4,6),16);
			for (int i = byteLength -1; i >= 0; i--) {
				String b = hex.substring(6 + i * 2,8 + i * 2);
				results.addAll(ByteUtils.byteToBitList((byte)Integer.parseInt(b,16)));
			}
			return results;
		}
		return results;
	}

	@Override
	public byte[] encoder() {
		
		String hex = SLAVE_ID + Fun.READ_SWTICH.getCode() + String.format("%04x", (int)address) + String.format("%04x", (int)count);
		
		return ConvertUtils.hexStringToBytes(getHex(hex));
	}
	public String getHex(String hex) {
		return  String.format("%04x%04x%04x%s", (int)0,(int)0,(int)(hex.length()/2),hex);
	}
}
