package com.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年9月8日 下午5:50:28 类说明
 */
public class ZipUtil {

	/***
	 * 压缩GZip
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	/***
	 * 解压GZip
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			GZIPInputStream gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
			baos.close();
			gzip.close();
			bis.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	/**  
     * 字符串的解压  
     *   
     * @param str  
     *            对字符串解压  
     * @return    返回解压缩后的字符串  
     * @throws IOException  
     */  
    public static String unCompress(String str) throws IOException {  
        if (null == str || str.length() <= 0) {  
            return str;  
        }  
        // 创建一个新的 byte 数组输出流  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组  
        ByteArrayInputStream in = new ByteArrayInputStream(str  
                .getBytes("ISO-8859-1"));  
        // 使用默认缓冲区大小创建新的输入流  
        GZIPInputStream gzip = new GZIPInputStream(in);  
        byte[] buffer = new byte[1024];  
        int n = 0;  
        while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组  
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流  
            out.write(buffer, 0, n);  
        }  
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串  
        return out.toString();  
    }  
	
	
	public static String compress(String str ) throws IOException {  
        if (null == str || str.length() <= 0) {  
            return str;  
        }  
        // 创建一个新的 byte 数组输出流  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        // 使用默认缓冲区大小创建新的输出流  
        GZIPOutputStream gzip = new GZIPOutputStream(out);  
        // 将 b.length 个字节写入此输出流  
        gzip.write(str.getBytes());  
        gzip.close();  
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串  
        return out.toString("ISO-8859-1");  
    } 
	
	/**
	 * 对字节流进行压缩
	 * @author  wavy_zheng
	 * 2022年1月5日
	 * @param arrData
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] arrData) throws IOException {  

		// 创建一个新的 byte 数组输出流  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
		// 使用默认缓冲区大小创建新的输出流  
        GZIPOutputStream gzip = new GZIPOutputStream(out); 
        gzip.write(arrData);
        gzip.close();  
        return out.toByteArray();
		
	}
	/**
	 * 对字节流进行解压缩
	 * @author  wavy_zheng
	 * 2022年1月5日
	 * @param arrData
	 * @return
	 * @throws IOException
	 */
	public static byte[] unCompress(byte[] arrData) throws IOException {  
        
        // 创建一个新的 byte 数组输出流  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组  
        ByteArrayInputStream in = new ByteArrayInputStream(arrData);  
        // 使用默认缓冲区大小创建新的输入流  
        GZIPInputStream gzip = new GZIPInputStream(in);  
        byte[] buffer = new byte[1024];  
        int n = 0;  
        while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组  
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流  
            out.write(buffer, 0, n);  
        }  
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串  
        return out.toByteArray();
    }  
	
	
	
	
	public static void main(String[] args) {
		  
		
		byte[] arr = new byte[10024];
		for(int n = 0 ; n < 10000 ; n++) {
			
			arr[n] = (byte) (n % 255);
			
		}
		
		System.out.println("start to encode bytes");
		try {
			byte[] destData = ZipUtil.compress(arr);
			System.out.println("compress arr size = " + destData.length);
			
			for(int n = 0 ; n < destData.length ; n++) {
				
				System.out.print((destData[n] & 0xff) + " ");
			}
			
			System.out.println("start to uncompress byte arrs");
			
			byte[] src = ZipUtil.unCompress(destData);
			
			System.out.println("decode arr size = " + src.length);
			
			for(int n = 0 ; n < 300 ; n++) {
				
				System.out.print(src[n] + " ");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		String str = "68 04 11 02 00 01 01 A9 CE 86 68 04 11 02 00 01 01 A9 CE 86 68 04 11 02 00 01 01 A9 CE 86"
//				+ "68 04 11 02 00 01 01 A9 CE 86"
//				+ "68 04 11 02 00 01 01 A9 CE 86"
//				+ "68 04 11 02 00 01 01 A9 CE 86"
//				+ "68 04 11 02 00 01 01 A9 CE 86"
//				+ "68 04 11 02 00 01 01 A9 CE 86";
//		byte[] srcData = ProtocolUtil.convertListToArray(ProtocolUtil.converString(str));
//	
//		try {
//			System.out.println(Arrays.toString(srcData));
//			System.out.println("srcData len = " + srcData.length);
//			byte[] destData = ZipUtil.compress(srcData);
//			System.out.println("compress data size = " + destData.length);
//			System.out.println(Arrays.toString(destData));
//			byte[] uncompress = ZipUtil.unCompress(destData);
//			System.out.println(Arrays.toString(uncompress));
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

}
