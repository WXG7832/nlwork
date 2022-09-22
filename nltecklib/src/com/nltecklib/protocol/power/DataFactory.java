package com.nltecklib.protocol.power;

import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.Environment.Orient;
import com.nltecklib.protocol.power.calBox.calBoard.CALIBRATE_DOT;
import com.nltecklib.protocol.power.calBox.calBoard.CONDUCTANCE;
import com.nltecklib.protocol.power.calBox.calBoard.CONNECT_METER;
import com.nltecklib.protocol.power.calBox.calBoard.CalBoardEnvironment.CalBoardCode;
import com.nltecklib.protocol.power.calBox.calBoard.MATCH_DOT;
import com.nltecklib.protocol.power.calBox.calBoard.MEASURE_DOT;
import com.nltecklib.protocol.power.calBox.calBoard.TEMP_SWITCH;
import com.nltecklib.protocol.power.calBox.calBoard.UPDATE;
import com.nltecklib.protocol.power.calBox.calBoard.UPDATE_SWITCH;
import com.nltecklib.protocol.power.calBox.calBox_device.MBChannelSwitchData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbBaseInfoData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbDriverModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbModeChangeData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbSelfCheckData;
import com.nltecklib.protocol.power.calBox.calSoft.AUTO_MATCH;
import com.nltecklib.protocol.power.calBox.calSoft.CONNECT_DEVICE;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.power.calBox.calSoft.HEART_BEAT;
import com.nltecklib.protocol.power.calBox.calSoft.IP;
import com.nltecklib.protocol.power.calBox.calSoft.JSON_DATA;
import com.nltecklib.protocol.power.calBox.calSoft.MANUAL_MATCH;
import com.nltecklib.protocol.power.calBox.calSoft.PUSH;
import com.nltecklib.protocol.power.calBox.calSoft.TEST_QUEUE;
import com.nltecklib.protocol.power.calBox.calSoft.TIME;
import com.nltecklib.protocol.power.calBox.calSoft.WORK_COMPLETE;
import com.nltecklib.protocol.power.check.BattProtectData;
import com.nltecklib.protocol.power.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.power.check.ChnnCalData;
import com.nltecklib.protocol.power.check.ChnnPickData;
import com.nltecklib.protocol.power.check.ChnnVarData;
import com.nltecklib.protocol.power.check.DeviceStateData;
import com.nltecklib.protocol.power.check.FlashData;
import com.nltecklib.protocol.power.check.SwitchData;
import com.nltecklib.protocol.power.check.UpgradeData;
import com.nltecklib.protocol.power.check.VersionData;
import com.nltecklib.protocol.power.check.VolPickData;
import com.nltecklib.protocol.power.check.WorkEnvData;
import com.nltecklib.protocol.power.driver.DriverAddressData;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData;
import com.nltecklib.protocol.power.driver.DriverCalculateData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverChannelTemperData;
import com.nltecklib.protocol.power.driver.DriverChannelVoltData;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.power.driver.DriverHeartbeatData;
import com.nltecklib.protocol.power.driver.DriverInfoData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.protocol.power.driver.DriverModeData;
import com.nltecklib.protocol.power.driver.DriverModuleSwitchData;
import com.nltecklib.protocol.power.driver.DriverOperateData;
import com.nltecklib.protocol.power.driver.DriverPickupData;
import com.nltecklib.protocol.power.driver.DriverPoleData;
import com.nltecklib.protocol.power.driver.DriverProtectData;
import com.nltecklib.protocol.power.driver.DriverResumeData;
import com.nltecklib.protocol.power.driver.DriverStepData;
import com.nltecklib.protocol.power.driver.DriverUpgradeData;
import com.nltecklib.protocol.power.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.power.main.MainModeData;
import com.nltecklib.protocol.power.temper.OutControlData;
import com.nltecklib.protocol.power.temper.TempAddressData;
import com.nltecklib.protocol.power.temper.TempSwPickData;
import com.nltecklib.protocol.power.temper.TemperAdjustData;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.power.temper.UpgradeForTemperData;
import com.nltecklib.protocol.power.temper.VersionForTemperData;
import com.nltecklib.protocol.power.temper.WorkPatternForTemperData;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年12月15日 下午3:21:08 协议工厂
 */
public class DataFactory {

	/**
	 * 生成驱动板协议实体
	 * 
	 * @author wavy_zheng 2021年12月15日
	 * @param orient
	 * @param code
	 * @return
	 */
	public static Decorator createData(Orient orient, Code code) {

		Decorator data = null;
		if (code instanceof DriverCode) {
			data = createDriverData(orient, (DriverCode) code);
		}

		return data;

	}

