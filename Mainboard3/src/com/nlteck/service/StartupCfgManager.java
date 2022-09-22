package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.XmlUtil;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AlarmType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.FanType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.ProbeType;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.TempBoardType;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;

/**
 * 启动配置管理器
 * 
 * @author Administrator
 *
 */
public class StartupCfgManager {

	private Document document;
	private String path;
	private boolean useVirtualData; // 使用虚拟数据?
	private boolean useAlert; // 启动常规报警?
	private boolean useReverseChnIndex; // 使用反向通道序号
	private boolean useCalboardReverseChnIndex; // 校准板是否通道反序,默认使用useReverseChnIndex
	private boolean useDebug; // 调试模式，不再采集逻辑板数据和反向推送
	private boolean useStepChangeProtect = true; // 是否使用转步保护,默认启用
	private boolean useSTM32Time; // 是否使用STM32的相对时间作为采集时间,主控将在此基础上累计时间;使用STM32芯片时间则更为准确
    
	private WorkEnvironment   workEnv = WorkEnvironment.WHOLE; //主控工作环境
	private OperationSystem    system =  OperationSystem.Linux; //默认操作系统为LINUX
	private boolean           useErrorPick = false; //模拟AD采样错误
	
	private ProcedureMode procedureMode = ProcedureMode.DEVICE; // 配置流程模式,默认为设备

	private double maxDeviceVoltage = 5000; // 设置允许最大设置电压,单位mV
	private double maxDeviceCurrent = 13000; // 设置允许最大设置电流,单位mA
	private double minDischargeVoltage = 2000; // 设置允许最小放电电压，单位mV
	private double minDeviceCurrent = 20; // 最小允许设置电流
	private double minDeviceVoltage = 24; // 最小允许设置电压
	private double minDefineVoltage = -500; // 最小允许界定电压
	private double maxDefineVoltage = 500; // 最大允许界定电压
	private double minRunningCurrent = 0; // 最小运行电流,单位mA
	private int maxContinueAlertCount = 3; // 默认通道在切换到无状态后的连续报警次数，超过该次数主控将在流程结束后自动锁定报警通道
	private int maxPushOffCount = 5; // 默认最大逻辑板推送次数和回馈推送消息次数差，此时超过该次数差主控将暂停相应的逻辑板采集

	private int driverChnCount = 8; // 驱动板通道数量
	private int logicDriverCount = 8; // 分区驱动板数量
	private int Cc2CvDescendCount = 2; // cc-cv时判断时电流下降的最小次数
	private double maxCurrPrecideOffset = 10; // 特殊处理电流精度的最大偏差范围
	private double maxVoltPrecideOffset = 5; // 特殊处理电压精度的最大偏差范围
	private int maxStartupTimeout = 7000; // 最大步次启动等待时间
	private int maxContinueStartupTimeout = 3000; // 连续步次启动或转休眠最大等待时间
	private double currentPrecide = 3; // 普通电流精度,mA
	private double voltagePreCide = 2; // 电压精度,mV
	private boolean disableDefaultProtection; // 是否禁用默认保护
	private double Cc2CvVoltageOffset = 5; // 恒压偏差范围内就进行cc-cv转序
	private double highCurrentPrecide = 0.1; // 高精度电流精度,mA
	private double precisionThreshold = 200; // 高低精度划分阀值，mA
	private double driverTemperatureAttention = 75; // 电阻提醒温度
	private double driverTemperatureAlert = 90; // 电阻报警温度

	private ProductType productType = ProductType.POWERBOX; // 默认产品为电源柜
	private Customer customer = Customer.ATL; // 默认为ATL客户
	private String language = "zh_CN";// 默认语言为中午

	private List<LogicInfo> logicInfos = new ArrayList<LogicInfo>();
	// private List<CheckInfo> checkInfos = new ArrayList<CheckInfo>();

	private List<DriverInfo> driverInfos = new ArrayList<>();

	private PowerManagerInfo powerManagerInfo = new PowerManagerInfo();
	private FanManagerInfo fanManagerInfo = new FanManagerInfo();
	private OMRTemperatureManagerInfo OMRManagerInfo = new OMRTemperatureManagerInfo();
	private MechanismManagerInfo mechanismManagerInfo = new MechanismManagerInfo();
	private ProbeManagerInfo probeManagerInfo = new ProbeManagerInfo();
	private AlarmManagerInfo alarmManagerInfo = new AlarmManagerInfo();
	private SmogAlertManagerInfo smogAlertManagerInfo = new SmogAlertManagerInfo();
	private DoorAlertManagerInfo doorAlertManagerInfo = new DoorAlertManagerInfo();
	private BeepAlertManagerInfo beepAlertManagerInfo = new BeepAlertManagerInfo();
	private ColorLightManagerInfo colorLightManagerInfo = new ColorLightManagerInfo();
	private StateLightControllerInfo stateLightControllerInfo = new StateLightControllerInfo();
	private BackupBoard backupBoard = new BackupBoard();
	private LogCfg logCfg = new LogCfg();
	private ScreenInfo screenInfo = new ScreenInfo();
	private ControlInfo controlInfo = new ControlInfo();
	private ProcedureSupportInfo procedureSupportInfo = new ProcedureSupportInfo();
	private SmartProtects smartProtects = new SmartProtects();
	private PingController  pingController = new PingController();
	private ChannelLightController  channelLightController = new ChannelLightController();
	private DriversCfg      driversCfg     = new DriversCfg();

	private Protocol protocol = new Protocol();
	private KBDecimal kbDecimal = new KBDecimal();
	private DoublePrecision doublePrecision = new DoublePrecision();
	private Range range = new Range();
	private Precision precision = new Precision();
	private Map<String, PortInfo> ports = new HashMap<String, PortInfo>();
	private String ip = "192.168.1.127"; // ip地址

	/**
	 * 可串口通信板部件
	 * 
	 * @author Administrator
	 *
	 */
	public static interface Communicatable {

		/**
		 * 获取通信口名称
		 * 
		 * @return
		 */
		String getPortName();

		/**
		 * 是否可用
		 */
		boolean isUseable();

		/**
		 * 序号
		 */
		int getIndex();

		/**
		 * 获取通信超时
		 */
		int getCommunicateTimeout();

	}

