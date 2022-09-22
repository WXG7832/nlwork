package com.nltecklib.protocol.fuel.main;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 主控停机流程协议数据——0x0D
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class StopProcessData extends Data implements Configable, Responsable, Queryable {
	private int loadIndex;
	private int oilTempIndex;
	private int oilTemp;
	private int anodeIndex;
	private int anodeTime;
	private int cathodeIndex;
	private int cathodeTime;
	private int n2Index;
	private int n2Time;
	private int oilLoopIndex;

	public int getLoadIndex() {
		return loadIndex;
	}

	public void setLoadIndex(int loadIndex) {
		this.loadIndex = loadIndex;
	}

	public int getOilTempIndex() {
		return oilTempIndex;
	}

	public void setOilTempIndex(int oilTempIndex) {
		this.oilTempIndex = oilTempIndex;
	}

	public int getOilTemp() {
		return oilTemp;
	}

	public void setOilTemp(int oilTemp) {
		this.oilTemp = oilTemp;
	}

	public int getAnodeIndex() {
		return anodeIndex;
	}

	public void setAnodeIndex(int anodeIndex) {
		this.anodeIndex = anodeIndex;
	}

	public int getAnodeTime() {
		return anodeTime;
	}

	public void setAnodeTime(int anodeTime) {
		this.anodeTime = anodeTime;
	}

	public int getCathodeIndex() {
		return cathodeIndex;
	}

	public void setCathodeIndex(int cathodeIndex) {
		this.cathodeIndex = cathodeIndex;
	}

	public int getCathodeTime() {
		return cathodeTime;
	}

	public void setCathodeTime(int cathodeTime) {
		this.cathodeTime = cathodeTime;
	}

	public int getN2Index() {
		return n2Index;
	}

	public void setN2Index(int n2Index) {
		this.n2Index = n2Index;
	}

	public int getN2Time() {
		return n2Time;
	}

	public void setN2Time(int n2Time) {
		this.n2Time = n2Time;
	}

	public int getOilLoopIndex() {
		return oilLoopIndex;
	}

	public void setOilLoopIndex(int oilLoopIndex) {
		this.oilLoopIndex = oilLoopIndex;
	}

	@Override
	public void encode() {
		data.add((byte) loadIndex);
		data.add((byte) oilTempIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split(oilTemp, 2, true))); // 编码
		data.add((byte) anodeIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split(anodeTime, 2, true))); // 编码
		data.add((byte) cathodeIndex);
		data.addAll(Arrays.asList(ProtocolUtil.split(cathodeTime, 2, true))); // 编码
		data.add((byte) n2Index);
		data.addAll(Arrays.asList(ProtocolUtil.split(n2Time, 2, true))); // 编码
		data.add((byte) oilLoopIndex);
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data = encodeData;
		int index = 0;
		loadIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		oilTempIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		oilTemp = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		anodeIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		anodeTime = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		cathodeIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		cathodeTime = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		n2Index = ProtocolUtil.getUnsignedByte(data.get(index++));
		n2Time = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		oilLoopIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	}

	@Override
	public Code getCode() {
		return MainCode.STOP_PROCESS_CODE;
	}

	@Override
	public String toString() {
		return "StopProcessData [loadIndex=" + loadIndex + ", oilTempIndex=" + oilTempIndex + ", oilTemp=" + oilTemp
				+ ", anodeIndex=" + anodeIndex + ", anodeTime=" + anodeTime + ", cathodeIndex=" + cathodeIndex
				+ ", cathodeTime=" + cathodeTime + ", n2Index=" + n2Index + ", n2Time=" + n2Time + ", oilLoopIndex="
				+ oilLoopIndex + "]";
	}

}
