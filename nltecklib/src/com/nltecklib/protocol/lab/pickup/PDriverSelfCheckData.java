package com.nltecklib.protocol.lab.pickup;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.CheckResult;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.Fan;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.FanType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 驱动板自检 0x24
 * @author: JenHoard_Shaw
 * @date: 创建时间：2022年7月29日 上午10:00:08
 *
 */
public class PDriverSelfCheckData extends Data implements Queryable, Responsable {

	private CheckResult driverSram;
	private CheckResult driverFlash;
	private CheckResult calParam;
	private CheckResult adPick;
	private CheckResult tempPick;
	private CheckResult checkboard;
	private boolean powerOk;

	private int dataSize;

	private List<Fan> fans = new ArrayList<>();

	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getDataSize() {

		return dataSize;

	}

	@Override
	public void encode() {

		data.add((byte) driverSram.ordinal());
		data.add((byte) driverFlash.ordinal());
		data.add((byte) calParam.ordinal());
		data.add((byte) adPick.ordinal());
		data.add((byte) tempPick.ordinal());
		data.add((byte) checkboard.ordinal());
		data.add((byte) (powerOk ? 0x00 : 0x01));
		data.add((byte) fans.size());
		for (int n = 0; n < fans.size(); n++) {

			data.add((byte) fans.get(n).fanType.ordinal());
			data.add((byte) (fans.get(n).stateOk ? 0x00 : 0x01));
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CheckResult.values().length - 1) {

			throw new RuntimeException("error sram code : " + code);
		}
		driverSram = CheckResult.values()[code];
		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CheckResult.values().length - 1) {

			throw new RuntimeException("error flash code : " + code);
		}
		driverFlash = CheckResult.values()[code];

		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CheckResult.values().length - 1) {

			throw new RuntimeException("error cal param code : " + code);
		}
		calParam = CheckResult.values()[code];

		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CheckResult.values().length - 1) {

			throw new RuntimeException("error ad pick code : " + code);
		}
		adPick = CheckResult.values()[code];

		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CheckResult.values().length - 1) {

			throw new RuntimeException("error temp pick code : " + code);
		}
		tempPick = CheckResult.values()[code];

		code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > CheckResult.values().length - 1) {

			throw new RuntimeException("error checkboard code : " + code);
		}
		checkboard = CheckResult.values()[code];

		powerOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;

		int fanCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		for (int n = 0; n < fanCount; n++) {

			Fan fan = new Fan();
			code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > FanType.values().length - 1) {

				throw new RuntimeException("error fan type code : " + code);
			}
			fan.fanType = FanType.values()[code];

			fan.stateOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;

			fans.add(fan);
		}

		dataSize = index;

	}

	@Override
	public Code getCode() {
		return ChipPickupCode.DriverSelfCheckCode;
	}

	public CheckResult getDriverSram() {
		return driverSram;
	}

	public void setDriverSram(CheckResult driverSram) {
		this.driverSram = driverSram;
	}

	public CheckResult getDriverFlash() {
		return driverFlash;
	}

	public void setDriverFlash(CheckResult driverFlash) {
		this.driverFlash = driverFlash;
	}

	public CheckResult getCalParam() {
		return calParam;
	}

	public void setCalParam(CheckResult calParam) {
		this.calParam = calParam;
	}

	public CheckResult getAdPick() {
		return adPick;
	}

	public void setAdPick(CheckResult adPick) {
		this.adPick = adPick;
	}

	public CheckResult getTempPick() {
		return tempPick;
	}

	public void setTempPick(CheckResult tempPick) {
		this.tempPick = tempPick;
	}

	public CheckResult getCheckboard() {
		return checkboard;
	}

	public void setCheckboard(CheckResult checkboard) {
		this.checkboard = checkboard;
	}

	public boolean isPowerOk() {
		return powerOk;
	}

	public void setPowerOk(boolean powerOk) {
		this.powerOk = powerOk;
	}

	public List<Fan> getFans() {
		return fans;
	}

	public void setFans(List<Fan> fans) {
		this.fans = fans;
	}

	

}
