package com.nlteck.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;

import com.nltecklib.utils.BaseUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午12:57:01 环境类
 */
public class Env {

	public static boolean isLinuxEnvironment() {

		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("linux")) {

			return true;
		}
		return false;
	}

	/**
	 * 获取linux MAC地址
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String getMac() throws IOException, InterruptedException {
		String[] cmd = new String[] { "cat", "/sys/class/net/eth1/address" };
		return executeSysCmd(cmd).toUpperCase().trim();
	}

	/**
	 * 获取windows MAC地址
	 * 
	 * @return
	 */
	public static String getWindowsMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// windows下的命令，显示信息中包含有mac地址信息
			process = Runtime.getRuntime().exec("ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				System.out.println(line);
				// 寻找标示字符串[physical
				index = line.toLowerCase().indexOf("物理地址");// 中文系统
				if (index < 0) {
					index = line.toLowerCase().indexOf("physical address");// 英文系统
				}

				if (index >= 0) {// 找到了
					index = line.indexOf(":");// 寻找":"的位置
					if (index >= 0) {
//						System.out.println(mac);
						// 取出mac地址并去除2边空格
						mac = line.substring(index + 1).trim();
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac != null ? mac.toUpperCase().replace("-", ":") : null;
	}

	/**
	 * 执行linux系统命令
	 * 
	 * @param cmd
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String executeSysCmd(String[] cmd) throws IOException, InterruptedException {

		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		InputStream stderr = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(stderr, "ISO-8859-1");
		BufferedReader br = new BufferedReader(isr);

		String line = "";
		StringBuffer result = new StringBuffer();
		while ((line = br.readLine()) != null) { // 打印出命令执行的结果

			result.append(line);
			result.append("\n");
		}
		// Environment.infoLogger.info(result.toString());
		return result.toString();

	}

	/**
	 * 立即修改系统IP并生效
	 * 
	 * @param address
	 * @return
	 */
	public static boolean configIpAddress(String address) {

		String[] cmd = new String[3];
		cmd[0] = "ifconfig";
		cmd[1] = "eth1";
		cmd[2] = address;
		// 及时生效
		try {
			executeSysCmd(cmd);
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static int getCurrentProcessId() {

		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName(); // format: "pid@hostname"
		try {
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		} catch (Exception e) {
			return -1;
		}

	}

	private static String readToString(String fileName) {
		String encoding = "UTF-8";
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将字符串写入文件
	 * 
	 * @param fileName
	 * @param content
	 */
	private static void writeStringToFile(String fileName, String content) {

		String encoding = "UTF-8";
		File file = new File(fileName);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(content.getBytes(Charset.forName(encoding)));
			out.flush();
			out.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * "/etc/network/interfaces" 修改设备IP地址
	 * 
	 * @param newIp
	 * @return
	 */

	public static boolean writeIpAddress(String path, String newIp) {

		String content = readToString(path);
		System.out.println(content);
		int findSt = 0;
		StringBuffer writeBuff = new StringBuffer();
		if ((findSt = content.indexOf("iface eth1 inet static")) >= 0) {

			if ((findSt = content.indexOf("address", findSt)) >= 0) {

				int findEd = content.indexOf("\n", findSt);
				System.out.println(content.substring(findSt + 7, findEd));
				writeBuff.append(content.substring(0, findSt + 7));
				writeBuff.append(" ");
				writeBuff.append(newIp);
				writeBuff.append(content.substring(findEd));
				// 写入文档
				writeStringToFile(path, writeBuff.toString());
				if (isLinuxEnvironment()) {

					return configIpAddress(newIp);
				}
			}
		}

		return false;
	}

	/**
	 * 判定linux系统中该进程是否已经存在?
	 * 
	 * @param processName
	 * @return
	 */
	public static boolean isAnotherProcessExists(String processName) {

		String[] cmd = new String[2];
		cmd[0] = "ps";
		cmd[1] = "ef";
		// 及时生效
		try {
			String response = executeSysCmd(cmd);
			String[] processLines = response.split("\n");
			int processId = getCurrentProcessId();
			for (String line : processLines) {

				// Environment.infoLogger.info(line);
				if (line.contains(processName) && !line.contains(processId + "")) {

					return true;
				}

			}
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * 获取当前设备IP
	 * 
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */

	public static String getLocalIP() {

		try {
			if (isLinuxEnvironment()) {
				return getLinuxLocalIp();
			} else {
				return InetAddress.getLocalHost().getHostAddress();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "0.0.0.0";
		}
	}

	/**
	 * 获取linux本地ip
	 * 
	 * @return
	 * @throws SocketException
	 */
	private static String getLinuxLocalIp() throws SocketException {
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			String name = intf.getName();
			if (!name.contains("docker") && !name.contains("lo")) {
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ipaddress = inetAddress.getHostAddress().toString();
						if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
//							System.out.println(ipaddress);
							return ipaddress;
						}
					}
				}
			}
		}
		throw new SocketException("get local ip error!");
	}

	/**
	 * 设置系统时间
	 * 
	 * @param date
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String setSysDatetime(Date date) throws IOException, InterruptedException {
		Date st = new Date();
		String str = BaseUtil.formatDate("yyyy-MM-dd HH:mm:ss", date);
		// System.out.println("date -s \"" + str + "\"");
		String result = executeSysCmd(new String[] { "date", "-s", str });
		BaseUtil.sleep(10);
		result += executeSysCmd(new String[] { "hwclock", "-w" });
		System.out.println("set date spend " + (System.currentTimeMillis() - st.getTime()) / 1000);
		return result;
	}

}
