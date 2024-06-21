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
 * FTP������
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
			// �������Ĭ�϶˿ڣ�����ʹ��ftp.connect(url)�ķ�ʽֱ������FTP������
			ftpClient.connect(url);
			// ftp.connect(url, port);// ����FTP������
			// ��¼
			ftpClient.login(username, password);
			
			ftpClient.setControlEncoding("utf-8");
			ftpClient.enterLocalPassiveMode();
			// �����Ƿ����ӳɹ�
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				System.out.println("����ʧ��");
				ftpClient.disconnect();
				return result;
			}

			// ת�ƹ���Ŀ¼��ָ��Ŀ¼��
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
	 * ��ftp����������ָ�����ļ�
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
			ftpClient.connect(url); // Ĭ������
			ftpClient.login(username, password);// ��¼
			
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); // �����ļ����䷽ʽ
			reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {

				ftpClient.disconnect();
				System.err.println("FTP server refused connection.");
				return result;
			}
			// ת�Ƶ�FTP������Ŀ¼��ָ����Ŀ¼��
			// 
			boolean changeResult = ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(encoding),"iso-8859-1"));
			System.out.println("change working directory " + changeResult);
			ftpClient.enterLocalPassiveMode();
			FTPFile[] fs = ftpClient.listFiles(); // ��ȡ�����ļ��б�
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
			// �˳�
			ftpClient.logout();
			if (downloadResult) {
				result = true; // ���سɹ�
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
