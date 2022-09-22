package com.nlteck.calSoftConfig.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.calSoftConfig.controller.ControlFactory;
import com.nlteck.calSoftConfig.view.tableViewer.TableViewerExAutoProvide;
import com.nlteck.calSoftConfig.viewModel.InnerCalPlanDialogViewModel;

public class InnerCalPlanDialog extends Dialog {

    protected InnerCalPlanDialogViewModel viewModel;

    public InnerCalPlanDialog(Shell shell, InnerCalPlanDialogViewModel viewModel) {
	super(shell);
	this.viewModel = viewModel;
    }

    @Override
    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("内置方案");
    }

    @Override
    protected Point getInitialSize() {
	return new Point(400, 250);
    }

    @Override
    protected Control createContents(Composite parent) {
	Group group = ControlFactory.getInstance().newGroup(parent, "内置方案", 2, 200);

	Composite composite = new Composite(group, SWT.NONE);
	GridDataFactory.fillDefaults().span(4, 1).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
	GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 2).applyTo(composite);

	TableViewerExAutoProvide tableViewer = new TableViewerExAutoProvide(composite, true, false);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());
	viewModel.controlMappingObject.put("innerCalPlanLst", tableViewer);
	List<String> headers = new ArrayList<>();
	headers.add("方案名");
	headers.add("修改时间");
	headers.add("路径");
	List<Integer> widths = new ArrayList<>();
	widths.add(100);
	widths.add(100);
	widths.add(100);
	tableViewer.setHeaders(headers, widths);

	try {
	    tableViewer.setInputNotTableExItem(viewModel.model.innerCalPlanLst);
	    tableViewer.setEnabledFieldNameArr(new String[] { "name", "modifiedTime", "path" });
	    tableViewer.setLabelProviderAutomatic();

	} catch (Exception e) {
	    viewModel.exceptionReceiver("内置校准方案表格控件, 赋值数据源发生异常", e);
	}

	Composite tableViewerToolbar = new Composite(composite, SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(80, SWT.DEFAULT).grab(false, true)
		.applyTo(tableViewerToolbar);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(5, 5).applyTo(tableViewerToolbar);

	Button btn = ControlFactory.getInstance().newButton(tableViewerToolbar, "导入");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		viewModel.importBtnClickEvent();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(tableViewerToolbar, "增加");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		viewModel.addBtnClickEvent();

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(tableViewerToolbar, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		viewModel.delBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(tableViewerToolbar, "保存");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		viewModel.saveBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = ControlFactory.getInstance().newButton(tableViewerToolbar, "改名");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		viewModel.renameBtnClickEvent();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});
	viewModel.refreshControl();
	return group;
    }
}
