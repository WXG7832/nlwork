package com.nltecklib.protocol.fuel.heatConduct;

import com.nltecklib.protocol.fuel.Environment.Code;

/**
 * 导热罐控制板协议功能码枚举常量
 * 
 * @author caichao_tang
 *
 */
public enum HeatConductBoardFunctionCode implements Code {
	PICKUP(1, "查询采集"), TEMP_ALERT(2, "温度报警"), TEMP(3, "温度"), SOV(4, "电磁阀"), ROTATE_SPEED(5, "转速设置"),
	TRANSDUCER(6, "变频器"), WORK_MODE(7, "工作模式"), PID(8, "PID"), ASSOCIATION_MODE(9, "连锁模式"),
	CALIBRATION_PICKUP(0x0A, "校准采集"), BIG_CYCLE(0x0B, "大循环状态"), PRESSURE_UP_LIMIT(0x0C, "压力报警上限值"),
	TEMP_UP_LIMIT(0x0d, "温度报警上限值"), STACK_PRESSURE_DIFFERENCE(0x0e, "电堆压力差报警值"),UPDATE_CODE(0x0F,"在线升级"),ALERT_CLEAR_CODE(0x10,"报警清除");

	/**
	 * 功能码的十进制整数
	 */
	private int code;
	/**
	 * 功能码的功能描述
	 */
	private String describe;

	/**
	 * 构造器
	 * 
	 * @param code     功能码
	 * @param describe 功能描述
	 */
	private HeatConductBoardFunctionCode(int code, String describe) {
		this.code = code;
		this.describe = describe;
	}

	/**
	 * 获得指定功能码的功能码对象
	 * 
	 * @param code 功能码
	 * @return 功能码对象
	 */
	public static HeatConductBoardFunctionCode getCode(int code) {
		for (HeatConductBoardFunctionCode col : HeatConductBoardFunctionCode.values()) {
			if (col.getCode() == code) {
				return col;
			}
		}
		return null;
	}

	public int getCode() {
		return code;
	}

	/**
	 * 获得功能码描述
	 * 
	 * @return
	 */
	public String getDescribe() {
		return describe;
	}

}
