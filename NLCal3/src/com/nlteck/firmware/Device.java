package com.nlteck.firmware;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.session.IoSession;

import com.nlteck.firmware.WorkBench.CalType;
import com.nlteck.firmware.WorkBench.DeviceType;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestLog;
import com.nlteck.service.CalboxService.MatchState;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.main.MainEnvironment;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;

/**
 * 设备对象的抽象
 * 
 * @author caichao_tang
 *
 */
public class Device {
	private String name;
	private String ip;
	private String mac;
	private DeviceType type;
	private int logicNum;
	private int logicState;
	private int driverNumInLogic;
	private int chnNumInDriver;
	private CalculatePlanData calculatePlan; // 计量方案
	private int id;
	private String info;
	private CalibrateCoreWorkMode mode = CalibrateCoreWorkMode.NONE;
	private List<ChannelDO> channels = new ArrayList<>();
	private CalType calType = CalType.CAL;

	public static final int PORT = 8161;

	private NetworkConnector connector; // 网络连接器
	private Map<Code, ResponseDecorator> recvQueue = new ConcurrentHashMap<Code, ResponseDecorator>(); // 命令接收缓存
	/**
	 * 已绑定的校准箱
	 */
	private List<CalBox> calBoxList = new ArrayList<>();;
	private List<LogicBoard> logicBoardList = new ArrayList<>();
	private List<TestLog> testLogs = Collections.synchronizedList(new ArrayList<>());

	private ExecutorService workThread; // 性能测试线程
	private boolean work;

	// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	// $
	// $以下setter and getter
	// $
	// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
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

	public List<CalBox> getCalBoxList() {
		return calBoxList;
	}

	public void setCalBoxList(List<CalBox> calBoxList) {
		this.calBoxList = calBoxList;
	}

	public List<LogicBoard> getLogicBoardList() {
		return logicBoardList;
	}

	public void setLogicBoardList(List<LogicBoard> logicBoardList) {
		this.logicBoardList = logicBoardList;
	}

	public int getLogicNum() {
		return logicNum;
	}

	public void setLogicNum(int logicNum) {
		this.logicNum = logicNum;
	}

	public int getLogicState() {
		return logicState;
	}

	public void setLogicState(int logicState) {
		this.logicState = logicState;
	}

	public int getDriverNumInLogic() {
		return driverNumInLogic;
	}

	public void setDriverNumInLogic(int driverNumInLogic) {
		this.driverNumInLogic = driverNumInLogic;
	}

	public int getChnNumInDriver() {
		return chnNumInDriver;
	}

	public void setChnNumInDriver(int chnNumInDriver) {
		this.chnNumInDriver = chnNumInDriver;
	}

	public CalculatePlanData getCalculatePlan() {
		return calculatePlan;
	}

	public void setCalculatePlan(CalculatePlanData calculatePlan) {
		this.calculatePlan = calculatePlan;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<ChannelDO> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelDO> channels) {
		this.channels = channels;
	}

	public void appendChannel(ChannelDO chn) {

		this.channels.add(chn);
		chn.setDevice(this);
	}

