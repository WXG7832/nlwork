package com.nlteck.produce;

/**
 * 数据产生器
 * @author Administrator
 *
 */
public interface ChnDataProducer {
     
	  /**
	   * 产生数据
	   */
	  void produceData();
	  
	  /**
	   * 获取电压
	   * @return
	   */
	  double getProduceVoltage();
	  
	  /**
	   * 获取电流
	   */
	  double getProduceCurrent();
	  
	  /**
	   * 获取报警电压
	   */
	  double getAlertVoltage();
	  
	  /**
	   * 获取报警电流
	   */
	  double getAlertCurrent();
	  
	  /**
	   * 查询通道是否测试完成或关闭
	   * @return
	   */
	  boolean isClose();
	  
	  /**
	   * 打开或关闭通道
	   * @param closed
	   */
	  void    setClose(boolean closed);
	  
	  void    setProgram(double v , double i , double end);
	  
	  /**
	   * 设置产生的电压值
	   * @param produceVoltage
	   */
	  public void setProduceVoltage(double produceVoltage);
}
