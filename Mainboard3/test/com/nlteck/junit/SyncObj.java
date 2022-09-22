package com.nlteck.junit;

import com.nlteck.util.CommonUtil;

public class SyncObj {
    
	
	  public synchronized String  getString1() {
		  
		  System.out.println("getString1() start");
		  CommonUtil.sleep(400);
		  System.out.println("getString1() end");
		  return "string1";
	  }
	  
	  
	  public synchronized String getString2() {
		  
		  System.out.println("getString2() start");
		  CommonUtil.sleep(200);
		  System.out.println("getString2() end");
		  return "string2";
	  }
}
