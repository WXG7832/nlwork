package com.nlteck.calSoftConfig.view.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;

import com.nlteck.calModel.base.I18N;
import com.nlteck.calSoftConfig.controller.ControlFactory;
import com.nlteck.calSoftConfig.model.DelayConfig.DetailConfig;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.MappingObject;
import com.nlteck.calSoftConfig.viewModel.ViewModel;
import com.nlteck.calSoftConfig.viewModel.composite.DelayConfigViewModel;
import com.nlteck.swtlib.table.TableViewerEx;
import com.nlteck.swtlib.table.TableViewerEx.EditCtrlType;

//import com.nlteck.controller.ControlFactory;
//import com.nlteck.model.DelayConfig.DetailConfig;
//import com.nlteck.swtlib.table.TableViewerEx;
//import com.nlteck.swtlib.table.TableViewerEx.EditCtrlType;
//import com.nlteck.util.I18N;
//import com.nlteck.view.ViewModelComposite;
//import com.nlteck.viewModel.MappingObject;
//import com.nlteck.viewModel.ViewModel;
//import com.nlteck.viewModel.composite.DelayConfigViewModel;

public class DelayConfigComposite extends ViewModelComposite {

    protected DelayConfigViewModel delayConfigViewModel;

    public DelayConfigComposite(Composite parent, int style, Object[] args) {
	super(parent, style, args);
    }

    @Override
    protected void createControl(Composite parent) {

	globalSettingGroup(parent);
	stepSettingGroup(parent);
    }

    @Override
    protected ViewModel createViewModel(ViewModelComposite composite) {
	delayConfigViewModel = new DelayConfigViewModel(composite);
	return delayConfigViewModel;
    }

    protected void globalSettingGroup(Composite parent) {
	MappingObject globalSettingMappingObject = delayConfigViewModel.controlMappingObject
		.getAsMappingObject("defaultConfig");
	Group group = ControlFactory.getInstance().newGroup(parent, "全局参数", 8, 100);

	Spinner spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_relayON));
	globalSettingMappingObject.put("moduleOpenDelay", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_relayOFF));
	globalSettingMappingObject.put("moduleCloseDelay", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_switchStep));
	globalSettingMappingObject.put("modeChangeDelay", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group, "下发CC/DC工步的DAC后");
	globalSettingMappingObject.put("programSetDelay", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group, "下发CV工步的DAC后");
	globalSettingMappingObject.put("programSetDelayCV2", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_readMeter));
	globalSettingMappingObject.put("readMeterDelay", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_switchMeter));
	globalSettingMappingObject.put("turnOnMeterDelay", spinner);
	globalSettingMappingObject.put("turnOffMeterDelay", spinner);

	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "测试点执行后关闭膜片");
	globalSettingMappingObject.put("dotClose", cLabel);

    }

    protected void stepSettingGroup(Composite parent) {
	Group group = ControlFactory.getInstance().newGroup(parent, "工步参数", 1, 200);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	viewModel.controlMappingObject.put("detailConfigs", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("工步");
	headers.add("极性");
	headers.add("档位");
	headers.add("测试点执行后关闭膜片");
	headers.add("下发CC/DC工步的DAC后");
	headers.add("下发CV工步的DAC后");
	headers.add("膜片开启后");
	headers.add("膜片关闭后");
	headers.add("读万用表前");
	headers.add("串入万用表后");
	headers.add("断开万用表后");
	headers.add("工步变化后");
	List<Integer> widths = new ArrayList<>();
	widths.add(50);
	widths.add(100);
	widths.add(50);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(120);
	widths.add(120);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(0, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(1, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(2, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(3, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(4, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(5, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(6, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(7, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(8, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(9, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(10, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(11, EditCtrlType.SPINNER);

	tableViewerEx.setColumnEditContent(0, "CC", "CV", "DC");
	tableViewerEx.setColumnEditContent(1, "POSITIVE", "NEGTIVE");
	tableViewerEx.setColumnEditRange(2, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditContent(3, "true", "false");
	tableViewerEx.setColumnEditRange(4, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(5, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(6, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(7, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(8, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(9, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(10, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(11, 0, Integer.MAX_VALUE);
	tableViewerEx.setLabelProvider(new ITableLabelProvider() {

	    @Override
	    public void addListener(ILabelProviderListener listener) {

	    }

	    @Override
	    public void dispose() {

	    }

	    @Override
	    public boolean isLabelProperty(Object element, String property) {

		return false;
	    }

	    @Override
	    public void removeListener(ILabelProviderListener listener) {

	    }

	    @Override
	    public Image getColumnImage(Object element, int columnIndex) {

		return null;
	    }

	    @Override
	    public String getColumnText(Object element, int columnIndex) {

		String text = "";
		DetailConfig item = (DetailConfig) element;
		switch (columnIndex) {

		case 0:
		    text = item.mode.toString();
		    break;
		case 1:
		    text = item.pole.toString();
		    break;
		case 2:
		    text = item.precision + "";
		    break;
		case 3:
		    text = item.dotClose + "";
		    break;
		case 4:
		    text = item.programSetDelay + "";
		    break;
		case 5:
		    text = item.programSetDelayCV2 + "";
		    break;
		case 6:
		    text = item.moduleOpenDelay + "";
		    break;
		case 7:
		    text = item.moduleCloseDelay + "";
		    break;
		case 8:
		    text = item.readMeterDelay + "";
		    break;
		case 9:
		    text = item.turnOnMeterDelay + "";
		    break;
		case 10:
		    text = item.turnOffMeterDelay + "";
		    break;
		case 11:
		    text = item.modeChangeDelay + "";
		    break;

		}

		return text;
	    }

	});

	Composite opt = new Composite(composite, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(80, SWT.DEFAULT).grab(false, true).applyTo(opt);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(5, 5).applyTo(opt);

	Button btn = ControlFactory.getInstance().newButton(opt, "增加");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		delayConfigViewModel.stepSettingLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		DetailConfig item = (DetailConfig) tableViewerEx.getStructuredSelection().getFirstElement();
		delayConfigViewModel.stepSettingLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

    }
}
