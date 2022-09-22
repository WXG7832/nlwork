package com.nlteck.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月26日 下午1:44:52
* 类说明
*/
public class BaseCfgDlg extends Dialog {

	public BaseCfgDlg(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container=new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		createCfg(container);
		return container;
	}



	private void createCfg(Composite container) {
		Composite composite=new Composite(container,SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
//		CLabel label=new CLabel(composite, SWT.NONE);
//		label.setText("isUseHugeChns");
		Button button =new Button(composite, SWT.CHECK);
		button.setText("isUseHugeChns");
 	}



	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("基本配置");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(380, 630);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
