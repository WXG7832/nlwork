package com.nlteck.base;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.Element;

import com.nlteck.fireware.CalibrateCore;
import com.nlteck.model.DelayConfig;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.DelayData;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.utils.XmlUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月27日 下午7:38:47 校准配置管理器
 */
public class CalCfgManager {

	private Document document;

	private static final String PATH = "config/calibrate.xml";

	public boolean needMatch; // 是否使用基准电压匹配?

	public CalibratePlanData calibratePlanData;// 校准方案
//	public DelayData delayData;// 延时配置
	public DelayConfig delayConfig;// 延时配置
	public CalculatePlanData calculatePlanData;// 计量方案
	public RangeCurrentPrecisionData rangeCurrentPrecisionData;// 精度档位
	public SteadyCfgData steadyCfgData;// ADC稳定度检测配置

	private CalibrateCore core;

	public CalCfgManager(CalibrateCore core) {
		this.core = core;
		init();
	}

	private void init() {
		calibratePlanData = core.getDiskService().loadCalibratePlan();
//		delayData = core.getDiskService().loadDelay();
		delayConfig = core.getDiskService().loadDelay2();
		calculatePlanData = core.getDiskService().loadCalculatePlan();
		rangeCurrentPrecisionData = core.getDiskService().loadRangeCurrentPrecision();
		steadyCfgData = core.getDiskService().loadSteadyCfg();
	}

	/**
	 * 校准前检查
	 * 
	 * @throws Exception
	 */
	public void checkCfg() throws Exception {
		if (calibratePlanData == null) {
			throw new Exception(I18N.getVal(I18N.CalibratePlanDataNotExist));
		}
		if (calculatePlanData == null) {
			throw new Exception(I18N.getVal(I18N.RangeCurrentPrecisionDataNotExist));
		}
		if (delayConfig == null) {
			throw new Exception(I18N.getVal(I18N.DelayDataNotExist));
		}
		if (rangeCurrentPrecisionData == null) {
			throw new Exception(I18N.getVal(I18N.RangeCurrentPrecisionDataNotExist));
		}
		if (steadyCfgData == null) {
			throw new Exception(I18N.getVal(I18N.SteadyCfgDataNotExist));
		}

	}

	/**
	 * ADC稳定度测试配置
	 * 
	 * @author wavy_zheng 2020年10月27日
	 *
	 */
	public class Steady {

		public int adcCount;
		public int maxSigma;
		public int trailCount; // 头尾去除个数
	}

	/**
	 * 加载校准参数文件
	 * 
	 * @author wavy_zheng 2020年10月27日
	 * @throws Exception
	 */
	public void loadDocument() throws Exception {

		document = XmlUtil.loadXml(PATH);
		Element root = document.getRootElement();
		needMatch = Boolean.parseBoolean(root.attributeValue("needMatch"));

	}

	/**
	 * 校准配置
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void cfgCalibratePlan(CalibratePlanData data) throws IOException {
		// TODO Auto-generated method stub
		calibratePlanData = data;
		core.getDiskService().saveCalibratePlan(calibratePlanData);
	}

	/**
	 * 延时设置
	 * 
	 * @param data
	 * @throws IOException
	 */
//	public void cfgDelay(DelayData data) throws IOException {
//		delayData = data;
//		core.getDiskService().saveDelay(delayData);
//	}

	/**
	 * ADC稳定度检测配置
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void cfgSteadyCfg(SteadyCfgData data) throws IOException {
		steadyCfgData = data;
		core.getDiskService().saveSteadyCfg(steadyCfgData);
	}

	/**
	 * 精度档位
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void cfgRangeCurrentPrecision(RangeCurrentPrecisionData data) throws IOException {
		rangeCurrentPrecisionData = data;
		core.getDiskService().saveRangeCurrentPrecision(rangeCurrentPrecisionData);

	}

	/**
	 * 计量方案
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void cfgCalculatePlan(CalculatePlanData data) throws IOException {

		if (calculatePlanData == null) {
			calculatePlanData = data;
		} else {
			// 不操作精度
			calculatePlanData.setModes(data.getModes());
		}
		core.getDiskService().saveCalculatePlan(calculatePlanData);

	}

//	public void qryDelay(DelayData data) throws Exception {
//		if (delayData == null) {
//			throw new Exception(I18N.getVal(I18N.DelayDataNotExist));
//		}
//		data.setModuleOpenDelay(delayData.getModuleOpenDelay());
//		data.setModuleCloseDelay(delayData.getModuleCloseDelay());
//		data.setModeSwitchDelay(delayData.getModeSwitchDelay());
//		data.setProgramSetDelay(delayData.getProgramSetDelay());
//		data.setLow2hightDelay(delayData.getLow2hightDelay());
//		data.setHigh2lowDelay(delayData.getHigh2lowDelay());
//		data.setReadMeterDelay(delayData.getReadMeterDelay());
//		data.setSwitchMeterDelay(delayData.getSwitchMeterDelay());
//
//	}

	public void qryCalibratePlan(CalibratePlanData data) throws Exception {

		if (calibratePlanData == null) {
			throw new Exception(I18N.getVal(I18N.CalibratePlanDataNotExist));
		}
		data.setNeedValidate(calibratePlanData.isNeedValidate());
		data.setNeedCalculateAfterCalibrate(calibratePlanData.isNeedCalculateAfterCalibrate());
		data.setModes(calibratePlanData.getModes());

	}

	public void qrySteadyCfg(SteadyCfgData data) throws Exception {

		if (steadyCfgData == null) {
			throw new Exception(I18N.getVal(I18N.SteadyCfgDataNotExist));
		}
		data.setSampleCount(steadyCfgData.getSampleCount());
		data.setMaxSigma(steadyCfgData.getMaxSigma());
		data.setTrailCount(data.getTrailCount());

	}

	public void qryRangeCurrentPrecision(RangeCurrentPrecisionData data) throws Exception {

		if (rangeCurrentPrecisionData == null) {
			throw new Exception(I18N.getVal(I18N.RangeCurrentPrecisionDataNotExist));
		}
		data.setRanges(rangeCurrentPrecisionData.getRanges());
	}

	public void qryCalculatePlan(CalculatePlanData data) throws Exception {
		if (calculatePlanData == null) {
			throw new Exception(I18N.getVal(I18N.RangeCurrentPrecisionDataNotExist));
		}
		data.setMaxMeterOffset(calculatePlanData.getMaxMeterOffset());
		data.setMaxAdcOffset(calculatePlanData.getMaxAdcOffset());
		data.setMinCalculateCurrent(calculatePlanData.getMinCalculateCurrent());
		data.setMaxCalculateCurrent(calculatePlanData.getMaxCalculateCurrent());
		data.setMinCalculateVoltage(calculatePlanData.getMinCalculateVoltage());
		data.setMaxCalculateVoltage(calculatePlanData.getMaxCalculateVoltage());
		data.setModes(calculatePlanData.getModes());
	}
}
