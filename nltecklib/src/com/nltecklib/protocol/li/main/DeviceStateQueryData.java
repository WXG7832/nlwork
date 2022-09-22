package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;

import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatLine;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMeterCurrent;
import com.nltecklib.protocol.li.accessory.AccessoryEnvironment.HeatMode;
import com.nltecklib.protocol.li.check2.Check2Environment;
import com.nltecklib.protocol.li.logic2.Logic2Environment.LogicState;
import com.nltecklib.protocol.li.main.MainEnvironment.AirPressureState;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.CheckMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.CoreData;
import com.nltecklib.protocol.li.main.MainEnvironment.CylinderMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.DriverMonitorState;
import com.nltecklib.protocol.li.main.MainEnvironment.FanMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.FittingData;
import com.nltecklib.protocol.li.main.MainEnvironment.FittingRunState;
import com.nltecklib.protocol.li.main.MainEnvironment.FittingType;
import com.nltecklib.protocol.li.main.MainEnvironment.FittingWorkState;
import com.nltecklib.protocol.li.main.MainEnvironment.FixtureMonitorState;
import com.nltecklib.protocol.li.main.MainEnvironment.LogicMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.PingStateMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.PowerMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.ProbeMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.State;
import com.nltecklib.protocol.li.main.MainEnvironment.SwitchState;
import com.nltecklib.protocol.li.main.MainEnvironment.TempMeterMonitorData;
import com.nltecklib.protocol.li.main.MainEnvironment.TrayState;
import com.nltecklib.protocol.power.driver.DriverCheckData;
import com.nltecklib.protocol.li.main.MainEnvironment.ControlUnitMonitorData;
import com.nltecklib.protocol.util.ProtocolUtil;

public class DeviceStateQueryData extends Data implements Queryable, Responsable {


	// Çý¶¯°å×´Ì¬
	private List<DriverMonitorState> driverStates = new ArrayList<DriverMonitorState>();

	// ¿ØÖÆ·ÖÇø×´Ì¬¼¯ºÏ
	private List<ControlUnitMonitorData> controlUnitStates = new ArrayList<ControlUnitMonitorData>();

	// Ö÷¿Ø×´Ì¬
	private CoreData coreData = new CoreData();
	// Âß¼­°å×´Ì¬¼¯ºÏ
	private List<LogicMonitorData> logicsData = new ArrayList<LogicMonitorData>();
	// »Ø¼ì°å×´Ì¬¼¯ºÏ
	private List<CheckMonitorData> checksData = new ArrayList<CheckMonitorData>();
	// Õë´²×´Ì¬¼¯ºÏ
	private List<PingStateMonitorData> pingsData = new ArrayList<PingStateMonitorData>();
	
	
	// ÎÂ¿Ø±í×´Ì¬¼¯ºÏ
	private List<TempMeterMonitorData> tempBoardsData = new ArrayList<TempMeterMonitorData>();
	
