package com.nlteck;

public class UIException extends RuntimeException {
    
	private Exception ex;
	
	public UIException(String msg , Exception ex) {
		  super(msg);	
	}
	
	public Exception getCause() {
		
		return ex;
	}
	  
}
