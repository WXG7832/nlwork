package com.nltecklib.protocol.modbus.tcp.data;

import com.nltecklib.protocol.modbus.tcp.constant.Environment;
import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;

/**
 * 
 * @ClassName ReadInputRegister
 * @Description ∂¡ ‰»Îºƒ¥Ê∆˜÷µ
 */
public class ReadInputRegister implements Data{
	
	private Integer address;
	private Integer count;
	
	public ReadInputRegister() {
		super();
	}

	public ReadInputRegister(Integer address, Integer count) {
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
	public String decoder(String hex) {
		String result = null;
		hex = hex.substring(Environment.getHeadLength(),hex.length());
		String func = hex.substring(2, 4);
		if (func.equals(Fun.READ_INPUT_REGISTER.getCode())) {
			int byteLength = Integer.parseInt(hex.substring(4,6),16);
			result = hex.substring(6,6 + byteLength * 2);			
			return result;
		}
		return result;
	}

	@Override
	public byte[] encoder() {
		
		String hex = SLAVE_ID + Fun.READ_INPUT_REGISTER.getCode() + String.format("%04x", (int)address) + String.format("%04x", (int)count);
		return ConvertUtils.hexStringToBytes(getHex(hex));
	}
	
	public String getHex(String hex) {
		return  String.format("%04x%04x%04x%s", (int)0,(int)0,(int)(hex.length()/2),hex);
	}
}

