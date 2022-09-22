package com.nlteck.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import com.nlteck.controller.PCController;
import com.nlteck.fireware.CalibrateCore;
import com.nlteck.model.Channel;
import com.nlteck.model.TestDot;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.io.mina.NetworkServer;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.MBWorkform.MBCalMatchData;
import com.nltecklib.protocol.li.PCWorkform.CalMatchValueData;
import com.nltecklib.protocol.li.PCWorkform.LivePushData;
import com.nltecklib.protocol.li.PCWorkform.LogDebugPushData;
import com.nltecklib.protocol.li.PCWorkform.LogPushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbMatchAdcData;
import com.nltecklib.protocol.power.driver.DriverMatchAdcData;
import com.nltecklib.utils.BaseUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午1:23:29 网络组件服务
 */
/**
 * @author guofang_ma
 *
 */
public class NetworkService {

	private NetworkServer server;
	/**
	 * 校准核心板容器
	 */
	private CalibrateCore core;
	/**
	 * 校准主控端口
	 */
	public final static int PORT = 8151;

	private LinkedBlockingQueue<Decorator> sendQueue = new LinkedBlockingQueue<Decorator>(); // 发送队列
//	private LinkedBlockingQueue<Decorator> recvQueue = new LinkedBlockingQueue<Decorator>(); // 接收队列
	private Map<String, ResponseDecorator> responseMap = new ConcurrentHashMap<>(); // 结果队列

//	private LinkedBlockingQueue<AlertDecorator>needReplyAlertQueue=new LinkedBlockingQueue<>();//需要实时回复的上报队列

//	private Map<Integer, LinkedBlockingQueue<PushLog>> logMap = new ConcurrentHashMap<Integer, LinkedBlockingQueue<PushLog>>();

//	private LinkedBlockingQueue<PushData> chnPushData = new LinkedBlockingQueue<>();
//	private final int MAX_CHN_PUSH_COUNT = 512;
//	private final int MAX_LOG_PUSH_COUNT = 128;

//	private long dataSendIndex; // 数据推送序号
//	private long dataRecvIndex; // 数据回复序号

//	private Date lastHeartBeatDate;

//	private ScheduledExecutorService executor;
	
	

	private PCController pcController;

	public PCController getPcController() {
		return pcController;
	}

	public void setPcController(PCController pcController) {
		this.pcController = pcController;
	}

	public Map<String, ResponseDecorator> getResponseMap() {
		return responseMap;
	}

	public void setResponseMap(Map<String, ResponseDecorator> responseMap) {
		this.responseMap = responseMap;
	}

//	public LinkedBlockingQueue<Decorator> getRecvQueue() {
//		return recvQueue;
//	}
//
//	public void setRecvQueue(LinkedBlockingQueue<Decorator> recvQueue) {
//		this.recvQueue = recvQueue;
//	}

	public NetworkService(CalibrateCore core) {
		this.core = core;
		pcController = new PCController(core);
	}

	/**
	 * 处理发送网络队列
	 */
	protected void processSendQueue() {

		Thread sendThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					Decorator decorator;
					try {
						if (!isConnected()) {
							// dataSendIndex = 0;
							// dataRecvIndex = 0;
							BaseUtil.sleep(1000);
							// 处于掉线状态，无发送
							continue;
						}
						decorator = sendQueue.take();
						if (decorator != null) {

							// System.out.println("sendQueue:" + decorator.toString());
							server.send(decorator);

						}
					} catch (InterruptedException e) {
						core.getLogger().error(e.getMessage(), e);
					}

				}
			}

		});
		sendThread.setDaemon(true);
		sendThread.start();
	}

	/**
	 * 处理接收网络消息队列
	 * 
	 * @author wavy_zheng 2020年10月26日
	 */
