package com.nltecklib.io.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


/**
 * 服务端消息处理器
 * 
 * @author Administrator
 *
 */
public class ServerMessageHandler extends IoHandlerAdapter {

	private boolean singleConnect = true; // 只允许连接单个IP地址
	private int maxIdleTimeout = 10; // 最大空闲时间，单位s;超时将自动切断网络；0表示不切断
	private NetworkListener listener;
	private IoSession serviceSession; // 已连接的会话对象，主控同个时刻只能允许一个IP连接

	public NetworkListener getListener() {
		return listener;
	}

	public void setListener(NetworkListener listener) {
		this.listener = listener;
	}

	public boolean isSingleConnect() {
		return singleConnect;
	}

	public void setSingleConnect(boolean singleConnect) {
		this.singleConnect = singleConnect;
	}

	public int getMaxIdleTimeout() {
		return maxIdleTimeout;
	}

	public void setMaxIdleTimeout(int maxIdleTimeout) {
		this.maxIdleTimeout = maxIdleTimeout;
	}
	
	@Override
	public void inputClosed(IoSession session) throws Exception {

		
		super.inputClosed(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {


		
		if(listener != null) {
			
			listener.exception(session,cause);
		}

	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		if (listener != null) {
			listener.receive(session, message);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
            
		if (listener != null) {
			
			listener.send(session, message);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

		if (listener != null) {
			listener.disconnected(session);
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
        
		if(serviceSession != null) {
			
			//切断旧连接
			serviceSession.closeNow();
            /*if(this.listener != null) {
				
				this.listener.disconnected(serviceSession);
			}*/
		}
		serviceSession = session;
		//通知设备网络连接消息
		if (listener != null) {
			listener.connected(session);
		}
	}
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		
		
		 System.out.println("idle time " + status );
		
		 if(listener != null) {
			 
			 listener.idled(session);
		 }
		 
		
	}
	
	public boolean isConnected() {
		
		if(serviceSession != null) {
			
			return serviceSession.isConnected();
		}
		return false;
	}
	
	public void disconnect() {
		
		if(serviceSession != null) {
			
			serviceSession.closeNow();
			serviceSession = null;
		}
	}

	public IoSession getServiceSession() {
		return serviceSession;
	}
	
	

}
