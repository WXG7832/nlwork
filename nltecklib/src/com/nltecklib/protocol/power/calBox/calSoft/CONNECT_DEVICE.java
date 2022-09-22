package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 连接设备主控
 * 
 * @author Administrator
 *
 */
public class CONNECT_DEVICE extends Data implements Queryable, Configable, Responsable {

    private boolean connect; // 连接/断开设备主控
    private String ip;
    private int port;
    private byte partitionNum;
    private byte partitionChnNum;

    @Override
    public boolean supportDriver() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void encode() {

	data.add((byte) (connect ? 1 : 0));
	data.addAll(ProtocolUtil.encodeIp(ip));
	data.addAll(Arrays.asList(ProtocolUtil.split((long) port, 4, true)));
    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;
	connect = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	ip = ProtocolUtil.decodeIp(data.subList(index, index + 4));
	index += 4;
	port = (int) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true);
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.CONNECT_DEVICE;
    }

    public boolean isConnected() {
	return connect;
    }

    public void setConnect(boolean connect) {
	this.connect = connect;
    }

    public String getIp() {
	return ip;
    }

    public void setIp(String ip) {
	this.ip = ip;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public byte getPartitionNum() {
	return partitionNum;
    }

    public void setPartitionNum(byte partitionNum) {
	this.partitionNum = partitionNum;
    }

    public byte getPartitionChnNum() {
	return partitionChnNum;
    }

    public void setPartitionChnNum(byte partitionChnNum) {
	this.partitionChnNum = partitionChnNum;
    }

}
