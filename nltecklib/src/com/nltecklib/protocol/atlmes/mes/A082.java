package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.ResponseRoot;
import com.nltecklib.protocol.fins.Environment.Area;

/**
 * /// MES获取离线生产数据响应 ///
 * {"Header":{"SessionID":"GUID","FunctionID":"A052","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24
 * 15:28:34
 * 488"},"RequestInfo":{"Result":"OK","FolderPath":"E:\ATL-MES2.0\200_设备改造\20_改造方案\通讯协议规约"}}
 * /// Result--反馈结果，OK正确找到路径，NG为没有找到路径，当Result为NG时FolderPath为空 / * @author
 * guofang_ma
 *
 */
public class A082 extends ResponseRoot {

	public ResponseInfo ResponseInfo = new ResponseInfo();

	public A082() {
		Header.FunctionID = "A082";
	}

	@Override
	public String toString() {
		return "A082 [Header=" + Header + ", ResponseInfo=" + ResponseInfo + "]";
	}

	public static class CellNameTable implements Comparable<CellNameTable> {

		public String MACHINENO;//设备号
		public String AREANO;//区域号，
		public String CHANNELNO;
		public String BATTERYNO;
		public String BOARDNO;
		public String SCANUSER;
		public String SCAN_TIME;

		@Override
		public String toString() {
			return "CellNameTable [MACHINENO=" + MACHINENO + ", AREANO=" + AREANO + ", CHANNELNO=" + CHANNELNO
					+ ", BATTERYNO=" + BATTERYNO + ", BOARDNO=" + BOARDNO + ", SCANUSER=" + SCANUSER + ", SCAN_TIME="
					+ SCAN_TIME + "]";
		}

		@Override
		public int compareTo(CellNameTable o) {
			try {
				int thisArea = Integer.parseInt(AREANO);
				int otherArea = Integer.parseInt(o.AREANO);

				if (thisArea != otherArea) {
					return thisArea - otherArea;
				} else {
					int thisChnNo = Integer.parseInt(CHANNELNO);
					int otherChnNo = Integer.parseInt(o.CHANNELNO);
					return thisChnNo - otherChnNo;
				}

			} catch (Exception e) {
				// TODO: handle exception
				return 0;
			}
		}

	}

	public static class ResponseInfo {
		public List<CellNameTable> CELLNAMETABLE = new ArrayList<>();

		@Override
		public String toString() {
			return "ResponseInfo [CELLNAMETABLE=" + CELLNAMETABLE + "]";
		}

	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return "OK";
	}
}
