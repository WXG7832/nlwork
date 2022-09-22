package com.nltecklib.protocol.li.PCWorkform;

import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PCWorkformCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月29日 下午5:17:58 类说明
 */
public class SteadyCfgData extends Data implements Configable, Queryable, Responsable {

	private int sampleCount;
	private double maxSigma; // 0.01
	private double maxSigmabackup1;
	private double maxSigmabackup2;
	
	
	private int trailCount; // 需要忽略的头尾个数
	private int adcReadCount;// adc不稳定查询次数
	private int adcRetryDelay;// adc不稳定重试延时
	
	private int adcReadCountCV; //cv模式下不稳定查询次数
	private int adcRetryDelayCV;// cv模式下adc不稳定重试延时

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {

		data.add((byte) sampleCount);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (maxSigma * 100), 2, true)));
		data.add((byte) trailCount);
		data.add((byte) adcReadCount);
		data.addAll(Arrays.asList(ProtocolUtil.split(adcRetryDelay, 2, true)));
		data.add((byte) adcReadCountCV);
		data.addAll(Arrays.asList(ProtocolUtil.split(adcRetryDelayCV, 2, true)));
	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;
		sampleCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		maxSigma = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 100;
		index += 2;
		trailCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		adcReadCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		adcRetryDelay = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
		adcReadCountCV = ProtocolUtil.getUnsignedByte(data.get(index++));
		adcRetryDelayCV = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;
	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return PCWorkformCode.SteadyCode;
	}

	public int getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(int sampleCount) {
		this.sampleCount = sampleCount;
	}

	public double getMaxSigma() {
		return maxSigma;
	}

	public void setMaxSigma(double maxSigma) {
		this.maxSigma = maxSigma;
	}

	public int getTrailCount() {
		return trailCount;
	}

	public void setTrailCount(int trailCount) {
		this.trailCount = trailCount;
	}

	public int getAdcReadCount() {
		return adcReadCount;
	}

	public void setAdcReadCount(int adcReadCount) {
		this.adcReadCount = adcReadCount;
	}

	public int getAdcRetryDelay() {
		return adcRetryDelay;
	}

	public void setAdcRetryDelay(int adcRetryDelay) {
		this.adcRetryDelay = adcRetryDelay;
	}

	public int getAdcReadCountCV() {
		return adcReadCountCV;
	}

	public void setAdcReadCountCV(int adcReadCountCV) {
		this.adcReadCountCV = adcReadCountCV;
	}

	public int getAdcRetryDelayCV() {
		return adcRetryDelayCV;
	}

	public void setAdcRetryDelayCV(int adcRetryDelayCV) {
		this.adcRetryDelayCV = adcRetryDelayCV;
	}


	public double getMaxSigmabackup1() {
		return maxSigmabackup1;
	}

	public void setMaxSigmabackup1(double maxSigmabackup1) {
		this.maxSigmabackup1 = maxSigmabackup1;
	}

	public double getMaxSigmabackup2() {
		return maxSigmabackup2;
	}

	public void setMaxSigmabackup2(double maxSigmabackup2) {
		this.maxSigmabackup2 = maxSigmabackup2;
	}

	@Override
	public String toString() {
		return "SteadyCfgData [sampleCount=" + sampleCount + ", maxSigma=" + maxSigma + ", maxSigmabackup1="
				+ maxSigmabackup1 + ", maxSigmabackup2=" + maxSigmabackup2 + ", trailCount=" + trailCount
				+ ", adcReadCount=" + adcReadCount + ", adcRetryDelay=" + adcRetryDelay + ", adcReadCountCV="
				+ adcReadCountCV + ", adcRetryDelayCV=" + adcRetryDelayCV + "]";
	}

	
	
	
	

	

}
