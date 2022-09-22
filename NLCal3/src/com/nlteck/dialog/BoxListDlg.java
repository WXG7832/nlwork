package com.nlteck.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench;
import com.nlteck.swtlib.tools.MyMsgDlg;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月18日 下午9:50:27 类说明
 */
public class BoxListDlg extends Dialog {

	private CCombo combo;
	private Device device;
	private CalBox bindBox;

	public BoxListDlg(Shell parentShell , Device device) {
		super(parentShell);
        this.device = device;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("校准箱列表");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(280, 200);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(10, 5).margins(5, 15)
				.applyTo(container);
		CLabel label = new CLabel(container, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("校准箱列表:");

		combo = new CCombo(container, SWT.BORDER | SWT.DROP_DOWN);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(combo);

		for (CalBox box : WorkBench.calBoxList) {
			combo.add(box.getName());
		}

		return container;

	}

	@Override
	protected void initializeBounds() {

		Composite composite = (Composite) getButtonBar();

		super.createButton(composite, IDialogConstants.OK_ID, "绑定", true);
		super.createButton(composite, IDialogConstants.CANCEL_ID, "取消", false);

		super.initializeBounds();
	}
	
	@Override
	protected void okPressed() {
		
		String boxName = combo.getText();
		for (CalBox box : WorkBench.calBoxList) {
			
			if(box.getName().equals(boxName)) {
				
				
				try {
					WorkBench.getDatabaseManager().bindBoxToDevice(device, box);
					device.bindCalbox(box);
					bindBox = box;
					WorkBench.calBoxList.remove(box); //未绑定列表移除
					
					MyMsgDlg.openInfoDialog(getShell(), "绑定成功", "已成功将" + box.getName() + "绑定到设备" + device.getName(), false);
					
					
				} catch (Exception e) {
					
					e.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "绑定失败:" + e.getMessage());
					
				}
				
				
				super.okPressed();
				return;
				
			}
		}
		
		MyMsgDlg.openErrorDialog(getShell(), "操作失败", "绑定失败!");
		
		
		
	}

	public CalBox getBindBox() {
		return bindBox;
	}
	
	
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

}
