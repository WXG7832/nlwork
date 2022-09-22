package com.nlteck;

import com.nlteck.so.CLibrary;

public class TestPrint {
    
	
	    public static void main(String[] args) {

	        CLibrary.INSTANCE.printf("Hello, JNA\n");
	        CLibrary.INSTANCE.printf("args lenth = %d\n",args.length);
	        for (int i=0;i < args.length;i++) {

	            CLibrary.INSTANCE.printf("Argument %d: %s/n", i, args[i]);

	        }

	    }

	
	
}
