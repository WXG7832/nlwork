package com.nltecklib.protocol.fins;

import java.util.List;
import java.util.Map;

import com.nltecklib.io.mina.NetworkException;
import com.nltecklib.protocol.fins.Environment.Area;
import com.nltecklib.protocol.fins.Environment.Error;
import com.nltecklib.protocol.fins.Environment.Result;
import com.nltecklib.protocol.fins.Environment.Type;
import com.nltecklib.protocol.plc2.pief44.model.Alert;
import com.nltecklib.protocol.plc2.pief44.model.PressKB;
import com.nltecklib.protocol.plc2.pief44.model.Speed;

/**
 * @ClassName: PIEFPlcData
 * @Description: PIEF数据接口
 * @author zhang_longyong
 * @date 2019年12月13日
 */
public interface PIEFPlcData {

	/**
	 * 握手
	 * 
	 * @return
	 * @throws NetworkException
	 */
	Error handShake() throws NetworkException;

	/**
	 * 通道温度采集
	 * 
	 * @param fixtureIndex
	 *            :夹具号
	 * @param isIC
	 *            true:化成;false:分容
	 * @return 该夹具上所有通道的温度
	 * @throws NetworkException
	 */
	Map<Integer, Integer> readTemperature(int fixtureIndex) throws NetworkException;

	/**
	 * 夹具压力采集
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return 所有夹具的压力
	 * @throws NetworkException
	 */
	List<Integer> readPressure(boolean isIC) throws NetworkException;

	/**
	 * 夹具压力设置
	 * 
	 * @param values
	 *            压力集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writePressure(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具压力设置
	 * 
	 * @param fixtureIndex
	 *            夹具号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOnePressure(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 容量夹具电池来源
	 * 
	 * @throws NetworkException
	 */
	List<Integer> readBatterySource() throws NetworkException;

	/**
	 * 写入容量夹具来源
	 * 
	 * @param values
	 * @return
	 * @throws NetworkException
	 */
	Result writeBatterySource(List<Integer> values) throws NetworkException;

