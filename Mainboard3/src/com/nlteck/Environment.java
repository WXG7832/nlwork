package com.nlteck;

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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.nlteck.firmware.ControlUnit;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.LogUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.CoreData;


/**
 * 主控运行环境
 * 
 * @author Administrator
 *
 */
public class Environment {

	public static Logger infoLogger;
	public static Logger errLogger;
	public static Logger protocolLogger;
	public static Logger capLogger;
	public static Logger bugLogger;
	// public static Customer customer;

	/**
	 * gpio引脚定义
	 */
	public static final int RESET_OUTPUT_PIN = 69;
	public static final int RESET_INPUT_PIN = 70;
	public static final String IP_PATTERN = "((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}";

	public static final String RUNTIME_PATTERN = "^Mem:/s+([0-9\\.]+/s+)";

	static {

		try {

			if (isLinuxEnvironment()) {
				infoLogger = LogUtil.createLog("log/run.log");
				errLogger = LogUtil.createLog("log/err.log");
				protocolLogger = LogUtil.createLog("log/protocol.log");
				capLogger = LogUtil.createLog("log/capacity.log");
				bugLogger = LogUtil.createLog("log/bug.log");
			} else {

				infoLogger = LogUtil.createLog("run.log");
				errLogger = LogUtil.createLog("err.log");
				protocolLogger = LogUtil.createLog("protocol.log");
				capLogger = LogUtil.createLog("capacity.log");
				bugLogger = LogUtil.createLog("bug.log");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		infoLogger.info("create run.log");

		// customer = Customer.ATL;
	}

	/**
	 * 客户定义 ATL 宁德时代 ETC 天弋
	 */
	public enum Customer {

		ATL, ETC, LISHEN, CATL, HP

	}

	/**
	 * 产品基本定义
	 */

	/**
	 * 产品充电类型
	 * 
	 * @author Administrator
	 *
	 */
	public enum ChargeType {

		A3, A6, A15, A40, A60
	}

	/**
	 * 开关状态
	 * 
	 * @author Administrator
	 *
	 */
	public enum SwitchState {

		OFF, ON;
	}

	/**
	 * 轻微程度报警；设备不做任何处理 中等程度报警：设备做报警提醒（声光报警） 严重程度报警: 设备做出暂停流程处理 最高等级报警: 设备做出关闭电源等操作
	 */
	public enum AlertGrade {

		SLIGHT, MID, SEVERE, HIGHEST

	}

	/**
	 * 主控运行信息
	 * 
	 * @author Administrator
	 *
	 */
	public static class RuntimeInfo {

		public double cpu; // cpu占用率
		public double loadAverage1;
		public double loadAverage2;
		public double loadAverage3;
		public double memInUse; // used memory
		public double memInFree; // free memory
	}

	/**
	 * 转成GTM8时区
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date convertToGTM8(Date date) throws ParseException {

		return CommonUtil.convertToZone(date, "Asia/Shanghai");
	}

	public static String setSysDatetime(Date date) throws IOException, InterruptedException {

		Date st = new Date();
		String str = CommonUtil.formatTime(date, "yyyy-MM-dd HH:mm:ss");
		// System.out.println("date -s \"" + str + "\"");
		String result = executeSysCmd(new String[] { "date", "-s", str });
		CommonUtil.sleep(10);
		result = executeSysCmd(new String[] { "hwclock", "-w" });
		if (!result.isEmpty()) {

			return result;
		}

		System.out.println("set date spend " + (System.currentTimeMillis() - st.getTime()) / 1000);

		return result;
	}

	public static Date getSysDatetime() throws IOException, InterruptedException, ParseException {

		String[] cmd = new String[] { "date", "+%Y-%m-%d %H:%M:%S" };
		String result = executeSysCmd(cmd);
		return CommonUtil.parseTime(result, "yyyy-MM-dd HH:mm:ss");

	}
	
	public static String getMacAddress() throws IOException, InterruptedException {
		
		String[] cmd = new String[] { "cat", "/sys/class/net/eth1/address" };
		String result = executeSysCmd(cmd);
		return result.trim().toUpperCase();
	}
	

	public static boolean isLinuxEnvironment() {

		String os = System.getProperty("os.name");
		if (os.toLowerCase().startsWith("linux")) {

			return true;
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
			Environment.infoLogger.info("current process id :" + processId);
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

		MainBoard.startupCfg.saveIpAddress(address);

		return true;
	}
	
	/**
	 * 清空日志
	 * @author  wavy_zheng
	 * 2021年6月5日
	 * @throws Exception
	 */
	public static void clearLogs() throws Exception {
		
		if(isLinuxEnvironment()) {
			
		    executeSysCmd("rm" , "-rf" ,"~/log/*");
		    
		}
		
	}
	/**
	 * 检查主控硬盘使用比例,0-1
	 * @author  wavy_zheng
	 * 2021年6月15日
	 * @return
	 * @throws InterruptedException 
	 * @throws Exception 
	 */
	public static double detectDiskUsedRate() throws Exception {
		
		if(isLinuxEnvironment()) {
			
			String response = executeSysCmd("df");
			String[] secs   = response.split("\n");
			for(String sec : secs) {
				
				if(sec.contains("/dev/mmcb")) {
					
					String[] datas = sec.split(" ");
					for(String data : datas) {
						
						int index = -1;
						if((index = data.indexOf("%")) >= 0) {
							
							return Double.parseDouble(data.substring(0, index));
						}
						
					}
					
				}
			}
		}
		
		return 0;
		
	}
	
	
	public static void reboot() throws IOException, InterruptedException {
		
		if(isLinuxEnvironment()) {
		   executeSysCmd("reboot");
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
	 * 获取主控核心状态数据
	 * 
	 * @return
	 */
	public static CoreData getCoreData() throws AlertException {

		CoreData coreData = new CoreData();
		if (Environment.isLinuxEnvironment()) {
			try {

				String response = Environment.executeSysCmd(new String[] { "top", "-b", "-n", "1" });

				String[] lines = response.split("\n");
				if (lines.length < 3) {

					// throw new AlertException(AlertCode.LOGIC, "解析主控状态数据错误!");
					throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.MainStateDataParsingError));
				}

				coreData = parseMemoryLineString(lines[0], coreData);

				//coreData = parseCpuLineString(lines[1], coreData);

				coreData = parseLoadAverageLineString(lines[2], coreData);
				
				//软件版本
				
				coreData.setMacAddress(Environment.getMacAddress());
				
				

			} catch (IOException e) {

				e.printStackTrace();
//				throw new AlertException(AlertCode.COMM_ERROR, "获取主控状态失败:执行IO命令错误");
				throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.MainStateGetError_IO));

			} catch (InterruptedException e) {

				e.printStackTrace();
//				throw new AlertException(AlertCode.COMM_ERROR, "获取主控状态失败:执行命令被中断");
				throw new AlertException(AlertCode.COMM_ERROR, I18N.getVal(I18N.MainStateGetError_CmdInterrupt));
			}
		} else {

			// 使用虚拟数据模拟主控
			double sysCpu = (double) new Random().nextInt(200) / 10;
			double userCpu = (double) new Random().nextInt(400) / 10;

			
			coreData.setMemory(508);

			// java虚拟内存
			double val = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
					/ (1024 * 1024);
			coreData.setMemoryUsed(val + 70);
			coreData.setMemoryFree(coreData.getMemory() - val - 70);

//			Environment.infoLogger.info("heap total memory:" + (double)Runtime.getRuntime().totalMemory() / (1024 * 1024) + "mb");
//			Environment.infoLogger.info("used total memory:" + val);

			coreData.setLoadAverage5(new Random().nextDouble() * 2);
			coreData.setLoadAverage10(new Random().nextDouble() * 2);
			coreData.setLoadAverage15(new Random().nextDouble() * 2);
			coreData.setMacAddress("00:00:00:00:00:00");
			
			

		}
		return coreData;
	}

