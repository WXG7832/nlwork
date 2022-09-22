package com.nlteck.calSoftConfig.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @description FTP客户端
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:55:16 AM
 */
public class FtpConnector extends Controller {

    private FTPClient ftpClient = new FTPClient();
    private String encoding = System.getProperty("file.encoding");
    protected String url;
    protected String username = "ftp";
    protected String password = "";

    /**
     * @description 连接并登陆
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 1:27:31 PM
     */
    public boolean connect(String url, String username, String password) throws Exception {

	notifyLogEvent("connect ftp server: " + url);
	// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
	ftpClient.connect(url);
	// ftp.connect(url, port);// 连接FTP服务器
	// 登录
	boolean result = ftpClient.login(username, password);

	if (!result) {
	    disconnect();
	}
	this.url = url;
	ftpClient.setControlEncoding("utf-8");
	ftpClient.enterLocalPassiveMode();
	return result;
    }

    /**
     * @description 连接并登陆, 使用默认账密
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 1:44:44 PM
     */
    public boolean connect(String url) throws Exception {
	return connect(url, username, password);
    }

    /**
     * @description 断开会话
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 1:23:05 PM
     */
    public void disconnect() throws Exception {
	if (ftpClient.isConnected()) {
	    notifyLogEvent("disconnect ftp server: " + url);
	    // ftpClient.logout();
	    ftpClient.disconnect();
	}
    }

    /**
     * @description 是否已建立会话
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 1:54:08 PM
     */
    public boolean isConnected() {
	return ftpClient.isConnected();
    }

    /**
     * @description 上传文件
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 1:22:02 PM
     */
    public boolean upload(String path, String fileName, InputStream input) throws Exception {

	if (!ftpClient.isConnected()) {
	    throw new Exception("ftp server not connected");
	}
	notifyLogEvent("upload file: " + fileName);
	boolean result = false;

	// 转移工作目录至指定目录下
	boolean change = ftpClient.changeWorkingDirectory(path);
	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	if (change) {

	    result = ftpClient.storeFile(new String(fileName.getBytes("utf-8"), "iso-8859-1"), input);

	}
	input.close();

	return result;
    }

    /**
     * 从ftp服务器下载指定的文件
     * 
     * @param url
     * @param port
     * @param username
     * @param password
     * @param remotePath
     * @param fileName
     * @param localPath
     * @return
     */
    public boolean download(String remotePath, String fileName, String localPath) throws Exception {

	if (!ftpClient.isConnected()) {
	    throw new Exception("ftp server not connected");
	}
	notifyLogEvent("download file: " + fileName);
	boolean result = false;

	ftpClient.setControlEncoding(encoding);

	ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件传输方式
	// 转移到FTP服务器目录至指定的目录下
	//
	boolean changeResult = ftpClient
		.changeWorkingDirectory(new String(remotePath.getBytes(encoding), "iso-8859-1"));
	notifyLogEvent("change working directory: " + remotePath);
	if (!changeResult) {
	    throw new Exception("change working directory failed:" + remotePath);
	}
	ftpClient.enterLocalPassiveMode();
	FTPFile[] fs = ftpClient.listFiles(); // 获取下载文件列表
	boolean downloadResult = true;
	for (FTPFile ff : fs) {
	    if (ff.getName().equals(fileName)) {
		File localFile = new File(localPath + "/" + ff.getName());
		OutputStream is = new FileOutputStream(localFile);
		// new String(ff.getName().getBytes(encoding),"iso-8859-1")
		boolean b = ftpClient.retrieveFile(ff.getName(), is);

		is.close();
		if (!b) {

		    downloadResult = false;
		    break;
		}

	    }
	}
	if (fs.length == 0) {

	    downloadResult = false;
	}
	if (downloadResult) {
	    result = true; // 下载成功
	}

	return result;

    }

}
