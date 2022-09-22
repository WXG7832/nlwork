package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 *  MES返回已收到设备履历数据响应
 * {"Header":{"SessionID":"GUID","FunctionID":"A024","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
 * @author guofang_ma
 *
 */
public class A024 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A024() {
		Header.FunctionID="A024";
	}
	
	
	@Override
	public String toString() {
		return "A024 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
