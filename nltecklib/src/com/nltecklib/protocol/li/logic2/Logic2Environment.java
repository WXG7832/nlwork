package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;

/**
 * 逻辑板运行环境
 * 
 * @author Administrator
 *
 */
public class Logic2Environment {

	public enum SwitchState {

		OFF, ON;
	}

	public enum BatExist {

		/* 无电芯 , 有电芯 */
		NONE, HAVE
	}

	public enum OptMode {

		SYNC /* 同步 */, ASYNC /* 异步 */
	}

	public enum LogicState {

		UDT(0), WORK(1), JOINT(2)/*对接*/,MAINTAIN(3), UPGRADE_LOGIC(4)/*逻辑升级*/ , CAL(5) , UPGRADE_DRIVER(6)/*驱动升级*/;

		private int code;

		private LogicState(int code) {

			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
    
	/**
	 * 逻辑板流程步次工作模式
	 * @author wavy_zheng
	 * 2020年11月16日
	 *
	 */
	public enum WorkMode {

		SLEEP, CC, CV,  DC , CC_CV;
	}

	/**
	 * 校准的模式
	 * 
	 * @author wavy_zheng 2020年10月30日
	 *
	 */
	public enum CalMode {

		SLEEP, CC, CV, DC, CV2/* 第2路AD电压校准 */
	}

	public enum ChnState {

		NONE(0), UDT(1), RUNNING(2), STOP(4), CLOSE(6), EXCEPT(7), COMPLETE(8);

		private int code;

		private ChnState(int code) {

			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static ChnState valueOf(int code) {

			switch (code) {
			case 0:
				return NONE;
			case 1:
				return UDT;
			case 2:
				return RUNNING;
			case 4:
				return STOP;
			case 6:
				return CLOSE;
			case 7:
				return EXCEPT;
			case 8:
				return COMPLETE;

			}

			return null;

		}

	}

	public static class CalculateAdcGroup {

		public double adc2;
		public double adc1;
		public double finalAdc;  //最终ADC
		@Override
		public String toString() {
			return "CalculateAdcGroup [adc2=" + adc2 + ", adc1=" + adc1 + ", finalAdc=" + finalAdc + "]";
		}
		
		
	}
	
	public static class CalibrateAdcGroup {
		
		public double adc2; //第2段ADC，即驱动板ADC
		public double adc1;
		@Override
		public String toString() {
			return "CalibrateAdcGroup [adc2=" + adc2 + ", adc1=" + adc1 + "]";
		}
		
		
		
	}

	public enum AlertCode {


		NORMAL, OVER_VOLT, OVER_CURR, POLE_REVERSE, HARDERR ,OVER_TIME;

	}

	public enum Logic2Code implements Code {

		PoleCode(0x02), BaseCountCode(0x03), Heartbeat(0x04), ChnStartCode(0x05),SyncSkipCode(0x06), PickupCode(0x07),
		StartupCode(0x09), StateCode(0x0b), DeviceProtectCode(0x12), ExtraCvCalProcessCode(0x13),
		WriteCalFlashCode(0x14), ChnCalCode(0x15), MatchCalCode(0x16), CalculateCode(0x17), FaultCheckCode(0x18),
		ModuleSwitchCode(0x19), LabPoleCode(0x1a), LabProtectCode(0x1b), WriteCheckFlashCode(0x1c), TestNameCode(0x20),
		ProcessCode(0x21), OffLineRecoveryCode(0x23), BatExistCode(0x24), PickupTestCode(0x25),
		READ_CHIP_ID(0x26),UpgradeCode(0x27),BaseVoltTestCode(0x28),SoftversionCode(0x29),UUIDCode(0x2A),ProgramStateCode(0x2B),
		ResetAddress(0x2C),REPAIR_MODE(0x2E),BatExistSwitchCode(0x2f),SingleStepCode(0x30),DeviceProtectExCode(0x31),
		PoleExCode(0x32);

		private int code;

		private Logic2Code(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static Logic2Code valueOf(int code) {

			for (Logic2Code temp : Logic2Code.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}

			return null;
		}

	}
	
	/**
	 * 驱动板故障信息
	 * @author wavy_zheng
	 * 2020年12月4日
	 *
	 */
	public static class DriverFaultInfo {
		
		public boolean driverFlashOk;
		public List<Boolean> chnFlashOkList = new ArrayList<>();
		
		
	}
	
	


}
