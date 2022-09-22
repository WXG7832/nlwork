package com.nlteck.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.firmware.Device;
import com.nlteck.table.CheckListTableViewer;
import com.nlteck.table.CheckListTableViewer.CheckItem;
import com.nltecklib.protocol.li.MBWorkform.MBSelfTestInfo.CheckInfo;
import com.nltecklib.protocol.li.MBWorkform.MBSelfTestInfo.DriverInfo;
import com.nltecklib.protocol.li.MBWorkform.MBSelfTestInfo.LogicInfo;
import com.nltecklib.protocol.li.PCWorkform.PCSelfTestInfoData;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年2月9日 上午9:15:40 校准设备自检窗口
 */
public class CheckListDlg extends Dialog {

	private PCSelfTestInfoData data;
	private Device device;
	private CheckListTableViewer tableViewer;

	public CheckListDlg(Shell parentShell, Device device, PCSelfTestInfoData data) {
		super(parentShell);
		this.device = device;
		this.data = data;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(device.getName() + "自检信息");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 660);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(1).applyTo(container);
		tableViewer = new CheckListTableViewer(container);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());
		refreshData();
		return container;
	}

	/**
	 * 刷新表格数据
	 * 
	 * @author wavy_zheng 2021年2月9日
	 */
	public void refreshData() {

		List<CheckItem> list = new ArrayList<>();

		List<DriverInfo> logicDrivers = new ArrayList<>();
		List<DriverInfo> checkDrivers = new ArrayList<>();
		for (int n = 0; n < data.getSelfTestInfo().logicInfos.size(); n++) {

			LogicInfo li = data.getSelfTestInfo().logicInfos.get(n);
			CheckItem ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "烧录APP程序";
			ci.itemResult = li.appMode ? "OK" : "NG";
			ci.err = !li.appMode;
			list.add(ci);

			ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "APP程序版本";
			ci.itemResult = li.version;
			list.add(ci);

			ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "AD1";
			ci.itemResult = li.ad1OK ? "OK" : "NG";
			ci.err = !li.ad1OK;
			list.add(ci);

			ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "AD2";
			ci.itemResult = li.ad2OK ? "OK" : "NG";
			ci.err = !li.ad2OK;
			list.add(ci);

			ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "AD3";
			ci.itemResult = li.ad3OK ? "OK" : "NG";
			ci.err = !li.ad3OK;
			list.add(ci);

			ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "FLASH";
			ci.itemResult = li.flashOK ? "OK" : "NG";
			ci.err = !li.flashOK;
			list.add(ci);

			ci = new CheckItem();
			ci.item = "逻辑板" + (n + 1) + "SRAM";
			ci.itemResult = li.sramOK ? "OK" : "NG";
			ci.err = !li.sramOK;
			list.add(ci);

			logicDrivers.addAll(li.driverInfos);
		}

		for (int n = 0; n < data.getSelfTestInfo().checkInfos.size(); n++) {

			CheckInfo ci = data.getSelfTestInfo().checkInfos.get(n);
			CheckItem item = new CheckItem();
			item.item = "回检板" + (n + 1) + "烧录APP程序";
			item.itemResult = ci.appMode ? "OK" : "NG";
			item.err = !ci.appMode;
			list.add(item);

			item = new CheckItem();
			item.item = "回检板" + (n + 1) + "APP程序版本";
			item.itemResult = ci.version;
			list.add(item);

			item = new CheckItem();
			item.item = "回检板" + (n + 1) + "AD";
			item.itemResult = ci.adOK ? "OK" : "NG";
			item.err = !ci.adOK;
			list.add(item);

			item = new CheckItem();
			item.item = "回检板" + (n + 1) + "FLASH";
			item.itemResult = ci.flashOK ? "OK" : "NG";
			item.err = !ci.flashOK;
			list.add(item);

			item = new CheckItem();
			item.item = "回检板" + (n + 1) + "ADC FLASH";
			item.itemResult = ci.flashParamOK ? "OK" : "NG";
			item.err = !ci.flashParamOK;
			list.add(item);

			checkDrivers.addAll(ci.driverInfos);

		}

		for (int n = 0; n < logicDrivers.size(); n++) {

			DriverInfo di = logicDrivers.get(n);

			CheckItem item = new CheckItem();
			item.item = "驱动板" + (n + 1) + "逻辑APP程序烧录";
			item.itemResult = di.appMode ? "OK" : "NG";
			item.err = !di.appMode;
			list.add(item);

			item = new CheckItem();
			item.item = "驱动板" + (n + 1) + "逻辑程序版本";
			item.itemResult = di.version;
			list.add(item);

			if (n < checkDrivers.size()) {
				
				di = checkDrivers.get(n);
				
				item = new CheckItem();
				item.item = "驱动板" + (n + 1) + "回检APP程序烧录";
				item.itemResult = di.appMode ? "OK" : "NG";
				item.err = !di.appMode;
				list.add(item);
				
				item = new CheckItem();
				item.item = "驱动板" + (n + 1) + "回检程序版本";
				item.itemResult = di.version;
				list.add(item);

			}

		}

		tableViewer.setInput(list);
		tableViewer.refresh();
	}

}
