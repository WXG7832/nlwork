package com.nlteck.i18n;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.nlteck.firmware.MainBoard;
import com.nlteck.util.XmlUtil;

public class I18N {

	// key常量

	// Environment
	public static final String MainStateDataParsingError = "MainStateDataParsingError";// 解析主控状态数据错误!
	public static final String MainStateGetError_IO = "MainStateGetError_IO";// 获取主控状态失败:执行IO命令错误
	public static final String MainStateGetError_CmdInterrupt = "MainStateGetError_CmdInterrupt";// 获取主控状态失败:执行命令被中断
	
	// RunningLamp
	public static final String RunningLampInitError = "RunningLampInitError";// 初始化运行灯发生错误
	
	//Channel
	public static final String DeviceMalfunctionProtect = "DeviceMalfunctionProtect";// 设备故障保护
	public static final String ChnStartError_UdtAbnormal = "ChnStartError_UdtAbnormal";// 该通道状态异常UDT无法正常启动
	public static final String StepInto = "StepInto";// 转入步次
	public static final String ChannelBufferedTooMuchDataAndPaused = "ChannelBufferedTooMuchDataAndPaused";// 因通道缓存数据过多而暂停
	public static final String LogicBoardCommunicationTimeout = "LogicBoardCommunicationTimeout";// 逻辑板(%d)通信超时
	public static final String WaitingStateDueToSynchronousMode = "WaitingStateDueToSynchronousMode";// 因同步模式进行等待状态
	public static final String ProcessCompleted = "ProcessCompleted";// 流程完毕!
	public static final String ProcessStart = "ProcessStart";// 开始流程测试
	public static final String UserStopChn = "UserStopChn";// 用户停止了通道
	public static final String UserPauseChn = "UserPauseChn";// 用户暂停了通道
	public static final String UserRecoveryChn = "UserRecoveryChn";// 用户恢复了通道
	public static final String RecoverStepFail_MaybeCommunicationAbnormal = "RecoverStepFail_MaybeCommunicationAbnormal";// 可能因通信异常导致恢复步次失败!
	public static final String JumpStepFail_MaybeCommunicationAbnormal = "JumpStepFail_MaybeCommunicationAbnormal";// 可能因通信异常导致恢复步次失败!
	public static final String JumpStepFail_VoltNotHighEnough = "JumpStepFail_VoltNotHighEnough";// 因电压未充到%smV,%d-%d跳转步次失败
	public static final String JumpStepFail_CurrNotLowEnough = "JumpStepFail_CurrNotLowEnough";// 因电流未降到%smA截止电流,%d-%d跳转步次失败
	public static final String JumpStepFail_VoltNotLowEnough = "JumpStepFail_VoltNotLowEnough";// 因电压未放到%smV,%d-%d跳转步次失败
	public static final String SleepRecoveryAlarm = "SleepRecoveryAlarm";// 休眠恢复报警
	public static final String SleepStartAlarm = "SleepStartAlarm";// 休眠启动报警
	public static final String SleepStartAlarm_ChnStartWithCurr = "SleepStartAlarm_ChnStartWithCurr";// 休眠启动报警[通道启动有电流]
	public static final String RecoveryAlarm_ChnRecoveryTimeout = "RecoveryAlarm_ChnRecoveryTimeout";// 恢复报警[通道恢复超时]
	public static final String RecoveryAlarm_ChnRecoveryStartNoCurr = "RecoveryAlarm_ChnRecoveryStartNoCurr";// 恢复报警[通道恢复启动无电流]
	public static final String StartAlarm_ChnStartTimeout = "StartAlarm_ChnStartTimeout";// 启动报警[通道启动超时]
	public static final String StartAlarm_ChnStartNoCurr = "StartAlarm_ChnStartNoCurr";// 启动报警[通道启动无电流]
	public static final String ContinuousStartAlarm_ChnStartCurrError = "ContinuousStartAlarm_ChnStartCurrError";// 连续启动报警[通道启动电流错误]
	public static final String DeviceVoltUpper_MainDetectedLogicVolt = "DeviceVoltUpper_MainDetectedLogicVolt";// 设备电压超限[主控检测到逻辑电压:%smV%s];
	public static final String VoltHighThanSetValue = "VoltHighThanSetValue";// ,超过设定值%f,mV;
	public static final String CurrHighThanSetValue = "CurrHighThanSetValue";// ,超过设定值%f,mA;
	public static final String DeviceVoltUpper_MeasureVolt = "DeviceVoltUpper_MeasureVolt";// 设备电压超限[回检电压:%smV%s]
	public static final String ChnMeasureMalfunction = "ChnMeasureMalfunction";// 通道回检故障
	public static final String BatteryVoltUpper_MainDetectedLogicVolt = "BatteryVoltUpper_MainDetectedLogicVolt";// 电芯电压超限[主控检测电压:%smV%s]
	public static final String BatteryCurrUpper_MainDetectedLogicCurr = "BatteryCurrUpper_MainDetectedLogicCurr";// 电芯电流超限[主控检测电流:%smA%s]
	public static final String LogicBoardDetectedAbnormal_Instant = "LogicBoardDetectedAbnormal_Instant";// 逻辑板检测到异常[瞬时电压%smV,瞬时电流%smA]
	public static final String BatteryVoltUpper_LogicBoardDetectedVolt = "BatteryVoltUpper_LogicBoardDetectedVolt";// 电芯电压超限[逻辑板检测电压:%smV%s]
	public static final String ChnCurrUpper_LogicBoardDetectedCurr = "ChnCurrUpper_LogicBoardDetectedCurr";// 通道电流超限[逻辑板检测电流:%smA%s]
	public static final String PoleReverse_DetectedChnPoleReverse = "PoleReverse_DetectedChnPoleReverse";// 极性反接[检测到通道极性反接]
	public static final String CapatityUpper_StepCapatityUpper = "CapatityUpper_StepCapatityUpper";// 容量超限[步次容量:%smAh,,超过流程预设容量(%d * %f)]
	
