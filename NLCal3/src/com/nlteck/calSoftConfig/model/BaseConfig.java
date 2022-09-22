package com.nlteck.calSoftConfig.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.nlteck.swtlib.table.TableViewerEx.TableExItem;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.utils.XmlUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午8:20:49 配置管理器
 */
public class BaseConfig {

    public final static String PATH = "temp/base.xml";

    private Document document;

//	public boolean virtual; // 是否启用虚拟机

    public List<Port> ports = new ArrayList<>(); // 串口配置

    public boolean debug;
    public Base base = new Base();
    public ProtocolParam protocol = new ProtocolParam();
    public I18nConfig i18n = new I18nConfig();
    public VirtualParam virtual = new VirtualParam();
    public List<Network> networks = new ArrayList<>(); // 网口配置
    public DeviceParam device = new DeviceParam();
    public int calChnCount;// 校准板通道数
    public boolean downFirstDotOnly; // 只下发每个模式每个量程的第一个点
    public List<CalBoardParam> calboards = new ArrayList<>(); // 校准板配置
    public List<IOParam> meters = new ArrayList<>(); // 网络万用表配置
    public IOParam screen = new IOParam(); // 液晶屏配置
    public Match match = new Match();
//	public CV cv = new CV();
//	public DcModuleOpenDelay dcModuleOpenDelay = new DcModuleOpenDelay();
    public AdjustParam adjustParam = new AdjustParam();
    // 计量偏差新偏差配置
    public List<CalculateValidate> calculateValidates = new ArrayList<>();
    // KB值重新调整
    public KBAdjust kbAdjust = new KBAdjust();

    /**
     * 通道端口类型
     * 
     * @author wavy_zheng 2020年10月27日
     *
     */
    public enum CommType {
	NETWORK, SERIAL;
    }

    public enum EquipType {
	TempBox, // 恒温箱，整版对接
	PowerCab, // 电源柜，俩俩对接
	PingBed,// 针床，分区对接
    }

    public class VirtualParam {
	public boolean use;
	public int driverCount;// 虚拟驱动板数量
	public int driverChnCount;// 虚拟驱动板通道数量
	public List<BoardParam> driverboards = new ArrayList<>(); // 逻辑板虚拟配置

    }

    /**
     * 计量偏差判定配置
     * 
     * @author wavy_zheng 2022年4月28日
     *
     */
    public static class CalculateValidate implements TableExItem {

	public double min;
	public double max;
	public double adcOffset;
	public double meterOffset;

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		min = Double.parseDouble(text);
		break;
	    case 1:
		max = Double.parseDouble(text);
		break;
	    case 2:
		adcOffset = Double.parseDouble(text);
		break;
	    case 3:
		meterOffset = Double.parseDouble(text);
		break;

