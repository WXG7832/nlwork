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
import com.nltecklib.protocol.li.driver.DriverEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 多膜片测试0x27 支持查询 （ 200A 用） @Desc：
 * 
 * @author：LLC
 * @Date：2021年10月25日 下午1:12:51
 * @version
 */
public class DriverMultiDiapTestData extends Data implements Configable, Queryable, Responsable {

    // 要操作的膜片个数
    private int diapCount;
    // 是否正极性
    private boolean positivePole;
    // 工作方式
    private WorkMode workMode;
    // 精度3个档
    private short highPrecision;
    // 程控电压
    private long specialVoltage;
    // 各膜片的程控电流
    private List<DiapTestModel> diapTestModels = new ArrayList<DiapTestModel>();

    // 单膜片的程控电流
    public static class DiapTestModel {
	public long specialCurrent;

	public DiapTestModel(long specialCurrent) {
	    this.specialCurrent = specialCurrent;
	}
    }

    public int getDiapCount() {
	return diapCount;
    }

    public void setDiapCount(int diapCount) {
	this.diapCount = diapCount;
    }

    @Override
    public boolean supportUnit() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean supportDriver() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public boolean supportChannel() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public void encode() {

	data.add((byte) driverIndex);

	data.add((byte) chnIndex);

	data.add((byte) diapCount);

	data.add((byte) (positivePole ? 0x01 : 0x00));

	data.add((byte) workMode.ordinal());

	data.add((byte) highPrecision);

	data.addAll(Arrays.asList(ProtocolUtil.split(specialVoltage, 2, true)));

	for (int n = 0; n < diapTestModels.size(); n++) {

	    data.addAll(Arrays.asList(ProtocolUtil.split(diapTestModels.get(n).specialCurrent, 3, true)));
	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	int index = 0;
	data = encodeData;

	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	diapCount = ProtocolUtil.getUnsignedByte(data.get(index++));

	positivePole = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	int code = ProtocolUtil.getUnsignedByte(data.get(index++));

	if (code > WorkMode.values().length - 1) {

	    throw new RuntimeException("error work mode code : " + code);
	}
	workMode = WorkMode.values()[code];

	highPrecision = (short) ProtocolUtil.getUnsignedByte(data.get(index++));

	specialVoltage = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);

	index += 2;
	diapTestModels.clear();

	for (int n = 0; n < bitcount(diapCount); n++) {

	    // 电流
	    long specialCurrent = ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true);
	    index += 3;
	    diapTestModels.add(new DiapTestModel(specialCurrent));
	}

    }

    @Override
    public Code getCode() {
	return DriverCode.Driver200aMultiDiapTestCode;
    }

    public boolean isPositivePole() {
	return positivePole;
    }

    public void setPositivePole(boolean positivePole) {
	this.positivePole = positivePole;
    }

    public WorkMode getWorkMode() {
	return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
	this.workMode = workMode;
    }

    public short getHighPrecision() {
	return highPrecision;
    }

    public long getSpecialVoltage() {
	return specialVoltage;
    }

    public void setHighPrecision(short highPrecision) {
	this.highPrecision = highPrecision;
    }

    public void setSpecialVoltage(long specialVoltage) {
	this.specialVoltage = specialVoltage;
    }

    public List<DiapTestModel> getDiapTestModels() {
	return diapTestModels;
    }

    public void setDiapTestModels(List<DiapTestModel> diapTestModels) {
	this.diapTestModels = diapTestModels;
    }
    
    public int bitcount(int n)
    {
        int count=0 ;
        while (n!=0) {
            count++ ;
            n &= (n - 1) ;
        }
        return count ;
    }

}
