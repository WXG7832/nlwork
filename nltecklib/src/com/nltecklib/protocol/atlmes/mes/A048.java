package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 /// 设备接收到物料信息进行响应
/// {"Header":{"SessionID":"Guid","FunctionID":"A049","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"ModelInfo":"123","ProductSN":["12345","12345"],"UserInfo":{"UserID":"123","UserName":"ATL","UserLevel":"1"},"MaterialInfo":[{"MaterialID":"P001","MaterialName":"绿胶","LabelNumber":"001","MaterialQuantity":"100","UoM":"EA"}]}}
* @author guofang_ma
 *
 */
public class A048 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A048() {
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
