
package com.nlteck.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.dialog.PrecisonConfigDialog;

public class PrecisionConfigHandler {
    @Execute
    public void execute() {
	new PrecisonConfigDialog(new Shell(), SWT.NONE).open();
    }
}