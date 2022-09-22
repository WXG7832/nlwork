package com.nltecklib.protocol.li.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 新机械电气状态查询（针床产品专用） 0x38 只支持查询
 * @author: JenHoard_Shaw
 * @date: 2022年6月16日 下午1:42:58
 *
 */
public class PingStateData extends Data implements Queryable, Responsable {

	private boolean trayOffsetOk; //托盘偏移OK?
	private boolean trayBackPosOk;
	private boolean trayFrontPosOk;
	private boolean doorCylinder1PosOk; //true门开
	private boolean doorCylinder2PosOk; //true门关
	private boolean trayCylinder1PosOk; //true 抬起来
	private boolean trayCylinder2PosOk;  //true 下降
	private boolean trayCylinder3PosOk; //true 抬起来
	private boolean trayCylinder4PosOk; //true 下降
	private boolean smogCheck1Ok;
	private boolean smogCheck2Ok;
	
	public static class TempProbe {
		
		public boolean tempOk;
		public int     temperature;
		@Override
		public String toString() {
			return "TempProbe [tempOk=" + tempOk + ", temperature=" + temperature + "]";
		}
		
		
		
	}
	
	public static class Fan {
		
		public boolean open;
		public boolean normal;
		@Override
		public String toString() {
			return "Fan [open=" + open + ", normal=" + normal + "]";
		}
		
	}
	
	
	
	private List<TempProbe> tempProbes = new ArrayList<>();
	private List<Fan> fans = new ArrayList<>();
	

	@Override
	public boolean supportUnit() {

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

	@Override
	public void encode() {

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		unitIndex = data.get(index++);

		
		trayOffsetOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		trayBackPosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		trayFrontPosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		doorCylinder1PosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		doorCylinder2PosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		trayCylinder1PosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		trayCylinder2PosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		trayCylinder3PosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		trayCylinder4PosOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		smogCheck1Ok = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		smogCheck2Ok = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
		
		int probeCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		for(int n = 0 ; n < probeCount ; n++) {
			
			TempProbe probe = new TempProbe();
			probe.tempOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
			
			probe.temperature = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
			index += 2;
			
			tempProbes.add(probe);
			
		}
		
		int fanCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
        for(int n = 0 ; n < fanCount ; n++) {
			
			Fan fan = new Fan();
			fan.open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
			fan.normal = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0;
			fans.add(fan);
			
		}
		
		
	}

	@Override
	public Code getCode() {

		return AccessoryCode.PingStateCode;
	}

	public boolean isTrayOffsetOk() {
		return trayOffsetOk;
	}

	public void setTrayOffsetOk(boolean trayOffsetOk) {
		this.trayOffsetOk = trayOffsetOk;
	}

	public boolean isTrayBackPosOk() {
		return trayBackPosOk;
	}

	public void setTrayBackPosOk(boolean trayBackPosOk) {
		this.trayBackPosOk = trayBackPosOk;
	}

	public boolean isTrayFrontPosOk() {
		return trayFrontPosOk;
	}

	public void setTrayFrontPosOk(boolean trayFrontPosOk) {
		this.trayFrontPosOk = trayFrontPosOk;
	}

	public boolean isDoorCylinder1PosOk() {
		return doorCylinder1PosOk;
	}

	public void setDoorCylinder1PosOk(boolean doorCylinder1PosOk) {
		this.doorCylinder1PosOk = doorCylinder1PosOk;
	}

	public boolean isDoorCylinder2PosOk() {
		return doorCylinder2PosOk;
	}

	public void setDoorCylinder2PosOk(boolean doorCylinder2PosOk) {
		this.doorCylinder2PosOk = doorCylinder2PosOk;
	}

	public boolean isTrayCylinder1PosOk() {
		return trayCylinder1PosOk;
	}

	public void setTrayCylinder1PosOk(boolean trayCylinder1PosOk) {
		this.trayCylinder1PosOk = trayCylinder1PosOk;
	}

	public boolean isTrayCylinder2PosOk() {
		return trayCylinder2PosOk;
	}

	public void setTrayCylinder2PosOk(boolean trayCylinder2PosOk) {
		this.trayCylinder2PosOk = trayCylinder2PosOk;
	}

	public boolean isTrayCylinder3PosOk() {
		return trayCylinder3PosOk;
	}

	public void setTrayCylinder3PosOk(boolean trayCylinder3PosOk) {
		this.trayCylinder3PosOk = trayCylinder3PosOk;
	}

	public boolean isTrayCylinder4PosOk() {
		return trayCylinder4PosOk;
	}

	public void setTrayCylinder4PosOk(boolean trayCylinder4PosOk) {
		this.trayCylinder4PosOk = trayCylinder4PosOk;
	}

	public boolean isSmogCheck1Ok() {
		return smogCheck1Ok;
	}

	public void setSmogCheck1Ok(boolean smogCheck1Ok) {
		this.smogCheck1Ok = smogCheck1Ok;
	}

	public boolean isSmogCheck2Ok() {
		return smogCheck2Ok;
	}

	public void setSmogCheck2Ok(boolean smogCheck2Ok) {
		this.smogCheck2Ok = smogCheck2Ok;
	}

	public List<TempProbe> getTempProbes() {
		return tempProbes;
	}

	public void setTempProbes(List<TempProbe> tempProbes) {
		this.tempProbes = tempProbes;
	}

	public List<Fan> getFans() {
		return fans;
	}

	public void setFans(List<Fan> fans) {
		this.fans = fans;
	}

	@Override
	public String toString() {
		return "PingStateData [trayOffsetOk=" + trayOffsetOk + ", trayBackPosOk=" + trayBackPosOk + ", trayFrontPosOk="
				+ trayFrontPosOk + ", doorCylinder1PosOk=" + doorCylinder1PosOk + ", doorCylinder2PosOk="
				+ doorCylinder2PosOk + ", trayCylinder1PosOk=" + trayCylinder1PosOk + ", trayCylinder2PosOk="
				+ trayCylinder2PosOk + ", trayCylinder3PosOk=" + trayCylinder3PosOk + ", trayCylinder4PosOk="
				+ trayCylinder4PosOk + ", smogCheck1Ok=" + smogCheck1Ok + ", smogCheck2Ok=" + smogCheck2Ok
				+ ", tempProbes=" + tempProbes + ", fans=" + fans + "]";
	}

	


	
	
	

}
