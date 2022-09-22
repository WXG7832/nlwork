package com.nlteck.firmware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.nlteck.model.ChannelDO;
import com.nlteck.model.LogItem;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;

/**
 * 驱动板对象,主要校准对象
 * 
 * @author Administrator
 *
 */
public class DriverBoard {
	private LogicBoard logicBoard;
	private int driverIndexInLogic;
	private boolean open = true;
	private List<ChannelDO> channels = new ArrayList<ChannelDO>();
	private CalBoard calboardBind; // 绑定的校准板

	/**
	 * 推送的测试点数据
	 */
	private List<UploadTestDot> uploadTestDotList = new CopyOnWriteArrayList<>();
	/**
	 * 推送的驱动板日志
	 */
	private List<LogItem> logData = new CopyOnWriteArrayList<>();
	/**
	 * 主控推送日志
	 */
	private List<PushLog> pushLogList = new CopyOnWriteArrayList<>();

	public List<PushLog> getPushLogList() {
		return pushLogList;
	}

	public List<UploadTestDot> getUploadTestDotList() {
		return uploadTestDotList;
	}

	public void setUploadTestDotList(List<UploadTestDot> uploadTestDotList) {
		this.uploadTestDotList = uploadTestDotList;
	}

	public void setPushLogList(List<PushLog> pushLogList) {
		this.pushLogList = pushLogList;
	}

	public List<LogItem> getLogData() {
		return logData;
	}

	public void addLogData(LogItem logItem) {
		this.logData.add(logItem);
	}

	public void setLogData(List<LogItem> logData) {
		this.logData = logData;
	}

	public DriverBoard(LogicBoard logicBoard, int index) {

		this.logicBoard = logicBoard;
		this.driverIndexInLogic = index;

		int driverChnCount = logicBoard.getDevice().getChnNumInDriver();

		channels = logicBoard.getChannels().subList(index * driverChnCount, (index + 1) * driverChnCount);

	}

	/**
	 * 获取驱动板在设备内的序号
	 * 
	 * @return
	 */
	public int getDriverIndexInDevice() {
		int driverNumInLogic = logicBoard.getDrivers().size();
		return logicBoard.getLogicIndex() * driverNumInLogic + driverIndexInLogic;
	}

	public List<ChannelDO> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelDO> channels) {
		this.channels = channels;
	}

	public LogicBoard getLogicBoard() {
		return logicBoard;
	}

	public int getDriverIndex() {
		return driverIndexInLogic;
	}

	public void setDriverIndex(int driverIndex) {
		this.driverIndexInLogic = driverIndex;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setLogicBoard(LogicBoard logicBoard) {
		this.logicBoard = logicBoard;
	}

	/**
	 * 绑定校准板
	 * 
	 * @author wavy_zheng 2021年2月9日
	 * @param board
	 */
	public void bind(CalBoard board) {

		if (board != null) {
			if (board.getDbBind() != null) {

				board.getDbBind().bind(null);
			}
			this.calboardBind = board;
			board.setDbBind(this);

		} else {
             
			// 解绑
			if (calboardBind != null) {
				calboardBind.setDbBind(null);
				calboardBind = null;
			}
		}
	}
	
	public CalBoard getBindCalBoard() {
		
		return calboardBind;
	}

}
