package com.nltecklib.device;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nltecklib.utils.BaseUtil;

public class DM7275A implements Meter{
	protected final static int TIME_OUT = 5000;
	protected Socket socket;
	protected String ip;
	protected boolean use;
	protected int index;
	protected String terminator = "\n";
	public final static int PORT = 23; // 默认端口
	
	public DM7275A(int index) {
		this.index=index;
	}
	
	public enum SampleRate {

		FAST, MEDIUM, SLOW;

	}
	
	/**
	 * 读取一次表值固定，表值不会刷新
	 */
	@Override
	public double ReadSingle() throws IOException, InterruptedException {
		clearRecvBuff();
		send("FETCH?");
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println("message=" + message);
		Double val = MatchMeterReadVal(message);
		if (val == null) {

			throw new IOException("error pattern read str:" + message);
		}
		// 转为mA或mV
		return val * 1000;

	}

	@Override
	public double ReadRealSingle() throws IOException, InterruptedException {
		return Math.abs(ReadSingle());
	}

	@Override
	public void connect(String ip) throws Exception {
		if (isConnected()) {
			socket.close();

		}

		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, PORT), TIME_OUT);
//		init();
	}

	protected void send(String command) throws IOException {
		command += terminator;
		socket.getOutputStream().write(command.getBytes());
	}
	
	@Override
	public void disconnect() throws Exception {
		if (isConnected()) {
			socket.close();
		}		
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getIpAddress() {
		return ip;
	}

	@Override
	public void setIpAddress(String ip) {
		this.ip=ip;
		
	}

	@Override
	public boolean isUse() {
		return use;
	}

	@Override
	public void setUse(boolean use) {
		this.use=use;
		
	}

	@Override
	public boolean isConnected() {
		if (socket == null || socket.isClosed()) {
			return false;
		}
		return socket.isConnected();
	}

	@Override
	public void connect() throws Exception {
		connect(ip);
	}

	@Override
	public double ReadSingleClearBuffer() throws IOException, InterruptedException {
		clearRecvBuff();
		send("READ?");
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
		Double val = MatchMeterReadVal(message);
		if (val == null) {

			throw new IOException("error pattern read str:" + message);
		}
		// 转为mA或mV mR
		return Math.abs(val * 1000);
	}

	
	private void clearRecvBuff() throws IOException {

		while (socket.getInputStream().available() > 0) {

			System.out.println("当前输入流中存在数据：" + socket.getInputStream().available());
			byte[] readBuffer = new byte[socket.getInputStream().available()];
			socket.getInputStream().read(readBuffer);
		}
	}
	
	public String readSampleRate() throws Exception {

	
		return null;
	}

	
	public static Double MatchMeterReadVal(String readStr) {
		int val = 0;
		// String pattern = "^([+-]\\d{1,}[.]\\d*)E([+-]\\d*)\n?$";
		// String pattern = "^([+-]\\d+.?\\d*)E([+-]\\d*)[\\s\\S]*$";
		String pattern = "^([+-]?\\d+\\.?\\d*)E([+-]\\d*)\n?$";
		readStr = readStr.trim();
		Matcher matcher = Pattern.compile(pattern).matcher(readStr);
		if (matcher.matches()) {

			return Double.parseDouble(matcher.group(1)) * Math.pow(10, Double.parseDouble(matcher.group(2)));
		}

		return null;

	}
	
	public static void main(String[] args) {
		DM7275A dm7275a=new DM7275A(0);
		dm7275a.setIpAddress("192.168.1.100");
		try {
			dm7275a.connect();
			for(int i=0;i<5;i++) {
				
//				dm7275a.ReadSingle();
				double meterVal=dm7275a.ReadSingleClearBuffer();
				System.out.println("meterVal: "+meterVal);
			}
			dm7275a.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 读取单行字符串消息;在未读到\n前该方法将一直阻塞
	 * 
	 * @param serialPort
	 * @param charset
	 *            使用的编码字符集
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
					if (readVal == '\n') {

						return line.toString();
					}
					line.append((char) readVal);
				}

			}
			// System.out.println("is.available() = " + is.available());
			if (System.currentTimeMillis() - tick > timeOut) {

				throw new IOException("表读取网络数据超时");
			}
		}

	}

	
}
