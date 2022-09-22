package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;

/**
 * 响应客户端心跳包请求
 * {"Header":{"SessionID":"Guid","FunctionID":"A006","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24
 * 15:28:34 509","ResponseTime":"2019-05-24 15:28:34
 * 509"},"ResponseInfo":{"Result": "OK"}}
 * 
 * @author guofang_ma
 *
 */
public class A006 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();

	public A006() {
		Header.FunctionID = "A006";
	}

	@Override
	public String toString() {
		return "A006 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	public static class ResponseInfo {
		public String Result = "";
		//public String IP = "";//本地自己添加的，用于主备用服务器识别

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
