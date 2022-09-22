package com.nltecklib.protocol.modbus.tcp.data;

import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;

/**
 * 
 * @ClassName WriteSingleCoil
 * @Description 写单个线圈
 */
public class WriteSingleCoil implements Data{
	
	private static final String OPEN = "FF00";
	private static final String CLOSR = "0000";
	private boolean flag;
	private Integer address;	
	
	public WriteSingleCoil() {
		super();
	}
	public WriteSingleCoil(boolean flag, Integer address) {
		super();
		this.flag = flag;
		this.address = address;
	}
	

	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public Integer getAddress() {
		return address;
	}
	public void setAddress(Integer address) {
		this.address = address;
	}
	@Override
	public Boolean decoder(String hex) {
		if (hex.equals(getHex().toUpperCase().trim())) {
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
		
		String hex = SLAVE_ID + Fun.WRITE_SINGLE_COIL.getCode() + String.format("%04x", (int)address);
		if (flag) {
			hex += OPEN;
		}else {
			hex += CLOSR;
		}
		
		return String.format("%04x%04x%04x%s", (int)0,(int)0,(int)hex.length()/2,hex);
	}
	

}
