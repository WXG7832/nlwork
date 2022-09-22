package com.nlteck.calSoftConfig.controller;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calSoftConfig.view.tableViewer.TableViewerExAutoProvide;
import com.nlteck.swtlib.table.TableViewerEx;


/**
 * @description 控件工厂
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:55:51 AM
 */
public class ControlFactory {

    private static ControlFactory instance;
    protected Image SWITCHER_ON_BLUE = new Image(Display.getCurrent(), "icons/switch-yes.png");
    protected Image SWITCHER_OFF_WHITE = new Image(Display.getCurrent(), "icons/switch-no.png");
    public Image ADD_ITEM_IMAGE = new Image(Display.getCurrent(), "icons/schemaConfig/add_32.png");
    public Image DEL_ITEM_IMAGE = new Image(Display.getCurrent(), "icons/schemaConfig/del_32.png");
    protected GridDataFactory customLabelLayoutDataFactory;
    protected GridDataFactory customControlLayoutDataFactory;
    protected boolean useDefaultLayoutData = true;

    public static ControlFactory getInstance() {
	if (instance == null) {
	    instance = new ControlFactory();
	}
	return instance;
    }

    public Group newGroup(Composite parent, String description, int columnNum, int yHint) {
	Group group = new Group(parent, SWT.NONE);
	group.setText(description);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, yHint)
		.applyTo(group);
	GridLayoutFactory.fillDefaults().numColumns(columnNum).equalWidth(false).margins(2, 2).applyTo(group);
	return group;
    }

    public CLabel newCLabel(Composite parent, String description, String toolTip) {
	Label label = new Label(parent, SWT.NONE);
	label.setLayoutData(getLabelLayoutData());
	label.setText(description);

	CLabel cLabel = new CLabel(parent, SWT.NONE);
	cLabel.setLayoutData(getControlLayoutData());
	cLabel.setImage(SWITCHER_OFF_WHITE);
	cLabel.setToolTipText(toolTip);
	cLabel.setCursor(new Cursor(null, SWT.CURSOR_HAND));
	cLabel.setData(false);
	cLabel.addMouseListener(new MouseListener() {

	    @Override
	    public void mouseUp(MouseEvent e) {

		if (e.button == 1) {

		    boolean open = (Boolean) cLabel.getData();

		    cLabel.setData(!open);

		    cLabel.setImage(!open ? SWITCHER_ON_BLUE : SWITCHER_OFF_WHITE);

		}

	    }

	    @Override
	    public void mouseDown(MouseEvent e) {

	    }

	    @Override
	    public void mouseDoubleClick(MouseEvent e) {

	    }
	});

	return cLabel;
    }

    public CLabel newCLabel(Composite parent, String description) {
	return newCLabel(parent, description, description);
    }

    public CCombo newCCombo(Composite parent, String description, String[] selectionArr, String tooltip) {
	Label label = new Label(parent, SWT.NONE);
	label.setLayoutData(getLabelLayoutData());
	label.setText(description);
	label.setToolTipText(tooltip);

	CCombo cCombo = new CCombo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
	cCombo.setLayoutData(getControlLayoutData());
	for (String selection : selectionArr) {

	    cCombo.add(selection);
	}
	cCombo.setText(selectionArr[0]);

	return cCombo;
    }

    public CCombo newCCombo(Composite parent, String description, String[] selectionArr) {
	return newCCombo(parent, description, selectionArr, description);
    }

    public CCombo newCCombo(Composite parent, String description, String tooltip) {
	return newCCombo(parent, description, new String[] { "true", "false" }, tooltip);
    }

    public Spinner newSpinner(Composite parent, String description) {
	Label label = new Label(parent, SWT.NONE);
	label.setLayoutData(getLabelLayoutData());
	label.setText(description);

	Spinner spinner = new Spinner(parent, SWT.BORDER);
	spinner.setLayoutData(getControlLayoutData());
	spinner.setValues(0, 0, Integer.MAX_VALUE, 0, 1, 5);

	return spinner;
    }

    public Text newText(Composite parent, String description) {
	Label label = new Label(parent, SWT.NONE);
	label.setLayoutData(getLabelLayoutData());
	label.setText(description);

	Text text = new Text(parent, SWT.BORDER);
	text.setLayoutData(getControlLayoutData());
	return text;
    }

    public TableViewerEx newTableViewerEx(Composite parent) {
	TableViewerEx tableViewerEx = new TableViewerEx(parent, true, false);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewerEx.getTable());
	return tableViewerEx;
    }

    public TableViewerExAutoProvide newTableViewerExAutoProvide(Composite parent) {
	TableViewerExAutoProvide tableViewer = new TableViewerExAutoProvide(parent, true, false);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());
	return tableViewer;
    }

    /**
     * @description 表格控件的工具栏: 添加/删除
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 10:04:21 AM
     */
    public Composite newTableViewerToolbar(TableViewerExAutoProvide tableViewer) {
	Composite opt = new Composite(tableViewer.getTable().getParent(), SWT.NONE);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(80, SWT.DEFAULT).grab(false, true).applyTo(opt);
	GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(5, 5).applyTo(opt);

	Button btn = newButton(opt, "增加");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		try {
		    tableViewer.addRow();
		} catch (Exception e1) {

		    DialogFactory.getInstance().errorDialog(e1);
		}

	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	btn = newButton(opt, "删除");
	btn.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		tableViewer.delSelectRow();
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	return opt;
    }

    public Button newButton(Composite parent, String description) {
	Button btn = new Button(parent, SWT.PUSH);
	GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(70, SWT.DEFAULT).applyTo(btn);
	btn.setText(description);
	return btn;
    }

    public void newSeparator(Composite parent, String description) {
	Composite composite = new Composite(parent, SWT.NONE);
	composite.setLayout(new FillLayout(SWT.VERTICAL));
	Label label = new Label(composite, SWT.None);
	label.setText(description);
	label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    }

    protected GridData getLayoutData(boolean useDefaultLayoutData, boolean labelOrControl) {
	GridData gridData = null;
	if (useDefaultLayoutData) {
	    GridData defaultGridData = null;
	    if (labelOrControl) {
		defaultGridData = GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).indent(20, 0).create();
	    } else {
		defaultGridData = GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).create();
	    }
	    gridData = defaultGridData;
	} else {
	    if (labelOrControl) {
		gridData = customLabelLayoutDataFactory.create();
	    } else {
		gridData = customControlLayoutDataFactory.create();
	    }
	}
	return gridData;
    }

    protected GridData getLabelLayoutData() {
	return getLayoutData(useDefaultLayoutData, true);
    }

    protected GridData getControlLayoutData() {
	return getLayoutData(useDefaultLayoutData, false);
    }

    public void setLayoutDataFactory(GridDataFactory labelLayoutDataFactory, GridDataFactory controlLayoutDataFactory) {
	customLabelLayoutDataFactory = labelLayoutDataFactory;
	customControlLayoutDataFactory = controlLayoutDataFactory;
	useDefaultLayoutData = false;
    }

    public void useDefautLayoutData() {
	useDefaultLayoutData = true;
    }
}
