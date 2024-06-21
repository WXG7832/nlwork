package com.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * telnet�ն˹���
 * @author Administrator
 *
 */
public class TelnetUtil {
    
	
	/**
	 * ����ָ��λ��,�������¶�
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
					// ��ƴ�ӵ��ַ�����ָ�����ַ�����βʱ,���ڼ�����
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
	 * д�������
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


}
