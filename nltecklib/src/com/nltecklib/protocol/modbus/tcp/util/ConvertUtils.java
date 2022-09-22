package com.nltecklib.protocol.modbus.tcp.util;
/**
 * 
 * @ClassName HexConvert
 * @Description 16进制和byte数组之间的相互转换工具类
 */
public class ConvertUtils {
	
	private static final char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};



/*
     * byte[]数组转十六进制
     */
    public static String bytes2hexStr(byte[] bytes) {
        int len = bytes.length;
        if (len==0) {
            return null;
        }
        char[] cbuf = new char[len*2];
        for (int i=0; i<len; i++) {
            int x = i*2;
            cbuf[x]     = HEX_CHARS[(bytes[i] >>> 4) & 0xf];
            cbuf[x+1]    = HEX_CHARS[bytes[i] & 0xf];
        }
        return new String(cbuf);
    }
	
	public static String hexStringToString(String s) {
	    if (s == null || s.equals("")) {
	        return null;
	    }
	    s = s.replace(" ", "");
	    byte[] baKeyword = new byte[s.length() / 2];
	    for (int i = 0; i < baKeyword.length; i++) {
	        try {
	            baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    try {
	        s = new String(baKeyword, "UTF-8");
	        new String();
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    return s;
	}
	
	/**
	 * 16进制转成byte[]
	 * @param hexString
	 * @return
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		hexString = hexString.replace(" ", "");
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 16进制转换工程功能类
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * byte数组变成16进制
	 * @param bytes
	 * @return
	 */
	public static String binaryToHexString(byte[] bytes) {
		String hexStr = "0123456789ABCDEF";
		String result = "";
		String hex = "";
		for (Byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result += hex + " ";
		}
		return result;
	}
	
	/**
	 * Byte数组变成16进制
	 * @param bytes
	 * @return
	 */
	public static String binaryToHexString(Byte[] bytes) {
		String hexStr = "0123456789ABCDEF";
		String result = "";
		String hex = "";
		for (Byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result += hex + " ";
		}
		return result;
	}

	/**
	 * string转成unicode
	 * @param value
	 * @return
	 */
	public static String stringToUnicode(String value){  
		StringBuffer sbu = new StringBuffer();  
		char[] chars = value.toCharArray();   
		for (int i = 0; i < chars.length; i++) {          
			sbu.append(String.format("%04x", (int)chars[i])+" ");    
		}  
		return sbu.toString().replaceAll(" ", "");  
	}  
		
	/**
	 * string转成ascii
	 * @param value
	 * @return
	 */
	public static String stringToAscii(String value){  
		StringBuffer sbu = new StringBuffer();  
		char[] chars = value.toCharArray();   
		for (int i = 0; i < chars.length; i = i + 2) {
			if (i+1 < chars.length) {
				sbu.append(String.format("%02x", (int)chars[i+1])+" ");    
				sbu.append(String.format("%02x", (int)chars[i])+" ");    				
			}
			if (i == chars.length - 1) {
				sbu.append(String.format("%02x", 0));
				sbu.append(String.format("%02x", (int)chars[i])+" ");  
			}
		}  
		return sbu.toString();  
	}
		
	/**
	 * ascii码转换成string//高位字符在后，地位字符在前
	 * @param value
	 * @return
	 */
	public static String asciiToString(String value){  
		byte[] bs = hexStringToBytes(value);
		for (int i = 0; i < bs.length; i = i + 2) {
			byte b = bs[i];
			bs[i] = bs[i + 1];
			bs[i + 1] = b;
		} 
		return new String(bs);
	}  


}
