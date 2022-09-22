package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;

import com.nltecklib.protocol.fuel.Environment.Code;

/**
 * 主控环境
 * 
 * @author caichao_tang
 *
 */
public class MainEnvironment {

    /**
     * 报警码，用于区分报警类型
     * 
     * @author Administrator
     *
     */
    public enum AlertCode {
	NORMAL(0, "正常"), COMPONENT_EXCEPTION(1, "器件异常"), COMMUNICATION_EXCEPTION(2, "通信异常"), LOGIC_EXCEPTION(3, "逻辑错误"), STATE_ERR(4, "状态异常"), PARAM_ERR(5, "参数错误"), OTHER_ERR(255, "其他异常");

	private int index;
	private String description;

	AlertCode(int index, String description) {
	    this.index = index;
	    this.description = description;
	}

	public int getIndex() {
	    return index;
	}

	public void setIndex(int index) {
	    this.index = index;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public static AlertCode getValue(int index) {
	    for (AlertCode val : AlertCode.values()) {
		if (val.getIndex() == index) {
		    return val;
		}
	    }
	    return null;
	}
    }

    /**
     * 报警信息等级
     * 
     * @author caichao_tang
     *
     */
    public enum AlertLevel {
	NORMAL(0, "正常"), ALERT(1, "报警"), STOP(2, "停机");

	private int index;
	private String description;

	AlertLevel(int index, String description) {
	    this.index = index;
	    this.description = description;
	}

	public int getIndex() {
	    return index;
	}

	public void setIndex(int index) {
	    this.index = index;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public static AlertLevel getValue(int index) {
	    for (AlertLevel val : AlertLevel.values()) {
		if (val.getIndex() == index) {
		    return val;
		}
	    }
	    return null;
	}
    }

    public enum LogCode {

	NORMAL(0, "正常");

	private int code;
	private String context;

	private LogCode(int code, String context) {
	    this.code = code;
	    this.context = context;
	}

	public int getCode() {
	    return code;
	}

	public String getContext() {
	    return context;
	}

	public static LogCode getValue(int code) {
	    for (LogCode val : LogCode.values()) {
		if (val.getCode() == code) {
		    return val;
		}
	    }
	    return null;
	}
    }

    /**
     * 主控PC通信协议功能码定义
     * 
     * @author Administrator
     *
     */
    public enum MainCode implements Code {

	/**
	 * 常量定义
	 */
	SOLENOID_VALUE_CODE(1), TEMPERATURE_CONTROL_CODE(2), TEMPERATURE_PROTECT_CODE(3), DIFFPRESSURE_PROTECT_CODE(4), PRESSURE_PROTECT_CODE(5), FLOW_CONTROL_CODE(6), FLOW_PROTECT_CODE(7), RESERVOIR_SYSTEM_CODE(8), PUMP_CODE(9), VARIABLE_PUMP_CODE(0x0A), PICKUP_CODE(0x0B), ALERT_CODE(0x0C), STOP_PROCESS_CODE(0x0D), WORK_MODE_CODE(0X11), SYSTEM_VERSION_CODE(0X12), PROCESS_CODE(0X14), VOL_CUR_PICKUP_CODE(0X15), LINKAGE_MODE_CODE(0X16), FLOW_MODE_CODE(0X17), ELEC_LOAD_CODE(0X18), STACK_CODE(0X19), HYDROGEN_ALERT_CODE(0X1A), HEART(0x1B), SINGLE_VOL_PROTECT_CODE(0x1C), STACK_VOL_PROTECT_CODE(0x1D), LOWER_ALERT_CODE(0X1E), LOG_CODE(0x1F), DATE_CODE(0x20), PROTECT_CODE(0x21), SET_CODE(0x22), BOARD_LIMIT_CODE(0x23), PID_CODE(0x24), ALERT_CLEAR_CODE(0x25), ELEC_LOAD_CONNECT_CODE(0x26), VOLT_VERSION_INFO_CODE(0x32), VOLT_UPDATE_CODE(0x33), HEAT_UPDATE_CODE(0X34), FLOW_UPDATE_CODE(0X35), CONTROL_UPDATE_CODE(0X36), PROTECT_UPDATE_CODE(0x37),

