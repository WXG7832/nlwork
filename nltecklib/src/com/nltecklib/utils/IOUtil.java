package com.nltecklib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.io.NlteckPackageFactory;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.rm5248.serial.NoSuchPortException;
import com.rm5248.serial.NotASerialPortException;
import com.rm5248.serial.SerialPort;
import com.rm5248.serial.SerialPort.BaudRate;
import com.rm5248.serial.SerialPort.DataBits;
import com.rm5248.serial.SerialPort.FlowControl;
import com.rm5248.serial.SerialPort.Parity;
import com.rm5248.serial.SerialPort.StopBits;

/**
 * io口工具类
 * 
 * @author Administrator
 *
 */
public class IOUtil {

	public static final SerialPort openPort(String portName, int baudrate) throws Exception {

		// 通过端口名识别端口
		// CommPortIdentifier portIdentifier =
		// CommPortIdentifier.getPortIdentifier(portName);

		// 打开端口，设置端口名与timeout（打开操作的超时时间）
		BaudRate b = null;
		switch (baudrate) {

		case 9600:
			b = BaudRate.B9600;
			break;
		case 19200:
			b = BaudRate.B19200;
			break;
		case 38400:
			b = BaudRate.B38400;
			break;
		case 115200:
			b = BaudRate.B115200;
			break;
		default:
			throw new Exception("不支持的波特率: " + baudrate);

		}

		SerialPort port = null;
		try {
			port = new SerialPort(portName, b, SerialPort.NO_CONTROL_LINE_CHANGE);

			port.setFlowControl(FlowControl.NONE);
			port.setDataSize(DataBits.DATABITS_8);
			port.setParity(Parity.NONE);
			port.setStopBits(StopBits.STOPBITS_1);
		} catch (NoSuchPortException e) {

			e.printStackTrace();
			throw new Exception("串口" + portName + "不存在 ");
		} catch (NotASerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("串口" + portName + "不存在 ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("打开串口" + portName + "发生错误");
		}

		return port;

	}

	/**
	 * 关闭串口
	 * 
	 * @param serialport 待关闭的串口对象
	 */
	public static void closePort(SerialPort serialPort) {
		if (serialPort != null) {
			serialPort.close();
			serialPort = null;
		}
	}

	/**
	 * 单次写入消息
	 * 
	 * @param serialPort
	 * @param message
	 * @throws IOException
	 */
	public static void writeMessageOnce(OutputStream os, String message) throws IOException {

		writeStreamOnce(os, message.getBytes());
	}

	/**
	 * 单次写入流
	 * 
	 * @param serialPort
	 * @param stream
	 * @throws IOException
	 */
	public static void writeStreamOnce(OutputStream os, byte[] stream) throws IOException {

		os.write(stream);
		os.flush();
		os.close();
	}

//	/**
//	 * 单次读取，直到读到
//	 * 
//	 * @param is
//	 * @param timeOut
//	 * @return
//	 * @throws Exception
//	 */
//	public static byte[] readStreamOnce(InputStream is, int timeOut) throws Exception {
//		long startTime = System.currentTimeMillis();
//		while (is.available() == 0) {
//			if (System.currentTimeMillis() - startTime > timeOut) {
//				throw new Exception("read serial data time out");
//			}
//			Thread.sleep(20);
//		}
//		byte[] input = new byte[is.available()];
//		is.read(input);
//		return input;
//	}

	/**
	 * 单次读取流字节
	 * 
	 * @param serialPort
	 * @return 返回null表示接收缓存区没有任何字节流
	 * @throws IOException
	 */
	public static byte[] readStreamOnce(InputStream is) throws IOException {

		if (is.available() > 0) {

			byte[] input = new byte[is.available()];
			is.read(input);
			return input;
		}

		return null;

	}

	/**
	 * 从缓存区读取byteToReadNum个字节，当读取的字节数不足时该方法将一直阻塞到超时
	 * 
	 * @param serialPort
	 * @param byteToReadNum
	 * @param 超时时间
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static byte[] readStreamByCount(InputStream is, int byteToReadNum, int timeOut)
			throws IOException, InterruptedException {

		long tick = System.currentTimeMillis();
		while (true) {
			if (is.available() >= byteToReadNum) {
				byte[] input = new byte[byteToReadNum];
				int actualReadNum = is.read(input);
				if (actualReadNum != byteToReadNum) {

					throw new IOException("实际读取的字节数" + actualReadNum + "和预期字节数" + byteToReadNum + "不匹配");
				}
				return input;
			} else {

				Thread.sleep(20); // 必要延时
				if (System.currentTimeMillis() - tick > timeOut) {

					// 读取剩余的字节
					if (is.available() > 0) {

						byte[] input = new byte[is.available()];
						is.read(input);
						return input;
					} else {
						return null;
					}
				}
			}
		}

	}

	/**
	 * 指定的时长内读取串口字节流，
	 * 
	 * @param serialPort
	 * @param miliseconds
	 * @return byte[0]表示未读取任何字节流
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static byte[] readStreamUntilTimeout(InputStream is, int miliseconds)
			throws InterruptedException, IOException {

		long tick = System.currentTimeMillis();
		final long span = 20;
		List<Byte> list = new ArrayList<Byte>();
		do {

			Thread.sleep(span);
			// 读取
			byte[] buff = null;
			if ((buff = readStreamOnce(is)) != null) {

				list.addAll(ProtocolUtil.convertArrayToList(buff));
			}

		} while (tick + span < miliseconds);

		return ProtocolUtil.convertListToArray(list);

	}

	/**
	 * 指定的时长内读取串口字符串消息，使用默认平台字符集解码字节流
	 * 
	 * @param serialPort
	 * @param miliseconds
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static String readMessageUntilTimeout(InputStream is, int miliseconds)
			throws InterruptedException, IOException {

		return new String(readStreamUntilTimeout(is, miliseconds));
	}

	/**
	 * 指定的时长内读取串口字符串消息，使用指定的字符集解码字节流
	 * 
	 * @param serialPort
	 * @param miliseconds
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static String readMessageUntilTimeout(InputStream is, int miliseconds, Charset charset)
			throws InterruptedException, IOException {

		return new String(readStreamUntilTimeout(is, miliseconds), charset);
	}

	/**
	 * 读取单行字符串消息;在未读到\n前该方法将一直阻塞
	 * 
	 * @param serialPort
	 * @param charset    使用的编码字符集
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String readMessageOneLine(InputStream is, int timeOut) throws IOException, InterruptedException {

		StringBuffer line = new StringBuffer();

		long tick = System.currentTimeMillis();
		while (true) {

			Thread.sleep(20);
			if (is.available() > 0) {

				for (int n = 0; n < is.available(); n++) {

					int readVal = is.read();
					if (readVal == '\n') {

						return line.toString();
					}
					line.append((char) readVal);
				}

			}
			if (System.currentTimeMillis() - tick > timeOut) {

				throw new IOException("表读取网络数据超时");
			}
		}

	}

	/**
	 * 
	 * @param serialPort
	 * @param charset    字符编码
	 * @return 读取的消息
	 * @throws IOException
	 */
	public static String readMessageOnce(InputStream is, Charset charset) throws IOException {

		byte[] stream = readStreamOnce(is);
		if (stream == null) {

			return null;
		}
		return new String(stream, charset);
	}

	/**
	 * 使用平台默认的字符集进行解码消息字符
	 * 
	 * @param serialPort
	 * @return
	 * @throws IOException
	 */
	public static String readMessageOnce(InputStream is) throws IOException {

		byte[] stream = readStreamOnce(is);
		if (stream == null) {

			return null;
		}
		return new String(stream);
	}

	/**
	 * 从串口缓存中读取字节流，再根据字节流解码成对象; 在字节流不完整的情况下该方法将一直阻塞直到完整的数据接收到后才返回
	 * 该方法只取一个package对象，如何串口缓存仍有数据则不会读取
	 * 
	 * @param serialPort
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static NlteckIOPackage readPackageOnce(InputStream is, NlteckPackageFactory factory, int timeOut)
			throws IOException, InterruptedException {

		byte[] packageHead = null;
		if ((packageHead = readStreamByCount(is, factory.getMinDecodeLen(), timeOut)) == null) {

			throw new IOException("读取数据包超时");
		}
		// 计算完整包的长度
		byte[] packStream = new byte[factory.getPackLen(packageHead)];
		System.arraycopy(packageHead, 0, packStream, 0, packageHead.length);
		// 读取剩余的包数据
		byte[] packTail = new byte[packStream.length - packageHead.length];
		if ((packTail = readStreamByCount(is, packTail.length, timeOut)) == null) {

			throw new IOException("读取数据包超时");
		}
		// 组成完整的数据包字节流
		System.arraycopy(packTail, 0, packStream, packageHead.length, packTail.length);
		// 开始解码
		return factory.decode(packStream);

	}

}
