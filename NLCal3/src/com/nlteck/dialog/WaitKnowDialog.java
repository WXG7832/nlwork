package com.nlteck.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

import com.nlteck.firmware.WorkBench;
import com.nlteck.model.CalData;
import com.nlteck.model.TestData;
import com.nlteck.utils.UIUtil;
import com.nltecklib.utils.CvsUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * 已知时间的等待弹窗
 * 
 * @author caichao_tang
 *
 */
public class WaitKnowDialog extends Dialog {
	protected Object result;
	protected Shell shell;
	private int seconds;
	private int count;
	private String info;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public WaitKnowDialog(Shell parent, int style, int seconds, String info) {
		super(parent, style);
		this.seconds = seconds;
		this.info = info;
		setText("搜索设备");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.APPLICATION_MODAL);
		shell.setAlpha(200);
		shell.setSize(500, 52);
		shell.setText(getText());
		UIUtil.setShellAlignCenter(shell);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		ProgressBar progressBar = new ProgressBar(composite, SWT.SMOOTH);
		progressBar.setMaximum(seconds);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label infoLabel = new Label(composite, SWT.NONE);
		infoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		infoLabel.setText(info);

		new Thread(() -> {
			count = 0;
			for (int i = 0; i < seconds; i++) {
				count++;
				Display.getDefault().asyncExec(() -> {
					progressBar.setSelection(count);
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Display.getDefault().asyncExec(() -> {
				shell.dispose();
			});
		}).start();
	}

	/**
	 * 打开一个规定等待秒数的弹窗，计时完成自动关闭
	 * 
	 * @param shell
	 * @param style
	 * @param seconds
	 * @param info
	 */
	public static void openDialog(Shell shell, int style, int seconds, String info) {
		new Thread(() -> {
			Display.getDefault().asyncExec(() -> {
				new WaitKnowDialog(shell, style, seconds, info).open();
			});
		}).start();
	}
}
