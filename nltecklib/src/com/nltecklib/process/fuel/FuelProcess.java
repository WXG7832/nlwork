package com.nltecklib.process.fuel;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.process.fuel.ProcessEnvironment.ProcessType;

/**
 * 燃料电池流程类
 * 
 * @author caichao_tang
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FuelProcess {
    private String name;
    private ProcessType processType;
    private ArrayList<ProcessStep> processStepList = new ArrayList<>();
    private int currentIndex;// 当前执行步次，从1开始

    public int getCurrentIndex() {
	return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
	this.currentIndex = currentIndex;
    }

    public ProcessType getProcessType() {
	return processType;
    }

    public void setProcessType(ProcessType processType) {
	this.processType = processType;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public ArrayList<ProcessStep> getProcessStepList() {
	return processStepList;
    }

    public void setProcessStepList(ArrayList<ProcessStep> content) {
	this.processStepList = content;
    }

    /**
     * 流程对象序列化JSON
     * 
     * @return
     * @throws JsonProcessingException
     */
    public String toJson() {

	Gson gson = new GsonBuilder().create();
	return gson.toJson(this);
    }

    /**
     * JSON String 反序列化流程对象
     * 
     * @param json
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static FuelProcess fromJson(String json) {

	Gson gson = new GsonBuilder().create();
	return gson.fromJson(json, FuelProcess.class);
    }

}
