package com.nltecklib.utils;

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
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;

/**
 * 核心板A7Linux系统工具
 * @author Administrator
 *
 */
public class CoreA7EnvUtil {
    
	
	
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
	 * 临时修改系统IP并生效,但这个IP在系统重启后失效；writeIpAddress可以永久变更IP
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
	
	public static boolean isLinuxEnvironment() {

		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("linux")) {

			return true;
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
			int  processId = getCurrentProcessId();
			for (String line : processLines) {

				//Environment.infoLogger.info(line);
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
	
	public static int getCurrentProcessId() {

		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName(); // format: "pid@hostname"
		try {
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		} catch (Exception e) {
			return -1;
		}

	}
    
	public static String setSysDatetime(Date date) throws IOException, InterruptedException {

		Date st = new Date();
		String str = BaseUtil.formatDate("yyyy-MM-dd HH:mm:ss",date);
		// System.out.println("date -s \"" + str + "\"");
		String result = executeSysCmd(new String[] { "date", "-s", str });
		BaseUtil.sleep(10);
		result = executeSysCmd(new String[] { "hwclock", "-w" });
        if(!result.isEmpty()) {
			
			return result;
		}

		System.out.println("set date spend " + (System.currentTimeMillis() - st.getTime()) / 1000);

		return result;
	}

	public static Date getSysDatetime() throws IOException, InterruptedException, ParseException {
        
		String[] cmd = new String[] { "date", "+%Y-%m-%d %H:%M:%S" };
		String result = executeSysCmd(cmd);
		return BaseUtil.parseDate("yyyy-MM-dd HH:mm:ss",result);

	}
	
}
