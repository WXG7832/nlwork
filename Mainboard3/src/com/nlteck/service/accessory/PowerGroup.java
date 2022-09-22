package com.nlteck.service.accessory;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;

/**
 * 电源分组
 * @author Administrator
 *
 */
public class PowerGroup {
    
	protected int  groupIndex; //组序号
	protected List<InverterPower>  powers = new ArrayList<InverterPower>();
	protected Object   data;
	
	
	public PowerGroup(int groupIndex) {
		
		this.groupIndex = groupIndex;
	}
	/**
	 * 该组是否已经打开任意一个电源
	 * @return
	 */
	public boolean isAnyPowerOn() {
		
		for(InverterPower power : powers) {
			
			if(power.getPs() == PowerState.ON) {
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 打开或关闭组内电源
	 * @param powerIndexInGroup
	 * @param pw
	 * @throws AlertException 
	 */
	public  void power( PowerState ps ) throws AlertException {
		
		for(InverterPower power : powers) {
			
			power.power(ps);
		}
	}
	
	/**
	 * 获取组内电源个数
	 * @return
	 */
	public  int  getPowerCount() {
		
		return powers.size();
	}
	
	public void appendPower(InverterPower power) {
		
		powers.add(power);
	}
	
	public InverterPower getPowerByIndex(int indexInGroup) {
		
		return powers.get(indexInGroup);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
	
}
