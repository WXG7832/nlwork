package com.nltecklib.protocol.fins;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.nltecklib.protocol.plc.PlcData;
import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.LogUtil;

/**
 * 连接器
 * 
 * @author Administrator
 *
 */
public class FinsTcpConnector {

	public static boolean handshaked ; // 是否已经发送握手包
	public static int port = 9600;
	public static String ip = "192.168.250.1"/*"10.110.59.1"*/; // PLC ip

	private NioSocketConnector connector;
	private IoSession session;
	private FinsMessageReceiver receiver;
	
	private static Logger   logger;

	public static byte[] client = new byte[4]; // 本机数据
	public static byte[] server = new byte[4]; // PLC数据

	private static FinsTcpConnector finsTcpConnector;

	// 获取连接器 单例
	public synchronized static FinsTcpConnector getInstance(String ip) {
		if (finsTcpConnector == null) {
			//String ip = "192.168.250.96";
			if (BaseUtil.checkIpAddress(ip)) {
                
				System.out.println(ip);
				
				String[] arr = ip.split("\\.");
				if (arr.length == 4) {
					for (int i = 0; i < arr.length; i++) {

						client[i] = (byte) Integer.parseInt(arr[i]);
					}
				}
			}
			try {
				logger = LogUtil.getLogger("plc");
			} catch (Exception e) {
			
				e.printStackTrace();
			}
			logger.info("创建本机节点" + ip);
			
			// client = InetAddress.getLocalHost().getAddress(); // 获取本机IP地址节点

			finsTcpConnector = new FinsTcpConnector();
		}
		return finsTcpConnector;
	}
	
	

