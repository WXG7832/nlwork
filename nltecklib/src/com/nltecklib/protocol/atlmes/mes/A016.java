package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 * MES接收上料相关数据做验证、返回验证结果
 * {"Header":{"SessionID":"GUID","FunctionID":"A016","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True ","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"Ok"}}
 * @author guofang_ma
 *
 */
public class A016 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A016() {
		Header.FunctionID="A016";
	}
	
	
	@Override
	public String toString() {
		return "A016 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}


	public static class ResponseInfo {
		public String Result ;
		public boolean IsReset;
		@Override
		public String toString() {
			return "ResponseInfo [Result=" + Result + ", IsReset=" + IsReset + "]";
		}
		
		
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return ResponseInfo.Result;
	}
}
