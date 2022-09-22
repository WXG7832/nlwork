package com.nltecklib.protocol.fuel.protect;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.HardwareState;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;

/**
 * 保护板状态读取协议数据
 * 
 * @author caichao_tang
 *
 */
public class PBoardStateReadData extends Data implements Configable, Responsable, Queryable {
    /**
     * 急停
     */
    private HardwareState scram = HardwareState.NORMAL;
    /**
     * 加热死锁
     */
    private HardwareState warmLock = HardwareState.NORMAL;
    /**
     * 气体死锁
     */
    private HardwareState gasLock = HardwareState.NORMAL;
    /**
     * 气体死锁
     */
    private HardwareState smokeWarning = HardwareState.NORMAL;

    public HardwareState getScram() {
	return scram;
    }

    public void setScram(HardwareState scram) {
	this.scram = scram;
    }

    public HardwareState getWarmLock() {
	return warmLock;
    }

    public void setWarmLock(HardwareState warmLock) {
	this.warmLock = warmLock;
    }

    public HardwareState getGasLock() {
	return gasLock;
    }

    public void setGasLock(HardwareState gasLock) {
	this.gasLock = gasLock;
    }

    public HardwareState getSmokeWarning() {
	return smokeWarning;
    }

    public void setSmokeWarning(HardwareState smokeWarning) {
	this.smokeWarning = smokeWarning;
    }

    @Override
    public void encode() {
	data.add((byte) scram.ordinal());
	data.add((byte) warmLock.ordinal());
	data.add((byte) gasLock.ordinal());
	data.add((byte) smokeWarning.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	scram = HardwareState.values()[data.get(index++)];
	warmLock = HardwareState.values()[data.get(index++)];
	gasLock = HardwareState.values()[data.get(index++)];
	smokeWarning = HardwareState.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return ProtectCode.STATE_READ_CODE;
    }

    @Override
	public String toString() {
		return "PBoardStateReadData [scram=" + scram + ", warmLock=" + warmLock + ", gasLock=" + gasLock
				+ ", smokeWarning=" + smokeWarning + "]";
	}

}
