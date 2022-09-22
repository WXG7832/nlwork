package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.ComponentSupportable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控PID协议数据
 * 
 * @author caichao_tang
 *
 */
public class PIDData extends Data implements ComponentSupportable, Configable, Responsable, Queryable {
	private double p;
	private double i;
	private double d;

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public double getI() {
		return i;
	}

	public void setI(double i) {
		this.i = i;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	@Override
	public void encode() {
		data.addAll(Arrays.asList(ProtocolUtil.split((int) (p * 10), 2, true))); // 编码
		data.addAll(Arrays.asList(ProtocolUtil.split((int) (i * 10), 2, true))); // 编码
		data.addAll(Arrays.asList(ProtocolUtil.split((int) (d * 10), 2, true))); // 编码
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		double pRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		p = pRecv / 10;
		index += 2;
		double iRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		i = iRecv / 10;
		index += 2;
		double dRecv = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		d = dRecv / 10;
		index += 2;
	}

	@Override
	public Code getCode() {
		 return MainCode.PID_CODE;
	}

	@Override
	public String toString() {
		return "PIDData [p=" + p + ", i=" + i + ", d=" + d + ", componentCode=" + component + "]";
	}

}
