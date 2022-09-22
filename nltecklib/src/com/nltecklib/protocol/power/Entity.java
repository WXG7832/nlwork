package com.nltecklib.protocol.power;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;

import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.Environment.Orient;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.util.CRC16;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月15日 上午10:38:26 协议核心实体对象,用于编码和解码
 */
public class Entity implements NlteckPackageFactory {

	// 子板通信协议头（同步通信）
	public static final byte[] HEAD = { 0x68, (byte) 0xff, (byte) 0xfe, (byte) 0xfe };
	public static final byte TRAIL = 0x16;

	public static final int MIN_DECODE_LEN = 11;
	public static final int SHELL_LEN = 14;

	/**
	 * 通信协议类型
	 * 
	 * @author wavy_zheng 2021年12月15日
	 *
	 */
	public enum ProtocolType {

		DRIVER(0x01),TEMPER(0x02),CHECK(0x03),MAIN(0x04),CALBOARD(0x05),CALSOFT(0x06),CALBOX_DEVICE(0x07);

		private int code;

		private ProtocolType(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}

		public static ProtocolType valueOf(int code) {

			for (ProtocolType temp : ProtocolType.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}

			return null;
		}
	}
	
	public static Decorator decode(List<Byte> data) {
		return (Decorator) new Entity().decode(ProtocolUtil.convertListToArray(data));
	}

	@Override
	public NlteckIOPackage decode(byte[] data) {

		// 获取界定点
		int index = findHeadPos(data);
		int len = getPackLen(data);
		List<Byte> list = ProtocolUtil.convertArrayToList(data);
		// 检查协议头
		if (index == -1) {
			throw new RuntimeException("error head code:" + printList(list));
		}
		list = list.subList(index, index + len);

		// 检查协议类型
		int type = ProtocolUtil.getUnsignedByte(list.get(4));

		ProtocolType pt = null;
		if ((pt = ProtocolType.valueOf(type)) == null) {

			throw new RuntimeException("error protocol type code:" + printList(list));
		}
		
		int code = ProtocolUtil.getUnsignedByte(list.get(5));
		// 解析发送模式方向
		Orient orient = Orient.values()[list.get(6)];
		
		
		
		// 检查CRC16校验码是否正确
		int crcCalc = CRC16.calcCrc16(list.subList(0, list.size() - 3).toArray(new Byte[0]));
		Byte[] crcArray = new Byte[] { list.get(list.size() - 3), list.get(list.size() - 2) };
		long crcRecv = ProtocolUtil.compose(crcArray, true);

		if (crcRecv != crcCalc) {

			throw new RuntimeException(
					"calculate CRC16=" + crcCalc + ",recv CRC16=" + crcRecv + ",CRC16 ERROR:" + printList(list));
		}
		// 检查协议尾
		if (list.get(list.size() - 1) != TRAIL) {
			throw new RuntimeException("error trail code :" + printList(list));
		}
		
		// 解析数据
		Decorator dataSec = null;

		if (ProtocolType.valueOf(type) == ProtocolType.DRIVER) {
			dataSec = DataFactory.createDriverData(orient, DriverCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.CHECK) {
			dataSec = DataFactory.createCheckData(orient, CheckCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.TEMPER) {
			dataSec = DataFactory.createTemperData(orient, TemperCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.MAIN) {
			dataSec = DataFactory.createMainData(orient, MainCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.CALBOARD) {
			dataSec = DataFactory.createCalBoardData(orient, CalBoardCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.CALSOFT) {
			dataSec = DataFactory.createCalSoftData(orient, CalSoftCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.CALBOX_DEVICE) {
			dataSec = DataFactory.createCalBox_DeviceData(orient, CalBoxDeviceCode.valueOf(code));
		}

		dataSec.decode(new LinkedList<Byte>(list.subList(MIN_DECODE_LEN, list.size() - 3)));
		return (NlteckIOPackage) dataSec;
	}
	
	public static ProtocolType convertFromCode(Code code) {

		if (code instanceof DriverCode) {
			return ProtocolType.DRIVER;
		}else if (code instanceof TemperCode) {
			return ProtocolType.TEMPER;
		}else if (code instanceof CheckCode) {
			return ProtocolType.CHECK;
		}else if (code instanceof MainCode) {
			return ProtocolType.MAIN;
		}else if (code instanceof CalBoardCode) {
			return ProtocolType.CALBOARD;
		}else if (code instanceof CalSoftCode) {
			return ProtocolType.CALSOFT;
		}else if (code instanceof CalBoxDeviceCode) {
			return ProtocolType.CALBOX_DEVICE;
		}
		
		return null;
	}
	
	public static List<Byte> encode(Decorator dec) throws Exception {

		return ProtocolUtil.convertArrayToList(new Entity().encode((NlteckIOPackage) dec));
		
	}


	@Override
	public byte[] encode(NlteckIOPackage pack) {
		
		
		Decorator dec = (Decorator) pack;
		dec.encode();
		List<Byte> buff = new ArrayList<Byte>();
		buff.addAll(ProtocolUtil.convertArrayToList(HEAD));
		ProtocolType pt = convertFromCode(dec.getCode());
		if (pt == null) {

			throw new RuntimeException("invalid protocol type : " + dec.getCode());
		}
		
		buff.add((byte) pt.getCode());
		buff.add((byte) dec.getCode().getCode());
		buff.add((byte) dec.getOrient().ordinal());
		
		int bytesLen = 4; //默认两字节
		
		Byte[] lenData = ProtocolUtil.split(dec.getDestData().getLength(), bytesLen, true);
		buff.addAll(Arrays.asList(lenData)); // 数据长度
		buff.addAll(dec.getDestData().getEncodeData());
		int crc = CRC16.calcCrc16(buff.toArray(new Byte[0]));
		Byte[] crcData = ProtocolUtil.split(crc, 2, true);
		buff.addAll(Arrays.asList(crcData)); // 数据长度
		buff.add(TRAIL);
		

		return ProtocolUtil.convertListToArray(buff);
	}

	@Override
	public int getMinDecodeLen() {
		// TODO Auto-generated method stub
		return MIN_DECODE_LEN;
	}

	@Override
	public int getPackLen(byte[] data) {
		int index = findHeadPos(data);
		if (data.length - index < getMinDecodeLen()) {

			throw new RuntimeException("can not decode data with short of data length");
		}
		if (index == -1) {

			throw new RuntimeException("error protocol head :" + data[0]);
		}
		int dataLen = (int) ProtocolUtil
				.compose(new byte[] { data[index + 7], data[index + 8], data[index + 9], data[index + 10] }, true);
		return dataLen + SHELL_LEN;
	}

	@Override
	public int findHeadPos(byte[] data) {
		int start = -1;
		if (data.length < HEAD.length) {

			return start;
		}
		for (int i = 0; i <= data.length - HEAD.length; i++) {

			byte[] sec = Arrays.copyOfRange(data, i, i + HEAD.length);
			if (Arrays.equals(sec, HEAD)) {

				start = i;
//				return start;
				break;
			}

		}
		return start;
	}

	public static String printList(List<Byte> list) {

		StringBuilder buff = new StringBuilder();
		for (Byte b : list) {

			String str = Integer.toHexString(ProtocolUtil.getUnsignedByte(b));
			if (str.length() < 2)
				str = "0" + str;
			else
				str = str.substring(str.length() - 2);
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();

	}

}
