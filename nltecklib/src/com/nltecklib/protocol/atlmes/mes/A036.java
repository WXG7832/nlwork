package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 *M涂布、冷压、分条Output数据
 * {"Header":{"SessionID":"GUID","FunctionID":"A038","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"ProductInfo":[{"ProductName":"xxxx","StandardValue":"xxxx","UpperLimitValue":"XXXX","LowerLimitValue":"100"}]}}
 * @author guofang_ma
 *
 */
public class A036 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A036() {
		Header.FunctionID="A036";
	}
	
	
	@Override
	public String toString() {
		return "A036 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
