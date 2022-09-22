package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;

/**
 * 上物料或更换物料时将物料信息发送至设备
 * {"Header":{"SessionID":"Guid","FunctionID":"A047","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24
 * 15:28:34
 * 488"},"RequestInfo":{"ModelInfo":"123","UserInfo":{"UserID":"123","UserName":"ATL","UserLevel":"1"},"MaterialInfo":[{"MaterialID":"P001","MaterialName":"绿胶","LabelNumber":"001","MaterialQuality":"100","UoM":"EA"}]}}
 * 
 * @author guofang_ma
 *
 */
public class A047 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A047() {
		Header.FunctionID = "A047";
	}

	@Override
	public String toString() {
		return "A047 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

//	public static class UserInfo {
//		public String UserID;
//		public String UserName;
//		public String UserLevel;
//	}

	public static class MaterialInfoItem // implements Serializable
	{
		/**
		 * 
		 */
		// private static final long serialVersionUID = 1L;
		public String MaterialID;
		public String MaterialName;
		public String LabelNumber;
		public String MaterialQuality;
		public String UoM;
	}

	public static class RequestInfo {
		public String ModelInfo;

		/// <summary>
		///
		/// </summary>
		public UserInfo UserInfo;

		/// <summary>
		///
		/// </summary>
		public List<MaterialInfoItem> MaterialInfo = new ArrayList<MaterialInfoItem>();

		@Override
		public String toString() {
			return "RequestInfo [ModelInfo=" + ModelInfo + ", UserInfo=" + UserInfo + ", MaterialInfo=" + MaterialInfo
					+ "]";
		}

	}
}
