package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 定时向服务端发送心跳包请求（Dll文件里已经封装连接错误方法，可参考利用）
 *  {"Header":{"SessionID":"Guid","FunctionID":"A005","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{}}
 * @author guofang_ma
 *
 */
public class A005 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A005() {
	  Header.FunctionID = "A005";
	}
	
	

	@Override
	public String toString() {
		return "A005 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {

		@Override
		public String toString() {
			return "RequestInfo []";
		}
		
		
	}
}
