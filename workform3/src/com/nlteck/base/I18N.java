package com.nlteck.base;

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

import com.nlteck.fireware.CalibrateCore;
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
