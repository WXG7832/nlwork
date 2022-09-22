package com.nlteck.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nltecklib.protocol.li.PCWorkform.CalTempControlDebugData;

/**
* @author  wavy_zheng
* @version 创建时间：2022年2月13日 上午10:47:19
* 校准板恒温控制窗口
*/
public class CalTempControlDlg extends Dialog {

	private Spinner  tempControl;
	private CLabel   tempSwitch;
	private CalBox   calbox;
	
	private boolean open;
	
	public CalTempControlDlg(Shell parentShell , CalBox box , boolean open) {
		super(parentShell);
		this.calbox = box;
		this.open = open;
		
		
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("校准板恒温控制窗口");
	}

	@Override
	protected Point getInitialSize() {

		return new Point(400, 200);
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(container);
		Label title = new Label(container, SWT.NONE);
		title.setText("恒温开关");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		tempSwitch = new CLabel(container, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(tempSwitch);
		tempSwitch.setImage(open ? Resources.SWITCH_ON_IMAGE : Resources.SWITCH_OFF_IMAGE);
		tempSwitch.setData(open);
		tempSwitch.setCursor(Resources.handCursor);
		tempSwitch.setToolTipText("打开或关闭校准板恒温开关");
		tempSwitch.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {

				if (e.button != 1) {

					return;
				}
				boolean open = (Boolean) tempSwitch.getData();
				tempSwitch.setData(!open);
				tempSwitch.setImage(!open ? Resources.SWITCH_ON_IMAGE : Resources.SWITCH_OFF_IMAGE);

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		title = new Label(container, SWT.NONE);
		title.setText("恒温温度");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		tempControl = new Spinner(container, SWT.BORDER);
		tempControl.setValues(42, 0, 100, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(tempControl);
		
		return container;
	}
	
	@Override
	protected void okPressed() {
		
		
		CalTempControlDebugData data = new CalTempControlDebugData();
		data.setDriverIndex(0);
		data.setOpen((Boolean)tempSwitch.getData());
		data.setTemperature(tempControl.getSelection());
		try {
			WorkBench.getBoxService().cfgTempControl(calbox, data);
			
			open = data.isOpen();
		} catch (Exception e1) {

			e1.printStackTrace();

			MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

			return;
		}

		MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置恒温参数成功", false);
		
		
		super.okPressed();
	}


	public boolean isOpen() {
		return open;
	}


	public void setOpen(boolean open) {
		this.open = open;
	}
	
	
	

}
