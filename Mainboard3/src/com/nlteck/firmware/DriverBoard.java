package com.nlteck.firmware;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.Environment;
import com.nlteck.ParameterName;
import com.nlteck.i18n.I18N;
import com.nlteck.service.LabDriverBoardService.ResponseTicket;
import com.nlteck.service.StartupCfgManager.DriverInfo;
import com.nlteck.service.StartupCfgManager.DriverboardType;
import com.nlteck.service.connector.LogicConnector;
import com.nlteck.util.CommonUtil;
import com.nltecklib.io.mina.DebugDataListener;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Decorator;
import com.nltecklib.protocol.lab.Entity;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Result;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.logic2.Logic2Environment.LogicState;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.li.main.ProcedureData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.DriverMode;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.LogUtil;
import com.nltecklib.protocol.power.driver.DriverPickupData;
import com.rm5248.serial.SerialPort;

/**
 * 驱动板抽象对象
 * 
 * @author Administrator
 *
 */
public class DriverBoard {

	public enum ChangeResult {

		NONE, OK, NG;
	}

	protected MainBoard mainboard;
	// protected LogicBoard logicBoard;
	protected int driverIndex;
	protected double temp1; // 驱动板温度1
	protected double temp2; // 驱动板温度2
	protected boolean alert; // 已发生报警
	protected long runMiliseconds;

	private boolean use = true;
	private ChangeResult changeResult = ChangeResult.NONE; // 更换驱动板失败
	private Date changeTime; // 更换时间
	protected LogicState state = LogicState.UDT; // 驱动板状态
	protected volatile Map<Step, Short> openFlagMap = new ConcurrentHashMap<Step, Short>(); // 启动标志

	public final static double DRIVER_TEMP_UPPER = 80; // 温度报警上限

	protected List<Channel> channels = new ArrayList<Channel>(); // 内置通道
	protected NetworkConnector connector; // 通信连接器

	protected ProcedureData procedure;
	protected ParameterName pn = new ParameterName(WorkType.AG); // 默认保护值
	protected String testName; // 测试名

	protected boolean syncStepNextStep;

	private boolean logicDriverFlashOk;
	private boolean checkDriverFlashOk;
	private boolean checkDriverBaseVoltOk; // 基准电压

	private String logicSoftversion;
	private String checkSoftversion;

	private String tempSoftversion;
	private String pickSoftversion;

	private int maxCurrentSupport; // 最大电流

	private boolean logicProgramBurnOk; // 程序烧录
	private boolean checkProgramBurnOk;
	private String logicUuid = ""; // 逻辑驱动唯一标识码
	private String checkUuid = ""; // 回检驱动唯一标识码

	private ScheduledExecutorService executor = null;
	private PickupData lastPickupData;

	private SerialPort port;

	private long recvPickupCount; // 当前逻辑板采集次数统计
	private long sendPickupCount; // 当前逻辑板发送采样次数统计

	private boolean pickupOver; // 刚采集完数据

	protected DriverCheckData driverCheckData; // 驱动板自检数据

	protected DriverMode driverMode = DriverMode.NORMAL;

	private List<DriverPickupData> driverCaches = Collections.synchronizedList(new LinkedList<DriverPickupData>()); // 用于驱动板超温的缓存数据

	/***************************** 实验室协议 ****************************/
	private ScheduledExecutorService heartbeatThread;
	private Date lastHeartbeatDate; // 最近一次心跳链接时间

	private String ip;
	private static final int PORT = 8166; // 驱动板网路端口
	private Map<ResponseTicket, Data> poolMap = new ConcurrentHashMap<>();
	private LinkedBlockingQueue<Decorator> sendQueue = new LinkedBlockingQueue<Decorator>();
	private ExecutorService executorGroup;
	
	private Logger  logger;

