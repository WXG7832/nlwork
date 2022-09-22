package com.nltecklib.protocol.li.driver.curr200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.driver.DriverEnvironment.DriverCode;
import com.nltecklib.protocol.li.driver.curr200.DriverMultiDiapTestData.DiapTestModel;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 多模片开关 (0x28)
 * 
 * @author admin
 */
public class DriverDieSwitchData extends Data implements Configable, Queryable, Responsable {

	private boolean OPEN;// 使能开关
	private int diapCount; // 膜片编号

	// private List<Long> currentLst = new ArrayList<>();//膜片程控电流集合

	@Override
	public boolean supportUnit() {
		return false;
	}

	@Override
	public boolean supportDriver() {
		return true;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	@Override
	public void encode() {

		data.add((byte) driverIndex);

		data.add((byte) chnIndex);

		data.add((byte) (OPEN ? 0x01 : 0x00));
		
		//data.addAll(Arrays.asList(ProtocolUtil.split((long)diapCount, 1, true)));

		// data.add((byte) diapCount);

	}

	@Override
	public void decode(List<Byte> encodeData) {

		int index = 0;
		data = encodeData;

		driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

		diapCount = ProtocolUtil.getUnsignedByte(data.get(index++));
		
//		diapCount = (int)ProtocolUtil.compose(data.subList(index, index + 1).toArray(new Byte[0]), true);
//		index += 1;

		// OPEN = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

		// Byte[] lenData = ProtocolUtil.split(diapCount, 2, true);

		// 程控电流
		// currentLst.clear();
		// for (int i = 0; i < bitcount(diapCount); i++) {
		// long specialCurrent = ProtocolUtil.compose(data.subList(index, index +
		// 3).toArray(new Byte[0]), true);
		// index += 3;
		// currentLst.add(specialCurrent);
		// }

	}

	@Override
	public Code getCode() {
		return DriverCode.Driver200aDieSwitchCode;
	}

	public int getDiapCount() {
		return diapCount;
	}

	public void setDiapCount(int diapCount) {
		this.diapCount = diapCount;
	}

	public boolean isOPEN() {
		return OPEN;
	}

	public void setOPEN(boolean OPEN) {
		this.OPEN = OPEN;
	}

	// public List<Long> getCurrentLst() {
	// return currentLst;
	// }
	//
	// public void setCurrentLst(List<Long> currentLst) {
	// this.currentLst = currentLst;
	// }

	public int bitcount(int n) {
		int count = 0;
		while (n != 0) {
			count++;
			n &= (n - 1);
		}
		return count;
	}

}
