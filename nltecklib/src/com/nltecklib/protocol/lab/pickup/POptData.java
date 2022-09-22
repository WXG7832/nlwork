package com.nltecklib.protocol.lab.pickup;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.Opt;
import com.nltecklib.protocol.util.ProtocolUtil;

public class POptData extends Data implements Configable, Responsable, Queryable {

	private Opt opt = Opt.STOP;

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) opt.ordinal());

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Opt.values().length - 1) {

			throw new RuntimeException("error opt code :" + code);
		}
		opt = Opt.values()[code];

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return ChipPickupCode.OptCode;
	}

	public Opt getOpt() {
		return opt;
	}

	public void setOpt(Opt opt) {
		this.opt = opt;
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "OptData [opt=" + opt + "]";
	}

	
}