	//ControlUnit
	public static final String StartUnitNoProcess = "StartUnitNoProcess";//启动分区%d无流程
	public static final String StartUnitProcessSize = "StartUnitProcessSize";//启动分区%d流程%d步次
	public static final String LogicBoardCommunicateInterrupted_PleaseCheck = "LogicBoardCommunicateInterrupted_PleaseCheck";//逻辑板%d已通信中断,请检查!
	public static final String CheckBoardCommunicateInterrupted_PleaseCheck = "CheckBoardCommunicateInterrupted_PleaseCheck";//回检板%d已通信中断,请检查!
	public static final String Device="Device";//设备
	public static final String UnitWithIndex="UnitWithIndex";//分区%d
	public static final String StartTimeout="StartTimeout";//启动超时
	
	//DriverBoard
	public static final String ChnPickupCommunicationTimeout="ChnPickupCommunicationTimeout";//通道采集数据通信超时!
	public static final String DriverBoardTempUpper="DriverBoardTempUpper";//驱动板%d温度(%s℃)超安全限值%f℃
	public static final String DriverBoardTempRecoverNormal="DriverBoardTempRecoverNormal";//驱动板%d温度(%s℃)已恢复正常
	
	//GpioPowerController
	public static final String GpioInitError="GpioInitError";//初始化电源控制器发生错误!
	public static final String PowerOffPauseDevice = "PowerOffPauseDevice"; //设备断电，暂停流程
	public static final String PowerResetPauseDevice = "PowerResetPauseDevice"; //设备正在复位，暂停流程
	public static final String SavePowerOffStateData = "SavePowerOffStateData"; //保存断电前的状态和数据
	public static final String PowerOffState = "PowerOffState"; //掉电状态
	public static final String PowerResetingDevice = "PowerResetingDevice"; //正在复位电源，请稍后
	public static final String PowerResetResumeDevice = "PowerResetResumeDevice"; //设备复位完成，开始恢复流程
	//LogicBoard
	public static final String StepSkipErrorNull = "StepSkipErrorNull"; //已经无法转下一个步次
	public static final String StepSkipError = "StepSkipError"; //步次跳转异常
	public static final String EnterCalMode="EnterCalMode";//进入校准模式
	public static final String EnterCalModeFail = "EnterCalModeFail";
	public static final String LogicBoardCommunicateInterrupted = "LogicBoardCommunicateInterrupted";//逻辑板%d通信中断
	public static final String LogicBoardCommunicateNormal = "LogicBoardCommunicateNormal";//逻辑板%d通信正常
	public static final String OfflineRuntimeUpper = "OfflineRuntimeUpper";//离线运行超过设定时间%dmin,退出离线运行
	public static final String DeviceDropped = "DeviceDropped";//设备掉线
	public static final String CloseChnByChnProtect = "CloseChnByChnProtect";//因通道保护,关闭通道
	public static final String JumpStep = "JumpStep";//跳步次,%d-%d,mode:%s
	public static final String CloseChnByProcessComplete = "CloseChnByProcessComplete";//因通道流程结束,关闭通道
	public static final String StepIntoBySetedTime = "StepIntoBySetedTime";//因设定时间转入步次,%d-%d,mode:%s
	public static final String StepNextBySetedTime = "StepNextBySetedTime";//因设定时间转下步次,%d-%d,mode:%s
	public static final String StepNextByDeltaVolt = "StepNextByDeltaVolt";//因△v转下步次,%d-%d,mode:%s
	public static final String StepNextByCapacity = "StepNextByCapacity";//因容量转下步次,%d-%d,mode:%s
	public static final String StepNextByCutOffCondition = "StepNextByCutOffCondition";//因截止条件转下步次,%d-%d,mode:%s
	public static final String ChnSleepWithCurr = "ChnSleepWithCurr";//通道休眠仍有电流%smA
	public static final String CcCloseChnAbnormalByCutOffVolt = "CcCloseChnAbnormalByCutOffVolt";//cc未正常通过截止电压关闭通道
	public static final String CvCloseChnAbnormalByCutOffCurr = "CvCloseChnAbnormalByCutOffCurr";//cv未正常通过截止电流关闭通道
	public static final String DcCloseChnAbnormalByCutOffVolt = "DcCloseChnAbnormalByCutOffVolt";//dc未正常通过截止电压关闭通道
	public static final String CloseLogicBoardChnCauseLogicError = "CloseLogicBoardChnCauseLogicError";//关闭逻辑板%d通道发生逻辑错误
	public static final String CloseLogicBoardChnCauseError = "CloseLogicBoardChnCauseError";//关闭逻辑板%d通道发生错误
	public static final String OpenLogicBoardChnCauseLogicError = "OpenLogicBoardChnCauseLogicError";//打开逻辑板%d通道发生逻辑错误
	public static final String LogicBoardProtectSetError = "LogicBoardProtectSetError";//设置逻辑板%d设备保护返回错误
	public static final String LogicBoardPoleSetError = "LogicBoardPoleSetError";//配置逻辑板%d极性发生通信错误
	public static final String LogicBoardChnStateInitError = "LogicBoardChnStateInitError";//初始化逻辑板%d通道状态发生逻辑错误
	public static final String LogicBoardIsCalMode_PleaseExit = "LogicBoardIsCalMode_PleaseExit";//逻辑板%d处于校准模式,请先退出校准模式
	public static final String LogicBoardExitCalModeFail = "LogicBoardExitCalModeFail";//退出逻辑板%d校准模式失败
	public static final String LogicBoardSetFail = "LogicBoardSetFail";//配置逻辑板%d失败
	public static final String CalBoardBaseVoltSetFail = "CalBoardBaseVoltSetFail";//设置校准板基准电压发生错误
	public static final String CalBoardBaseVoltMatchExitFail = "CalBoardBaseVoltMatchExitFail";//退出校准板基准电压匹配模式发生错误
	public static final String CalLogicBoardCauseLogicError = "CalLogicBoardCauseLogicError";//校准逻辑板%d发生逻辑错误
	
