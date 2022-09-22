package com.nltecklib.protocol.lab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Orient;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.CRC16;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控协议核心实体对象,用于编码和解码
 * 
 * @author Administrator
 *
 */

public class Entity implements NlteckPackageFactory {

    // 子板通信协议头（同步通信）
    public static final byte HEAD = 0x68;

    public static final byte TRAIL = (byte) 0x86;
    public static final int MIN_DECODE_LEN = 6;
    public static final int SHELL_LEN = 9;

    public enum ProtocolType {

		MAIN(0), PICKUP(1),BACKUP(2), ACCESSORY(4), CALBOARD(5) , MBCALBOARD(6) , SCREEN(7),DiapTest(0xF0), ADCALBOARD(8);

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

	int start = 0, end = 0;
	for (int i = 0; i < data.size(); i++) {

	    start = i;
	    if (data.get(i) == HEAD) { // 找协议头

		break;
	    }

	}

	for (int i = data.size() - 1; i >= 0; i--) {

	    end = i;
	    if (data.get(i) == TRAIL) { // 找协议头

		break;
	    }

	}

	// 找到
	if (start > 0 || end < data.size() - 1) {
	    data = data.subList(start, end + 1);
	}

	// 检查协议头
	if (data.get(0) != HEAD)
	    throw new RuntimeException("error head code:" + printList(data));
	// 检查协议类型
	int type = ProtocolUtil.getUnsignedByte(data.get(1));

	if (ProtocolType.valueOf(type) == null) {

	    throw new RuntimeException("error protocol type code:" + printList(data));
	}

	int code = ProtocolUtil.getUnsignedByte(data.get(2));
	// 解析发送模式方向
	Orient orient = Orient.values()[data.get(3)];
	// 解析数据区长度
	Byte[] lenArray = new Byte[] { data.get(4), data.get(5) };
	long len = ProtocolUtil.compose(lenArray, true);
	if (len + 9 != data.size()) {

	    throw new RuntimeException("error protocol length:" + data.size() + "," + printList(data));
	}
	// 检查CRC16校验码是否正确
	int crcCalc = CRC16.calcCrc16(data.subList(0, data.size() - 3).toArray(new Byte[0]));
	Byte[] crcArray = new Byte[] { data.get(data.size() - 3), data.get(data.size() - 2) };
	long crcRecv = ProtocolUtil.compose(crcArray, true);

	if (crcRecv != crcCalc) {

	    throw new RuntimeException("calculate CRC16=" + crcCalc + ",recv CRC16=" + crcRecv + ",CRC16 ERROR:" + printList(data));
	}
	// 检查协议尾
	if (data.get(data.size() - 1) != TRAIL)
	    throw new RuntimeException("error trail code :" + printList(data));

	// 解析数据
	Decorator dataSec = null;

