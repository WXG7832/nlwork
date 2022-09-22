package com.nltecklib.protocol.camera.data;

import java.util.List;

import com.nltecklib.protocol.camera.Data;
import com.nltecklib.protocol.camera.Decode;
import com.nltecklib.protocol.camera.Environment.CameraCode;
import com.nltecklib.protocol.camera.Environment.Code;
import com.nltecklib.protocol.util.ProtocolUtil;
/**
 * 温度综合统计数据协议
 * @author Administrator
 *
 */
public class TempCountData extends Data implements Decode{
	private int maxPosX;
	private int maxPosY;
	private int minPosX;
	private int minPosY;
	private double maxTemp;
	private double minTemp;
	private double avgTemp;
	private double cenTemp;
	public int getMaxPosX() {
		return maxPosX;
	}

	public void setMaxPosX(int maxPosX) {
		this.maxPosX = maxPosX;
	}

	public int getMaxPosY() {
		return maxPosY;
	}

	public void setMaxPosY(int maxPosY) {
		this.maxPosY = maxPosY;
	}

	public int getMinPosX() {
		return minPosX;
	}

	public void setMinPosX(int minPosX) {
		this.minPosX = minPosX;
	}

	public int getMinPosY() {
		return minPosY;
	}

	public void setMinPosY(int minPosY) {
		this.minPosY = minPosY;
	}

	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}

	public double getAvgTemp() {
		return avgTemp;
	}

	public void setAvgTemp(double avgTemp) {
		this.avgTemp = avgTemp;
	}

	public double getCenTemp() {
		return cenTemp;
	}

	public void setCenTemp(double cenTemp) {
		this.cenTemp = cenTemp;
	}

	@Override
	public void decode(List<Byte> encodeData) {
		data.clear();
		data = encodeData;
		int index = 0;
		maxPosX = ProtocolUtil.getUnsignedByte(data.get(index++));
		maxPosY = ProtocolUtil.getUnsignedByte(data.get(index++));
		minPosX = ProtocolUtil.getUnsignedByte(data.get(index++));
		minPosY = ProtocolUtil.getUnsignedByte(data.get(index++));
		int sign = ProtocolUtil.getUnsignedByte(data.get(index++));
		maxTemp = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)/10.0;
		if (sign == 0) {
			maxTemp = -maxTemp;
		}else if (sign == 1) {
		}else {
			throw new RuntimeException("max state error code:" + data.get(index - 1));
		}
		index += 2;
		sign = ProtocolUtil.getUnsignedByte(data.get(index++));
		minTemp = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)/10.0;
		if (sign == 0) {
			minTemp = -minTemp;
		}else if (sign == 1) {
		}else {
			throw new RuntimeException("min state error code:" + data.get(index - 1));
		}
		index += 2;
		sign = ProtocolUtil.getUnsignedByte(data.get(index++));
		avgTemp = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)/10.0;
		if (sign == 0) {
			avgTemp = -avgTemp;
		}else if (sign == 1) {
		}else {
			throw new RuntimeException("avg state error code:" + data.get(index - 1));
		}
		index += 2;
		sign = ProtocolUtil.getUnsignedByte(data.get(index++));
		cenTemp = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)/10.0;
		if (sign == 0) {
			cenTemp = -cenTemp;
		}else if (sign == 1) {
		}else {
			throw new RuntimeException("cen state error code:" + data.get(index - 1));
		}
		index += 2;
	}

	@Override
	public Code getCode() {
		return CameraCode.TEMP_COUNT;
	}

	@Override
	public String toString() {
		return "TempCountData [maxPosX=" + maxPosX + ", maxPosY=" + maxPosY + ", minPosX=" + minPosX + ", minPosY="
				+ minPosY + ", maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", avgTemp=" + avgTemp + ", cenTemp="
				+ cenTemp + "]";
	}

}
