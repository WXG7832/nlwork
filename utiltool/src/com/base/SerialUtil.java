package com.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.Entity;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.li.ResponseDecorator;
import com.nltecklib.protocol.li.check.CheckEnvironment;
import com.nltecklib.protocol.li.logic.LogicEnvironment;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.rm5248.serial.NoSuchPortException;
import com.rm5248.serial.NotASerialPortException;
import com.rm5248.serial.SerialPort;
import com.rm5248.serial.SerialPort.BaudRate;
import com.rm5248.serial.SerialPort.DataBits;
import com.rm5248.serial.SerialPort.FlowControl;
import com.rm5248.serial.SerialPort.Parity;
import com.rm5248.serial.SerialPort.StopBits;


public class SerialUtil {

	
	public static final SerialPort openPort(String portName, int baudrate) throws Exception {

		// ͨ���˿���ʶ��˿�
		// CommPortIdentifier portIdentifier =
		// CommPortIdentifier.getPortIdentifier(portName);

		// �򿪶˿ڣ����ö˿�����timeout���򿪲����ĳ�ʱʱ�䣩
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
		case 57600:
			b = BaudRate.B57600;
			break;
		case 115200:
			b = BaudRate.B115200;
			break;
		default:
			throw new Exception("baud not support :" + baudrate);

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
			throw new Exception("port " + portName + " not exist");
		} catch (NotASerialPortException e) {
			e.printStackTrace();
			throw new Exception("port " + portName + " not exist");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("open port " + portName + " error");
		}

		return port;

		// return port;

		// �ж��ǲ��Ǵ���
		// if (commPort instanceof SerialPort) {
		// SerialPort serialPort = (SerialPort) commPort;
		// // ���ô��ڵĲ����ʵȲ���
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
		// // ����ر���Ϣѭ����⣬��ɾ���ᵼ��CPUռ���ʴ������
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
	 * �رմ���
	 * 
	 * @param serialport ���رյĴ��ڶ���
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
	 * �����������������ȡ��ƴ�ӻ���������,�ж��Ƿ���������������������
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
					e.printStackTrace();
				}

				// ����¼�����

				// ��մ���
				SerialUtil.readMessageOnce(serialPort);

				if (decorator.getCode() != CheckEnvironment.CheckCode.PickupCode
						&& decorator.getCode() != LogicEnvironment.LogicCode.PickupCode) {

					// ��¼�߼��巢����־
					//Environment.protocolLogger.info("SEND:" + Entity.printList(sendData));
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
						if (decorator.getCode() != CheckEnvironment.CheckCode.PickupCode
								&& decorator.getCode() != LogicEnvironment.LogicCode.PickupCode) {

							// ��¼�߼��巢����־
							//Environment.protocolLogger.info("RECV:" + Entity.printList(recvData));
						}

						if (dec.getResult().getCode() != Result.SUCCESS) {

							//Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
							//Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
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

						// ��������
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

//				Environment.infoLogger.error(CommonUtil.getThrowableException(e));
//				Environment.infoLogger.error("SEND:" + Entity.printList(sendData));
//				Environment.infoLogger.error("RECV:" + Entity.printList(recvData));
				throw new IOException(e.getMessage());
			} finally {

			}
			// } catch(TooManyListenersException e){
			//
			// throw new IOException("");
			//
			// } finally {
			//
			//
			// //CommonUtil.sleep(10);
			// //serialPort.removeEventListener();
			//
			// }

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

					// ��¼�߼��巢����־
					//Environment.protocolLogger.info("SEND:" + Entity.printList(sendData));
				}

				SerialUtil.sendMessage(serialPort, sendData);

				CommonUtil.sleep(timeOut); // ����ʱ�����ȥ��ȡ

				byte[] response = SerialUtil.readMessageOnce(serialPort);

				if (response == null) {

					throw new IOException("read received data from port time out");
				}

				ResponseDecorator dec = (ResponseDecorator) Entity
						.decode(Arrays.asList(ProtocolUtil.convertArrayType(response)));

				if (decorator.getCode() != CheckEnvironment.CheckCode.PickupCode
						&& decorator.getCode() != LogicEnvironment.LogicCode.PickupCode) {

					// ��¼�߼��巢����־
//					Environment.protocolLogger
//							.info("RECV:" + Entity.printList(Arrays.asList(ProtocolUtil.convertArrayType(response))));
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

}
