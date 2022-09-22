package com.nlteck.calSoftConfig.view.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;

import com.nlteck.calSoftConfig.controller.ControlFactory;
import com.nlteck.calSoftConfig.model.BaseConfig.AdcAdjust;
import com.nlteck.calSoftConfig.model.BaseConfig.CalBoardParam;
import com.nlteck.calSoftConfig.model.BaseConfig.CalculateValidate;
import com.nlteck.calSoftConfig.model.BaseConfig.IOParam;
import com.nlteck.calSoftConfig.model.BaseConfig.Network;
import com.nlteck.calSoftConfig.model.BaseConfig.Port;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.MappingObject;
import com.nlteck.calSoftConfig.viewModel.ViewModel;
import com.nlteck.calSoftConfig.viewModel.composite.BaseConfigViewModel;
import com.nlteck.swtlib.table.TableViewerEx;
import com.nlteck.swtlib.table.TableViewerEx.EditCtrlType;


/**
 * @description 基础参数控件
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:48:35 AM
 */
public class BaseConfigComposite extends ViewModelComposite {

    protected BaseConfigViewModel baseConfigViewModel;

    public BaseConfigComposite(Composite parent, Integer style, Object[] args) {
	super(parent, style, args);

    }

    protected void baseSettingGroup(Composite parent) {
	MappingObject mappingObject = baseConfigViewModel.controlMappingObject.getAsMappingObject("base");
	Group group = new Group(parent, SWT.NONE);
	group.setText("基础信息配置");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 100)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(8).equalWidth(false).margins(2, 2).applyTo(group);

	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "计量时忽略备份电压偏差");
	mappingObject.put("ignoreCV2", cLabel);

	cLabel = ControlFactory.getInstance().newCLabel(group, "校准备份电压");
	mappingObject.put("calCV2", cLabel);

	CCombo cCombo = ControlFactory.getInstance().newCCombo(group, "校准失败时切断回路", new String[] { "0", "1" });
	mappingObject.put("stopMode", cCombo);

	cLabel = ControlFactory.getInstance().newCLabel(group, "校准回检芯片");
	mappingObject.put("calCheckBoard", cLabel);

	cLabel = ControlFactory.getInstance().newCLabel(group, "仅校准回检芯片", "仅校准回检板，不校准驱动板");
	mappingObject.put("calCheckOnly", cLabel);

	Spinner spinner = ControlFactory.getInstance().newSpinner(group, "驱动板通道数");
	mappingObject.put("driverChnCount", spinner);

	cLabel = ControlFactory.getInstance().newCLabel(group, "校准失败时重试");
	mappingObject.put("useRecal", cLabel);

	cLabel = ControlFactory.getInstance().newCLabel(group, "所有计量点执行后皆关闭输出");
	mappingObject.put("measureNeedClose", cLabel);
    }

    protected void calBoardGroup(Composite parent) {

	Group group = new Group(parent, SWT.NONE);
	group.setText("校准板参数");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 150)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).margins(2, 2).applyTo(group);

	Spinner spinner = ControlFactory.getInstance().newSpinner(group, "校准板通道数");
	baseConfigViewModel.controlMappingObject.put("calChnCount", spinner);

	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "仅执行各工步首点");
	baseConfigViewModel.controlMappingObject.put("downFirstDotOnly", cLabel);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().span(4, 1).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 2).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	baseConfigViewModel.controlMappingObject.put("calboards", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("板序号");
	headers.add("通信方式");
	headers.add("通信地址序号");
	headers.add("万用表序号");
	headers.add("停用");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(150);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(1, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(2, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(3, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(4, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditRange(2, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(3, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditContent(1, "SERIAL", "NETWORK");
	tableViewerEx.setColumnEditContent(4, "true", "false");

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
		CalBoardParam item = (CalBoardParam) element;
		switch (columnIndex) {

		case 0:
		    text = item.index + "";
		    break;
		case 1:
		    text = item.commType.toString();
		    break;
		case 2:
		    text = item.commIndex + "";
		    break;
		case 3:
		    text = item.meterIndex + "";
		    break;
		case 4:
		    text = item.disabled + "";
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

		baseConfigViewModel.calBoardLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		CalBoardParam item = (CalBoardParam) tableViewerEx.getStructuredSelection().getFirstElement();
		baseConfigViewModel.calBoardLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

    }

    protected void measurementDeviationLimitGroup(Composite parent) {

	Group group = new Group(parent, SWT.NONE);
	group.setText("计量偏差上限");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 150)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(2, 2).applyTo(group);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	baseConfigViewModel.controlMappingObject.put("calculateValidates", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("量程起点");
	headers.add("量程终点");
	headers.add("ADC偏差上限");
	headers.add("万用表值偏差上限");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(0, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(1, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(2, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(3, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditRange(0, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(1, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(2, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(3, 0, Integer.MAX_VALUE);

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
		CalculateValidate item = (CalculateValidate) element;
		switch (columnIndex) {

		case 0:
		    text = item.min + "";
		    break;
		case 1:
		    text = item.max + "";
		    break;
		case 2:
		    text = item.adcOffset + "";
		    break;
		case 3:
		    text = item.meterOffset + "";
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

		baseConfigViewModel.calculateValidateLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		CalculateValidate item = (CalculateValidate) tableViewerEx.getStructuredSelection().getFirstElement();
		baseConfigViewModel.calculateValidateLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
    }

    protected void adjustDacBgroup(Composite parent) {

	MappingObject mappingObject = baseConfigViewModel.controlMappingObject.getAsMappingObject("kbAdjust");

	Group group = new Group(parent, SWT.NONE);
	group.setText("NG计量点, 调整dacB");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 50)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(8).equalWidth(false).margins(2, 2).applyTo(group);

	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "启用");
	mappingObject.put("enable", cLabel);

	Spinner spinner = ControlFactory.getInstance().newSpinner(group, "重试次数");
	mappingObject.put("count", spinner);
    }

    protected void fakeFinalAdcGroup(Composite parent) {

	Group group = new Group(parent, SWT.NONE);
	group.setText("修正最终ADC偏差");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 200)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(2, 2).applyTo(group);

	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "启用");
	baseConfigViewModel.controlMappingObject.getAsMappingObject("adjustParam").put("use", cLabel);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	baseConfigViewModel.controlMappingObject.getAsMappingObject("adjustParam").put("adcAdjusts", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("采样源");
	headers.add("工步类型");
	headers.add("极性");
	headers.add("量程档位");
	headers.add("触发值");
	headers.add("除数");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(0, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(1, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(2, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(3, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(4, EditCtrlType.TEXT);
	tableViewerEx.setColumnEditType(5, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditContent(0, "主芯片", "回检芯片");
	tableViewerEx.setColumnEditContent(1, "CC", "CV", "DC");
	tableViewerEx.setColumnEditContent(2, "POSITIVE", "NEGTIVE");
	tableViewerEx.setColumnEditRange(3, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditRange(5, 0, Integer.MAX_VALUE);

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
		AdcAdjust item = (AdcAdjust) element;
		switch (columnIndex) {

		case 0:
		    text = item.logic ? "主芯片" : "回检芯片";
		    break;
		case 1:
		    text = item.mode.toString();
		    break;
		case 2:
		    text = item.pole.toString();
		    break;
		case 3:
		    text = item.level + "";
		    break;
		case 4:
		    text = item.threshold + "";
		    break;
		case 5:
		    text = item.div + "";
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

		baseConfigViewModel.adcAdjustLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		AdcAdjust item = (AdcAdjust) tableViewerEx.getStructuredSelection().getFirstElement();
		baseConfigViewModel.adcAdjustLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
    }

    protected void meterSettingGroup(Composite parent) {

	Group group = new Group(parent, SWT.NONE);
	group.setText("万用表参数");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 150)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(2, 2).applyTo(group);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	baseConfigViewModel.controlMappingObject.put("meters", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("序号");
	headers.add("通信方式");
	headers.add("通信地址序号");
	headers.add("停用");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(1, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(2, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(3, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditContent(1, "SERIAL", "NETWORK");
	tableViewerEx.setColumnEditRange(2, 0, Integer.MAX_VALUE);
	tableViewerEx.setColumnEditContent(3, "true", "false");

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
		IOParam item = (IOParam) element;
		switch (columnIndex) {

		case 0:
		    text = item.index + "";
		    break;
		case 1:
		    text = item.commType.toString();
		    break;
		case 2:
		    text = item.commIndex + "";
		    break;
		case 3:
		    text = item.disabled + "";
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

		baseConfigViewModel.meterLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		IOParam item = (IOParam) tableViewerEx.getStructuredSelection().getFirstElement();
		baseConfigViewModel.meterLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
    }

    protected void protocolSettingGroup(Composite parent) {

	MappingObject mappingObject = baseConfigViewModel.controlMappingObject.getAsMappingObject("protocol");
	Group group = new Group(parent, SWT.NONE);
	group.setText("通信协议参数");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 50)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(8).equalWidth(false).margins(2, 2).applyTo(group);

	CLabel cLabel = ControlFactory.getInstance().newCLabel(group, "控制台打印报文");
	mappingObject.put("print", cLabel);

	cLabel = ControlFactory.getInstance().newCLabel(group, "通道反序", "软件中显示的通道号与报文中相反");
	mappingObject.put("chnOrderResverse", cLabel);

	Spinner spinner = ControlFactory.getInstance().newSpinner(group, "膜片数量");
	mappingObject.put("moduleCount", spinner);
    }

    protected void serialPortSettingGroup(Composite parent) {
	Group group = new Group(parent, SWT.NONE);
	group.setText("串口参数");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 150)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(2, 2).applyTo(group);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	baseConfigViewModel.controlMappingObject.put("ports", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("序号");
	headers.add("名称");
	headers.add("波特率");
	headers.add("停用");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(1, EditCtrlType.TEXT);
	tableViewerEx.setColumnEditType(2, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(3, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditContent(2, "19200", "38400", "115200");
	tableViewerEx.setColumnEditContent(3, "true", "false");

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
		Port item = (Port) element;
		switch (columnIndex) {

		case 0:
		    text = item.index + "";
		    break;
		case 1:
		    text = item.name;
		    break;
		case 2:
		    text = item.baudrate + "";
		    break;
		case 3:
		    text = item.disabled + "";
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

		baseConfigViewModel.serialPortLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		Port item = (Port) tableViewerEx.getStructuredSelection().getFirstElement();
		baseConfigViewModel.serialPortLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
	    }
	});
    }

    protected void ethernetPortSettingGroup(Composite parent) {
	Group group = new Group(parent, SWT.NONE);
	group.setText("网口参数");
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, 150)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(2, 2).applyTo(group);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(composite);

	TableViewerEx tableViewerEx = ControlFactory.getInstance().newTableViewerEx(composite);
	baseConfigViewModel.controlMappingObject.put("networks", tableViewerEx);

	List<String> headers = new ArrayList<>();
	headers.add("序号");
	headers.add("IP");
	headers.add("端口");
	headers.add("停用");
	headers.add("异步");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewerEx.setHeaders(headers, widths);

	tableViewerEx.setColumnEditType(1, EditCtrlType.TEXT);
	tableViewerEx.setColumnEditType(2, EditCtrlType.SPINNER);
	tableViewerEx.setColumnEditType(3, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditType(4, EditCtrlType.COMBO);
	tableViewerEx.setColumnEditRange(2, 1, 65535);
	tableViewerEx.setColumnEditContent(3, "true", "false");
	tableViewerEx.setColumnEditContent(4, "true", "false");

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
		Network item = (Network) element;
		switch (columnIndex) {

		case 0:
		    text = item.index + "";
		    break;
		case 1:
		    text = item.ip;
		    break;
		case 2:
		    text = item.port + "";
		    break;
		case 3:
		    text = item.disabled + "";
		    break;
		case 4:
		    text = item.async + "";
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

		baseConfigViewModel.ethernetPortLstAdd();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		Network item = (Network) tableViewerEx.getStructuredSelection().getFirstElement();
		baseConfigViewModel.ethernetPortLstDel(item);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
    }

    @Override
    protected ViewModel createViewModel(ViewModelComposite composite) {
	baseConfigViewModel = new BaseConfigViewModel(composite);
	return baseConfigViewModel;
    }

    @Override
    protected void createControl(Composite parent) {
	baseSettingGroup(parent);
	protocolSettingGroup(parent);
	serialPortSettingGroup(parent);
	ethernetPortSettingGroup(parent);
	calBoardGroup(parent);
	meterSettingGroup(parent);
	fakeFinalAdcGroup(parent);
	measurementDeviationLimitGroup(parent);
	adjustDacBgroup(parent);
    }

}
