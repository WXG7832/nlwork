package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 /// ACT反馈
/// {"Header":{"SessionID":"GUID","FunctionID":"A054","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
/// Result--反馈结果，OK正确找到路径，NG为没有找到路径，当Result为NG时FolderPath为空
* @author guofang_ma
 *
 */
public class A054 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A054() {
		Header.FunctionID="A006";
	}
	
	
	@Override
	public String toString() {
		return "A006 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
