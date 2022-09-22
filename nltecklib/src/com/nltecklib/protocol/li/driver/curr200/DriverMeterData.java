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
 * 计量时, 输出指定电流/电压, 查询时返回程控值 0x26
 * 
 * @author admin
 */
public class DriverMeterData extends Data implements Configable, Queryable, Responsable {
	
    private int diapIndex;// 膜片编号
	
    private boolean POLE_POS;//极性

    private WorkMode workMode;//工作模式
    
    private List<Double> calculateDotLst = new ArrayList<>();// 各个膜片的计量点

    private List<ProgramKB> programKbLst = new ArrayList<DriverMeterData.ProgramKB>();//程控KB,程控值
//	public static final int CAL_BIT_COUNT = 2;
//	public static final int K_BIT_COUNT = 7;
//	public static final int B_BIT_COUNT = 7;

    // 要操作的膜片个数
    //private int diapCount;
    

    // 单膜片的程控KB
    public static class ProgramKB {

	public Double programK;

	public Double programB;

	public Long program;

    }

    public int getDiapIndex() {
	return diapIndex;
    }

    public void setDiapIndex(int diapIndex) {
	this.diapIndex = diapIndex;
    }

//    public int getDiapCount() {
//	return diapCount;
//    }
//
//    public void setDiapCount(int diapCount) {
//	this.diapCount = diapCount;
//    }

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
	//data.add((byte) diapCount);
	data.add((byte) diapIndex);

	data.add((byte) (POLE_POS ? 0x01 : 0x00));

	data.add((byte) workMode.ordinal());

	for (Double dot : calculateDotLst) {
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (dot * Math.pow(10, currentResolution)), 3, true)));

	}
	for (int n = 0; n < programKbLst.size(); n++) {

	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (programKbLst.get(n).programK * Math.pow(10, currentResolution)), 4, true)));
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (programKbLst.get(n).programB * Math.pow(10, currentResolution)), 4, true)));
	    Long program = programKbLst.get(n).program;
	    data.addAll(Arrays.asList(ProtocolUtil.split(program, 2, true)));
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {

	int index = 0;
	data = encodeData;

	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	//diapCount = ProtocolUtil.getUnsignedByte(data.get(index++));
	diapIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	POLE_POS = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;

	int code = ProtocolUtil.getUnsignedByte(data.get(index++));

	if (code > WorkMode.values().length - 1) {

	    throw new RuntimeException("error work mode code : " + code);
	}
	workMode = WorkMode.values()[code];
	calculateDotLst.clear();
	for (int n = 0; n < bitcount(diapIndex); n++) {
	    double calculateDot = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, currentResolution);
	    index += 3;
	    calculateDotLst.add(calculateDot);
	}
	programKbLst.clear();
	for (int n = 0; n < bitcount(diapIndex); n++) {
	    double programK = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, programKResolution);
	    index += 4;
	    double programB = (double) ProtocolUtil.composeSpecialMinus(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, programBResolution);
	    index += 4;
	    long program = ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
	    index += 2;
	    ProgramKB progKB = new ProgramKB();
	    progKB.programK = programK;
	    progKB.programB = programB;
	    progKB.program = program;
	    programKbLst.add(progKB);
	}

    }

    public boolean isPOLE_POS() {
	return POLE_POS;
    }

    public void setPOS_POLE(boolean POLE_POS) {
	this.POLE_POS = POLE_POS;
    }

    @Override
    public Code getCode() {
	return DriverCode.Driver200aMeterCode;
    }

    public WorkMode getWorkMode() {
	return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
	this.workMode = workMode;
    }

    public List<ProgramKB> getProgramKbLst() {
	return programKbLst;
    }

    public void setProgramKbLst(List<ProgramKB> programKbLst) {
	this.programKbLst = programKbLst;
    }

    public List<Double> getCalDotLst() {
	return calculateDotLst;
    }

    public void setCalDotLst(List<Double> calculateDotLst) {
	this.calculateDotLst = calculateDotLst;
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
