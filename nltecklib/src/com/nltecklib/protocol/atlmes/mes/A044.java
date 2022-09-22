package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 /// MES收到图片生成请求，去对应文件夹下解析图片数据解析完成数据，向设备返回对应电芯结果
/// {"Header":{"SessionID":"GUID","FunctionID":"A044","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"PicRec":"OK"}}
* @author guofang_ma
 *
 */
public class A044 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A044() {
		Header.FunctionID="A044";
	}
	
	
	@Override
	public String toString() {
		return "A044 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
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