	public void clearChannels() {

		this.channels.clear();

	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * 绑定
	 * 
	 * @author wavy_zheng 2021年1月18日
	 * @param box
	 */
	public void bindCalbox(CalBox box) {

		calBoxList.add(box);
		box.setDevice(this);

	}

	public void unbindCalbox(CalBox box) {

		if (calBoxList.contains(box)) {

			calBoxList.remove(box);
			box.setDevice(null);
		}
	}

	public boolean isConnected() {

		for (CalBox box : calBoxList) {

			if (!box.isConnected()) {

				return false;
			}
		}
		if (calBoxList.isEmpty()) {

			return false;
		}

		return true;

	}

	public CalibrateCoreWorkMode getMode() {
		return mode;
	}

	public void setMode(CalibrateCoreWorkMode mode) {
		this.mode = mode;
	}

	/**
	 * 选出所有已成功对接的电压
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @return
	 */
	public List<ChannelDO> listAllConnectedChannels() {

		List<ChannelDO> list = new ArrayList<>();
		for (ChannelDO chn : channels) {

			if (chn.isConnectCalboard()) {
				list.add(chn);
			}
		}

		return list;
	}

	public boolean isTesting() {

		for (ChannelDO chn : channels) {

			if (chn.getState() == CalState.CALCULATE || chn.getState() == CalState.CALIBRATE
					|| chn.getState() == CalState.READY) {

				return true;
			}
		}

		return false;

	}

	/**
	 * 获取所有已选中的通道
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @return
	 */
	public List<ChannelDO> listAllSelectedChannels() {

		List<ChannelDO> list = new ArrayList<>();
		for (ChannelDO chn : channels) {

			if (chn.isSelected()) {
				list.add(chn);
			}
		}

		return list;
	}

	/**
	 * 所有准备或已经在测试的通道
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @return
	 */
	public List<ChannelDO> listAllTestOrReadyChannels() {

		List<ChannelDO> list = new ArrayList<>();
		for (ChannelDO chn : channels) {

			if (chn.getState() == CalState.READY || chn.getState() == CalState.CALCULATE
					|| chn.getState() == CalState.CALIBRATE) {
				list.add(chn);
			}
		}

		return list;
	}

	public CalType getCalType() {
		return calType;
	}

	public void setCalType(CalType calType) {
		this.calType = calType;
	}

	/**
	 * 对接是否完成
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @return
	 */
	public boolean isMatched() {

		for (CalBox box : calBoxList) {

			if (box.getMatchState() != MatchState.MATCHED) {

				return false;
			}
		}

		return true;
	}

	public List<TestLog> getTestLogs() {
		return testLogs;
	}

	public void setTestLogs(List<TestLog> testLogs) {
		this.testLogs = testLogs;
	}

	@Override
	public String toString() {
		return "Device [name=" + name + ", ip=" + ip + ", mac=" + mac + ", logicNum=" + logicNum + ", driverNumInLogic="
				+ driverNumInLogic + ", chnNumInDriver=" + chnNumInDriver + ", calculatePlan=" + calculatePlan + ", id="
				+ id + ", channels=" + channels + ", calBoxList=" + calBoxList + "]";
	}

	public void disconnect() {

		if (connector != null && connector.isConnected()) {
             
			stopWork();
			connector.disconnect();

		}
	}

	public boolean connect() {

		if (connector == null) {

			initNetwork();
		}
		startWork();
		if (!connector.isConnected()) {

			recvQueue.clear(); // 网络重新连接,清空接收缓存
			return connector.connect(ip, PORT);
			

		}
		
		

		return true;

	}

	public void initNetwork() {

		// 初始化网络
		connector = new NetworkConnector(new Entity(), false);
		connector.setConnectTimeOut(5000);
		connector.setNetworkListener(new NetworkListener() {

			@Override
			public void send(IoSession session, Object message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void receive(IoSession session, Object message) {

				String remoteIp = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();

				Decorator response = (Decorator) message;
				Data responseData = response.getDestData();

				if (responseData.getCode() == MainEnvironment.MainCode.PickupCode) {

					// 回复主控推送
					int unitIndex = responseData.getUnitIndex();
					PickupData pd = new PickupData();
					pd.setResult(DefaultResult.SUCCESS);
					pd.setUnitIndex(unitIndex);
//					connector.send(new ResponseDecorator(pd, false)); // 发送到网络

					// 将数据分配到每一个通道上
					for (int i = 0; i < pd.getChnDatas().size(); i++) {

						ChannelData chnData = pd.getChnDatas().get(i);
						getChannels().get(chnData.getChannelIndex()).setChannelData(chnData);

					}

				} else {

					if (responseData.getCode() != MainEnvironment.MainCode.OfflineUploadCode
							&& responseData.getCode() != MainEnvironment.MainCode.DeviceStateCode
							&& responseData.getCode() != MainCode.AlertCode
							&& responseData.getCode() != MainCode.UpgradeProgressCode) {

						// 存入接收缓存
						recvQueue.put(responseData.getCode(), (ResponseDecorator) response);
					}

				}

			}

			@Override
			public void connected(IoSession session) {

				System.out.println("connect device!");

			}

			@Override
			public void disconnected(IoSession session) {

				System.out.println("disconnect device!");

			}

			@Override
			public void exception(IoSession session, Throwable cause) {
				// TODO Auto-generated method stub

			}

			@Override
			public void idled(IoSession session) {
				// TODO Auto-generated method stub

			}

		});
	}

	public ResponseDecorator findRecvCommand(int timeOut, Data send) {

		ResponseDecorator rd = null;
		long st = System.currentTimeMillis();

		while (recvQueue.get(send.getCode()) == null) {

			if (System.currentTimeMillis() - st > timeOut) {

				return rd; // 超时
			}
			CommonUtil.sleep(100);
		}
		return recvQueue.remove(send.getCode()); // 从缓存中删除
	}

	public Data queryCommand(Data data, StringBuffer info, int timeOut) {

		try {

			recvQueue.remove(data.getCode());
			// 暂停采集
			connector.send(new QueryDecorator(data));

			ResponseDecorator rd = findRecvCommand(timeOut, data);
			if (rd == null) {

				info.append("网络通信超时");
				return null;
			}
			if (rd.getResult().getCode() != Result.SUCCESS) {

				info.append("结果码:" + rd.getResult() + (rd.getInfo().isEmpty() ? "" : ",原因:" + rd.getInfo()));
				return null;
			}
			if (rd.getDestData().getCode() != data.getCode()) {

				info.append("回复功能码不一致，下发:" + data.getCode() + ",回复:" + rd.getDestData().getCode());
				return null;
			}

			return rd.getDestData();

		} catch (Exception e) {
			info.append("网络通信被中断");
			e.printStackTrace();
		}

		return null;

	}

	public boolean configCommand(Data data, StringBuffer info, int timeOut) {

		try {

			recvQueue.remove(data.getCode());
			// 暂停采集
			connector.send(new ConfigDecorator(data));

			ResponseDecorator rd = findRecvCommand(timeOut, data);
			if (rd == null) {

				info.append("网络通信超时");
				return false;
			}
			if (rd.getResult().getCode() != Result.SUCCESS) {

				info.append("结果码:" + rd.getResult() + (rd.getInfo().isEmpty() ? "" : ",原因:" + rd.getInfo()));
				return false;
			}
			if (rd.getDestData().getCode() != data.getCode()) {

				info.append("回复功能码不一致，下发:" + data.getCode() + ",回复:" + rd.getDestData().getCode());
				return false;
			}

		} catch (Exception e) {
			info.append("网络通信被中断");
			e.printStackTrace();
		}

		return true;

	}

	/**
	 * 当前设备在做普通性测试
	 * 
	 * @author wavy_zheng 2022年3月31日
	 * @return
	 */
	public boolean isCommonTesting() {

		for (ChannelDO chn : channels) {

			if (chn.getRunningMode() != null) {

				return true;
			}
		}

		return false;
	}

	public void startWork() {

		if (workThread == null) {
			this.work = true;
			workThread = Executors.newSingleThreadExecutor();
			workThread.execute(new Runnable() {

				@Override
				public void run() {

					while (work) {
						if (!isCommonTesting()) {

							// 扫描有没有测试的通道
							for (ChannelDO chn : channels) {

								if (chn.isReadyCommonTest()) {

									// 启动测试
									try {
										WorkBench.coreService.executeTest(chn);
									} catch (Exception e) {

										e.printStackTrace();
									} finally {
										
										
									}

									CommonUtil.sleep(100);

								}

							}

						}
						CommonUtil.sleep(1000);

					}
				}

			});

		}

	}

	public void stopWork() {

		work = false;
		workThread = null;
	}

}
