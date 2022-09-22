package com.nlteck.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.naming.Context;

import org.apache.commons.net.telnet.TelnetClient;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nltecklib.utils.TelnetUtil;

/**
 * 命令工具
 * 
 * @author Administrator xingguo_wang
 */
public class TelnetOperateUtil {
	TelnetClient telnetClient = new TelnetClient();
	private String ip;
	private String endstr="";

	/**
	 * 读到指定位置,不在向下读
	 * 
	 * @param endFlag
	 * @param in
	 * @return
	 * 
	 */
	public static String readUntil(String endFlag, InputStream in) {

		InputStreamReader isr = new InputStreamReader(in);

		char[] charBytes = new char[1024];
		int n = 0;
		boolean flag = false;
		String str = "";
		try {
			while ((n = isr.read(charBytes)) != -1) {
				for (int i = 0; i < n; i++) {
					char c = (char) charBytes[i];
					str += c;
					// 当拼接的字符串以指定的字符串结尾时,不在继续读
					if (str.endsWith(endFlag)) {
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String result = null;
		try {
			result = new String(str.getBytes("iso-8859-1"), System.getProperty("file.encoding"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

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

	/**
	 * 以root身份登录主控linux系统
	 * 
	 * @param password
	 * @return
	 */
	public boolean login(String username, String password) {

		TelnetUtil.writeUtil(username, telnetClient.getOutputStream());
		String response = TelnetUtil.readUntil(":", telnetClient.getInputStream());

		if (!"root".equals(username) || response.indexOf("Password:") >= 0) {

			TelnetUtil.writeUtil(password, telnetClient.getOutputStream());
			response = TelnetUtil.readUntil(":", telnetClient.getInputStream());
		}

		if (response.indexOf("incorrect") >= 0) {

			return false;
		}
		return true;
	}

	public void changeDir(String dir) {

		TelnetUtil.writeUtil("cd " + dir, telnetClient.getOutputStream());
		System.out.println(TelnetUtil.readUntil(endstr, telnetClient.getInputStream()));
	}

	/**
	 * 
	 * @param dir
	 * @param fileName
	 * @param to
	 */
	public void cpFile(String dir, String fileName, String to) {
		changeDir(dir);
		TelnetUtil.writeUtil("cp -f " + fileName + " " + to, telnetClient.getOutputStream());
		CommonUtil.sleep(100);
		System.out.println(TelnetUtil.readUntil(endstr, telnetClient.getInputStream()));
	}

	/**
	 * 修改目标权限
	 * 
	 * @param privilege
	 * @param dest
	 */
	public void changePrivilege(String privilege, String dest) {

		TelnetUtil.writeUtil("chmod -R " + privilege + " " + dest, telnetClient.getOutputStream());
		System.out.println(TelnetUtil.readUntil(endstr, telnetClient.getInputStream()));
	}

	public void removeFilesInDir(String dir) {

		changeDir(dir);
		TelnetUtil.writeUtil("rm -f *", telnetClient.getOutputStream());
		CommonUtil.sleep(100);
		System.out.println(TelnetUtil.readUntil(endstr, telnetClient.getInputStream()));

	}

	/**
	 * 上传文件
	 * 
	 * @author wavy_zheng 2021年1月11日
	 * @param path
	 * @param filename
	 * @throws Exception
	 */
	public void upgradeFile(String path, String filename) throws Exception {

		// 修改默认ftp路径权限777，否则无法上传
		changePrivilege("777", "/var/ftp/pub");
		// 清空文件夹
		removeFilesInDir("/var/ftp/pub");

		File file = new File(path);

		if (!file.exists()) {

			throw new Exception("file not exist");
		}

		// 将文件拷贝到ftp server
		try {
			if (!FtpUtil.uploadFile(ip, 21, "ftp", "", "pub", filename, new FileInputStream(file))) {

				throw new Exception("file upload error");
			}
			System.out.println("upload file " + file.getName() + " success");
		} catch (FileNotFoundException e) {

			throw e;

		}

	}

	/**
	 * 写入命令方法
	 * 
	 * @param cmd
	 * @param os
	 */
	public static void writeUtil(String cmd, OutputStream os) {
		try {
			cmd = cmd + "\n";
			os.write(cmd.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 重启系统
	 */
	public void reboot() {

		TelnetUtil.writeUtil("reboot", telnetClient.getOutputStream());
		System.out.println(TelnetUtil.readUntil(":", telnetClient.getInputStream()));
	}

}
