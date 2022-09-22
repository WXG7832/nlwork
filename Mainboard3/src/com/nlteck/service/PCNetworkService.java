package com.nlteck.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.RunningLamp;
import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.service.data.OfflineDataProviderService;
import com.nlteck.service.data.OnlineDataProviderService;
import com.nlteck.util.LogUtil;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.io.mina.NetworkServer;
import com.nltecklib.protocol.li.AlertDecorator;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.MBWorkform.MBWorkformEnvironment.MBWorkformCode;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.PickupData;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月12日 上午11:30:39
* 主控与PC的网络交互服务组件
*/
public class PCNetworkService extends AbsNetworkService {
    

	
	
	private final static int  PORT = 8161; //PC与主控通信端口
	
	public PCNetworkService(MainBoard mb) {
		super(mb);
		
		
		
	}

	@Override
	public void initNetwork() throws Exception {
		
		
		logger.info("正在初始化网络服务器");
		server = new NetworkServer(new Entity());
		server.setIdleTimeout(0); //不启用空闲检测
		
		
		server.createServer(localIp, PORT, new NetworkListener() {
			
			@Override
			public void send(IoSession session, Object message) {
				
				
			}
			
			@Override
			public void receive(IoSession session, Object message) {
				
					
					Decorator decorator = (Decorator)message;
					//logger.info("code = " + decorator.getCode());
					ResponseDecorator response = null;
					if(decorator.getCode() instanceof MainCode) {
						//PC协议命令回复
					  response = Context.getPcController().processCmdFromNetwork(decorator);
					} 

					if (response != null) {

						sendQueue.offer(response);
					}
	
			}
			
			@Override
			public void idled(IoSession session) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void exception(IoSession session, Throwable cause) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void disconnected(IoSession session) {
				
				
				logger.info("network is cut off");
				mainboard.setOffline(true); // 掉线状态
				stopPushWork(); //停止推送工作
				server.disconnect();
             
				//设置成离线运行数据工厂
				Context.setDataProvider(new OfflineDataProviderService(mainboard));
				
				if (Context.getCoreService().getOfflineRunningCfg().isForbid()) {// 禁用离线运行
                      
					//暂停整机流程
					if(mainboard.getState() == State.FORMATION) {
						
						logger.info("pause mainboard");
						try {
							Context.getCoreService().emergencyPause(null);
						} catch (AlertException e) {
							
							e.printStackTrace();
						}
					}
					
					// 进入掉线状态
					try {
						Context.getAlertManager().handle(AlertCode.OFFLINE, I18N.getVal(I18N.NetworkOff), false);
					} catch (AlertException e) {

						e.printStackTrace();
					}
				} else {

					 //进入离线运行模式
					Context.getPcNetworkService().pushSendQueue(-1, -1, AlertCode.NORMAL, I18N.getVal(I18N.DeviceEnterOfflineRun));
				}
				
			}
			
			@Override
			public void connected(IoSession session) {
				
				
				mainboard.setOffline(false);
				logger.info("connect client");
				/*for(ControlUnit cu : mainboard.getControls()) {
					
					 cu.clearNetworkState();
				}*/
				// 收到PC的推送数据确认回复
				recvResponseCount = sendPushCount;
				
				//设置成在线运行数据工厂
				Context.setDataProvider(new OnlineDataProviderService(mainboard));

				// 进入上线状态
				try {
					Context.getAlertManager().handle(AlertCode.OFFLINE, "", true);
				} catch (AlertException e) {

					e.printStackTrace();
				}
				alertRecvDataIndex = alertSendDataIndex;
				sendLeavedAlertData();// 将因断网的离线数据或报警发送完毕
				
				//推送设备离线数据
				Context.getDataProvider().pushAllOfflineDataToPc(null);
				
				startPushWork(); //重新启用数据推送
				
			}
		});
		
		//启用发送工作线程
		processSendQueue(); 
	}

	@Override
	public ResponseDecorator findResponse(Decorator decorator) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

    
	/**
	 * 断开服务
	 * @author  wavy_zheng
	 * 2020年11月14日
	 */
	@Override
	public  void closeService() {
		
		server.disconnect(); // 切断网络
		server.stopListen(); // 停止监听网络
		
	}
	
	
	/**
	 * 转换结果
	 * @author  wavy_zheng
	 * 2022年1月23日
	 * @param response
	 * @return
	 */
	public ResponseDecorator  convertResponseFrom(com.nltecklib.protocol.power.ResponseDecorator response) {
		
		
		
		
		return null;
		
	}

	
	
	
	
	
}
