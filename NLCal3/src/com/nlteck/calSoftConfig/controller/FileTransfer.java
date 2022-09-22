package com.nlteck.calSoftConfig.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.net.telnet.TelnetClient;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;

import com.nltecklib.utils.BaseUtil;
import com.nltecklib.utils.TelnetUtil;

/**
 * @description 文件传输者: 通过Telnet与Ftp与主板交换文件
 * @author zemin_zhu
 * @dateTime Jun 23, 2022 10:20:05 AM
 */
public class FileTransfer extends Controller {

    private TelnetClient telnetClient = new TelnetClient();
    protected FtpConnector ftpConnector;
    private static FileTransfer instance;
    private DisposeListener disposeListener;

    public static FileTransfer getInstance() {
	if (instance == null) {
	    instance = new FileTransfer();
	}
	return instance;
    }

    public DisposeListener getDisposeListener() {
	if (disposeListener == null) {
	    disposeListener = new DisposeListener() {

		@Override
		public void widgetDisposed(DisposeEvent e) {
		    
		    disconnect();
		}

	    };
	}
	return disposeListener;
    }

    private final static String FTP_PATH = "/var/ftp/pub";

    public final static String USER = "root";
    public final static String PASSWORD = "";

    protected String coreFileName; // 主控程序名
    protected String cfgFileName; // 主控配置文件名

    public String telnetRespSymbol = ":";

    private String ip;

    public enum UpdateType {

	/**
	 * ENV 升级环境 PROGRAM 升级主控程序 , IP: 升级IP文件
	 */
	ENV, PROGRAM, IP;
    }

    public FileTransfer() {
	ftpConnector = new FtpConnector();
	ftpConnector.addListener(new Listener() {

	    @Override
	    public void onLogEvent(Controller sender, LogEventArgs logEventArgs) {
		
		notifyLogEvent(logEventArgs);
	    }
	});
    }

    /**
     * 连接主机
     * 
     * @param ipAddress
     * @throws Exception
     */
    public void connect(String ipAddress) throws Exception {
	notifyLogEvent("connect telnet server: " + ipAddress);
	telnetClient.connect(ipAddress);
	this.ip = ipAddress;
	TelnetUtil.readUntil("login:", telnetClient.getInputStream());
	ftpConnector.connect(ipAddress);
    }

    /**
     * 以root身份登录主控linux系统
     * 
     * @param password
     * @return
     */
    public boolean login(String username, String password) {

	notifyLogEvent("login telnet server: " + username);

	TelnetUtil.writeUtil(username, telnetClient.getOutputStream());
	String response = TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());

