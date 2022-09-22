package com.nlteck.calModel.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.nlteck.model.TestDot;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.utils.XmlUtil;
//2022.4.11 xingguo_wang 从3代校准主控复制 添加构造方法
public class DelayConfig {
	public Logger logger;
	public DetailConfig defaultConfig = new DetailConfig();
	public List<DetailConfig> detailConfigs = new ArrayList<>();

	public DelayConfig() {
		
	}
	public DelayConfig(String path){
		try {
			loadDelayConfig(path);
		} catch (Exception e) {
			logger.error("loadDelay error:" + e.getMessage(), e);
		}
	}
	
	public static class DetailConfig implements Cloneable {
		public CalMode mode;
		public Pole pole;
		public int precision = -1;
		public boolean dotClose;
		public int programSetDelay;
		public int programSetDelayCV2; //当CV2打开校准时，特殊延时时间ms
		public int moduleOpenDelay;
		public int moduleCloseDelay;
		public int readMeterDelay;
		public int turnOnMeterDelay;
		public int turnOffMeterDelay;
		public int modeChangeDelay;

		@Override
		protected DetailConfig clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return (DetailConfig) super.clone();
		}

	}

	/**
	 * 通过测试点查找延时
	 * 
	 * @param dot
	 * @return
	 */
	public DetailConfig findDelay(TestDot dot) {
		for (DetailConfig detailConfig : detailConfigs) {
			if (detailConfig.mode != null && detailConfig.mode != dot.getMode()) {
				continue;
			}

			if (detailConfig.pole != null && detailConfig.pole != dot.getPole()) {
				continue;
			}

			if (detailConfig.precision != -1 && detailConfig.precision != dot.getPrecision()) {
				continue;
			}
			return detailConfig;
		}
		return defaultConfig;
	}

	/**
	 * 上位机初始化
	 */
	public static DelayConfig initDelayConfig() {
		DelayConfig config=new DelayConfig();
		config.defaultConfig.programSetDelay=750;
		return config;
	}
	
	
	/**
	 * 从配置文件载入，同时处理默认值
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static DelayConfig loadDelayConfig(String path) throws Exception {
		DelayConfig config = new DelayConfig();
		Document doc = XmlUtil.loadXml(path);

		Element rootElement = (Element) doc.getRootElement();
		Element defaultEle = rootElement.element("default");
		
		config.defaultConfig.programSetDelay = Integer.parseInt(defaultEle.attributeValue("programSetDelay"));
		config.defaultConfig.programSetDelayCV2 = Integer.parseInt(defaultEle.attributeValue("programSetDelayCV2") == null ? "2000" : defaultEle.attributeValue("programSetDelayCV2"));
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
