package com.nltecklib.protocol.power.calBox.calSoft.model;

public class MeasurePlan extends TestPlan {

    public MeasToleranceLimit toleranceLimit = new MeasToleranceLimit();

    // 计量方案, 数值与计量点的偏差上限
    public static class MeasToleranceLimit implements ToleranceLimit {
	public float meterVal = (float) 0.01; // 万用表值偏差上限
	public float voltFinalADC = (float) 0.01; // 电压最终ADC偏差上限
	public float currentFinalADC = (float) 0.01; // 电流最终ADC偏差上限
    }

}
