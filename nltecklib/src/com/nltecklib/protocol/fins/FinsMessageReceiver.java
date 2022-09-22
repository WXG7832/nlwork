package com.nltecklib.protocol.fins;

/**
 * 用于SOCKET通信消息接收
 * @author Administrator
 *
 */
public interface FinsMessageReceiver {
      
	   void receive(String msg , boolean exception);
	  
}
