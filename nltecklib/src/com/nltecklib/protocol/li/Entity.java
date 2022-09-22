package com.nltecklib.protocol.li;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.li.calTools.checkDriver.CalToolsCheckDriverEnvironment.CalToolsCheckDriverCode;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.li.calTools.logicDriver.CalToolsLogicDriverEnvironment.CalToolsLogicDriverCode;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestEnvironment.CalToolsTestCode;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.curr200.DriverMultiDiapTestData.DiapTestModel;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.DriverG0Code;
import com.nltecklib.protocol.li.goldFinger.GoldEnvironment.GoldCode;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.CalBoardTestCode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.DriverTestCode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.CRC16;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控协议核心实体对象,用于编码和解码
 * 
 * @author Administrator
 *
 */

public class Entity implements NlteckPackageFactory {

	private static boolean printProtocol = false;

	public static final byte HEAD = 0x18;
	public static final byte TRAIL = 0X16;
	public static final int MIN_DECODE_LEN = 6;
	public static final int SHELL_LEN = 9;

	public enum ProtocolType {

		LOGIC(0), CHECK(1), CAL(2), MAIN(4), GDF(5), WORKFORM(7), ACCESSORY(0X10), LOGIC2(0x20), CHECK2(0x21),
		MBWorkform(0x22), PCWorkform(0x23), DRIVER(0xA0), DriverG0(0xA1), LogicCalTools(0x30), CheckCalTools(0x31), TestTools(0x32),
		DriverTest(0x33), LogicDriverCalTools(0xb0), CheckDriverCalTools(0xb1),DiapTest(0xF0),CalibrationBoard(0xF1);

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

//	public static Decorator decode2(List<Byte> data) {
//
//		int start = 0, end = 0;
//		for (int i = 0; i < data.size(); i++) {
//
//			start = i;
//			if (data.get(i) == HEAD) { // 找协议头
//
//				break;
//			}
//
//		}
//
//		for (int i = data.size() - 1; i >= 0; i--) {
//
//			end = i;
//			if (data.get(i) == TRAIL) { // 找协议头
//
//				break;
//			}
//
//		}
//
//		// 找到
//		if (start > 0 || end < data.size() - 1) {
//			data = data.subList(start, end + 1);
//		}
//
//		// 检查协议头
//		if (data.get(0) != HEAD)
//			throw new RuntimeException("error head code:" + printList(data));
//		// 检查协议类型
//		int type = ProtocolUtil.getUnsignedByte(data.get(1));
//
//		if (ProtocolType.valueOf(type) == null) {
//
//			throw new RuntimeException("error protocol type code:" + printList(data));
//		}
//
//		int code = ProtocolUtil.getUnsignedByte(data.get(2));
//		// 解析发送模式方向
//		Orient orient = Orient.values()[data.get(3)];
//		// 解析数据区长度
//		Byte[] lenArray = new Byte[] { data.get(4), data.get(5) };
//		long len = ProtocolUtil.compose(lenArray, true);
//		if (len + 9 != data.size()) {
//
//			throw new RuntimeException("error protocol length:" + data.size() + "," + printList(data));
//		}
//		// 检查CRC16校验码是否正确
//		int crcCalc = CRC16.calcCrc16(data.subList(0, data.size() - 3).toArray(new Byte[0]));
//		Byte[] crcArray = new Byte[] { data.get(data.size() - 3), data.get(data.size() - 2) };
//		long crcRecv = ProtocolUtil.compose(crcArray, true);
//
//		if (crcRecv != crcCalc) {
//
////			throw new RuntimeException(
////					"calculate CRC16=" + crcCalc + ",recv CRC16=" + crcRecv + ",CRC16 ERROR:" + printList(data));
//			throw new RuntimeException(String.format("calculate CRC16=0x%04X,recv CRC16=0x%04X,CRC16 ERROR:%s", crcCalc,
//					crcRecv, printList(data)));
//		}
//		// 检查协议尾
//		if (data.get(data.size() - 1) != TRAIL)
//			throw new RuntimeException("error trail code :" + printList(data));
//
//		// 解析数据
//		Decorator dataSec = null;
//
//		// dataSec = DataFactory.createData(orient, code);
//		if (ProtocolType.valueOf(type) == ProtocolType.MAIN) {
//			dataSec = DataFactory.createMainData(orient, MainCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.GDF) {
//			// 金手指
//			dataSec = DataFactory.createGdFingerData(orient, GoldCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.LOGIC) {
//			dataSec = DataFactory.createLogicData(orient, LogicCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.CHECK) {
//			dataSec = DataFactory.createCheckData(orient, CheckCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.CAL) {
//			dataSec = DataFactory.createCalData(orient, CalCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.WORKFORM) {
//			dataSec = DataFactory.createWorkformData(orient, WorkformCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.ACCESSORY) {
//			dataSec = DataFactory.createAccessoryData(orient, AccessoryCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.LOGIC2) {
//			dataSec = DataFactory.createLogic2Data(orient, Logic2Code.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.DriverG0) {
//			dataSec = DataFactory.createDriverG0Data(orient, DriverG0Code.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.CHECK2) {
//			dataSec = DataFactory.createCheck2Data(orient, Check2Code.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.LogicCalTools) {
//			dataSec = DataFactory.createCalToolsLogicData(orient, CalToolsLogicCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.CheckCalTools) {
//			dataSec = DataFactory.createCalToolsCheckData(orient, CalToolsCheckCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.TestTools) {
//			dataSec = DataFactory.createCalToolsTestData(orient, CalToolsTestCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.LogicDriverCalTools) {
//			dataSec = DataFactory.createCalToolsLogicDriverData(orient, CalToolsLogicDriverCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.CheckDriverCalTools) {
//			dataSec = DataFactory.createCalToolsCheckDriverData(orient, CalToolsCheckDriverCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.PCWorkform) {
//			dataSec = DataFactory.createPCWorkformData(orient, PCWorkformCode.valueOf(code));
//		} else if (ProtocolType.valueOf(type) == ProtocolType.MBWorkform) {
//			dataSec = DataFactory.createMBWorkformData(orient, MBWorkformCode.valueOf(code));
//		}
//		dataSec.decode(new LinkedList<Byte>(data.subList(6, data.size() - 3)));
//		return dataSec;
//	}
	
	

