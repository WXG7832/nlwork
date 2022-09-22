package com.nlteck.calSoftConfig.view.composite;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calModel.base.I18N;
import com.nlteck.calSoftConfig.controller.ControlFactory;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.ViewModel;
import com.nlteck.calSoftConfig.viewModel.composite.CalibrationPlanViewModel;

/**
 * @description 校准方案控件
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:48:16 AM
 */
public class CalibrationPlanComposite extends ViewModelComposite {

    protected CalibrationPlanViewModel calibrationPlanViewModel;
    public Composite contentComposite;
    public SashForm containerSashForm;

    public CalibrationPlanComposite(Composite parent, int style, Object[] args) {
	super(parent, style, args);
    }

    /**
     * @description 校准方案参数
     * @author zemin_zhu
     * @dateTime Jun 29, 2022 11:38:41 AM
     */
    protected void calibrationPlanSettingGroup(Composite parent) {
	Group group = ControlFactory.getInstance().newGroup(parent, "校准方案参数", 4, 100);
	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "ADC/表值/KB不可超限");
	calibrationPlanViewModel.controlMappingObject.put("needValidate", cLabel);
	cLabel = ControlFactory.getInstance().newCLabel(group, "校准后计量");
	calibrationPlanViewModel.controlMappingObject.put("needCalculateAfterCalibrate", cLabel);
    }

    /**
     * 创建一个校准模式区域
     * 
     * @param contentComposite
     */
    public void createCalibrateModeComposite(Composite contentComposite, int dotNum) {
	Composite calibrateModeComposite = new Composite(contentComposite, SWT.NONE);
	calibrateModeComposite.setLayout(new FormLayout());
	GridData gd_calibrateModeComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	gd_calibrateModeComposite.heightHint = 180;
	calibrateModeComposite.setLayoutData(gd_calibrateModeComposite);

	Group grpConfig = new Group(calibrateModeComposite, SWT.NONE);
	grpConfig.setText(I18N.getVal(I18N.ClibrateConfigPart_groupBox_stepSetting));
	GridLayout gl_grpConfig = new GridLayout(2, false);
	grpConfig.setLayout(gl_grpConfig);
	FormData fd_grpConfig = new FormData();
	fd_grpConfig.top = new FormAttachment(0, 10);
	fd_grpConfig.bottom = new FormAttachment(100, -10);
	fd_grpConfig.left = new FormAttachment(0, 10);
	fd_grpConfig.right = new FormAttachment(0, 170);
	grpConfig.setLayoutData(fd_grpConfig);

	Label modeLabel = new Label(grpConfig, SWT.NONE);
	modeLabel.setText(I18N.getVal(I18N.ClibrateConfigPart_label_step));

	Combo modeCombo = new Combo(grpConfig, SWT.READ_ONLY);
	modeCombo.setItems("SLEEP", "CC", "CV", "DC", "CV2");
	modeCombo.select(0);
	GridData gd_modeCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
	gd_modeCombo.widthHint = 36;
	modeCombo.setLayoutData(gd_modeCombo);

	Label poleLabel = new Label(grpConfig, SWT.NONE);
	poleLabel.setText(I18N.getVal(I18N.ClibrateConfigPart_label_pole));

	Combo poleCombo = new Combo(grpConfig, SWT.READ_ONLY);
	poleCombo.setItems(new String[] { I18N.getVal(I18N.ClibrateConfigPart_comboBox_pole_negative),
		I18N.getVal(I18N.ClibrateConfigPart_comboBox_pole_positive) });
	poleCombo.select(0);
	poleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

	Label precisionLabel = new Label(grpConfig, SWT.NONE);
	precisionLabel.setText(I18N.getVal(I18N.ClibrateConfigPart_label_precision));

	Spinner precisionSpinner = new Spinner(grpConfig, SWT.BORDER);
	precisionSpinner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

	ControlFactory.getInstance().setLayoutDataFactory(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER),
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER));
	ControlFactory.getInstance().newSpinner(grpConfig, "膜片序号");

	ControlFactory.getInstance().newCCombo(grpConfig, "自动DAC", "使用主膜片dacKB与预估电流值计算得到DAC");

	ControlFactory.getInstance().newSpinner(grpConfig, "预估电流值");

	ControlFactory.getInstance().useDefautLayoutData();

	Group grpKb = new Group(calibrateModeComposite, SWT.NONE);
	grpKb.setText(I18N.getVal(I18N.ClibrateConfigPart_groupBox_kbSetting));
	grpKb.setLayout(new FillLayout(SWT.HORIZONTAL));
	FormData fd_grpKb = new FormData();
	fd_grpKb.top = new FormAttachment(0, 10);
	fd_grpKb.bottom = new FormAttachment(100, -10);
	fd_grpKb.left = new FormAttachment(0, 180);
	fd_grpKb.right = new FormAttachment(0, 350);
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
	grpDot.setText(I18N.getVal(I18N.ClibrateConfigPart_groupBox_dotSetting));
	grpDot.setLayout(new FillLayout(SWT.HORIZONTAL));
	FormData fd_grpDot = new FormData();
	fd_grpDot.top = new FormAttachment(0, 10);
	fd_grpDot.bottom = new FormAttachment(100, -10);
	fd_grpDot.left = new FormAttachment(0, 360);
	fd_grpDot.right = new FormAttachment(50, -30);
	fd_grpDot.width = 450;
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
	deleteButton.setImage(ControlFactory.getInstance().DEL_ITEM_IMAGE);
	FormData fd_addButton = new FormData();
	fd_addButton.top = new FormAttachment(100, -34);
	fd_addButton.bottom = new FormAttachment(100, -10);
	fd_addButton.left = new FormAttachment(50, -24);
	fd_addButton.right = new FormAttachment(50, 0);
	deleteButton.setLayoutData(fd_addButton);

	Button addButton = new Button(calibrateModeComposite, SWT.NONE);
	addButton.setImage(ControlFactory.getInstance().ADD_ITEM_IMAGE);
	FormData fd_btnNewButton = new FormData();
	fd_btnNewButton.top = new FormAttachment(0, 10);
	fd_btnNewButton.bottom = new FormAttachment(0, 34);
	fd_btnNewButton.left = new FormAttachment(50, -24);
	fd_btnNewButton.right = new FormAttachment(50, 0);
	addButton.setLayoutData(fd_btnNewButton);

	deleteButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		calibrateModeComposite.dispose();
		contentComposite.layout();
		((ScrolledComposite) contentComposite.getParent())
			.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});

	addButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		insertCalibrateModeComposite(contentComposite, addButton);
		((ScrolledComposite) contentComposite.getParent())
			.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});
    }

    /**
     * 插入一个校准模式区域
     * 
     * @param contentComposite
     * @param clickedButton
     */
    protected void insertCalibrateModeComposite(Composite contentComposite, Button clickedButton) {
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
    protected void createDotComposite(Composite allDotComposite) {
	Composite dotComposite = new Composite(allDotComposite, SWT.NONE);
	dotComposite.setLayout(new GridLayout(12, false));
	GridData gd_dotComposite = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
	gd_dotComposite.heightHint = 32;
	dotComposite.setLayoutData(gd_dotComposite);

	Button addDotButton = new Button(dotComposite, SWT.NONE);
	GridData gd_addDotButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	gd_addDotButton.heightHint = 25;
	gd_addDotButton.widthHint = 25;
	addDotButton.setLayoutData(gd_addDotButton);
	addDotButton.setText("ADD");

	Button delDotButton = new Button(dotComposite, SWT.NONE);
	GridData gd_delDotButton = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	gd_delDotButton.heightHint = 25;
	gd_delDotButton.widthHint = 25;
	delDotButton.setLayoutData(gd_delDotButton);
	delDotButton.setText("DEL");

	Label lblValue = new Label(dotComposite, SWT.NONE);
	lblValue.setText("DAvalue:");

	Spinner valueSpinner = new Spinner(dotComposite, SWT.BORDER);
	valueSpinner.setMaximum(1000000);
	GridData gd_valueText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	gd_valueText.widthHint = 50;
	valueSpinner.setLayoutData(gd_valueText);

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

	delDotButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		dotComposite.dispose();
		allDotComposite.layout();
		((ScrolledComposite) allDotComposite.getParent())
			.setMinSize(allDotComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});

	addDotButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		insertDotComposite(allDotComposite, addDotButton);
		((ScrolledComposite) allDotComposite.getParent())
			.setMinSize(allDotComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    }
	});
    }

    /**
     * 插入一个校准点布局
     * 
     * @param contentComposite
     * @param clickedButton
     */
    protected void insertDotComposite(Composite contentComposite, Button clickedButton) {
	int index = 0;
	Control[] insertComposites = contentComposite.getChildren();
	for (int i = 0; i < insertComposites.length; i++) {
	    if (insertComposites[i] == clickedButton.getParent()) {
		index = i;
		break;
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

    @Override
    protected ViewModel createViewModel(ViewModelComposite composite) {

	calibrationPlanViewModel = new CalibrationPlanViewModel(composite);
	return calibrationPlanViewModel;
    }

    @Override
    protected void createControl(Composite parent) {

	containerSashForm = new SashForm(parent, SWT.VERTICAL);
	calibrationPlanSettingGroup(containerSashForm);

	ControlFactory.getInstance().newSeparator(containerSashForm, "校准方案");

	ScrolledComposite scrolledComposite = new ScrolledComposite(containerSashForm, SWT.V_SCROLL);
	scrolledComposite.setExpandHorizontal(true);
	scrolledComposite.setExpandVertical(true);

	containerSashForm.setWeights(new int[] {2,1,7});

	contentComposite = new Composite(scrolledComposite, SWT.NONE);
	GridLayout gl_contentComposite = new GridLayout(1, false);
	gl_contentComposite.verticalSpacing = 10;
	gl_contentComposite.marginHeight = 10;
	gl_contentComposite.marginWidth = 10;
	contentComposite.setLayout(gl_contentComposite);

	scrolledComposite.setContent(contentComposite);
	scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }
}