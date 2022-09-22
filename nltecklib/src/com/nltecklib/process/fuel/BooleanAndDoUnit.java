package com.nltecklib.process.fuel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nltecklib.process.fuel.ProcessEnvironment.ConditionSymbol;
import com.nltecklib.process.fuel.ProcessEnvironment.LogicSymbol;
import com.nltecklib.process.fuel.ProcessEnvironment.OperationType;
import com.nltecklib.process.fuel.unitExtend.ElecLoad;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * 步次的布尔单元或执行单元
 * 
 * @author caichao_tang
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BooleanAndDoUnit extends ElecLoad {
    private Component component;
    private boolean open;
    private double value;
    private long waitTime;
    private int jumpStep;
    private String jumpProcess;
    private OperationType operationType;
    private LogicSymbol logicSymbol;
    private ConditionSymbol conditionSymbol;

    public LogicSymbol getLogicSymbol() {
	return logicSymbol;
    }

    public void setLogicSymbol(LogicSymbol logicSymbol) {
	this.logicSymbol = logicSymbol;
    }

    public ConditionSymbol getConditionSymbol() {
	return conditionSymbol;
    }

    public void setConditionSymbol(ConditionSymbol conditionSymbol) {
	this.conditionSymbol = conditionSymbol;
    }

    public long getWaitTime() {
	return waitTime;
    }

    public void setWaitTime(long waitTime) {
	this.waitTime = waitTime;
    }

    public int getJumpStep() {
	return jumpStep;
    }

    public void setJumpStep(int jumpStep) {
	this.jumpStep = jumpStep;
    }

    public String getJumpProcess() {
	return jumpProcess;
    }

    public void setJumpProcess(String jumpProcess) {
	this.jumpProcess = jumpProcess;
    }

    public Component getComponent() {
	return component;
    }

    public void setComponent(Component component) {
	this.component = component;
    }

    public boolean isOpen() {
	return open;
    }

    public void setOpen(boolean isOpen) {
	this.open = isOpen;
    }

    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    public OperationType getOperationType() {
	return operationType;
    }

    public void setOperationType(OperationType operationType) {
	this.operationType = operationType;
    }

}