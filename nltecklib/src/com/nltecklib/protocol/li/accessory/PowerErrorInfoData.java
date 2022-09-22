/**
 * 
 */
package com.nltecklib.protocol.li.accessory;

import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.AccessoryCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 
 * @Description: 图为电源故障信息查询 0x35 支持查询
 * @author: JenHoard_Shaw
 * @date: 2022年2月9日 上午10:31:35
 *
 */
public class PowerErrorInfoData extends Data implements Queryable, Responsable {

	private boolean communicationOk;// 通信故障
	private boolean fanOk;// 风扇故障
	private boolean busbarOk;// 母线故障
	private boolean rectifierOk;// 整流器故障
	private boolean temperOverOk;// 过温故障
	private boolean noTemperatureSensorOk;// 无温度传感器故障
	private boolean highVolOk;// 充电器高压
	private boolean lowVolOk;// 充电器低压
	private boolean shortCircuitOk;// 充电器短路

	@Override
	public boolean supportUnit() {
		return true;
	}

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

		data.add((byte) unitIndex);
		data.add((byte) driverIndex);
		data.add((byte) (communicationOk ? 0x00 : 0x01));
		data.add((byte) (fanOk ? 0x00 : 0x01));
		data.add((byte) (busbarOk ? 0x00 : 0x01));
		data.add((byte) (rectifierOk ? 0x00 : 0x01));
		data.add((byte) (temperOverOk ? 0x00 : 0x01));
		data.add((byte) (noTemperatureSensorOk ? 0x00 : 0x01));
		data.add((byte) (highVolOk ? 0x00 : 0x01));
		data.add((byte) (lowVolOk ? 0x00 : 0x01));
		data.add((byte) (shortCircuitOk ? 0x00 : 0x01));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
		communicationOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		fanOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		busbarOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		rectifierOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		temperOverOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		noTemperatureSensorOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		highVolOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		lowVolOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;
		shortCircuitOk = ProtocolUtil.getUnsignedByte(data.get(index++)) == 0x00;

	}

	@Override
	public Code getCode() {
		return AccessoryCode.PowerErrorInfoCode;
	}

	public boolean isFanOk() {
		return fanOk;
	}

	public void setFanOk(boolean fanOk) {
		this.fanOk = fanOk;
	}

	public boolean isBusbarOk() {
		return busbarOk;
	}

	public void setBusbarOk(boolean busbarOk) {
		this.busbarOk = busbarOk;
	}

	public boolean isRectifierOk() {
		return rectifierOk;
	}

	public void setRectifierOk(boolean rectifierOk) {
		this.rectifierOk = rectifierOk;
	}

	public boolean isTemperOverOk() {
		return temperOverOk;
	}

	public void setTemperOverOk(boolean temperOverOk) {
		this.temperOverOk = temperOverOk;
	}

	public boolean isNoTemperatureSensorOk() {
		return noTemperatureSensorOk;
	}

	public void setNoTemperatureSensorOk(boolean noTemperatureSensorOk) {
		this.noTemperatureSensorOk = noTemperatureSensorOk;
	}

	public boolean isHighVolOk() {
		return highVolOk;
	}

	public void setHighVolOk(boolean highVolOk) {
		this.highVolOk = highVolOk;
	}

	public boolean isLowVolOk() {
		return lowVolOk;
	}

	public void setLowVolOk(boolean lowVolOk) {
		this.lowVolOk = lowVolOk;
	}

	public boolean isShortCircuitOk() {
		return shortCircuitOk;
	}

	public void setShortCircuitOk(boolean shortCircuitOk) {
		this.shortCircuitOk = shortCircuitOk;
	}

	public boolean isCommunicationOk() {
		return communicationOk;
	}

	public void setCommunicationOk(boolean communicationOk) {
		this.communicationOk = communicationOk;
	}

}
