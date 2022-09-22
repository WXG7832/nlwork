package com.nltecklib.protocol.modbus.tcp.data;

import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;

/**
 * 
 * @ClassName WriteSingleRegister
 * @Description 写单个寄存器
 */
public class WriteSingleRegister implements Data{
	
	private Integer address;
	private Integer value;

	
	public WriteSingleRegister() {
		super();
	}

	public WriteSingleRegister(Integer address, Integer value) {
		super();
		this.address = address;
		this.value = value;
	}

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public Boolean decoder(String hex) {
		if (hex.equals(hex.toUpperCase().trim())) {
			return true;
		}
		return false;
	}

	@Override
	public byte[] encoder() {
		return ConvertUtils.hexStringToBytes(getHex());
	}
	/**
	 * 获得hex码
	 * @return
	 */
	private String getHex() {

		String hex = SLAVE_ID + Fun.WRITE_SINGLE_REGISTER.getCode() + String.format("%04x", (int)address) + String.format("%04x", (int)value);

		return  String.format("%04x%04x%04x%s", (int)0,(int)0,(int)hex.length()/2,hex);
	}

}