	TEST_CODE(0x66);

	private int code;

	private MainCode(int funCode) {

	    this.code = funCode;
	}

	@Override
	public int getCode() {
	    return code;
	}

	public static MainCode getCode(int code) {
	    for (MainCode main : MainCode.values()) {
		if (main.getCode() == code) {
		    return main;
		}
	    }
	    return null;
	}

    }

    /**
     * 主控运行模式/指令
     * 
     * @author caichao_tang
     *
     */
    public enum MainWorkMode {
	/**
	 * 待机
	 */
	NONE,
	/**
	 * 测试
	 */
	TEST,
	/**
	 * 校准
	 */
	CAL,
	/**
	 * 手动
	 */
	MANUAL,
	/**
	 * 升级
	 */
	UPDATE,
	/**
	 * 流程停机
	 */
	STOP,
	/**
	 * 流程开机
	 */
	START,
	/**
	 * 主流程
	 */
	RUN,
	/**
	 * 中断流程
	 */
	ABORT;
    }

    public enum LinkageMode {
	NONE, VALVE_FIRST, PUMP_FIRST;
    }

    public enum FlowMode {
	STATIC_FLOW, DYNAMIC_FLOW;
    }

    public enum LoadMode {
	/**
	 * 恒流
	 */
	CC,
	/**
	 * 恒压
	 */
	CV,
	/**
	 * 恒功率
	 */
	CP,
    }

    public enum CompStyle {
	/**
	 * 默认
	 */
	NONE,
	/**
	 * 温控
	 */
	TC,
	/**
	 * 温度显示
	 */
	THC,
	/**
	 * 流量计
	 */
	MFC,
	/**
	 * 流量传感器
	 */
	FE,
	/**
	 * 液位计
	 */
	LI,
	/**
	 * 电磁阀
	 */
	SOV,
	/**
	 * 普通泵
	 */
	PMP,
	/**
	 * 变速泵
	 */
	VPMP,
	/**
	 * 压力传感器
	 */
	PT,
	/**
	 * 储液罐
	 */
	WT,
	/**
	 * 温度开关
	 */
	TS,
	/**
	 * 
	 */
	HV,
    }

    public enum Component {

	NONE(0), MFC_101(101), MFC_111(111), HV145(145), MFC_163(163), MFC_313(313), MFC_354(354), FE_128(128), FE_344(344), TC_397(397), TC_504(504), TC_413(413), TC_433(433), TC_453(453), TC_454(454), TC_692(692), TC_481(481), TC_482(482), TC_483(483), TC_484(484), TC_633(633), TC_414(414), TC_434(434), TC_314(314), TC_315(315), TC_331(331), TC_332(332), TC_333(333), TC_355(355), TC_357(357), TC_381(381), TC_382(382), TC_383(383), TC_399(399), TC_501(501), TC_502(502), TC_661(661), TC_666(666), TC_668(668), TC_911(911), TC_912(912), TC_913(913), TC_914(914), TC_915(915), TC_991(991), PT_411(411), PT_431(431), PT_451(451), PT_452(452), PT_372(372), PT_412(412), PT_432(432), PT_151(151), PT_662(662), SOV_102(102), SOV_112(112), SOV_121(121), SOV_152(152), SOV_153(153), SOV_162(162), SOV_341(341), SOV_455(455), SOV_456(456), SOV_457(457), SOV_458(458), SOV_459(459), SOV_460(460), SOV_667(667), SOV_992(992), SOV_993(993), LI_123(123), LI_124(124), LI_142(142), LI_143(143), WT_125(125), WT_144(144), PMP_122(122), PMP_141(141), PMP_342(342), PMP_130(130), PMP_312(312), PMP_352(352), PMP_665(665), PMP_631(631), SOV_351(351), SOV_311(311), SOV_461(461), TS_916(916), TS_917(917),
	TS_918(918), TC_490(490), TC_317(317), TC_318(318),

