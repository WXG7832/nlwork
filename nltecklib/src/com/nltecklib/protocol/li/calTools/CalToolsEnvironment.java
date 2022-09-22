package com.nltecklib.protocol.li.calTools;


public class CalToolsEnvironment {

	
	/***
	 * 0x73 :逻辑板、回检板ADC校准
	 * 0：不采集   1：采集ADC值
	 *
	 */
	public enum PickWork {
		NONE,ADC;
	}
	
	/***
	 * 0x72:逻辑板、回检板ADC计量 
	 * 0：不采集   1：采集计量值
	 *
	 */
	public enum PickMeterWork {
		NONE,METER;
	}
	
	
	/**
	 * 校准点格式
	 * 
	 * @author Administrator
	 *
	 */
	public static class CalDot {

		public double adc;
		public double adcK;
		public double adcB;
		
		public  CalDot() {}
		
		public  CalDot(double adc,double adcK,double adcB) {
			this.adc = adc;
			this.adcK = adcK;
			this.adcB = adcB;
		}

	}
	
	/** 极性 */
	public enum Pole{
		POSITIVE,NEGATIVE
	}
	
	public static class AdcGroup{
		public double adc;
		public double originalAdc;
	}
}
