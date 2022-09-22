package com.nltecklib.protocol.li.logic2;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.logic.LogicEnvironment.PickupState;
import com.nltecklib.protocol.li.logic2.Logic2Environment.AlertCode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.ChnState;
import com.nltecklib.protocol.li.logic2.Logic2Environment.Logic2Code;
import com.nltecklib.protocol.li.logic2.Logic2Environment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2020年10月26日 下午7:08:09 类说明
 */
public class Logic2PickupTestData extends Data implements Queryable, Responsable {

    private int temperature1;
    private int temperature2;
    private PickupState logicPickupState; // 逻辑板采集状态
    private PickupState driverPickupState; // 驱动板采集状态
    private WorkMode workMode; // 工作模式
    private List<ChnData> chnDatas = new LinkedList<ChnData>(); // 通道集合

    /**
     * 测试单通道数据
     * 
     * @author wavy_zheng 2020年10月26日
     *
     */
    public class ChnData {

	private int chnIndex; // 逻辑板通道序号
	private double voltage;
	private double current;
	private double capacity;// 通道
	private double totalCapacity;// 累计容量
	private long stepElapsedTime;// 步次流逝时间ms
	private int loopIndex;// 步次循环号，第几遍循环
	private int stepIndex;// 步次序号，从1开始
	private AlertCode alertCode;
	private ChnState state = ChnState.UDT;
	private WorkMode workMode;

	private Date date;

	private double alertVolt;
	private double alertCurrent;
	private long voltageAD;
	private long currentAD;

	public int getChnIndex() {
	    return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
	    this.chnIndex = chnIndex;
	}

	public double getVoltage() {
	    return voltage;
	}

	public void setVoltage(double voltage) {
	    this.voltage = voltage;
	}

	public double getCurrent() {
	    return current;
	}

	public void setCurrent(double current) {
	    this.current = current;
	}

	public double getCapacity() {
	    return capacity;
	}

	public void setCapacity(double capacity) {
	    this.capacity = capacity;
	}

	public double getTotalCapacity() {
	    return totalCapacity;
	}

	public void setTotalCapacity(double totalCapacity) {
	    this.totalCapacity = totalCapacity;
	}

	public long getStepElapsedTime() {
	    return stepElapsedTime;
	}

	public void setStepElapsedTime(long stepElapsedTime) {
	    this.stepElapsedTime = stepElapsedTime;
	}

	public int getLoopIndex() {
	    return loopIndex;
	}

	public void setLoopIndex(int loopIndex) {
	    this.loopIndex = loopIndex;
	}

	public int getStepIndex() {
	    return stepIndex;
	}

	public void setStepIndex(int stepIndex) {
	    this.stepIndex = stepIndex;
	}

	public AlertCode getAlertCode() {
	    return alertCode;
	}

	public void setAlertCode(AlertCode alertCode) {
	    this.alertCode = alertCode;
	}

	public ChnState getState() {
	    return state;
	}

	public void setState(ChnState state) {
	    this.state = state;
	}

	public WorkMode getWorkMode() {
	    return workMode;
	}

	public void setWorkMode(WorkMode workMode) {
	    this.workMode = workMode;
	}

	public Date getDate() {
	    return date;
	}

	public void setDate(Date date) {
	    this.date = date;
	}

	public double getAlertVolt() {
	    return alertVolt;
	}

	public void setAlertVolt(double alertVolt) {
	    this.alertVolt = alertVolt;
	}

	public double getAlertCurrent() {
	    return alertCurrent;
	}

	public void setAlertCurrent(double alertCurrent) {
	    this.alertCurrent = alertCurrent;
	}

	public long getVoltageAD() {
	    return voltageAD;
	}

	public void setVoltageAD(long voltageAD) {
	    this.voltageAD = voltageAD;
	}

	public long getCurrentAD() {
	    return currentAD;
	}

	public void setCurrentAD(long currentAD) {
	    this.currentAD = currentAD;
	}

    }

    public int getTemperature1() {
	return temperature1;
    }

    public void setTemperature1(int temperature1) {
	this.temperature1 = temperature1;
    }

    public int getTemperature2() {
	return temperature2;
    }

    public void setTemperature2(int temperature2) {
	this.temperature2 = temperature2;
    }

    public PickupState getLogicPickupState() {
	return logicPickupState;
    }

    public void setLogicPickupState(PickupState logicPickupState) {
	this.logicPickupState = logicPickupState;
    }

    public PickupState getDriverPickupState() {
	return driverPickupState;
    }

    public void setDriverPickupState(PickupState driverPickupState) {
	this.driverPickupState = driverPickupState;
    }

    public WorkMode getWorkMode() {
	return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
	this.workMode = workMode;
    }

    public List<ChnData> getChnDatas() {
	return chnDatas;
    }

    public void setChnDatas(List<ChnData> chnDatas) {
	this.chnDatas = chnDatas;
    }