	// 保护板报警项
	H2_SENSOR(2001), EMERGENCY_STOP(2002), SMOKE_SENSOR(2003), HARD_WARM_LOCK(2004), HARD_GAS_LOCK(2005), ELEC_LOAD(2006);

	private int number;

	private Component(int number) {
	    this.number = number;
	}

	public int getNumber() {
	    return number;
	}

	public void setNumber(int number) {
	    this.number = number;
	}

	public static Component get(int number) {
	    for (Component c : Component.values()) {
		if (c.number == number) {
		    return c;
		}
	    }
	    return null;
	}

	public CompStyle getStyle() {
	    switch (this) {
	    case FE_128:
	    case FE_344:
		return CompStyle.FE;
	    case LI_123:
	    case LI_124:
	    case LI_142:
	    case LI_143:
		return CompStyle.LI;
	    case MFC_101:
	    case MFC_111:
	    case MFC_163:
	    case MFC_313:
	    case MFC_354:
		return CompStyle.MFC;
	    case PMP_122:
	    case PMP_141:
		return CompStyle.PMP;
	    case PMP_130:
	    case PMP_312:
	    case PMP_342:
	    case PMP_352:
	    case PMP_631:
	    case PMP_665:
		return CompStyle.VPMP;
	    case PT_372:
	    case PT_411:
	    case PT_431:
	    case PT_451:
	    case PT_452:
	    case PT_412:
	    case PT_432:
	    case PT_151:
		// case PT_622:
	    case PT_662:
		return CompStyle.PT;
	    case SOV_102:
	    case SOV_112:
	    case SOV_121:
	    case SOV_152:
	    case SOV_153:
	    case SOV_162:
	    case SOV_311:
	    case SOV_341:
	    case SOV_351:
	    case SOV_455:
	    case SOV_456:
	    case SOV_457:
	    case SOV_458:
	    case SOV_459:
	    case SOV_460:
	    case SOV_461:
	    case SOV_667:
	    case SOV_992:
	    case SOV_993:
		return CompStyle.SOV;
	    case TC_314:
	    case TC_315:
	    case TC_331:
	    case TC_332:
	    case TC_333:
	    case TC_355:
	    case TC_357:
	    case TC_381:
	    case TC_382:
	    case TC_383:
	    case TC_397:
	    case TC_399:
	    case TC_501:
	    case TC_502:
	    case TC_504:
	    case TC_661:
	    case TC_666:
	    case TC_668:
	    case TC_317:
	    case TC_318:
		return CompStyle.TC;
	    case TC_413:
	    case TC_433:
	    case TC_453:
	    case TC_454:
	    case TC_481:
	    case TC_482:
	    case TC_483:
	    case TC_484:
	    case TC_633:
	    case TC_692:
	    case TC_414:
	    case TC_434:
	    case TC_911:
	    case TC_912:
	    case TC_913:
	    case TC_914:
	    case TC_915:
	    case TC_490:
	    case TC_991:
		return CompStyle.THC;
	    case WT_125:
	    case WT_144:
		return CompStyle.WT;
	    case TS_916:
	    case TS_917:
	    case TS_918:
		return CompStyle.TS;
	    case HV145:
		return CompStyle.HV;
	    default:
		return CompStyle.NONE;
	    }
	}

