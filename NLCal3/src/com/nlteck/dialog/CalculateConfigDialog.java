package com.nlteck.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.nlteck.firmware.WorkBench;
import com.nlteck.utils.UIUtil;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

/**
 * 
 * 计量方案设置弹窗
 * 弃用，使用CalculateDotDlg 代替
 * @author caichao_tang
 *
 */
@Deprecated
public class CalculateConfigDialog extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CalculateConfigDialog(Shell parent, int style) {
		super(parent, style);
		setText("计量方案设置");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
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

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(680, 208);
		UIUtil.setShellAlignCenter(shell);
		shell.setText(getText());
		shell.setLayout(new FormLayout());

		Group otherSetGroup = new Group(shell, SWT.NONE);
		otherSetGroup.setLayout(new GridLayout(6, false));
		FormData fd_grpDfd = new FormData();
		fd_grpDfd.top = new FormAttachment(0, 10);
		fd_grpDfd.bottom = new FormAttachment(0, 100);
		fd_grpDfd.left = new FormAttachment(0, 10);
		fd_grpDfd.right = new FormAttachment(100, -10);
		otherSetGroup.setLayoutData(fd_grpDfd);
		otherSetGroup.setText("参数设置");

		Label needMatchLabel = new Label(otherSetGroup, SWT.NONE);
		needMatchLabel.setText("最大表压偏差：");

		Spinner maxMeterVolOffsetSpinner = new Spinner(otherSetGroup, SWT.BORDER);
		maxMeterVolOffsetSpinner.setDigits(1);
		maxMeterVolOffsetSpinner.setMaximum(1000000);
		maxMeterVolOffsetSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label validateLabel = new Label(otherSetGroup, SWT.NONE);
		validateLabel.setText("最大ADC偏差：");

		Spinner maxAdcVolOffsetSpinner = new Spinner(otherSetGroup, SWT.BORDER);
		maxAdcVolOffsetSpinner.setDigits(1);
		maxAdcVolOffsetSpinner.setMaximum(1000000);
		maxAdcVolOffsetSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label needCalculateLabel = new Label(otherSetGroup, SWT.NONE);
		needCalculateLabel.setText("最小计量电流：");

		Spinner minCalculateCurrSpinner = new Spinner(otherSetGroup, SWT.BORDER);
		minCalculateCurrSpinner.setDigits(2);
		minCalculateCurrSpinner.setMaximum(1000000);
		minCalculateCurrSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label adcCountLabel = new Label(otherSetGroup, SWT.NONE);
		adcCountLabel.setText("最大计量电流：");

		Spinner maxCalculateCurrSpinner = new Spinner(otherSetGroup, SWT.BORDER);
		maxCalculateCurrSpinner.setDigits(2);
		maxCalculateCurrSpinner.setMaximum(1000000);
		maxCalculateCurrSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label maxSigmaLabel = new Label(otherSetGroup, SWT.NONE);
		maxSigmaLabel.setText("最小计量电压：");

		Spinner minCalculateVolSpinner = new Spinner(otherSetGroup, SWT.BORDER);
		minCalculateVolSpinner.setDigits(1);
		minCalculateVolSpinner.setMaximum(1000000);
		minCalculateVolSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label trailCountLabel = new Label(otherSetGroup, SWT.NONE);
		trailCountLabel.setText("最大计量电压：");

		Spinner maxCalculateVolSpinner = new Spinner(otherSetGroup, SWT.BORDER);
		maxCalculateVolSpinner.setDigits(1);
		maxCalculateVolSpinner.setMaximum(1000000);
		maxCalculateVolSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button saveButton = new Button(shell, SWT.NONE);
		saveButton.setLayoutData(new FormData());
		FormData fd_sendButton = new FormData();
		fd_sendButton.top = new FormAttachment(100, -60);
		fd_sendButton.bottom = new FormAttachment(100, -30);
		fd_sendButton.left = new FormAttachment(50, -50);
		fd_sendButton.right = new FormAttachment(50, 50);
		saveButton.setLayoutData(fd_sendButton);
		saveButton.setText("保存");

		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WorkBench.calculatePlanData.setMaxMeterOffset((double) maxMeterVolOffsetSpinner.getSelection() / 10);
				WorkBench.calculatePlanData.setMaxAdcOffset((double) maxAdcVolOffsetSpinner.getSelection() / 10);
				WorkBench.calculatePlanData
						.setMinCalculateCurrent((double) minCalculateCurrSpinner.getSelection() / 100);
				WorkBench.calculatePlanData
						.setMaxCalculateCurrent((double) maxCalculateCurrSpinner.getSelection() / 100);
				WorkBench.calculatePlanData.setMinCalculateVoltage((double) minCalculateVolSpinner.getSelection() / 10);
				WorkBench.calculatePlanData.setMaxCalculateVoltage((double) maxCalculateVolSpinner.getSelection() / 10);
				shell.dispose();
			}
		});

		// 初始化上位机数据到界面中
		maxMeterVolOffsetSpinner.setSelection((int) (WorkBench.calculatePlanData.getMaxMeterOffset() * 10));
		maxAdcVolOffsetSpinner.setSelection((int) WorkBench.calculatePlanData.getMaxAdcOffset() * 10);
		minCalculateCurrSpinner.setSelection((int) (WorkBench.calculatePlanData.getMinCalculateCurrent() * 100));
		maxCalculateCurrSpinner.setSelection((int) (WorkBench.calculatePlanData.getMaxCalculateCurrent() * 100));
		minCalculateVolSpinner.setSelection((int) WorkBench.calculatePlanData.getMinCalculateVoltage() * 10);
		maxCalculateVolSpinner.setSelection((int) WorkBench.calculatePlanData.getMaxCalculateVoltage() * 10);
	}
}
