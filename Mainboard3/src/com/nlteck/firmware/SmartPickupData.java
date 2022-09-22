package com.nlteck.firmware;

import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年5月16日 下午2:53:44
* 用于智能保护的数据采集对象
* 重新创建该对象的目录主要是为了节省主控不多的内存
*/
public class SmartPickupData {
   
	public int  stepIndex;
	public int  loopIndex;
	public WorkMode  workMode;
	
	public double  voltage;
	public double  current;
	public int     seconds; //流逝时间
	
}
