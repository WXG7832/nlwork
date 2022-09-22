package com.nltecklib.protocol.li;

import com.nltecklib.protocol.fuel.main.WorkModeData;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.MBWorkform.MBCalMatchData;
import com.nltecklib.protocol.li.MBWorkform.MBCheckCalculateData;
import com.nltecklib.protocol.li.MBWorkform.MBCheckCalibrateData;
import com.nltecklib.protocol.li.MBWorkform.MBCheckFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBDeviceBaseInfoData;
import com.nltecklib.protocol.li.MBWorkform.MBHeartbeatData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCalculateData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCalibrateData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicCheckFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBLogicFlashWriteData;
import com.nltecklib.protocol.li.MBWorkform.MBModuleSwitchData;
import com.nltecklib.protocol.li.MBWorkform.MBSelfCheckData;
import com.nltecklib.protocol.li.MBWorkform.MBSelfTestInfoData;
import com.nltecklib.protocol.li.MBWorkform.MBUuidData;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.BaseCfgData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoConfigData;
import com.nltecklib.protocol.li.PCWorkform.BaseInfoQueryData;
import com.nltecklib.protocol.li.PCWorkform.BindCalBoardData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardTestModeData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardUpdateFileData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardUpdateModeData;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalMatchData;
import com.nltecklib.protocol.li.PCWorkform.CalMatchValueData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempQueryDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibrateTerminalData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.ConnectCalboardData;
import com.nltecklib.protocol.li.PCWorkform.ConnectDeviceData;
import com.nltecklib.protocol.li.PCWorkform.ConnectMeterData;
import com.nltecklib.protocol.li.PCWorkform.DelayData;
import com.nltecklib.protocol.li.PCWorkform.DeviceConnectData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.DriverBindData;
import com.nltecklib.protocol.li.PCWorkform.DriverModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.HeartbeatData;
import com.nltecklib.protocol.li.PCWorkform.IPCfgData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogDebugPushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.li.PCWorkform.MatchStateData;
import com.nltecklib.protocol.li.PCWorkform.MeterConnectData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData;
import com.nltecklib.protocol.li.PCWorkform.ModuleSwitchData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.PCSelfTestInfoData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.ReadMeterData;
import com.nltecklib.protocol.li.PCWorkform.RelayControlExDebugData;
import com.nltecklib.protocol.li.PCWorkform.RelaySwitchDebugData;
import com.nltecklib.protocol.li.PCWorkform.RequestCalculateData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.protocol.li.PCWorkform.TestModeData;
import com.nltecklib.protocol.li.PCWorkform.TimeData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;
import com.nltecklib.protocol.li.PCWorkform.WorkformUpdateData;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.accessory.AddressData;
import com.nltecklib.protocol.li.accessory.AirPressureStateData;
import com.nltecklib.protocol.li.accessory.AirValveSwitchData;
import com.nltecklib.protocol.li.accessory.AnotherFanStateQueryData;
import com.nltecklib.protocol.li.accessory.ChannelLightOptData;
import com.nltecklib.protocol.li.accessory.ColorLightData;
import com.nltecklib.protocol.li.accessory.DoorData;
import com.nltecklib.protocol.li.accessory.EmergencyData;
import com.nltecklib.protocol.li.accessory.FanControlData;
import com.nltecklib.protocol.li.accessory.FanControlData2;
import com.nltecklib.protocol.li.accessory.FanStateQueryData2;
import com.nltecklib.protocol.li.accessory.FourLightStateData;
import com.nltecklib.protocol.li.accessory.FanStateQueryData;
import com.nltecklib.protocol.li.accessory.HeartBeatData;
import com.nltecklib.protocol.li.accessory.HeartBeatData2;
import com.nltecklib.protocol.li.accessory.HeatPipeStatusData;
import com.nltecklib.protocol.li.accessory.LightAudioData;
import com.nltecklib.protocol.li.accessory.IndicatorControlData;
import com.nltecklib.protocol.li.accessory.MechanismStateQueryData;
import com.nltecklib.protocol.li.accessory.NLTempData;
import com.nltecklib.protocol.li.accessory.PingStateData;
import com.nltecklib.protocol.li.accessory.OMRTempData;
import com.nltecklib.protocol.li.accessory.OMRTempModeData;
import com.nltecklib.protocol.li.accessory.OMRTempSwitchData;
import com.nltecklib.protocol.li.accessory.OMRTempUpperData;
import com.nltecklib.protocol.li.accessory.PingCalibrateSwitchData;
import com.nltecklib.protocol.li.accessory.PoleLightData;
import com.nltecklib.protocol.li.accessory.PowerErrorInfoData;
import com.nltecklib.protocol.li.accessory.PowerFaultReasonData;
import com.nltecklib.protocol.li.accessory.PowerLLZErrorInfoData;
import com.nltecklib.protocol.li.accessory.PowerResetData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData2;
import com.nltecklib.protocol.li.accessory.PowerSupplyData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerSwitchData;
import com.nltecklib.protocol.li.accessory.PowerSwitchData2;
import com.nltecklib.protocol.li.accessory.SelectLightData;
import com.nltecklib.protocol.li.accessory.SmogAlertData;
import com.nltecklib.protocol.li.accessory.TempProbeData;
import com.nltecklib.protocol.li.accessory.TempQueryData;
import com.nltecklib.protocol.li.accessory.TempStateQueryData;
import com.nltecklib.protocol.li.accessory.TurboFanData;
import com.nltecklib.protocol.li.accessory.ValveSwitchData;
import com.nltecklib.protocol.li.accessory.ValveTempData;
import com.nltecklib.protocol.li.cal.CalAddressData;
import com.nltecklib.protocol.li.cal.CalEnvironment.CalCode;
import com.nltecklib.protocol.li.cal.CalResisterFactorData;
import com.nltecklib.protocol.li.cal.CalUpdateFileData;
import com.nltecklib.protocol.li.cal.CalUpdateModeData;
import com.nltecklib.protocol.li.cal.CalWorkModeData;
import com.nltecklib.protocol.li.cal.Calculate2Data;
import com.nltecklib.protocol.li.cal.Calibrate2Data;
import com.nltecklib.protocol.li.cal.CalibrateData;
import com.nltecklib.protocol.li.cal.ELoadOverTempAlarmData;
import com.nltecklib.protocol.li.cal.NewCalibrateData;
import com.nltecklib.protocol.li.cal.OverTempAlertData;
import com.nltecklib.protocol.li.cal.RelayControlData;
import com.nltecklib.protocol.li.cal.RelayControlExData;
import com.nltecklib.protocol.li.cal.RelaySwitchData;
import com.nltecklib.protocol.li.cal.ResistanceModeData;
import com.nltecklib.protocol.li.cal.ResistanceModeRelayData;
import com.nltecklib.protocol.li.cal.TempControlData;
import com.nltecklib.protocol.li.cal.TemperatureData;
import com.nltecklib.protocol.li.cal.TestPatternData;
import com.nltecklib.protocol.li.cal.VoltageBaseData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckAddressData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckCalculateData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckCalculateFactorData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckEnvironment.CalToolsCheckCode;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckMeterData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckPowerRelayData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckSerialTestData;
import com.nltecklib.protocol.li.calTools.check.CalToolsCheckTestModeData;
import com.nltecklib.protocol.li.calTools.checkDriver.CalToolsCheckDriverAddressData;
import com.nltecklib.protocol.li.calTools.checkDriver.CalToolsCheckDriverEnvironment.CalToolsCheckDriverCode;
import com.nltecklib.protocol.li.calTools.checkDriver.CalToolsCheckDriverSerialTestData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicAddressData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicCalculateData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicCalculateFactorData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicEnvironment.CalToolsLogicCode;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicMeterData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicSelfTestData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicSerialTestData;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicTestModeData;
import com.nltecklib.protocol.li.calTools.logicDriver.CalToolsLogicDriverAddressData;
import com.nltecklib.protocol.li.calTools.logicDriver.CalToolsLogicDriverEnvironment.CalToolsLogicDriverCode;
import com.nltecklib.protocol.li.calTools.logicDriver.CalToolsLogicDriverSerialTestData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestCalculateData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestChnSelectData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestCurrentPickupData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestCurrentProtectionData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestDacData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestEnvironment.CalToolsTestCode;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestLoadSwitchData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestPowerData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestSerialSelectData;
import com.nltecklib.protocol.li.calTools.test.CalToolsTestVoltagePolarityData;
import com.nltecklib.protocol.li.check.CheckBaseCountData;
import com.nltecklib.protocol.li.check.CheckCalProcessData;
import com.nltecklib.protocol.li.check.CheckCalculateData;
import com.nltecklib.protocol.li.check.CheckEnvironment.CheckCode;
import com.nltecklib.protocol.li.check.CheckFaultCheckData;
import com.nltecklib.protocol.li.check.CheckHKCalibrateData;
import com.nltecklib.protocol.li.check.CheckHKDriverStateData;
import com.nltecklib.protocol.li.check.CheckPickupData;
import com.nltecklib.protocol.li.check.CheckPoleData;
import com.nltecklib.protocol.li.check.CheckStartupData;
import com.nltecklib.protocol.li.check.CheckWriteCalFlashData;
import com.nltecklib.protocol.li.check2.Check2SoftversionData;
import com.nltecklib.protocol.li.check2.Check2CalProcessData;
import com.nltecklib.protocol.li.check2.Check2CalculateData;
import com.nltecklib.protocol.li.check2.Check2ConfirmCloseData;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.check2.Check2FaultCheckData;
import com.nltecklib.protocol.li.check2.Check2Heartbeat;
import com.nltecklib.protocol.li.check2.Check2OverVoltProcessData;
import com.nltecklib.protocol.li.check2.Check2PickupData;
import com.nltecklib.protocol.li.check2.Check2PoleData;
import com.nltecklib.protocol.li.check2.Check2ProgramStateData;
import com.nltecklib.protocol.li.check2.Check2ProtectionSwitchData;
import com.nltecklib.protocol.li.check2.Check2RepairModeData;
import com.nltecklib.protocol.li.check2.Check2StartupData;
import com.nltecklib.protocol.li.check2.Check2TestPickData;
import com.nltecklib.protocol.li.check2.Check2UUIDData;
import com.nltecklib.protocol.li.check2.Check2UpgradeData;
import com.nltecklib.protocol.li.check2.Check2VoltProtectData;
import com.nltecklib.protocol.li.check2.Check2WriteCalFlashData;
import com.nltecklib.protocol.li.driver.DriverAnalogChnSwitchData;
import com.nltecklib.protocol.li.driver.DriverCalculateData;
import com.nltecklib.protocol.li.driver.DriverCalibrationFactorData;
import com.nltecklib.protocol.li.driver.DriverCheckCalFactorData;
import com.nltecklib.protocol.li.driver.DriverChnPickupData;
import com.nltecklib.protocol.li.driver.DriverChnSwitchData;
import com.nltecklib.protocol.li.driver.curr200.Driver200aChipIDData;
import com.nltecklib.protocol.li.driver.curr200.Driver200aMultiChangeStepData;
import com.nltecklib.protocol.li.driver.curr200.Driver200aProcedureData;
import com.nltecklib.protocol.li.driver.curr200.Driver200aProgramStateData;
import com.nltecklib.protocol.li.driver.curr200.DriverDiapCalData;
import com.nltecklib.protocol.li.driver.DriverDieSwitchData;
import com.nltecklib.protocol.li.driver.DriverMeterData;
import com.nltecklib.protocol.li.driver.DriverMultiChangeStepData;
import com.nltecklib.protocol.li.driver.DriverPoleData;
import com.nltecklib.protocol.li.driver.DriverProcedureData;
import com.nltecklib.protocol.li.driver.DriverSelfCheck2Data;
import com.nltecklib.protocol.li.driver.DriverSelfCheckData;
import com.nltecklib.protocol.li.driver.DriverStartInitData;
import com.nltecklib.protocol.li.driver.DriverUUIDData;
import com.nltecklib.protocol.li.driver.DriverUpgradeData;
import com.nltecklib.protocol.li.driver.DriverVersionData;
import com.nltecklib.protocol.li.driver.DriverMultiModePickupData;
import com.nltecklib.protocol.li.driver.DriverChangeStepData;
import com.nltecklib.protocol.li.driver.DriverWorkModeData;
import com.nltecklib.protocol.li.driver.curr200.DriverMultiDiapTestData;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driverG0.DriverG0UpgradeData;
import com.nltecklib.protocol.li.driverG0.DriverG0WorkModeData;
import com.nltecklib.protocol.li.driverG0.DriverG0BVoltChnSwitchData;
import com.nltecklib.protocol.li.driverG0.DriverG0ChipAddressData;
import com.nltecklib.protocol.li.driverG0.DriverG0Environment.DriverG0Code;
import com.nltecklib.protocol.li.driverG0.DriverG0PVoltChnSwitchData;
import com.nltecklib.protocol.li.goldFinger.GoldEnvironment.GoldCode;
import com.nltecklib.protocol.li.goldFinger.GoldFingerData;
import com.nltecklib.protocol.li.logic.LogicBaseCountData;
import com.nltecklib.protocol.li.logic.LogicCalMatchData;
import com.nltecklib.protocol.li.logic.LogicCalProcessData;
import com.nltecklib.protocol.li.logic.LogicCalculateData;
import com.nltecklib.protocol.li.logic.LogicChnStartData;
import com.nltecklib.protocol.li.logic.LogicChnStopData;
import com.nltecklib.protocol.li.logic.LogicChnSwitchData;
import com.nltecklib.protocol.li.logic.LogicDeviceProtectData;
import com.nltecklib.protocol.li.logic.LogicEnvironment.LogicCode;
import com.nltecklib.protocol.li.logic.LogicFaultCheckData;
import com.nltecklib.protocol.li.logic.LogicFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKCalculateData;
import com.nltecklib.protocol.li.logic.LogicHKCalibrateData;
import com.nltecklib.protocol.li.logic.LogicHKFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKOperationData;
import com.nltecklib.protocol.li.logic.LogicHKProcedureData;
import com.nltecklib.protocol.li.logic.LogicLabPoleData;
import com.nltecklib.protocol.li.logic.LogicLabProtectData;
import com.nltecklib.protocol.li.logic.LogicModuleSwitchData;
import com.nltecklib.protocol.li.logic.LogicNewCalProcessData;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.nltecklib.protocol.li.logic.LogicPoleData;
import com.nltecklib.protocol.li.logic.LogicStartupData;
import com.nltecklib.protocol.li.logic.LogicStateData;
import com.nltecklib.protocol.li.logic.LogicStopData;
import com.nltecklib.protocol.li.logic2.Logic2BaseCountData;
import com.nltecklib.protocol.li.logic2.Logic2BaseVoltTestData;
import com.nltecklib.protocol.li.logic2.Logic2BatExistData;
import com.nltecklib.protocol.li.logic2.Logic2BatExistSwitchData;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData;
import com.nltecklib.protocol.li.logic2.Logic2CalProcessData;
import com.nltecklib.protocol.li.logic2.Logic2CalculateData;
import com.nltecklib.protocol.li.logic2.Logic2CheckFlashWriteData;
import com.nltecklib.protocol.li.logic2.Logic2ChnOptData;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectData;
import com.nltecklib.protocol.li.logic2.Logic2DeviceProtectExData;
import com.nltecklib.protocol.li.logic2.Logic2ReadChipIdData;
import com.nltecklib.protocol.li.logic2.Logic2RepairModeData;
import com.nltecklib.protocol.li.logic2.Logic2ResetDriverAddressData;
import com.nltecklib.protocol.li.logic2.Logic2SingleStepData;
import com.nltecklib.protocol.li.logic2.Logic2SoftversionData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2ExtraCvCalProcess;
import com.nltecklib.protocol.li.logic2.Logic2FaultCheckData;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData;
import com.nltecklib.protocol.li.logic2.Logic2Heartbeat;
import com.nltecklib.protocol.li.logic2.Logic2LabPoleData;
import com.nltecklib.protocol.li.logic2.Logic2LabProtectData;
import com.nltecklib.protocol.li.logic2.Logic2ModuleSwitchData;
import com.nltecklib.protocol.li.logic2.Logic2PickupData;
import com.nltecklib.protocol.li.logic2.Logic2PickupTestData;
import com.nltecklib.protocol.li.logic2.Logic2PoleData;
import com.nltecklib.protocol.li.logic2.Logic2PoleExData;
import com.nltecklib.protocol.li.logic2.Logic2ProcedureData;
import com.nltecklib.protocol.li.logic2.Logic2ProgramStateData;
import com.nltecklib.protocol.li.logic2.Logic2StartupInitData;
import com.nltecklib.protocol.li.logic2.Logic2StateData;
import com.nltecklib.protocol.li.logic2.Logic2SyncStepSkipData;
import com.nltecklib.protocol.li.logic2.Logic2UUIDData;
import com.nltecklib.protocol.li.logic2.Logic2UpgradeData;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.AllowStepSkipData;
import com.nltecklib.protocol.li.main.BaseCountData;
import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CCVProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CabChnIndexDefineData;
import com.nltecklib.protocol.li.main.ChannelOptData;
import com.nltecklib.protocol.li.main.ChannelSwitchData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.ChnLightData;
import com.nltecklib.protocol.li.main.ClearSmogData;
import com.nltecklib.protocol.li.main.ControlUnitData;
import com.nltecklib.protocol.li.main.CylinderCfgData;
import com.nltecklib.protocol.li.main.CylinderControlData;
import com.nltecklib.protocol.li.main.CylinderPressureProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.DateData;
import com.nltecklib.protocol.li.main.DebugControlData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.DeviceStateQueryData;
import com.nltecklib.protocol.li.main.DriverChnIndexDefineData;
import com.nltecklib.protocol.li.main.EnableCheckProtectionData;
import com.nltecklib.protocol.li.main.EnergySaveData;
import com.nltecklib.protocol.li.main.ExChnsOperateData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.IPAddressData;
import com.nltecklib.protocol.li.main.JsonDeviceProtectData;
import com.nltecklib.protocol.li.main.JsonPoleData;
import com.nltecklib.protocol.li.main.JsonProcedureData;
import com.nltecklib.protocol.li.main.JsonProcedureExData;
import com.nltecklib.protocol.li.main.JsonProductCfgData;
import com.nltecklib.protocol.li.main.JsonProtectionData;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.SyncPressureParamData;
import com.nltecklib.protocol.li.main.OfflineAcquireData;
import com.nltecklib.protocol.li.main.OfflinePickupData;
import com.nltecklib.protocol.li.main.OfflineRunningData;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PressureChangeProtectData;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureModeData;
import com.nltecklib.protocol.li.main.PushSwitchData;
import com.nltecklib.protocol.li.main.RecoveryData;
import com.nltecklib.protocol.li.main.ResetData;
import com.nltecklib.protocol.li.main.SaveParamData;
import com.nltecklib.protocol.li.main.SelfCheckData;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.SolenoidValveData;
import com.nltecklib.protocol.li.main.SolenoidValveTempProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.li.main.StartupData;
import com.nltecklib.protocol.li.main.TempData;
import com.nltecklib.protocol.li.main.TemperAdjustData;
import com.nltecklib.protocol.li.main.TestNameData;
import com.nltecklib.protocol.li.main.UnitChannelSwitchData;
import com.nltecklib.protocol.li.main.UpgradeProgressData;
import com.nltecklib.protocol.li.main.VersionData;
import com.nltecklib.protocol.li.main.UpgradeProgramData;
import com.nltecklib.protocol.li.main.UpgradeProgramExData;
import com.nltecklib.protocol.li.main.VoiceAlertData;
import com.nltecklib.protocol.li.test.CalBoard.AddressConfigData;
import com.nltecklib.protocol.li.test.CalBoard.PowerSwitchControlData;
import com.nltecklib.protocol.li.test.CalBoard.WorkPatternData;
import com.nltecklib.protocol.li.test.CalBoard.CalBoardEnvironment.CalBoardTestCode;
import com.nltecklib.protocol.li.test.diap.DiapControlSwitch;
import com.nltecklib.protocol.li.test.diap.DiapItemConfig;
import com.nltecklib.protocol.li.test.diap.DiapPowerConfig;
import com.nltecklib.protocol.li.test.diap.InformationCollectionData;
import com.nltecklib.protocol.li.test.diap.MultiReadchannelSelect;
import com.nltecklib.protocol.li.test.diap.Power16VSwitch;
import com.nltecklib.protocol.li.test.diap.PowerItemConfig;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.li.test.driver.DTPowerConfigData;
import com.nltecklib.protocol.li.test.driver.DTPowerState;
import com.nltecklib.protocol.li.test.driver.DTPowerSwitchData;
import com.nltecklib.protocol.li.test.driver.DTRelayData;
import com.nltecklib.protocol.li.test.driver.DriverTestEnvironment.DriverTestCode;
import com.nltecklib.protocol.li.workform.CalBaseVoltageData;
import com.nltecklib.protocol.li.workform.CalCheckCalculateData;
import com.nltecklib.protocol.li.workform.CalCheckFlashWriteData;
import com.nltecklib.protocol.li.workform.CalCheckProcessData;
import com.nltecklib.protocol.li.workform.CalExFlashWriteData;
import com.nltecklib.protocol.li.workform.CalFlashWriteData;
import com.nltecklib.protocol.li.workform.CalHKCalculateData;
import com.nltecklib.protocol.li.workform.CalHKCalibrateData;
import com.nltecklib.protocol.li.workform.CalHKCheckCalibrateData;
import com.nltecklib.protocol.li.workform.CalHKFlashWriteData;
import com.nltecklib.protocol.li.workform.CalLogicModeData;
import com.nltecklib.protocol.li.workform.CalLogicModuleSwitchData;
import com.nltecklib.protocol.li.workform.CalProcessData;
import com.nltecklib.protocol.li.workform.CalTemperatureData;
import com.nltecklib.protocol.li.workform.CalWorkformModeData;
import com.nltecklib.protocol.li.workform.CalculateData;
import com.nltecklib.protocol.li.workform.CheckBoardMatchData;
import com.nltecklib.protocol.li.workform.ExResisterFactorData;
import com.nltecklib.protocol.li.workform.LogicBaseVoltageData;
import com.nltecklib.protocol.li.workform.MainHeartBeatData;
import com.nltecklib.protocol.li.workform.ResisterFactorData;
import com.nltecklib.protocol.li.workform.SwitchMeterData;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;

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
    // public static Decorator createData(Orient orient, Code code) {
    //
    // Decorator data = null;
    // if (code instanceof MainCode) {
    // data = createMainData(orient, (MainCode) code);
    // } else if (code instanceof GoldCode) {
    // // 金手指
    // data = createGdFingerData(orient, (GoldCode) code);
    // } else if (code instanceof LogicCode) {
    // data = createLogicData(orient, (LogicCode) code);
    // } else if (code instanceof CalCode) {
    // data = createCalData(orient, (CalCode) code);
    // } else if (code instanceof CheckCode) {
    // data = createCheckData(orient, (CheckCode) code);
    // } else if (code instanceof AccessoryCode) {
    // data = createAccessoryData(orient, (AccessoryCode) code);
    // } else if (code instanceof WorkformCode) {
    // data = createWorkformData(orient, (WorkformCode) code);
    // } else if (code instanceof Logic2Code) {
    // data = createLogic2Data(orient, (Logic2Code) code);
    // } else if (code instanceof Check2Code) {
    // data = createCheck2Data(orient, (Check2Code) code);
    // } else if (code instanceof CalToolsLogicCode) {
    // data = createCalToolsLogicData(orient, (CalToolsLogicCode) code);
    // } else if (code instanceof CalToolsCheckCode) {
    // data = createCalToolsCheckData(orient, (CalToolsCheckCode) code);
    // } else if (code instanceof CalToolsLogicDriverCode) {
    // data = createCalToolsLogicDriverData(orient, (CalToolsLogicDriverCode) code);
    // } else if (code instanceof CalToolsCheckDriverCode) {
    // data = createCalToolsCheckDriverData(orient, (CalToolsCheckDriverCode) code);
    // }
    // return data;
    // }

    // 金手指
    public static Data createGdFingerData(Orient orient, GoldCode code) {
	Data data = null;
	switch (code) {
	case GoldFinCode:
	    data = new GoldFingerData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createCalData(Orient orient, CalCode code) {

	Data data = null;
	switch (code) {
	case VOLT_BASE:
	    data = new VoltageBaseData();
	    break;
	case CAL:
	    data = new CalibrateData();
	    break;
	case RELAY:
	    data = new RelayControlData();
	    break;
	case TEMP:
	    data = new TemperatureData();
	    break;
	case RESISTER:
	    data = new CalResisterFactorData();
	    break;
	case ADDRESS_CAL:
	    data = new CalAddressData();
	    break;
	case NEW_CAL:
	    data = new NewCalibrateData();
	    break;
	case EXRESISTER:
	    data = new com.nltecklib.protocol.li.cal.ExCalResisterFactorData();
	    break;
	case CAL2:
	    data = new Calibrate2Data();
	    break;
	case MEASURE2:
	    data = new Calculate2Data();
	    break;
	case UpdateFileCode:
	    data = new CalUpdateFileData();
	    break;
	case UpdateModeCode:
	    data = new CalUpdateModeData();
	    break;
	case TempControlCode:
	    data = new TempControlData();
	    break;
	case OverTempAlertCode:
	    data = new OverTempAlertData();
	    break;
	case WorkModeCode:
	    data = new CalWorkModeData();
	    break;
	case ResistanceCode:
	    data = new ResistanceModeData();
	    break;
	case TestPatternCode:
	    data = new TestPatternData();
	    break;
	case ELoadOverTempAlarmCode:
	    data = new ELoadOverTempAlarmData();
	    break;
	case RelaySwitch:
	    data = new RelaySwitchData();
	    break;
	case RelayEx:
		data = new RelayControlExData();
		break;
	case ResistanceRelayCode:
		data = new ResistanceModeRelayData();
		break;
	
	
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createCheckData(Orient orient, CheckCode code) {

	Data data = null;
	switch (code) {

	case PoleCode:
	    data = new CheckPoleData();
	    break;
	case PickupCode:
	    data = new CheckPickupData();
	    break;
	case BaseCountCode:
	    data = new CheckBaseCountData();
	    break;
	case ChnCalCode:
	    data = new CheckCalProcessData();
	    break;
	case DeviceProtectCode:
	    data = new com.nltecklib.protocol.li.check.CheckVoltProtectData();
	    break;
	case StartupCode:
	    data = new CheckStartupData();
	    break;
	case WriteCalFlashCode:
	    data = new CheckWriteCalFlashData();
	    break;
	case FaultCheckCode:
	    data = new CheckFaultCheckData();
	    break;
	case HKCalibrateCode:
	    data = new CheckHKCalibrateData();
	    break;
	case HKDriverState:
	    data = new CheckHKDriverStateData();
	    break;
	case CalculateCode:
	    data = new CheckCalculateData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

    public static Data createCalToolsCheckDriverData(Orient orient, CalToolsCheckDriverCode code) {

	Data data = null;
	switch (code) {

	case Address:
	    data = new CalToolsCheckDriverAddressData();
	    break;
	case SerialTestCode:
	    data = new CalToolsCheckDriverSerialTestData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createCalToolsLogicDriverData(Orient orient, CalToolsLogicDriverCode code) {

	Data data = null;
	switch (code) {

	case Address:
	    data = new CalToolsLogicDriverAddressData();
	    break;
	case SerialTestCode:
	    data = new CalToolsLogicDriverSerialTestData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createCalToolsTestData(Orient orient, CalToolsTestCode code) {

	Data data = null;
	switch (code) {
	case DacCode:
	    data = new CalToolsTestDacData();
	    break;
	case ChnSelectCode:
	    data = new CalToolsTestChnSelectData();
	    break;
	case CurrentPickupCode:
	    data = new CalToolsTestCurrentPickupData();
	    break;
	case CurrentProtectionCode:
	    data = new CalToolsTestCurrentProtectionData();
	    break;
	case LoadSwitchCode:
	    data = new CalToolsTestLoadSwitchData();
	    break;
	case PowerCode:
	    data = new CalToolsTestPowerData();
	    break;
	case SerialSelectCode:
	    data = new CalToolsTestSerialSelectData();
	    break;
	case VoltagePolarityCode:
	    data = new CalToolsTestVoltagePolarityData();
	    break;
	case CalculateCode:
	    data = new CalToolsTestCalculateData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createCalToolsCheckData(Orient orient, CalToolsCheckCode code) {

	Data data = null;
	switch (code) {

	case Address:
	    data = new CalToolsCheckAddressData();
	    break;
	case CalFactorCode:
	    data = new CalToolsCheckCalculateFactorData();
	    break;
	case MeterCode:
	    data = new CalToolsCheckMeterData();
	    break;
	case CalculateCode:
	    data = new CalToolsCheckCalculateData();
	    break;
	case SerialTestCode:
	    data = new CalToolsCheckSerialTestData();
	    break;
	case TestModeCode:
	    data = new CalToolsCheckTestModeData();
	    break;
	case PowerRelayCode:
	    data = new CalToolsCheckPowerRelayData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createCalToolsLogicData(Orient orient, CalToolsLogicCode code) {

	Data data = null;
	switch (code) {

	case Address:
	    data = new CalToolsLogicAddressData();
	    break;
	case CalFactorCode:
	    data = new CalToolsLogicCalculateFactorData();
	    break;
	case MeterCode:
	    data = new CalToolsLogicMeterData();
	    break;
	case CalculateCode:
	    data = new CalToolsLogicCalculateData();
	    break;
	case SerialTestCode:
	    data = new CalToolsLogicSerialTestData();
	    break;
	case TestModeCode:
	    data = new CalToolsLogicTestModeData();
	    break;
	case SelfTestCode:
	    data = new CalToolsLogicSelfTestData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createDriverTestData(Orient orient, DriverTestCode code) {

	Data data = null;
	switch (code) {

	case PowerSwitch:
	    data = new DTPowerSwitchData();
	    break;
	case PowerState:
	    data = new DTPowerState();
	    break;
	case PowerConfig:
	    data = new DTPowerConfigData();
	    break;
	case RelayCode:
	    data = new DTRelayData();
	    break;

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }
    
    public static Data createDiapTestData(Orient orient, DiapTestCode code) {

    	Data data = null;
    	switch (code) {

    	case DiapPowerConfig:
    	    data = new DiapPowerConfig();
    	    break;
    	case DiapItemConfig:
    	    data = new DiapItemConfig();
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

    	    
    	}
    	if (data == null) {

    	    throw new RuntimeException("unrecognized function code:" + code);
    	}

    	return data;

        }
    
    public static Data createCalBoardTestData(Orient orient, CalBoardTestCode code) {

    	Data data = null;
    	switch (code) {

    	case WorkPattern:
    	    data = new WorkPatternData();
    	    break;
    	case AddressConfig:
    	    data = new AddressConfigData();
    	    break;
    	case PowerSwitch:
    	    data = new PowerSwitchControlData();
    	    break;

    	    
    	}
    	if (data == null) {

    	    throw new RuntimeException("unrecognized function code:" + code);
    	}

    	return data;

        }

    public static Data createCheck2Data(Orient orient, Check2Code code) {

	Data data = null;
	switch (code) {

	case PoleCode:
	    data = new Check2PoleData();
	    break;
	case PickupCode:
	    data = new Check2PickupData();
	    break;
	case SoftversionCode:
	    data = new Check2SoftversionData();
	    break;
	case ChnCalCode:
	    data = new Check2CalProcessData();
	    break;
	case DeviceProtectCode:
	    data = new Check2VoltProtectData();
	    break;
	case StartupCode:
	    data = new Check2StartupData();
	    break;
	case WriteCalFlashCode:
	    data = new Check2WriteCalFlashData();
	    break;
	case FaultCheckCode:
	    data = new Check2FaultCheckData();
	    break;
	case CalculateCode:
	    data = new Check2CalculateData();
	    break;
	case Heartbeat:
	    data = new Check2Heartbeat();
	    break;
	case OverVoltCode:
	    data = new Check2OverVoltProcessData();
	    break;
	case UpgradeCode:
	    data = new Check2UpgradeData();
	    break;
	case TestPickCode:
	    data = new Check2TestPickData();
	    break;
	case UUID_CODE:
	    data = new Check2UUIDData();
	    break;
	case ConfirmCloseCode:
	    data = new Check2ConfirmCloseData();
	    break;
	case ProgramStateCode:
	    data = new Check2ProgramStateData();
	    break;
	case REPAIR_MODE:
	    data = new Check2RepairModeData();
	    break;
	case ProtectionSwitchCode:
	    data = new Check2ProtectionSwitchData();
	    break;

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

    public static Data createAccessoryData(Orient orient, AccessoryCode code) {
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
	case FanControlCode:
	    data = new FanControlData();
	    break;
	case LightAudioCode:
	    data = new LightAudioData();
	    break;
	case NLTempCode:
	    data = new NLTempData();
	    break;
	case MeterStateCode:
	    data = new TempStateQueryData();
	    break;
	case OMRMeterSwitchCode:
	    data = new OMRTempSwitchData();
	    break;
	case OMRMeterTempCode:
	    data = new OMRTempData();
	    break;
	case ORMMeterTempUpperCode:
	    data = new OMRTempUpperData();
	    break;
	case PoleLightCode:
	    data = new PoleLightData();
	    break;
	case SmogAlertCode:
	    data = new SmogAlertData();
	    break;
	case TempQueryCode:
	    data = new TempQueryData();
	    break;
	case TurboFanCode:
	    data = new TurboFanData();
	    break;
	case AnotherFanStateCode:
	    data = new AnotherFanStateQueryData();
	    break;
	case MechanismStateQueryCode:
	    data = new MechanismStateQueryData();
	    break;
	case ValveSwitchCode:
	    data = new ValveSwitchData();
	    break;
	case ValveTempCode:
	    data = new ValveTempData();
	    break;
	case OMRTempModeCode:
	    data = new OMRTempModeData();
	    break;
	case TempProbeCode:
	    data = new TempProbeData();
	    break;
	case DoorCode:
	    data = new DoorData();
	    break;
	case HeatPipeStatusCode:
	    data = new HeatPipeStatusData();
	    break;
	case HeartBeatCode:
	    data = new HeartBeatData();
	    break;
	case PowerFaultReasonCode:
	    data = new PowerFaultReasonData();
	    break;
	case BeepCode:
	    data = new com.nltecklib.protocol.li.accessory.BeepAlertData();
	    break;
	case ColorLightCode:
	    data = new ColorLightData();
	    break;
	case POWER_STATE:
	    data = new PowerStateQueryData2();
	    break;
	case POWER_SWITCH2:
	    data = new PowerSwitchData2();
	    break;
	case FAN_STATE:
	    data = new FanStateQueryData2();
	    break;
	case FAN_CONTROL2:
	    data = new FanControlData2();
	    break;
	case HEART_BEAT2:
	    data = new HeartBeatData2();
	    break;
	case IndicatorCode:
	    data = new IndicatorControlData();
	    break;
	case ADDRESS:
	    data = new AddressData();
	    break;
	case EmergencyStopCode:
	    data = new EmergencyData();
	    break;
	case PowerResetCode:
	    data = new PowerResetData();
	    break;
	case SelectLightCode:
	    data = new SelectLightData();
	    break;
	case PowerSupplyCode:
	    data = new PowerSupplyData();
	    break;
	case PowerErrorInfoCode:
	    data = new PowerErrorInfoData();
	    break;
	case PowerLLZErrorInfoCode:
	    data = new PowerLLZErrorInfoData();
	    break;
	case ChannelLightCode:
	    data = new ChannelLightOptData();
	    break;
	case PingStateCode:
		data = new PingStateData();
		break;
	case FourLightStateCode:
		data = new FourLightStateData();
		break;
	case AirPressureStateCode:
		data = new AirPressureStateData();
		break;
	case AirValveSwitchCode:
		data = new AirValveSwitchData();
		break;
	case PingCalibrateSwitchCode:
		data = new PingCalibrateSwitchData();
		break;
		

	}
	if (data == null) {
	    throw new RuntimeException("unrecognized function code:" + code);
	}
	return data;
    }

    /**
     * 校准主控-设备主控通信协议
     * 
     * @author wavy_zheng 2020年10月31日
     * @param orient
     * @param code
     * @return
     */
    public static Data createMBWorkformData(Orient orient, MBWorkformCode code) {

	Data data = null;
	switch (code) {
	case CheckCalculateCode:
	    data = new MBCheckCalculateData();
	    break;
	case CheckCalibrateCode:
	    data = new MBCheckCalibrateData();
	    break;
	case CheckFlashWriteCode:
	    data = new MBCheckFlashWriteData();
	    break;
	case HeartbeatCode:
	    data = new MBHeartbeatData();
	    break;
	case LogicCalculateCode:
	    data = new MBLogicCalculateData();
	    break;
	case LogicCalibrateCode:
	    data = new MBLogicCalibrateData();
	    break;
	case LogicFlashWriteCode:
	    data = new MBLogicFlashWriteData();
	    break;
	case ModuleSwitchCode:
	    data = new MBModuleSwitchData();
	    break;
	case MatchCalCode:
	    data = new MBCalMatchData();
	    break;
	case DeviceBaseInfoCode:
	    data = new MBDeviceBaseInfoData();
	    break;
	case LogicCheckFlashWriteCode:
	    data = new MBLogicCheckFlashWriteData();
	    break;
	case UUIDCode:
	    data = new MBUuidData();
	    break;
	case SelfTestInfoCode:
	    data = new MBSelfTestInfoData();
	    break;
	case SelfCheckCode:
	    data = new MBSelfCheckData();
	    break;

	default:
	    break;

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    /**
     * 校准主控-PC上位机通信协议
     * 
     * @author wavy_zheng 2020年10月31日
     * @param orient
     * @param code
     * @return
     */
    public static Data createPCWorkformData(Orient orient, PCWorkformCode code) {

	Data data = null;
	switch (code) {
	case BaseCfgCode:
	    data = new BaseCfgData();
	    break;
	case LogicCalculateDebugCode:
	    data = new LogicCalculateDebugData();
	    break;
	case CalculatePlanCode:
	    data = new CalculatePlanData();
	    break;
	case ChnSelectCode:
	    data = new com.nltecklib.protocol.li.PCWorkform.ChnSelectData();
	    break;
	case LogicCalibrateDebugCode:
	    data = new LogicCalibrateDebugData();
	    break;
	case CalibratePlanCode:
	    data = new CalibratePlanData();
	    break;
	case CheckFlashWriteCode:
	    data = new CheckFlashWriteData();
	    break;
	case DataPushCode:
	    data = new LivePushData();
	    break;
	case DelayCode:
	    data = new DelayData();
	    break;
	case CalTempQueryDebugCode:
		data = new CalTempQueryDebugData();
		break;
	case DriverBindCode:
	    data = new DriverBindData();
	    break;
	case LogDebugCode:
	    data = new LogDebugPushData();
	    break;
	case LogicFlashWriteCode:
	    data = new com.nltecklib.protocol.li.PCWorkform.LogicFlashWriteData();
	    break;
	case LogPushData:
	    data = new LogPushData();
	    break;
	case MeterConnectCode:
	    data = new MeterConnectData();
	    break;
	case ModeSwitchCode:
	    data = new ModeSwitchData();
	    break;
	case ModuleSwitchCode:
	    data = new ModuleSwitchData();
	    break;
	case RangeCurrentPrecisionCode:
	    data = new RangeCurrentPrecisionData();
	    break;
	case RequireDataCode:
	    data = new RequestCalculateData();
	    break;
	case SteadyCode:
	    data = new SteadyCfgData();
	    break;
	case TestModeCode:
	    data = new TestModeData();
	    break;
	case CalMatchCode:
	    data = new CalMatchData();
	    break;
	case CheckCalculateDebugCode:
	    data = new CheckCalculateDebugData();
	    break;
	case CheckCalibrateDebugCode:
	    data = new CheckCalibrateDebugData();
	    break;
	case DeviceConnectCode:
	    data = new DeviceConnectData();
	    break;
	case UploadTestDotCode:
	    data = new UploadTestDotData();
	    break;
	case TimeCode:
	    data = new TimeData();
	    break;
	case IpCfgCode:
	    data = new IPCfgData();
	    break;
	case BaseInfoQueryCode:
	    data = new BaseInfoQueryData();
	    break;
	case CalibrateTerminalCode:
	    data = new CalibrateTerminalData();
	    break;
	case BindCalBoardCode:
	    data = new BindCalBoardData();
	    break;
	case CalUpdateFileCode:
	    data = new CalBoardUpdateFileData();
	    break;
	case CalUpdateModeCode:
	    data = new CalBoardUpdateModeData();
	    break;
	case MatchStateCode:
	    data = new MatchStateData();
	    break;
	case WorkformUpdateCode:
	    data = new WorkformUpdateData();
	    break;
	case SelfTestInfoCode:
	    data = new PCSelfTestInfoData();
	    break;
	case CalMathValueCode:
	    data = new CalMatchValueData();
	    break;
	case SelfCheckCode:
	    data = new PCSelfCheckData();
	    break;
	case HeartbeatCode:
	    data = new HeartbeatData();
	    break;
	case BaseInfoConfigCode:
	    data = new BaseInfoConfigData();
	    break;
	case CalCalculate2DebugCode:
	    data = new CalCalculate2DebugData();
	    break;
	case CalCalibrate2DebugCode:
	    data = new CalCalibrate2DebugData();
	    break;
	case CalRelayControlDebugCode:
	    data = new CalRelayControlDebugData();
	    break;
	case LogicCalculate2DebugCode:
		data = new LogicCalculate2DebugData();
		break;
	case LogicCalibrate2DebugCode:
		data = new LogicCalibrate2DebugData();
		break;
	case ConnectCalboardCode:
		data = new ConnectCalboardData();
		break;
	case ConnectDeviceCode:
		data = new ConnectDeviceData();
		break;
	case ConnectMeterCode:
		data = new ConnectMeterData();
		break;
	case ReadMeterCode:
		data = new ReadMeterData();
		break;
	case SwitchMeterCode:
		data = new com.nltecklib.protocol.li.PCWorkform.SwitchMeterData();
		break;
	case CalResistanceDebugCode:
		data = new CalResistanceDebugData();
		break;
	case CalTempControlDebugCode:
		data = new CalTempControlDebugData();
		break;
	case LogicFlashWrite2Code:
		data = new LogicFlashWrite2DebugData();
		break;
	case DeviceSelfCheckCode:
		data = new DeviceSelfCheckData();
		break;
	case DriverModeSwitchCode:
		data = new DriverModeSwitchData();
		break;
	case CalBoardTestModeCode:
		data = new CalBoardTestModeData();
		break;
	case RelaySwitchDebugCode:
		data = new RelaySwitchDebugData();
		break;
	case RelayEx:
		data = new RelayControlExDebugData();
		break;
	case CalRelayResistanceDebugCode:
		data = new ResistanceModeRelayDebugData();
		break;
	default:
	    break;

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

    /**
     * 第一代主控和上位机PC校准通信协议
     * 
     * @author wavy_zheng 2020年10月31日
     * @param orient
     * @param code
     * @return
     */
    public static Data createWorkformData(Orient orient, WorkformCode code) {

	Data data = null;
	switch (code) {
	case LogicCalCode:
	    data = new CalProcessData();
	    break;
	case LogicFlashWriteCode:
	    data = new CalFlashWriteData();
	    break;
	case CheckCalCode:
	    data = new CalCheckProcessData();
	    break;
	case CheckFlashWriteCode:
	    data = new CalCheckFlashWriteData();
	    break;
	case BoardMatchCode:
	    data = new CheckBoardMatchData();
	    break;
	case BoardTempCheckCode:
	    data = new CalTemperatureData();
	    break;
	case HeartBeatCode:
	    data = new MainHeartBeatData();
	    break;
	case MeasureCode:
	    data = new CalculateData();
	    break;
	case MeterSwitchCode:
	    data = new SwitchMeterData();
	    break;
	case ResisterFactorCode:
	    data = new ResisterFactorData();
	    break;
	case CalModeCode:
	    data = new CalWorkformModeData();
	    break;
	case LogicModeCode:
	    data = new CalLogicModeData();
	    break;
	case ModuleSwitchCode:
	    data = new CalLogicModuleSwitchData();
	    break;
	case CalBaseVoltCode:
	    data = new CalBaseVoltageData();
	    break;
	case LogicBaseVoltCode:
	    data = new LogicBaseVoltageData();
	    break;
	case HKCalculateCode:
	    data = new CalHKCalculateData();
	    break;
	case HKCalibrateCode:
	    data = new CalHKCalibrateData();
	    break;
	case HKCheckCalibrateCode:
	    data = new CalHKCheckCalibrateData();
	    break;
	case HKFlashWriteCode:
	    data = new CalHKFlashWriteData();
	    break;
	case CheckCalculateCode:
	    data = new CalCheckCalculateData();
	    break;
	case ExResisterFactorCode:
	    data = new ExResisterFactorData();
	    break;
	case ExLogicFlashWriteCode:
	    data = new CalExFlashWriteData();
	    break;
	default:
	    break;

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createLogicData(Orient orient, LogicCode code) {

	Data data = null;
	switch (code) {
	case PoleCode:
	    data = new LogicPoleData();
	    break;
	case BaseCountCode:
	    data = new LogicBaseCountData();
	    break;
	case ChnStopCode:
	    data = new LogicChnStopData();
	    break;
	case ChnSwitchCode:
	    data = new LogicChnSwitchData();
	    break;
	case DeviceProtectCode:
	    data = new LogicDeviceProtectData();
	    break;
	case WriteCalFlashCode:
	    data = new LogicFlashWriteData();
	    break;
	case PickupCode:
	    data = new LogicPickupData();
	    break;
	case StateCode:
	    data = new LogicStateData();
	    break;
	case ChnCalCode:
	    data = new LogicCalProcessData();
	    break;
	case ChnStartCode:
	    data = new LogicChnStartData();
	    break;
	case MatchCalCode:
	    data = new LogicCalMatchData();
	    break;
	case StartupCode:
	    data = new LogicStartupData();
	    break;
	case StopCode:
	    data = new LogicStopData();
	    break;
	case CalculateCode:
	    data = new LogicCalculateData();
	    break;
	case ModuleSwitchCode:
	    data = new LogicModuleSwitchData();
	    break;
	case FaultCheckCode:
	    data = new LogicFaultCheckData();
	    break;
	case HKCalculateCode:
	    data = new LogicHKCalculateData();
	    break;
	case HKCalibrateCode:
	    data = new LogicHKCalibrateData();
	    break;
	case HKWriteCalFlashCode:
	    data = new LogicHKFlashWriteData();
	    break;
	case HKOperateCode:
	    data = new LogicHKOperationData();
	    break;
	case HKProcedureCode:
	    data = new LogicHKProcedureData();
	    break;
	case LabPoleCode:
	    data = new LogicLabPoleData();
	    break;
	case LabProtectCode:
	    data = new LogicLabProtectData();
	    break;
	case LogicNewCalProcessCode:
	    data = new LogicNewCalProcessData();
	    break;

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

    public static Data createMainData(Orient orient, MainCode code) {

	Data data = null;
	switch (code) {
	case DateCode:
	    data = new DateData();
	    break;
	case PoleCode:
	    data = new PoleData();
	    break;
	case BaseCountCode:
	    data = new BaseCountData();
	    break;
	case LogicChnSwitchCode:
	    data = new UnitChannelSwitchData();
	    break;
	case ChnOperateCode:
	    data = new ChannelOptData();
	    break;
	case ProcedureCode:
	    data = new ProcedureData();
	    break;
	case PickupCode:
	    data = new PickupData();
	    break;
	case FirstCCProtectCode:
	    data = new FirstCCProtectData();
	    break;

	case DeviceStateCode:
	    data = new DeviceStateQueryData();
	    break;
	case StartupCode:
	    data = new StartupData();
	    break;
	case TempControlCode:
	    data = new TempData();
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
	case DeviceProtectCode:
	    data = new DeviceProtectData();
	    break;
	case TestNameCode:
	    data = new TestNameData();
	    break;
	case CheckProtectCode:
	    data = new CheckVoltProtectData();
	    break;
	case OfflineAcquireCode:
	    data = new OfflineAcquireData();
	    break;
	case OfflineUploadCode:
	    data = new OfflinePickupData();
	    break;
	case PressureChangeCode:
	    data = new PressureChangeProtectData();
	    break;
	case SaveParamCode:
	    data = new SaveParamData();
	    break;
	case IPAddressCode:
	    data = new IPAddressData();
	    break;
	case StartEndCheckCode:
	    data = new StartEndCheckData();
	    break;
	case DebugControlCode:
	    data = new DebugControlData();
	    break;
	case EnergyCode:
	    data = new EnergySaveData();
	    break;
	case UpgradeCode:
	    data = new UpgradeProgramData();
	    break;
	case UpgradeExCode:
		data = new UpgradeProgramExData();
		break;
	case UpgradeProgressCode:
	    data = new UpgradeProgressData();
	    break;
	case ChnSwitchCode:
	    data = new ChannelSwitchData();
	    break;
	case CylinderPressureCode:
	    data = new CylinderPressureProtectData();
	    break;
	case OfflineRunningCode:
	    data = new OfflineRunningData();
	    break;
	case ProcedureModeCode:
	    data = new ProcedureModeData();
	    break;
	case PushSwitchCode:
	    data = new PushSwitchData();
	    break;
	case SolenoidValveSwitchCode:
	    data = new SolenoidValveData();
	    break;
	case TrayTempUpperCode:
	    data = new SolenoidValveTempProtectData();
	    break;
	case BeepAlertCode:
	    data = new VoiceAlertData();
	    break;
	case ExChnOperateCode:
	    data = new ExChnsOperateData();
	    break;
	case ControlUnitCode:
	    data = new ControlUnitData();
	    break;
	case AlertCode:
	    data = new AlertData();
	    break;
	case AllowStepSkipCode:
	    data = new AllowStepSkipData();
	    break;

	case versionCode:
	    data = new VersionData();
	    break;
	case SelfCheckCode:
	    data = new SelfCheckData();
	    break;
	case ResetCode:
	    data = new ResetData();
	    break;
	case RecoveryCode:
	    data = new RecoveryData();
	    break;
	case DriverChnIndexDefineCode:
	    data = new DriverChnIndexDefineData();
	    break;
	case CabChnIndexDefineCode:
		data = new CabChnIndexDefineData(); 
		break;
	case JsonProcedureCode:
	    data = new JsonProcedureData();
	    break;
	case JsonProtectionCode:
	    data = new JsonProtectionData();
	    break;
	case JsonDeviceProtectCode:
	    data = new JsonDeviceProtectData();
	    break;
	case JsonPoleCode:
	    data = new JsonPoleData();
	    break;
	case ClearSmogCode:
	    data = new ClearSmogData();
	    break;
	case EnableCheckProtectionCode:
	    data = new EnableCheckProtectionData();
	    break;
	case SyncPressureParamCode:
		data = new SyncPressureParamData();
		break;
	case TemperAdjustCode:
		data = new TemperAdjustData();
		break;
	case JsonProductCfgCode:
		data = new JsonProductCfgData();
		break;
	case CylinderCfgCode:
		data = new CylinderCfgData();
		break;
	case CylinderControlCode:
		data = new CylinderControlData();
		break;
	case JsonProcedureEx:
		data = new JsonProcedureExData();
		break;
	case CCCVProtectCode:
		data = new CCVProtectData();
		break;
	case ChnLightCode:
		data = new ChnLightData();
		break;
	

	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;
    }

    public static Data createLogic2Data(Orient orient, Logic2Code code) {

	Data data = null;
	switch (code) {
	case PoleCode:
	    data = new Logic2PoleData();
	    break;
	case BaseCountCode:
	    data = new Logic2BaseCountData();
	    break;
	case Heartbeat:
	    data = new Logic2Heartbeat();
	    break;
	case DeviceProtectCode:
	    data = new Logic2DeviceProtectData();
	    break;
	case WriteCalFlashCode:
	    data = new Logic2FlashWriteData();
	    break;
	case PickupCode:
	    data = new Logic2PickupData();
	    break;
	case StateCode:
	    data = new Logic2StateData();
	    break;
	case ChnCalCode:
	    data = new Logic2CalProcessData();
	    break;
	case ChnStartCode:
	    data = new Logic2ChnOptData();
	    break;
	case MatchCalCode:
	    data = new Logic2CalMatchData();
	    break;
	case CalculateCode:
	    data = new Logic2CalculateData();
	    break;
	case ModuleSwitchCode:
	    data = new Logic2ModuleSwitchData();
	    break;
	case FaultCheckCode:
	    data = new Logic2FaultCheckData();
	    break;
	case LabPoleCode:
	    data = new Logic2LabPoleData();
	    break;
	case LabProtectCode:
	    data = new Logic2LabProtectData();
	    break;

	case ProcessCode:
	    data = new Logic2ProcedureData();
	    break;
	case OffLineRecoveryCode:
	    data = new Logic2StartupInitData();
	    break;
	case BatExistCode:
	    data = new Logic2BatExistData();
	    break;
	case ExtraCvCalProcessCode:
	    data = new Logic2ExtraCvCalProcess();
	    break;
	case PickupTestCode:
	    data = new Logic2PickupTestData();
	    break;
	case WriteCheckFlashCode:
	    data = new Logic2CheckFlashWriteData();
	    break;
	case READ_CHIP_ID:
	    data = new Logic2ReadChipIdData();
	    break;
	case UpgradeCode:
	    data = new Logic2UpgradeData();
	    break;
	case BaseVoltTestCode:
	    data = new Logic2BaseVoltTestData();
	    break;
	case SyncSkipCode:
	    data = new Logic2SyncStepSkipData();
	    break;
	case SoftversionCode:
	    data = new Logic2SoftversionData();
	    break;
	case UUIDCode:
	    data = new Logic2UUIDData();
	    break;
	case ProgramStateCode:
	    data = new Logic2ProgramStateData();
	    break;
	case ResetAddress:
	    data = new Logic2ResetDriverAddressData();
	    break;
	case REPAIR_MODE:
	    data = new Logic2RepairModeData();
	    break;
	case BatExistSwitchCode:
	    data = new Logic2BatExistSwitchData();
	    break;
	case DeviceProtectExCode:
	    data = new Logic2DeviceProtectExData();
	    break;
	case PoleExCode:
	    data = new Logic2PoleExData();
	    break;
	case SingleStepCode:
	    data = new Logic2SingleStepData();
	    break;
	
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

    public static Data createDriverG0Data(Orient orient, DriverG0Code code) {

	Data data = null;
	switch (code) {
	case PowerVoltageChnSwitchCode:
	    data = new DriverG0PVoltChnSwitchData();
	    break;
	case BackupVoltageChnSwitchCode:
	    data = new DriverG0BVoltChnSwitchData();
	    break;
	case ChipAddressCode:
	    data = new DriverG0ChipAddressData();
	    break;
	case UPGRADE_CODE:
	    data = new DriverG0UpgradeData();
	    break;
	case CHIP_WORK_MODE:
	    data = new DriverG0WorkModeData();
	    break;
	}
	if (data == null) {

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

    public static Data createDriverData(Orient orient, DriverCode code) {

	Data data = null;
	switch (code) {
	case PoleCode:
	    data = new DriverPoleData();
	    break;
	case ChnSwitchCode:
	    data = new DriverChnSwitchData();
	    break;
	case ChnPickupDataCode:
	    data = new DriverChnPickupData();
	    break;
	case ProcedureCode:
	    data = new DriverProcedureData();
	    break;
	case WorkModeCode:
	    data = new DriverWorkModeData();
	    break;
	case CalculateCode:
	    data = new DriverCalculateData();
	    break;
	case MeterCode:
	    data = new DriverMeterData();
	    break;
	case DieSwitchCode:
	    data = new DriverDieSwitchData();
	    break;
	case CalibrationFactorCode:
	    data = new DriverCalibrationFactorData();
	    break;
	case SelfCheckCode:
	    data = new DriverSelfCheckData();
	    break;
	case CheckCalFactorCode:
	    data = new DriverCheckCalFactorData();
	    break;
	case MultiModePickupCode:
	    data = new DriverMultiModePickupData();
	    break;
	case TransferProcessCode:
	    data = new DriverChangeStepData();
	    break;
	case UPGRADE_CODE:
	    data = new DriverUpgradeData();
	    break;
	case AnalogChnSwitchCode:
	    data = new DriverAnalogChnSwitchData();
	    break;
	case MultiTransferProcessCode:
	    data = new DriverMultiChangeStepData();
	    break;
	case SelfCheck2Code:
	    data = new DriverSelfCheck2Data();
	    break;
	case CALIBRATE_UUID_CODE:
	    data = new DriverUUIDData();
	    break;
	case VERSION_CODE:
	    data = new DriverVersionData();
	    break;
	case START_INIT_CODE:
	    data = new DriverStartInitData();
	    break;
	case Driver200aDiapCalCode:
	    data = new DriverDiapCalData();
	    break;
	case Driver200aCalibrationFactorCode:
	    data = new com.nltecklib.protocol.li.driver.curr200.DriverCalibrationFactorData();
	    break;
	case Driver200aMeterCode:
	    data = new com.nltecklib.protocol.li.driver.curr200.DriverMeterData();
	    break;
	case Driver200aMultiDiapTestCode:
	    data = new com.nltecklib.protocol.li.driver.curr200.DriverMultiDiapTestData();
	    break;
	case Driver200aDieSwitchCode:
	    data = new com.nltecklib.protocol.li.driver.curr200.DriverDieSwitchData();
	    break;
	case Driver200aAnalogChnSwitchCode:
	    data = new com.nltecklib.protocol.li.driver.curr200.Driver200aAnalogChnSwitchData();
	    break;
	case Driver200aProcedureCode:
	    data = new Driver200aProcedureData();
	    break;
	case MultiTransferProcessCode200a:
	    data = new Driver200aMultiChangeStepData();
	    break;
	case ProgramStateCode:
	    data = new Driver200aProgramStateData();
	    break;
	case DRIVER_CHIPID:
	    data = new Driver200aChipIDData();
	    break;
	    
	    //DRIVER_CHIPID
	}

	if (data == null)

	{

	    throw new RuntimeException("unrecognized function code:" + code);
	}

	return data;

    }

}
