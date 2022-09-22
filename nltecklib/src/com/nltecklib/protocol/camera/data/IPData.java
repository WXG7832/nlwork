package com.nltecklib.protocol.camera.data;

import java.util.Arrays;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Encode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;

public class IPData extends Data implements Encode{
	private int  addr1 = 192;
	private int  addr2 = 168;
	private int  addr3 = 0;
	private int  addr4 = 8;
	private int port = 20100;
	private int addr = 1;
	private int serviceAddr1 = 192;
	private int serviceAddr2 = 168;
	private int serviceAddr3 = 0;
	private int serviceAddr4 = 201;
	private int servicePort = 8123;
	
	public int getAddr1() {
		return addr1;
	}

	public void setAddr1(int addr1) {
		this.addr1 = addr1;
	}

	public int getAddr2() {
		return addr2;
	}

	public void setAddr2(int addr2) {
		this.addr2 = addr2;
	}

	public int getAddr3() {
		return addr3;
	}

	public void setAddr3(int addr3) {
		this.addr3 = addr3;
	}

	public int getAddr4() {
		return addr4;
	}

	public void setAddr4(int addr4) {
		this.addr4 = addr4;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getAddr() {
		return addr;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int getServiceAddr1() {
		return serviceAddr1;
	}

	public void setServiceAddr1(int serviceAddr1) {
		this.serviceAddr1 = serviceAddr1;
	}

	public int getServiceAddr2() {
		return serviceAddr2;
	}

	public void setServiceAddr2(int serviceAddr2) {
		this.serviceAddr2 = serviceAddr2;
	}

	public int getServiceAddr3() {
		return serviceAddr3;
	}

	public void setServiceAddr3(int serviceAddr3) {
		this.serviceAddr3 = serviceAddr3;
	}

	public int getServiceAddr4() {
		return serviceAddr4;
	}

	public void setServiceAddr4(int serviceAddr4) {
		this.serviceAddr4 = serviceAddr4;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	@Override
	public void encode() {
		data.clear();
		data.add((byte) addr1);
		data.add((byte) addr2);
		data.add((byte) addr3);
		data.add((byte) addr4);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)port, 2, true)));
		data.add((byte) addr);
		data.add((byte) serviceAddr1);
		data.add((byte) serviceAddr2);
		data.add((byte) serviceAddr3);
		data.add((byte) serviceAddr4);
		data.addAll(Arrays.asList(ProtocolUtil.split((long)servicePort, 2, true)));
	}

	@Override
	public Code getCode() {
		return CameraCode.IP;
	}

	@Override
	public String toString() {
		return "IPData [addr1=" + addr1 + ", addr2=" + addr2 + ", addr3=" + addr3 + ", addr4=" + addr4 + ", port="
				+ port + ", addr=" + addr + ", serviceAddr1=" + serviceAddr1 + ", serviceAddr2=" + serviceAddr2
				+ ", serviceAddr3=" + serviceAddr3 + ", serviceAddr4=" + serviceAddr4 + ", servicePort=" + servicePort
				+ "]";
	}
 
}
