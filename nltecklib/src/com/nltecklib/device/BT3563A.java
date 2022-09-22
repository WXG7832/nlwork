package com.nltecklib.device;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nltecklib.device.Meter;
import com.nltecklib.utils.BaseUtil;

/**
 * 日置BT3563A
 * 
 * @author wavy_zheng 2022年7月20日
 *
 */
public class BT3563A implements Meter {
	protected final static int TIME_OUT = 5000;
	protected Socket socket;
	protected MeasurementTarget measTarget = MeasurementTarget.R;
	public final static int PORT = 23; // 默认端口
	protected int index;
	protected boolean use;
	protected String ip;
	protected String terminator = "\n";
	protected SampleRate sampleRate = SampleRate.MEDIUM; // 默认中等采样速度

	/**
	 * 采样速率
	 * 
	 * @author wavy_zheng 2022年7月20日
	 *
	 */
	public enum SampleRate {

		EXFAST, FAST, MEDIUM, SLOW;

	}

	/**
	 * @description 目标测量值
	 * @author zemin_zhu
	 * @dateTime Jul 7, 2022 4:36:24 PM
	 */
	public enum MeasurementTarget {
		R, // 电阻
		V, // 电压
		RV, // 电阻及电压
	}

	public BT3563A(int index) {

		this.index = index;
	}

	public String readSampleRate() throws Exception {

		send("SAMPle:RATE?");
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
		return message;
	}

	@Override
	public double ReadSingle() throws IOException, InterruptedException {

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

		init();

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
		this.ip = ip;
	}

	@Override
	public boolean isUse() {
		return use;
	}

	@Override
	public void setUse(boolean use) {
		this.use = use;
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

	public void setSampleRate(SampleRate rate) throws IOException {

		send(":SAMP:RATE " + rate.name());
		this.sampleRate = rate;

	}

	/**
	 * @description 初始化
	 * @author zemin_zhu
	 * @dateTime Jul 7, 2022 4:15:23 PM
	 */
	protected void init() throws Exception {

		String[] commandArr = new String[] { ":HEADER OFF", // 回应报文无报头
				":AUT ON", // 自动检测量程
				":SYST:DATA OFF", // 关闭自动上报测量值
				// ":MODE " + measTarget.toString(), // 目标测量值
				":TRIGger:SOURce IMM", // 触发读值的条件来自内部
		};
		String command = String.join(";", commandArr);
		send(command);
	}

	/**
	 * @description 发送指令
	 * @author zemin_zhu
	 * @dateTime Jul 7, 2022 4:47:21 PM
	 */
	protected void send(String command) throws IOException {
		command += terminator;
		socket.getOutputStream().write(command.getBytes());
	}

	public void setMeasTarget(MeasurementTarget measTarget) throws IOException {
		this.measTarget = measTarget;
		send(":MODE " + measTarget.toString());
	}

	private void clearRecvBuff() throws IOException {

		while (socket.getInputStream().available() > 0) {

			System.out.println("当前输入流中存在数据：" + socket.getInputStream().available());
			byte[] readBuffer = new byte[socket.getInputStream().available()];
			socket.getInputStream().read(readBuffer);
		}
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

	public double ReadSingleClearBuffer() throws IOException, InterruptedException {

		clearRecvBuff();
		send("FETCH?");
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

	public SampleRate getSampleRate() {
		return sampleRate;
	}

	/**
	 * 读取多条组合数据
	 * 
	 * @author wavy_zheng 2022年7月20日
	 * @return
	 */
	public List<Double> readMultiClearBuffer() throws IOException, InterruptedException {

		List<Double> buff = new ArrayList<>();
		clearRecvBuff();
		send("FETCH?");
		BaseUtil.sleep(20);
		String message = readMessageOneLine(socket.getInputStream(), TIME_OUT);
		System.out.println(message);
		String[] arr = message.split(",");
		for (String element : arr) {

			Double val = MatchMeterReadVal(element);
			if (val != null) {
				buff.add(val * 1000);
			} else {

				buff.add(0.0);
			}

		}
		return buff;

	}

	public static void main(String[] args) {

		BT3563A resisMeter = new BT3563A(0);
		resisMeter.setIpAddress("192.168.1.100");
		try {
			resisMeter.connect();
			resisMeter.setMeasTarget(MeasurementTarget.RV);
			// 等电阻表得到量程
			Thread.sleep(1000);
			resisMeter.readSampleRate();

			List<Double> list = resisMeter.readMultiClearBuffer();
			System.out.println(list);

			resisMeter.setMeasTarget(MeasurementTarget.V);
			double voltage = resisMeter.ReadSingleClearBuffer();
			System.out.println(String.format("%f mv", voltage));
			resisMeter.setMeasTarget(MeasurementTarget.R);
			Thread.sleep(500);
			double value = resisMeter.ReadSingle();
			System.out.println(String.format("%f mΩ", value));
			resisMeter.disconnect();
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
