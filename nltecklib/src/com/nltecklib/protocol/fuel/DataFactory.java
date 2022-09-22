package com.nltecklib.protocol.fuel;

import com.nltecklib.protocol.fuel.Environment.Orient;
import com.nltecklib.protocol.fuel.Environment.ProtocolType;
import com.nltecklib.protocol.fuel.alert.ABoardBuzzerData;
import com.nltecklib.protocol.fuel.alert.ABoardCalPickupData;
import com.nltecklib.protocol.fuel.alert.ABoardColorLightData;
import com.nltecklib.protocol.fuel.alert.ABoardPickupData;
import com.nltecklib.protocol.fuel.alert.ABoardWorkModeData;
import com.nltecklib.protocol.fuel.alert.AlertEnviroment.AlertCode;
import com.nltecklib.protocol.fuel.control.CBoardAlertClearData;
import com.nltecklib.protocol.fuel.control.CBoardAssociationModeData;
import com.nltecklib.protocol.fuel.control.CBoardCalPickupData;
import com.nltecklib.protocol.fuel.control.CBoardEFFlowData;
import com.nltecklib.protocol.fuel.control.CBoardFlowData;
import com.nltecklib.protocol.fuel.control.CBoardN2Data;
import com.nltecklib.protocol.fuel.control.CBoardN2StateData;
import com.nltecklib.protocol.fuel.control.CBoardPIDData;
import com.nltecklib.protocol.fuel.control.CBoardPickupData;
import com.nltecklib.protocol.fuel.control.CBoardPressureUpLimitData;
import com.nltecklib.protocol.fuel.control.CBoardPumpData;
import com.nltecklib.protocol.fuel.control.CBoardSolidRelayData;
import com.nltecklib.protocol.fuel.control.CBoardSovData;
import com.nltecklib.protocol.fuel.control.CBoardStackPressDiffData;
import com.nltecklib.protocol.fuel.control.CBoardTempData;
import com.nltecklib.protocol.fuel.control.CBoardTempUpLimitData;
import com.nltecklib.protocol.fuel.control.CBoardUpdateData;
import com.nltecklib.protocol.fuel.control.CBoardWaterData;
import com.nltecklib.protocol.fuel.control.CBoardWorkModeData;
import com.nltecklib.protocol.fuel.control.CboardCapacityUpperLimitData;
import com.nltecklib.protocol.fuel.control.ControlEnviroment.ControlCode;
import com.nltecklib.protocol.fuel.flow.FBoardAssociationModeData;
import com.nltecklib.protocol.fuel.flow.FBoardCalPickUpData;
import com.nltecklib.protocol.fuel.flow.FBoardCalWriteData;
import com.nltecklib.protocol.fuel.flow.FBoardElecLoadData;
import com.nltecklib.protocol.fuel.flow.FBoardElecLoadSwitchData;
import com.nltecklib.protocol.fuel.flow.FBoardFlowData;
import com.nltecklib.protocol.fuel.flow.FBoardFlowModeData;
import com.nltecklib.protocol.fuel.flow.FBoardFlowModeParamData;
import com.nltecklib.protocol.fuel.flow.FBoardH2LevelData;
import com.nltecklib.protocol.fuel.flow.FBoardPickupData;
import com.nltecklib.protocol.fuel.flow.FBoardPressureUpLimitData;
import com.nltecklib.protocol.fuel.flow.FBoardSolidRelayData;
import com.nltecklib.protocol.fuel.flow.FBoardSovData;
import com.nltecklib.protocol.fuel.flow.FBoardStackPressDiffData;
import com.nltecklib.protocol.fuel.flow.FBoardTempUpLimitData;
import com.nltecklib.protocol.fuel.flow.FBoardTemperatureData;
import com.nltecklib.protocol.fuel.flow.FBoardUpdateData;
import com.nltecklib.protocol.fuel.flow.FBoardVariablePmpData;
import com.nltecklib.protocol.fuel.flow.FBoardWorkModeData;
import com.nltecklib.protocol.fuel.flow.FboardAlertClearData;
import com.nltecklib.protocol.fuel.flow.FboardElecLoadConnectData;
import com.nltecklib.protocol.fuel.flow.FboardPIDData;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardAlertClearData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardAssociationModeData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardBigCycleData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardCalibrationPickupData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardFunctionCode;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardPIDData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardPickupData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardPressureUpLimitData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardRotateSpeedData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardSovData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardStackPressDiffData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardTempAlertData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardTempData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardTempUpLimitData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardTransducerData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardUpdateData;
import com.nltecklib.protocol.fuel.heatConduct.HeatConductBoardWorkModeData;
import com.nltecklib.protocol.fuel.main.AlertClearData;
import com.nltecklib.protocol.fuel.main.AlertData;
import com.nltecklib.protocol.fuel.main.BoardLimitData;
import com.nltecklib.protocol.fuel.main.ControlBoardUpdateData;
import com.nltecklib.protocol.fuel.main.DateData;
import com.nltecklib.protocol.fuel.main.DiffPressureProtectData;
import com.nltecklib.protocol.fuel.main.ElecLoadConnectData;
import com.nltecklib.protocol.fuel.main.ElecLoadData;
import com.nltecklib.protocol.fuel.main.FlowBoardUpdateData;
import com.nltecklib.protocol.fuel.main.FlowControlData;
import com.nltecklib.protocol.fuel.main.FlowModeData;
import com.nltecklib.protocol.fuel.main.FlowProtectData;
import com.nltecklib.protocol.fuel.main.HeartData;
import com.nltecklib.protocol.fuel.main.HeatBoardUpdateData;
import com.nltecklib.protocol.fuel.main.HydrogenAlertData;
import com.nltecklib.protocol.fuel.main.LinkageModeData;
import com.nltecklib.protocol.fuel.main.LogData;
import com.nltecklib.protocol.fuel.main.LowerAlertData;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.fuel.main.PIDData;
import com.nltecklib.protocol.fuel.main.PickUpData;
import com.nltecklib.protocol.fuel.main.PressureProtectData;
import com.nltecklib.protocol.fuel.main.ProcessData;
import com.nltecklib.protocol.fuel.main.ProtectBoardUpdateData;
import com.nltecklib.protocol.fuel.main.ProtectData;
import com.nltecklib.protocol.fuel.main.PumpData;
import com.nltecklib.protocol.fuel.main.SetData;
import com.nltecklib.protocol.fuel.main.SingleVolProtectData;
import com.nltecklib.protocol.fuel.main.SolenoidData;
import com.nltecklib.protocol.fuel.main.StackData;
import com.nltecklib.protocol.fuel.main.StackVolProtectData;
import com.nltecklib.protocol.fuel.main.StopProcessData;
import com.nltecklib.protocol.fuel.main.SystemVersionData;
import com.nltecklib.protocol.fuel.main.TemperatureControlData;
import com.nltecklib.protocol.fuel.main.TemperatureProtectData;
import com.nltecklib.protocol.fuel.main.TestData;
import com.nltecklib.protocol.fuel.main.VariablePumpData;
import com.nltecklib.protocol.fuel.main.VolAndCurPickUpData;
import com.nltecklib.protocol.fuel.main.VoltBoardUpdateData;
import com.nltecklib.protocol.fuel.main.VoltBoardVersionInfoData;
import com.nltecklib.protocol.fuel.main.WaterSystemData;
import com.nltecklib.protocol.fuel.main.WorkModeData;
import com.nltecklib.protocol.fuel.protect.PBoardActionDelayData;
import com.nltecklib.protocol.fuel.protect.PBoardCalPickupData;
import com.nltecklib.protocol.fuel.protect.PBoardH2ConcentrationData;
import com.nltecklib.protocol.fuel.protect.PBoardN2TimeDurationData;
import com.nltecklib.protocol.fuel.protect.PBoardPickupData;
import com.nltecklib.protocol.fuel.protect.PBoardPowerData;
import com.nltecklib.protocol.fuel.protect.PBoardPumpData;
import com.nltecklib.protocol.fuel.protect.PBoardRGBLightData;
import com.nltecklib.protocol.fuel.protect.PBoardSovData;
import com.nltecklib.protocol.fuel.protect.PBoardStateReadData;
import com.nltecklib.protocol.fuel.protect.PBoardUpdateData;
import com.nltecklib.protocol.fuel.protect.PBoardUpdateModeData;
import com.nltecklib.protocol.fuel.protect.PBoardWorkModeData;
import com.nltecklib.protocol.fuel.protect.ProtectEnviroment.ProtectCode;
import com.nltecklib.protocol.fuel.temp.TBoardCalPickupData;
import com.nltecklib.protocol.fuel.temp.TBoardFlowData;
import com.nltecklib.protocol.fuel.temp.TBoardPIDData;
import com.nltecklib.protocol.fuel.temp.TBoardPickupData;
import com.nltecklib.protocol.fuel.temp.TBoardSovData;
import com.nltecklib.protocol.fuel.temp.TBoardTempData;
import com.nltecklib.protocol.fuel.temp.TBoardVariablePumpData;
import com.nltecklib.protocol.fuel.temp.TBoardVariablePumpSwitchData;
import com.nltecklib.protocol.fuel.temp.TBoardWorkModeData;
import com.nltecklib.protocol.fuel.temp.TboardTemperatureAlertData;
import com.nltecklib.protocol.fuel.temp.TempEnviroment.TempCode;
import com.nltecklib.protocol.fuel.voltage.VBoardAlertValueSetData;
import com.nltecklib.protocol.fuel.voltage.VBoardCalChannelData;
import com.nltecklib.protocol.fuel.voltage.VBoardCalWriteData;
import com.nltecklib.protocol.fuel.voltage.VBoardExceptionQueryData;
import com.nltecklib.protocol.fuel.voltage.VBoardExceptionStateDealData;
import com.nltecklib.protocol.fuel.voltage.VBoardMeasureChannelData;
import com.nltecklib.protocol.fuel.voltage.VBoardVersionData;
import com.nltecklib.protocol.fuel.voltage.VBoardVersionWriteData;
import com.nltecklib.protocol.fuel.voltage.VBoardVolModeData;
import com.nltecklib.protocol.fuel.voltage.VBoardVolPickUpData;
import com.nltecklib.protocol.fuel.voltage.VoltageEnviroment.VolCode;

