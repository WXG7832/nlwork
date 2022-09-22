package com.nlteck.junit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.nlteck.util.NetUtil;
import com.nltecklib.protocol.li.Decorator;

public class NetServer {

	public interface NetListener {

		void connected(Socket socket);

		void disconnected(Socket socket);

		void receive(Socket socket, Decorator dec);

		void except(Socket socket, StringBuffer err);
	}

	private final static int PORT = 8161;
	private ServerSocket socket;
	private Socket client; // 已连接的网络套接字
	private List<NetListener> listeners = new ArrayList<NetListener>();
	
	public static void main(String[] args) {
		
		
		
	}

	public NetServer() throws IOException {

		socket = new ServerSocket(PORT);
		listen();
		
		

	}

	private void monitor() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					

				}

			}

		});
		thread.setDaemon(true);
		thread.start();

	}

	public void close() throws IOException {

		if (socket != null) {

			socket.close();
		}
	}

	/**
	 * 监听端口
	 * 
	 * @throws IOException
	 */
	public Socket listen() throws IOException {

		if (client == null) {

			client = socket.accept();
			System.out.println(NetUtil.getIpAddress(client) + " connected!");
		}

		return client;

	}

}
