package com.nltecklib.protocol.modbus.tcp.util;

import java.util.concurrent.TimeUnit;

public class CommonUtil {
	
	public static void sleep(int timeout){
		
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isNotEmpty(String arg) {
		
		return !isEmpty(arg);
	}

	public static boolean isEmpty(String arg) {
		// TODO Auto-generated method stub
		return arg == null || arg.isEmpty();
	}
	
}
