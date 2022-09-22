package com.nlteck.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.TestLog;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.PCWorkform.RangeCurrentPrecisionData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.RangeCurrentPrecision;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class PrecisonConfigDialog extends Dialog {
	protected Object result;
	protected Shell shell;
	private Composite contentComposite;
	private CalBox calBox;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public PrecisonConfigDialog(Shell parent, int style) {
		super(parent, style);
		setText("设备精度档位");
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
		shell = new Shell(getParent(), SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setSize(757, 420);
		UIUtil.setShellAlignCenter(shell);
		shell.setText(getText());
		shell.setLayout(new FormLayout());

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FormLayout());
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.bottom = new FormAttachment(100, -45);
		composite.setLayoutData(fd_composite);

		ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 0);
		fd_scrolledComposite.bottom = new FormAttachment(100, 0);
		fd_scrolledComposite.left = new FormAttachment(0, 0);
		fd_scrolledComposite.right = new FormAttachment(100, 0);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		contentComposite = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_contentComposite = new GridLayout(1, false);
		gl_contentComposite.marginWidth = 0;
		gl_contentComposite.marginHeight = 0;
		contentComposite.setLayout(gl_contentComposite);

		Composite titleComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout gl_titleComposite = new GridLayout(6, true);
		gl_titleComposite.verticalSpacing = 0;
		gl_titleComposite.marginWidth = 0;
		gl_titleComposite.marginHeight = 0;
		gl_titleComposite.horizontalSpacing = 0;
		titleComposite.setLayout(gl_titleComposite);
		GridData gd_titleComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_titleComposite.heightHint = 18;
		titleComposite.setLayoutData(gd_titleComposite);

		Label lblNewLabel = new Label(titleComposite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		GridData gd_lblNewLabel = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblNewLabel.widthHint = 86;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("精度档位");

		Label lblNewLabel_1 = new Label(titleComposite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.CENTER);
		GridData gd_lblNewLabel_1 = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblNewLabel_1.widthHint = 86;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel_1.setText("电流精度(mA)");

		Label lblNewLabel_2 = new Label(titleComposite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.CENTER);
		GridData gd_lblNewLabel_2 = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblNewLabel_2.widthHint = 86;
		lblNewLabel_2.setLayoutData(gd_lblNewLabel_2);
		lblNewLabel_2.setText("电流下限(mA)");

		Label lblNewLabel_3 = new Label(titleComposite, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.CENTER);
		lblNewLabel_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_3.setText("电流上限(mA)");

		Label lblNewLabel_4 = new Label(titleComposite, SWT.NONE);
		lblNewLabel_4.setAlignment(SWT.CENTER);
		lblNewLabel_4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_4.setText("最大ADC偏差(mA)");

		Label lblNewLabel_5 = new Label(titleComposite, SWT.NONE);
		lblNewLabel_5.setAlignment(SWT.CENTER);
		lblNewLabel_5.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_5.setText("最大表值偏差(mA)");

		createItemComposite(contentComposite);

		scrolledComposite.setContent(contentComposite);
		scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button configButton = new Button(shell, SWT.NONE);
		FormData fd_configButton = new FormData();
		fd_configButton.bottom = new FormAttachment(100, -10);
		fd_configButton.left = new FormAttachment(0, 60);
		fd_configButton.right = new FormAttachment(0, 160);
		configButton.setLayoutData(fd_configButton);
		configButton.setText("配置");

		Button queryButton = new Button(shell, SWT.NONE);
		FormData fd_queryButton = new FormData();
		fd_queryButton.bottom = new FormAttachment(100, -10);
		fd_queryButton.left = new FormAttachment(100, -160);
		fd_queryButton.right = new FormAttachment(100, -60);
		queryButton.setLayoutData(fd_queryButton);
		queryButton.setText("查询");

		configButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// save();
				// StringBuffer info = new StringBuffer();
				// if (WorkBench.configCommand(calBox, WorkBench.rangeCurrentPrecisionData,
				// 2000, info)) {
				// MessageDialog.openInformation(shell, "操作成功", "下发精度档位设置成功！");
				// if (WorkBench.currenTestData != null)
				// WorkBench.getDatabaseManager().getTestLogDao().insert(new
				// TestLog(WorkBench.currenTestData.getId(), "INFO", "下发进度档位配置成功", new Date()));
				// shell.dispose();
				// } else {
				// MessageDialog.openError(shell, "操作失败", "下发精度档位设置失败！\n" + info.toString());
				// if (WorkBench.currenTestData != null)
				// WorkBench.getDatabaseManager().getTestLogDao().insert(new
				// TestLog(WorkBench.currenTestData.getId(), "ERROR", "下发进度档位配置失败", new
				// Date()));
				// }
			}
		});

		queryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				StringBuffer info = new StringBuffer();
