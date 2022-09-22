package com.nltecklib.protocol.lab.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

public class ChannelAlertData extends Data implements Alertable,Responsable {
	
	private List<AlertPack> alertPacks = new ArrayList<AlertPack>();
	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void encode() {
		
		data.add((byte)alertPacks.size());
		for (int i = 0; i < alertPacks.size(); i++) {
			
			AlertPack pack = alertPacks.get(i);
			
			//通道序号
			data.add((byte)pack.getChnIndex());
			// 绝对时间
			Calendar cal = Calendar.getInstance();
			cal.setTime(pack.getDate() == null ? new Date() : pack.getDate());

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
			
			//报警码
			data.add((byte) pack.getAlertCode().ordinal());	
			
			//异常信息
			try {
				byte[] infoArray = pack.getAlertInfo().getBytes("utf-8");
				//信息长度
				data.add((byte) infoArray.length);
				
				//信息
				for (int j = 0; j < infoArray.length; j++) {
					data.add(infoArray[j]);
				}
		
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		//个数
		int alertCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		//清空
		alertPacks.clear();
		for (int i = 0; i < alertCount; i++) {
			
			AlertPack alertData = new AlertPack();
			//通道地址
			alertData.setChnIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
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

			alertData.setDate(cal.getTime());
			
			//报警码
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > AlertCode.values().length - 1) {

				throw new RuntimeException("error alertcode index : " + code);
			}
			alertData.setAlertCode(AlertCode.values()[code]);
			
			//信息长度
			int infoBytes = ProtocolUtil.getUnsignedByte(data.get(index++));
			alertData.setInfoBytes(infoBytes);
			//异常信息
			byte[] infoArray = new byte[infoBytes];
			for(int j = index ; j < index + infoBytes ; j++) {
				
				infoArray[j - index] = data.get(j);
				
			}
			index += infoBytes;
			try {
				alertData.setAlertInfo(new String(infoArray,"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			alertPacks.add(alertData);
		}	
	}
	
	@Override
	public Code getCode() {
		return MainCode.ChannelAlertCode;
	}

	@Override
	public String toString() {
		return "ChannelAlertData [alertDatas=" + alertPacks + "]";
	}

	public List<AlertPack> getAlertPacks() {
		return alertPacks;
	}

	public void setAlertPacks(List<AlertPack> packs) {
		this.alertPacks = packs;
	}
	
	public void appendAlertPack(AlertPack pack) {
		
		this.alertPacks.add(pack);
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	
	
}
