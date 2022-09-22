package com.nltecklib.protocol.li.logic;


import com.nltecklib.protocol.li.Environment.Code;

/**
 * 逻辑板运行环境
 * 
 * @author Administrator
 *
 */
public class LogicEnvironment {

	public enum PickupState {

		UDT, RUNNING, HARDERR, POWEROFF, CAL , DRIVER_COMM_ERROR , DRIVER_BASEVOLT_ERROR
	}

	public enum MatchState {

		MATCHED, NORMAL
	}

	public static class StepAndLoop {
		
		public StepAndLoop() {
			
			
		}
		
		public StepAndLoop(int loop , int stepIndex) {
			
			this.nextLoop = loop;
			this.nextStep = stepIndex;
		}
		
		public int nextStep;
		public int nextLoop;
		@Override
		public String toString() {
			return "StepAndLoop [nextStep=" + nextStep + ", nextLoop=" + nextLoop + "]";
		}
		
		@Override
		public int hashCode() {
			
			return nextLoop * 100 + nextStep;
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if(obj instanceof StepAndLoop) {
				
				return  this.nextStep == ((StepAndLoop) obj).nextStep && this.nextLoop == ((StepAndLoop) obj).nextLoop;
			}
			
			return true;
		}
		
		
	}

	public enum LogicState {

		UDT(0), WORK(1), MAINTAIN(3), CAL(5);
		private int code;

		private LogicState(int code) {

			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	public static class CalDot {

		public double meter; // 表值
		public double adc;
		public double adcK;
		public double adcB;
		public long   da;
		public double programK;
		public double programB;

	}

	public enum WorkMode {

		UDT, CC, CC_CV, DC
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

//	public enum Pole {
//
//		REVERSE, NORMAL
//	}

	public enum AlertCode {

		NORMAL, OVER_VOLT, OVER_CURR, POLE_REVERSE, HARDERR;
	}

	public enum LogicCode implements Code {

		PoleCode(0x02), BaseCountCode(0x03), ChnSwitchCode(0x04), ChnStartCode(0x05), ChnStopCode(0x06), PickupCode(
				0x07), MultiPickupCode(0x08), StartupCode(0x09), StopCode(0x0A), StateCode(0x0b), DeviceProtectCode(
						0x12), WriteCalFlashCode(0x14), ChnCalCode(0x15), MatchCalCode(0x16), CalculateCode(0x17),
		FaultCheckCode(0x18) ,ModuleSwitchCode(0x19), LabPoleCode(0x1a), LabProtectCode(0x1b) ,
		/**
		 *    HK专用功能码
		 * 
		 * */
		HKWriteCalFlashCode(0x20) , HKCalibrateCode(0x21),HKCalculateCode(0x22) , HKProcedureCode(0x23),HKOperateCode(0x24),
		
		LogicNewCalProcessCode(0x25)
		;

		private int code;

		private LogicCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static LogicCode valueOf(int code) {

			switch (code) {

			case 2:
				return PoleCode;
			case 3:
				return BaseCountCode;
			case 4:
				return ChnSwitchCode;
			case 5:
				return ChnStartCode;
			case 6:
				return ChnStopCode;
			case 7:
				return PickupCode;
			case 8:
				return MultiPickupCode;
			case 0x0b:
				return StateCode;
			case 0x12:
				return DeviceProtectCode;
			case 0x14:
				return WriteCalFlashCode;
			case 0x15:
				return ChnCalCode;
			case 0x16:
				return MatchCalCode;
			case 0x17:
				return CalculateCode;
			case 0x18:
				return FaultCheckCode;
			case 0x19:
				return ModuleSwitchCode;
			case 0x1a:
				return LabPoleCode;
			case 0x1b:
				return LabProtectCode;
			case 0x20:
				return HKWriteCalFlashCode;
			case 0x21:
				return HKCalibrateCode;
			case 0x22:
				return HKCalculateCode;
			case 0x23:
				return HKProcedureCode;
			case 0x24:
				return HKOperateCode;
			case 0x25:
				return LogicNewCalProcessCode;
			}

			return null;
		}

	}

}
