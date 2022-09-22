package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
  * 涂布、冷压、分条Output数据
 *  {"Header":{"SessionID":"Guid","FunctionID":"A035","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019/06/11 20:41:34"},"RequestInfo":{"Model":"品种","EquType":"","ProductSN":"膜卷号","DataList":[{"Data ":[{"ParamID":"","Value":"","ParamDesc":"极片数量"},{"ParamID":"","Value":"","ParamDesc":"下膜长"},{"ParamID":"","Value":"","ParamDesc":"下留白"},{"ParamID":"","Value":"","ParamDesc":"头部错位"},{"ParamID":"","Value":"","ParamDesc":"尾部错位"},{"ParamID":"","Value":"","ParamDesc":"下膜补偿值"},{"ParamID":"","Value":"","ParamDesc":"下留白补偿值"},{"ParamID":"","Value":"","ParamDesc":"上膜长补偿值"},{"ParamID":"","Value":"","ParamDesc":"上留白补偿值"},{"ParamID":"","Value":"","ParamDesc":"头部错位补偿值"},{"ParamID":"","Value":"","ParamDesc":"尾部错位补偿值"},{"ParamID":"","Value":"","ParamDesc":"光纤点距离"},{"ParamID":"","Value":"","ParamDesc":"备注"},{"ParamID":"","Value":"","ParamDesc":"过辊直径"},{"ParamID":"","Value":"","ParamDesc":"上下膜长规格是否对调"},{"ParamID":"","Value":"","ParamDesc":"头尾错位规格是否对调"},{"ParamID":"","Value":"","ParamDesc":"是否闭环"}]}]}}
  * @author guofang_ma
 *
 */
public class A035 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A035() {
	  Header.FunctionID = "A035";
	}
	
	

	@Override
	public String toString() {
		return "A035 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	public static class DataItem
    {
        /// <summary>
        /// 
        /// </summary>
        public String ParamID ;
        /// <summary>
        /// 
        /// </summary>
        public String Value ;
        /// <summary>
        /// 极片数量
        /// </summary>
        public String ParamDesc ;
    }

    public static class DataListItem
    {
        /// <summary>
        /// 
        /// </summary>
        public List<DataItem> Data ;
    }

	public static class RequestInfo {
		/// <summary>
        /// 品种
        /// </summary>
        public String Model ;
        /// <summary>
        /// 
        /// </summary>
        public String EquType ;
        /// <summary>
        /// 膜卷号
        /// </summary>
        public String ProductSN ;
        /// <summary>
        /// 
        /// </summary>
        public List<DataListItem> DataList=new ArrayList<DataListItem>(); 
		@Override
		public String toString() {
			return "RequestInfo []";
		}
		
		
	}
}
