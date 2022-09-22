package com.nltecklib.protocol.modbus.tcp.data;

import java.util.Arrays;

import com.nltecklib.protocol.modbus.tcp.constant.Environment.SendType;
import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;
import com.nltecklib.protocol.modbus.tcp.util.ProtocolUtil;


/**
 * 
 * @ClassName WriteMultiRester
 * @Description 写多个寄存器
 */
public class WriteMultiRegister implements Data{
	
	private Integer address;
	private Integer count;
	private Object value;
	private SendType type;
	
	public WriteMultiRegister() {
		super();
	}

	public WriteMultiRegister(Integer address, Integer count, Object value,SendType type) {
		super();
		this.address = address;
		this.count = count;
		this.value = value;
		this.type = type;
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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Boolean decoder(String hex) {
		String s = SLAVE_ID + Fun.WRITE_MULTI_REGISTER.getCode() + String.format("%04x", (int)address) + String.format("%04x",(int)count);

		if (hex.equals((getHex(s)).toUpperCase().trim())) {
			return true;
		}
		return false;
	}

	@Override
	public byte[] encoder() {
		String hex = SLAVE_ID + Fun.WRITE_MULTI_REGISTER.getCode() + String.format("%04x", (int)address) + String.format("%04x",
				(int)count) + String.format("%02x", count * 2);
		if (type.equals(SendType.INT) && value instanceof int[]) {
			int[] values = (int[]) value;
			int[] newValues = Arrays.copyOf(values, count);
			for (int i = 0; i < newValues.length; i++) {
				hex += String.format("%04x", newValues[i]);
			}
		}
		if (value instanceof String) {
			String values = (String) value;
			if (type.equals(SendType.UNICODE)) {
				
				String unicode = ConvertUtils.stringToUnicode(values);
				int unicodeLength = ProtocolUtil.calUnicodeLength(values);
				  //                  11
				if(unicodeLength >= count*2) {
					hex += unicode.substring(0, count*2);
				}else {
					hex += unicode.substring(0, unicodeLength*2);
					int size = count*2 - unicodeLength;
					for(int i = 0;i < size ; i ++) {
						hex += String.format("%02x", 0);
					}
				}		
			}
			if (type.equals(SendType.ASCII)) {
				if (values.length() >= count * 2) {
					values = values.substring(0, count);
					hex += ConvertUtils.stringToAscii(values);																				
				}else {
					hex += ConvertUtils.stringToAscii(values);
					int size = (int) ((count  - Math.ceil(values.length()/2.0)) * 2);
					for(int i = 0;i < size ; i ++) {
						hex += String.format("%02x", 0);
					}
				}
			}

		}		

		return ConvertUtils.hexStringToBytes(getHex(hex));
	}

	public String getHex(String hex) {
		return  String.format("%04x%04x%04x%s", (int)0,(int)0,(int)(hex.length()/2),hex);
	}
	
	
	/******
	 * 
	 * 协议规则：
	 * 
	 * 
	 * Modbus TCP  16（16进制=10码）功能码 写多（3）个寄存器的值
		发送详解：00 00 00 00 00 0D 01 10 00 00 00 03 06 00 0A 00 0B 00 0F
		00 00 事务标识符
		00 00 协议标识符
		00 0D 长度标识符
		   01 站号
		   10 功能码
		00 00 首个寄存器通讯地址
		00 03 写入的寄存器个数
		06 00 写入的字节数
		00 0A 写入第一个寄存器的数值
		00 0B 写入第二个寄存器的数值
		00 0F 写入第三个寄存器的数值
		
		接收详解：00 00 00 00 00 06 01 10 00 00 00 03
		00 00 事务标识符
		00 00 协议标识符
		00 06 长度标识符
		01 站号
		10 功能码
		00 00 首个寄存器通讯地址
		00 03 写入的寄存器个数
	 * 
	 * 
	 * 
	 * 
	 * *****
	 */

}
