/**
 * 
 */
package com.nltecklib.protocol.power.check;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.check.CheckEnvironment.SwitchState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 逆变开关启动停止功能码 0x03支持配置
 * @version: v1.0.0
 * @author: Admin
 * @date: 2021年12月29日 上午10:10:59
 *
 */
public class SwitchData extends Data implements Configable, Responsable {

	private SwitchState switchState;

	public SwitchState getSwitchState() {
		return switchState;
	}

	public void setSwitchState(SwitchState switchState) {
		this.switchState = switchState;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) switchState.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {
		// TODO Auto-generated method stub

		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > SwitchState.values().length - 1) {

			throw new RuntimeException("error switchState code : " + code);
		}
		switchState = SwitchState.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return CheckCode.SwitchCode;
	}

}
