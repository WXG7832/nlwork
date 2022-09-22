package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 设备向MES发送程序更改履历数据
 *  {"Header":{"SessionID":"GUID","FunctionID":"A023","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019/06/10 19:38:46"},"RequestInfo":{"ResourceRecordInfo":[{"ResourceID":"EQ00000001","ProgramName":"上位机","ProgramVension":"Ver 19.06.02.190602"},{"ResourceID":"EQ00000001","ProgramName":"PLC","ProgramVension":"Ver 19.06.02.190602"},{"ResourceID":"EQ00000001","ProgramName":"HMI","ProgramVension":"Ver 19.06.02.190602"}]}}
 * @author guofang_ma
 *
 */
public class A023 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A023() {
	  Header.FunctionID = "A023";
	}
	
	

	@Override
	public String toString() {
		return "A023 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}


	 public static class ResourceRecordInfoItem
	    {
	        public String ResourceID;

	        /// <summary>
	        /// 上位机
	        /// </summary>
	        public String ProgramName;
	        public String ProgramVension;

	    }
	public static class RequestInfo {
		
		public List<ResourceRecordInfoItem> ResourceRecordInfo=new ArrayList<ResourceRecordInfoItem>();

		@Override
		public String toString() {
			return "RequestInfo [ResourceRecordInfo=" + ResourceRecordInfo + "]";
		}
		
		
	}
}
