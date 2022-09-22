package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ProtectData extends Data implements Configable, Queryable, Responsable {

    private final static int TEMP_BYTES = 2;
    private final static int PRESS_BYTES = 3;
    private final static int FLOW_BYTES = 3;
    private final static int VOLT_BYTES = 2;

    private final static int TEMP_DIV = 10;
    private final static int PRESS_DIV = 1;
    private final static int FLOW_DIV = 10;
    private final static int VOLT_DIV = 1;

    private boolean onLowLimitProtect;
    private List<ComponentProtectItem> tempProtects = new ArrayList<>();// 2字节，单位0.1
    private double diffPressProtectsAlert;// 3字节，单位1
    private double diffPressProtectsStop;// 3字节，单位1
    private List<ComponentProtectItem> pressProtects = new ArrayList<>();// 3字节，单位1
    private List<ComponentProtectItem> flowProtects = new ArrayList<>();// 3字节，单位0.1
    private ProtectItem stackVoltProtect = new ProtectItem();// 2字节，单位1
    private ProtectItem singleVoltProtect = new ProtectItem();// 2字节，单位1

    @Override
    public void encode() {
	// 下线保护开启
	data.add((byte) (onLowLimitProtect ? 1 : 0));
	// 温度保护
	data.add((byte) tempProtects.size());
	for (ComponentProtectItem item : tempProtects) {
	    data.addAll(Arrays.asList(ProtocolUtil.split(item.component.getNumber(), 2, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.alertMax * TEMP_DIV), TEMP_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.alertMin * TEMP_DIV), TEMP_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.stopMax * TEMP_DIV), TEMP_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.stopMin * TEMP_DIV), TEMP_BYTES, true)));
	}
	// 压差保护
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (diffPressProtectsAlert*PRESS_DIV), PRESS_BYTES, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (diffPressProtectsStop*PRESS_DIV), PRESS_BYTES, true)));
	// 压力保护
	data.add((byte) pressProtects.size());
	for (ComponentProtectItem item : pressProtects) {
	    data.addAll(Arrays.asList(ProtocolUtil.split(item.component.getNumber(), 2, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.alertMax * PRESS_DIV), PRESS_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.alertMin * PRESS_DIV), PRESS_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.stopMax * PRESS_DIV), PRESS_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.stopMin * PRESS_DIV), PRESS_BYTES, true)));
	}
	// 流量保护
	data.add((byte) flowProtects.size());
	for (ComponentProtectItem item : flowProtects) {
	    data.addAll(Arrays.asList(ProtocolUtil.split(item.component.getNumber(), 2, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.alertMax * FLOW_DIV), FLOW_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.alertMin * FLOW_DIV), FLOW_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.stopMax * FLOW_DIV), FLOW_BYTES, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (item.stopMin * FLOW_DIV), FLOW_BYTES, true)));
	}
	// 堆电压保护
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stackVoltProtect.standard * VOLT_DIV), VOLT_BYTES, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stackVoltProtect.alertVal * VOLT_DIV), VOLT_BYTES, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (stackVoltProtect.stopVal * VOLT_DIV), VOLT_BYTES, true)));
	// 单电压保护
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (singleVoltProtect.standard * VOLT_DIV), VOLT_BYTES, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (singleVoltProtect.alertVal * VOLT_DIV), VOLT_BYTES, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) (singleVoltProtect.stopVal * VOLT_DIV), VOLT_BYTES, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	// 下线保护开启
	onLowLimitProtect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	// 温度保护
	int count = ProtocolUtil.getUnsignedByte(data.get(index++));
	for (int i = 0; i < count; i++) {
	    ComponentProtectItem item = new ComponentProtectItem();
	    item.component = Component.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
	    index += 2;
	    item.alertMax = (double) ProtocolUtil.compose(data.subList(index, index + TEMP_BYTES).toArray(new Byte[0]), true) / TEMP_DIV;
	    index += TEMP_BYTES;
	    item.alertMin = (double) ProtocolUtil.compose(data.subList(index, index + TEMP_BYTES).toArray(new Byte[0]), true) / TEMP_DIV;
	    index += TEMP_BYTES;
	    item.stopMax = (double) ProtocolUtil.compose(data.subList(index, index + TEMP_BYTES).toArray(new Byte[0]), true) / TEMP_DIV;
	    index += TEMP_BYTES;
	    item.stopMin = (double) ProtocolUtil.compose(data.subList(index, index + TEMP_BYTES).toArray(new Byte[0]), true) / TEMP_DIV;
	    index += TEMP_BYTES;
	    tempProtects.add(item);
	}
	// 压差保护
	diffPressProtectsAlert =  (double)ProtocolUtil.compose(data.subList(index, index += PRESS_BYTES).toArray(new Byte[0]), true)/PRESS_DIV;
	diffPressProtectsStop = (double) ProtocolUtil.compose(data.subList(index, index += PRESS_BYTES).toArray(new Byte[0]), true)/PRESS_DIV;

	// 压力保护
	count = ProtocolUtil.getUnsignedByte(data.get(index++));
	for (int i = 0; i < count; i++) {
	    ComponentProtectItem item = new ComponentProtectItem();
	    item.component = Component.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
	    index += 2;
	    item.alertMax = (double) ProtocolUtil.compose(data.subList(index, index + PRESS_BYTES).toArray(new Byte[0]), true) / PRESS_DIV;
	    index += PRESS_BYTES;
	    item.alertMin = (double) ProtocolUtil.compose(data.subList(index, index + PRESS_BYTES).toArray(new Byte[0]), true) / PRESS_DIV;
	    index += PRESS_BYTES;
	    item.stopMax = (double) ProtocolUtil.compose(data.subList(index, index + PRESS_BYTES).toArray(new Byte[0]), true) / PRESS_DIV;
	    index += PRESS_BYTES;
	    item.stopMin = (double) ProtocolUtil.compose(data.subList(index, index + PRESS_BYTES).toArray(new Byte[0]), true) / PRESS_DIV;
	    index += PRESS_BYTES;
	    pressProtects.add(item);
	}
	// 流量保护
	count = ProtocolUtil.getUnsignedByte(data.get(index++));
	for (int i = 0; i < count; i++) {
	    ComponentProtectItem item = new ComponentProtectItem();
	    item.component = Component.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
	    index += 2;
	    item.alertMax = (double) ProtocolUtil.compose(data.subList(index, index + FLOW_BYTES).toArray(new Byte[0]), true) / FLOW_DIV;
	    index += FLOW_BYTES;
	    item.alertMin = (double) ProtocolUtil.compose(data.subList(index, index + FLOW_BYTES).toArray(new Byte[0]), true) / FLOW_DIV;
	    index += FLOW_BYTES;
	    item.stopMax = (double) ProtocolUtil.compose(data.subList(index, index + FLOW_BYTES).toArray(new Byte[0]), true) / FLOW_DIV;
	    index += FLOW_BYTES;
	    item.stopMin = (double) ProtocolUtil.compose(data.subList(index, index + FLOW_BYTES).toArray(new Byte[0]), true) / FLOW_DIV;
	    index += FLOW_BYTES;
	    flowProtects.add(item);
	}
	// 堆电压保护
	stackVoltProtect.standard = (double) ProtocolUtil.compose(data.subList(index, index + VOLT_BYTES).toArray(new Byte[0]), true) / VOLT_DIV;
	index += VOLT_BYTES;
	stackVoltProtect.alertVal = (double) ProtocolUtil.compose(data.subList(index, index + VOLT_BYTES).toArray(new Byte[0]), true) / VOLT_DIV;
	index += VOLT_BYTES;
	stackVoltProtect.stopVal = (double) ProtocolUtil.compose(data.subList(index, index + VOLT_BYTES).toArray(new Byte[0]), true) / VOLT_DIV;
	index += VOLT_BYTES;
	// 单电压保护
	singleVoltProtect.standard = (double) ProtocolUtil.compose(data.subList(index, index + VOLT_BYTES).toArray(new Byte[0]), true) / VOLT_DIV;
	index += VOLT_BYTES;
	singleVoltProtect.alertVal = (double) ProtocolUtil.compose(data.subList(index, index + VOLT_BYTES).toArray(new Byte[0]), true) / VOLT_DIV;
	index += VOLT_BYTES;
	singleVoltProtect.stopVal = (double) ProtocolUtil.compose(data.subList(index, index + VOLT_BYTES).toArray(new Byte[0]), true) / VOLT_DIV;
	index += VOLT_BYTES;

    }

    @Override
    public Code getCode() {
	return MainCode.PROTECT_CODE;
    }

    public boolean isOnLowLimitProtect() {
	return onLowLimitProtect;
    }

    public void setOnLowLimitProtect(boolean onLowLimitProtect) {
	this.onLowLimitProtect = onLowLimitProtect;
    }

    public List<ComponentProtectItem> getTempProtects() {
	return tempProtects;
    }

    public void setTempProtects(List<ComponentProtectItem> tempProtects) {
	this.tempProtects = tempProtects;
    }

    public double getDiffPressProtectsAlert() {
	return diffPressProtectsAlert;
    }

    public void setDiffPressProtectsAlert(double diffPressProtectsAlert) {
	this.diffPressProtectsAlert = diffPressProtectsAlert;
    }

    public double getDiffPressProtectsStop() {
	return diffPressProtectsStop;
    }

    public void setDiffPressProtectsStop(double diffPressProtectsStop) {
	this.diffPressProtectsStop = diffPressProtectsStop;
    }

    public List<ComponentProtectItem> getPressProtects() {
	return pressProtects;
    }

    public void setPressProtects(List<ComponentProtectItem> pressProtects) {
	this.pressProtects = pressProtects;
    }

    public List<ComponentProtectItem> getFlowProtects() {
	return flowProtects;
    }

    public void setFlowProtects(List<ComponentProtectItem> flowProtects) {
	this.flowProtects = flowProtects;
    }

    public ProtectItem getStackVoltProtect() {
	return stackVoltProtect;
    }

    public void setStackVoltProtect(ProtectItem stackVoltProtect) {
	this.stackVoltProtect = stackVoltProtect;
    }

    public ProtectItem getSingleVoltProtect() {
	return singleVoltProtect;
    }

    public void setSingleVoltProtect(ProtectItem singleVoltProtect) {
	this.singleVoltProtect = singleVoltProtect;
    }

	@Override
	public String toString() {
		return "ProtectData [onLowLimitProtect=" + onLowLimitProtect + ", tempProtects=" + tempProtects
				+ ", diffPressProtectsAlert=" + diffPressProtectsAlert + ", diffPressProtectsStop="
				+ diffPressProtectsStop + ", pressProtects=" + pressProtects + ", flowProtects=" + flowProtects
				+ ", stackVoltProtect=" + stackVoltProtect + ", singleVoltProtect=" + singleVoltProtect + "]";
	}

    

}