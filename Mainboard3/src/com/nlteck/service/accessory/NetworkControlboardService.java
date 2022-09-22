package com.nlteck.service.accessory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.nltecklib.io.mina.DebugDataListener;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.protocol.lab.ConfigDecorator;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Decorator;
import com.nltecklib.protocol.lab.Entity;
import com.nltecklib.protocol.lab.QueryDecorator;
import com.nltecklib.protocol.lab.ResponseDecorator;
import com.nltecklib.protocol.lab.accessory.IPC_PowerOptData;
import com.nltecklib.protocol.lab.accessory.IPC_PowerOptData.PowerOFFSign;
import com.nltecklib.protocol.lab.accessory.PowerSwitchData;
import com.nltecklib.protocol.lab.accessory.AIPAddressData;
import com.nltecklib.protocol.lab.accessory.AccessoryEnvironment.RunState;
import com.nltecklib.protocol.li.test.CalBoard.PowerSwitchControlData;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Result;
import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.LogUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年6月15日 下午6:16:52
* 类说明
*/
public class NetworkControlboardService extends AbsControlboardService {
   
	public static final int PORT = 5000;
	protected NetworkConnector connector ; // 网络连接器
	public final int NET_CONNECT_TIME_OUT = 3000;
	private Map<Code,Decorator>   recvBuff = new ConcurrentHashMap<>();
	private Logger logger;
	private String ip;
	
	
	public NetworkControlboardService(String ip) {
		
		logger = LogUtil.getLogger("controlboard");
		this.ip = ip;
		initNetwork();
		
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		// 固定延时
		
		service.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {

				try {

					if (!isConnected()) {

						// 开始自动重连
						connect(); // 连接控制板
						
						logger.info("connect board ok!");

					} 
				} catch (Exception e) {
                      
					
					//e.printStackTrace();
				}

			}

		}, 3, 15, TimeUnit.SECONDS);
		
	}
	
	
	/**
	 * 初始化控制板网络
	 * @author  wavy_zheng
	 * 2021年8月11日
	 */
	private void initNetwork() {
		
		Entity entity = new Entity();
		connector = new NetworkConnector(entity, false);
        connector.setConnectTimeOut(NET_CONNECT_TIME_OUT);
		
		connector.setDebugDataListener(new DebugDataListener() {

			@Override
			public void onEncode(byte[] data) {
				
				logger.info("send:" +  LogUtil.printArray(data));
				
			}

			@Override
			public void onDecode(byte[] data) {
				// TODO Auto-generated method stub
				logger.info("recv:" + LogUtil.printArray(data));
			}
			
			
		});
		connector.setNetworkListener(new NetworkListener() {
			
			@Override
			public void send(IoSession session, Object message) {
				
				
				
			}
			
			@Override
			public void receive(IoSession session, Object message) {
				
				ResponseDecorator response = (ResponseDecorator)message;
				recvBuff.put(response.getCode(), response);
				
			}
			
			@Override
			public void idled(IoSession session) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void exception(IoSession session, Throwable cause) {
				
				
				logger.error(BaseUtil.getThrowableException(cause));
				
			}
			
			@Override
			public void disconnected(IoSession session) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connected(IoSession session) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
    private void clearRecvBuff(Data data) {
		
		recvBuff.remove(data.getCode());
	}
    
    private ResponseDecorator findRecvData(Data data , int timeOut) {
		
		long tick = System.currentTimeMillis();
		do {
			
			BaseUtil.sleep(200);
			ResponseDecorator response = (ResponseDecorator) recvBuff.get(data.getCode());
			if(response != null) {
				
				return response;
			}
			
			
		} while(System.currentTimeMillis() - tick <= timeOut);
		
		return null;
		
	}
    
    public void connect() throws Exception {
    	
    	if (!connector.isConnected()) {
    		if (!connector.connect(ip, PORT)) {

    			throw new Exception("连接控制板失败！");
    		}
    		
    	}
    }
    
    public void disconnect() {
    	
    	if(connector != null ) {
    		
    		if(connector.isConnected()) {
    			
    			connector.disconnect();
    			connector = null;
    		}
    	}
    	
    	
    }
    
    
    public ResponseDecorator  sendAndRecv(Data sendData , int timeOut) {
		
		clearRecvBuff(sendData);
		connector.send(new ConfigDecorator(sendData));
		return findRecvData(sendData, timeOut);
		
	}
	
    public ResponseDecorator queryAndRecv(Data queryData , int timeOut) {
		
		clearRecvBuff(queryData);
		connector.send(new QueryDecorator(queryData));
		return findRecvData(queryData, timeOut);
	}
	
	
	@Override
	public void power(boolean on) throws Exception {
		
		PowerSwitchData psd = new PowerSwitchData();
        psd.setMainIndex(0);
        psd.setPowerState(RunState.ON);
        ResponseDecorator response = sendAndRecv(psd , 2000);
		if(response.getResult() != Result.SUCCESS) {
			
			throw new Exception("启动逆变电源失败,错误码:" + response.getResult());
		}
        
	}

	@Override
	public void cutoffPower() throws Exception {
		
		IPC_PowerOptData  data = new IPC_PowerOptData();
		data.setPowerOFFSign(PowerOFFSign.POWER_OFF);
		ResponseDecorator response = sendAndRecv(data , 2000);
		if(response.getResult() != Result.SUCCESS) {
			
			throw new Exception("通知控制板切断电源失败,错误码:" + response.getResult());
		}

	}

	@Override
	public void changeIp(String ip) throws Exception {
		
		
		AIPAddressData data = new AIPAddressData();
		data.setIpAddress(ip);
		//修改完
		ResponseDecorator response = sendAndRecv(data , 2000);
        if(response.getResult() != Result.SUCCESS) {
			
			throw new Exception("修改控制板IP失败,错误码:" + response.getResult());
		}

	}

	@Override
	public IPC_PowerOptData heartbeat() throws Exception {
		
		
		IPC_PowerOptData  data = new IPC_PowerOptData();
		ResponseDecorator response = queryAndRecv(data, 2000);
		data = (IPC_PowerOptData)response.getDestData();
		
		System.out.println(data.getPowerOFFSign().name());
		return data;

	}
	
	public boolean isConnected() {

		return connector == null ? false : connector.isConnected();
	}

}
