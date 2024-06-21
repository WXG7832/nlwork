package com.base;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FileUtil {

	/**
     * copy("F:\\myjava","E:\\myjava");
              * 文件夹携带文件 拷贝
     * @param oldPath 原文件夹路径
     * @param newPath 目标文件夹路径
     * @throws IOException 
     */
	public static void copy(String oldPath, String newPath) throws Exception {
		
		File file1=new File(oldPath);
		File[] fs=file1.listFiles();
		File file2=new File(newPath);
		
		if(!file2.exists()){
			file2.mkdirs();
		}
		
		for (File f : fs) {
			if(f.isFile()){
				copyFile(new File(f.getPath()),new File(newPath+File.separator+f.getName())); //调用文件拷贝的方法
			}else if(f.isDirectory()){
				copy(f.getPath(),newPath+File.separator+f.getName());
			}
		}
		
	}
	
	
	/**
	 * 按行读取文档
	 * 
	 * @author wavy_zheng 2020年3月25日
	 * @param fileName
	 * @return
	 */
	public static List<String> readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		List<String> list = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读一行，读入null时文件结束
			while ((tempString = reader.readLine()) != null) {
				// 把当前行号显示出来
				System.out.println("line " + line + ": " + tempString);
				list.add(tempString);
				line++;
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return list;

	}
	
	public static String readToString(String fileName , String charset) {
		
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
			return charset != null ? new String(filecontent, charset) : new String(filecontent);
		} catch (UnsupportedEncodingException e) {
			System.err.println("The OS does not support " + charset);
			e.printStackTrace();
			return null;
		}
	}
	
	public static void copyFile(File source, File dest) throws IOException {

		if (!source.exists()) {
			throw new IOException("source file not exist");
		}

		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(dest);
		byte[] buffer = new byte[2048];
		int byteRead = 0, byteWrite = 0;
		while ((byteRead = fis.read(buffer)) != -1) {

			fos.write(buffer, 0, byteRead);
		}
		fis.close();
		fos.close();

	}
	
	public static byte[] getBytes(File file) throws IOException {
		
        FileInputStream fis = new FileInputStream(file);  
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024 * 1024);  
        byte[] b = new byte[1024 * 1024];  
        int n;  
        while ((n = fis.read(b)) != -1) {  
            bos.write(b, 0, n);  
        }  
        fis.close();  
        bos.close();  
        return bos.toByteArray(); 
	}

	public static boolean moveFile(File source, File dest) throws IOException {

		copyFile(source,dest);
		return source.delete();
	}
	
	public static void removeAllFielsExcept(String directoryPath , String fileName) {
		
		
		 File directory = new File(directoryPath);
		  if(!directory.exists()) {
			  
			  return;
		  }
		  for(File file : directory.listFiles()) {
			  
			    if(file.isDirectory()) {
			    	
			    	removeAllFielsExcept(file.getPath(),fileName); //递归
			    }else {
			    	
			    	if(fileName == null || !fileName.equals(file.getName())) {
			    	  
			    	   file.delete();
			    	}else {
			    		
			    		 System.out.println("except delete " + fileName);
			    	}
			    }
		  }
	}
	
	public static void removeAllFiles(String directoryPath) {
		
		
		  removeAllFielsExcept(directoryPath , null);
//		  File directory = new File(directoryPath);
//		  if(!directory.exists()) {
//			  
//			  return;
//		  }
//		  for(File file : directory.listFiles()) {
//			  
//			    if(file.isDirectory()) {
//			    	
//			    	removeAllFiles(file.getPath()); //递归
//			    }else {
//			    	
//			    	file.delete();
//			    }
//		  }
	}
	
	
	/**
	 * 按行写入文件
	 * @author  wavy_zheng
	 * 2021年4月12日
	 * @param fileName
	 * @param content
	 * @throws IOException 
	 */
	public static void writeFileByLines(String fileName , List<String> content) throws IOException {
		
		FileWriter fw = new FileWriter(fileName);
        for (int i = 0; i < content.size(); i++) {
        	
        	if(!content.get(i).substring(content.get(i).length() - 1).equals("\n")) {
        		
        		fw.write(content.get(i) + "\n");
        	}
            
        }
        fw.close();
		
	}

	public  static void writeStringToFile(String fileName, String content , String charset) {

		File file = new File(fileName);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(charset != null ? content.getBytes(Charset.forName(charset)) : content.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	/**
     * 通过文件路径直接修改文件名
     * 
     * @param filePath    需要修改的文件的完整路径
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    public static String FixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return null;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return null;
        String newFilePath = filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" + newFileName;
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return newFilePath;
    }
    
    
    public static boolean createFile(String path) {
		File file = new File(path);
		return (file.mkdirs());
	}
    @Test
    public void test() {
    	String path = "./config"+File.separator+2;
    	System.out.println(createFile(path));
    }
}