	public static final String WriteLogicStartupInitResponseFail = "WriteLogicStartupInitResponseFail"; //写入初始化状态参数失败
	public static final String WriteLogicProcedureResponseFail = "WriteLogicProcedureResponseFail"; //写入逻辑板流程失败
	public static final String WriteLogicBatExistResponseFail = "WriteLogicBatExistResponseFail"; //写入逻辑板通道电芯是否存在失败
	public static final String WriteLogicStateModeResponseFail = "WriteLogicStateModeResponseFail"; //写入逻辑板通道电芯是否存在失败
	
	
	//MainBoard
	public static final String DateTimeAdjustFail = "DateTimeAdjustFail";//校准时间失败:%s
	public static final String DateTimeAdjustCauseError = "DateTimeAdjustCauseError";//校准时间发生错误:%s
	public static final String IpAddressSetFail = "IpAddressSetFail";//修改IP地址失败
	public static final String ChnVoltSetUpper = "ChnVoltSetUpper";//配置通道电压上限:%fmV,不能超过最大设置值%fmV
	public static final String DeviceVoltSetUpper = "DeviceVoltSetUpper";//配置设备电压上限:%fmV,不能超过最大设置值%fmV
	public static final String ChnCurrSetUpper = "ChnCurrSetUpper";//配置设备电流上限:%fmA,不能超过最大设置值%fmA
	public static final String PoleVoltDefineError = "PoleVoltDefineError";//配置极性界定值:%fmV,必须在范围(%f-%f)之间
	public static final String FirstStepCCVoltLower = "FirstStepCCVoltLower";//首步次CC电压下限:%fmV不能大于电压上限%fmV
	public static final String FirstStepCCVoltUpper = "FirstStepCCVoltUpper";//首步次CC电压不能超过最大电压设置上限%fmV
	public static final String DeviceExitOfflineMode = "DeviceExitOfflineMode";//设备已退出离线运行模式
	public static final String NoProcess_QueryError = "NoProcess_QueryError";//没有配置流程,查询错误!
	public static final String OperationError = "OperationError"; //操作异常
	
