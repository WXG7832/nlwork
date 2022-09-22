package com.nltecklib.atlmes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.nltecklib.protocol.atlmes.RequestRoot;
import com.nltecklib.protocol.atlmes.ResponseRoot;
import com.nltecklib.protocol.atlmes.ResponseRoot.ResponseHeader;
import com.nltecklib.protocol.atlmes.Root;
import com.nltecklib.protocol.atlmes.mes.A001;
import com.nltecklib.protocol.atlmes.mes.A002;
import com.nltecklib.protocol.atlmes.mes.A005;
import com.nltecklib.protocol.atlmes.mes.A006;
import com.nltecklib.protocol.atlmes.mes.A011;
import com.nltecklib.protocol.atlmes.mes.A012;
import com.nltecklib.protocol.atlmes.mes.A013;
import com.nltecklib.protocol.atlmes.mes.A013.ProductsItem;
import com.nltecklib.protocol.atlmes.mes.A014;
import com.nltecklib.protocol.atlmes.mes.A015;
import com.nltecklib.protocol.atlmes.mes.A016;
import com.nltecklib.protocol.atlmes.mes.A019;
import com.nltecklib.protocol.atlmes.mes.A019.ChildEQItem;
import com.nltecklib.protocol.atlmes.mes.A020;
import com.nltecklib.protocol.atlmes.mes.A021;
import com.nltecklib.protocol.atlmes.mes.A021.SpartLifeTimeInfoItem;
import com.nltecklib.protocol.atlmes.mes.A022;
import com.nltecklib.protocol.atlmes.mes.A023;
import com.nltecklib.protocol.atlmes.mes.A023.ResourceRecordInfoItem;
import com.nltecklib.protocol.atlmes.mes.A024;
import com.nltecklib.protocol.atlmes.mes.A025;
import com.nltecklib.protocol.atlmes.mes.A025.ResourceAlertInfoItem;
import com.nltecklib.protocol.atlmes.mes.A026;
import com.nltecklib.protocol.atlmes.mes.A029;
import com.nltecklib.protocol.atlmes.mes.A030;
import com.nltecklib.protocol.atlmes.mes.A031;
import com.nltecklib.protocol.atlmes.mes.A032;
import com.nltecklib.protocol.atlmes.mes.A033;
import com.nltecklib.protocol.atlmes.mes.A033.CellsItem;
import com.nltecklib.protocol.atlmes.mes.A033.RawDataItem;
import com.nltecklib.protocol.atlmes.mes.A034;
import com.nltecklib.protocol.atlmes.mes.A035;
import com.nltecklib.protocol.atlmes.mes.A035.DataListItem;
import com.nltecklib.protocol.atlmes.mes.A036;
import com.nltecklib.protocol.atlmes.mes.A037;
import com.nltecklib.protocol.atlmes.mes.A037.SerialNo;
import com.nltecklib.protocol.atlmes.mes.A038;
import com.nltecklib.protocol.atlmes.mes.A039;
import com.nltecklib.protocol.atlmes.mes.A040;
import com.nltecklib.protocol.atlmes.mes.A045;
import com.nltecklib.protocol.atlmes.mes.A045.EquParamItem;
import com.nltecklib.protocol.atlmes.mes.A046;
import com.nltecklib.protocol.atlmes.mes.A049;
import com.nltecklib.protocol.atlmes.mes.A049.MaterialInfoItem;
import com.nltecklib.protocol.atlmes.mes.A050;
import com.nltecklib.protocol.atlmes.mes.A051;
import com.nltecklib.protocol.atlmes.mes.A052;
import com.nltecklib.protocol.atlmes.mes.A055;
import com.nltecklib.protocol.atlmes.mes.A055.ParamInfoItem;
import com.nltecklib.protocol.atlmes.mes.A056;
import com.nltecklib.protocol.atlmes.mes.A081;
import com.nltecklib.protocol.atlmes.mes.A082;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.EQStateCode;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.FeedType;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.HLFlag;
import com.nltecklib.protocol.atlmes.mes.MesEnvironment.ProcessType;
import com.nltecklib.protocol.atlmes.mes.UserInfo;

