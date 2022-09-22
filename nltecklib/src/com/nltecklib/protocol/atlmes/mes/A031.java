package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 设备向MES发送弹夹号及对应各个电芯号
 * {"Header":{"SessionID":"GUID","FunctionID":"A031","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"Carrier":"W12345","Cell":["C00000001","C00000002","C00000003"]}}
 * @author guofang_ma
 *
 */
public class A031 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A031() {
	  Header.FunctionID = "A031";
	}
	
	

	@Override
	public String toString() {
		return "A031 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		public String Carrier ;

        /// <summary>
        /// 
        /// </summary>
        public List<String> Cell =new ArrayList<String>();
		@Override
		public String toString() {
			return "RequestInfo [Carrier=" + Carrier + ", Cell=" + Cell + "]";
		}
		
		
	}
}
