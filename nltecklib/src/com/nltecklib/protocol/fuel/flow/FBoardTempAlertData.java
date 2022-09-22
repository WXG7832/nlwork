package com.nltecklib.protocol.fuel.flow;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class FBoardTempAlertData extends Data implements ComponentSupportable, Configable, Queryable, Responsable {
    private int tempAlertUp;
    private int tempAlertDown;
    private int tempStopUp;
    private int tempStopDown;

    public int getTempAlertUp() {
	return tempAlertUp;
    }

    public void setTempAlertUp(int tempAlertUp) {
	this.tempAlertUp = tempAlertUp;
    }

    public int getTempAlertDown() {
	return tempAlertDown;
    }

    public void setTempAlertDown(int tempAlertDown) {
	this.tempAlertDown = tempAlertDown;
    }

    public int getTempStopUp() {
	return tempStopUp;
    }

    public void setTempStopUp(int tempStopUp) {
	this.tempStopUp = tempStopUp;
    }

    public int getTempStopDown() {
	return tempStopDown;
    }

    public void setTempStopDown(int tempStopDown) {
	this.tempStopDown = tempStopDown;
    }

    @Override
    public void encode() {
	data.addAll(Arrays.asList(ProtocolUtil.split(tempAlertUp, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(tempAlertDown, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(tempStopUp, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split(tempStopDown, 2, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	tempAlertUp = (int) ProtocolUtil.compose(encodeData.subList(index, index += 2).toArray(new Byte[0]), true);
	tempAlertDown = (int) ProtocolUtil.compose(encodeData.subList(index, index += 2).toArray(new Byte[0]), true);
	tempStopUp = (int) ProtocolUtil.compose(encodeData.subList(index, index += 2).toArray(new Byte[0]), true);
	tempStopDown = (int) ProtocolUtil.compose(encodeData.subList(index, index += 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return FlowCode.TEMP_ALERT;
    }

    @Override
    public String toString() {
	return "FBoardTempAlertData [tempAlertUp=" + tempAlertUp + ", tempAlertDown=" + tempAlertDown + ", tempStopUp=" + tempStopUp + ", tempStopDown=" + tempStopDown + "]";
    }

}
