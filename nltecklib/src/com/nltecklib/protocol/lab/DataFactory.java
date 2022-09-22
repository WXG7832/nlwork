package com.nltecklib.protocol.lab;

import com.nltecklib.protocol.lab.Entity.ProtocolType;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Orient;
import com.nltecklib.protocol.lab.accessory.AHeartBeatData;
import com.nltecklib.protocol.lab.accessory.AIPAddressData;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.lab.accessory.BuzzerData;
import com.nltecklib.protocol.lab.accessory.ChannelLightData;
import com.nltecklib.protocol.lab.accessory.ChannelLightOptData;
import com.nltecklib.protocol.lab.accessory.ChnLightBatchControlData;
import com.nltecklib.protocol.lab.accessory.CoolFanData;
import com.nltecklib.protocol.lab.accessory.DeviceOperateBtnData;
import com.nltecklib.protocol.lab.accessory.FanStateQueryData;
import com.nltecklib.protocol.lab.accessory.HeatDuctSwitchData;
import com.nltecklib.protocol.lab.accessory.IPC_PowerOptData;
import com.nltecklib.protocol.lab.accessory.OMRStateQueryData;
import com.nltecklib.protocol.lab.accessory.OMRTempData;
import com.nltecklib.protocol.lab.accessory.OMRTempSwitchData;
import com.nltecklib.protocol.lab.accessory.OMRTempUpperData;
import com.nltecklib.protocol.lab.accessory.PolarLightOptData;
import com.nltecklib.protocol.lab.accessory.PowerStateQueryData;
import com.nltecklib.protocol.lab.accessory.PowerSwitchData;
import com.nltecklib.protocol.lab.accessory.ScreenCommStateData;
import com.nltecklib.protocol.lab.accessory.ScreenControlStateData;
import com.nltecklib.protocol.lab.accessory.ScreenUnitIpAddressData;
import com.nltecklib.protocol.lab.accessory.ScreenUnitStateData;
import com.nltecklib.protocol.lab.accessory.SmogAlertData;
import com.nltecklib.protocol.lab.accessory.TempProbeData;
import com.nltecklib.protocol.lab.accessory.TempQueryData;
import com.nltecklib.protocol.lab.accessory.ThreeColorLightData;
import com.nltecklib.protocol.lab.accessory.TurboFanData;
import com.nltecklib.protocol.lab.backup.BCalculateData;
import com.nltecklib.protocol.lab.backup.BCalibrateData;
import com.nltecklib.protocol.lab.backup.BChnWorkModeData;
import com.nltecklib.protocol.lab.backup.BCurrRelayData;
import com.nltecklib.protocol.lab.backup.BDcRelayData;
import com.nltecklib.protocol.lab.backup.BDefineVoltData;
import com.nltecklib.protocol.lab.backup.BFlashParamsData;
import com.nltecklib.protocol.lab.backup.BHardErrData;
import com.nltecklib.protocol.lab.backup.BInfoData;
import com.nltecklib.protocol.lab.backup.BLedData;
import com.nltecklib.protocol.lab.backup.BPickupData;
import com.nltecklib.protocol.lab.backup.BProtectionData;
import com.nltecklib.protocol.lab.backup.BUpgradeData;
import com.nltecklib.protocol.lab.backup.BWorkEnvData;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.lab.cal.CAddressData;
import com.nltecklib.protocol.lab.cal.CCalResisterFactorData;
import com.nltecklib.protocol.lab.cal.CCalculateData;
import com.nltecklib.protocol.lab.cal.CCalculateDataC;
import com.nltecklib.protocol.lab.cal.CCalibrateData;
import com.nltecklib.protocol.lab.cal.CCalibrateDataE;
import com.nltecklib.protocol.lab.cal.CHeartBeatData;
import com.nltecklib.protocol.lab.cal.CIPAddressData;
import com.nltecklib.protocol.lab.cal.CTemperatureData;
import com.nltecklib.protocol.lab.cal.CVoltageBaseData;
import com.nltecklib.protocol.lab.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.lab.cal.NewCCalibrateData;
import com.nltecklib.protocol.lab.cal.NewCResisterData;
import com.nltecklib.protocol.lab.cal.SwitchMeterData;
import com.nltecklib.protocol.lab.main.CCProtectData;
import com.nltecklib.protocol.lab.main.CCVProtectData;
import com.nltecklib.protocol.lab.main.CPProtectData;
import com.nltecklib.protocol.lab.main.CRProtectData;
import com.nltecklib.protocol.lab.main.CVProtectData;
import com.nltecklib.protocol.lab.main.CalBackupVoltageData;
import com.nltecklib.protocol.lab.main.CalBackupVoltageMeterData;
import com.nltecklib.protocol.lab.main.CalChnBaseVoltageData;
import com.nltecklib.protocol.lab.main.CalChnEnableData;
import com.nltecklib.protocol.lab.main.CalChnMeterData;
import com.nltecklib.protocol.lab.main.CalChnMeterExData;
import com.nltecklib.protocol.lab.main.CalChnVoltageCurrentData;
import com.nltecklib.protocol.lab.main.CalQueryChnADCData;
import com.nltecklib.protocol.lab.main.CalQueryChnADCExData;
import com.nltecklib.protocol.lab.main.CalSaveChnBackupVoltageFlashData;
import com.nltecklib.protocol.lab.main.CalSaveChnFlashData;
import com.nltecklib.protocol.lab.main.CalSaveChnFlashExData;
import com.nltecklib.protocol.lab.main.CcCvProtectData;
import com.nltecklib.protocol.lab.main.ChannelAlertData;
import com.nltecklib.protocol.lab.main.ChannelAssistData;
import com.nltecklib.protocol.lab.main.ChannelOperateData;
import com.nltecklib.protocol.lab.main.ChannelPushData;
import com.nltecklib.protocol.lab.main.ChannelPushExData;
import com.nltecklib.protocol.lab.main.ChannelSwitchData;
import com.nltecklib.protocol.lab.main.ChnFirstLevelProtectData;
import com.nltecklib.protocol.lab.main.DCProtectData;
import com.nltecklib.protocol.lab.main.DCVProtectData;
import com.nltecklib.protocol.lab.main.DPProtectData;
import com.nltecklib.protocol.lab.main.DRProtectData;
import com.nltecklib.protocol.lab.main.DVProtectData;
import com.nltecklib.protocol.lab.main.DateData;
import com.nltecklib.protocol.lab.main.DcDvProtectData;
import com.nltecklib.protocol.lab.main.DeviceExceptionData;
import com.nltecklib.protocol.lab.main.EnvProtectData;
import com.nltecklib.protocol.lab.main.FileTransData;
import com.nltecklib.protocol.lab.main.GeneralProtectData;
import com.nltecklib.protocol.lab.main.HeartBeatData;
import com.nltecklib.protocol.lab.main.IPAddressData;
import com.nltecklib.protocol.lab.main.InitCheckData;
import com.nltecklib.protocol.lab.main.JsonData;
import com.nltecklib.protocol.lab.main.TestResultData;
import com.nltecklib.protocol.lab.main.LoopCapacityData;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.OfflineCfgData;
import com.nltecklib.protocol.lab.main.OfflineUploadData;
import com.nltecklib.protocol.lab.main.PoleData;
import com.nltecklib.protocol.lab.main.PoleExData;
import com.nltecklib.protocol.lab.main.ProcedureData;
import com.nltecklib.protocol.lab.main.ResetData;
import com.nltecklib.protocol.lab.main.SecurityProtectData;
import com.nltecklib.protocol.lab.main.SkipStepData;
import com.nltecklib.protocol.lab.main.SlpProtectData;
import com.nltecklib.protocol.lab.main.SnapShotData;
import com.nltecklib.protocol.lab.main.SoftVersionData;
import com.nltecklib.protocol.lab.main.SoftVersionExData;
import com.nltecklib.protocol.lab.main.TestNameData;
import com.nltecklib.protocol.lab.main.TouchProtectData;
import com.nltecklib.protocol.lab.main.UnitAddressData;
import com.nltecklib.protocol.lab.main.UnitConnectData;
import com.nltecklib.protocol.lab.main.UnitConnectInfoData;
import com.nltecklib.protocol.lab.main.UnitConnectStateData;
import com.nltecklib.protocol.lab.main.UnitManageData;
import com.nltecklib.protocol.lab.main.UnitTitleData;
import com.nltecklib.protocol.lab.main.UpgradeData;
import com.nltecklib.protocol.lab.main.UpgradeProgressData;
import com.nltecklib.protocol.lab.mbcal.MbCalEnvironment.MbCalCode;
import com.nltecklib.protocol.lab.mbcal.MbCalResisterFactorData;
import com.nltecklib.protocol.lab.mbcal.MbCalculateData;
import com.nltecklib.protocol.lab.mbcal.MbCalibrateData;
import com.nltecklib.protocol.lab.mbcal.MbDateData;
import com.nltecklib.protocol.lab.mbcal.MbHeartbeatData;
import com.nltecklib.protocol.lab.mbcal.MbIPAddressData;
import com.nltecklib.protocol.lab.mbcal.MbNewCalibrateData;
import com.nltecklib.protocol.lab.mbcal.MbSwitchMeter;
import com.nltecklib.protocol.lab.mbcal.MbTemperatureData;
import com.nltecklib.protocol.lab.mbcal.MbVoltageBaseData;
import com.nltecklib.protocol.lab.pickup.PCalculateData;
import com.nltecklib.protocol.lab.pickup.PCalculateExData;
import com.nltecklib.protocol.lab.pickup.PCalibrateData;
import com.nltecklib.protocol.lab.pickup.PCalibrateExData;
import com.nltecklib.protocol.lab.pickup.PConnectData;
import com.nltecklib.protocol.lab.pickup.PDeviceVersionData;
import com.nltecklib.protocol.lab.pickup.PDriverSelfCheckData;
import com.nltecklib.protocol.lab.pickup.PFlashParamsData;
import com.nltecklib.protocol.lab.pickup.PFlashParamsExData;
import com.nltecklib.protocol.lab.pickup.PHardErrInfoData;
import com.nltecklib.protocol.lab.pickup.PIPAdressData;
import com.nltecklib.protocol.lab.pickup.PInfoData;
import com.nltecklib.protocol.lab.pickup.PInfoExData;
import com.nltecklib.protocol.lab.pickup.PModuleEnableData;
import com.nltecklib.protocol.lab.pickup.PMultiModuleCaculateData;
import com.nltecklib.protocol.lab.pickup.PMultiModuleCalibrateData;
import com.nltecklib.protocol.lab.pickup.PMultiModuleFlashParamsData;
import com.nltecklib.protocol.lab.pickup.POptData;
import com.nltecklib.protocol.lab.pickup.PPickupData;
import com.nltecklib.protocol.lab.pickup.PPickupExData;
import com.nltecklib.protocol.lab.pickup.PProtectionData;
import com.nltecklib.protocol.lab.pickup.PStepData;
import com.nltecklib.protocol.lab.pickup.PUpgradeData;
import com.nltecklib.protocol.lab.pickup.PVoltBoundaryData;
import com.nltecklib.protocol.lab.pickup.PVoltBoundaryExData;
import com.nltecklib.protocol.lab.pickup.PWorkEnvData;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.screen.ChnCountData;
import com.nltecklib.protocol.lab.screen.ChnStateData;
import com.nltecklib.protocol.lab.screen.CommunicationData;
import com.nltecklib.protocol.lab.screen.IpAddressData;
import com.nltecklib.protocol.lab.screen.LedControlData;
import com.nltecklib.protocol.lab.screen.ScreenAddressData;
import com.nltecklib.protocol.lab.screen.ScreenEnvironment.ScreenCode;
import com.nltecklib.protocol.lab.screen.TitleData;
import com.nltecklib.protocol.lab.screen.UnitData;
import com.nltecklib.protocol.lab.test.diap.CheckResultData;
import com.nltecklib.protocol.lab.test.diap.CurrQueryData;
import com.nltecklib.protocol.lab.test.diap.DiapBurnStateData;
import com.nltecklib.protocol.lab.test.diap.DiapControlSwitch;
import com.nltecklib.protocol.lab.test.diap.WorkPatternConfig;
import com.nltecklib.protocol.lab.test.diap.DiapPowerConfig;
import com.nltecklib.protocol.lab.test.diap.DiapPowerSwitchData;
import com.nltecklib.protocol.lab.test.diap.DiapTemperCheckData;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.lab.test.diap.InformationCollectionData;
import com.nltecklib.protocol.lab.test.diap.MultiReadchannelSelect;
import com.nltecklib.protocol.lab.test.diap.Power16VSwitch;
import com.nltecklib.protocol.lab.test.diap.PowerItemConfig;
import com.nltecklib.protocol.lab.adCal.METERDOT_AD0;
import com.nltecklib.protocol.lab.adCal.METERDOT_AD1;
import com.nltecklib.protocol.lab.adCal.RESET;
import com.nltecklib.protocol.lab.adCal.UPDATE;
import com.nltecklib.protocol.lab.adCal.VERSION;
import com.nltecklib.protocol.lab.adCal.CALDOT_5MV;
import com.nltecklib.protocol.lab.adCal.CALDOT_N140MV;
import com.nltecklib.protocol.lab.adCal.CALDOT_P140MV;
import com.nltecklib.protocol.lab.adCal.CHN_STATE;
import com.nltecklib.protocol.lab.adCal.CalEnvironment.ADCalCode;
import com.nltecklib.protocol.lab.adCal.FLASHDATA_P5MV;
import com.nltecklib.protocol.lab.adCal.FLASHDATA_N140MV;
import com.nltecklib.protocol.lab.adCal.FLASHDATA_N5MV;
import com.nltecklib.protocol.lab.adCal.FLASHDATA_P140MV;
import com.nltecklib.protocol.lab.adCal.GET_VOLT;

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
	public static Decorator createData(Orient orient, Code code) {

		Decorator data = null;
		if (code instanceof MainCode) {
			data = createMainData(orient, (MainCode) code);
		} else if (code instanceof AccessoryCode) {
			data = createAccessoryData(orient, (AccessoryCode) code);
		} else if (code instanceof ChipPickupCode) {
			data = createPickupData(orient, (ChipPickupCode) code);
		} else if (code instanceof CalCode) {
			data = createCalBoardData(orient, (CalCode) code);
		} else if (code instanceof BackupCode) {
			data = createBackupBoardData(orient, (BackupCode) code);
		} else if (code instanceof ScreenCode) {
			data = createScreenData(orient, (ScreenCode) code);
		} else if (code instanceof DiapTestCode) {
			data = createDiapTestData(orient, (DiapTestCode) code);
		} else if (code instanceof ADCalCode) {
			data = createADCalBoardData(orient, (ADCalCode) code);
		}

		return data;
	}

	public static Decorator createBackupBoardData(Orient orient, BackupCode code) {
		Data data = null;
		switch (code) {
		case WorkStateCode:
			data = new BWorkEnvData();
			break;
		case PickupCode:
			data = new BPickupData();
			break;
		case DefineVoltCode:
			data = new BDefineVoltData();
			break;
		case ChnWorkModeCode:
			data = new BChnWorkModeData();
			break;
		case BatterProtectCode:
			data = new BProtectionData();
			break;
		case CalChnCode:
			data = new BCalibrateData();
			break;
		case CheckChnCode:
			data = new BCalculateData();
			break;
		case FlashCode:
			data = new BFlashParamsData();
			break;
		case UpdateCode:
			data = new BUpgradeData();
			break;
		case DcRelayCode:
			data = new BDcRelayData();
			break;
		case CurrDelayCode:
			data = new BCurrRelayData();
			break;
		case VersionInfoCode:
			data = new BInfoData();
			break;
		case HardErrCode:
			data = new BHardErrData();
			break;
		case LedCode:
			data = new BLedData();
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

	public static Decorator createScreenData(Orient orient, ScreenCode code) {

		Data data = null;
		switch (code) {
		case TitleCode:
			data = new TitleData();
			break;
		case AddressCode:
			data = new ScreenAddressData();
			break;
		case ChnCountCode:
			data = new ChnCountData();
			break;
		case ChnStateCode:
			data = new ChnStateData();
			break;
		case CommunicateCode:
			data = new CommunicationData();
			break;
		case IpAddressCode:
			data = new IpAddressData();
			break;
		case LEDCode:
			data = new LedControlData();
			break;
		case UnitCode:
			data = new UnitData();
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

	public static Decorator createDiapTestData(Orient orient, DiapTestCode code) {

		Data data = null;
		switch (code) {

		case DiapPowerConfig:
			data = new DiapPowerConfig();
			break;
		case WorkPatternConfig:
			data = new WorkPatternConfig();
			break;
		case PowerItemConfig:
			data = new PowerItemConfig();
			break;
		case Power16VSwitch:
			data = new Power16VSwitch();
			break;
		case DiapControlSwitch:
			data = new DiapControlSwitch();
			break;
		case MultiReadchannelSelect:
			data = new MultiReadchannelSelect();
			break;
		case InformationCollection:
			data = new InformationCollectionData();
			break;
		case PowerSwitch:
			data = new DiapPowerSwitchData();
			break;
		case CurrQuery:
			data = new CurrQueryData();
			break;
		case CheckResult:
			data = new CheckResultData();
			break;
		case DiapTemperCheck:
			data = new DiapTemperCheckData();
			break;
		case BurnState:
			data = new DiapBurnStateData();
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

		// return data;

	}

	public static Decorator createMbCalBoardData(Orient orient, MbCalCode code) {

		Data data = null;
		switch (code) {
		case CAL:
			data = new MbCalibrateData();
			break;
		case VOLT_BASE:
			data = new MbVoltageBaseData();
			break;
		case TEMP:
			data = new MbTemperatureData();
			break;
		case RESISTER:
			data = new MbCalResisterFactorData();
			break;
		case HEARTBEAT:
			data = new MbHeartbeatData();
			break;
		case CALCULATE:
			data = new MbCalculateData();
			break;
		case IP_ADDRESS:
			data = new MbIPAddressData();
			break;
		case CAL2:
			data = new MbNewCalibrateData();
			break;
		case DATE:
			data = new MbDateData();
			break;
		case SWITCH_METER:
			data = new MbSwitchMeter();
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

	public static Decorator createCalBoardData(Orient orient, CalCode code) {
		Data data = null;
		switch (code) {
		case CAL:
			data = new CCalibrateData();
			break;
		case VOLT_BASE:
			data = new CVoltageBaseData();
			break;
		case TEMP:
			data = new CTemperatureData();
			break;
		case RESISTER:
			data = new CCalResisterFactorData();
			break;
		case HEARTBEAT:
			data = new CHeartBeatData();
			break;
		case CALCULATE:
			data = new CCalculateData();
			break;
		case IP_ADDRESS:
			data = new CIPAddressData();
			break;
		case CAL2:
			data = new NewCCalibrateData();
			break;
		case SwitchMeter:
			data = new SwitchMeterData();
			break;
			
		case CAL3:
			data = new CCalibrateDataE();
			break;
		case CALCULATE3:
			data = new CCalculateDataC();
			break;	
		case ADDRESS:
			data = new CAddressData();
		case RESISTER2:
			data = new NewCResisterData();
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

	public static Decorator createMainData(Orient orient, MainCode code) {

		Data data = null;
		switch (code) {
		case ProcedureCode:
			data = new ProcedureData();
			break;
		case PoleCode:
			data = new PoleData();
			break;
		case InitCheckCode:
			data = new InitCheckData();
			break;
		case ChnSwitchCode:
			data = new ChannelSwitchData();
			break;
		case ChnOperateCode:
			data = new ChannelOperateData();
			break;
		case AssistCode:
			data = new ChannelAssistData();
			break;
		case PickupCode:
			data = new ChannelPushData();
			break;
		case PickupExCode:
			data = new ChannelPushExData();
			break;
		case ChannelAlertCode:
			data = new ChannelAlertData();
			break;
		case ChnFirstLevelProtectCode:
			data = new ChnFirstLevelProtectData();
			break;
		case SleepProtectCode:
			data = new SlpProtectData();
			break;
		case CCProtectCode:
			data = new CCProtectData();
			break;
		case CVProtectCode:
			data = new CVProtectData();
			break;
		case DCProtectCode:
			data = new DCProtectData();
			break;
		case CPProtectCode:
			data = new CPProtectData();
			break;
		case DPProtectCode:
			data = new DPProtectData();
			break;
		case CRProtectCode:
			data = new CRProtectData();
			break;
		case DRProtectCode:
			data = new DRProtectData();
			break;
		case DeviceExceptionCode:
			data = new DeviceExceptionData();
			break;
		case TouchProtectCode:
			data = new TouchProtectData();
			break;
		case IPAddressCode:
			data = new IPAddressData();
			break;
		case DateCode:
			data = new DateData();
			break;
		case UpgradeCode:
			data = new UpgradeData();
			break;
		case DVProtectCode:
			data = new DVProtectData();
			break;
		case JsonCode:
			data = new JsonData();
			break;
		case CalChnVoltageCurrentCode:
			data = new CalChnVoltageCurrentData();
			break;
		case CalQueryChnADCCode:
			data = new CalQueryChnADCData();
			break;
		case CalBackupVoltageCode:
			data = new CalBackupVoltageData();
			break;
		case CalChnEnableCode:
			data = new CalChnEnableData();
			break;
		case CalChnMeterCode:
			data = new CalChnMeterData();
			break;
		case CalSaveChnFlashCode:
			data = new CalSaveChnFlashData();
			break;
		case CalSaveChnFlashExCode:
			data = new CalSaveChnFlashExData();
			break;
			
		case CalSaveChnBackupVoltageFlashCode:
			data = new CalSaveChnBackupVoltageFlashData();
			break;
		case CalChnBaseVoltageCode:
			data = new CalChnBaseVoltageData();
			break;
		case CalBackupVoltageMeterCode:
			data = new CalBackupVoltageMeterData();
			break;
		case UpgradeProgressCode:
			data = new UpgradeProgressData();
			break;
		case SoftVersionCode:
			data = new SoftVersionData();
			break;
		case TestnameCode:
			data = new TestNameData();
			break;
		case SkipStepCode:
			data = new SkipStepData();
			break;
		case ResetCode:
			data = new ResetData();
			break;
		case HeartBeatCode:
			data = new HeartBeatData();
			break;
		case EnvProtectCode:
			data = new EnvProtectData();
			break;
		case CCVProtectCode:
			data = new CCVProtectData();
			break;
		case CDVProtectCode:
			data = new DCVProtectData();
			break;
		case OfflineCfgCode:
			data = new OfflineCfgData();
			break;
		case OfflineDataCode:
			data = new OfflineUploadData();
			break;
		case UnitManageCode:
			data = new UnitManageData();
			break;
		case UnitTitleCode:
			data = new UnitTitleData();
			break;
		case LoopCapacityCode:
			data = new LoopCapacityData();
			break;
		case TestResultCode:
			data = new TestResultData();
			break;
		case SecurityProtectCode:
			data = new SecurityProtectData();
			break;
		case UnitStateCode:
			data = new UnitConnectStateData();
			break;
		case CalChnMeterExCode:
			data = new CalChnMeterExData();
			break;
		case CalQueryChnADCExCode:
			data = new CalQueryChnADCExData();
			break;
		case SoftVersionExCode:
			data = new SoftVersionExData();
			break;
		case UnitConnectCode:
			data = new UnitConnectData();
			break;
		case UnitConnectInfoCode:
			data= new UnitConnectInfoData();
			break;
		case UnitAddressCode:
			data = new UnitAddressData();
			break;
		case FileTransCode:
			data = new FileTransData();
			break;
		case SnapShotCode:
			data = new SnapShotData();
			break;
		case CcCvProtectCode:
			data = new CcCvProtectData();
			break;
		case DcDvProtectCode:
			data = new DcDvProtectData();
			break;
		case GeneralCode:
			data = new GeneralProtectData();
			break;
		case PoleExCode:
			data = new PoleExData();
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

	public static Decorator createPickupData(Orient orient, ChipPickupCode code) {
		Data data = null;
		switch (code) {
		case WorkEnvCode:
			data = new PWorkEnvData();
			break;
		case StepCode:
			data = new PStepData();
			break;
		case OptCode:
			data = new POptData();
			break;
		case PickupCode:
			data = new PPickupData();
			break;
		case ProtectCode:
			data = new PProtectionData();
			break;
		case CalibrateCode:
			data = new PCalibrateData();
			break;
		case CalculateCode:
			data = new PCalculateData();
			break;
		case ModuleEnableCode:
			data = new PModuleEnableData();
			break;
		case FlashParamsCode:
			data = new PFlashParamsData();
			break;
		case UpgradeCode:
			data = new PUpgradeData();
			break;
		case ConnectCode:
			data = new PConnectData();
			break;
		case VoltBoundaryCode:
			data = new PVoltBoundaryData();
			break;
		case InfoCode:
			data = new PInfoData();
			break;
		case HardErrInfoCode:
			data = new PHardErrInfoData();
			break;
		case CalculateExCode:
			data = new PCalculateExData();
			break;
		case CalibrateExCode:
			data = new PCalibrateExData();
			break;
		case InfoExCode:
			data = new PInfoExData();
			break;
		case PickupExCode:
			data = new PPickupExData();
			break;
		case IPAddressCode:
			data = new PIPAdressData();
			break;
		case FlashParamsExCode:
			data = new PFlashParamsExData();
			break;
		case VoltBoundaryExCode:// 0x19
			data = new PVoltBoundaryExData();
			break;
		case DriverSelfCheckCode:
			data = new PDriverSelfCheckData();
			break;
		case MultiModuleCalculateCode:
			data = new PMultiModuleCaculateData();
			break;
		case MultiModuleCalibrateCode:
			data = new PMultiModuleCalibrateData();
			break;
		case MultiModuleFlashParamsCode:
			data = new PMultiModuleFlashParamsData();
			break;
		case SoftInfoCode:
			data = new PDeviceVersionData();
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

	public static Decorator createAccessoryData(Orient orient, AccessoryCode code) {
		Data data = null;
		switch (code) {
		case PowerStateCode:
			data = new PowerStateQueryData();
			break;
		case PowerSwitchCode:
			data = new PowerSwitchData();
			break;
		case FanStateCode:
			data = new FanStateQueryData();
			break;
		case OMRMeterSwitchCode:
			data = new OMRTempSwitchData();
			break;
		case OMRMeterTempCode:
			data = new OMRTempData();
			break;
		case OMRMeterStateCode:
			data = new OMRStateQueryData();
			break;
		case ORMMeterTempUpperCode:
			data = new OMRTempUpperData();
			break;
		case ThreeColorLightCode:
			data = new ThreeColorLightData();
			break;
		case TempQueryCode:
			data = new TempQueryData();
			break;
		case CoolFanCode:
			data = new CoolFanData();
			break;
		case TurboFanCode:
			data = new TurboFanData();
			break;
		case TempProbeCode:
			data = new TempProbeData();
			break;
		case ChnLightCode:
			data = new ChannelLightData();
			break;
		case HeatDuctSwitchCode:
			data = new HeatDuctSwitchData();
			break;
		case BuzzerCode:
			data = new BuzzerData();
			break;
		case SmogAlertCode:
			data = new SmogAlertData();
			break;
		case HeartBeatCode:
			data = new AHeartBeatData();
			break;
		case IPAddressCode:
			data = new AIPAddressData();
			break;
		case CHN_LIGHT_BATCH_CONTROL:
			data = new ChnLightBatchControlData();
			break;
		case ScreenCommStateCode:
			data = new ScreenCommStateData();
			break;
		case ScreenUnitIPCode:
			data = new ScreenUnitIpAddressData();
			break;
		case ScreenUnitStateCode:
			data = new ScreenUnitStateData();
			break;
		case ScreenControlStateCode:
			data = new ScreenControlStateData();
			break;
		case DeviceOptBtnCode:
			data = new DeviceOperateBtnData();
			break;
		case ChannelLightCode:
			data = new ChannelLightOptData();
			break;
		case PolarLightCode:
			data = new PolarLightOptData();
			break;
		case IPC_PowerCode:
			data = new IPC_PowerOptData();
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

	public static Decorator createADCalBoardData(Orient orient, ADCalCode code) {
		Data data = null;
		switch (code) {
		case CHN_STATE:
			data = new CHN_STATE();
			break;
		case GET_VOLT:
			data = new GET_VOLT();
			break;
		case CALDOT_P140MV:
			data = new CALDOT_P140MV();
			break;
		case CALDOT_N140MV:
			data = new CALDOT_N140MV();
			break;
		case CALDOT_5MV:
			data = new CALDOT_5MV();
			break;
		case METERDOT_AD1:
			data = new METERDOT_AD1();
			break;
		case METERDOT_AD0:
			data = new METERDOT_AD0();
			break;
		case FLASHDATA_P140MV:
			data = new FLASHDATA_P140MV();
			break;
		case FLASHDATA_N140MV:
			data = new FLASHDATA_N140MV();
			break;
		case FLASHDATA_P5MV:
			data = new FLASHDATA_P5MV();
			break;
		case FLASHDATA_N5MV:
			data = new FLASHDATA_N5MV();
			break;
		case UPDATE:
			data = new UPDATE();
			break;
		case VERSION:
			data = new VERSION();
			break;
		case RESET:
			data = new RESET();
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
