package com.nltecklib.protocol.modbus.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.modbus.tcp.constant.Environment;
import com.nltecklib.protocol.modbus.tcp.util.CommonUtil;
import com.nltecklib.protocol.modbus.tcp.util.ConvertUtils;
import com.nltecklib.protocol.modbus.tcp.data.Data;
import com.nltecklib.protocol.modbus.tcp.service.ModbusTcpService;
import com.nltecklib.protocol.modbus.tcp.util.ProtocolUtil;

public class ModbusTcpMaster {

	
	public  Socket socket;
	public  OutputStream outputStream;
	public  InputStream inputStream;
	
	public static final int DEFAULT_SLEEP_TIME = 2000;//默认休眠时间
	
	public ModbusTcpMaster(Socket socket) throws IOException{
		
		this.socket = socket;
		
		try {
			outputStream =  socket.getOutputStream();			//获取一个输出流，向服务端发送信息
			inputStream =  socket.getInputStream();			//获取一个输入流，接收服务端的信息
		} catch (IOException e) {
			throw new IOException(String.format("tcp连接失败,%s",e.getMessage()));
		}
	}

	
	
	public  String send(Data d) throws Exception {
		return send(d,DEFAULT_SLEEP_TIME);
	}
	
	public synchronized String send(Data d,int sleepTime) throws Exception{
		
	
		byte[] data = d.encoder();

		while (inputStream.available() > 0) {

			inputStream.read(new byte[128]);
			System.out.println("当前输入流中是否还存在数据：" + (inputStream.available() > 0));
		}

		byte[] bytes = new byte[data.length];
		for(int i = 0; i < data.length; i++) {
			bytes[i] = data[i];
		}		
		outputStream.write(bytes, 0, bytes.length);
		
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		//System.out.println(sdf.format(date)+"  :发送的hex："+ConvertUtils.binaryToHexString(data));
		
		// 一直读取数据，直到读到完整的数据为止
		long start = System.currentTimeMillis();
		List<Byte> list = new ArrayList<>();
		while(true){

			int numBytes = -1;
			if (inputStream.available() > 0) {
				byte[] readBuffer = new byte[inputStream.available()];
				numBytes = inputStream.read(readBuffer);

				// 如果接受到数据
				if (numBytes > 0) {
					for (int i = 0; i < numBytes; i++) {
						//System.out.print(readBuffer[i]);
						list.add(readBuffer[i]);
					}

				}
			}

			// 读到数据
			//list 字节
			if(list.size() >= Environment.getHeadByteLength()){
				
				String hex = ProtocolUtil.binaryToHexString(list);
				
				return ProtocolUtil.validProtocol(hex);
			}

			long runTime = System.currentTimeMillis() - start;
			if(runTime >= 2000) {	
				throw new Exception(String.format("协议长度异常，hex: %s", ProtocolUtil.binaryToHexString(list)));				
			}
			Thread.sleep(100);

		}
	}
	

	public  void disconnect() {
		try {
			outputStream.close();
			inputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
