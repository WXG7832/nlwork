package com.nltecklib.protocol.lab.main;

/**
 * json传输时，通道包内的json数据的组成结构
 * 
 * @author Administrator
 */
public class JsonPack {
   
	private int  index;  //从0开始
	private ProcedureData procedure; // 流程
	private ProtectionParamPack protection; // 保护参数
	private DeviceExceptionData  exeception;  //异常处理
	private int       reference = -1; //引用哪个pack的序号,-1表示不引用，从0开始
	private String    testname;
	

	public ProcedureData getProcedure() {
		return procedure;
	}

	public void setProcedure(ProcedureData procedure) {
		this.procedure = procedure;
	}


	public ProtectionParamPack getProtection() {
		return protection;
	}

	public void setProtection(ProtectionParamPack protection) {
		this.protection = protection;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}


	public int getReference() {
		return reference;
	}

	public void setReference(int reference) {
		this.reference = reference;
	}

	public DeviceExceptionData getExeception() {
		return exeception;
	}

	public void setExeception(DeviceExceptionData exeception) {
		this.exeception = exeception;
	}

	public String getTestname() {
		return testname;
	}

	public void setTestname(String testname) {
		this.testname = testname;
	}
	
	

	
}