	@Override
	public String toString() {
	    switch (this) {
	    case MFC_101:
		return "H2流量控制";
	    case MFC_111:
		return "CO2流量控制";
	    case MFC_163:
		return "空气流量控制";
	    case MFC_313:
		return "阳极增湿流量控制";
	    case MFC_354:
		return "阴极增湿流量控制";
	    case FE_128:
		return "液体甲醇流量";
	    case FE_344:
		return "去离子水流量";
	    case LI_123:
		return "甲醇罐上液位";
	    case LI_124:
		return "甲醇罐下液位";
	    case LI_142:
		return "去离子水罐上液位";
	    case LI_143:
		return "去离子水罐下液位";
	    case PMP_122:
		return "燃料罐供给泵";
	    case PMP_130:
		return "电池系统燃料泵";
	    case PMP_141:
		return "去离子水罐供给泵";
	    case PMP_312:
		return "阳极增湿供给水泵";
	    case PMP_342:
		return "电池系统供给水泵";
	    case PMP_352:
		return "阴极增湿供给水泵";
	    case PMP_631:
		return "油路散热供给水泵";
	    case PMP_665:
		return "导热油路循环油泵";
	    case PT_151:
		return "氮气供气压力";
	    case PT_372:
		return "电池系统前压";
	    case PT_411:
		return "阳极堆前压力";
	    case PT_412:
		return "阳极堆后压力";
	    case PT_431:
		return "阴极堆前压力";
	    case PT_432:
		return "阴极堆后压力";
	    case PT_451:
		return "油路堆前压力";
	    case PT_452:
		return "油路堆后压力";
	    case SOV_102:
		return "阳极进口电磁阀";
	    case SOV_112:
		return "CO2进口电磁阀";
	    case SOV_121:
		return "燃料供给电磁阀";
	    case SOV_152:
		return "阳极氮气进口电磁阀";
	    case SOV_153:
		return "阴极氮气进口电磁阀";
	    case SOV_162:
		return "阴极进口电磁阀";
	    case SOV_341:
		return "电池系统水进口电磁阀";
	    case SOV_311:
		return "阳极增湿进口电磁阀";
	    case SOV_351:
		return "阴极增湿进口电磁阀";
	    case SOV_455:
		return "油温冷却回路电磁阀";
	    case SOV_456:
		return "油温预热回路电磁阀";
	    case SOV_457:
		return "阳极油进口电磁阀";
	    case SOV_458:
		return "阴极油进口电磁阀";
	    case SOV_459:
		return "备用电磁阀";
	    case SOV_460:
		return "备用电磁阀";
	    case SOV_667:
		return "油路电堆进口电磁阀";
	    case TC_314:
		return "阳极增湿加热温度";
	    case TC_315:
		return "油路电堆进口温度";
	    case TC_331:
		return "阳极混合罐温度";
	    case TC_332:
		return "阳极混合加热温度";
	    case TC_333:
		return "阳极堆前辅热温度";
	    case TC_355:
		return "阴极增湿加热温度";
	    case TC_357:
		return "阴极增湿出口温度";
	    case TC_381:
		return "阴极混合罐温度";
	    case TC_382:
		return "阴极混合加热温度";
	    case TC_383:
		return "阴极堆前辅热温度";
	    case TC_397:
		return "TC397";
	    case TC_399:
		return "TC399";
	    case TC_501:
		return "TC501";
	    case TC_502:
		return "TC502";
	    case TC_504:
		return "TC504";
	    case TC_661:
		return "导热油罐加热温度";
	    case TC_666:
		return "TC666";
	    case TC_668:
		return "电堆进口油路保温";
	    case TC_413:
		return "阳极堆前温度";
	    case TC_414:
		return "阳极堆后温度";
	    case TC_433:
		return "阴极堆前温度";
	    case TC_434:
		return "阴极堆后温度";
	    case TC_453:
		return "电堆前油温";
	    case TC_454:
		return "电堆后油温";
	    case TC_481:
		return "外围辅助测温点1";
	    case TC_482:
		return "外围辅助测温点2";
	    case TC_483:
		return "外围辅助测温点3";
	    case TC_484:
		return "外围辅助测温点4";
	    case TC_633:
		return "换热后油温";
	    case TC_692:
		return "换热后水温";
	    case WT_125:
		return "甲醇罐";
	    case WT_144:
		return "去离子水罐";
	    case SOV_461:
		return "冷却水循环电磁阀";
	    case TC_911:
		return "阳极混合罐二次保护";
	    case TC_912:
		return "阳极增湿器二次保护";
	    case TC_913:
		return "阴极增湿器二次保护";
	    case TC_914:
		return "阴极混合罐二次保护";
	    case TC_915:
		return "油罐二次保护";
	    case TS_916:
		return "温度开关1";
	    case TS_917:
		return "温度开关2";
	    case TS_918:
		return "温度开关3";
	    case TC_490:
		return "虚拟温度开关";
	    case TC_317:
		return "阳极预热温度";
	    case TC_318:
		return "阴极预热温度";
	    case TC_991:
		return "温控TC991";
	    case PT_662:
		return "压力控制pt662";
	    case SOV_992:
		return "电磁阀sov992";
	    case SOV_993:
		return "电磁阀sov993";
	    case H2_SENSOR:
		return "氢气浓度传感器";
	    case EMERGENCY_STOP:
		return "急停按钮";
	    case HARD_WARM_LOCK:
		return "硬件加热死锁";
	    case HARD_GAS_LOCK:
		return "硬件气体死锁";
	    case ELEC_LOAD:
		return "电子负载";
	    default:
		return name();
	    }
	}

