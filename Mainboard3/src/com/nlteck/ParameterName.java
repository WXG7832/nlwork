package com.nlteck;

import com.nltecklib.protocol.li.main.CCProtectData;
import com.nltecklib.protocol.li.main.CVProtectData;
import com.nltecklib.protocol.li.main.CheckVoltProtectData;
import com.nltecklib.protocol.li.main.DCProtectData;
import com.nltecklib.protocol.li.main.DeviceProtectData;
import com.nltecklib.protocol.li.main.FirstCCProtectData;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.li.main.PressureChangeProtectData;
import com.nltecklib.protocol.li.main.SlpProtectData;
import com.nltecklib.protocol.li.main.StartEndCheckData;
import com.nltecklib.protocol.li.main.TempData;

public class ParameterName {

	private String name = "";
	private WorkType workType = WorkType.AG;
	private boolean defaultPlan;
	
	private CCProtectData ccProtect = new CCProtectData();
	private DCProtectData dcProtect = new DCProtectData();
	private CVProtectData cvProtect = new CVProtectData();
	private SlpProtectData slpProtect = new SlpProtectData();
	private FirstCCProtectData firstCCProtect = new FirstCCProtectData();
	private DeviceProtectData deviceProtect = new DeviceProtectData();
	private PoleData poleProtect = new PoleData();
	private PressureChangeProtectData pressureProtect = new PressureChangeProtectData();
	private CheckVoltProtectData checkVoltProtect = new CheckVoltProtectData();
	private TempData             tempProtect      = new TempData();
	private StartEndCheckData    startEndCheckProtect = new StartEndCheckData();
	
	public ParameterName(WorkType wt) {
		
		this.workType = wt;
				
	}
	
	public ParameterName(WorkType wt , boolean retest) {
		
		this.workType = wt;
		this.defaultPlan = retest;
	}
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public boolean isDefaultPlan() {
		return defaultPlan;
	}

	public void setDefaultPlan(boolean defaultPlan) {
		this.defaultPlan = defaultPlan;
	}

	public CCProtectData getCcProtect() {
		return ccProtect;
	}

	public void setCcProtect(CCProtectData ccProtect) {
		this.ccProtect = ccProtect;
	}

	public DCProtectData getDcProtect() {
		return dcProtect;
	}

	public void setDcProtect(DCProtectData dcProtect) {
		this.dcProtect = dcProtect;
	}

	public CVProtectData getCvProtect() {
		return cvProtect;
	}

	public void setCvProtect(CVProtectData cvProtect) {
		this.cvProtect = cvProtect;
	}

	public SlpProtectData getSlpProtect() {
		return slpProtect;
	}

	public void setSlpProtect(SlpProtectData slpProtect) {
		this.slpProtect = slpProtect;
	}

	public FirstCCProtectData getFirstCCProtect() {
		return firstCCProtect;
	}

	public void setFirstCCProtect(FirstCCProtectData firstCCProtect) {
		this.firstCCProtect = firstCCProtect;
	}

	public DeviceProtectData getDeviceProtect() {
		return deviceProtect;
	}

	public void setDeviceProtect(DeviceProtectData deviceProtect) {
		this.deviceProtect = deviceProtect;
		
	}

	public PoleData getPoleProtect() {
		return poleProtect;
	}

	public void setPoleProtect(PoleData poleProtect) {
		this.poleProtect = poleProtect;
	}

	public PressureChangeProtectData getPressureProtect() {
		return pressureProtect;
	}

	public void setPressureProtect(PressureChangeProtectData pressureProtect) {
		this.pressureProtect = pressureProtect;
	}

	public CheckVoltProtectData getCheckVoltProtect() {
		return checkVoltProtect;
	}

	public void setCheckVoltProtect(CheckVoltProtectData checkVoltProtect) {
		this.checkVoltProtect = checkVoltProtect;
	}

	public TempData getTempProtect() {
		return tempProtect;
	}

	public void setTempProtect(TempData tempProtect) {
		this.tempProtect = tempProtect;
	}

	public StartEndCheckData getStartEndCheckProtect() {
		return startEndCheckProtect;
	}

	public void setStartEndCheckProtect(StartEndCheckData startEndCheckProtect) {
		this.startEndCheckProtect = startEndCheckProtect;
	}
	
	
	

}
