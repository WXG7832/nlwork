package com.nltecklib.io.mina;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.lab.Decorator;

public class SyncNetworkConnector {
	
	private String ip;
	public static final int PORT = 8161;

	private NioSocketConnector connector;
	private IoSession session;

	private final static String PATTERN_IP = "^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$";
	private FSProtocolCodecFactory fsProtocolCodecFactory;
	
	public SyncNetworkConnector(NlteckPackageFactory factory) {
		
		connector = new NioSocketConnector();
		
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(fsProtocolCodecFactory = new FSProtocolCodecFactory(true, factory)));
		
		SocketSessionConfig cfg = connector.getSessionConfig();
		
		cfg.setUseReadOperation(true);		

	}

	
	public boolean connect(String ip, int port) {
		if (ip == null || !checkIP(ip)) {
			
			//System.out.println(ip);
			return false;
		}		
		
		ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, port));
		
		// 阻塞等待，知道链接服务器成功，或被中断
		connectFuture.awaitUninterruptibly();
		
		if (!connectFuture.isConnected()) {
			
			return false;
		}
		session = connectFuture.getSession();
	
		return true;
	}
	
	public void disConnect() {
		
		if (session != null) {
			session.closeNow();		
		}
	}
	
	public static boolean checkIP(String input) {
		
		Pattern p = Pattern.compile(PATTERN_IP);
		Matcher m = p.matcher(input);
		return m.matches();
	}
	
	public void setConnectTimeOut(long miliSeconds) {

		connector.setConnectTimeoutMillis(miliSeconds);
	}

	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public boolean isConnected() {

		return session != null ? session.isConnected() : false;
	}
	
	public synchronized Object send(Decorator sendData, int timeout) throws NetworkException {
		if (session == null) {
			return null;
		}
				
		if (session.isConnected()) {
			
			//发送
			session.write(sendData).awaitUninterruptibly();
			
			//接收
			ReadFuture readFuture  = session.read();
			
			//超时
			if (readFuture.awaitUninterruptibly(timeout, TimeUnit.MILLISECONDS)) {
				
				if (readFuture.getMessage() instanceof Decorator) {
					Decorator pack = (Decorator) (readFuture.getMessage());
					
					return pack;
				}				
			} else {
				
				throw new NetworkException("网络通信超时");
			}
		}	
		return null;
	}
	
	public synchronized Object send(NlteckIOPackage sendData, int timeout) throws NetworkException {
		if (session == null) {
			return null;
		}
		
		Object obj = null;		
		if (session.isConnected()) {
			
			//发送
			session.write(sendData).awaitUninterruptibly();
			
			//接收
			ReadFuture readFuture  = session.read();
			
			//超时
			if (readFuture.awaitUninterruptibly(timeout, TimeUnit.MILLISECONDS)) {
				
				obj = readFuture.getMessage();
			} else {
				
				throw new NetworkException("网络通信超时");
			}
		}	
		return obj;
	}
	
	/**
	 * 设置调试数据监听器
	 * @author  wavy_zheng
	 * 2020年10月31日
	 * @param listener
	 */
	public void setDebugDataListener(DebugDataListener listener) {
		
		if(fsProtocolCodecFactory != null) {
			
			fsProtocolCodecFactory.setListener(listener);
		}
	}

	

}