	/**
	 * 产品类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum ProductType {

		/**
		 * 电源柜， 恒温箱 ， 针床
		 */
		POWERBOX, CAPACITY, SOFTPING, COLUMNPING;
	}

	public enum Customer {

		ATL, CATL, LISTEN, ETC, HK
	}
	/**
	 * 主控工作环境
	 * @author wavy_zheng
	 * 2022年7月14日
	 *
	 */
	public enum WorkEnvironment {
		
		WHOLE /*将流程所有工步下发给下位机，由下位机转工步;目前软件默认的工作环境*/, 
		SINGLE /*由主控接管流程，实现单步次工步下发;推荐使用此种方式工作，可能带来转步次比较慢的副作用*/ 
	}
	

	/**
	 * 
	 * 电源管理器类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum PowerManagerType {

		GROUP;/* 分组电源类型 */
	}

	/**
	 * 电源产品
	 * 
	 * @author wavy_zheng 2022年2月21日
	 *
	 */
	public enum PowerProduct {

		GD/* 国电赛思和老方的电源 */ , TBM/* 图伟 */
	}

	/**
	 * 备份板配置
	 * 
	 * @author wavy_zheng 2021年5月28日
	 *
	 */
	public static class BackupBoard {

		public boolean enable;
		public int time; // 修改时间
		public double resister; // 接触电阻值
		public double offset; // 波动偏差值
		public double voltThreshold; // 阀值;备份电压偏差多少开始处理
		public double resisterThreshold; // 接触电阻阀值
	}
	
	public static class ChannelLightController {
		
		public boolean enable;
		public String  portName;
		public int     communicateTimeout;
		
		
	}
	

	/**
	 * 日志打印配置
	 * 
	 * @author wavy_zheng 2021年8月8日
	 *
	 */
	public static class LogCfg {

		public boolean printChnLog = true; // 打印通道日志
		public boolean printSysLog = true; // 打印系统日志
		public boolean printProtocolLog; // 打印协议日志,不建议开启
		public int     printChnPickLog = -1; //详细打印指定通道的运行采集日志
	}

	/**
	 * 电源管理器配置情况
	 * 
	 * @author Administrator
	 *
	 */
	public static class PowerManagerInfo {

		public boolean use;
		public boolean monitor;
		public PowerManagerType type;
		public PowerProduct product = PowerProduct.GD; // 默认国电电源
		public List<PowerInfo> powerInfos = new ArrayList<PowerInfo>();
		public List<Integer> powerGroups = new ArrayList<Integer>();

	}

	/**
	 * 智能保护
	 * 
	 * @author wavy_zheng 2022年5月4日
	 *
	 */
	public static class SmartProtects {

		public boolean enalbe; // 启用?
		public int interval; // 采集间隔.单位s
		public TouchResister touch = new TouchResister();

	}
	
	/**
	 * 针床控制
	 * @author wavy_zheng
	 * 2022年6月20日
	 *
	 */
	public static class PingController {
		
		public boolean enable;
		public String  portName;
		public int     communicateTimeout;
		
		
		
	}
	

	/**
	 * 智能接触电阻保护
	 * 
	 * @author wavy_zheng 2022年5月4日
	 *
	 */
	public static class TouchResister {

		public boolean enable;
		public int sampleMinCount = 8;
		public int trailCount;
		public double minCurrent;
		public double protectRatio = 2.0;

	}

	/**
	 * 三色灯配置管理器
	 * 
	 * @author wavy_zheng 2020年11月27日
	 *
	 */
	public static class ColorLightManagerInfo {

		public boolean use;
		public List<ColorLightInfo> colorLights = new ArrayList<>();

	}

	public static class ColorLightInfo implements Communicatable {

		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public int index;

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

	}

	/**
	 * 电源配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class PowerInfo implements Communicatable {

		public PowerType powerType;
		public PowerProduct product;
		public int powerCount = 7;
		public Boolean monitor; // 是否监控电源状态
		public String portName;
		public Boolean use;
		public int communicateTimeout;

		public PowerInfo() {

		}

		public PowerInfo(PowerType powerType, Boolean use, Boolean monitor) {

			this.powerType = powerType;
			this.use = use;
			this.monitor = monitor;

		}

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof PowerInfo) {

				PowerInfo pi = (PowerInfo) obj;
				if (this.powerType != null) {

					if (pi.powerType == this.powerType) {

						if (this.use == null) {

							return true;
						}
						if (pi.use == this.use) {

							if (this.monitor == null) {

								return true;
							}
							return this.monitor == pi.monitor;
						}
					}

				} else {

					return true;
				}

			}
			return false;
		}
	}

	/**
	 * 风机管理器配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class FanManagerInfo {

		public boolean use;
		public boolean monitor;
		public List<FanInfo> fanInfos = new ArrayList<FanInfo>();

	}
	
	/**
	 * 主控运行的操作系统
	 * @author wavy_zheng
	 * 2022年8月1日
	 *
	 */
	public enum  OperationSystem {
		
		Linux , Windows;
		
	}
	
	/**
	 * 驱动板类型
	 * @author wavy_zheng
	 * 2022年8月1日
	 *
	 */
	public enum DriverboardType {
		
		PRODUCT/*生产*/,
		LAB/*实验室*/
		
	}
	

	/**
	 * 风机配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class FanInfo implements Communicatable {

		public FanType fanType;
		public int fanCount = 24;
		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public Boolean monitor; // 是否监控风机状态
		public int index;

		public FanInfo() {

		}

		public FanInfo(FanType fanType, Boolean use, Boolean monitor) {

			this.fanType = fanType;
			this.use = use;
			this.monitor = monitor;
		}

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof FanInfo) {

				FanInfo fi = (FanInfo) obj;
				if (this.fanType != null) {

					if (fi.fanType == this.fanType) { // 先判断风机类型

						if (this.use == null) {

							return true;
						}
						if (this.use == fi.use) { // 判断风机启用情况

							if (this.monitor == null) {

								return true;
							}
							return this.monitor == fi.monitor; // 最后判断监控情况
						}
					}

				} else {

					return true;
				}

			}
			return false;
		}
	}

	public static class MechanismManagerInfo {

		public boolean use;
		public boolean monitor;
		public List<CylinderInfo> cylinderInfos = new ArrayList<CylinderInfo>();
		public List<TempProbeInfo> tempProbeInfos = new ArrayList<TempProbeInfo>();

	}

	public static class StateLightControllerInfo {

		public boolean use;
		public List<StateLightInfo> stateLights = new ArrayList<>();

	}

	public static class StateLightInfo implements Communicatable {

		public boolean use;
		public String portName;
		public int index;
		public int communicateTimeout;

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

	}

	/**
	 * 多流程支持
	 * 
	 * @author wavy_zheng 2021年8月23日
	 *
	 */
	public static class ProcedureSupportInfo {

		public boolean supportLogic; // 支持多逻辑板分区
		public boolean supportDriver; // 支持多驱动板分区
		public boolean supportImportantData = true; // 支持采集关键点数据
		public boolean supportLeadData; // 处理转步前几条数据
	}

	public static class ProbeInfo {

		public ProbeType type;
		public Boolean use;
		public Boolean monitor;
		public int count;
		public String portName;
		public int communicateTimeout;

		public boolean controlTemp;// 温控

		public ProbeInfo() {

		}

		public ProbeInfo(ProbeType type, Boolean use) {

			this.type = type;
			this.use = use;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof ProbeInfo) {

				ProbeInfo tpi = (ProbeInfo) obj;
				if (this.use == null) {

					return true;
				}
				if (this.use == tpi.use) {

					if (this.monitor == null) {

						return true;
					}
					return tpi.monitor == this.monitor;
				}

			}
			return false;
		}

	}

	/**
	 * 检测探头管理器
	 * 
	 * @author Administrator
	 *
	 */
	public static class ProbeManagerInfo {

		public boolean use;
		public boolean monitor;
		public List<ProbeInfo> probeInfos = new ArrayList<ProbeInfo>();

	}

	/**
	 * 气缸参数
	 * 
	 * @author Administrator
	 *
	 */
	public static class CylinderInfo implements Communicatable {

		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public Boolean monitor; // 是否监控托盘状态
		public int count;

		public CylinderInfo() {

		}

		public CylinderInfo(Boolean use, Boolean monitor) {

			this.use = use;
			this.monitor = monitor;
		}

		@Override
		public String getPortName() {

			return portName;
		}

		@Override
		public boolean isUseable() {

			return use;
		}

		@Override
		public int getIndex() {

			return 0;
		}

		@Override
		public int getCommunicateTimeout() {

			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof CylinderInfo) {

				CylinderInfo tpi = (CylinderInfo) obj;
				if (this.use == null) {

					return true;
				}
				if (this.use == tpi.use) {

					if (this.monitor == null) {

						return true;
					}
					return tpi.monitor == this.monitor;
				}

			}
			return false;
		}

		@Override
		public String toString() {
			return "CylinderInfo [portName=" + portName + ", use=" + use + ", communicateTimeout=" + communicateTimeout
					+ ", monitor=" + monitor + ", count=" + count + "]";
		}

	}

	/**
	 * 温度探头
	 * 
	 * @author Administrator
	 *
	 */
	public static class TempProbeInfo implements Communicatable {

		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public Boolean monitor; // 是否监控托盘状态
		public int count;

		public TempProbeInfo() {

		}

		public TempProbeInfo(Boolean use, Boolean monitor) {

			this.use = use;
			this.monitor = monitor;
		}

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {

			return use;
		}

		@Override
		public int getIndex() {

			return 0;
		}

		@Override
		public int getCommunicateTimeout() {

			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof TempProbeInfo) {

				TempProbeInfo tpi = (TempProbeInfo) obj;
				if (this.use == null) {

					return true;
				}
				if (this.use == tpi.use) {

					if (this.monitor == null) {

						return true;
					}
					return tpi.monitor == this.monitor;
				}

			}
			return false;
		}

		@Override
		public String toString() {
			return "TempProbeInfo [portName=" + portName + ", use=" + use + ", communicateTimeout=" + communicateTimeout
					+ ", monitor=" + monitor + ", count=" + count + "]";
		}

	}

	/**
	 * 驱动板配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class DriverInfo {

		public int index; // 实际下发的真实驱动板地址
		public boolean use; // 启用
		public String portName; // 串口名
		public long communication; // 通信超时
		public long pickupTime; // 采集时间
		public boolean reverseChnIndex; // 是否反向排序通道序号
		public DriverboardType  driverboardType = DriverboardType.PRODUCT; //默认生产类型
		public String  ip ; //当driverboardType为LAB时，IP地址生效，portName无效

	}

	/**
	 * KB小数位
	 * 
	 * @author wavy_zheng 2021年1月14日
	 *
	 */
	public static class KBDecimal {

		public int programK = 7; // 小数位
		public int programB = 7;
		public int adcK = 7;
		public int adcB = 7;

	}

	/**
	 * 通信协议配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class Protocol {

		public int currentResolution = 1; // 小数位
		public int voltageResolution = 1;
		public int capacityResolution = 1;
		public int energyResolution = 1;

		public boolean useLogicPowerVoltage; // 单板采集是否采集功率电压
		public boolean useStandaloneProcedure; // 是否使用独立流程模式?
		public boolean useTempProbe; // 是否使用温度探头数据
		public int maxFanSupportCount = 32; // 最大风机支持个数，8的整数倍
		public boolean useAnotherFanStateQuery; // 采用分组风机查询协议
		public boolean useChnTempPick; // 是否采集单通道温度

		public boolean useFrameTempPick; // 是否采集料框温度

		public boolean useAirPressure; // 使用气压增量协议 ， 增量和停留时间
		public boolean useStepRecord; // 使用步次记录协议

		public boolean useProcedureWorkType;// 开启使用流程类型,便于选择保护参数

		public int moduleCount = 2;// 膜片数量
	}

	/**
	 * 精度控制
	 * 
	 * @author wavy_zheng 2020年3月20日
	 *
	 */
	public static class Precision {

		public boolean useDebug = false; // 调试模式.此时不对精度做任何处理
		public double voltageOffsetRange = 10; // 电压精度处理
		public double currentOffsetRange = 10; // 电流精度处理
		public int continueCCPrecisionCount = 3; // 连续cc最大精度偏差转序阀值
	}

	/**
	 * 双精度配置
	 * 
	 * @author Administrator
	 *
	 */
	public static class DoublePrecision {

		public boolean use;
		public double threshold = 200;
		public double precision = 0.1;
		public boolean useLogic; // 逻辑板启用双精度
		public boolean useCal; // 校准板启用双精度

	}

	/**
	 * 量程档位
	 * 
	 * @author wavy_zheng 2020年8月7日
	 *
	 */
	public static class RangeSection {

		public int level; // 0表示精度最低的档位
		public double lower;
		public double upper;
		public double precision; // 精度
		public boolean filter; // 滤波？
		public double currentFilterRange; // 电流滤波范围,取两者较大值作为阀值
        public double currentFilterPercent; //电流滤波百分比，取两者较大值作为阀值
	}

	/**
	 * 量程
	 * 
	 * @author wavy_zheng 2020年8月7日
	 *
	 */
	public static class Range {

		public boolean use; // true时DoublePrecision无效
		public double disableCurrentLine; // 屏蔽电流界限值
		public double disableVoltageLine; // 屏蔽电压界限值
		public double voltagePrecision; // 电压处理精度
		public double voltageFilterRange; // 电压波动处理范围
		public double voltageStartOffset; // 启动时回检，逻辑电压差阀值
		public boolean filterVoltage; // 是否开启电压过滤
		public boolean filterCurrent; // 是否开启电流过滤
		public boolean continueAlertFilter = true; // 开启连续两次保护开关,默认开，提高优率
		public double microVolt = 2500; // 小电压电流过滤的电压最大阀值
		public double microCurr = 50; // 小电压电流过滤的电流最大阀值
		public boolean useCvCurrentFilter; // 是否启用cv电流过滤处理
		public boolean useDcVoltageFilter; // 是否启用DC电压过滤处理
		public boolean useCcVoltageFilter; // 是否启用CC电压过滤处理
		public boolean useSlpVoltFilter; // 是否启用休眠电压过滤处理
		public boolean adjustCcCvVoltage; //在CCCV模式转模式时，延缓处理升压，使电压不会出现过充

		public List<RangeSection> sections = new ArrayList<>();
	}

	/**
	 * 液晶屏配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class ScreenInfo implements Communicatable {

		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public String password = "123456";

		@Override
		public String getPortName() {

			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return -1;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

	}

	/**
	 * 控制板配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class ControlInfo implements Communicatable {

		public String portName;
		public boolean use;
		public int communicateTimeout;
		public boolean heartbeat; // 心跳?
		public boolean resetPower; // 复位辅助电源
		public String  ip;     //当设置了IP，默认就是网络控制板

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			return -1;
		}

		@Override
		public int getCommunicateTimeout() {

			return communicateTimeout;
		}

	}

	/**
	 * 流程方式
	 * 
	 * @author Administrator
	 *
	 */
	public static class ProcedureInfo {

		public ProcedureMode mode = ProcedureMode.DEVICE;
	}

	/**
	 * 报警或指示管理器配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class AlarmManagerInfo {

		public boolean use;
		public boolean monitor;
		public List<AlarmInfo> alarmInfos = new ArrayList<AlarmInfo>();

	}

	public static class AlarmInfo implements Communicatable {

		public int index;
		public String portName;
		public boolean use;
		public int communicateTimeout;
		public AlarmType alarmType = AlarmType.AUDIO_LIGHT; // 默认为声光报警器

		public AlarmInfo() {

		}

		public AlarmInfo(AlarmType alarmType, boolean use) {

			this.alarmType = alarmType;
			this.use = use;
		}

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof AlarmInfo) {

				AlarmInfo ai = (AlarmInfo) obj;
				return (this.alarmType == null || ai.alarmType == this.alarmType) && ai.use == this.use;
			}
			return false;
		}

	}

	/**
	 * 蜂鸣报警管理器信息
	 * 
	 * @author wavy_zheng 2020年11月27日
	 *
	 */
	public static class BeepAlertManagerInfo {

		public boolean use = false;
		public List<BeepAlertInfo> beepInfos = new ArrayList<BeepAlertInfo>();
	}

	/**
	 * 门禁报警器管理信息
	 */
	public static class DoorAlertManagerInfo {

		public boolean use = false;
		public boolean monitor = false;
		public List<DoorInfo> doorInfos = new ArrayList<DoorInfo>();
	}
	
	/**
	 * 驱动板配置信息
	 * @author wavy_zheng
	 * 2022年7月7日
	 *
	 */
	public static class DriversCfg {
		
		//是否启用驱动板温度采集
		public boolean useTempPick;
		
		//生产驱动板
		public DriverboardType  driverboardType = DriverboardType.PRODUCT;
		
	}
	

	/**
	 * 单个门禁信息
	 * 
	 * @author wavy_zheng 2020年3月6日
	 *
	 */
	public static class DoorInfo implements Communicatable {

		public int index;
		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public Boolean monitor;

		public DoorInfo(Boolean monitor, Boolean use) {

			this.monitor = monitor;
			this.use = use;
		}

		public DoorInfo() {

		}

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof DoorInfo) {

				DoorInfo di = (DoorInfo) obj;

				if (this.use == null) {

					return true;
				}
				if (this.use == di.use) {

					if (this.monitor == null) {

						return true;
					}
					return this.monitor == di.monitor;

				}

			}
			return false;
		}

	}

	/**
	 * 烟雾报警管理器信息
	 * 
	 * @author wavy_zheng 2020年3月5日
	 *
	 */
	public static class SmogAlertManagerInfo {

		public boolean use = false;
		public boolean monitor = false;
		public List<SmogInfo> smogInfos = new ArrayList<SmogInfo>();
	}

	public static class BeepAlertInfo implements Communicatable {

		public int index;
		public String portName;
		public Boolean use;
		public int communicateTimeout;

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {
			// TODO Auto-generated method stub
			return communicateTimeout;
		}

	}

	/**
	 * 单个烟雾报警器信息
	 * 
	 * @author wavy_zheng 2020年3月5日
	 *
	 */
	public static class SmogInfo implements Communicatable {

		public int index;
		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public Boolean monitor;

		public SmogInfo() {

		}

		public SmogInfo(Boolean monitor, Boolean use) {

			this.monitor = monitor;
			this.use = use;
		}

		@Override
		public String getPortName() {

			return portName;
		}

		@Override
		public boolean isUseable() {

			return use;
		}

		@Override
		public int getIndex() {

			return index;
		}

		@Override
		public int getCommunicateTimeout() {

			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof SmogInfo) {

				SmogInfo si = (SmogInfo) obj;

				if (this.use == null) {

					return true;
				}
				if (this.use == si.use) {

					if (this.monitor == null) {

						return true;
					}
					return this.monitor == si.monitor;

				}

			}
			return false;
		}

		@Override
		public String toString() {
			return "SmogInfo [index=" + index + ", portName=" + portName + ", use=" + use + ", communicateTimeout="
					+ communicateTimeout + ", monitor=" + monitor + "]";
		}

	}

	/**
	 * OMR温控表管理器
	 * 
	 * @author Administrator
	 *
	 */
	public static class OMRTemperatureManagerInfo {

		public boolean use;
		public boolean monitor;
		public List<TempBoardInfo> meterInfos = new ArrayList<TempBoardInfo>();
	}

	public static class TempBoardInfo implements Communicatable {

		public String portName;
		public Boolean use;
		public int communicateTimeout;
		public TempBoardType tempBoardType = TempBoardType.OMR;
		public int index;
		public Boolean monitor;

		public TempBoardInfo() {

		}

		public TempBoardInfo(TempBoardType type, Boolean use, Boolean monitor) {

			this.tempBoardType = type;
			this.use = use;
			this.monitor = monitor;
		}

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {

			return communicateTimeout;
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof TempBoardInfo) {

				TempBoardInfo tbi = (TempBoardInfo) obj;
				if (this.tempBoardType == null) {

					return true;
				}
				if (tbi.tempBoardType == this.tempBoardType) {

					if (this.use == null) {

						return true;
					}
					if (this.use == tbi.use) {

						if (this.monitor == null) {

							return true;
						}
						return this.monitor == tbi.monitor;

					}

				}
			}
			return false;
		}

	}

	/**
	 * 逻辑板配置信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class LogicInfo implements Communicatable {

		public int index;
		public String portName;
		public boolean use; // 启用
		public List<DriverInfo> drivers = new ArrayList<DriverInfo>();
		public boolean reverseDriverIndex; // 反向排序驱动板序号
		public int pickupTime = 100; // 采样时间间隔
		public int communicateTimeout = 500; // 通信超时
		public int enableFlag = 0xffff; // bit1表示启用，否则表示禁用；默认0xffff;

		@Override
		public String getPortName() {
			// TODO Auto-generated method stub
			return portName;
		}

		@Override
		public boolean isUseable() {
			// TODO Auto-generated method stub
			return use;
		}

		@Override
		public int getIndex() {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public int getCommunicateTimeout() {

			return communicateTimeout;
		}

	}

	public static class PortInfo {

		public String name;
		public int baudrate;
		public boolean use = true;
	}

	/*
	 * // 回检板配置信息 public static class CheckInfo implements Communicatable {
	 * 
	 * public int index; public String portName; public boolean use; // 启用 public
	 * List<DriverInfo> drivers = new ArrayList<DriverInfo>(); public boolean
	 * reverseDriverIndex; // 反向排序驱动板序号 public int pickupTime = 200; // 采样时间间隔
	 * public int communicateTimeout = 500; // 通信超时 public int enableFlag;
	 * 
	 * @Override public String getPortName() { // TODO Auto-generated method stub
	 * return portName; }
	 * 
	 * @Override public boolean isUseable() { // TODO Auto-generated method stub
	 * return use; }
	 * 
	 * @Override public int getIndex() { // TODO Auto-generated method stub return
	 * index; }
	 * 
	 * @Override public int getCommunicateTimeout() {
	 * 
	 * return communicateTimeout; }
	 * 
	 * }
	 */

	public StartupCfgManager(String path) throws AlertException {

		this.path = path;
		loadXml(path);
		System.out.println("logic count = " + logicInfos.size());
	}

	/**
	 * 修改主控配置
	 * 
	 * @author wavy_zheng 2022年6月2日
	 * @throws AlertException
	 * @throws IOException
	 */
	public void changeDocument() throws AlertException, IOException {

		Element root = document.getRootElement();
		Element base = root.element("base");
		Attribute attr = base.attribute("productType");
		if (attr != null) {
			attr.setValue(productType.name());
		}
		attr = base.attribute("driverChnCount");
		if (attr != null) {
			attr.setValue(driverChnCount + "");
		}
		// 是否禁用默认保护
		attr = base.attribute("disableDefaultProtection");
		if (attr != null) {
			attr.setValue(disableDefaultProtection ? "true" : "false");
		}
		// 中英文切换
		attr = base.attribute("language");
		if (attr != null) {
			attr.setValue(language);
		} else {

			base.addAttribute("language", language);
		}
		//主控工作环境
		attr = base.attribute("workEnvironment");
		if (attr != null) {
			
			attr.setValue(workEnv.name());
		} else {
			
			base.addAttribute("workEnv", workEnv.name());
		}

		// 开关
		Element switcher = root.element("switcher");
		attr = switcher.attribute("useVirtualData");
		if (attr != null) {
			attr.setValue(useVirtualData + "");
		}
		attr = switcher.attribute("useAlert");
		if (attr != null) {
			attr.setValue(useAlert + "");
		}
		attr = switcher.attribute("useReverseChnIndex");
		if (attr != null) {
			attr.setValue(useReverseChnIndex + "");
		}
		attr = switcher.attribute("useDebug");
		if (attr != null) {
			attr.setValue(useDebug + "");
		}

		// 达到转步条件未发生转步保护
		attr = switcher.attribute("useStepChangeProtect");
		if (attr != null) {
			attr.setValue(useStepChangeProtect ? "true" : "false");
		} else {

			base.addAttribute("useStepChangeProtect", useStepChangeProtect + "");
		}

		attr = switcher.attribute("useSTM32Time");
		if (attr != null) {
			attr.setValue(useSTM32Time + "");
		} else {

			base.addAttribute("useSTM32Time", useSTM32Time + "");
		}
		
	

		/**
		 * 日志记录条件
		 */

		Element logCfgElement = root.element("logCfg");
		if (logCfgElement != null) {

			attr = logCfgElement.attribute("printChnLog");
			if (attr != null) {
				attr.setValue(this.logCfg.printChnLog + "");
			}

			attr = logCfgElement.attribute("printSysLog");
			if (attr != null) {
				attr.setValue(this.logCfg.printSysLog + "");
			}

			attr = logCfgElement.attribute("printProtocolLog");
			if (attr != null) {
				attr.setValue(this.logCfg.printProtocolLog + "");
			}

		}
		/**
		 * 量程精度管控
		 */
		Element rangeElement = root.element("range");
		if (rangeElement != null) {
			root.remove(rangeElement);
		}
		rangeElement = root.addElement("range");
		rangeElement.addAttribute("use", range.use + "");
		rangeElement.addAttribute("disableCurrentLine", range.disableCurrentLine + "");
		rangeElement.addAttribute("disableVoltageLine", range.disableVoltageLine + "");
		rangeElement.addAttribute("voltageFilterRange", range.voltageFilterRange + "");
		rangeElement.addAttribute("voltagePrecision", range.voltagePrecision + "");
		rangeElement.addAttribute("voltageStartOffset", range.voltageStartOffset + "");
		rangeElement.addAttribute("filterVoltage", range.filterVoltage + "");
		rangeElement.addAttribute("filterCurrent", range.filterCurrent + "");
		rangeElement.addAttribute("continueAlertFilter", range.continueAlertFilter + "");
		rangeElement.addAttribute("microVolt", range.microVolt + "");
		rangeElement.addAttribute("microCurr", range.microCurr + "");
		rangeElement.addAttribute("useCvCurrentFilter", range.useCvCurrentFilter + "");

		for (RangeSection rs : this.range.sections) {

			Element sectionElement = rangeElement.addElement("section");

			sectionElement.addAttribute("level", rs.level + "");
			sectionElement.addAttribute("lower", rs.lower + "");
			sectionElement.addAttribute("upper", rs.upper + "");
			sectionElement.addAttribute("precision", rs.precision + "");
			sectionElement.addAttribute("filter", rs.filter + "");
			sectionElement.addAttribute("currentFilterRange", rs.currentFilterRange + "");

		}

		// 通信协议
		Element protocolElement = root.element("protocol");
		if (protocolElement != null) {

			attr = protocolElement.attribute("useChnTempPick");
			if (attr != null) {
				attr.setValue(this.protocol.useChnTempPick + "");
			} else {

				protocolElement.addAttribute("useChnTempPick", this.protocol.useChnTempPick + "");
			}

			attr = protocolElement.attribute("useFrameTempPick");
			if (attr != null) {
				attr.setValue(this.protocol.useFrameTempPick + "");
			} else {

				protocolElement.addAttribute("useFrameTempPick", this.protocol.useFrameTempPick + "");
			}

			attr = protocolElement.attribute("useAirPressure");
			if (attr != null) {
				attr.setValue(this.protocol.useAirPressure + "");
			} else {

				protocolElement.addAttribute("useAirPressure", this.protocol.useAirPressure + "");
			}

			attr = protocolElement.attribute("useStepRecord");
			if (attr != null) {
				attr.setValue(this.protocol.useStepRecord + "");
			} else {

				protocolElement.addAttribute("useStepRecord", this.protocol.useStepRecord + "");
			}

			attr = protocolElement.attribute("useProcedureWorkType");
			if (attr != null) {
				attr.setValue(this.protocol.useProcedureWorkType + "");
			} else {

				protocolElement.addAttribute("useProcedureWorkType", this.protocol.useProcedureWorkType + "");
			}

			attr = protocolElement.attribute("moduleCount");
			if (attr != null) {
				attr.setValue(this.protocol.moduleCount + "");
			} else {

				protocolElement.addAttribute("moduleCount", this.protocol.moduleCount + "");
			}

		}
		// 限制
		Element limit = root.element("limit");
		if (limit != null) {

			attr = limit.attribute("maxDeviceVoltage");
			if (attr != null) {
				attr.setValue(this.maxDeviceVoltage + "");
			}

			attr = limit.attribute("maxDeviceCurrent");
			if (attr != null) {
				attr.setValue(this.maxDeviceCurrent + "");
			}

			attr = limit.attribute("minDeviceVoltage");
			if (attr != null) {
				attr.setValue(this.minDeviceVoltage + "");
			}

			attr = limit.attribute("minDeviceCurrent");
			if (attr != null) {
				attr.setValue(this.minDeviceCurrent + "");
			}
			
			
		}

		// 加载驱动板信息
		Element driversElement = root.element("drivers");
		root.remove(driversElement);
		driversElement = root.addElement("drivers");
		for (int n = 0; n < this.driverInfos.size(); n++) {

			DriverInfo driverInfo = this.driverInfos.get(n);

			Element driverElement = driversElement.addElement("driver");
			driverElement.addAttribute("index", driverInfo.index + "");
			driverElement.addAttribute("portName", driverInfo.portName + "");
			driverElement.addAttribute("use", driverInfo.use + "");
			driverElement.addAttribute("communicateTimeout", driverInfo.communication + "");
			driverElement.addAttribute("pickupTime", driverInfo.pickupTime + "");
			

		}

		// 加载控制板
		Element control = root.element("control");
		if (control != null) {

			attr = control.attribute("portName");
			if (attr != null) {
				attr.setValue(controlInfo.portName);
			}

			attr = control.attribute("use");
			if (attr != null) {
				attr.setValue(controlInfo.use + "");
			}

			attr = control.attribute("heartbeat");
			if (attr != null) {
				attr.setValue(controlInfo.heartbeat + "");
			} else {

				control.addAttribute("heartbeat", controlInfo.heartbeat + "");
			}

			attr = control.attribute("communicateTimeout");
			if (attr != null) {
				attr.setValue(controlInfo.communicateTimeout + "");
			}

		}

		// 加载温控板(表)
		Element OMRManagerEle = root.element("OMRTemperatureManager");
		if (OMRManagerEle != null) {

			attr = OMRManagerEle.attribute("use");
			if (attr != null) {
				attr.setValue(OMRManagerInfo.use + "");
			}

			attr = OMRManagerEle.attribute("monitor");
			if (attr != null) {
				attr.setValue(OMRManagerInfo.monitor + "");
			}

			List<Element> tempBoardList = OMRManagerEle.elements("tempBoard");
			for (int n = 0; n < tempBoardList.size(); n++) {

				Element element = tempBoardList.get(n);
				attr = element.attribute("portName");
				if (attr != null) {

					attr.setValue(OMRManagerInfo.meterInfos.get(n).portName);
				}
			}

		}

		// 加载探头管理器信息
		Element probeManagerElement = root.element("probeManager");
		if (probeManagerElement != null) {

			attr = probeManagerElement.attribute("use");
			if (attr != null) {

				attr.setValue(probeManagerInfo.use + "");
			}
			attr = probeManagerElement.attribute("monitor");
			if (attr != null) {

				attr.setValue(probeManagerInfo.monitor + "");
			}

			for (int i = 0; i < probeManagerElement.elements("probe").size(); i++) {

				Element ele = probeManagerElement.elements("probe").get(i);
				ProbeInfo pi = probeManagerInfo.probeInfos.get(i);

				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(pi.portName);
				}

				attr = ele.attribute("count");
				if (attr != null) {

					attr.setValue(pi.count + "");
				}

			}
		}

		// 加载烟雾报警管理器
		Element smogManagerElement = root.element("smogAlertManager");
		if (smogManagerElement != null) {

			attr = smogManagerElement.attribute("use");
			if (attr != null) {

				attr.setValue(smogAlertManagerInfo.use + "");
			}
			attr = smogManagerElement.attribute("monitor");
			if (attr != null) {

				attr.setValue(smogAlertManagerInfo.monitor + "");
			}

			for (int i = 0; i < smogManagerElement.elements("smogInfo").size(); i++) {

				Element ele = smogManagerElement.elements("smogInfo").get(i);
				SmogInfo si = smogAlertManagerInfo.smogInfos.get(i);

				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(si.portName);
				}
			}
		}

		// 加载三色灯报警器
		Element colorLightManagerElement = root.element("colorLightManager");
		if (colorLightManagerElement != null) {

			attr = colorLightManagerElement.attribute("use");
			if (attr != null) {

				attr.setValue(colorLightManagerInfo.use + "");
			}

			for (int i = 0; i < colorLightManagerElement.elements("colorLightInfo").size(); i++) {

				Element ele = colorLightManagerElement.elements("colorLightInfo").get(i);
				ColorLightInfo cli = colorLightManagerInfo.colorLights.get(i);

				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(cli.portName);
				}
			}
		}

		// 加载状态&极性灯配置
		Element stateLightControllerElement = root.element("stateLightController");
		if (stateLightControllerElement != null) {

			attr = stateLightControllerElement.attribute("use");
			if (attr != null) {

				attr.setValue(stateLightControllerInfo.use + "");
			}

			for (int i = 0; i < stateLightControllerElement.elements("stateLightInfo").size(); i++) {

				Element ele = stateLightControllerElement.elements("stateLightInfo").get(i);
				StateLightInfo sli = stateLightControllerInfo.stateLights.get(i);
				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(sli.portName);
				}

			}
		}

		// 加载蜂鸣报警管理器
		Element beepManagerElement = root.element("beepAlertManager");
		if (beepManagerElement != null) {

			attr = beepManagerElement.attribute("use");
			if (attr != null) {

				attr.setValue(beepAlertManagerInfo.use + "");
			}

			for (int i = 0; i < beepManagerElement.elements("beepInfo").size(); i++) {

				Element ele = beepManagerElement.elements("beepInfo").get(i);
				BeepAlertInfo bi = beepAlertManagerInfo.beepInfos.get(i);
				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(bi.portName);
				}
			}

		}

		// 加载门禁报警管理器
		Element doorManagerElement = root.element("doorAlertManager");
		if (doorManagerElement != null) {

			attr = doorManagerElement.attribute("use");
			if (attr != null) {

				attr.setValue(doorAlertManagerInfo.use + "");
			}

			attr = doorManagerElement.attribute("monitor");
			if (attr != null) {

				attr.setValue(doorAlertManagerInfo.monitor + "");
			}

			for (int i = 0; i < doorManagerElement.elements("doorInfo").size(); i++) {

				Element ele = doorManagerElement.elements("doorInfo").get(i);
				DoorInfo di = doorAlertManagerInfo.doorInfos.get(i);
				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(di.portName);
				}

			}
		}

		// 加载分区流程支持
		Element procedureSupport = root.element("procedureSupport");
		if (procedureSupport != null) {

			attr = procedureSupport.attribute("driver");
			if (attr != null) {

				attr.setValue(procedureSupportInfo.supportDriver + "");
			}

			attr = procedureSupport.attribute("supportImportantData");
			if (attr != null) {

				attr.setValue(procedureSupportInfo.supportImportantData + "");
			}

		}

		// 智能保护支持
		Element smartProtectsElement = root.element("smartProtects");
		if (smartProtectsElement != null) {

			attr = smartProtectsElement.attribute("enable");
			if (attr != null) {

				attr.setValue(this.smartProtects.enalbe + "");
			}
			/**
			 * 智能保护采样间隔
			 */
			attr = smartProtectsElement.attribute("interval");
			if (attr != null) {

				attr.setValue(this.smartProtects.interval + "");
			}

			// 接触电阻
			Element touchResisterElement = smartProtectsElement.element("touchResister");
			if (touchResisterElement != null) {

				attr = touchResisterElement.attribute("enable");
				if (attr != null) {

					attr.setValue(this.smartProtects.touch.enable + "");
				}
				attr = touchResisterElement.attribute("minCurrent");
				if (attr != null) {

					attr.setValue(this.smartProtects.touch.minCurrent + "");
				}

				attr = touchResisterElement.attribute("protectRatio");
				if (attr != null) {

					attr.setValue(this.smartProtects.touch.protectRatio + "");
				}

				attr = touchResisterElement.attribute("sampleMinCount");
				if (attr != null) {

					attr.setValue(this.smartProtects.touch.sampleMinCount + "");
				}

				attr = touchResisterElement.attribute("trailCount");
				if (attr != null) {

					attr.setValue(this.smartProtects.touch.trailCount + "");
				}

			}

		}

		// 加载液晶屏
		Element screen = root.element("screen");
		if (screen != null) {

			attr = screen.attribute("portName");
			if (attr != null) {

				attr.setValue(this.screenInfo.portName + "");
			}

			attr = screen.attribute("use");
			if (attr != null) {

				attr.setValue(this.screenInfo.use + "");
			}

			attr = screen.attribute("communicateTimeout");
			if (attr != null) {

				attr.setValue(this.screenInfo.communicateTimeout + "");
			}

		}

		// 电源配置信息
		Element powers = root.element("powerManager");

		if (powers != null) {

			attr = powers.attribute("product");
			if (attr != null) {

				attr.setValue(this.powerManagerInfo.product.name() + "");
			}

			attr = powers.attribute("use");
			if (attr != null) {

				attr.setValue(this.powerManagerInfo.use + "");
			}

			attr = powers.attribute("monitor");
			if (attr != null) {

				attr.setValue(this.powerManagerInfo.monitor + "");
			}

			attr = powers.attribute("group");
			if (attr != null) {

				String val = "";
				for (int n = 0; n < this.powerManagerInfo.powerGroups.size(); n++) {

					val += this.powerManagerInfo.powerGroups.get(n)
							+ (n < this.powerManagerInfo.powerGroups.size() - 1 ? "," : "");

				}
				if (this.powerManagerInfo.powerGroups.size() == 1) {

					val += ",0";
				}

				attr.setValue(val);
			}

			List<Element> powerList = powers.elements();
			for (int i = 0; i < powerList.size(); i++) {

				Element ele = powerList.get(i);
				PowerInfo pi = this.powerManagerInfo.powerInfos.get(i);

				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(pi.portName + "");
				}

				attr = ele.attribute("powerCount");
				if (attr != null) {

					attr.setValue(pi.powerCount + "");
				}
			}

		}

		// 风机配置信息
		Element fans = root.element("fanManager");
		if (fans != null) {

			attr = fans.attribute("use");
			if (attr != null) {

				attr.setValue(this.fanManagerInfo.use + "");
			}

			attr = fans.attribute("monitor");
			if (attr != null) {

				attr.setValue(this.fanManagerInfo.monitor + "");
			}
			List<Element> fanList = fans.elements("fanInfo");

			for (int i = 0; i < fanList.size(); i++) {

				Element ele = fanList.get(i);

				FanInfo fi = new FanInfo();

				attr = ele.attribute("portName");
				if (attr != null) {

					attr.setValue(fi.portName + "");
				}

				attr = ele.attribute("fanCount");
				if (attr != null) {

					attr.setValue(fi.fanCount + "");
				}

			}

		}

		XmlUtil.saveXml(path, document);

	}

	private void loadFromDocument() throws AlertException {

		Element root = document.getRootElement();
		// 基本信息
		Element base = root.element("base");
		customer = base.attributeValue("customer") == null ? Customer.ATL
				: Customer.valueOf(base.attributeValue("customer"));
		productType = ProductType.valueOf(base.attributeValue("productType"));
		driverChnCount = Integer.parseInt(base.attributeValue("driverChnCount"));
		logicDriverCount = Integer.parseInt(base.attributeValue("logicDriverCount"));
		Cc2CvDescendCount = Integer.parseInt(base.attributeValue("Cc2CvDescendCount"));
		currentPrecide = Double.parseDouble(base.attributeValue("currentPrecide"));
		voltagePreCide = Double.parseDouble(base.attributeValue("voltagePrecide"));
		disableDefaultProtection = base.attributeValue("disableDefaultProtection") == null ? false
				: Boolean.parseBoolean(base.attributeValue("disableDefaultProtection"));

		if (base.attributeValue("language") != null) {
			language = base.attributeValue("language");
		}
		if(base.attributeValue("workEnv") != null) {
			
			workEnv = WorkEnvironment.valueOf(base.attributeValue("workEnv"));
		}
		if(base.attributeValue("system") != null) {
			
			system = OperationSystem.valueOf(base.attributeValue("system"));
		}
		if(base.attributeValue("useErrorPick") != null) {
			
			useErrorPick = Boolean.parseBoolean(base.attributeValue("useErrorPick"));
		}
		

		System.out.println("load driver chn count = " + driverChnCount);

		// 加载开关
		Element switcher = root.element("switcher");
		useVirtualData = Boolean.parseBoolean(switcher.attributeValue("useVirtualData"));
		useAlert = Boolean.parseBoolean(switcher.attributeValue("useAlert"));
		useReverseChnIndex = Boolean.parseBoolean(switcher.attributeValue("useReverseChnIndex"));
		useDebug = Boolean.parseBoolean(switcher.attributeValue("useDebug"));
		if (switcher.attributeValue("procedureMode") != null) {

			procedureMode = ProcedureMode.valueOf(switcher.attributeValue("procedureMode"));
		}

		if (switcher.attributeValue("useStepChangeProtect") != null) {

			useStepChangeProtect = Boolean.parseBoolean(switcher.attributeValue("useStepChangeProtect"));
		}

		if (switcher.attributeValue("useSTM32Time") != null) {

			useSTM32Time = Boolean.parseBoolean(switcher.attributeValue("useSTM32Time"));
		}

		// 校准板通道反序?
		String calboardReverseStr = switcher.attributeValue("useCalboardReverseChnIndex");
		if (CommonUtil.isNullOrEmpty(calboardReverseStr)) {

			useCalboardReverseChnIndex = useReverseChnIndex;
		} else {

			useCalboardReverseChnIndex = Boolean.parseBoolean(calboardReverseStr);
		}

		Element logCfg = root.element("logCfg");
		if (logCfg != null) {

			String enableStr = logCfg.attributeValue("printChnLog");
			if (enableStr != null) {

				this.logCfg.printChnLog = Boolean.parseBoolean(enableStr);
			}

			enableStr = logCfg.attributeValue("printSysLog");
			if (enableStr != null) {

				this.logCfg.printSysLog = Boolean.parseBoolean(enableStr);
			}

			enableStr = logCfg.attributeValue("printProtocolLog");
			if (enableStr != null) {

				this.logCfg.printProtocolLog = Boolean.parseBoolean(enableStr);
			}
			
			String printChnPickup = logCfg.attributeValue("printChnPickLog");
			if(printChnPickup != null) {
				
				this.logCfg.printChnPickLog = Integer.parseInt(printChnPickup);
			}
		}

		Element backup = root.element("backupBoard");
		if (backup != null) {

			String enableStr = backup.attributeValue("enable");
			if (enableStr != null) {

				this.backupBoard.enable = Boolean.parseBoolean(enableStr);
			}
			String timeStr = backup.attributeValue("time");
			if (timeStr != null) {

				this.backupBoard.time = Integer.parseInt(timeStr);
			}
			String str = backup.attributeValue("resister");
			if (str != null) {

				this.backupBoard.resister = Double.parseDouble(str);
			}
			str = backup.attributeValue("offset");
			if (str != null) {

				this.backupBoard.offset = Double.parseDouble(str);
			}
			str = backup.attributeValue("voltThreshold");
			if (str != null) {

				this.backupBoard.voltThreshold = Double.parseDouble(str);
			}
			str = backup.attributeValue("resisterThreshold");
			if (str != null) {

				this.backupBoard.resisterThreshold = Double.parseDouble(str);
			}

			/**
			 * public double offset; //波动偏差值 public double voltThreshold; //阀值;备份电压偏差多少开始处理
			 * public double resisterThreshold; //接触电阻阀值
			 */
		}

		// 加载IP地址
		Element ipAddress = root.element("ip");
		if (ipAddress != null) {

			this.ip = ipAddress.getTextTrim();
			System.out.println("load ip address :" + ip);
		}

		// 多量程
		Element rangeElement = root.element("range");
		if (rangeElement != null) {

			List<Element> sections = rangeElement.elements("section");
			for (Element sectionElement : sections) {

				RangeSection rs = new RangeSection();
				rs.level = Integer.parseInt(sectionElement.attributeValue("level"));
				rs.lower = Double.parseDouble(sectionElement.attributeValue("lower"));
				rs.upper = Double.parseDouble(sectionElement.attributeValue("upper"));
				rs.precision = Double.parseDouble(sectionElement.attributeValue("precision"));
				rs.filter = Boolean.parseBoolean(sectionElement.attributeValue("filter"));
				rs.currentFilterRange = Double.parseDouble(sectionElement.attributeValue("currentFilterRange"));
                if(sectionElement.attributeValue("currentFilterPercent") != null) {
                	
                	rs.currentFilterPercent = Double.parseDouble(sectionElement.attributeValue("currentFilterPercent"));
                }
				range.sections.add(rs);
			}
			range.use = Boolean.parseBoolean(rangeElement.attributeValue("use"));
			range.disableCurrentLine = Double
					.parseDouble(rangeElement.attributeValue("disableCurrentLine") == null ? "0"
							: rangeElement.attributeValue("disableCurrentLine"));
			range.disableVoltageLine = Double
					.parseDouble(rangeElement.attributeValue("disableVoltageLine") == null ? "0"
							: rangeElement.attributeValue("disableVoltageLine"));
			range.voltageFilterRange = Double
					.parseDouble(rangeElement.attributeValue("voltageFilterRange") == null ? "0"
							: rangeElement.attributeValue("voltageFilterRange"));
			range.voltagePrecision = Double.parseDouble(rangeElement.attributeValue("voltagePrecision") == null ? "2"
					: rangeElement.attributeValue("voltagePrecision"));
			range.voltageStartOffset = Double
					.parseDouble(rangeElement.attributeValue("voltageStartOffset") == null ? "0"
							: rangeElement.attributeValue("voltageStartOffset"));
			range.filterVoltage = Boolean.parseBoolean(rangeElement.attributeValue("filterVoltage") == null ? "false"
					: rangeElement.attributeValue("filterVoltage"));
			range.filterCurrent = Boolean.parseBoolean(rangeElement.attributeValue("filterCurrent") == null ? "false"
					: rangeElement.attributeValue("filterCurrent"));
			range.continueAlertFilter = Boolean
					.parseBoolean(rangeElement.attributeValue("continueAlertFilter") == null ? "true"
							: rangeElement.attributeValue("continueAlertFilter"));

			range.microVolt = Double.parseDouble(rangeElement.attributeValue("microVolt") == null ? "2500"
					: rangeElement.attributeValue("microVolt"));
			range.microCurr = Double.parseDouble(
					rangeElement.attributeValue("microCurr") == null ? "50" : rangeElement.attributeValue("microCurr"));

			range.useCvCurrentFilter = rangeElement.attributeValue("useCvCurrentFilter") == null ? false
					: Boolean.parseBoolean(rangeElement.attributeValue("useCvCurrentFilter"));
			
			range.useDcVoltageFilter = rangeElement.attributeValue("useDcVoltageFilter") == null ? false
					: Boolean.parseBoolean(rangeElement.attributeValue("useDcVoltageFilter"));
			
			range.useCcVoltageFilter = rangeElement.attributeValue("useCcVoltageFilter") == null ? false
					: Boolean.parseBoolean(rangeElement.attributeValue("useCcVoltageFilter"));

			range.useSlpVoltFilter = rangeElement.attributeValue("useSlpVoltFilter") == null ? false
					: Boolean.parseBoolean(rangeElement.attributeValue("useSlpVoltFilter"));
			
			range.adjustCcCvVoltage = rangeElement.attributeValue("adjustCcCvVoltage") == null ? false
					: Boolean.parseBoolean(rangeElement.attributeValue("adjustCcCvVoltage"));
			
			

		}

		// 精度控制?
		/*
		 * Element precisionElement = root.element("precision"); if (precisionElement !=
		 * null) {
		 * 
		 * precision.useDebug =
		 * Boolean.parseBoolean(precisionElement.attributeValue("useDebug"));
		 * precision.currentOffsetRange =
		 * Double.parseDouble(precisionElement.attributeValue("currentOffsetRange"));
		 * precision.voltageOffsetRange =
		 * Double.parseDouble(precisionElement.attributeValue("voltageOffsetRange"));
		 * precision.continueCCPrecisionCount = Integer
		 * .parseInt(precisionElement.attributeValue("continueCCPrecisionCount")); }
		 */
		// 通信协议
		Element protocolElement = root.element("protocol");

		protocol.voltageResolution = Integer.parseInt(protocolElement.attributeValue("voltageResolution"));
		protocol.currentResolution = Integer.parseInt(protocolElement.attributeValue("currentResolution"));
		protocol.capacityResolution = Integer.parseInt(protocolElement.attributeValue("capacityResolution"));
		protocol.energyResolution = Integer.parseInt(protocolElement.attributeValue("energyResolution"));
		protocol.useLogicPowerVoltage = protocolElement.attributeValue("useLogicPowerVoltage") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useLogicPowerVoltage"));
		protocol.useStandaloneProcedure = protocolElement.attributeValue("useStandaloneProcedure") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useStandaloneProcedure"));
		protocol.useTempProbe = protocolElement.attributeValue("useTempProbe") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useTempProbe"));

		protocol.useChnTempPick = protocolElement.attributeValue("useChnTempPick") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useChnTempPick"));

		protocol.useFrameTempPick = protocolElement.attributeValue("useFrameTempPick") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useFrameTempPick"));

		protocol.useAirPressure = protocolElement.attributeValue("useAirPressure") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useAirPressure"));

		protocol.useStepRecord = protocolElement.attributeValue("useStepRecord") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useStepRecord"));

		protocol.useProcedureWorkType = protocolElement.attributeValue("useProcedureWorkType") == null ? false
				: Boolean.parseBoolean(protocolElement.attributeValue("useProcedureWorkType"));

		if (protocolElement.attributeValue("moduleCount") != null) {

			protocol.moduleCount = Integer.parseInt(protocolElement.attributeValue("moduleCount"));

		}

		Element fanElement = protocolElement.element("fan");
		if (fanElement != null) {

			protocol.maxFanSupportCount = Integer.parseInt(fanElement.attributeValue("maxFanSupportCount"));
			protocol.useAnotherFanStateQuery = Boolean
					.parseBoolean(fanElement.attributeValue("useAnotherFanStateQuery"));
		}

		// KB系数小数位
		Element kbElement = root.element("kbDecimal");
		if (kbElement != null) {

			kbDecimal.adcK = Integer.parseInt(kbElement.attributeValue("adcK"));
			kbDecimal.adcB = Integer.parseInt(kbElement.attributeValue("adcB"));
			kbDecimal.programK = Integer.parseInt(kbElement.attributeValue("programK"));
			kbDecimal.programB = Integer.parseInt(kbElement.attributeValue("programB"));
		}

		// 限制
		Element limit = root.element("limit");
		maxDeviceVoltage = Double.parseDouble(limit.attributeValue("maxDeviceVoltage"));
		maxDeviceCurrent = Double.parseDouble(limit.attributeValue("maxDeviceCurrent"));
		maxDefineVoltage = Double.parseDouble(limit.attributeValue("maxDefineVoltage"));
		minDefineVoltage = Double.parseDouble(limit.attributeValue("minDefineVoltage"));
		minDeviceVoltage = Double.parseDouble(limit.attributeValue("minDeviceVoltage"));
		minDeviceCurrent = Double.parseDouble(limit.attributeValue("minDeviceCurrent"));
		maxStartupTimeout = Integer.parseInt(limit.attributeValue("maxStartupTimeout"));
		maxContinueStartupTimeout = Integer.parseInt(limit.attributeValue("maxContinueStartupTimeout"));
		minDischargeVoltage = Double.parseDouble(limit.attributeValue("minDischargeVoltage"));
		driverTemperatureAlert = Double.parseDouble(limit.attributeValue("driverTemperatureAlert"));
		driverTemperatureAttention = Double.parseDouble(limit.attributeValue("driverTemperatureAttention"));
		minRunningCurrent = Double.parseDouble(
				limit.attributeValue("minRunningCurrent") == null ? "0" : limit.attributeValue("minRunningCurrent"));
		maxContinueAlertCount = Integer.parseInt(limit.attributeValue("maxContinueAlertCount") == null ? "3"
				: limit.attributeValue("maxContinueAlertCount"));
		maxPushOffCount = Integer.parseInt(
				limit.attributeValue("maxPushOffCount") == null ? "5" : limit.attributeValue("maxPushOffCount"));

		// 加载串口信息
		Element portsElement = root.element("ports");
		List<Element> portList = portsElement.elements("port");
		for (int i = 0; i < portList.size(); i++) {

			PortInfo pi = new PortInfo();
			pi.name = portList.get(i).attributeValue("name");
			pi.baudrate = Integer.parseInt(portList.get(i).attributeValue("baudrate"));
			pi.use = Boolean.parseBoolean(
					portList.get(i).attributeValue("use") == null ? "true" : portList.get(i).attributeValue("use"));
			ports.put(pi.name, pi);
		}
        
		Element driversElement = root.element("drivers");
		if(driversElement.attributeValue("useTempPick") != null) {
			
			driversCfg.useTempPick = Boolean.parseBoolean(driversElement.attributeValue("useTempPick"));
		}
		if(driversElement.attributeValue("driverboardType") != null) {
			
			driversCfg.driverboardType = DriverboardType.valueOf(driversElement.attributeValue("driverboardType"));
		}
		
		// 加载驱动板信息
		List<Node> drivers = root.selectNodes("drivers/driver");
		for (int n = 0; n < drivers.size(); n++) {

			Element driverElement = (Element) drivers.get(n);
			DriverInfo driverInfo = new DriverInfo();
			driverInfo.index = Integer.parseInt(driverElement.attributeValue("index"));
			driverInfo.portName = driverElement.attributeValue("portName");
			driverInfo.use = Boolean.parseBoolean(driverElement.attributeValue("use"));
			//driverInfo.useAssist = Boolean.parseBoolean(driverElement.attributeValue("useAssist"));
			driverInfo.communication = Long.parseLong(driverElement.attributeValue("communicateTimeout"));
			driverInfo.pickupTime = Long.parseLong(driverElement.attributeValue("pickupTime"));
			if(driverElement.attributeValue("reverseChnIndex") != null) {
				
				driverInfo.reverseChnIndex = Boolean.parseBoolean(driverElement.attributeValue("reverseChnIndex"));
			}
			if(driverElement.attributeValue("ip") != null) {
				
				driverInfo.ip = driverElement.attributeValue("ip");
			}
			
			
			//driverInfo.assistCommunication = Long.parseLong(driverElement.attributeValue("assistCommunicateTimeout"));
			//driverInfo.assistPickupTime = Long.parseLong(driverElement.attributeValue("assistPickupTime"));

			this.driverInfos.add(driverInfo);

		}

		// 加载逻辑板信息
		List<Node> logics = root.selectNodes("logics/logic");
		for (int n = 0; n < logics.size(); n++) {

			Element logicElement = (Element) logics.get(n);
			LogicInfo logicInfo = new LogicInfo();
			logicInfo.index = Integer.parseInt(logicElement.attributeValue("index"));
			logicInfo.portName = logicElement.attributeValue("portName");
			logicInfo.communicateTimeout = Integer.parseInt(logicElement.attributeValue("communicateTimeout"));
			logicInfo.pickupTime = Integer.parseInt(logicElement.attributeValue("pickupTime"));
			logicInfo.reverseDriverIndex = Boolean.parseBoolean(logicElement.attributeValue("reverseDriverIndex"));
			logicInfo.use = Boolean.parseBoolean(logicElement.attributeValue("use"));
			logicInfo.enableFlag = Integer.parseInt(logicElement.attributeValue("enableFlag") == null ? "ffff"
					: logicElement.attributeValue("enableFlag"), 16);
			logicInfos.add(logicInfo);

		}

		// 加载控制板
		Element control = root.element("control");
		controlInfo.portName = control.attributeValue("portName");
		controlInfo.use = Boolean.parseBoolean(control.attributeValue("use"));
		controlInfo.heartbeat = Boolean.parseBoolean(
				control.attributeValue("heartbeat") == null ? "false" : control.attributeValue("heartbeat"));
		controlInfo.resetPower = Boolean.parseBoolean(
				control.attributeValue("resetPower") == null ? "false" : control.attributeValue("resetPower"));
		controlInfo.communicateTimeout = Integer.parseInt(control.attributeValue("communicateTimeout"));
		if(control.attributeValue("ip") != null) {
			
			controlInfo.ip = control.attributeValue("ip");
		}
		
		// 加载温控板(表)
		Element OMRManagerEle = root.element("OMRTemperatureManager");
		boolean useTempManager = OMRManagerEle.attributeValue("use") == null ? false
				: Boolean.parseBoolean(OMRManagerEle.attributeValue("use"));
		boolean monitorTempManager = OMRManagerEle.attributeValue("monitor") == null ? false
				: Boolean.parseBoolean(OMRManagerEle.attributeValue("monitor"));
		OMRManagerInfo.use = useTempManager;
		OMRManagerInfo.monitor = monitorTempManager;

		List<Element> tempBoardList = OMRManagerEle.elements("tempBoard");
		for (Element element : tempBoardList) {

			TempBoardInfo tbi = new TempBoardInfo();
			tbi.tempBoardType = TempBoardType.valueOf(element.attributeValue("tempBoardType"));
			tbi.portName = element.attributeValue("portName");
			tbi.index = Integer.parseInt(element.attributeValue("index"));
			tbi.use = Boolean.parseBoolean(element.attributeValue("use"));
			tbi.communicateTimeout = Integer.parseInt(element.attributeValue("communicateTimeout"));
			OMRManagerInfo.meterInfos.add(tbi);
		}

		// 加载机械配置信息
		Element mechanismElement = root.element("mechanismManager");
		if (mechanismElement != null) {

			mechanismManagerInfo.use = mechanismElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(mechanismElement.attributeValue("use"));
			mechanismManagerInfo.monitor = mechanismElement.attributeValue("monitor") == null ? false
					: Boolean.parseBoolean(mechanismElement.attributeValue("monitor"));

			// 加载气缸参数
			for (int i = 0; i < mechanismElement.elements("cylinder").size(); i++) {

				Element ele = mechanismElement.elements("cylinder").get(i);
				CylinderInfo ci = new CylinderInfo();
				ci.count = Integer.parseInt(ele.attributeValue("count"));
				ci.portName = ele.attributeValue("portName");
				ci.use = Boolean.parseBoolean(ele.attributeValue("use"));
				ci.monitor = Boolean.parseBoolean(ele.attributeValue("monitor"));
				ci.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				mechanismManagerInfo.cylinderInfos.add(ci);
			}

			for (int i = 0; i < mechanismElement.elements("tempProbe").size(); i++) {

				Element ele = mechanismElement.elements("tempProbe").get(i);
				TempProbeInfo tpi = new TempProbeInfo();
				tpi.count = Integer.parseInt(ele.attributeValue("count"));
				tpi.portName = ele.attributeValue("portName");
				tpi.use = Boolean.parseBoolean(ele.attributeValue("use"));
				tpi.monitor = Boolean.parseBoolean(ele.attributeValue("monitor"));
				tpi.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				mechanismManagerInfo.tempProbeInfos.add(tpi);
			}

		}

		// 加载探头管理器信息
		Element probeManagerElement = root.element("probeManager");
		if (probeManagerElement != null) {
			probeManagerInfo.use = probeManagerElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(probeManagerElement.attributeValue("use"));
			probeManagerInfo.monitor = probeManagerElement.attributeValue("monitor") == null ? false
					: Boolean.parseBoolean(probeManagerElement.attributeValue("monitor"));
			for (int i = 0; i < probeManagerElement.elements("probe").size(); i++) {

				Element ele = probeManagerElement.elements("probe").get(i);
				ProbeInfo pi = new ProbeInfo();
				pi.count = Integer.parseInt(ele.attributeValue("count"));
				pi.type = ProbeType.valueOf(ele.attributeValue("probeType"));
				pi.portName = ele.attributeValue("portName");
				pi.use = Boolean.parseBoolean(ele.attributeValue("use"));
				pi.monitor = Boolean.parseBoolean(ele.attributeValue("monitor"));
				pi.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				String controlTemp = ele.attributeValue("controlTemp");
				pi.controlTemp = CommonUtil.isNullOrEmpty(controlTemp) ? false : Boolean.parseBoolean(controlTemp);

				probeManagerInfo.probeInfos.add(pi);

			}
		}

		// 加载烟雾报警管理器
		Element smogManagerElement = root.element("smogAlertManager");
		if (smogManagerElement != null) {

			smogAlertManagerInfo.use = smogManagerElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(smogManagerElement.attributeValue("use"));
			smogAlertManagerInfo.monitor = smogManagerElement.attributeValue("monitor") == null ? false
					: Boolean.parseBoolean(smogManagerElement.attributeValue("monitor"));

			for (int i = 0; i < smogManagerElement.elements("smogInfo").size(); i++) {

				Element ele = smogManagerElement.elements("smogInfo").get(i);
				SmogInfo si = new SmogInfo();
				si.index = Integer.parseInt(ele.attributeValue("index"));
				si.portName = ele.attributeValue("portName");
				si.use = Boolean.parseBoolean(ele.attributeValue("use"));
				si.monitor = Boolean.parseBoolean(ele.attributeValue("monitor"));
				si.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				smogAlertManagerInfo.smogInfos.add(si);
			}
		}

		// 加载三色灯报警器
		Element colorLightManagerElement = root.element("colorLightManager");
		if (colorLightManagerElement != null) {

			colorLightManagerInfo.use = colorLightManagerElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(colorLightManagerElement.attributeValue("use"));

			for (int i = 0; i < colorLightManagerElement.elements("colorLightInfo").size(); i++) {

				Element ele = colorLightManagerElement.elements("colorLightInfo").get(i);
				ColorLightInfo cli = new ColorLightInfo();
				cli.index = Integer.parseInt(ele.attributeValue("index"));
				cli.portName = ele.attributeValue("portName");
				cli.use = Boolean.parseBoolean(ele.attributeValue("use"));
				cli.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				colorLightManagerInfo.colorLights.add(cli);
			}
		}

		// 加载状态&极性灯配置
		Element stateLightControllerElement = root.element("stateLightController");
		if (stateLightControllerElement != null) {

			stateLightControllerInfo.use = stateLightControllerElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(stateLightControllerElement.attributeValue("use"));

			for (int i = 0; i < stateLightControllerElement.elements("stateLightInfo").size(); i++) {

				Element ele = stateLightControllerElement.elements("stateLightInfo").get(i);
				StateLightInfo sli = new StateLightInfo();
				sli.index = Integer.parseInt(ele.attributeValue("index"));
				sli.portName = ele.attributeValue("portName");
				sli.use = Boolean.parseBoolean(ele.attributeValue("use"));
				sli.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));

				stateLightControllerInfo.stateLights.add(sli);
			}
		}

		// 加载蜂鸣报警管理器
		Element beepManagerElement = root.element("beepAlertManager");
		if (beepManagerElement != null) {

			beepAlertManagerInfo.use = beepManagerElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(beepManagerElement.attributeValue("use"));

			for (int i = 0; i < beepManagerElement.elements("beepInfo").size(); i++) {

				Element ele = beepManagerElement.elements("beepInfo").get(i);
				BeepAlertInfo bi = new BeepAlertInfo();
				bi.index = Integer.parseInt(ele.attributeValue("index"));
				bi.portName = ele.attributeValue("portName");
				bi.use = Boolean.parseBoolean(ele.attributeValue("use"));
				bi.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				beepAlertManagerInfo.beepInfos.add(bi);
			}

		}

		// 加载门禁报警管理器
		Element doorManagerElement = root.element("doorAlertManager");
		if (doorManagerElement != null) {

			doorAlertManagerInfo.use = doorManagerElement.attributeValue("use") == null ? false
					: Boolean.parseBoolean(doorManagerElement.attributeValue("use"));
			doorAlertManagerInfo.monitor = doorManagerElement.attributeValue("monitor") == null ? false
					: Boolean.parseBoolean(doorManagerElement.attributeValue("monitor"));

			for (int i = 0; i < doorManagerElement.elements("doorInfo").size(); i++) {

				Element ele = doorManagerElement.elements("doorInfo").get(i);
				DoorInfo di = new DoorInfo();
				di.index = Integer.parseInt(ele.attributeValue("index"));
				di.portName = ele.attributeValue("portName");
				di.use = Boolean.parseBoolean(ele.attributeValue("use"));
				di.monitor = Boolean.parseBoolean(ele.attributeValue("monitor"));
				di.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
				doorAlertManagerInfo.doorInfos.add(di);
			}
		}

		// 加载报警器
		Element alarms = root.element("alarmManager");
		if (alarms != null) {
			alarmManagerInfo.use = alarms.attributeValue("use") == null ? false
					: Boolean.parseBoolean(alarms.attributeValue("use"));
			alarmManagerInfo.monitor = alarms.attributeValue("monitor") == null ? false
					: Boolean.parseBoolean(alarms.attributeValue("monitor"));
			List<Element> alarmList = alarms.elements("alarm");
			for (Element element : alarmList) {

				AlarmInfo ami = new AlarmInfo();
				ami.portName = element.attributeValue("portName");
				ami.use = Boolean.parseBoolean(element.attributeValue("use"));
				ami.communicateTimeout = Integer.parseInt(element.attributeValue("communicateTimeout"));
				ami.alarmType = AlarmType.valueOf(element.attributeValue("alarmType"));
				if (element.attributeValue("index") != null) {

					ami.index = Integer.parseInt(element.attributeValue("index"));
				}
				alarmManagerInfo.alarmInfos.add(ami);
			}
		}

		// 加载分区流程支持
		Element procedureSupport = root.element("procedureSupport");
		if (procedureSupport != null) {

			procedureSupportInfo.supportLogic = Boolean
					.parseBoolean(procedureSupport.attributeValue("logic") == null ? "false"
							: procedureSupport.attributeValue("logic"));
			procedureSupportInfo.supportDriver = Boolean
					.parseBoolean(procedureSupport.attributeValue("driver") == null ? "false"
							: procedureSupport.attributeValue("driver"));
			procedureSupportInfo.supportImportantData = Boolean
					.parseBoolean(procedureSupport.attributeValue("supportImportantData") == null ? "true"
							: procedureSupport.attributeValue("supportImportantData"));
			procedureSupportInfo.supportLeadData = Boolean
					.parseBoolean(procedureSupport.attributeValue("supportLeadData") == null ? "false"
							: procedureSupport.attributeValue("supportLeadData"));
		}

		// 智能保护支持
		Element smartProtectsElement = root.element("smartProtects");
		if (smartProtectsElement != null) {
			this.smartProtects.enalbe = Boolean.parseBoolean(smartProtectsElement.attributeValue("enable"));
			this.smartProtects.interval = Integer.parseInt(smartProtectsElement.attributeValue("interval"));
			// 接触电阻
			Element touchResisterElement = smartProtectsElement.element("touchResister");
			if (touchResisterElement != null) {

				smartProtects.touch.enable = Boolean.parseBoolean(touchResisterElement.attributeValue("enable"));
				smartProtects.touch.minCurrent = Double.parseDouble(touchResisterElement.attributeValue("minCurrent"));
				smartProtects.touch.protectRatio = Double
						.parseDouble(touchResisterElement.attributeValue("protectRatio"));
				smartProtects.touch.sampleMinCount = Integer
						.parseInt(touchResisterElement.attributeValue("sampleMinCount"));
				smartProtects.touch.trailCount = Integer.parseInt(touchResisterElement.attributeValue("trailCount"));

			}

		}
		//针床控制器
		Element  pingElement = root.element("pingController");
		if(pingElement != null) {
			
			this.pingController.enable = Boolean.parseBoolean(pingElement.attributeValue("enable"));
			this.pingController.portName = pingElement.attributeValue("portName");
			this.pingController.communicateTimeout = Integer.parseInt(pingElement.attributeValue("communicateTimeout"));
			
		}
		
		//通道灯
		Element clightElement = root.element("channelLightController");
		if(clightElement != null) {
			
			this.channelLightController.enable = Boolean.parseBoolean(clightElement.attributeValue("enable"));
			this.channelLightController.portName = clightElement.attributeValue("portName");
			this.channelLightController.communicateTimeout = Integer.parseInt(clightElement.attributeValue("communicateTimeout"));
		}
		
		
		

		// 加载液晶屏
		Element screen = root.element("screen");
		
		if(screen != null) {
			
			screenInfo.portName = screen.attributeValue("portName");
			screenInfo.use = Boolean.parseBoolean(screen.attributeValue("use"));
			screenInfo.communicateTimeout = Integer.parseInt(screen.attributeValue("communicateTimeout"));
			screenInfo.password = screen.attributeValue("password") == null ? "123456" : screen.attributeValue("password");
		}

		// 电源配置信息
		Element powers = root.element("powerManager");
		if (powers == null) {

			throw new AlertException(AlertCode.LOGIC, "配置文件cfg.xml丢失powers节点");

		}

		powerManagerInfo.type = powers.attributeValue("type") == null ? PowerManagerType.GROUP
				: PowerManagerType.valueOf(powers.attributeValue("type"));

		/**
		 * 逆变电源产家或品牌
		 */
		if (powers.attributeValue("product") != null) {
			powerManagerInfo.product = PowerProduct.valueOf(powers.attributeValue("product"));
		}

		powerManagerInfo.use = powers.attributeValue("use") == null ? false
				: Boolean.parseBoolean(powers.attributeValue("use"));
		powerManagerInfo.monitor = powers.attributeValue("monitor") == null ? false
				: Boolean.parseBoolean(powers.attributeValue("monitor"));
		// 分组情况
		if (powers.attributeValue("group") == null) {

			throw new AlertException(AlertCode.LOGIC, "配置文件cfg.xml丢失powers节点group属性");
		}
		String[] strs = powers.attributeValue("group").split(",");
		for (String str : strs) {

			powerManagerInfo.powerGroups.add(Integer.parseInt(str));
		}

		List<Element> powerList = powers.elements();
		for (Element ele : powerList) {

			PowerInfo pi = new PowerInfo();
			pi.powerCount = Integer.parseInt(ele.attributeValue("powerCount"));
			pi.powerType = PowerType.valueOf(ele.attributeValue("powerType"));
			pi.portName = ele.attributeValue("portName");
			pi.use = Boolean.parseBoolean(ele.attributeValue("use"));
			pi.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
			pi.monitor = Boolean
					.parseBoolean(ele.attributeValue("monitor") == null ? "false" : ele.attributeValue("monitor"));
			powerManagerInfo.powerInfos.add(pi);
		}

		// 风机配置信息
		Element fans = root.element("fanManager");
		if (fans == null) {

			throw new AlertException(AlertCode.LOGIC, "配置文件cfg.xml丢失fans节点");

		}
		fanManagerInfo.use = fans.attributeValue("use") == null ? false
				: Boolean.parseBoolean(fans.attributeValue("use"));
		fanManagerInfo.monitor = fans.attributeValue("monitor") == null ? false
				: Boolean.parseBoolean(fans.attributeValue("monitor"));
		List<Element> fanList = fans.elements("fanInfo");

		for (Element ele : fanList) {

			FanInfo fi = new FanInfo();
			fi.index = ele.attributeValue("index") == null ? 0 : Integer.parseInt(ele.attributeValue("index"));
			fi.fanType = FanType.valueOf(ele.attributeValue("fanType"));
			fi.fanCount = Integer.parseInt(ele.attributeValue("fanCount"));
			fi.portName = ele.attributeValue("portName");
			fi.use = Boolean.parseBoolean(ele.attributeValue("use"));
			fi.communicateTimeout = Integer.parseInt(ele.attributeValue("communicateTimeout"));
			fi.monitor = Boolean
					.parseBoolean(ele.attributeValue("monitor") == null ? "false" : ele.attributeValue("monitor"));
			fanManagerInfo.fanInfos.add(fi);
		}

	}

	private void loadXml(String path) throws AlertException {

		try {

			int pos = path.lastIndexOf("/");
			File f = new File(path.substring(0, pos));
			f.mkdirs();

			File file = new File(path);
			if (!file.exists()) {

				createDefaultConfigFile();
				XmlUtil.saveXml(path, document);
			}
			document = XmlUtil.loadXml(path);
			loadFromDocument();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isUseVirtualData() {
		return useVirtualData;
	}

	public void setUseVirtualData(boolean useVirtualData) {
		this.useVirtualData = useVirtualData;
	}

	public boolean isUseAlert() {
		return useAlert;
	}

	public void setUseAlert(boolean useAlert) {
		this.useAlert = useAlert;
	}

	public double getMaxDeviceVoltage() {
		return maxDeviceVoltage;
	}

	public void setMaxDeviceVoltage(double maxDeviceVoltage) {
		this.maxDeviceVoltage = maxDeviceVoltage;
	}

	public double getMaxDeviceCurrent() {
		return maxDeviceCurrent;
	}

	public void setMaxDeviceCurrent(double maxDeviceCurrent) {
		this.maxDeviceCurrent = maxDeviceCurrent;
	}

	public double getMinDischargeVoltage() {
		return minDischargeVoltage;
	}

	public void setMinDischargeVoltage(double minDischargeVoltage) {
		this.minDischargeVoltage = minDischargeVoltage;
	}

	public double getMinDeviceCurrent() {
		return minDeviceCurrent;
	}

	public void setMinDeviceCurrent(double minDeviceCurrent) {
		this.minDeviceCurrent = minDeviceCurrent;
	}

	public double getMinDeviceVoltage() {
		return minDeviceVoltage;
	}

	public void setMinDeviceVoltage(double minDeviceVoltage) {
		this.minDeviceVoltage = minDeviceVoltage;
	}

	public double getMinDefineVoltage() {
		return minDefineVoltage;
	}

	public void setMinDefineVoltage(double minDefineVoltage) {
		this.minDefineVoltage = minDefineVoltage;
	}

	public double getMaxDefineVoltage() {
		return maxDefineVoltage;
	}

	public void setMaxDefineVoltage(double maxDefineVoltage) {
		this.maxDefineVoltage = maxDefineVoltage;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public String getPath() {
		return path;
	}

	public int getLogicBoardCount() {

		return logicInfos.size();
	}

	public int getDriverChnCount() {
		return driverChnCount;
	}

	public void setDriverChnCount(int driverChnCount) {
		this.driverChnCount = driverChnCount;
	}

	/**
	 * 保存为文件
	 * 
	 * @param path
	 */
	public void saveXml(String path) {

		try {
			XmlUtil.saveXml(path, document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 创建默认启动配置
	 */
	public void createDefaultConfigFile() {

		document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		Element switcher = root.addElement("switcher");
		switcher.addAttribute("useVirtualData", useVirtualData + "");
		switcher.addAttribute("useAlert", useAlert + "");
		switcher.addAttribute("useReverseChnIndex", useReverseChnIndex + "");
		switcher.addAttribute("procedureMode", "DEVICE");
		switcher.addAttribute("useTempProbe", "false");
		// switcher.addAttribute("useSynchronize", useSynchronize + "");
		// 配置
		Element limit = root.addElement("limit");
		limit.addAttribute("maxDeviceVoltage", "" + maxDeviceVoltage);
		limit.addAttribute("maxDeviceCurrent", "" + maxDeviceCurrent);
		limit.addAttribute("minDeviceVoltage", "" + minDeviceVoltage);
		limit.addAttribute("minDeviceCurrent", "" + minDeviceCurrent);
		limit.addAttribute("minDefineVoltage", "" + minDefineVoltage);
		limit.addAttribute("maxDefineVoltage", "" + maxDefineVoltage);
		limit.addAttribute("maxStartupTimeout", "" + maxStartupTimeout);
		limit.addAttribute("maxContinueStartupTimeout", "" + maxContinueStartupTimeout);
		limit.addAttribute("minDischargeVoltage", "" + minDischargeVoltage);
		limit.addAttribute("maxCurrPrecideOffset", "" + maxCurrPrecideOffset);
		limit.addAttribute("maxVoltPrecideOffset", "" + maxVoltPrecideOffset);
		limit.addAttribute("driverTemperatureAttention", "" + driverTemperatureAttention);
		limit.addAttribute("driverTemperatureAlert", "" + driverTemperatureAlert);
		limit.addAttribute("minRunningCurrent", "" + minRunningCurrent);
		limit.addAttribute("maxContinueAlertCount", "" + maxContinueAlertCount);

		// 基本配置
		Element base = root.addElement("base");
		base.addAttribute("productType", ProductType.POWERBOX.name());
		base.addAttribute("driverChnCount", "" + driverChnCount);
		base.addAttribute("logicDriverCount", "" + logicDriverCount);
		base.addAttribute("Cc2CvDescendCount", "" + Cc2CvDescendCount);
		base.addAttribute("currentPrecide", "" + currentPrecide);
		base.addAttribute("voltagePrecide", "" + voltagePreCide);

		// 通信协议配置
		Element precision = root.addElement("doublePrecision");
		precision.addAttribute("use", "false"); // 电流精度
		precision.addAttribute("precision", "0.1"); // 电压精度
		precision.addAttribute("threshold", "200"); // 容量精度

		// 通信协议配置
		Element pc = root.addElement("protocol");
		pc.addAttribute("currentResolution", "1"); // 电流精度
		pc.addAttribute("voltageResolution", "1"); // 电压精度
		pc.addAttribute("capacityResolution", "1"); // 容量精度
		pc.addAttribute("energyResolution", "1"); // 能量精度

		// 电源项配置信息
		Element powers = root.addElement("powers");
		Element powerInfo = powers.addElement("powerInfo");
		powerInfo.addAttribute("usePower", "false");
		powerInfo.addAttribute("powerType", "INVERTER");
		powerInfo.addAttribute("powerCount", "7");

		powerInfo = powers.addElement("powerInfo");
		powerInfo.addAttribute("usePower", "false");
		powerInfo.addAttribute("powerType", "AUXILIARY");
		powerInfo.addAttribute("powerCount", "3");

		// 风扇配置信息
		Element fans = root.addElement("fans");
		Element fanInfo = fans.addElement("fanInfo");
		fanInfo.addAttribute("useFan", "false");
		fanInfo.addAttribute("fanType", "ADDA");
		fanInfo.addAttribute("fanCount", "3");

		// 控制板
		Element control = root.addElement("control");
		control.addAttribute("portName", "/dev/ttyS4");
		control.addAttribute("use", "false");
		control.addAttribute("communicateTimeout", "2000");

		// 温控板
		Element temperature = root.addElement("temperature");
		temperature.addAttribute("portName", "/dev/ttyS4");
		temperature.addAttribute("use", "false");
		temperature.addAttribute("communicateTimeout", "2000");

		/**
		 * 报警器
		 */
		Element alarms = root.addElement("alarms");
		Element alarm = alarms.addElement("alarm");
		alarm.addAttribute("portName", "/dev/ttyS4");
		alarm.addAttribute("use", "false");
		alarm.addAttribute("communicateTimeout", "2000");
		alarm.addAttribute("alarmType", "AUDIO_LIGHT");

		// 液晶屏
		Element screen = root.addElement("screen");
		screen.addAttribute("portName", "/dev/ttyS5");
		screen.addAttribute("use", "false");
		screen.addAttribute("communicateTimeout", "2000");
		screen.addAttribute("password", "123456");
		// 逻辑板
		Element logics = root.addElement("logics");

		for (int i = 0; i < 2; i++) {
			Element logic = logics.addElement("logic");
			logic.addAttribute("index", i + "");
			logic.addAttribute("portName", "/dev/ttyS" + i);
			logic.addAttribute("use", i == 0 ? "true" : "false");
			logic.addAttribute("reverseDriverIndex", "false");
			logic.addAttribute("communicateTimeout", 500 + "");
			logic.addAttribute("pickupTime", 100 + "");
		}

		// 回检板
		Element checks = root.addElement("checks");

		for (int i = 0; i < 2; i++) {
			Element check = checks.addElement("check");
			check.addAttribute("index", i + "");
			check.addAttribute("portName", "/dev/ttyS" + (i + 2));
			check.addAttribute("use", "false");
			check.addAttribute("reverseDriverIndex", "false");
			check.addAttribute("communicateTimeout", 500 + "");
			check.addAttribute("pickupTime", 200 + "");
		}

	}

	public int getDriverCount() {

		return driverInfos.size();
	}

	public int getDeviceChnCount() {

		int total = 0;
		for (int n = 0; n < getDriverCount(); n++) {

			total += driverChnCount;
		}
		return total;
	}

	public int getLogicChnCount() {

		return logicDriverCount * driverChnCount;
	}

	public LogicInfo getLogicInfo(int index) {

		return logicInfos.get(index);
	}

	public ScreenInfo getScreenInfo() {
		return screenInfo;
	}

	public ControlInfo getControlInfo() {
		return controlInfo;
	}

	public boolean isUseReverseChnIndex() {
		return useReverseChnIndex;
	}

	// public int getLogicDriverCount() {
	// return logicDriverCount;
	// }

	public int getCc2CvDescendCount() {
		return Cc2CvDescendCount;
	}

	public double getMaxCurrPrecideOffset() {
		return maxCurrPrecideOffset;
	}

	public double getMaxVoltPrecideOffset() {
		return maxVoltPrecideOffset;
	}

	public double getCurrentPrecide() {
		return currentPrecide;
	}

	public double getVoltagePreCide() {
		return voltagePreCide;
	}

	public double getMinRunningCurrent() {
		return minRunningCurrent;
	}

	public void setMinRunningCurrent(double minRunningCurrent) {
		this.minRunningCurrent = minRunningCurrent;
	}

	public double getDriverTemperatureAttention() {
		return driverTemperatureAttention;
	}

	public double getDriverTemperatureAlert() {
		return driverTemperatureAlert;
	}

	public int getMaxStartupTimeout() {
		return maxStartupTimeout;
	}

	public void setMaxStartupTimeout(int maxStartupTimeout) {
		this.maxStartupTimeout = maxStartupTimeout;
	}

	public int getMaxContinueStartupTimeout() {
		return maxContinueStartupTimeout;
	}

	public void setMaxContinueStartupTimeout(int maxContinueStartupTimeout) {
		this.maxContinueStartupTimeout = maxContinueStartupTimeout;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Map<String, PortInfo> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, PortInfo> ports) {
		this.ports = ports;
	}

	public DoublePrecision getDoublePrecision() {
		return doublePrecision;
	}

	public void setDoublePrecision(DoublePrecision doublePrecision) {
		this.doublePrecision = doublePrecision;
	}

	public void saveIpAddress(String address) {

		Element root = document.getRootElement();
		Element ipElement = root.element("ip");
		if (ipElement == null) {

			ipElement = root.addElement("ip");
		}
		ipElement.setText(address);
		saveXml(path);
	}

	/**
	 * 保存液晶屏的密码
	 * 
	 * @param password
	 */
	public void saveScreenPassword(String password) {

		Element root = document.getRootElement();
		Element screenElement = root.element("screen");
		if (screenElement == null) {

			return;
		}
		screenElement.addAttribute("password", password);
		saveXml(path);

	}

	public Customer getCustomer() {
		return customer;
	}

	public String getLanguage() {
		return language;
	}

	public String getIpAddress() {
		return ip;
	}

	public ProcedureMode getProcedureMode() {
		return procedureMode;
	}

	public int getMaxContinueAlertCount() {
		return maxContinueAlertCount;
	}

	/**
	 * 获取数据推送 次数和回馈次数差
	 * 
	 * @return
	 */
	public int getMaxPushOffCount() {
		return maxPushOffCount;
	}

	public PowerManagerInfo getPowerManagerInfo() {
		return powerManagerInfo;
	}

	public void setPowerManagerInfo(PowerManagerInfo powerManagerInfo) {
		this.powerManagerInfo = powerManagerInfo;
	}

	public FanManagerInfo getFanManagerInfo() {
		return fanManagerInfo;
	}

	public void setFanManagerInfo(FanManagerInfo fanManagerInfo) {
		this.fanManagerInfo = fanManagerInfo;
	}

	public OMRTemperatureManagerInfo getOMRManagerInfo() {
		return OMRManagerInfo;
	}

	public void setOMRManagerInfo(OMRTemperatureManagerInfo oMRManagerInfo) {
		OMRManagerInfo = oMRManagerInfo;
	}

	public MechanismManagerInfo getMechanismManagerInfo() {
		return mechanismManagerInfo;
	}

	public void setMechanismManagerInfo(MechanismManagerInfo mechanismManagerInfo) {
		this.mechanismManagerInfo = mechanismManagerInfo;
	}

	public AlarmManagerInfo getAlarmManagerInfo() {
		return alarmManagerInfo;
	}

	public void setAlarmManagerInfo(AlarmManagerInfo alarmManagerInfo) {
		this.alarmManagerInfo = alarmManagerInfo;
	}

	public ProbeManagerInfo getProbeManagerInfo() {
		return probeManagerInfo;
	}

	public void setProbeManagerInfo(ProbeManagerInfo probeManagerInfo) {
		this.probeManagerInfo = probeManagerInfo;
	}

	public SmogAlertManagerInfo getSmogAlertManagerInfo() {
		return smogAlertManagerInfo;
	}

	public void setSmogAlertManagerInfo(SmogAlertManagerInfo smogAlertManagerInfo) {
		this.smogAlertManagerInfo = smogAlertManagerInfo;
	}

	public DoorAlertManagerInfo getDoorAlertManagerInfo() {
		return doorAlertManagerInfo;
	}

	public void setDoorAlertManagerInfo(DoorAlertManagerInfo doorAlertManagerInfo) {
		this.doorAlertManagerInfo = doorAlertManagerInfo;
	}

	public BeepAlertManagerInfo getBeepAlertManagerInfo() {
		return beepAlertManagerInfo;
	}

	public void setBeepAlertManagerInfo(BeepAlertManagerInfo beepAlertManagerInfo) {
		this.beepAlertManagerInfo = beepAlertManagerInfo;
	}

	public Precision getPrecision() {
		return precision;
	}

	public Range getRange() {
		return range;
	}

	public boolean isUseCalboardReverseChnIndex() {
		return useCalboardReverseChnIndex;
	}

	public ColorLightManagerInfo getColorLightManagerInfo() {
		return colorLightManagerInfo;
	}

	public boolean isUseDebug() {
		return useDebug;
	}

	public StateLightControllerInfo getStateLightControllerInfo() {
		return stateLightControllerInfo;
	}

	public KBDecimal getKbDecimal() {
		return kbDecimal;
	}

	public boolean isDisableDefaultProtection() {
		return disableDefaultProtection;
	}

	public List<DriverInfo> getDriverInfos() {
		return driverInfos;
	}

	public void setDriverInfos(List<DriverInfo> driverInfos) {
		this.driverInfos = driverInfos;
	}

	public void setUseReverseChnIndex(boolean useReverseChnIndex) {
		this.useReverseChnIndex = useReverseChnIndex;
	}

	public void setUseDebug(boolean useDebug) {
		this.useDebug = useDebug;
	}

	public void setUseStepChangeProtect(boolean useStepChangeProtect) {
		this.useStepChangeProtect = useStepChangeProtect;
	}

	public void setUseSTM32Time(boolean useSTM32Time) {
		this.useSTM32Time = useSTM32Time;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setDisableDefaultProtection(boolean disableDefaultProtection) {
		this.disableDefaultProtection = disableDefaultProtection;
	}

	public BackupBoard getBackupBoard() {
		return backupBoard;
	}

	public LogCfg getLogCfg() {
		return logCfg;
	}

	public ProcedureSupportInfo getProcedureSupportInfo() {
		return procedureSupportInfo;
	}

	public void setProcedureSupportInfo(ProcedureSupportInfo procedureSupportInfo) {
		this.procedureSupportInfo = procedureSupportInfo;
	}

	public DriverInfo getDriverInfo(int n) {

		return this.driverInfos.get(n);
	}

	public List<DriverInfo> listDriverInfos() {

		return this.driverInfos;
	}

	public boolean isUseStepChangeProtect() {
		return useStepChangeProtect;
	}

	public boolean isUseSTM32Time() {
		return useSTM32Time;
	}

	public SmartProtects getSmartProtects() {
		return smartProtects;
	}

	public void setSmartProtects(SmartProtects smartProtects) {
		this.smartProtects = smartProtects;
	}

	public PingController getPingController() {
		return pingController;
	}

	public DriversCfg getDriversCfg() {
		return driversCfg;
	}

	public WorkEnvironment getWorkEnv() {
		return workEnv;
	}

	public void setWorkEnv(WorkEnvironment workEnv) {
		this.workEnv = workEnv;
	}

	public OperationSystem getSystem() {
		return system;
	}

	public void setSystem(OperationSystem system) {
		this.system = system;
	}

	public boolean isUseErrorPick() {
		return useErrorPick;
	}

	public void setUseErrorPick(boolean useErrorPick) {
		this.useErrorPick = useErrorPick;
	}

	public ChannelLightController getChannelLightController() {
		return channelLightController;
	}

	
	
	

}
