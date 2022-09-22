package com.nltecklib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

import com.nltecklib.protocol.modbus.tcp.util.CommonUtil;

/**
 * 主控升级工具类 支持exefileName 和 cfgFileName 的可配置，以匹配各种平台的升级
 * 
 * @author Administrator
 *
 */
public class UpdateUtil {

	private TelnetClient telnetClient = new TelnetClient();

	private final static String FTP_PATH = "/var/ftp/pub";
	private final static String PROGRAM_FILE = "mainboard.jar";
	// private final static String CFG_FILE = "cfg.xml";
	private final static String PROFILE_FILE = "profile";
	private final static String LOGIN_FILE = "login.sh";
	private final static String INITTAB_FILE = "inittab";
	private final static String INTERFACES_FILE = "interfaces";

	private final static String MANUALBOOT_PATH = "files/manualboot";
	private final static String AUTOBOOT_PATH = "files/autoboot";
	
	private final static String NETWORK_PATH = "/etc/network/interfaces";

	private String exeFileName;
	private String cfgFileName;

	private String ip;

	public enum UpdateType {

		/**
		 * ENV 升级环境 PROGRAM 升级主控程序 , IP: 升级IP文件
		 */
		ENV, PROGRAM, IP;
	}

	/**
	 * 连接主机
	 * 
	 * @param ipAddress
	 * @throws Exception
	 */
	public void connect(String ipAddress) throws Exception {

		telnetClient.connect(ipAddress);
		this.ip = ipAddress;
		TelnetUtil.readUntil("login:", telnetClient.getInputStream());

	}

	public String getExeFileName() {
		return exeFileName;
	}

	public void setExeFileName(String exeFileName) {
		this.exeFileName = exeFileName;
	}

	public String getCfgFileName() {
		return cfgFileName;
	}

	public void setCfgFileName(String cfgFileName) {
		this.cfgFileName = cfgFileName;
	}

	/**
	 * 检测本地文件是否满足升级要求
	 * 
	 * @param type
	 * @param autoboot
	 * @param localDirPath
	 * @return
	 * @throws Exception
	 */
	private List<File> checkLocalFilesExists(UpdateType type, boolean autoboot, String localDirPath) throws Exception {

		File dir = null;
		dir = new File(localDirPath);
		if (!dir.exists()) {

			return null;
		}

		List<File> files = new ArrayList<File>();
		String[] filenames = dir.list();
		List<String> filenameList = Arrays.asList(filenames);

		if (type == UpdateType.PROGRAM) {
			if (!filenameList.contains(exeFileName)) {

				throw new Exception("丢失文件:" + exeFileName);
			}
			files.add(new File(dir + "/" + exeFileName));

			if (!filenameList.contains(cfgFileName)) {

				throw new Exception("丢失文件:" + cfgFileName);
			}
			files.add(new File(dir + "/" + cfgFileName));
		} else if (type == UpdateType.ENV) {

			if (!filenameList.contains(PROFILE_FILE)) {

				throw new Exception("丢失文件:" + PROFILE_FILE);
			}
			files.add(new File(dir + "/" + PROFILE_FILE));
			if (!filenameList.contains(INITTAB_FILE)) {

				throw new Exception("丢失文件:" + INITTAB_FILE);
			}
			files.add(new File(dir + "/" + INITTAB_FILE));
			if (autoboot) {
				if (!filenameList.contains(LOGIN_FILE)) {

					throw new Exception("丢失文件:" + LOGIN_FILE);
				}
				files.add(new File(dir + "/" + LOGIN_FILE));
			}
		}

		return files;
	}

	/**
	 * 以root身份登录主控linux系统
	 * 
	 * @param password
	 * @return
	 */
	public boolean login(String username, String password) {

		TelnetUtil.writeUtil(username, telnetClient.getOutputStream());
		String response = TelnetUtil.readUntil(":", telnetClient.getInputStream());

		if (!"root".equals(username) && response.indexOf("Password:") >= 0) {

			TelnetUtil.writeUtil(password, telnetClient.getOutputStream());
			response = TelnetUtil.readUntil(":", telnetClient.getInputStream());
		}
		if (response.indexOf("incorrect") >= 0) {

			return false;
		}
		return true;
	}

