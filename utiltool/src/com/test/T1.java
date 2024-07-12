package com.test;

import org.junit.jupiter.api.Test;

import com.base.CommonUtil;

public class T1 {
	@Test
	public void test() {
	String str =  "1.2";
	boolean b = CommonUtil.checkDigit(str, false);
	System.out.println(b);
	
	// 
	long last = System.currentTimeMillis();
	
	CommonUtil.sleep(6 * 1000);
	
	System.out.println("Ê±¼ä²î"+(System.currentTimeMillis() - last));
	
	}
}
