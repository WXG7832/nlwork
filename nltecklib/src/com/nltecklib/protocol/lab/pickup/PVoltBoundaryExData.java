package com.nltecklib.protocol.lab.pickup;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.Pole;
import com.nltecklib.protocol.lab.pickup.PickupEnvironment.ChipPickupCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 界定电压功能码 0x19 支持配置，查询
 * @author: JenHoard_Shaw
 * @date: 2022年5月10日 下午1:59:50
 *
 */
public class PVoltBoundaryExData extends Data implements Configable, Queryable, Responsable {

	private PoleEx pole = PoleEx.NORMAL;
	private double boundary;
	
	public enum PoleEx {

		NORMAL , REVERSE;
	}

	@Override
	public boolean supportMain() {
		return false;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) pole.ordinal());// 极性
		data.add((byte) (boundary >= 0 ? 0 : 1));// 电压极性方向
		data.addAll(Arrays.asList(ProtocolUtil.split((long) Math.abs(boundary) * 1000, 4, true)));// 极性界定电压

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;

		int code = ProtocolUtil.getUnsignedByte(data.get(index++));
		if (code > Pole.values().length - 1) {

			throw new RuntimeException("error pole mode code :" + code);
		}
		pole = PoleEx.values()[code];// 极性

		boolean pole = data.get(index++) == 0;// 极性方向	| 0为正 
		// 界定值
		boundary = (pole ? 1 : -1)
				* (double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / 1000;
		index += 4;

	}

	@Override
	public Code getCode() {
		return ChipPickupCode.VoltBoundaryExCode;
	}

	public PoleEx getPole() {
		return pole;
	}

	public void setPole(PoleEx pole) {
		this.pole = pole;
	}

	public double getBoundary() {
		return boundary;
	}

	public void setBoundary(double boundary) {
		this.boundary = boundary;
	}

	@Override
	public String toString() {
		return "PVoltBoundaryExData [pole=" + pole + ", boundary=" + boundary + "]";
	}

}
