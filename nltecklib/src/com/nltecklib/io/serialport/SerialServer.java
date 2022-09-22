package com.nltecklib.io.serialport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.li.Entity;
import com.rm5248.serial.SerialPort;

public class SerialServer {

	private int retryCount = 2;// 重发次数
	private int revTimeOut = 3000;// 接收超时时间
	private NlteckPackageFactory factory;
	private SerialPort port;
	private SerialListener listener;
	private InputStream inputStream; // 从串口来的输入流
	private OutputStream outputStream;// 向串口输出的
	/**
	 * 最后收到的完整原数据
	 */
	private List<Byte> receiveByteList;

	public SerialServer(NlteckPackageFactory factory, SerialPort serialPort, SerialListener listener) {
		this.factory = factory;
		this.port = serialPort;
		this.listener = listener;
		outputStream = serialPort.getOutputStream();
		inputStream = serialPort.getInputStream();
		startReceive();
	}

	private 	Thread revThread;
	
	private void startReceive() {
		revThread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isConnected()) {
					
					try {
						NlteckIOPackage pack=readOnePack();
						if(pack!=null) {
							if(listener!=null) {
								listener.receiveData(pack);
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}	
			}
		});
		revThread.setDaemon(true);
		revThread.start();
		// TODO Auto-generated method stub
		
	}

	public void sendData(NlteckIOPackage pack) throws IOException {
		while (inputStream.available() > 0) {

			System.out.println("data in buff :" + inputStream.available());
			byte[] readBuffer = new byte[inputStream.available()];
			inputStream.read(readBuffer);
		}

		byte[] sendData = factory.encode(pack);

		if (listener != null) {
			listener.send(sendData);
		}
		outputStream.write(sendData);
	}


	private synchronized NlteckIOPackage readOnePack()
			throws IOException {
		if (port == null || port.isClosed()) {
			throw new IOException("port not open");
		}


		while (true) {
			// 发送前先清空输入流中数据，防止各种奇葩的意外


			List<Byte> recData = new ArrayList<>();

			while (isConnected()) {

				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				recData = read(recData);

				// 读到数据
				if (recData.size() > factory.getMinDecodeLen()) {

					int len = factory.getPackLen(listToArray(recData));

					if (recData.size() == len) { // 读到完整的数据
						receiveByteList = recData;
						return factory.decode(listToArray(recData));
					} else if (recData.size() >= len) {

						throw new IOException("sticky bag(" + printList(recData) + ")");
					} else {
						// System.out.println("还没有读到完整数据，继续读取...");
					}
				}
			}

			throw new IOException("response out time (" + printList(recData) + ")");
		}
	}

	public List<Byte> getReceiveByteList() {
		return receiveByteList;
	}

	public void setReceiveByteList(List<Byte> receiveByteList) {
		this.receiveByteList = receiveByteList;
	}

	protected List<Byte> read(List<Byte> recData) throws IOException {

		byte[] readBuffer = null;
		int numBytes = -1;
		while (inputStream.available() > 0) {

			readBuffer = new byte[inputStream.available()];
			numBytes = inputStream.read(readBuffer);

			if (listener != null) {
				listener.receive(readBuffer);
			}

			if (numBytes > 0) {
				for (int i = 0; i < numBytes; i++) {
					recData.add(readBuffer[i]);
				}
			}
		}
		return recData;
	}

	private static byte[] listToArray(List<Byte> list) {
		byte[] result = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	private static String printList(List<Byte> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(String.format("%02X", list.get(i)));
			if (i < list.size() - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public boolean isConnected() {
		return port != null && !port.isClosed();
	}

}