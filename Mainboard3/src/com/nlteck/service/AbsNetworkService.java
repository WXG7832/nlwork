package com.nlteck.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.RunningLamp;
import com.nlteck.UIException;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.io.mina.NetworkServer;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.DefaultResult;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.main.AlertData;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月24日 下午3:16:07 网络收发服务组件
 */
public abstract class AbsNetworkService {

	public final static int MAX_SEND_RECV_COUNT = 10; // 发送接收最大差异数，用于判断设备是否掉线
	public final static int PUSH_TIME_DELAY = 1000; // 1秒推送1次数据

	protected Logger logger;
	protected MainBoard mainboard;
	protected NetworkServer server;
	protected String localIp;
	protected LinkedBlockingQueue<Decorator> sendQueue = new LinkedBlockingQueue<Decorator>(); // 发送队列
	protected long alertSendDataIndex; // 报警或离线数据推送序号
	protected long alertRecvDataIndex; // 报警或离线数据推送回复序号
	protected long sendPushCount;
	protected long recvResponseCount;

	protected ScheduledExecutorService executor; // 定时推送线程

	// 数据推送缓存
	protected List<PickupData> sendBuff = Collections.synchronizedList(new ArrayList<PickupData>());
	// 等待发送缓存队列，在PC确认回复后再发送下一条
	protected Queue<Decorator> wait2SendBuff = new ConcurrentLinkedQueue<Decorator>();

