package com.nlteck.service.connector;

import java.io.IOException;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.logic.LogicCalMatchData;
import com.nltecklib.protocol.li.logic.LogicCalProcessData;
import com.nltecklib.protocol.li.logic.LogicCalculateData;
import com.nltecklib.protocol.li.logic.LogicChnStartData;
import com.nltecklib.protocol.li.logic.LogicChnStopData;
import com.nltecklib.protocol.li.logic.LogicChnSwitchData;
import com.nltecklib.protocol.li.logic.LogicDeviceProtectData;
import com.nltecklib.protocol.li.logic.LogicEnvironment.MatchState;
import com.nltecklib.protocol.li.logic.LogicEnvironment.WorkMode;
import com.nltecklib.protocol.li.logic.LogicFaultCheckData;
import com.nltecklib.protocol.li.logic.LogicFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKCalculateData;
import com.nltecklib.protocol.li.logic.LogicHKCalibrateData;
import com.nltecklib.protocol.li.logic.LogicHKFlashWriteData;
import com.nltecklib.protocol.li.logic.LogicHKOperationData;
import com.nltecklib.protocol.li.logic.LogicLabPoleData;
import com.nltecklib.protocol.li.logic.LogicLabProtectData;
import com.nltecklib.protocol.li.logic.LogicPickupData;
import com.nltecklib.protocol.li.logic.LogicPoleData;
import com.nltecklib.protocol.li.logic.LogicStateData;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * 逻辑板通信控制器
 * @author Administrator
 *
 */
public interface LogicConnector {
     
	/**
	 * 设置超时时间
	 * @param timeout
	 */
	public void setRecvTimeout(int timeout);
	/**
	 * 清空串口缓存
	 */
	public void clearBuff();
	 /**
	  * 配置极性
	  * @param lpd
	  * @param err
	  * @return
	  */
	  public boolean configPole(LogicPoleData lpd,StringBuffer err) throws AlertException ;
	  
	  /**
	   * 配置通道极性
	   * @param llpd
	   * @return
	   * @throws AlertException
	   */
	  public boolean configSinglePole(LogicLabPoleData llpd)  throws AlertException;
	  
	  /**
	   * 配置设备保护
	   * @param ldpd
	   * @param err
	   * @return
	   */
	  public boolean configDeviceProtect(LogicDeviceProtectData ldpd,StringBuffer err) throws AlertException ;
	  
	  /**
	   * 配置通道超限保护
	   * @param llpd
	   * @return
	   * @throws AlertException
	   */
	  public boolean configSingleProtect(LogicLabProtectData llpd ) throws AlertException;
	  
	  /**
	   * 配置通道开关
	   * @param lcsd
	   * @param err
	   * @return
	   */
	  public boolean configChnSwitchState(LogicChnSwitchData lcsd ,StringBuffer err) throws AlertException ;
	  /**
	   * 配置启动或停止
	   * @param lsd
	   * @param err
	   * @return
	   */
	  public boolean configStartup(LogicStateData lsd ,StringBuffer err ) throws AlertException ;
	  
	  /**
	   * 采集数据
	   * @return
	   */
	  public LogicPickupData pickup(int driverIndex) throws AlertException ;
	  
	  
	  /**
	   * HK通道操作
	   * @author  wavy_zheng
	   * 2020年4月24日
	   * @param lod
	   * @return
	   * @throws AlertException
	   */
	  public boolean operateHkChns(LogicHKOperationData lod) throws AlertException;
	  
	  
	  /**
	   * 配置HK下一个步次将要执行的流程步次
	   * @author  wavy_zheng
	   * 2020年4月24日
	   * @param step
	   * @return
	   * @throws AlertException
	   */
	  public boolean configHKProcedureStep(WorkMode workMode,double specialVoltage , double specialCurrent , double threshold) throws AlertException;
	  
	  
	  /**
	   * 打开通道并启动
	   */
	  public boolean startChn(LogicChnStartData lcsd) throws AlertException;
	  
