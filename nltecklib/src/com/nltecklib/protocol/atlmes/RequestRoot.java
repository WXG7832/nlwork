package com.nltecklib.protocol.atlmes;

import java.util.Date;
import java.util.UUID;

public abstract class RequestRoot extends Root {

	public ResquestHeader Header = new ResquestHeader();

	public String getFuncID() {
		return Header.FunctionID;
	}

	public String getSessionID() {
		return Header.SessionID;
	}

	public String getResult() {
		return "OK";
	}

	public static class ResquestHeader {
		public String SessionID;
		public String FunctionID;
		public String PCName;
		public String EQCode;
		public String SoftName;
		public String RequestTime;

		public ResquestHeader() {
			// TODO Auto-generated constructor stub
			SessionID = UUID.randomUUID().toString();
			PCName = MesInfo.PCName;
			EQCode = MesInfo.EQCode;
			SoftName = MesInfo.SoftName;
			RequestTime = Root.sdf.format(new Date());
		}

		@Override
		public String toString() {
			return "ResquestHeader [SessionID=" + SessionID + ", FunctionID=" + FunctionID + ", PCName=" + PCName
					+ ", EQCode=" + EQCode + ", SoftName=" + SoftName + ", RequestTime=" + RequestTime + "]";
		}

	}
}
