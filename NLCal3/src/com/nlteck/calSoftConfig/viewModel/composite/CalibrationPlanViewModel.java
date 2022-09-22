package com.nlteck.calSoftConfig.viewModel.composite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.view.composite.CalibrationPlanComposite;
import com.nlteck.calSoftConfig.viewModel.ConfigDialogViewModel;
import com.nltecklib.protocol.li.PCWorkform.CalibratePlanData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.utils.XmlUtil;

/**
 * @description 校准方案控件视图模型
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:51:40 AM
 */
public class CalibrationPlanViewModel extends ConfigDialogViewModel {
    protected CalibrationPlanComposite calibrationPlanComposite;
    public CalibratePlanData calibratePlanData;

    public CalibrationPlanViewModel(ViewModelComposite composite) {
	super(composite);
	this.calibrationPlanComposite = (CalibrationPlanComposite) composite;
    }

    @Override
    protected void loadXmlFile(String filePath) throws Exception {
	calibratePlanData = loadCalibratePlan(filePath);
    }

    @Override
    protected void saveXmlFile(String filePath) throws Exception {
	saveCalibratePlan(filePath);
    }

    @Override
    protected String xmlFileDefaultPath() {
	localPath = "temp/calibratePlan.xml";
	remotePath = "~/config/calConfig/calibratePlan.xml";
	return localPath;
    }

    @Override
    protected Object newModel() {
	calibratePlanData = new CalibratePlanData();
	return calibratePlanData;
    }

