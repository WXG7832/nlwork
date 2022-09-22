package com.nltecklib.device;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.nltecklib.utils.BaseUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年7月21日 下午1:41:59
* 基恩士扫码枪SR1000
*/
public class KeyenceSR1000 {
   
	private String ip = "192.168.100.100"; //默认IP
	private final static int PORT = 9004; //默认端口号
	private final static String CR = "\r"; //结束符
	private Socket   socket;
	private final static int TIME_OUT = 5000;
	
	public KeyenceSR1000(String ip) {
		
		this.ip = ip;
		
	}
	
	public KeyenceSR1000() {
		
		
	}
	
	
	private void clearRecvBuff() throws IOException {

		while (socket.getInputStream().available() > 0) {

			System.out.println("当前输入流中存在数据：" + socket.getInputStream().available());
			byte[] readBuffer = new byte[socket.getInputStream().available()];
			socket.getInputStream().read(readBuffer);
		}
	}
	
	/**
	 * 对焦
	 * @author  wavy_zheng
	 * 2022年7月25日
	 * @throws Exception 
	 */
	public void focus() throws Exception {
		
		String cmd = "FTUNE";
		clearRecvBuff();
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
	}
	
	/**
	 * 读取条码
	 * @author  wavy_zheng
	 * 2022年7月21日
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String readBarcode() throws IOException, InterruptedException {
		
		String cmd = "LON";
		clearRecvBuff();
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		message = message.trim();
		return message.replace("\r", "");
	}
	
	
	public void endRead() throws Exception {
		
		String cmd = "LOFF";
		clearRecvBuff();
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
	}
	
	
	/**
	 * 读取单行字符串消息;在未读到\n前该方法将一直阻塞
	 * 
	 * @param serialPort
	 * @param charset    使用的编码字符集
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String readMessageOneLine(InputStream is, int timeOut) throws IOException, InterruptedException {

		StringBuffer line = new StringBuffer();

		long tick = System.currentTimeMillis();
		while (true) {

			Thread.sleep(20);
			if (is.available() > 0) {

				for (int n = 0; n < is.available(); n++) {

					int readVal = is.read();
					if (readVal == '\r') {

						return line.toString();
					}
					line.append((char) readVal);
				}

			}
			//System.out.println("is.available() = " + is.available());
			if (System.currentTimeMillis() - tick > timeOut) {

				throw new IOException("扫描仪读取网络数据超时");
			}
		}

	}
	
	private void send(String cmd) throws IOException {
		
		cmd += CR;
		socket.getOutputStream().write(cmd.getBytes());
		
	}
	
	/**
	 * 清除扫描仪缓存
	 * @author  wavy_zheng
	 * 2022年7月21日
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void clearCache() throws Exception{
		
		String cmd = "BCLR";
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		if(!message.contains("OK")) {
			
			throw new Exception("清除缓存失败:" + message);
		}
	}
	
	public int readCount() throws Exception {
		
		String cmd = "NUM";
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
		
		return 0;
	}
	
	public void reset() throws Exception {
		
		
		String cmd = "RESET";
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		if(!message.contains("OK")) {
			
			throw new Exception("复位失败:" + message);
		}
	}
	
	
	/**
	 * 扫描仪自检
	 * @author  wavy_zheng
	 * 2022年7月21日
	 */
	public void test() {
		
		
		
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void changeIpAddress(String ip) throws Exception {
		
		if(!BaseUtil.checkIpAddress(ip)) {
			
			throw new Exception("ip格式不符合规范!");
		}
		String cmd = "WN,200," + ip;
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
		if(!message.contains("OK")) {
			
			throw new Exception("修改通信地址失败:" + message);
		}
		//确认修改
		cmd = "RN,200";
		send(cmd);
		BaseUtil.sleep(20);
		message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
		
		
		//保存配置
		save();
		
		
		this.ip = ip;
	}
	
	
	
	public boolean isConnected() {
		if (socket == null || socket.isClosed()) {
			return false;
		}
		return socket.isConnected();
	}
	
	public void connect() throws Exception {
		if (isConnected()) {
			socket.close();

		}

		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, PORT), TIME_OUT);
		
	}
	
	
	public void disconnect() throws IOException {
		
		if(socket == null || !socket.isConnected()) {
			
			return;
		}
		socket.close();
		
	}
	
	public void save() throws Exception {
		
		String cmd = "SAVE";
		send(cmd);
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		if(!message.contains("OK")) {
			
			throw new Exception("保存配置失败!");
		}
		
	}
	
	
	public static void main(String[] args) {
		 
		KeyenceSR1000 sr = new KeyenceSR1000();
		
		try {
			 sr.setIp("192.168.1.100");
		     sr.connect();
		     System.out.println("连接成功");
		      
		     //sr.readCount();
		     
		    // sr.reset();
		     
		     //sr.focus();
		     
		   System.out.println(sr.readBarcode());
		   
		     
		   
		
		} catch(Exception ex) {
			
			
			 try {
				sr.endRead();
				
				 sr.readCount();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 ex.printStackTrace();
			 
			
			
		}
		
		
	}
	
	
}
