package com.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * FTP工具类
 * 
 * @author Administrator
 *
 */
public class FtpUtil {

	private static FTPClient ftpClient = new FTPClient();
	private static String encoding = System.getProperty("file.encoding");

	public static boolean uploadFile(String url, int port, String username, String password, String path,
			String filename, InputStream input) {

		boolean result = false;
		try {
			int reply;
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftpClient.connect(url);
			// ftp.connect(url, port);// 连接FTP服务器
			// 登录
			ftpClient.login(username, password);
			
			ftpClient.setControlEncoding("utf-8");
			ftpClient.enterLocalPassiveMode();
			// 检验是否连接成功
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				System.out.println("连接失败");
				ftpClient.disconnect();
				return result;
			}

			// 转移工作目录至指定目录下
			boolean change = ftpClient.changeWorkingDirectory(path);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			if (change) {
				
				
				result = ftpClient.storeFile(new String(filename.getBytes("utf-8"), "iso-8859-1"), input);
				
			}
			input.close();
			ftpClient.logout();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException ioe) {

				}
			}
		}
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
	public static boolean downFile(String url, int port, String username, String password, String remotePath,
			String fileName, String localPath) {

		boolean result = false;

		try {
			int reply;
			ftpClient.setControlEncoding(encoding);
			ftpClient.connect(url); // 默认连接
			ftpClient.login(username, password);// 登录
			
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置文件传输方式
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {

				ftpClient.disconnect();
				System.err.println("FTP server refused connection.");
				return result;
			}
			// 转移到FTP服务器目录至指定的目录下
			// 
			boolean changeResult = ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(encoding),"iso-8859-1"));
			System.out.println("change working directory " + changeResult);
			ftpClient.enterLocalPassiveMode();
			FTPFile[] fs = ftpClient.listFiles(); // 获取下载文件列表
			boolean downloadResult = true;
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					File localFile = new File(localPath + "/" + ff.getName());
					OutputStream is = new FileOutputStream(localFile);
					System.out.println("ftpClient connect state = " + ftpClient.isConnected());
					//new String(ff.getName().getBytes(encoding),"iso-8859-1")
					boolean b = ftpClient.retrieveFile(ff.getName(), is);

					System.out.println("download file " + b);
					is.close();
					if (!b) {

						downloadResult = false;
						break;
					}

				}
			}
			if(fs.length == 0) {
				
				downloadResult = false;
			}
			// 退出
			ftpClient.logout();
			if (downloadResult) {
				result = true; // 下载成功
			}

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			if (ftpClient.isConnected()) {

				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;

	}

}
