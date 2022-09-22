package com.nlteck.firmware;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.mina.core.session.IoSession;
import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.nlteck.model.BaseCfg;
import com.nlteck.model.TestDot;
import com.nlteck.parts.CalibrateConsolePart;
import com.nlteck.parts.ChannelConsolePart;
import com.nlteck.parts.DebugModePart;
import com.nlteck.parts.DriverConsolePart;
import com.nlteck.parts.LogicConsolePart;
import com.nlteck.service.CalboxService.CalboxListener;
import com.nlteck.service.CalboxService.MatchState;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogDebugPushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.PCWorkform.MatchStateData;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.TestMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.workform.MainHeartBeatData;
import com.nltecklib.utils.XmlUtil;

import nlcal.NlteckCalEnvrionment;

/**
 * 校准箱的抽象
 * 
 * @author caichao_tang
 *
 */
public class CalBox {

	public static class CalboardInfo {

		public int index;
		public int chnCount;

	}

	public static final String RANGE_CURRENT_PRECISION_PATH = "calCfg/workformCfg/rangeCurrentPrecision.xml";

	public final static int PORT = 8151;
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SSS");
	private boolean isHeatBeating;
	private String name;
	private String ip;
	private String screenIp;
	private String mac;
	private Device device;
	private int id;
	// private int calBoardCount;
	private String meterIp;
	private List<String> meterIps = new ArrayList<>(); // 关联的万用表
	private String info;

	private int bindFlag;

	private int beatCount;

	private List<CalBoard> calBoardList = new ArrayList<>();
	private CalibrateCoreWorkMode workMode = CalibrateCoreWorkMode.NONE;
	private Entity entity = new Entity();
	private NetworkConnector connector;
	private int heartBeatExceptionTotal;
	private ScheduledExecutorService heartThread;
	private Map<String, Decorator> receiveBufferMap = new ConcurrentHashMap<>();
	private static BlockingQueue<Decorator> pushDataBlockingQueue = new ArrayBlockingQueue<Decorator>(1000);

	private CalculatePlanData calculatePlan; // 当前校准箱内绑定的计量方案
	private CalibrateCoreWorkMode mode; // 当前工作模式
	private TestMode testMode; // 当前测试模式
	private MatchState matchState = MatchState.NONE;

	// 存着各档位和模式的电阻系数
	private List<CalResistanceDebugData> resisterFactors = new ArrayList<>();

	private RangeCurrentPrecisionData rangeCurrentPrecision; // 电流和精度档位的关系