    @Override
    public boolean supportUnit() {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public boolean supportDriver() {
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

	data.add((byte) unitIndex); // 查询，回复都要分区序号
	data.add((byte) driverIndex); // 板号
	data.add((byte) temperature1); // 温度1
	data.add((byte) temperature2); // 温度2
	data.add((byte) logicPickupState.ordinal()); // 逻辑板状态
	data.add((byte) driverPickupState.ordinal()); // 驱动板状态
	data.add((byte) chnDatas.size());

	for (int n = 0; n < chnDatas.size(); n++) {

	    ChnData chnData = chnDatas.get(n);
	    int chnIndexInLogic = chnData.getChnIndex();
	    if (isReverseDriverChnIndex()) {

		chnIndexInLogic = Data.getDriverChnCount() - 1 - n;
	    }
	    // 通道序号
	    data.add((byte) chnIndexInLogic);
	    // 通道状态
	    data.add((byte) chnData.getState().getCode());
	    data.add((byte) chnData.getWorkMode().ordinal()); // 工作模式
	    // 步次循环号
	    data.add((byte) chnData.getLoopIndex());
	    // 步次序号
	    data.add((byte) chnData.getStepIndex());
	    // 电压
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getVoltage() * 10), 2, true)));
	    // 电流
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getCurrent() * Math.pow(10, Data.getCurrentResolution())), 3, true)));
	    // 容量
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getCapacity() * Math.pow(10, Data.getCapacityResolution())), 4, true)));

	    // 步次流逝时间
	    data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getStepElapsedTime(), 4, true)));

	    // 报警码
	    data.add((byte) chnData.getAlertCode().ordinal());
	    // 报警电压
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertVolt() * 10), 2, true)));
	    // 电压AD
	    data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getVoltageAD(), 3, true)));
	    // 报警电流
	    data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getAlertCurrent() * Math.pow(10, Data.getCurrentResolution())), 3, true)));
	    // 电流AD
	    data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getCurrentAD(), 3, true)));

	}

    }

    @Override
    public void decode(List<Byte> encodeData) {

	data = encodeData;
	int index = 0;

	unitIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	driverIndex = ProtocolUtil.getUnsignedByte(data.get(index++));

	// 板载温度1
	temperature1 = ProtocolUtil.getUnsignedByte(data.get(index++));
	// 板载温度2
	temperature2 = ProtocolUtil.getUnsignedByte(data.get(index++));

	// 分区状态
	int stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (stateIndex > PickupState.values().length - 1) {

	    throw new RuntimeException("error logic pickup state index :" + stateIndex);
	}
	logicPickupState = PickupState.values()[stateIndex];

	stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	if (stateIndex > PickupState.values().length - 1) {

	    throw new RuntimeException("error driver pickup state index :" + stateIndex);
	}
	driverPickupState = PickupState.values()[stateIndex];

	int chnCount = ProtocolUtil.getUnsignedByte(data.get(index++)); // 通道包数

	chnDatas.clear();

	for (int n = 0; n < chnCount; n++) {

	    ChnData chnData = new ChnData();

	    int chnIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    // 通道序号
	    chnData.setChnIndex(Data.isReverseDriverChnIndex() ? Data.getDriverChnCount() - 1 - chnIndex : chnIndex);
	    // 通道状态
	    stateIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    if (ChnState.valueOf(stateIndex) == null) {

		throw new RuntimeException("error channel(" + (n + 1) + ") state index :" + stateIndex);
	    }
	    chnData.setState(ChnState.valueOf(stateIndex));
	    // 通道工作模式
	    int modeIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    if (modeIndex > WorkMode.values().length - 1) {

		throw new RuntimeException("error work mode index :" + modeIndex);
	    }
	    chnData.setWorkMode(WorkMode.values()[modeIndex]);

	    // 步次循环号
	    chnData.setLoopIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));
	    // 步次序号
	    chnData.setStepIndex(ProtocolUtil.getUnsignedByte(data.get(index++)));

	    // 通道电压
	    chnData.setVoltage((double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
	    index += 2;
	    // 通道电流
	    chnData.setCurrent((double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, Data.getCurrentResolution()));
	    index += 3;
	    // 容量
	    chnData.setCapacity((double) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true) / Math.pow(10, Data.getCapacityResolution()));
	    index += 4;

	    // 步次流逝时间
	    chnData.setStepElapsedTime(ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true));
	    index += 4;

	    // 报警码
	    int alertIndex = ProtocolUtil.getUnsignedByte(data.get(index++));
	    if (alertIndex > AlertCode.values().length - 1) {

		throw new RuntimeException("error channel(" + (n + 1) + ") alert code :" + alertIndex);
	    }
	    chnData.setAlertCode(AlertCode.values()[alertIndex]);
	    // 报警电压
	    chnData.setAlertVolt((double) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
	    index += 2;
	    // 电压AD值
	    chnData.setVoltageAD(ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true));
	    index += 3;
	    // 报警电流
	    chnData.setAlertCurrent((double) ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true) / Math.pow(10, Data.getCurrentResolution()));
	    index += 3;
	    // 电流AD值
	    chnData.setCurrentAD(ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true));
	    index += 3;

	}

    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return Logic2Code.PickupTestCode;
    }

}
