package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;

/**
 * /// 设备运行标准参数人工更改后需要向MES递交参数 ///
 * {"Header":{"SessionID":"Guid","FunctionID":"A013","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019/06/11
 * 20:41:34"},"RequestInfo":{"UserInfo":{"UserID":"123","UserName":"ATL","UserLevel":"1"},"EquParam":[{"ParamID":"001","ParamDesc":"压力","OldParamValue":"125.78","NewParamValue":"100"},{"ParamID":"002","ParamDesc":"温度","OldParamValue":"20","NewParamValue":"32"}]}}
 * 
 * @author guofang_ma
 *
 */
public class A045 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A045() {
		Header.FunctionID = "A045";
	}

	@Override
	public String toString() {
		return "A045" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	public static class EquParamItem {
		public int ParamID;
		/// <summary>
		/// 压力
		/// </summary>
		public String ParamDesc;
		public double OldParamValue;
		public double NewParamValue;

	}

//	public static class UserInfo {
//		public String UserID;
//		public String UserLevel;
//		public String UserName;
//	}

	public static class RequestInfo {
		public UserInfo UserInfo;
		public List<EquParamItem> EquParam=new ArrayList<EquParamItem>();

		@Override
		public String toString() {
			return "RequestInfo [UserInfo=" + UserInfo + ", EquParam=" + EquParam + "]";
		}

	}
}
