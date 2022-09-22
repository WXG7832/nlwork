package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.Environment.SwitchState;
import com.nlteck.ParameterName;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.CheckBoard;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.StartupCfgManager.ProductType;
import com.nlteck.service.accessory.Fan;
import com.nlteck.service.accessory.FanManager;
import com.nlteck.service.accessory.InverterPower;
import com.nlteck.service.accessory.PowerManager;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.CvsUtil;
import com.nlteck.util.LogUtil;
import com.nlteck.util.XmlUtil;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CCVProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.DriverChnIndexDefineData;
import com.nltecklib.protocol.li.main.EnergySaveData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.JsonProcedureExData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.OverMode;
import com.nltecklib.protocol.li.main.MainEnvironment.ProcedureMode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.StepMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.OfflineRunningData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.li.main.TempData;
import com.nltecklib.protocol.li.main.VoiceAlertData;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月23日 下午1:52:13 主控内部文件保存服务
 */
public class FileSaveService {

	private MainBoard mainboard;

	private static final String SWITCH_PATH = "config/switch.xml";
	private final static String ENERGY_PATH = "config/energy.xml"; // 节能文件路径
	private final static String BEEP_PATH = "config/beep.xml"; // 蜂鸣报警文件路劲
	private final static String TEMP_CONTROL_PATH = "config/tempControl.xml";
	public final static String STRUCT_FILE_PATH = "config/struct"; // 结构文件路径
	private final static String OFFLINE_RUNNING_PATH = "config/offlineRunning.xml";
	public final static String OFFLINE_DIR_PATH = "config/struct/offline";
	private final static String IDENTITY_PATH = "config/identity.xml"; // 校准ID
	private final static String MAINTAIN_PATH = "config/maintain.xml"; // 维修记录
	private final static String FACTORY_PATH = "config/factory.xml"; // 出厂配置
	private final static String LIFE_PATH = "config/life.xml"; // 运行时长
	private final static String DRIVER_CHN_DEFINE_PATH = "config/driverChnIndexDefine.xml";

	private Logger logger;

