package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 设备向MES发送单个Tab号和电芯号
 *   {"Header":{"SessionID":"GUID","FunctionID":"A029","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"Tab":"T00000001","Cell":"C00000001"}}
 * @author guofang_ma
 *
 */
public class A029 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A029() {
	  Header.FunctionID = "A029";
	}
	
	

	@Override
	public String toString() {
		return "A029 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		  public String Tab;
	        public String Cell ;
		@Override
		public String toString() {
			return "RequestInfo [Tab=" + Tab + ", Cell=" + Cell + "]";
		}
		
		
	}
}
