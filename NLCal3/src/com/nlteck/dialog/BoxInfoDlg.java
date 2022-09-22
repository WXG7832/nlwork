package com.nlteck.dialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.swtlib.tools.UITools;
import com.nlteck.utils.CommonUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月18日 下午6:09:43 类说明
 */
public class BoxInfoDlg extends Dialog {

	private Text nameField;
	private Text ipField;
	private CCombo calboardCountField;
	private CCombo meterCountField;
	private Button sendBtn;
	private List<Text> meterIpFields = new ArrayList<>();
	private Text infoField;
	private CalBox calbox;
	private Composite container;
	private Composite calboardComposite;
	private Group group1;
	private Text screenIpField;
	// private Map<Integer, Boolean> calBoardEnable;

	private Composite boxPanel;
	private Composite calBoardPanel;
	private List<Button> calboardEnableChecks = new ArrayList<Button>();

	public BoxInfoDlg(Shell parentShell, CalBox box) {
		super(parentShell);
		this.calbox = box;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(calbox == null ? "新建校准箱" : calbox.getName() + "校准箱信息");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(380, 630);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		this.container = container;

		container.setLayout(new FormLayout());

		/** 校准箱信息 */
		createBoxInfo(container);
		/** 校准板状态 */
		createCalBoardInfo(container);
		/** 底部按钮 */
		createButton(container);

		return container;
	}

	private void createButton(Composite parent) {

		Composite btnsPanel = new Composite(parent, SWT.NONE);

		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 500);
		fd_scrolledComposite.bottom = new FormAttachment(calBoardPanel, 520);
		fd_scrolledComposite.left = new FormAttachment(0, 10);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		btnsPanel.setLayoutData(fd_scrolledComposite);
		btnsPanel.setLayout(new FillLayout());

		Composite btnComp = new Composite(btnsPanel, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(5).equalWidth(false).spacing(10, 5).margins(5, 15).applyTo(btnComp);

		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).span(2, 1).grab(true, false)
				.applyTo(new Label(btnComp, SWT.NONE));

		String optType = calbox == null ? "创建" : "修改";

		Button saveBtn = new Button(btnComp, SWT.NONE);
		saveBtn.setText(optType);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(100, SWT.DEFAULT).grab(false, false)
				.applyTo(saveBtn);

		if (calbox != null && calbox.getCalBoardCount() > 0) {

			sendBtn = new Button(btnComp, SWT.NONE);
			sendBtn.setText("下发");
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(100, SWT.DEFAULT).grab(false, false)
					.applyTo(sendBtn);

			/** 下发按钮事件 */
			sendBtn.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					sendBtn.setEnabled(false);

					/** 保存校准箱信息 */
					StringBuffer buf = new StringBuffer();
					if (!saveBoxInfo(buf)) {

						MyMsgDlg.openErrorDialog(getShell(), "操作失败", buf.toString());
						sendBtn.setEnabled(true);
						return;
					}

					/** 下发校准箱和校准板状态信息 */
					if (sendBaseConfig() && sendCalBoardState()) {
						MyMsgDlg.openInfoDialog(getShell(), "操作成功", "下发配置成功！！", false);

					}

					sendBtn.setEnabled(true);

				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

		}

		Button debugBtn = new Button(btnComp, SWT.NONE);
		debugBtn.setText("调试");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(100, SWT.DEFAULT).grab(false, false)
				.applyTo(debugBtn);

		/** 保存按钮事件 */
		saveBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				StringBuffer buf = new StringBuffer();
				if (!saveBoxInfo(buf)) {

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", buf.toString());
					return;
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", buf.toString(), false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		/** 调试按钮事件 */
		debugBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				container.getParent().getParent().dispose();
				
				DebugDlg dlg = new DebugDlg(Display.getDefault().getActiveShell(),calbox.getIp());
				dlg.create();
				UITools.centerScreen(dlg.getShell());
				dlg.open();
				
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	private void createCalBoardInfo(Composite parent) {

		calBoardPanel = new Composite(parent, SWT.NONE);

		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 400);
		fd_scrolledComposite.bottom = new FormAttachment(boxPanel, 500);
		fd_scrolledComposite.left = new FormAttachment(0, 10);
		fd_scrolledComposite.right = new FormAttachment(100, -10);
		calBoardPanel.setLayoutData(fd_scrolledComposite);
		calBoardPanel.setLayout(new FillLayout());

		if (calbox != null && calbox.getCalBoardCount() > 0) {

			Group group2 = new Group(calBoardPanel, SWT.NULL);
			group2.setText("校准板");

			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(10, 5).margins(5, 15)
					.applyTo(group2);

			CLabel label = new CLabel(group2, SWT.NONE);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
			label.setText("校准板状态:");

			if (calbox.getCalBoardCount() > 0) {

				calboardComposite = new Composite(group2, SWT.NONE);

				GridLayoutFactory.fillDefaults().numColumns(calbox.getCalBoardCount()).equalWidth(false)
						.spacing(12, SWT.DEFAULT).applyTo(calboardComposite);

				for (int i = 0; i < calbox.getCalBoardCount(); i++) {

					Button btn = new Button(calboardComposite, SWT.CHECK);
					btn.setText("" + (i + 1));

					List<CalBoard> calBoards = calbox.getCalBoardList();
					if (calBoards == null) {

						btn.setSelection(false);

					} else {

						btn.setSelection(calBoards.get(i).isOpen());
					}
					calboardEnableChecks.add(btn);

				}
			}

		}

	}

