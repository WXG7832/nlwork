package com.nltecklib.utils;

/**
 * pid算法工具
 * @author Administrator
 *
 */
public class PIDAlgorithm {
   
	private double rateFactor; //比例系数
	private double integrateFactor; //积分系数
	private double differentialFactor; //微分系数
	private double dest; //目标值
	
	//积分值
	private double integrateVal;
	//微分值
	private double differentialVal ;
	private double lastVal;
	
	public PIDAlgorithm(double dest , double rateFactor , double integrateFactor , double differentialFactor) {
		
		this.dest = dest;
		this.rateFactor = rateFactor;
		this.integrateFactor = integrateFactor;
		this.differentialFactor = differentialFactor;
	}
	
	public double compute(double val) {
		
		double delta = dest - val ;
		integrateVal += delta;
		differentialVal += lastVal - val;
		val += delta * rateFactor + integrateVal * integrateFactor + differentialVal * differentialFactor;
		lastVal = val;
		return val;
	}
	
	
	
}
