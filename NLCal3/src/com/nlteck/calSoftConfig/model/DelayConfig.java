package com.nlteck.calSoftConfig.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.nlteck.swtlib.table.TableViewerEx.TableExItem;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.utils.XmlUtil;

public class DelayConfig {

    public DetailConfig defaultConfig = new DetailConfig();
    public List<DetailConfig> detailConfigs = new ArrayList<>();
    private Document document;

    public static class DetailConfig implements Cloneable, TableExItem {
	public CalMode mode;
	public Pole pole;
	public int precision = -1;
	public boolean dotClose;
	public int programSetDelay;
	public int programSetDelayCV2;
	public int moduleOpenDelay;
	public int moduleCloseDelay;
	public int readMeterDelay;
	public int turnOnMeterDelay;
	public int turnOffMeterDelay;
	public int modeChangeDelay;

	@Override
	protected DetailConfig clone() throws CloneNotSupportedException {
	    
	    return (DetailConfig) super.clone();
	}

	@Override
	public void flushItemText(int columnIndex, String text) {
	    
	    switch (columnIndex) {
	    case 0:
		mode = CalMode.valueOf(text);
		break;
	    case 1:
		pole = Pole.valueOf(text);
		break;
	    case 2:
		precision = Integer.parseInt(text);
		break;
	    case 3:
		dotClose = Boolean.parseBoolean(text);
		break;
	    case 4:
		programSetDelay = Integer.parseInt(text);
		break;
	    case 5:
		programSetDelayCV2 = Integer.parseInt(text);
		break;
	    case 6:
		moduleOpenDelay = Integer.parseInt(text);
		break;
	    case 7:
		moduleCloseDelay = Integer.parseInt(text);
		break;
	    case 8:
		readMeterDelay = Integer.parseInt(text);
		break;
	    case 9:
		turnOnMeterDelay = Integer.parseInt(text);
		break;
	    case 10:
		turnOffMeterDelay = Integer.parseInt(text);
		break;
	    case 11:
		modeChangeDelay = Integer.parseInt(text);
		break;
	    default:
		break;
	    }
	}

    }

    public void flush(String filePath) throws Exception {
	Element rootElement = document.getRootElement();
	Element defaultEle = rootElement.element("default");
	defaultEle.setAttributeValue("programSetDelay", this.defaultConfig.programSetDelay + "");
	defaultEle.setAttributeValue("programSetDelayCV2", this.defaultConfig.programSetDelayCV2 + "");
	defaultEle.setAttributeValue("moduleOpenDelay", this.defaultConfig.moduleOpenDelay + "");
	defaultEle.setAttributeValue("moduleCloseDelay", this.defaultConfig.moduleCloseDelay + "");
	defaultEle.setAttributeValue("readMeterDelay", this.defaultConfig.readMeterDelay + "");
	defaultEle.setAttributeValue("turnOnMeterDelay", this.defaultConfig.turnOnMeterDelay + "");
	defaultEle.setAttributeValue("turnOffMeterDelay", this.defaultConfig.turnOffMeterDelay + "");
	defaultEle.setAttributeValue("modeChangeDelay", this.defaultConfig.modeChangeDelay + "");
	defaultEle.setAttributeValue("dotClose", this.defaultConfig.dotClose + "");

	List<Element> detailEleList = rootElement.elements("detail");
	for (Element detailEle : detailEleList) {
	    rootElement.remove(detailEle);
	}
	for (DetailConfig stepSetting : detailConfigs) {
	    Element stepSettingElem = rootElement.addElement("detail");
	    stepSettingElem.addAttribute("mode", stepSetting.mode + "");
	    stepSettingElem.addAttribute("pole", stepSetting.pole + "");
	    stepSettingElem.addAttribute("precision", stepSetting.precision + "");
	    stepSettingElem.addAttribute("dotClose", stepSetting.dotClose + "");
	    stepSettingElem.addAttribute("programSetDelay", stepSetting.programSetDelay + "");
	    stepSettingElem.addAttribute("programSetDelayCV2", stepSetting.programSetDelayCV2 + "");
	    stepSettingElem.addAttribute("moduleOpenDelay", stepSetting.moduleOpenDelay + "");
	    stepSettingElem.addAttribute("moduleCloseDelay", stepSetting.moduleCloseDelay + "");
	    stepSettingElem.addAttribute("readMeterDelay", stepSetting.readMeterDelay + "");
	    stepSettingElem.addAttribute("turnOnMeterDelay", stepSetting.turnOnMeterDelay + "");
	    stepSettingElem.addAttribute("turnOffMeterDelay", stepSetting.turnOffMeterDelay + "");
	    stepSettingElem.addAttribute("modeChangeDelay", stepSetting.modeChangeDelay + "");
	}
	XmlUtil.saveXml(filePath, document);
    }

