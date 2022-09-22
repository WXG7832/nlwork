package com.nltecklib.protocol.li.PCWorkform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月30日 上午10:56:37 类说明
 */
public class ModeSwitchData extends Data implements Configable, Queryable, Responsable {

	private CalibrateCoreWorkMode mode;

	public enum CalibrateCoreWorkMode {
		NONE, MATCH, CAL
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	public CalibrateCoreWorkMode getMode() {
		return mode;
	}

	public void setMode(CalibrateCoreWorkMode mode) {
		this.mode = mode;
	}

	@Override
	public void encode() {

		data.add((byte) (mode.ordinal()));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CalibrateCoreWorkMode.values().length - 1) {
			throw new RuntimeException("CalibrateCoreWorkMode code error :" + code);
		}
		mode = CalibrateCoreWorkMode.values()[code];
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.ModeSwitchCode;
	}

	@Override
	public String toString() {
		return "ModeSwitchData [mode=" + mode + "]";
	}

}
