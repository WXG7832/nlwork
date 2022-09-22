package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 /// ACT上传整炉电芯号
/// {"Header":{"SessionID":"GUID","FunctionID":"A051","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"Cell":["C00000001","C00000002","C00000003"]}}
* @author guofang_ma
 *
 */
public class A081 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A081() {
	  Header.FunctionID = "A081";
	}
	
	

	@Override
	public String toString() {
		return "A081 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		
		public String machine;
		public boolean is_export;
		@Override
		public String toString() {
			return "RequestInfo [machine=" + machine + ", is_export=" + is_export + "]";
		}
	}
}