public class AtlMes implements BaseAtlMes {

	private static long timeOut = 10000;

	public static long getTimeout() {
		return timeOut;
	}

	public static void setTimeout(long timeout) {
		timeOut = timeout;
	}

	private AtlMesConnector connector;

	private Map<String, ResponseRoot> revResponseMap = new HashMap<String, ResponseRoot>();// 接收MES回复
	// private Queue<RequestRoot> revRequestQueue = new LinkedList<RequestRoot>();//
	// 接收MES请求

	private List<MesListener> listeners = new ArrayList<MesListener>();

	@Override
	public void addMesListener(MesListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * 接收mes请求：A007,A011,A017,A047,A053
	 * 
	 * @param data
	 */
	private void revRequestData(RequestRoot data) {

		for (MesListener mesListener : listeners) {
			mesListener.receiveRequestData(data);
		}
	}

	private void mesResult(Root root, boolean send) {
		for (MesListener mesListener : listeners) {
			mesListener.sendMesResult(root, send);
		}
	}

	private String ip = "127.0.0.1";
	private int port = 5010;

	private void init() {

		connector = new AtlMesConnector();
		connector.registerListener(new MesSocketListener() {

			@Override
			public void revData(Root data) {
				// TODO Auto-generated method stub
				if (data instanceof ResponseRoot) {
					String sessID = data.getSessionID();
					revResponseMap.put(sessID, (ResponseRoot) data);

				} else if (data instanceof RequestRoot) {

					revRequestData((RequestRoot) data);
					// revRequestQueue.add((RequestRoot) data);
				}

			}

			@Override
			public void sendMesResult(Root root, boolean send) {
				// TODO Auto-generated method stub
				mesResult(root, send);
			}
		});

	}

	public AtlMes() {

		init();
	}

	@Override
	public void connectMes() throws IOException {

		if (connector == null) {

			init();
		}

		connector.setIp(ip);
		connector.setPort(port);
		connector.connect();
	}

	public void disconnectMes(boolean force) {

		if (connector != null && (connector.isConnected() || force)) {

			connector.registerListener(null);
			connector.disConnect();
		}
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void send(Root root) throws IOException {

		if (connector == null) {

			init();
		}
		if (!connector.isConnected()) {

			connector.connect();
		}
		connector.send(root);
	}

	private ResponseRoot findResponse(String sessID) throws IOException {
		return findResponse(sessID, timeOut);
	}

	/**
	 * 清除缓存
	 * 
	 * @param funcID
	 */
	private void clearSessionID(String sessID) {
		if (revResponseMap.containsKey(sessID)) {
			revResponseMap.remove(sessID);
		}
	}

	private ResponseRoot findResponse(String sessID, long timeOut) throws IOException {
		long time = System.currentTimeMillis();
		while (true) {
			sleep(20);
			if (revResponseMap.containsKey(sessID)) {
				ResponseRoot result = revResponseMap.get(sessID);
				revResponseMap.remove(sessID);
				return result;
			}
			if (System.currentTimeMillis() - time > timeOut) {
				throw new IOException("超时");
			}
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void a001() throws Exception {
		// TODO Auto-generated method stub

		A001 a001 = new A001();

		clearSessionID(a001.getSessionID());
		send(a001);

		A002 a002 = (A002) findResponse(a001.getSessionID());
		checkResult(a002);
		if (!a002.ResponseInfo.Result.equals("OK")) {
			throw new Exception("设备信息请求回复失败" + a002.ResponseInfo.Result);
		}
	}

	@Override
	public void a005() throws Exception {
		// TODO Auto-generated method stub

		A005 a005 = new A005();
		clearSessionID(a005.getSessionID());
		send(a005);

		A006 a006 = (A006) findResponse(a005.getSessionID(), 4000);
		checkResult(a006);
		boolean ok = a006.ResponseInfo.Result.equals("OK");

		// sendMesResult(a005, a006, ok ? 1 : 0);

		if (!ok) {
			throw new Exception("心跳回复失败" + a006.ResponseInfo.Result);
		}

		// return a006.ResponseInfo.IP;

	}

	@Override
	public void a019(EQStateCode parentEQStateCode, String andonState, int quantity, List<ChildEQItem> childEQ,
			String eqCode) throws Exception {
		// TODO Auto-generated method stub
		A019 a019 = new A019();
		a019.RequestInfo.ParentEQStateCode = parentEQStateCode;
		a019.RequestInfo.AndonState = andonState;
		a019.RequestInfo.Quantity = quantity;

		a019.RequestInfo.ChildEQ = childEQ;
		if (eqCode != null) {
			a019.Header.EQCode = eqCode;
		}

		clearSessionID(a019.getSessionID());
		send(a019);
		A020 a020 = (A020) findResponse(a019.getSessionID());
		checkResult(a020);
		if (!a020.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送设备状态回复失败" + a020.ResponseInfo.Result);
		}
	}

	@Override
	public void a021(List<SpartLifeTimeInfoItem> spartLifeTimeInfo) throws Exception {
		// TODO Auto-generated method stub
		A021 a021 = new A021();
		a021.RequestInfo.SpartLifeTimeInfo = spartLifeTimeInfo;

		clearSessionID(a021.getSessionID());
		send(a021);
		A022 a022 = (A022) findResponse(a021.getSessionID());
		checkResult(a022);
		if (!a022.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送设备状态回复失败" + a022.ResponseInfo.Result);
		}
	}

	@Override
	public void a023(List<ResourceRecordInfoItem> resourceRecordInfo) throws Exception {
		// TODO Auto-generated method stub
		A023 a023 = new A023();
		a023.RequestInfo.ResourceRecordInfo = resourceRecordInfo;
		clearSessionID(a023.getSessionID());
		send(a023);
		A024 a024 = (A024) findResponse(a023.getSessionID());
		checkResult(a024);
		if (!a024.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送设备状态回复失败" + a024.ResponseInfo.Result);
		}
	}

	@Override
	public A034.ResponseInfo a033(String fileName, String machineNo, int deviceNo, int zoneNo, String path,
			Date startTime, Date endTime, int overFlag, String schedule, String testName, String miPackAgeNo,
			String testType, int cellCountTest, List<CellsItem> cells, List<RawDataItem> rawData, String eqCode,
			int timeout) throws Exception {
		// TODO Auto-generated method stub
		A033 a033 = new A033();
		a033.RequestInfo.FILE_NAME = fileName;
		a033.RequestInfo.MACHINE_NO = machineNo;
		a033.RequestInfo.DEVICE_NO = deviceNo;
		a033.RequestInfo.ZONE_NO = zoneNo;
		a033.RequestInfo.PATH = path;
		a033.RequestInfo.START_TIME = startTime;
		a033.RequestInfo.END_TIME = endTime;
		a033.RequestInfo.OVER_FLAG = overFlag;
		a033.RequestInfo.SCHEDULE = schedule;
		a033.RequestInfo.TEST_NAME = testName;
		a033.RequestInfo.MI_PACKAGE_NO = miPackAgeNo;
		a033.RequestInfo.TEST_TYPE = testType;
		a033.RequestInfo.CELL_COUNT_TEST = cellCountTest;
		a033.RequestInfo.cells = cells;
		a033.RequestInfo.RawData = rawData;
		if (eqCode != null) {
			a033.Header.EQCode = eqCode;
		}
		clearSessionID(a033.getSessionID());
		send(a033);
		A034 a034 = (A034) findResponse(a033.getSessionID(), timeout * 1000);
		checkResult(a034);
		return a034.ResponseInfo;
	}

	@Override
	public boolean a015(String materialID, HLFlag highOrLowFlag, FeedType type, ProcessType operation)
			throws Exception {
		A015 a015 = new A015();
		a015.RequestInfo.MaterialID = materialID;
		a015.RequestInfo.HighOrLowFlag = highOrLowFlag;
		a015.RequestInfo.Type = (type == null ? 0 : type.ordinal());
		a015.RequestInfo.Operation = operation;
		clearSessionID(a015.getSessionID());
		send(a015);
		A016 a016 = (A016) findResponse(a015.getSessionID());
		checkResult(a016);
		if (!a016.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送上料验证请求回复失败" + a016.ResponseInfo.Result);
		}
		return a016.ResponseInfo.IsReset;

	}

	@Override
	public void a025(List<ResourceAlertInfoItem> resourceAlertInfo, String eqCode) throws Exception {
		A025 a025 = new A025();
		a025.RequestInfo.ResourceAlertInfo = resourceAlertInfo;
		if (eqCode != null) {
			a025.Header.EQCode = eqCode;
		}
		clearSessionID(a025.getSessionID());
		send(a025);
		A026 a026 = (A026) findResponse(a025.getSessionID());
		checkResult(a026);
		if (!a026.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送设备报警数据回复失败" + a026.ResponseInfo.Result);
		}
	}

	@Override
	public void replyA012(A011 a011, boolean isOK) throws Exception {
		A012 a012 = new A012();
		a012.Header.SessionID = a011.getSessionID();
		a012.ResponseInfo.Result = isOK ? "OK" : "NG";
		send(a012);
	}

	@Override
	public A014.ResponseInfo a013(String type, List<ProductsItem> products) throws Exception {
		A013 a013 = new A013();
		a013.RequestInfo.Type = type;
		a013.RequestInfo.Products = products;
		clearSessionID(a013.getSessionID());
		send(a013);
		A014 a014 = (A014) findResponse(a013.getSessionID());
		checkResult(a014);
		return a014.ResponseInfo;
	}

	@Override
	public void a045(UserInfo userInfo, List<EquParamItem> equParam) throws Exception {
		A045 a045 = new A045();
		a045.RequestInfo.UserInfo = userInfo;
		a045.RequestInfo.EquParam = equParam;
		clearSessionID(a045.getSessionID());
		send(a045);
		A046 a046 = (A046) findResponse(a045.getSessionID());
		checkResult(a046);
		if (!a046.ResponseInfo.Result.equals("OK")) {
			throw new Exception("设备运行设定参数更改上传请求回复失败" + a046.ResponseInfo.Result);
		}
	}

	@Override
	public void a029(String tab, String cell) throws Exception {
		A029 a029 = new A029();
		a029.RequestInfo.Tab = tab;
		a029.RequestInfo.Cell = cell;
		clearSessionID(a029.getSessionID());
		send(a029);
		A030 a030 = (A030) findResponse(a029.getSessionID());
		checkResult(a030);
		if (!a030.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送单个Tab号和电芯号回复失败" + a030.ResponseInfo.Result);
		}
	}

	@Override
	public void a055(List<ParamInfoItem> paramInfo) throws Exception {
		A055 a055 = new A055();
		a055.RequestInfo.ParamInfo = paramInfo;
		clearSessionID(a055.getSessionID());
		send(a055);
		A056 a056 = (A056) findResponse(a055.getSessionID());
		checkResult(a056);
		if (!a056.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送真空baking设备与freebaking设备OUTPUT数据回复失败" + a056.ResponseInfo.Result);
		}
	}

	@Override
	public void a031(String carrier, List<String> cell) throws Exception {
		A031 a031 = new A031();
		a031.RequestInfo.Carrier = carrier;
		a031.RequestInfo.Cell = cell;

		clearSessionID(a031.getSessionID());
		send(a031);
		A032 a032 = (A032) findResponse(a031.getSessionID());
		checkResult(a032);
		if (!a032.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送弹夹号和对应各个电芯号回复失败" + a032.ResponseInfo.Result);
		}
	}

	@Override
	public void a035(String model, String equType, String productSN, List<DataListItem> dataList) throws Exception {
		A035 a035 = new A035();
		a035.RequestInfo.Model = model;
		a035.RequestInfo.EquType = equType;
		a035.RequestInfo.ProductSN = productSN;
		a035.RequestInfo.DataList = dataList;

		clearSessionID(a035.getSessionID());
		send(a035);
		A036 a036 = (A036) findResponse(a035.getSessionID());
		checkResult(a036);
		if (!a036.ResponseInfo.Result.equals("OK")) {
			throw new Exception("发送涂布、冷压、分条上传Output数据回复失败" + a036.ResponseInfo.Result);
		}
	}

	@Override
	public A038.ResponseInfo a037(String operationCode, boolean reworkFlag, List<SerialNo> serialNoInfo, String eqCode)
			throws Exception {
		A037 a037 = new A037();
		a037.RequestInfo.OperationCode = operationCode;
		a037.RequestInfo.ReworkFlag = reworkFlag;
		a037.RequestInfo.SerialNoInfo = serialNoInfo;
		if (eqCode != null) {
			a037.Header.EQCode = eqCode;
		}

		clearSessionID(a037.getSessionID());
		send(a037);
		A038 a038 = (A038) findResponse(a037.getSessionID(), 12000);
		checkResult(a038);
		return a038.ResponseInfo;
	}

	@Override
	public UserInfo a039(String userID, String userPassword) throws Exception {
		A039 a039 = new A039();
		a039.RequestInfo.UserID = userID;
		a039.RequestInfo.UserPassword = userPassword;
		clearSessionID(a039.getSessionID());
		send(a039);
		A040 a040 = (A040) findResponse(a039.getSessionID());
		checkResult(a040);
		return new UserInfo(a040.ResponseInfo.UserID, a040.ResponseInfo.UserLevel, a040.ResponseInfo.UserName);
	}

	@Override
	public String a051(List<String> cells, String eqCode) throws Exception {
		A051 a051 = new A051();
		a051.RequestInfo.Cell = cells;
		if (eqCode != null) {
			a051.Header.EQCode = eqCode;
		}
		clearSessionID(a051.getSessionID());
		send(a051);
		A052 a052 = (A052) findResponse(a051.getSessionID(), 12000);
		checkResult(a052);
		if (!a052.ResponseInfo.Result.equals("OK")) {
			throw new Exception(" ACT上传整炉电芯号回复失败" + a052.ResponseInfo.Result);
		}
		return a052.ResponseInfo.FolderPath;
	}

	@Override
	public void a049(String modelInfo, List<String> productSN, UserInfo userInfo, List<MaterialInfoItem> materialInfo)
			throws Exception {
		A049 a049 = new A049();
		a049.RequestInfo.ModelInfo = modelInfo;
		a049.RequestInfo.ProductSN = productSN;
		a049.RequestInfo.UserInfo = userInfo;
		a049.RequestInfo.MaterialInfo = materialInfo;
		clearSessionID(a049.getSessionID());
		send(a049);
		A050 a050 = (A050) findResponse(a049.getSessionID());
		checkResult(a050);
		if (!a050.ResponseInfo.Result.equals("OK")) {
			throw new Exception(" 上传离线电芯条码失败：" + a050.ResponseInfo.Result);
		}
	}

	@Override
	public A082.ResponseInfo a081(String machine, boolean is_export, String eqCode, int timeout) throws Exception {
		A081 a081 = new A081();
		a081.RequestInfo.machine = machine;
		a081.RequestInfo.is_export = is_export;
		if (eqCode != null) {
			a081.Header.EQCode = eqCode;
		}
		clearSessionID(a081.getSessionID());
		send(a081);
		A082 a082 = (A082) findResponse(a081.getSessionID(), timeout * 1000);
		checkResult(a082);
		return a082.ResponseInfo;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return connector.isConnected();
	}

	/**
	 * 错误检查
	 * 
	 * @param root
	 * @throws Exception
	 */
	public void checkResult(ResponseRoot root) throws Exception {
		if (!root.Header.IsSuccess.equalsIgnoreCase("True")) {
			throw new Exception(
					String.format("%s 回复失败 [%s] %s", root.getFuncID(), root.Header.ErrorCode, root.Header.ErrorMsg));
		}
	}

}
