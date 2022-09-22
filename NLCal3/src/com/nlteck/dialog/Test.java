package com.nlteck.dialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.nlteck.utils.UIUtil;

public class Test {

    protected Shell shell;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
	try {
	    Test window = new Test();
	    window.open();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Open the window.
     */
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

    /**
     * Create contents of the window.
     */
    protected void createContents() {
	shell = new Shell();
	shell.setSize(700, 649);
	shell.setText("SWT Application");
	shell.setLayout(new FillLayout(SWT.HORIZONTAL));

	Image image = SWTResourceManager.getImage("C:\\Users\\caichao_tang\\Downloads\\start_cali_32.png");

	Composite composite = new Composite(shell, SWT.NONE);

	composite.addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		UIUtil.drawAwesomeBattery(gc, new Rectangle(0, 0, 90, 160), SWTResourceManager.getColor(146, 239, 150), false);
	    }
	});

    }
}