	/**
	 * 从MAP缓存中找到指定的code的对象
	 * 
	 * @param buff
	 * @param hashCode
	 * @param timeout
	 * @return
	 */
	public Decorator findResponseBy(String codeKey, int timeout) {
		long st = System.currentTimeMillis();
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (receiveBufferMap.containsKey(codeKey)) {
				Decorator dec = receiveBufferMap.get(codeKey);
				receiveBufferMap.remove(codeKey);
				return dec;
			}
		} while (System.currentTimeMillis() - st <= timeout);
		return null;
	}

	/**
	 * 连接校准箱
	 * 
	 * @return
	 */
	public boolean connect() {
		connector.setConnectTimeOut(1000);
		boolean result = connector.connect(ip, PORT);
		if (result) {

			connector.setNetworkListener(new NetworkListener() {
				@Override
				public void send(IoSession session, Object message) {
				}

				@Override
				public void receive(IoSession session, Object message) {
					Decorator decorator = (Decorator) message;
					// 推入校准箱map
					if (decorator instanceof ResponseDecorator)
						receiveBufferMap.put(decorator.getDestData().getCodeKey(), decorator);
					// 推入队列
					if (decorator instanceof AlertDecorator)
						try {
							pushDataBlockingQueue.offer(decorator, 10, TimeUnit.SECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}

				@Override
				public void connected(IoSession session) {
				}

				@Override
				public void disconnected(IoSession session) {
				}

				@Override
				public void exception(IoSession session, Throwable cause) {
				}

				@Override
				public void idled(IoSession session) {
				}
			});

			// ********************以下调试网口监听器，非调试下禁用**************************************
			// connector.setDebugDataListener(new DebugDataListener() {
			// @Override
			// public void onEncode(byte[] data) {
			// System.out.println("[ 发送 ] ---> " +
			// ProtocolUtil.printList(ProtocolUtil.convertArrayToList(data)));
			// }
			//
			// @Override
			// public void onDecode(byte[] data) {
			// System.out.println("[ 接收 ] <--- " +
			// ProtocolUtil.printList(ProtocolUtil.convertArrayToList(data)));
			// }
			// });
			// ********************以上调试网口监听器，非调试下禁用**************************************
		}
		return result;
	}

	// ****************************************************************************************************************
	// *
	// *setter and getter
	// *
	// ****************************************************************************************************************

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getCalBoardCount() {
		return calBoardList.size();
	}

	// public void setCalBoardCount(int calBoardCount) {
	// this.calBoardCount = calBoardCount;
	// }

	public List<String> getMeterIps() {
		return meterIps;
	}

	public void setMeterIps(List<String> meterIps) {
		this.meterIps = meterIps;
	}

	public String getScreenIp() {
		return screenIp;
	}

	public void setScreenIp(String screenIp) {
		this.screenIp = screenIp;
	}

	public List<CalBoard> getCalBoardList() {
		return calBoardList;
	}

	public void setCalBoardList(List<CalBoard> calBoardList) {
		this.calBoardList = calBoardList;
	}

	public CalibrateCoreWorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(CalibrateCoreWorkMode workMode) {
		this.workMode = workMode;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public NetworkConnector getConnector() {
		return connector;
	}

	public void setConnector(NetworkConnector connector) {
		this.connector = connector;
	}

	public int getHeartBeatExceptionTotal() {
		return heartBeatExceptionTotal;
	}

	public void setHeartBeatExceptionTotal(int heartBeatExceptionTotal) {
		this.heartBeatExceptionTotal = heartBeatExceptionTotal;
	}

	public ScheduledExecutorService getHeartThread() {
		return heartThread;
	}

	public void setHeartThread(ScheduledExecutorService heartThread) {
		this.heartThread = heartThread;
	}

	public Map<String, Decorator> getReceiveBufferMap() {
		return receiveBufferMap;
	}

	public void setReceiveBufferMap(Map<String, Decorator> receiveBufferMap) {
		this.receiveBufferMap = receiveBufferMap;
	}

	public static BlockingQueue<Decorator> getPushDataBlockingQueue() {
		return pushDataBlockingQueue;
	}

	public static void setPushDataBlockingQueue(BlockingQueue<Decorator> pushDataBlockingQueue) {
		CalBox.pushDataBlockingQueue = pushDataBlockingQueue;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public CalculatePlanData getCalculatePlan() {
		return calculatePlan;
	}

	public void setCalculatePlan(CalculatePlanData calculatePlan) {
		this.calculatePlan = calculatePlan;
	}

	public CalibrateCoreWorkMode getMode() {
		return mode;
	}

	public void setMode(CalibrateCoreWorkMode mode) {
		this.mode = mode;
	}

	public TestMode getTestMode() {
		return testMode;
	}

	public void setTestMode(TestMode testMode) {
		this.testMode = testMode;
	}

	public MatchState getMatchState() {
		return matchState;
	}

	public void setMatchState(MatchState matchState) {
		this.matchState = matchState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMeterIp() {
		return meterIp;
	}

	public void setMeterIp(String meterIp) {
		this.meterIp = meterIp;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isConnected() {

		return connector != null && connector.isConnected();
	}

	public int getBindFlag() {
		return bindFlag;
	}

	public void setBindFlag(int bindFlag) {
		this.bindFlag =  bindFlag;
	}

	public int getBeatCount() {
		return beatCount;
	}

	public void setBeatCount(int beatCount) {
		this.beatCount = beatCount;
	}

	/**
	 * 读取精度档位
	 * 
	 * @return
	 */
	public RangeCurrentPrecisionData loadRangeCurrentPrecision() throws Exception {

		RangeCurrentPrecisionData data = new RangeCurrentPrecisionData();

		String dir = Platform.getInstallLocation().getURL().getPath();

		Document doc = XmlUtil.loadXml(dir + RANGE_CURRENT_PRECISION_PATH);
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

	}

	/**
	 * 获取电阻系数
	 * 
	 * @author wavy_zheng 2022年3月28日
	 * @param range
	 * @param wp
	 * @return
	 */
	public double findResisterFactor(int range, WorkPattern wp) {

		for (CalResistanceDebugData crd : resisterFactors) {

			if (crd.getWorkPattern() == wp && crd.getRange() == range) {

				return crd.getResistance();
			}

		}

		return 0;

	}
   
	/**
	 * 获取该电流的档位
	 * @author  wavy_zheng
	 * 2022年3月29日
	 * @param current
	 * @return
	 */
	public int getCurrentRangeLevel(double current) {

		for (RangeCurrentPrecision range : rangeCurrentPrecision.getRanges()) {

			if (current >= range.min && current < range.max) {

				return range.level;
			}

		}

		return -1;
	}

	public RangeCurrentPrecisionData getRangeCurrentPrecision() {
		return rangeCurrentPrecision;
	}

	public List<CalResistanceDebugData> getResisterFactors() {
		return resisterFactors;
	}

	public void setResisterFactors(List<CalResistanceDebugData> resisterFactors) {
		this.resisterFactors = resisterFactors;
	}

}
