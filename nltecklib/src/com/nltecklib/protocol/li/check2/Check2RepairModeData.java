package com.nltecklib.protocol.li.check2;

import java.util.ArrayList;
import java.util.List;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.check2.Check2Environment.Check2Code;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * »Ø¼ìÎ¬ÐÞ0x21
 * 
 * @author caichao_tang
 *
 */
public class Check2RepairModeData extends Data implements Configable, Responsable, Queryable {

    public class Check2RepairChnData {
	public double backOriginAdc;
	public double backPortAdc;
	public double backVoltage;
	public double powerOriginAdc;
	public double powerPortAdc;
	public double powerVoltage;
    }

    private int repairDriverNo;
    private boolean repairMode;
    private List<Check2RepairChnData> repairChnDataList = new ArrayList<>();

    @Override
    public boolean supportUnit() {
	return true;
    }

    @Override
    public boolean supportDriver() {
	return false;
    }

    @Override
    public boolean supportChannel() {
	return false;
    }

    @Override
    public void encode() {
	data.add((byte) unitIndex);
	data.add((byte) (repairMode ? 1 : 0));
	data.add((byte) repairDriverNo);
    }

    @Override
    public void decode(List<Byte> encodeData) {
	int index = 0;
	data = encodeData;
	unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	repairMode = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
	repairDriverNo = ProtocolUtil.getUnsignedByte(data.get(index++));
	int chnNum = ProtocolUtil.getUnsignedByte(data.get(index++));
	List<Check2RepairChnData> chnDataList = new ArrayList<>();
	for (int i = 0; i < chnNum; i++) {
	    Check2RepairChnData repairChnData = new Check2RepairChnData();
	    repairChnData.backOriginAdc = ((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    repairChnData.backPortAdc = ((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    repairChnData.backVoltage = ((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    repairChnData.powerOriginAdc = ((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    repairChnData.powerPortAdc = ((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    repairChnData.powerVoltage = ((double) ProtocolUtil.composeSpecialMinus(data.subList(index, index += 3).toArray(new Byte[0]), true)) / 10;
	    chnDataList.add(repairChnData);
	}
	repairChnDataList = chnDataList;
    }

    @Override
    public Code getCode() {
	return Check2Code.REPAIR_MODE;
    }

    public int getRepairDriverNo() {
	return repairDriverNo;
    }

    public void setRepairDriverNo(int repairDriverNo) {
	this.repairDriverNo = repairDriverNo;
    }

    public boolean isRepairMode() {
	return repairMode;
    }

    public void setRepairMode(boolean repairMode) {
	this.repairMode = repairMode;
    }

    public List<Check2RepairChnData> getRepairChnDataList() {
	return repairChnDataList;
    }

    public void setRepairChnDataList(List<Check2RepairChnData> repairChnDataList) {
	this.repairChnDataList = repairChnDataList;
    }

}