	//procedure support 
	public static final String LogicProcedureNotSupport = "LogicProcedureNotSupport"; //该设备不支持多逻辑分区功能
	public static final String DriverProcedureNotSupport = "DriverProcedureNotSupport"; //该设备不支持多驱动分区功能
	//comm
	public static final String CommError = "CommError";//通信错误
	public static final String CommTimeout = "CommTimeout";//通信回复超时
	public static final String ResponseError = "ResponseError";//通道回复码错误
	public static final String LogicCommError = "LogicCommError"; //逻辑板通信失联
	public static final String DriverCommError = "DriverCommError"; //逻辑板通信失联
	//workmode

	public static final String CheckChangeWorkmodeFail = "CheckChangeWorkmodeFail";//切换模式失败
	public static final String LogicChangeWorkmodeFail = "LogicChangeWorkmodeFail";//切换模式失败
	//coreService
	public static final String ChnNotAllInUnit = "ChnNotAllInUnit";//通道需要全部在同一分区
	public static final String LoadFileError = "LoadFileError"; //加载结构文件错误
	public static final String GetUnitInfoEmpty = "GetUnitInfoEmpty"; //获取分区信息为空
	public static final String DataIsFull = "DataIsFull"; //数据缓存区已满
	public static final String ProcedureNotExist = "ProcedureNotExist"; //流程不存在
	public static final String SyncStepExceptionAndPause = "SyncStepExceptionAndPause"; //同步异常
	public static final String ChnSyncStepErr = "ChnSyncStepErr"; //同步步次错误
	public static final String ChnSyncLoopErr = "ChnSyncLoopErr"; //同步步次错误
	public static final String SyncNotSupport = "SyncNotSupport"; //因同步模式不支持
	public static final String PressureChange = "PressureChange"; //压力变更消息
	public static final String MaintainNotExecute = "MaintainNotExecute"; //因维护无法操作
	public static final String ResetRunning = "ResetRunning"; //因复位后无法操作
	public static final String CannotOptInRunning = "CannotOptInRunning"; //因正在运行而无法操作
	
	//offline
	public static final String NetworkOff = "NetworkOff";//设备已掉线
	public static final String NetworkOn = "NetworkOn";//设备已掉线
	public static final String DeviceEnterOfflineRun = "DeviceEnterOfflineRun";//设备已进入离线运行模式
	public static final String DeviceExitOfflineRun = "DeviceExitOfflineRun";//设备已退出离线运行模式
	//logicservice
	
