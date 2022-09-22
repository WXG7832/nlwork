package com.nltecklib.protocol.power.calBox.calSoft.model;

public class CalibratePlan extends TestPlan {

    public CalToleranceLimit toleranceLimit = new CalToleranceLimit();

    // 校准方案, 数值的上限, 决定是否终止校准
    public static class CalToleranceLimit implements ToleranceLimit {
	public int originADC = 10000; // 原始ADC上限
	public short k = 1000;
	public short b = 10000;
	public int meterVal = 60000;
    }

}