	public DriverBoard(int driverIndex, MainBoard mainboard) {

		this.driverIndex = driverIndex;
		this.mainboard = mainboard;

		System.out.println("init driver " + driverIndex);
		
		this.logger = LogUtil.getLogger("driver" + (driverIndex + 1));
		this.use = MainBoard.startupCfg.getDriverInfo(driverIndex).use;
		// 获取串口
		if (!MainBoard.startupCfg.isUseVirtualData()) {

			if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.PRODUCT) {
				String portName = MainBoard.startupCfg.getDriverInfo(driverIndex).portName;
				port = Context.getPortManager().getPortByName(portName);
			} else if (MainBoard.startupCfg.getDriversCfg().driverboardType == DriverboardType.LAB) {

				ip = MainBoard.startupCfg.getDriverInfo(driverIndex).ip;
				connector = new NetworkConnector(new Entity(), false);
				
				/*connector.setDebugDataListener(new DebugDataListener() {
					
					@Override
					public void onEncode(byte[] data) {
						
						String str = Entity.printList(ProtocolUtil.convertArrayToList(data));
						
						if(!str.contains("68 01 12")) {
						    logger.info("send:" + str);
						}
						
					}
					
					@Override
					public void onDecode(byte[] data) {
						
                        String str = Entity.printList(ProtocolUtil.convertArrayToList(data));
						
						if(!str.contains("68 01 12")) {
						    logger.info("recv:" + str);
						}
						
						
						
					}
				});*/
				connector.setNetworkListener(new NetworkListener() {
					
					@Override
					public void send(IoSession session, Object message) {
						
						
					}
					
					@Override
					public void receive(IoSession session, Object message) {
						
						
						String remoteIp = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
						Decorator response = (Decorator) message;
						Data recvData = response.getDestData();
						
						//System.out.println("recvData:" + recvData);
						poolMap.put(new ResponseTicket(response.getCode(), recvData.getChnIndex()), recvData);
						lastHeartbeatDate = new Date();
					}
					
					@Override
					public void idled(IoSession session) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void exception(IoSession session, Throwable cause) {
						
						logger.error("network exception!");
						logger.error(CommonUtil.getThrowableException(cause));
						
					}
					
					@Override
					public void disconnected(IoSession session) {
						// TODO Auto-generated method stub
						logger.info("disconnected !!!");
					}
					
					@Override
					public void connected(IoSession session) {
					
						 logger.info("connected !!!");
					}
				});
				
				if(this.use) {
					
					connector.connect(ip, PORT);
					logger.info("connect to " + ip + " OK!");
					// 创建发送线程
					creatSendThread();
					// 创建心跳线程
					//createHeartbeatThread();
				}

			}
		}

		

	}

	private void createHeartbeatThread() {

		if (heartbeatThread != null) {

			return;
		}
		heartbeatThread = Executors.newSingleThreadScheduledExecutor();
		heartbeatThread.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {

				if (!isConnected()) {

					// 开始自动连接
					
					boolean ok = connect();
					logger.info("connect driver :" + ip + (ok ? " ok" : " ng"));

				} else {

					if (lastHeartbeatDate == null) {

						lastHeartbeatDate = new Date();
						return;
					}

					if (new Date().getTime() - lastHeartbeatDate.getTime() >= 10000) {

						// 断开网络
						disconnect();
						
						logger.info("because of heartbeat ,disconnect driver");

					}

				}

			}

		}, 100, 5000, TimeUnit.MILLISECONDS);

	}

	public boolean connect() {

		if (MainBoard.startupCfg.isUseVirtualData()) {

			return true;
		}
		if (!isConnected()) {

			connector.connect(ip, PORT);
			return true;

		}

		return true;

	}

	public boolean isConnected() {

		if (MainBoard.startupCfg.isUseVirtualData()) {

			return true;
		}

		return connector != null && connector.isConnected();
	}

	public void disconnect() {

		if (MainBoard.startupCfg.isUseVirtualData()) {

			return;
		}

		if (isConnected()) {

			connector.disconnect();
			connector = null;
		}

	}

	public NetworkConnector getConnector() {
		return connector;
	}

	public MainBoard getMainboard() {
		return mainboard;
	}

	public int getDriverIndex() {
		return driverIndex;
	}

	/**
	 * 至少有个通道已运行
	 * 
	 * @return
	 */
	public boolean isAnyChnRunning() {

		for (int i = 0; i < getChannelCount(); i++) {

			if (getChannel(i).getState() == ChnState.RUN) {

				return true;
			}
		}

		return false;

	}

	public boolean isAnyChnPaused() {

		for (int i = 0; i < getChannelCount(); i++) {

			if (getChannel(i).getState() == ChnState.PAUSE) {

				return true;
			}
		}

		return false;
	}

	public boolean isAnyChnStoped() {

		for (int i = 0; i < getChannelCount(); i++) {

			if (getChannel(i).getState() == ChnState.STOP) {

				return true;
			}
		}

		return false;
	}

	public boolean isAllChnOver() {

		for (int i = 0; i < getChannelCount(); i++) {

			if (getChannel(i).getState() == ChnState.RUN || getChannel(i).getState() == ChnState.PAUSE) {

				return false;
			}
		}

		return true;

	}

	/**
	 * 驱动板初始化
	 */
	public void init() {

		for (int i = 0; i < MainBoard.startupCfg.getDriverChnCount(); i++) {

			Channel channel = new Channel(i, this);
			channels.add(channel);
		}

	}

	public int getChannelCount() {

		return channels.size();
	}

	/**
	 * 获取驱动板最新数据
	 * 
	 * @return
	 */
	public List<ChannelData> fetchData() throws AlertException {

		List<ChannelData> list = new LinkedList<ChannelData>();
		for (int n = 0; n < channels.size(); n++) {

			ChannelData data = channels.get(n).getRuntimeCaches().get(0);
			if (data == null) {

				// throw new AlertException(AlertCode.COMM_ERROR, "通道采集数据通信超时!");
				throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.ChnPickupCommunicationTimeout));
			} else {

			}

			list.add(data);
		}
		return list;
	}

	public int getDeviceDriverIndex() {

		return driverIndex;
	}

	/**
	 * 是否可以正常工作
	 * 
	 * @return
	 */
	public boolean isReadyToWork() {

		for (int n = 0; n < channels.size(); n++) {

			if (!channels.get(n).isReadyToWork()) {

				System.out.println("channel " + n + " is not ready,buff size = " + channels.get(n));

				return false;
			}
		}

		return true;
	}

	/**
	 * 获取实际下发驱动板号
	 * 
	 * @return
	 */
	public int getLoadDriverIndex() {

		// int boardIndex = driverIndex;
		// int logicIndex = logicBoard.getLogicIndex();
		// if (MainBoard.startupCfg.getLogicInfo(logicIndex).reverseDriverIndex) {
		// // 处理板号
		//
		// boardIndex = MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
		// }
		// return boardIndex;

		return driverIndex;
	}

	public Channel getChannel(int index) {

		return channels.get(index);
	}

	public double getTemp1() {
		return temp1;
	}

	public double getTemp2() {
		return temp2;
	}

	public void setTemp1(double temp1) {
		this.temp1 = temp1;
	}

	public void setTemp2(double temp2) {
		this.temp2 = temp2;
	}

	/**
	 * 检测是否超温
	 * 
	 * @throws AlertException
	 */
	public void checkTemp(int index, double currentTemp) throws AlertException {

		if (index == 0) {
			if (currentTemp > DRIVER_TEMP_UPPER && temp1 < DRIVER_TEMP_UPPER) {

				Context.getAlertManager().handle(AlertCode.TEMP_OVER, I18N.getVal(I18N.DriverBoardTempUpper,
						getDeviceDriverIndex() + 1, CommonUtil.formatNumber(currentTemp, 1), DRIVER_TEMP_UPPER), false);

			} else if (currentTemp <= DRIVER_TEMP_UPPER && temp1 > DRIVER_TEMP_UPPER) {

				// 恢复
				Context.getAlertManager().handle(AlertCode.TEMP_OVER, I18N.getVal(I18N.DriverBoardTempRecoverNormal,
						getDeviceDriverIndex() + 1, CommonUtil.formatNumber(currentTemp, 1)), true);

			}
		} else {

			if (currentTemp > DRIVER_TEMP_UPPER && temp2 < DRIVER_TEMP_UPPER) {

				Context.getAlertManager().handle(AlertCode.TEMP_OVER, I18N.getVal(I18N.DriverBoardTempUpper,
						getDeviceDriverIndex() + 1, CommonUtil.formatNumber(currentTemp, 1), DRIVER_TEMP_UPPER), false);

			} else if (currentTemp <= DRIVER_TEMP_UPPER && temp2 > DRIVER_TEMP_UPPER) {

				// 恢复
				Context.getAlertManager().handle(AlertCode.TEMP_OVER, I18N.getVal(I18N.DriverBoardTempRecoverNormal,
						getDeviceDriverIndex() + 1, CommonUtil.formatNumber(currentTemp, 1)), true);

			}
		}
	}

	// public int getDriverIndexInDevice() {
	//
	// return logicBoard.getLogicIndex() *
	// MainBoard.startupCfg.getLogicDriverCount() + driverIndex;
	// }

	public boolean isAlert() {
		return alert;
	}

	public ProcedureData getProcedure() {
		return procedure;
	}

	public void setProcedure(ProcedureData procedure) {
		this.procedure = procedure;
	}

	public ParameterName getPn() {
		return pn;
	}

	public void setPn(ParameterName pn) {
		this.pn = pn;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public ChangeResult getChangeResult() {
		return changeResult;
	}

	public void setChangeResult(ChangeResult changeResult) {
		this.changeResult = changeResult;
	}

	public boolean isSyncStepNextStep() {
		return syncStepNextStep;
	}

	public MainBoard getMainBoard() {

		return mainboard;
	}

	public ProcedureData getCurrentProcedure() {

		return getControlUnit().getProcedure();

	}

	/**
	 * 
	 * @param stepIndex
	 * @return
	 */
	public ProcedureData.Step getProcedureStep(int stepIndex) {

		ProcedureData procedure = getCurrentProcedure();
		if (procedure == null) {

			return null;
		}

		if (stepIndex > 0 && stepIndex <= procedure.getStepCount()) {
			return procedure.getStep(stepIndex - 1);
		}

		return null;
	}

	/**
	 * 通道的实时运行数据区是否已经清空，此项作为通道是否完成的依据;
	 * 
	 * @return
	 */
	public boolean isAllChnRuntimeCachesEmpty() {

		for (int i = 0; i < getChannelCount(); i++) {

			if (!getChannel(i).isRuntimeCachesEmpty()) {

				return false;
			}
		}

		return true;
	}

	public Map<Step, Short> getOpenFlagMap() {
		return openFlagMap;
	}

	public void setOpenFlagMap(Map<Step, Short> openFlagMap) {
		this.openFlagMap = openFlagMap;
	}

	public ControlUnit getControlUnit() {

		for (ControlUnit control : getMainBoard().getControls()) {

			if (control.containsDriver(this)) {

				return control;
			}
		}

		return null;

	}

	public boolean isLogicDriverFlashOk() {
		return logicDriverFlashOk;
	}

	public void setLogicDriverFlashOk(boolean logicDriverFlashOk) {
		this.logicDriverFlashOk = logicDriverFlashOk;
	}

	public boolean isCheckDriverFlashOk() {
		return checkDriverFlashOk;
	}

	public void setCheckDriverFlashOk(boolean checkDriverFlashOk) {
		this.checkDriverFlashOk = checkDriverFlashOk;
	}

	public boolean isCheckDriverBaseVoltOk() {
		return checkDriverBaseVoltOk;
	}

	public void setCheckDriverBaseVoltOk(boolean checkDriverBaseVoltOk) {
		this.checkDriverBaseVoltOk = checkDriverBaseVoltOk;
	}

	public String getLogicSoftversion() {
		return logicSoftversion;
	}

	public void setLogicSoftversion(String logicSoftversion) {
		this.logicSoftversion = logicSoftversion;
	}

	public String getCheckSoftversion() {
		return checkSoftversion;
	}

	public void setCheckSoftversion(String checkSoftversion) {
		this.checkSoftversion = checkSoftversion;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean isLogicProgramBurnOk() {
		return logicProgramBurnOk;
	}

	public void setLogicProgramBurnOk(boolean logicProgramBurnOk) {
		this.logicProgramBurnOk = logicProgramBurnOk;
	}

	public boolean isCheckProgramBurnOk() {
		return checkProgramBurnOk;
	}

	public void setCheckProgramBurnOk(boolean checkProgramBurnOk) {
		this.checkProgramBurnOk = checkProgramBurnOk;
	}

	public String getLogicUuid() {
		return logicUuid;
	}

	public void setLogicUuid(String logicUuid) {
		this.logicUuid = logicUuid;
	}

	public String getCheckUuid() {
		return checkUuid;
	}

	public void setCheckUuid(String checkUuid) {
		this.checkUuid = checkUuid;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public LogicState getState() {
		return state;
	}

	public void setState(LogicState state) {
		this.state = state;
	}

	public long getRunMiliseconds() {
		return runMiliseconds;
	}

	public void setRunMiliseconds(long runMiliseconds) {
		this.runMiliseconds = runMiliseconds;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public SerialPort getPort() {
		return port;
	}

	/**
	 * 因驱动板线序的问题可能出现反序 得到真实下发给驱动板的板号
	 * 
	 * @author wavy_zheng 2022年1月23日
	 * @param driverIndex
	 * @return
	 */
	public static int getDownloadDriverIndex(int driverIndex) {

		return MainBoard.startupCfg.getDriverInfo(driverIndex).index;
	}

	public static int getOrgignalDriverIndex(int actualDriverIndex) {

		for (int n = 0; n < MainBoard.startupCfg.listDriverInfos().size(); n++) {

			DriverInfo di = MainBoard.startupCfg.listDriverInfos().get(n);
			if (di.index == actualDriverIndex) {

				return n;
			}

		}

		return -1;

	}

	public ScheduledExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public long getRecvPickupCount() {
		return recvPickupCount;
	}

	public void setRecvPickupCount(long recvPickupCount) {
		this.recvPickupCount = recvPickupCount;
	}

	public long getSendPickupCount() {
		return sendPickupCount;
	}

	public void setSendPickupCount(long sendPickupCount) {
		this.sendPickupCount = sendPickupCount;
	}

	public PickupData getLastPickupData() {
		return lastPickupData;
	}

	public void setLastPickupData(PickupData lastPickupData) {
		this.lastPickupData = lastPickupData;
	}

	public synchronized boolean isPickupOver() {
		return pickupOver;
	}

	public synchronized void setPickupOver(boolean pickupOver) {
		this.pickupOver = pickupOver;
	}

	public DriverCheckData getDriverCheckData() {
		return driverCheckData;
	}

	public void setDriverCheckData(DriverCheckData driverCheckData) {
		this.driverCheckData = driverCheckData;
	}

	public String getTempSoftversion() {
		return tempSoftversion;
	}

	public void setTempSoftversion(String tempSoftversion) {
		this.tempSoftversion = tempSoftversion;
	}

	public String getPickSoftversion() {
		return pickSoftversion;
	}

	public void setPickSoftversion(String pickSoftversion) {
		this.pickSoftversion = pickSoftversion;
	}

	public DriverMode getDriverMode() {
		return driverMode;
	}

	public void setDriverMode(DriverMode driverMode) {
		this.driverMode = driverMode;
	}

	public int getMaxCurrentSupport() {
		return maxCurrentSupport;
	}

	public void setMaxCurrentSupport(int maxCurrentSupport) {
		this.maxCurrentSupport = maxCurrentSupport;
	}

	public void appendDriverCaches(DriverPickupData dpd) {

		this.driverCaches.add(dpd);

	}

	public void clearDriverCaches() {

		this.driverCaches.clear();
	}

	public List<DriverPickupData> listDriverCaches() {

		return this.driverCaches;
	}
	
	
	private void clearPoolBuff( Code code, int unitChnIndex) {

		ResponseTicket key = new ResponseTicket(code, unitChnIndex);
		poolMap.remove(key); // 清除缓存
		

	}
	
	public Data sendAndRecvData(int chnIndexInDriver, Decorator decorator, int timeOut, int sendTime)
			throws Exception {
		
		for (int n = 0; n < sendTime; n++) {

			clearPoolBuff(decorator.getCode(), chnIndexInDriver);
			// 发送数据
			sendData(decorator);
			long tick = System.currentTimeMillis();
			Data response = null;
			do {
				BaseUtil.sleep(100);
				response = findResponseFromPool(decorator.getCode(), chnIndexInDriver);

			} while (response == null && System.currentTimeMillis() - tick < timeOut);

			if (response != null) {

				if (response.getResult() != Result.SUCCESS) {

					throw new Exception(decorator.getCode() + "通信返回错误码:" + response.getResult());
				}

				return response;
			}
			BaseUtil.sleep(1000);

		}

		throw new Exception(decorator.getCode() + "通信超时");
		
	}

	private void sendData(Decorator decorator) {

		sendQueue.add(decorator);
	}

	private Data findResponseFromPool(Code code, int unitChnIndex) {

		ResponseTicket key = new ResponseTicket(code, unitChnIndex);
		Data response = poolMap.get(key);
		if (response != null) {

			poolMap.remove(key);

		}
		return response;

	}

	public void creatSendThread() {
        
		if(executorGroup != null) {
			
			return;
		}
		executorGroup = Executors.newSingleThreadExecutor();
		executorGroup.execute(new Runnable() {

			@Override
			public void run() {

				while (true) {
					if (!connector.isConnected()) {

						BaseUtil.sleep(1000);
						continue;
					}

					try {
						// System.out.println(connector + ":" + sendQueue.get(connector).size());
						Decorator decorator = sendQueue.take();
						if (decorator != null) {

							connector.send(decorator);
							//System.out.println("send dec:" + decorator);
							BaseUtil.sleep(10);

						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		});

	}


	
	public void log(String info) {
		
		logger.info(info);
	}

}
