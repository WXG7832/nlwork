package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * MES发送采集设备运行参数指令，用于点检
 *  {"Header":{"SessionID":"GUID","FunctionID":"A017","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"ParamInfo":[{"ParamID":"001","ParamDesc":"压力"},{"ParamID":"002","ParamDesc":"转速"},{"ParamID":"003","ParamDesc":"温度"}]}}
 * @author guofang_ma
 *
 */
public class A017 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A017() {
	  Header.FunctionID = "A017";
	}
	
	

	@Override
	public String toString() {
		return "A017 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

    public static class ParamInfoItem
    {
        public int ParamID;
        /// <summary>
        /// 压力
        /// </summary>
        public String ParamDesc;

    }

	public static class RequestInfo {
		
		 public List<ParamInfoItem> ParamInfo=new ArrayList<ParamInfoItem>();
		@Override
		public String toString() {
			return "RequestInfo [ParamInfo=" + ParamInfo + "]";
		}
		
		
	}
}
