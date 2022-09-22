package com.nltecklib.protocol.lab.backup;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.backup.BackupEnvironment.BackupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2020年1月3日 下午11:33:13
* 备份芯片的信息版本
*/
public class BInfoData extends Data implements Queryable, Responsable {
     
	private String version = ""; //版本
	private String product = "";    //产品类型
	private int    year;
	private int    month;
	private int    day;
	
	@Override
	public boolean supportMain() {
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
		
		data.add((byte) (year - 2000));
		data.add((byte) month);
		data.add((byte) day);
		//产品型号
		data.add((byte) product.length());
		data.addAll(ProtocolUtil.convertArrayToList(product.getBytes()));
		//软件版本
		data.add((byte) version.length());
		data.addAll(ProtocolUtil.convertArrayToList(version.getBytes()));

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
		month = ProtocolUtil.getUnsignedByte(data.get(index++));
		day = ProtocolUtil.getUnsignedByte(data.get(index++));
		int len = ProtocolUtil.getUnsignedByte(data.get(index++));
		product = new String(ProtocolUtil.convertListToArray(data.subList(index, index + len)));
		index += len;
		len = ProtocolUtil.getUnsignedByte(data.get(index++));
		version = new String(ProtocolUtil.convertListToArray(data.subList(index, index + len)));
		index += len;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return BackupCode.VersionInfoCode;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	

}
