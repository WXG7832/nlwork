package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * 设备向MES发送设备关键件寿命数据
 * {"Header":{"SessionID":"GUID","FunctionID":"A021","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019/06/11 20:16:02"},"RequestInfo":{"SpartLifeTimeInfo":[{"SpartID":"001","SpartName":"同步带","UseLifetime":"568"},{"SpartID":"002","SpartName":"电动隔膜泵密封","UseLifetime":"854"},{"SpartID":"003","SpartName":"牵引皮带","UseLifetime":"124"},{"SpartID":"004","SpartName":"皮带","UseLifetime":"674"}]}}
 * @author guofang_ma
 *
 */
public class A021 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A021() {
	  Header.FunctionID = "A021";
	}
	
	@Override
	public String toString() {
		return "A021 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	 public static class SpartLifeTimeInfoItem
	    {
	        public String SpartID ;

	        /// <summary>
	        /// 同步带
	        /// </summary>
	        public String SpartName ;
	        public int UseLifetime ;

	    }

	public static class RequestInfo {
		 public List<SpartLifeTimeInfoItem> SpartLifeTimeInfo =new ArrayList<SpartLifeTimeInfoItem>();
		@Override
		public String toString() {
			return "RequestInfo [SpartLifeTimeInfo=" + SpartLifeTimeInfo + "]";
		}
		
		
	}
}
