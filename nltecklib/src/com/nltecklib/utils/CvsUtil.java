package com.nltecklib.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * cvs格式文件书写器
 * 
 * @author Administrator
 *
 */
public class CvsUtil {

	private boolean append;
	private String filePath;
	private String[] header;
	private List<Object[]> data = new ArrayList<Object[]>();
	private static String spit = ",";

	/**
	 * 使用默认格式，重写文件
	 * 
	 * @param filePath
	 */
	public CvsUtil(String filePath) {

		this(filePath, false);
	}

	/**
	 * 
	 * @param filePath
	 * @param append   是否追加写入文件末尾
	 */
	public CvsUtil(String filePath, boolean append) {

		this.filePath = filePath;
		this.append = append;
	}

	public void setHeader(String[] header) {

		this.header = header;
	}

	public boolean isHaveHeader() {

		return header != null;
	}

	public static void setSpit(String spit) {
		CvsUtil.spit = spit;
	}

	/**
	 * 写入数据到缓存
	 * 
	 * @param records
	 * @throws IOException
	 */
	public synchronized void writeRecord(Object[] records) {

		data.add(records);

	}

	public void flush(String charsetName) {
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		try {
			fo = new FileOutputStream(new File(filePath), append);
			ow = new OutputStreamWriter(fo, charsetName);

			for (int n = 0; n < data.size(); n++) {

				Object[] record = data.get(n);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < record.length; i++) {

					sb.append(record[i]);
					if (i < record.length - 1) {
						sb.append(spit);
					}
				}
				ow.write(sb.toString());
				ow.write("\n");
			}
			ow.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (ow != null) {
					ow.close();
				}

				if (fo != null) {
					fo.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void flush() {

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(new File(filePath), append);
			bw = new BufferedWriter(fw);
			for (int n = 0; n < data.size(); n++) {

				Object[] record = data.get(n);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < record.length; i++) {

					sb.append(record[i]);
					if (i < record.length - 1) {
						sb.append(spit);
					}
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			try {

				if (fw != null) {

					fw.close();
				}

				if (bw != null) {

					bw.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void flushCheckHead() throws IOException {

		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(new File(filePath), append);
			bw = new BufferedWriter(fw);

			if (isHaveHeader()) {

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < header.length; i++) {

					sb.append(header[i]);
					if (i < header.length - 1) {
						sb.append(spit);
					}
				}
				bw.write(sb.toString());
				bw.newLine();
			}

			for (int n = 0; n < data.size(); n++) {

				Object[] record = data.get(n);
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < record.length; i++) {

					sb.append(record[i]);
					if (i < record.length - 1) {
						sb.append(spit);
					}
				}
				bw.write(sb.toString());
				bw.newLine();
			}
			bw.flush();
		} finally {

			try {

				if (fw != null) {

					fw.close();
				}

				if (bw != null) {

					bw.close();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 读取记录
	 * 
	 * @return
	 * @throws IOException
	 */
	public synchronized List<String[]> readRecord(String charsetName) {

		List<String[]> buff = new ArrayList<String[]>();
		if (!new File(filePath).exists()) {

			return buff;
		}
		// Environment.errLogger.info("start read record from file");
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {

			fr = new InputStreamReader(new FileInputStream(filePath), charsetName);
			br = new BufferedReader(fr);
			String rec = null;
			while ((rec = br.readLine()) != null) {

				buff.add(rec.split(spit));
			}

			return buff;
		} catch (Throwable e) {

			e.printStackTrace();
			// Environment.errLogger.error(CommonUtil.getThrowableException(e));
			return buff;

		} finally {

			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// Environment.errLogger.info("end read record from file");
		}

	}

	/**
	 * 读取记录
	 * 
	 * @return
	 * @throws IOException
	 */
	public synchronized List<String[]> readRecord() {

		List<String[]> buff = new ArrayList<String[]>();
		if (!new File(filePath).exists()) {

			return buff;
		}
		// Environment.errLogger.info("start read record from file");
		InputStreamReader fr = null;
		BufferedReader br = null;
		try {

			fr = new InputStreamReader(new FileInputStream(filePath));
			br = new BufferedReader(fr);
			String rec = null;
			while ((rec = br.readLine()) != null) {

				buff.add(rec.split(spit));
			}

			return buff;
		} catch (Throwable e) {

			e.printStackTrace();
			// Environment.errLogger.error(CommonUtil.getThrowableException(e));
			return buff;

		} finally {

			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// Environment.errLogger.info("end read record from file");
		}

	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

}