	/**
	 * 写入单个容量夹具来源
	 * 
	 * @param fixyureIndex
	 * @param value
	 * @return
	 * @throws NetworkException
	 */
	Result writeOneBatterySource(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 电池的测试结果
	 * 
	 * @param fixtureIndex夹具标号
	 * @param values
	 *            电池结果
	 * @param batteryIndex
	 *            从第几个电池开始
	 * @throws NetworkException
	 */
	Result writeTestResult(int fixtureIndex, List<Integer> values, int batteryIndex) throws NetworkException;

	/**
	 * 读取电池的测试结果
	 * 
	 * @param fixtureIndex夹具标号
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readTestResult(int fixtureIndex) throws NetworkException;

	/**
	 * 下料水车的测试结果
	 * 
	 * @param values
	 *            电池结果
	 * @param batteryIndex
	 *            从第几个电池开始
	 * @throws NetworkException
	 */
	Result writeWaterWheelResult(List<Integer> values, int batteryIndex) throws NetworkException;

	/**
	 * 读取下料水车的测试结果
	 * 
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readWaterWheelResult() throws NetworkException;

	/**
	 * 写夹具的kb值
	 * 
	 * @param pressKBs
	 *            夹具的kb对象集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeFixtureKB(List<PressKB> pressKBs, boolean isIC) throws NetworkException;

	/**
	 * 写单个夹具kb值
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param kb
	 *            kb对象
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneFixtureKB(int fixtureIndex, PressKB kb) throws NetworkException;

	/**
	 * 读夹具的kb值
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<PressKB> readFixtureKB(boolean isIC) throws NetworkException;

	/**
	 * 预压位坐标地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writePrePress(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具预压位地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOnePrePress(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读预压位地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readPrePress(boolean isIC) throws NetworkException;

	/**
	 * 预压位坐标反馈地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writePrePressBack(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具预压位反馈地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOnePrePressBack(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读预压位反馈地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readPrePressBack(boolean isIC) throws NetworkException;

	/**
	 * 压紧第一段速度地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeFirstSpeed(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具压紧第一段速度地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneFirstSpeed(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读压紧第一段速度地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readFirstSpeed(boolean isIC) throws NetworkException;

	/**
	 * 压紧第一段速度反馈地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeFirstSpeedBack(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具压紧第一段速度反馈地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneFirstSpeedBack(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读压紧第一段速度反馈地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readFirstSpeedBack(boolean isIC) throws NetworkException;

	/**
	 * 压紧第五段速度地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeFifthSpeed(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具压紧第五段速度地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数字
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneFifthSpeed(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读压紧第五段速度地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readFifthSpeed(boolean isIC) throws NetworkException;

	/**
	 * 压紧第五段速度反馈地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeFifthSpeedBack(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具压紧第五段速度反馈地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneFifthSpeedBack(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读压紧第五段速度反馈地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readFifthSpeedBack(boolean isIC) throws NetworkException;

	/**
	 * 完全张开时坐标地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeCompleteOpenAddr(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具完全张开时坐标地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneCompleteOpenAddr(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读完全张开时坐标地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readCompleteOpenAddr(boolean isIC) throws NetworkException;

	/**
	 * 完全张开时压力地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeCompleteOpenPress(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具完全张开时压力地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneCompleteOpenPress(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读完全张开时压力地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readCompleteOpenPress(boolean isIC) throws NetworkException;

	/**
	 * 压力补偿值地址
	 * 
	 * @param values
	 *            数值集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writePressCompensation(List<Integer> values, boolean isIC) throws NetworkException;

	/**
	 * 单个夹具压力补偿值地址
	 * 
	 * @param fixtureIndex
	 *            夹具编号
	 * @param value
	 *            数值
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOnePressCompensation(int fixtureIndex, int value) throws NetworkException;

	/**
	 * 读压力补偿值地址
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Integer> readPressCompensation(boolean isIC) throws NetworkException;

	/**
	 * 设置温度报警
	 * 
	 * @param alerts
	 *            温度报警集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeTempAlert(List<Alert> alerts, boolean isIC) throws NetworkException;

	/**
	 * 设置单个夹具设置温度报警
	 * 
	 * @param alert
	 *            温度报警
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOneTempAlert(int fixtureIndex, Alert alert) throws NetworkException;

	/**
	 * 读取温度报警
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Alert> readTempAlert(boolean isIC) throws NetworkException;

	/**
	 * 设置压力报警
	 * 
	 * @param alerts
	 *            压力报警集合
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writePresAlert(List<Alert> alerts, boolean isIC) throws NetworkException;

	/**
	 * 设置单个夹具设置压力报警
	 * 
	 * @param alert
	 *            压力报警
	 * @param isIC
	 *            true:化成;false:分容
	 * @throws NetworkException
	 */
	Result writeOnePresAlert(int fixtureIndex, Alert alert) throws NetworkException;

	/**
	 * 读取压力报警
	 * 
	 * @param isIC
	 *            true:化成;false:分容
	 * @return
	 * @throws NetworkException
	 */
	List<Alert> readPresAlert(boolean isIC) throws NetworkException;

	/**
	 * 设置速度相关
	 * 
	 * @param speeds
	 *            速度对象集合
	 * @throws NetworkException
	 */
	Result writeICSpeed(List<Speed> speeds) throws NetworkException;

	/**
	 * 设置单个夹具速度相关
	 * 
	 * @param speed
	 *            速度对象
	 * @throws NetworkException
	 */
	Result writeOneICSpeed(int fixtureIndex, Speed speed) throws NetworkException;

	/**
	 * 读取压力报警
	 * 
	 * @return
	 * @throws NetworkException
	 */
	List<Speed> readICSpeed() throws NetworkException;

	/**
	 * 写当前电芯通道号
	 * 
	 * @param number
	 * @return
	 * @throws NetworkException
	 */
	Result writeCurrentChannel(int number) throws NetworkException;

	/**
	 * 读当前电芯通道号
	 * 
	 * @return
	 * @throws NetworkException
	 */
	Integer readCurrentChannel() throws NetworkException;

	/**
	 * 写电池数量
	 * 
	 * @param number
	 * @return
	 * @throws NetworkException
	 */
	Result writeBatteryCount(int number) throws NetworkException;

	/**
	 * 读电池数量
	 * 
	 * @return
	 * @throws NetworkException
	 */
	Integer readBatterycount() throws NetworkException;

	/**
	 * 下料水车电芯来源夹具
	 * 
	 * @param number
	 * @return
	 * @throws NetworkException
	 */
	Result writeWaterWheelBatterySource(int number) throws NetworkException;

	/**
	 * 读下料水车电芯来源夹具
	 * 
	 * @return
	 * @throws NetworkException
	 */
	Integer readWaterWheelBatterySource() throws NetworkException;

	/**
	 * 下料机械手电芯对数
	 * 
	 * @param number
	 * @return
	 * @throws NetworkException
	 */
	Result writeMachanicalArmCurrent(int number) throws NetworkException;

	/**
	 * 下料机械手电芯对数
	 * 
	 * @return
	 * @throws NetworkException
	 */
	Integer readMachinicalArmCurrent() throws NetworkException;

	/**
	 * 报警--上位机读功能的写入
	 * 
	 * @param isAlert
	 * @return
	 * @throws NetworkException
	 */
	Result writeAlertRead(boolean isAlert) throws NetworkException;

	/**
	 * 报警--上位机读功能的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readAlertRead() throws NetworkException;

	/**
	 * 自动运行--上位机读功能的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readAutoRead() throws NetworkException;

	/**
	 * 自动运行---上位机读功能的写入
	 * 
	 * @param isAuto
	 * @return
	 * @throws NetworkException
	 */
	Result writeAutoRead(boolean isAuto) throws NetworkException;

	/**
	 * 停止--上位机读功能的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readStopRead() throws NetworkException;

	/**
	 * 停止--上位机读功能的写入
	 * 
	 * @param isStop
	 * @return
	 * @throws NetworkException
	 */
	Result writeStopRead(boolean isStop) throws NetworkException;

	/**
	 * 重置--上位机写功能的写入
	 * 
	 * @param isReset
	 * @return
	 * @throws NetworkException
	 */
	Result writeResetWrite(boolean isReset) throws NetworkException;

	/**
	 * 重置--上位机写功能的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readResetWrite() throws NetworkException;

	/**
	 * 启动--上位机写功能的写入
	 * 
	 * @param isStart
	 * @return
	 * @throws NetworkException
	 */
	Result writeStartWrite(boolean isStart) throws NetworkException;

	/**
	 * 启动--上位机写功能的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readStartWrite() throws NetworkException;

	/**
	 * 停止--上位机写功能的写入
	 * 
	 * @param isStop
	 * @return
	 * @throws NetworkException
	 */
	Result writeStopWrite(boolean isStop) throws NetworkException;

	/**
	 * 停止--上位机写功能的写入
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readStopWrite() throws NetworkException;

	/**
	 * 自动运行的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeAutoState(boolean isOn) throws NetworkException;

	/**
	 * 自动运行的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readAutoState() throws NetworkException;

	/**
	 * 夹具压力状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeFixturePres(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 夹具压力状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readFixturePres( int fixtureIndex) throws NetworkException;

	/**
	 * 夹具压力是否屏蔽的写入
	 * 
	 * @param isOn
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeFixtureShield(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 夹具压力是否屏蔽的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readFixtureShield( int fixtureIndex) throws NetworkException;

	/**
	 * 夹具温度状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeFixtureTemp(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 夹具温度状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readFixtureTemp( int fixtureIndex) throws NetworkException;

	/**
	 * 夹具状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeFixtureState(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 夹具状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readFixtureState( int fixtureIndex) throws NetworkException;

	/**
	 * 夹具有无料的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeHasMaterial(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 夹具有无料的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readHasMaterial( int fixtureIndex) throws NetworkException;

	/**
	 * 移栽1的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeMove1(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 移栽1的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readMove1( int fixtureIndex) throws NetworkException;

	/**
	 * 移栽2的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeMove2(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 移栽2的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readMove2( int fixtureIndex) throws NetworkException;

	/**
	 * NG状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeNgState(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * NG状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readNgState( int fixtureIndex) throws NetworkException;

	/**
	 * 流程请求状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeProcessRequest(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 流程请求状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readProcessRequest( int fixtureIndex) throws NetworkException;

	/**
	 * 测试状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeTestState(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 测试状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readTestState( int fixtureIndex) throws NetworkException;

	/**
	 * 工作完成状态的写入
	 * 
	 * @param isOn
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	Result writeWorkCompletSign(boolean isOn,  int fixtureIndex) throws NetworkException;

	/**
	 * 工作完成状态的读取
	 * 
	 * @param isIC
	 * @param fixtureIndex
	 * @return
	 * @throws NetworkException
	 */
	boolean readWorkCompletSign( int fixtureIndex) throws NetworkException;

	/**
	 * 扫码状态的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeBarcodeate(boolean isOn) throws NetworkException;

	/**
	 * 扫码状态的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readBarcodeState() throws NetworkException;

	/**
	 * 数据库状态的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeDatabaseState(boolean isOn) throws NetworkException;

	/**
	 * 数据库状态的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readDabaseState() throws NetworkException;

	/**
	 * 下料扫码的ng信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadBarcodeNg(boolean isOn) throws NetworkException;

	/**
	 * 下料扫码的ng信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadBarcodeNg() throws NetworkException;

	/**
	 * 下料扫码的ok信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadBarcodeOk(boolean isOn) throws NetworkException;

	/**
	 * 下料扫码的ok信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadBarcodeOk() throws NetworkException;

	/**
	 * 下料扫码的触发的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadBarcodeSign(boolean isOn) throws NetworkException;

	/**
	 * 下料扫码的触发的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadBarcodeSign() throws NetworkException;

	/**
	 * 下料弹夹绑定的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadClipBinding(boolean isOn) throws NetworkException;

	/**
	 * 下料弹夹绑定的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadClipBinding() throws NetworkException;

	/**
	 * 下料弹夹绑定结束的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadClipBindingFinish(boolean isOn) throws NetworkException;

	/**
	 * 下料弹夹绑定结束的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadClipBindingFinish() throws NetworkException;

	/**
	 * 下料确认信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadConfirm(boolean isOn) throws NetworkException;

	/**
	 * 下料确认信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadConfirm() throws NetworkException;

	/**
	 * 下料信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadSign(boolean isOn) throws NetworkException;

	/**
	 * 下料信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadSign() throws NetworkException;

	/**
	 * 电芯放下料传送带信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadConveyorSign(boolean isOn) throws NetworkException;

	/**
	 * 电芯放下料传送带信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadConveyorSign() throws NetworkException;

	/**
	 * 电芯放下料传送带信号确认的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadConveyorConfirmSign(boolean isOn) throws NetworkException;

	/**
	 * 电芯放下料传送带信号确认的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadConveyorConfirmSign() throws NetworkException;

	/**
	 * 电芯入弹夹信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadToClipSign(boolean isOn) throws NetworkException;

	/**
	 * 电芯入弹夹信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadToClipSign() throws NetworkException;

	/**
	 * 电芯入弹夹确认信号的写入
	 * 
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeUnloadToClipConfirmSign(boolean isOn) throws NetworkException;

	/**
	 * 电芯入弹夹确认信号的读取
	 * 
	 * @return
	 * @throws NetworkException
	 */
	boolean readUnloadToClipConfirmSign() throws NetworkException;

	/**
	 * 写入DM区自定义地址
	 * 
	 * @param address
	 * @param type
	 * @param value
	 * @return
	 * @throws NetworkException
	 */
	Result writeCustomDMAddress(int address, Type type, int value) throws NetworkException;

	/**
	 * 读取DM自定义地址
	 * 
	 * @param address
	 * @param type
	 * @return
	 * @throws NetworkException
	 */
	Integer readCustomDMAddress(int address, Type type) throws NetworkException;

	/**
	 * 写入WR区自定义地址
	 * 
	 * @param address
	 * @param index
	 * @param isOn
	 * @return
	 * @throws NetworkException
	 */
	Result writeCustomWRAddress(int address, int index, boolean isOn) throws NetworkException;

	/**
	 * 读取DM自定义地址
	 * 
	 * @param address
	 * @param index
	 * @return
	 * @throws NetworkException
	 */
	boolean readCustomWRAddress(int address, int index) throws NetworkException;

	List<Integer> readAreaData(int address, int length, Area area) throws NetworkException;
	
	List<Integer> readAreaDuintData(int address,int length,Area area)throws NetworkException;
}