	if (!"root".equals(username) && response.indexOf("Password:") >= 0) {

	    TelnetUtil.writeUtil(password, telnetClient.getOutputStream());
	    response = TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());
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
	String pos = TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());
	return pos;
    }

    /**
     * 列举文件夹
     * 
     * @return
     */
    public String list() {

	TelnetUtil.writeUtil("ls", telnetClient.getOutputStream());
	String response = TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());
	notifyLogEvent(response);
	return response;
    }

    /**
     * 修改当前linux系统的当前位置
     * 
     * @param dir
     */
    public void changeDir(String dir) {

	TelnetUtil.writeUtil("cd " + dir, telnetClient.getOutputStream());
	notifyLogEvent(TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream()));
    }

    public void removeFileInDir(String dir, String fileName) {

	changeDir(dir);
	TelnetUtil.writeUtil("rm -f " + fileName, telnetClient.getOutputStream());
	BaseUtil.sleep(100);
	notifyLogEvent(TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream()));
    }

    public void removeFilesInDir(String dir) throws Exception {

	changeDir(dir);
	TelnetUtil.writeUtil("rm -f *", telnetClient.getOutputStream());
	notifyLogEvent(TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream()));
	sleep(100);

    }

    /**
     * 修改目标权限
     * 
     * @param privilege
     * @param dest
     */
    public void changePrivilege(String privilege, String dest) {

	TelnetUtil.writeUtil("chmod -R " + privilege + " " + dest, telnetClient.getOutputStream());
	String response = TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());
	notifyLogEvent(response);
    }

    /**
     * 重启系统
     */
    public void reboot() {

	TelnetUtil.writeUtil("reboot", telnetClient.getOutputStream());
	notifyLogEvent(TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream()));
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

	    throw new Exception(String.format("文件不存在: %s", file.getName()));
	}

	// 将文件拷贝到ftp server
	try {
	    if (!ftpConnector.upload("pub", filename, new FileInputStream(file))) {

		throw new Exception(String.format("上传文件失败: %s", file.getName()));
	    }
	    notifyLogEvent("upload file " + file.getName() + " success");
	} catch (FileNotFoundException e) {

	    throw e;

	}

    }

    /**
     * @description 上传文件到主板
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 11:28:44 AM
     */
    public void upload(String localPath, String remotePath) throws Exception {
	// 修改默认ftp路径权限777，否则无法上传
	changePrivilege("777", "/var/ftp/pub");
	// 清空文件夹
	removeFilesInDir("/var/ftp/pub");
	// 检测文件是否存在
	File file = new File(localPath);
	// 将文件拷贝到ftp server
	if (!ftpConnector.upload("pub", file.getName(), new FileInputStream(file))) {
	    throw new Exception(String.format("上传文件失败: %s", file.getName()));
	}
	notifyLogEvent("upload file " + file.getName() + " success");

	changeDir(FTP_PATH);

	// 确保指定路径中, 所有文件夹存在
	ensureFolderExist(remotePath);

	// 升级配置文件
	notifyLogEvent("move file: " + remotePath);
//		TelnetUtil.writeUtil("mv -f " + file.getName() + " " + CFG_FILE, telnetClient.getOutputStream());
	TelnetUtil.writeUtil("mv -f " + FTP_PATH + "/" + file.getName() + " " + remotePath,
		telnetClient.getOutputStream());
	TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());

    }

    /**
     * @description 确保指定路径中, 所有文件夹存在
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 11:42:07 AM
     */
    public void ensureFolderExist(String path) {
	String[] pathArr = path.split("/");
	String folderPath = "";
	for (int i = 0; i < pathArr.length - 1; i++) {
	    folderPath += pathArr[i] + "/";
	    notifyLogEvent("mkdir " + folderPath);
	    TelnetUtil.writeUtil("mkdir " + folderPath, telnetClient.getOutputStream());
	    TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());
	}
    }

    public void disconnect() {
	try {
	    if (telnetClient.isConnected()) {
		telnetClient.disconnect();
	    }
	    if (ftpConnector.isConnected()) {
		ftpConnector.disconnect();
	    }
	} catch (Exception e) {
	   
	    notifyErrorLogEvent(e);
	}
    }

    /**
     * @description 若telnet会话未建立, 则连接并登陆
     * @author zemin_zhu
     * @dateTime Jun 13, 2022 8:40:36 PM
     */
    public void connectAndLogin(String ip) throws Exception {
	connect(ip);
	boolean logined = login(USER, PASSWORD);
	if (!logined) {
	    throw new Exception("Telnet服务器登录失败: " + ip);
	}
    }

    /**
     * @description telnet会话是否已建立
     * @author zemin_zhu
     * @dateTime Jun 13, 2022 8:41:06 PM
     */
    public boolean isConnected() {
	return telnetClient.isConnected();
    }

    /**
     * @description 下载文件到计算机
     * @author zemin_zhu
     * @dateTime Jun 14, 2022 9:03:38 AM
     */
    public void download(String remotePath, String localPath) throws Exception {
	notifyLogEvent("download file: " + remotePath);
	TelnetUtil.writeUtil("cp -f " + remotePath + " " + FTP_PATH, telnetClient.getOutputStream());
	TelnetUtil.readUntil(telnetRespSymbol, telnetClient.getInputStream());
	String[] filePathArr = remotePath.split("/");
	String fileName = filePathArr[filePathArr.length - 1];
	if (!ftpConnector.download("pub/", fileName, localPath)) {
	    throw new Exception(String.format("下载文件失败: %s", fileName));
	}
	notifyLogEvent("download file " + fileName + " success");
    }
}
