package com.nlteck.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.nlteck.firmware.Channel;
import com.nlteck.util.CvsUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.ProcedureData.Step;

/**
* @author  wavy_zheng
* @version 创建时间：2022年4月27日 上午8:41:50
* CV电流平滑测试
*/
public class CVCurrentTestor {
    
	
	public final static int FILTER_COUNT = 2;
	public final static int FILTER_SLP_COUNT = 5;
	
	private List<Double> buff = new ArrayList<>();
	
	
	
	@Test
	public void testSlp() {
		
		
		String path = "C:\\Users\\Administrator.BF-20181028DQOU\\Desktop\\现场问题\\新浦派能\\休眠测试.csv";
		CvsUtil util = new CvsUtil(path);
		List<String[]> srcDatas = util.readRecord();
		System.out.println(srcDatas);
		
		List<Double> srcList = new ArrayList<>();
		for(String[] row : srcDatas) {
			
			System.out.println(row[0]);
			if(row[0].isEmpty()) {
				
				break;
			}
			srcList.add(Double.parseDouble(row[0]));
		}
		
		
		
		List<Double> destList = new ArrayList<>();
		
		
       for(int n = 0 ; n < srcList.size() ; n++) {
			
  
    	   destList.add(processSlpVolt(srcList.get(n),true));
		}
		
		//重新生成到第2列
        for(int n = 0 ; n < srcList.size() ; n++) {
			
        	
        	
        	String[] nrow = new String[] {srcList.get(n)+ "" , destList.get(n) + ""};
			
        	util.writeRecord(nrow);
			
		}
        
        util.flush();
		
	}
	
	
	@Test
	public void test() {
		
		String path = "C:\\Users\\Administrator.BF-20181028DQOU\\Desktop\\现场问题\\新浦蜂巢\\60A\\222.csv";
		CvsUtil util = new CvsUtil(path);
		List<String[]> srcDatas = util.readRecord();
		System.out.println(srcDatas);
		
		List<Double> srcList = new ArrayList<>();
		for(String[] row : srcDatas) {
			
			System.out.println(row[0]);
			if(row[0].isEmpty()) {
				
				break;
			}
			srcList.add(Double.parseDouble(row[0]));
		}
		List<Double> destList = new ArrayList<>();
		
		for(int n = 0 ; n < srcList.size() ; n++) {
			
			destList.add(processCvCurrent(srcList.get(n), 1000));
			
		}
		
		//重新生成到第2列
        for(int n = 0 ; n < srcList.size() ; n++) {
			
        	
        	
        	String[] nrow = new String[] {srcList.get(n)+ "" , destList.get(n) + ""};
			
        	util.writeRecord(nrow);
			
		}
        
        util.flush();
		
		
		
		
		
		
		
	}
	

	
	
	private double processChangeData(double data , boolean charge) {

		
		double avg = 0 , std = 0;
		List<Double> processList = new ArrayList<>();
		for (int n = 0; n < buff.size(); n++) {

			avg += buff.get(n);			
			
		}
		avg = avg / buff.size();
		
		for (int n = 0; n < buff.size(); n++) {

			std += Math.pow(buff.get(n) - avg, 2);		
			
		}	
		std = Math.sqrt(std);
			
		System.out.println("std = " + std);
			
		//计算极差
		double maxOffset = std * 3.5;
		
		double lastVolt = buff.get(buff.size() - 1);

		if(maxOffset > 0 && Math.abs(data - lastVolt) > maxOffset) {
			
			buff.add(lastVolt);
			System.out.println(data + ","  + lastVolt + "偏差大于" + maxOffset + ",调整到" + lastVolt);
			
		} else {
		
		   buff.add(data);
		
		}

		
		buff.remove(0);
		
		return buff.get(buff.size() - 1);
		
	}
	
	
	public double processSlpVolt(double data , boolean charge) {
		
		if (buff == null || buff.size() < FILTER_SLP_COUNT) {

			if (buff == null) {

				buff = new ArrayList<>();
				
			}

			buff.add(data);
			
			return data;
		}
		return	processChangeData(data,charge);
			
		
		
		
	}
	
	
	public double processCvCurrent(double data , double endVal) {

		

		if (buff == null || buff.size() < FILTER_COUNT) {

			if (buff == null) {

				buff = new ArrayList<>();
				
			}

			buff.add(data);

			return data;
		}
		boolean maxCurrent = true;
		
		if(data <= buff.get(buff.size() - 1) + 5) {
			
			maxCurrent = false;
		}
		
		/*for(Double val : buff) {
			
			if(val  < data) {
				
				maxCurrent = false;
				break;
			}
			
		}*/
		//添加尾数
		buff.add(data);
		
		//截止电流不做处理
		if(data < endVal) {
			
			System.out.println("end val return");
			return data;
		}
		double val = 0;

		for(int n = 0 ; n < buff.size() ; n++) {
			
			if(maxCurrent) {
				
                if(n == 0) {
					
				    val += buff.get(n) * 0.10;
					
				} else if(n == 1 ) {
					
					val += buff.get(n) * 0.80;
				} else {
					
					val += buff.get(n) * 0.10;
				}
				
			} else {
				
				if(n == 0 ) {
					
					val += buff.get(n) * 0.1;
				} else if(n == 1 ) {
					
					val += buff.get(n) * 0.20;
				} else {
					
					val += buff.get(n) * 0.70;
				}
				
				
			}
		}
        
		buff.set(buff.size() - 1 , val);
		// 更新缓存
		buff.remove(0);
		
		return val;
		
	}
	
	
}
