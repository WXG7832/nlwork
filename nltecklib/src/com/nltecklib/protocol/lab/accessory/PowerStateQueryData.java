package com.nltecklib.protocol.lab.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电源工作状态
 * 
 * @author Administrator
 *
 */
public class PowerStateQueryData extends Data implements Queryable, Responsable {

//	private final static int CHARGE_POWER_SIZE = 16;
//	private final static int AUXILIARY_POWER_SIZE = 8;
//
//	private RunState[] chargePowerRunStates = new RunState[CHARGE_POWER_SIZE];// 充放电源运行状态标志
//	private WorkState[] chargePowerWorkStates = new WorkState[CHARGE_POWER_SIZE];// 充放电源故障状态标志
//
//	private RunState[] auxiliariePowerRunStates = new RunState[AUXILIARY_POWER_SIZE];// 辅助电源运行状态标志
//	private WorkState[] auxiliariePowerWorkStates = new WorkState[AUXILIARY_POWER_SIZE];// 辅助电源故障状态标志
//
//	private List<Byte> convertRunState(RunState[] runStates) {
//
//		int byteCount = runStates.length / 8 + (runStates.length % 8 > 0 ? 1 : 0);
//		for (int i = 0; i < byteCount; i++) {
//			byte val = 0;
//			for (int j = 0; j < runStates.length - i * 8 && j < 8; j++) {
//
//			}
//		}
//	}

	private final static int CHARGE_POWER_BYTE_SIZE = 2;
	private final static int AUXILIARY_POWER_BYTE_SIZE = 1;

	// 充放电源
	private int chargePowerCount;
	private List<Byte> chargePowerSwitches = new ArrayList<Byte>(CHARGE_POWER_BYTE_SIZE); // 运行状态
	private List<Byte> chargePowerStates = new ArrayList<Byte>(CHARGE_POWER_BYTE_SIZE); // 故障状态

	// 辅助电源
	private int auxiliaryPowerCount;
	private List<Byte> auxiliariePowerSwitches = new ArrayList<Byte>(AUXILIARY_POWER_BYTE_SIZE); // 运行状态
	private List<Byte> auxiliariePowerStates = new ArrayList<Byte>(AUXILIARY_POWER_BYTE_SIZE); // 故障状态

	@Override
	public boolean supportMain() {

		return false;
	}

	@Override
	public void encode() {

		data.add((byte) chargePowerCount);
		data.addAll(chargePowerSwitches.subList(0, CHARGE_POWER_BYTE_SIZE));
		data.addAll(chargePowerStates.subList(0, CHARGE_POWER_BYTE_SIZE));

		data.add((byte) auxiliaryPowerCount);
		data.addAll(auxiliariePowerSwitches.subList(0, AUXILIARY_POWER_BYTE_SIZE));
		data.addAll(auxiliariePowerStates.subList(0, AUXILIARY_POWER_BYTE_SIZE));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		chargePowerCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		chargePowerSwitches.clear();
		chargePowerSwitches.addAll(new ArrayList<Byte>(data.subList(index, index + CHARGE_POWER_BYTE_SIZE)));
		index += CHARGE_POWER_BYTE_SIZE;
		chargePowerStates.clear();
		chargePowerStates.addAll(new ArrayList<Byte>(data.subList(index, index + CHARGE_POWER_BYTE_SIZE)));
		index += CHARGE_POWER_BYTE_SIZE;

		auxiliaryPowerCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		auxiliariePowerSwitches.clear();
		auxiliariePowerSwitches.addAll(new ArrayList<Byte>(data.subList(index, index + AUXILIARY_POWER_BYTE_SIZE)));
		index += AUXILIARY_POWER_BYTE_SIZE;
		auxiliariePowerStates.clear();
		auxiliariePowerStates.addAll(new ArrayList<Byte>(data.subList(index, index + AUXILIARY_POWER_BYTE_SIZE)));
		index += AUXILIARY_POWER_BYTE_SIZE;
	}

	@Override
	public Code getCode() {
		return AccessoryCode.PowerStateCode;
	}

	/**
	 * 初始化时，所有电源开关为关闭，故障状态为正常
	 * 
	 * @param chargePowerCount
	 * @param auxiliaryPowerCount
	 */
	public PowerStateQueryData(int chargePowerCount, int auxiliaryPowerCount) {

		for (int n = 0; n < CHARGE_POWER_BYTE_SIZE; n++) {

			chargePowerStates.add((byte) 0);
			chargePowerSwitches.add((byte) 0);
		}
		this.chargePowerCount = chargePowerCount;

		for (int n = 0; n < AUXILIARY_POWER_BYTE_SIZE; n++) {

			auxiliariePowerStates.add((byte) 0);
			auxiliariePowerSwitches.add((byte) 0);
		}
		this.auxiliaryPowerCount = auxiliaryPowerCount;
	}

	public PowerStateQueryData() {

	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	public int getChargePowerCount() {
		return chargePowerCount;
	}

	public void setChargePowerCount(int chargePowerCount) {
		this.chargePowerCount = chargePowerCount;
	}

	public List<Byte> getChargePowerSwitches() {
		return chargePowerSwitches;
	}

	public void setChargePowerSwitches(List<Byte> chargePowerSwitches) {
		this.chargePowerSwitches = chargePowerSwitches;
	}

	public List<Byte> getChargePowerStates() {
		return chargePowerStates;
	}

	public void setChargePowerStates(List<Byte> chargePowerStates) {
		this.chargePowerStates = chargePowerStates;
	}

	public int getAuxiliaryPowerCount() {
		return auxiliaryPowerCount;
	}

	public void setAuxiliaryPowerCount(int auxiliaryPowerCount) {
		this.auxiliaryPowerCount = auxiliaryPowerCount;
	}

	public List<Byte> getAuxiliariePowerSwitches() {
		return auxiliariePowerSwitches;
	}

	public void setAuxiliariePowerSwitches(List<Byte> auxiliariePowerSwitches) {
		this.auxiliariePowerSwitches = auxiliariePowerSwitches;
	}

	public List<Byte> getAuxiliariePowerStates() {
		return auxiliariePowerStates;
	}

	public void setAuxiliariePowerStates(List<Byte> auxiliariePowerStates) {
		this.auxiliariePowerStates = auxiliariePowerStates;
	}

	@Override
	public String toString() {
		return "PowerStateQueryData [chargePowerCount=" + chargePowerCount + ", chargePowerSwitches="
				+ chargePowerSwitches + ", chargePowerStates=" + chargePowerStates + ", auxiliaryPowerCount="
				+ auxiliaryPowerCount + ", auxiliariePowerSwitches=" + auxiliariePowerSwitches
				+ ", auxiliariePowerStates=" + auxiliariePowerStates + "]";
	}
	
	

}
