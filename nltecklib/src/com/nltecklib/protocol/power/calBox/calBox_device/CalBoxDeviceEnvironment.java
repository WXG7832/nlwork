package com.nltecklib.protocol.power.calBox.calBox_device;

import com.nltecklib.protocol.power.Environment.Code;

public class CalBoxDeviceEnvironment {

	public enum CalBoxDeviceCode implements Code {

		CalibrateChnCode(0x01), SelfCheckCode(0x02), /*CheckCalibrateCode(0x04),*/ HeartbeatCode(0x05), ChannelSwitchCode(
				0x06), MatchAdcCode(
						0x07)/* 基准电压 */, MeasureChnCode(0x08), BaseInfoCode(0x09)/*设备基础信息*/ , FlashParamCode(0x0A),
		ModeCode(0x0B) , AccessoryStateCode(0x0C) , DriverModeCode(0x0D);

		private int code;

		private CalBoxDeviceCode(int code) {

			this.code = code;
		}

		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}

		public static CalBoxDeviceCode valueOf(int code) {

			for (CalBoxDeviceCode temp : CalBoxDeviceCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}

			return null;
		}
	}
	
	
	public enum WorkMode {
		
		NORMAL/*退出模式，回到待测模式*/ , JOINT/*对接模式*/, CAL/*校准模式*/
		
	}

	/**
	 * 校准ID类型
	 * 
	 * @author wavy_zheng 2020年12月31日
	 *
	 */
	public enum IDType {

		DRIVER_LOGIC, DRIVER_CHECK, LOGIC, CHECK;
	}

	/*// 查询ADC的数据模型
	public static class AdcData {

		public double originADC;
		public double finalADC;

		public double back1OrigADC;
		public double back1FinalADC;

		public double back2OrigADC;
		public double back2FinalADC;
	}*/
}
