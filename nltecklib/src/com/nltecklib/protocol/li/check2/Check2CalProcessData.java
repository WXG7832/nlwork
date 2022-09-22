package com.nltecklib.protocol.li.check2;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.AdcGroup;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.check2.Check2Environment.Work;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 回检板主要校准通信协议
 * 
 * @author Administrator
 *
 */
public class Check2CalProcessData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private VoltMode voltMode = VoltMode.Backup;// 备份电压还是功率电压
	private Work work = Work.NONE;// 工作
	private List<AdcGroup> adcs = new ArrayList<AdcGroup>();

	public Check2CalProcessData() {

	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
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
		return true;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public List<AdcGroup> getAdcs() {
		return adcs;
	}

	public void setAdcs(List<AdcGroup> adcs) {
		this.adcs = adcs;
	}

	@Override
	public void encode() {

		data.add((byte) unitIndex);
		if (isReverseDriverChnIndex()) {

			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());
		}

		data.add((byte) chnIndex);
		data.add((byte) pole.ordinal());
		data.add((byte) voltMode.ordinal());
		data.add((byte) work.ordinal());
		data.add((byte) adcs.size()); // 下发不带数据

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (isReverseDriverChnIndex()) {

			chnIndex = ProtocolUtil.reverseChnIndexInLogic(chnIndex, Data.getDriverChnCount());

		}

		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];

		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= VoltMode.values().length) {

			throw new RuntimeException("the voltmode value is error:" + flag);
		}
		voltMode = VoltMode.values()[flag];

		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Work.values().length) {

			throw new RuntimeException("the work value is error:" + flag);
		}
		work = Work.values()[flag];

		int count = ProtocolUtil.getUnsignedByte(data.get(index++));
		for (int n = 0; n < count; n++) {
            
			AdcGroup group = new AdcGroup();
			// adc
			group.adc2 = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, voltageResolution);
			index += 3;
			
			group.adc1 = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, voltageResolution);
			index += 3;
			adcs.add(group);
		}

	}

	@Override
	public Code getCode() {

		return Check2Code.ChnCalCode;
	}

	public VoltMode getVoltMode() {
		return voltMode;
	}

	public void setVoltMode(VoltMode voltMode) {
		this.voltMode = voltMode;
	}

	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

}