	//accessories  配件
	public static final String TempMeterOver="TempMeterOver"; //温控表超温
	public static final String DeviceTempUpper = "DeviceTempUpper"; //设备超温报警
	public static final String DeviceTempLower = "DeviceTempLower"; //设备低温报警
	public static final String TempMeterError="TempMeterError"; //温控表超温
	public static final String TempProtectNotInRange ="TempProtectNotInRange"; //恒温范围保护
	public static final String TempProtectNotOpen = "TempProtectNotOpen"; //恒温启动保护
	public static final String TempOverUnderCloseSystem = "TempOverUnderCloseSystem"; //恒温系统关闭后温度仍异常
	
	public static final String FirmIsDisabled = "FirmIsDisabled"; //操作异常
	
//	public static final String NotSuccessResult = "ChnNotAllInUnit";//通道需要全部在同一分区
	
	//OPERATRION
	public static final String startupFans = "startupFans" ; //正在启动风机
	public static final String OpenSerialFail = "OpenSerialFail"; //打开串口失败
	public static final String OpenFansFail = "OpenFansFail"; //启动风机失败
	public static final String OpenFansSuccess = "OpenFansSuccess"; //启动风机成功
	public static final String CloseFansFail = "CloseFansFail"; //关闭风机失败
	public static final String CloseFansSuccess = "CloseFansSuccess"; //启动风机失败
	public static final String StartTempControlSystem = "StartTempControlSystem"; //温控系统已经启动
	public static final String ScreenCommError = "ScreenCommError"; //液晶屏通信异常
	public static final String SelfCheckingFirms = "SelfCheckingFirms"; //正在自检设备配件
	public static final String SelfCheckingFirmsOver = "SelfCheckingFirmsOver"; //正在自检设备配件
	public static final String SelfCheckingMustBeUdt = "SelfCheckingMustBeUdt"; //正在自检设备配件
	public static final String SelfCheckingIds = "SelfCheckingIds"; //正在自检设备配件
	public static final String SelfCheckingCores = "SelfCheckingCores"; //正在自检核心配件
	
	public static final String DriverSelfCheckingErrCode = "DriverSelfCheckingErrCode"; //自检通信返回错误
	public static final String DriverSelfCheckingTimeout = "DriverSelfCheckingTimeout"; //自检通信返回超时
	public static final String DriverSelfCheckingVersionErrCode = "DriverSelfCheckingVersionErrCode"; //自检软件版本通信返回错误
	public static final String DriverSelfCheckingVersionTimeout = "DriverSelfCheckingVersionTimeout"; //自检软件版本通信返回超时
	
	
	
	public static final String ChangeCheckFlash = "ChangeCheckFlash"; //检测到ID变更开始写入回检系数
	public static final String ChangeDriverFlash = "ChangeDriverFlash"; //检测到ID变更开始写入回检系数
	public static final String ProgramFailed = "ProgramFailed"; //程序烧录失败
	
	//init
	
	public static final String StartingSystem = "StartingSystem"; //正在启动系统
	public static final String InitScreenSuccess = "InitScreenSuccess"; //初始化液晶屏成功
	public static final String InitFansSuccess = "InitFansSuccess"; //初始化风机成功
	public static final String InitPowersSuccess = "InitFansSuccess"; //初始化电源成功
	public static final String InitMechanismSuccess = "InitMechanismSuccess"; //初始化机械成功
	public static final String InitProbesSuccess = "InitProbesSuccess"; //初始化探头成功
	public static final String InitSmogsSuccess = "InitProbesSuccess"; //初始化烟雾报警器成功
	public static final String InitDoorsSuccess = "InitDoorsSuccess"; //初始化门近开关成功
	public static final String InitStateLightsSuccess = "InitStateLightsSuccess"; //初始化状态灯成功
	public static final String InitAudioLightsSuccess = "InitAudioLightsSuccess"; //初始化声光报警器成功
	public static final String InitTempControlSuccess = "InitTempControlSuccess"; //初始化温控系统成功
	public static final String InitAccessoriesFail = "InitAccessoriesFail"; //初始化温控系统成功
	public static final String InitSystemSuccess = "InitSystemSuccess"; //初始化系统成功
	public static final String InitSystemFail = "InitSystemFail"; //初始化系统错误
	public static final String DriverRest = "DriverRest"; //发现逻辑板%d与驱动板通信失联，开始复位
	public static final String InitDeviceAlertManagerSuccess = "InitDeviceAlertManagerSuccess"; //初始化报警管理器
	public static final String InitNetworkSuccess = "InitNetworkSuccess"; //初始化网路组件成功
	