    public DelayConfig loadDelayConfig(String filePath) throws Exception {
	DelayConfig config = this;
	document = XmlUtil.loadXml(filePath);

	Element rootElement = document.getRootElement();
	Element defaultEle = rootElement.element("default");

	config.defaultConfig.programSetDelay = Integer.parseInt(defaultEle.attributeValue("programSetDelay"));
	config.defaultConfig.programSetDelayCV2 = Integer
		.parseInt(defaultEle.attributeValue("programSetDelayCV2") == null ? "2000"
			: defaultEle.attributeValue("programSetDelayCV2"));
	config.defaultConfig.moduleOpenDelay = Integer.parseInt(defaultEle.attributeValue("moduleOpenDelay"));
	config.defaultConfig.moduleCloseDelay = Integer.parseInt(defaultEle.attributeValue("moduleCloseDelay"));
	config.defaultConfig.readMeterDelay = Integer.parseInt(defaultEle.attributeValue("readMeterDelay"));
	String val = defaultEle.attributeValue("turnOnMeterDelay");
	if (val != null) {
	    config.defaultConfig.turnOnMeterDelay = Integer.parseInt(defaultEle.attributeValue("turnOnMeterDelay"));
	    config.defaultConfig.turnOffMeterDelay = Integer.parseInt(defaultEle.attributeValue("turnOffMeterDelay"));
	} else {
	    config.defaultConfig.turnOnMeterDelay = Integer.parseInt(defaultEle.attributeValue("switchMeterDelay"));
	    config.defaultConfig.turnOffMeterDelay = Integer.parseInt(defaultEle.attributeValue("switchMeterDelay"));
	}
	config.defaultConfig.modeChangeDelay = Integer.parseInt(defaultEle.attributeValue("modeChangeDelay"));
	config.defaultConfig.dotClose = Boolean.parseBoolean(defaultEle.attributeValue("dotClose"));

	List<Element> detailEleList = rootElement.elements("detail");

	for (Element detailEle : detailEleList) {
	    DetailConfig dc = config.defaultConfig.clone();
	    dc.pole = Pole.POSITIVE;
	    // 模式
	    val = detailEle.attributeValue("mode");
	    if (val != null) {
		dc.mode = CalMode.valueOf(val);
	    }
	    // 极性
	    val = detailEle.attributeValue("pole");
	    if (val != null) {
		dc.pole = Pole.valueOf(val);
	    }
	    // 精度
	    val = detailEle.attributeValue("precision");
	    if (val != null) {
		dc.precision = Integer.parseInt(val);
	    }
	    // 关闭
	    val = detailEle.attributeValue("dotClose");
	    if (val != null) {
		dc.dotClose = Boolean.parseBoolean(val);
	    }
	    // 模式切换
	    val = detailEle.attributeValue("modeChangeDelay");
	    if (val != null) {
		dc.modeChangeDelay = Integer.parseInt(val);
	    }
	    // 程控之后开膜片
	    val = detailEle.attributeValue("programSetDelay");
	    if (val != null) {
		dc.programSetDelay = Integer.parseInt(val);
	    }
	    // 膜片之后读取
	    val = detailEle.attributeValue("moduleOpenDelay");
	    if (val != null) {
		dc.moduleOpenDelay = Integer.parseInt(val);
	    }
	    // 读取之后读表
	    val = detailEle.attributeValue("readMeterDelay");
	    if (val != null) {
		dc.readMeterDelay = Integer.parseInt(val);
	    }
	    // 开表后延时
	    val = detailEle.attributeValue("turnOnMeterDelay");
	    if (val != null) {
		dc.turnOnMeterDelay = Integer.parseInt(val);
	    }
	    // 关表后延时
	    val = detailEle.attributeValue("turnOffMeterDelay");
	    if (val != null) {
		dc.turnOffMeterDelay = Integer.parseInt(val);
	    }

	    config.detailConfigs.add(dc);
	}
	return config;
    }

}
