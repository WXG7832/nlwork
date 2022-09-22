package com.nlteck.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.Test;

import com.nlteck.base.BaseCfgManager.AdcAdjust;
import com.nlteck.fireware.CalibrateCore;
import com.nlteck.model.Channel;
import com.nlteck.model.TestDot;
import com.nltecklib.io.broadcast.BroadcastManager;
import com.nltecklib.protocol.li.PCWorkform.BaseCfgData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.utils.CvsUtil;

public class Tools {

//	public static void main(String[] args) {
//		
//		CalibrateCore core = new CalibrateCore();
//		core.init();
//		BaseCfgData data=new BaseCfgData();
//		List<String>meterIPs=new ArrayList<>();
//		meterIPs.add("192.168.1.150");
//		data.setDeviceIp("192.168.1.127");
//		data.setScreenIp("0.0.0.0");
//		data.setMeterIps(meterIPs);
//		
//		try {
//			core.cfgBaseCfg(data);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

	public static void main1(String[] args) {
		try {
			System.out.println(new BroadcastManager().sendBroadcaseAndRecv("192.168.1.166", "192.168.1.127", 5000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	

	
//	public static void convertCoreCsv(String path) throws IOException {
//		System.out.println("start convert core csv :" + path);
//		CalibrateCore core = new CalibrateCore();
//		core.init();
//
//		if (core.getDiskService().initDeviceFromXml(core.getDeviceCore(), path)) {
//			core.getDiskService().initChannelFromCsv(core.getDeviceCore(), path);
//		}
//
//		core.getDeviceCore().getChannelMap().entrySet().stream().forEach(x -> {
//			try {
//				core.getDiskService().appendDeviceTestDotsCsv(x.getValue(), path + "\\export");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		});

//		for (core.getDeviceCore().getChannelMap()) {
//			core.getDiskService().appendDeviceTestDotsCsv(core.getDeviceCore().getChannelMap().get(k),
//					path + "\\export");
//		}

//		System.out.println("ok");
//	}
}
