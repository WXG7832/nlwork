package com.nltecklib.protocol.atlmes.mes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.atlmes.RequestRoot;
/**
 * ACT上传Output数据
 *  {"Header":{"SessionID":"GUID","FunctionID":"A033","PCName":"PCName","EQCode":"EQ00000001","SoftName":"ClientSoft1","RequestTime":"2019-05-24 15:28:34 488"},"RequestInfo":{"FILE_NAME":"Y10D3098191901","MACHINE_NO":"Y10","DEVICE_NO":"4","ZONE_NO":"6","PATH":"D:\\BackupData\\AG-Y101-190819-2R\\20190819","START_TIME":"2019-08-19 19:01:36","END_TIME":"2019-08-19 23:55:02","OVER_FLAG":"2","SCHEDULE":"AG-3950-16-212-07C-M","TEST_NAME":"AG-Y101-190819-2R","MI_PACKAGE_NO":"V88","TEST_TYPE":"AG","CELL_COUNT_TEST":"13","cells":[{"CELL_NAME":"V88928D312F3","CHANNEL_INDEX":480},{"CELL_NAME":"V88929A303C7","CHANNEL_INDEX":479},{"CELL_NAME":"V88928C330E4","CHANNEL_INDEX":478},{"CELL_NAME":"V88928F31AA5","CHANNEL_INDEX":477},{"CELL_NAME":"V88929A30E8E","CHANNEL_INDEX":476},{"CELL_NAME":"V88927FW016D","CHANNEL_INDEX":475},{"CELL_NAME":"V88926A312C2","CHANNEL_INDEX":473},{"CELL_NAME":"V88928E330F7","CHANNEL_INDEX":472},{"CELL_NAME":"V88928B32A3C","CHANNEL_INDEX":470},{"CELL_NAME":"V88924F30166","CHANNEL_INDEX":468},{"CELL_NAME":"V88924E33761","CHANNEL_INDEX":467},{"CELL_NAME":"V88929A32356","CHANNEL_INDEX":466},{"CELL_NAME":"V88924D31059","CHANNEL_INDEX":465}],"RawData":[{"CELLNO":"225","STATUS":"--","CYCLICTIME":"1","STEP":"1","CURRENT":"0.0000","VOLTAGE":"513.3000","CAPACITY":"0.0","ENERGY":"0.0","STIME":"2019-08-20 08:57:24 488","DTIME":"0","TEMP":"85.4","PRESSURE":"1060"}]}}
 * @author guofang_ma
 *
 */
public class A033 extends RequestRoot {

	public RequestInfo RequestInfo = new RequestInfo();

	public A033() {
	  Header.FunctionID = "A033";
	}
	
	

	@Override
	public String toString() {
		return "A033 [Header=" + Header + ", RequestInfo=" + RequestInfo + "]";
	}

	public static class CellsItem
    {
        public String CELL_NAME ;
        public String OVER_FLAG;
        public int CHANNEL_INDEX ;
    }

    public static class RawDataItem
    {
        public int CELLNO ;
        public String STATUS ;
        public int CYCLICTIME ;
        public int STEP ;
        public double CURRENT ;
        public double VOLTAGE ;
        public double CAPACITY ;
        public double ENERGY ;
        public Date STIME ;
        public int DTIME ;
        public double TEMP ;
        public double PRESSURE ;
    }


	public static class RequestInfo {
		  public String FILE_NAME ;
	        public String MACHINE_NO ;
	        public int DEVICE_NO ;
	        public int ZONE_NO ;
	        public String PATH ;
	        public Date START_TIME ;
	        public Date END_TIME ;
	        public int OVER_FLAG ;
	        public String SCHEDULE ;
	        public String TEST_NAME ;
	        public String MI_PACKAGE_NO ;
	        public String TEST_TYPE ;
	        public int CELL_COUNT_TEST ;
	        public List<CellsItem> cells =new ArrayList<CellsItem>();
	        public List<RawDataItem> RawData =new ArrayList<RawDataItem>();
		@Override
		public String toString() {
			return "RequestInfo [FILE_NAME=" + FILE_NAME + ", MACHINE_NO=" + MACHINE_NO + ", DEVICE_NO=" + DEVICE_NO
					+ ", ZONE_NO=" + ZONE_NO + ", PATH=" + PATH + ", START_TIME=" + START_TIME + ", END_TIME="
					+ END_TIME + ", OVER_FLAG=" + OVER_FLAG + ", SCHEDULE=" + SCHEDULE + ", TEST_NAME=" + TEST_NAME
					+ ", MI_PACKAGE_NO=" + MI_PACKAGE_NO + ", TEST_TYPE=" + TEST_TYPE + ", CELL_COUNT_TEST="
					+ CELL_COUNT_TEST + ", cells=" + cells + ", RawData=" + RawData + "]";
		}
		
		
	}
}
