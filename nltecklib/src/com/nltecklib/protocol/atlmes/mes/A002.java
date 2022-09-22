package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 * MES根据设备ID查询该设备是否注册，成功结果返回OK，否则结果返回NG
 * {"Header":{"SessionID":"GUID","FunctionID":"A002","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34509"},"ResponseInfo":{"Result": "OK"}}
 * @author guofang_ma
 *
 */
public class A002 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A002() {
		Header.FunctionID="A002";
	}
	
	
	@Override
	public String toString() {
		return "A002 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}


	public static class ResponseInfo {
		public String Result = "";

		@Override
		public String toString() {
			return "ResponseInfo [Result=" + Result + "]";
		}
		
		
	}


	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return ResponseInfo.Result;
	}
}
