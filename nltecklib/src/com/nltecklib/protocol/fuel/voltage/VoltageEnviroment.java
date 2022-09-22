package com.nltecklib.protocol.fuel.voltage;

import com.nltecklib.protocol.fuel.Environment.Code;

public class VoltageEnviroment {

	/**
	 * 电压采集板功能码
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum VolCode implements Code {
		/**
		 * 常量定义
		 */
		VOL_PICKUP_CODE(1), WORK_MODE_CODE(2), CAL_CHANNEL_CODE(3), CAL_DATA_CODE(4), ALERT_VALUE_SET_CODE(5),
		MEASURE_CHANNEL_CODE(6), EXCEPTION_QUERY(7), EXCEPTION_DEAL(8), VERSION_INFO(0x32), VERSION_WRITE(0x33);

		private int code;

		private VolCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static VolCode getCode(int code) {
			for (VolCode vol : VolCode.values()) {
				if (vol.getCode() == code) {
					return vol;
				}
			}
			return null;
		}

	}

	/**
	 * 电压采集板工作模式枚举量
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum VBoardWorkMode {
		AWAIT(0, "待机"), PICK(1, "校准"), CAL(2, "采集");

		private int code;
		private String describe;

		private VBoardWorkMode(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}

	/**
	 * 电压采集板通道kb值状态
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum KbState {
		NOMAL(0, "正常"), MISS(1, "缺失");

		private int code;
		private String describe;

		private KbState(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}

	/**
	 * 电压采集板AD芯片状态
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum AdChipState {
		NOMAL(0, "正常"), BREAK(1, "故障");

		private int code;
		private String describe;

		private AdChipState(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}

	/**
	 * 电压采集板升级标志
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum UpgradeSign {
		DO_NOT(0, "不升级"), BE_READY(1, "准备升级");

		private int code;
		private String describe;

		private UpgradeSign(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}

	/**
	 * 电压采集板通道状态
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum ChnVolStatus {
		FORWARD_NORMAL(0, "电池正接，在正常电压范围"), FORWARD_LOW(1, "电池正接，电压低于正常值"), FORWARD_HIGH(2, "电池正接，电压高于正常值"),
		OPPOSITE_NORMAL(3, "电池反接，在正常电压范围"), OPPOSITE_LOW(4, "电池反接，电压低于正常值"), OPPOSITE_HIGH(5, "电池反接，电压高于正常值");

		private int code;
		private String describe;

		private ChnVolStatus(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}
}
