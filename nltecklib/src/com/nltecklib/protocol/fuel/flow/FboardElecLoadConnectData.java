package com.nltecklib.protocol.fuel.flow;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * ±¨¾¯Çå³ý
 * @author guofang_ma
 *
 */
public class FboardElecLoadConnectData extends Data implements Configable, Responsable, Queryable {

	public final static int CONNECT_FLAG = 1;
	private int flag;

	public int getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * @see #CONNECT_FLAG
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	@Override
	public void encode() {
		data.add((byte) flag);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		return FlowCode.ELEC_LOAD_CONNECT_CODE;
	}

	@Override
	public String toString() {
		return "AlertClearData [flag=" + flag + "]";
	}

}
