package com.nltecklib.protocol.modbus.tcp.data;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.modbus.tcp.constant.Environment;
import com.nltecklib.protocol.modbus.tcp.util.ByteUtils;
import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;


/**
 * 
 * @ClassName ReadCoil
 * @Description 读线圈
 */
public class ReadCoils implements Data{
	
	private Integer address;
	private Integer count;
	
	public ReadCoils() {
		super();
	}

	public ReadCoils(Integer address, Integer count) {
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
		String func = hex.substring(Environment.getHeadLength() + 2, Environment.getHeadLength() + 4);
		if (func.equals(Fun.READ_COILS.getCode())) {
			int byteLength = Integer.parseInt(hex.substring(Environment.getHeadLength() + 4,Environment.getHeadLength() + 6),16); //hex.substring(4,6) 地址的前两位，可能为补的两个0
			for (int i = byteLength -1; i >= 0; i--) {
				String b = hex.substring(Environment.getHeadLength() + 6 + i * 2,Environment.getHeadLength() + 8 + i * 2); //地址字符串
				results.addAll(ByteUtils.byteToBitList((byte)Integer.parseInt(b,16)));
			}
			return results;
		}
		return results;
	}

	@Override
	public byte[] encoder() {
		//String.format("%04x", (int)address)  定义按16进制输出数据，最小输出宽度为4个字符，右对齐，如果输出的数据小于4个字符，前补0
		String hex = SLAVE_ID + Fun.READ_COILS.getCode() + String.format("%04x", (int)address) + String.format("%04x", (int)count);

		return ConvertUtils.hexStringToBytes(String.format("%04x%04x%04x%s", (int)0,(int)0,(int)hex.length()/2,hex));
	}
}
