package com.nltecklib.protocol.li.check2;

import com.nltecklib.protocol.li.Environment.Code;

public class Check2Environment {

	public enum Check2Code implements Code {

		PoleCode(0x02), SoftversionCode(0x03), Heartbeat(0x04), PickupCode(0x07), MultiPickupCode(0x08), StartupCode(0x0b),
		DeviceProtectCode(0x12), WriteCalFlashCode(0x14), ChnCalCode(0x15), FaultCheckCode(0x16), CalculateCode(0x19),	
		OverVoltCode(0x1A),TestPickCode(0x1B),UpgradeCode(0x1C),UUID_CODE(0x1D),ConfirmCloseCode(0x1e),ProgramStateCode(0x1f),
		REPAIR_MODE(0x21),ProtectionSwitchCode(0x22);

		private int code;

		private Check2Code(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}
		
		public static Check2Code valueOf(int code) {
			for (Check2Code temp : Check2Code.values()) {
				if(temp.getCode()==code) {
					return temp;
				}
			}
			return null;
		}


	}



	public enum PowerState {

		POWEROFF, POWERON;
	}

	public enum HKAlertCode {

		NORMAL, DEV_VOLT_OVER, HARD_ERROR;
	}

	public enum AlertCode {

		NORMAL, DEV_VOLT_OVER, POLE_REVERSE, HARD_ERROR;
	}

	public enum ChnState {

		NONE, NORMAL, EXCEPT;
	}

//	public enum Pole {
//
//		NORMAL, REVERSE
//	}

	public enum CheckWorkMode {

		STANDBY(0, "待机"), CHECK(1, "回检"), CAL(2, "校准"), HARDERR(3, "硬件错误") , UPGRADE_CHECK(4,"回检升级") , UPGRADE_DRIVER(5,"驱动升级");

		private int code;
		private String description;

		private CheckWorkMode(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescriotion() {
			return description;
		}

		public int getCode() {
			return code;

		}

		public static CheckWorkMode valueOf(int code) {
			for (CheckWorkMode mode : CheckWorkMode.values()) {
				if (mode.getCode() == code) {
					return mode;
				}
			}
			return null;
		}

	}
	
	public enum VoltMode{
		Backup,Power
	}

	public enum Work {

		NONE, CONSTANT;
	}
	/**
	 * 驱动板故障信息包
	 * @author wavy_zheng
	 * 2020年12月4日
	 *
	 */
	public static class DriverFaultInfo {
		
		public boolean stateOk; //驱动板状态OK？
		public boolean baseVoltOk; //基准电压Ok?
		public short   chnsFlag ; //驱动板通道校准标志，位1表示已校准，位0表示未校准
		
	}

	/**
	 * 校准点格式
	 * 
	 * @author Administrator
	 *
	 */
	public static class CalDot {

		public double adc;
		public double adcCalculate; //用于计算KB时使用，回检板内存储的ADC = K1 * adc + B1
		public double adcK;
		public double adcB;
		@Override
		public String toString() {
			return "CalDot [adc=" + adc + ", adcCalculate=" + adcCalculate + ", adcK=" + adcK + ", adcB=" + adcB + "]";
		}

		
	}

	public enum SwitchState {

		OFF, ON;
	}
	
	public static class AdcGroup {
		
		public double adc2;
		public double adc1;
		public double finalAdc; //最终检测值
		@Override
		public String toString() {
			return "AdcGroup [adc2=" + adc2 + ", adc1=" + adc1 + ", finalAdc=" + finalAdc + "]";
		}
		
		
		
	}

}