	public static Component getComponent(String name) {
	    for (Component c : Component.values()) {
		if (c.toString().equals(name)) {
		    return c;
		}
	    }
	    return null;
	}

	/**
	 * 判断一个器件是否为开关器件
	 * 
	 * @param component
	 * @return
	 */
	public static boolean isSwitchComponent(Component component) {
	    CompStyle compnentStyle = component.getStyle();
	    switch (compnentStyle) {
	    case SOV:
	    case PMP:
	    case TS:
		return true;
	    default:
		return false;
	    }
	}

	/**
	 * 根据器件代号获取器件
	 * 
	 * @param code
	 * @return
	 */
	public static Component getComponentFromCode(String code) {
	    for (Component component : Component.values()) {
		if (code.equals(component.name())) {
		    return component;
		}
	    }
	    return null;
	}

	/**
	 * 获得器件的编号，名称组成的数组
	 * 
	 * @return
	 */
	public static String[] getComponets() {
	    ArrayList<String> componentList = new ArrayList<>();
	    for (int i = 0; i < Component.values().length; i++) {
		componentList.add(Component.values()[i].name() + " " + Component.values()[i].toString());
	    }
	    componentList.remove(0);
	    String[] components = componentList.toArray(new String[0]);
	    return components;
	}

	/**
	 * 根据器件名称组合获得器件
	 * 
	 * @param comboString
	 * @return
	 */
	public static Component getComponetFromComboString(String comboString) {
	    int blankIndex = comboString.indexOf(" ");
	    String componentCode = comboString.substring(0, blankIndex);
	    Component component = Component.getComponentFromCode(componentCode);
	    return component;
	}

	/**
	 * 获得某种类型器件List
	 * 
	 * @param compStyle
	 * @return
	 */
	public static ArrayList<Component> getStyleComponent(CompStyle compStyle) {
	    ArrayList<Component> tempComponentList = new ArrayList<Component>();
	    ArrayList<Component> presureComponentList = new ArrayList<Component>();
	    ArrayList<Component> flowComponentList = new ArrayList<Component>();
	    for (Component component : Component.values()) {
		if (component.getStyle() == CompStyle.TC || component.getStyle() == CompStyle.THC) {
		    tempComponentList.add(component);
		}
		if (component.getStyle() == CompStyle.MFC) {
		    flowComponentList.add(component);
		}
		if (component.getStyle() == CompStyle.PT) {
		    presureComponentList.add(component);
		}
	    }
	    switch (compStyle) {
	    case TC:
		return tempComponentList;
	    case MFC:
		return flowComponentList;
	    case PT:
		return presureComponentList;
	    default:
		return null;
	    }
	}

    }

}
