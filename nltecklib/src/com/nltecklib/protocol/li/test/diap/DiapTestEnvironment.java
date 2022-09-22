/**
 * 
 */
package com.nltecklib.protocol.li.test.diap;

import com.nltecklib.protocol.li.Environment.Code;

/**
 * 
 * @Description: 膜片板测试协议
 * @version: v1.0.0
 * @author Admin_shaw
 * @date: 2021年11月12日 上午9:19:09
 *
 */
public class DiapTestEnvironment {

	public enum DiapTestCode implements Code {

		DiapPowerConfig(0X01), DiapItemConfig(0x02), PowerItemConfig(0x03), Power16VSwitch(0x04), DiapControlSwitch(
				0x05), MultiReadchannelSelect(0x06),InformationCollection(0x07);

		@Override
		public int getCode() {
			// TODO Auto-generated method stub
			return code;
		}

		private int code;

		private DiapTestCode(int code) {

			this.code = code;
		}

		public static DiapTestCode valueOf(int code) {

			for (DiapTestCode item : DiapTestCode.values()) {
				if (item.getCode() == code) {
					return item;
				}
			}
			return null;
		}

	}

	/**
	 * 充放电方式 0:充电 1:放电
	 * 
	 * @date: 2021年11月12日 上午9:58:04
	 *
	 */
	public enum ChargeMode {

		Charge, Discharge
	}

	/**
	 * 膜片极性 0:正极性 1:反极性
	 * 
	 * @date: 2021年11月12日 上午10:01:57
	 *
	 */
	public enum PolarityMode {

		Positive, Reverse
	}

	/**
	 * 膜片精度 0:普通精度 1:高精度
	 * 
	 * @date: 2021年11月12日 上午10:04:50
	 *
	 */
	public enum PrecisionMode {
		General, HighPreci
	}

	/**
	 * 开关 0:关闭 1:打开
	 * 
	 * @date: 2021年11月12日 上午10:06:21
	 *
	 */
	public enum SwitchMode {
		Close, Open
	}

	/**
	 * 
	 * 万用表档位 0：挡位1 1：挡位2 2：挡位3 3：电压
	 * 
	 * @date: 2021年11月12日 上午10:09:09
	 *
	 */
	public enum MultiRange {
		Range1, Range2, Range3, Voltage
	}

	/**
	 * 功能码0x03 档位 档位0：I<200mA 档位1:200mA<=I<6000mA 档位2：6000mA<=I<=20000mA
	 *
	 * @date: 2021年11月12日 上午10:37:18
	 */
	public enum Power03Range {
		Range0, Range1, Range2
	}

}