//	private void processRecvQueue() {
//
//		Thread recvThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				while (true) {
//					Decorator decorator;
//					try {
//
//						decorator = recvQueue.take();
//						if (decorator != null) {
//
//							if (decorator instanceof ResponseDecorator) {
//
//								ResponseDecorator response = (ResponseDecorator) decorator;
//
//								if (response.getDestData() instanceof LogPushData) {
//									// dataRecvIndex++;
//
//								} else if (response.getDestData() instanceof LivePushData) {
//									// dataRecvIndex++;
//
//								} else {
//									// 回复命令
//									responseMap.put(decorator.getDestData().getCodeKey(),
//											(ResponseDecorator) decorator);
//								}
//
//							} else {
//
//							
//								
//								try {
//									ResponseDecorator response = pcController.process(decorator);
//									if (response != null) {
//										pushSendQueue(response);
//									}
//								} catch (Exception e) {
//									core.getLogger().error(e.getMessage(), e);
//								}
//							}
//						}
//					} catch (InterruptedException e) {
//						core.getLogger().error(e.getMessage(), e);
//					}
//				}
//			}
//
//		});
//		recvThread.setDaemon(true);
//		recvThread.start();
//
//	}

	public boolean isConnected() {

		if (server != null) {

			return server.isConnected();
		}

		return false;
	}

	public void disconnect() {

		if (server != null) {
			if (!server.isConnected()) {

				return;
			}

			server.disconnect();
		}
	}

	/**
	 * 初始化网络服务
	 * 
	 * @throws IOException
	 */
	public void init(NetworkListener listener) throws IOException {

		server = new NetworkServer(new Entity());
//		processRecvQueue();
		processSendQueue();
		server.createServer(null, PORT, listener);
	}

	/**
	 * 关闭网络服务
	 * 
	 * @author wavy_zheng 2020年4月13日
	 */
	public void shutDownService() {

		server.disconnect();
		server.stopListen();

	}

	/**
	 * 推入发送队列
	 */
	public void pushSendQueue(Decorator dec) {

		sendQueue.offer(dec);

	}

	/**
	 * 查找接收命令
	 * 
	 * @param timeOut
	 * @param decorator
	 * @return
	 */
	public ResponseDecorator findRecvCommand(int timeOut, Decorator decorator) {

		ResponseDecorator rd = null;
		long st = System.currentTimeMillis();

		do {
			CommonUtil.sleep(100);
			rd = responseMap.get(decorator.getDestData().getCodeKey());
			if (System.currentTimeMillis() - st > timeOut) {

				return rd; // 超时
			}
		} while (rd == null);
		return responseMap.remove(decorator.getDestData().getCodeKey()); // 从缓存中删除
	}

	/**
	 * 上报生产日志
	 * 
	 * @param channel
	 * @param log
	 * @param error
	 */
	public void pushLog(Channel channel, String log, boolean error) {

		LogPushData data = new LogPushData();
		PushLog pushLog = new PushLog();
		pushLog.log = log;
		pushLog.error = error;

		if (channel != null) {
			data.setUnitIndex(0);
			pushLog.chnIndexInLogic = channel.getDeviceChnIndex();
		} else {
			data.setUnitIndex(0xFF);
			pushLog.chnIndexInLogic = 0xFF;

		}

		data.appendLog(pushLog);

		pushSendQueue(new AlertDecorator(data));
	}

	/**
	 * 生产整柜日志
	 * 
	 * @param log
	 */
	public void pushLog(String log, boolean error) {
		pushLog(null, log, error);
	}

	/**
	 * 通道调试日志
	 * 
	 * @param debugLog
	 */
	public void pushDebugLog(Channel channel, String log, boolean error) {

		// pushLog(channel, log, error);

		LogDebugPushData data = new LogDebugPushData();
		if (channel != null) {
			data.setUnitIndex(0);
			data.setChnIndex(channel.getDeviceChnIndex());
		} else {
			data.setUnitIndex(0xFF);
			data.setChnIndex(0xFF);
		}
		data.setError(error);
		data.setDate(new Date());
		data.setLog(log);
		AlertDecorator dec = new AlertDecorator(data);
		pushSendQueue(dec);
	}

	/**
	 * 整柜调试日志
	 * 
	 * @param channel
	 * @param log
	 */
	public void pushDebugLog(String log, boolean error) {
		pushDebugLog(null, log, error);
	}

	private Map<Integer, PushData> lastPushData = new ConcurrentHashMap<>();

	public void pushChnData(List<Channel> channels) {

		if (!isConnected()) {
			return;
		}

		for (Channel chn : channels) {
			PushData pd = new PushData();
			pd.unitIndex = 0;
			pd.chnIndex = chn.getDeviceChnIndex();

			pd.calState = chn.isReady()
					&& (chn.getChnState() != CalState.CALIBRATE || chn.getChnState() != CalState.CALCULATE)
							? CalState.READY
							: chn.getChnState();
			pd.matched = chn.getBindingCalBoardChannel() != null;
			if (pd.matched) {
				pd.matchBoardIndex = chn.getBindingCalBoardChannel().getBoardIndex();
				pd.matchChnIndex = chn.getBindingCalBoardChannel().getChnIndex();
			}

			if (lastPushData.containsKey(chn.getDeviceChnIndex())
					&& lastPushData.get(chn.getDeviceChnIndex()).equals(pd)) {
				continue;
			}
			lastPushData.put(chn.getDeviceChnIndex(), pd);
			LivePushData data = new LivePushData();
			data.appendPushData(pd);
			pushSendQueue(new AlertDecorator(data));
		}

	}

	/**
	 * 上报回复
	 * 
	 * @param timeOut
	 * @param dec
	 * @return
	 * @throws Exception
	 */
	private synchronized ResponseDecorator AlertDataAndResponse(int timeOut, AlertDecorator dec) throws Exception {
		if (responseMap.keySet().contains(dec.getDestData().getCodeKey())) {
			responseMap.remove(dec.getDestData().getCodeKey());
		}
		pushSendQueue(dec);
		// server.send(dec);
		ResponseDecorator response = findRecvCommand(timeOut, dec);
		checkResult(response);
		return response;
	}

	private void checkResult(ResponseDecorator response) throws Exception {
		if (response == null) {
			throw new Exception("response time out");
		}

		if (response.getResult().getCode() != Result.SUCCESS) {
			throw new Exception("result code error :" + response.getResult() + ",err info :" + response.getInfo());
		}
	}

	/**
	 * 追加测试并上报
	 * 
	 * @param dot
	 */
	public void appendTestDot(TestDot dot) {
		if (!server.isConnected()) {
			return;
		}
		pushTestDot(dot.channel, dot, null, false);
	}

	/**
	 * 发送测试点清空命令
	 * 
	 * @param channel
	 * @param type
	 */
	public void clearTestDot(Channel channel, TestType type) {
		pushTestDot(channel, null, type, true);
	}

	/**
	 * 上报测试点
	 * 
	 * @param channel
	 * @param dot     此为空时，判断 type,clear，否则忽略type clear
	 * @param type
	 * @param clear
	 */
	private void pushTestDot(Channel channel, TestDot dot, TestType type, boolean clear) {
		UploadTestDotData data = new UploadTestDotData();
		data.setUnitIndex(0);
		data.setChnIndex(channel.getDeviceChnIndex());

		UploadTestDot ud = null;
		if (dot != null) {
			ud = Channel.testDotToUploadDot(dot);
		} else {
			ud = new UploadTestDot();
			ud.unitIndex = 0;
			ud.chnIndex = channel.getDeviceChnIndex();
			ud.testType = type;
			ud.clear = clear;
		}
		data.setDot(ud);

		pushSendQueue(new AlertDecorator(data));
	}

	public void pushCalMatchVolt(MbMatchAdcData response) {
		CalMatchValueData data = new CalMatchValueData();
		data.setUnitIndex(0);
		//类型转换
		List<Logic2CalMatchData.AdcData> list = new ArrayList<>();
		for(DriverMatchAdcData.AdcData  adc : response.getAdcList()) {
			
			Logic2CalMatchData.AdcData  ad = new Logic2CalMatchData.AdcData();
			ad.adc = adc.adc;
			ad.chnIndex = adc.chnIndex;
			list.add(ad);
			
		}
		
		data.setAdcList(list);
		pushSendQueue(new AlertDecorator(data));
	}

}
