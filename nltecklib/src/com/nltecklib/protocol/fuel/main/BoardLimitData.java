package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class BoardLimitData extends Data implements Configable, Queryable, Responsable {

	private HashMap<Component, Double> compLimits = new HashMap<>();
	private double flowStackPressDiff;
	private double controlStackPressDiff;
	private double heatStackPressDiff;

	@Override
	public void encode() {
		data.add((byte) compLimits.size());
		for (Component comp : compLimits.keySet()) {
			data.addAll(Arrays.asList(ProtocolUtil.split(comp.getNumber(), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (compLimits.get(comp) * 10), 2, true)));
		}
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (flowStackPressDiff * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (controlStackPressDiff * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (heatStackPressDiff * 10), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int i = 0; i < count; i++) {
			Component comp = Component
					.get((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			double val = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
			index += 2;
			compLimits.put(comp, val);
		}
		flowStackPressDiff = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
				/ 10;
		index += 2;
		controlStackPressDiff = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
				/ 10;
		index += 2;
		heatStackPressDiff = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
				/ 10;
		index += 2;
	}

	@Override
	public Code getCode() {
		return MainCode.BOARD_LIMIT_CODE;
	}

	public HashMap<Component, Double> getCompLimits() {
		return compLimits;
	}

	public void setCompLimits(HashMap<Component, Double> compLimits) {
		this.compLimits = compLimits;
	} 

	public double getFlowStackPressDiff() {
		return flowStackPressDiff;
	}

	public void setFlowStackPressDiff(double flowStackPressDiff) {
		this.flowStackPressDiff = flowStackPressDiff;
	}

	public double getControlStackPressDiff() {
		return controlStackPressDiff;
	}

	public void setControlStackPressDiff(double controlStackPressDiff) {
		this.controlStackPressDiff = controlStackPressDiff;
	}

	public double getHeatStackPressDiff() {
		return heatStackPressDiff;
	}

	public void setHeatStackPressDiff(double heatStackPressDiff) {
		this.heatStackPressDiff = heatStackPressDiff;
	}

	@Override
	public String toString() {
		return "BoardLimitData [compLimits=" + compLimits + ", flowStackPressDiff=" + flowStackPressDiff
				+ ", controlStackPressDiff=" + controlStackPressDiff + ", heatStackPressDiff=" + heatStackPressDiff
				+ "]";
	}

}
