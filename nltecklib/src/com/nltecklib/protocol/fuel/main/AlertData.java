package com.nltecklib.protocol.fuel.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.AlertLevel;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控报警协议数据
 * 
 * @author caichao_tang
 *
 */
public class AlertData extends Data implements Alertable, Responsable {

    private List<AlertContent> alertList = new ArrayList<AlertContent>();

    public int getAlertNumber() {
	return alertList.size();
    }

    public List<AlertContent> getAlertList() {
	return alertList;
    }

    public void setAlertList(List<AlertContent> alertList) {
	this.alertList = alertList;
    }

    @Override
    public void encode() {
	// 报警个数
	data.add((byte) alertList.size());
	for (int i = 0; i < alertList.size(); i++) {
	    AlertContent alertContent = alertList.get(i);
	    // 器件
	    data.addAll(Arrays.asList(ProtocolUtil.split(alertContent.getComponent().getNumber(), 2, true)));
	    // 时间
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(alertContent.getAlertDate() == null ? new Date() : alertContent.getAlertDate());
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
	    // 报警码
	    data.add((byte) alertContent.getAlertCode().getIndex());
	    // 报警等级
	    data.add((byte) alertContent.getAlertLevel().getIndex());
	    try {
		byte[] infoArray = alertContent.getInfo().getBytes("utf-8");
		// 信息长度
		data.add((byte) infoArray.length);
		// 异常信息
		for (byte byteInfo : infoArray) {
		    data.add(byteInfo);
		}
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	int infoLength = 0;
	data = encodeData;
	// 清空
	alertList.clear();
	// 报警数量
	int count = data.get(index++);
	// 报警信息
	for (int i = 0; i < count; i++) {
	    AlertContent alertContent = new AlertContent();
	    // 器件
	    int componentCode = (int) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true);
	    alertContent.setComponent(Component.get(componentCode));
	    // 时间
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
	    alertContent.setAlertDate(cal.getTime());
	    // 报警码
	    alertContent.setAlertCode(AlertCode.getValue(ProtocolUtil.getUnsignedByte(data.get(index++))));
	    // 报警等级
	    alertContent.setAlertLevel(AlertLevel.getValue(ProtocolUtil.getUnsignedByte(data.get(index++))));
	    // 信息长度
	    infoLength = data.get(index++);
	    // 异常信息
	    Byte[] infoBytes = data.subList(index, index += infoLength).toArray(new Byte[0]);
	    try {
		alertContent.setInfo(new String(ProtocolUtil.convertArrayType(infoBytes), "utf-8"));
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }
	    alertList.add(alertContent);
	}
    }

    @Override
    public MainCode getCode() {
	return MainCode.ALERT_CODE;
    }

    @Override
    public String toString() {
	return "AlertData [alertList=" + alertList + "]";
    }

}
