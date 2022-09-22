package com.nltecklib.protocol.atlmes.mes;

import com.nltecklib.protocol.atlmes.ResponseRoot;
/**
 /// MES获取离线生产数据响应
/// {"Header":{"SessionID":"GUID","FunctionID":"A052","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"Result":"OK","FolderPath":"E:\ATL-MES2.0\200_设备改造\20_改造方案\通讯协议规约"}}
/// Result--反馈结果，OK正确找到路径，NG为没有找到路径，当Result为NG时FolderPath为空
/ * @author guofang_ma
 *
 */
public class A052 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();
	public A052() {
		Header.FunctionID="A052";
	}
	
	
	@Override
	public String toString() {
		return "A052 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}


	public static class ResponseInfo {
		 /// <summary>
        /// 反馈结果，OK正确找到路径，NG为没有找到路径，当Result为NG时FolderPath为空
        /// </summary>
        public String Result ;
        /// <summary>
        /// E:ATL-MES2.0_设备改造_改造方案通讯协议规约
        /// </summary>
        public String FolderPath ;
        
		@Override
		public String toString() {
			return "ResponseInfo [Result=" + Result + ", FolderPath=" + FolderPath + "]";
		}
		
		
	}
	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return ResponseInfo.Result;
	}
}
