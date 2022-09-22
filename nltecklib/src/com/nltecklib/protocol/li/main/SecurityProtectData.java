package com.nltecklib.protocol.li.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月19日 上午10:22:35
* 安全策略保护
*/
public class SecurityProtectData extends Data implements Configable, Queryable, Responsable {
    
	private double generalVoltUpper;
	private double generalDeviceVoltUpper;
	private double generalVoltLower;
	private double generalCurrUpper;
	
	private double chargeVoltValOffset; // 充电截止电压偏移绝对值
	private double chargeVoltPercentOffset; //充电截止电压偏移百分比
	
	private double dischargeVoltValOffset; //放电截止电压偏移绝对保值值
	private double dischargeVoltPercentOffset; //放电截止电压便宜百分比
	
	private int chargeVoltDescCount; //充电电压连续下降个数
	private double chargeVoltDescRange ; //充电电压单次下降幅度
	
	private int dischargeVoltAscCount; //放电电压连续上升个数
	private double dischargeVoltAscRange ; //放电电压单次上升幅度
	
	
	private boolean  autoCfg; //自动配置开关
	
	
	

	@Override
	public void encode() {
		
		data.add((byte) (autoCfg ? 0x01 : 0x00));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (generalDeviceVoltUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (generalVoltUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (generalVoltLower * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (generalCurrUpper * 10), 2, true)));
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chargeVoltValOffset * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chargeVoltPercentOffset * 10), 2, true)));
		
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (dischargeVoltValOffset * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (dischargeVoltPercentOffset * 10), 2, true)));
		
		data.add((byte) chargeVoltDescCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chargeVoltDescRange * 10), 2, true)));
		data.add((byte) dischargeVoltAscCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (dischargeVoltAscRange * 10), 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;
		autoCfg = data.get(index++) == 0x01;
		generalDeviceVoltUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		generalVoltUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		generalVoltLower = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		generalCurrUpper = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		chargeVoltValOffset = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		chargeVoltPercentOffset = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		dischargeVoltValOffset = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		dischargeVoltPercentOffset = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		chargeVoltDescCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		chargeVoltDescRange = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		
		dischargeVoltAscCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		dischargeVoltAscRange = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.SecurityProtectCode;
	}

	public double getGeneralVoltUpper() {
		return generalVoltUpper;
	}

	public void setGeneralVoltUpper(double generalVoltUpper) {
		this.generalVoltUpper = generalVoltUpper;
	}

	public double getGeneralDeviceVoltUpper() {
		return generalDeviceVoltUpper;
	}

	public void setGeneralDeviceVoltUpper(double generalDeviceVoltUpper) {
		this.generalDeviceVoltUpper = generalDeviceVoltUpper;
	}

	public double getGeneralVoltLower() {
		return generalVoltLower;
	}

	public void setGeneralVoltLower(double generalVoltLower) {
		this.generalVoltLower = generalVoltLower;
	}

	public double getGeneralCurrUpper() {
		return generalCurrUpper;
	}

	public void setGeneralCurrUpper(double generalCurrUpper) {
		this.generalCurrUpper = generalCurrUpper;
	}

	public double getChargeVoltValOffset() {
		return chargeVoltValOffset;
	}

	public void setChargeVoltValOffset(double chargeVoltValOffset) {
		this.chargeVoltValOffset = chargeVoltValOffset;
	}

	public double getChargeVoltPercentOffset() {
		return chargeVoltPercentOffset;
	}

	public void setChargeVoltPercentOffset(double chargeVoltPercentOffset) {
		this.chargeVoltPercentOffset = chargeVoltPercentOffset;
	}

	public double getDischargeVoltValOffset() {
		return dischargeVoltValOffset;
	}

	public void setDischargeVoltValOffset(double dischargeVoltValOffset) {
		this.dischargeVoltValOffset = dischargeVoltValOffset;
	}

	public double getDischargeVoltPercentOffset() {
		return dischargeVoltPercentOffset;
	}

	public void setDischargeVoltPercentOffset(double dischargeVoltPercentOffset) {
		this.dischargeVoltPercentOffset = dischargeVoltPercentOffset;
	}

	public int getChargeVoltDescCount() {
		return chargeVoltDescCount;
	}

	public void setChargeVoltDescCount(int chargeVoltDescCount) {
		this.chargeVoltDescCount = chargeVoltDescCount;
	}

	public double getChargeVoltDescRange() {
		return chargeVoltDescRange;
	}

	public void setChargeVoltDescRange(double chargeVoltDescRange) {
		this.chargeVoltDescRange = chargeVoltDescRange;
	}

	public int getDischargeVoltAscCount() {
		return dischargeVoltAscCount;
	}

	public void setDischargeVoltAscCount(int dischargeVoltAscCount) {
		this.dischargeVoltAscCount = dischargeVoltAscCount;
	}

	public double getDischargeVoltAscRange() {
		return dischargeVoltAscRange;
	}

	public void setDischargeVoltAscRange(double dischargeVoltAscRange) {
		this.dischargeVoltAscRange = dischargeVoltAscRange;
	}

	public boolean isAutoCfg() {
		return autoCfg;
	}

	public void setAutoCfg(boolean autoCfg) {
		this.autoCfg = autoCfg;
	}

	@Override
	public String toString() {
		return "SecurityProtectData [generalVoltUpper=" + generalVoltUpper + ", generalDeviceVoltUpper="
				+ generalDeviceVoltUpper + ", generalVoltLower=" + generalVoltLower + ", generalCurrUpper="
				+ generalCurrUpper + ", chargeVoltValOffset=" + chargeVoltValOffset + ", chargeVoltPercentOffset="
				+ chargeVoltPercentOffset + ", dischargeVoltValOffset=" + dischargeVoltValOffset
				+ ", dischargeVoltPercentOffset=" + dischargeVoltPercentOffset + ", chargeVoltDescCount="
				+ chargeVoltDescCount + ", chargeVoltDescRange=" + chargeVoltDescRange + ", dischargeVoltAscCount="
				+ dischargeVoltAscCount + ", dischargeVoltAscRange=" + dischargeVoltAscRange + ", autoCfg=" + autoCfg
				+ "]";
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
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
