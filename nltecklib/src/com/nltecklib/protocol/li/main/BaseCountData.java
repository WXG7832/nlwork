package com.nltecklib.protocol.li.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;

/**
 * 机柜数和分区数配置
 * 
 * @author Administrator
 *
 */
public class BaseCountData extends Data implements Configable, Queryable, Responsable {

	private byte unitCount; // 分区数
	private int logicCount; // 逻辑板数量
	private byte checkCount; // 回检板数
	private byte logicFlag; // 逻辑板使用情况
	private byte checkFlag; // 回检板使用情况
	private int baseLogicDriverCount; // 逻辑板驱动板数
	private int baseDriverChnCount; // 驱动板通道数

	@Override
	public void encode() {

		data.add(unitCount);
		data.add((byte) logicCount);
		data.add(logicFlag);
		data.add(checkCount);
		data.add(checkFlag);
		data.add((byte) baseLogicDriverCount);
		data.add((byte) baseDriverChnCount);

	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		unitCount = data.get(index++);
		logicCount = data.get(index++);
		logicFlag = data.get(index++);
		checkCount = data.get(index++);
		checkFlag  = data.get(index++);
		baseLogicDriverCount = data.get(index++);
		baseDriverChnCount = data.get(index++);

	}

	@Override
	public Code getCode() {

		return MainCode.BaseCountCode;
	}

	public int getLogicCount() {
		return logicCount;
	}

	public void setLogicCount(int logicCount) {
		this.logicCount = logicCount;
	}

	

	public int getBaseLogicDriverCount() {
		return baseLogicDriverCount;
	}

	public void setBaseLogicDriverCount(int baseLogicDriverCount) {
		this.baseLogicDriverCount = baseLogicDriverCount;
	}

	public int getBaseDriverChnCount() {
		return baseDriverChnCount;
	}

	public void setBaseDriverChnCount(int baseDriverChnCount) {
		this.baseDriverChnCount = baseDriverChnCount;
	}

	public byte getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(byte unitCount) {
		this.unitCount = unitCount;
	}

	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}
	

	public byte getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(byte checkCount) {
		this.checkCount = checkCount;
	}

	public byte getLogicFlag() {
		return logicFlag;
	}

	public void setLogicFlag(byte logicFlag) {
		this.logicFlag = logicFlag;
	}

	public byte getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(byte checkFlag) {
		this.checkFlag = checkFlag;
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

}