	private FinsTcpConnector() {
		handshaked = false;

		connector = new NioSocketConnector();
		// 协议过滤器
		connector.getFilterChain().addLast("finsCodec",
				new ProtocolCodecFilter(new FinsProtocolCodecFactory(true, receiver)));
		// 业务处理器
		connector.setHandler(new FinsMessageHandler(receiver));
		
		try {
			logger = LogUtil.getLogger(getLocalNode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 获取本机节点
	 * @return
	 */
	public String getLocalNode() {
		
		String str = "";
		for(int n = 0 ; n < client.length ; n++) {
			
			str += client[n] + (n == client.length - 1 ? "" : ".");
		}
		return str;
	}
	
	/**
	 * 获取远程节点
	 * @return
	 */
	public String getRemoteNode() {
		
		String str = "";
		for(int n = 0 ; n < server.length ; n++) {
			
			str += server[n] + (n == server.length - 1 ? "" : ".");
		}
		return str;
	}

	public void setConnectTimeOut(long miliSeconds) {

		connector.setConnectTimeoutMillis(miliSeconds);
	}

	/**
	 * 是否同步消息接收和发送
	 * 
	 * @param sync
	 */
	private void setSynchronize(boolean sync) {

		session.getConfig().setUseReadOperation(sync);

	}

	private Object readSyncMessage(int timeout) {

		ReadFuture readFuture = session.read();

		if (!readFuture.awaitUninterruptibly(timeout)) {
			throw new RuntimeException("PLC网络通信超时!");
		}

		return readFuture.getMessage();

	}

	/**
	 * 连接PLC
	 */
	public boolean connect() {
        
		//logger.info("连接PLC地址" + ip);
		// 链接服务端
		ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, port));

		// 阻塞等待，知道链接服务器成功，或被中断
		connectFuture.awaitUninterruptibly(100);

		if (!connectFuture.isConnected()) {
            
			//logger.info("连接PLC地址" + ip + "失败");
			
			String info = "connect to " + ip + " failed";
			if (receiver != null)
				receiver.receive(info, false);
			return false;

		}

		session = connectFuture.getSession();
		setSynchronize(true); // 设置同步模式

		if (receiver != null)
			receiver.receive("connect to" + ip + " successed!", true);
        
		
		//logger.info("连接PLC地址" + ip + "成功");
		return true;
	}

	public boolean isConnected() {
        
		return session != null ? session.isConnected() : false;
	}

	// 同个时刻只能有一条网络报文写入网口
	public synchronized PlcData send(PlcData data, int timeout) {

		send(data, true);
		return (PlcData) readSyncMessage(timeout);
	}

	private void send(PlcData data, boolean sync) {

		if (session.isConnected()) {

			WriteFuture wf = session.write(data); // 发送对象
			if (sync)
				wf.awaitUninterruptibly();

		} else {
			if (receiver != null)
				receiver.receive("connection is disconnected", false);
		}

	}

	/**
	 * 断开连接
	 */
	public void disconnect() {
		
		if (session != null) {
			session.closeNow(); // 关闭连接
			handshaked = false; // 是否发送握手包 置为false
			logger.info("断开PLC地址" + ip);
		}
	}

//	public static final String GET_SCHEDULT_RESULT = "GetSchedultResult"; // 获取流程方法名
//	public static final String GET_TEST_RESULT_BY_CAP = "GetTestResultByCap"; // 获取容量测试结果方法名
//	public static final String GET_TEST_RESULTS = "GetTestResults"; // 获取化成测试结果方法名
//	public static final String SET_MARKING = "SetMarking"; // 复测方法名
//	public static final int TIMEOUT = 100; // PLC读写超时时间
//
//	/**
//	 * 测试结果
//	 */
//	public enum TestResult {
//
//		None, Good, FormationBad, CapacityBad;
//
//		public String toString() {
//
//			switch (this) {
//			case Good:
//				return "良品";
//			case FormationBad:
//				return "化成不良";
//			case CapacityBad:
//				return "容量测试不良";
//			default:
//				return "未知";
//			}
//		}
//
//	}
//
//	/**
//	 * 读取Barcode
//	 * 
//	 * @param filePath
//	 *            文件路径
//	 * @return
//	 */
//	public static List<String> readBarcode(String filePath) {
//
//		List<String> barcode = new ArrayList<>();
//		CsvReader csvReader = null;
//		try {
//			// 创建CSV读对象
//			csvReader = new CsvReader(filePath);
//
//			// 读表头
//			csvReader.readHeaders();
//			while (csvReader.readRecord()) {
//				// System.out.println(csvReader.getRawRecord());
//				barcode.add(csvReader.get("Barcode"));
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (csvReader != null) {
//				csvReader.close();
//			}
//		}
//		return barcode;
//	}
//
//	/**
//	 * 获取当前设备对应夹具压力值
//	 * 
//	 * @param cabinet
//	 * @return 压力值
//	 */
//	public static int getPressure(Cabinet cabinet) {
//
//		int fixtureIndex = getFixtureIndex(cabinet);
//		DoubleACTPressureData data = new DoubleACTPressureData(fixtureIndex, 1, true);
//		Map<Integer, Integer> pressureMap = data.decode(FinsTcpConnector.getInstance().send(data, TIMEOUT));
//		// Map<Integer, Integer> pressureMap =
//		// PickupManager.plcData.get(PlcDataIndex.PRESSURE.ordinal());
//		return pressureMap.get(fixtureIndex);
//	}
//
//	/**
//	 * 设置当前设备对应夹具压力值
//	 * 
//	 * @param
//	 * @param pressure
//	 *            压力值
//	 * @return 是否成功（需要等待PLC改变压力值，并没有立刻改变夹具压力）
//	 */
//	public static boolean setPressure(int fixtureIndex, int pressure) {
//
//		Map<Integer, Integer> map = new HashMap<>();
//		map.put(fixtureIndex, pressure);
//		// 变更压力
//		DoubleACTPressureData pressureData = new DoubleACTPressureData(fixtureIndex, 1, false);
//		pressureData.setDatas(map);
//		byte[] rev = FinsTcpConnector.getInstance().send(pressureData, TIMEOUT);
//
//		if (rev[0] == -1) { // 变更压力成功（返回数据为-1代表成功）
//
//			// 置位 夹具压力变更信号
//			return position(fixtureIndex, PlcDataCode.FixturePressure);
//		}
//
//		return false;
//	}
//
//	/**
//	 * 获取机器控制状态
//	 * 
//	 * @param cabinet
//	 * @param code
//	 * @return
//	 */
//	public static ControlState getControlState(Cabinet cabinet, PlcDataCode code) {
//
//		int fixtureIndex = getFixtureIndex(cabinet);
//		PIEFControlData data = new PIEFControlData(code, fixtureIndex);
//		ControlState state = data.controlDecode(FinsTcpConnector.getInstance().send(data, TIMEOUT));
//		return state;
//	}
//
//	/**
//	 * 置位
//	 * 
//	 * @param code
//	 * @return
//	 */
//	public static boolean position(int fixtureIndex, PlcDataCode code) {
//
//		PIEFControlData controlData = new PIEFControlData(code, fixtureIndex, false, ControlState.ON);
//		byte[] rev = FinsTcpConnector.getInstance().send(controlData, TIMEOUT);
//		return rev[0] == -1;
//	}
//
//	/**
//	 * 复位
//	 * 
//	 * @param code
//	 * @return
//	 */
//	public static boolean reset(int fixtureIndex, PlcDataCode code) {
//
//		PIEFControlData controlData = new PIEFControlData(code, fixtureIndex, false, ControlState.OFF);
//		byte[] rev = FinsTcpConnector.getInstance().send(controlData, TIMEOUT);
//		return rev[0] == -1;
//	}
//
//	/**
//	 * 根据设备名称 获取 夹具号
//	 * 
//	 * @param cabinet
//	 * @return 夹具号
//	 */
//	public static int getFixtureIndex(Cabinet cabinet) {
//
//		String name = cabinet.getName();
//		String deviceNo = name.substring(name.length() - 1, name.length());
//		int fixtureIndex = -1; // 夹具号
//		if (deviceNo.equals("A")) {
//			fixtureIndex = 9;
//		} else {
//			fixtureIndex = Integer.parseInt(deviceNo) - 1;
//		}
//		return fixtureIndex;
//
//	}
//
//	/**
//	 * 启动应用程序
//	 * 
//	 * @param programName
//	 * @return
//	 * @throws IOException
//	 */
//	public static void startProgram(String programPath) throws IOException {
//		if (programPath != null && programPath.length() != 0) {
//			try {
//				String programName = programPath.substring(programPath.lastIndexOf("/") + 1,
//						programPath.lastIndexOf("."));
//				List<String> list = new ArrayList<String>();
//				list.add("cmd.exe");
//				list.add("/c");
//				list.add("start");
//				list.add("\"" + programName + "\"");
//				list.add("\"" + programPath + "\"");
//				ProcessBuilder pBuilder = new ProcessBuilder(list);
//				pBuilder.start();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
