package com.nlteck.model;

import java.util.ArrayList;
import java.util.List;

import com.nlteck.swtlib.table.TableViewerEx.TableExItem;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月26日 下午1:59:23
* 稳定度测试方案
*/
public class StablePlan {
    
	public static class StableStep implements TableExItem,Comparable {
		
		public int index;
		public double  destVal; //计量目前值
		public CalMode   mode;
		public int       pickCount; //稳定度采集次数
		public int       pickInterval; //采集间隔时间
		public double    maxAdcOffset;  //adc偏差值
		public double    maxMeterOffset; //表偏差值
		public double    maxSigmaAdcOffset;  //adc标准差最大值
		public double    maxSigmaMeterOffset; //表标准差偏差值
		public double    steadySigmaOffset = 2.0; //稳定度标准偏差
		public int       maxSteadyReadCount = 3; //稳定度最大次数
		public long      steadyReadInterval = 1000; //读稳定度时间间隔,ms
		
		@Override
		public void flushItemText(int columnIndex, String text) {
			
			
           switch(columnIndex) {
			
			case 0:
				this.index = Integer.parseInt(text);
				break;
			case 1:
				this.mode = CalMode.valueOf(text);
				break;
			case 2:
				this.destVal = Double.parseDouble(text);
				break;
			case 3:
				this.pickCount = Integer.parseInt(text);
				break;
			case 4:
				this.pickInterval = Integer.parseInt(text);
				break;
			   
			
			}
			
		}

		@Override
		public int compareTo(Object o) {
			
			StableStep ss = (StableStep)o;
			int compare = this.mode.compareTo(ss.mode);
			if(compare == 0) {
				
				return (int) (this.destVal - ss.destVal);
				
			}
			
			return compare;
		}
		
		
		
	}
	
	
	private String  name;
	private int     openModuleDelay = 10000; //通道使能时间
	private int     closeModuleDelay = 100; //通道使能关闭延时
	private List<StableStep>  steps = new ArrayList<>();
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	public List<StableStep> getSteps() {
		return steps;
	}
	public void setSteps(List<StableStep> steps) {
		this.steps = steps;
	}
	
	
	public void addStep(StableStep step) {
		
		steps.add(step);
	}
	
	public void clearSteps() {
		
		steps.clear();
	}
	
	
	public void removeStep(int index) {
		
		steps.remove(index);
	}
	public int getOpenModuleDelay() {
		return openModuleDelay;
	}
	public void setOpenModuleDelay(int openModuleDelay) {
		this.openModuleDelay = openModuleDelay;
	}
	public int getCloseModuleDelay() {
		return closeModuleDelay;
	}
	public void setCloseModuleDelay(int closeModuleDelay) {
		this.closeModuleDelay = closeModuleDelay;
	}
	@Override
	public String toString() {
		return "StablePlan [name=" + name + ", openModuleDelay=" + openModuleDelay + ", closeModuleDelay="
				+ closeModuleDelay + ", steps=" + steps + "]";
	}
	
	
	
	
	
	
	
}
