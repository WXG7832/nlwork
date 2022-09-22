package com.nltecklib.io.mina;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;

public class NetworkServer {

	private final static int SERVICE_PORT = 8161;
	private NioSocketAcceptor acceptor;
	private ServerMessageHandler handler;
	private NlteckPackageFactory factory;
	private int idleTimeout = 10; // 多久空闲断网

	public NetworkServer(NlteckPackageFactory factory) {

		this.factory = factory;
	}

	/**
	 * 建立网络服务
	 * 
	 * @param listener
	 *            网络事件监听器
	 * @throws IOException
	 */
	public void createServer(NetworkListener listener) throws IOException {
        
		createServer(null, SERVICE_PORT, listener);

	}
	
	/**
	 * 建立网络服务
	 * @author  wavy_zheng
	 * 2021年5月2日
	 * @param port
	 * @param listener
	 * @throws IOException
	 */
	public void createServer(Integer port , NetworkListener listener) throws IOException {
		
		
		createServer(null, port, listener);
	}

	/**
	 * 通过ip 端口建立网络
	 * 
	 * @param ip
	 * @param port
	 * @param listener
	 * @throws IOException
	 */
	public void createServer(String ip, Integer port, NetworkListener listener) throws IOException {

		// 创建一个非堵塞的server端的Socket
		acceptor = new NioSocketAcceptor();

		// 指定编码解码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new FSProtocolCodecFactory(false, factory)));

		// 设置重用端口，防止重启后端口被占
		acceptor.setReuseAddress(true);
       
		// 不使用MINA框架的处理线程IOProcessor
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));

		// 指定最终业务处理器
		acceptor.setHandler(handler = new ServerMessageHandler());
		// 注册事件
		registerNetworkEvent(listener);

		// 启动服务器
		if (ip != null) {
			acceptor.bind(new InetSocketAddress(ip, port));
		} else {
          
			acceptor.bind(new InetSocketAddress(port));
		}

		// 设置读写空闲时间
		if (idleTimeout > 0) {
			setIdleTimeout(idleTimeout);// 初始化时默认为10s掉线
		}

	}

	public void setIdleTimeout(int timeOut) {

		// 设置读写空闲时间
		if(acceptor != null) {
		   acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, timeOut);
		}
		idleTimeout = timeOut;
	}

	/**
	 * 注册网络事件
	 * 
	 * @param listener
	 */
	public void registerNetworkEvent(NetworkListener listener) {

		handler.setListener(listener);

	}

	/**
	 * 切断网络
	 */
	public void disconnect() {

		handler.disconnect();
	}

	public boolean isConnected() {

		if (handler != null) {
			return handler.isConnected();
		}
		return false;
	}

	/**
	 * 停止监听
	 */
	public void stopListen() {

		acceptor.unbind();
		acceptor.dispose();
	}

	public static String getLocalIpAddress(IoSession session) {

		InetSocketAddress inetSocketAddress = (InetSocketAddress) session.getLocalAddress();
		InetAddress address = inetSocketAddress.getAddress();
		return address.getHostAddress();
	}

	public static String getRemoteIpAddress(IoSession session) {

		InetSocketAddress inetSocketAddress = (InetSocketAddress) session.getRemoteAddress();
		InetAddress address = inetSocketAddress.getAddress();
		return address.getHostAddress();
	}

	// 同个时刻最能有一条网络报文写入网口
	public synchronized boolean send(NlteckIOPackage data) {

		if (handler.getServiceSession() == null) {
			return false;
		}
		if (isConnected()) {

			WriteFuture wf = handler.getServiceSession().write(data); // 发送对象
			
			return wf.isWritten();
		}

		return false;
	}

	/**
	 * 设置接收缓存大小
	 * 
	 * @param buffSize
	 */
	public void setRecvBuffSize(int buffSize) {

		acceptor.getSessionConfig().setReceiveBufferSize(buffSize);

	}

	/**
	 * 设置发送缓存大小
	 * 
	 * @param buffSize
	 */
	public void setSendBuffSize(int buffSize) {

		acceptor.getSessionConfig().setSendBufferSize(buffSize);
	}

}
