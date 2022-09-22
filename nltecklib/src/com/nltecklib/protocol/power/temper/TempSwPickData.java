/**
 * 
 */
package com.nltecklib.protocol.power.temper;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.check.VolPickData.PickData;
import com.nltecklib.protocol.power.temper.TemperEnvironment.TemperCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**   
* 
* @Description: 温度和光电开关采集 功能码0x01 (支持查询) 
* @version: v1.0.0
* @date: 2021年12月29日 下午7:55:36 
*
*/
public class TempSwPickData extends Data implements Queryable, Responsable {

	private int switchCount;
	private int optoSwitch;
	private int temperChnn;
	
	private List<Temper> tempers = new ArrayList<>();
	
	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {

	}

	@Override
	public void decode(List<Byte> encodeData) {
		int index = 0;
		data = encodeData;
		switchCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		optoSwitch = ProtocolUtil.getUnsignedByte(data.get(index++));
		temperChnn = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		tempers.clear();

		for (int n = 0; n < temperChnn; n++) {

			Temper temper = new Temper();

			temper.setIndex(n+1);
			temper.setTemper(
					(double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;

			tempers.add(temper);
		}
	}

	@Override
	public Code getCode() {
		return TemperCode.TempSwPickCode;
	}

	public int getSwitchCount() {
		return switchCount;
	}

	public void setSwitchCount(int switchCount) {
		this.switchCount = switchCount;
	}

	public int getOptoSwitch() {
		return optoSwitch;
	}

	public void setOptoSwitch(int optoSwitch) {
		this.optoSwitch = optoSwitch;
	}

	public int getTemperChnn() {
		return temperChnn;
	}

	public void setTemperChnn(int temperChnn) {
		this.temperChnn = temperChnn;
	}

	public List<Temper> getTempers() {
		return tempers;
	}

	public void setTempers(List<Temper> tempers) {
		this.tempers = tempers;
	}

	public static class Temper{
		private int index;
		private double temper;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public double getTemper() {
			return temper;
		}
		public void setTemper(double temper) {
			this.temper = temper;
		}
		public Temper() {
		}
		public Temper(int index, double temper) {
			this.index = index;
			this.temper = temper;
		}
		
		
	}
}