	public static Decorator createDriverData(Orient orient, DriverCode code) {

		Data data = null;
		switch (code) {
		case PoleCode:
			data = new DriverPoleData();
			break;
		case InfoCode:
			data = new DriverInfoData();
			break;
		case ModeCode:
			data = new DriverModeData();
			break;
		case HeartbeatCode:
			data = new DriverHeartbeatData();
			break;
		case OperateCode:
			data = new DriverOperateData();
			break;
		case StepCode:
			data = new DriverStepData();
			break;
		case PickupCode:
			data = new DriverPickupData();
			break;
		case ProtectCode:
			data = new DriverProtectData();
			break;
		case CheckCode:
			data = new DriverCheckData();
			break;
		case CalibrateCode:
			data = new DriverCalibrateData();
			break;
		case CalculateCode:
			data = new DriverCalculateData();
			break;
		case FlashParamCode:
			data = new DriverCalParamSaveData();
			break;
		case ModuleSwitchCode:
			data = new DriverModuleSwitchData();
			break;
		case ResumeCode:
			data = new DriverResumeData();
			break;
		case UpgradeCode:
			data = new DriverUpgradeData();
			break;
		case MatchAdcCode:
			data = new DriverMatchAdcData();
			break;
		case DriverAddressCode:
			data = new DriverAddressData();
			break;
		case ChannelTemperCode:
			data = new DriverChannelTemperData();
			break;
		case ChannelVoltCode:
			data = new DriverChannelVoltData();
			break;
			
		}

		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;

	}

	public static Decorator createCheckData(Orient orient, CheckCode code) {
		Data data = null;
		switch (code) {
		case WorkEnvCode:
			data = new WorkEnvData();
			break;
		case ChnnPickCode:
			data = new ChnnPickData();
			break;
		case SwitchCode:
			data = new SwitchData();
			break;
		case VolPickCode:
			data = new VolPickData();
			break;
		case ChnnCalCode:
			data = new ChnnCalData();
			break;
		case ChnnVarCode:
			data = new ChnnVarData();
			break;
		case FlashCode:
			data = new FlashData();
			break;
		case BatteryProtecCode:
			data = new BattProtectData();
			break;
		case UpgradeCode:
			data = new UpgradeData();
			break;
		case VersionCode:
			data = new VersionData();
			break;
		case DeviceStateCode:
			data = new DeviceStateData();
			break;

		}

		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;
	}

	public static Decorator createTemperData(Orient orient, TemperCode code) {
		Data data = null;
		switch (code) {
		case TempSwPickCode:
			data = new TempSwPickData();
			break;
		case OutControlCode:
			data = new OutControlData();
			break;
		case UpgradeCode:
			data = new UpgradeForTemperData();
			break;
		case VersionCode:
			data = new VersionForTemperData();
			break;
		case WorkPattern:
			data = new WorkPatternForTemperData();
			break;
		case AddressCode:
			data = new TempAddressData();
			break;
		case TemperAdjustCode:
			data = new TemperAdjustData();
			break;

		}

		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;
	}

