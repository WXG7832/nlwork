package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 * MES将Tab和电芯号绑定，并返回绑定结果
 * {"Header":{"SessionID":"GUID","FunctionID":"A030","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
 * @author guofang_ma
 *
 */
public class A032 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A032() {
		Header.FunctionID="A030";
	}
	
	
	@Override
	public String toString() {
		return "A030 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
