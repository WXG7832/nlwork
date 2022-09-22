package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 *接收设备生成的数据
 * {"Header":{"SessionID":"GUID","FunctionID":"A014","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","IsSuccess":"True","ErrorCode":"0","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"Type":"Normal","Products":[{"ProductSN":"01","Pass":"OK/NG","Param":[{"ParamID":"001","ParamDesc":"压力","Installation":"125.78","KValue":"125.78","Result":"OK"},{"ParamID":"002","ParamDesc":"温度","Installation":"125.78","KValue":"125.78","Result ":"UN/OK/NG"}]},{"ProductSN":"02","Pass":"OK/NG","Param":[{"ParamID":"001","ParamDesc":"压力","Installation":"125.78","KValue":"125.78","Result ":"UN/OK/NG"},{"ParamID":"002","ParamDesc":"温度","Installation":"125.78","KValue":"125.78","Result ":"UN/OK/NG"}]}]}}
 * @author guofang_ma
 *
 */
public class A014 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A014() {
		Header.FunctionID="A014";
	}
	
	
	@Override
	public String toString() {
		return "A014 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	public static class ParamItem
    {
        public String ParamID ;
        /// <summary>
        /// 压力
        /// </summary>
        public String ParamDesc ;
        public String Installation ;
        public String KValue ;
        public String Result ;
		@Override
		public String toString() {
			return "ParamItem [ParamID=" + ParamID + ", ParamDesc=" + ParamDesc + ", Installation=" + Installation
					+ ", KValue=" + KValue + ", Result=" + Result + "]";
		}

    }



    public static class ProductsItem
    {
        public String ProductSN ;
        public String Pass ;
        public List<ParamItem> Param=new ArrayList<ParamItem>();
		@Override
		public String toString() {
			return "ProductsItem [ProductSN=" + ProductSN + ", Pass=" + Pass + ", Param=" + Param + "]";
		}

    }


	public static class ResponseInfo {
		
		 public String Type ;

	        /// <summary>
	        /// 
	        /// </summary>
	        public List<ProductsItem> Products =new ArrayList<ProductsItem>();
	        
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
