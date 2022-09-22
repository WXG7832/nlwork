package com.nlteck.connector;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.IoSession;

import com.google.gson.Gson;
import com.nlteck.base.I18N;
import com.nlteck.fireware.DeviceCore;
import com.nlteck.fireware.DeviceCore.UnitLocker;
import com.nltecklib.io.mina.NetworkConnector;
import com.nltecklib.io.mina.NetworkListener;
import com.nltecklib.utils.CoreA7EnvUtil;
import com.nltecklib.protocol.li.main.PickupData;
import com.nltecklib.protocol.power.AlertDecorator;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Decorator;
import com.nltecklib.protocol.power.Entity;
import com.nltecklib.protocol.power.Environment.Result;
import com.nltecklib.protocol.power.ResponseDecorator;

/**
 * 设备主控通信，此为客户端
 * 
 * @author guofang_ma
 *
 */
public class DeviceConnector {

	private DeviceCore deviceCore;

	private String ip;
	private int port;

	private NetworkConnector connector;
	private Map<String, ResponseDecorator> receiveData = new ConcurrentHashMap<String, ResponseDecorator>();

	private static String getKey(Data data) {
		return  data.getCode() + "_"  + data.getDriverIndex() + "_"
				+ data.getChnIndex();
	}
	
	//wang_xingguo
	private static String getKey2(Data data) {
		return  data.getCode() + "_"  + 255 + "_"
				+ 255;
	}

	public NetworkConnector getConnector() {
		return connector;

	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 异步通信
	 * 
	 * @param sendData
	 * @param timeOut
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	private ResponseDecorator asyncSend(Decorator sendData, int timeOut) throws IOException {

		if (connector == null || !connector.isConnected()) {
			throw new RuntimeException("network is not connected");
		}

		String key = getKey(sendData.getDestData());
//		String key2 = getKey2(sendData.getDestData());
		if (receiveData.containsKey(key)) {
			receiveData.remove(key);
		}
		connector.send(sendData);
		long startTime = System.currentTimeMillis();
		while (true) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (receiveData.containsKey(key)) {
				ResponseDecorator response = receiveData.get(key);
				receiveData.remove(key);
				return response;
			}
//			else if (receiveData.containsKey(key2)) {
//				
//				ResponseDecorator response = receiveData.get(key2);
//				receiveData.remove(key2);
//				return response;
//			}
			if (System.currentTimeMillis() - startTime > timeOut) {
				Gson gson=new Gson();
				System.out.println(gson.toJson(receiveData) );
				throw new IOException(I18N.getVal(I18N.DeviceCommunicationTimeOut,
						I18N.getVal(sendData.getDestData().getClass().getSimpleName())));
			}
			
		}
	}

	public boolean connect() {

		if (connector.isConnected()) {
			connector.disconnect();
		}

		return connector.connect(ip, port);
	}

	public void disConnect() {
		if (connector.isConnected()) {
			connector.disconnect();
		}
	}

	public DeviceConnector(DeviceCore deviceCore) {
		this.deviceCore = deviceCore;

		connector = new NetworkConnector(new Entity(), false);
		connector.setNetworkListener(new NetworkListener() {

			@Override
			public void send(IoSession session, Object message) {

			}

			@Override
			public void receive(IoSession session, Object message) {
				if (message instanceof AlertDecorator) {// 采集主动上报回复

					ResponseDecorator response = null;

					try {
						Data data = ((AlertDecorator) message).getDestData();
						data.setResult(Result.SUCCESS);
						response = new ResponseDecorator(data, false);

					} catch (Exception e) {
						if (response.getResult() == Result.SUCCESS) {
							response.setResult(Result.FAIL);
						}
						response.setInfo(e.getMessage() == null ? "null" : e.getMessage());
					}
					if (response != null) {
						connector.send(response);
					}

				} else if (message instanceof ResponseDecorator) {
					// 回复协议
					ResponseDecorator resDec = (ResponseDecorator) message;
					System.out.println("==========resDec"+resDec.getDestData().getCode()+"=============");
					receiveData.put(getKey(resDec.getDestData()), resDec);
				}

			}

			@Override
			public void idled(IoSession session) {
				deviceCore.getCore().getLogger().info("device idled");
			}

			@Override
			public void exception(IoSession session, Throwable cause) {
				deviceCore.getCore().getLogger().error("device connect error:" + cause.getMessage(), cause);
			}

			@Override
			public void disconnected(IoSession session) {
				deviceCore.getCore().getLogger().info("device disconnected");
				deviceCore.getCore().getDeviceCore().setBaseInfoData(null);

			}

			@Override
			public void connected(IoSession session) {
				// TODO Auto-generated method stub
				deviceCore.getCore().getLogger().info("device connected");
			}
		});
	}

	private void checkResult(ResponseDecorator response) {

		if (response.getResult() != Result.SUCCESS) {
			throw new RuntimeException(I18N.getVal(response.getDestData().getClass().getSimpleName()) + ":"
					+ response.getResult().name() + "[" + response.getInfo() + "]");
		}
	}

	public ResponseDecorator sendAndResponse(Decorator dec, int timeOut) {

		ResponseDecorator result = null;
		// 分区同步
		synchronized (this) {
			try {
				result = asyncSend(dec, timeOut);
			} catch (IOException e) {
				try {
					Thread.sleep(1000);
					result = asyncSend(dec, timeOut);
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}

			}
		}
		checkResult(result);
		return result;

	}

}
