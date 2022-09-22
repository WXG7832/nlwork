package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 /// MES收到文件生成请求，去对应文件夹下解析文件数据解析完成数据再写回文件响应位置，在将文件返回给设备上位机对应目录下
/// {"Header":{"SessionID":"GUID","FunctionID":"A042","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Result":"OK"}}
* @author guofang_ma
 *
 */
public class A042 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A042() {
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
