package com.nltecklib.device;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.IOUtil;
import com.nltecklib.utils.LogUtil;

/**
 * KEY SIGHT 万用表 型号34461A
 * 
 * @author Administrator
 *
 */
public class KeySight34461A implements Meter {

	private final static int TIME_OUT = 5000;
	private Socket socket;
	private MEASURE_TYPE measureType = MEASURE_TYPE.VOLT_DC;
	private TRIGGER_SOURCE triggerSource = TRIGGER_SOURCE.IMM;
	public final static int PORT = 5025; // 默认端口
	private double resolution = 0.00001; // 默认精度为0.01mV
	private int maxRange = 5; // 最大量程
	private int sampleCount = 1; // 采样次数
	private int index;
	private boolean use;
	private String ip;
	private boolean impAuto = true; // 阻抗输入模式
	private double nplc = 10; // plc积分时间

	private Logger logger;

	public enum MEASURE_TYPE {

		VOLT_DC, CURRENT_DC, RES, FREQ
	}

	public enum TRIGGER_SOURCE {
		// 立即触发，外部触发，软件触发，内部触发
		IMM, EXT, BUS, INT
	}

	public KeySight34461A(int index) {

		this.index = index;
		logger = LogUtil.getLogger("meter");
	}

	@Override
	public void connect(String ip) throws Exception {

		connect(ip, PORT);
	}

	@Override
	public void connect() throws Exception {
		connect(ip, PORT);

	}

	public void connect(String ip, int port) throws Exception {

		if (isConnected()) {
			socket.close();

		}
		logger.info("connect meter : " + ip + ",port:" + port);

		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, port), 1000);

		initCfg();

	}

	@Override
	public boolean isConnected() {

		if (socket == null || socket.isClosed()) {

			return false;
		}

		return socket.isConnected();
	}

	private void writeMessage(String cmd) throws IOException {

		socket.getOutputStream().write(cmd.getBytes());
	}

	private String readMessage() throws IOException {

		byte[] buff = new byte[1024];
		return "";
		// socket.getInputStream().read(b)
	}

	/**
	 * 初始化配置
	 * 
	 * @throws IOException
	 */
	public void initCfg() throws IOException {

		/**
		 * VOLT:DC:NPLC 10 设置积分时间解析度
		 * 
		 * VOLT:IMP:AUTO OFF 输入阻抗
		 * 
		 */

		String type = "";
		// 配置测试类型
		StringBuilder cmd = new StringBuilder();
		cmd.append("CONF:");
		switch (measureType) {
		case CURRENT_DC:
			cmd.append("CURR:DC ");
			type = "CURRENT";
			break;
		case VOLT_DC:
			cmd.append("VOLT:DC ");
			type = "VOLT";
			break;
		case RES:
			cmd.append("RES ");
			type = "RES";
			break;
		case FREQ:
			cmd.append("FREQ");
			type = "FREQ";
			break;
		}
		// cmd.Append(maxRange );

		// 一次发送
		cmd.append(";");
		cmd.append(":TRIG:SOUR " + triggerSource + ";");
		cmd.append(":" + type + ":NPLC " + nplc + ";");
		cmd.append(":" + type + ":IMP:AUTO " + (impAuto ? "ON" : "OFF") + ";");
		cmd.append(":SAMP:COUNT " + sampleCount + "\n");

		logger.info(cmd.toString());
		writeMessage(cmd.toString());
		BaseUtil.sleep(100);

		// //把OK读掉
		// try {
		// IOUtil.readMessageOneLine(socket.getInputStream(), TIME_OUT);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// cmd.append("\n");
		//
		// writeMessage(cmd.toString());
		// BaseUtil.sleep(100);
		// writeMessage("TRIG:SOUR " + triggerSource + "\n");// 触发方式
		// BaseUtil.sleep(100);
		// writeMessage("SAMP:COUNT " + sampleCount + "\n");// 采样次数
		// BaseUtil.sleep(100);

	}


	@Override
	public double ReadSingleClearBuffer() throws IOException, InterruptedException {
		
		while (socket.getInputStream().available() > 0) {
			System.out.println("当前输入流中存在数据：" + socket.getInputStream().available());
			byte[] readBuffer = new byte[socket.getInputStream().available()];
			socket.getInputStream().read(readBuffer);
		}
		
		writeMessage("READ?\n");
		BaseUtil.sleep(20);
		String message = IOUtil.readMessageOneLine(socket.getInputStream(), TIME_OUT);
		Double val = MatchMeterReadVal(message);
		if (val == null) {

			throw new IOException("error pattern read str:" + message);
		}
		// 转为mA或mV
		return Math.abs(val * 1000);

	}


	@Override
	public double ReadSingle() throws IOException, InterruptedException {
		writeMessage("READ?\n");
		BaseUtil.sleep(20);
		String message = IOUtil.readMessageOneLine(socket.getInputStream(), TIME_OUT);
		Double val = MatchMeterReadVal(message);
		if (val == null) {

			throw new IOException("error pattern read str:" + message);
		}
		// 转为mA或mV
		return Math.abs(val * 1000);

	}
	
	
	

	/**
	 * 读取实际值，不取绝对值
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */

	@Override
	public double ReadRealSingle() throws IOException, InterruptedException {
		writeMessage("READ?\n");
		BaseUtil.sleep(20);
		String message = IOUtil.readMessageOneLine(socket.getInputStream(), TIME_OUT);
		Double val = MatchMeterReadVal(message);
		if (val == null) {

			throw new IOException("error pattern read str:" + message);
		}
		// 转为mA或mV
		return val * 1000;

	}

	public void reset() throws IOException {
		writeMessage("*RST\n");
	}

	public static Double MatchMeterReadVal(String readStr) {
		int val = 0;
		// String pattern = "^([+-]\\d{1,}[.]\\d*)E([+-]\\d*)\n?$";
		String pattern = "^([+-]\\d+.?\\d*)E([+-]\\d*)\n?$";
		Matcher matcher = Pattern.compile(pattern).matcher(readStr);
		if (matcher.matches()) {

			return Double.parseDouble(matcher.group(1)) * Math.pow(10, Double.parseDouble(matcher.group(2)));
		}

		return null;

	}

	@Override
	public int getIndex() {

		return index;
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
	public String getIpAddress() {
		// TODO Auto-generated method stub
		return ip;
	}

	@Override
	public void setIpAddress(String ip) {
		// TODO Auto-generated method stub
		this.ip = ip;
	}

	@Override
	public void disconnect() throws Exception {
		if (isConnected()) {
			socket.close();
		}
	}

	public boolean isImpAuto() {
		return impAuto;
	}

	public void setImpAuto(boolean impAuto) {
		this.impAuto = impAuto;
	}

	public double getNplc() {
		return nplc;
	}

	public void setNplc(double nplc) {
		this.nplc = nplc;
	}

}
