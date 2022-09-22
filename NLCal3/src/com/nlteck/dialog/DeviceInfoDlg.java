package com.nlteck.dialog;

import java.sql.SQLException;
import java.util.Arrays;

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
import org.eclipse.swt.widgets.Text;

import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench;
import com.nlteck.firmware.WorkBench.DeviceType;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.utils.CommonUtil;


/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月17日 下午11:22:58 设备信息窗口
 */
public class DeviceInfoDlg extends Dialog {


	private Text nameField;
	private CCombo typeCombo;
	private Text  ipField;
	private CCombo logicCountField;
	private CCombo driverCountField;
	private CCombo driverChnCountField;
	private Text    infoField;
	private Device  device;

	public DeviceInfoDlg(Shell parentShell,  Device device) {
		super(parentShell);
		
		this.device = device;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(device == null ? "新建设备" : device.getName() + "设备信息");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(360, 460);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
        
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(10, 5).margins(5, 15).applyTo(container);
		CLabel label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("设备名:");
		
		nameField = new Text(container,SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(nameField);
		if(device != null) {
			
			nameField.setText(device.getName());
		}
		
		label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("IP:");
		
		ipField = new Text(container,SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(ipField);
        if(device != null) {
			
        	ipField.setText(device.getIp());
		}
		
		label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("设备类型:");
		
		typeCombo = new CCombo(container,SWT.BORDER | SWT.READ_ONLY);
		typeCombo.setItems(new String[] {DeviceType.POWERBOX.toString() , DeviceType.CAPACITY.toString()});
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(typeCombo);
		if(device == null) {
          typeCombo.select(0);
		} else {
			
			typeCombo.select(device.getType().ordinal());
			typeCombo.setEnabled(false);
		}
		
		label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("分区数:");
		
		logicCountField = new CCombo(container,SWT.BORDER | SWT.READ_ONLY);
		logicCountField.setItems(new String[] {"1" , "2" , "4"});
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(logicCountField);
		logicCountField.select(1);
		logicCountField.setEnabled(device == null);
		if(device != null) {
			
			logicCountField.setText(device.getLogicNum() + "");
		}
		
		label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("分区板数:");
		
		driverCountField = new CCombo(container,SWT.BORDER | SWT.READ_ONLY);
		String[] items = new String[32];
		for(int n = 1 ; n <= 32 ; n++) {
			
			items[n - 1] = n + "";
		}
		driverCountField.setItems(items);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(driverCountField);
		driverCountField.select(1);
		
		driverCountField.setEnabled(device == null);
        if(device != null) {
			
        	driverCountField.setText(device.getDriverNumInLogic() + "");
		}
		
		
		label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("板通道数:");
		
		driverChnCountField = new CCombo(container,SWT.BORDER | SWT.READ_ONLY);
		driverChnCountField.setItems(new String[] {"2","4","8" , "16","18"});
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(driverChnCountField);
		driverChnCountField.select(0);
		driverChnCountField.setEnabled(device == null);
        if(device != null) {
			
        	driverChnCountField.setText(device.getChnNumInDriver() + "");
		}
		
		label = new CLabel(container,SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("备注信息:");
		
		infoField = new Text(container,SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT).applyTo(infoField);
		
		
		return container;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {

		Composite composite = (Composite) getButtonBar();

		if (device == null) {
			super.createButton(composite, IDialogConstants.OK_ID, "创建", true);
			super.createButton(composite, IDialogConstants.CANCEL_ID, "取消", false);
		} else {
			
			super.createButton(composite, IDialogConstants.OK_ID, "修改", true);
			super.createButton(composite, IDialogConstants.CANCEL_ID, "取消", false);
		}

		super.initializeBounds();
	}
	
	
	@Override
	protected void okPressed() {
		
		
		String name = nameField.getText();
		if(CommonUtil.isNullOrEmpty(name)) {
			
			MyMsgDlg.openErrorDialog(getShell(), "操作失败", "设备名不能为空!");
			return;
		}
		String ip = ipField.getText();
		if(!CommonUtil.checkIP(ip)) {
			
			MyMsgDlg.openErrorDialog(getShell(), "操作失败", "IP不合法");
			return;
		}
		DeviceType type = null;
		String typeStr = typeCombo.getText();
		if(typeStr.equals(DeviceType.CAPACITY.toString())) {
			type = DeviceType.CAPACITY;
		} else if(typeStr.equals(DeviceType.POWERBOX.toString())) {
			
			type = DeviceType.POWERBOX;
		}
		
		
		int logicCount = Integer.parseInt(logicCountField.getText());
		int driverCount = Integer.parseInt(driverCountField.getText());
		int chnCountInDriver = Integer.parseInt(driverChnCountField.getText());
		String info = infoField.getText();
		try {
			
			if(device == null) {
			   device = WorkBench.getDatabaseManager().createDevice(name, ip,type ,logicCount, driverCount, chnCountInDriver);
			   WorkBench.deviceList.add(device);
			} else {
				
				WorkBench.getDatabaseManager().updateDevice(device, name, ip, info);
			}
			
		} catch (SQLException e) {
		    
			e.printStackTrace();
			MyMsgDlg.openErrorDialog(getShell(), "操作失败", "创建设备失败:" + e.getMessage());
			return;
		}
		
		
		MyMsgDlg.openInfoDialog(getShell(), "操作成功", "创建设备" + name + "成功!", false);
	
		super.okPressed();
	}

	public Device getDevice() {
		return device;
	}
	
	
	

}