	/**
	 * 当前位置
	 * 
	 * @return
	 */
	public String getPosition() {

		TelnetUtil.writeUtil("pwd", telnetClient.getOutputStream());
		String pos = TelnetUtil.readUntil(":", telnetClient.getInputStream());
		return pos;
	}
	/**
	 * 直接通过telnet协议修改主控的IP地址
	 * @author  wavy_zheng
	 * 2021年9月18日
	 * @param path
	 * @param ip
	 * @throws Exception
	 */
	public void configIpAddress(InputStream ios ) throws Exception {
		
		if (ip == null) {

			throw new Exception("请先连接" + ip);
		}
		// 修改默认ftp路径权限777，否则无法上传
		changePrivilege("777", "/var/ftp/pub");
		// 清空文件夹
		removeFilesInDir("/var/ftp/pub");
		
		// 将文件拷贝到ftp server
		if (!FtpUtil.uploadFile(ip, 21, "ftp", "", "pub", INTERFACES_FILE, ios)) {

			throw new Exception("上传文件" + INTERFACES_FILE + "到ftp服务器失败!");
		}
		System.out.println("upload file " + INTERFACES_FILE + " success");
		
		
		changeDir(FTP_PATH);
		
		//将interfaces文件mv到/etc/network/interfaces
		TelnetUtil.writeUtil("mv -f " + INTERFACES_FILE + " " + NETWORK_PATH, telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());
		
	}

	/**
	 * 是否配置成自启动程序
	 * 
	 * @param autoboot
	 *            true自启动配置 false 人工启动
	 * @throws UIException
	 */
	public void configAutoBoot(boolean autoboot, String localDirPath) throws Exception {

		if (ip == null) {

			throw new Exception("请先连接" + ip);
		}
		// 修改默认ftp路径权限777，否则无法上传
		changePrivilege("777", "/var/ftp/pub");
		// 清空文件夹
		removeFilesInDir("/var/ftp/pub");

		// 检测文件是否存在
		List<File> filesUpload = checkLocalFilesExists(UpdateType.ENV, autoboot, localDirPath);
		for (File file : filesUpload) {

			// 将文件拷贝到ftp server
			try {
				if (!FtpUtil.uploadFile(ip, 21, "ftp", "", "pub", file.getName(), new FileInputStream(file))) {

					throw new Exception("上传文件" + file.getName() + "到ftp服务器失败!");
				}
				System.out.println("upload file " + file.getName() + " success");
			} catch (FileNotFoundException e) {

			}
		}
		// 查看是否存在profile文件
		changeDir(FTP_PATH);

		System.out.println("upgrade profile..");
		TelnetUtil.writeUtil("mv -f " + PROFILE_FILE + " /etc", telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());

		System.out.println("upgrade " + INITTAB_FILE);
		TelnetUtil.writeUtil("mv -f " + INITTAB_FILE + " /etc", telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());

		if (autoboot) {

			System.out.println("upgrade " + LOGIN_FILE);
			changePrivilege("777", LOGIN_FILE);
			TelnetUtil.writeUtil("mv -f " + LOGIN_FILE + " /bin", telnetClient.getOutputStream());
			TelnetUtil.readUntil(":", telnetClient.getInputStream());
		}

	}

	/**
	 * 列举文件夹
	 * 
	 * @return
	 */
	public String list() {

		TelnetUtil.writeUtil("ls", telnetClient.getOutputStream());
		String response = TelnetUtil.readUntil(":", telnetClient.getInputStream());
		System.out.println(response);
		return response;
	}

	/**
	 * 修改当前linux系统的当前位置
	 * 
	 * @param dir
	 */
	public void changeDir(String dir) {

		TelnetUtil.writeUtil("cd " + dir, telnetClient.getOutputStream());
		System.out.println(TelnetUtil.readUntil(":", telnetClient.getInputStream()));
	}

	public void removeFileInDir(String dir, String fileName) {

		changeDir(dir);
		TelnetUtil.writeUtil("rm -f " + fileName, telnetClient.getOutputStream());
		BaseUtil.sleep(100);
		System.out.println(TelnetUtil.readUntil(":", telnetClient.getInputStream()));
	}

	public void removeFilesInDir(String dir) {

		changeDir(dir);
		TelnetUtil.writeUtil("rm -f *", telnetClient.getOutputStream());
		BaseUtil.sleep(100);
		System.out.println(TelnetUtil.readUntil(":", telnetClient.getInputStream()));

	}

