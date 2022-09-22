package com.nlteck.calModel.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.core.runtime.Platform;

import com.nlteck.calModel.base.BaseCfgManager.Base;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestDot;
import com.nlteck.service.ChnMapService;
import com.nlteck.service.DiskService;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculatePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.utils.XmlUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月27日 下午7:38:47 校准配置管理器
 * @description 2022.4.11 xingguo_wang 修改 
 */
public class CalCfgManager {

	private Document document;

	private static final String PATH = "calCfg/calibrate.xml";

	public boolean needMatch; // 是否使用基准电压匹配?

	public BaseCfgManager baseCfgManager=new BaseCfgManager();
	public CalibratePlanData calibratePlanData;// 校准方案
//	public DelayData delayData;// 延时配置
	public DelayConfig delayConfig;// 延时配置
	public CalculatePlanData calculatePlanData;// 计量方案
	public CalculatePlanDataMoudle calculatePlanDataMoudle;//单膜片计量方案
	public RangeCurrentPrecisionData rangeCurrentPrecisionData;// 精度档位
	public SteadyCfgData steadyCfgData;// ADC稳定度检测配置
	
	public ChnMapService chnMapService=new ChnMapService();
	public DiskService diskService=new DiskService();

	public CalCfgManager() {
		init();
	}

	private void init() {	
		calibratePlanData = diskService.loadCalibratePlan();
//		delayData = core.getDiskService().loadDelay();
		delayConfig =diskService.loadDelay2();
		calculatePlanData = diskService.loadCalculatePlan();
		rangeCurrentPrecisionData = diskService.loadRangeCurrentPrecision();
		steadyCfgData = diskService.loadSteadyCfg();
		calculatePlanDataMoudle=diskService.loadCalculatePlanMoudle();
		
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
		String dir = Platform.getInstallLocation().getURL().getPath();
		document = XmlUtil.loadXml(dir+PATH);
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
		diskService.saveCalibratePlan(calibratePlanData);
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
		diskService.saveSteadyCfg(steadyCfgData);
	}

	/**
	 * 精度档位
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void cfgRangeCurrentPrecision(RangeCurrentPrecisionData data) throws IOException {
		rangeCurrentPrecisionData = data;
		diskService.saveRangeCurrentPrecision(rangeCurrentPrecisionData);

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
		diskService.saveCalculatePlan(calculatePlanData);

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

	//初始化校准点  xingguo_wang 2022.4.13
	public List<TestDot> initCalibrate(ChannelDO channelDO) {
		List<TestDot> calDots=new ArrayList<>();
		for (CalibratePlanMode mode : calibratePlanData.getModes()) {

			if (baseCfgManager.base.calCheckOnly) {
				if (mode.mode != Logic2Environment.CalMode.CV) {
					continue;
				}
			}

			for (CalibratePlanDot planDot : mode.dots) {
				TestDot testDot = new TestDot();
				testDot.setChannelDO(channelDO);
				testDot.setTestType(TestType.Cal);
				testDot.setMode(CalMode.values()[mode.mode.ordinal()]);// 模式
				testDot.setPole(Pole.values()[mode.pole.ordinal()]);// 极性
				testDot.setVoltMode(null);
				testDot.setPrecision(mode.level);// 精度
				testDot.setProgramVal(planDot.da);
				testDot.moduleIndex = mode.moduleIndex;

				// 放入比较范围
				testDot.minAdc = planDot.adcMin;
				testDot.maxAdc = planDot.adcMax;
				testDot.minMeter = planDot.meterMin;
				testDot.maxMeter = planDot.meterMax;

				testDot.minAdcK = mode.adcKMin;
				testDot.maxAdcK = mode.adcKMax;
				testDot.minAdcB = mode.adcBMin;
				testDot.maxAdcB = mode.adcBMax;
				testDot.minProgramK = mode.pKMin;
				testDot.maxProgramK = mode.pKMax;
				testDot.minProgramB = mode.pBMin;
				testDot.maxProgramB = mode.pBMax;

				testDot.minCheckAdcK = mode.checkAdcKMin;
				testDot.maxCheckAdcK = mode.checkAdcKMax;
				testDot.minCheckAdcB = mode.checkAdcBmin;
				testDot.maxCheckAdcB = mode.checkAdcBmax;
				testDot.setChnIndex(channelDO.getChnIndex());
				
				calDots.add(testDot);
			}
		}
		return calDots;
	}

	//初始化计量点
	public List<TestDot> initCalculate(ChannelDO channelDO) {
		List<TestDot> measureDots=new ArrayList<>();
		for (CalculatePlanMode mode : calculatePlanData.getModes()) {
			if (mode.disabled) {
				continue;
			}

			for (double checkDot : mode.dots) {
				TestDot testDot = new TestDot();
				testDot.setChannelDO(channelDO);
				testDot.setTestType(TestType.Measure);
				testDot.setMode(CalMode.values()[mode.mode.ordinal()]);// 模式
				testDot.setPole(Pole.values()[mode.pole.ordinal()]);// 极性
				testDot.setVoltMode(null);
				testDot.setProgramVal(checkDot);
				testDot.setChnIndex(channelDO.getChnIndex());
				calculatePrecision(testDot);// 精度

				measureDots.add(testDot);
			}
		}
		/*if (CalibrateCore.getBaseCfg().base.calCV2) {
			measureDots.addAll(measureDots.stream().filter(x -> x.mode == CalMode.CV).map(x -> {
				try {
					TestDot cv2Dot = x.clone();
					cv2Dot.mode = CalMode.CV;
					cv2Dot.voltMode = null;
					return cv2Dot;
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				return x;
			}).collect(Collectors.toList()));
		}*/
	
