package com.nlteck.dialog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.utils.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class WaitUnknowDialog extends Dialog {

    protected Object result;
    protected Shell shell;
    private String info;

    public WaitUnknowDialog(Shell parent, int style, String info) {
	super(parent, style);
	this.info = info;
	setText("SWT Dialog");
    }

    private Object open() {
	createContents();
	shell.open();
	shell.layout();
	Display display = getParent().getDisplay();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	return result;
    }

    private void createContents() {
	shell = new Shell(getParent(), SWT.NO_TRIM | SWT.APPLICATION_MODAL);
	shell.setAlpha(200);
	shell.setSize(500, 52);
	shell.setText(getText());

	UIUtil.setShellAlignCenter(shell);
	shell.setLayout(new FillLayout(SWT.HORIZONTAL));

	Composite composite = new Composite(shell, SWT.NONE);
	composite.setLayout(new GridLayout(1, false));

	ProgressBar progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
	progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label infoLabel = new Label(composite, SWT.NONE);
	infoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	infoLabel.setText(info);

    }

    /**
     * 打开一个未知时间的等待弹窗
     * 
     * @param parentShell
     * @param style
     * @param info
     * @return
     */
    public static WaitUnknowDialog openWaitUnknowDialog(Shell parentShell, int style, String info) {
	WaitUnknowDialog waitUnknowDialog = new WaitUnknowDialog(parentShell, style, info);
	new Thread(() -> {
	    Display.getDefault().asyncExec(() -> {
		waitUnknowDialog.open();
	    });
	}).start();
	return waitUnknowDialog;

    }

    /**
     * 关闭等待弹窗
     */
    public void closeWaitUnknowDialog() {
	shell.dispose();
    }

}
