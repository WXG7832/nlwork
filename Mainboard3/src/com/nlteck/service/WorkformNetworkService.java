package com.nlteck.service;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.io.mina.NetworkServer;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.power.Decorator;
import com.nltecklib.protocol.power.Entity;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.calBox.calBox_device.CalBoxDeviceEnvironment.CalBoxDeviceCode;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月12日 下午1:38:16 与校准主控通信网络组件 校准支持多个网络校准箱连接到端口上
 */
public class WorkformNetworkService {

	private Logger logger;
	private MainBoard mainboard;
	private final static int PORT = 8163; // 校准箱与校准主控通信端口
	private NetworkServer server;
	private String localIp;
	private LinkedBlockingQueue<Decorator> sendQueue = new LinkedBlockingQueue<Decorator>(); // 发送队列
	

	public WorkformNetworkService(MainBoard mb) {

		this.mainboard = mb;
		
		try {
			logger = LogUtil.createLog("log/WorkformNetworkService.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void initNetwork() throws Exception {

		logger.info("正在初始化网络服务器");
		server = new NetworkServer(new Entity());
		server.setIdleTimeout(0); // 不启用空闲检测

		server.createServer(localIp, PORT, new NetworkListener() {

			@Override
			public void send(IoSession session, Object message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void receive(IoSession session, Object message) {
				
				Decorator decorator = (Decorator)message;
				//logger.info("code = " + decorator.getCode());
				ResponseDecorator response = null;
				if(decorator.getCode() instanceof CalBoxDeviceCode) {
					//PC协议命令回复
				  response = Context.getWorkformController().processCmdFromNetwork(decorator);
				} 

				if (response != null) {
                     
					
					sendQueue.offer(response);
				}

			}

			@Override
			public void connected(IoSession session) {
				// TODO Auto-generated method stub
               
				logger.info("connect calbox! , box address : " + server.getRemoteIpAddress(session));
			}

			@Override
			public void disconnected(IoSession session) {
				
				server.disconnect();
				logger.info("disconnect !!!");

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
		
		//启用发送工作线程
		processSendQueue(); 

	}

	public ResponseDecorator findResponse(Decorator decorator) {
		// TODO Auto-generated method stub
		return null;
	}

	public void closeService() {
		// TODO Auto-generated method stub

	}

	/**
	 * 推入发送队列
	 */
	public void pushSendQueue(Decorator dec) {

		sendQueue.offer(dec);
	}

	// 发送队列
	protected void processSendQueue() {

		Thread sendThread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					Decorator decorator;
					try {
						if (!server.isConnected()) {

							CommonUtil.sleep(1000);
							// 处于掉线状态，无发送
							continue;
						}
						decorator = sendQueue.take();
						if (decorator != null) {
                            
							if(decorator instanceof ResponseDecorator) {
								
								   logger.info("response: driverIndex = " + decorator.getDestData().getDriverIndex() + "," +
								      decorator.getDestData().getChnIndex() + ",code:" + decorator.getCode() + ",result=" + decorator.getDestData().getResult().name());
							} else {
							       logger.info("send:" + decorator.getDestData());
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

}
