package com.nlteck.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.ResponseDecorator;

public class NetUtil {

	public static String getIpAddress(Socket socket) {

		InetAddress inetAddress = socket.getInetAddress();
		return inetAddress.getHostAddress();

	}

	public static void writeSocket(Socket socket, List<Byte> sendData) throws IOException {

		OutputStream ois = socket.getOutputStream();
		byte[] array = ProtocolUtil.convertArrayType(sendData.toArray(new Byte[0]));
		ois.write(array);
		ois.flush();
		ois.close();
	}

	public static byte[] receiveSocket(Socket socket) throws IOException {

		InputStream is = socket.getInputStream();
		if (is.available() > 0) {

			byte[] input = new byte[is.available()];
			int numberRead = is.read(input);
			is.close();
			return input;
		}

		return null;

	}

	private static int readToBuff(InputStream is, List<Byte> buff) throws IOException {

		byte[] array = new byte[is.available()];
		int readNum = is.read(array, 0, array.length);
		for (int i = 0; i < readNum; i++) {
			buff.add(array[i]);
		}
		return readNum;
	}
	
	
	
	/**
	 * 收到完整的网络数据包后立即返回
	 * 
	 * @param socket
	 * @param decorator
	 * @param timeOut
	 * @return
	 * @throws IOException
	 */
	public static ResponseDecorator sendAndRecvImmediate(Socket socket, Decorator decorator, int timeOut)
			throws Exception {

		List<Byte> sendData = null;
		sendData = Entity.encode(decorator);

//		if (MainBoard.printDebugLog == 1) {
//
//			try {
//				Logger logger = Entity.getLogger(
//						decorator.getCode() == MainCode.PickupCode || decorator.getCode() == MainCode.DeviceStateCode
//								? "netpick"
//								: "netconfig");
//				logger.debug(" -> pc " + Entity.printList(sendData));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
		NetUtil.writeSocket(socket, sendData);

		long tick = System.currentTimeMillis();
		List<Byte> recvData = new ArrayList<Byte>();
		InputStream is = socket.getInputStream();
		int len = 0;
		do {

			CommonUtil.sleep(3);
			if (len > 0 && is.available() >= len + 9 - recvData.size()) {

				readToBuff(is, recvData);

//				if (MainBoard.printDebugLog == 1) {
//
//					try {
//						Logger logger = Entity.getLogger(
//								decorator.getCode() == MainCode.PickupCode || decorator.getCode() == MainCode.DeviceStateCode
//										? "netpick"
//										: "netconfig");
//						logger.debug(" <- pc " + Entity.printList(recvData));
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
				ResponseDecorator dec = (ResponseDecorator) Entity.decode(recvData);
				if (dec.getResult().getCode() != Result.SUCCESS) {

					throw new IOException("socket communicate failed ,failed code: " + dec.getResult());
				}

				return dec;

			} else if (is.available() >= 6) {

				readToBuff(is, recvData);
				int j = 0;
				for (j = 0; j < recvData.size(); j++) {

					if (recvData.get(j) == 0x18) {

						break;
					}
				}
				if (j > 0) {

					recvData = recvData.subList(j, recvData.size());
				}

				// 解析长度
				if (recvData.size() >= 6) {

					len = (int) ProtocolUtil.compose(recvData.subList(4, 6).toArray(new Byte[0]), true);

				}

			}
		} while (System.currentTimeMillis() - tick <= timeOut);

		throw new IOException("can not read received data from net");

	}
}
