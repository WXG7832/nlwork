package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;

/**
 * /// MES收到返回用户信息结果 ///
 * {"Header":{"SessionID":"GUID","FunctionID":"A040","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":true,"ErrorCode":"00/01/02","ErrorMsg":"XXXX","RequestTime":"2019-05-24
 * 15:28:34 509","ResponseTime":"2019-05-24 15:28:34
 * 509"},"ResponseInfo":{"UserID":"000777665","UserName":"xxxx","UserLevel":"Administrator"}}
 * 
 * @author guofang_ma
 *
 */
public class A040 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();

	public A040() {
		Header.FunctionID = "A040";
	}

	@Override
	public String toString() {
		return "A040 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	public static class ResponseInfo {

		public String UserID;
		public String UserName;
		public int UserLevel;

		@Override
		public String toString() {
			return "ResponseInfo [UserID=" + UserID + ", UserName=" + UserName + ", UserLevel=" + UserLevel + "]";
		}

	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return "OK";
	}
}
