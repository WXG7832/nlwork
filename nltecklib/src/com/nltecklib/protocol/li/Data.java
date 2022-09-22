package com.nltecklib.protocol.li;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.li.Entity.ProtocolType;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.Environment.Orient;
import com.nltecklib.protocol.li.Environment.Result;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
 * 协议数据内容格式
 * 
 * @author Administrator
 *
 */
public abstract class Data implements NlteckIOPackage, UnitSupportable, DriverSupportable, ChnSupportable , Cloneable {

	protected transient List<Byte> data = new LinkedList<Byte>();

	protected transient final int ALLSELECTED_UNIT_CODE = 0xff; // 全区选中标记

	protected transient ProtocolType type;

	protected transient Result result;
	protected transient Orient orient;
	protected transient int unitIndex = 0xff; // 分区号
	protected transient int driverIndex = 0xffff; // 板号
	protected transient int chnIndex = 0xff; // 通道号
	protected transient boolean responseWithData;
	
	private static boolean reverseDriverChnIndex; // 驱动板通道反序

	protected static boolean doubleResolutionSupport; // 是否支持高精度
	protected static int driverChnCount = 8; // 单板通道数
	protected static int logicDriverCount = 8; // 逻辑板接入的驱动板数量

	protected static int voltageResolution = 1; // 电压分辨率,?位小数位
	protected static int currentResolution = 1; // 电流分辨率,?位小数位
	protected static int capacityResolution = 2; // 容量分辨率,?位小数位
	protected static int energyResolution = 2; // 能量单位分辨率,?位小数位

	protected static int resistanceResolution = 7; // 校准板电阻分辨率,?位小数位

	protected static boolean useLogicPowerVoltage = false; // 使用功率电压

	protected static int programKResolution = 7;// 二代程控K值分辨率，？位小数位
	protected static int programBResolution = 7;// 二代程控K值分辨率，？位小数位
	protected static int adcKResolution = 7;// 二代adcK值分辨率，？位小数位
	protected static int adcBResolution = 7;// 二代adcK值分辨率，？位小数位
	
	protected static int moduleCount = 2 ; //模片数量
	
	protected static boolean newPickupProtocol; //使用新采集协议
	
	protected static boolean useMainStepVariable; //使用上位机协议Step多增量
	protected static boolean useMainStepRecordTime; //使用上位机协议Step 记录阀值
	protected static boolean useMainStepVarWaitTime;//使用上位机协议Step增到后停留多长时间
	protected static boolean useProcedureWorkType;  //是否使用流程工序类型?
	
	protected static boolean useChnTemperature; //采集协议使用通道温度记录
	protected static boolean useSleepVoltProtect; //休眠保护增加特殊电压幅度保护
	
	protected static boolean useFrameTemperature; //采集协议使用料框温度记录
	protected static boolean useHugeModuleCount; //单通道是否使用巨量模片(超过8个)
	protected static boolean useHugeDriverChnCount; //单驱动是否使用巨量通道数(板通道数超过16个)

	public static final double DEFAULT_CC_VOLT_UPPER = 4500;
	public static final int DEFAULT_CC_ASC_UNIT_SECOND = 300;
	public static final double DEFAULT_CC_ASC_VAL_LOWER = 1;
	public static final double DEFAULT_CC_CURRENT_OFFSET_PERCENT = 10;
	
	public Data() {

	}

	public abstract void encode(); // 编码

	public abstract void decode(List<Byte> encodeData); // 解码

	public abstract Code getCode(); // 获取功能码

	public int getLength() {
		// 数据区长度
		return data.size();
	}

