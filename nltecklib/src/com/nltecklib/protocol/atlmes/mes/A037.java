package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;

/**
 * A037 ²éÑ¯µçÐ¾¼ÇÂ¼
 * {"Header":{"SessionID":"GUID","FunctionID":"A037","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24
 * 15:28:34
 * 488"},"RequestInfo":{"OperationCode":"","ReworkFlag":"True/False","SerialNoInfo":[{"SerialNo":"XXXXXXXX"},{"SerialNo":"XXXXXXXX"},{"SerialNo":"XXXXXXXX"}]}}
 * 
 * @author Administrator
 *
 */
public class A037 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A037() {
		Header.FunctionID = "A037";
	}

	@Override
	public String toString() {
		return "A037 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	public static class SerialNo {
		public String SerialNo;

		public SerialNo(String serialNo) {
			this.SerialNo = serialNo;
		}
	}

	public static class RequestInfo {
		public String OperationCode;
		public boolean ReworkFlag;
		public List<SerialNo> SerialNoInfo = new ArrayList<>();

		@Override
		public String toString() {
			return "RequestInfo [OperationCode=" + OperationCode + ", ReworkFlag=" + ReworkFlag + ", SerialNoInfo="
					+ SerialNoInfo + "]";
		}

	}
}
