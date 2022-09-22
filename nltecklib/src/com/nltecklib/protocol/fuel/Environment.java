package com.nltecklib.protocol.fuel;

public class Environment {

	public interface Code {

		public int getCode();
	}

	/**
	 * 协议类型码枚举常量
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum ProtocolType implements Code {

		VOL(0), HEATCONDUCT(1), FLOW(2), MAIN(4), ALERT(5), PROTECT(6), TEMP(19), CONTROL(3),;

		private int code;

		private ProtocolType(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}

		public static ProtocolType getCode(int code) {
			for (ProtocolType type : ProtocolType.values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * 协议数据区类型枚举常量
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum Orient {
		QUERY, CONFIG, RESPONSE, ALERT;
	}

	/**
	 * 结果码枚举常量
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum Result {

		FAIL, SUCCESS, UNKNOWN_CODE, CRC_ERROR, PARAME_LOGIC_ERROR, RUNNING_ERROR, OTHER, CLOSE, TIMEOUT,
		DEVICE_STATUS_ERROR, RETURN_ERROR, TRANSDUCER_ERROR, PROTECT_NOT_SET;

//		@Override
//		public String toString() {
//			switch (this) {
//			case FAIL:
//				return "操作失败";
//			case SUCCESS:
//				return "操作成功";
//			case UNKNOWN_CODE:
//				return "未知功能码";
//			case CRC_ERROR:
//				return "CRC校验错误";
//			case PARAME_LOGIC_ERROR:
//				return "参数逻辑错误";
//			case RUNNING_ERROR:
//				return "正在运行无法配置";
//			case OTHER:
//				return "其它原因";
//			case CLOSE:
//				return "系统在关机状态";
//			case TIMEOUT:
//				return "通信超时错误（指单片机与外部设备通信）";
//			case DEVICE_STATUS_ERROR:
//				return "设备状态异常错误（变频器、电子负载等）";
//			case RETURN_ERROR:// 0A
//				return "通信设备返回数据错误";
//			case TRANSDUCER_ERROR:
//				return "变频器设备关机当中，不允许设置";
//			case PROTECT_NOT_SET:
//				return "保护未设置";
//			}
//
//			return "";
//		}

	}

	/**
	 * 开关状态枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum State {
		OFF, ON, HOLD;
	}

	/**
	 * 工作模式枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum WorkMode {
		STOP, TEST, UPDATE;
	}

	/**
	 * 硬件状态枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum HardwareState {
		NORMAL(0, "正常"), TRIGGER(1, "已触发");

		private int code;
		private String describe;

		private HardwareState(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}
	}

//    /**
//     * 器件报警状态枚举对象
//     * 
//     * @author caichao_tang
//     *
//     */
//    public enum ComponentAlertState {
//	NORMAL(0, "良好"), OPEN_CIRCUIT(1, "开路"), BREAK_CIRCUIT(2, "断路");
//
//	private int code;
//	private String describe;
//
//	private ComponentAlertState(int code, String describe) {
//	    this.code = code;
//	    this.describe = describe;
//	}
//
//	public int getCode() {
//	    return code;
//	}
//
//	public String getDescribe() {
//	    return describe;
//	}
//    }

	/**
	 * 变频器状态
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum TransducerState {
		OFF(0, "关闭"), ON(1, "打开"), SPEED_DOWN(2, "减速中"), AWAIT(3, "待机");

		private int code;
		private String describe;

		private TransducerState(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}
	}

	/**
	 * 报警状态枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum WarningState {
		NORMAL(0, "正常"), WARNING(1, "已触发");

		private int code;
		private String describe;

		private WarningState(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}
	}

	/**
	 * 关联模式枚举对象
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum AssociationMode {
		NOT_CHAIN(0, "非连锁模式"), CHAIN(1, "连锁模式");

		private int code;
		private String describe;

		private AssociationMode(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

	}

	public enum ComponentState {
		NORMAL(0, "正常"), OPEN_CIRCLE(1, "开路"), SHORT_CIRCLE(2, "短路"), NOT_USE(3, "未启用");

		private int code;
		private String describe;

		private ComponentState(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

	}

}
