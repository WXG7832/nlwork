package com.nltecklib.protocol.fuel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.Orient;
import com.nltecklib.protocol.fuel.Environment.ProtocolType;
import com.nltecklib.protocol.fuel.alert.AlertEnviroment.AlertCode;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardFunctionCode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.CRC16;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.LogUtil;

/**
 * 燃料电池协议报文实体
 * 
 * @author caichao_tang
 *
 */
public class Entity implements NlteckPackageFactory {
	/**
	 * 协议头
	 */
	private static final Byte[] HEAD = new Byte[] { 0x18, (byte) 0xFE, (byte) 0xFE, (byte) 0xEE };
	/**
	 * 协议尾
	 */
	private static final Byte[] FOOT = new Byte[] { 0x16, (byte) 0xFE };
	/**
	 * 最小长度
	 */
	private static final int MIN_LENGTH = 9;
	/**
	 * 协议尾长度
	 */
	private static final int LAST_LENGTH = 4;
	/**
	 * 日志对象
	 */
	private static final Logger logger = LogUtil.getLogger("protocol");

	@Override
	public Decorator decode(byte[] data) {

		String log = "  <--  :" + LogUtil.printArray(data);
		logger.info(log);

		// 获取界定点
		int index = findHeadPos(data);
		int len = getPackLen(data);
		List<Byte> list = ProtocolUtil.convertArrayToList(data);
		list = list.subList(index, index + len);

		int i = 0;

		// 检查协议头
		if (list.get(i++) != HEAD[0] | list.get(i++) != HEAD[1] | list.get(i++) != HEAD[2] | list.get(i++) != HEAD[3])
			throw new RuntimeException("error head code:" + LogUtil.printList(list));
		// 检查协议类型
		int type = ProtocolUtil.getUnsignedByte(list.get(i++));

		if (ProtocolType.getCode(type) == null) {

			throw new RuntimeException("error protocol type code:" + LogUtil.printList(list));
		}
		// 功能码
		int code = ProtocolUtil.getUnsignedByte(list.get(i++));
		// 编号
		// Byte[] numArray = new Byte[] {list.get(i++),list.get(i++)};
		// int number = (int) ProtocolUtil.compose(numArray, true);

		// 解析发送模式方向
		Orient orient = Orient.values()[list.get(i++)];

		// 检查CRC16校验码是否正确
		int crcCalc = CRC16.calcCrc16(list.subList(0, list.size() - LAST_LENGTH).toArray(new Byte[0]));
		Byte[] crcArray = new Byte[] { list.get(list.size() - LAST_LENGTH), list.get(list.size() - LAST_LENGTH + 1) };
		long crcRecv = ProtocolUtil.compose(crcArray, true);

		if (crcRecv != crcCalc) {

			throw new RuntimeException(String.format("calculate CRC16 = 0x%04X,recv CRC16 = 0x%04X,CRC16 ERROR:%s",
					crcCalc, crcRecv, LogUtil.printList(list)));
			// "calculate CRC16=" + crcCalc + ",recv CRC16=" + crcRecv + ",CRC16 ERROR:" +
			// LogUtil.printList(list));
		}

		// 检查协议尾
		if (list.get(list.size() - LAST_LENGTH + 2) != FOOT[0] || list.get(list.size() - LAST_LENGTH + 3) != FOOT[1])
			throw new RuntimeException("error trail code :" + LogUtil.printList(list));

		// 解析数据
		Decorator dataSec = DataFactory.createData(orient, ProtocolType.getCode(type), code);

		dataSec.decode(new LinkedList<Byte>(list.subList(MIN_LENGTH, list.size() - LAST_LENGTH)));

		return dataSec;
	}