	public static Decorator createMainData(Orient orient, MainCode code) {

		Data data = null;
		switch (code) {
		// case DateCode:
		// data = new DateData();
		// break;
		// case PoleCode:
		// data = new PoleData();
		// break;
		// case BaseCountCode:
		// data = new BaseCountData();
		// break;
		// case LogicChnSwitchCode:
		// data = new UnitChannelSwitchData();
		// break;
		// case ChnOperateCode:
		// data = new ChannelOptData();
		// break;
		// case ProcedureCode:
		// data = new ProcedureData();
		// break;
		// case PickupCode:
		// data = new PickupData();
		// break;
		// case FirstCCProtectCode:
		// data = new FirstCCProtectData();
		// break;
		//
		// case DeviceStateCode:
		// data = new DeviceStateQueryData();
		// break;
		case ModeCode:
			data = new MainModeData();
			break;
		// case TempControlCode:
		// data = new TempData();
		// break;
		// case SleepProtectCode:
		// data = new SlpProtectData();
		// break;
		// case CCProtectCode:
		// data = new CCProtectData();
		// break;
		// case CVProtectCode:
		// data = new CVProtectData();
		// break;
		// case DCProtectCode:
		// data = new DCProtectData();
		// break;
		// case DeviceProtectCode:
		// data = new DeviceProtectData();
		// break;
		// case TestNameCode:
		// data = new TestNameData();
		// break;
		// case CheckProtectCode:
		// data = new CheckVoltProtectData();
		// break;
		// case OfflineAcquireCode:
		// data = new OfflineAcquireData();
		// break;
		// case OfflineUploadCode:
		// data = new OfflinePickupData();
		// break;
		// case PressureChangeCode:
		// data = new PressureChangeProtectData();
		// break;
		// case SaveParamCode:
		// data = new SaveParamData();
		// break;
		// case IPAddressCode:
		// data = new IPAddressData();
		// break;
		// case StartEndCheckCode:
		// data = new StartEndCheckData();
		// break;
		// case DebugControlCode:
		// data = new DebugControlData();
		// break;
		// case EnergyCode:
		// data = new EnergySaveData();
		// break;
		// case UpgradeCode:
		// data = new UpgradeProgramData();
		// break;
		// case UpgradeProgressCode:
		// data = new UpgradeProgressData();
		// break;
		// case ChnSwitchCode:
		// data = new ChannelSwitchData();
		// break;
		// case CylinderPressureCode:
		// data = new CylinderPressureProtectData();
		// break;
		// case OfflineRunningCode:
		// data = new OfflineRunningData();
		// break;
		// case ProcedureModeCode:
		// data = new ProcedureModeData();
		// break;
		// case PushSwitchCode:
		// data = new PushSwitchData();
		// break;
		// case SolenoidValveSwitchCode:
		// data = new SolenoidValveData();
		// break;
		// case TrayTempUpperCode:
		// data = new SolenoidValveTempProtectData();
		// break;
		// case BeepAlertCode:
		// data = new VoiceAlertData();
		// break;
		// case ExChnOperateCode:
		// data = new ExChnsOperateData();
		// break;
		// case ControlUnitCode:
		// data = new ControlUnitData();
		// break;
		// case AlertCode:
		// data = new AlertData();
		// break;
		// case AllowStepSkipCode:
		// data = new AllowStepSkipData();
		// break;
		//
		// case versionCode:
		// data = new VersionData();
		// break;
		// case SelfCheckCode:
		// data = new SelfCheckData();
		// break;
		// case ResetCode:
		// data = new ResetData();
		// break;
		// case RecoveryCode:
		// data = new RecoveryData();
		// break;
		// case DriverChnIndexDefineCode:
		// data = new DriverChnIndexDefineData();
		// break;
		// case JsonProcedureCode:
		// data = new JsonProcedureData();
		// break;
		// case JsonProtectionCode:
		// data = new JsonProtectionData();
		// break;
		// case JsonDeviceProtectCode:
		// data = new JsonDeviceProtectData();
		// break;
		// case JsonPoleCode:
		// data = new JsonPoleData();
		// break;
		// case ClearSmogCode:
		// data = new ClearSmogData();
		// break;
		// case EnableCheckProtectionCode:
		// data = new EnableCheckProtectionData();
		// break;
		}
		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;
	}

	public static Decorator createCalBoardData(Orient orient, CalBoardCode code) {

		Data data = null;
		switch (code) {
		case MATCH_DOT:
			data = new MATCH_DOT();
			break;
		// case CAL:
		// data = new CalibrateData();
		// break;
		case CONNECT_METER:
			data = new CONNECT_METER();
			break;
		case TEMP_SWITCH:
			data = new TEMP_SWITCH();
			break;
		case CONDUCTANCE:
			data = new CONDUCTANCE();
			break;
		// case ADDRESS_CAL:
		// data = new CalAddressData();
		// break;
		// case NEW_CAL:
		// data = new NewCalibrateData();
		// break;
		// case EXRESISTER:
		// data = new com.nltecklib.protocol.li.cal.ExCalResisterFactorData();
		// break;
		case CALIBRATE_DOT:
			data = new CALIBRATE_DOT();
			break;
		case MEASURE_DOT:
			data = new MEASURE_DOT();
			break;
		case UPDATE:
			data = new UPDATE();
			break;
		case UPDATE_SWITCH:
			data = new UPDATE_SWITCH();
			break;
		// case TempControlCode:
		// data = new TempControlData();
		// break;
		// case OverTempAlertCode:
		// data = new OverTempAlertData();
		// break;
		}
		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;
	}