	// Æø¸××´Ì¬
	private List<CylinderMonitorData> cylinderDatas = new ArrayList<CylinderMonitorData>();
	//Ì½²â×é¼þ×´Ì¬
	private List<ProbeMonitorData>  probeDatas = new ArrayList<ProbeMonitorData>();
	//·ç»ú×´Ì¬
	private List<FanMonitorData>  fanDatas = new ArrayList<FanMonitorData>();
	//µçÔ´×´Ì¬
	private List<PowerMonitorData> powerDatas = new ArrayList<PowerMonitorData>();
	//¼Ð¾ß»ú×´Ì¬
	private List<FixtureMonitorState>  fixtureDatas = new ArrayList<FixtureMonitorState>();
	
   
	@Override
	public void encode() {
        
		//Ö÷¿Ø×´Ì¬
	
		GsonBuilder build = new GsonBuilder();
		
		build.setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				
				if(f.getName().equals("result") || f.getName().equals("data")) {
					
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				
				
				return false;
			}
			
			
		});
		
		Gson gson = build.create();
	
		String content = gson.toJson(this);
		try {
			byte[] bytes = content.getBytes("utf-8");
			data.addAll(ProtocolUtil.convertArrayToList(bytes));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("convert content to bytes error:" + e.getMessage());
		}
		
		
	}

	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		GsonBuilder build = new GsonBuilder();
		
		build.setExclusionStrategies(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				
				if(f.getName().equals("result") || f.getName().equals("data")) {
					
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				
				
				return false;
			}
			
			
		});
		Gson gson = build.create();
		
		
		//×ª³É×Ö·û´®
		String content;
		try {
			content = new String(ProtocolUtil.convertListToArray(data) , "utf-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
			throw new RuntimeException("convert byte to json string error:" + e.getMessage());
		}
		DeviceStateQueryData dsqd = gson.fromJson(content, this.getClass());
	    this.coreData = dsqd.getCoreData();
	    this.cylinderDatas = dsqd.getCylinderDatas();
	    this.checksData = dsqd.getChecksData();
	    this.controlUnitStates = dsqd.getControlUnitStates();
	    this.logicsData = dsqd.getLogicsData();
	    this.driverStates = dsqd.getDriverStates();
	    this.fanDatas = dsqd.getFanDatas();
	    this.fixtureDatas = dsqd.getFixtureDatas();
	    this.powerDatas = dsqd.getPowerDatas();
	    this.probeDatas = dsqd.getProbeDatas();
	    this.tempBoardsData = dsqd.getTempBoardsData();
	    this.pingsData   = dsqd.getPingsData();
	   
	   

	}

	
	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.DeviceStateCode;
	}

	@Override
	public boolean supportUnit() {

		return false;
	}

	
	public List<DriverMonitorState> getDriverStates() {
		return driverStates;
	}

	public void setDriverStates(List<DriverMonitorState> driverStates) {
		this.driverStates = driverStates;
	}

	public CoreData getCoreData() {
		return coreData;
	}

	public List<LogicMonitorData> getLogicsData() {
		return logicsData;
	}

	public void setCoreData(CoreData coreData) {
		this.coreData = coreData;
	}

	public void setLogicsData(List<LogicMonitorData> logicsData) {
		this.logicsData = logicsData;
	}
	

	public List<TempMeterMonitorData> getTempBoardsData() {
		return tempBoardsData;
	}


	public void setTempBoardsData(List<TempMeterMonitorData> tempBoardsData) {
		this.tempBoardsData = tempBoardsData;
	}

	public List<ControlUnitMonitorData> getControlUnitStates() {
		return controlUnitStates;
	}

	public void setControlUnitStates(List<ControlUnitMonitorData> controlUnitStates) {
		this.controlUnitStates = controlUnitStates;
	}

	public List<CheckMonitorData> getChecksData() {
		return checksData;
	}

	public void setChecksData(List<CheckMonitorData> checksData) {
		this.checksData = checksData;
	}

	public List<CylinderMonitorData> getCylinderDatas() {
		return cylinderDatas;
	}

	public void setCylinderDatas(List<CylinderMonitorData> cylinderDatas) {
		this.cylinderDatas = cylinderDatas;
	}

	public List<ProbeMonitorData> getProbeDatas() {
		return probeDatas;
	}

	public void setProbeDatas(List<ProbeMonitorData> probeDatas) {
		this.probeDatas = probeDatas;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<FanMonitorData> getFanDatas() {
		return fanDatas;
	}

	public void setFanDatas(List<FanMonitorData> fanDatas) {
		this.fanDatas = fanDatas;
	}

	public List<PowerMonitorData> getPowerDatas() {
		return powerDatas;
	}

	public void setPowerDatas(List<PowerMonitorData> powerDatas) {
		this.powerDatas = powerDatas;
	}

	public List<FixtureMonitorState> getFixtureDatas() {
		return fixtureDatas;
	}

	public void setFixtureDatas(List<FixtureMonitorState> fixtureDatas) {
		this.fixtureDatas = fixtureDatas;
	}
	
	
	

	public List<PingStateMonitorData> getPingsData() {
		return pingsData;
	}

	public void setPingsData(List<PingStateMonitorData> pingsData) {
		this.pingsData = pingsData;
	}

	@Override
	public String toString() {
		return "DeviceStateQueryData [driverStates=" + driverStates + ", controlUnitStates=" + controlUnitStates
				+ ", coreData=" + coreData + ", logicsData=" + logicsData + ", checksData=" + checksData
				+ ", tempBoardsData=" + tempBoardsData + ", cylinderDatas=" + cylinderDatas + ", probeDatas="
				+ probeDatas + ", fanDatas=" + fanDatas + ", powerDatas=" + powerDatas + ", fixtureDatas="
				+ fixtureDatas + "]";
	}
	
	


	
	
	
	
	
	

}
