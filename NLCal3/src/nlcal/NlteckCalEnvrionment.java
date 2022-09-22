package nlcal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.utils.CommonUtil;

/**
 * 上位机配置对象
 * 
 * @author caichao_tang
 *
 */
public class NlteckCalEnvrionment {
	private final static String FILE_PATH = "config/nlteckCal.xml";
	public static List<Device> deviceList;
	public static List<CalBox> calBoxList;
	public static double maxMeterOffset;
	public static double maxAdcOffset;
	public static double minCalculateCurrent;
	public static double maxCalculateCurrent;
	public static double minCalculateVoltage;
	public static double maxCalculateVoltage;
	public static boolean isDevelopMode;
	public static boolean isMatchMode;

	/**
	 * 加载配置文件到配置类
	 * 
	 * @param filePath
	 * @throws DocumentException
	 */
	public static void loadXML() throws DocumentException {

		Document document = null;
		document = new SAXReader().read(new File(FILE_PATH));

		Element rootElement = document.getRootElement();
		isDevelopMode = Boolean.parseBoolean(rootElement.attribute("isDevelopMode").getValue());
		isMatchMode = Boolean.parseBoolean(rootElement.attribute("isMatchMode").getValue());

		Element configElement = rootElement.element("config");

		Element calculateConfigElement = configElement.element("calculateConfig");
		maxMeterOffset = Double.parseDouble(calculateConfigElement.attribute("maxMeterOffset").getValue());
		maxAdcOffset = Double.parseDouble(calculateConfigElement.attribute("maxAdcOffset").getValue());
		minCalculateCurrent = Double.parseDouble(calculateConfigElement.attribute("minCalculateCurrent").getValue());
		maxCalculateCurrent = Double.parseDouble(calculateConfigElement.attribute("maxCalculateCurrent").getValue());
		minCalculateVoltage = Double.parseDouble(calculateConfigElement.attribute("minCalculateVoltage").getValue());
		maxCalculateVoltage = Double.parseDouble(calculateConfigElement.attribute("maxCalculateVoltage").getValue());

		Element hardwareElement = rootElement.element("hardware");

		Element devicesElement = hardwareElement.element("devices");

		Element calBoxesElement = hardwareElement.element("calBoxes");
		List<Element> calBoxElementList = calBoxesElement.elements();
		calBoxList = new ArrayList<>();
		for (Element calBoxElement : calBoxElementList) {
			CalBox calBox = new CalBox();
			calBox.setName(calBoxElement.attribute("name").getValue());
			calBox.setIp(calBoxElement.attribute("ip").getValue());
			calBox.setMeterIp(calBoxElement.attribute("meterIp").getValue());
			calBox.setScreenIp(calBoxElement.attribute("screenIp").getValue());
			calBox.setMac(calBoxElement.attribute("mac").getValue());
			Device tempDevice = new Device();
			tempDevice.setMac(calBoxElement.attribute("deviceMac").getValue());
			calBox.setDevice(tempDevice);
			int calNum = Integer.parseInt(calBoxElement.attribute("calNum").getValue());
			int calState = Integer.parseInt(calBoxElement.attribute("calState").getValue());
			int chnNumInCal = Integer.parseInt(calBoxElement.attribute("chnNumInCal").getValue());
			initCalBox(calBox, calNum, calState, chnNumInCal);
			calBoxList.add(calBox);
		}

		List<Element> devicElementList = devicesElement.elements();
		deviceList = new ArrayList<>();
		for (Element devicElement : devicElementList) {
			Device device = new Device();
			device.setName(devicElement.attribute("name").getValue());
			device.setIp(devicElement.attribute("ip").getValue());
			device.setMac(devicElement.attribute("mac").getValue());
			int logicNum = Integer.parseInt(devicElement.attribute("logicNum").getValue());
			int logicState = Integer.parseInt(devicElement.attribute("logicState").getValue());
			int driverNumInLogic = Integer.parseInt(devicElement.attribute("driverNumInLogic").getValue());
			int chnNumInDriver = Integer.parseInt(devicElement.attribute("chnNumInDriver").getValue());
			//initDevice(device, logicNum, logicState, driverNumInLogic, chnNumInDriver);
			deviceList.add(device);
		}

		// 进行绑定

		for (CalBox calBox : calBoxList) {
			for (Device device : deviceList) {
				if (device.getMac().equals(calBox.getDevice().getMac())) {
					calBox.setDevice(device);
					device.getCalBoxList().add(calBox);
				}
			}
			if (!deviceList.contains(calBox.getDevice())) {
				calBox.setDevice(null);
			}
		}

	}