	private void createBoxInfo(Composite parent) {

		boxPanel = new Composite(parent, SWT.NONE);
		FormData fd_toolBarcomposite = new FormData();
		fd_toolBarcomposite.top = new FormAttachment(0, 0);
		fd_toolBarcomposite.bottom = new FormAttachment(0, 400);
		fd_toolBarcomposite.left = new FormAttachment(0, 10);
		fd_toolBarcomposite.right = new FormAttachment(100, -10);
		boxPanel.setLayoutData(fd_toolBarcomposite);
		boxPanel.setLayout(new FillLayout());

		Group group1 = new Group(boxPanel, SWT.NULL);
		group1.setText("校准箱");

		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(10, 5).margins(5, 15).applyTo(group1);
		CLabel label = new CLabel(group1, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("校准箱名:");

		nameField = new Text(group1, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(nameField);
		if (this.calbox != null) {

			nameField.setText(this.calbox.getName());
		}

		label = new CLabel(group1, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("校准箱IP:");

		ipField = new Text(group1, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(ipField);
		if (this.calbox != null) {

			ipField.setText(this.calbox.getIp());
		}

		label = new CLabel(group1, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("校准板数量:");

		calboardCountField = new CCombo(group1, SWT.BORDER | SWT.READ_ONLY);
		calboardCountField.setItems(new String[] { "1", "2", "3", "4" });
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(calboardCountField);

		calboardCountField.setEnabled(false);
		if (calbox != null) {

			this.calboardCountField.setText(calbox.getCalBoardCount() + "");
		} else {

			calboardCountField.select(-1);

		}

		label = new CLabel(group1, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("备注:");

		infoField = new Text(group1, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(infoField);
		if (this.calbox != null) {

			this.infoField.setText(calbox.getInfo() == null ? "" : calbox.getInfo());
		}

		// 液晶屏IP
		label = new CLabel(group1, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("液晶屏IP:");

		screenIpField = new Text(group1, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(screenIpField);
		if (this.calbox != null) {

			this.screenIpField.setText(calbox.getScreenIp() == null ? "" : calbox.getScreenIp());
		} else {

			this.screenIpField.setEnabled(false);
		}

		label = new CLabel(group1, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
		label.setText("万用表数量:");

		int count = calbox == null ? 0 : calbox.getMeterIps().size();
		meterCountField = new CCombo(group1, SWT.BORDER | SWT.READ_ONLY);
		meterCountField.setItems(new String[] { "1", "2" });
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
				.applyTo(meterCountField);
		meterCountField.select(count - 1);
		meterCountField.setEnabled(false);

		if (this.calbox != null) {

			this.meterCountField.setText(this.calbox.getMeterIps().size() + "");
		} else {

			this.meterCountField.setEnabled(false);
		}

		meterCountField.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				changeMetersCount(meterCountField.getSelectionIndex() + 1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		if (this.calbox != null) {

			List<String> ips = new ArrayList<>();
			for (int n = 0; n < count; n++) {

				label = new CLabel(group1, SWT.NONE);
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
				label.setText("万用表" + (n + 1) + " IP:");

				Text ipField = new Text(group1, SWT.BORDER);
				GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
						.applyTo(ipField);
				meterIpFields.add(ipField);

				if (calbox.getMeterIps().get(n) != null && CommonUtil.checkIP(calbox.getMeterIps().get(n))) {
					ipField.setText(calbox.getMeterIps().get(n));
					ips.add(calbox.getMeterIps().get(n));
				}

			}
			calbox.setMeterIps(ips);
		}

	}

	private void changeMetersCount(int count) {

		int metersCount = meterIpFields.size();
		if (count > metersCount) {

			for (int n = metersCount; n < count; n++) {

				CLabel label = new CLabel(group1, SWT.NONE);
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).grab(true, false).applyTo(label);
				label.setText("万用表" + (n + 1) + " IP:");

				Text ipField = new Text(group1, SWT.BORDER);
				GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).hint(160, SWT.DEFAULT)
						.applyTo(ipField);
				meterIpFields.add(ipField);
			}
		} else if (count < metersCount) {

			for (int n = count; n < metersCount; n++) {

				// 销毁
				meterIpFields.get(n).dispose();

				System.out.println("dispose " + n);
				Control control = findLabelControl("万用表" + (n + 1));
				if (control != null) {

					control.dispose();
				}

			}
			meterIpFields.subList(count, metersCount).clear();

		}

		group1.requestLayout();
	}

	private Control findLabelControl(String name) {

		for (Control control : group1.getChildren()) {

			if (control instanceof CLabel) {

				CLabel label = (CLabel) control;
				if (label.getText().contains(name)) {

					return label;
				}

			}

		}

		return null;

	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected void initializeBounds() {

		Composite composite = (Composite) getButtonBar();
		// super.createButton(composite, IDialogConstants.OK_ID, "修改", true);
		// super.createButton(composite, 1000, "修改", true);
		// super.createButton(composite, IDialogConstants.CANCEL_ID, "取消", false);

		super.initializeBounds();
	}

	@Override
	protected void okPressed() {

		StringBuffer buf = new StringBuffer();
		if (!saveBoxInfo(buf)) {

			MyMsgDlg.openErrorDialog(getShell(), "操作失败", buf.toString());
			return;
		}

		MyMsgDlg.openInfoDialog(getShell(), "操作成功", buf.toString(), false);
		// super.okPressed();
	}

	public boolean saveBoxInfo(StringBuffer buf) {

		String name = nameField.getText();
		if (CommonUtil.isNullOrEmpty(name)) {
			buf.append("设备名不能为空!");
			return false;
		}
		String ip = ipField.getText();
		if (!CommonUtil.checkIP(ip)) {
			buf.append("IP不合法!");
			return false;
		}

		String optType = calbox == null ? "创建" : "修改";

		List<String> ips = new ArrayList<String>();
		if (this.calbox != null) {

			for (Text text : meterIpFields) {

				String ipText = text.getText();
				if (!CommonUtil.checkIP(ipText)) {

					buf.append("IP" + ipText + "不合法");
					return false;

				}
				ips.add(ipText);
			}
		}

		String info = infoField.getText();
		String screenIp = screenIpField.getText();

		if (!CommonUtil.isNullOrEmpty(screenIp) && !CommonUtil.checkIP(screenIp)) {

			buf.append("液晶屏 IP : " + screenIp + "不合法");
			return false;

		}

		try {
			if (calbox == null) {

				calbox = new CalBox();
				calbox.setName(name);
				calbox.setIp(ip);
				calbox.setScreenIp(screenIp);

				WorkBench.getDatabaseManager().createCalbox(calbox);
				WorkBench.calBoxList.add(calbox);

			} else {

				// 更新校准箱
				WorkBench.getDatabaseManager().updateBox(calbox, name, ip, ips, screenIp, info);
				calbox.setName(name);
				calbox.setIp(ip);
				calbox.setMeterIps(ips);
				calbox.setInfo(info);

			}

		} catch (SQLException e) {

			e.printStackTrace();
			buf.append(optType + "校准箱失败:" + e.getMessage());
			return false;
		}

		buf.append(optType + "校准箱" + name + "成功!");

		return true;

	}

	public CalBox getCalbox() {
		return calbox;
	}

	private boolean sendCalBoardState() {

		StringBuilder stringBuilder = new StringBuilder();
		int calboardSize = calboardComposite.getChildren().length;

		byte flag = 0;
		for (int i = 0; i < calboardSize; i++) {
          
			boolean check = calboardEnableChecks.get(i).getSelection();
			
			if(check) {
			   flag = (byte) (flag | 0x01 << i);
			}
			calbox.getCalBoardList().get(i).setOpen(check);
			
		}

		try {
			WorkBench.getBoxService().configCalBoardState(calbox, flag);
			

		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "操作失败", "下发配置校准板状态失败!");
			return false;
		}

		return true;

	}

	private boolean sendBaseConfig() {

		try {
			WorkBench.getBoxService().configBaseInfo(calbox);
			System.out.println("下发配置校准箱信息成功！");
		} catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "操作失败", "下发配置校准箱信息失败!");
			return false;
		}

		return true;

	}

}
