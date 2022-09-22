/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 电源开关配置功能码 0x08 支持配置 支持查询
* @version: v1.0.0
* @author: Admin
* @date: 2021年11月19日 下午1:48:00 
*
*/
public class DiapPowerSwitchData extends Data implements Configable, Queryable, Responsable {

	private long powerSwitch;//开关编号
	private long delay;// 指令延迟时间
	 
	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerSwitch, 4, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(delay, 2, true)));
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		powerSwitch = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true);
		index += 4;
		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		
	}

	@Override
	public Code getCode() {
		return DiapTestCode.PowerSwitch;
	}

	public long getPowerSwitch() {
		return powerSwitch;
	}

	public void setPowerSwitch(long powerSwitch) {
		this.powerSwitch = powerSwitch;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	

}
