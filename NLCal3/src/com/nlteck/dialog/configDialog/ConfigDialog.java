package com.nlteck.dialog.configDialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import com.nlteck.calModel.base.I18N;
import com.nlteck.calSoftConfig.view.MenuToolItem;
import com.nlteck.calSoftConfig.view.composite.AvgAdcSettingComposite;
import com.nlteck.calSoftConfig.view.composite.BaseConfigComposite;
import com.nlteck.calSoftConfig.view.composite.CalPlanCompositeTableViewerVer;
import com.nlteck.calSoftConfig.view.composite.CalibrationPlanComposite;
import com.nlteck.calSoftConfig.view.composite.DelayConfigComposite;
import com.nlteck.calSoftConfig.viewModel.ConfigDialogViewModel;

public class ConfigDialog extends Dialog {

    protected ConfigDialogViewModel viewModel;
    private String calibrationDeviceIP;
    private boolean useCalPlanTableViewer = true;

    /**
     * @wbp.parser.constructor
     */
    public ConfigDialog(Shell parentShell, String calibrationDeviceIP) {
	super(parentShell);
	init(calibrationDeviceIP, "zh-CN", "zh-CN", useCalPlanTableViewer);
    }

    public ConfigDialog(Shell parentShell, String calibrationDeviceIP, String language, String langFileEncoding) {
	super(parentShell);
	init(calibrationDeviceIP, language, langFileEncoding, useCalPlanTableViewer);
    }

    public ConfigDialog(Shell parentShell, String calibrationDeviceIP, boolean useCalPlanTableViewer) {
	super(parentShell);
	init(calibrationDeviceIP, "zh-CN", "zh-CN", useCalPlanTableViewer);
    }

    protected void init(String calibrationDeviceIP, String language, String langFileEncoding,
	    boolean useCalPlanTableViewer) {
	this.calibrationDeviceIP = calibrationDeviceIP;
	this.useCalPlanTableViewer = useCalPlanTableViewer;
	String langFilePath = String.format("language/%s.xml", language);
	try {
	    I18N.init(langFilePath, langFileEncoding);
	} catch (Exception e) {
		e.printStackTrace();
	    viewModel.exceptionReceiver("载入语言文件发生异常: " + langFilePath, e);
	}
    }

