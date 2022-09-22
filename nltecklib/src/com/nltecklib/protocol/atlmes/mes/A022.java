package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 *  MES返回已收到设备关键件寿命数据响应
 * {"Header":{"SessionID":"A022","FunctionID":"A022","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":true,"ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
 * @author guofang_ma
 *
 */
public class A022 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A022() {
		Header.FunctionID="A022";
	}
	
	
	@Override
	public String toString() {
		return "A022 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