//				RangeCurrentPrecisionData receiveData = (RangeCurrentPrecisionData) WorkBench.queryCommand(calBox,
//						new RangeCurrentPrecisionData(), 2000, info);
//				if (receiveData == null) {
//					MessageDialog.openError(shell, "操作失败", "查询精度档位设置失败！\n" + info.toString());
//					return;
//				} else {
//					WorkBench.rangeCurrentPrecisionData = receiveData;
//				}
//				show();
			}
		});

		show();
		if (contentComposite.getChildren().length == 1) {
			createItemComposite(contentComposite);
		}
	}

	/**
	 * 创建 item Composite
	 * 
	 * @param contentComposite
	 */
	private void createItemComposite(Composite contentComposite) {
		Composite itemComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout gl_itemComposite = new GridLayout(6, true);
		gl_itemComposite.verticalSpacing = 0;
		gl_itemComposite.marginWidth = 2;
		gl_itemComposite.horizontalSpacing = 2;
		gl_itemComposite.marginHeight = 0;
		itemComposite.setLayout(gl_itemComposite);
		GridData gd_itemComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_itemComposite.heightHint = 24;
		itemComposite.setLayoutData(gd_itemComposite);

		Spinner spinner = new Spinner(itemComposite, SWT.BORDER);
		spinner.setToolTipText("右击添加/删除");
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Spinner spinner_1 = new Spinner(itemComposite, SWT.BORDER);
		spinner_1.setDigits(2);
		spinner_1.setMaximum(1000000);
		spinner_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Spinner spinner_2 = new Spinner(itemComposite, SWT.BORDER);
		spinner_2.setDigits(2);
		spinner_2.setMaximum(1000000);
		spinner_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Spinner spinner_3 = new Spinner(itemComposite, SWT.BORDER);
		spinner_3.setDigits(2);
		spinner_3.setMaximum(1000000);
		spinner_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Spinner spinner_4 = new Spinner(itemComposite, SWT.BORDER);
		spinner_4.setDigits(2);
		spinner_4.setMaximum(1000000);
		spinner_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Spinner spinner_5 = new Spinner(itemComposite, SWT.BORDER);
		spinner_5.setDigits(2);
		spinner_5.setMaximum(1000000);
		spinner_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Menu menu = new Menu(spinner);
		spinner.setMenu(menu);

		MenuItem addMenuItem = new MenuItem(menu, SWT.NONE);
		addMenuItem.setText("下方添加");

		MenuItem delMenuItem = new MenuItem(menu, SWT.NONE);
		delMenuItem.setText("删除本条");

		addMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				insertCalibrateModeComposite(contentComposite, spinner);
			}
		});

		delMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				itemComposite.dispose();
				contentComposite.layout();
				((ScrolledComposite) contentComposite.getParent())
						.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

	}

	/**
	 * 往下插入一个item
	 * 
	 * @param contentComposite
	 * @param control
	 */
	private void insertCalibrateModeComposite(Composite contentComposite, Control control) {
		int index = 0;
		Control[] insertComposites = contentComposite.getChildren();
		for (int i = 0; i < insertComposites.length; i++) {
			if (insertComposites[i] == control.getParent()) {
				index = i;
			}
		}
		Composite catchComposite = new Composite(contentComposite.getShell(), SWT.NONE);
		catchComposite.setVisible(false);
		// 从第index+1开始的insertComposite全部转移到临时的catchComposite
		for (int i = index + 1; i < insertComposites.length; i++) {
			insertComposites[i].setParent(catchComposite);
		}
		// 插入一个新的insertComposite
		createItemComposite(contentComposite);
		// 还原insertComposite到contentComposite
		for (Control insertControl : catchComposite.getChildren()) {
			insertControl.setParent(contentComposite);
		}
		// 销毁catchComposite
		catchComposite.dispose();
		// 刷新布局
		contentComposite.layout();
		((ScrolledComposite) contentComposite.getParent())
				.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * 当前界面数据保存到上位机
	 */
	private void save() {
		Control[] itemControls = contentComposite.getChildren();
		List<RangeCurrentPrecision> rangeList = new ArrayList<>();
		for (int i = 1; i < itemControls.length; i++) {
			RangeCurrentPrecision rangeData = new RangeCurrentPrecision();
			rangeData.level = ((Spinner) ((Composite) itemControls[i]).getChildren()[0]).getSelection();
			rangeData.precide = (double) ((Spinner) ((Composite) itemControls[i]).getChildren()[1]).getSelection()
					/ 100;
			rangeData.min = (double) ((Spinner) ((Composite) itemControls[i]).getChildren()[2]).getSelection() / 100;
			rangeData.max = (double) ((Spinner) ((Composite) itemControls[i]).getChildren()[3]).getSelection() / 100;
			rangeData.maxAdcOffset = (double) ((Spinner) ((Composite) itemControls[i]).getChildren()[4]).getSelection()
					/ 100;
			rangeData.maxMeterOffset = (double) ((Spinner) ((Composite) itemControls[i]).getChildren()[5])
					.getSelection() / 100;
			rangeList.add(rangeData);
		}
		WorkBench.rangeCurrentPrecisionData.setRanges(rangeList);
	}

	/**
	 * 上位机数据显示到当前界面中
	 */
	private void show() {
		List<RangeCurrentPrecision> rangeList = WorkBench.rangeCurrentPrecisionData.getRanges();
		// 清空界面
		Control[] itemControls = contentComposite.getChildren();
		for (int i = 1; i < itemControls.length; i++)
			itemControls[i].dispose();
		// 创建界面
		for (int i = 0; i < rangeList.size(); i++)
			createItemComposite(contentComposite);
		itemControls = contentComposite.getChildren();
		for (int i = 1; i < itemControls.length; i++) {
			RangeCurrentPrecision rangeData = rangeList.get(i - 1);
			((Spinner) ((Composite) itemControls[i]).getChildren()[0]).setSelection(rangeData.level);
			((Spinner) ((Composite) itemControls[i]).getChildren()[1]).setSelection((int) (rangeData.precide * 100));
			((Spinner) ((Composite) itemControls[i]).getChildren()[2]).setSelection((int) (rangeData.min * 100));
			((Spinner) ((Composite) itemControls[i]).getChildren()[3]).setSelection((int) (rangeData.max * 100));
			((Spinner) ((Composite) itemControls[i]).getChildren()[4])
					.setSelection((int) (rangeData.maxAdcOffset * 100));
			((Spinner) ((Composite) itemControls[i]).getChildren()[5])
					.setSelection((int) (rangeData.maxMeterOffset * 100));
		}
		// 刷新布局
		contentComposite.layout();
		((ScrolledComposite) contentComposite.getParent())
				.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

}
