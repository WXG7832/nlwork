 
package com.nlteck.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

import com.nlteck.E4LifeCycle;
import com.nlteck.parts.CalibrateConfigPart;
import com.nlteck.parts.StablePlanPart;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class StablePlanHandler {
	
	
	@Execute
	public void execute() {
		
		E4LifeCycle.showOrCreatePart(StablePlanPart.ID, "稳定度测试方案");
		
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}