	public List<Byte> getEncodeData() {

		return data;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public int getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(int unitIndex) {
		this.unitIndex = unitIndex;
	}

	public ProtocolType getType() {

		if (type != null) {
			return type;
		} else {
			return Entity.convertFromCode(getCode());
		}
	}

	public void setType(ProtocolType type) {
		this.type = type;
	}

	public int getDriverIndex() {
		return driverIndex;
	}

	public void setDriverIndex(int driverIndex) {
		this.driverIndex = driverIndex;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}

	public Orient getOrient() {
		return orient;
	}

	public void setOrient(Orient orient) {
		this.orient = orient;
	}

	public void clear() {

		this.data.clear();
	}

	public static boolean isReverseDriverChnIndex() {
		return reverseDriverChnIndex;
	}

	/**
	 * 设置通道反序标记，默认不进行反序
	 * 
	 * @param reverse
	 */
	public static void setReverseDriverChnIndex(boolean reverse) {
		reverseDriverChnIndex = reverse;
	}

	public static boolean isDoubleResolutionSupport() {
		return doubleResolutionSupport;
	}

	public static void setDoubleResolutionSupport(boolean doubleResolutionSupport) {
		Data.doubleResolutionSupport = doubleResolutionSupport;
	}

	public static int getResistanceResolution() {
		return resistanceResolution;
	}

	public static void setResistanceResolution(int resistanceResolution) {
		Data.resistanceResolution = resistanceResolution;
	}

	public static int getProgramKResolution() {
		return programKResolution;
	}

	public static void setProgramKResolution(int programKResolution) {
		Data.programKResolution = programKResolution;
	}

	public static int getProgramBResolution() {
		return programBResolution;
	}

	public static void setProgramBResolution(int programBResolution) {
		Data.programBResolution = programBResolution;
	}

	public static int getAdcKResolution() {
		return adcKResolution;
	}

	public static void setAdcKResolution(int adcKResolution) {
		Data.adcKResolution = adcKResolution;
	}

	public static int getAdcBResolution() {
		return adcBResolution;
	}

	public static void setAdcBResolution(int adcBResolution) {
		Data.adcBResolution = adcBResolution;
	}

	/**
	 * 获取单板通道数
	 * 
	 * @return
	 */
	public static int getDriverChnCount() {
		return driverChnCount;
	}

	public static void setDriverChnCount(int driverChnCount) {
		Data.driverChnCount = driverChnCount;
	}

	public static int getVoltageResolution() {
		return voltageResolution;
	}

	public static void setVoltageResolution(int voltageResolution) {
		Data.voltageResolution = voltageResolution;
	}

	public static int getCurrentResolution() {
		return currentResolution;
	}

	public static void setCurrentResolution(int currentResolution) {
		Data.currentResolution = currentResolution;
	}

	public static int getCapacityResolution() {
		return capacityResolution;
	}

	public static void setCapacityResolution(int capacityResolution) {
		Data.capacityResolution = capacityResolution;
	}

	public static int getEnergyResolution() {
		return energyResolution;
	}

	public static void setEnergyResolution(int energyResolution) {
		Data.energyResolution = energyResolution;
	}

	public static int getLogicDriverCount() {
		return logicDriverCount;
	}

	public static void setLogicDriverCount(int logicDriverCount) {
		Data.logicDriverCount = logicDriverCount;
	}

	public static boolean isUseLogicPowerVoltage() {
		return useLogicPowerVoltage;
	}

	public static void setUseLogicPowerVoltage(boolean useLogicPowerVoltage) {
		Data.useLogicPowerVoltage = useLogicPowerVoltage;
	}
	
	public static boolean isNewPickupProtocol() {
		return newPickupProtocol;
	}

	public static void setNewPickupProtocol(boolean newPickupProtocol) {
		Data.newPickupProtocol = newPickupProtocol;
	}
	
	public static int getModuleCount() {
		return moduleCount;
	}

	public static void setModuleCount(int moduleCount) {
		Data.moduleCount = moduleCount;
	}

	/**
	 * 用于标识通信指令回复识别
	 * @author  wavy_zheng
	 * 2021年1月13日
	 * @return
	 */
	public String getCodeKey() {
		
		return (getType() + "." + getCode().getCode() + "." + unitIndex + "." + driverIndex + "." + chnIndex);
	}

	/**
	 * 唯一码
	 */
	@Override
	public int hashCode() {

		return (getType() + "." + getCode().getCode() + "." + unitIndex + "." + driverIndex + "." + chnIndex)
				.hashCode();
	}

	public static List<Byte> encodeJson(Data data) {

		List<Byte> list = new ArrayList<>();
		Gson gson = new Gson();
		String content = gson.toJson(data);
		try {
			byte[] bytes = content.getBytes("utf-8");
			list.addAll(ProtocolUtil.convertArrayToList(bytes));

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("convert content to bytes error:" + e.getMessage());
		}
		return list;
	}

	public static boolean isUseMainStepVariable() {
		return useMainStepVariable;
	}

	public static void setUseMainStepVariable(boolean useMainStepVariable) {
		Data.useMainStepVariable = useMainStepVariable;
	}

	public static boolean isUseMainStepRecordTime() {
		return useMainStepRecordTime;
	}
	public static void setUseMainStepRecordTime(boolean useMainStepRecordTime) {
		Data.useMainStepRecordTime = useMainStepRecordTime;
	}

	public static boolean isUseChnTemperature() {
		return useChnTemperature;
	}

	public static void setUseChnTemperature(boolean useChnTemperature) {
		Data.useChnTemperature = useChnTemperature;
	}

	public static boolean isUseSleepVoltProtect() {
		return useSleepVoltProtect;
	}

	public static void setUseSleepVoltProtect(boolean useSleepVoltProtect) {
		Data.useSleepVoltProtect = useSleepVoltProtect;
	}
	
	

	public static boolean isUseMainStepVarWaitTime() {
		return useMainStepVarWaitTime;
	}

	public static void setUseMainStepVarWaitTime(boolean useMainStepVarWaitTime) {
		Data.useMainStepVarWaitTime = useMainStepVarWaitTime;
	}

	public static boolean isUseFrameTemperature() {
		return useFrameTemperature;
	}

	public static void setUseFrameTemperature(boolean useFrameTemperature) {
		Data.useFrameTemperature = useFrameTemperature;
	}

	public static boolean isUseProcedureWorkType() {
		return useProcedureWorkType;
	}

	public static void setUseProcedureWorkType(boolean useProcedureWorkType) {
		Data.useProcedureWorkType = useProcedureWorkType;
	}
	
	

    public static boolean isUseHugeModuleCount() {
		return useHugeModuleCount;
	}

	public static void setUseHugeModuleCount(boolean useHugeModuleCount) {
		Data.useHugeModuleCount = useHugeModuleCount;
	}

	public static boolean isUseHugeDriverChnCount() {
		return useHugeDriverChnCount;
	}

	public static void setUseHugeDriverChnCount(boolean useHugeDriverChnCount) {
		Data.useHugeDriverChnCount = useHugeDriverChnCount;
	}


	@Override
    public Object clone() throws CloneNotSupportedException {
    	
    	return super.clone();
    }
    
    

}
