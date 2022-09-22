package com.nltecklib.protocol.fuel.heatConduct;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 导热罐控制板温度报警配置采集
 * 
 * @author caichao_tang
 *
 */
public class HeatConductBoardTempAlertData extends Data implements ComponentSupportable, Queryable, Responsable, Configable {
    /**
     * 温度警告上限值
     */
    private double alertMax;
    /**
     * 温度警告下限值
     */
    private double alertMin;
    /**
     * 停机温度上限值
     */
    private double stopMax;
    /**
     * 停机温度下限值
     */
    private double stopMin;

    // ====================== setter and getter start =========================
    public double getAlertMax() {
	return alertMax;
    }

    public void setAlertMax(double alertMax) {
	this.alertMax = alertMax;
    }

    public double getAlertMin() {
	return alertMin;
    }

    public void setAlertMin(double alertMin) {
	this.alertMin = alertMin;
    }

    public double getStopMax() {
	return stopMax;
    }

    public void setStopMax(double stopMax) {
	this.stopMax = stopMax;
    }

    public double getStopMin() {
	return stopMin;
    }

    public void setStopMin(double stopMin) {
	this.stopMin = stopMin;
    }

    // ====================== setter and getter end =========================
    @Override
    public void encode() {
	// 设置报警值
	data.addAll(Arrays.asList(ProtocolUtil.split((long) alertMin, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) alertMax, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) stopMin, 2, true)));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) stopMax, 2, true)));

    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	alertMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	alertMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	stopMin = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	index += 2;
	stopMax = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	return HeatConductBoardFunctionCode.TEMP_ALERT;
    }

}
