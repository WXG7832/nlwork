package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.ControlCode ;

/**
 * MES发送设备控制指令
 * {"Header":{"SessionID":"Guid","FunctionID":"A011","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"ControlCode":"Run","StateCode":"","StateDesc":""}
 * @author guofang_ma
 *
 */
public class A011 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A011() {
	  Header.FunctionID = "A011";
	}
	
	

	@Override
	public String toString() {
		return "A011 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
        public ControlCode ControlCode ;
        public String StateCode ;
        public String StateDesc ;
		@Override
		public String toString() {
			return "RequestInfo [ControlCode=" + ControlCode + ", StateCode=" + StateCode + ", StateDesc=" + StateDesc
					+ "]";
		}
		
		
	}
}
