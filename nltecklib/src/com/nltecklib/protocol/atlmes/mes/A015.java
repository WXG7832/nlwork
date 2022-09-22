package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.RequestRoot;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.HLFlag;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.ProcessType;
/**
 * 发送上料相关数据
 * {"Header":{"SessionID":"GUID","FunctionID":"A015","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"MaterialID":"xxxx"}}
 * @author guofang_ma
 *
 */
public class A015 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A015() {
	  Header.FunctionID = "A015";
	}
	
	

	@Override
	public String toString() {
		return "A015 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	

	public static class RequestInfo {
		
        public String MaterialID;
        /// <summary>
        /// HighOrLowFlag 高低阻标签 H高阻L低阻
        /// </summary>
        public HLFlag HighOrLowFlag;
        /// <summary>
        /// 0-电芯进料校验，1-电芯出料校验，2-弹夹进设备校验，3弹夹下料位校验，4-弹夹机电芯校验（专用）；无此字段默认为0-电芯进料校验
        /// </summary>
        public  int Type;
        /// <summary>
        /// 工序标识:IC/AC
        /// </summary>
        public ProcessType Operation;
		@Override
		public String toString() {
			return "RequestInfo [MaterialID=" + MaterialID + ", HighOrLowFlag=" + HighOrLowFlag + ", Type=" + Type
					+ ", Operation=" + Operation + "]";
		}
		
		
		
	}
}
