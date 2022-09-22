package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 设备向MES发送设备报警数据
 *   {"Header": {"SessionID": "GUID","FunctionID": "A025","PCName":"PCName","EQCode": "EQ00000001","SoftName": "ClientSoft1","RequestTime": "2019/06/11 19:55:00"},"RequestInfo": {"ResourceAlertInfo": [{"AlertCode": "001","AlertName": "Red","AlertLevel": "A"},{"AlertCode": "002","AlertName": "yellow","AlertLevel": "B"}]}}
 * @author guofang_ma
 *
 */
public class A025 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A025() {
	  Header.FunctionID = "A025";
	}
	
	

	@Override
	public String toString() {
		return "A025 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	  public static class ResourceAlertInfoItem
	    {
	        public String AlertCode;
	        public String AlertName;
	        public String AlertLevel;

	    }

	public static class RequestInfo {
		  public List<ResourceAlertInfoItem> ResourceAlertInfo=new 	ArrayList<ResourceAlertInfoItem>();
		@Override
		public String toString() {
			return "RequestInfo [ResourceAlertInfo=" + ResourceAlertInfo + "]";
		}
		
		
	}
}
