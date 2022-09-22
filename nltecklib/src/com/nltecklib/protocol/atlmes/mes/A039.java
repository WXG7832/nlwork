package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
/// 设备向MES发送用户信息请求
/// {"Header":{"SessionID":"GUID","FunctionID":"A039","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"UserID":"XXXXXXXX","UserPassword":"1234"}}
* @author guofang_ma
 *
 */
public class A039 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A039() {
	  Header.FunctionID = "A039";
	}
	
	

	@Override
	public String toString() {
		return "A039 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		
		public String UserID ;

        /// <summary>
        /// 
        /// </summary>
        public String UserPassword;
		@Override
		public String toString() {
			return "RequestInfo [UserID=" + UserID + ", UserPassword=" + UserPassword + "]";
		}
		
		
	}
}