	/**
	 * Workbench配置以及当前类写入到XML文件
	 * 
	 * @param filePath
	 */
	public static void writeXML() {
		try {
			Document document = DocumentHelper.createDocument();
			// 根节点
			Element rootElement = document.addElement("nlteckCal");
			rootElement.addAttribute("isDevelopMode", isDevelopMode + "");
			rootElement.addAttribute("isMatchMode", isMatchMode + "");

			Element configElement = rootElement.addElement("config");

			Element calculateConfigElement = configElement.addElement("calculateConfig");
			calculateConfigElement.addAttribute("maxMeterOffset", maxMeterOffset + "");
			calculateConfigElement.addAttribute("maxAdcOffset", maxAdcOffset + "");
			calculateConfigElement.addAttribute("minCalculateCurrent", minCalculateCurrent + "");
			calculateConfigElement.addAttribute("maxCalculateCurrent", maxCalculateCurrent + "");
			calculateConfigElement.addAttribute("minCalculateVoltage", minCalculateVoltage + "");
			calculateConfigElement.addAttribute("maxCalculateVoltage", maxCalculateVoltage + "");

			Element hardwareElement = rootElement.addElement("hardware");

			Element devicesElement = hardwareElement.addElement("devices");

			for (Device device : WorkBench.deviceList) {
				Element deviceElement = devicesElement.addElement("device");
				deviceElement.addAttribute("name", device.getName());
				deviceElement.addAttribute("ip", device.getIp());
				deviceElement.addAttribute("mac", device.getMac());
				deviceElement.addAttribute("logicNum", device.getLogicBoardList().size() + "");
				String string = "";
				for (LogicBoard logicBoard : device.getLogicBoardList()) {
					string = logicBoard.isOpen() ? "1" : "0" + string;
				}
				deviceElement.addAttribute("logicState", Integer.parseInt(string.isEmpty() ? "0" : string, 2) + "");
				deviceElement.addAttribute("driverNumInLogic",
						device.getLogicBoardList().get(0).getDrivers().size() + "");
				deviceElement.addAttribute("chnNumInDriver",
						device.getLogicBoardList().get(0).getDrivers().get(0).getChannels().size() + "");
			}

			Element calBoxesElement = hardwareElement.addElement("calBoxes");

			for (CalBox calBox : calBoxList) {
				Element calBoxElement = calBoxesElement.addElement("calBox");
				calBoxElement.addAttribute("name", calBox.getName());
				calBoxElement.addAttribute("ip", calBox.getIp());
				calBoxElement.addAttribute("deviceIp", calBox.getDevice() == null ? "" : calBox.getDevice().getIp());
				calBoxElement.addAttribute("meterIp", calBox.getMeterIp());
				calBoxElement.addAttribute("screenIp", calBox.getScreenIp());
				calBoxElement.addAttribute("mac", calBox.getMac());
				calBoxElement.addAttribute("deviceMac", calBox.getDevice() == null ? "" : calBox.getDevice().getMac());
				calBoxElement.addAttribute("calNum", calBox.getCalBoardList().size() + "");
				String string = "";
				for (CalBoard calBoard : calBox.getCalBoardList()) {
					string = calBoard.isOpen() ? "1" : "0" + string;
				}
				calBoxElement.addAttribute("calState", Integer.parseInt(string.isEmpty() ? "0" : string, 2) + "");
				calBoxElement.addAttribute("chnNumInCal", calBox.getCalBoardList().get(0).getChannelList().size() + "");
			}
			if (FILE_PATH == null || FILE_PATH.isEmpty()) {
				return;
			}
			// 删除同名文件
			new File(FILE_PATH).delete();
			// 输出格式刷
			OutputFormat outputFormat = OutputFormat.createPrettyPrint();
			// 设置文件编码
			outputFormat.setEncoding("UTF-8");
			// 设置文件的输出流
			OutputStream outputStream;
			outputStream = new FileOutputStream(FILE_PATH);
			// 创建xmlWriter
			XMLWriter xmlWriter = new XMLWriter(outputStream, outputFormat);
			// 写入文件到XML
			xmlWriter.write(document);
			// 清空缓存，关闭资源
			xmlWriter.flush();
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化一个设备
	 * 
	 * @param mainBoard
	 * @param logicNum
	 * @param logicState
	 * @param driverNumInLogic
	 * @param chnNumInDriver
	 */
//	public static void initDevice(Device device, int logicNum, int logicState, int driverNumInLogic,
//			int chnNumInDriver) {
//		
//		device.setChnNumInDriver(chnNumInDriver);
//		device.setLogicNum(logicNum);
//		device.setLogicState(logicState);
//		device.setDriverNumInLogic(driverNumInLogic);
//		
//		
//		List<Integer> logicSignList = CommonUtil.numToBinaryIntList(logicState, logicNum);
//		// 逻辑板
//		List<LogicBoard> logicBoardList = new ArrayList<>();
//		for (int i = 0; i < logicNum; i++) {
//			LogicBoard logicBoard = new LogicBoard();
//			logicBoard.setLogicIndex(i);
//			logicBoard.setDevice(device);
//			logicBoard.setOpen(logicSignList.get(i) == 0 ? false : true);
//			// 驱动板
//			List<DriverBoard> driverBoardList = new ArrayList<>();
//			for (int j = 0; j < driverNumInLogic; j++) {
//				DriverBoard driverBoard = new DriverBoard();
//				driverBoard.setDriverIndex(j);
//				driverBoard.setLogicBoard(logicBoard);
//				driverBoard.setOpen(true);
//				// 通道
//				List<Channel> channelList = new ArrayList<>();
//				for (int k = 0; k < chnNumInDriver; k++) {
//					Channel channel = new Channel();
//					channel.setChannelIndexInDriver(k);
//					channel.setDriver(driverBoard);
//					channelList.add(channel);
//				}
//				driverBoard.setChannels(channelList);
//				driverBoardList.add(driverBoard);
//			}
//			logicBoard.setDrivers(driverBoardList);
//			logicBoardList.add(logicBoard);
//		}
//		device.setLogicBoardList(logicBoardList);
//	}

	/**
	 * 初始化校准箱
	 * 
	 * @param calBox
	 * @param calNum
	 */
	public static void initCalBox(CalBox calBox, int calNum, int calState, int chnNumInCal) {
		List<Integer> calSignList = CommonUtil.numToBinaryIntList(calState, calNum);
		// 校准版
		List<CalBoard> calBoardList = new ArrayList<>();
		for (int i = 0; i < calNum; i++) {
			CalBoard calBoard = new CalBoard(calBox , i, true);
			calBoard.setCalBox(calBox);
			calBoard.setOpen(calSignList.get(i) == 0 ? false : true);
			// 校准板通道
			List<Channel> channelList = new ArrayList<>();
			for (int j = 0; j < chnNumInCal; j++) {
				Channel channel = new Channel();
				channelList.add(channel);
				channel.setChannelIndexInCal(j);
				channel.setCalBoard(calBoard);
			}
			calBoard.setChannelList(channelList);
			calBoardList.add(calBoard);
		}
		calBox.setCalBoardList(calBoardList);
	}

}
