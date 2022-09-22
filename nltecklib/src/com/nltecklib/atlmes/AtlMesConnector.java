package com.nltecklib.atlmes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.atlmes.MesFactory;
import com.nltecklib.protocol.atlmes.Root;
import com.nltecklib.protocol.atlmes.mes.A005;
import com.nltecklib.protocol.atlmes.mes.A006;
import com.nltecklib.protocol.atlmes.mes.A033;
import com.nltecklib.protocol.atlmes.mes.A034;

public class AtlMesConnector {

	private String ip;
	private int port;

	private Socket socket;
	private OutputStreamWriter outputStream;
	private BufferedReader reader;

	private MesSocketListener listener;

	public void registerListener(MesSocketListener listener) {
		this.listener = listener;
	}

	public void setIp(String ip) {
		// TODO Auto-generated method stub
		this.ip = ip;
	}

	public String getIp() {
		// TODO Auto-generated method stub
		return ip;
	}

	public void setPort(int port) {
		// TODO Auto-generated method stub
		this.port = port;
	}

	public int getPort() {
		// TODO Auto-generated method stub
		return port;
	}

	private void pushRevBuffer(Root data) {

		// System.out.println("<--" + data);

		if (listener != null) {
			listener.revData(data);
		}
	}

	private void mesResult(Root root, boolean send) {
		if (listener != null) {
			listener.sendMesResult(root, send);
		}
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	public void connect() throws IOException {
		socket = new Socket(ip, port);
		InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
		outputStream = new OutputStreamWriter(socket.getOutputStream());
		reader = new BufferedReader(inputStream);
		startRevThread();
	}

	// 接收线程
	private Thread revThread;
	private String byteArray = "";// 用于存放解析后剩余的无法解析的字符串

	private void runRev() {

		while (socket.isConnected()) {

			try {

				Thread.sleep(20);
				String result = null;

				try {
					result = reader.readLine();
				} catch (Exception e) {

					break;
				}
				// String result = reader.readLine();

				if (result == null || result.length() == 0) {

					continue;
				}

				// System.out.println("<--" + result);

				// 接包
				if (byteArray.length() > 0) {
					result = byteArray + result;
					byteArray = "";
				}
				// 粘包处理

				List<String> buffs = new ArrayList<String>();
				int start = 0;
				int end = 0;
				// 计算长度处理
				while (end < result.length()) {
					// System.out.println(result.substring(start, start + 1) .equals("{"));
					// System.out.println( (start < result.length() - 1));
					while (!result.substring(start, start + 1).equals("{") && (start < result.length() - 1)) {
						start++;
					}
					int bracketCount = 0;
					boolean flag = false;
					boolean find = false;

					for (int i = start; i < result.length(); i++) {
						if (result.substring(i, i + 1).equals("\"")) {
							flag = !flag;
						}
						if (!flag) {
							if (result.substring(i, i + 1).equals("{")) {
								bracketCount++;
							} else if (result.substring(i, i + 1).equals("}")) {
								bracketCount--;

								if (bracketCount == 0) {
									end = i + 1;
									find = true;
									break;
								}
							}

						}
					}
					if (!find) {
						break;
					}
					String sub = result.substring(start, end);
					buffs.add(sub);
					start = end;
				}
				// 有剩余接包
				if (start != result.length()) {
					byteArray = result.substring(start, result.length());
				}

				int index = 0;

				for (String buffChild : buffs) {
					try {

						// recodeJson(buffChild);

						Root root = MesFactory.decode(buffChild);

						mesResult(root, false);

						// String log = "RX:" + root;
						// Logger logger = LogUtil.getLogger(ip + "_protocol");
						// logger.debug(log);

						if (!(root instanceof A006 || root instanceof A034)) {
							System.out.println("RX:" + buffChild);
						}

						pushRevBuffer(root);

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void startRevThread() {
		revThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runRev();
			}
		});
		revThread.start();
	}

	public synchronized void send(Root root) throws IOException {

		String jsonStr = MesFactory.encode(root);

		mesResult(root, true);

		// String log = "TX:" + jsonStr;
		// Logger logger = LogUtil.getLogger(ip + "_protocol");
		// logger.debug(log);
		if (!(root instanceof A005 || root instanceof A033)) {
			System.out.println("TX:" + jsonStr);
		}

		outputStream.getEncoding();
		outputStream.write(jsonStr);
		outputStream.flush();

	}

	public void disConnect() {
		try {
			outputStream.close();
			reader.close();
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
