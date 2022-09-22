package com.nltecklib.protocol.lab;

public class Environment {
     
	

	public interface Code {

		public int getCode();
	}


	public enum Orient {

		QUERY, CONFIG, RESPONSE, ALERT;
	}

	public enum Result {

		FAIL, SUCCESS, UNKNOWN_CODE, CRC, LOGIC, TESTING, OTHER, POLE_UNSUPPORT;

		@Override
		public String toString() {
			switch (this) {

			case FAIL:
				return "操作失败";
			case SUCCESS:
				return "操作成功";
			case UNKNOWN_CODE:
				return "未知功能码";
			case CRC:
				return "CRC校准失败";
			case LOGIC:
				return "逻辑错误";
			case TESTING:
				return "正在测试无法操作";
			case OTHER:
				return "其它原因";
			case POLE_UNSUPPORT:
				return "不支持此极性";
			}

			return "";
		}
	}
	
	public enum Role{
		/**管理员*/ADMIN,/**工程师*/ENGINEER,/**普通员工*/NORMAL;

		@Override
		public String toString() {
			switch (this) {
			case ADMIN:
				return "管理员";
			case NORMAL:
				return "普通员工";
			case ENGINEER:
				return "工程师";
			}
			return null;
		}	
	}
	
	
}
