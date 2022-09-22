/**
 * 
 */
package com.nltecklib.protocol.li.test.diap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.ChargeMode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.DiapTestCode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.PolarityMode;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.Power03Range;
import com.nltecklib.protocol.li.test.diap.DiapTestEnvironment.PrecisionMode;
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

	private ChargeMode chargeMode;// 模片充放电
	private PolarityMode polarityMode;// 膜片极性
	private PrecisionMode precisionMode;// 膜片精度
	private long delay;// 指令延迟时间1
	private long da1;
	private long da2;
	private Power03Range power03Range;//档位
	
	/**
	 * 电源板或功率板数量
	 */
	private int total;
	
	private List<PowerItem> powerItems = new ArrayList<>();
	
	public static class PowerItem {
		private long pbVolReference;// 电源板电压基准
		private long pbCurrReference;// 电源板电流基准
		
		private long rbVolReference;// 功率板电压基准
		private long rbCurrReference;// 功率板电流基准
		private long delay;//电源开关开启延时
		private boolean OPEN;// 电源开关
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
		public long getDelay() {
			return delay;
		}
		public void setDelay(long delay) {
			this.delay = delay;
		}
		public boolean isOPEN() {
			return OPEN;
		}
		public void setOPEN(boolean oPEN) {
			OPEN = oPEN;
		}
		public PowerItem() {
		}
		public PowerItem(long pbVolReference, long pbCurrReference, long rbVolReference, long rbCurrReference,
				long delay, boolean oPEN) {
			this.pbVolReference = pbVolReference;
			this.pbCurrReference = pbCurrReference;
			this.rbVolReference = rbVolReference;
			this.rbCurrReference = rbCurrReference;
			this.delay = delay;
			OPEN = oPEN;
		}
		
		
	}
	
	private long delay2;//指令延时2

	@Override
	public boolean supportUnit() {
		return true;
	}

	@Override
	public boolean supportDriver() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return false;
	}

	@Override
	public void encode() {
		data.add((byte) unitIndex);
		data.add((byte) chargeMode.ordinal());
		data.add((byte) polarityMode.ordinal());
		data.add((byte) precisionMode.ordinal());
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) delay, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) da1, 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) da2, 2, true)));
		data.add((byte) power03Range.ordinal());//档位
		data.add((byte) total);
		
		for (PowerItem powerItem : powerItems) {
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.pbVolReference, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.pbCurrReference, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.rbVolReference, 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.rbCurrReference, 2, true)));
			data.add((byte) (powerItem.OPEN ? 0x01 : 0x00));
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus(powerItem.delay, 2, true)));////电源开关开启延时
		}
		
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) delay2, 2, true)));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		
		int code_charge = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_charge > ChargeMode.values().length - 1) {

			throw new RuntimeException("error charge mode code : " + code_charge);
		}
		chargeMode = ChargeMode.values()[code_charge];

		int code_polarity = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_polarity > PolarityMode.values().length - 1) {

			throw new RuntimeException("error polarity mode code : " + code_polarity);
		}
		polarityMode = PolarityMode.values()[code_polarity];

		int code_precision = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_precision > PrecisionMode.values().length - 1) {

			throw new RuntimeException("error precision mode code : " + code_precision);
		}
		precisionMode = PrecisionMode.values()[code_precision];

		delay = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		da1 = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		da2 = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		int code_power03Range = ProtocolUtil.getUnsignedByte(data.get(index++));

		if (code_power03Range > Power03Range.values().length - 1) {

			throw new RuntimeException("error power03Range mode code : " + code_power03Range);
		}
		power03Range = Power03Range.values()[code_power03Range];
		
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
			
			powerItem.setDelay(ProtocolUtil.composeSpecialMinus(
					data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			
			powerItem.setOPEN( ProtocolUtil.getUnsignedByte(data.get(index++)) == 1);
			
			powerItems.add(powerItem);
		}

		delay2 = ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

	}

	@Override
	public Code getCode() {
		return DiapTestCode.PowerItemConfig;
	}

	public ChargeMode getChargeMode() {
		return chargeMode;
	}

	public void setChargeMode(ChargeMode chargeMode) {
		this.chargeMode = chargeMode;
	}

	public PolarityMode getPolarityMode() {
		return polarityMode;
	}

	public void setPolarityMode(PolarityMode polarityMode) {
		this.polarityMode = polarityMode;
	}

	public PrecisionMode getPrecisionMode() {
		return precisionMode;
	}

	public void setPrecisionMode(PrecisionMode precisionMode) {
		this.precisionMode = precisionMode;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getDa1() {
		return da1;
	}

	public void setDa1(long da1) {
		this.da1 = da1;
	}

	public long getDa2() {
		return da2;
	}

	public void setDa2(long da2) {
		this.da2 = da2;
	}

	public Power03Range getPower03Range() {
		return power03Range;
	}

	public void setPower03Range(Power03Range power03Range) {
		this.power03Range = power03Range;
	}

	public long getDelay2() {
		return delay2;
	}

	public void setDelay2(long delay2) {
		this.delay2 = delay2;
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
	

	
}
