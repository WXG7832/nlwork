package com.nlteck.calModel.base;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.nltecklib.utils.XmlUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午1:29:37 类说明
 */
public class I18N {

	public static final String Success = "Success";
	public static final String Fail = "Fail";

	public static final String CmdFromDeviceError = "CmdFromDeviceError";// 收到设备主控的命令(该主控是被动接收方),请查询命令逻辑!
	public static final String InitMainboard = "InitMainboard"; // 初始化主控板
	public static final String InitCalboard = "InitCalboard"; // 初始化校准板
	public static final String DelayDataNotExist = "DelayDataNotExist"; // 延时配置不存在
	public static final String CalibratePlanDataNotExist = "CalibratePlanDataNotExist"; // 校准方案不存在
	public static final String SteadyCfgDataNotExist = "SteadyCfgDataNotExist"; // ADC稳定度检测方案不存在
	public static final String RangeCurrentPrecisionDataNotExist = "RangeCurrentPrecisionDataNotExist"; // 精度档位配置不存在
	public static final String CalculatePlanDataNotExist = "CalculatePlanDataNotExist"; // 计量方案不存在
	public static final String LogicIndexError = "LogicIndexError"; // 错误的逻辑板号(%d)
	public static final String CommTypeNotSupport = "CommTypeNotSupport";// 不支持的通信类型：%s
	public static final String SetSystemIPError = "SetSystemIPError";// 配置设备IP发生错误
	public static final String AdcNotStable = "AdcNotStable";// adc不稳定
	public static final String AdcCountNotEnough = "AdcCountNotEnough";// adc数量不足：%d
	public static final String GpioInitError = "GpioInitError";// 初始化电源控制器发生错误!
	public static final String RunningLampInitError = "RunningLampInitError";// 初始化运行灯发生错误
	public static final String ConnectFail = "ConnectFail";// %s连接失败
	public static final String StartCalibrate = "StartCalibrate";// 开始校准
	public static final String StartCalculate = "StartCalculate";// 开始计量
	public static final String UserStoped = "UserStoped";// 用户强行停止
	public static final String WriteLogicFlash = "WriteLogicFlash";// 写入逻辑板flash
	public static final String WriteCheckFlash = "WriteCheckFlash";// 写入回检板flash
	public static final String StartTestDot = "StartTestDot";// 开始测试点：%s
	public static final String AdcOffsetOver = "AdcOffsetOver";// adc偏差过大：%f(%f-%f)
	public static final String MeasureAdcOffsetOver = "MeasureAdcOffsetOver";// adc偏差过大：%f
	public static final String CheckBoardAdcOffsetOver = "CheckBoardAdcOffsetOver";// 回检板adc偏差过大：%f(%f-%f)
	public static final String ActualValOffsetOver = "ActualValOffsetOver";// 实际值偏差过大：%f
	public static final String MeasureActualValOffsetOver = "MeasureActualValOffsetOver";// 实际值偏差过大：%f
	public static final String ProgramKOffsetOver = "ProgramKOffsetOver";// 程控K值偏差过大：%f
	public static final String ProgramBOffsetOver = "ProgramBOffsetOver";// 程控B值偏差过大：%f
	public static final String AdcKOffsetOver = "AdcKOffsetOver";// adcK值偏差过大：%f
	public static final String AdcBOffsetOver = "AdcBOffsetOver";// adcB值偏差过大：%f
	public static final String CheckBoardAdcKOffsetOver = "CheckBoardAdcKOffsetOver";// 回检板adcK值偏差过大：%f
	public static final String CheckBoardAdcBOffsetOver = "CheckBoardAdcBOffsetOver";// 回检板adcB值偏差过大：%f
	public static final String QryCheckBoardFinalAdc = "QryCheckBoardFinalAdc";// 查询回检板最终ADC
	public static final String QryLogicBoardFinalAdc = "QryLogicBoardFinalAdc";// 查询逻辑板最终ADC
	public static final String CfgCalBoardMeasure = "CfgCalBoardMeasure";// 配置校准板计量
	public static final String CfgCheckBoardMeasure = "CfgCheckBoardMeasure";// 配置回检板计量
	public static final String CfgLogicBoardMeasure = "CfgLogicBoardMeasure";// 配置逻辑板计量
	public static final String QryCheckBoardAdc = "QryCheckBoardAdc";// 查询回检板ADC
	public static final String QryLogicBoardAdc = "QryLogicBoardAdc";// 查询逻辑板ADC
	public static final String CfgCalBoardCalibrate = "CfgCalBoardCalibrate";// 设置校准板：板号=%d，通道=%d
	public static final String CfgCalBoardCalibrateDebug = "CfgCalBoardCalibrateDebug";// 设置校准板：板号=%d，通道=%d，%s，模式=%s，极性=%s，精度=%d，程控V=%d，程控I=%d
	public static final String CfgLogicBoardCalibrate = "CfgLogicBoardCalibrate";// 设置逻辑板程控：模式=%s，极性=%s
	public static final String CfgLogicBoardCalibrateDebug = "CfgLogicBoardCalibrateDebug";// 设置逻辑板程控：模式=%s，极性=%s，精度=%d，程控V=%d，程控I=%d
	public static final String CfgCheckBoardCalibrate = "CfgCheckBoardCalibrate";// 设置回检板程控：%s，模式=%s，极性=%s
	public static final String On = "On";// 打开
	public static final String Off = "Off";// 关闭
	public static final String Diaphragm = "Diaphragm";// 膜片
	public static final String Delay = "Delay";// 延时
	public static final String GetDeviceInfoError = "GetDeviceInfoError";// 获取设备信息错误
	public static final String NotInCalMode = "NotInCalMode";// 获取设备信息错误
	public static final String MatchBusy = "MatchBusy";// 获取设备信息错误
	public static final String DeviceCommunicationTimeOut = "DeviceCommunicationTimeOut";// 设备通信超时
	public static final String EnterCalUpdateModeError = "EnterCalUpdateModeError";// 校准板%d进入升级模式失败，当前不在非工作模式
	public static final String MainUpdateError = "MainUpdateError";// 主控升级失败，当前不在非工作模式
	public static final String UpdateFileNotExist = "UpdateFileNotExist";// 主控升级失败，文件不存在
	public static final String Reboot = "Reboot";// 主控即将重启，请稍后重新连接
	public static final String LogicNotUse = "LogicNotUse";// 逻辑板%d未启用
	public static final String CalBoardNotUse = "CalBoardNotUse";// 校准板%d未启用
	public static final String CalBoardIsWorking = "CalBoardIsWorking";// 校准板%d正在工作，无法操作
	public static final String MeterDiv0 = "MeterDiv0";// 万用表值错误，导致程控K值无穷大
	public static final String AdcDiv0 = "AdcDiv0";// adc值错误，导致adcK值无穷大
	public static final String Cv2AdcDiv0 = "Cv2AdcDiv0";// 逻辑板备份电压adc值错误，导致adcK值无穷大
	public static final String CheckAdcDiv0 = "CheckAdcDiv0";// 回检板adc值错误，导致回检板adcK值无穷大
	public static final String ReadMeterTimeOut = "ReadMeterTimeOut";// 万用表读取超时
	public static final String CorePowerOff = "CorePowerOff";// 校准主控板约在%ds后断电
	public static final String DriverChnCountIsDifferentFormCalBoard = "DriverChnCountIsDifferentFormCalBoard";// 驱动板通道数量与校准板不一致!
																												// (%d,%d)
	public static final String MatchFailed = "MatchFailed";// 对接失败：%s
	public static final String LogicChnMatchVoltOver = "LogicChnMatchVoltOver";// "逻辑板%d通道静止电压超过阈值：%f mV"
	public static final String AppStart = "AppStart";// "程序启动"
	public static final String LoadingConfigFile = "LoadingConfigFile";// "程序启动"
	public static final String LoadingCalibrateInfo = "LoadingCalibrateInfo";// "程序启动"
	public static final String InitSerialPorts = "InitSerialPorts";// "正在初始化串口"
	public static final String InitCalBoards = "InitCalBoards";// "正在初始化校准板"
	public static final String InitScreens = "InitScreens";// "正在初始化液晶屏"
	public static final String InitFailed = "InitFailed";// "初始化失败：%s"
	public static final String InitNetWork = "InitNetWork";// "正在初始化网络"
	public static final String ConnectingDevice = "ConnectingDevice";// "正在连接设备"
	public static final String ConnectingMeters = "ConnectingMeters";// "正在连接万用表"
	public static final String InitDevice = "InitDevice";// "正在初始化设备"
	public static final String InitDeviceFailed = "InitDeviceFailed";// "设备初始化失败：%s"
	public static final String InitDeviceSuccess = "InitDeviceSuccess";// "设备初始化成功"
	public static final String NoChannelSelected = "NoChannelSelected";// "请选择通道"
	public static final String NoCanCalibrateChannelSelected = "NoCanCalibrateChannelSelected";// "请选择可校准的通道，正在工作的校准板无法新增通道"
	public static final String NoCanMeasureChannelSelected = "NoCanMeasureChannelSelected";// "请选择可计量的通道，正在工作的校准板无法新增通道"
	public static final String NoCanStopChannelSelected = "NoCanStopChannelSelected";// "请选择可停止的通道"
	public static final String PCNotConnected = "PCNotConnected";// 上位机已断线;
	public static final String LogicBoard = "LogicBoard";// 逻辑板;
	public static final String CheckBoard = "CheckBoard";// 回检板;
	public static final String CalBoard = "CalBoard";// 校准板;
	public static final String Device = "Device";// 设备;
	public static final String Meter = "Meter";// 万用表%d;
	public static final String Screen = "Screen";// 液晶屏;
	public static final String AdcIsZero = "AdcIsZero";// ADCadc值为0
	public static final String OrignalAdc = "OrignalAdc";// adc原始值
	public static final String LoadXmlError = "LoadXmlError";// 加载配置文件(%s)错误：%s
	