	public static final String InitLogicboard = "InitLogicboard"; //初始化逻辑板
	public static final String InitCheckboard = "InitCheckboard"; //初始化备份电压板
	public static final String InitDriverboard = "InitDriverboard"; //初始化驱动板
	public static final String InitControlunitSuccess = "InitControlunitSuccess"; //初始化控制分区完成
	
	//chn
	public static final String ChnStartup = "ChnStartup"; //通道开始测试
	public static final String ChnPause = "ChnPause"; //通道暂停
	public static final String ChnStop = "ChnStop"; //通道停止
	public static final String ChnResume = "ChnResume"; //通道恢复
	public static final String ChnSkipStep = "ChnSkipStep"; //通道跳转步次
	public static final String ChnClose = "ChnClose"; //通道关闭
	public static final String ChnAlert = "ChnAlert"; //通道保护
	
	//power manager
	public static final String AuxiliaryPowerException = "AuxiliaryPowerException"; //辅助电源异常
	public static final String WaitForPowerOn = "WaitForPowerOn"; //等待电源启动
	public static final String PowerNotEnough = "PowerNotEnough"; //电源个数不足
	public static final String PowerNotMeetProcedure = "PowerNotMeetProcedure"; //电源个数无法满足流程需求
	public static final String PowerNotMeetMinNeed = "PowerNotMeetMinNeed"; //电源无法满足最小需求
	public static final String PowerBackup  = "PowerBackup" ; //同组电源备份
	public static final String Inverter  = "Inverter" ; //
	public static final String AuxiliaryPower  = "AuxiliaryPower" ; //辅助电源备份
	public static final String Breakdown  = "Breakdown" ; //发生故障
	public static final String PowerCountErr = "PowerCountErr"; //电源数据不匹配
	public static final String PowerFaultRecovery = "PowerFaultRecovery"; //电源故障，开始尝试重启电源修复
	//mechnism 
	public static final String CylinderClosing = "CylinderClosing"; //气缸正在闭合
	public static final String CylinderOpening = "CylinderOpening"; //气缸正在打开
	public static final String CylinderChecking = "CylinderChecking"; //气缸正在打开
	
	
	//protection
	public static final String CurrentConstProtection = "CurrentConstProtection"; //恒流保护
	public static final String VoltageConstProtection = "VoltageConstProtection"; //恒压保护
	public static final String VoltageDescendProtection = "VoltageDescendProtection"; //电压下降保护
	public static final String VoltageAscendProtection = "VoltageAscendProtection"; //电压上升保护
	
	
	//alert
	public static final String CheckDeviceOverVolt = "CheckDeviceOverVolt"; //设备超压
	public static final String CoreDeviceOverVolt = "CoreDeviceOverVolt"; //设备超压
	public static final String CheckPoleReverse = "CheckPoleReverse"; //回检极性反转
	public static final String LogicPoleReverse = "LogicPoleReverse"; //逻辑极性反接
	public static final String LogicBatOverVolt = "LogicBatOverVolt"; //逻辑板电池超压
	public static final String LogicBatOverCurr = "LogicBatOverCurr"; //逻辑板电池超流
	public static final String LogicOverTime    = "LogicOverTime"; //逻辑板检测到步次时间超时，终止流程
	public static final String DeviceError = "DeviceError"; //设备超压
	public static final String CapacityCoefficien = "CapacityCoefficien"; //容量系数保护
	public static final String CoreBatOverVolt = "CoreBatOverVolt"; //主控检测到电池超压
	public static final String CoreBatOverCurr = "CoreBatOverCurr"; //主控检测到电池超流
	public static final String StepChangeExcept = "StepChangeExcept"; //逻辑板未按条件正常转步次
	public static final String LogicUnknownExcept = "LogicUnknownExcept"; //逻辑板发生异常
	public static final String LogicChnSelfStop   = "LogicChnSelfStop"; //逻辑板通道意外关闭
	public static final String CheckVoltageOver   = "CheckVoltageOver"; //检测到电压超限，关闭通道
	public static final String ChnCurrentException = "ChnCurrentException"; //通道异常电流
	
