/**
 * 
 */
package com.nltecklib.protocol.lab.test.diap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.ChargeMode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.PolarityMode;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.Power03Range;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.Power03Range2;
import com.nltecklib.protocol.lab.test.diap.DiapTestEnvironment.PrecisionMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 电池充放电、极性、精度设置 功能码0x03支持配置 支持查询
 * @version: v1.0.0
 * @author Admin
 * @date: 2021年11月12日 上午9:43:59
 *
 */
public class PowerItemConfig extends Data implements Configable, Queryable, Responsable {

	private long diapVolReference;// 膜片电压基准
	private long diapCurrReference;// 膜片板电流基准
	
	private List<PowerItem> powerItems = new ArrayList<>();
	
	private Power03Range power03Range;
	
	private Power03Range2 power03Range2;
	
	private long delay;// 指令延迟时间1
	
	/**
	 * 电源板或功率板数量
	 */
	private int total;
	
	
	public static class PowerItem {
		private long pbVolReference;// 电源板电压基准
		private long pbCurrReference;// 电源板电流基准
		
		private long rbVolReference;// 功率板电压基准
		private long rbCurrReference;// 功率板电流基准
		public long getPbVolReference() {
			return pbVolReference;
		}
		public void setPbVolReference(long pbVolReference) {
			this.pbVolReference = pbVolReference;
		}
		public long getPbCurrReference() {
			return pbCurrReference;
		}
		public void setPbCurrReference(long pbCurrReference) {
			this.pbCurrReference = pbCurrReference;
		}
		public long getRbVolReference() {
			return rbVolReference;
		}
		public void setRbVolReference(long rbVolReference) {
			this.rbVolReference = rbVolReference;
		}
		public long getRbCurrReference() {
			return rbCurrReference;
		}
		public void setRbCurrReference(long rbCurrReference) {
			this.rbCurrReference = rbCurrReference;
		}
		
		public PowerItem() {
		}
		
		public PowerItem(long pbVolReference, long pbCurrReference, long rbVolReference, long rbCurrReference) {
			this.pbVolReference = pbVolReference;
			this.pbCurrReference = pbCurrReference;
			this.rbVolReference = rbVolReference;
			this.rbCurrReference = rbCurrReference;
		}
		
	}
	

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
		
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(diapVolReference, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(diapCurrReference, 2, true)));
		data.add((byte) power03Range.ordinal());
		data.add((byte) power03Range2.ordinal());
		data.add((byte) total);
		
		for (PowerItem powerItem : powerItems) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.pbVolReference, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.pbCurrReference, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.rbVolReference, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.rbCurrReference, 2, true)));
		}
		
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(delay, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		diapVolReference = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		diapCurrReference = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		
		
		int code_power03Range = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_power03Range > Power03Range.values().length - 1) {

			throw new RuntimeException("error power03Range mode code : " + code_power03Range);
		}
		power03Range = Power03Range.values()[code_power03Range];
		
		
		
		int code_power03Range2 = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_power03Range2 > Power03Range2.values().length - 1) {

			throw new RuntimeException("error power03Range2 mode code : " + code_power03Range2);
		}
		power03Range2 = Power03Range2.values()[code_power03Range2];
		
		total = ProtocolUtil.getUnsignedByte(data.get(index++));

		powerItems.clear();
		for (int i = 0; i < total; i++) {
			
			PowerItem powerItem = new PowerItem();
			
			powerItem.setPbVolReference(ProtocolUtil.composeSpecialMinus(
					data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			
			powerItem.setPbCurrReference(ProtocolUtil.composeSpecialMinus(
							data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			
			powerItem.setRbVolReference(ProtocolUtil.composeSpecialMinus(
							data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			
			powerItem.setRbCurrReference(
					ProtocolUtil.composeSpecialMinus(
							data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			
			
			powerItems.add(powerItem);
		}
		
		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		

	}

	@Override
	public Code getCode() {
		return DiapTestCode.PowerItemConfig;
	}


	public Power03Range getPower03Range() {
		return power03Range;
	}

	public void setPower03Range(Power03Range power03Range) {
		this.power03Range = power03Range;
	}


	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<PowerItem> getPowerItems() {
		return powerItems;
	}

	public void setPowerItems(List<PowerItem> powerItems) {
		this.powerItems = powerItems;
	}

	public long getDiapVolReference() {
		return diapVolReference;
	}

	public void setDiapVolReference(long diapVolReference) {
		this.diapVolReference = diapVolReference;
	}

	public long getDiapCurrReference() {
		return diapCurrReference;
	}

	public void setDiapCurrReference(long diapCurrReference) {
		this.diapCurrReference = diapCurrReference;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public Power03Range2 getPower03Range2() {
		return power03Range2;
	}

	public void setPower03Range2(Power03Range2 power03Range2) {
		this.power03Range2 = power03Range2;
	}

	

	
}
