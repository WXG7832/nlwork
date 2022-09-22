package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 发送设备生成的数据，内容主要包括原料、半成品或成品的工艺参数
 * {"Header":{"SessionID":"Guid","FunctionID":"A013","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019/06/11 20:41:34"},"RequestInfo":{"Type":"Normal","Products":[{"ProductSN":"01","OutputParam":[{"ParamID":"001","ParamDesc":"压力","ParamValue":"125.78","Result ":"OK"},{"ParamID":"002","ParamDesc":"温度","ParamValue":"125.78","Result ":"UN/OK/NG"}]},{"ProductSN":"02","OutputParam":[{"ParamID":"001","ParamDesc":"压力","ParamValue":"125.78","Result ":"UN/OK/NG"},{"ParamID":"002","ParamDesc":"温度","ParamValue":"125.78","Result":"UN/OK/NG"}]}]}}
 * @author guofang_ma
 *
 */
public class A013 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A013() {
	  Header.FunctionID = "A013";
	}
	
	

	@Override
	public String toString() {
		return "A013 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	 public static class OutputParamItem
	    {
	        /// <summary>
	        /// 
	        /// </summary>
	        public String ParamID ;

	        /// <summary>
	        /// 压力
	        /// </summary>
	        public String ParamDesc ;

	        /// <summary>
	        /// 
	        /// </summary>
	        public String ParamValue ;

	        /// <summary>
	        /// OK/NG/null
	        /// </summary>
	        public String Result ;

	    }



	    public static class ProductsItem
	    {
	        /// <summary>
	        /// 
	        /// </summary>
	        public String ProductSN ;
	        /// <summary>
	        /// 
	        /// </summary>
	        public String Pass ;
	        /// <summary>
	        /// 
	        /// </summary>
	        public String ChildEquCode ;
	        /// <summary>
	        /// 
	        /// </summary>
	        public String Station ;
	        /// <summary>
	        /// 
	        /// </summary>
	        public List<OutputParamItem> OutputParam=new ArrayList<OutputParamItem>() ;
	    }

	public static class RequestInfo {
		 public String Type ;
	        public List<ProductsItem> Products=new ArrayList<ProductsItem>() ;
		@Override
		public String toString() {
			return "RequestInfo [Type=" + Type + ", Products=" + Products + "]";
		}
		
		
	}
}