	@Override
	public byte[] encode(NlteckIOPackage pack) {

		Decorator dec = (Decorator) pack;
		dec.encode();
		List<Byte> buff = new ArrayList<Byte>();

		buff.addAll(Arrays.asList(HEAD));

		ProtocolType pt = convertFromCode(dec.getCode());
		if (pt == null) {

			throw new RuntimeException("invalid protocol type : " + dec.getCode());
		}
		buff.add((byte) pt.getCode());

		buff.add((byte) dec.getCode().getCode());

		// int number = dec.getNumber();
		// Byte[] numData = ProtocolUtil.split(number, 2, true);
		// buff.addAll(Arrays.asList(numData)); // 编码

		buff.add((byte) dec.getOrient().ordinal());
		// 获得数据区长度协议报文
		Byte[] lenData = ProtocolUtil.split(dec.getDestData().getLength(), 2, true);
		buff.addAll(Arrays.asList(lenData));
		// 在协议报文中放入数据区内容
		List<Byte> dataByteList = dec.getDestData().getEncodeData();
		buff.addAll(dataByteList);

		int crc = CRC16.calcCrc16(buff.toArray(new Byte[0]));
		Byte[] crcData = ProtocolUtil.split(crc, 2, true);
		buff.addAll(Arrays.asList(crcData)); // crc

		buff.addAll(Arrays.asList(FOOT));

		String log = "  -->  :" + LogUtil.printArray(ProtocolUtil.convertListToArray(buff));
		logger.info(log);
		return ProtocolUtil.convertListToArray(buff);
	}

	/**
	 * 根据协议的功能码获得协议类型码
	 * 
	 * @param code 协议的功能码
	 * @return 协议类型码
	 */
	public static ProtocolType convertFromCode(Code code) {

		if (code instanceof MainCode) {
			return ProtocolType.MAIN;
		}
		if (code instanceof VolCode) {
			return ProtocolType.VOL;
		}
		if (code instanceof TempCode) {
			return ProtocolType.TEMP;
		}
		if (code instanceof FlowCode) {
			return ProtocolType.FLOW;
		}
		if (code instanceof ControlCode) {
			return ProtocolType.CONTROL;
		}
		if (code instanceof AlertCode) {
			return ProtocolType.ALERT;
		}
		if (code instanceof ProtectCode) {
			return ProtocolType.PROTECT;
		}
		if (code instanceof HeatConductBoardFunctionCode) {
			return ProtocolType.HEATCONDUCT;
		}
		return null;
	}

	@Override
	public int getMinDecodeLen() {
		return MIN_LENGTH;
	}

	@Override
	public int getPackLen(byte[] data) {
		int index = 0;// findHeadPos(data);

//		if (index == -1) {
//			throw new RuntimeException("can not decode data with short of data length");
//		}

		if (data.length - index < getMinDecodeLen()) {

			throw new RuntimeException("can not decode data with short of data length");
		}
		if (data[index] != HEAD[0] || data[index + 1] != HEAD[1] || data[index + 2] != HEAD[2]
				|| data[index + 3] != HEAD[3]) {

			throw new RuntimeException("error protocol head :" + data[0]);
		}
		int dataLen = (int) ProtocolUtil.compose(new byte[] { data[index + 7], data[index + 8] }, true);
		return MIN_LENGTH + dataLen + LAST_LENGTH;
	}

	@Override
	public int findHeadPos(byte[] data) {
		int start = -1;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == HEAD[0] && data[i + 1] == HEAD[1] && data[i + 2] == HEAD[2] && data[i + 3] == HEAD[3]) { // 找协议头
				start = i;
				break;
			}
		}
		return start;
	}

	public String printList(List<Byte> list) {

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

	public String printBytes(byte[] bs) {

		StringBuilder buff = new StringBuilder();
		for (Byte b : bs) {

			String str = Integer.toHexString(ProtocolUtil.getUnsignedByte(b));
			if (str.length() < 2) {
				str = "0" + str;
			} else {
				str = str.substring(str.length() - 2);
			}
			buff.append(str + " ");
		}

		return buff.toString().toUpperCase();
	}

	public static void main(String[] args) {
		String txt = "18 FE FE EE 06 07 02 00 01 01 94 4C 16 FE";
		txt = txt.replace(" ", "");
		int size = txt.length() / 2;
		byte[] list = new byte[size];
		for (int i = 0; i < size; i++) {
			list[i] = (byte) ((Character.digit(txt.charAt(2 * i), 16) << 4)
					| Character.digit(txt.charAt(2 * i + 1), 16));
		}

		Data da = (Data) new Entity().decode(list).getDestData();
		System.out.println(da);
	}

}
