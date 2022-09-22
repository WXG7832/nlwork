package com.nlteck.service;

import java.util.HashMap;
import java.util.Map;

import com.nlteck.fireware.CalBoard;
import com.nlteck.fireware.CalibrateCore;
import com.nlteck.model.CalBoardChannel;
import com.nlteck.model.Channel;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.main.MainEnvironment.State;

public class VirtualService {

	private CalibrateCore core;
	private State state = State.NORMAL;

	private Map<CalBoardChannel, Channel> bindingMap = new HashMap<CalBoardChannel, Channel>();

	public VirtualService(CalibrateCore core) {
		this.core = core;
	}

	public void virtualBinding() {
		for (int calIndex : core.getCalBoardMap().keySet()) {
			CalBoard calBoard = core.getCalBoardMap().get(calIndex);
			if (!calBoard.isDisabled()) {
				for (CalBoardChannel cch : calBoard.getCalBoardChannels()) {
					Channel channel = core.getDeviceCore().getChannelMap()
							.get(cch.getBoardIndex() * core.getDeviceCore().getDriverChnCount() + cch.getChnIndex());
					bindingMap.put(cch, channel);
				}
			}
		}
	}

	public void cfgVoltBase(int calIndex, int chnIndex, WorkState workState, double voltBase) {
		for (CalBoardChannel cch : bindingMap.keySet()) {
			if (cch.getBoardIndex() == calIndex && cch.getChnIndex() == chnIndex) {
				bindingMap.get(cch).getVirtualChannelData().virtualVolt = workState == WorkState.WORK ? voltBase : 0;
			} else {
//				bindingMap.get(cch).setVirtualVolt(0);
			}
		}
	}

}
