package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 /// 设备运行设定参数更改上传响应
/// {"Header":{"SessionID":"GUID","FunctionID":"A030","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
* @author guofang_ma
 *
 */
public class A046 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A046() {
		Header.FunctionID="A046";
	}
	
	
	@Override
	public String toString() {
		return "A046 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
