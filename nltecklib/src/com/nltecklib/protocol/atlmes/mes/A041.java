package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
/// 设备向MES提供固定共享文件夹地址传送文件，传送完成向MES发送文件已生成请求
/// {"Header":{"SessionID":"GUID","FunctionID":"A041","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"FileTransmis":"OK","FolderPath":"E:\ATL-MES2.0\200_设备改造\20_改造方案\通讯协议规约"}}
 * @author guofang_ma
 *
 */
public class A041 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A041() {
	  Header.FunctionID = "A041";
	}
	
	

	@Override
	public String toString() {
		return "A041 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		
		public String FileTransmis;

        /// <summary>
		///E:ATL-MES2.0_设备改造,改造方案通讯协议规约
        /// </summary>
        public String FolderPath;
		@Override
		public String toString() {
			return "RequestInfo [FileTransmis=" + FileTransmis + ", FolderPath=" + FolderPath + "]";
		}
		
		
	}
}
