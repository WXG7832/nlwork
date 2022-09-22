package com.nltecklib.io.mina;

import org.apache.mina.core.session.IoSession;

/**
 * 网络数据交互监听器
 * 
 * @author Administrator
 *
 */
public interface NetworkListener {
   
	/**
	 * 数据已成功发送到网络端口
	 * @param session
	 * @param message
	 */
	void send(IoSession session, Object message);
	/**
	 * 接收到网络数据
	 * 
	 * @param message
	 */
	void receive(IoSession session, Object message);
    
	/**
	 * 网络已连接
	 * @param session
	 */
	void connected(IoSession session);
    
	/**
	 * 网络已断开
	 * @param session
	 */
	void disconnected(IoSession session);
	
	/**
	 * 有异常发生
	 * @param session
	 */
	void exception(IoSession session , Throwable cause);
	
	/**
	 * 网络空闲
	 * @param session
	 */
	void idled(IoSession session) ;

}
