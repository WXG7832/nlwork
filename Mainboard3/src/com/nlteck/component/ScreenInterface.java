package com.nlteck.component;

import java.util.Date;

/**
 * 液晶屏功能接口
 * @author Administrator
 *
 */
public interface ScreenInterface {
    
	/**
	 * 设备逻辑分区极性
	 * @author Administrator
	 *
	 */
	public enum Pole{
		
		POSITIVE , NEGTIVE;
	}
	
	/**
	 * 在液晶屏主界面显示当前机柜温度
	 * @param temp
	 */
	public void showTemperature(double temp);
	
	
	/**
	 * 在液晶屏主界面显示当前机各分区柜极性
	 * @param  logicIndex 分区序号
	 * @param  分区极性
	 */
	public void showPole(int logicIndex , Pole pole);
	
	/**
	 * 在液晶屏主界面显示当前机柜各分区测试名
	 * @param  logicIndex  分区序号
	 * @param  testName    分区测试名
	 */
	public void showTestName(int logicIndx , String testName);
	
	/**
	 * 在液晶屏主界面显示当前机柜名
	 * @param devicerName
	 */
	public void showDeviceName(String devicerName);
	
	/**
	 * 在液晶屏主界面显示当前日期时间
	 */
	public void showCurrentTimestamp(Date datetime);
	
	/**
	 * 在温度曲线界面添加一个最新的温度数据
	 */
	public void appendTempDotToChart(double temp);
	
	/**
	 * 在液晶主界面显示逻辑分区统计数据
	 * @param logicIndex  分区序号
	 * @param charge  充电个数
	 * @param discharge 放电个数
	 * @param 搁置个数
	 * @param 报警个数
	 * 
	 */
	public void showLogicStatistics(int logicIndex , int charge , int discharge , int sleep , int alert);
	
	/**
	 * 液晶屏弹出报警窗口并显示当前的报警信息
	 * @param code  报警码
	 * @param type  报警类别
	 * @param info  报警详细内容
	 */
	public void popupAlertWindow(int code ,String type , String info);
	
	/**
	 * 切换到液晶屏主界面
	 */
	public void switchToMainWindow();
	
	
	/**
	 * 获取用户当前输入的IP地址
	 * @return
	 */
	public String getInputIpAddress();
	
	/**
	 * 在修改地址页面上显示修改的结果及信息
	 * @param info  主控反馈的修改信息
	 * @param result  true修改IP成功;false修改IP失败
	 */ 
	public void changeIpAddress(boolean result , String info);
	
	/**
	 * 获取用户输入的原密码
	 * @return
	 */
	public String getInputOriginPassword();
	
	/**
	 * 
	 * @return  用户第一次输入的修改密码
	 */
	public String getInputNewPassword1();
	
	
	/**
	 * 
	 * @return  用户第二次输入的修改密码
	 */
	public String getInputNewPassword2();
	
	/**
	 * 在修改界面上显示密码修改的结果
	 * @param info   主控反馈的修改信息
	 * @param result  true密码修改成功;false密码修改失败
	 */
	public void changePassword(boolean result , String info);
	
	
}
