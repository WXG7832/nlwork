package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.nlteck.base.I18N;
import com.nlteck.fireware.CalibrateCore;
import com.nlteck.fireware.DeviceCore;
import com.nlteck.model.Channel;
import com.nlteck.model.DelayConfig;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.DelayData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculatePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.utils.CvsUtil;
import com.nltecklib.utils.LogUtil;
import com.nltecklib.utils.XmlUtil;

public class DiskService {

	private CalibrateCore core;
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private Logger logger;
	private final static String CAL_CONFIG_PATH = "config/calConfig";
	private final static String DATA_PATH = "data";

	private final static String CALIBRATE_PLAN_PATH = CAL_CONFIG_PATH + "/calibratePlan.xml";
	private final static String DELAY_PATH = CAL_CONFIG_PATH + "/delay.xml";
	private final static String CALCULATE_PLAN_PATH = CAL_CONFIG_PATH + "/calculatePlan.xml";
	private final static String RANGE_CURRENT_PRECISION_PATH = CAL_CONFIG_PATH + "/rangeCurrentPrecision.xml";
	private final static String STEADY_CFG_PATH = CAL_CONFIG_PATH + "/steadyCfg.xml";
	private final static String DEVICE_FILE_NAME = "/deviceState.xml";

	/**
	 * 初始化文件保存单核线程池
	 */
	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());

	// private LinkedBlockingQueue<Channel> saveChannels = new
	// LinkedBlockingQueue<>();

	public DiskService(CalibrateCore core) {
		this.core = core;
		logger = LogUtil.getLogger("disk");
		File file = new File(CAL_CONFIG_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		// startSaveChannel();
	}

	// private void startSaveChannel() {
	//
	// logger.info("startSaveChannel");
	//
	// Thread thread = new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// while (true) {
	// try {
	// Channel channel = saveChannels.take();
	//
	//// saveTestDots(channel);
	// saveTestDotsCsv(channel);
	// appendDeviceTestDotsCsv(channel);
	// updateDevice(channel);
	//
	// } catch (Exception e) {
	// logger.error("saveTestDots error:" + e.getMessage(), e);
	// }
	// }
	// }
	//
	// });
	// thread.setDaemon(true);
	// thread.start();
	//
	// }

	/**
	 * 放入线程池
	 * 
	 * @param channel
	 * @throws InterruptedException
	 */
	public void pushChannel(Channel channel) {
		// saveChannels.put(channel);

//		threadPoolExecutor.execute(new Runnable() {
//			public void run() {
//				try {
//					logger.info(String.format("start save chn %d", channel.getDeviceChnIndex() + 1));
//					// 保存通道测试点
//					saveTestDotsCsv(channel);
//					// appendDeviceTestDotsCsv(channel);
//					// 保存到设备信息
//					updateDevice(channel);
//				} catch (Exception e) {
//					logger.error(String.format("save  chn %d testDots error:", channel.getDeviceChnIndex() + 1)
//							+ e.getMessage(), e);
//				}
//
//			}
//		});

	}

	// public static void main(String[] args) {
	// List<TestDot> dots = new ArrayList<>();
	// String fileName = "chn-" + 1 + ".csv";
	//
	// CvsUtil cvsUtil = new CvsUtil(DATA_PATH + "/" + "11_22_33_44_55_66" + "/" +
	// fileName);
	// List<String[]> records = cvsUtil.readRecord();
	// for (int i = 1; i < records.size(); i++) {// 去除头
	// System.out.println("records length=" + records.get(i).length + "," +
	// Arrays.toString(records.get(i)));
	// int index = 2;// 从第二列开始
	// TestDot dot = new TestDot();
	// dot.testType = TestType.valueOf(records.get(i)[index++]);
	// dot.mode = CalMode.valueOf(records.get(i)[index++]);
	// String item = records.get(i)[index++];
	// if (!item.isEmpty()) {
	// dot.voltMode = VoltMode.valueOf(item);
	// }
	// dot.pole = Pole.valueOf(records.get(i)[index++]);
	// dot.precision = Integer.parseInt(records.get(i)[index++]);
	// dot.meterVal = Double.parseDouble(records.get(i)[index++]);
	// dot.programVal = Double.parseDouble(records.get(i)[index++]);
	// dot.adc = Double.parseDouble(records.get(i)[index++]);
	// dot.checkAdc = Double.parseDouble(records.get(i)[index++]);
	// dot.programK = Double.parseDouble(records.get(i)[index++]);
	// dot.programB = Double.parseDouble(records.get(i)[index++]);
	// dot.adcK = Double.parseDouble(records.get(i)[index++]);
	// dot.adcB = Double.parseDouble(records.get(i)[index++]);
	// dot.checkAdcK = Double.parseDouble(records.get(i)[index++]);
	// dot.checkAdcB = Double.parseDouble(records.get(i)[index++]);
	// dot.testResult = TestResult.valueOf(records.get(i)[index++]);
	// if (index < records.get(i).length) {
	// item = records.get(i)[index++];
	// if (!item.isEmpty()) {
	// dot.info = item;
	// }
	// }
	// dots.add(dot);
	// }
	// }

	/**
	 * 读取校准方案
	 * 
	 * @return
	 */
	public CalibratePlanData loadCalibratePlan() {

		try {
			CalibratePlanData data = new CalibratePlanData();
			Document doc = XmlUtil.loadXml(CALIBRATE_PLAN_PATH);
			Element rootElement = doc.getRootElement();
			data.setNeedValidate(Boolean.parseBoolean(rootElement.attributeValue("needValidate")));
			data.setNeedCalculateAfterCalibrate(
					Boolean.parseBoolean(rootElement.attributeValue("needCalculateAfterCalibrate")));
			data.setMaxProgramV(Long.parseLong(rootElement.attributeValue("maxProgramV")));
			data.setMaxProgramI(Long.parseLong(rootElement.attributeValue("maxProgramI")));

			List<Element> modeElements = rootElement.elements("mode");
			modeElements.stream().forEach(me -> {
				CalibratePlanMode mode = new CalibratePlanMode();
				data.getModes().add(mode);
				mode.mode = CalMode.valueOf(me.attributeValue("mode"));
				mode.moduleIndex = me.attributeValue("module") == null ? 0 : Integer.parseInt(me.attributeValue("module"));
				mode.level = Integer.parseInt(me.attributeValue("level"));
				mode.pole = Pole.valueOf(me.attributeValue("pole"));
				mode.pKMin = Double.parseDouble(me.attributeValue("pKMin"));
				mode.pKMax = Double.parseDouble(me.attributeValue("pKMax"));
				mode.pBMin = Double.parseDouble(me.attributeValue("pBMin"));
				mode.pBMax = Double.parseDouble(me.attributeValue("pBMax"));
				mode.adcKMin = Double.parseDouble(me.attributeValue("adcKMin"));
				mode.adcKMax = Double.parseDouble(me.attributeValue("adcKMax"));
				mode.adcBMin = Double.parseDouble(me.attributeValue("adcBMin"));
				mode.adcBMax = Double.parseDouble(me.attributeValue("adcBMax"));
				mode.checkAdcKMin = Double.parseDouble(me.attributeValue("checkAdcKMin"));
				mode.checkAdcKMax = Double.parseDouble(me.attributeValue("checkAdcKMax"));
				mode.checkAdcBmin = Double.parseDouble(me.attributeValue("checkAdcBmin"));
				mode.checkAdcBmax = Double.parseDouble(me.attributeValue("checkAdcBmax"));
				if(me.attributeValue("combine") != null) {
					
					mode.combine = Boolean.parseBoolean(me.attributeValue("combine"));
				}
				if(me.attributeValue("mainMeter") != null) {
					
					mode.mainMeter = Double.parseDouble(me.attributeValue("mainMeter"));
				}

				List<Element> dotElements = me.elements("dot");
				dotElements.stream().forEach(de -> {
					CalibratePlanDot dot = new CalibratePlanDot();
					mode.dots.add(dot);
					dot.da = Long.parseLong(de.attributeValue("da"));
					dot.adcMin = Double.parseDouble(de.attributeValue("adcMin"));
					dot.adcMax = Double.parseDouble(de.attributeValue("adcMax"));
					dot.meterMin = Double.parseDouble(de.attributeValue("meterMin"));
					dot.meterMax = Double.parseDouble(de.attributeValue("meterMax"));
				});

			});

			return data;
		} catch (Exception e) {
			logger.error("loadCalibratePlan error:" + e.getMessage(), e);
			core.getNetworkService().pushLog(I18N.getVal(I18N.LoadXmlError, CALIBRATE_PLAN_PATH, e.getMessage()), true);
		}

		return null;
	}

	/**
	 * 保存校准方案
	 * 
	 * @return
	 * @throws IOException
	 */
	public void saveCalibratePlan(CalibratePlanData data) throws IOException {

		Document doc = DocumentFactory.getInstance().createDocument();

		Element rootElement = DocumentFactory.getInstance().createElement("calibratePlan");
		doc.setRootElement(rootElement);

		rootElement.addAttribute("needValidate", data.isNeedValidate() + "");
		rootElement.addAttribute("needCalculateAfterCalibrate", data.isNeedCalculateAfterCalibrate() + "");
		rootElement.addAttribute("maxProgramV", data.getMaxProgramV() + "");
		rootElement.addAttribute("maxProgramI", data.getMaxProgramI() + "");

		data.getModes().stream().forEach(mode -> {
			Element modeElement = DocumentFactory.getInstance().createElement("mode");
			rootElement.add(modeElement);
			modeElement.addAttribute("mode", mode.mode.name());
			modeElement.addAttribute("level", mode.level + "");
			modeElement.addAttribute("pole", mode.pole.name());
			modeElement.addAttribute("pKMin", mode.pKMin + "");
			modeElement.addAttribute("pKMax", mode.pKMax + "");
			modeElement.addAttribute("pBMin", mode.pBMin + "");
			modeElement.addAttribute("pBMax", mode.pBMax + "");
			modeElement.addAttribute("adcKMin", mode.adcKMin + "");
			modeElement.addAttribute("adcKMax", mode.adcKMax + "");
			modeElement.addAttribute("adcBMin", mode.adcBMin + "");
			modeElement.addAttribute("adcBMax", mode.adcBMax + "");
			modeElement.addAttribute("checkAdcKMin", mode.checkAdcKMin + "");
			modeElement.addAttribute("checkAdcKMax", mode.checkAdcKMax + "");
			modeElement.addAttribute("checkAdcBmin", mode.checkAdcBmin + "");
			modeElement.addAttribute("checkAdcBmax", mode.checkAdcBmax + "");

			mode.dots.stream().forEach(dot -> {

				Element dotElement = DocumentFactory.getInstance().createElement("dot");
				modeElement.add(dotElement);
				dotElement.addAttribute("da", dot.da + "");
				dotElement.addAttribute("adcMin", dot.adcMin + "");
				dotElement.addAttribute("adcMax", dot.adcMax + "");
				dotElement.addAttribute("meterMin", dot.meterMin + "");
				dotElement.addAttribute("meterMax", dot.meterMax + "");

			});
		});

		XmlUtil.saveXml(CALIBRATE_PLAN_PATH, doc);

	}

	public DelayConfig loadDelay2() {
		try {
			return DelayConfig.loadDelayConfig(DELAY_PATH);
		} catch (Exception e) {
			logger.error("loadDelay error:" + e.getMessage(), e);
			core.getNetworkService().pushLog(I18N.getVal(I18N.LoadXmlError, DELAY_PATH, e.getMessage()), true);
		}
		return null;
	}

	/**
	 * 读取延时
	 * 
	 * @return
	 */
	public DelayData loadDelay() {
		try {
			DelayData data = new DelayData();
			Document doc = XmlUtil.loadXml(DELAY_PATH);
			Element rootElement = doc.getRootElement();

			data.setModuleOpenDelay(Integer.parseInt(rootElement.attributeValue("moduleOpenDelay")));
			data.setModuleCloseDelay(Integer.parseInt(rootElement.attributeValue("moduleCloseDelay")));
			data.setModeSwitchDelay(Integer.parseInt(rootElement.attributeValue("modeSwitchDelay")));
			data.setProgramSetDelay(Integer.parseInt(rootElement.attributeValue("programSetDelay")));
			data.setLow2hightDelay(Integer.parseInt(rootElement.attributeValue("low2hightDelay")));
			data.setHigh2lowDelay(Integer.parseInt(rootElement.attributeValue("high2lowDelay")));
			data.setReadMeterDelay(Integer.parseInt(rootElement.attributeValue("readMeterDelay")));
			data.setSwitchMeterDelay(Integer.parseInt(rootElement.attributeValue("switchMeterDelay")));

			return data;
		} catch (Exception e) {
			logger.error("loadDelay error:" + e.getMessage(), e);
			core.getNetworkService().pushLog(I18N.getVal(I18N.LoadXmlError, DELAY_PATH, e.getMessage()), true);
			return null;
		}

	}

	/**
	 * 保存延时
	 * 
	 * @return
	 * @throws IOException
	 */
	public void saveDelay(DelayData data) throws IOException {
		Document doc = DocumentFactory.getInstance().createDocument();

		Element rootElement = DocumentFactory.getInstance().createElement("delay");
		doc.setRootElement(rootElement);

		rootElement.addAttribute("moduleOpenDelay", data.getModuleOpenDelay() + "");
		rootElement.addAttribute("moduleCloseDelay", data.getModuleCloseDelay() + "");
		rootElement.addAttribute("modeSwitchDelay", data.getModeSwitchDelay() + "");
		rootElement.addAttribute("programSetDelay", data.getProgramSetDelay() + "");
		rootElement.addAttribute("low2hightDelay", data.getLow2hightDelay() + "");
		rootElement.addAttribute("high2lowDelay", data.getHigh2lowDelay() + "");
		rootElement.addAttribute("readMeterDelay", data.getReadMeterDelay() + "");
		rootElement.addAttribute("switchMeterDelay", data.getSwitchMeterDelay() + "");

		XmlUtil.saveXml(DELAY_PATH, doc);
	}

	/**
	 * 读取计量方案
	 * 
	 * @return
	 */
	public CalculatePlanData loadCalculatePlan() {
		try {
			CalculatePlanData data = new CalculatePlanData();
			Document doc = XmlUtil.loadXml(CALCULATE_PLAN_PATH);
			Element rootElement = doc.getRootElement();

			data.setMaxMeterOffset(Double.parseDouble(rootElement.attributeValue("maxMeterOffset")));
			data.setMaxAdcOffset(Double.parseDouble(rootElement.attributeValue("maxAdcOffset")));
			data.setMaxAdcOffsetCheck(Double.parseDouble(rootElement.attributeValue("maxAdcOffsetCheck")));
			data.setMaxAdcOffsetCV2(Double.parseDouble(rootElement.attributeValue("maxAdcOffsetCV2")));
			data.setMinCalculateCurrent(Double.parseDouble(rootElement.attributeValue("minCalculateCurrent")));
			data.setMaxCalculateCurrent(Double.parseDouble(rootElement.attributeValue("maxCalculateCurrent")));
			data.setMinCalculateVoltage(Double.parseDouble(rootElement.attributeValue("minCalculateVoltage")));
			data.setMaxCalculateVoltage(Double.parseDouble(rootElement.attributeValue("maxCalculateVoltage")));

			List<Element> modeElements = rootElement.elements("mode");
			modeElements.stream().forEach(me -> {
				CalculatePlanMode mode = new CalculatePlanMode();
				data.getModes().add(mode);
				mode.mode = CalMode.valueOf(me.attributeValue("mode"));
				mode.pole = Pole.valueOf(me.attributeValue("pole"));
				mode.disabled = Boolean.parseBoolean(me.attributeValue("disabled"));

				List<Element> dotElements = me.elements("dot");
				dotElements.stream().forEach(de -> {
					mode.dots.add(Double.parseDouble(de.getText()));
				});

			});

			return data;
		} catch (Exception e) {
			logger.error("loadCalculatePlan error:" + e.getMessage(), e);
			core.getNetworkService().pushLog(I18N.getVal(I18N.LoadXmlError, CALCULATE_PLAN_PATH, e.getMessage()), true);
		}

		return null;
	}

	/**
	 * 保存计量方案
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void saveCalculatePlan(CalculatePlanData data) throws IOException {

		Document doc = DocumentFactory.getInstance().createDocument();

		Element rootElement = DocumentFactory.getInstance().createElement("calculatePlan");
		doc.setRootElement(rootElement);

		rootElement.addAttribute("maxMeterOffset", data.getMaxMeterOffset() + "");
		rootElement.addAttribute("maxAdcOffset", data.getMaxAdcOffset() + "");
		rootElement.addAttribute("maxAdcOffsetCheck", data.getMaxAdcOffsetCheck() + "");
		rootElement.addAttribute("maxAdcOffsetCV2", data.getMaxAdcOffsetCV2() + "");
		rootElement.addAttribute("minCalculateCurrent", data.getMinCalculateCurrent() + "");
		rootElement.addAttribute("maxCalculateCurrent", data.getMaxCalculateCurrent() + "");
		rootElement.addAttribute("minCalculateVoltage", data.getMinCalculateVoltage() + "");
		rootElement.addAttribute("maxCalculateVoltage", data.getMaxCalculateVoltage() + "");

		data.getModes().stream().forEach(mode -> {
			Element modeElement = DocumentFactory.getInstance().createElement("mode");
			rootElement.add(modeElement);
			modeElement.addAttribute("mode", mode.mode.name());
			modeElement.addAttribute("pole", mode.pole.name());
			modeElement.addAttribute("disabled", mode.disabled + "");

			mode.dots.stream().forEach(dot -> {

				Element dotElement = DocumentFactory.getInstance().createElement("dot");
				modeElement.add(dotElement);
				dotElement.setText(dot + "");

			});
		});

		XmlUtil.saveXml(CALCULATE_PLAN_PATH, doc);

	}

	/**
	 * 读取精度档位
	 * 
	 * @return
	 */
	public RangeCurrentPrecisionData loadRangeCurrentPrecision() {
		try {
			RangeCurrentPrecisionData data = new RangeCurrentPrecisionData();
			Document doc = XmlUtil.loadXml(RANGE_CURRENT_PRECISION_PATH);
			Element rootElement = doc.getRootElement();

			List<Element> rangeElements = rootElement.elements("range");

			rangeElements.stream().forEach(re -> {
				RangeCurrentPrecision range = new RangeCurrentPrecision();
				data.appendRange(range);
				range.level = Integer.parseInt(re.attributeValue("level"));
				range.precide = Double.parseDouble(re.attributeValue("precide"));
				range.min = Double.parseDouble(re.attributeValue("min"));
				range.max = Double.parseDouble(re.attributeValue("max"));
				range.maxAdcOffset = Double.parseDouble(re.attributeValue("maxAdcOffset"));
				range.maxMeterOffset = Double.parseDouble(re.attributeValue("maxMeterOffset"));
			});

			data.getRanges().sort(null);

			return data;
		} catch (Exception e) {
			logger.error("loadRangeCurrentPrecision error:" + e.getMessage(), e);
			core.getNetworkService()
					.pushLog(I18N.getVal(I18N.LoadXmlError, RANGE_CURRENT_PRECISION_PATH, e.getMessage()), true);
		}

		return null;

	}

	/**
	 * 保存精度档位
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void saveRangeCurrentPrecision(RangeCurrentPrecisionData data) throws IOException {
		Document doc = DocumentFactory.getInstance().createDocument();

		Element rootElement = DocumentFactory.getInstance().createElement("rangeCurrentPrecision");
		doc.setRootElement(rootElement);

		data.getRanges().stream().forEach(range -> {
			Element rangeElement = DocumentFactory.getInstance().createElement("range");
			rootElement.add(rangeElement);
			rangeElement.addAttribute("level", range.level + "");
			rangeElement.addAttribute("precide", range.precide + "");
			rangeElement.addAttribute("min", range.min + "");
			rangeElement.addAttribute("max", range.max + "");
			rangeElement.addAttribute("maxAdcOffset", range.maxAdcOffset + "");
			rangeElement.addAttribute("maxMeterOffset", range.maxMeterOffset + "");
		});

		XmlUtil.saveXml(RANGE_CURRENT_PRECISION_PATH, doc);
	}

	/**
	 * 读取ADC稳定度
	 * 
	 * @return
	 */
	public SteadyCfgData loadSteadyCfg() {
		try {
			SteadyCfgData data = new SteadyCfgData();
			Document doc = XmlUtil.loadXml(STEADY_CFG_PATH);
			Element rootElement = doc.getRootElement();

			data.setSampleCount(Integer.parseInt(rootElement.attributeValue("sampleCount")));
			data.setMaxSigma(Double.parseDouble(rootElement.attributeValue("maxSigma")));
			
			String str = rootElement.attributeValue("maxSigmaBackup1");
			if(str == null) {
				
				data.setMaxSigmabackup1(data.getMaxSigma());
			} else {
				
				data.setMaxSigmabackup1(Double.parseDouble(str));
			}
			
			str = rootElement.attributeValue("maxSigmaBackup2");
			if(str == null) {
				
				data.setMaxSigmabackup2(data.getMaxSigma());
			} else {
				
				data.setMaxSigmabackup2(Double.parseDouble(str));
			}
			
			data.setTrailCount(Integer.parseInt(rootElement.attributeValue("trailCount")));
			data.setAdcReadCount(Integer.parseInt(rootElement.attributeValue("adcReadCount")));
			data.setAdcRetryDelay(Integer.parseInt(rootElement.attributeValue("adcRetryDelay")));
			//增加CV模式下稳定度读取策略
			data.setAdcReadCountCV(Integer.parseInt(rootElement.attributeValue("adcReadCountCV") == null ? "30" : rootElement.attributeValue("adcReadCountCV")));
			data.setAdcRetryDelayCV(Integer.parseInt(rootElement.attributeValue("adcRetryDelayCV") == null ? "2" : rootElement.attributeValue("adcRetryDelayCV")));

			return data;
		} catch (Exception e) {
			logger.error("loadSteadyCfg error:" + e.getMessage(), e);
			core.getNetworkService().pushLog(I18N.getVal(I18N.LoadXmlError, STEADY_CFG_PATH, e.getMessage()), true);
		}

		return null;

	}

	/**
	 * 保存ADC稳定度
	 * 
	 * @param data
	 * @throws IOException
	 */
	public void saveSteadyCfg(SteadyCfgData data) throws IOException {
		Document doc = DocumentFactory.getInstance().createDocument();

		Element rootElement = DocumentFactory.getInstance().createElement("steadyCfg");
		doc.setRootElement(rootElement);

		rootElement.addAttribute("sampleCount", data.getSampleCount() + "");
		rootElement.addAttribute("maxSigma", data.getMaxSigma() + "");
		rootElement.addAttribute("trailCount", data.getTrailCount() + "");
		rootElement.addAttribute("adcReadCount", data.getAdcReadCount() + "");
		rootElement.addAttribute("adcRetryDelay", data.getAdcRetryDelay() + "");

		XmlUtil.saveXml(STEADY_CFG_PATH, doc);
	}

	

	public void createDeviceXml(DeviceCore deviceCore) throws IOException {

		Document doc = DocumentFactory.getInstance().createDocument();
		Element rootElement = DocumentFactory.getInstance().createElement("device");
		doc.setRootElement(rootElement);
		deviceCore.getChannelMap().entrySet().stream().forEach(x -> {

			Channel chn = x.getValue();

			Element chnElement = DocumentFactory.getInstance().createElement("chn");
			rootElement.add(chnElement);

			chnElement.addAttribute("driverIndex", chn.getDriverIndex() + "");
			chnElement.addAttribute("chnIndex", chn.getChnIndex() + "");
			chnElement.addAttribute("chnState", chn.getChnState().name());
			if (chn.getStartDate() != null) {
				chnElement.addAttribute("startDate", sdf.format(chn.getStartDate()));
			}
			if (chn.getEndDate() != null) {
				chnElement.addAttribute("endDate", sdf.format(chn.getEndDate()));
			}
		});

		String path = DATA_PATH + "/coreData" ;

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		XmlUtil.saveXml(path + DEVICE_FILE_NAME, doc, CalibrateCore.getBaseCfg().i18n.charsetName);
	}

	/**
	 * 从配置文件初始化
	 * 
	 * @param deviceCore
	 * @return
	 */
	public boolean initDeviceFromXml(DeviceCore deviceCore) {
		try {
			String foldPath = DATA_PATH + "/coreData";
			File foldFile = new File(foldPath);
			if (!foldFile.exists()) {
				return false;
			}
			String filePath = foldPath + "/" + DEVICE_FILE_NAME;

			if (!new File(filePath).exists()) {
				throw new Exception("file not exist:" + filePath);
			}

			Document doc = XmlUtil.loadXml(filePath);
			Element rootElement = doc.getRootElement();

			List<Element> chnElements = rootElement.elements("chn");

			chnElements.stream().forEach(x -> {

				int unitIndex = Integer.parseInt(x.attributeValue("unitIndex"));
				int chnIndex = Integer.parseInt(x.attributeValue("chnIndex"));
				Channel channel = new Channel(deviceCore, unitIndex, chnIndex);
				deviceCore.getChannelMap().put(channel.getDeviceChnIndex(), channel);
				channel.setChnState(CalState.valueOf(x.attributeValue("chnState")));

				String tempString = x.attributeValue("startDate");
				if (tempString != null) {
					try {
						channel.setStartDate(sdf.parse(tempString));
					} catch (ParseException e) {
						logger.error("startDate parse error " + tempString + ":" + e.getMessage(), e);
					}
				}
				tempString = x.attributeValue("endDate");
				if (tempString != null) {
					try {
						channel.setEndDate(sdf.parse(tempString));
					} catch (ParseException e) {
						logger.error("endDate parse error " + tempString + ":" + e.getMessage(), e);
					}
				}
			});
			return true;
		} catch (Exception e) {
			logger.error("initDeviceFromXml error:" + e.getMessage(), e);
			return false;
		}

	}

	/**
	 * 导出工具用
	 * 
	 * @param deviceCore
	 * @param path
	 * @return
	 */
	public boolean initDeviceFromXml(DeviceCore deviceCore, String path) {
		try {
			String foldPath = path;
			File foldFile = new File(foldPath);
			if (!foldFile.exists()) {
				return false;
			}
			String filePath = foldPath + "/" + DEVICE_FILE_NAME;

			if (!new File(filePath).exists()) {
				throw new Exception("file not exist:" + filePath);
			}

			Document doc = XmlUtil.loadXml(filePath);
			Element rootElement = doc.getRootElement();

			List<Element> chnElements = rootElement.elements("chn");

			chnElements.stream().forEach(x -> {

				int unitIndex = Integer.parseInt(x.attributeValue("unitIndex"));
				int chnIndex = Integer.parseInt(x.attributeValue("chnIndex"));
				Channel channel = new Channel(deviceCore, unitIndex, chnIndex);
				deviceCore.getChannelMap().put(channel.getDeviceChnIndex(), channel);
				channel.setChnState(CalState.valueOf(x.attributeValue("chnState")));

				String tempString = x.attributeValue("startDate");
				if (tempString != null) {
					try {
						channel.setStartDate(sdf.parse(tempString));
					} catch (ParseException e) {
						logger.error("startDate parse error " + tempString + ":" + e.getMessage(), e);
					}
				}
				tempString = x.attributeValue("endDate");
				if (tempString != null) {
					try {
						channel.setEndDate(sdf.parse(tempString));
					} catch (ParseException e) {
						logger.error("endDate parse error " + tempString + ":" + e.getMessage(), e);
					}
				}
			});
			return true;
		} catch (Exception e) {
			logger.error("initDeviceFromXml error:" + e.getMessage(), e);
			return false;
		}

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
