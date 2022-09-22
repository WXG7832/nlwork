package com.nlteck.service.accessory.firmware;

import com.nlteck.AlertException;
import com.nlteck.firmware.MainBoard;

/**
* @author  wavy_zheng
* @version 创建时间：2020年12月25日 上午11:45:28
* 探测器
*/
public abstract class AbsProbe {
   
	protected int   index;
	protected boolean use;
	protected double  value; //检测值
	protected boolean monitor;
	
	protected AbsProbe(int index) {
		
		this.index = index;
		use = MainBoard.startupCfg.getProbeManagerInfo().probeInfos.get(index).use;
        monitor =  MainBoard.startupCfg.getProbeManagerInfo().probeInfos.get(index).monitor;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public boolean isUse() {
		return use;
	}
	
	
	
}
