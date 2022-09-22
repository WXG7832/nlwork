package com.nlteck.calSoftConfig.view.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calSoftConfig.controller.ControlFactory;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.view.tableViewer.CalPlanTableViewer;
import com.nlteck.calSoftConfig.viewModel.ViewModel;
import com.nlteck.calSoftConfig.viewModel.composite.CalPlanTableViewerVerViewModel;

public class CalPlanCompositeTableViewerVer extends CalibrationPlanComposite {

    protected CalPlanTableViewerVerViewModel calPlanTableViewerVerViewModel;

    public CalPlanCompositeTableViewerVer(Composite parent, int style, Object[] args) {
	super(parent, style, args);
    }

    @Override
    protected void createControl(Composite parent) {
	calibrationPlanSettingGroup(parent);
    }

    @Override
    protected void createSubViewModelControl(Composite parent) {
	calStepPropertyGroup(parent);
	calibrationPlanGroup(parent);
    }

    protected void calibrationPlanGroup(Composite parent) {
	Group group = ControlFactory.getInstance().newGroup(parent, "校准工步", 2, 500);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().span(4, 1).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 2).applyTo(composite);

	CalPlanTableViewer tableViewer = new CalPlanTableViewer(composite, true, false);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());
	calPlanTableViewerVerViewModel.controlMappingObject.put("modes", tableViewer);
	List<String> headers = new ArrayList<>();
	headers.add("极性");
	headers.add("工步");
	headers.add("档位");
	headers.add("膜片号");
	headers.add("自动DAC");
	headers.add("预估电流值");
	headers.add("DAC");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewer.setHeaders(headers, widths);

	try {
	    tableViewer.setInputNotTableExItem(calPlanTableViewerVerViewModel.calibratePlanData.getModes());
	    tableViewer.setEnabledFieldNameArr(
		    new String[] { "pole", "mode", "level", "moduleIndex", "combine", "mainMeter", "da" });
	    tableViewer.setLabelProviderAutomatic();
	    tableViewer.setColumnEditorAutomatic();

	} catch (Exception e) {
	    viewModel.exceptionReceiver("校准方案表格控件, 赋值数据源发生异常", e);
	}

	ControlFactory.getInstance().newTableViewerToolbar(tableViewer);
    }

    @Override
    protected ViewModel createViewModel(ViewModelComposite composite) {
	calPlanTableViewerVerViewModel = new CalPlanTableViewerVerViewModel(composite);
	calibrationPlanViewModel = calPlanTableViewerVerViewModel;
	return calPlanTableViewerVerViewModel;
    }

    protected void calStepPropertyGroup(Composite parent) {
	Group group = ControlFactory.getInstance().newGroup(parent, "数值范围", 8, 200);

	String[] calDacEnableFieldNameArr = new String[] { "adcMin", "adcMax", "meterMin", "meterMax" };
	for (String fieldName : calDacEnableFieldNameArr) {
	    Text text = ControlFactory.getInstance().newText(group, fieldName);
	    calPlanTableViewerVerViewModel.calDacViewModel.controlMappingObject.put(fieldName, text);
	}
	String[] calStepEnableFieldNameArr = new String[] { "pKMin", "pKMax", "pBMin", "pBMax", "adcKMin", "adcKMax",
		"adcBMin", "adcBMax", "checkAdcKMin", "checkAdcKMax", "checkAdcBmin", "checkAdcBmax" };
	for (String fieldName : calStepEnableFieldNameArr) {
	    Text text = ControlFactory.getInstance().newText(group, fieldName);
	    calPlanTableViewerVerViewModel.calStepViewModel.controlMappingObject.put(fieldName, text);
	}
    }
}
