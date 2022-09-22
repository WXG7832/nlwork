package com.nltecklib.protocol.li.driverG0;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.DriverG0Code;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.VoltChnSwitch;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 *    备份电压通道开关
 * @author admin
 *
 */
public class DriverG0BVoltChnSwitchData extends DriverG0Data  implements Configable, Responsable {

	
	private VoltChnSwitch chnSwitch = VoltChnSwitch.CLOSE;

	public static void main(String[] args) {
		new DriverG0BVoltChnSwitchData();
	}
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	public VoltChnSwitch getChnSwitch() {
		return chnSwitch;
	}

	public void setChnSwitch(VoltChnSwitch chnSwitch) {
		this.chnSwitch = chnSwitch;
	}
	
	@Override
	public void encode() {
		
		data.add((byte) driverIndex);
		data.add((byte)chnIndex);
		
		// 启动通道
		data.add((byte)chnSwitch.ordinal());
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		chnSwitch = VoltChnSwitch.values()[data.get(index)];
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return DriverG0Code.BackupVoltageChnSwitchCode;
	}

}