	public static ProtocolType convertFromCode(Code code) {

		if (code instanceof MainCode) {

			return ProtocolType.MAIN;
		} else if (code instanceof GoldCode) {

			return ProtocolType.GDF;
		} else if (code instanceof LogicCode) {

			return ProtocolType.LOGIC;
		} else if (code instanceof CalCode) {

			return ProtocolType.CAL;
		} else if (code instanceof CheckCode) {

			return ProtocolType.CHECK;
		} else if (code instanceof WorkformCode) {

			return ProtocolType.WORKFORM;
		} else if (code instanceof AccessoryCode) {

			return ProtocolType.ACCESSORY;
		} else if (code instanceof Logic2Code) {

			return ProtocolType.LOGIC2;
		} else if (code instanceof DriverCode) {

			return ProtocolType.DRIVER;
		} else if (code instanceof DriverG0Code) {

			return ProtocolType.DriverG0;
		} else if (code instanceof Check2Code) {

			return ProtocolType.CHECK2;
		} else if (code instanceof CalToolsLogicCode) {

			return ProtocolType.LogicCalTools;
		} else if (code instanceof CalToolsCheckCode) {

			return ProtocolType.CheckCalTools;
		} else if (code instanceof CalToolsLogicDriverCode) {

			return ProtocolType.LogicDriverCalTools;
		} else if (code instanceof CalToolsCheckDriverCode) {

			return ProtocolType.CheckDriverCalTools;
		} else if (code instanceof PCWorkformCode) {

			return ProtocolType.PCWorkform;
		} else if (code instanceof MBWorkformCode) {

			return ProtocolType.MBWorkform;
		} else if (code instanceof CalToolsTestCode) {

			return ProtocolType.TestTools;
		} else if (code instanceof DriverTestCode) {

			return ProtocolType.DriverTest;
		}else if (code instanceof DiapTestCode) {

			return ProtocolType.DiapTest;
		}else if (code instanceof CalBoardTestCode) {

			return ProtocolType.CalibrationBoard;
		}
		

		return null;
	}

	public static void setPrintProtocol(boolean printProtocol) {
		Entity.printProtocol = printProtocol;
	}

