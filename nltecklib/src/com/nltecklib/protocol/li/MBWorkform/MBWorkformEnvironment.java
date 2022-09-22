package com.nltecklib.protocol.li.MBWorkform;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月31日 上午9:31:36 类说明
 */
public class MBWorkformEnvironment {

	public enum MBWorkformCode implements Code {

		LogicCalibrateCode(0x01), DeviceBaseInfoCode(0x02), CheckCalibrateCode(0x04), HeartbeatCode(0x05),
		ModuleSwitchCode(0x06), MatchCalCode(0x07)/* 基准电压 */, LogicCalculateCode(0x08), CheckCalculateCode(0x09),
		LogicFlashWriteCode(0x0A), CheckFlashWriteCode(0x0B), LogicCheckFlashWriteCode(0x0C), UUIDCode(0x0D),
		SelfTestInfoCode(0x0F),  SelfCheckCode(0x10);

		private int code;

		private MBWorkformCode(int code) {

			this.code = code;
		}

		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}

		public static MBWorkformCode valueOf(int code) {

			for (MBWorkformCode temp : MBWorkformCode.values()) {
				if (temp.getCode() == code) {
					return temp;
				}
			}

			return null;
		}
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

}
