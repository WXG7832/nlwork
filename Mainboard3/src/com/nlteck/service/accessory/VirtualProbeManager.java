package com.nlteck.service.accessory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nlteck.Context;
import com.nlteck.firmware.MainBoard;
import com.nlteck.util.CommonUtil;
import com.nltecklib.io.serialport.SerialConnector;
import com.nltecklib.protocol.li.accessory.TempProbeData;
import com.nltecklib.protocol.power.Entity;
import com.nltecklib.protocol.power.QueryDecorator;
import com.nltecklib.protocol.power.ResponseDecorator;
import com.nltecklib.protocol.power.temper.TempSwPickData;
import com.nltecklib.protocol.power.temper.TemperAdjustData;
import com.nltecklib.protocol.power.temper.TempSwPickData.Temper;
import com.rm5248.serial.SerialPort;

public class VirtualProbeManager extends ProbeManager {

	private List<TempSwPickData> list = new ArrayList<>();

	public VirtualProbeManager(MainBoard mainBoard) throws AlertException {
		super(mainBoard);

		for (int n = 0; n < probeInfos.size(); n++) {

			TempSwPickData data = new TempSwPickData();
			data.setDriverIndex(0);
			data.setOptoSwitch(0);

			List<Temper> list1 = new ArrayList<>();
			list1.add(new Temper(0, 25.5));
			list1.add(new Temper(1, 24.5));
			list1.add(new Temper(2, 23.5));
			list1.add(new Temper(3, 22.5));
			list1.add(new Temper(4, 21.5));
			data.setTempers(list1);

			list.add(data);

		}

	}

	@Override
	public TempSwPickData readTempList(int index) throws Exception {
         
		if(index >=  probeInfos.size()) {
			
			return null;
		}
		return list.get(index);
		
	}

	@Override
	public void setConstantTemp(int driverIndex, double temp) throws Exception {
		
		if(driverIndex >=  probeInfos.size()) {
			
			return;
		}
		System.out.println(String.format("当前驱动板号：%d,温度：%.1f", (driverIndex + 1), temp));
	}
}
