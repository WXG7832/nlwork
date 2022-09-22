package com.nltecklib.protocol.li.MBWorkform;

import java.util.ArrayList;
import java.util.List;

public class MBSelfTestInfo {

	public List<LogicInfo> logicInfos = new ArrayList<>();
	public List<CheckInfo> checkInfos = new ArrayList<>();
	public AccessInfo accessInfo = new AccessInfo();

	/**
	 * 逻辑板信息
	 * 
	 * @author guofang_ma
	 *
	 */
	public static class LogicInfo {
		public int index;// 分区号
		public String version;// 版本号
		public boolean appMode;// 进入正常模式
		// 硬件ok
		public boolean sramOK;
		public boolean ad1OK;
		public boolean ad2OK;
		public boolean ad3OK;
		public boolean flashOK;
		// 系数ok
		public boolean ad1ParamOK;
		public boolean ad2ParamOK;
		public boolean ad3ParamOK;
		

		public List<DriverInfo> driverInfos = new ArrayList<>();

		@Override
		public String toString() {
			return "LogicInfo [index=" + index + ", version=" + version + ", appMode=" + appMode + ", sramOK=" + sramOK
					+ ", ad1OK=" + ad1OK + ", ad2OK=" + ad2OK + ", ad3OK=" + ad3OK + ", flashOK=" + flashOK
					+ ", ad1ParamOK=" + ad1ParamOK + ", ad2ParamOK=" + ad2ParamOK + ", ad3ParamOK=" + ad3ParamOK
					+ ", driverInfos=" + driverInfos + "]";
		}

	}

	public static class DriverInfo {
		public int index;// 分区板号
		public String version;
		public boolean appMode;// 进入正常模式

		@Override
		public String toString() {
			return "DriverInfo [index=" + index + ", version=" + version + ", appMode=" + appMode + "]";
		}

	}

	public static class CheckInfo {
		public int index;// 分区号
		public String version;
		public boolean appMode;// 进入正常模式
		// 硬件ok
		public boolean adOK;
		public boolean flashOK;
		public boolean flashParamOK;
		public List<DriverInfo> driverInfos = new ArrayList<>();

		@Override
		public String toString() {
			return "CheckInfo [index=" + index + ", version=" + version + ", appMode=" + appMode + ", adOK=" + adOK
					+ ", flashOK=" + flashOK + ", flashParamOK=" + flashParamOK + ", driverInfos=" + driverInfos + "]";
		}

	}

	public static class AccessInfo {
		public List<Boolean> fanOks = new ArrayList<>();
		public List<Boolean> powerOKs = new ArrayList<>();

		@Override
		public String toString() {
			return "AccessInfo [fanOks=" + fanOks + ", powerOKs=" + powerOKs + "]";
		}

	}

	@Override
	public String toString() {
		return "MBSelfTestInfo [logicInfos=" + logicInfos + ", checkInfos=" + checkInfos + ", accessInfo=" + accessInfo
				+ "]";
	}

}
