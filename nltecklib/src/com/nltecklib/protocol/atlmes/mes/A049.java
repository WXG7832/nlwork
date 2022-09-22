package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 /// 设备在MES宕机后，进行清尾生产时产生的电芯码及物料号进行绑定
/// {"Header":{"SessionID":"Guid","FunctionID":"A049","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"ModelInfo":"123","ProductSN":["12345","12345"],"UserInfo":{"UserID":"123","UserName":"ATL","UserLevel":"1"},"MaterialInfo":[{"MaterialID":"P001","MaterialName":"绿胶","LabelNumber":"001","MaterialQuantity":"100","UoM":"EA"}]}}
* @author guofang_ma
 *
 */
public class A049 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A049() {
	  Header.FunctionID = "A049";
	}
	

	@Override
	public String toString() {
		return "A049 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

//	public static class UserInfo
//    {
//        public String UserID ;
//        public String UserName ;
//        public String UserLevel ;
//    }

    public static class MaterialInfoItem
    {
        public String MaterialID ;
        /// <summary>
        /// 绿胶
        /// </summary>
        public String MaterialName ;
        public String LabelNumber ;
        public String MaterialQuantity ;
        public String UoM ;

    }

	public static class RequestInfo {

        /// <summary>
        /// 
        /// </summary>
        public String ModelInfo ;

        /// <summary>
        /// 
        /// </summary>
        public List<String> ProductSN=new ArrayList<String>() ;

        /// <summary>
        /// 
        /// </summary>
        public UserInfo UserInfo ;

        /// <summary>
        /// 
        /// </summary>
        public List<MaterialInfoItem> MaterialInfo =new ArrayList<MaterialInfoItem>();
		@Override
		public String toString() {
			return "RequestInfo [ModelInfo=" + ModelInfo + ", ProductSN=" + ProductSN + ", UserInfo=" + UserInfo
					+ ", MaterialInfo=" + MaterialInfo + "]";
		}
		
		
	}
}