	public static List<Byte> encode(Decorator dec) throws Exception {

		return ProtocolUtil.convertArrayToList(new Entity().encode((NlteckIOPackage) dec));

//		dec.encode();
//		List<Byte> buff = new ArrayList<Byte>();
//		buff.add(HEAD);
//		ProtocolType pt = convertFromCode(dec.getCode());
//		if (pt == null) {
//
//			throw new Exception("invalid protocol type : " + dec.getCode());
//		}
//		buff.add((byte) pt.getCode());
//		buff.add((byte) dec.getCode().getCode());
//		buff.add((byte) dec.getOrient().ordinal());
//		Byte[] lenData = ProtocolUtil.split(dec.getDestData().getLength(), 2, true);
//		buff.addAll(Arrays.asList(lenData)); // 数据长度
//		buff.addAll(dec.getDestData().getEncodeData());
//		int crc = CRC16.calcCrc16(buff.toArray(new Byte[0]));
//		Byte[] crcData = ProtocolUtil.split(crc, 2, true);
//		buff.addAll(Arrays.asList(crcData)); // 数据长度
//		buff.add(TRAIL);
//		return buff;
	}

	public static List<Byte> splitIp(String ip) {

		List<Byte> buff = new ArrayList<Byte>();
		String[] args = ip.split("\\.");
		for (int i = 0; i < args.length; i++) {

			buff.add((byte) Integer.parseInt(args[i]));
		}

		return buff;

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

//	@Override
//	public NlteckIOPackage decode(byte[] data) {
//		return decode(ProtocolUtil.convertArrayToList(data));
//	}

	@Override
	public NlteckIOPackage decode(byte[] data) {
		if (printProtocol) {
			if(!((data[1]==0x23&&data[2]==0x0D)||(data[1]==0x07&&data[2]==0x22))) {
				System.out.println("<--" + ProtocolUtil.printList(ProtocolUtil.convertArrayToList(data)));
			}
		}
		// 获取界定点
		int index = findHeadPos(data);
		int len = getPackLen(data);
		List<Byte> list = ProtocolUtil.convertArrayToList(data);
		list = list.subList(index, index + len);

		// 检查协议头
		if (list.get(0) != HEAD)
			throw new RuntimeException("error head code:" + printList(list));
		// 检查协议类型
		int type = ProtocolUtil.getUnsignedByte(list.get(1));

		if (ProtocolType.valueOf(type) == null) {

			throw new RuntimeException("error protocol type code:" + printList(list));
		}

		int code = ProtocolUtil.getUnsignedByte(list.get(2));
		// 解析发送模式方向
		Orient orient = Orient.values()[list.get(3)];

		// 检查CRC16校验码是否正确
		int crcCalc = CRC16.calcCrc16(list.subList(0, list.size() - 3).toArray(new Byte[0]));
		Byte[] crcArray = new Byte[] { list.get(list.size() - 3), list.get(list.size() - 2) };
		long crcRecv = ProtocolUtil.compose(crcArray, true);

		if (crcRecv != crcCalc) {

//			throw new RuntimeException(
//					"calculate CRC16=" + crcCalc + ",recv CRC16=" + crcRecv + ",CRC16 ERROR:" + printList(list));
			throw new RuntimeException(String.format("calculate CRC16=0x%04X,recv CRC16=0x%04X,CRC16 ERROR:%s", crcCalc,
					crcRecv, printList(list)));
		}
		// 检查协议尾
		if (list.get(list.size() - 1) != TRAIL)
			throw new RuntimeException("error trail code :" + printList(list));

		// 解析数据
		Data entityData = null;

		// dataSec = DataFactory.createData(orient, code);
		if (ProtocolType.valueOf(type) == ProtocolType.MAIN) {
			entityData = DataFactory.createMainData(orient, MainCode.valueOf(code));
			// 金手指
		} else if (ProtocolType.valueOf(type) == ProtocolType.GDF) {
			entityData = DataFactory.createGdFingerData(orient, GoldCode.valueOf(code));

		} else if (ProtocolType.valueOf(type) == ProtocolType.LOGIC) {
			entityData = DataFactory.createLogicData(orient, LogicCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.CHECK) {
			entityData = DataFactory.createCheckData(orient, CheckCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.CAL) {
			entityData = DataFactory.createCalData(orient, CalCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.WORKFORM) {
			entityData = DataFactory.createWorkformData(orient, WorkformCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.ACCESSORY) {
			entityData = DataFactory.createAccessoryData(orient, AccessoryCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.LOGIC2) {
			entityData = DataFactory.createLogic2Data(orient, Logic2Code.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.DRIVER) {
			entityData = DataFactory.createDriverData(orient, DriverCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.DriverG0) {
			entityData = DataFactory.createDriverG0Data(orient, DriverG0Code.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.CHECK2) {
			entityData = DataFactory.createCheck2Data(orient, Check2Code.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.LogicCalTools) {
			entityData = DataFactory.createCalToolsLogicData(orient, CalToolsLogicCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.CheckCalTools) {
			entityData = DataFactory.createCalToolsCheckData(orient, CalToolsCheckCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.LogicDriverCalTools) {
			entityData = DataFactory.createCalToolsLogicDriverData(orient, CalToolsLogicDriverCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.CheckDriverCalTools) {
			entityData = DataFactory.createCalToolsCheckDriverData(orient, CalToolsCheckDriverCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.PCWorkform) {
			entityData = DataFactory.createPCWorkformData(orient, PCWorkformCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.MBWorkform) {
			entityData = DataFactory.createMBWorkformData(orient, MBWorkformCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.TestTools) {
			entityData = DataFactory.createCalToolsTestData(orient, CalToolsTestCode.valueOf(code));
		} else if (ProtocolType.valueOf(type) == ProtocolType.DriverTest) {
			entityData = DataFactory.createDriverTestData(orient, DriverTestCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.DiapTest) {
			entityData = DataFactory.createDiapTestData(orient, DiapTestCode.valueOf(code));
		}else if (ProtocolType.valueOf(type) == ProtocolType.CalibrationBoard) {
			entityData = DataFactory.createCalBoardTestData(orient, CalBoardTestCode.valueOf(code));
		}

		Decorator dataSec = null;
		switch (orient) {
		case ALERT:
			dataSec = new AlertDecorator(entityData);
			break;
		case CONFIG:
			dataSec = new ConfigDecorator(entityData);
			break;
		case QUERY:
			dataSec = new QueryDecorator(entityData);
			break;
		case RESPONSE:
			dataSec = new ResponseDecorator(entityData, false);
			break;

		}

		if (dataSec == null) {
			throw new RuntimeException("unrecognized orient code:" + orient);
		}
		
		dataSec.decode(new LinkedList<Byte>(list.subList(MIN_DECODE_LEN, list.size() - (SHELL_LEN - MIN_DECODE_LEN))));
		return (NlteckIOPackage) dataSec;
	}

	@Override
	public byte[] encode(NlteckIOPackage pack) {

		Decorator dec = (Decorator) pack;
		dec.encode();
		List<Byte> buff = new ArrayList<Byte>();
		buff.add(HEAD);
		ProtocolType pt = convertFromCode(dec.getCode());
		if (pt == null) {

			throw new RuntimeException("invalid protocol type : " + dec.getCode());
		}
		buff.add((byte) pt.getCode());
		buff.add((byte) dec.getCode().getCode());
		buff.add((byte) dec.getOrient().ordinal());
		Byte[] lenData = ProtocolUtil.split(dec.getDestData().getLength(), 2, true);
		buff.addAll(Arrays.asList(lenData)); // 数据长度
		buff.addAll(dec.getDestData().getEncodeData());
		int crc = CRC16.calcCrc16(buff.toArray(new Byte[0]));
		Byte[] crcData = ProtocolUtil.split(crc, 2, true);
		buff.addAll(Arrays.asList(crcData)); // 数据长度
		buff.add(TRAIL);
		if (printProtocol) {
			if(!((buff.get(1)==0x23&&buff.get(2)==0x0D)||(buff.get(1)==0x07&&buff.get(2)==0x22))) {
				System.out.println("-->" + ProtocolUtil.printList(buff));
			}
		}
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
		if (data[index] != HEAD) {

			throw new RuntimeException("error protocol head :" + data[0]);
		}
		int dataLen = (int) ProtocolUtil.compose(new byte[] { data[index + 4], data[index + 5] }, true);
		return dataLen + SHELL_LEN;
	}

	@Override
	public int findHeadPos(byte[] data) {
		int start = -1;
		for (int i = 0; i < data.length; i++) {

			start = i;
			if (data[i] == HEAD) { // 找协议头

				break;
			}

		}
		return start;
	}

}
