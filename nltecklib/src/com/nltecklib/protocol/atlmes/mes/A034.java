package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.ResponseRoot;

/**
 * MES收到ACT的output数据,反馈电芯结果和不良代码
 * {"Header":{"SessionID":"GUID","FunctionID":"A034","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24
 * 15:28:34 509","ResponseTime":"2019-05-24 15:28:34
 * 509"},"ResponseInfo":{"Type":"Normal","Products":[{"ProductSN":"01","Pass":"OK/NG","ResultCode":"","Param":{"STATUS":"OK/NG","CYCLICTIME":"OK/NG","STEP":"OK/NG","CURRENT":"OK/NG","VOLTAGE":"OK/NG","CAPACITY":"OK/NG","ENERGY":"OK/NG","STIME":"OK/NG","DTIME":"OK/NG","TEMP":"OK/NG","PRESSURE":"OK/NG"}},{"ProductSN":"02","Pass":"OK/NG","ResultCode":"","Param":{"STATUS":"OK/NG","CYCLICTIME":"OK/NG","STEP":"OK/NG","CURRENT":"OK/NG","VOLTAGE":"OK/NG","CAPACITY":"OK/NG","ENERGY":"OK/NG","STIME":"OK/NG","DTIME":"OK/NG","TEMP":"OK/NG","PRESSURE":"OK/NG"}}]}}
 * 
 * @author guofang_ma
 *
 */
public class A034 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();

	public A034() {
		Header.FunctionID = "A034";
	}

	@Override
	public String toString() {
		return "A034 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	public static class ParamItem {
		
		public String ParamID;
		public String ParamDesc;
		public String Installation;
		public String KValue;
		public String Result;
		
		
		
//		public String STATUS;
//		public String CYCLICTIME;
//		public String STEP;
//		public String CURRENT;
//		public String VOLTAGE;
//		public String CAPACITY;
//		public String ENERGY;
//		public String STIME;
//		public String DTIME;
//		public String TEMP;
//		public String PRESSURE;
//		@Override
//		public String toString() {
//			return "Param [STATUS=" + STATUS + ", CYCLICTIME=" + CYCLICTIME + ", STEP=" + STEP + ", CURRENT=" + CURRENT
//					+ ", VOLTAGE=" + VOLTAGE + ", CAPACITY=" + CAPACITY + ", ENERGY=" + ENERGY + ", STIME=" + STIME
//					+ ", DTIME=" + DTIME + ", TEMP=" + TEMP + ", PRESSURE=" + PRESSURE + "]";
//		}
		
	}

	public static class ProductsItem {
		public String ProductSN;
		public String Pass;
		public String ResultCode;
		public List<ParamItem> Param;
		@Override
		public String toString() {
			return "ProductsItem [ProductSN=" + ProductSN + ", Pass=" + Pass + ", ResultCode=" + ResultCode + ", Param="
					+ Param + "]";
		}
	
		
	}

	public static class ResponseInfo {

		public String Type;
		public List<ProductsItem> Products = new ArrayList<>();

		@Override
		public String toString() {
			return "ResponseInfo [Type=" + Type + ", Products=" + Products + "]";
		}

	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return "OK";
	}
}
