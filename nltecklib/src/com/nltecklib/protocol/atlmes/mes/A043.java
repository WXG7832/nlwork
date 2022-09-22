package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 /// 设备向MES提供固定共享文件夹地址传送图片，传送完成向MES发送图片已生成请求
/// {"Header":{"SessionID":"GUID","FunctionID":"A043","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"PicTransmis":"OK"，"FolderPath":"E:\ATL-MES2.0\200_设备改造\20_改造方案\通讯协议规约"}}
 @author guofang_ma
 *
 */
public class A043 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A043() {
	  Header.FunctionID = "A043";
	}
	
	

	@Override
	public String toString() {
		return "A043 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public static class RequestInfo {
		  public String PicTransmis ;

	        /// <summary>
	        /// E:ATL-MES2.0_设备改造_改造方案通讯协议规约
	        /// </summary>
	        public String FolderPath ;
		@Override
		public String toString() {
			return "RequestInfo [PicTransmis=" + PicTransmis + ", FolderPath=" + FolderPath + "]";
		}
		
		
	}
}
