package com.nltecklib.protocol.atlmes;

import java.util.Date;
import java.util.UUID;

public abstract class ResponseRoot extends Root {

	public	ResponseHeader Header=new ResponseHeader();
	
	
	public String getFuncID() {
		return Header.FunctionID;
	}
	
	public String getSessionID() {
		return Header.SessionID;
	}
	
	public static class ResponseHeader {
		
	    public String SessionID;
	    public String FunctionID ;
	    public String PCName ;
	    public String EQCode ;
	    public String SoftName ;
	    public String IsSuccess ;
	    public String ErrorCode;
	    public String ErrorMsg ;
	    public String RequestTime ;
	    public String ResponseTime ;
	    
	    public ResponseHeader() {
			// TODO Auto-generated constructor stub
	    	SessionID =UUID.randomUUID().toString();
	    	PCName=MesInfo.PCName;
	    	EQCode=MesInfo.EQCode;
	    	SoftName=MesInfo.SoftName;
	    	IsSuccess="True";
	    	ResponseTime=Root.sdf.format(new Date());
		}
	    
		@Override
		public String toString() {
			return "ResponseHeader [SessionID=" + SessionID + ", FunctionID=" + FunctionID + ", PCName=" + PCName
					+ ", EQCode=" + EQCode + ", SoftName=" + SoftName + ", IsSuccess=" + IsSuccess + ", ErrorCode="
					+ ErrorCode + ", ErrorMsg=" + ErrorMsg + ", RequestTime=" + RequestTime + ", ResponseTime="
					+ ResponseTime + "]";
		}
	    
	    
	}


	
	

}
