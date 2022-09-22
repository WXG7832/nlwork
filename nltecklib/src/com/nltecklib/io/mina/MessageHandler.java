package com.nltecklib.io.mina;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;


/**
 * 消息收发处理
 * 
 * @author Administrator
 *
 */
class MessageHandler extends IoHandlerAdapter {

	private NetworkListener listener;

	public MessageHandler() {

	}

	public NetworkListener getListener() {
		return listener;
	}

	public void setListener(NetworkListener listener) {
		this.listener = listener;
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
	
	private String getThrowableException(Throwable cause) {
		
		ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		cause.printStackTrace(new java.io.PrintWriter(buf, true));
		String expMessage = buf.toString();
		try {
			buf.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return expMessage;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		if (listener != null) {
			listener.receive(session, message);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
            
		//System.out.println("messageSend:" + message);
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

		if (listener != null) {
			listener.connected(session);
		}
	}

}
