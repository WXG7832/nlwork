package com.nltecklib.io.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.google.gson.Gson;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年11月11日 上午10:32:55 广播，用于查询在线的所有纽联网络设备
 */
public class BroadcastManager {

	public final static int PORT = 6666; // 广播地址端口

	/**
	 * 建立广播响应器,每个纽联设备必须调用该方法建立广播响应
	 * 
	 * @author wavy_zheng 2020年11月11日
	 * @param type
	 *            ,当前设备的类型
	 * @param ip,
	 *            当前设备的IP地址
	 * @param identity
	 *            , 当前设备的唯一标识符
	 * @throws IOException
	 *
	 */
	public void createBroadcaseResponsor(DeviceType type, String ip, String identity) throws IOException {

		DatagramSocket datagramSocket = new DatagramSocket(PORT, InetAddress.getByName(ip));
		
		while (true) {

			// 构造DatagramPacket实例，用来接收最大长度为512字节的数据包
			byte[] data = new byte[512];
			DatagramPacket receivePacket = new DatagramPacket(data, 512);
			// 接收报文，此方法在接收到数据报前一直阻塞
			datagramSocket.receive(receivePacket);
			String recvMsg = new String(receivePacket.getData(), 0, receivePacket.getLength());
			System.out.println("recv:" + recvMsg);
			// 转成json
			BroadcastPackage pack = new Gson().fromJson(recvMsg, BroadcastPackage.class);
			if (pack.orient == Orient.REQUEST) {

				// 请求包
				BroadcastPackage response = new BroadcastPackage();
				response.deviceType = type;
				response.identity = identity;
				response.ipAddress = ip;
				response.orient = Orient.RESPONSE;

				String responseStr = new Gson().toJson(response);
				// 回复设备信息
				DatagramPacket sendPacket = new DatagramPacket(responseStr.getBytes(), 0, responseStr.length(),
						receivePacket.getAddress(), receivePacket.getPort());
				datagramSocket.send(sendPacket);

			}

		}

	}

	/**
	 * 用于上位机软件查询在线设备
	 * 
	 * @author wavy_zheng 2020年11月11日
	 * @param broadcastIp
	 *            广播地址，例如想查询192.168.1.*网段内所有的设备情况，填写192.168.1.255; 255.255.255.255
	 *            查询所有网段内的设备情况
	 * @param localIp
	 *            本地绑定ip
	 * @param timeOut
	 *            单位ms，最多等待时长；上位机需要等待多久后才返回结果!!
	 * @return 所有网段内在线的设备集合
	 * @throws Exception
	 */
	public List<BroadcastPackage> sendBroadcaseAndRecv(String localIp, String broadcastIp, int timeOut)
			throws Exception {

		BroadcastPackage pack = new BroadcastPackage();
		pack.orient = Orient.REQUEST;
		String sendStr = new Gson().toJson(pack);
		InetAddress inet = InetAddress.getByName(broadcastIp);
		DatagramPacket sendPacket = new DatagramPacket(sendStr.getBytes(), 0, sendStr.length(), inet, PORT);
		DatagramSocket datagramSocket = new DatagramSocket(PORT, InetAddress.getByName(localIp));
		datagramSocket.send(sendPacket); // 发送广播

		// 等待接收所有设备的回复
		long tick = System.currentTimeMillis();
		StringBuffer recvBuff = new StringBuffer();

		datagramSocket.setSoTimeout(1000);
		do {

			byte[] buff = new byte[10024];
			DatagramPacket recvPacket = new DatagramPacket(buff, 0, buff.length);
			try {
				datagramSocket.receive(recvPacket);
			} catch (IOException e) {

			}
			// 过滤掉本身
			if (recvPacket.getAddress() != null && !recvPacket.getAddress().getHostAddress().equals(localIp)) {

				String recvMsg = new String(recvPacket.getData(), 0, recvPacket.getLength());
				System.out.println(recvMsg);
				// 接收到设备信息,先缓存后再解析
				recvBuff.append(recvMsg);
			}

		} while (System.currentTimeMillis() - tick <= timeOut);

		// 超时时间到,开始解析缓存包
		System.out.println(recvBuff);
		
		return decodeBroadcastPacks(recvBuff.toString());

		
	}
	
	
	private static List<BroadcastPackage> decodeBroadcastPacks(String response) {
		
		List<BroadcastPackage> packs = new ArrayList<BroadcastPackage>();
		
		String[] secs = response.split("\\}\\{");
		for(String sec : secs) {
			
			if(!sec.substring(0,1).equals("{")) {
				
				sec = "{" + sec;
			}
			if(!sec.substring(sec.length() - 1, sec.length()).equals("}")) {
				
				sec += "}";
			}
			
			packs.add(new Gson().fromJson(sec, BroadcastPackage.class));
			
		}
		
		return packs;
		
		
	}

	/**
	 * 获取本机所有的网络地址
	 * 
	 * @author wavy_zheng 2020年11月11日
	 * @return
	 */
	public List<InetAddress> getLocalIPAddresses() {

		List<InetAddress> list = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = null;
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> addresss = ni.getInetAddresses();
				while (addresss.hasMoreElements()) {
					InetAddress nextElement = addresss.nextElement();
					if (nextElement instanceof Inet4Address) {
						list.add(nextElement);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

}
