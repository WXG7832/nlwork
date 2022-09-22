package com.nlteck.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.CalData;
import com.nlteck.model.TestData;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculateDotData;
import com.nltecklib.protocol.li.PCWorkform.RequestCalculateData;
import com.nltecklib.utils.CvsUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;

/**
 * 数据管理
 * 
 * @author caichao_tang
 *
 */
public class DataHandleDialog extends Dialog {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_LONG = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss:SSS");
	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public DataHandleDialog(Shell parent, int style) {
		super(parent, style);
		setText("数据管理");
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
		shell.setSize(300, 350);
		shell.setText(getText());
		UIUtil.setShellAlignCenter(shell);
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.marginHeight = 10;
		gl_shell.marginWidth = 10;
		shell.setLayout(gl_shell);

		Group composite = new Group(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setText("数据库数据");
		composite.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("测试名称：");

		Combo testNameCombo = new Combo(composite, SWT.READ_ONLY);
		testNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		testNameCombo.setItems(getTestsFromDB());
		testNameCombo.select(1);

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("数据类型：");

		Combo testDataTypeCombo = new Combo(composite, SWT.READ_ONLY);
		testDataTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, true));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		Button delButton = new Button(composite_1, SWT.NONE);
		delButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		delButton.setText("删除数据");

		Button exportButton = new Button(composite_1, SWT.NONE);
		exportButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		exportButton.setText("导出数据");

		Group coreGroup = new Group(shell, SWT.NONE);
		coreGroup.setLayout(new GridLayout(2, false));
		coreGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		coreGroup.setText("主控数据");

		Label lblNewLabel_12 = new Label(coreGroup, SWT.NONE);
		lblNewLabel_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_12.setText("选择设备：");

		Combo deviceCombo = new Combo(coreGroup, SWT.READ_ONLY);
		deviceCombo.setItems(getDevices());
		deviceCombo.select(0);
		deviceCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_2 = new Label(coreGroup, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("数据类型：");

		Combo coreDataTypeCombo = new Combo(coreGroup, SWT.READ_ONLY);
		coreDataTypeCombo.setItems("通道数据");
		coreDataTypeCombo.select(0);
		coreDataTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_3 = new Label(coreGroup, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("分区编号：");

		Spinner spinner = new Spinner(coreGroup, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button queryAndExportButton = new Button(coreGroup, SWT.NONE);
		queryAndExportButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		queryAndExportButton.setText("导出");

		exportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}

		});

		delButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});

		queryAndExportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
	}

	private String[] getTestsFromDB() {
		
		return null;
	}

	private String[] getDevices() {
		String[] strings = new String[WorkBench.calBoxList.size()];
		for (int i = 0; i < WorkBench.calBoxList.size(); i++) {
			strings[i] = WorkBench.calBoxList.get(i).getName();
		}
		return strings;
	}

	private void exportDataToCVS(String filePath, List<CalData> data) throws IOException {
		CvsUtil cvsUtil = new CvsUtil(filePath);
		cvsUtil.setHeader(new String[] { "逻辑板号", "驱动板号", "通道号", "通道状态", "当前模式", "极性", "精度等级", "步次", "总步次", "当前点",
				"adc值", "万用表值", "耗时（秒）", "数据时间" });
		for (CalData calData : data) {
			List<String> rowStrings = new ArrayList<>();
			rowStrings.add(calData.getLogicIndex() + "");
			rowStrings.add(calData.getDriverIndex() + "");
			rowStrings.add(calData.getChannelIndex() + "");
			rowStrings.add(calData.getState());
			rowStrings.add(calData.getMode());
			rowStrings.add(calData.getPole());
			rowStrings.add(calData.getLevel() + "");
			rowStrings.add(calData.getStep() + "");
			rowStrings.add(calData.getTotalStep() + "");
			rowStrings.add(calData.getCalDot() + "");
			rowStrings.add(calData.getAdc() + "");
			rowStrings.add(calData.getMeter() + "");
			rowStrings.add(calData.getElapseSeconds() + "");
			rowStrings.add(SIMPLE_DATE_FORMAT_LONG.format(calData.getDate()));
			// 输出一行记录
			cvsUtil.writeRecord(rowStrings.toArray());
		}
		cvsUtil.flushCheckHead();
	}

	private void exportUnitDataToCVS(String filePath, List<CalculateDotData> data) throws IOException {
		CvsUtil cvsUtil = new CvsUtil(filePath);
		cvsUtil.setHeader(new String[] { "分区通道号", "校准状态", "校准值", "adc值", "万用表值", "耗时(秒)", "日期" });
		for (CalculateDotData calData : data) {
			List<String> rowStrings = new ArrayList<>();
			rowStrings.add(calData.chnIndexInLogic + "");
			rowStrings.add(calData.state.getDescribe());
			rowStrings.add(calData.value + "");
			rowStrings.add(calData.adc + "");
			rowStrings.add(calData.meter + "");
			rowStrings.add(calData.seconds + "");
			rowStrings.add(SIMPLE_DATE_FORMAT_LONG.format(calData.date));
			// 输出一行记录
			cvsUtil.writeRecord(rowStrings.toArray());
		}
		cvsUtil.flushCheckHead();
	}
}
