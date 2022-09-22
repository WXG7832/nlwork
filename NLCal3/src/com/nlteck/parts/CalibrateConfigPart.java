
package com.nlteck.parts;

import javax.inject.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;

import com.nlteck.dialog.CalibrateConfigDialog;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.resources.Resources;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.DelayData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FillLayout;

/**
 * 校准方案配置面板
 * 
 * @author caichao_tang
 *
 */
public class CalibrateConfigPart {
    public static final String ID = "nlcal.partdescriptor.calibrateConfig";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private Composite contentComposite;
    private CalBox calBox;

    @Inject
    public CalibrateConfigPart() {

    }

    @PostConstruct
    public void postConstruct(Composite parent) {

	Composite composite = new Composite(parent, SWT.NONE);
	composite.setLayout(new FormLayout());

	ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
	FormData fd_toolBar = new FormData();
	fd_toolBar.top = new FormAttachment(0, 0);
	fd_toolBar.bottom = new FormAttachment(0, 36);
	fd_toolBar.left = new FormAttachment(0, 0);
	toolBar.setLayoutData(fd_toolBar);

	ToolItem newToolItem = new ToolItem(toolBar, SWT.NONE);
	newToolItem.setText("新建");
	newToolItem.setImage(Resources.NEW_SCHEMA_IMAGE);

	ToolItem saveToolItem = new ToolItem(toolBar, SWT.NONE);
	saveToolItem.setText("保存");
	saveToolItem.setImage(Resources.SAVE_SCHEMA_IMAGE);

	ToolItem importToolItem = new ToolItem(toolBar, SWT.NONE);
	importToolItem.setText("导入");
	importToolItem.setImage(Resources.IMPORT_SCHEMA_IMAGE);

	ToolItem sendToolItem = new ToolItem(toolBar, SWT.NONE);
	sendToolItem.setImage(Resources.SEND_SCHEMA_IMAGE);
	sendToolItem.setText("下发");

	ToolItem queryToolItem = new ToolItem(toolBar, SWT.NONE);
	queryToolItem.setImage(Resources.QUERY_SCHEMA_IMAGE);
	queryToolItem.setText("查询");

	ToolItem clearToolItem = new ToolItem(toolBar, SWT.NONE);
	clearToolItem.setImage(Resources.CLEAR_SCHEMA_IMAGE);
	clearToolItem.setText("清空");

	new ToolItem(toolBar, SWT.SEPARATOR);

	ToolItem configToolItem = new ToolItem(toolBar, SWT.NONE);
	configToolItem.setImage(Resources.SCHEMA_CONFIG_IMAGE);
	configToolItem.setText("配置");

	if (contentComposite != null && !contentComposite.isDisposed()) {
	    contentComposite.getParent().dispose();
	}
	createOutContainer(composite);
	createCalibrateModeComposite(contentComposite, 1);
	composite.layout();

	show();

	// ***********************************************************
	//
	// toolBar 监听器开始
	//
	// ***********************************************************
	newToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		for (Control control : contentComposite.getChildren()) {
		    control.dispose();
		}
		createCalibrateModeComposite(contentComposite, 1);
		contentComposite.layout();
	    }
	});

	clearToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		for (Control control : contentComposite.getChildren()) {
		    control.dispose();
		}
	    }
	});

	saveToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// 打开文件保存窗口
		FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*.xml" });
		fileDialog.setFileName("calibratePlan_" + SIMPLE_DATE_FORMAT.format(new Date()));
		String filePath = fileDialog.open();
		if (filePath == null || filePath.isEmpty()) {
		    return;
		}
		// 删除同名文件
		new File(filePath).delete();
		save();
		StringBuffer info = new StringBuffer();
		if (writeClibrateToXML(filePath, info))
		    MessageDialog.openInformation(parent.getShell(), "操作成功", "已保存至文件！");
		else
		    MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
	    }
	});

	importToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*.xml" });
		String filePath = fileDialog.open();
		if (filePath == null) {
		    return;
		}
		StringBuffer info = new StringBuffer();
		if (readXMLToClibrate(filePath, info)) {
		    show();
		    MessageDialog.openInformation(parent.getShell(), "操作成功", "已导入文件！");
		} else
		    MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
	    }
	});

	configToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		new CalibrateConfigDialog(parent.getShell(), SWT.NONE).open();
	    }
	});

	queryToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
				// StringBuffer info = new StringBuffer();
				// CalibratePlanData calibratePlanData = (CalibratePlanData)
				// WorkBench.queryCommand(calBox, WorkBench.calibratePlanData, 5000, info);
				// if (calibratePlanData == null) {
				// MessageDialog.openError(contentComposite.getShell(), "操作失败", "查询校准方案失败！\n" +
				// info.toString());
				// return;
				// }
				// WorkBench.calibratePlanData = calibratePlanData;
				// DelayData delayData = (DelayData) WorkBench.queryCommand(calBox,
				// WorkBench.delayData, 5000, info);
				// if (delayData == null) {
				// MessageDialog.openError(contentComposite.getShell(), "操作失败", "查询延时配置失败！\\n" +
				// info.toString());
				// return;
				// }
				// WorkBench.delayData = delayData;
				// SteadyCfgData steadyCfgData = (SteadyCfgData) WorkBench.queryCommand(calBox,
				// WorkBench.steadyCfgData, 5000, info);
				// if (steadyCfgData == null) {
				// MessageDialog.openError(contentComposite.getShell(), "操作失败", "查询校准方案失败！\n" +
				// info.toString());
				// return;
				// }
				// WorkBench.steadyCfgData = steadyCfgData;
				// show();
				// MessageDialog.openInformation(contentComposite.getShell(), "操作成功",
				// "校准方案查询成功！\n延时设置查询成功！\nADC稳定度设置查询成功！");
	    }
	});

	sendToolItem.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		save();
		StringBuffer info = new StringBuffer();
		if (!WorkBench.configCommand(calBox, WorkBench.calibratePlanData, 5000, info)) {
		    MessageDialog.openError(contentComposite.getShell(), "操作失败", "下发校准方案失败！\n" + info.toString());
		    return;
		}
		if (!WorkBench.configCommand(calBox, WorkBench.delayData, 5000, info)) {
		    MessageDialog.openError(contentComposite.getShell(), "操作失败", "下发延时配置失败！\\n" + info.toString());
		    return;
		}
		if (!WorkBench.configCommand(calBox, WorkBench.steadyCfgData, 5000, info)) {
		    MessageDialog.openError(contentComposite.getShell(), "操作失败", "下发ADC稳定度检测配置失败！\\n" + info.toString());
		    return;
		}
		MessageDialog.openInformation(contentComposite.getShell(), "操作成功", "校准方案已下发！\n延时设置已下发！\nADC稳定度设置已下发！");
	    }
	});

	// ***********************************************************
	//
	// toolBar 监听器结束
	//
	// ***********************************************************

    }

    /**
     * 创建编辑区域的外部容器
     * 
     * @param composite
     */
    private void createOutContainer(Composite composite) {
	ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL);
	scrolledComposite.setExpandHorizontal(true);
	scrolledComposite.setExpandVertical(true);
	FormData fd_scrolledComposite = new FormData();
	fd_scrolledComposite.top = new FormAttachment(0, 32);
	fd_scrolledComposite.bottom = new FormAttachment(100, 0);
	fd_scrolledComposite.left = new FormAttachment(0, 0);
	fd_scrolledComposite.right = new FormAttachment(100, 0);
	scrolledComposite.setLayoutData(fd_scrolledComposite);

	contentComposite = new Composite(scrolledComposite, SWT.NONE);
	GridLayout gl_contentComposite = new GridLayout(1, false);
	gl_contentComposite.verticalSpacing = 10;
	gl_contentComposite.marginHeight = 10;
	gl_contentComposite.marginWidth = 10;
	contentComposite.setLayout(gl_contentComposite);
	scrolledComposite.setContent(contentComposite);

	scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * 创建一个校准模式区域
     * 
     * @param contentComposite
     */
    private void createCalibrateModeComposite(Composite contentComposite, int dotNum) {
	Composite calibrateModeComposite = new Composite(contentComposite, SWT.NONE);
	calibrateModeComposite.setLayout(new FormLayout());
	GridData gd_calibrateModeComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	gd_calibrateModeComposite.heightHint = 180;
	calibrateModeComposite.setLayoutData(gd_calibrateModeComposite);

	Group grpConfig = new Group(calibrateModeComposite, SWT.NONE);
	grpConfig.setText("配置");
	GridLayout gl_grpConfig = new GridLayout(2, false);
	gl_grpConfig.marginTop = 23;
	grpConfig.setLayout(gl_grpConfig);
	FormData fd_grpConfig = new FormData();
	fd_grpConfig.top = new FormAttachment(0, 10);
	fd_grpConfig.bottom = new FormAttachment(100, -10);
	fd_grpConfig.left = new FormAttachment(0, 10);
	fd_grpConfig.right = new FormAttachment(0, 170);
	grpConfig.setLayoutData(fd_grpConfig);

	Label modeLabel = new Label(grpConfig, SWT.NONE);
	modeLabel.setText("模式：");

	Combo modeCombo = new Combo(grpConfig, SWT.READ_ONLY);
	modeCombo.setItems("SLEEP", "CC", "CV", "DC", "CV2");
	modeCombo.select(0);
	GridData gd_modeCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
	gd_modeCombo.widthHint = 36;
	modeCombo.setLayoutData(gd_modeCombo);

	Label poleLabel = new Label(grpConfig, SWT.NONE);
	poleLabel.setText("极性：");

	Combo poleCombo = new Combo(grpConfig, SWT.READ_ONLY);
	poleCombo.setItems(new String[] { "负极性", "正极性" });
	poleCombo.select(0);
	poleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

	Label precisionLabel = new Label(grpConfig, SWT.NONE);
	precisionLabel.setText("精度：");

	Spinner precisionSpinner = new Spinner(grpConfig, SWT.BORDER);
	precisionSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

	Group grpKb = new Group(calibrateModeComposite, SWT.NONE);
	grpKb.setText("kb值");
	grpKb.setLayout(new FillLayout(SWT.HORIZONTAL));
	FormData fd_grpKb = new FormData();
	fd_grpKb.top = new FormAttachment(0, 10);
	fd_grpKb.bottom = new FormAttachment(100, -10);
	fd_grpKb.left = new FormAttachment(0, 180);
	fd_grpKb.right = new FormAttachment(0, 400);
	grpKb.setLayoutData(fd_grpKb);

	ScrolledComposite kbScrolledComposite = new ScrolledComposite(grpKb, SWT.H_SCROLL | SWT.V_SCROLL);
	kbScrolledComposite.setExpandHorizontal(true);
	kbScrolledComposite.setExpandVertical(true);

	Composite kbComposite = new Composite(kbScrolledComposite, SWT.NONE);
	kbComposite.setLayout(new GridLayout(2, false));

	Label lblPkmin = new Label(kbComposite, SWT.NONE);
	lblPkmin.setText("pkMin:");

	Spinner pkMinSpinner = new Spinner(kbComposite, SWT.BORDER);
	pkMinSpinner.setMinimum(1000000);
	pkMinSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lblPkmax = new Label(kbComposite, SWT.NONE);
	lblPkmax.setText("pkMax:");

	Spinner pkMaxSpinner = new Spinner(kbComposite, SWT.BORDER);
	pkMaxSpinner.setMaximum(1000000);
	pkMaxSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lblPbmin = new Label(kbComposite, SWT.NONE);
	lblPbmin.setText("pbMin:");

	Text pbMinText = new Text(kbComposite, SWT.BORDER);
	pbMinText.setText("0");
	pbMinText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2));

	Label lblPbmax = new Label(kbComposite, SWT.NONE);
	lblPbmax.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
	lblPbmax.setText("pbMax:");

	Text pbMaxText = new Text(kbComposite, SWT.BORDER);
	pbMaxText.setText("0");
	pbMaxText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lbladckmin = new Label(kbComposite, SWT.NONE);
	lbladckmin.setText("adckMin:");

	Spinner adckMinSpinner = new Spinner(kbComposite, SWT.BORDER);
	adckMinSpinner.setMaximum(1000000);
	adckMinSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lbladckMax = new Label(kbComposite, SWT.NONE);
	lbladckMax.setText("adckMax:");

	Spinner adckMaxSpinner = new Spinner(kbComposite, SWT.BORDER);
	adckMaxSpinner.setMaximum(1000000);
	adckMaxSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lbladcbMin = new Label(kbComposite, SWT.NONE);
	lbladcbMin.setText("adcbMin:");

	Text adcbMinText = new Text(kbComposite, SWT.BORDER);
	adcbMinText.setText("0");
	adcbMinText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2));

	Label lbladcbMax = new Label(kbComposite, SWT.NONE);
	lbladcbMax.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
	lbladcbMax.setText("adcbMax:");

	Text adcbMaxText = new Text(kbComposite, SWT.BORDER);
	adcbMaxText.setText("0");
	adcbMaxText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lblcheckAdckMin = new Label(kbComposite, SWT.NONE);
	lblcheckAdckMin.setText("checkAdckMin:");

	Spinner checkAdckMinSpinner = new Spinner(kbComposite, SWT.BORDER);
	checkAdckMinSpinner.setMaximum(1000000);
	checkAdckMinSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lblcheckAdckMax = new Label(kbComposite, SWT.NONE);
	lblcheckAdckMax.setText("checkAdckMax:");

	Spinner checkAdckMaxSpinner = new Spinner(kbComposite, SWT.BORDER);
	checkAdckMaxSpinner.setMaximum(1000000);
	checkAdckMaxSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	Label lblcheckAdcbMin = new Label(kbComposite, SWT.NONE);
	lblcheckAdcbMin.setText("checkAdcbMin:");

	Text checkAdcbMinText = new Text(kbComposite, SWT.BORDER);
	checkAdcbMinText.setText("0");
	checkAdcbMinText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 2));

	Label lblcheckAdcbMax = new Label(kbComposite, SWT.NONE);
	lblcheckAdcbMax.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
	lblcheckAdcbMax.setText("checkAdcbMax:");

	Text checkAdcbMaxText = new Text(kbComposite, SWT.BORDER);
	checkAdcbMaxText.setText("0");
	checkAdcbMaxText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	kbScrolledComposite.setContent(kbComposite);
	kbScrolledComposite.setMinSize(kbComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	Group grpDot = new Group(calibrateModeComposite, SWT.NONE);
	grpDot.setLayoutData(new FormData());
	grpDot.setText("校准点");
	grpDot.setLayout(new FillLayout(SWT.HORIZONTAL));
	FormData fd_grpDot = new FormData();
	fd_grpDot.top = new FormAttachment(0, 10);
	fd_grpDot.bottom = new FormAttachment(100, -10);
	fd_grpDot.left = new FormAttachment(0, 410);
	fd_grpDot.right = new FormAttachment(100, -30);
	grpDot.setLayoutData(fd_grpDot);

	ScrolledComposite dotScrolledComposite = new ScrolledComposite(grpDot, SWT.H_SCROLL | SWT.V_SCROLL);
	dotScrolledComposite.setExpandHorizontal(true);
	dotScrolledComposite.setExpandVertical(true);

	Composite allDotComposite = new Composite(dotScrolledComposite, SWT.NONE);
	allDotComposite.setLayout(new GridLayout(1, false));

	for (int i = 0; i < dotNum; i++) {
	    createDotComposite(allDotComposite);
	}

	dotScrolledComposite.setContent(allDotComposite);
	dotScrolledComposite.setMinSize(dotScrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	Button deleteButton = new Button(calibrateModeComposite, SWT.NONE);
	deleteButton.setImage(Resources.DEL_ITEM_IMAGE);
	FormData fd_btnNewButton = new FormData();
	fd_btnNewButton.top = new FormAttachment(0, 10);
	fd_btnNewButton.bottom = new FormAttachment(0, 34);
	fd_btnNewButton.left = new FormAttachment(100, -24);
	fd_btnNewButton.right = new FormAttachment(100, 0);
	deleteButton.setLayoutData(fd_btnNewButton);

	Button addButton = new Button(calibrateModeComposite, SWT.NONE);
	addButton.setImage(Resources.ADD_ITEM_IMAGE);
	FormData fd_addButton = new FormData();
	fd_addButton.top = new FormAttachment(100, -34);
	fd_addButton.bottom = new FormAttachment(100, -10);
	fd_addButton.left = new FormAttachment(100, -24);
	fd_addButton.right = new FormAttachment(100, 0);
	addButton.setLayoutData(fd_addButton);

	deleteButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		calibrateModeComposite.dispose();
		contentComposite.layout();
		((ScrolledComposite) contentComposite.getParent()).setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});

	addButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		insertCalibrateModeComposite(contentComposite, addButton);
		((ScrolledComposite) contentComposite.getParent()).setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});
    }

    /**
     * 插入一个校准模式区域
     * 
     * @param contentComposite
     * @param clickedButton
     */
    private void insertCalibrateModeComposite(Composite contentComposite, Button clickedButton) {
	int index = 0;
	Control[] insertComposites = contentComposite.getChildren();
	for (int i = 0; i < insertComposites.length; i++) {
	    if (insertComposites[i] == clickedButton.getParent()) {
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
	createCalibrateModeComposite(contentComposite, 1);
	// 还原insertComposite到contentComposite
	for (Control insertControl : catchComposite.getChildren()) {
	    insertControl.setParent(contentComposite);
	}
	// 销毁catchComposite
	catchComposite.dispose();
	contentComposite.layout();
    }

    /**
     * 创建一个校准点区域
     * 
     * @param allDotComposite
     */
    private void createDotComposite(Composite allDotComposite) {
	Composite dotComposite = new Composite(allDotComposite, SWT.NONE);
	dotComposite.setLayout(new GridLayout(12, false));
	GridData gd_dotComposite = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
	gd_dotComposite.heightHint = 32;
	dotComposite.setLayoutData(gd_dotComposite);

	Button delDotButton = new Button(dotComposite, SWT.NONE);
	GridData gd_delDotButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	gd_delDotButton.heightHint = 18;
	gd_delDotButton.widthHint = 18;
	delDotButton.setLayoutData(gd_delDotButton);

	Label lblAdcmin = new Label(dotComposite, SWT.NONE);
	lblAdcmin.setText("adcMin:");

	Spinner adcMinSpinner = new Spinner(dotComposite, SWT.BORDER);
	adcMinSpinner.setMaximum(1000000);
	GridData gd_adcMinText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_adcMinText.widthHint = 50;
	adcMinSpinner.setLayoutData(gd_adcMinText);

	Label lblAdcmax = new Label(dotComposite, SWT.NONE);
	lblAdcmax.setText("adcMax:");

	Spinner adcMaxSpinner = new Spinner(dotComposite, SWT.BORDER);
	adcMaxSpinner.setMaximum(1000000);
	GridData gd_adcMaxText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_adcMaxText.widthHint = 50;
	adcMaxSpinner.setLayoutData(gd_adcMaxText);

	Label lblMetermin = new Label(dotComposite, SWT.NONE);
	lblMetermin.setText("meterMin:");

	Spinner meterMinSpinner = new Spinner(dotComposite, SWT.BORDER);
	meterMinSpinner.setMaximum(1000000);
	GridData gd_meterMinText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_meterMinText.widthHint = 50;
	meterMinSpinner.setLayoutData(gd_meterMinText);

	Label lblMetermax = new Label(dotComposite, SWT.NONE);
	lblMetermax.setText("meterMax:");

	Spinner meterMaxSpinner = new Spinner(dotComposite, SWT.BORDER);
	meterMaxSpinner.setMaximum(1000000);
	GridData gd_meterMaxText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_meterMaxText.widthHint = 50;
	meterMaxSpinner.setLayoutData(gd_meterMaxText);

	Label lblValue = new Label(dotComposite, SWT.NONE);
	lblValue.setText("DAvalue:");

	Spinner valueSpinner = new Spinner(dotComposite, SWT.BORDER);
	valueSpinner.setMaximum(1000000);
	GridData gd_valueText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_valueText.widthHint = 50;
	valueSpinner.setLayoutData(gd_valueText);

	Button addDotButton = new Button(dotComposite, SWT.NONE);
	GridData gd_addDotButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	gd_addDotButton.heightHint = 18;
	gd_addDotButton.widthHint = 18;
	addDotButton.setLayoutData(gd_addDotButton);

	delDotButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		dotComposite.dispose();
		allDotComposite.layout();
		((ScrolledComposite) allDotComposite.getParent()).setMinSize(allDotComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});

	addDotButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		insertDotComposite(allDotComposite, addDotButton);
		((ScrolledComposite) allDotComposite.getParent()).setMinSize(allDotComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});
    }

    /**
     * 插入一个校准点布局
     * 
     * @param contentComposite
     * @param clickedButton
     */
    private void insertDotComposite(Composite contentComposite, Button clickedButton) {
	int index = 0;
	Control[] insertComposites = contentComposite.getChildren();
	for (int i = 0; i < insertComposites.length; i++) {
	    if (insertComposites[i] == clickedButton.getParent()) {
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
	createDotComposite(contentComposite);
	// 还原insertComposite到contentComposite
	for (Control insertControl : catchComposite.getChildren()) {
	    insertControl.setParent(contentComposite);
	}
	// 销毁catchComposite
	catchComposite.dispose();
	contentComposite.layout();
    }

    /**
     * 保存数据到上位机
     */
    private void save() {
	Control[] controls = contentComposite.getChildren();
	List<CalibratePlanMode> calibratePlanModeList = new ArrayList<>();
	for (int i = 0; i < controls.length; i++) {
	    CalibratePlanMode calibratePlanMode = new CalibratePlanMode();
	    // 配置组
	    Control[] configs = ((Group) ((Composite) controls[i]).getChildren()[0]).getChildren();
	    calibratePlanMode.mode = CalMode.values()[((Combo) configs[1]).getSelectionIndex()];
	    calibratePlanMode.pole = Pole.values()[((Combo) configs[3]).getSelectionIndex()];
	    calibratePlanMode.level = ((Spinner) configs[5]).getSelection();
	    // 参数组
	    Control[] parameters = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i]).getChildren()[1]).getChildren()[0]).getChildren()[0]).getChildren();
	    calibratePlanMode.pKMin = ((Spinner) parameters[1]).getSelection();
	    calibratePlanMode.pKMax = ((Spinner) parameters[3]).getSelection();
	    calibratePlanMode.pBMin = Double.parseDouble(((Text) parameters[5]).getText());
	    calibratePlanMode.pBMax = Double.parseDouble(((Text) parameters[7]).getText());
	    calibratePlanMode.adcKMin = ((Spinner) parameters[9]).getSelection();
	    calibratePlanMode.adcKMax = ((Spinner) parameters[11]).getSelection();
	    calibratePlanMode.adcBMin = Double.parseDouble(((Text) parameters[13]).getText());
	    calibratePlanMode.adcBMax = Double.parseDouble(((Text) parameters[15]).getText());
	    calibratePlanMode.checkAdcKMin = ((Spinner) parameters[17]).getSelection();
	    calibratePlanMode.checkAdcKMax = ((Spinner) parameters[19]).getSelection();
	    calibratePlanMode.checkAdcBmin = Double.parseDouble(((Text) parameters[21]).getText());
	    calibratePlanMode.checkAdcBmax = Double.parseDouble(((Text) parameters[23]).getText());
	    // 校准点组
	    List<CalibratePlanDot> planDotList = new ArrayList<>();
	    Control[] dots = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i]).getChildren()[2]).getChildren()[0]).getChildren()[0]).getChildren();
	    for (int j = 0; j < dots.length; j++) {
		CalibratePlanDot planDot = new CalibratePlanDot();
		// 校准点
		Control[] dotControls = ((Composite) dots[j]).getChildren();
		planDot.adcMin = ((Spinner) dotControls[2]).getSelection();
		planDot.adcMax = ((Spinner) dotControls[4]).getSelection();
		planDot.meterMin = ((Spinner) dotControls[6]).getSelection();
		planDot.meterMax = ((Spinner) dotControls[8]).getSelection();
		planDot.da = ((Spinner) dotControls[10]).getSelection();
		planDotList.add(planDot);
	    }
	    calibratePlanMode.dots = planDotList;
	    calibratePlanModeList.add(calibratePlanMode);
	}
	WorkBench.calibratePlanData.setModes(calibratePlanModeList);
    }

    /**
     * 上位机数据展示到界面中
     */
    private void show() {
	// 清空布局
	for (Control control : contentComposite.getChildren()) {
	    control.dispose();
	}
	// 创建布局
	for (int i = 0; i < WorkBench.calibratePlanData.getModes().size(); i++) {
	    createCalibrateModeComposite(contentComposite, WorkBench.calibratePlanData.getModes().get(i).dots.size());
	}
	// 填充数据
	Control[] controls = contentComposite.getChildren();

	for (int i = 0; i < controls.length; i++) {
	    CalibratePlanMode calibratePlanMode = WorkBench.calibratePlanData.getModes().get(i);
	    // 配置组
	    Control[] configs = ((Group) ((Composite) controls[i]).getChildren()[0]).getChildren();
	    ((Combo) configs[1]).select(calibratePlanMode.mode.ordinal());
	    ((Combo) configs[3]).select(calibratePlanMode.pole.ordinal());
	    ((Spinner) configs[5]).setSelection(calibratePlanMode.level);
	    // 参数组
	    Control[] parameters = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i]).getChildren()[1]).getChildren()[0]).getChildren()[0]).getChildren();
	    ((Spinner) parameters[1]).setSelection((int) calibratePlanMode.pKMin);
	    ((Spinner) parameters[3]).setSelection((int) calibratePlanMode.pKMax);
	    ((Text) parameters[5]).setText(calibratePlanMode.pBMin + "");
	    ((Text) parameters[7]).setText(calibratePlanMode.pBMax + "");
	    ((Spinner) parameters[9]).setSelection((int) calibratePlanMode.adcKMin);
	    ((Spinner) parameters[11]).setSelection((int) calibratePlanMode.adcKMax);
	    ((Text) parameters[13]).setText(calibratePlanMode.adcBMin + "");
	    ((Text) parameters[15]).setText(calibratePlanMode.adcBMax + "");
	    ((Spinner) parameters[17]).setSelection((int) calibratePlanMode.checkAdcKMin);
	    ((Spinner) parameters[19]).setSelection((int) calibratePlanMode.checkAdcKMax);
	    ((Text) parameters[21]).setText(calibratePlanMode.checkAdcBmin + "");
	    ((Text) parameters[23]).setText(calibratePlanMode.checkAdcBmax + "");
	    // 校准点组
	    Control[] dots = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i]).getChildren()[2]).getChildren()[0]).getChildren()[0]).getChildren();
	    for (int j = 0; j < dots.length; j++) {
		CalibratePlanDot planDot = calibratePlanMode.dots.get(j);
		// 校准点
		Control[] dotControls = ((Composite) dots[j]).getChildren();
		((Spinner) dotControls[2]).setSelection((int) planDot.adcMin);
		((Spinner) dotControls[4]).setSelection((int) planDot.adcMax);
		((Spinner) dotControls[6]).setSelection((int) planDot.meterMin);
		((Spinner) dotControls[8]).setSelection((int) planDot.meterMax);
		((Spinner) dotControls[10]).setSelection((int) planDot.da);
	    }
	}
	contentComposite.layout();
	((ScrolledComposite) contentComposite.getParent()).setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * 输出workbench中校准数据到xml文件中
     * 
     * @param filePath
     * @param info
     * @return
     */
    public static boolean writeClibrateToXML(String filePath, StringBuffer info) {
	CalibratePlanData calibratePlanData = WorkBench.calibratePlanData;
	SteadyCfgData steadyCfgData = WorkBench.steadyCfgData;
	DelayData delayData = WorkBench.delayData;
	List<CalibratePlanMode> calibratePlanModeList = calibratePlanData.getModes();

	Document document = DocumentHelper.createDocument();
	// 根节点
	Element calibrateSchemaElement = document.addElement("calibrateSchema");
	// 一级节点
	Element calibratePlanDataElement = calibrateSchemaElement.addElement("calibratePlanData");
	calibratePlanDataElement.addAttribute("needValidate", calibratePlanData.isNeedValidate() + "");
	calibratePlanDataElement.addAttribute("needCalculateAfterCalibrate", calibratePlanData.isNeedCalculateAfterCalibrate() + "");
	Element steadyCfgDataElement = calibrateSchemaElement.addElement("steadyCfgData");
	Element delayDataElement = calibrateSchemaElement.addElement("delayData");
	for (int i = 0; i < calibratePlanModeList.size(); i++) {
	    CalibratePlanMode calibratePlanMode = calibratePlanModeList.get(i);
	    // 二级节点
	    Element calibrateModeElement = calibratePlanDataElement.addElement("calibrateMode");
	    // 三级节点
	    Element configElement = calibrateModeElement.addElement("config");
	    configElement.addAttribute("mode", calibratePlanMode.mode.ordinal() + "");
	    configElement.addAttribute("pole", calibratePlanMode.pole.ordinal() + "");
	    configElement.addAttribute("level", calibratePlanMode.level + "");
	    Element kbValueElement = calibrateModeElement.addElement("kbValue");
	    kbValueElement.addAttribute("pKMin", calibratePlanMode.pKMin + "");
	    kbValueElement.addAttribute("pKMax", calibratePlanMode.pKMax + "");
	    kbValueElement.addAttribute("pBMin", calibratePlanMode.pBMin + "");
	    kbValueElement.addAttribute("pBMax", calibratePlanMode.pBMax + "");
	    kbValueElement.addAttribute("adcKMin", calibratePlanMode.adcKMin + "");
	    kbValueElement.addAttribute("adcKMax", calibratePlanMode.adcKMax + "");
	    kbValueElement.addAttribute("adcBMin", calibratePlanMode.adcBMin + "");
	    kbValueElement.addAttribute("adcBMax", calibratePlanMode.adcBMax + "");
	    kbValueElement.addAttribute("checkAdcKMin", calibratePlanMode.checkAdcKMin + "");
	    kbValueElement.addAttribute("checkAdcKMax", calibratePlanMode.checkAdcKMax + "");
	    kbValueElement.addAttribute("checkAdcBmin", calibratePlanMode.checkAdcBmin + "");
	    kbValueElement.addAttribute("checkAdcBmax", calibratePlanMode.checkAdcBmax + "");
	    Element dotsElement = calibrateModeElement.addElement("dots");
	    List<CalibratePlanDot> calibratePlanDotList = calibratePlanMode.dots;
	    for (int j = 0; j < calibratePlanDotList.size(); j++) {
		CalibratePlanDot calibratePlanDot = calibratePlanDotList.get(j);
		// 四级节点
		Element dotElement = dotsElement.addElement("dot");
		dotElement.addAttribute("da", calibratePlanDot.da + "");
		dotElement.addAttribute("adcMin", calibratePlanDot.adcMin + "");
		dotElement.addAttribute("adcMax", calibratePlanDot.adcMax + "");
		dotElement.addAttribute("meterMin", calibratePlanDot.meterMin + "");
		dotElement.addAttribute("meterMax", calibratePlanDot.meterMax + "");
	    }

	}

	steadyCfgDataElement.addAttribute("sampleCount", steadyCfgData.getSampleCount() + "");
	steadyCfgDataElement.addAttribute("maxSigma", steadyCfgData.getMaxSigma() + "");
	steadyCfgDataElement.addAttribute("trailCount", steadyCfgData.getTrailCount() + "");

	delayDataElement.addAttribute("moduleOpenDelay", delayData.getModuleOpenDelay() + "");
	delayDataElement.addAttribute("moduleCloseDelay", delayData.getModuleCloseDelay() + "");
	delayDataElement.addAttribute("modeSwitchDelay", delayData.getModeSwitchDelay() + "");
	delayDataElement.addAttribute("programSetDelay", delayData.getProgramSetDelay() + "");
	delayDataElement.addAttribute("low2hightDelay", delayData.getLow2hightDelay() + "");
	delayDataElement.addAttribute("high2lowDelay", delayData.getHigh2lowDelay() + "");
	delayDataElement.addAttribute("readMeterDelay", delayData.getReadMeterDelay() + "");
	delayDataElement.addAttribute("switchMeterDelay", delayData.getSwitchMeterDelay() + "");

	// 输出格式刷
	OutputFormat outputFormat = OutputFormat.createPrettyPrint();
	// 设置文件编码
	outputFormat.setEncoding("UTF-8");
	// 设置文件的输出流
	OutputStream outputStream;
	try {
	    outputStream = new FileOutputStream(filePath);
	    // 创建xmlWriter
	    XMLWriter xmlWriter = new XMLWriter(outputStream, outputFormat);
	    // 写入文件到xml
	    xmlWriter.write(document);
	    // 清空缓存，关闭资源
	    xmlWriter.flush();
	    xmlWriter.close();
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    info.append(e.getMessage());
	    return false;
	}
    }

    /**
     * 读取校准的xml到workbench
     * 
     * @param filePath
     * @param info
     * @return
     */
    public static boolean readXMLToClibrate(String filePath, StringBuffer info) {
	Document document = null;
	try {
	    document = new SAXReader().read(new File(filePath));
	} catch (DocumentException e) {
	    e.printStackTrace();
	    info.append(e.getMessage());
	    return false;
	}
	CalibratePlanData calibratePlanData = WorkBench.calibratePlanData;
	SteadyCfgData steadyCfgData = WorkBench.steadyCfgData;
	DelayData delayData = WorkBench.delayData;
	List<CalibratePlanMode> calibratePlanModeList = new ArrayList<>();

	// 根节点
	Element rootElement = document.getRootElement();
	// 一级节点
	Element calibratePlanDataElement = rootElement.element("calibratePlanData");

	Element steadyCfgDataElement = rootElement.element("steadyCfgData");
	steadyCfgData.setSampleCount(Integer.parseInt(steadyCfgDataElement.attribute("sampleCount").getValue()));
	steadyCfgData.setMaxSigma(Double.parseDouble(steadyCfgDataElement.attribute("maxSigma").getValue()));
	steadyCfgData.setTrailCount(Integer.parseInt(steadyCfgDataElement.attribute("trailCount").getValue()));

	Element delayDataaElement = rootElement.element("delayData");
	delayData.setModuleOpenDelay(Integer.parseInt(delayDataaElement.attribute("moduleOpenDelay").getValue()));
	delayData.setModuleCloseDelay(Integer.parseInt(delayDataaElement.attribute("moduleCloseDelay").getValue()));
	delayData.setModeSwitchDelay(Integer.parseInt(delayDataaElement.attribute("modeSwitchDelay").getValue()));
	delayData.setProgramSetDelay(Integer.parseInt(delayDataaElement.attribute("programSetDelay").getValue()));
	delayData.setLow2hightDelay(Integer.parseInt(delayDataaElement.attribute("low2hightDelay").getValue()));
	delayData.setHigh2lowDelay(Integer.parseInt(delayDataaElement.attribute("high2lowDelay").getValue()));
	delayData.setReadMeterDelay(Integer.parseInt(delayDataaElement.attribute("readMeterDelay").getValue()));
	delayData.setSwitchMeterDelay(Integer.parseInt(delayDataaElement.attribute("switchMeterDelay").getValue()));

	calibratePlanData.setNeedValidate(Boolean.parseBoolean(calibratePlanDataElement.attribute("needValidate").getValue()));
	calibratePlanData.setNeedCalculateAfterCalibrate(Boolean.parseBoolean(calibratePlanDataElement.attribute("needCalculateAfterCalibrate").getValue()));
	List<Element> calibrateModeElementList = calibratePlanDataElement.elements();
	for (int i = 0; i < calibrateModeElementList.size(); i++) {
	    CalibratePlanMode calibratePlanMode = new CalibratePlanMode();
	    // 二级节点
	    Element calibrateModeElement = calibrateModeElementList.get(i);
	    // 三级节点
	    Element configElement = calibrateModeElement.element("config");
	    calibratePlanMode.mode = CalMode.values()[Integer.parseInt(configElement.attribute("mode").getValue())];
	    calibratePlanMode.pole = Pole.values()[Integer.parseInt(configElement.attribute("pole").getValue())];
	    calibratePlanMode.level = Integer.parseInt(configElement.attribute("level").getValue());
	    Element kbValueElement = calibrateModeElement.element("kbValue");
	    calibratePlanMode.pKMin = Double.parseDouble(kbValueElement.attribute("pKMin").getValue());
	    calibratePlanMode.pKMax = Double.parseDouble(kbValueElement.attribute("pKMax").getValue());
	    calibratePlanMode.pBMin = Double.parseDouble(kbValueElement.attribute("pBMin").getValue());
	    calibratePlanMode.pBMax = Double.parseDouble(kbValueElement.attribute("pBMax").getValue());
	    calibratePlanMode.adcKMin = Double.parseDouble(kbValueElement.attribute("adcKMin").getValue());
	    calibratePlanMode.adcKMax = Double.parseDouble(kbValueElement.attribute("adcKMax").getValue());
	    calibratePlanMode.adcBMin = Double.parseDouble(kbValueElement.attribute("adcBMin").getValue());
	    calibratePlanMode.adcBMax = Double.parseDouble(kbValueElement.attribute("adcBMax").getValue());
	    calibratePlanMode.checkAdcKMin = Double.parseDouble(kbValueElement.attribute("checkAdcKMin").getValue());
	    calibratePlanMode.checkAdcKMax = Double.parseDouble(kbValueElement.attribute("checkAdcKMax").getValue());
	    calibratePlanMode.checkAdcBmin = Double.parseDouble(kbValueElement.attribute("checkAdcBmin").getValue());
	    calibratePlanMode.checkAdcBmax = Double.parseDouble(kbValueElement.attribute("checkAdcBmax").getValue());
	    Element dotsElement = calibrateModeElement.element("dots");
	    List<Element> dotElementList = dotsElement.elements();
	    for (int j = 0; j < dotElementList.size(); j++) {
		CalibratePlanDot dot = new CalibratePlanDot();
		// 四级节点
		Element dotElement = dotElementList.get(j);
		dot.da = Long.parseLong(dotElement.attribute("da").getValue());
		dot.adcMin = Double.parseDouble(dotElement.attribute("adcMin").getValue());
		dot.adcMax = Double.parseDouble(dotElement.attribute("adcMax").getValue());
		dot.meterMin = Double.parseDouble(dotElement.attribute("meterMin").getValue());
		dot.meterMax = Double.parseDouble(dotElement.attribute("meterMax").getValue());
		calibratePlanMode.dots.add(dot);
	    }
	    calibratePlanModeList.add(calibratePlanMode);
	}
	calibratePlanData.setModes(calibratePlanModeList);
	return true;
    }
}