	// dataSec = DataFactory.createData(orient, code);
	if (ProtocolType.valueOf(type) == ProtocolType.MAIN) {

	    dataSec = DataFactory.createMainData(orient, MainCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.PICKUP) {

	    dataSec = DataFactory.createPickupData(orient, ChipPickupCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.ACCESSORY) {

	    dataSec = DataFactory.createAccessoryData(orient, AccessoryCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.CALBOARD) {

	    dataSec = DataFactory.createCalBoardData(orient, CalCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.BACKUP) {

	    dataSec = DataFactory.createBackupBoardData(orient, BackupCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.MBCALBOARD) {

	    dataSec = DataFactory.createMbCalBoardData(orient, MbCalCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.SCREEN) {

	    dataSec = DataFactory.createScreenData(orient, ScreenCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.ADCALBOARD) {

	    dataSec = DataFactory.createADCalBoardData(orient, ADCalCode.valueOf(code));
	}
		else if (ProtocolType.valueOf(type) == ProtocolType.DiapTest) {
			dataSec = DataFactory.createDiapTestData(orient, DiapTestCode.valueOf(code));
		}

	dataSec.decode(new LinkedList<Byte>(data.subList(6, data.size() - 3)));
	return dataSec;
    }

    public static ProtocolType convertFromCode(Code code) {

	if (code instanceof MainCode) {

	    return ProtocolType.MAIN;
	}
	if (code instanceof ChipPickupCode) {

	    return ProtocolType.PICKUP;
	}
	if (code instanceof AccessoryCode) {

	    return ProtocolType.ACCESSORY;
	}
	if (code instanceof CalCode) {

	    return ProtocolType.CALBOARD;
	}
	if (code instanceof BackupCode) {

	    return ProtocolType.BACKUP;
	}
	if (code instanceof MbCalCode) {

	    return ProtocolType.MBCALBOARD;
	}
	if (code instanceof ScreenCode) {

	    return ProtocolType.SCREEN;
	}
		if (code instanceof DiapTestCode) {
			
			return ProtocolType.DiapTest;
		}
			if (code instanceof ADCalCode) {


	    return ProtocolType.ADCALBOARD;
	}

	return null;
    }

    public static List<Byte> encode(Decorator dec) throws Exception {

	dec.encode();
	List<Byte> buff = new ArrayList<Byte>();
	buff.add(HEAD);
	ProtocolType pt = convertFromCode(dec.getCode());
	if (pt == null) {

	    throw new Exception("invalid protocol type : " + dec.getCode());
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
	return buff;
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

    @Override
    public NlteckIOPackage decode(byte[] data) {

	// 获取界定点
	int index = findHeadPos(data);
	int len = getPackLen(data);
	List<Byte> list = ProtocolUtil.convertArrayToList(data);
	list = list.subList(index, index + len);

	// 检查协议头
		if (list.get(0) != HEAD) {
	    throw new RuntimeException("error head code:" + printList(list));
		}
	// 检查协议类型
	int type = ProtocolUtil.getUnsignedByte(list.get(1));

	ProtocolType pt = null;
	if ((pt = ProtocolType.valueOf(type)) == null) {

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

	    throw new RuntimeException("calculate CRC16=" + crcCalc + ",recv CRC16=" + crcRecv + ",CRC16 ERROR:" + printList(list));
	}
	// 检查协议尾
		if (list.get(list.size() - 1) != TRAIL) {
	    throw new RuntimeException("error trail code :" + printList(list));
		}

	// 解析数据
	Decorator dataSec = null;

	// dataSec = DataFactory.createData(orient, code);
	if (ProtocolType.valueOf(type) == ProtocolType.MAIN) {

	    dataSec = DataFactory.createMainData(orient, MainCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.PICKUP) {

	    dataSec = DataFactory.createPickupData(orient, ChipPickupCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.ACCESSORY) {

	    dataSec = DataFactory.createAccessoryData(orient, AccessoryCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.CALBOARD) {

	    dataSec = DataFactory.createCalBoardData(orient, CalCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.BACKUP) {

	    dataSec = DataFactory.createBackupBoardData(orient, BackupCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.MBCALBOARD) {

	    dataSec = DataFactory.createMbCalBoardData(orient, MbCalCode.valueOf(code));
	} else if (ProtocolType.valueOf(type) == ProtocolType.SCREEN) {

	    dataSec = DataFactory.createScreenData(orient, ScreenCode.valueOf(code));
		}else if(ProtocolType.valueOf(type) == ProtocolType.DiapTest) {
			
			dataSec = DataFactory.createDiapTestData(orient, DiapTestCode.valueOf(code));
	}else if (ProtocolType.valueOf(type) == ProtocolType.ADCALBOARD) {

	    dataSec = DataFactory.createADCalBoardData(orient, ADCalCode.valueOf(code));
	}
	dataSec.decode(new LinkedList<Byte>(list.subList(MIN_DECODE_LEN, list.size() - 3)));
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

	int bytesLen = 2; // 默认两字节
//		if(pt == ProtocolType.MAIN) {
//			
//			//上位机和主控用到json通信，需要扩充字节数
//			bytesLen = 4; 
//		}

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
