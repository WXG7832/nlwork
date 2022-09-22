package com.nlteck.calModel.model;

import com.nlteck.model.ChannelDO;

/**
 * 校准板上的通道
 * @version xingguo_wang 2022.4.12 
 */
public class CalBoardChannel {
	private int calBoardIndex; //校准板索引
	private int calBoardChannelIndex; //校准板上通道索引
	
	private ChannelDO bindingChannel; // 校准通道绑定的驱动通道
	
	public CalBoardChannel() {
		
	}
	public CalBoardChannel(int calBoardIndex,int calBoardChannelIndex) {
		this.calBoardIndex=calBoardIndex;
		this.calBoardChannelIndex=calBoardChannelIndex;
	}
	
	public int getCalBoardIndex() {
		return calBoardIndex;
	}
	public void setCalBoardIndex(int calBoardIndex) {
		this.calBoardIndex = calBoardIndex;
	}
	public int getCalBoardChannelIndex() {
		return calBoardChannelIndex;
	}
	public void setCalBoardChannelIndex(int calBoardChannelIndex) {
		this.calBoardChannelIndex = calBoardChannelIndex;
	}
	public ChannelDO getBindingChannel() {
		return bindingChannel;
	}
	public void setBindingChannel(ChannelDO bindingChannel) {
		this.bindingChannel = bindingChannel;
	}

}
