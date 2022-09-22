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

public class TempData extends Data implements Cloneable, Configable, Queryable, Responsable {


	private double tempConstant = 0;
	public double upper = 0;
	public double lower = 0;
	//public int time = 0;
   
	private int protectMinute;
	
	private boolean syncTempOpen;
	//private boolean audioAlertOpen;
	private boolean tempControlOpen;

	public TempData() {

	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split((int)(tempConstant * 10), 2, true)));

		data.addAll(Arrays.asList(ProtocolUtil.split((int) upper * 10, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((int) lower * 10, 2, true)));
		//data.addAll(Arrays.asList(ProtocolUtil.split((int) time, 2, true)));
		data.add((byte) protectMinute);

		//data.add((byte) (audioAlertOpen ? 1 : 0));
		data.add((byte) (syncTempOpen ? 1 : 0));
		data.add((byte) (tempControlOpen ? 1 : 0));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		tempConstant = (double) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		upper = (double) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
		lower = (double) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true) / 10;
		index += 2;
//		time = (int) ProtocolUtil.compose(encodeData.subList(index, index + 2).toArray(new Byte[0]), true);
//		index += 2;
		
		protectMinute = ProtocolUtil.getUnsignedByte(data.get(index++));

		int flag = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
//		audioAlertOpen = flag == 1;
//		flag = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		syncTempOpen = flag == 1;
		flag = ProtocolUtil.getUnsignedByte(encodeData.get(index++));
		tempControlOpen = flag == 1;

	}

	@Override
	public Code getCode() {

		return MainCode.TempControlCode;
	}

	public double getTempConstant() {
		return tempConstant;
	}

	public void setTempConstant(double tempConstant) {
		this.tempConstant = tempConstant;
	}

	public boolean isSyncTempOpen() {
		return syncTempOpen;
	}

	public void setSyncTempOpen(boolean syncTempOpen) {
		this.syncTempOpen = syncTempOpen;
	}

//	public boolean isAudioAlertOpen() {
//		return audioAlertOpen;
//	}
//
//	public void setAudioAlertOpen(boolean audioAlertOpen) {
//		this.audioAlertOpen = audioAlertOpen;
//	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

//	public int getTime() {
//		return time;
//	}
//
//	public void setTime(int time) {
//		this.time = time;
//	}
	
	public boolean isTempControlOpen() {
		return tempControlOpen;
	}

	public void setTempControlOpen(boolean tempControlOpen) {
		this.tempControlOpen = tempControlOpen;
	}

//	@Override
//	public String toString() {
//		return "TempData [tempConstant=" + tempConstant + ", upper=" + upper + ", lower=" + lower + ", time=" + time
//				+ ", syncTempOpen=" + syncTempOpen + ", audioAlertOpen=" + audioAlertOpen + ", tempControlOpen="
//				+ tempControlOpen + "]";
//	}
	

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public String toString() {
		return "TempData [tempConstant=" + tempConstant + ", upper=" + upper + ", lower=" + lower + ", protectMinute="
				+ protectMinute + ", syncTempOpen=" + syncTempOpen + ", tempControlOpen=" + tempControlOpen + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	

	public int getProtectMinute() {
		return protectMinute;
	}

	public void setProtectMinute(int protectMinute) {
		this.protectMinute = protectMinute;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lower);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + protectMinute;
		result = prime * result + (syncTempOpen ? 1231 : 1237);
		temp = Double.doubleToLongBits(tempConstant);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (tempControlOpen ? 1231 : 1237);
		temp = Double.doubleToLongBits(upper);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TempData other = (TempData) obj;
		if (Double.doubleToLongBits(lower) != Double.doubleToLongBits(other.lower))
			return false;
		if (protectMinute != other.protectMinute)
			return false;
		if (syncTempOpen != other.syncTempOpen)
			return false;
		if (Double.doubleToLongBits(tempConstant) != Double.doubleToLongBits(other.tempConstant))
			return false;
		if (tempControlOpen != other.tempControlOpen)
			return false;
		if (Double.doubleToLongBits(upper) != Double.doubleToLongBits(other.upper))
			return false;
		return true;
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

//	@Override
//	public boolean equals(Object obj) {
//		
//		if (obj == null) {
//			
//			return false;
//		} else if (obj instanceof TempData){
//			
//			TempData td = (TempData) obj;
//			if (td.getUnitIndex() == this.unitIndex 
//					&& td.getTempConstant() == this.tempConstant
//					&& td.getUpper() == this.upper
//					&& td.getLower() == this.lower
//					&& td.getTime() == this.time
//					&& td.isSyncTempOpen() == this.syncTempOpen
//					&& td.isAudioAlertOpen() == this.audioAlertOpen
//					&& td.isTempControlOpen() == this.tempControlOpen) {
//				
//				return true;
//			}
//		}
//		return false;
//	}
	
	
}
