package com.nlteck.listener;

import com.nlteck.AlertException;
import com.nlteck.firmware.LogicBoard;
/**
 * 逻辑板事件监听器
 * @author Administrator
 *
 */
public interface LogicListener{
	 
	 /**
	  *   流程测试完毕
	  */
	 void procedureComplete(LogicBoard lb) throws AlertException;
	 
	 /**
	  * 流程测试被停止
	  */
	 void procedureStoped(LogicBoard lb) throws AlertException;
	 
	 /**
	  * 流程测试被暂停
	  */
	 void procedurePaused(LogicBoard lb) throws AlertException;
	 
	 /**
	  * 流程测试被启动
	  */
	 void procedureStarted(LogicBoard lb) ;
	 
	 /**
	  * 流程测试被恢复
	  */
	 void procedureResumed(LogicBoard lb) throws AlertException;
	 
//	 /**
//	  * 进入校准模式
//	  */
//	 void enterCalibrateMode(LogicBoard lb) throws AlertException;
//	 
//	 /**
//	  * 退出校准模式
//	  */
//	 void exitCalibrateMode(LogicBoard lb) throws AlertException;
	 
 
	
}
