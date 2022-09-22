package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
/// 真空baking设备与freebaking设备OUTPUT数据专用,因Freebaking与真空baking无法获取电芯码，故使用此结构的OUTPUT数据;数据上传
/// {"Header":{"SessionID":"GUID","FunctionID":"A055","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"ParamInfo":[{"ParamID":"001","ParamDesc":"压力","ParamValue":"123","TestDate":"2019-05-24 15:28:34 488"},{"ParamID":"002","ParamDesc":"转速","ParamValue":"123","TestDate":"2019-05-24 15:28:34 488"},{"ParamID":"003","ParamDesc":"温度","ParamValue":"123","TestDate":"2019-05-24 15:28:34 488"}]}}
 * @author guofang_ma
 *
 */
public class A055 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A055() {
	  Header.FunctionID = "A055";
	}
	

	@Override
	public String toString() {
		return "A055 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	public static class ParamInfoItem
    {
        public String ParamID;
        public String ParamDesc;
        public String ParamValue;
        public String TestDate;
    }


	public static class RequestInfo {
		 public List<ParamInfoItem> ParamInfo=new ArrayList<ParamInfoItem>();
		@Override
		public String toString() {
			return "RequestInfo [ParamInfo=" + ParamInfo + "]";
		}
		
		
	}
}