		return measureDots;
	}
	
	//单膜片初始化计量点
	public List<TestDot> initCalculateMoudle(ChannelDO channelDO) {
		channelDO.getMeasureDotList().clear();
		for (CalculatePlanMoudle mode : calculatePlanDataMoudle.getModes()) {
			if (mode.disabled) {
				continue;
			}
			
			for (double checkDot : mode.dots) {
				TestDot testDot = new TestDot();
				testDot.setChannelDO(channelDO);
				testDot.setTestType(TestType.Measure);
				testDot.setMode(CalMode.values()[mode.mode.ordinal()]);// 模式
				testDot.setPole(Pole.values()[mode.pole.ordinal()]);// 极性
				testDot.setVoltMode(null);
				testDot.setProgramVal(checkDot);
				testDot.moduleIndex=mode.moudleIndex;
				calculatePrecision(testDot);// 精度
				
				channelDO.getMeasureDotList().add(testDot);
			}
		}
		/*if (CalibrateCore.getBaseCfg().base.calCV2) {
			measureDots.addAll(measureDots.stream().filter(x -> x.mode == CalMode.CV).map(x -> {
				try {
					TestDot cv2Dot = x.clone();
					cv2Dot.mode = CalMode.CV;
					cv2Dot.voltMode = null;
					return cv2Dot;
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				return x;
			}).collect(Collectors.toList()));
		}*/
		return channelDO.getMeasureDotList();
		
		
	}

	/**
	 * 计算精度
	 */
	public void calculatePrecision(TestDot testDot) {

		if (testDot.getTestType() != TestType.Measure) {
			return;
		}

		Optional<RangeCurrentPrecision> a = rangeCurrentPrecisionData
				.getRanges().stream().filter(x -> testDot.getProgramVal() > x.min && testDot.getProgramVal() <= x.max).findAny();

		if (a.equals(Optional.empty())) {
			
			
		}

		// 临界档位往高精度
		if (testDot.getMode() == CalMode.CC || testDot.getMode() == CalMode.DC) {
			List<RangeCurrentPrecision> ranges = rangeCurrentPrecisionData
					.getRanges();

			testDot.setPrecision(ranges.get(ranges.size() - 1).level);// 默认最高精度

			for (RangeCurrentPrecision range : ranges) {
				if (testDot.getProgramVal() > range.min) {
					testDot.setPrecision(range.level);
					break;
				}
			}
		}
	}
	
	// 设置通道绑定的校准板上的通道
	public void bindCalBoardChannel(ChannelDO channelDO) {
		
	}
	/**
	 * 计量方案模式单膜片
	 *
	 */
	public static class CalculatePlanMoudle {

		public com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode mode;
		public com.nltecklib.protocol.li.main.PoleData.Pole pole;
		public boolean disabled;
		public List<Double> dots = new ArrayList<>();
		public int moudleIndex;
	}
	
}
