package com.nltecklib.protocol.fins;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.LogUtil;

/**
 * 消息收发处理
 * 
 * @author Administrator
 *
 */
public class FinsMessageHandler extends IoHandlerAdapter {

	private FinsMessageReceiver receiver;

	public FinsMessageHandler(FinsMessageReceiver receiver) {

		this.receiver = receiver;

	}

	@Override
	public void inputClosed(IoSession session) throws Exception {

		super.inputClosed(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		String remoteIp = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		Logger logger = LogUtil.getLogger(remoteIp + "(error)");
		String exception = cause.getMessage();
		if (receiver != null)
			receiver.receive("网络通信异常：" + exception, false);

		logger.error("网络通信异常:" + exception);
		logger.error("详细错误信息:" + BaseUtil.getThrowableException(cause));

	}

	

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		// 同步读取数据，这里不做任何处理
		
//		ResponseData data = (ResponseData) message;
//		System.out.println("接收数据：(" + System.currentTimeMillis() + ") " + FinsData.printList(data));

	}


	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {

	}

	
}
