package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;


/**
 * 电源故障状态查询
 * 
 * @author Administrator
 *
 */
public class PowerStateQueryData extends Data implements Queryable, Responsable {

	private final static int CHARGE_POWER_BYTE_SIZE = 2;
	private final static int AUXILIARY_POWER_BYTE_SIZE = 1;

	// 逆变电源状态列表
	private List<Byte> chargePowerStates = new ArrayList<Byte>(CHARGE_POWER_BYTE_SIZE);
	private List<Byte> chargePowerSwitches = new ArrayList<Byte>(CHARGE_POWER_BYTE_SIZE);
	// 辅助电源状态列表
	private List<Byte> auxiliariePowerStates = new ArrayList<Byte>(AUXILIARY_POWER_BYTE_SIZE);
	private List<Byte> auxiliariePowerSwitches = new ArrayList<Byte>(AUXILIARY_POWER_BYTE_SIZE);

	private int chargePowerCount;
	private int auxiliaryPowerCount;

	public PowerStateQueryData() {

	}
   /**
    * 构造初始化，所有通道关闭，默认为都正常
    * @param chargePowerCount
    * @param auxiliaryPowerCount
    */
	public PowerStateQueryData(int chargePowerCount, int auxiliaryPowerCount) {

		for (int n = 0; n < CHARGE_POWER_BYTE_SIZE; n++) {

			chargePowerStates.add((byte) 0);
			chargePowerSwitches.add((byte) 0);
		}
		this.chargePowerCount = chargePowerCount;
		
		for (int n = 0 ; n < AUXILIARY_POWER_BYTE_SIZE ; n++) {
			
			auxiliariePowerStates.add((byte) 0);
			auxiliariePowerSwitches.add((byte) 0);
		}
		this.auxiliaryPowerCount = auxiliaryPowerCount;
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

		chargePowerSwitches.addAll(new ArrayList<Byte>(data.subList(index, index + CHARGE_POWER_BYTE_SIZE)));
		index += CHARGE_POWER_BYTE_SIZE;
		chargePowerStates.addAll(new ArrayList<Byte>(data.subList(index, index + CHARGE_POWER_BYTE_SIZE)));
		index += CHARGE_POWER_BYTE_SIZE;

		auxiliaryPowerCount = ProtocolUtil.getUnsignedByte(data.get(index++));

		auxiliariePowerSwitches.addAll(new ArrayList<Byte>(data.subList(index, index + AUXILIARY_POWER_BYTE_SIZE)));
		index += AUXILIARY_POWER_BYTE_SIZE;
		auxiliariePowerStates.addAll(new ArrayList<Byte>(data.subList(index, index + AUXILIARY_POWER_BYTE_SIZE)));
		index += AUXILIARY_POWER_BYTE_SIZE;
	}

	@Override
	public Code getCode() {

		return AccessoryCode.PowerStateCode;
	}

	public void setChargePowerStates(List<Byte> chargePowerStates) {
		this.chargePowerStates = chargePowerStates;
	}

	public void setChargePowerSwitches(List<Byte> chargePowerSwitches) {
		this.chargePowerSwitches = chargePowerSwitches;
	}

	public void setAuxiliariePowerStates(List<Byte> auxiliariePowerStates) {
		this.auxiliariePowerStates = auxiliariePowerStates;
	}

	public void setAuxiliariePowerSwitches(List<Byte> auxiliariePowerSwitches) {
		this.auxiliariePowerSwitches = auxiliariePowerSwitches;
	}

	public void setAuxiliaryPowerCount(int auxiliaryPowerCount) {
		this.auxiliaryPowerCount = auxiliaryPowerCount;
	}

	public List<Byte> getChargePowerStates() {
		return chargePowerStates;
	}

	public List<Byte> getAuxiliariePowerStates() {
		return auxiliariePowerStates;
	}

	public List<Byte> getAuxiliariePowerSwitches() {
		return auxiliariePowerSwitches;
	}

	public int getAuxiliaryPowerCount() {
		return auxiliaryPowerCount;
	}

	public int getChargePowerCount() {
		return chargePowerCount;
	}

	public void setChargePowerCount(int chargePowerCount) {
		this.chargePowerCount = chargePowerCount;
	}

	public int getAuxiliaryCount() {
		return auxiliaryPowerCount;
	}

	public void setAuxiliaryCount(int auxiliaryCount) {
		this.auxiliaryPowerCount = auxiliaryCount;
	}

	public List<Byte> getChargePowerSwitches() {
		return chargePowerSwitches;
	}

}