	/**
	 * 解析内存字符串
	 * 
	 * @param line
	 * @param coreData
	 * @return
	 */
	private static CoreData parseMemoryLineString(String line, CoreData coreData) {

		String pattern = "^Mem:\\s+(\\d+)K\\s+used,\\s+(\\d+)K\\s+free,[\\s\\S]*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);
		if (m.find()) {

			double memUsed = Double.parseDouble(m.group(1));
			double memFree = Double.parseDouble(m.group(2));
			coreData.setMemoryUsed(memUsed / 1024);
			coreData.setMemoryFree(memFree / 1024);
			coreData.setMemory((memUsed + memFree) / 1024);

			// java虚拟内存
			double val = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
					/ (1024 * 1024);
			coreData.setMemoryUsed(val + 70);
			coreData.setMemoryFree(coreData.getMemory() - val - 70);

//			Environment.infoLogger.info("heap total memory:" + (double)Runtime.getRuntime().totalMemory() / (1024 * 1024) + "mb");
//			Environment.infoLogger.info("used total memory:" + val);
		}

		return coreData;
	}

	/**
	 * 解析cpu使用率信息
	 * 
	 * @param line
	 * @param coreData
	 * @return
	 */
	private static CoreData parseCpuLineString(String line, CoreData coreData) {
		//
		String pattern = "^CPU:\\s+(\\d+\\.\\d+)%\\s+usr\\s+(\\d+\\.\\d)%\\s+sys\\s+(\\d+\\.\\d+)%\\s+nic\\s+(\\d+\\.\\d+)%\\s+idle[\\s\\S]*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);
		if (m.find()) {

			// System.out.println("usr:" + m.group(1));
			// System.out.println("sys:" + m.group(2));
			// System.out.println("nic:" + m.group(3));
			// System.out.println("idle:" + m.group(4));
//			double usr = Double.parseDouble(m.group(1));
//			double sys = Double.parseDouble(m.group(2));
//			double idle = Double.parseDouble(m.group(4));
//			coreData.setUserCpu(usr);
//			coreData.setSysCpu(sys);
//			coreData.setTotalCpu(100 - idle);

		} else {

			//System.out.println("match failed");
		}
		return coreData;
	}

