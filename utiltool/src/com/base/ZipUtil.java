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
 * @version ����ʱ�䣺2021��9��8�� ����5:50:28 ��˵��
 */
public class ZipUtil {

	/***
	 * ѹ��GZip
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
	 * ��ѹGZip
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
     * �ַ����Ľ�ѹ  
     *   
     * @param str  
     *            ���ַ�����ѹ  
     * @return    ���ؽ�ѹ������ַ���  
     * @throws IOException  
     */  
    public static String unCompress(String str) throws IOException {  
        if (null == str || str.length() <= 0) {  
            return str;  
        }  
        // ����һ���µ� byte ���������  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        // ����һ�� ByteArrayInputStream��ʹ�� buf ��Ϊ�仺��������  
        ByteArrayInputStream in = new ByteArrayInputStream(str  
                .getBytes("ISO-8859-1"));  
        // ʹ��Ĭ�ϻ�������С�����µ�������  
        GZIPInputStream gzip = new GZIPInputStream(in);  
        byte[] buffer = new byte[1024];  
        int n = 0;  
        while ((n = gzip.read(buffer)) >= 0) {// ��δѹ�����ݶ����ֽ�����  
            // ��ָ�� byte �����д�ƫ���� off ��ʼ�� len ���ֽ�д��� byte���������  
            out.write(buffer, 0, n);  
        }  
        // ʹ��ָ���� charsetName��ͨ�������ֽڽ�����������ת��Ϊ�ַ���  
        return out.toString();  
    }  
	
	
	public static String compress(String str ) throws IOException {  
        if (null == str || str.length() <= 0) {  
            return str;  
        }  
        // ����һ���µ� byte ���������  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        // ʹ��Ĭ�ϻ�������С�����µ������  
        GZIPOutputStream gzip = new GZIPOutputStream(out);  
        // �� b.length ���ֽ�д��������  
        gzip.write(str.getBytes());  
        gzip.close();  
        // ʹ��ָ���� charsetName��ͨ�������ֽڽ�����������ת��Ϊ�ַ���  
        return out.toString("ISO-8859-1");  
    } 
	
	/**
	 * ���ֽ�������ѹ��
	 * @author  wavy_zheng
	 * 2022��1��5��
	 * @param arrData
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] arrData) throws IOException {  

		// ����һ���µ� byte ���������  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
		// ʹ��Ĭ�ϻ�������С�����µ������  
        GZIPOutputStream gzip = new GZIPOutputStream(out); 
        gzip.write(arrData);
        gzip.close();  
        return out.toByteArray();
		
	}
	/**
	 * ���ֽ������н�ѹ��
	 * @author  wavy_zheng
	 * 2022��1��5��
	 * @param arrData
	 * @return
	 * @throws IOException
	 */
	public static byte[] unCompress(byte[] arrData) throws IOException {  
        
        // ����һ���µ� byte ���������  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        // ����һ�� ByteArrayInputStream��ʹ�� buf ��Ϊ�仺��������  
        ByteArrayInputStream in = new ByteArrayInputStream(arrData);  
        // ʹ��Ĭ�ϻ�������С�����µ�������  
        GZIPInputStream gzip = new GZIPInputStream(in);  
        byte[] buffer = new byte[1024];  
        int n = 0;  
        while ((n = gzip.read(buffer)) >= 0) {// ��δѹ�����ݶ����ֽ�����  
            // ��ָ�� byte �����д�ƫ���� off ��ʼ�� len ���ֽ�д��� byte���������  
            out.write(buffer, 0, n);  
        }  
        // ʹ��ָ���� charsetName��ͨ�������ֽڽ�����������ת��Ϊ�ַ���  
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
