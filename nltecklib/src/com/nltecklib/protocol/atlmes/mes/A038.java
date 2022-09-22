package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.ResponseRoot;

/**
 * A038 返回电芯记录结果
 * {"Header":{"SessionID":"GUID","FunctionID":"A038","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24
 * 15:28:34 509","ResponseTime":"2019-05-24 15:28:34
 * 509"},"ResponseInfo":{"ProductInfo":[{"SerialNo":"XXXXXXXX","Result":"OK/NG/NotFound","ResultCode":""}]}}
 * 
 * @author guofang_ma
 *
 */
public class A038 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();

	public A038() {
		Header.FunctionID = "A038";
	}

	@Override
	public String toString() {
		return "A038 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	public enum BatteryResult {
		OK, NG, NotFound
	}

	public static class ProductInfo {
		public String SerialNo;
		public BatteryResult Result;
		public int ResultCode;

		@Override
		public String toString() {
			return "ProductInfo [SerialNo=" + SerialNo + ", Result=" + Result + ", ResultCode=" + ResultCode + "]";
		}

	}

	public static class ResponseInfo {
		public List<ProductInfo> ProductInfo = new ArrayList<>();

		@Override
		public String toString() {
			return "ResponseInfo [ProductInfo=" + ProductInfo + "]";
		}

	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return "OK";
	}
}