	/**
	 * 解析cpu平均负载率
	 * 
	 * @param line
	 * @param coreData
	 * @return
	 */
	private static CoreData parseLoadAverageLineString(String line, CoreData coreData) {
		//
		String pattern = "^Load\\s+average:\\s+(\\d+\\.\\d+)\\s+(\\d+\\.\\d+)\\s+(\\d+\\.\\d+)[\\s\\S]*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);
		if (m.find()) {

			// System.out.println("load average5:" + m.group(1));
			// System.out.println("load average10:" + m.group(2));
			// System.out.println("load average15:" + m.group(3));
			coreData.setLoadAverage5(Double.parseDouble(m.group(1)));
			coreData.setLoadAverage10(Double.parseDouble(m.group(2)));
			coreData.setLoadAverage15(Double.parseDouble(m.group(3)));

		} else {

			//System.out.println("match failed");
		}
		return coreData;
	}

	/**
	 * 执行linux系统命令
	 * 
	 * @param cmd
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String executeSysCmd(String ... cmd) throws IOException, InterruptedException {

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

	@Deprecated
	public static void executeLinuxCmd(String cmd) {
		System.out.println("got cmd job : " + cmd);
		Runtime run = Runtime.getRuntime();
		try {
			Process process = run.exec(cmd);
			InputStream in = process.getInputStream();
			BufferedReader bs = new BufferedReader(new InputStreamReader(in));
			// System.out.println("[check] now size \n"+bs.readLine());
			StringBuffer out = new StringBuffer();
			byte[] b = new byte[8192];
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			System.out.println("job result [" + out.toString() + "]");
			in.close();
			// process.waitFor();
			process.destroy();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 控制分区消息监听器
	 * 
	 * @author Administrator
	 *
	 */
	public interface ControlUnitListener {

		/**
		 * 流程测试完毕
		 */
		void procedureComplete(ControlUnit cu) throws AlertException;

		/**
		 * 流程测试被停止
		 */
		void procedureStoped(ControlUnit cu) throws AlertException;

		/**
		 * 流程测试被暂停
		 */
		void procedurePaused(ControlUnit cu) throws AlertException;

		/**
		 * 流程测试被启动
		 */
		void procedureStarted(ControlUnit cu) throws AlertException;

		/**
		 * 流程测试被恢复
		 */
		void procedureResumed(ControlUnit cu) throws AlertException;

		/**
		 * 流程测试被异常终止
		 */
		void procedureInterrupt(ControlUnit cu) throws AlertException;

	}
}