	//流程起始结束电压保护
	public static final String StartVoltLower = "StartVoltLower";
	public static final String StartVoltUpper = "StartVoltUpper";
	public static final String EndVoltLower = "EndVoltLower";
	public static final String EndVoltUpper = "EndVoltUpper";
	public static final String EndCapacityLower = "EndCapacityLower";
	public static final String EndCapacityUpper = "EndCapacityUpper";
	public static final String StartVoltOffset = "StartVoltOffset"; //起始电压存在偏差
	
	//cc保护
	public static final String CCVoltUpper = "CCVoltUpper";
	public static final String CCVoltLower = "CCVoltLower";
	public static final String CCCurOffset = "CCCurOffset";
	public static final String CCVoltOffset2000u = "CCVoltOffset2000u";
	public static final String CCVoltOffset2000d = "CCVoltOffset2000d";
	public static final String CCVoltDesc = "CCVoltDesc";
	public static final String CCCapacityUpper = "CCCapacityUpper";
	public static final String CCTimeUpper = "CCTimeUpper";
	public static final String CCSlopeUpper = "CCSlopeUpper"; //斜率超上限
	public static final String CCSlopeLower = "CCSlopeLower"; //斜率超上限
	
	//cv保护
	public static final String CVCurUpper = "CVCurUpper";
	public static final String CVCurLower = "CVCurLower";
	public static final String CVVoltOffset = "CVVoltOffset";
	public static final String CVCapacityUpper = "CVCapacityUpper";
	public static final String CVTimeUpper = "CVTimeUpper";
	public static final String CVCurAsc = "CVCurAsc";
	
	//dc保护
	public static final String DCVoltUpper = "DCVoltUpper";
	public static final String DCVoltLower = "DCVoltLower";
	public static final String DCCurOffset = "DCCurOffset";
	public static final String DCVoltOffset2000u = "DCVoltOffset2000u";
	public static final String DCVoltOffset2000d = "DCVoltOffset2000d";
	public static final String DCVoltAsc = "DCVoltAsc";
	public static final String DCCapacityUpper = "DCCapacityUpper";
	public static final String DCTimeUpper = "DCTimeUpper";
	public static final String DCSlopeUpper = "DCSlopeUpper";
	public static final String DCSlopeLower = "DCSlopeLower";
	
	//first cc
	public static final String FirstCCProtect = "FirstCCProtect";
	
	//SLP
	public static final String SleepProtect = "SleepProtect";
	
	//other
	public static final String VoltageOffset = "VoltageOffset";
	public static final String ResisterOffset = "ResisterOffset";
	public static final String CurrLowerProtect = "CurrLowerProtect";
	public static final String StepTimeout = "StepTimeout";
	
	//upgrade
	public static final String UpgradeCoreFail = "UpgradeCoreFail"; //升级主控程序失败
	public static final String UpgradeCoreCfgFail = "UpgradeCoreCfgFail"; //升级主控配置文件失败
	public static final String UpgradeLogicFail = "UpgradeLogicFail"; //升级逻辑板程序失败
	public static final String UpgradeCheckFail = "UpgradeCheckFail"; //升级回检板程序失败
	public static final String UpgradeLogicDriverFail = "UpgradeLogicDriverFail"; //升级逻辑板驱动程序失败
	public static final String UpgradeCheckDriverFail = "UpgradeCheckDriverFail"; //升级逻辑板驱动程序失败
	
