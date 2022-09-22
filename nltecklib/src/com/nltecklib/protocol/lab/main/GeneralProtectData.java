package com.nltecklib.protocol.lab.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Data.Generation;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年7月29日 上午9:36:21 支持批量下发超压保护
 */
public class GeneralProtectData extends Data implements Configable, Responsable {

	// 通道选择标记集合
	private List<Byte> chnFlag = new ArrayList<>(64);

	private double firstLevelOverVolt;
	private double secondLevelOverVolt;
	private double overCurr;
	private double chnTempUpper;
	private double chnTempLower;
	private boolean enableTempProtect;

	public GeneralProtectData() {

		for (int n = 0; n < 64; n++) {

			chnFlag.add((byte) 0);
		}
	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {

		data.addAll(chnFlag);
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (firstLevelOverVolt * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (secondLevelOverVolt * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (overCurr * 10), 3, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (chnTempUpper * 10), 2, true)));
		data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (chnTempLower * 10), 2, true)));
		data.add((byte) (enableTempProtect ? 0x01 : 0x00));

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;

		chnFlag = data.subList(index, index + 64);
		index += 64;

		firstLevelOverVolt = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
				/ 10;
		index += 2;
		secondLevelOverVolt = (double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true)
				/ 10;
		index += 2;
		overCurr = (double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / 10;
		index += 3;
		chnTempUpper = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]),
				true) / 10;
		index += 2;
		chnTempLower = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]),
				true) / 10;
		index += 2;
		enableTempProtect = data.get(index++) == 0x01;

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.GeneralCode;
	}

	public double getFirstLevelOverVolt() {
		return firstLevelOverVolt;
	}

	public void setFirstLevelOverVolt(double firstLevelOverVolt) {
		this.firstLevelOverVolt = firstLevelOverVolt;
	}

	public double getSecondLevelOverVolt() {
		return secondLevelOverVolt;
	}

	public void setSecondLevelOverVolt(double secondLevelOverVolt) {
		this.secondLevelOverVolt = secondLevelOverVolt;
	}

	public double getOverCurr() {
		return overCurr;
	}

	public void setOverCurr(double overCurr) {
		this.overCurr = overCurr;
	}

	public double getChnTempUpper() {
		return chnTempUpper;
	}

	public void setChnTempUpper(double chnTempUpper) {
		this.chnTempUpper = chnTempUpper;
	}

	public double getChnTempLower() {
		return chnTempLower;
	}

	public void setChnTempLower(double chnTempLower) {
		this.chnTempLower = chnTempLower;
	}

	public boolean isEnableTempProtect() {
		return enableTempProtect;
	}

	public void setEnableTempProtect(boolean enableTempProtect) {
		this.enableTempProtect = enableTempProtect;
	}

	public void appendChnIndex(int chnIndex) {

		int index = chnIndex / 8;
		int pos = chnIndex % 8;

		if (index > 63) {

			throw new RuntimeException("error byte index :" + index);
		}

		byte b = (byte) (chnFlag.get(index) | 0x01 << pos);
		chnFlag.set(index, b);
	}
	
   public boolean isChnSelected(int chnIndex) {
		
		int index = chnIndex / 8;
		int pos   = chnIndex % 8;
        if(index > 63) {
			
			return false;
		}
       return  (chnFlag.get(index) & 0x01 << pos) > 0 ;
	}

}
