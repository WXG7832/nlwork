package com.nlteck.service;

import java.io.IOException;
import java.util.Timer;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.Direction;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.PowerState;
import com.nltecklib.protocol.li.accessory.FanStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerStateQueryData;
import com.nltecklib.protocol.li.accessory.PowerSwitchData;
import com.nltecklib.protocol.li.cal.CalEnvironment.Pole;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkMode;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.CalibrateData;
import com.nltecklib.protocol.li.cal.RelayControlData;


/**
 * 控制板控制器
 * 
 * @author Administrator
 *
 */
public abstract class DeviceController {

	
	private final static int TIME_OUT = 1200;
	

	private String lightCtrlCode = "01"; // 0表示灯灭1s，1表示绿灯亮1s ， 2 表示黄灯亮1s
	private Timer timer;
	private int lampIndex = 0;

	public DeviceController()  {

		
	}

	/**
	 * 打开或关闭充放电源
	 * 
	 * @param index
	 *            当前只有1组；不支持该功能
	 * @param state
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public abstract boolean power(int index, PowerState state) throws AlertException;

	public abstract PowerSwitchData getPowerState(int index) throws AlertException ;

	/**
	 * 风扇控制
	 * 
	 * @param index
	 *            风扇组号，暂时不支持该功能；
	 * @param direction
	 * @param state
	 * @param grade
	 *            风速档位
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public abstract boolean fan(int index, Direction direction, PowerState state, int grade) throws AlertException ;

	/**
	 * 控制极性灯
	 * 
	 * @param index
	 * @param color
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public abstract boolean lightPole(int index, byte colorFlag , short lightFlag) throws AlertException ;
	/**
	 * 设置校准板基准电压
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract boolean setCalBoardBaseVoltage(int boardIndex, int chnIndexInBoard, WorkState ws, double baseVolt)
			throws IOException ;


	/**
	 * 获取逻辑板上的通道基准电压值
	 * 
	 * @param boardIndex
	 * @param chnIndexInBoard
	 * @return
	 * @throws IOException
	 */
	public abstract double getCalBoardBaseVoltage(int boardIndex, int chnIndexInBoard) throws IOException ;

	/**
	 * 设置校准板工作状态
	 * 
	 * @param driverIndex
	 * @param chnIndex
	 * @param pole
	 * @param workMode
	 * @param err
	 * @return
	 * @throws IOException
	 */
	public abstract boolean setCalBoardState(int driverIndex, int chnIndex, Pole pole, WorkMode workMode, WorkState ws , boolean highPrecision)
			throws IOException ;

	/**
	 * 读取校准板状态
	 * 
	 * @param driverIndex
	 * @param chnIndex
	 * @return
	 * @throws IOException
	 */
	public abstract CalibrateData getCalBoardState(int driverIndex, int chnIndex) throws IOException ;

	public  abstract boolean switchMeter(int boardIndex, boolean connected) throws IOException ;

	public abstract RelayControlData readSwitchMeter(int boardIndex) throws IOException ;

	public abstract Data readCalBoardTemperature(int boardIndex) throws IOException ;
	
	/**
	 * 读取电源监控状态数据
	 * @return
	 * @throws IOException
	 */
	public abstract PowerStateQueryData readPowersState() throws IOException;
	
	/**
	 * 读取监控风机状态
	 * @return
	 * @throws IOException
	 */
	public abstract FanStateQueryData readFansState() throws IOException ;
	
	

}
