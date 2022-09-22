package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 * 设备返回接收到初始化开机指令
 * {"Header":{"SessionID":"Guid","FunctionID":"A008","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
 * @author guofang_ma
 *
 */
public class A008 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A008() {
		Header.FunctionID="A008";
	}
	
	
	@Override
	public String toString() {
		return "A008 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return ResponseInfo.Result;
	}

	public static class ResponseInfo {
		public String Result = "";

		@Override
		public String toString() {
			return "ResponseInfo [Result=" + Result + "]";
		}
		
		
	}
}
