package com.nltecklib.protocol.li.workform;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * 校准工作环境
 * 
 * @author Administrator
 *
 */
public class WorkformEnvironment {

	public enum WorkformCode implements Code {

		LogicFlashWriteCode(0x14), LogicCalCode(0x15), LogicModeCode(0x16), CalModeCode(0x17), CheckFlashWriteCode(
				0x1c), CheckCalCode(0x1d), BoardMatchCode(0x1e), HeartBeatCode(0x22), MeterSwitchCode(
						0x23), BoardTempCheckCode(0x24), MeasureCode(0x25), ResisterFactorCode(
								0x26), ModuleSwitchCode(0x27), CalBaseVoltCode(0x28), LogicBaseVoltCode(0x29),
		/**
		 * HK专用功能码
		 * 
		 */
		HKFlashWriteCode(0x30), HKCalibrateCode(0x31), HKCalculateCode(0x32), HKCheckCalibrateCode(0x33),
		
		/**
		 * 新功能码
		 */
		CheckCalculateCode(0x34),ExResisterFactorCode(0x35),ExLogicFlashWriteCode(0x36)

		;

		private int code;

		private WorkformCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {

			return code;
		}

		public static WorkformCode valueOf(int code) {

			for (WorkformCode temp : WorkformCode.values()) {
				if(temp.getCode()==code) {
					return temp;
				}
			}

			return null;
		}

	}

	/**
	 * 极性
	 * 
	 * @author Administrator
	 *
	 */
	public enum Pole {

		REVERSE, NORMAL;
	}

	/**
	 * 校准板状态
	 * 
	 * @author wavy_zheng 2020年4月23日
	 *
	 */
	public enum CalBoardState {

		UNREADY, READY, OVER_CHAEGE, OVER_DISCHARGE, OVER_VOLT, REVERSE_VOLT, OVER_TEMP;

	}

	public static class AdcChunk {

		public double primitiveAdc;// 原始ADC
		public double finalAdc; // 最终ADC集合
	}

	public static class HKCalDot {

		public double adc;
		public double adcK;
		public double adcB;
		public double meter; // 表值
		public long da;
		public double programK1;
		public double programB1;
		public double programK2;
		public double programB2;
		public double programK3;
		public double programB3;
	}
	public enum WorkMode {
		
		NONE , CAL
	}
}
