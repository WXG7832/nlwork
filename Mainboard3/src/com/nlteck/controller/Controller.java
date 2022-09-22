package com.nlteck.controller;

import com.nltecklib.protocol.li.Decorator;
import com.nltecklib.protocol.li.ResponseDecorator;

/**
* @author  wavy_zheng
* @version 创建时间：2020年11月12日 上午11:13:59
* 纽联设备控制器接口
*/
public interface Controller {
    
	/**
	 * 处理网络另外一端的命令请求；
	 * 为了提高网络并发效率，此条命令不从networkService接收队列获取，直接在mina receive响应方法内调用！
	 * @author  wavy_zheng
	 * 2020年11月12日
	 * @param command
	 * @throws Exception
	 */
	public ResponseDecorator  processCmdFromNetwork(Decorator command) ;
	
	/**
	 * 向网络另一端的纽联设备（软件）发送请求命令，直到等待超时；此命令从networkservice接收队列里获取返回结果，为同步命令!
	 * @author  wavy_zheng
	 * 2020年11月12日
	 * @param command
	 * @throws Exception
	 */
	public ResponseDecorator  requestCmdToNetwork(Decorator command,int timeOut) ;
	
	

	
	
	
}
