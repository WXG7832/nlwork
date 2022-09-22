package com.nlteck.util;

import java.util.ArrayList;
import java.util.List;

/**
* @author  wavy_zheng
* @version 创建时间：2020年7月7日 下午2:48:18
* 数学计算工具辅助类
*/
public class MathUtil {
   
	
	/**
	 * 获取数据变化平均值
	 * @author  wavy_zheng
	 * 2021年5月18日
	 * @param datas
	 * @return
	 */
	public static double calculateChangeStep(List<Double> datas) {
		
		
        if(datas.size() < 2) {
			
			return 0;
		}
        List<Double> steps = new ArrayList<>();
        for(int n = 0 ; n < datas.size() - 1 ; n++) {
			
			 steps.add(datas.get(n+1) - datas.get(n));
		}
        
        return calculateMean(steps);
		
	}
	
	
	/**
	 * 计算平均数
	 * @author  wavy_zheng
	 * 2021年5月18日
	 * @param datas
	 * @return
	 */
	public static double calculateMean(List<Double> datas) {
		
		if(datas.isEmpty()) {
			
			return 0;
		}
		double val = 0;
        for(int n = 0 ; n < datas.size() ; n++) {
			
			val += datas.get(n);	
		}
        return val / datas.size();
	}
	
	/**
	 * 计算标准差
	 * @author  wavy_zheng
	 * 2020年7月7日
	 * @param mean
	 * @param datas
	 * @return
	 */
	public static double calculateStd(double mean , List<Double> datas) {
		
		if(datas.isEmpty()) {
			
			return 0;
		}
		
		double val = 0;
		for(int n = 0 ; n < datas.size() ; n++) {
			
			val += Math.pow(datas.get(n) - mean , 2);
			
		}
		return Math.sqrt(val / datas.size());
		
		
	}
	
	
	
}
