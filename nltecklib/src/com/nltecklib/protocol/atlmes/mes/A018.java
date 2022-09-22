package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 * 设备收到MES需要数据响应
 * {"Header":{"SessionID":"GUID","FunctionID":"A018","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","IsSuccess":"True","ErrorCode":"00","ErrorMsg":"Null","RequestTime":"2019-05-24 15:28:34 509","ResponseTime":"2019-05-24 15:28:34 509"},"ResponseInfo":{"ParamInfo":[{"ParamID":"001","ParamDesc":"压力","ParamValue":"123"},{"ParamID":"002","ParamDesc":"转速","ParamValue":"123"},{"ParamID":"003","ParamDesc":"温度","ParamValue":"123"}]}}
 * @author guofang_ma
 *
 */
public class A018 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A018() {
		Header.FunctionID="A018";
	}
	
	
	@Override
	public String toString() {
		return "A018 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

    public static class ParamInfoItem
    {
        public int ParamID;

        /// <summary>
        /// 压力
        /// </summary>
        public String ParamDesc;
        public double ParamValue;

    }

	public static class ResponseInfo {
		public List<ParamInfoItem> ParamInfo=new ArrayList<ParamInfoItem>();
		@Override
		public String toString() {
			return "ResponseInfo [ParamInfo=" + ParamInfo + "]";
		}
		
		
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return "OK";
	}
}
