package com.nltecklib.protocol.fuel.main;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.LogCode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控报警协议数据
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class LogData extends Data implements ComponentSupportable, Alertable, Responsable {

	private LogCode logCode = LogCode.NORMAL;
	private Date date;
	private String alertInfo; // 异常信息

	@Override
	public void encode() {

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

		data.add((byte) logCode.ordinal());

		try {
			byte[] infoArray = alertInfo.getBytes("utf-8");
			data.add((byte) infoArray.length);
			for (int n = 0; n < infoArray.length; n++) {

				data.add(infoArray[n]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;

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

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));

		logCode = LogCode.getValue(code);
		if (logCode == null) {

			throw new RuntimeException("error alertcode index : " + code);
		}

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

	public LogCode getLogCode() {
		return logCode;
	}

	public void setLogCode(LogCode logCode) {
		this.logCode = logCode;
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
	public Code getCode() {
		return MainCode.ALERT_CODE;
	}

	@Override
	public String toString() {
		return "AlertData [result=" + result + ", orient=" + orient + ", alertCode=" + logCode + ", date=" + date
				+ ", alertInfo=" + alertInfo + ",componentCode=" + component + "]";
	}

}
