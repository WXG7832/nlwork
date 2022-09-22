package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * MES发送初始化开机指令,内容主要包括设备关键部件及设备运行标准参数
 * {"Header":{"SessionID":"Guid","FunctionID":"A007","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ServerSoft","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"Count":"","CmdInfo":{"ControlCode":"Run","StateCode":"","StateDesc":""},"UserInfo":{"UserID":"123","UserName":"ATL","UserLevel":"1"},"ResourceInfo":{"ResourceID":"EQ00000001","ResourceShift":"M"},"SpartInfo":[{"SpartName":"上烘箱电机同步带","SpartID":"001","SpartExpectedLifetime":"100","ChangeFlag":"true"},{"SpartName":"下烘箱电机同步带","SpartID":"002","SpartExpectedLifetime":"100","ChangeFlag":"true"},{"SpartName":"正涂隔膜泵油封","SpartID":"003","SpartExpectedLifetime":"100","ChangeFlag":"true"},{"SpartName":"反涂隔膜泵油封","SpartID":"004","SpartExpectedLifetime":"100","ChangeFlag":"true"},{"SpartName":"入牵皮带","SpartID":"005","SpartExpectedLifetime":"100","ChangeFlag":"true"},{"SpartName":"出牵皮带","SpartID":"006","SpartExpectedLifetime":"100","ChangeFlag":"true"},{"SpartName":"中间牵引皮带","SpartID":"007","SpartExpectedLifetime":"100","ChangeFlag":"true"}],"ModelInfo":"123","ParameterInfo":[{"ParamID":"001","StandardValue":"45","UpperLimitValue":"50","LowerLimitValue":"30","Description":"温度"},{"ParamID":"002","StandardValue":"45","UpperLimitValue":"50","LowerLimitValue":"30","Description":"压力"}]}}
 * @author guofang_ma
 *
 */
public class A007 extends RequestRoot{

	public RequestInfo RequestInfo = new RequestInfo();
	
	public A007() {
		Header.FunctionID="A007";
	}
	
	@Override
	public String toString() {
		return "A007 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	 public static class ParameterInfoItem
	    {
	        public int ParamID ;
	        public int StandardValue ;
	        public int UpperLimitValue ;
	        public int LowerLimitValue ;
	        /**
	         *  温度
	         */
	        public String Description ;

	    }
	 
	    public static  class SpartInfoItem
	    {
	        /// <summary>
	        /// 上烘箱电机同步带
	        /// </summary>
	        public String SpartName ;
	        public String SpartID ;
	        public String SpartExpectedLifetime;
	        /// <summary>
	        /// 等于true表示刚更换易损件，否则只指示该易损件当前的值
	        /// </summary>
	        public String ChangeFlag;

	    }
	    
//	    public static class UserInfo
//	    {
//	        public String UserID ;
//	        public String UserName ;
//	        public String UserLevel ;
//	    }



	    public static class ResourceInfo
	    {
	        public String ResourceID ;
	        public String ResourceShift ;

	    }

	    public static class CmdInfo
	    {
	        public String ControlCode ;
	        public String StateCode ;
	        public String StateDesc ;
	    }

	public static class RequestInfo {
		
        public String Count ;
        public CmdInfo CmdInfo =new CmdInfo();
        public UserInfo UserInfo =new UserInfo();
        public ResourceInfo ResourceInfo =new ResourceInfo();
        public List<SpartInfoItem> SpartInfo =new ArrayList<SpartInfoItem> ();
        public String ModelInfo ;
        public List<ParameterInfoItem> ParameterInfo =new ArrayList<ParameterInfoItem> ();
        
        public String OutputParameterInfo()
        {	
        	Gson gson = new GsonBuilder().create();
            return gson.toJson(ParameterInfo);
        }
		@Override
		public String toString() {
			return "RequestInfo [Count=" + Count + ", CmdInfo=" + CmdInfo + ", UserInfo=" + UserInfo + ", ResourceInfo="
					+ ResourceInfo + ", SpartInfo=" + SpartInfo + ", ModelInfo=" + ModelInfo + ", ParameterInfo="
					+ ParameterInfo + "]";
		}
		
		
	}
}