	public static Decorator createCalSoftData(Orient orient, CalSoftCode code) {

		Data data = null;
		switch (code) {
		// case BaseCfgCode:
		// data = new BaseCfgData();
		// break;
		// case LogicCalculateDebugCode:
		// data = new LogicCalculateDebugData();
		// break;
		// case CalculatePlanCode:
		// data = new CalculatePlanData();
		// break;
		// case SELECT_CHN:
		// data = new SELECT_CHN();
		// break;
		// case LogicCalibrateDebugCode:
		// data = new LogicCalibrateDebugData();
		// break;
		// case CalibratePlanCode:
		// data = new CalibratePlanData();
		// break;
		// case CheckFlashWriteCode:
		// data = new CheckFlashWriteData();
		// break;
		// case DataPushCode:
		// data = new LivePushData();
		// break;
		// case DelayCode:
		// data = new DelayData();
		// break;
		// case DriverBindCode:
		// data = new DriverBindData();
		// break;
		// case LogDebugCode:
		// data = new LogDebugPushData();
		// break;
		// case LogicFlashWriteCode:
		// data = new com.nltecklib.protocol.li.PCWorkform.LogicFlashWriteData();
		// break;
		case PUSH:
			data = new PUSH();
			break;
		case CONNECT_METER:
			data = new com.nltecklib.protocol.power.calBox.calSoft.CONNECT_METER();
			break;
		// case DEVICE_STATE:
		// data = new DEVICE_STATE();
		// break;
		// case ModuleSwitchCode:
		// data = new ModuleSwitchData();
		// break;
		// case RangeCurrentPrecisionCode:
		// data = new RangeCurrentPrecisionData();
		// break;
		// case RequireDataCode:
		// data = new RequestCalculateData();
		// break;
		// case SteadyCode:
		// data = new SteadyCfgData();
		// break;
		case TEST_QUEUE:
			data = new TEST_QUEUE();
			break;
		// case CalMatchCode:
		// data = new CalMatchData();
		// break;
		// case CheckCalculateDebugCode:
		// data = new CheckCalculateDebugData();
		// break;
		case MANUAL_MATCH:
			data = new MANUAL_MATCH();
			break;
		case CONNECT_DEVICE:
			data = new CONNECT_DEVICE();
			break;
		// case UploadTestDotCode:
		// data = new UploadTestDotData();
		// break;
		case TIME:
			data = new TIME();
			break;
		case IP:
			data = new IP();
			break;
		// case BaseInfoQueryCode:
		// data = new BaseInfoQueryData();
		// break;
		// case CalibrateTerminalCode:
		// data = new CalibrateTerminalData();
		// break;
		case AUTO_MATCH:
			data = new AUTO_MATCH();
			break;
		// case CalUpdateFileCode:
		// data = new CalBoardUpdateFileData();
		// break;
		// case CalUpdateModeCode:
		// data = new CalBoardUpdateModeData();
		// break;
		// case MatchStateCode:
		// data = new MatchStateData();
		// break;
		// case WorkformUpdateCode:
		// data = new WorkformUpdateData();
		// break;
		// case SelfTestInfoCode:
		// data = new PCSelfTestInfoData();
		// break;
		// case CalMathValueCode:
		// data = new CalMatchValueData();
		// break;
		// case SelfCheckCode:
		// data = new PCSelfCheckData();
		// break;
		case HEART_BEAT:
			data = new HEART_BEAT();
			break;
		// case BaseInfoConfigCode:
		// data = new BaseInfoConfigData();
		// break;
		case WORK_COMPLETE:
			data = new WORK_COMPLETE();
			break;
		// case CalCalibrate2DebugCode:
		// data = new CalCalibrate2DebugData();
		// break;
		// case CalRelayControlDebugCode:
		// data = new CalRelayControlDebugData();
		// break;
		case JSON_DATA:
			data = new JSON_DATA();
			break;
		default:
			break;

		}
		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}
		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;

	}

	public static Decorator createCalBox_DeviceData(Orient orient, CalBoxDeviceCode code) {

		Data data = null;
		switch (code) {
		case HeartbeatCode:
			data = new com.nltecklib.protocol.power.calBox.calBox_device.MbHeartbeatData();
			break;
		case MeasureChnCode:
			data = new com.nltecklib.protocol.power.calBox.calBox_device.MbMeasureChnData();
			break;
		case CalibrateChnCode:
			data = new com.nltecklib.protocol.power.calBox.calBox_device.MbCalibrateChnData();
			break;
		case FlashParamCode:
			data = new MbFlashParamData();
			break;
		case ChannelSwitchCode:
			data = new MBChannelSwitchData();
			break;
		case MatchAdcCode:
			data = new MbMatchAdcData();
			break;
		case ModeCode:
			data = new MbModeChangeData();
			break;
		case SelfCheckCode:
			data = new MbSelfCheckData();
			break;
		case BaseInfoCode:
			data = new MbBaseInfoData();
			break;
		case DriverModeCode:
			data = new MbDriverModeChangeData();
			break;

		default:
			break;

		}
		if (data == null) {

			throw new RuntimeException("unrecognized function code:" + code);
		}

		Decorator dec = null;
		switch (orient) {
		case ALERT:
			dec = new AlertDecorator(data);
			break;
		case CONFIG:
			dec = new ConfigDecorator(data);
			break;
		case QUERY:
			dec = new QueryDecorator(data);
			break;
		case RESPONSE:
			dec = new ResponseDecorator(data, false);
			break;
		}
		return dec;
	}

}
