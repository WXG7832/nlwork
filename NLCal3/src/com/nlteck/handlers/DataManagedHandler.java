package com.nlteck.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import com.nlteck.dialog.DataHandleDialog;

public class DataManagedHandler {
    @Execute
    public void execute(Shell shell) {
	new DataHandleDialog(shell, SWT.NONE).open();
    }

}