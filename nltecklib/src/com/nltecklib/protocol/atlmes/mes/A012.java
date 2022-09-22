package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 *设备接收MES控制指令响应
 * {"Header":{"SessionID":"Guid","FunctionID":"A012","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 488","ResponseTime":"2019/06/10 15:26:59"},"ResponseInfo":{"Result":"OK"}}
 * @author guofang_ma
 *
 */
public class A012 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A012() {
		Header.FunctionID="A012";
	}
	
	
	@Override
	public String toString() {
		return "A012 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
