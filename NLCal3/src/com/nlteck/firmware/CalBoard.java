package com.nlteck.firmware;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.cal.CalEnvironment.TestType;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;

/**
 * 校准板抽象
 * 
 * @author caichao_tang
 *
 */
public class CalBoard {

	private CalBox calBox;
	private int index;
	private boolean open;
    private DriverBoard dbBind;
	private int channelCount;

	private List<Channel> channelList;
	
	
	
	//校准板测试模式
	private TestType          testType;

	public CalBoard(CalBox box, int index, boolean open) {

		this.calBox = box;
		this.index = index;
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}

	public CalBox getCalBox() {
		return calBox;
	}

	public void setCalBox(CalBox calBox) {
		this.calBox = calBox;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public List<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Channel> channelList) {
		this.channelList = channelList;
	}

	public DriverBoard getDbBind() {
		return dbBind;
	}

	public void setDbBind(DriverBoard dbBind) {
		this.dbBind = dbBind;
	}
	
	

	public TestType getTestType() {
		return testType;
	}

	public void setTestType(TestType testType) {
		this.testType = testType;
	}
	
	

}
