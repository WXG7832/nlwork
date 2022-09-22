package com.nltecklib.protocol.camera;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.LogUtil;

/**
 * 主控协议核心实体对象,用于编码和解码
 * 
 * @author Administrator
 *
 */

public class Entity implements NlteckPackageFactory {
	private static final int MIN_LENGTH = 1;
	public static final int IMAGECOUNT = 692;
	public static final int TEMPCOUNT = 17;
	public static final int TEMPRETURNCOUNT = 2049;
	@Override
	public NlteckIOPackage decode(byte[] data) {
		// 获取界定点
		int index = findHeadPos(data);
		int len = getPackLen(data);
		List<Byte> list = ProtocolUtil.convertArrayToList(data);
		list = list.subList(index, index + len);
		// 检查协议类型
		int type = ProtocolUtil.getUnsignedByte(list.get(0));
		if (CameraCode.getCode(type) == null) {

			throw new RuntimeException("error protocol type code:" + LogUtil.printList(list));
		}
		Decode decode = DataFactory.createData(CameraCode.getCode(type));
		decode.decode(new LinkedList<Byte>(list.subList(MIN_LENGTH, list.size())));
		return (NlteckIOPackage) decode;
	}

	@Override
	public byte[] encode(NlteckIOPackage pack) {
		Data data = (Data) pack;
		if (data instanceof Encode) {			
			((Encode) data).encode();
		}
		List<Byte> buff = new ArrayList<Byte>();
		buff.add((byte) data.getCode().getCode());
		buff.addAll(data.getEncodeData());
		return ProtocolUtil.convertListToArray(buff);
	}

	@Override
	public int getMinDecodeLen() {
		return MIN_LENGTH;
	}

	@Override
	public int getPackLen(byte[] data) {
		int index = findHeadPos(data);
		if (data.length - index < getMinDecodeLen()) {

			throw new RuntimeException("can not decode data with short of data length");
		}
		if (data[index] == (byte)CameraCode.IMAGE.getCode()) {
			return IMAGECOUNT; 
		}
		if (data[index] == (byte)CameraCode.TEMP_COUNT.getCode()) {
			return TEMPCOUNT; 
		}
		if (data[index] == (byte)CameraCode.TEMP_RETURN.getCode()) {
			return TEMPRETURNCOUNT; 
		}
		if (data[index] == (byte)CameraCode.HEART_RETURN.getCode()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int findHeadPos(byte[] data) {
		int start = -1;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == (byte)CameraCode.IMAGE.getCode() || data[i] == (byte)CameraCode.TEMP_COUNT.getCode()
					|| data[i] == (byte)CameraCode.TEMP_RETURN.getCode() || data[i] == (byte)CameraCode.HEART_RETURN.getCode()) { // 找协议头
				start = i;
				break;
			}
		}
		return start;
	}

}