/**
 * 协议工厂类
 * 
 * @author Administrator
 *
 */
public class DataFactory {

	/**
	 * 根据code生成空协议对象
	 * 
	 * @param orient
	 * @param code
	 * @return
	 */
	public static Decorator createData(Orient orient, ProtocolType type, int code) {

		Decorator data = null;
		if (type == ProtocolType.MAIN) {
			data = createMainData(orient, MainCode.getCode(code));
		}
		if (type == ProtocolType.VOL) {
			data = createVolData(orient, VolCode.getCode(code));
		}
		if (type == ProtocolType.TEMP) {
			data = createTempData(orient, TempCode.getCode(code));
		}
		if (type == ProtocolType.FLOW) {
			data = createFlowData(orient, FlowCode.getCode(code));
		}
		if (type == ProtocolType.CONTROL) {
			data = createControlData(orient, ControlCode.getCode(code));
		}
		if (type == ProtocolType.ALERT) {
			data = createAlertData(orient, AlertCode.getCode(code));
		}
		if (type == ProtocolType.PROTECT) {
			data = createProtectData(orient, ProtectCode.getCode(code));
		}
		if (type == ProtocolType.HEATCONDUCT) {
			data = createHeatConductData(orient, HeatConductBoardFunctionCode.getCode(code));
		}
		return data;
	}