    @Override
    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("校准工装参数");
    }

    @Override
    protected Point getInitialSize() {

	return new Point(1580, 1000);
    }

    @Override
    protected boolean isResizable() {
	return true;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	Composite container = (Composite) super.createDialogArea(parent);
	container.setLayout(new FillLayout());

	CTabFolder tabFolder = new CTabFolder(container, SWT.TOP | SWT.BORDER);
	tabFolder.setTabHeight(25);
	tabFolder.marginHeight = 5;
	tabFolder.marginWidth = 5;
	tabFolder.setLayout(new FillLayout());
	tabFolder.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		CTabItem tabItem = (CTabItem) e.item;
		viewModel = (ConfigDialogViewModel) tabItem.getData();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	// 传递给视图模型的参数
	Object[] args = new Object[] { calibrationDeviceIP, tabFolder };
	// 基础参数
	CTabItem tabItem = newTabItem(tabFolder, "基础参数");
	Composite tabItemContentComp = getTabItemContentComp(tabItem);
	BaseConfigComposite baseConfigComposite = new BaseConfigComposite(tabItemContentComp, SWT.NONE, args);
	tabItem.setData(baseConfigComposite.viewModel);
	// 校准方案
	if (!useCalPlanTableViewer) {
	    tabItem = new CTabItem(tabFolder, SWT.NONE | SWT.MULTI | SWT.V_SCROLL);
	    tabItem.setText("校准方案");
	    CalibrationPlanComposite calibrationPlanComposite = new CalibrationPlanComposite(tabFolder, SWT.NONE, args);
	    tabItem.setControl(calibrationPlanComposite.containerSashForm);
	    tabItem.setData(calibrationPlanComposite.viewModel);
	} else {
	    tabItem = newTabItem(tabFolder, "校准方案");
	    tabItemContentComp = getTabItemContentComp(tabItem);
	    CalPlanCompositeTableViewerVer calPlanCompositeTableViewerVer = new CalPlanCompositeTableViewerVer(
		    tabItemContentComp, SWT.NONE, args);
	    tabItem.setData(calPlanCompositeTableViewerVer.viewModel);
	}
	// 延时参数
	tabItem = newTabItem(tabFolder, "延时参数");
	tabItemContentComp = getTabItemContentComp(tabItem);
	DelayConfigComposite delayConfigComposite = new DelayConfigComposite(tabItemContentComp, SWT.NULL, args);
	tabItem.setData(delayConfigComposite.viewModel);
	// 平均ADC参数
	tabItem = newTabItem(tabFolder, "平均ADC参数");
	tabItemContentComp = getTabItemContentComp(tabItem);
	AvgAdcSettingComposite avgAdcSettingComposite = new AvgAdcSettingComposite(tabItemContentComp, SWT.NULL, args);
	tabItem.setData(avgAdcSettingComposite.viewModel);
	// 选中首页
	Event event = new Event();
	event.item = tabFolder.getItem(0);
	tabFolder.notifyListeners(SWT.Selection, event);
	getShell().addDisposeListener(viewModel.getDisposeListener());
	return container;
    }

    /**
     * @description 构建tabItem控件
     * @author zemin_zhu
     * @dateTime Jun 30, 2022 10:34:40 AM
     */
    protected CTabItem newTabItem(CTabFolder tabFolder, String tabTitle) {
	ScrolledComposite scrolledComposite = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
	Composite composite = new Composite(scrolledComposite, SWT.NONE);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(2, 2).applyTo(composite);
	composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
	composite.setSize(1580, 2000);
	scrolledComposite.setContent(composite);
	scrolledComposite.setExpandHorizontal(true);
	CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE | SWT.MULTI | SWT.V_SCROLL);
	tabItem.setText(tabTitle);
	tabItem.setControl(scrolledComposite);
	return tabItem;
    }

    /**
     * @description 获得tabItem控件的子控件
     * @author zemin_zhu
     * @dateTime Jun 30, 2022 10:34:57 AM
     */
    protected Composite getTabItemContentComp(CTabItem tabItem) {
	ScrolledComposite scrolledComposite = (ScrolledComposite) tabItem.getControl();
	Composite composite = (Composite) scrolledComposite.getChildren()[0];
	return composite;
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
	return null;
    }

    @Override
    protected void initializeBounds() {

	Composite composite = (Composite) getButtonBar();

	ToolBar bar = new ToolBar(composite, SWT.HORIZONTAL);
	MenuToolItem menuToolItem = new MenuToolItem(bar);
	menuToolItem.setText("校准方案");
	menuToolItem.addMenuItem("导入", new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		viewModel.calPlanMenuViewModel.importBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	menuToolItem.addMenuItem("导出", new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		viewModel.calPlanMenuViewModel.exportBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	menuToolItem.addMenuItem("查询", new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		viewModel.calPlanMenuViewModel.queryBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	menuToolItem.addMenuItem("下发", new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		viewModel.calPlanMenuViewModel.sendBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	menuToolItem.addMenuItem("内置方案", new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		viewModel.calPlanMenuViewModel.innerCalPlanBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	((GridLayout) composite.getLayout()).numColumns++;

	super.createButton(composite, IDialogConstants.OK_ID, "下发", true);
	super.createButton(composite, IDialogConstants.CANCEL_ID, "读取", false);
	super.createButton(composite, IDialogConstants.BACK_ID, "重启", false);
	super.createButton(composite, IDialogConstants.ABORT_ID, "导入", false);
	super.createButton(composite, IDialogConstants.CLOSE_ID, "导出", false);

	super.initializeBounds();
    }

    @Override
    protected void okPressed() {
	viewModel.sendBtnClickEvent();
    }

    @Override
    protected void cancelPressed() {
	viewModel.queryBtnClickEvent();
    }

    @Override
    protected void buttonPressed(int buttonId) {
	super.buttonPressed(buttonId);
	switch (buttonId) {
	case IDialogConstants.BACK_ID:
	    viewModel.rebootBtnClickEvent();
	    break;
	case IDialogConstants.ABORT_ID:
	    viewModel.importBtnClickEvent();
	    break;
	case IDialogConstants.CLOSE_ID:
	    viewModel.exportBtnClickEvent();
	    break;
	default:
	    break;
	}
    }
}
