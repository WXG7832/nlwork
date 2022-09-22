package com.nltecklib.protocol.lab;

import java.util.LinkedList;
import java.util.List;

import com.nltecklib.io.NlteckIOPackage;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.Environment.Orient;
import com.nltecklib.protocol.lab.Environment.Result;

public abstract class Data implements NlteckIOPackage, MainSupportable, ChnSupportable, Cloneable {

	protected transient List<Byte> data = new LinkedList<Byte>();

	/**
	 * json系列化和反序列化忽略字段
	 */
	protected transient Result result = Result.SUCCESS;
	protected transient Orient orient;
	protected transient int chnIndex = 0xff; // 通道号
	protected transient int mainIndex = 0xff; // 主控地址
	protected transient boolean responseWithData;

	/**
	 * 常规协议配置
	 */
	private static boolean reverseDriverChnIndex; // 驱动板通道反序
	private static boolean doubleResolutionSupport; // 是否支持高精度
	private static boolean useTotalMiliseconds; // 采集数据中是否带累计时间ms
	private static boolean useAndStepCondition; // 开启且条件流程转步条件
	private static boolean useStringCompress; // 使用字符串压缩方法
	private static boolean usePickupCapacity; // 启用采集容量
	private static boolean useOfflineTestname; // 离线数据带测试名
	private static boolean useCcCvDescProtect; // 是否启用cccv压降保护
	private static boolean useChnResponse; // 驱动板返回协议支持带通道序号,在结果码以后
	private static boolean useModuleCal; // 膜片校正--上中协议中使用

	protected static int coreBoardCount = 16; // 主控板的数量
	protected static int maxCoreChnCount = 8; // 单个主控板的最大通道数

	private static int voltageResolution = 2; // 电压分辨率,?位小数位
	private static int currentResolution = 2; // 电流分辨率,?位小数位
	private static int capacityResolution = 2; // 容量分辨率,?位小数位
	private static int energyResolution = 2; // 能量单位分辨率,?位小数位

	private static Generation generation = Generation.TH1; // 为了兼容1代实验室协议，默认使用1代协议

	/**
	 * 实验室代数
	 * 
	 * @author wavy_zheng 2022年5月11日
	 *
	 */
	public enum Generation {

		TH1 /* 1代实验室 */, ND2 /* 2代实验室 */
	}

	protected final static int FACTOR_EXP_K = 7;// K值系数
	protected final static int FACTOR_EXP_B = 7;// B值系数

	protected final static int BIT_COUNT_K = 5;// K值字节数
	protected final static int BIT_COUNT_B = 5;// B值字节数

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

	public int getMainIndex() {
		return mainIndex;
	}

	public void setMainIndex(int mainIndex) {
		this.mainIndex = mainIndex;
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

	public static int getCoreBoardCount() {
		return coreBoardCount;
	}

	public static void setCoreBoardCount(int coreBoardCount) {
		Data.coreBoardCount = coreBoardCount;
	}

	public static int getMaxCoreChnCount() {
		return Data.maxCoreChnCount;
	}

	public static void setMaxCoreChnCount(int chnCount) {
		Data.maxCoreChnCount = chnCount;
	}

	public static boolean isUseTotalMiliseconds() {
		return useTotalMiliseconds;
	}

	public static void setUseTotalMiliseconds(boolean useTotalMiliseconds) {
		Data.useTotalMiliseconds = useTotalMiliseconds;
	}

	public static boolean isUseAndStepCondition() {
		return useAndStepCondition;
	}

	public static void setUseAndStepCondition(boolean useAndStepCondition) {
		Data.useAndStepCondition = useAndStepCondition;
	}

	public static boolean isUseStringCompress() {
		return useStringCompress;
	}

	public static void setUseStringCompress(boolean useStringCompress) {
		Data.useStringCompress = useStringCompress;
	}

	public static boolean isUsePickupCapacity() {
		return usePickupCapacity;
	}

	public static void setUsePickupCapacity(boolean usePickupCapacity) {
		Data.usePickupCapacity = usePickupCapacity;
	}

	public static boolean isUseOfflineTestname() {
		return useOfflineTestname;
	}

	public static void setUseOfflineTestname(boolean useOfflineTestname) {
		Data.useOfflineTestname = useOfflineTestname;
	}

	public static boolean isUseCcCvDescProtect() {
		return useCcCvDescProtect;
	}

	public static void setUseCcCvDescProtect(boolean useCcCvDescProtect) {
		Data.useCcCvDescProtect = useCcCvDescProtect;
	}

	public static boolean isUseChnResponse() {
		return useChnResponse;
	}

	public static void setUseChnResponse(boolean useChnResponse) {
		Data.useChnResponse = useChnResponse;
	}

	public static Generation getGeneration() {
		return generation;
	}

	public static void setGeneration(Generation generation) {
		Data.generation = generation;
	}

	public static boolean isUseModuleCal() {
		return useModuleCal;
	}

	public static void setUseModuleCal(boolean useModuleCal) {
		Data.useModuleCal = useModuleCal;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		return super.clone();
	}

}