	protected AbsNetworkService(MainBoard mb) {

		this.mainboard = mb;
		try {
			logger = LogUtil.createLog("log/NetworkService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 网络组件初始化
	 * 
	 * @author wavy_zheng 2020年11月12日
	 */
	public abstract void initNetwork() throws Exception;

	/**
	 * 匹配命令返回对象
	 * 
	 * @author wavy_zheng 2020年11月12日
	 * @param decorator
	 * @return
	 */
	public abstract ResponseDecorator findResponse(Decorator decorator);

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	/**
	 * 推入发送队列
	 */
	public void pushSendQueue(Decorator dec) {

		if (dec.getCode() == MainCode.PickupCode || dec.getCode() == MainCode.UpgradeProgressCode) {

			sendPushCount++; // 数据推送计数
			sendQueue.offer(dec);
		} else {

			wait2SendBuff.offer(dec);
			if (!mainboard.isOffline()) {
				sendLeavedAlertData(); // 发送报警或离线缓存队列的数据
			}
		}
	}

	/**
	 * 发送缓存的数据
	 */
	public synchronized void sendLeavedAlertData() {

		if (alertSendDataIndex == alertRecvDataIndex && wait2SendBuff.peek() != null) {

			alertSendDataIndex++;
			System.out.println("send alert or log :" + wait2SendBuff.peek());
			sendQueue.offer(wait2SendBuff.peek());

		}
	}

	// 发送队列
	protected void processSendQueue() {

		Thread sendThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					Decorator decorator;
					try {
						if (mainboard.isOffline()) {

							CommonUtil.sleep(1000);
							// 处于掉线状态，无发送
							continue;
						}
						decorator = sendQueue.take();
						if (decorator != null) {

							// 写入网络
							if (decorator.getCode() != MainCode.PickupCode
									&& decorator.getCode() != MainCode.DeviceStateCode) {
								// System.out.println("-->PC：" + decorator);
							}

							boolean sendOk = server.send(decorator);

						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		});
		sendThread.setDaemon(true);
		sendThread.start();
	}

	/**
	 * 推送日志或报警日志
	 * 
	 * @param unitIndex
	 * @param deviceChnIndex
	 * @param content
	 */
	public void pushSendQueue(int unitIndex, int deviceChnIndex, AlertCode code, String content) {

		AlertData alertData = new AlertData();
		alertData.setAlertCode(code);
		alertData.setAlertInfo(content);
		alertData.setUnitIndex(unitIndex);
		alertData.setChnIndex(deviceChnIndex);
		alertData.setDeviceChnIndex(deviceChnIndex);
		alertData.setResult(DefaultResult.SUCCESS);
		pushSendQueue(new AlertDecorator(alertData));
	}

	/**
	 * 推送异常报警日志
	 * 
	 * @author wavy_zheng 2020年11月13日
	 * @param exception
	 */
	public void pushSendQueue(AlertException exception) {

		AlertData alertData = new AlertData();
		alertData.setAlertCode(exception.getAlertCode());
		alertData.setAlertInfo(exception.getMessage());
		alertData.setResult(DefaultResult.SUCCESS);
		alertData.setDate(new Date());
		alertData.setUnitIndex(exception.getChannel() == null ? -1 : 0);
		alertData.setChnIndex(exception.getChannel() == null ? -1 : exception.getChannel().getDeviceChnIndex());
		alertData.setDeviceChnIndex(alertData.getChnIndex());
		pushSendQueue(new AlertDecorator(alertData));
	}

	// 收到PC的报警消息，回复报警确认
	public void confirmAlertOrOfflineResponse() {

		System.out.println("confirm alert and send next");

		alertRecvDataIndex = alertSendDataIndex;
		wait2SendBuff.poll(); // 删除
		sendLeavedAlertData(); // 发送下一条

	}

	/**
	 * 切断网络
	 * 
	 * @author wavy_zheng 2020年11月26日
	 */
	public void cutoff() {

		if (server.isConnected()) {

			server.disconnect();
			mainboard.setOffline(true);
			; // 掉线状态
		}
	}

	/**
	 * 关闭服务
	 * 
	 * @author wavy_zheng 2020年11月14日
	 */
	public abstract void closeService();

	public List<PickupData> getSendBuff() {
		return sendBuff;
	}

	/**
	 * 收到推送回复确认
	 * 
	 * @author wavy_zheng 2022年1月24日
	 */
	public void recvPcPushResponse() {

		recvResponseCount = sendPushCount;
		mainboard.setHeartbeatTime(new Date()); //设置心跳时间
		sendBuff.remove(0);
	}

	public void stopPushWork() {

		if (executor != null) {
			CommonUtil.exitThread(executor, 2000);
			executor = null;
		}

	}

	/**
	 * 启动推送工作
	 * 
	 * @author wavy_zheng 2022年1月24日
	 */
	public void startPushWork() {

		if (executor == null) {
			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					
					if(mainboard.isOffline()) {
						
						return;
					}

					// 每次推送后至少保留一个缓存数据
					PickupData pd = new PickupData();
					pd.setDate(new Date());
					pd.setResult(DefaultResult.SUCCESS);

					pd.setState(mainboard.getState());
					pd.setLoopIndex(0);

					List<ChannelData> list = new ArrayList<ChannelData>();

					for (DriverBoard driver : mainboard.getDriverBoards()) {

						for (int n = 0; n < driver.getChannelCount(); n++) {

							Channel channel = driver.getChannel(n);
							synchronized (channel.getRuntimeCaches()) {

								if (channel.getState() == ChnState.RUN) {
									if (channel.getRuntimeCaches().size() > 1) {

										list.addAll(channel.getRuntimeCaches().subList(0,
												channel.getRuntimeCaches().size() - 1));
										
                                      

										
										channel.getRuntimeCaches().subList(0, channel.getRuntimeCaches().size() - 1)
												.clear(); // 清除打包上传缓存
										
										
									}
								} else {
									
									if(channel.isRuntimeCachesEmpty()) {
										
										//推送udt
										if(channel.getUdtData() != null) {
										    list.add(channel.getUdtData());
										   
										}
										
									} else {
									    list.addAll(channel.getRuntimeCaches());
									    channel.getRuntimeCaches().clear();
									    
                                       
									}
									
									
								}

							}
						}
					}

					pd.setChnCount(list.size());
					pd.setTemp(0); // 电源柜没有此项
					pd.setChnDatas(list); // 设置采集数据集
					pd.setUnitIndex(0);

					pushDataToPc(pd);

				}

			}, 1000, PUSH_TIME_DELAY, TimeUnit.MILLISECONDS);

		}

	}

	/**
	 * 将数据推送给上位机
	 * 
	 * @author wavy_zheng 2020年11月26日
	 * @param pickupData
	 * @return true 代表数据已经发送给PC ； false则表示未能发送
	 */
	public boolean pushDataToPc(PickupData pickupData) {

		if (sendBuff.isEmpty()) {

			sendBuff.add(pickupData);
			pushSendQueue(new AlertDecorator(pickupData));

			if (getSendPushCount() - getRecvResponseCount() >= MAX_SEND_RECV_COUNT) {

				logger.info("push data to pc send :" + getSendPushCount() + ",recv:" + getRecvResponseCount()
						+ " ,cut off net work!");

				if (!mainboard.isOffline()) {
					// 切断网络
					cutoff();

				}

			}

			return true;
		} else {

			// 推送上一次的数据
			pushSendQueue(new AlertDecorator(sendBuff.get(0)));
			return true;

		}

	}

	public long getSendPushCount() {
		return sendPushCount;
	}

	public long getRecvResponseCount() {
		return recvResponseCount;
	}
	

}
