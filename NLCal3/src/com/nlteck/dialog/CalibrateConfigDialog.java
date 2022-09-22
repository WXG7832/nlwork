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

public class CalibrateConfigDialog extends Dialog {

    protected Object result;
    protected Shell shell;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public CalibrateConfigDialog(Shell parent, int style) {
	super(parent, style);
	setText("校准方案设置");
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
	shell.setSize(586, 476);
	UIUtil.setShellAlignCenter(shell);
	shell.setText(getText());
	shell.setLayout(new FormLayout());

	Group grpDealy = new Group(shell, SWT.NONE);
	grpDealy.setLayout(new GridLayout(4, true));
	FormData fd_grpDealy = new FormData();
	fd_grpDealy.bottom = new FormAttachment(0, 165);
	fd_grpDealy.right = new FormAttachment(100, -10);
	fd_grpDealy.top = new FormAttachment(0, 10);
	fd_grpDealy.left = new FormAttachment(0, 10);
	grpDealy.setLayoutData(fd_grpDealy);
	grpDealy.setText("延时设置(ms)");

	Label diapOnDelayLabel = new Label(grpDealy, SWT.NONE);
	diapOnDelayLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	diapOnDelayLabel.setText("模片打开延时：");

	Spinner diapOnDelaySpinner = new Spinner(grpDealy, SWT.BORDER);
	diapOnDelaySpinner.setMaximum(65535);
	diapOnDelaySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label diapOffDelayLabel = new Label(grpDealy, SWT.NONE);
	diapOffDelayLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	diapOffDelayLabel.setText("模片关闭延时");

	Spinner diapOffDelaySpinner = new Spinner(grpDealy, SWT.BORDER);
	diapOffDelaySpinner.setMaximum(65535);
	diapOffDelaySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label modeChangeLabel = new Label(grpDealy, SWT.NONE);
	modeChangeLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	modeChangeLabel.setText("模式切换延时：");

	Spinner modeChangeSpinner = new Spinner(grpDealy, SWT.BORDER);
	modeChangeSpinner.setMaximum(65535);
	modeChangeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label logicProgramLabel = new Label(grpDealy, SWT.NONE);
	logicProgramLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	logicProgramLabel.setText("程控配置延时：");

	Spinner logicProgramSpinner = new Spinner(grpDealy, SWT.BORDER);
	logicProgramSpinner.setMaximum(65535);
	logicProgramSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lowToHighLabel = new Label(grpDealy, SWT.NONE);
	lowToHighLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	lowToHighLabel.setText("低精转换高精：");

	Spinner lowToHighSpinner = new Spinner(grpDealy, SWT.BORDER);
	lowToHighSpinner.setMaximum(65535);
	lowToHighSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label highToLowLabel = new Label(grpDealy, SWT.NONE);
	highToLowLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	highToLowLabel.setText("高精转换低精：");

	Spinner highToLowSpinner = new Spinner(grpDealy, SWT.BORDER);
	highToLowSpinner.setMaximum(65535);
	highToLowSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label closeMeterLabel = new Label(grpDealy, SWT.NONE);
	closeMeterLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	closeMeterLabel.setText("读表延时：");

	Spinner readMeterSpinner = new Spinner(grpDealy, SWT.BORDER);
	readMeterSpinner.setMaximum(65535);
	readMeterSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label openMeterLabel = new Label(grpDealy, SWT.NONE);
	openMeterLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	openMeterLabel.setText("切表延时");

	Spinner switchMeterSpinner = new Spinner(grpDealy, SWT.BORDER);
	switchMeterSpinner.setMaximum(65535);
	switchMeterSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Group otherSetGroup = new Group(shell, SWT.NONE);
	otherSetGroup.setLayout(new GridLayout(4, true));
	FormData fd_grpDfd = new FormData();
	fd_grpDfd.top = new FormAttachment(0, 181);
	fd_grpDfd.bottom = new FormAttachment(0, 366);
	fd_grpDfd.left = new FormAttachment(0, 10);
	fd_grpDfd.right = new FormAttachment(100, -10);
	otherSetGroup.setLayoutData(fd_grpDfd);
	otherSetGroup.setText("其它设置");

	Label validateLabel = new Label(otherSetGroup, SWT.NONE);
	validateLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	validateLabel.setText("校准是否验证：");

	Combo validateCombo = new Combo(otherSetGroup, SWT.READ_ONLY);
	validateCombo.setItems(new String[] { "无需验证", "验证系数" });

	validateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

	Label needCalculateLabel = new Label(otherSetGroup, SWT.NONE);
	needCalculateLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	needCalculateLabel.setText("是否自动计量：");

	Combo needCalculateCombo = new Combo(otherSetGroup, SWT.READ_ONLY);
	needCalculateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	needCalculateCombo.setItems(new String[] { "无需计量", "自动计量" });

	Label adcCountLabel = new Label(otherSetGroup, SWT.NONE);
	adcCountLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	adcCountLabel.setText("采集ADC个数：");

	Spinner adcCountSpinner = new Spinner(otherSetGroup, SWT.BORDER);
	adcCountSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label maxSigmaLabel = new Label(otherSetGroup, SWT.NONE);
	maxSigmaLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	maxSigmaLabel.setText("最大标准差值：");

	Spinner maxSigmaSpinner = new Spinner(otherSetGroup, SWT.BORDER);
	maxSigmaSpinner.setMaximum(1000000);
	maxSigmaSpinner.setDigits(2);
	maxSigmaSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label trailCountLabel = new Label(otherSetGroup, SWT.NONE);
	trailCountLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	trailCountLabel.setText("头尾去除数量：");

	Spinner trailCountSpinner = new Spinner(otherSetGroup, SWT.BORDER);
	trailCountSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label maxProgramVoLabel = new Label(otherSetGroup, SWT.NONE);
	maxProgramVoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	maxProgramVoLabel.setText("最大程控电压：");

	Spinner maxProgramVoLSpinner = new Spinner(otherSetGroup, SWT.BORDER);
	maxProgramVoLSpinner.setMaximum(1000000);
	maxProgramVoLSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label maxProgramCurLabel = new Label(otherSetGroup, SWT.NONE);
	maxProgramCurLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	maxProgramCurLabel.setText("最大程控电流：");

	Spinner maxProgramCurSpinner = new Spinner(otherSetGroup, SWT.BORDER);
	maxProgramCurSpinner.setMaximum(1000000);
	maxProgramCurSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label adcRetryTimeLabel = new Label(otherSetGroup, SWT.NONE);
	adcRetryTimeLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	adcRetryTimeLabel.setText("ADC重查次数：");

	Spinner adcRetryTimeSpinner = new Spinner(otherSetGroup, SWT.BORDER);
	adcRetryTimeSpinner.setMaximum(1000000);
	adcRetryTimeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label adcRetryDelayLabel = new Label(otherSetGroup, SWT.NONE);
	adcRetryDelayLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	adcRetryDelayLabel.setText("ADC不稳定查询延迟：");

	Spinner adcRetryDelaySpinner = new Spinner(otherSetGroup, SWT.BORDER);
	adcRetryDelaySpinner.setMaximum(1000000);
	adcRetryDelaySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Button saveButton = new Button(shell, SWT.NONE);
	saveButton.setLayoutData(new FormData());
	FormData fd_saveButton = new FormData();
	fd_saveButton.top = new FormAttachment(100, -60);
	fd_saveButton.bottom = new FormAttachment(100, -30);
	fd_saveButton.left = new FormAttachment(50, -50);
	fd_saveButton.right = new FormAttachment(50, 50);
	saveButton.setLayoutData(fd_saveButton);
	saveButton.setText("保存");

	saveButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		WorkBench.calibratePlanData.setNeedValidate(validateCombo.getSelectionIndex() == 1);
		WorkBench.calibratePlanData.setNeedCalculateAfterCalibrate(needCalculateCombo.getSelectionIndex() == 1);
		WorkBench.delayData.setModuleOpenDelay(diapOnDelaySpinner.getSelection());
		WorkBench.delayData.setModuleCloseDelay(diapOffDelaySpinner.getSelection());
		WorkBench.delayData.setModeSwitchDelay(modeChangeSpinner.getSelection());
		WorkBench.delayData.setProgramSetDelay(logicProgramSpinner.getSelection());
		WorkBench.delayData.setHigh2lowDelay(highToLowSpinner.getSelection());
		WorkBench.delayData.setLow2hightDelay(lowToHighSpinner.getSelection());
		WorkBench.delayData.setReadMeterDelay(readMeterSpinner.getSelection());
		WorkBench.delayData.setSwitchMeterDelay(switchMeterSpinner.getSelection());
		WorkBench.steadyCfgData.setSampleCount(adcCountSpinner.getSelection());
		WorkBench.steadyCfgData.setMaxSigma((double) maxSigmaSpinner.getSelection() / 100);
		WorkBench.steadyCfgData.setTrailCount(trailCountSpinner.getSelection());
		WorkBench.calibratePlanData.setMaxProgramV(maxProgramVoLSpinner.getSelection());
		WorkBench.calibratePlanData.setMaxProgramV(maxProgramCurSpinner.getSelection());
		WorkBench.steadyCfgData.setAdcReadCount(adcRetryTimeSpinner.getSelection());
		WorkBench.steadyCfgData.setAdcRetryDelay(adcRetryDelaySpinner.getSelection());
		shell.dispose();
	    }
	});

	// 读取上位机保存的配置到界面中
	validateCombo.select(WorkBench.calibratePlanData.isNeedValidate() ? 1 : 0);
	needCalculateCombo.select(WorkBench.calibratePlanData.isNeedCalculateAfterCalibrate() ? 1 : 0);
	diapOnDelaySpinner.setSelection(WorkBench.delayData.getModuleOpenDelay());
	diapOffDelaySpinner.setSelection(WorkBench.delayData.getModuleCloseDelay());
	modeChangeSpinner.setSelection(WorkBench.delayData.getModeSwitchDelay());
	logicProgramSpinner.setSelection(WorkBench.delayData.getProgramSetDelay());
	highToLowSpinner.setSelection(WorkBench.delayData.getHigh2lowDelay());
	lowToHighSpinner.setSelection(WorkBench.delayData.getLow2hightDelay());
	readMeterSpinner.setSelection(WorkBench.delayData.getReadMeterDelay());
	switchMeterSpinner.setSelection(WorkBench.delayData.getSwitchMeterDelay());
	adcCountSpinner.setSelection(WorkBench.steadyCfgData.getSampleCount());
	maxSigmaSpinner.setSelection((int) (WorkBench.steadyCfgData.getMaxSigma() * 100));
	trailCountSpinner.setSelection(WorkBench.steadyCfgData.getTrailCount());
	maxProgramVoLSpinner.setSelection((int) WorkBench.calibratePlanData.getMaxProgramV());
	maxProgramCurSpinner.setSelection((int) WorkBench.calibratePlanData.getMaxProgramI());
	adcRetryTimeSpinner.setSelection(WorkBench.steadyCfgData.getAdcReadCount());
	adcRetryDelaySpinner.setSelection(WorkBench.steadyCfgData.getAdcRetryDelay());
	new Label(otherSetGroup, SWT.NONE);
	new Label(otherSetGroup, SWT.NONE);
    }

}
