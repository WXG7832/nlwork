package com.nlteck.model;

/**
 * 校准板上的通道
 * @author guofang_ma
 *
 */
public class CalBoardChannel {
	
	private int boardIndex;//所在的校准板序号
	private int chnIndex;//校准板内的通道号
	
	private Channel bindingChannel;

	public CalBoardChannel(int boardIndex, int chnIndex) {
		this.boardIndex=boardIndex;
		this.chnIndex=chnIndex;
	}

	public Channel getBindingChannel() {
		return bindingChannel;
	}

	public void setBindingChannel(Channel bindingChannel) {
		this.bindingChannel = bindingChannel;
	}



	public int getBoardIndex() {
		return boardIndex;
	}

	public int getChnIndex() {
		return chnIndex;
	}

	@Override
	public String toString() {
		return "b [" + chnIndex + "," + (bindingChannel==null?null:bindingChannel.getChnIndex()) + "]";
	}
	
	
	
}
