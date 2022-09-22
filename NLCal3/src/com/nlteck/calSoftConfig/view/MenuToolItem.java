package com.nlteck.calSoftConfig.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @description 菜单型工具栏按钮
 * @author zemin_zhu
 * @dateTime Jul 11, 2022 11:39:50 AM
 */
public class MenuToolItem extends ToolItem {

    protected DropdownSelectionListener listener;

    public MenuToolItem(ToolBar parent) {
	super(parent, SWT.DROP_DOWN);
	listener = new DropdownSelectionListener(this);
	this.addSelectionListener(listener);
    }

    public void addMenuItem(String title, SelectionListener selectionListener) {
	listener.add(title, selectionListener);
    }

    public Menu getMenu() {
	return listener.getMenu();
    }

    public class DropdownSelectionListener extends SelectionAdapter {
	private ToolItem toolItem;

	private Menu menu;

	public DropdownSelectionListener(ToolItem dropdown) {
	    this.toolItem = dropdown;
	    menu = new Menu(dropdown.getParent().getShell());
	}

	public void add(String title, SelectionListener selectionListener) {
	    MenuItem menuItem = new MenuItem(menu, SWT.NONE);
	    menuItem.setText(title);
	    menuItem.addSelectionListener(selectionListener);
	}

	public void widgetSelected(SelectionEvent event) {
	    if (event.detail == SWT.ARROW) {
		ToolItem item = (ToolItem) event.widget;
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	    } else {
		System.out.println(toolItem.getText() + " Pressed");
	    }
	}

	public Menu getMenu() {
	    return menu;
	}
    }

    @Override
    protected void checkSubclass() {
    }
}
