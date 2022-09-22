package com.nltecklib.protocol.lab.accessory;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.RunState;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电源开关
 * 
 * @author Administrator
 *
 */
public class PowerSwitchData extends Data implements Configable, Queryable, Responsable {

	// 开关组号：0xff表示所有电源,使用mainIndex位置
	// private int group = 0xff;
	// 电源类型：0 充放电源 1辅助电源，使用channel位置

    private PowerType powerType = PowerType.CHARGE;
	private RunState powerState = RunState.OFF;

	public PowerSwitchData() {
		setGroup(0xFF);
	}
	
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		// 开关组号
		// data.add((byte) group);
		// 电源类型
		// data.add((byte) powerType.ordinal());
		// 开关状态
		data.add((byte) powerState.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		// 开关组号
		// group = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 电源类型
//		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
//		if (code > PowerType.values().length - 1) {
//			
//			throw new RuntimeException("error power type code :" + code);
//		}
//		powerType = PowerType.values()[code];

		// 开关状态
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > RunState.values().length - 1) {

			throw new RuntimeException("error power switch code :" + code);
		}
		powerState = RunState.values()[code];
	}

	@Override
	public Code getCode() {
		return AccessoryCode.PowerSwitchCode;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}
	// 开关组号：0xff表示所有电源,使用mainIndex位置
	public int getGroup() {
		// return group;
		return mainIndex;
	}

	public void setGroup(int group) {
		// this.group = group;
		this.mainIndex = group;
	}
	
	// 电源类型：0 充放电源 1辅助电源，使用channel位置
	public PowerType getPowerType() {
		return PowerType.values()[chnIndex];
	}

	public void setPowerType(PowerType powerType) {
		chnIndex = powerType.ordinal();
	}

	public RunState getPowerState() {
		return powerState;
	}

	public void setPowerState(RunState powerState) {
		this.powerState = powerState;
	}

	@Override
	public String toString() {
		return "PowerSwitchData [powerState=" + powerState + "]";
	}
	
	

}
