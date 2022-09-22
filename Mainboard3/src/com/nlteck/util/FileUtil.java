package com.nlteck.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	public static void copyFile(File source, File dest) throws IOException {

		if (!source.exists()) {

			throw new IOException("源文件不存在");
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
		
		
		  removeAllFielsExcept(directoryPath ,null);
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
}
