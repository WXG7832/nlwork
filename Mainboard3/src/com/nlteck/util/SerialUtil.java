package com.nlteck.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.Environment;
import com.nlteck.i18n.I18N;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.li.check.CheckEnvironment;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.logic.LogicEnvironment;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.rm5248.serial.NoSuchPortException;
import com.rm5248.serial.NotASerialPortException;
import com.rm5248.serial.SerialPort;
import com.rm5248.serial.SerialPort.BaudRate;
import com.rm5248.serial.SerialPort.DataBits;
import com.rm5248.serial.SerialPort.FlowControl;
import com.rm5248.serial.SerialPort.Parity;
import com.rm5248.serial.SerialPort.StopBits;

import gnu.io.CommPortIdentifier;

public class SerialUtil {

	public static final ArrayList<String> findPort() {
		// 获得当前所有可用串口
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> portNameList = new ArrayList<String>();
		// 将可用串口名添加到List并返回该List
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				String portName = portId.getName();
				portNameList.add(portName);
			}
		}
		return portNameList;
	}

	public static final SerialPort openPort(String portName, int baudrate) throws AlertException {

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
		/*case 57600:
			b = BaudRate.B57600;
			break;*/
			
		case 115200:
			b = BaudRate.B115200;
			break;
		default:
			throw new AlertException(AlertCode.LOGIC, "不支持的波特率: " + baudrate);

		}

		SerialPort port = null;
		try {
			port = new SerialPort(portName, b, SerialPort.NO_CONTROL_LINE_CHANGE);
           
			//port = new SerialPort(portName, b, DataBits.DATABITS_8,  StopBits.STOPBITS_1 , Parity.NONE , SerialPort.NO_CONTROL_LINE_CHANGE);
			port.setFlowControl(FlowControl.NONE);
			port.setDataSize(DataBits.DATABITS_8);
			port.setParity(Parity.NONE);
			port.setStopBits(StopBits.STOPBITS_1);
		} catch (NoSuchPortException e) {

			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "串口" + portName + "不存在 ");
		} catch (NotASerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "串口" + portName + "不存在 ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AlertException(AlertCode.LOGIC, "打开串口" + portName + "发生错误");
		}

		return port;

		// return port;

		// 判断是不是串口
		// if (commPort instanceof SerialPort) {
		// SerialPort serialPort = (SerialPort) commPort;
		// // 设置串口的波特率等参数
		// serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8,
		// SerialPort.STOPBITS_1,
		// SerialPort.PARITY_NONE);
		//
		// serialPort.notifyOnBreakInterrupt(false);
		// serialPort.notifyOnCTS(false);
		// serialPort.notifyOnDataAvailable(false);
		// serialPort.notifyOnCarrierDetect(false);
		// serialPort.notifyOnDSR(false);
		// serialPort.notifyOnFramingError(false);
		// serialPort.notifyOnOverrunError(false);
		// serialPort.notifyOnParityError(false);
		// serialPort.notifyOnRingIndicator(false);
		// // 必须关闭消息循环检测，不删除会导致CPU占用率大幅上升
		// serialPort.removeEventListener();
		//
		//
		//
		// return serialPort;
		// } else {
		//
		// throw new Exception("not a serial port !");
		// }

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

	public static void sendMessage(SerialPort serialPort, String cmd) {

		if (serialPort != null) {

			try {
				serialPort.getOutputStream().write(cmd.getBytes());
				serialPort.getOutputStream().flush();
				serialPort.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendMessage(SerialPort serialPort, List<Byte> data) {

		if (serialPort != null) {

			try {

				byte[] array = ProtocolUtil.convertArrayType(data.toArray(new Byte[0]));
				// System.out.println(Entity.printList(data));
				serialPort.getOutputStream().write(array);
				serialPort.getOutputStream().flush();
				serialPort.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] readMessageOnce(SerialPort serialPort) throws IOException {

		if (serialPort != null) {

			InputStream is = serialPort.getInputStream();

			if (is.available() > 0) {
				byte[] input = new byte[is.available()];

				int numberRead = serialPort.getInputStream().read(input);

				return input;
			}
		}
		return null;
	}

	private static int readToBuff(InputStream is, List<Byte> buff) throws IOException {

		byte[] array = new byte[is.available()];
		int readNum = is.read(array, 0, array.length);
		for (int i = 0; i < readNum; i++) {
			buff.add(array[i]);
		}
		return readNum;
	}
	
	
	
	

	/**
	 * 发送命令完后立即读取，拼接缓存区数据,判断是否完整；完整则立即返回
	 * 
	 * @param pt
	 * @param serialPort
	 * @param decorator
	 * @param timeOut
	 * @return
	 * @throws IOException
	 */
	public static ResponseDecorator sendAndRecvImmediate(ProtocolType pt, SerialPort serialPort, Decorator decorator,
			int timeOut) throws IOException {

		synchronized (serialPort) {

			List<Byte> sendData = null;
			List<Byte> recvData = new ArrayList<Byte>();
			try {

				try {
					sendData = Entity.encode(decorator);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 添加事件监听

				// 清空串口
				SerialUtil.readMessageOnce(serialPort);

				if (decorator.getCode() != Check2Environment.Check2Code.PickupCode
						&& decorator.getCode() != Logic2Environment.Logic2Code.PickupCode /*&&
						  decorator.getCode().getClass() != AccessoryCode.class*/) {

					// 记录逻辑板发生日志
					Environment.protocolLogger.info("SEND:" + Entity.printList(sendData));
				}

				SerialUtil.sendMessage(serialPort, sendData);

				long tick = System.currentTimeMillis();

				InputStream is = serialPort.getInputStream();

				int len = 0;
				do {

					CommonUtil.sleep(20);

					if (len > 0 && is.available() >= len + 9 - recvData.size()) {

						readToBuff(is, recvData);

						ResponseDecorator dec = (ResponseDecorator) Entity.decode(recvData);
						CommonUtil.sleep(10);
						if (decorator.getCode() != Check2Environment.Check2Code.PickupCode
								&& decorator.getCode() != Logic2Environment.Logic2Code.PickupCode /*&&
								decorator.getCode().getClass() != AccessoryCode.class*/) {

							// 记录逻辑板发生日志
							Environment.protocolLogger.info("RECV:" + Entity.printList(recvData));
						}

						if (dec.getResult().getCode() != Result.SUCCESS) {

							Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
							Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
							throw new IOException("comm communicate failed ,failed code: " + dec.getResult());
						}

						return dec;

					} else if (is.available() >= 6) {

						readToBuff(is, recvData);
						int j = 0;
						for (j = 0; j < recvData.size(); j++) {

							if (recvData.get(j) == 0x18) {

								break;
							}
						}
						if (j > 0) {

							recvData = recvData.subList(j, recvData.size());
						}

						// 解析长度
						if (recvData.size() >= 6) {

							len = (int) ProtocolUtil.compose(recvData.subList(4, 6).toArray(new Byte[0]), true);

						}

					} else {

					}
				} while (System.currentTimeMillis() - tick <= timeOut);

				throw new IOException("Entity.printList(recvData) = " + Entity.printList(recvData)
						+ "\nis.available() = " + is.available() + "," + System.currentTimeMillis() + " - " + tick
						+ " > " + timeOut + " , received data from port time out");
			} catch (IOException e) {

				/*
				 * Environment.infoLogger.error(CommonUtil.getThrowableException(e));
				 * Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
				 * Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
				 */

				throw e;
			} catch (Exception e) {

				Environment.infoLogger.error(CommonUtil.getThrowableException(e));
				Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
				Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
				throw new IOException(e.getMessage());
			} finally {

			}
			
		}

	}

	public static ResponseDecorator sendAndRecv(ProtocolType pt, SerialPort serialPort, Decorator decorator,
			int timeOut) throws IOException {

		synchronized (serialPort) {

			try {
				List<Byte> sendData = null;
				try {
					sendData = Entity.encode(decorator);
				} catch (Exception e) {

					e.printStackTrace();
				}

				if (decorator.getCode() != CheckEnvironment.CheckCode.PickupCode
						&& decorator.getCode() != LogicEnvironment.LogicCode.PickupCode) {

					// 记录逻辑板发生日志
					Environment.protocolLogger.info("SEND:" + Entity.printList(sendData));
				}

				SerialUtil.sendMessage(serialPort, sendData);

				CommonUtil.sleep(timeOut); // 休眠时间后再去读取

				byte[] response = SerialUtil.readMessageOnce(serialPort);

				if (response == null) {

					throw new IOException("read received data from port time out");
				}

				ResponseDecorator dec = (ResponseDecorator) Entity
						.decode(Arrays.asList(ProtocolUtil.convertArrayType(response)));

				if (decorator.getCode() != CheckEnvironment.CheckCode.PickupCode
						&& decorator.getCode() != LogicEnvironment.LogicCode.PickupCode) {

					// 记录逻辑板发生日志
					Environment.protocolLogger
							.info("RECV:" + Entity.printList(Arrays.asList(ProtocolUtil.convertArrayType(response))));
				}

				if (dec.getResult().getCode() != Result.SUCCESS) {

					System.out.println("SEND:" + Entity.printList(sendData));
					System.out.println(
							"RECV:" + Entity.printList(Arrays.asList(ProtocolUtil.convertArrayType(response))));
					throw new IOException("comm communicate failed ,failed code: " + dec.getResult());
				}

				return dec;
			} catch (IOException ioE) {

				throw ioE;
			}
			// } catch(TooManyListenersException e){
			//
			// throw new IOException("");
			//
			// } finally {
			//
			// CommonUtil.sleep(10);
			// serialPort.removeEventListener();
			// }

		}

	}
	
	/**
	 * 加入重发机制，简化配置
	 * @author  wavy_zheng
	 * 2020年11月27日
	 * @param pt
	 * @param port
	 * @param cmd
	 * @param timeout
	 * @param commCountIndex
	 * @param retryCount
	 * @throws AlertException
	 */
	public static void  configAndRecvImmediate(ProtocolType pt , SerialPort port ,ConfigDecorator cmd , 
			int timeout ,int commCountIndex ,  int retryCount ) throws AlertException {
		
		try {
			commCountIndex++;
			ResponseDecorator response = sendAndRecvImmediate(pt, port, cmd, timeout);
			if(response.getResult().getCode() != Result.SUCCESS) {
				
				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.ResponseError,response.getResult()));
			}
		} catch (IOException e) {
			
			if(commCountIndex < retryCount) { //重发
				
				CommonUtil.sleep(1000);
				configAndRecvImmediate(pt,port,cmd,timeout,commCountIndex,retryCount);
			} else {
				
				e.printStackTrace();
				//抛异常
				throw new AlertException(AlertCode.LOGIC, I18N.getVal(I18N.CommError));
			}
			
			
		}
		
		
	}

}
