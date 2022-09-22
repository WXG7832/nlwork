
package com.nlteck.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.nlteck.dialog.WaitKnowDialog;
import com.nltecklib.io.broadcast.BroadcastManager;
import com.nltecklib.io.broadcast.BroadcastPackage;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class SearchDeviceHandller {
	private static final int TIME_OUT = 5;
	private List<BroadcastPackage> broadcastPackageList;
	private String ip = null;

	@Execute
	public void execute(Shell shell) {
		WaitKnowDialog.openDialog(new Shell(), SWT.NONE, 5, "搜索中...");
		try {
			ip = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String[] ips = ip.split("\\.");
		ips[3] = 255 + "";
		String ipArea = ips[0] + "." + ips[1] + "." + ips[2] + "." + ips[3];
		// 网络搜索线程
		new Thread(() -> {
			try {
				BroadcastManager broadcastManager = new BroadcastManager();
				broadcastPackageList = broadcastManager.sendBroadcaseAndRecv(ip, ipArea, TIME_OUT * 1000);
				if (broadcastPackageList.size() == 0) {
					Display.getDefault().asyncExec(() -> {
						MessageDialog.openWarning(shell, "警告", "该网段未搜索到任何设备！");
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}

}