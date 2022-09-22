package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class AlertData extends Data implements Alertable, Responsable {

	private int chnIndexInDevice;
	private AlertCode alertCode = AlertCode.NORMAL;
	private Date date;
	private String alertInfo; // 异常信息

	@Override
	public void encode() {

		// 分区号
		data.add((byte) unitIndex);
		// 绝对时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(date == null ? new Date() : date);

		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);

		data.add((byte) year);
		data.add((byte) month);
		data.add((byte) date);
		data.add((byte) hour);
		data.add((byte) min);
		data.add((byte) sec);
        if(chnIndexInDevice == -1) {
        	
        	chnIndexInDevice = 0xffff;
        }
		data.addAll(Arrays.asList(ProtocolUtil.split(chnIndexInDevice, 2, true)));
		data.add((byte) alertCode.ordinal());
		
		try {
			byte[] infoArray = alertInfo.getBytes("utf-8");
			data.add((byte)infoArray.length);
			for(int n = 0 ; n < infoArray.length ; n++) {
				
				data.add(infoArray[n]);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		// 分区序号
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 解析时间
		Calendar cal = Calendar.getInstance();
		int year = ProtocolUtil.getUnsignedByte(data.get(index++)) + 2000;
		int month = ProtocolUtil.getUnsignedByte(data.get(index++)) - 1;
		int day = ProtocolUtil.getUnsignedByte(data.get(index++));
		int hour = ProtocolUtil.getUnsignedByte(data.get(index++));
		int min = ProtocolUtil.getUnsignedByte(data.get(index++));
		int sec = ProtocolUtil.getUnsignedByte(data.get(index++));

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);

		date = cal.getTime();
         
		//解析设备通道号
		chnIndexInDevice = (int) ProtocolUtil.compose(encodeData.subList(index, index+2).toArray(new Byte[0]), true);
		if(chnIndexInDevice == 0xffff) {
			
			chnIndexInDevice = -1;
		}
		index += 2;
		
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > AlertCode.values().length - 1) {

			throw new RuntimeException("error alertcode index : " + code);
		}
		alertCode = AlertCode.values()[code];
		
		// 报警信息长度
		int infoBytes = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 解码信息
		byte[] infoArray = new byte[infoBytes];
		for (int i = index; i < index + infoBytes; i++) {

			infoArray[i - index] = data.get(i);

		}
		index += infoBytes;
		try {
			alertInfo = new String(infoArray, "utf-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

	}

	@Override
	public Code getCode() {

		return MainCode.AlertCode;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public int getDeviceChnIndex() {
		return chnIndexInDevice;
	}

	public void setDeviceChnIndex(int chnIndex) {
		this.chnIndexInDevice = chnIndex;
	}

	public AlertCode getAlertCode() {
		return alertCode;
	}

	public void setAlertCode(AlertCode alertCode) {
		this.alertCode = alertCode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAlertInfo() {
		return alertInfo;
	}

	public void setAlertInfo(String alertInfo) {
		this.alertInfo = alertInfo;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toString() {
		return "AlertData [chnIndex=" + chnIndexInDevice + ", alertCode=" + alertCode + ", date=" + date + ", alertInfo="
				+ alertInfo + "]";
	}

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
	
	

}