	/**
	 * 修改目标权限
	 * 
	 * @param privilege
	 * @param dest
	 */
	public void changePrivilege(String privilege, String dest) {

		TelnetUtil.writeUtil("chmod -R " + privilege + " " + dest, telnetClient.getOutputStream());
		System.out.println(TelnetUtil.readUntil(":", telnetClient.getInputStream()));
	}

	/**
	 * 升级主控程序
	 * 
	 * @author wavy_zheng 2020年6月23日
	 * @param exeFileName
	 */
	public void upgradeCoreProgram(String exeFileName , String destFileName ,  boolean reboot) {

		changeDir(FTP_PATH);

		// 升级主控程序
		System.out.println("upgrade program");
		TelnetUtil.writeUtil("mv -f " + exeFileName + " ~/" +  destFileName , telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());
        CommonUtil.sleep(2000);
		if(reboot) {
		   reboot();
		}

	}

	public void upgradeCoreCfgFile(String cfgFileName,String destFileName , boolean reboot) {

		// 创建config文件夹
		System.out.println("mkdir config");
		TelnetUtil.writeUtil("mkdir ~/config", telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());
		
		changeDir(FTP_PATH);

		// 升级配置文件
		System.out.println("upgrade " + cfgFileName);
		TelnetUtil.writeUtil("mv -f " + cfgFileName + " ~/config/" + destFileName, telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());
		
		if(reboot) {
			
			reboot();
		}

	}

	/**
	 * 重启系统
	 */
	public void reboot() {

		TelnetUtil.writeUtil("reboot", telnetClient.getOutputStream());
		System.out.println(TelnetUtil.readUntil(":", telnetClient.getInputStream()));
	}

	/**
	 * 将当前本地文件传输到ftp://ip/pub
	 * 
	 * @author wavy_zheng 2020年1月4日
	 * @param localFilePath
	 * @throws Exception
	 */
	public void upgradeFile(String localFilePath, String destFileName) throws Exception {

		if (ip == null) {

			throw new Exception("请先连接" + ip);
		}
		File file = new File(localFilePath);
		if (!file.exists()) {

			throw new Exception(file.getAbsolutePath() + "不存在!");
		}
		// 修改默认ftp路径权限777，否则无法上传
		changePrivilege("777", FTP_PATH);
		// 删除
		removeFileInDir(FTP_PATH, destFileName);
		if (!FtpUtil.uploadFile(ip, 21, "ftp", "", "pub", destFileName, new FileInputStream(file))) {

			throw new Exception("上传文件" + file.getName() + "到ftp服务器失败!");
		}

	}

	/**
	 * 升级主控程序和配置文件
	 * 
	 * @throws Exception
	 */
	public void upgradeProgram(String localDirPath) throws Exception {

		if (ip == null) {

			throw new Exception("请先连接" + ip);
		}
		// 修改默认ftp路径权限777，否则无法上传
		changePrivilege("777", "/var/ftp/pub");
		// 清空文件夹
		removeFilesInDir("/var/ftp/pub");
		// 检测文件是否存在
		List<File> filesUpload = checkLocalFilesExists(UpdateType.PROGRAM, true, localDirPath);
		for (File file : filesUpload) {

			// 将文件拷贝到ftp server
			try {
				if (!FtpUtil.uploadFile(ip, 21, "ftp", "", "pub", file.getName(), new FileInputStream(file))) {

					throw new Exception("上传文件" + file.getName() + "到ftp服务器失败!");
				}
				System.out.println("upload file " + file.getName() + " success");

			} catch (FileNotFoundException e) {

			}
		}

		changeDir(FTP_PATH);

		// 升级主控程序
		System.out.println("upgrade program");
		TelnetUtil.writeUtil("mv -f " + exeFileName + " ~", telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());

		// 创建config文件夹
		System.out.println("mkdir config");
		TelnetUtil.writeUtil("mkdir ~/config", telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());

		// 升级配置文件
		System.out.println("upgrade cfg.xml");
		TelnetUtil.writeUtil("mv -f " + cfgFileName + " ~/config", telnetClient.getOutputStream());
		TelnetUtil.readUntil(":", telnetClient.getInputStream());

	}

	public void disconnect() throws IOException {

		telnetClient.disconnect();
	}
	
	

}