	// 新建校准箱对话窗
    public static final String BoxInfoDlg_title_new = "BoxInfoDlg_title_new";
    public static final String BoxInfoDlg_title_edit = "BoxInfoDlg_title_edit";
    public static final String BoxInfoDlg_btn_ok_create = "BoxInfoDlg_btn_ok_create";
    public static final String BoxInfoDlg_btn_ok_edit = "BoxInfoDlg_btn_ok_edit";
    public static final String BoxInfoDlg_btn_config = "BoxInfoDlg_btn_config";
    public static final String BoxInfoDlg_resp_title_fail = "BoxInfoDlg_resp_title_fail";
    public static final String BoxInfoDlg_resp_title_suc = "BoxInfoDlg_resp_title_suc";
    public static final String BoxInfoDlg_resp_msg_configCalbox_suc = "BoxInfoDlg_resp_msg_configCalbox_suc";
    public static final String BoxInfoDlg_btn_cancel = "BoxInfoDlg_btn_cancel";
    public static final String BoxInfoDlg_groupBox_calBoard = "BoxInfoDlg_groupBox_calBoard";
    public static final String BoxInfoDlg_label_calBoard_state = "BoxInfoDlg_label_calBoard_state";
    public static final String BoxInfoDlg_groupBox_calBox = "BoxInfoDlg_groupBox_calBox";
    public static final String BoxInfoDlg_label_calBox_name = "BoxInfoDlg_label_calBox_name";
    public static final String BoxInfoDlg_label_calBox_ip = "BoxInfoDlg_label_calBox_ip";
    public static final String BoxInfoDlg_label_calBox_calBrdNum = "BoxInfoDlg_label_calBox_calBrdNum";
    public static final String BoxInfoDlg_label_calBox_remark = "BoxInfoDlg_label_calBox_remark";
    public static final String BoxInfoDlg_label_screen_ip = "BoxInfoDlg_label_screen_ip";
    public static final String BoxInfoDlg_label_meter_number = "BoxInfoDlg_label_meter_number";
    public static final String BoxInfoDlg_label_meter_ip = "BoxInfoDlg_label_meter_ip";
    public static final String BoxInfoDlg_resp_msg_calBoxNameEmpty = "BoxInfoDlg_resp_msg_calBoxNameEmpty";
    public static final String BoxInfoDlg_resp_msg_calBoxIpInllegal = "BoxInfoDlg_resp_msg_calBoxIpInllegal";
    public static final String BoxInfoDlg_resp_msg_meterIpInllegal = "BoxInfoDlg_resp_msg_meterIpInllegal";
    public static final String BoxInfoDlg_resp_msg_screenIpInllegal = "BoxInfoDlg_resp_msg_screenIpInllegal";
    public static final String BoxInfoDlg_resp_msg_editCalBox_fail = "BoxInfoDlg_resp_msg_editCalBox_fail";
    public static final String BoxInfoDlg_resp_msg_editCalBox_suc = "BoxInfoDlg_resp_msg_editCalBox_suc";
    public static final String BoxInfoDlg_resp_msg_switchCalboard_fail = "BoxInfoDlg_resp_msg_switchCalboard_fail";
    public static final String BoxInfoDlg_resp_msg_configCalbox_fail = "BoxInfoDlg_resp_msg_configCalbox_fail";
    // 绑定校准箱对话窗
    public static final String BoxListDlg_title = "BoxListDlg_title";
    public static final String BoxListDlg_label_calboxLst = "BoxListDlg_label_calboxLst";
    public static final String BoxListDlg_btn_ok = "BoxListDlg_btn_ok";
    public static final String BoxListDlg_btn_cancel = "BoxListDlg_btn_cancel";
    public static final String BoxListDlg_resp_title_fail = "BoxInfoDlg_resp_title_fail";
    public static final String BoxListDlg_resp_title_suc = "BoxInfoDlg_resp_title_suc";
    public static final String BoxListDlg_resp_msg_suc = "BoxListDlg_resp_msg_suc";
    public static final String BoxListDlg_resp_msg_fail = "v";
    // 计量参数对话窗
    public static final String CalculateConfigDialog_title = "CalculateConfigDialog_title";
    public static final String CalculateConfigDialog_groupBox = "CalculateConfigDialog_groupBox";
    public static final String CalculateConfigDialog_label_meterOffsetMax = "CalculateConfigDialog_label_meterOffsetMax";
    public static final String CalculateConfigDialog_label_adcOffsetMax = "CalculateConfigDialog_label_adcOffsetMax";
    public static final String CalculateConfigDialog_label_currentMin = "CalculateConfigDialog_label_currentMin";
    public static final String CalculateConfigDialog_label_currentMax = "CalculateConfigDialog_label_currentMax";
    public static final String CalculateConfigDialog_label_voltMin = "CalculateConfigDialog_label_voltMin";
    public static final String CalculateConfigDialog_label_voltMax = "CalculateConfigDialog_label_voltMax";
    public static final String CalculateConfigDialog_btn_save = "CalculateConfigDialog_btn_save";
    // 计量方案对话窗
    public static final String CalculateDotDlg_title = "CalculateDotDlg_title";
    public static final String CalculateDotDlg_table_header_idx = "CalculateDotDlg_table_header_idx";
    public static final String CalculateDotDlg_table_header_step = "CalculateDotDlg_table_header_step";
    public static final String CalculateDotDlg_table_header_pole = "CalculateDotDlg_table_header_pole";
    public static final String CalculateDotDlg_table_header_measureDot = "CalculateDotDlg_table_header_measureDot";
    public static final String CalculateDotDlg_toolBar_new = "CalculateDotDlg_toolBar_new";
    public static final String CalculateDotDlg_toolBar_add = "CalculateDotDlg_toolBar_add";
    public static final String CalculateDotDlg_toolBar_del = "CalculateDotDlg_toolBar_del";
    public static final String CalculateDotDlg_toolBar_up = "CalculateDotDlg_toolBar_up";
    public static final String CalculateDotDlg_toolBar_down = "CalculateDotDlg_toolBar_down";
    public static final String CalculateDotDlg_toolBar_sort = "CalculateDotDlg_toolBar_sort";
    public static final String CalculateDotDlg_toolBar_import = "CalculateDotDlg_toolBar_import";
    public static final String CalculateDotDlg_toolBar_export = "CalculateDotDlg_toolBar_export";
    public static final String CalculateDotDlg_toolBar_config = "CalculateDotDlg_toolBar_config";
    public static final String CalculateDotDlg_toolBar_query = "CalculateDotDlg_toolBar_query";
    public static final String CalculateDotDlg_resp_title_fail = "CalculateDotDlg_resp_title_fail";
    public static final String CalculateDotDlg_resp_title_suc = "CalculateDotDlg_resp_title_suc";
    public static final String CalculateDotDlg_resp_msg_export_fail = "CalculateDotDlg_resp_msg_export_fail";
    public static final String CalculateDotDlg_resp_msg_export_suc = "CalculateDotDlg_resp_msg_export_suc";
    public static final String CalculateDotDlg_resp_msg_config_fail = "CalculateDotDlg_resp_msg_config_fail";
    public static final String CalculateDotDlg_resp_msg_config_suc = "CalculateDotDlg_resp_msg_config_suc";
    public static final String CalculateDotDlg_resp_msg_query_fail_notBinding = "CalculateDotDlg_resp_msg_query_fail_notBinding";
    public static final String CalculateDotDlg_resp_msg_query_fail_disconnect = "CalculateDotDlg_resp_msg_query_fail";
    public static final String CalculateDotDlg_resp_msg_query_fail = "CalculateDotDlg_resp_msg_query_fail";
    public static final String CalculateDotDlg_resp_msg_query_fail_empty = "CalculateDotDlg_resp_msg_query_fail_empty";
    public static final String CalculateDotDlg_resp_msg_query_suc = "CalculateDotDlg_resp_msg_query_suc";
    public static final String CalculateDotDlg_resp_msg_config_fail_stepEmpty = "CalculateDotDlg_resp_msg_config_fail_stepEmpty";
    public static final String CalculateDotDlg_resp_msg_config_fail_poleEmpty = "CalculateDotDlg_resp_msg_config_fail_poleEmpty";
    // 校准参数对话窗
    public static final String CalibrateConfigDialog_title = "CalibrateConfigDialog_title";
    public static final String CalibrateConfigDialog_groupBox_delay = "CalibrateConfigDialog_groupBox_delay";
    public static final String CalibrateConfigDialog_label_relayON = "CalibrateConfigDialog_label_relayON";
    public static final String CalibrateConfigDialog_label_relayOFF = "CalibrateConfigDialog_label_relayOFF";
    public static final String CalibrateConfigDialog_label_switchStep = "CalibrateConfigDialog_label_switchStep";
    public static final String CalibrateConfigDialog_label_configDA = "CalibrateConfigDialog_label_configDA";
    public static final String CalibrateConfigDialog_label_lowResisToHighResis = "CalibrateConfigDialog_label_lowResisToHighResis";
    public static final String CalibrateConfigDialog_label_highResisToLowResis = "CalibrateConfigDialog_label_highResisToLowResis";
    public static final String CalibrateConfigDialog_label_readMeter = "CalibrateConfigDialog_label_readMeter";
    public static final String CalibrateConfigDialog_label_switchMeter = "CalibrateConfigDialog_label_switchMeter";
    public static final String CalibrateConfigDialog_groupBox_other = "CalibrateConfigDialog_groupBox_other";
    public static final String CalibrateConfigDialog_label_verifyKB = "CalibrateConfigDialog_label_verifyKB";
    public static final String CalibrateConfigDialog_comboBox_verifyKB_on = "CalibrateConfigDialog_comboBox_verifyKB_on";
    public static final String CalibrateConfigDialog_comboBox_verifyKB_off = "CalibrateConfigDialog_comboBox_verifyKB_off";
    public static final String CalibrateConfigDialog_label_measureAfterCal = "CalibrateConfigDialog_label_measureAfterCal";
    public static final String CalibrateConfigDialog_comboBox_measureAfterCal_on = "CalibrateConfigDialog_comboBox_measureAfterCal_on";
    public static final String CalibrateConfigDialog_comboBox_measureAfterCal_off = "CalibrateConfigDialog_comboBox_measureAfterCal_off";
    public static final String CalibrateConfigDialog_label_queryAdcNum = "CalibrateConfigDialog_label_queryAdcNum";
    public static final String CalibrateConfigDialog_label_adcOffsetMax = "CalibrateConfigDialog_label_adcOffsetMax";
    public static final String CalibrateConfigDialog_label_avgAdcExcept = "CalibrateConfigDialog_label_avgAdcExcept";
    public static final String CalibrateConfigDialog_label_voltDaMax = "CalibrateConfigDialog_label_voltDaMax";
    public static final String CalibrateConfigDialog_label_currentDaMax = "CalibrateConfigDialog_label_currentDaMax";
    public static final String CalibrateConfigDialog_label_queryAdcRetryMax = "CalibrateConfigDialog_label_queryAdcRetryMax";
    public static final String CalibrateConfigDialog_label_queryAdcRetryDelay = "CalibrateConfigDialog_label_queryAdcRetryDelay";
    public static final String CalibrateConfigDialog_btn_save = "CalibrateConfigDialog_btn_save";
    // 自检信息对话窗
    public static final String CheckListDlg_title = "CheckListDlg_title";
    public static final String CheckListDlg_checkItem_logic_soft = "CheckListDlg_checkItem_logic_soft";
    public static final String CheckListDlg_checkItem_logic_softVersion = "CheckListDlg_checkItem_logic_softVersion";
    public static final String CheckListDlg_checkItem_logic_ad1KB = "CheckListDlg_checkItem_logic_ad1KB";
    public static final String CheckListDlg_checkItem_logic_ad2KB = "CheckListDlg_checkItem_logic_ad2KB";
    public static final String CheckListDlg_checkItem_logic_ad3KB = "CheckListDlg_checkItem_logic_ad3KB";
    public static final String CheckListDlg_checkItem_logic_FLASH = "CheckListDlg_checkItem_logic_FLASH";
    public static final String CheckListDlg_checkItem_logic_SRAM = "CheckListDlg_checkItem_logic_SRAM";
    public static final String CheckListDlg_checkItem_check_soft = "CheckListDlg_checkItem_check_soft";
    public static final String CheckListDlg_checkItem_check_softVersion = "CheckListDlg_checkItem_check_softVersion";
    public static final String CheckListDlg_checkItem_check_ad = "CheckListDlg_checkItem_check_ad";
    public static final String CheckListDlg_checkItem_check_FLASH = "CheckListDlg_checkItem_check_FLASH";
    public static final String CheckListDlg_checkItem_check_kb = "CheckListDlg_checkItem_check_kb";
    public static final String CheckListDlg_checkItem_driver_softFromLogic = "CheckListDlg_checkItem_driver_softFromLogic";
    public static final String CheckListDlg_checkItem_driver_softVersionFromLogic = "CheckListDlg_checkItem_driver_softVersionFromLogic";
    public static final String CheckListDlg_checkItem_driver_softFromCheck = "CheckListDlg_checkItem_driver_softFromCheck";
    public static final String CheckListDlg_checkItem_driver_softVersionFromCheck = "CheckListDlg_checkItem_driver_softVersionFromCheck";
    // 校准数据对话窗
    public static final String DebugInfoDlg_title = "DebugInfoDlg_title";
    // 新建设备对话窗
    public static final String DeviceInfoDlg_title_new = "DeviceInfoDlg_title_new";
    public static final String DeviceInfoDlg_title_edit = "DeviceInfoDlg_title_edit";
    public static final String DeviceInfoDlg_label_deviceName = "DeviceInfoDlg_label_deviceName";
    public static final String DeviceInfoDlg_label_deviceType = "DeviceInfoDlg_label_deviceType";
    public static final String DeviceInfoDlg_label_logicNum = "DeviceInfoDlg_label_logicNum";
    public static final String DeviceInfoDlg_label_driverNum = "DeviceInfoDlg_label_driverNum";
    public static final String DeviceInfoDlg_label_driverChnNum = "DeviceInfoDlg_label_driverChnNum";
    public static final String DeviceInfoDlg_label_remark = "DeviceInfoDlg_label_remark";
    public static final String DeviceInfoDlg_btn_ok_create = "DeviceInfoDlg_btn_ok_create";
    public static final String DeviceInfoDlg_btn_ok_edit = "DeviceInfoDlg_btn_ok_edit";
    public static final String DeviceInfoDlg_btn_cancel = "DeviceInfoDlg_btn_cancel";
    public static final String DeviceInfoDlg_resp_title_fail = "DeviceInfoDlg_resp_title_fail";
    public static final String DeviceInfoDlg_resp_title_suc = "DeviceInfoDlg_resp_title_suc";
    public static final String DeviceInfoDlg_resp_msg_deviceNameEmpty = "DeviceInfoDlg_resp_msg_deviceNameEmpty";
    public static final String DeviceInfoDlg_resp_msg_ipInllegal = "DeviceInfoDlg_resp_msg_ipInllegal";
    public static final String DeviceInfoDlg_resp_msg_fail = "DeviceInfoDlg_resp_msg_fail";
    public static final String DeviceInfoDlg_resp_msg_suc = "DeviceInfoDlg_resp_msg_suc";
    // 计量数据窗口
    public static final String MeasureWindow_title = "MeasureWindow_title";
    public static final String MeasureWindow_table_header_idx = "MeasureWindow_table_header_idx";
    public static final String MeasureWindow_table_header_chnIdx = "MeasureWindow_table_header_chnIdx";
    public static final String MeasureWindow_table_header_step = "MeasureWindow_table_header_step";
    public static final String MeasureWindow_table_header_pole = "MeasureWindow_table_header_pole";
    public static final String MeasureWindow_table_header_measureDot = "MeasureWindow_table_header_measureDot";
    public static final String MeasureWindow_table_header_meter = "MeasureWindow_table_header_meter";
    public static final String MeasureWindow_table_header_adcOffset = "MeasureWindow_table_header_adcOffset";
    public static final String MeasureWindow_table_header_meterOffset = "MeasureWindow_table_header_meterOffset";
    public static final String MeasureWindow_table_header_result = "MeasureWindow_table_header_result";
    public static final String MeasureWindow_table_header_log = "MeasureWindow_table_header_log";
    // 电流档位参数窗口
    public static final String PrecisonConfigDialog_title = "PrecisonConfigDialog_title";
    public static final String PrecisonConfigDialog_label_precisionLevel = "PrecisonConfigDialog_label_precisionLevel";
    public static final String PrecisonConfigDialog_label_precisionUnit = "PrecisonConfigDialog_label_precisionUnit";
    public static final String PrecisonConfigDialog_label_rangeMin = "PrecisonConfigDialog_label_rangeMin";
    public static final String PrecisonConfigDialog_label_rangeMax = "PrecisonConfigDialog_label_rangeMax";
    public static final String PrecisonConfigDialog_label_adc_da_offsetMax = "PrecisonConfigDialog_label_adc_da_offsetMax";
    public static final String PrecisonConfigDialog_label_meter_da_offsetMax = "PrecisonConfigDialog_label_meter_da_offsetMax";
    public static final String PrecisonConfigDialog_btn_config = "PrecisonConfigDialog_btn_config";
    public static final String PrecisonConfigDialog_btn_query = "PrecisonConfigDialog_btn_query";
    public static final String PrecisonConfigDialog_tooltip = "PrecisonConfigDialog_tooltip";
    public static final String PrecisonConfigDialog_menuitem_add = "PrecisonConfigDialog_menuitem_add";
    public static final String PrecisonConfigDialog_menuitem_del = "PrecisonConfigDialog_menuitem_del";
    // 搜索设备窗口
    public static final String WaitKnowDialog_title = "WaitKnowDialog_title";
    // WorkBench
    public static final String WorkBench_DeviceType_PowerBox = "WorkBench_DeviceType_PowerBox";
    public static final String WorkBench_DeviceType_CAPACITY = "WorkBench_DeviceType_CAPACITY";
    public static final String WorkBench_configCommand_err_disconnect = "WorkBench_configCommand_err_disconnect";
    public static final String WorkBench_configCommand_err_timeout = "WorkBench_configCommand_err_timeout";
    // Handler
    public static final String Handler_measurePlan = "Handler_measurePlan";
    public static final String Handler_calibratePlan = "Handler_calibratePlan";
    public static final String Handler_debug = "Handler_debug";
    public static final String Handler_searchDevice_dlg_searching = "Handler_searchDevice_dlg_searching";
    public static final String Handler_searchDevice_resp_title = "Handler_searchDevice_resp_title";
    public static final String Handler_searchDevice_resp_notResult = "Handler_searchDevice_resp_notResult";
    public static final String Handler_searchDevice_dlg_msg = "Handler_searchDevice_dlg_msg";
    public static final String CalculateConfigPart_toolBar_new = "CalculateConfigPart_toolBar_new";
    public static final String CalculateConfigPart_toolBar_export = "CalculateConfigPart_toolBar_export";
    public static final String CalculateConfigPart_toolBar_import = "CalculateConfigPart_toolBar_import";
    public static final String CalculateConfigPart_toolBar_config = "CalculateConfigPart_toolBar_config";
    public static final String CalculateConfigPart_toolBar_query = "CalculateConfigPart_toolBar_query";
    public static final String CalculateConfigPart_toolBar_add = "CalculateConfigPart_toolBar_add";
    public static final String CalculateConfigPart_toolBar_insert = "CalculateConfigPart_toolBar_insert";
    public static final String CalculateConfigPart_toolBar_del = "CalculateConfigPart_toolBar_del";
    public static final String CalculateConfigPart_toolBar_up = "CalculateConfigPart_toolBar_up";
    public static final String CalculateConfigPart_toolBar_down = "CalculateConfigPart_toolBar_down";
    public static final String CalculateConfigPart_toolBar_sort = "CalculateConfigPart_toolBar_sort";
    public static final String CalculateConfigPart_toolBar_setting = "CalculateConfigPart_toolBar_setting";
    public static final String CalculateConfigPart_table_header_idx = "CalculateConfigPart_table_header_idx";
    public static final String CalculateConfigPart_table_header_step = "CalculateConfigPart_table_header_step";
    public static final String CalculateConfigPart_table_header_pole = "CalculateConfigPart_table_header_pole";
    public static final String CalculateConfigPart_table_header_measureDot = "CalculateConfigPart_table_header_measureDot";
    public static final String CalculateConfigPart_menuItem_insert = "CalculateConfigPart_menuItem_insert";
    public static final String CalculateConfigPart_menuItem_del = "CalculateConfigPart_menuItem_del";
    public static final String CalculateConfigPart_menuItem_add = "CalculateConfigPart_menuItem_add";
    public static final String CalculateConfigPart_menuItem_up = "CalculateConfigPart_menuItem_up";
    public static final String CalculateConfigPart_menuItem_down = "CalculateConfigPart_menuItem_down";
    public static final String CalculateConfigPart_resp_title_suc = "CalculateConfigPart_resp_title_suc";
    public static final String CalculateConfigPart_resp_title_fail = "CalculateConfigPart_resp_title_fail";
    public static final String CalculateConfigPart_resp_msg_export_suc = "CalculateConfigPart_resp_msg_export_suc";
    public static final String CalculateConfigPart_resp_msg_export_fail = "CalculateConfigPart_resp_msg_export_fail";
    public static final String CalculateConfigPart_resp_msg_import_suc = "CalculateConfigPart_resp_msg_import_suc";
    public static final String CalculateConfigPart_resp_msg_import_fail = "CalculateConfigPart_resp_msg_import_fail";
    public static final String CalculateConfigPart_resp_msg_config_suc = "CalculateConfigPart_resp_msg_config_suc";
    public static final String CalculateConfigPart_resp_msg_config_fail = "CalculateConfigPart_resp_msg_config_fail";
    public static final String CalculateConfigPart_resp_msg_query_fail = "CalculateConfigPart_resp_msg_query_fail";
    public static final String CalculateConfigPart_resp_msg_query_suc = "CalculateConfigPart_resp_msg_query_suc";
    // 校准方案页面
    public static final String ClibrateConfigPart_toolBar_new = "ClibrateConfigPart_toolBar_new";
    public static final String ClibrateConfigPart_toolBar_export = "ClibrateConfigPart_toolBar_export";
    public static final String ClibrateConfigPart_toolBar_import = "ClibrateConfigPart_toolBar_import";
    public static final String ClibrateConfigPart_toolBar_config = "ClibrateConfigPart_toolBar_config";
    public static final String ClibrateConfigPart_toolBar_query = "ClibrateConfigPart_toolBar_query";
    public static final String ClibrateConfigPart_toolBar_empty = "ClibrateConfigPart_toolBar_empty";
    public static final String ClibrateConfigPart_toolBar_setting = "ClibrateConfigPart_toolBar_setting";
    public static final String ClibrateConfigPart_resp_title_suc = "ClibrateConfigPart_resp_title_suc";
    public static final String ClibrateConfigPart_resp_title_fail = "ClibrateConfigPart_resp_title_fail";
    public static final String ClibrateConfigPart_resp_msg_export_suc = "ClibrateConfigPart_resp_msg_export_suc";
    public static final String ClibrateConfigPart_resp_msg_export_fail = "ClibrateConfigPart_resp_msg_export_fail";
    public static final String ClibrateConfigPart_resp_msg_import_suc = "ClibrateConfigPart_resp_msg_import_suc";
    public static final String ClibrateConfigPart_resp_msg_import_fail = "ClibrateConfigPart_resp_msg_import_fail";
    public static final String ClibrateConfigPart_resp_msg_query_fail_calibratePlan = "ClibrateConfigPart_resp_msg_query_fail_calibratePlan";
    public static final String ClibrateConfigPart_resp_msg_query_fail_delay = "ClibrateConfigPart_resp_msg_query_fail_delay";
    public static final String ClibrateConfigPart_resp_msg_query_fail_avgADC = "ClibrateConfigPart_resp_msg_query_fail_avgADC";
    public static final String ClibrateConfigPart_resp_msg_query_suc = "ClibrateConfigPart_resp_msg_query_suc";
    public static final String ClibrateConfigPart_resp_msg_config_fail_calibratePlan = "ClibrateConfigPart_resp_msg_config_fail_calibratePlan";
    public static final String ClibrateConfigPart_resp_msg_config_fail_delay = "ClibrateConfigPart_resp_msg_config_fail_delay";
    public static final String ClibrateConfigPart_resp_msg_config_fail_avgADC = "ClibrateConfigPart_resp_msg_config_fail_avgADC";
    public static final String ClibrateConfigPart_resp_msg_config_suc = "ClibrateConfigPart_resp_msg_config_suc";
    public static final String ClibrateConfigPart_groupBox_stepSetting = "ClibrateConfigPart_groupBox_stepSetting";
    public static final String ClibrateConfigPart_label_step = "ClibrateConfigPart_label_step";
    public static final String ClibrateConfigPart_label_pole = "ClibrateConfigPart_label_pole";
    public static final String ClibrateConfigPart_comboBox_pole_negative = "ClibrateConfigPart_comboBox_pole_negative";
    public static final String ClibrateConfigPart_comboBox_pole_positive = "ClibrateConfigPart_comboBox_pole_positive";
    public static final String ClibrateConfigPart_label_precision = "ClibrateConfigPart_label_precision";
    public static final String ClibrateConfigPart_groupBox_kbSetting = "ClibrateConfigPart_groupBox_kbSetting";
    public static final String ClibrateConfigPart_groupBox_dotSetting = "ClibrateConfigPart_groupBox_dotSetting";
    // 电池图示区默认页面
    public static final String ClibrateConsolePart_title = "ClibrateConsolePart_title";
    public static final String ClibrateConsolePart_driverBoardIdx_notMatched = "ClibrateConsolePart_driverBoardIdx_notMatched";
    public static final String ClibrateConsolePart_defaultContent = "ClibrateConsolePart_defaultContent";
    // 设备树
    public static final String DeviceListPart_title = "DeviceListPart_title";
    public static final String DeviceListPart_treeNode_device = "DeviceListPart_treeNode_device";
    public static final String DeviceListPart_treeNode_calBox = "DeviceListPart_treeNode_calBox";
    public static final String DeviceListPart_treeNode_partition = "DeviceListPart_treeNode_partition";
    public static final String DeviceListPart_menuItem_addDevice = "DeviceListPart_menuItem_addDevice";
    public static final String DeviceListPart_menuItem_delDevice = "DeviceListPart_menuItem_delDevice";
    public static final String DeviceListPart_resp_title_confirm = "DeviceListPart_resp_title_confirm";
    public static final String DeviceListPart_resp_msg_delDevice_confirm = "DeviceListPart_resp_msg_delDevice_confirm";
    public static final String DeviceListPart_resp_title_fail = "DeviceListPart_resp_title_fail";
    public static final String DeviceListPart_resp_msg_delDevice_fail_haveData = "DeviceListPart_resp_msg_delDevice_fail_haveData";
    public static final String DeviceListPart_resp_title_suc = "DeviceListPart_resp_title_suc";
    public static final String DeviceListPart_resp_msg_delDevice_suc = "DeviceListPart_resp_msg_delDevice_suc";
    public static final String DeviceListPart_resp_msg_delDevice_fail = "DeviceListPart_resp_msg_delDevice_fail";
    public static final String DeviceListPart_menuItem_addCalBox = "DeviceListPart_menuItem_addCalBox";
    public static final String DeviceListPart_menuItem_delCalBox = "DeviceListPart_menuItem_delCalBox";
    public static final String DeviceListPart_resp_msg_delCalBox_fail_notUnbind = "DeviceListPart_resp_msg_delCalBox_fail_notUnbind";
    public static final String DeviceListPart_resp_msg_delCalBox_confirm = "DeviceListPart_resp_msg_delCalBox_confirm";
    public static final String DeviceListPart_resp_msg_delCalBox_fail = "DeviceListPart_resp_msg_delCalBox_fail";
    public static final String DeviceListPart_resp_msg_delCalBox_suc = "DeviceListPart_resp_msg_delCalBox_suc";
    public static final String DeviceListPart_menuItem_binding = "DeviceListPart_menuItem_binding";
    public static final String DeviceListPart_menuItem_unbind = "DeviceListPart_menuItem_unbind";
    public static final String DeviceListPart_resp_msg_unbind_fail = "DeviceListPart_resp_msg_unbind_fail";
    public static final String DeviceListPart_menuItem_debug = "DeviceListPart_menuItem_debug";
    public static final String DeviceListPart_debugPage_title = "DeviceListPart_debugPage_title";
    // 电池图示页面
    public static final String LogicConsolePart_toolBar_connect = "LogicConsolePart_toolBar_connect";
    public static final String LogicConsolePart_toolBar_disconnect = "LogicConsolePart_toolBar_disconnect";
    public static final String LogicConsolePart_resp_title_suc = "LogicConsolePart_resp_title_suc";
    public static final String LogicConsolePart_resp_title_fail = "LogicConsolePart_resp_title_fail";
    public static final String LogicConsolePart_resp_title_confirm = "LogicConsolePart_resp_title_confirm";
    public static final String LogicConsolePart_resp_msg_connect_fail = "LogicConsolePart_resp_msg_connect_fail";
    public static final String LogicConsolePart_toolBar_calState = "LogicConsolePart_toolBar_calState";
    public static final String LogicConsolePart_toolBar_outCalState = "LogicConsolePart_toolBar_outCalState";
    public static final String LogicConsolePart_toolBar_calState_toolTip = "LogicConsolePart_toolBar_calState_toolTip";
    public static final String LogicConsolePart_resp_msg_calState_fail = "LogicConsolePart_resp_msg_calState_fail";
    public static final String LogicConsolePart_toolBar_autoMatching = "LogicConsolePart_toolBar_autoMatching";
    public static final String LogicConsolePart_toolBar_autoMatching_toolTip = "LogicConsolePart_toolBar_autoMatching_toolTip";
    public static final String LogicConsolePart_resp_msg_autoMatching_confirm = "LogicConsolePart_resp_msg_autoMatching_confirm";
    public static final String LogicConsolePart_toolBar_startCal = "LogicConsolePart_toolBar_startCal";
    public static final String LogicConsolePart_resp_msg_startCal_fail = "LogicConsolePart_resp_msg_startCal_fail";
    public static final String LogicConsolePart_toolBar_stMeasure = "LogicConsolePart_toolBar_stMeasure";
    public static final String LogicConsolePart_resp_msg_stMeasure_fail = "LogicConsolePart_resp_msg_stMeasure_fail";
    public static final String LogicConsolePart_toolBar_stop = "LogicConsolePart_toolBar_stop";
    public static final String LogicConsolePart_resp_msg_stop_fail = "LogicConsolePart_resp_msg_stop_fail";
    public static final String LogicConsolePart_toolBar_measurePlan = "LogicConsolePart_toolBar_measurePlan";
    public static final String LogicConsolePart_toolBar_export = "LogicConsolePart_toolBar_export";
    public static final String LogicConsolePart_exportDlg_title = "LogicConsolePart_exportDlg_title";
    public static final String LogicConsolePart_exportDlg_label_fileType = "LogicConsolePart_exportDlg_label_fileType";
    public static final String LogicConsolePart_exportDlg_btn_singleCSV = "LogicConsolePart_exportDlg_btn_singleCSV";
    public static final String LogicConsolePart_exportDlg_btn_multiCSV = "LogicConsolePart_exportDlg_btn_multiCSV";
    public static final String LogicConsolePart_exportDlg_btn_singleXLSX = "LogicConsolePart_exportDlg_btn_singleXLSX";
    public static final String LogicConsolePart_toolBar_selfCheck = "LogicConsolePart_toolBar_selfCheck";
    public static final String LogicConsolePart_toolBar_selfCheck_toolTip = "LogicConsolePart_toolBar_selfCheck_toolTip";
    public static final String LogicConsolePart_resp_msg_selfCheck_fail_notBinding = "LogicConsolePart_resp_msg_selfCheck_fail_notBinding";
    public static final String LogicConsolePart_resp_msg_selfCheck_fail_disconnect = "LogicConsolePart_resp_msg_selfCheck_fail_disconnect";
    public static final String LogicConsolePart_processBar_selfCheck = "LogicConsolePart_processBar_selfCheck";
    public static final String LogicConsolePart_resp_msg_selfCheck_fail = "LogicConsolePart_resp_msg_selfCheck_fail";
    public static final String LogicConsolePart_resp_msg_autoMatching_suc = "LogicConsolePart_resp_msg_autoMatching_suc";
    public static final String LogicConsolePart_resp_title_err = "LogicConsolePart_resp_title_err";
    public static final String LogicConsolePart_report_singleFile_fileName = "LogicConsolePart_report_singleFile_fileName";
    public static final String LogicConsolePart_processBar_export = "LogicConsolePart_processBar_export";
    public static final String LogicConsolePart_resp_msg_export_suc = "LogicConsolePart_resp_msg_export_suc";
    public static final String LogicConsolePart_resp_msg_export_fail = "LogicConsolePart_resp_msg_export_fail";
    public static final String LogicConsolePart_report_singleFile_header_chnIdx = "LogicConsolePart_report_singleFile_header_chnIdx";
    public static final String LogicConsolePart_report_singleFile_header_step = "LogicConsolePart_report_singleFile_header_step";
    public static final String LogicConsolePart_report_singleFile_header_pole = "LogicConsolePart_report_singleFile_header_pole";
    public static final String LogicConsolePart_report_singleFile_header_measureDot = "LogicConsolePart_report_singleFile_header_measureDot";
    public static final String LogicConsolePart_report_singleFile_header_meter = "LogicConsolePart_report_singleFile_header_meter";
    public static final String LogicConsolePart_report_singleFile_header_adc = "LogicConsolePart_report_singleFile_header_adc";
    public static final String LogicConsolePart_report_singleFile_header_meterOffset = "LogicConsolePart_report_singleFile_header_meterOffset";
    public static final String LogicConsolePart_report_singleFile_header_adcOffset = "LogicConsolePart_report_singleFile_header_adcOffset";
    public static final String LogicConsolePart_report_singleFile_header_result = "LogicConsolePart_report_singleFile_header_result";
    public static final String LogicConsolePart_report_singleFile_header_log = "LogicConsolePart_report_singleFile_header_log";
    public static final String LogicConsolePart_report_multiFile_header_chnIdx = "LogicConsolePart_report_multiFile_header_chnIdx";
    public static final String LogicConsolePart_report_multiFile_header_measureDot = "LogicConsolePart_report_multiFile_header_measureDot";
    public static final String LogicConsolePart_report_multiFile_header_meter = "LogicConsolePart_report_multiFile_header_meter";
    public static final String LogicConsolePart_report_multiFile_header_adc = "LogicConsolePart_report_multiFile_header_adc";
    public static final String LogicConsolePart_report_multiFile_header_meterOffset = "LogicConsolePart_report_multiFile_header_meterOffset";
    public static final String LogicConsolePart_report_multiFile_header_adcOffset = "LogicConsolePart_report_multiFile_header_adcOffset";
    public static final String LogicConsolePart_report_multiFile_header_meterPrecision = "LogicConsolePart_report_multiFile_header_meterPrecision";
    public static final String LogicConsolePart_report_multiFile_header_adcPrecision = "LogicConsolePart_report_multiFile_header_adcPrecision";
    // 调试页面
    public static final String MultCalConsolePart_groupBox_device = "MultCalConsolePart_groupBox_device";
    public static final String MultCalConsolePart_label_notice = "MultCalConsolePart_label_notice";
    public static final String MultCalConsolePart_groupBox_calBox = "MultCalConsolePart_groupBox_calBox";
    public static final String MultCalConsolePart_label_calBoard = "MultCalConsolePart_label_calBoard";
    public static final String MultCalConsolePart_label_logicIdx = "MultCalConsolePart_label_logicIdx";
    public static final String MultCalConsolePart_label_chnIdx = "MultCalConsolePart_label_chnIdx";
    public static final String MultCalConsolePart_label_calBoardChnIdx = "MultCalConsolePart_label_calBoardChnIdx";
    public static final String MultCalConsolePart_label_step = "MultCalConsolePart_label_step";
    public static final String MultCalConsolePart_label_pole = "MultCalConsolePart_label_pole";
    public static final String MultCalConsolePart_comboBox_pole_negative = "MultCalConsolePart_comboBox_pole_negative";
    public static final String MultCalConsolePart_comboBox_pole_positive = "MultCalConsolePart_comboBox_pole_positive";
    public static final String MultCalConsolePart_label_precision = "MultCalConsolePart_label_precision";
    public static final String MultCalConsolePart_label_volt = "MultCalConsolePart_label_volt";
    public static final String MultCalConsolePart_label_curr = "MultCalConsolePart_label_curr";
    public static final String MultCalConsolePart_label_measureDot = "MultCalConsolePart_label_measureDot";
    public static final String MultCalConsolePart_btn_logic_cal_config = "MultCalConsolePart_btn_logic_cal_config";
    public static final String MultCalConsolePart_btn_logic_cal_query = "MultCalConsolePart_btn_logic_cal_query";
    public static final String MultCalConsolePart_btn_logic_measure_config = "MultCalConsolePart_btn_logic_measure_config";
    public static final String MultCalConsolePart_btn_logic_measure_query = "MultCalConsolePart_btn_logic_measure_query";
    public static final String MultCalConsolePart_btn_check_cal_config = "MultCalConsolePart_btn_check_cal_config";
    public static final String MultCalConsolePart_btn_check_cal_query = "MultCalConsolePart_btn_check_cal_query";
    public static final String MultCalConsolePart_btn_check_measure_config = "MultCalConsolePart_btn_check_measure_config";
    public static final String MultCalConsolePart_btn_check_measure_query = "MultCalConsolePart_btn_check_measure_query";
    public static final String MultCalConsolePart_btn_calBoard_cal_config = "MultCalConsolePart_btn_calBoard_cal_config";
    public static final String MultCalConsolePart_btn_calBoard_measure_config = "MultCalConsolePart_btn_calBoard_measure_config";
    public static final String MultCalConsolePart_log_logic_cal_config = "MultCalConsolePart_log_logic_cal_config";
    public static final String MultCalConsolePart_resp_title_suc = "MultCalConsolePart_resp_title_suc";
    public static final String MultCalConsolePart_resp_title_fail = "MultCalConsolePart_resp_title_fail";
    public static final String MultCalConsolePart_resp_msg_logic_cal_config_suc = "MultCalConsolePart_resp_msg_logic_cal_config_suc";
    public static final String MultCalConsolePart_resp_msg_logic_cal_config_fail = "MultCalConsolePart_resp_msg_logic_cal_config_fail";
    public static final String MultCalConsolePart_log_logic_measure_config = "MultCalConsolePart_log_logic_measure_config";
    public static final String MultCalConsolePart_resp_msg_logic_measure_config_suc = "MultCalConsolePart_resp_msg_logic_measure_config_suc";
    public static final String MultCalConsolePart_resp_msg_logic_measure_config_fail = "MultCalConsolePart_resp_msg_logic_measure_config_fail";
    public static final String MultCalConsolePart_log_check_cal_config = "MultCalConsolePart_log_check_cal_config";
    public static final String MultCalConsolePart_resp_msg_check_cal_config_suc = "MultCalConsolePart_resp_msg_check_cal_config_suc";
    public static final String MultCalConsolePart_resp_msg_check_cal_config_fail = "MultCalConsolePart_resp_msg_check_cal_config_fail";
    public static final String MultCalConsolePart_log_check_measure_config = "MultCalConsolePart_log_check_measure_config";
    public static final String MultCalConsolePart_resp_msg_check_measure_config_suc = "MultCalConsolePart_resp_msg_check_measure_config_suc";
    public static final String MultCalConsolePart_resp_msg_check_measure_config_fail = "MultCalConsolePart_resp_msg_check_measure_config_fail";
    public static final String MultCalConsolePart_log_calBoard_cal_config = "MultCalConsolePart_log_calBoard_cal_config";
    public static final String MultCalConsolePart_resp_msg_calBoard_cal_config_suc = "MultCalConsolePart_resp_msg_calBoard_cal_config_suc";
    public static final String MultCalConsolePart_resp_msg_calBoard_cal_config_fail = "MultCalConsolePart_resp_msg_calBoard_cal_config_fail";
    public static final String MultCalConsolePart_log_calBoard_measure_config = "MultCalConsolePart_log_calBoard_measure_config";
    public static final String MultCalConsolePart_resp_msg_calBoard_measure_config_suc = "MultCalConsolePart_resp_msg_calBoard_measure_config_suc";
    public static final String MultCalConsolePart_resp_msg_calBoard_measure_config_fail = "MultCalConsolePart_resp_msg_calBoard_measure_config_fail";
    public static final String MultCalConsolePart_btn_module_open = "MultCalConsolePart_btn_module_open";
    public static final String MultCalConsolePart_resp_msg_module_open_suc = "MultCalConsolePart_resp_msg_module_open_suc";
    public static final String MultCalConsolePart_resp_msg_module_open_fail = "MultCalConsolePart_resp_msg_module_open_fail";
    public static final String MultCalConsolePart_btn_meter_open = "MultCalConsolePart_btn_meter_open";
    public static final String MultCalConsolePart_resp_msg_meter_open_suc = "MultCalConsolePart_resp_msg_meter_open_suc";
    public static final String MultCalConsolePart_resp_msg_meter_open_fail = "MultCalConsolePart_resp_msg_meter_open_fail";
    public static final String MultCalConsolePart_resp_msg_logic_cal_query_suc = "MultCalConsolePart_resp_msg_logic_cal_query_suc";
    public static final String MultCalConsolePart_resp_msg_logic_cal_query_fail = "MultCalConsolePart_resp_msg_logic_cal_query_fail";
    public static final String MultCalConsolePart_resp_msg_logic_measure_query_suc = "MultCalConsolePart_resp_msg_logic_measure_query_suc";
    public static final String MultCalConsolePart_resp_msg_logic_measure_query_fail = "MultCalConsolePart_resp_msg_logic_measure_query_fail";
    public static final String MultCalConsolePart_resp_msg_check_cal_query_suc = "MultCalConsolePart_resp_msg_check_cal_query_suc";
    public static final String MultCalConsolePart_resp_msg_check_cal_query_fail = "MultCalConsolePart_resp_msg_check_cal_query_fail";
    public static final String MultCalConsolePart_resp_msg_check_measure_query_suc = "MultCalConsolePart_resp_msg_check_measure_query_suc";
    public static final String MultCalConsolePart_resp_msg_check_measure_query_fail = "MultCalConsolePart_resp_msg_check_measure_query_fail";
    public static final String MultCalConsolePart_btn_module_close = "MultCalConsolePart_btn_module_close";
    public static final String MultCalConsolePart_resp_msg_module_close_suc = "MultCalConsolePart_resp_msg_module_close_suc";
    public static final String MultCalConsolePart_resp_msg_module_close_fail = "MultCalConsolePart_resp_msg_module_close_fail";
    public static final String MultCalConsolePart_btn_meter_close = "MultCalConsolePart_btn_meter_close";
    public static final String MultCalConsolePart_resp_msg_meter_close_suc = "MultCalConsolePart_resp_msg_meter_close_suc";
    public static final String MultCalConsolePart_resp_msg_meter_close_fail = "MultCalConsolePart_resp_msg_meter_close_fail";
    public static final String MultCalConsolePart_btn_meter_read = "MultCalConsolePart_btn_meter_read";
    public static final String MultCalConsolePart_resp_msg_meter_read_suc = "MultCalConsolePart_resp_msg_meter_read_suc";
    // xlsx计量报表
    public static final String MeasureReport_tipLine1_color = "MeasureReport_tipLine1_color";
    public static final String MeasureReport_tipLine1_result = "MeasureReport_tipLine1_result";
    public static final String MeasureReport_tipLine1_condition = "MeasureReport_tipLine1_condition";
    public static final String MeasureReport_tipLine1_operator = "MeasureReport_tipLine1_operator";
    public static final String MeasureReport_tipLine1_verifier = "MeasureReport_tipLine1_verifier";
    public static final String MeasureReport_tipLine2_condition = "MeasureReport_tipLine2_condition";
    public static final String MeasureReport_tipLine2_condition_offsetMin = "MeasureReport_tipLine2_condition_offsetMin";
    public static final String MeasureReport_tipLine2_condition_offsetMax = "MeasureReport_tipLine2_condition_offsetMax";
    public static final String MeasureReport_tipLine3_condition = "MeasureReport_tipLine3_condition";
    public static final String MeasureReport_tipLine3_condition_offsetMin = "MeasureReport_tipLine3_condition_offsetMin";
    public static final String MeasureReport_tipLine3_condition_offsetMax = "MeasureReport_tipLine3_condition_offsetMax";
    public static final String MeasureReport_header_chnIdx = "MeasureReport_header_chnIdx";
    public static final String MeasureReport_header_measureDot = "MeasureReport_header_measureDot";
    public static final String MeasureReport_header_meter = "MeasureReport_header_meter";
    public static final String MeasureReport_header_adc = "MeasureReport_header_adc";
    public static final String MeasureReport_header_meterOffset = "MeasureReport_header_meterOffset";
    public static final String MeasureReport_header_adcOffset = "MeasureReport_header_adcOffset";
    public static final String MeasureReport_header_result = "MeasureReport_header_result";
    // CalboxService
    public static final String CalboxService_recvCalBoxResp = "CalboxService_recvCalBoxResp";
    public static final String CalboxService_configCalBox = "CalboxService_configCalBox";
    public static final String CalboxService_configCalBox_fail_timeout = "CalboxService_configCalBox_fail_timeout";
    public static final String CalboxService_configCalBox_fail = "CalboxService_configCalBox_fail";
    public static final String CalboxService_configMeasurePlan_fail_emptyPlan = "CalboxService_configMeasurePlan_fail_emptyPlan";
    public static final String CalboxService_checkSetting_fail_logicNotEqual = "CalboxService_checkSetting_fail_logicNotEqual";
    public static final String CalboxService_checkSetting_fail_driverNotEqual = "CalboxService_checkSetting_fail_driverNotEqual";
    public static final String CalboxService_checkSetting_fail_chnNotEqual = "CalboxService_checkSetting_fail_chnNotEqual";
    // 设备自检窗口_表控件
    public static final String CheckListTableViewer_header_item = "CheckListTableViewer_header_item";
    public static final String CheckListTableViewer_header_result = "CheckListTableViewer_header_result";
    // 校准数据窗口_表控件
    public static final String DebugTableViewer_column_type_cal = "DebugTableViewer_column_type_cal";
    public static final String DebugTableViewer_column_type_measure = "DebugTableViewer_column_type_measure";
    public static final String DebugTableViewer_header_chnIdx = "DebugTableViewer_header_chnIdx";
    public static final String DebugTableViewer_header_type = "DebugTableViewer_header_type";
    public static final String DebugTableViewer_header_step = "DebugTableViewer_header_step";
    public static final String DebugTableViewer_header_pole = "DebugTableViewer_header_pole";
    public static final String DebugTableViewer_header_testDot = "DebugTableViewer_header_testDot";
    public static final String DebugTableViewer_header_adc = "DebugTableViewer_header_adc";
    public static final String DebugTableViewer_header_meter = "DebugTableViewer_header_meter";
    public static final String DebugTableViewer_header_adcOffset = "DebugTableViewer_header_adcOffset";
    public static final String DebugTableViewer_header_meterOffset = "DebugTableViewer_header_meterOffset";
    public static final String DebugTableViewer_header_precision = "DebugTableViewer_header_precision";
    public static final String DebugTableViewer_header_checkADC = "DebugTableViewer_header_checkADC";
    public static final String DebugTableViewer_header_daK = "DebugTableViewer_header_daK";
    public static final String DebugTableViewer_header_daB = "DebugTableViewer_header_daB";
    public static final String DebugTableViewer_header_adcK = "DebugTableViewer_header_adcK";
    public static final String DebugTableViewer_header_adcB = "DebugTableViewer_header_adcB";
    public static final String DebugTableViewer_header_checkK = "DebugTableViewer_header_checkK";
    public static final String DebugTableViewer_header_checkB = "DebugTableViewer_header_checkB";
    public static final String DebugTableViewer_header_result = "DebugTableViewer_header_result";
    public static final String DebugTableViewer_header_log = "DebugTableViewer_header_log";
    // CommonUtil
    public static final String CommonUtil_execThread_cancel = "CommonUtil_execThread_cancel";
    public static final String CommonUtil_execThread_err = "CommonUtil_execThread_err";
    // E4LifeCycle
    public static final String E4LifeCycle_resp_title_confirm = "E4LifeCycle_resp_title_confirm";
    public static final String E4LifeCycle_resp_msg_exit = "E4LifeCycle_resp_msg_exit";
    public static final String E4LifeCycle_shell_title = "E4LifeCycle_shell_title";
    // 电池图示区
    public static final String LogicBatteryComposite_resp_title_fail = "LogicBatteryComposite_resp_title_fail";
    public static final String LogicBatteryComposite_resp_msg_fail = "LogicBatteryComposite_resp_msg_fail";
    