	  /**
	   * 临时关闭通道；此时该通道在本流程还能再打开
	   * @param lcsd
	   * @return
	   */
	  public boolean stopChn(LogicChnStopData lcsd,StringBuffer err) throws AlertException ;
	 
	  
	  /**
	   * 读ADC
	   * @param lcpd
	   * @param err
	   * @return
	   */
	  public LogicCalProcessData  getLogicCalProcess(LogicCalProcessData lcpd , StringBuffer err) throws AlertException ;
	  
	  
	  /**
	   * 读取HK逻辑板校准信息
	   * @author  wavy_zheng
	   * 2020年4月23日
	   * @param data
	   * @param err
	   * @return
	   * @throws AlertException
	   */
	  public LogicHKCalibrateData readLogicHKCalibration(LogicHKCalibrateData data , StringBuffer err) throws AlertException ;
	  
	  
	  /***
	   * 设置逻辑板状态值
	   * @param lcpd
	   * @param err
	   * @return
	   */
	  public boolean  setLogicCalProcess(LogicCalProcessData lcpd , StringBuffer err) throws AlertException ;
	  
	  
	  /**
	   * 校准HK逻辑板
	   * @author  wavy_zheng
	   * 2020年4月23日
	   * @param data
	   * @param err
	   * @return
	   * @throws AlertException
	   */
	  public boolean  configHKLogicCalibration(LogicHKCalibrateData data , StringBuffer err) throws AlertException;
	  
	  
	  /**
	   * 写入逻辑板Flash
	   */
	  public boolean writeCalFlash(LogicFlashWriteData lfwd , StringBuffer err) throws AlertException ;
	  
	  
	  /**
	   * 写入HK逻辑板Flash
	   */
	  public boolean writeHKCalFlash(LogicHKFlashWriteData lfwd , StringBuffer err) throws AlertException ;
	  
	  
	  /**
	   * 读取逻辑板Flash
	   * @param logicIndex
	   * @param chnIndexInLogic
	   * @return
	   */
	  public LogicFlashWriteData readCalFlash(int logicIndex , int chnIndexInLogic,StringBuffer err);
	  
	  
	  /**
	   * 读取HK逻辑板Flash
	   * @param logicIndex
	   * @param chnIndexInLogic
	   * @return
	   */
	  public LogicHKFlashWriteData readHKCalFlash(int logicIndex , int chnIndexInLogic,StringBuffer err);
	  
	  /**
	   * 读取基准电压值
	   * @param logincIndex
	   * @param chnIndexInLogic
	   * @return
	   */
	  public LogicCalMatchData readBaseVoltage(int logicIndex ,int chnIndexInLogic);
	  
	  /**
	   * 设置基准电压测试
	   * @param logicIndex
	   * @param chnIndexInLogic
	   * @param pole
	   * @return
	   */
	  public boolean configBaseVoltage(int logicIndex ,int chnIndexInLogic ,Pole pole , MatchState ms) throws AlertException;
	  
	  /**
	   * 读取计量数据
	   * @param logicIndex
	   * @param chnIndex
	   * @return
	   */
	  public LogicCalculateData readCalculateData(int logicIndex , int chnIndex) throws IOException;
	  
	  /**
	   * 读取HK计量数据
	   * @author  wavy_zheng
	   * 2020年4月23日
	   * @param logicIndex
	   * @param chnIndex
	   * @return
	   * @throws AlertException
	   */
	  public LogicHKCalculateData readHKCalculation(int logicIndex , int chnIndex) throws AlertException;
	  
	  
	  /**
	   * 设置计量点
	   */
	  public boolean writeCalculateData(LogicCalculateData lcd) throws AlertException;
	  
	  
	  /**
	   * 设置HK计量点
	   * @author  wavy_zheng
	   * 2020年4月23日
	   * @param lcd
	   * @return
	   * @throws AlertException
	   */
	  public boolean writeHKCalculation(LogicHKCalculateData lcd) throws AlertException;
	  
	  
	  /**
	   * 启动分区所有通道
	   * @param logicIndex
	   * @throws AlertException
	   */
	  public void  startAllChns(int logicIndex , WorkMode workMode , double voltage, double current , double threshold) throws AlertException;
	  
	  /**
	   * 停止分区所有通道
	   * @param logicIndex
	   * @throws AlertException
	   */
	  public void stopAllChns(int logicIndex) throws AlertException;
	  
	  /**
	   * 读取故障详情
	   * @param logicIndex
	   * @return
	   * @throws AlertException
	   */
	  public LogicFaultCheckData  readFaultCheckData(int logicIndex) throws AlertException;
	  
	  /**
	   * 模块使能
	   * @param lmsd
	   * @throws AlertException
	   */
	  public void enableModule(int chnIndexInLogic , boolean open) throws AlertException;
	  
	 
	  
	  
}
