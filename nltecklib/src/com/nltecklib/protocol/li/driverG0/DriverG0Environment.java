package com.nltecklib.protocol.li.driverG0;


import com.nltecklib.protocol.li.Environment.Code;

/**
 * 逻辑板运行环境
 * 
 * @author Administrator
 *
 */
public class DriverG0Environment {



	public enum DriverG0Code implements Code {

		PowerVoltageChnSwitchCode(0x01), BackupVoltageChnSwitchCode(0x02), ChipAddressCode(0x03),CHIP_WORK_MODE(0x04),UPGRADE_CODE(0x08);

		private int code;

		private DriverG0Code(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static DriverG0Code valueOf(int code) {

			for (DriverG0Code temp : DriverG0Code.values()) {
				if(temp.getCode()==code) {
					return temp;
				}
			}

			return null;
		}

	}
	
	
	public enum VoltChnSwitch{
		CLOSE,OPEN;
		
		@Override
		public String toString() {
			
			switch (this) {
			case OPEN:
				return "开";
			case CLOSE:
				return "关";
			}
			return this.name();
		}
	}
	

}
