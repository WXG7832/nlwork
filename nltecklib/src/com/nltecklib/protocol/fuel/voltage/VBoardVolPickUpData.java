package com.nltecklib.protocol.fuel.voltage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.BoardNoSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VBoardWorkMode;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 电压采集板，电压采集数据（01）
 * 
 * @author caichao_tang
 *
 */
public class VBoardVolPickUpData extends Data implements BoardNoSupportable, Responsable, Queryable {
    private VBoardWorkMode workMode = VBoardWorkMode.AWAIT;
    private boolean isException;
    private List<Double> chnVoltageList = new ArrayList<>();// 0.01mV

    public VBoardWorkMode getWorkMode() {
	return workMode;
    }

    public void setWorkMode(VBoardWorkMode workMode) {
	this.workMode = workMode;
    }

    public boolean isException() {
	return isException;
    }

    public void setException(boolean isException) {
	this.isException = isException;
    }

    public List<Double> getChnVoltageList() {
	return chnVoltageList;
    }

    public void setChnVoltageList(List<Double> chnVoltageList) {
	this.chnVoltageList = chnVoltageList;
    }

    @Override
    public void encode() {
	data.add((byte) workMode.ordinal());
	data.add((byte) (isException ? 1 : 0));
	data.add((byte) chnVoltageList.size());
	for (double chnlVoltage : chnVoltageList) {
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnlVoltage * 100), 4, true)));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	chnVoltageList.clear();
	int index = 0;
	data = encodeData;
	workMode = VBoardWorkMode.values()[data.get(index++)];
	isException = data.get(index++) == 1 ? true : false;
	int chnNum = ProtocolUtil.getUnsignedByte(data.get(index++));

	for (int i = 0; i < chnNum; i++) {
	    double chnVol = ProtocolUtil.composeSpecialMinus(data.subList(index, index += 4).toArray(new Byte[0]), true) / 100.0;
	    chnVoltageList.add(chnVol);
	}
    }

    @Override
    public Code getCode() {
	return VolCode.VOL_PICKUP_CODE;
    }

	@Override
	public String toString() {
		return "VBoardVolPickUpData [workMode=" + workMode + ", isException=" + isException + ", chnVoltageList="
				+ chnVoltageList + "]";
	}



}
