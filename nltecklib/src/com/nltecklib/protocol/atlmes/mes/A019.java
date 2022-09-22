package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.EQStateCode;
/**
 * 设备向MES发送设备状态
 *  {"Header":{"SessionID":"GUID","PCName":"PCName","FunctionID":"A019","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"ParentEQStateCode":"Run/Stop","AndonState":"","ChildEQ":[{"ChildEQCode":"","ChildEQState":""},{"ChildEQCode":"","ChildEQState":""}],"Quantity":"1000"}}
 * @author guofang_ma
 *
 */
public class A019 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A019() {
	  Header.FunctionID = "A019";
	}
	
	

	@Override
	public String toString() {
		return "A019 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}
    public static class ChildEQItem
    {
        /// <summary>
        /// 
        /// </summary>
        public String ChildEQCode;

        /// <summary>
        /// 
        /// </summary>
        public String ChildEQState;

    }


	public static class RequestInfo {
	       /// <summary>
        /// 
        /// </summary>
        public EQStateCode ParentEQStateCode;

        /// <summary>
        /// 
        /// </summary>
        public String AndonState;

        /// <summary>
        /// 
        /// </summary>
        public List<ChildEQItem> ChildEQ=new ArrayList<ChildEQItem>();

        /// <summary>
        /// A019指令中上传的Quantity的值为设备自投产以来生产的产品总数量--（设备中无法被人工操作清零）
        /// </summary>
        public int Quantity;
        
		@Override
		public String toString() {
			return "RequestInfo [ParentEQStateCode=" + ParentEQStateCode + ", AndonState=" + AndonState + ", ChildEQ="
					+ ChildEQ + ", Quantity=" + Quantity + "]";
		}
		
		
	}
}
