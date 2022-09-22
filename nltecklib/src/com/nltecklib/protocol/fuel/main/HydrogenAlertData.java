package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.Environment.WarningState;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控氢气报警浓度协议数据——0x1A
 * 
 * @author caichao_tang
 *
 */
public class HydrogenAlertData extends Data implements Configable, Responsable, Queryable {
	// 氢气浓度报警限制
	private double h2ConcentrationWarningLimit;
	private double h2Concentration;
	// 氢气报警状态
	private WarningState warningState = WarningState.NORMAL;

	public double getH2Concentration() {
		return h2Concentration;
	}

	public void setH2Concentration(double h2Concentration) {
		this.h2Concentration = h2Concentration;
	}

	public double getH2ConcentrationWarningLimit() {
		return h2ConcentrationWarningLimit;
	}

	public void setH2ConcentrationWarningLimit(double h2ConcentrationWarningLimit) {
		this.h2ConcentrationWarningLimit = h2ConcentrationWarningLimit;
	}

	public WarningState getWarningState() {
		return warningState;
	}

	public void setWarningState(WarningState warningState) {
		this.warningState = warningState;
	}

	@Override
	public void encode() {
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (h2ConcentrationWarningLimit * 10), 2, true)));
		if (h2Concentration != -1) {
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (h2Concentration * 10), 2, true)));
		}
		if (warningState != null) {
			data.add((byte) warningState.ordinal());
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		h2ConcentrationWarningLimit = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)
				/ 10.0;
		h2Concentration = ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true) / 10.0;
		warningState = WarningState.values()[data.get(index)];
	}

	@Override
	public Code getCode() {
		return MainCode.HYDROGEN_ALERT_CODE;
	}

	@Override
	public String toString() {
		return "HydrogenAlertData [h2ConcentrationWarningLimit=" + h2ConcentrationWarningLimit + ", h2Concentration="
				+ h2Concentration + ", warningState=" + warningState + "]";
	}

}
