package com.nlteck.parts;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthLookAndFeel;

import org.apache.poi.sl.draw.Drawable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;
import org.h2.command.ddl.CreateAggregate;

import com.nlteck.resources.Resources;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.ConfigDecorator;
import com.nltecklib.protocol.li.QueryDecorator;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.CalDot;
import com.nltecklib.protocol.li.calTools.CalToolsEnvironment.Pole;
import com.nltecklib.protocol.li.calTools.logic.CalToolsLogicCalculateFactorData;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseEvent;

public class TestUI {

    protected Shell shell;

    public static void main(String[] args) {
	try {
	    TestUI window = new TestUI();
	    window.open();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void open() {
	Display display = Display.getDefault();
	createContents();
	shell.open();
	shell.layout();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
    }

    protected void createContents() {
	shell = new Shell();
	shell.setSize(500, 500);
	shell.setText("SWT TEST");
	shell.setLayout(new FillLayout(SWT.HORIZONTAL));

	Composite composite = new Composite(shell, SWT.NONE);
	composite.addMouseTrackListener(new MouseTrackAdapter() {
		@Override
		public void mouseHover(MouseEvent e) {
		}
	});

	Menu menu = new Menu(composite);
	composite.setMenu(menu);

	MenuItem menuItem = new MenuItem(menu, SWT.NONE);
	menuItem.setText("123");
	
	MenuItem mntmNewItem_1 = new MenuItem(menu, SWT.CASCADE);
	mntmNewItem_1.setText("New Item");
	
	Menu menu_1 = new Menu(mntmNewItem_1);
	mntmNewItem_1.setMenu(menu_1);

    }
}
