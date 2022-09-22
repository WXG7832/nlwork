package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 /// MES向ACT发送路径
/// {"Header":{"SessionID":"GUID","FunctionID":"A053","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"FolderPath":"E:\\ATL-MES2.0\\200_设备改造\\20_改造方案\\通讯协议规约"}}
/* @author guofang_ma
 *
 */
public class A053 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A053() {
	  Header.FunctionID = "A053";
	}
	
	

	@Override
	public String toString() {
		return "A053 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
        /// <summary>
        /// E:\ATL-MES2.0\200_设备改造\20_改造方案\通讯协议规约
        /// </summary>
        public String FolderPath;
		@Override
		public String toString() {
			return "RequestInfo [FolderPath=" + FolderPath + "]";
		}
		
		
	}
}