	public static final String UpgradeCoreMissing = "UpgradeCoreMissing"; //升级主控程序失败
	public static final String UpgradeCoreCfgMissing = "UpgradeCoreCfgMissing"; //升级主控配置文件失败
	public static final String UpgradeLogicMissing = "UpgradeLogicMissing"; //升级逻辑板程序失败
	public static final String UpgradeCheckMissing = "UpgradeCheckMissing"; //升级回检板程序失败
	public static final String UpgradeLogicDriverMissing = "UpgradeLogicDriverMissing"; //升级逻辑板驱动程序失败
	public static final String UpgradeCheckDriverMissing = "UpgradeCheckDriverMissing"; //升级逻辑板驱动程序失败
	public static final String UpgradeFailWithFormation = "UpgradeFailWithFormation"; //设备正在运行无法升级
	public static final String UpgradeFailNoInUpgradeMode = "UpgradeFailNoInUpgradeMode";//设备未进入升级模式
	public static final String UpgradeFileMissing = "UpgradeFileMissing";//升级文件丢失
	public static final String UpgradeFileFail = "UpgradeFileFail";//升级文件丢失
	public static final String UpgradeFileStateErr = "UpgradeFileStateErr";//升级文件状态不符
	
	public static final String CanNotRecovery = "CanNotRecovery";  //设备处于未可修复状态;
	public static final String ChnOverVoltException = "ChnOverVoltException"; //设备通道超压后电压仍上升
	public static final String ChnStepChangeException = "ChnStepChangeException";//步次未按正常条件截止
	public static final String ChnContinueAlertException = "ChnContinueAlertException"; //通道连续报警异常
	
	public static final String DriverBaseProtectProtectException = "DriverBaseProtectProtectException"; //驱动板基础保护配置失败
	public static final String DriverPoleProtectProtectException = "DriverPoleProtectProtectException"; //驱动板极性保护配置失败
	
	public static final String DriverEnterWorkmodeException = "DriverEnterWorkmodeException"; //驱动板工作模式
	
	
	public static final String PressureChangeTimeOutException = "PressureChangeTimeOutException"; //变压超时
	
	private static I18N i18n;
	public final static String LANGUAGE_PATH = "/com/nlteck/resources/";
	
//	public enum Language {
//		
//		en_US , zh_CH;
//	}
//	

	private Map<String, String> map = new HashMap<String, String>();

	public static void init() throws Exception {
		i18n = new I18N();
		
		String path = LANGUAGE_PATH + MainBoard.startupCfg.getLanguage() + ".xml";
		
		InputStream is = I18N.class.getResourceAsStream(path);
		Document document = XmlUtil.loadXml(is);
		Element languageRoot = document.getRootElement();
		List<Node> itemNodes = languageRoot.selectNodes("item");
		for (Node node : itemNodes) {
			if (node instanceof Element) {
				Element ele = (Element) node;
				String key = ele.attributeValue("key");
				String value = ele.attributeValue("value");
				if (i18n.map.containsKey(key)) {
					System.err.println("xml repeat : "+key);
				}
				i18n.map.put(key, value);
			}
		}
		
		//System.out.println(I18N.getVal(I18N.CapacityCoefficien, 1.0,2.0,3.0));

//		 System.out.println(i18n.map);
	}

	public static String getVal(String key, Object... obj) {

		if (i18n.map.containsKey(key)) {
			try {
				return String.format(i18n.map.get(key), obj);
			} catch (Exception e) {
				return i18n.map.get(key);
			}
		}
		System.err.println("xml not exist : "+key);
		return key;
	}

	public static String getVal(String key) {

		if (i18n.map.containsKey(key)) {

			return i18n.map.get(key);
		}
		System.err.println("xml not exist : "+key);
		return key;
	}

	
	public static void main(String[] args) {
		try {

			//init("config/language/zh_CN.xml");

			// 检查key完整性
			Field[] fields = I18N.class.getDeclaredFields();

			for (Field field : fields) {
				String descriptor = Modifier.toString(field.getModifiers());// 获得其属性的修饰

				if (descriptor.contains("public static final")) {

					if (!field.getName().equals(field.get(new I18N()))) {
						System.err.println(field.getName() + " != " + field.get(new I18N()));
					}
				}
			}
			System.out.println("========================================");
			for (Field field : fields) {
				String descriptor = Modifier.toString(field.getModifiers());// 获得其属性的修饰

				if (descriptor.contains("public static final")) {
					getVal(field.get(new I18N()).toString());
//					System.out.println( getVal(field.get(new I18N()).toString()));
				}
			}
			System.out.println("check over!");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
