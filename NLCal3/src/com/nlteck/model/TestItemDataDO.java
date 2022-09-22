package com.nlteck.model;

import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.BaseCfg.TestName;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月27日 下午3:28:24
* 测试项目，除校准计量
*/
public class TestItemDataDO {
   
	private  int      id;
	private  TestName   name;
	private  RunMode  runMode;
	private  CalMode  calMode;
	private  Pole     pole;
	private  double   testVal;
	private  double   lower;
	private  double   upper;
	private  String   info = "";
	private  String   state = "" ; //测试状态
	private  long     milisecs; //测试消耗时间
	private  int      chn_id;
	private  Object   param; //附带参数
	private int        index;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public TestName getName() {
		return name;
	}
	public void setName(TestName name) {
		this.name = name;
	}
	public RunMode getRunMode() {
		return runMode;
	}
	public void setRunMode(RunMode runMode) {
		this.runMode = runMode;
	}
	public double getTestVal() {
		return testVal;
	}
	public void setTestVal(double testVal) {
		this.testVal = testVal;
	}
	public double getLower() {
		return lower;
	}
	public void setLower(double lower) {
		this.lower = lower;
	}
	public double getUpper() {
		return upper;
	}
	public void setUpper(double upper) {
		this.upper = upper;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public long getMilisecs() {
		return milisecs;
	}
	public void setMilisecs(long milisecs) {
		this.milisecs = milisecs;
	}
	public int getChn_id() {
		return chn_id;
	}
	public void setChn_id(int chn_id) {
		this.chn_id = chn_id;
	}
	
	
	public Object getParam() {
		return param;
	}
	public void setParam(Object param) {
		this.param = param;
	}
	public CalMode getCalMode() {
		return calMode;
	}
	public void setCalMode(CalMode calMode) {
		this.calMode = calMode;
	}
	public Pole getPole() {
		return pole;
	}
	public void setPole(Pole pole) {
		this.pole = pole;
	}
	
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public String toString() {
		return "TestItemDataDO [id=" + id + ", name=" + name + ", runMode=" + runMode + ", calMode=" + calMode
				+ ", pole=" + pole + ", testVal=" + testVal + ", lower=" + lower + ", upper=" + upper + ", info=" + info
				+ ", state=" + state + ", milisecs=" + milisecs + ", chn_id=" + chn_id + ", param=" + param + "]";
	}
	
	
	
	
	
	
	
	
	
}
