package com.nlteck.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import com.nlteck.E4LifeCycle;
import com.nlteck.parts.CalculateConfigPart;

public class CalculateHandler {

    @CanExecute
    public boolean canExecute(EPartService partService) {
	return true;
    }

    @Execute
    public void execute(EPartService partService) {
	partService.saveAll(false);
	E4LifeCycle.showOrCreatePart(CalculateConfigPart.ID, "计量方案");
    }

}