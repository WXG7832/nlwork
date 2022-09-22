package com.nltecklib.protocol.li.logic2;

import java.util.ArrayList;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 0x2e维修模式
 * 
 * @author caichao_tang
 *
 */
public class Logic2RepairModeData extends Data implements Configable, Responsable, Queryable {
    public class Logic2ChnRepairData {
	public int chnIndex;
	public double voltageAdc;
	public double voltageFirst;
	public double voltageReal;
	public double currentAdc;
	public double currentFirst;
	public double currentReal;
    }

    private double temp1pick;
    private int temp1;
    private double temp2pick;
    private int temp2;
    private double base1;
    private double base2;
    private List<Logic2ChnRepairData> logic2ChnRepairDataList = new ArrayList<>();

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
	return true;
    }

    @Override
    public void encode() {
	// 分区
	data.add((byte) unitIndex);
	// 工作模式
	data.add((byte) driverIndex);
	// 驱动板号
	data.add((byte) chnIndex);
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	temp1pick = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	temp1 = ProtocolUtil.getUnsignedByte(data.get(index++));
	temp2pick = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	temp2 = ProtocolUtil.getUnsignedByte(data.get(index++));
	base1 = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	base2 = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	int chnNum = ProtocolUtil.getUnsignedByte(data.get(index++));
	List<Logic2ChnRepairData> chnDataList = new ArrayList<>();
	for (int i = 0; i < chnNum; i++) {
	    Logic2ChnRepairData logic2ChnRepairData = new Logic2ChnRepairData();
	    logic2ChnRepairData.chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    logic2ChnRepairData.voltageAdc = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	    logic2ChnRepairData.voltageFirst = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	    logic2ChnRepairData.voltageReal = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	    logic2ChnRepairData.currentAdc = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	    logic2ChnRepairData.currentFirst = ((double) ProtocolUtil.compose(data.subList(index, index += 2).toArray(new Byte[0]), true)) / 10;
	    logic2ChnRepairData.currentReal = ((double) ProtocolUtil.compose(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    chnDataList.add(logic2ChnRepairData);
	}
	logic2ChnRepairDataList = chnDataList;
    }

    @Override
    public Code getCode() {
	return Logic2Code.REPAIR_MODE;
    }

    public double getTemp1pick() {
	return temp1pick;
    }

    public void setTemp1pick(double temp1pick) {
	this.temp1pick = temp1pick;
    }

    public int getTemp1() {
	return temp1;
    }

    public void setTemp1(int temp1) {
	this.temp1 = temp1;
    }

    public double getTemp2pick() {
	return temp2pick;
    }

    public void setTemp2pick(double temp2pick) {
	this.temp2pick = temp2pick;
    }

    public int getTemp2() {
	return temp2;
    }

    public void setTemp2(int temp2) {
	this.temp2 = temp2;
    }

    public double getBase1() {
	return base1;
    }

    public void setBase1(double base1) {
	this.base1 = base1;
    }

    public double getBase2() {
	return base2;
    }

    public void setBase2(double base2) {
	this.base2 = base2;
    }

    public List<Logic2ChnRepairData> getLogic2ChnRepairDataList() {
	return logic2ChnRepairDataList;
    }

    public void setLogic2ChnRepairDataList(List<Logic2ChnRepairData> logic2ChnRepairDataList) {
	this.logic2ChnRepairDataList = logic2ChnRepairDataList;
    }

}