    @Override
    public void refreshModel() {
	if (calibrationPlanComposite.contentComposite != null) {
	    Display.getDefault().asyncExec(new Runnable() {

		@Override
		public void run() {
		    Control[] controls = calibrationPlanComposite.contentComposite.getChildren();
		    List<CalibratePlanMode> calibratePlanModeList = new ArrayList<>();
		    for (int i = 0; i < controls.length; i++) {
			CalibratePlanMode calibratePlanMode = new CalibratePlanMode();
			// 工步
			Control[] configs = ((Group) ((Composite) controls[i]).getChildren()[0]).getChildren();
			calibratePlanMode.mode = CalMode.values()[((Combo) configs[1]).getSelectionIndex()];
			calibratePlanMode.pole = Pole.values()[((Combo) configs[3]).getSelectionIndex()];
			calibratePlanMode.level = ((Spinner) configs[5]).getSelection();
			calibratePlanMode.moduleIndex = ((Spinner) configs[7]).getSelection();
			calibratePlanMode.combine = ((CCombo) configs[9]).getSelectionIndex() == 0;
			calibratePlanMode.mainMeter = ((Spinner) configs[11]).getSelection();
			// 数值范围
			Control[] parameters = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i])
				.getChildren()[1]).getChildren()[0]).getChildren()[0]).getChildren();
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
			Control[] dots = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i])
				.getChildren()[2]).getChildren()[0]).getChildren()[0]).getChildren();
			for (int j = 0; j < dots.length; j++) {
			    CalibratePlanDot planDot = new CalibratePlanDot();
			    // 校准点
			    Control[] dotControls = ((Composite) dots[j]).getChildren();
			    planDot.da = ((Spinner) dotControls[3]).getSelection();
			    planDot.adcMin = ((Spinner) dotControls[5]).getSelection();
			    planDot.adcMax = ((Spinner) dotControls[7]).getSelection();
			    planDot.meterMin = ((Spinner) dotControls[9]).getSelection();
			    planDot.meterMax = ((Spinner) dotControls[11]).getSelection();
			    planDotList.add(planDot);
			}
			calibratePlanMode.dots = planDotList;
			calibratePlanModeList.add(calibratePlanMode);
		    }
		    calibratePlanData.setModes(calibratePlanModeList);

		}
	    });
	}
	super.refreshModel();
    }

    @Override
    public void refreshControl() {
	if (calibrationPlanComposite.contentComposite != null) {
	    Display.getDefault().asyncExec(new Runnable() {

		@Override
		public void run() {
		    // 清空布局
		    for (Control control : calibrationPlanComposite.contentComposite.getChildren()) {
			control.dispose();
		    }
		    // 创建布局
		    for (int i = 0; i < calibratePlanData.getModes().size(); i++) {
			calibrationPlanComposite.createCalibrateModeComposite(calibrationPlanComposite.contentComposite,
				calibratePlanData.getModes().get(i).dots.size());
		    }
		    // 填充数据
		    Control[] controls = calibrationPlanComposite.contentComposite.getChildren();

		    for (int i = 0; i < controls.length; i++) {
			CalibratePlanMode calibratePlanMode = calibratePlanData.getModes().get(i);
			// 工步
			Control[] configs = ((Group) ((Composite) controls[i]).getChildren()[0]).getChildren();
			((Combo) configs[1]).select(calibratePlanMode.mode.ordinal());
			((Combo) configs[3]).select(calibratePlanMode.pole.ordinal());
			((Spinner) configs[5]).setSelection(calibratePlanMode.level);
			((Spinner) configs[7]).setSelection(calibratePlanMode.moduleIndex);
			((CCombo) configs[9]).select(calibratePlanMode.combine ? 0 : 1);
			((Spinner) configs[11]).setSelection((int) calibratePlanMode.mainMeter);

			// 数值范围
			Control[] parameters = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i])
				.getChildren()[1]).getChildren()[0]).getChildren()[0]).getChildren();
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
			// 校准点
			Control[] dots = ((Composite) ((ScrolledComposite) ((Group) ((Composite) controls[i])
				.getChildren()[2]).getChildren()[0]).getChildren()[0]).getChildren();
			for (int j = 0; j < dots.length; j++) {
			    CalibratePlanDot planDot = calibratePlanMode.dots.get(j);
			    // 校准点
			    Control[] dotControls = ((Composite) dots[j]).getChildren();
			    ((Spinner) dotControls[3]).setSelection((int) planDot.da);
			    ((Spinner) dotControls[5]).setSelection((int) planDot.adcMin);
			    ((Spinner) dotControls[7]).setSelection((int) planDot.adcMax);
			    ((Spinner) dotControls[9]).setSelection((int) planDot.meterMin);
			    ((Spinner) dotControls[11]).setSelection((int) planDot.meterMax);
			}
		    }
		    calibrationPlanComposite.contentComposite.layout();
		    ScrolledComposite scrolledComposite = (ScrolledComposite) calibrationPlanComposite.contentComposite
			    .getParent();
		    scrolledComposite.setMinSize(
			    calibrationPlanComposite.contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		}
	    });
	}
	super.refreshControl();
    }

    /**
     * 读取校准方案
     * 
     * @return
     */
    protected CalibratePlanData loadCalibratePlan(String filePath) throws Exception {

	CalibratePlanData data = calibratePlanData;
	Document doc = XmlUtil.loadXml(filePath);
	Element rootElement = doc.getRootElement();
	data.setNeedValidate(Boolean.parseBoolean(rootElement.attributeValue("needValidate")));
	data.setNeedCalculateAfterCalibrate(
		Boolean.parseBoolean(rootElement.attributeValue("needCalculateAfterCalibrate")));
	data.setMaxProgramV(Long.parseLong(rootElement.attributeValue("maxProgramV")));
	data.setMaxProgramI(Long.parseLong(rootElement.attributeValue("maxProgramI")));
	data.getModes().clear();
	List<Element> modeElements = rootElement.elements("mode");
	modeElements.stream().forEach(me -> {
	    CalibratePlanMode mode = new CalibratePlanMode();
	    data.getModes().add(mode);
	    mode.mode = CalMode.valueOf(me.attributeValue("mode"));
	    mode.moduleIndex = me.attributeValue("module") == null ? 0 : Integer.parseInt(me.attributeValue("module"));
	    mode.level = Integer.parseInt(me.attributeValue("level"));
	    mode.pole = Pole.valueOf(me.attributeValue("pole"));
	    mode.pKMin = Double.parseDouble(me.attributeValue("pKMin"));
	    mode.pKMax = Double.parseDouble(me.attributeValue("pKMax"));
	    mode.pBMin = Double.parseDouble(me.attributeValue("pBMin"));
	    mode.pBMax = Double.parseDouble(me.attributeValue("pBMax"));
	    mode.adcKMin = Double.parseDouble(me.attributeValue("adcKMin"));
	    mode.adcKMax = Double.parseDouble(me.attributeValue("adcKMax"));
	    mode.adcBMin = Double.parseDouble(me.attributeValue("adcBMin"));
	    mode.adcBMax = Double.parseDouble(me.attributeValue("adcBMax"));
	    mode.checkAdcKMin = Double.parseDouble(me.attributeValue("checkAdcKMin"));
	    mode.checkAdcKMax = Double.parseDouble(me.attributeValue("checkAdcKMax"));
	    mode.checkAdcBmin = Double.parseDouble(me.attributeValue("checkAdcBmin"));
	    mode.checkAdcBmax = Double.parseDouble(me.attributeValue("checkAdcBmax"));
	    if (me.attributeValue("combine") != null) {

		mode.combine = Boolean.parseBoolean(me.attributeValue("combine"));
	    }
	    if (me.attributeValue("mainMeter") != null) {

		mode.mainMeter = Double.parseDouble(me.attributeValue("mainMeter"));
	    }

	    List<Element> dotElements = me.elements("dot");
	    dotElements.stream().forEach(de -> {
		CalibratePlanDot dot = new CalibratePlanDot();
		mode.dots.add(dot);
		dot.da = Long.parseLong(de.attributeValue("da"));
		dot.adcMin = Double.parseDouble(de.attributeValue("adcMin"));
		dot.adcMax = Double.parseDouble(de.attributeValue("adcMax"));
		dot.meterMin = Double.parseDouble(de.attributeValue("meterMin"));
		dot.meterMax = Double.parseDouble(de.attributeValue("meterMax"));
	    });

	});
	return data;
    }

    /**
     * 保存校准方案
     * 
     * @return
     * @throws IOException
     */
    protected void saveCalibratePlan(String filePath) throws IOException {

	CalibratePlanData data = calibratePlanData;
	Document doc = DocumentFactory.getInstance().createDocument();

	Element rootElement = DocumentFactory.getInstance().createElement("calibratePlan");
	doc.setRootElement(rootElement);

	rootElement.addAttribute("needValidate", data.isNeedValidate() + "");
	rootElement.addAttribute("needCalculateAfterCalibrate", data.isNeedCalculateAfterCalibrate() + "");
	rootElement.addAttribute("maxProgramV", data.getMaxProgramV() + "");
	rootElement.addAttribute("maxProgramI", data.getMaxProgramI() + "");

	data.getModes().stream().forEach(mode -> {
	    Element modeElement = DocumentFactory.getInstance().createElement("mode");
	    rootElement.add(modeElement);
	    modeElement.addAttribute("mode", mode.mode.name());
	    modeElement.addAttribute("level", mode.level + "");
	    modeElement.addAttribute("pole", mode.pole.name());
	    modeElement.addAttribute("moduleIndex", mode.moduleIndex + "");
	    modeElement.addAttribute("combine", mode.combine + "");
	    modeElement.addAttribute("mainMeter", mode.mainMeter + "");
	    modeElement.addAttribute("pKMin", mode.pKMin + "");
	    modeElement.addAttribute("pKMax", mode.pKMax + "");
	    modeElement.addAttribute("pBMin", mode.pBMin + "");
	    modeElement.addAttribute("pBMax", mode.pBMax + "");
	    modeElement.addAttribute("adcKMin", mode.adcKMin + "");
	    modeElement.addAttribute("adcKMax", mode.adcKMax + "");
	    modeElement.addAttribute("adcBMin", mode.adcBMin + "");
	    modeElement.addAttribute("adcBMax", mode.adcBMax + "");
	    modeElement.addAttribute("checkAdcKMin", mode.checkAdcKMin + "");
	    modeElement.addAttribute("checkAdcKMax", mode.checkAdcKMax + "");
	    modeElement.addAttribute("checkAdcBmin", mode.checkAdcBmin + "");
	    modeElement.addAttribute("checkAdcBmax", mode.checkAdcBmax + "");

	    mode.dots.stream().forEach(dot -> {

		Element dotElement = DocumentFactory.getInstance().createElement("dot");
		modeElement.add(dotElement);
		dotElement.addAttribute("da", dot.da + "");
		dotElement.addAttribute("adcMin", dot.adcMin + "");
		dotElement.addAttribute("adcMax", dot.adcMax + "");
		dotElement.addAttribute("meterMin", dot.meterMin + "");
		dotElement.addAttribute("meterMax", dot.meterMax + "");

	    });
	});

	XmlUtil.saveXml(filePath, doc);

    }

}