	private static Decorator createControlData(Orient orient, ControlCode code) {
		Data data = null;
		switch (code) {
		case PICK_UP_CODE:
			data = new CBoardPickupData();
			break;
		case SOV_CODE:
			data = new CBoardSovData();
			break;
		case WT_CODE:
			data = new CBoardWaterData();
			break;
		case N2_CODE:
			data = new CBoardN2Data();
			break;
		case N2_STATE_CODE:
			data = new CBoardN2StateData();
			break;
		case WORK_MODE_CODE:
			data = new CBoardWorkModeData();
			break;
		case CAL_PICKUP_CODE:
			data = new CBoardCalPickupData();
			break;
		case FLOW_CODE:
			data = new CBoardFlowData();
			break;
		case PUMP_CODE:
			data = new CBoardPumpData();
			break;
		case ASSOCIATION_CODE:
			data = new CBoardAssociationModeData();
			break;
		case PID_CODE:
			data = new CBoardPIDData();
			break;
		case TEMP_CODE:
			data = new CBoardTempData();
			break;
		case EF_FLOW_CODE:
			data = new CBoardEFFlowData();
			break;
		case SOLID_RELAY_CODE:
			data = new CBoardSolidRelayData();
			break;
		case PRESSURE_UP_LIMIT:
			data = new CBoardPressureUpLimitData();
			break;
		case TEMP_UP_LIMIT:
			data = new CBoardTempUpLimitData();
			break;
		case STACK_PRESSURE_DIFFERENCE:
			data = new CBoardStackPressDiffData();
			break;
		case EF_CAPACITY_UPPER_LIMIT_CODE:
			data = new CboardCapacityUpperLimitData();
			break;
		case ALERT_CLEAR_CODE:
			data = new CBoardAlertClearData();
			break;
		case UPDATE_CODE:
			data = new CBoardUpdateData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	private static Decorator createFlowData(Orient orient, FlowCode code) {
		Data data = null;
		switch (code) {
		case VARIABLE_PUMP_CODE:
			data = new FBoardVariablePmpData();
			break;
		case CAL_DATA_CODE:
			data = new FBoardCalWriteData();
			break;
		case CAL_PICKUP_CODE:
			data = new FBoardCalPickUpData();
			break;
		case FLOW_CODE:
			data = new FBoardFlowData();
			break;
		case PICK_UP_CODE:
			data = new FBoardPickupData();
			break;
		case SOV_CODE:
			data = new FBoardSovData();
			break;
		case ELEC_LOAD_CODE:
			data = new FBoardElecLoadData();
			break;
		case ELEC_LOAD_SWITCH_CODE:
			data = new FBoardElecLoadSwitchData();
			break;
		case WORK_MODE_CODE:
			data = new FBoardWorkModeData();
			break;
		case FLOW_MODE_CODE:
			data = new FBoardFlowModeData();
			break;
		case FLOW_MODE_PARAM_CODE:
			data = new FBoardFlowModeParamData();
			break;
		case H2_LEVEL:
			data = new FBoardH2LevelData();
			break;
		case PID_CODE:
			data = new FboardPIDData();
			break;
		case TEMPERATURE_CODE:
			data = new FBoardTemperatureData();
			break;
		case ASSOCIATION_CODE:
			data = new FBoardAssociationModeData();
			break;
		case SOLID_RELAY_CODE:
			data = new FBoardSolidRelayData();
			break;
		case PRESSURE_UP_LIMIT:
			data = new FBoardPressureUpLimitData();
			break;
		case TEMP_UP_LIMIT:
			data = new FBoardTempUpLimitData();
			break;
		case STACK_PRESSURE_DIFFERENCE:
			data = new FBoardStackPressDiffData();
			break;
		case ALERT_CLEAR_CODE:
			data = new FboardAlertClearData();
			break;
		case UPDATE_CODE:
			data = new FBoardUpdateData();
			break;
		case ELEC_LOAD_CONNECT_CODE:
			data=new FboardElecLoadConnectData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	@Deprecated
	private static Decorator createTempData(Orient orient, TempCode code) {
		Data data = null;
		switch (code) {
		case VARIABLE_PUMP_CODE:
			data = new TBoardVariablePumpData();
			break;
		case FLOW_CODE:
			data = new TBoardFlowData();
			break;
		case PICK_UP_CODE:
			data = new TBoardPickupData();
			break;
		case SOV_CODE:
			data = new TBoardSovData();
			break;
		case TEMP_ALERT_CODE:
			data = new TboardTemperatureAlertData();
			break;
		case TEMP_CODE:
			data = new TBoardTempData();
			break;
		case VARIABLE_PUMP_SWITCH_CODE:
			data = new TBoardVariablePumpSwitchData();
			break;
		case WORK_MODE_CODE:
			data = new TBoardWorkModeData();
			break;
		case PID_CODE:
			data = new TBoardPIDData();
			break;
		case CAL_PICKUP_CODE:
			data = new TBoardCalPickupData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	private static Decorator createHeatConductData(Orient orient, HeatConductBoardFunctionCode code) {
		Data data = null;
		switch (code) {
		case PICKUP:
			data = new HeatConductBoardPickupData();
			break;
		case TEMP_ALERT:
			data = new HeatConductBoardTempAlertData();
			break;
		case TEMP:
			data = new HeatConductBoardTempData();
			break;
		case SOV:
			data = new HeatConductBoardSovData();
			break;
		case ROTATE_SPEED:
			data = new HeatConductBoardRotateSpeedData();
			break;
		case TRANSDUCER:
			data = new HeatConductBoardTransducerData();
			break;
		case WORK_MODE:
			data = new HeatConductBoardWorkModeData();
			break;
		case PID:
			data = new HeatConductBoardPIDData();
			break;
		case CALIBRATION_PICKUP:
			data = new HeatConductBoardCalibrationPickupData();
			break;
		case ASSOCIATION_MODE:
			data = new HeatConductBoardAssociationModeData();
			break;
		case BIG_CYCLE:
			data = new HeatConductBoardBigCycleData();
			break;
		case PRESSURE_UP_LIMIT:
			data = new HeatConductBoardPressureUpLimitData();
			break;
		case TEMP_UP_LIMIT:
			data = new HeatConductBoardTempUpLimitData();
			break;
		case STACK_PRESSURE_DIFFERENCE:
			data = new HeatConductBoardStackPressDiffData();
			break;
		case ALERT_CLEAR_CODE:
			data = new HeatConductBoardAlertClearData();
			break;
		case UPDATE_CODE:
			data = new HeatConductBoardUpdateData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	private static Decorator createVolData(Orient orient, VolCode code) {

		Data data = null;
		switch (code) {
		case VOL_PICKUP_CODE:
			data = new VBoardVolPickUpData();
			break;
		case CAL_CHANNEL_CODE:
			data = new VBoardCalChannelData();
			break;
		case CAL_DATA_CODE:
			data = new VBoardCalWriteData();
			break;
		case WORK_MODE_CODE:
			data = new VBoardVolModeData();
			break;
		case ALERT_VALUE_SET_CODE:
			data = new VBoardAlertValueSetData();
			break;
		case MEASURE_CHANNEL_CODE:
			data = new VBoardMeasureChannelData();
			break;
		case EXCEPTION_QUERY:
			data = new VBoardExceptionQueryData();
			break;
		case EXCEPTION_DEAL:
			data = new VBoardExceptionStateDealData();
			break;
		case VERSION_INFO:
			data = new VBoardVersionData();
			break;
		case VERSION_WRITE:
			data = new VBoardVersionWriteData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	private static Decorator createProtectData(Orient orient, ProtectCode code) {

		Data data = null;
		switch (code) {
		case PICK_UP_CODE:
			data = new PBoardPickupData();
			break;
		case SOV_CODE:
			data = new PBoardSovData();
			break;
		case POWER_CODE:
			data = new PBoardPowerData();
			break;
		case STATE_READ_CODE:
			data = new PBoardStateReadData();
			break;
		case H2_CONCENTRATION_CODE:
			data = new PBoardH2ConcentrationData();
			break;
		case RGB_LIGHT:
			data = new PBoardRGBLightData();
			break;
		case PUMP_CODE:
			data = new PBoardPumpData();
			break;
		case WORK_MODE_CODE:
			data = new PBoardWorkModeData();
			break;
		case CAL_PICKUP_CODE:
			data = new PBoardCalPickupData();
			break;
		case UPDATE_MODE_CODE:
			data = new PBoardUpdateModeData();
			break;
		case UPDATE_CODE:
			data = new PBoardUpdateData();
			break;
		case N2_TIME_DURATION_CODE:
			data = new PBoardN2TimeDurationData();
			break;
		case ACTION_DELAY:
			data = new PBoardActionDelayData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	@Deprecated
	private static Decorator createAlertData(Orient orient, AlertCode code) {
		Data data = null;
		switch (code) {
		case BUZZER_CODE:
			data = new ABoardBuzzerData();
			break;
		case COLOR_LIGHT_CODE:
			data = new ABoardColorLightData();
			break;
		case PICK_UP_CODE:
			data = new ABoardPickupData();
			break;
		case WORK_MODE_CODE:
			data = new ABoardWorkModeData();
			break;
		case CAL_PICKUP_CODE:
			data = new ABoardCalPickupData();
			break;
		}
		if (data == null) {
			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}

	private static Decorator createMainData(Orient orient, MainCode code) {

		Data data = null;
		switch (code) {
		case SOLENOID_VALUE_CODE:
			data = new SolenoidData();
			break;
		case TEMPERATURE_CONTROL_CODE:
			data = new TemperatureControlData();
			break;
		case TEMPERATURE_PROTECT_CODE:
			data = new TemperatureProtectData();
			break;
		case DIFFPRESSURE_PROTECT_CODE:
			data = new DiffPressureProtectData();
			break;
		case PRESSURE_PROTECT_CODE:
			data = new PressureProtectData();
			break;
		case FLOW_CONTROL_CODE:
			data = new FlowControlData();
			break;
		case FLOW_PROTECT_CODE:
			data = new FlowProtectData();
			break;
		case RESERVOIR_SYSTEM_CODE:
			data = new WaterSystemData();
			break;
		case PUMP_CODE:
			data = new PumpData();
			break;
		case VARIABLE_PUMP_CODE:
			data = new VariablePumpData();
			break;
//	case VARIABLE_PUMP_PICKUP_CODE:
//	    data = new VariablePumpPickupData();
//	    break;
		case ALERT_CODE:
			data = new AlertData();
			break;
		case STOP_PROCESS_CODE:
			data = new StopProcessData();
			break;
//	case TEMP_PICKUP_CODE:
//	    data = new TempPickUpData();
//	    break;
//	case PRES_PICKUP_CODE:
//	    data = new PresPickUpData();
//	    break;
//	case FLOW_PICKUP_CODE:
//	    data = new FlowPickUpData();
//	    break;
		case WORK_MODE_CODE:
			data = new WorkModeData();
			break;
		case SYSTEM_VERSION_CODE:
			data = new SystemVersionData();
			break;
//	case SWITCH_PICKUP_CODE:
//	    data = new SwitchPickUpData();
//	    break;
		case FLOW_MODE_CODE:
			data = new FlowModeData();
			break;
		case LINKAGE_MODE_CODE:
			data = new LinkageModeData();
			break;
		case PROCESS_CODE:
			data = new ProcessData();
			break;
		case VOL_CUR_PICKUP_CODE:
			data = new VolAndCurPickUpData();
			break;
		case ELEC_LOAD_CODE:
			data = new ElecLoadData();
			break;
		case STACK_CODE:
			data = new StackData();
			break;
		case HYDROGEN_ALERT_CODE:
			data = new HydrogenAlertData();
			break;
		case HEART:
			data = new HeartData();
			break;
		case SINGLE_VOL_PROTECT_CODE:
			data = new SingleVolProtectData();
			break;
		case STACK_VOL_PROTECT_CODE:
			data = new StackVolProtectData();
			break;
		case LOWER_ALERT_CODE:
			data = new LowerAlertData();
			break;
//	case CLOSE_BUZZER_CODE:
//	    data = new CloseBuzzerData();
//	    break;
//	case H2_LEVEL_CODE:
//	    data = new H2LevelData();
//	    break;
//	case PID_CODE:
//	    data = new PIDData();
//	    break;
		case PICKUP_CODE:
			data = new PickUpData();
			break;
		case LOG_CODE:
			data = new LogData();
			break;
		case DATE_CODE:
			data = new DateData();
			break;
		case PROTECT_CODE:
			data = new ProtectData();
			break;
		case SET_CODE:
			data = new SetData();
			break;
		case BOARD_LIMIT_CODE:
			data = new BoardLimitData();
			break;
		case TEST_CODE:
			data = new TestData();
			break;
		case PID_CODE:
			data = new PIDData();
			break;
		case ALERT_CLEAR_CODE:
			data = new AlertClearData();
			break;
		case VOLT_VERSION_INFO_CODE:
			data = new VoltBoardVersionInfoData();
			break;
		case VOLT_UPDATE_CODE:
			data = new VoltBoardUpdateData();
			break;
		case HEAT_UPDATE_CODE:
			data = new HeatBoardUpdateData();
			break;
		case FLOW_UPDATE_CODE:
			data = new FlowBoardUpdateData();
			break;
		case CONTROL_UPDATE_CODE:
			data = new ControlBoardUpdateData();
			break;
		case PROTECT_UPDATE_CODE:
			data = new ProtectBoardUpdateData();
			break;
		case ELEC_LOAD_CONNECT_CODE:
			data=new ElecLoadConnectData();
			break;
		}
		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		// data.setNumber(number);

		Decorator dec = null;
		switch (orient) {
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, true);
			break;

		}
		return dec;
	}
}