	private static I18N i18n;

	private Map<String, String> map = new HashMap<String, String>();

	public static void init(InputStream path) throws Exception {

		i18n = new I18N();
		SAXReader reader = new SAXReader();
		Document document = reader.read(path);
		Element languageRoot = document.getRootElement();
		List<Node> itemNodes = languageRoot.selectNodes("item");
		for (Node node : itemNodes) {
			if (node instanceof Element) {
				Element ele = (Element) node;
				String key = ele.attributeValue("key");
				String value = ele.getText();
				if (i18n.map.containsKey(key)) {
					System.err.println("xml repeat : " + key);
				}
				i18n.map.put(key, value);
			}
		}
		// i18n.map.entrySet().stream().forEach(x->{
		// System.out.println(x.getKey()+" = "+x.getValue());
		// });
	}

	public static String getVal(String key, Object... obj) {

		if (i18n == null) {
			return key;
		}

		if (i18n.map.containsKey(key)) {
			try {
				return String.format(i18n.map.get(key), obj);
			} catch (Exception e) {
				return i18n.map.get(key);
			}
		}
		System.err.println("xml not exist : " + key);
		return key;
	}

	public static String getVal(String key) {

		if (i18n == null) {
			return key;
		}

		if (i18n.map.containsKey(key)) {

			return i18n.map.get(key);
		}
		System.err.println("xml not exist : " + key);
		return key;
	}
	
	 public static void init(String path, String langFileEncoding) throws Exception {

			i18n = new I18N();
//			InputStream stream = I18N.class.getClassLoader().getResourceAsStream(path);
//			Charset charset = Charset.forName(langFileEncoding);
//			Document document = reader.read(new InputStreamReader(stream, charset));
			SAXReader reader = new SAXReader();
			reader.setEncoding(langFileEncoding);
			File file = new File(path);
			Document document = reader.read(file);
			Element languageRoot = document.getRootElement();
			List<Element> elementLst = languageRoot.elements("item");
			for (Element elem : elementLst) {
			    String key = elem.attributeValue("key");
			    String value = elem.getText();
			    if (i18n.map.containsKey(key)) {
				System.err.println("xml repeat : " + key);
			    }
			    i18n.map.put(key, value);
			}
			// i18n.map.entrySet().stream().forEach(x->{
			// System.out.println(x.getKey()+" = "+x.getValue());
			// });
		}

	public static void main(String[] args) {
		try {
			InputStream iStream = I18N.class.getClassLoader().getResourceAsStream("language/zh-CN.xml");
			init(iStream);

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
					// System.out.println( getVal(field.get(new I18N()).toString()));
				}
			}
			System.out.println("check over!");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
