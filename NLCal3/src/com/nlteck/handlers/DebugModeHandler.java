package com.nlteck.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import com.nlteck.E4LifeCycle;
import com.nlteck.parts.DebugModePart;

public class DebugModeHandler {

    @CanExecute
    public boolean canExecute(EPartService partService) {
	return true;
    }

    @Execute
    public void execute(EPartService partService) {
	partService.saveAll(false);
	E4LifeCycle.showOrCreatePart(DebugModePart.ID, "µ÷ÊÔÄ£Ê½");
    }

}