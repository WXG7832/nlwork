package com.nltecklib.protocol.atlmes.mes;

public class MesEnvironment {
	/**
	 * 工序标识:IC/AG
	 */
	public enum ProcessType {
		IC, AG
	}

	public enum EQStateCode {
		Run, Stop
	}

	public enum HLFlag {
		H, L
	}

	/**
	 * 上料工序 0-电芯进料校验，1-电芯出料校验，2-弹夹进设备校验，3弹夹下料位校验，4-弹夹机电芯校验（专用）；无此字段默认为0-电芯进料校验
	 */
	public enum FeedType {
		BatteryIn, BatteryOut, ClipIntoDevice, ClipCut, ClipBattery
	}
	public enum ControlCode{
		Run,Stop,Clear
	}
}