	    default:
		break;
	    }
	}

    }

    /**
     * 计量失败后是否调整B值
     * 
     * @author wavy_zheng 2022年4月29日
     *
     */
    public class KBAdjust {

	public boolean enable;
	public int count = 1; // 默认重校准1次

    }

    public class AdjustParam {
	public boolean use;
	public List<AdcAdjust> adcAdjusts = new ArrayList<>();
    }

    /**
     * ADC偏差校正
     * 
     * @author guofang_ma
     *
     */
    public static class AdcAdjust implements TableExItem {

	public boolean logic; // 逻辑板 or 回检板?
	public DriverEnvironment.CalMode mode;
	public DriverEnvironment.Pole pole;
	public double threshold;
	public double div;
	public int level;

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		logic = text == "主芯片";
		break;
	    case 1:
		mode = CalMode.valueOf(text);
		break;
	    case 2:
		pole = text == "POSITIVE" ? Pole.POSITIVE : Pole.NEGTIVE;
		break;
	    case 3:
		threshold = Double.parseDouble(text);
		break;
	    case 4:
		div = Double.parseDouble(text);
		break;
	    case 5:
		level = Integer.parseInt(text);
		break;
	    default:
		break;
	    }
	}
    }

    public class ProtocolParam {
	public boolean print;
	public boolean chnOrderResverse;
	public int resistanceResolution;
	public int voltageResolution;
	public int currentResolution;
	public int programKResolution;
	public int programBResolution;
	public int adcKResolution;
	public int adcBResolution;
	public int moduleCount = 2; // 模片默认数量为2
    }

    public class Base {

	public boolean ignoreCV2;// 忽略cv2计量判断
	public boolean calCV2;// 启用cv2校准
	public int stopMode = 0;// 停止方式
	public boolean calCheckBoard;// 启用回检板校准
	public boolean calCheckOnly;
	public EquipType equipType;
	public int driverChnCount = 8; // 驱动板通道个数
	public boolean useRecal; // 是否启用重新取点校准
	public boolean measureNeedClose = true; // 计量每个点都需要关闭模片使能?

    }

    public class I18nConfig {
	public String language;// 语言
	public String charsetName;// 文件编码

    }

    public class DcModuleOpenDelay {
	public boolean use;
	public Map<Integer, DcModultOpenDelayItem> delayMap = new HashMap<>();
    }

    public class DcModultOpenDelayItem {
	public int level;
	public int time;
    }

    public class CV {
	public boolean use;
	public double insertProgramV;
	public double insertMeasureVal;
    }

    public static class Port implements TableExItem {

	public int index;
	public String name;
	public int baudrate;
	public boolean disabled;

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		index = Integer.parseInt(text);
		break;
	    case 1:
		name = text;
		break;
	    case 2:
		baudrate = Integer.parseInt(text);
		break;
	    case 3:
		disabled = Boolean.parseBoolean(text);
		break;
	    default:
		break;
	    }
	}
    }

    public static class Network implements TableExItem {

	public int index;
	public String ip;
	public int port;
	public boolean disabled;
	public boolean async; // 异步通信?

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		index = Integer.parseInt(text);
		break;
	    case 1:
		ip = text;
		break;
	    case 2:
		port = Integer.parseInt(text);
		break;
	    case 3:
		disabled = Boolean.parseBoolean(text);
		break;
	    case 4:
		async = Boolean.parseBoolean(text);
		break;
	    default:
		break;
	    }
	}
    }

    public class Match {
	public int time;
	public int matchDelay;
	public double staticMaxVolt;
	public int voltOffset;// 偏差
	public List<List<Double>> matchCals = new ArrayList<>();// 对接配置 校准板/对接点
    }

    public class BoardParam {
	public int index;
	public boolean disabled;

    }

    public class DeviceParam {

	public CommType commType;
	public int commIndex;

    }

    public static class CalBoardParam implements TableExItem {
	public int index;
	public CommType commType;
	public int commIndex;
	public int meterIndex;
	public boolean disabled;

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		index = Integer.parseInt(text);
		break;
	    case 1:
		commType = CommType.valueOf(text);
		break;
	    case 2:
		commIndex = Integer.parseInt(text);
		break;
	    case 3:
		meterIndex = Integer.parseInt(text);
		break;
	    case 4:
		disabled = Boolean.parseBoolean(text);
		break;
	    default:
		break;
	    }
	}
    }

    public static class IOParam implements TableExItem {
	public int index;
	public CommType commType;
	public int commIndex;
	public boolean disabled;

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		index = Integer.parseInt(text);
		break;
	    case 1:
		commType = CommType.valueOf(text);
		break;
	    case 2:
		commIndex = Integer.parseInt(text);
		break;
	    case 3:
		disabled = Boolean.parseBoolean(text);
		break;
	    default:
		break;
	    }
	}
    }

    /**
     * 加载配置文件base.xml
     * 
     * @author wavy_zheng 2020年10月27日
     * @throws Exception
     */
    public void loadDocument(String path) throws Exception {

	document = XmlUtil.loadXml(path);
	Element root = document.getRootElement();

	if (root.attributeValue("debug") != null) {
	    debug = Boolean.parseBoolean(root.attributeValue("debug"));
	}

	Element i18nElement = root.element("i18n");
	i18n.language = i18nElement.attributeValue("language");
	i18n.charsetName = i18nElement.attributeValue("charsetName");

	Element virtualElement = root.element("virtual");
	if (virtualElement != null) {
	    virtual.use = Boolean.parseBoolean(virtualElement.attributeValue("use"));
	    virtual.driverCount = Integer.parseInt(virtualElement.attributeValue("logicDriverCount"));
	    virtual.driverChnCount = Integer.parseInt(virtualElement.attributeValue("driverChnCount"));
	    virtual.driverboards.clear();
	    Element driversElement = virtualElement.element("driverboards");
	    List<Element> driverListElement = driversElement.elements("driverboard");
	    for (Element driverEle : driverListElement) {

		BoardParam bp = new BoardParam();
		bp.index = Integer.parseInt(driverEle.attributeValue("index"));
		bp.disabled = Boolean.parseBoolean(driverEle.attributeValue("disabled"));
		virtual.driverboards.add(bp);
	    }
	}

	Element baseElement = root.element("base");
	base.ignoreCV2 = Boolean.parseBoolean(baseElement.attributeValue("ignoreCV2"));
	base.calCV2 = Boolean.parseBoolean(baseElement.attributeValue("calCV2"));
	if (baseElement.attributeValue("stopMode") != null) {
	    base.stopMode = Integer.parseInt(baseElement.attributeValue("stopMode"));
	}
	base.calCheckBoard = Boolean.parseBoolean(baseElement.attributeValue("calCheckBoard"));
	base.calCheckOnly = Boolean.parseBoolean(baseElement.attributeValue("calCheckOnly"));
	base.equipType = EquipType.valueOf(baseElement.attributeValue("equipType"));
	if (baseElement.attributeValue("driverChnCount") != null) {

	    base.driverChnCount = Integer.parseInt(baseElement.attributeValue("driverChnCount"));
	}

	if (baseElement.attributeValue("useRecal") != null) {

	    base.useRecal = Boolean.parseBoolean(baseElement.attributeValue("useRecal"));
	}

	if (baseElement.attributeValue("measureNeedClose") != null) {

	    base.measureNeedClose = Boolean.parseBoolean(baseElement.attributeValue("measureNeedClose"));
	}

	Element protocolElement = root.element("protocol");
	protocol.print = Boolean.parseBoolean(protocolElement.attributeValue("print"));
	protocol.chnOrderResverse = Boolean.parseBoolean(protocolElement.attributeValue("chnOrderResverse"));
	protocol.resistanceResolution = Integer.parseInt(protocolElement.attributeValue("resistanceResolution"));
	protocol.voltageResolution = Integer.parseInt(protocolElement.attributeValue("voltageResolution"));
	protocol.currentResolution = Integer.parseInt(protocolElement.attributeValue("currentResolution"));
	protocol.programKResolution = Integer.parseInt(protocolElement.attributeValue("programKResolution"));
	protocol.programBResolution = Integer.parseInt(protocolElement.attributeValue("programBResolution"));
	protocol.adcKResolution = Integer.parseInt(protocolElement.attributeValue("adcKResolution"));
	protocol.adcBResolution = Integer.parseInt(protocolElement.attributeValue("adcBResolution"));
	if (protocolElement.attributeValue("moduleCount") != null) {

	    protocol.moduleCount = Integer.parseInt(protocolElement.attributeValue("moduleCount"));
	}

	// 加载串口配置
	Element portsElement = root.element("ports");
	List<Element> portListElement = portsElement.elements("port");
	ports.clear();
	for (Element ele : portListElement) {

	    Port p = new Port();
	    p.index = Integer.parseInt(ele.attributeValue("index"));
	    p.name = ele.attributeValue("name");
	    p.baudrate = Integer.parseInt(ele.attributeValue("baudrate"));
	    p.disabled = Boolean.parseBoolean(ele.attributeValue("disabled"));
	    ports.add(p);

	}
	networks.clear();
	// 加载网口配置
	Element networksElement = root.element("networks");
	List<Element> networkListElement = networksElement.elements("network");
	for (Element ele : networkListElement) {

	    Network network = new Network();
	    network.index = Integer.parseInt(ele.attributeValue("index"));
	    network.ip = ele.attributeValue("ip");
	    network.port = Integer.parseInt(ele.attributeValue("port"));
	    network.disabled = Boolean.parseBoolean(ele.attributeValue("disabled"));
	    network.async = Boolean.parseBoolean(ele.attributeValue("async"));
	    networks.add(network);

	}

	List<Element> deviceElements = root.elements("device");

	for (Element deviceEle : deviceElements) {

	    device.commIndex = Integer.parseInt(deviceEle.attributeValue("commIndex"));
	    device.commType = CommType.valueOf(deviceEle.attributeValue("commType"));

	}

	// 加载校准板配置
	calboards.clear();
	Element calsElement = root.element("calboards");
	calChnCount = Integer.parseInt(calsElement.attributeValue("calChnCount"));

	String firstDownload = calsElement.attributeValue("downFirstDotOnly");
	if (firstDownload != null) {

	    downFirstDotOnly = Boolean.parseBoolean("downFirstDotOnly");
	}

	List<Element> calListElement = calsElement.elements("calboard");
	for (Element ele : calListElement) {

	    CalBoardParam cb = new CalBoardParam();
	    cb.index = Integer.parseInt(ele.attributeValue("index"));
	    cb.commIndex = Integer.parseInt(ele.attributeValue("commIndex"));
	    cb.commType = CommType.valueOf(ele.attributeValue("commType"));
	    cb.meterIndex = Integer.parseInt(ele.attributeValue("meterIndex"));
	    cb.disabled = Boolean.parseBoolean(ele.attributeValue("disabled"));
	    calboards.add(cb);
	}
	// 加载万用表配置
	meters.clear();
	Element metersElement = root.element("meters");
	List<Element> meterListElement = metersElement.elements("meter");
	for (Element meterElement : meterListElement) {
	    meters.add(loadIOParam(meterElement));
	}

	// 对接
	Element matchElement = root.element("match");
	match.time = Integer.parseInt(matchElement.attributeValue("time"));
	match.matchDelay = Integer.parseInt(matchElement.attributeValue("matchDelay"));
	match.staticMaxVolt = Integer.parseInt(matchElement.attributeValue("staticMaxVolt"));
	match.voltOffset = Integer.parseInt(matchElement.attributeValue("voltOffset"));
	match.matchCals.clear();
	List<Element> matchCalEles = matchElement.elements("calboard");
	for (Element me : matchCalEles) {
	    List<Double> dots = new ArrayList<>();
	    List<Element> dotEles = me.elements("dot");
	    for (Element dotEle : dotEles) {
		dots.add(Double.parseDouble(dotEle.attributeValue("matchVolt")));
	    }
	    match.matchCals.add(dots);
	}
	// 加载液晶屏
	Element screenElement = root.element("screen");
	screen = loadIOParam(screenElement);

	// 加载cv调整
//		Element cvElement = root.element("cv");
//		cv.use = Boolean.parseBoolean(cvElement.attributeValue("use"));
//		cv.insertProgramV = Double.parseDouble(cvElement.attributeValue("insertProgramV"));
//		cv.insertMeasureVal = Double.parseDouble(cvElement.attributeValue("insertMeasureVal"));

	// 加载dc模式高精膜片切换
//		Element dcModeElement = root.element("dcModuleOpenDelay");
//		dcModuleOpenDelay.use = Boolean.parseBoolean(dcModeElement.attributeValue("use"));
//		List<Element> delays = dcModeElement.elements("delay");
//		for (Element delayEle : delays) {
//			DcModultOpenDelayItem item = new DcModultOpenDelayItem();
//			item.level = Integer.parseInt(delayEle.attributeValue("level"));
//			item.time = Integer.parseInt(delayEle.attributeValue("time"));
//			dcModuleOpenDelay.delayMap.put(item.level, item);
//		}

	// 加载ADC校正系数
	Element adcAdjustElement = root.element("adcAdjust");
	if (adcAdjustElement != null) {

	    adjustParam.use = Boolean.parseBoolean(adcAdjustElement.attributeValue("use"));
	    adjustParam.adcAdjusts.clear();
	    List<Element> adjustElements = adcAdjustElement.elements();
	    for (Element element : adjustElements) {

		AdcAdjust adjust = new AdcAdjust();

		adjust.logic = element.getName().equals("adjust");
		adjust.mode = DriverEnvironment.CalMode.valueOf(element.attributeValue("mode"));
		adjust.pole = DriverEnvironment.Pole.valueOf(element.attributeValue("pole"));
		adjust.level = Integer.parseInt(element.attributeValue("level"));
		adjust.threshold = Double.parseDouble(element.attributeValue("threshold"));
		adjust.div = Double.parseDouble(element.attributeValue("div"));
		adjustParam.adcAdjusts.add(adjust);
	    }

//			 System.out.println(adcAdjusts);

	}
	calculateValidates.clear();
	Element calculateValidatesElement = root.element("calculateValidates");
	if (calculateValidatesElement != null) {

	    List<Element> validateElements = calculateValidatesElement.elements();
	    for (Element element : validateElements) {

		CalculateValidate validate = new CalculateValidate();
		validate.min = Double.parseDouble(element.attributeValue("min"));
		validate.max = Double.parseDouble(element.attributeValue("max"));
		validate.adcOffset = Double.parseDouble(element.attributeValue("adcOffset"));
		validate.meterOffset = Double.parseDouble(element.attributeValue("meterOffset"));

		calculateValidates.add(validate);

	    }

	}

	Element kbAdjustElement = root.element("kbAdjust");
	if (kbAdjustElement != null) {

	    kbAdjust.enable = Boolean.parseBoolean(kbAdjustElement.attributeValue("enable"));
	    kbAdjust.count = Integer.parseInt(kbAdjustElement.attributeValue("count"));
	}

    }

    public IOParam loadIOParam(Element ele) {
	IOParam ioParam = new IOParam();
	ioParam.index = Integer.parseInt(ele.attributeValue("index"));
	ioParam.commIndex = Integer.parseInt(ele.attributeValue("commIndex"));
	ioParam.commType = CommType.valueOf(ele.attributeValue("commType"));
	ioParam.disabled = Boolean.parseBoolean(ele.attributeValue("disabled"));
	return ioParam;
    }

    public void flush(String path) throws Exception {

	if (document == null) {
	    loadDocument(path);
	}
	Element root = document.getRootElement();

	// 基础参数
	Element baseElement = root.element("base");
	baseElement.setAttributeValue("ignoreCV2", base.ignoreCV2 + "");
	baseElement.setAttributeValue("calCV2", base.calCV2 + "");
	baseElement.setAttributeValue("stopMode", base.stopMode + "");
	baseElement.setAttributeValue("calCheckBoard", base.calCheckBoard + "");
	baseElement.setAttributeValue("calCheckOnly", base.calCheckOnly + "");
	baseElement.setAttributeValue("calCheckOnly", base.calCheckOnly + "");
	baseElement.setAttributeValue("equipType", base.equipType + "");
	baseElement.setAttributeValue("useRecal", base.useRecal + "");
	baseElement.setAttributeValue("measureNeedClose", base.measureNeedClose + "");
	baseElement.setAttributeValue("driverChnCount", base.driverChnCount + "");
//		baseElement.addAttribute("calibrateTerminal", base.calibrateTerminal.name());

	// 协议参数
	Element protocolElement = root.element("protocol");
	protocolElement.setAttributeValue("print", protocol.print + "");
	protocolElement.setAttributeValue("chnOrderResverse", protocol.chnOrderResverse + "");
	protocolElement.setAttributeValue("moduleCount", protocol.moduleCount + "");

	// 串口参数
	Element portsElement = root.element("ports");
	portsElement.clearContent();
	for (Port serialPort : ports) {
	    Element element = portsElement.addElement("port");
	    element.addAttribute("index", serialPort.index + "");
	    element.addAttribute("name", serialPort.name);
	    element.addAttribute("baudrate", serialPort.baudrate + "");
	    element.addAttribute("disabled", serialPort.disabled + "");
	}

	// 加载网口配置
	Element networksElement = root.element("networks");
	networksElement.clearContent();
	for (Network network : networks) {
	    Element element = networksElement.addElement("network");
	    element.addAttribute("index", network.index + "");
	    element.addAttribute("ip", network.ip);
	    element.addAttribute("port", network.port + "");
	    element.addAttribute("disabled", network.disabled + "");
	    element.addAttribute("async", network.async + "");
	}
//	List<Element> networkListElement = networksElement.elements("network");
//	for (Element ele : networkListElement) {
//	    int index = Integer.parseInt(ele.attributeValue("index"));
//	    Network nw = networks.stream().filter(x -> x.index == index).findAny().get();
//	    ele.addAttribute("ip", nw.ip);
//	}

	List<Element> deviceElements = root.elements("device");

	for (Element deviceEle : deviceElements) {

	}

	// 加载校准板配置
	Element calsElement = root.element("calboards");
	calsElement.setAttributeValue("calChnCount", calChnCount + "");
	calsElement.setAttributeValue("downFirstDotOnly", downFirstDotOnly + "");
	calsElement.clearContent();
	for (CalBoardParam calBoard : calboards) {
	    Element element = calsElement.addElement("calboard");
	    element.addAttribute("index", calBoard.index + "");
	    element.addAttribute("commType", calBoard.commType.toString());
	    element.addAttribute("commIndex", calBoard.commIndex + "");
	    element.addAttribute("meterIndex", calBoard.meterIndex + "");
	    element.addAttribute("disabled", calBoard.disabled + "");
	}
//	List<Element> calListElement = calsElement.elements("calboard");
//	for (Element ele : calListElement) {
//	    int index = Integer.parseInt(ele.attributeValue("index"));
//	    CalBoardParam cb = calboards.stream().filter(x -> x.index == index).findAny().get();
//	    ele.addAttribute("disabled", cb.disabled + "");
//	}

	// 加载万用表配置
	Element metersElement = root.element("meters");
	metersElement.clearContent();
	for (IOParam meter : meters) {
	    Element element = metersElement.addElement("meter");
	    element.addAttribute("index", meter.index + "");
	    element.addAttribute("commType", meter.commType.toString());
	    element.addAttribute("commIndex", meter.commIndex + "");
	    element.addAttribute("disabled", meter.disabled + "");
	}
//	List<Element> meterListElement = metersElement.elements("meter");
//	for (Element ele : meterListElement) {
//	    int index = Integer.parseInt(ele.attributeValue("index"));
//	    IOParam io = meters.stream().filter(x -> x.index == index).findAny().get();
//	    saveIOParam(ele, io);
//	}

	// 加载液晶屏
	Element screenElement = root.element("screen");
	saveIOParam(screenElement, screen);

	// 调整ADC偏差
	Element adcAdjustElement = root.element("adcAdjust");
	adcAdjustElement.clearContent();
	for (AdcAdjust adcAdjust : adjustParam.adcAdjusts) {
	    String elemName = adcAdjust.logic ? "adjust" : "adjustCheck";
	    Element subElement = adcAdjustElement.addElement(elemName);
	    subElement.addAttribute("mode", adcAdjust.mode.toString());
	    subElement.addAttribute("pole", adcAdjust.pole.toString());
	    subElement.addAttribute("level", adcAdjust.level + "");
	    subElement.addAttribute("threshold", adcAdjust.threshold + "");
	    subElement.addAttribute("div", adcAdjust.div + "");
	}

	// 计量偏差上限
	Element calculateValidateElem = root.element("calculateValidates");
	if (calculateValidateElem == null) {
	    calculateValidateElem = root.addElement("calculateValidates");
	}
	calculateValidateElem.clearContent();
	for (CalculateValidate calculateValidate : calculateValidates) {
	    Element element = calculateValidateElem.addElement("validate");
	    element.addAttribute("min", calculateValidate.min + "");
	    element.addAttribute("max", calculateValidate.max + "");
	    element.addAttribute("adcOffset", calculateValidate.adcOffset + "");
	    element.addAttribute("meterOffset", calculateValidate.meterOffset + "");
	}

	// 计量失败时, 调整KB重试
	Element kbAdjustElem = root.element("kbAdjust");
	if (kbAdjustElem == null) {
	    kbAdjustElem = root.addElement("kbAdjust");
	}
	kbAdjustElem.setAttributeValue("enable", kbAdjust.enable + "");
	kbAdjustElem.setAttributeValue("count", kbAdjust.count + "");

	XmlUtil.saveXml(path, document);
    }

    private void saveIOParam(Element ele, IOParam io) {
	ele.addAttribute("disabled", io.disabled + "");
    }

    public void loadDocument() throws Exception {
	loadDocument(PATH);
    }

    public void flush() throws Exception {
	flush(PATH);
    }
}