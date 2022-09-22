package com.nltecklib.protocol.fuel.flow;

import com.nltecklib.protocol.fuel.Environment.Code;

public class FlowEnviroment {
	/**
	 * 流量板功能码
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum FlowCode implements Code {

		PICK_UP_CODE(1), FLOW_CODE(2), CAL_PICKUP_CODE(3), CAL_DATA_CODE(4), VARIABLE_PUMP_CODE(5), SOV_CODE(6),
		ELEC_LOAD_CODE(7), ELEC_LOAD_SWITCH_CODE(8), WORK_MODE_CODE(9), TEMPERATURE_CODE(0x0a), PID_CODE(0x0b),
		ASSOCIATION_CODE(0x0c),TEMP_ALERT(0x0d), SOLID_RELAY_CODE(0x0e), PRESSURE_UP_LIMIT(0x0f), TEMP_UP_LIMIT(0x10),
		STACK_PRESSURE_DIFFERENCE(0x11), UPDATE_CODE(0x12), ALERT_CLEAR_CODE(0x13),ELEC_LOAD_CONNECT_CODE(0x14), FLOW_MODE_CODE(110),
		FLOW_MODE_PARAM_CODE(111), H2_LEVEL(112);

		private int code;

		private FlowCode(int funCode) {

			this.code = funCode;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static FlowCode getCode(int code) {
			for (FlowCode flow : FlowCode.values()) {
				if (flow.getCode() == code) {
					return flow;
				}
			}
			return null;
		}

	}

	public enum Pole {
		POSITIVE, NEGATIVE;
	}

	public enum Mode {
		SET, AUTO;
	}

}
