package com.nltecklib.protocol.power;

import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.Environment.Orient;
import com.nltecklib.protocol.power.Environment.Result;



public abstract class Data implements NlteckIOPackage,DriverSupportable , ChnSupportable {

	protected List<Byte> data = new LinkedList<Byte>();

	protected Result result = Result.SUCCESS;
	protected Orient orient;
	protected int chnIndex = 0xff; // 通道号
	protected int driverIndex = 0xff; // 从机地址
	protected boolean responseWithData;
	private static boolean reverseDriverChnIndex; //驱动板通道反序
	
	protected static int driverChnCount = 8; // 单板通道数
	
	private static int     voltageResolution = 2; //电压分辨率,?位小数位
	private static int     currentResolution = 1; //电流分辨率,?位小数位
	private static int     capacityResolution = 2; //容量分辨率,?位小数位
	private static int     energyResolution = 2; //能量单位分辨率,?位小数位
	private static int     moduleCount = 2; //单通道模片数量
	
	protected static boolean useHugeModuleCount; //单通道是否使用巨量模片(超过8个)
	protected static boolean useHugeDriverChnCount; //单驱动是否使用巨量通道数(板通道数超过16个)
	

	protected final static int FACTOR_EXP_K = 7;//K值系数
	protected final static int FACTOR_EXP_B = 7;//B值系数
	
	protected final static int BIT_COUNT_K=5;//K值字节数
	protected final static int BIT_COUNT_B=5;//B值字节数
	private static boolean useStringCompress;  //使用字符串压缩方法

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


	public int getChnIndex() {
		return chnIndex;
	}

	public void setChnIndex(int chnIndex) {
		this.chnIndex = chnIndex;
	}
	
	public int getDriverIndex() {
		return driverIndex;
	}

	public void setDriverIndex(int driverIndex) {
		this.driverIndex = driverIndex;
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
	 * @param reverse
	 */
	public static void setReverseDriverChnIndex(boolean reverse) {
		reverseDriverChnIndex = reverse;
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

	public static boolean isUseStringCompress() {
	    return useStringCompress;
	}

	public static void setUseStringCompress(boolean useStringCompress) {
	    Data.useStringCompress = useStringCompress;
	}

	public static int getModuleCount() {
		return moduleCount;
	}

	public static void setModuleCount(int moduleCount) {
		Data.moduleCount = moduleCount;
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
	
	
	
}
