package com.nltecklib.protocol.li.workform;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.Pole;
import com.nltecklib.protocol.li.workform.WorkformEnvironment.WorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 校准板基准模式配置和查询
 * 
 * @author Administrator
 *
 */
public class CalWorkformModeData extends Data implements Configable, Queryable, Responsable {

	private Pole pole = Pole.NORMAL;
	private boolean open; // 打开校准板
	private WorkMode workMode = WorkMode.CCC;
	private int precision; // 高精度?

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		// 校准板号
		data.add((byte) driverIndex);
		// 通道号
		data.add((byte) chnIndex);
		// 开关
		data.add((byte) (open ? 0x01 : 0x00));
		// 极性
		data.add((byte) pole.ordinal());
		// 工作模式
		data.add((byte) workMode.ordinal());
		// 精度
		if (isDoubleResolutionSupport()) {

			// 支持双精度
			data.add((byte) precision);
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 通道
		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		// 校准板开关
		open = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x01;
		// 极性
		int flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= Pole.values().length) {

			throw new RuntimeException("the pole value is error:" + flag);
		}
		pole = Pole.values()[flag];
		//
		flag = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (flag >= WorkMode.values().length) {

			throw new RuntimeException("the workMode value is error:" + flag);
		}
		workMode = WorkMode.values()[flag];
		// 双精度
		if (isDoubleResolutionSupport()) {

			precision = ProtocolUtil.getUnsignedByte(data.get(index++));
		}

	}

	@Override
	public Code getCode() {

		return WorkformCode.CalModeCode;
	}

	public Pole getPole() {
		return pole;
	}

	public void setPole(Pole pole) {
		this.pole = pole;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public WorkMode getWorkMode() {
		return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
		this.workMode = workMode;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}



}
