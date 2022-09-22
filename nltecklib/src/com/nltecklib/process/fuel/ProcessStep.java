package com.nltecklib.process.fuel;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nltecklib.process.fuel.ProcessEnvironment.StepType;

/**
 * 燃料电池步次类
 * 
 * @author caichao_tang
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessStep {
    private int stepNo;
    private StepType stepType;
    private ArrayList<BooleanAndDoUnit> conditionList;
    private BooleanAndDoUnit doAction;
    private BooleanAndDoUnit ifBlock;
    private BooleanAndDoUnit elseBlock;

    public int getStepNo() {
	return stepNo;
    }

    public void setStepNo(int stepNo) {
	this.stepNo = stepNo;
    }

    public StepType getStepType() {
	return stepType;
    }

    public void setStepType(StepType stepType) {
	this.stepType = stepType;
    }

    public ArrayList<BooleanAndDoUnit> getConditionList() {
	return conditionList;
    }

    public void setConditionList(ArrayList<BooleanAndDoUnit> conditionList) {
	this.conditionList = conditionList;
    }

    public BooleanAndDoUnit getDoAction() {
	return doAction;
    }

    public void setDoAction(BooleanAndDoUnit doAction) {
	this.doAction = doAction;
    }

    public BooleanAndDoUnit getIfBlock() {
	return ifBlock;
    }

    public void setIfBlock(BooleanAndDoUnit ifBlock) {
	this.ifBlock = ifBlock;
    }

    public BooleanAndDoUnit getElseBlock() {
	return elseBlock;
    }

    public void setElseBlock(BooleanAndDoUnit elseBlock) {
	this.elseBlock = elseBlock;
    }

    /**
     * 获得指定的执行动作块
     * 
     * @param actionBlockString
     * @return
     */
    public BooleanAndDoUnit getActionBlock(String actionBlockString) {
	switch (actionBlockString) {
	case "doAction":
	    return this.doAction;
	case "ifBlock":
	    return this.ifBlock;
	case "elseBlock":
	    return this.elseBlock;
	default:
	    return null;
	}
    }

    /**
     * 设置指定的动作块
     * 
     * @param actionBlockString doAction/ifBlock/elseBlock
     * @param actionBlock
     */
    public void setActionBlock(String actionBlockString, BooleanAndDoUnit actionBlock) {
	switch (actionBlockString) {
	case "doAction":
	    this.doAction = actionBlock;
	    break;
	case "ifBlock":
	    this.ifBlock = actionBlock;
	    break;
	case "elseBlock":
	    this.elseBlock = actionBlock;
	    break;
	default:
	}
    }
}
