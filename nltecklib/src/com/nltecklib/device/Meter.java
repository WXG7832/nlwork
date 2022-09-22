package com.nltecklib.device;

import java.io.IOException;
import java.util.List;

/**
 * 万用表接口
 * 
 * @author Administrator
 *
 */
public interface Meter {

	/**
	 * 单次读表,单位mA,mV
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public double ReadSingle() throws IOException, InterruptedException;

	/**
	 * 读取实际值，不取绝对值
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public double ReadRealSingle() throws IOException, InterruptedException;

	/**
	 * 连接网络
	 * 
	 * @param ip
	 * @throws Exception
	 */
	@Deprecated
	public void connect(String ip) throws Exception;

	/**
	 * 断开网络 * @throws Exception
	 */
	public void disconnect() throws Exception;

	/**
	 * 获取表序号
	 * 
	 * @return
	 */
	public int getIndex();

	/**
	 * 获取表地址
	 * 
	 * @return
	 */
	public String getIpAddress();

	/**
	 * 设置表地址
	 * 
	 * @param ip
	 */
	public void setIpAddress(String ip);

	public boolean isUse();

	/**
	 * 是否启用该万用表
	 * 
	 * @param use
	 */
	public void setUse(boolean use);

	public boolean isConnected();

	public void connect() throws Exception;

	/**
	 * 读取之前清空缓存
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public double ReadSingleClearBuffer() throws IOException, InterruptedException;
}
