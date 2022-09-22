package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.MesInfo;
import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 设备上位机通过设备ID向MES请求设备代码
 *  {"Header":{"SessionID":"Guid","FunctionID":"A001","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"PCName":"PCName","EQCode":"EQ00000001"}}
 * @author guofang_ma
 *
 */
public class A001 extends RequestRoot{

	public RequestInfo RequestInfo = new RequestInfo();
	
	public A001() {
		Header.FunctionID="A001";
	}
	
	
	
	@Override
	public String toString() {
		return "A001 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}



	public  static class RequestInfo {
		public String PCName=MesInfo.PCName;
		public String EQCode=MesInfo.EQCode;
		@Override
		public String toString() {
			return "RequestInfo [PCName=" + PCName + ", EQCode=" + EQCode + "]";
		}
	}
}
