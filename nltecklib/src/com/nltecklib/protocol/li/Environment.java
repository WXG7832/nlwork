package com.nltecklib.protocol.li;

import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestEnvironment.CalToolsTestCode;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.DriverG0Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;

public class Environment {

	public interface Code {

		public int getCode();
	}

	/**
	 * 
	 *数据区类型: 查询 配置 回复 ..
	 */
	public enum Orient {

		QUERY, CONFIG, RESPONSE, ALERT;
	}

	/**
	 * 结果码抽象接口
	 * 
	 * @author guofang_ma
	 *
	 */
	public interface Result {

		public static final int FAIL = 0;
		public static final int SUCCESS = 1;

		public int getCode();

		public String toString();

		public String getDescription();

		public static Result valueOf(Code funcCode, int code) {

			if (funcCode instanceof Check2Code) {
				return Check2Result.valueOf(code);

			} else if (funcCode instanceof Logic2Code) {
				return Logic2Result.valueOf(code);

			} else if (funcCode instanceof DriverG0Code) {
				return DriverG0Result.valueOf(code);

			} else if (funcCode instanceof CalToolsTestCode) {
				return TestResult.valueOf(code);

			}else if (funcCode instanceof DiapTestCode) {
				
				return DiapTestResult.valueOf(code);
				
			}
			else {
				return DefaultResult.valueOf(code);

			}

		}

	}

	public enum DefaultResult implements Result {

		FAIL(0, "操作失败"), SUCCESS(1, "操作成功"), UNKNOWN_CODE(2, "未知功能码"), CRC(3, "CRC校准失败"), LOGIC(4, "逻辑错误"),
		TESTING(5, "正在测试无法操作"), OTHER(6, "其它原因");

		private int code;
		private String description;

		private DefaultResult(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public int getCode() {
			return code;
		}

		public static DefaultResult valueOf(int code) {
			for (DefaultResult temp : DefaultResult.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return DefaultResult.UNKNOWN_CODE;
		}
	}

	public enum Check2Result implements Result {
		FAIL(0, "操作失败"), SUCCESS(1, "操作成功"), UNKNOWN_CODE(2, "未知功能码"), CRC(3, "CRC校准失败"), LOGIC(4, "逻辑错误"),
		TESTING(5, "工作模式配置错误"), OTHER(6, "不在校准模式错误"),CHN_OUT_RANGE(7,"通道超出范围"),BOARD_ADDRESS_ERR(8,"回检板地址错误"),
		BOARD_ADDRESS_WRITE_FAIL(9,"回检板地址写入失败"),CHN_VALUE_ERR(10,"不是开启校准的通道值"),CAL_DADA_OUT(11,"校准数据量超标"),POLE_SWITCH_ERR(12,"工作模式下，禁止切换极性"),
		ERR_CAL_MODE(13,"校准工作方式设置错误"),POLE_ERR(14,"极性设置错误"),DRIVER_INDEX_ERR(15,"驱动板号错误"),CONFIG_POLE_ERR(16,"不在待机模式，配置极性错误"),
		NO_CAL_PARAME(17,"芯片无校准系数"),FLASH_ID(18,"falshID错误"),NO_KB(19,"无通道KB系数");
		private int code;
		private String description;

		private Check2Result(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public int getCode() {
			return code;
		}

		public static Check2Result valueOf(int code) {
			for (Check2Result temp : Check2Result.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return Check2Result.UNKNOWN_CODE;
		}
	}

	public enum Logic2Result implements Result {
		FAIL(0, "操作失败"), SUCCESS(1, "操作成功"), UNKNOWN_CODE(2, "未知功能码"), CRC(3, "CRC校准失败"), LOGIC(4, "逻辑错误"),
		TESTING(5, "正在测试无法操作"), OTHER(6, "其它原因"),DRIVER_NO_RESPONSE(7,"驱动板未回复"),DRIVER_RESPONSE_ERROR(8,"驱动板回复数据异常");

		private int code;
		private String description;

		private Logic2Result(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public int getCode() {
			return code;
		}

		public static Logic2Result valueOf(int code) {
			for (Logic2Result temp : Logic2Result.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return Logic2Result.UNKNOWN_CODE;
		}
	}

	public enum TestResult implements Result {
		FAIL(0, "操作失败"), SUCCESS(1, "操作成功"), UNKNOWN_CODE(2, "未知功能码"), CRC(3, "数据校验错误"), LOGIC(4, "参数逻辑错误"),
		POWER(5, "总电源未上电错误"), DTYPE(6, "数据区类型错误"), DAC(7, "dac配置超出范围"), OPEN(8, "开个命令错误"), SELTCHN(9, "继电器选通错误"), OVER(10, "电流配置超出范围错误");

		private int code;
		private String description;

		private TestResult(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public int getCode() {
			return code;
		}

		public static TestResult valueOf(int code) {
			for (TestResult temp : TestResult.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return TestResult.UNKNOWN_CODE;
		}
	}
	
	public static enum DriverG0Result implements Result {

		FAIL(0, "操作失败"), SUCCESS(1, "操作成功"), UNKNOWN_CODE(2, "未知功能码"), CRC(3, "CRC校准失败"), LOGIC(4, "逻辑错误"),
		ADDRESS(5, "地址不匹配"), LENGTH(6, "数据长度不匹配");

		private int code;
		private String description;

		private DriverG0Result(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public int getCode() {
			return code;
		}

		public static DriverG0Result valueOf(int code) {
			for (DriverG0Result temp : DriverG0Result.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return DriverG0Result.UNKNOWN_CODE;
		}

	}
	
	public static enum DiapTestResult implements Result {

		FAIL(0, "操作失败"), SUCCESS(1, "操作成功"),UNKNOWN_CODE(2, "未知功能码"), BUSY_CODE(3, "忙碌"), PARAM(4, "参数错误"), POWERBOARDTIMEOUT(5, "电源板超时"),
		LOADTIMEOUT(6, "负载超时");

		private int code;
		private String description;

		private DiapTestResult(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public int getCode() {
			return code;
		}

		public static DiapTestResult valueOf(int code) {
			for (DiapTestResult temp : DiapTestResult.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}
			return DiapTestResult.UNKNOWN_CODE;
		}

	}

	public static Result toResult(ProtocolType type, int result) {

		if (type == null) {
			return DefaultResult.values()[result];
		}

		switch (type) {
		case LOGIC2:
			return Logic2Result.values()[result];
		case CHECK2:
			return Check2Result.values()[result];
		case DriverG0:
			return DriverG0Result.values()[result];
		case TestTools:
			return TestResult.values()[result];
		case DiapTest:
			return DiapTestResult.values()[result];
		default:
			return DefaultResult.values()[result];
		}

	}

}
