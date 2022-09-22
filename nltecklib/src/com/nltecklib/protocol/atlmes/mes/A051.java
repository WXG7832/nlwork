package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 /// ACT上传整炉电芯号
/// {"Header":{"SessionID":"GUID","FunctionID":"A051","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"Cell":["C00000001","C00000002","C00000003"]}}
* @author guofang_ma
 *
 */
public class A051 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A051() {
	  Header.FunctionID = "A051";
	}
	
	

	@Override
	public String toString() {
		return "A051 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		public List<String> Cell=new ArrayList<String>();
		@Override
		public String toString() {
			return "RequestInfo [Cell=" + Cell + "]";
		}
		
		
	}
}