	public FileSaveService(MainBoard mainboard) {

		this.mainboard = mainboard;

		try {
			logger = LogUtil.createLog("log/fileSaveService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 写入通道开关状态
	 * 
	 * @author wavy_zheng 2020年11月23日
	 * @throws AlertException
	 */
	public void writeChannelSwitchState() throws AlertException {

		Document document;
		try {
			document = XmlUtil.loadXml(SWITCH_PATH);
		} catch (Exception e1) {

			document = DocumentHelper.createDocument();
			document.addElement("root");
		}

		int index = 0;
		Element root = document.getRootElement();
		Element element = root.element("chnSwitch");
		if (element != null) {

			root.remove(element);
		}
		element = root.addElement("chnSwitch");

		for (int n = 0; n < MainBoard.startupCfg.getDeviceChnCount(); n++) {

			Channel chn = mainboard.getChannelByChnIndex(n);
			Element chnElement = element.addElement("chn");
			chnElement.addAttribute("index", index++ + "");
			chnElement.addAttribute("state", chn.getState() == ChnState.CLOSE ? "CLOSE" : "NORMAL");

		}

		try {
			XmlUtil.saveXml(SWITCH_PATH, document);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存通道开关状态文件" + SWITCH_PATH + "错误");
		}

	}

	/**
	 * 读取通道开关状态
	 * 
	 * @author wavy_zheng 2020年11月27日
	 * @throws AlertException
	 */
	public void readChnSwitchState() throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(SWITCH_PATH);
		} catch (Exception e1) {

			document = DocumentHelper.createDocument();
			document.addElement("root");
		}
		Element root = document.getRootElement();
		Element cs = root.element("chnSwitch");
		if (cs == null) {

			return;
		}
		List<Element> childs = cs.elements("chn");

		for (int n = 0; n < MainBoard.startupCfg.getDeviceChnCount(); n++) {

			if (n < childs.size()) {

				String state = childs.get(n).attributeValue("state");
				if ("CLOSE".equals(state)) {

					mainboard.getChannelByChnIndex(n).setState(ChnState.CLOSE);
				} else {

					if (mainboard.getChannelByChnIndex(n).getState() == ChnState.CLOSE) {

						mainboard.getChannelByChnIndex(n).setState(ChnState.UDT);
					}
				}
			}

		}

	}

	/**
	 * 读取分区结构文件，创建分区
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @throws AlertException
	 */
	public void readStructFile() throws AlertException {

		File dir = new File(STRUCT_FILE_PATH);
		if (!new File(STRUCT_FILE_PATH + ".xml").exists() || !dir.exists()) {

			// 默认生成以设备控制单元的分区
			ControlUnit cu = ControlUnit.createUnit(mainboard, 0,
					mainboard.getDriverBoards().toArray(new DriverBoard[0]));
			List<ControlUnit> controls = new ArrayList<>();
			controls.add(cu);
			mainboard.setControls(controls);
			cu.init();
			writeStructFile(); // 写入默认结构文件信息

		} else {

			Document doc = null;
			try {
				doc = XmlUtil.loadXml(STRUCT_FILE_PATH + ".xml");
			} catch (Exception e) {
				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.LoadFileError));
			}
			Element struct = doc.getRootElement();
			ProcedureMode mode = ProcedureMode.valueOf(struct.attributeValue("mode"));
			mainboard.setProcedureMode(mode);
			List<Element> cus = struct.elements("cu");
			if (cus.isEmpty()) {

				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.GetUnitInfoEmpty));
			}
			List<ControlUnit> controls = new ArrayList<>();
			for (Element ele : cus) {

				int index = Integer.parseInt(ele.attributeValue("index"));
				String testName = ele.attributeValue("testName");

				List<Element> dbElements = ele.elements("driver");
				List<DriverBoard> drivers = new ArrayList<>();
				for (Element dbElement : dbElements) {

					int driverIndex = Integer.parseInt(dbElement.attributeValue("index"));
					drivers.add(mainboard.getDriverByIndex(driverIndex));
				}
				ControlUnit cu = ControlUnit.createUnit(mainboard, index, drivers.toArray(new DriverBoard[0]));
				controls.add(cu);
				cu.setTestName(testName);

				// 获取流程,在分区初始化方法内完成

			}
			mainboard.setControls(controls); // 初始化分区完毕

			// 初始化分区
			for (ControlUnit cu : controls) {

				cu.init();
			}

		}

	}

	/**
	 * 写入结构文件-测试名
	 * 
	 * @author wavy_zheng 2020年12月7日
	 * @throws AlertException
	 */
	public void writeStructFileTestName() throws AlertException {

		File dir = new File(STRUCT_FILE_PATH);
		if (!dir.exists()) {

			dir.mkdirs();

		}
		Document doc = DocumentHelper.createDocument();
		Element structElement = doc.addElement("struct");
		structElement.addAttribute("mode", mainboard.getProcedureMode().name());

		for (ControlUnit cu : mainboard.getControls()) {

			Element cuElement = structElement.addElement("cu");
			cuElement.addAttribute("index", cu.getIndex() + "");
			cuElement.addAttribute("testName", cu.getTestName());
			for (DriverBoard db : cu.getDrivers()) {

				Element dbElement = cuElement.addElement("driver");
				dbElement.addAttribute("index", db.getDeviceDriverIndex() + "");
			}

		}

		try {
			XmlUtil.saveXml(STRUCT_FILE_PATH + ".xml", doc);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存" + STRUCT_FILE_PATH + ".xml发生错误:" + e.getMessage());
		}

	}

	/**
	 * 设备是否处于维修模式
	 * 
	 * @author wavy_zheng 2021年4月19日
	 * @return
	 */
	public boolean isMaintainFileExists() {

		File file = new File(MAINTAIN_PATH);
		return file.exists();

	}

	/**
	 * 生成维修标记
	 * 
	 * @author wavy_zheng 2021年4月19日
	 * @throws AlertException
	 */
	public void writeMaintainFile(boolean maintain) {

		if (maintain) {
			Document doc = DocumentHelper.createDocument();
			doc.addElement("device");

			try {
				XmlUtil.saveXml(MAINTAIN_PATH, doc);
			} catch (IOException e) {

				e.printStackTrace();
				// throw new AlertException()
			}
		} else {

			File file = new File(MAINTAIN_PATH);
			if (file.exists()) {

				file.delete();
			}

		}

	}

	/**
	 * 写入分区结构文件
	 * 
	 * @author wavy_zheng 2020年11月23日
	 * @throws AlertException
	 */
	public void writeStructFile() throws AlertException {

		File dir = new File(STRUCT_FILE_PATH);
		if (!dir.exists()) {

			dir.mkdirs();

		}

		// 删除struct.xml文件
		CommonUtil.deleteFile(new File(STRUCT_FILE_PATH + ".xml"));

		Document doc = DocumentHelper.createDocument();
		Element structElement = doc.addElement("struct");
		structElement.addAttribute("mode", mainboard.getProcedureMode().name());

		for (ControlUnit cu : mainboard.getControls()) {
			Element cuElement = structElement.addElement("cu");
			cuElement.addAttribute("index", cu.getIndex() + "");
			cuElement.addAttribute("testName", cu.getTestName());

			for (DriverBoard db : cu.getDrivers()) {

				Element dbElement = cuElement.addElement("driver");
				dbElement.addAttribute("index", db.getDeviceDriverIndex() + "");
			}

			// 新建分区目录
			dir = new File(STRUCT_FILE_PATH + "/" + (mainboard.getControlUnitCount() > 1 ? cu.getIndex() : "device"));
			if (!dir.exists()) {

				dir.mkdirs();
			}
			/**
			 * 
			 * 保留流程文件和保护参数，用于切换备用
			 */
			// 写入流程文件
			/*
			 * File procedureFile = new File(dir.getPath() + "/procedure.xml"); if
			 * (procedureFile.exists()) {
			 * 
			 * procedureFile.delete(); }
			 * 
			 * if (cu.getProcedure() != null) {
			 * 
			 * // 写入分区流程文件 writeProcedureFile(procedureFile, cu.getProcedure()); }
			 * 
			 * if (cu.getPnAG() != null) {
			 * 
			 * // 写入保护文件 File protectionFile = new File(dir.getPath() +
			 * "/protection_AG.xml"); if (protectionFile.exists()) {
			 * 
			 * protectionFile.delete(); } // 写入分区流程文件 writeProtectFile(protectionFile,
			 * cu.getPnAG()); } if(cu.getPnIC() != null) {
			 * 
			 * File protectionFile = new File(dir.getPath() + "/protection_IC.xml"); if
			 * (protectionFile.exists()) {
			 * 
			 * protectionFile.delete(); } writeProtectFile(protectionFile, cu.getPnIC()); }
			 */

		}

		try {
			XmlUtil.saveXml(STRUCT_FILE_PATH + ".xml", doc);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存" + STRUCT_FILE_PATH + ".xml发生错误:" + e.getMessage());
		}
	}

	/**
	 * 写入当前进阶流程对象到文件
	 * 
	 * @author wavy_zheng 2022年7月8日
	 * @param file
	 * @param procedure
	 * @throws Exception
	 */
	public void writeProcedureExFile(File file, JsonProcedureExData procedure) throws Exception {

		int index = file.getPath().lastIndexOf(File.separator);
		new File(file.getPath().substring(0, index)).mkdirs();

		Document doc = DocumentHelper.createDocument();

		Element element = doc.addElement("procedure");

		element.addAttribute("name", procedure.getProcedureData().getName());
		element.addAttribute("stepMode", procedure.getProcedureData().getStepMode().name());
		element.addAttribute("loopCount", procedure.getProcedureData().getLoopCount() + "");
		element.addAttribute("loopSt", procedure.getProcedureData().getLoopSt() + "");
		element.addAttribute("loopEd", procedure.getProcedureData().getLoopEd() + "");
		/** 流程类型，匹配保护 */
		if (Data.isUseProcedureWorkType()) {
			element.addAttribute("workType", procedure.getProcedureData().getWorkType().name());
		}
		/**
		 * 写入流程关联保护
		 */
		writeProcedureProtect(element, procedure.getDeviceProtect() , true);
		writeProcedureProtect(element, procedure.getCheckProtect() , true);
		writeProcedureProtect(element, procedure.getFirstEndProtect() , true);

		List<Element> childs = element.elements();

		for (int n = 0; n < procedure.getProcedureData().getStepCount(); n++) {

			Step step = procedure.getProcedureData().getStep(n);
			Element child = null;

			child = element.addElement("step");

			Field[] fields = Step.class.getDeclaredFields();
			for (Field f : fields) {

				f.setAccessible(true);

				if (f.getType() == Data.class) {

					// 工步保护
					writeProcedureProtect(child, (Data) f.get(step) , false);

				} else {

					try {
						child.addAttribute(f.getName(), f.get(step).toString());
					} catch (Exception e) {

						e.printStackTrace();
						throw new AlertException(AlertCode.LOGIC, "写入流程文件错误,无法写入属性:" + f.getName());
					}
				}
			}

		}

		try {
			XmlUtil.saveXml(file.getPath(), doc);
		} catch (IOException e) {

			logger.error(CommonUtil.getThrowableException(e));
			throw new AlertException(AlertCode.LOGIC, "保存流程文件" + file.getPath() + "错误");
		}

	}

	// 写入当前流程文件
	public void writeProcedureFile(File file, ProcedureData procedure) throws AlertException {

		int index = file.getPath().lastIndexOf(File.separator);
		new File(file.getPath().substring(0, index)).mkdirs();

		Document doc = DocumentHelper.createDocument();

		Element element = doc.addElement("procedure");

		element.addAttribute("name", procedure.getName());
		element.addAttribute("stepMode", procedure.getStepMode().name());
		element.addAttribute("loopCount", procedure.getLoopCount() + "");
		element.addAttribute("loopSt", procedure.getLoopSt() + "");
		element.addAttribute("loopEd", procedure.getLoopEd() + "");
		/** 流程类型，匹配保护 */
		if (Data.isUseProcedureWorkType()) {
			element.addAttribute("workType", procedure.getWorkType().name());
		}

		List<Element> childs = element.elements();

		for (int n = 0; n < procedure.getStepCount(); n++) {

			Step step = procedure.getStep(n);
			Element child = null;

			child = element.addElement("step");

			Field[] fields = Step.class.getDeclaredFields();
			for (Field f : fields) {

				f.setAccessible(true);
				try {
					child.addAttribute(f.getName(), f.get(step).toString());
				} catch (Exception e) {

					e.printStackTrace();
					throw new AlertException(AlertCode.LOGIC, "写入流程文件错误,无法写入属性:" + f.getName());
				}
			}

		}

		try {
			XmlUtil.saveXml(file.getPath(), doc);
		} catch (IOException e) {

			logger.error(CommonUtil.getThrowableException(e));
			throw new AlertException(AlertCode.LOGIC, "保存流程文件" + file.getPath() + "错误");
		}

	}

	/**
	 * 读取驱动板通道定义顺序文件
	 * 
	 * @author wavy_zheng 2021年4月26日
	 * @param path
	 * @return
	 * @throws AlertException
	 */
	public DriverChnIndexDefineData readDriverChnIndexDefineFile() throws AlertException {

		Document doc = null;

		try {
			doc = XmlUtil.loadXml(DRIVER_CHN_DEFINE_PATH);
		} catch (Exception ex) {

			throw new AlertException(AlertCode.LOGIC,
					I18N.getVal(I18N.LoadFileError, DRIVER_CHN_DEFINE_PATH, ex.getMessage()));
		}
		Element defineElement = doc.getRootElement();
		DriverChnIndexDefineData dcdd = new DriverChnIndexDefineData();
		dcdd.setEnable(defineElement.attributeValue("enable") == null ? false
				: Boolean.parseBoolean(defineElement.attributeValue("enable")));

		List<Element> defineElements = defineElement.elements("define");
		List<Byte> list = new ArrayList<>();
		for (int i = 0; i < defineElements.size(); i++) {

			list.add(Byte.parseByte(defineElements.get(i).attributeValue("mapIndex")));

		}
		dcdd.setChnIndexDefineList(list);

		return dcdd;

	}
	
	/**
	 * 读取流程进阶对象，这个方法同样兼容旧的流程文件格式
	 * @author  wavy_zheng
	 * 2022年7月8日
	 * @param path
	 * @return
	 * @throws AlertException
	 */
	public static JsonProcedureExData readProcedureExFile(String path) throws Exception {
		
		Document doc = null;

		try {
			doc = XmlUtil.loadXml(path);
		} catch (Exception ex) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.LoadFileError, path, ex.getMessage()));
		}

		Element procedureElement = doc.getRootElement();
       
		JsonProcedureExData jsonPd = new JsonProcedureExData();
		ProcedureData pd = new ProcedureData();
		pd.setName(procedureElement.attributeValue("name") == null ? "" : procedureElement.attributeValue("name"));
		pd.setStepMode(StepMode.valueOf(procedureElement.attributeValue("stepMode") == null ? "ASYNC"
				: procedureElement.attributeValue("stepMode")));
		pd.setLoopCount(Integer.parseInt(procedureElement.attributeValue("loopCount")));
		pd.setLoopSt(Integer.parseInt(procedureElement.attributeValue("loopSt")));
		pd.setLoopEd(Integer.parseInt(procedureElement.attributeValue("loopEd")));

		if (MainBoard.startupCfg.getProtocol().useProcedureWorkType) {
			if (procedureElement.attribute("workType") != null) {
				pd.setWorkType(
						WorkType.valueOf(procedureElement.attributeValue("workType") == null ? WorkType.AG.toString()
								: procedureElement.attributeValue("workType")));
			}
		}
		//加载流程关联保护
		List<Element> protectionElements = procedureElement.elements("protection");
		for(Element ele : protectionElements) {
			
			Data protect = readProcedureProtect(ele);
			if(protect instanceof DeviceProtectData) {
				
				jsonPd.setDeviceProtect((DeviceProtectData) protect);
			} else if(protect instanceof PoleData) {
				
				jsonPd.setPoleProtect((PoleData) protect);
			} else if(protect instanceof StartEndCheckData) {
				
				jsonPd.setFirstEndProtect((StartEndCheckData) protect);
			} else if(protect instanceof CheckVoltProtectData) {
				
				jsonPd.setCheckProtect((CheckVoltProtectData) protect);
			}
		}
		
		List<Element> stepElements = procedureElement.elements("step");
		for (int i = 0; i < stepElements.size(); i++) {

			Step step = new Step();
			step.stepIndex = i + 1;
			step.specialVoltage = Double.parseDouble(stepElements.get(i).attributeValue("specialVoltage"));
			step.specialCurrent = Double.parseDouble(stepElements.get(i).attributeValue("specialCurrent"));
			step.workMode = WorkMode.valueOf(stepElements.get(i).attributeValue("workMode"));
			step.overThreshold = Double.parseDouble(stepElements.get(i).attributeValue("overThreshold"));
			step.overCapacity = Double.parseDouble(stepElements.get(i).attributeValue("overCapacity"));
			step.overTime = Integer.parseInt(stepElements.get(i).attributeValue("overTime"));
			step.timeProtect = Boolean.parseBoolean(stepElements.get(i).attributeValue("timeProtect"));
			step.deltaVoltage = Double.parseDouble(stepElements.get(i).attributeValue("deltaVoltage"));
			if (stepElements.get(i).attribute("pressure") != null) {
				step.pressure = Integer.parseInt(stepElements.get(i).attributeValue("pressure"));
			}

			step.overMode = OverMode.valueOf(stepElements.get(i).attributeValue("overMode"));

			

			if (stepElements.get(i).attribute("pressureVariation") != null) {
				step.pressureVariation = Integer.parseInt(stepElements.get(i).attributeValue("pressureVariation"));
			}
			if (stepElements.get(i).attribute("varWaitTime") != null) {
				step.varWaitTime = Integer.parseInt(stepElements.get(i).attributeValue("varWaitTime"));
			}
			
			
			if (stepElements.get(i).attribute("recordTime") != null) {
				step.recordTime = Integer.parseInt(stepElements.get(i).attributeValue("recordTime"));
			}
			
			
			if (stepElements.get(i).element("protection") != null) {
				
				//读取子元素：工步保护
				Data protect = readProcedureProtect(stepElements.get(i).element("protection"));
				step.protection = protect;
				
			}
			
			pd.addStep(step);

		}
		jsonPd.setProcedureData(pd);
		
		return jsonPd;
		
		
	}
	

	/**
	 * 读取流程文件
	 * 
	 * @param mainBoard
	 * @throws Exception
	 */
	public static ProcedureData readProcedureFile(String path) throws AlertException {

		Document doc = null;

		try {
			doc = XmlUtil.loadXml(path);
		} catch (Exception ex) {

			throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.LoadFileError, path, ex.getMessage()));
		}

		Element procedureElement = doc.getRootElement();

		ProcedureData pd = new ProcedureData();
		pd.setName(procedureElement.attributeValue("name") == null ? "" : procedureElement.attributeValue("name"));
		pd.setStepMode(StepMode.valueOf(procedureElement.attributeValue("stepMode") == null ? "ASYNC"
				: procedureElement.attributeValue("stepMode")));
		pd.setLoopCount(Integer.parseInt(procedureElement.attributeValue("loopCount")));
		pd.setLoopSt(Integer.parseInt(procedureElement.attributeValue("loopSt")));
		pd.setLoopEd(Integer.parseInt(procedureElement.attributeValue("loopEd")));

		if (MainBoard.startupCfg.getProtocol().useProcedureWorkType) {
			if (procedureElement.attribute("workType") != null) {
				pd.setWorkType(
						WorkType.valueOf(procedureElement.attributeValue("workType") == null ? WorkType.AG.toString()
								: procedureElement.attributeValue("workType")));
			}
		}

		List<Element> stepElements = procedureElement.elements("step");
		for (int i = 0; i < stepElements.size(); i++) {

			Step step = new Step();
			step.stepIndex = i + 1;
			step.specialVoltage = Double.parseDouble(stepElements.get(i).attributeValue("specialVoltage"));
			step.specialCurrent = Double.parseDouble(stepElements.get(i).attributeValue("specialCurrent"));
			step.workMode = WorkMode.valueOf(stepElements.get(i).attributeValue("workMode"));
			step.overThreshold = Double.parseDouble(stepElements.get(i).attributeValue("overThreshold"));
			step.overCapacity = Double.parseDouble(stepElements.get(i).attributeValue("overCapacity"));
			step.overTime = Integer.parseInt(stepElements.get(i).attributeValue("overTime"));
			step.timeProtect = Boolean.parseBoolean(stepElements.get(i).attributeValue("timeProtect"));
			step.deltaVoltage = Double.parseDouble(stepElements.get(i).attributeValue("deltaVoltage"));
			if (stepElements.get(i).attribute("pressure") != null) {
				step.pressure = Integer.parseInt(stepElements.get(i).attributeValue("pressure"));
			}

			step.overMode = OverMode.valueOf(stepElements.get(i).attributeValue("overMode"));

			if (MainBoard.startupCfg.getProtocol().useAirPressure) {

				if (stepElements.get(i).attribute("pressureVariation") != null) {
					step.pressureVariation = Integer.parseInt(stepElements.get(i).attributeValue("pressureVariation"));
				}
				if (stepElements.get(i).attribute("varWaitTime") != null) {
					step.varWaitTime = Integer.parseInt(stepElements.get(i).attributeValue("varWaitTime"));
				}
			}
			if (MainBoard.startupCfg.getProtocol().useStepRecord) {
				if (stepElements.get(i).attribute("recordTime") != null) {
					step.recordTime = Integer.parseInt(stepElements.get(i).attributeValue("recordTime"));
				}
			}

			pd.addStep(step);

		}
		return pd;
	}

	// 存储CC保护
	private static void writeCCProtect(Element parent, CCProtectData protect) throws AlertException {

		Element cc = parent.element("cc");
		if (cc == null) {

			cc = parent.addElement("cc");
		}
		Field[] fields = CCProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				cc.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				Environment.errLogger.info(CommonUtil.getThrowableException(e));
				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入CC保护参数失败,无法写入属性" + f.getName());
			}
		}

	}

	private static CCProtectData readCCProtect(Element parent) throws AlertException {

		Element cc = parent.element("cc");

		CCProtectData protect = new CCProtectData();
		if (cc == null) {

			protect.setVoltAscUnitSeconds(300);
			protect.setVoltAscValLower(1);
			return protect;
		}

		for (Attribute attr : cc.attributes()) {

			try {

				Field field = CCProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取CC保护参数错误，无法读取属性:" + attr.getName());
			}
		}

		return protect;
	}

	// 存储CV保护
	private static void writeCVProtect(Element parent, CVProtectData protect) throws AlertException {

		Element cv = parent.element("cv");
		if (cv == null) {

			cv = parent.addElement("cv");
		}

		Field[] fields = CVProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				cv.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入cv保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取CV保护参数
	private static CVProtectData readCVProtect(Element parent) throws AlertException {

		Element cv = parent.element("cv");

		CVProtectData protect = new CVProtectData();
		if (cv == null) {
			return protect;
		}

		for (Attribute attr : cv.attributes()) {

			try {

				Field field = CVProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取cv保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;
	}

	// 存储DC保护
	private static void writeDCProtect(Element parent, DCProtectData protect) throws AlertException {

		Element dc = parent.element("dc");
		if (dc == null) {

			dc = parent.addElement("dc");
		}

		Field[] fields = DCProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				dc.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入dc保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取DC保护参数
	private static DCProtectData readDCProtect(Element parent) throws AlertException {

		Element dc = parent.element("dc");

		DCProtectData protect = new DCProtectData();
		if (dc == null) {

			// 默认斜率保护
			protect.setVoltDescUnitSeconds(300);
			protect.setVoltDescValLower(1);

			return protect;
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = DCProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取dc保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;
	}

	// 写入设备保护参数
	private static void writeDeviceProtect(Element parent, DeviceProtectData protect) throws AlertException {

		Element device = parent.element("device");
		if (device == null) {

			device = parent.addElement("device");
		}

		Field[] fields = DeviceProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入一级保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取设备保护保护参数
	private static DeviceProtectData readDeviceProtect(Element parent) throws AlertException {

		Element dc = parent.element("device");

		DeviceProtectData protect = new DeviceProtectData();
		if (dc == null) {

			// 默认值
			protect.setCapacityCoefficien(2.5);
			protect.setCurrUpper(MainBoard.startupCfg.getMaxDeviceCurrent());
			protect.setDeviceVoltUpper(5000); // 默认最大5v
			protect.setBatVoltUpper(4800);

			return protect;
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = DeviceProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取一级保护文件失败,无法读取属性:" + attr.getName());
			}
		}
		if (protect.getDeviceVoltUpper() == 0) {

			protect.setDeviceVoltUpper(MainBoard.startupCfg.getMaxDeviceVoltage());
		}
		if (protect.getBatVoltUpper() == 0) {

			protect.setDeviceVoltUpper(MainBoard.startupCfg.getMaxDeviceVoltage() - 100);
		}
		if (protect.getCurrUpper() == 0) {

			protect.setDeviceVoltUpper(MainBoard.startupCfg.getMaxDeviceCurrent());
		}
		if (protect.getCapacityCoefficien() == 0) {

			protect.setCapacityCoefficien(2.5);
		}

		return protect;
	}

	// 写入设备极性保护
	private static void writePoleProtect(Element parent, PoleData protect) throws AlertException {

		Element device = parent.element("pole");
		if (device == null) {

			device = parent.addElement("pole");
		}

		Field[] fields = PoleData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入极性保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取设备极性保护值
	private static PoleData readPoleProtect(Element parent) throws AlertException {

		Element dc = parent.element("pole");

		PoleData protect = new PoleData();
		if (dc == null) {

			protect.setPole(Pole.NORMAL);
			protect.setPoleDefine(100);
			return protect;
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = PoleData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == Pole.class) {

					field.set(protect, Pole.valueOf(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取极性保护文件失败,无法读取属性:" + attr.getName());
			}
		}
		if (protect.getPoleDefine() == 0) {

			protect.setPoleDefine(100);
		}

		return protect;
	}

	// 写入休眠保护
	private static void writeSleepProtect(Element parent, SlpProtectData protect) throws AlertException {

		Element device = parent.element("sleep");
		if (device == null) {

			device = parent.addElement("sleep");
		}

		Field[] fields = SlpProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入休眠保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取休眠保护值
	private static SlpProtectData readSleepProtect(Element parent) throws AlertException {

		Element dc = parent.element("sleep");

		SlpProtectData protect = new SlpProtectData();
		if (dc == null) {
			return protect;
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = SlpProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取休眠保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;
	}

	private static void writeStartEndCheckProtect(Element parent, StartEndCheckData secd) throws AlertException {

		Element startEndCheck = parent.element("startEndCheck");
		if (startEndCheck == null) {

			startEndCheck = parent.addElement("startEndCheck");
		}

		Field[] fields = StartEndCheckData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				startEndCheck.addAttribute(f.getName(), f.get(secd).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入流程首尾保护文件失败,无法写入属性:" + f.getName());
			}
		}
	}

	/**
	 * 读取流程首尾保护配置
	 * 
	 * @param parent
	 * @return
	 */
	private static StartEndCheckData readStartEndCheckProtect(Element parent) throws AlertException {

		Element startEndCheck = parent.element("startEndCheck");

		StartEndCheckData protect = new StartEndCheckData();
		if (startEndCheck == null) {
			return new StartEndCheckData();
		}

		for (Attribute attr : startEndCheck.attributes()) {

			try {

				Field field = StartEndCheckData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取流程首尾保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;

	}

	// 写入首CC保护配置
	private static void writeFirstCCProtect(Element parent, FirstCCProtectData protect) throws AlertException {

		Element device = parent.element("firstCC");
		if (device == null) {

			device = parent.addElement("firstCC");
		}

		Field[] fields = FirstCCProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入首步次CC保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取首个CC保护参数

	private static FirstCCProtectData readFirstCCProtect(Element parent) throws AlertException {

		Element dc = parent.element("firstCC");

		FirstCCProtectData protect = new FirstCCProtectData();
		if (dc == null) {
			return new FirstCCProtectData();
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = FirstCCProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == boolean.class) {

					field.set(protect, Boolean.parseBoolean(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取首步次CC保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;

	}

	// 写入回检板保护配置
	private static void writeCheckVoltProtect(Element parent, CheckVoltProtectData protect) throws AlertException {

		Element device = parent.element("checkVolt");
		if (device == null) {

			device = parent.addElement("checkVolt");
		}

		Field[] fields = CheckVoltProtectData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入回检保护文件失败,无法写入属性:" + f.getName());
			}
		}
	}

	// 读取回检板电压保护参数
	private static CheckVoltProtectData readCheckVoltProtect(Element parent) throws AlertException {

		Element dc = parent.element("checkVolt");

		CheckVoltProtectData protect = new CheckVoltProtectData();
		if (dc == null) {

			protect.setResisterOffset(MainBoard.startupCfg.getProductType() == ProductType.CAPACITY ? 500 : 800);
			return protect;
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = CheckVoltProtectData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取回检保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;

	}

	/**
	 * 写入分区保护配置参数
	 * 
	 * @param lb
	 */
	// public void writeProtectFile(ControlUnit cu, ParameterName pn) throws
	// AlertException {
	//
	// String path = mainboard.getControlUnitCount() == 1 ? STRUCT_FILE_PATH +
	// "/device" : STRUCT_FILE_PATH + "/" + cu.getIndex();
	//
	// new File(path).mkdirs();
	//
	// path += "/protection.xml";
	//
	// Document doc = DocumentHelper.createDocument();
	// writeParameterNameFile(doc, pn);
	//
	// try {
	// XmlUtil.saveXml(path, doc);
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	//
	// throw new AlertException(AlertCode.LOGIC, "写入" + path + "发生错误:" +
	// e.getMessage());
	// }
	//
	// }

	/**
	 * 读取流程保护
	 * 
	 * @author wavy_zheng 2022年7月8日
	 * @param node
	 * @return
	 * @throws AlertException
	 * @throws ClassNotFoundException
	 */
	private static Data readProcedureProtect(Element node) throws Exception {

		String classType = node.attributeValue("type");
		Class c = Class.forName(classType);
		Object protect = c.newInstance();
		
		if(c == CCVProtectData.class) {
			
			//读取子类
			List<Element> childs = node.elements("protection");
			for(Element child : childs) {
				
				Data cp = readProcedureProtect(child);
				if(cp.getClass() == CCProtectData.class) {
					
					((CCVProtectData)protect).setCcProtect((CCProtectData) cp);
				} else if(cp.getClass() == CVProtectData.class) {
					
					((CCVProtectData)protect).setCvProtect((CVProtectData) cp);
				}
			}

		}
		
		for (Attribute attr : node.attributes()) {

			try {

				if (attr.getName().equals("type")) {

					continue;
				}
				Field field = c.getDeclaredField(attr.getName());
				field.setAccessible(true);

				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == boolean.class) {

					field.set(protect, Boolean.parseBoolean(attr.getValue()));
				} 
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取" + c.getSimpleName() + "保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return (Data) protect;
	}

	/**
	 * 写入流程保护
	 * 
	 * @author wavy_zheng 2022年7月8日
	 * @param parent
	 * @param protect
	 * @throws AlertException
	 */
	private static void writeProcedureProtect(Element parent, Data protect , boolean append) throws Exception {

		if (protect == null) {

			return;
		}

		String type = protect.getClass().getName();
		Field[] fields = protect.getClass().getDeclaredFields();
		Element element = parent.element("protection");
		if (element == null || append) {

			element = parent.addElement("protection");
		} else {
			
			parent.remove(element);
			element = parent.addElement("protection");
			
		}
		element.addAttribute("type", type);

		for (Field f : fields) {

			f.setAccessible(true);
			if (f.getType() == CCProtectData.class) {
				
				//写入CC保护子类
				writeProcedureProtect(element,(Data)f.get(protect) , true);

			} else if (f.getType() == CVProtectData.class) {
                
				//写入CV保护子类
				writeProcedureProtect(element,(Data)f.get(protect) , true);
				
			} else {

				try {
					element.addAttribute(f.getName(), f.get(protect).toString());
				} catch (Exception e) {

					Environment.errLogger.info(CommonUtil.getThrowableException(e));
					e.printStackTrace();
					throw new AlertException(AlertCode.LOGIC,
							"写入" + protect.getClass().getSimpleName() + "保护参数失败,无法写入属性" + f.getName());
				}
			}
		}

	}

	private static Element writeParameterNameFile(Document doc, ParameterName pn) throws AlertException {

		Element pnElement = doc.addElement("pn");

		pnElement.addAttribute("name", pn.getName());
		pnElement.addAttribute("workType", pn.getWorkType().name());
		pnElement.addAttribute("defaultPlan", pn.isDefaultPlan() ? "true" : "false");

		writeCCProtect(pnElement, pn.getCcProtect());
		writeCVProtect(pnElement, pn.getCvProtect());
		writeDCProtect(pnElement, pn.getDcProtect());
		writeSleepProtect(pnElement, pn.getSlpProtect());
		writeCheckVoltProtect(pnElement, pn.getCheckVoltProtect());
		writeDeviceProtect(pnElement, pn.getDeviceProtect());
		writeFirstCCProtect(pnElement, pn.getFirstCCProtect());
		writePoleProtect(pnElement, pn.getPoleProtect());
		writeStartEndCheckProtect(pnElement, pn.getStartEndCheckProtect());

		return pnElement;
	}

	/**
	 * 写入通道携带数据
	 * 
	 * @author wavy_zheng 2021年4月5日
	 * @param element
	 * @param chnData
	 * @throws AlertException
	 */
	private Element writeChnRuntimeData(Element parent, ChannelData chnData) throws AlertException {

		Element child = parent.addElement("voltageOffsetProtectionData");

		Field[] fields = ChannelData.class.getDeclaredFields();
		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {

				continue;
			}
			f.setAccessible(true);
			if (f.getType() == WorkMode.class || f.getType() == ChnState.class || f.getType() == double.class
					|| f.getType() == long.class || f.getType() == Date.class || f.getType() == AlertCode.class
					|| f.getType() == int.class || f.getType() == boolean.class) {

				try {

					if (f.get(chnData) == null) {

						continue; // 空值不赋值
					}
					if (f.getType() == AlertCode.class) {

						child.addAttribute(f.getName(), ((AlertCode) f.get(chnData)).name());
					} else if (f.getType() == Date.class) {

						child.addAttribute(f.getName(),
								CommonUtil.formatTime((Date) f.get(chnData), "yyyy-MM-dd HH:mm:ss"));
					} else {

						child.addAttribute(f.getName(), f.get(chnData).toString());
					}
				} catch (Exception e) {

					e.printStackTrace();
					throw new AlertException(AlertCode.LOGIC, "写入通道携带数据文件发生错误");
				}
			}
		}
		return child;

	}

	/**
	 * 通过反射读取通道携带数据
	 * 
	 * @author wavy_zheng 2021年4月5日
	 * @param element
	 * @param chnData
	 * @throws AlertException
	 */
	private void readChnRuntimeData(Element element, ChannelData chnData) throws AlertException {

		try {
			for (Attribute attr : element.attributes()) {

				System.out.println("name:" + attr.getName() + ",value=" + attr.getValue());
				Field field = ChannelData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == AlertCode.class) {

					field.set(chnData, AlertCode.valueOf(attr.getValue()));

				} else if (field.getType() == Date.class) {

					field.set(chnData, CommonUtil.parseTime(attr.getValue(), "yyyy-MM-dd HH:mm:ss"));

				} else if (field.getType() == WorkMode.class) {

					field.set(chnData, WorkMode.valueOf(attr.getValue()));
				} else if (field.getType() == ChnState.class) {

					field.set(chnData, ChnState.valueOf(attr.getValue()));
				} else if (field.getType() == double.class) {

					field.set(chnData, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == long.class) {

					field.set(chnData, Long.parseLong(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(chnData, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == boolean.class) {

					field.set(chnData, Boolean.parseBoolean(attr.getValue()));
				} else if (field.getType() == ChnState.class) {

					field.set(chnData, ChnState.valueOf(attr.getValue()));
				} else {

					field.set(chnData, attr.getValue());
				}

			}
		} catch (Exception ex) {

			throw new AlertException(AlertCode.LOGIC, "读取设备通道携带数据文件失败:" + ex.getMessage());

		}

	}

	/**
	 * 通过反射读取通道状态文件
	 * 
	 * @author wavy_zheng 2021年3月23日
	 * @param element
	 * @param chn
	 * @throws AlertException
	 */
	private void readChnRuntimeState(Element element, Channel chn) throws AlertException {

		try {

			for (Attribute attr : element.attributes()) {

				System.out.println("name:" + attr.getName() + ",value=" + attr.getValue());
				Field field = null;
				try {
					field = Channel.class.getDeclaredField(attr.getName());
				} catch (Exception ex) {

					System.out.println("not suche field");
				}

				if (field == null) {

					continue;
				}
				field.setAccessible(true);

				if (field.getType() == AlertCode.class) {

					field.set(chn, AlertCode.valueOf(attr.getValue()));

				} else if (field.getType() == Date.class) {

					field.set(chn, CommonUtil.parseTime(attr.getValue(), "yyyy-MM-dd HH:mm:ss"));

				} else if (field.getType() == WorkMode.class) {

					field.set(chn, WorkMode.valueOf(attr.getValue()));
				} else if (field.getType() == ChnState.class) {

					field.set(chn, ChnState.valueOf(attr.getValue()));
				} else if (field.getType() == double.class) {

					field.set(chn, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == long.class) {

					field.set(chn, Long.parseLong(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(chn, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == boolean.class) {

					field.set(chn, Boolean.parseBoolean(attr.getValue()));
				} else if (field.getType() == State.class) {

					field.set(chn, State.valueOf(attr.getValue()));
				} else {

					field.set(chn, attr.getValue());
				}

			}
			// 查询子对象

			if (element.element("voltageOffsetProtectionData") != null) {

				ChannelData chnData = new ChannelData();
				readChnRuntimeData(element.element("voltageOffsetProtectionData"), chnData);
				chn.setVoltageOffsetProtectionData(chnData);
				System.out.println(chn.getVoltageOffsetProtectionData());
			}

		} catch (Exception ex) {

			throw new AlertException(AlertCode.LOGIC,
					"读取设备通道" + (chn.getDeviceChnIndex() + 1) + "运行状态文件失败:" + ex.getMessage());

		}

		/***
		 * 
		 * if (f.getType() == WorkMode.class || f.getType() == ChnState.class ||
		 * f.getType() == double.class || f.getType() == long.class || f.getType() ==
		 * Date.class || f.getType() == AlertCode.class || f.getType() == int.class ||
		 * f.getType() == boolean.class || f.getType() == State.class) {
		 * 
		 */

		/*
		 * chn.setState(ChnState.valueOf(element.attributeValue("state")));
		 * chn.setWorkMode(element.attributeValue("workMode") == null ? null :
		 * WorkMode.valueOf(element.attributeValue("workMode")));
		 * chn.setNextWorkmode(element.attributeValue("nextWorkmode") == null ? null :
		 * WorkMode.valueOf(element.attributeValue("nextWorkmode")));
		 * chn.setNextLoop(Integer.parseInt(element.attributeValue("nextLoop")));
		 * chn.setNextStep(Integer.parseInt(element.attributeValue("nextStep")));
		 * chn.setStepIndex(Integer.parseInt(element.attributeValue("stepIndex")));
		 * chn.setLoopIndex(Integer.parseInt(element.attributeValue("loopIndex")));
		 * chn.setAlertStepIndex(Integer.parseInt(element.attributeValue(
		 * "alertStepIndex")));
		 * 
		 * chn.setAlertCode(AlertCode.valueOf(element.attributeValue("alertCode")));
		 * chn.setLastPickupTime(null); try { chn.setProcedureStartTime(
		 * CommonUtil.parseTime(element.attributeValue("procedureStartTime"),
		 * "yyyy-MM-dd HH:mm:ss")); chn.setProcedureStopTime(
		 * CommonUtil.parseTime(element.attributeValue("procedureStopTime"),
		 * "yyyy-MM-dd HH:mm:ss"));
		 * 
		 * } catch (ParseException e) {
		 * 
		 * e.printStackTrace(); throw new AlertException(AlertCode.LOGIC, "读取设备通道" +
		 * (chn.getDeviceChnIndex() + 1) + "运行状态文件失败,无法解析最近次采集时间:" +
		 * element.attributeValue("lastComputerPickupTime")); }
		 * chn.setStepCapacity(Double.parseDouble(element.attributeValue("stepCapacity")
		 * ));
		 * chn.setStepEnergy(Double.parseDouble(element.attributeValue("stepEnergy")));
		 * chn.setAccumulateCapacity(Double.parseDouble(element.attributeValue(
		 * "accumulateCapacity")));
		 * chn.setAccumulateEnergy(Double.parseDouble(element.attributeValue(
		 * "accumulateEnergy")));
		 * chn.setStepElapseMiliseconds(Long.parseLong(element.attributeValue(
		 * "stepElapseMiliseconds")));
		 * chn.setReady(Boolean.parseBoolean(element.attributeValue("ready")));
		 * chn.setTotalSeconds(Long.parseLong(element.attributeValue("totalSeconds")));
		 * chn.setCharge(Boolean.parseBoolean(element.attributeValue("charge"))); //
		 * 充放电标志
		 */
	}

	/**
	 * 存储通道实时运行状态
	 * 
	 * @param chn
	 */
	private Element writeChnRuntimeState(Element parent, Channel chn) throws AlertException {

		Element child = parent.addElement("channel");

		Field[] fields = Channel.class.getDeclaredFields();
		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {

				continue;
			}
			f.setAccessible(true);
			if (f.getType() == WorkMode.class || f.getType() == ChnState.class || f.getType() == double.class
					|| f.getType() == long.class || f.getType() == Date.class || f.getType() == AlertCode.class
					|| f.getType() == int.class || f.getType() == boolean.class || f.getType() == State.class
					|| f.getType() == ChannelData.class) {

				try {

					if (f.get(chn) == null) {

						continue; // 空值不赋值
					}
					if (f.getType() == AlertCode.class) {

						child.addAttribute(f.getName(), ((AlertCode) f.get(chn)).name());
					} else if (f.getType() == Date.class) {

						child.addAttribute(f.getName(),
								CommonUtil.formatTime((Date) f.get(chn), "yyyy-MM-dd HH:mm:ss"));
					} else if (f.getType() == ChannelData.class) {

						writeChnRuntimeData(child, (ChannelData) f.get(chn));

					} else {

						child.addAttribute(f.getName(), f.get(chn).toString());
					}
				} catch (Exception e) {

					e.printStackTrace();
					throw new AlertException(AlertCode.LOGIC, "写入通道" + (chn.getDriverBoard().getDriverIndex() + 1) + "-"
							+ (chn.getChnIndex() + 1) + "运行状态文件发生错误");
				}
			}
		}
		return child;

	}

	/**
	 * 写入分区或设备的运行状态
	 * 
	 * @param cu
	 * @throws AlertException
	 */
	public void writeUnitRuntimeState(ControlUnit cu, String path) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				writeUnitRuntimeState(unit, path);
			}
		} else {

			if (path == null) {
				path = mainboard.getControlUnitCount() == 1 ? STRUCT_FILE_PATH + "/device"
						: STRUCT_FILE_PATH + "/" + cu.getIndex();
				path += "/runtime.xml";
			} else {

				path = "runtime.xml";
			}

			Document document = DocumentHelper.createDocument();

			Element root = document.addElement("root");

			Element cuElement = root.addElement("cu");
			cuElement.addAttribute("state", cu.getState().name());

			System.out.println("cu testname = " + cu.getTestName());
			cuElement.addAttribute("testName", cu.getTestName());
			cuElement.addAttribute("operateState", cu.getOperateState().name());
			cuElement.addAttribute("pressureChanged", cu.isPressureChanged() + "");
			cuElement.addAttribute("pressureComplete", cu.isPressureComplete() + "");
			cuElement.addAttribute("pressureCompleteTimeout", cu.getPressureCompleteTimeout() + "");

			List<Channel> list = cu.listAllChannels(null);
			for (int n = 0; n < list.size(); n++) {

				writeChnRuntimeState(cuElement, list.get(n));
			}
			try {
				XmlUtil.saveXml(path, document);
			} catch (IOException e) {

				e.printStackTrace();

			}
		}

	}

	/**
	 * 从文件读取保留的分区状态文件
	 * 
	 * @param cu
	 * @throws AlertException
	 */
	public void readUnitRuntimeState(ControlUnit cu) throws AlertException {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				readUnitRuntimeState(unit);
			}

		} else {

			String path = mainboard.getControlUnitCount() == 1 ? STRUCT_FILE_PATH + "/device"
					: STRUCT_FILE_PATH + "/" + cu.getIndex();
			path += "/runtime.xml";

			Document document;

			if (!new File(path).exists()) {
				// 当没有发生在线升级或断电暂停时，则不会生成runtime.xml文件

				return;
			}

			try {
				document = XmlUtil.loadXml(path);
			} catch (Exception e) {

				e.printStackTrace();
				document = DocumentHelper.createDocument();
				document.addElement("root");
			}

			Element root = document.getRootElement();
			Element cuElement = root.element("cu");
			if (cuElement == null) {

				return;

			}
			cu.setState(State.valueOf(cuElement.attributeValue("state")));
			cu.setOperateState(State.valueOf(cuElement.attributeValue("operateState")));
			cu.setTestName(cuElement.attributeValue("testName"));
			if (cuElement.attributeValue("pressureCompleteTimeout") != null) {

				cu.setPressureCompleteTimeout(Integer.parseInt(cuElement.attributeValue("pressureCompleteTimeout")));
			}
			if (cuElement.attributeValue("pressureComplete") != null) {

				cu.setPressureComplete(Boolean.parseBoolean(cuElement.attributeValue("pressureComplete")));
			}
			if (cuElement.attributeValue("pressureChanged") != null) {

				cu.setPressureChanged(Boolean.parseBoolean(cuElement.attributeValue("pressureChanged")));
			}

			// 更新掉电前的步次
			// int syncStepIndex = cuElement.attributeValue("syncStepIndex") == null ? 0
			// : Integer.parseInt(cuElement.attributeValue("syncStepIndex"));
			// int syncStepLoop = cuElement.attributeValue("syncStepLoop") == null ? 0
			// : Integer.parseInt(cuElement.attributeValue("syncStepLoop"));
			// if (syncStepIndex > 0 && syncStepLoop > 0) {
			//
			// System.out.println("load sync step index = " + syncStepIndex + ",sync step
			// loop = " + syncStepLoop);
			// cu.setSyncStep(cu.getProcedureStep(syncStepIndex));
			// if (cu.getSyncStep() != null) {
			//
			// cu.getSyncStep().loopIndex = syncStepLoop; // 从1开始表示循环号
			// }
			//
			// }

			List<Element> channels = cuElement.elements("channel");
			if (channels.size() != cu.getChannelCount()) {

				throw new AlertException(AlertCode.LOGIC,
						"分区状态文件保存的通道数" + channels.size() + "与实际分区通道数" + cu.getChannelCount() + "不符");
			}
			List<Channel> list = cu.listAllChannels(null);
			for (int n = 0; n < channels.size(); n++) {

				// 读取通道状态
				readChnRuntimeState(channels.get(n), list.get(n));
			}
			// 删除状态文件
			File file = new File(path);
			if (file.exists()) {

				file.delete();
			}

		}

	}

	/**
	 * 生成节能方案文件
	 * 
	 * @param esd
	 * @throws AlertException
	 */
	public void writeEnergySaveFile(EnergySaveData esd) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		Element element = root.element("energy");
		if (element != null) {

			root.remove(element);
		}
		element = root.addElement("energy");
		element.addAttribute("useSmartPower", esd.isUseSmartPower() + "");
		element.addAttribute("useSmartFan", esd.isUseSmartFan() + "");
		element.addAttribute("maxPowerWaitMin", esd.getMaxPowerWaitMin() + "");
		element.addAttribute("maxTempControlWaitMin", esd.getMaxTempControlWaitMin() + "");
		try {
			XmlUtil.saveXml(ENERGY_PATH, document);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存节能方案文件" + ENERGY_PATH + "错误");
		}

	}

	/**
	 * 读取节能方案文件
	 * 
	 * @param path
	 * @return
	 */
	public EnergySaveData readEnergySaveFile() {

		Document document = null;
		try {
			document = XmlUtil.loadXml(ENERGY_PATH);
		} catch (Exception e) {

			e.printStackTrace();
			// 自动生成一个默认文件
			EnergySaveData esd = new EnergySaveData();
			try {
				writeEnergySaveFile(esd);
			} catch (AlertException e1) {

				e1.printStackTrace();
			}
			return esd;
		}
		Element root = document.getRootElement();
		Element element = root.element("energy");
		EnergySaveData esd = new EnergySaveData();
		if (element == null) {

			return esd;
		}
		esd.setUseSmartPower(Boolean.parseBoolean(element.attributeValue("useSmartPower")));
		esd.setUseSmartFan(Boolean.parseBoolean(element.attributeValue("useSmartFan")));
		esd.setMaxPowerWaitMin(Integer.parseInt(element.attributeValue("maxPowerWaitMin")));
		esd.setMaxTempControlWaitMin(Integer.parseInt(element.attributeValue("maxTempControlWaitMin")));

		return esd;
	}

	// 温度保护配置
	private void writeTempProtect(Element parent, TempData protect) throws AlertException {

		Element device = parent.element("temperature");
		if (device == null) {

			device = parent.addElement("temperature");
		}

		Field[] fields = TempData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(protect).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入温度保护文件失败,无法写入属性:" + f.getName());
			}
		}

	}

	// 读取温度保护参数
	private TempData readTempProtect(Element parent) throws AlertException {

		Element dc = parent.element("temperature");

		TempData protect = new TempData();
		if (dc == null) {
			return new TempData();
		}

		for (Attribute attr : dc.attributes()) {

			try {

				if (attr.getName().equals("time") || attr.getName().equals("audioAlertOpen")) {

					continue; // 遗留属性不读
				}

				Field field = TempData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);

				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == boolean.class) {

					field.set(protect, Boolean.parseBoolean(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取温度保护文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;

	}

	/**
	 * 生成温控配置文件
	 * 
	 * @param tempData
	 * @throws AlertException
	 */
	public void writeTempControlFile(TempData tempData) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		writeTempProtect(root, tempData);
		try {
			XmlUtil.saveXml(TEMP_CONTROL_PATH, document);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存温控配置文件" + TEMP_CONTROL_PATH + "错误");
		}

	}

	/**
	 * 读取温控配置文件
	 * 
	 * @return
	 * @throws AlertException
	 */
	public TempData readTempControlFile() throws AlertException {

		Document document;
		try {
			document = XmlUtil.loadXml(TEMP_CONTROL_PATH);
		} catch (Exception e) {

			TempData tempData = new TempData();
			writeTempControlFile(tempData);

			e.printStackTrace();
			return tempData;
		}

		Element root = document.getRootElement();
		return readTempProtect(root);
	}

	public void writeBeepAlertFile(VoiceAlertData beep) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		Element device = root.element("beep");
		if (device == null) {

			device = root.addElement("beep");
		}

		Field[] fields = VoiceAlertData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				device.addAttribute(f.getName(), f.get(beep).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入蜂鸣控制文件失败,无法写入属性:" + f.getName());
			}
		}

		try {
			XmlUtil.saveXml(BEEP_PATH, document);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存蜂鸣配置文件" + BEEP_PATH + "错误");
		}
	}

	/**
	 * 读取蜂鸣控制文件
	 * 
	 * @return
	 * @throws AlertException
	 */
	public VoiceAlertData readBeepAlertFile() throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(BEEP_PATH);
		} catch (Exception e) {

			// e.printStackTrace();
			VoiceAlertData beep = new VoiceAlertData();
			writeBeepAlertFile(beep);
			return beep;
		}

		Element root = document.getRootElement();
		Element dc = root.element("beep");

		VoiceAlertData protect = new VoiceAlertData();
		if (dc == null) {
			return protect;
		}

		for (Attribute attr : dc.attributes()) {

			try {

				Field field = VoiceAlertData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(protect, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(protect, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == boolean.class) {

					field.set(protect, Boolean.parseBoolean(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取蜂鸣控制文件失败,无法读取属性:" + attr.getName());
			}
		}

		return protect;
	}

	/**
	 * 读取离线运行配置参数
	 * 
	 * @author wavy_zheng 2020年11月23日
	 * @return
	 * @throws AlertException
	 */
	public OfflineRunningData readOfflineRunningFile() throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(OFFLINE_RUNNING_PATH);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			// 默认生成一个
			OfflineRunningData offline = new OfflineRunningData();
			writeOfflineRunningFile(offline); // 写入默认文件
			return offline;
		}

		Element root = document.getRootElement();
		Element node = root.element("offlineRunning");

		OfflineRunningData offline = new OfflineRunningData();
		if (node == null) {

			return new OfflineRunningData();
		}

		for (Attribute attr : node.attributes()) {

			try {

				Field field = OfflineRunningData.class.getDeclaredField(attr.getName());
				field.setAccessible(true);
				if (field.getType() == double.class) {

					field.set(offline, Double.parseDouble(attr.getValue()));
				} else if (field.getType() == int.class) {

					field.set(offline, Integer.parseInt(attr.getValue()));
				} else if (field.getType() == SwitchState.class) {

					field.set(offline, SwitchState.valueOf(attr.getValue()));
				}
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "读取离线参数文件失败,无法读取属性:" + attr.getName());
			}
		}

		return offline;
	}

	public void readIdentityFile() throws AlertException {

		// Document document = null;
		// try {
		// document = XmlUtil.loadXml(IDENTITY_PATH);
		// } catch (Exception e1) {
		//
		// logger.error("can not read identity.xml:" + e1.getMessage());
		// logger.error(CommonUtil.getThrowableException(e1));
		// return;
		// }
		// Element root = document.getRootElement();
		// List<Element> nodes = root.element("logics").elements("logic");
		// for (int n = 0; n < nodes.size(); n++) {
		//
		// LogicBoard lb = mainboard.getLogicBoards().get(n);
		// Element node = nodes.get(n);
		// lb.setUuid(node.attributeValue("uuid"));
		// List<Element> drivers = node.elements("driver");
		// for (int i = 0; i < lb.getDrivers().size(); i++) {
		//
		// DriverBoard db = lb.getDrivers().get(i);
		// db.setLogicUuid(drivers.get(i).attributeValue("logicUuid") == null ? ""
		// : drivers.get(i).attributeValue("logicUuid"));
		// db.setCheckUuid(drivers.get(i).attributeValue("checkUuid") == null ? ""
		// : drivers.get(i).attributeValue("checkUuid"));
		//
		// }
		//
		// }
		// nodes = root.element("checks").elements("check");
		// for (int n = 0; n < nodes.size(); n++) {
		//
		// CheckBoard cb = mainboard.getCheckBoards().get(n);
		// Element node = nodes.get(n);
		// cb.setUuid(node.attributeValue("uuid") == null ? "" :
		// node.attributeValue("uuid"));
		// }

	}

	/**
	 * 写入出厂配置文件
	 * 
	 * @author wavy_zheng 2021年1月9日
	 * @throws AlertException
	 */
	public void writeFactoryFile() throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		root.addAttribute("factoryDatetime",
				CommonUtil.formatTime(mainboard.getFactoryDatetime(), "yyyy-MM-dd HH:mm:ss"));

		try {
			XmlUtil.saveXml(FACTORY_PATH, document);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存出厂配置文件" + FACTORY_PATH + "错误");
		}

	}

	/**
	 * 读取出厂配置
	 * 
	 * @author wavy_zheng 2021年1月9日
	 * @return
	 * @throws AlertException
	 */
	public boolean readFactoryFile() throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(FACTORY_PATH);
		} catch (Exception e) {

			logger.info("create factory datetime:");
			return false;
		}
		Element root = document.getRootElement();
		try {
			Date date = CommonUtil.parseTime(root.attributeValue("factoryDatetime"), "yyyy-MM-dd HH:mm:ss");
			mainboard.setFactoryDatetime(date);
		} catch (ParseException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "读取出厂配置失败");
		}

		return true;
	}

	/**
	 * 写入所有校准ID标记
	 * 
	 * @author wavy_zheng 2021年1月9日
	 * @throws AlertException
	 */
	public void writeIdentityFile() throws AlertException {

		// Document document = DocumentHelper.createDocument();
		// Element root = document.addElement("root");
		// Element logics = root.addElement("logics");
		// Element checks = root.addElement("checks");
		// for (int n = 0; n < mainboard.getLogicBoards().size(); n++) {
		//
		// LogicBoard lb = mainboard.getLogicBoards().get(n);
		// Element logic = logics.addElement("logic");
		// logic.addAttribute("index", n + "");
		// logic.addAttribute("uuid", lb.getUuid());
		//
		// // 添加驱动板校准ID
		// for (int i = 0; i < lb.getDrivers().size(); i++) {
		//
		// DriverBoard db = lb.getDrivers().get(i);
		// Element driver = logic.addElement("driver");
		// driver.addAttribute("index", i + "");
		// driver.addAttribute("logicUuid", db.getLogicUuid());
		// driver.addAttribute("checkUuid", db.getCheckUuid());
		// }
		//
		// }
		//
		// for (int n = 0; n < mainboard.getCheckBoards().size(); n++) {
		//
		// CheckBoard cb = mainboard.getCheckBoards().get(n);
		// Element check = checks.addElement("check");
		// check.addAttribute("index", n + "");
		// check.addAttribute("uuid", cb.getUuid());
		// }
		//
		// try {
		// XmlUtil.saveXml(IDENTITY_PATH, document);
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// throw new AlertException(AlertCode.LOGIC, "保存uuid配置文件" + IDENTITY_PATH +
		// "错误");
		// }
	}

	/**
	 * 读取寿命文件
	 * 
	 * @author wavy_zheng 2021年2月2日
	 * @throws AlertException
	 */
	public void readLifeFile() throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(LIFE_PATH);
		} catch (Exception e1) {
			// e1.printStackTrace();
			return;
		}
		Element root = document.getRootElement();
		mainboard.setRunMiliseconds(
				root.attributeValue("life") == null ? 0 : Long.parseLong(root.attributeValue("life")));

		List<Element> nodes = root.element("drivers").elements("driver");
		for (int n = 0; n < nodes.size(); n++) {

			DriverBoard db = mainboard.getDriverByIndex(n);
			Element node = nodes.get(n);
			db.setRunMiliseconds(node.attributeValue("life") == null ? 0 : Long.parseLong(node.attributeValue("life")));
		}

		if (root.element("inverters") != null) {
			nodes = root.element("inverters").elements("inverter");
			for (int n = 0; n < nodes.size(); n++) {

				PowerManager manager = Context.getAccessoriesService().getPowerManager();
				Element node = nodes.get(n);
				manager.findPowerByIndex(n).setRunMiliseconds(
						node.attributeValue("life") == null ? 0 : Long.parseLong(node.attributeValue("life")));
			}
		}

		if (root.element("coolFans") != null) {

			nodes = root.element("coolFans").elements("coolFan");
			for (int n = 0; n < nodes.size(); n++) {

				FanManager manager = Context.getAccessoriesService().getFanManager();
				Element node = nodes.get(n);
				if (manager != null) {
					manager.getHeatFanByIndex(n).setRunMiliseconds(
							node.attributeValue("life") == null ? 0 : Long.parseLong(node.attributeValue("life")));
				}
			}

		}

		if (root.element("turbos") != null) {

			nodes = root.element("turbos").elements("turbo");
			for (int n = 0; n < nodes.size(); n++) {

				FanManager manager = Context.getAccessoriesService().getFanManager();
				Element node = nodes.get(n);
				manager.getTurboFanByIndex(n).setRunMiliseconds(
						node.attributeValue("life") == null ? 0 : Long.parseLong(node.attributeValue("life")));
			}
		}

	}

	/**
	 * 写入驱动板通道顺序定义
	 * 
	 * @author wavy_zheng 2021年4月26日
	 * @throws AlertException
	 */
	public void writeDriverChnIndexDefineFile(DriverChnIndexDefineData dcdd) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		root.addAttribute("enable", dcdd.isEnable() + "");
		for (int n = 0; n < dcdd.getChnIndexDefineList().size(); n++) {

			Element element = root.addElement("define");
			element.addAttribute("index", n + "");
			element.addAttribute("mapIndex", dcdd.getChnIndexDefineList().get(n) + "");

		}
		try {
			XmlUtil.saveXml(DRIVER_CHN_DEFINE_PATH, document);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存驱动板通道序号定义配置文件" + DRIVER_CHN_DEFINE_PATH + "错误");
		}
	}

	public PoleData readPoleFile(File file) throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(file.getAbsolutePath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;

		}

		Element root = document.getRootElement();
		return readPoleProtect(root);

	}

	/**
	 * 读取设备保护
	 * 
	 * @author wavy_zheng 2021年6月21日
	 * @param file
	 * @return
	 * @throws AlertException
	 */
	public DeviceProtectData readDeviceProtectFile(File file) throws AlertException {

		Document document = null;
		try {
			document = XmlUtil.loadXml(file.getAbsolutePath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;

		}

		Element root = document.getRootElement();
		return readDeviceProtect(root);

	}

	/**
	 * 写入极性保护文件
	 * 
	 * @author wavy_zheng 2021年6月21日
	 * @param file
	 * @param pole
	 * @throws AlertException
	 */
	public void writePoleFile(File file, PoleData pole) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		writePoleProtect(root, pole);

		try {
			XmlUtil.saveXml(file.getPath(), document);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存极性配置文件pole.xml错误");
		}
	}

	/**
	 * 写入超压保护文件
	 * 
	 * @author wavy_zheng 2021年6月21日
	 * @param file
	 * @param dpd
	 * @throws AlertException
	 */
	public void writeDeviceProtectFile(File file, DeviceProtectData dpd) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		writeDeviceProtect(root, dpd);

		try {
			XmlUtil.saveXml(file.getPath(), document);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存超压保护文件device.xml错误");
		}

	}

	/**
	 * 写入配件寿命文件
	 * 
	 * @author wavy_zheng 2021年2月2日
	 * @throws AlertException
	 */
	public void writeLifeFile() throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		root.addAttribute("life", mainboard.getRunMiliseconds() + "");
		Element drivers = root.addElement("drivers");
		for (int n = 0; n < MainBoard.startupCfg.getDriverCount(); n++) {

			DriverBoard db = mainboard.getDriverByIndex(n);
			Element driver = drivers.addElement("driver");
			driver.addAttribute("index", n + "");
			driver.addAttribute("life", db.getRunMiliseconds() + "");

		}

		Element inverters = root.addElement("inverters");
		Element coolFans = root.addElement("coolFans");
		Element turbos = root.addElement("turbos");

		if (Context.getAccessoriesService().getPowerManager() != null) {
			// 电源
			for (int n = 0; n < Context.getAccessoriesService().getPowerManager().getChargePowerCount(); n++) {

				InverterPower ip = Context.getAccessoriesService().getPowerManager().findPowerByIndex(n);
				Element inverter = inverters.addElement("inverter");
				inverter.addAttribute("index", n + "");
				inverter.addAttribute("life", ip.getRunMiliseconds() + "");
			}

		}

		if (Context.getAccessoriesService().getFanManager() != null) {
			// 散热风机
			for (int n = 0; n < Context.getAccessoriesService().getFanManager().getHeatFanCount(); n++) {

				Fan fan = Context.getAccessoriesService().getFanManager().getHeatFanByIndex(n);
				Element coolFan = coolFans.addElement("coolFan");
				coolFan.addAttribute("index", n + "");
				coolFan.addAttribute("life", fan.getRunMiliseconds() + "");
			}
			// 涡轮风机
			for (int n = 0; n < Context.getAccessoriesService().getFanManager().getTurboFanCount(); n++) {

				Fan fan = Context.getAccessoriesService().getFanManager().getTurboFanByIndex(n);
				Element turbo = turbos.addElement("turbo");
				turbo.addAttribute("index", n + "");
				turbo.addAttribute("life", fan.getRunMiliseconds() + "");
			}

		}

		try {
			XmlUtil.saveXml(LIFE_PATH, document);
		} catch (IOException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存配件寿命配置文件" + LIFE_PATH + "错误");
		}

	}

	/**
	 * 配置离线运行方式
	 * 
	 * @author wavy_zheng 2020年11月23日
	 * @param ord
	 * @throws AlertException
	 */
	public void writeOfflineRunningFile(OfflineRunningData ord) throws AlertException {

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");
		Element node = root.addElement("offlineRunning");

		Field[] fields = OfflineRunningData.class.getDeclaredFields();
		for (Field f : fields) {

			f.setAccessible(true);
			try {
				node.addAttribute(f.getName(), f.get(ord).toString());
			} catch (Exception e) {

				e.printStackTrace();
				throw new AlertException(AlertCode.LOGIC, "写入离线运行参数文件失败,无法写入属性:" + f.getName());
			}
		}
		try {
			XmlUtil.saveXml(OFFLINE_RUNNING_PATH, document);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "保存离线运行配置文件" + OFFLINE_RUNNING_PATH + "错误");
		}

	}

	/**
	 * 读取分区保护配置文件
	 * 
	 * @param mainBoard
	 * @throws AlertException
	 */
	public static ParameterName readProtectFile(String path) throws AlertException {

		Document doc;
		try {
			doc = XmlUtil.loadXml(path);
		} catch (Exception e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "加载" + path + "发生错误:" + e.getMessage());
		}
		ParameterName pn = readParameterNameFile(doc);
		return pn;
	}

	/**
	 * 读取保护方案
	 * 
	 * @return
	 * @throws AlertException
	 */
	public static ParameterName readParameterNameFile(Document doc) throws AlertException {

		Element pnElement = doc.getRootElement();

		ParameterName pn = new ParameterName(WorkType.valueOf(pnElement.attributeValue("workType")));
		pn.setName(pnElement.attributeValue("name"));
		// pn.setWorkType(WorkType.valueOf(pnElement.attributeValue("workType")));
		pn.setDefaultPlan(Boolean.parseBoolean(pnElement.attributeValue("defaultPlan")));
		pn.setCcProtect(readCCProtect(pnElement));
		pn.setCvProtect(readCVProtect(pnElement));
		pn.setDcProtect(readDCProtect(pnElement));
		pn.setSlpProtect(readSleepProtect(pnElement));
		pn.setDeviceProtect(readDeviceProtect(pnElement));
		pn.setPoleProtect(readPoleProtect(pnElement));
		pn.setFirstCCProtect(readFirstCCProtect(pnElement));
		pn.setCheckVoltProtect(readCheckVoltProtect(pnElement));
		pn.setStartEndCheckProtect(readStartEndCheckProtect(pnElement));
		return pn;

	}

	/**
	 * 写入分区保护配置参数
	 * 
	 * @param lb
	 */
	public void writeProtectFile(File file, ParameterName pn) throws AlertException {

		int index = file.getPath().lastIndexOf(File.separator);
		if (index != -1) {

			new File(file.getPath().substring(0, index)).mkdirs(); // 创建目录
		}

		Document doc = DocumentHelper.createDocument();

		writeParameterNameFile(doc, pn);

		try {
			XmlUtil.saveXml(file.getAbsolutePath(), doc);
		} catch (IOException e) {

			e.printStackTrace();

			throw new AlertException(AlertCode.LOGIC, "写入" + file.getAbsolutePath() + "发生错误:" + e.getMessage());
		}

	}

	/**
	 * 写入分区离线缓存数据
	 * 
	 * @author wavy_zheng 2020年12月16日
	 * @param cu
	 */
	public void writeOfflineToDisk(ControlUnit cu) {

		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				writeOfflineToDisk(unit);
			}
		} else {

			logger.info("write controlunit " + cu.getIndex() + " offline date to file");

			List<Channel> channels = cu.listAllChannels(null);
			for (Channel chn : channels) {

				writeOfflineToDisk(chn);
			}

		}

	}

	/**
	 * 读取离线硬盘缓存数据
	 * 
	 * @author wavy_zheng 2021年1月4日
	 * @param cu
	 * @return
	 */
	public Map<Integer, List<ChannelData>> readOfflineFromDisk(ControlUnit cu) {

		Map<Integer, List<ChannelData>> map = new HashMap<>();
		if (cu == null) {

			for (ControlUnit unit : mainboard.getControls()) {

				readOfflineFromDisk(unit);
			}
		} else {

			logger.info("read controlunit " + cu.getIndex() + " offline date from file");

			List<Channel> channels = cu.listAllChannels(null);
			for (Channel chn : channels) {

				map.put(chn.getDeviceChnIndex(), readOfflineFromDisk(chn));
			}

		}

		return map;

	}

	/**
	 * 读取磁盘的离线文件数据
	 * 
	 * @return
	 */
	public synchronized List<ChannelData> readOfflineFromDisk(Channel chn) {

		if (!new File(OFFLINE_DIR_PATH + "/" + chn.getDeviceChnIndex() + ".cvs").exists()) {

			return new ArrayList<ChannelData>();
		}

		CvsUtil cvsUtil = new CvsUtil(OFFLINE_DIR_PATH + "/" + chn.getDeviceChnIndex() + ".cvs", true);
		List<String[]> readBuff = cvsUtil.readRecord();
		List<ChannelData> list = new ArrayList<ChannelData>();
		for (String[] row : readBuff) {

			ChannelData chnData = new ChannelData();
			int index = 0;
			// 步次
			chnData.setStepIndex(Integer.parseInt(row[index++]));
			// 循环
			chnData.setLoopIndex(Integer.parseInt(row[index++]));
			// 状态
			chnData.setState(ChnState.valueOf(row[index++]));
			// 模式
			chnData.setWorkMode(WorkMode.valueOf(row[index++]));
			// 电压
			chnData.setVoltage(Double.parseDouble(row[index++]));
			// 电流
			chnData.setCurrent(Double.parseDouble(row[index++]));
			// 容量
			chnData.setCapacity(Double.parseDouble(row[index++]));
			// 能量
			chnData.setEnergy(Double.parseDouble(row[index++]));
			// 累计容量
			chnData.setAccumulateCapacity(Double.parseDouble(row[index++]));
			// 累计能量
			chnData.setAccumulateEnergy(Double.parseDouble(row[index++]));
			// 功率电压
			chnData.setPowerVoltage(Double.parseDouble(row[index++]));
			// 备份电压
			chnData.setDeviceVoltage(Double.parseDouble(row[index++]));
			// 报警码
			chnData.setAlertCode(AlertCode.valueOf(row[index++]));
			// 报警电压
			chnData.setAlertVoltage(Double.parseDouble(row[index++]));
			// 报警电流
			chnData.setAlertCurrent(Double.parseDouble(row[index++]));
			// 步次流逝时间
			chnData.setTimeStepSpend(Long.parseLong(row[index++]));
			// 流程流逝时间
			chnData.setTimeTotalSpend(Long.parseLong(row[index++]));
			// 时间
			try {
				chnData.setDate(CommonUtil.parseTime(row[index++], "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 分区序号
			chnData.setUnitIndex(chn.getControlUnitIndex());
			chnData.setChannelIndex(chn.getDeviceChnIndex());

			list.add(chnData);
		}

		// 删除
		new File(cvsUtil.getFilePath()).delete();

		return list;

	}

	/**
	 * 将通道离线数据写入磁盘
	 */
	public synchronized void writeOfflineToDisk(Channel chn) {

		new File(OFFLINE_DIR_PATH).mkdirs();

		CvsUtil cvsUtil = new CvsUtil(OFFLINE_DIR_PATH + "/" + chn.getDeviceChnIndex() + ".cvs", true);
		for (int n = 0; n < chn.getOfflineCaches().size(); n++) {

			ChannelData chnData = chn.getOfflineCaches().get(n);
			StringBuffer sb = new StringBuffer();
			List<Object> row = new ArrayList<Object>();
			// 步次
			row.add(chnData.getStepIndex());
			// 循环号
			row.add(chnData.getLoopIndex());
			// 状态
			row.add(chnData.getState().name());
			// 模式
			row.add(chnData.getWorkMode().name());
			// 电压
			row.add(CommonUtil.formatNumber(chnData.getVoltage(),
					MainBoard.startupCfg.getProtocol().voltageResolution));
			// 电流
			row.add(CommonUtil.formatNumber(chnData.getCurrent(),
					MainBoard.startupCfg.getProtocol().currentResolution));
			// 容量
			row.add(CommonUtil.formatNumber(chnData.getCapacity(),
					MainBoard.startupCfg.getProtocol().capacityResolution));
			// 能量
			row.add(CommonUtil.formatNumber(chnData.getEnergy(), MainBoard.startupCfg.getProtocol().energyResolution));
			// 累计容量
			row.add(CommonUtil.formatNumber(chnData.getAccumulateCapacity(),
					MainBoard.startupCfg.getProtocol().capacityResolution));
			// 累计能量
			row.add(CommonUtil.formatNumber(chnData.getAccumulateEnergy(),
					MainBoard.startupCfg.getProtocol().energyResolution));
			// 功率电压
			row.add(CommonUtil.formatNumber(chnData.getPowerVoltage(),
					MainBoard.startupCfg.getProtocol().voltageResolution));
			// 设备备份电压
			row.add(CommonUtil.formatNumber(chnData.getDeviceVoltage(),
					MainBoard.startupCfg.getProtocol().voltageResolution));
			// 报警码
			row.add(chnData.getAlertCode().name());
			// 报警电压
			row.add(CommonUtil.formatNumber(chnData.getAlertVoltage(),
					MainBoard.startupCfg.getProtocol().voltageResolution));
			// 报警电流
			row.add(CommonUtil.formatNumber(chnData.getAlertVoltage(),
					MainBoard.startupCfg.getProtocol().currentResolution));
			// 步次流逝时间s
			row.add(chnData.getTimeStepSpend());
			// 流程流逝时间s
			row.add(chnData.getTimeTotalSpend());
			// 时间
			row.add(CommonUtil.formatTime(chnData.getDate(), "yyyy-MM-dd HH:mm:ss"));

			cvsUtil.writeRecord(row.toArray());
		}
		cvsUtil.flush(); // 写入磁盘
	}

}
