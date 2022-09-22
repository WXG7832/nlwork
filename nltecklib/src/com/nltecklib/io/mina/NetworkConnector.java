package com.nltecklib.io.mina;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.utils.LogUtil;

/**
 * 连接器
 * 
 * @author Administrator
 *
 */
public class NetworkConnector {

	public static final int PORT = 8161;
	private String ip;
	private boolean debug; // 是否用于调试

	private NioSocketConnector connector;
	private IoSession session;
	private MessageHandler handler;
	private NetworkListener listener;

	private boolean sync; // 是否同步?
	private Logger logger;
	private final static String PATTERN_IP = "^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$";
	private NlteckPackageFactory factory;

	private LinkedBlockingQueue<NlteckIOPackage> recvBuff = new LinkedBlockingQueue<NlteckIOPackage>();
	
	private FSProtocolCodecFactory fsProtocolCodecFactory;

	public NetworkConnector(NlteckPackageFactory factory, boolean sync) {

		connector = new NioSocketConnector();
		this.factory = factory;

		// 协议过滤器
		connector.getFilterChain().addLast("codec",  new ProtocolCodecFilter(fsProtocolCodecFactory = new FSProtocolCodecFactory(true, factory)));

		this.sync = sync;
		
		//不使用MINA框架的处理线程IOProcessor,使用按顺序处理的线程池技术;注意在receive方法里不能有耗时操作，会堵住线程；应另启用一个工作线程
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
				

		// 业务处理器
		connector.setHandler(handler = new MessageHandler());

		if (sync) {
			setNetworkListener(new NetworkListener() {

				@Override
				public void send(IoSession session, Object message) {
					// TODO Auto-generated method stub
                    
					System.out.println("send message : " + message);
				}

				@Override
				public void receive(IoSession session, Object message) {

					System.out.println("recv message : " + message);
					recvBuff.offer((NlteckIOPackage) message);

				}

				@Override
				public void exception(IoSession session, Throwable cause) {
					// TODO Auto-generated method stub

				}

				@Override
				public void disconnected(IoSession session) {
					// TODO Auto-generated method stub

				}

				@Override
				public void connected(IoSession session) {
					// TODO Auto-generated method stub

				}

				@Override
				public void idled(IoSession session) {
					// TODO Auto-generated method stub
					
				}
			});
		}

	}
	/**
	 * 设置接收缓存大小
	 * @param buffSize
	 */
	public void setRecvBuffSize(int buffSize) {
		
		connector.getSessionConfig().setReceiveBufferSize(buffSize);
		
	}
	/**
	 * 设置发送缓存大小
	 * @param buffSize
	 */
	public void setSendBuffSize(int buffSize) {
		
		connector.getSessionConfig().setSendBufferSize(buffSize);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setConnectTimeOut(long miliSeconds) {

		connector.setConnectTimeoutMillis(miliSeconds);
	}

	/**
	 * 同步发送接收消息对象；自动匹配回复消息，过滤其他消息；服务器禁用，适用于客户端小量数据发送
	 * 
	 * @param timeout
	 * @return
	 * @throws NetworkException
	 * @throws InterruptedException
	 */
	public Object sendAndRecvSyncMessage(Decorator sendData, int timeout) throws NetworkException, InterruptedException {
       
		if(!sync) {
			
			throw new NetworkException("不支持同步发送和接收消息");
		}
		//
		recvBuff.clear();
		
		send(sendData);
		long st = System.currentTimeMillis();

		do {
			Decorator pack = (Decorator) recvBuff.poll(timeout, TimeUnit.MILLISECONDS);
			if (pack == null) {

				throw new NetworkException("网络通信超时");
			}
			if (pack.getCode() == sendData.getCode()) {

				if (sendData.getDestData().supportUnit()) {

					if (pack.getDestData().getUnitIndex() == sendData.getDestData().getUnitIndex()) {

						return pack;
					}
				}else {
					
					return pack;
				}
			}
		} while (System.currentTimeMillis() - st <= timeout);
		
		return null;

		// ReadFuture readFuture = session.read();
		//
		// if (!readFuture.awaitUninterruptibly(timeout,TimeUnit.MILLISECONDS)) {
		//
		// throw new NetworkException("网络通信超时!");
		// }
		//
		// return readFuture.getMessage();

	}

	public static boolean checkIP(String input) {

		Pattern p = Pattern.compile(PATTERN_IP);
		Matcher m = p.matcher(input);
		return m.matches();
	}

	public boolean connect(String ip, int port) {

		if (ip == null || !checkIP(ip)) {

			//System.out.println(ip);
			return false;
		}

		logger = LogUtil.getLogger(ip);

		ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, port));

		// 阻塞等待，知道链接服务器成功，或被中断
		connectFuture.awaitUninterruptibly();

		if (!connectFuture.isConnected()) {

			String info = "connect to " + ip + " failed";

			return false;

		}

		session = connectFuture.getSession();

		return true;

	}

	public boolean isConnected() {

		return session != null ? session.isConnected() : false;
	}

	// 同个时刻最能有一条网络报文写入网口
	public synchronized void send(NlteckIOPackage data) {

		send(data, false);
	}

	public void send(NlteckIOPackage data, boolean sync) {

		if (session == null) {
			return;
		}
		if (session.isConnected()) {

			WriteFuture wf = session.write(data); // 发送对象
			if (sync)
				wf.awaitUninterruptibly();

		} else {

		}

	}

	public void disconnect() {

		if (session != null) {
			session.closeNow();
		}
	}

	public void setNetworkListener(NetworkListener listener) {

		handler.setListener(listener);
		this.listener = listener;
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
