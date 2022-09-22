package com.nlteck.service;

import com.nlteck.AlertException;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MainBoard;
import com.nlteck.i18n.I18N;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;

/**
* @author  wavy_zheng
* @version 创建时间：2021年4月26日 下午2:12:18
* 类说明
*/
public class ChannelIndexService {
   
	private MainBoard   mb;
	
	public ChannelIndexService(MainBoard  mb) {
		
		this.mb = mb;
	}
	
	/**
	 * 获取逻辑板实际通道序号
	 * @author  wavy_zheng
	 * 2021年4月26日
	 * @param chnIndexInLogic  上位机理论通道
	 * @return  实际下发通道
	 */
	public int  getActualLogicChnIndexBy(int logicIndex , int chnIndexInLogic) {
		
		
//		int driverIndex = chnIndexInLogic / MainBoard.startupCfg.getDriverChnCount();
//		int chnIndexInDriver = chnIndexInLogic %  MainBoard.startupCfg.getDriverChnCount();
//		
//		
//		LogicBoard lb = mb.getLogicBoards().get(logicIndex);
//		if(mb.getDcdd() == null || !mb.getDcdd().isEnable()) {
//			
//			//正常顺序
//			
//			if(lb.isReverseDriverIndex()) {
//				
//				return getReverseChnIndexByDriver(chnIndexInLogic);
//			} 
//			
//			return chnIndexInLogic;
//			
//			
//		} else {
//			
//			
//			if(lb.isReverseDriverIndex()) {
//				
//				driverIndex = getReverseDriverIndexByDriver(driverIndex);
//			}
//			
//			
//			//按用户定义的通道序号进行操作
//			int chnIndex = mb.getDcdd().getChnIndexDefineList().get(chnIndexInDriver);
//			return  driverIndex * MainBoard.startupCfg.getDriverChnCount() + chnIndex;
//
//		}
		
		return 0;

	}
	
	/**
	 * 返回真实下发的驱动板通道序号
	 * @author  wavy_zheng
	 * 2021年4月26日
	 * @param chnIndexInDriver
	 * @return
	 */
	public int getActualDriverChnIndexBy(int chnIndexInDriver) {
		
		if(mb.getDcdd() != null && mb.getDcdd().isEnable()) {
			
			return mb.getDcdd().getChnIndexDefineList().get(chnIndexInDriver);
			
		}
		
		return chnIndexInDriver;
	}
	
	/**
	 * 通过实际驱动板通道号找到软件对应的通道序号
	 * @author  wavy_zheng
	 * 2021年4月26日
	 * @param actualChnIndexInDriver
	 * @return
	 */
	public int getDriverChnIndexByActual(int actualChnIndexInDriver) throws AlertException {
		
		
       if(mb.getDcdd() != null && mb.getDcdd().isEnable()) {
			
			for(int n = 0 ; n < mb.getDcdd().getChnIndexDefineList().size() ; n++) {
				
				int actual = mb.getDcdd().getChnIndexDefineList().get(n);
				if(actual == actualChnIndexInDriver) {
					
					return n;
				}
				
			}
			throw new AlertException(AlertCode.LOGIC, "error map chn actual index:" + actualChnIndexInDriver);
			
			
		}
		
		return actualChnIndexInDriver;
	}
	
	
	
	/**
	 * 获取反序的驱动板序号
	 * @author  wavy_zheng
	 * 2020年12月4日
	 * @param driverIndex
	 * @return
	 */
	private int  getReverseDriverIndexByDriver(int driverIndex) {
		
		 // return MainBoard.startupCfg.getLogicDriverCount() - 1 - driverIndex;
		return driverIndex;
	}
	
	
	public int  getActualDriverIndex(int logicIndex , int driverIndex) {
		
//		LogicBoard lb = mb.getLogicBoards().get(logicIndex);
//		if(lb.isReverseDriverIndex()) {
//			
//			return getReverseDriverIndexByDriver(driverIndex);
//		}
//		
		return driverIndex;
	}
	
	
	
	
	/**
	 * 因驱动反序导致的通道序号
	 * @author  wavy_zheng
	 * 2020年12月4日
	 * @param chnIndex
	 * @return
	 */
	private int  getReverseChnIndexByDriver(int chnIndex) {
		
		int driverIndex = chnIndex / MainBoard.startupCfg.getDriverChnCount();
		int chnIndexInDriver = chnIndex %  MainBoard.startupCfg.getDriverChnCount();
		driverIndex = getReverseDriverIndexByDriver(driverIndex);
		return driverIndex * MainBoard.startupCfg.getDriverChnCount() + chnIndexInDriver;
	}
	
}
