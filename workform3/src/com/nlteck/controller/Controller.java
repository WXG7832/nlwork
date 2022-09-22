package com.nlteck.controller;

import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.ResponseDecorator;

/**
* @author  wavy_zheng
* @version 创建时间：2020年10月31日 下午3:50:23
* 控制器接口
*/
public interface Controller {
    
	
	
	/**
	 * 处理命令，返回处理结果
	 * @author  wavy_zheng
	 * 2020年10月31日
	 * @param decorator
	 * @return
	 */
	public ResponseDecorator  process(Decorator decorator) throws Exception ;
	
	
	
	/**
	 * 匹配下发命令后的回复指令，
	 * @author  wavy_zheng
	 * 2020年10月31日
	 * @param decorator
	 * @param timeOut  最长等待时间 单位ms
	 * @return
	 * @throws Exception
	 */
	public ResponseDecorator sendCommand(Decorator decorator, int timeOut) throws Exception;
	
	
	
	
}
