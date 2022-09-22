package com.nltecklib.protocol.power;
/**
* @author  wavy_zheng
* @version 创建时间：2021年12月15日 上午10:32:54
* 动力电池环境参数
*/
public class Environment {
    
	
	public interface Code {

		public int getCode();
	}


	public enum Orient {

		QUERY, CONFIG, RESPONSE, ALERT;
	}
	
	public enum Result {

		FAIL, SUCCESS, UNKNOWN_CODE, CRC, LOGIC, TESTING, OTHER;
		

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
			}

			return "";
		}
	}
	
	
	
}
