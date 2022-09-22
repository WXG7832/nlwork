package com.nlteck.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calModel.model.NetworkCalMianBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.progress.ShowProcessDialog;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.device.KeySight34461A;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.PCWorkform.CalCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalResistanceDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalTempControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrate2DebugData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.RelayControlExDebugData;
import com.nltecklib.protocol.li.PCWorkform.RelaySwitchDebugData;
import com.nltecklib.protocol.li.PCWorkform.ResistanceModeRelayDebugData;
import com.nltecklib.protocol.li.cal.CalEnvironment;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkPattern;
import com.nltecklib.protocol.li.cal.CalEnvironment.WorkState;
import com.nltecklib.protocol.li.cal.RelayControlData;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.power.driver.DriverCalculateData.ReadonlyAdcData;
import com.nltecklib.protocol.power.driver.DriverCalibrateData;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年2月7日 上午9:12:06 类说明
 */
public class DebugDlg extends Dialog {

	private CLabel switchLabel;
	private CLabel enableLabel; // 使能开关
	private Button moduleConnectBtn;
	private Spinner chnField;

	private Button connectBoxBtn;

	private Cursor handCursor = new Cursor(null, SWT.CURSOR_HAND);
	private CalBox calBox;

	private final static String POSITIVE = "+";
	private final static String NEGTIVE = "-";

	public DebugDlg(Shell parentShell, String boxIp) {
		super(parentShell);

		calBox = new CalBox();
		calBox.setName("debig");
		calBox.setIp(boxIp);
		calBox.setMeterIp("192.168.1.168");

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("调试窗口");
	}

	@Override
	protected Point getInitialSize() {

		return new Point(1224, 900);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(1).margins(5, 5).applyTo(container);
		createTopPanel(container);

		Composite mainPanel = new Composite(container, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(mainPanel);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(mainPanel);
		
		
		
		createModuleDebugPanel(mainPanel);
		createCalboardDebugPanel(mainPanel);
		createMeterDebugPanel(mainPanel);

		/**
		 * 关闭连接和资源
		 */
		container.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				try {
					WorkBench.getBoxService().connectDevice(calBox, "192.168.1.127", false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				WorkBench.getBoxService().disconnect(calBox);

			}

		});

		return container;
	}

	private Composite createTopPanel(Composite parent) {

		Composite panel = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(10).equalWidth(false).applyTo(panel);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, 48).grab(true, false)
				.applyTo(panel);

		Label title = new Label(panel, SWT.NONE);
		title.setText("校准箱IP:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Text ipField = new Text(panel, SWT.BORDER);
		ipField.setText(calBox.getIp());
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(120, SWT.DEFAULT).applyTo(ipField);

		connectBoxBtn = new Button(panel, SWT.PUSH);
		connectBoxBtn.setText("连接");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(80, SWT.DEFAULT).applyTo(connectBoxBtn);
		
		connectBoxBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				final ShowProcessDialog spd = new ShowProcessDialog(Display.getDefault().getActiveShell());
				spd.open("正在连接校准箱,请稍后...");
				String ip = ipField.getText();
				if (!CommonUtil.checkIP(ip)) {

					MyMsgDlg.openErrorDialog(getShell(), "连接失败", "ip地址" + ip + "不符合规范");
					return;
				}
				calBox.setIp(ip);

				final String btnText = connectBoxBtn.getText();

				new Thread(new Runnable() {

					@Override
					public void run() {

						boolean operate = false;
						if (btnText.equals("连接")) {
							operate = WorkBench.getBoxService().connectBoxDebug(calBox);
						} else {
							WorkBench.getBoxService().disconnect(calBox);
							operate = true;
						}

						final boolean operateFinal = operate;
						Display.getDefault().syncExec(new Runnable() {

							@Override
							public void run() {

								spd.close();

								if (operateFinal) {

									if (btnText.equals("连接")) {

										connectBoxBtn.setText("断开");
										MyMsgDlg.openInfoDialog(getShell(), "操作成功", "已连接校准箱", false);
									} else {

										connectBoxBtn.setText("连接");
										MyMsgDlg.openInfoDialog(getShell(), "操作成功", "已断开校准箱", false);
									}

								} else {

									MyMsgDlg.openErrorDialog(getShell(), "操作失败", "连接或断开校准箱失败");
								}

							}

						});

					}

				}).start();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		
		/**
		 * 万用表ip
		 */
		Label meterIP=new Label(panel, SWT.NONE);
		meterIP.setText("万用表ip");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(meterIP);
		Text text_meterIP=new Text(panel, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(120, SWT.DEFAULT).applyTo(text_meterIP);
		text_meterIP.setText("192.168.1.58");
		
		
		Button readMeter=new Button(panel, SWT.NONE);
		readMeter.setText("读万用表");
		
		readMeter.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Meter meter = new KeySight34461A(0);
				double meterVal;
				try {
					meter.setIpAddress(text_meterIP.getText());
					meter.connect();
					meterVal=meter.ReadSingleClearBuffer();
					meter.disconnect();
					MyMsgDlg.openInfoDialog(getParentShell(), "success", "表值： " + meterVal , false);
				}catch (Exception e1) {
					MyMsgDlg.openInfoDialog(getParentShell(), "fail", e1.getMessage() , false);
				}

			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		
		
		return panel;

	}

	/**
	 * 校准板2调试 450A
	 * 
	 * @param mainPanel
	 */
	private void createMeterDebugPanel(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText("校准板调试2");
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2).margins(10, 5).applyTo(group);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).hint(300, SWT.DEFAULT).grab(false, true)
				.applyTo(group);

		Label meterIP=new Label(group, SWT.NONE);
		meterIP.setText("万用表ip");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(meterIP);
		Text text_meterIP=new Text(group, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(120, SWT.DEFAULT).applyTo(text_meterIP);
		text_meterIP.setText("192.168.1.168");
		
		Label title = new Label(group, SWT.NONE);
		title.setText("板地址");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner driverField = new Spinner(group, SWT.BORDER);
		driverField.setValues(1, 1, 4, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(driverField);

		Label label = new Label(group, SWT.NONE);
		label.setText("程控继电器");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		Combo combo = new Combo(group, SWT.NONE);
		String[] str = new String[] { "1", "2" };
		combo.setItems(str);
		combo.select(0);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(combo);

		Label label_model = new Label(group, SWT.NONE);
		label_model.setText("工作模式");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label_model);

		Combo modeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (CalEnvironment.WorkMode calMode : CalEnvironment.WorkMode.values()) {
			modeCombo.add(calMode.name());
		}
		modeCombo.setText(CalMode.CC.name());

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(modeCombo);

		// 量程
		title = new Label(group, SWT.NONE);
		title.setText("量程");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner rangeField = new Spinner(group, SWT.BORDER);
		rangeField.setValues(0, 0, 4, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(rangeField);

		// 电阻系数
		title = new Label(group, SWT.NONE);
		title.setText("电阻系数");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner resistanceField = new Spinner(group, SWT.BORDER);
		resistanceField.setValues((int) (500 * Math.pow(10, 6)), 0, Integer.MAX_VALUE, 6, (int) Math.pow(10, 6),
				(int) Math.pow(10, 7));
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(resistanceField);

		// 读表值
		title = new Label(group, SWT.NONE);
		title.setText("表值");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);
		Text text = new Text(group, SWT.WRAP | SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(120, SWT.DEFAULT).applyTo(text);

		Button resistQuery = new Button(group, SWT.NONE);
		resistQuery.setText("查询电阻系数");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(resistQuery);

		Button resistCfg = new Button(group, SWT.NONE);
		resistCfg.setText("配置电阻系数");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(resistCfg);

		resistQuery.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ResistanceModeRelayDebugData query = new ResistanceModeRelayDebugData();
				query.setDriverIndex(driverField.getSelection() - 1);
				query.setRelayIndex((byte) combo.getSelectionIndex());
				query.setWorkPattern(WorkPattern.valueOf(modeCombo.getText()));
				query.setRange(rangeField.getSelection());

				try {
					double resistivity = WorkBench.calboxService.queryResistances(calBox, query).getResistance();
					MyMsgDlg.openInfoDialog(getParentShell(), "成功", "电阻系数： " + resistivity, false);

				} catch (Exception e1) {
					e1.printStackTrace();
					MyMsgDlg.openInfoDialog(getParentShell(), "失败", "查询失败:" + e1.getMessage(), false);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		resistCfg.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ResistanceModeRelayDebugData cfg = new ResistanceModeRelayDebugData();
				cfg.setDriverIndex(driverField.getSelection() - 1);
				cfg.setRelayIndex((byte) combo.getSelectionIndex());
				cfg.setWorkPattern(WorkPattern.valueOf(modeCombo.getText()));
				cfg.setRange(rangeField.getSelection());
				cfg.setResistance(resistanceField.getSelection());

				boolean ok=MyMsgDlg.openConfirmDialog(getParentShell(), "确认操作", "确认配置", false);
				if(!ok) {
					return;
				}
				try {
					WorkBench.calboxService.cfgResistances(calBox, cfg);
					MyMsgDlg.openInfoDialog(getShell(), "操作成功", "成功", false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MyMsgDlg.openInfoDialog(getShell(), "操作失败", "配置电阻系数失败:" + e1.getMessage(), false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button open = new Button(group, SWT.NONE);
		open.setText("开程控继电器");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(open);
		Button close = new Button(group, SWT.NONE);
		close.setText("关程控继电器");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(close);
		open.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(combo.getSelectionIndex());

				RelayControlExDebugData relayControlExDebugData = new RelayControlExDebugData();
				relayControlExDebugData.setDriverIndex(0);
				relayControlExDebugData.setRelayIndex((byte) combo.getSelectionIndex());
				relayControlExDebugData.setConnected(true);
				try {
					WorkBench.getBoxService().cfgMeterRelaySwitch(calBox, relayControlExDebugData);
				} catch (Exception e1) {
					MyMsgDlg.openErrorDialog(getShell(), "操作成功", "失败" + e1.getMessage(), false, false);
					e1.printStackTrace();
					return;
				}
				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "成功", false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		close.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelayControlExDebugData relayControlExDebugData = new RelayControlExDebugData();
				relayControlExDebugData.setDriverIndex(driverField.getSelection() - 1);
				relayControlExDebugData.setRelayIndex((byte) combo.getSelectionIndex());
				relayControlExDebugData.setConnected(false);
				try {
					WorkBench.getBoxService().cfgMeterRelaySwitch(calBox, relayControlExDebugData);
				} catch (Exception e1) {
					MyMsgDlg.openErrorDialog(getShell(), "操作成功", "失败" + e1.getMessage(), false, false);
					e1.printStackTrace();
					return;
				}
				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "成功", false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		// ============读表==========

		Button readmeter1 = new Button(group, SWT.NONE);
		readmeter1.setText("读表1");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(readmeter1);
		Button readmeter2 = new Button(group, SWT.NONE);
		readmeter2.setText("读表2");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(readmeter2);
		readmeter1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					double resistivity = 1;
					calBox.setMeterIp(text_meterIP.getText());
					if (!modeCombo.getText().equals("CV")) {

						ResistanceModeRelayDebugData query = new ResistanceModeRelayDebugData();
						query.setDriverIndex(0);
						query.setRelayIndex((byte) 0);
						query.setWorkPattern(WorkPattern.valueOf(modeCombo.getText()));
						query.setRange(rangeField.getSelection());
						resistivity = WorkBench.calboxService.queryResistances(calBox, query).getResistance();
					}
					NetworkCalMianBoard board = new NetworkCalMianBoard(calBox);
					double meterVal = board.readMeter();
					board.disconnect();
					text.setText(meterVal * resistivity+"");
					MyMsgDlg.openInfoDialog(getParentShell(), "success", "表值： " + meterVal * resistivity, false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MyMsgDlg.openInfoDialog(getParentShell(), "失败", "查询失败:" + e1.getMessage(), false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		readmeter2.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					calBox.setMeterIp(text_meterIP.getText());
					double resistivity = 1;
					if (!modeCombo.getText().equals("CV")) {

						ResistanceModeRelayDebugData query = new ResistanceModeRelayDebugData();
						query.setDriverIndex(0);
						query.setRelayIndex((byte) 1);
						query.setWorkPattern(WorkPattern.valueOf(modeCombo.getText()));
						query.setRange(rangeField.getSelection());
						resistivity = WorkBench.calboxService.queryResistances(calBox, query).getResistance();
					}

					NetworkCalMianBoard board = new NetworkCalMianBoard(calBox);

					double meterVal = board.readMeter();
//					text.setText(meterVal * resistivity+"");
					MyMsgDlg.openInfoDialog(getParentShell(), "success", "表值： " + meterVal * resistivity, false);

				} catch (Exception e1) {
					e1.printStackTrace();
					MyMsgDlg.openInfoDialog(getParentShell(), "失败", "查询失败:" + e1.getMessage(), false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}

	/**
	 * 创建校准板调试窗口
	 * 
	 * @author wavy_zheng 2022年2月8日
	 * @param parent
	 * @return
	 */
	private Composite createCalboardDebugPanel(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText("校准板调试");
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2).margins(10, 5).applyTo(group);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).hint(300, SWT.DEFAULT).grab(false, true)
				.applyTo(group);

		Label title = new Label(group, SWT.NONE);
		title.setText("板地址");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner driverField = new Spinner(group, SWT.BORDER);
		driverField.setValues(1, 1, 4, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(driverField);

		// 通道
		title = new Label(group, SWT.NONE);
		title.setText("通道号");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner chnField = new Spinner(group, SWT.BORDER);
		chnField.setValues(1, 1, 72, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(chnField);

		// 使能开关
		title = new Label(group, SWT.NONE);
		title.setText("校准开关");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		CLabel calSwitch = new CLabel(group, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(calSwitch);
		calSwitch.setImage(Resources.SWITCH_OFF_IMAGE);
		calSwitch.setData(false);
		calSwitch.setCursor(handCursor);
		calSwitch.setToolTipText("打开或关闭校准板开关");
		calSwitch.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {

				if (e.button != 1) {

					return;
				}

				try {

					boolean open = (Boolean) calSwitch.getData();
					calSwitch.setData(!open);
					calSwitch.setImage(!open ? Resources.SWITCH_ON_IMAGE : Resources.SWITCH_OFF_IMAGE);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

				}

			}

		});

		// 模式
		title = new Label(group, SWT.NONE);
		title.setText("模式:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Combo modeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (CalEnvironment.WorkMode calMode : CalEnvironment.WorkMode.values()) {
			modeCombo.add(calMode.name());
		}
		modeCombo.setText(CalMode.CC.name());

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(modeCombo);

		// 量程
		title = new Label(group, SWT.NONE);
		title.setText("量程");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner rangeField = new Spinner(group, SWT.BORDER);
		rangeField.setValues(0, 0, 4, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(rangeField);

		// 极性
		title = new Label(group, SWT.NONE);
		title.setText("极性:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Combo poleCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		poleCombo.add(NEGTIVE);
		poleCombo.add(POSITIVE);
		poleCombo.setText(POSITIVE);

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(poleCombo);

		// 量程
		title = new Label(group, SWT.NONE);
		title.setText("程控电压");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner programVField = new Spinner(group, SWT.BORDER);
		programVField.setValues(60000, 0, 65535, 0, 1, 1000);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(programVField);

		// 程控值
		title = new Label(group, SWT.NONE);
		title.setText("程控电流");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner programIField = new Spinner(group, SWT.BORDER);
		programIField.setValues(60000, 0, 65535, 0, 1, 1000);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(programIField);

		// 计量值
		title = new Label(group, SWT.NONE);
		title.setText("计量值(mA/mV)");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner dotField = new Spinner(group, SWT.BORDER);
		dotField.setValues(100000, 0, Integer.MAX_VALUE, 2, 100, 1000);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(dotField);

		// 电阻系数
		title = new Label(group, SWT.NONE);
		title.setText("电阻系数");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner resistanceField = new Spinner(group, SWT.BORDER);
		resistanceField.setValues((int) (500 * Math.pow(10, 6)), 0, Integer.MAX_VALUE, 6, (int) Math.pow(10, 6),
				(int) Math.pow(10, 7));
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(programIField);

		title = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).grab(true, false).applyTo(title);

		// 万用表切表地址
		title = new Label(group, SWT.NONE);
		title.setText("切表");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner meterField = new Spinner(group, SWT.BORDER);
		meterField.setValues(1, 1, 2, 0, 1, 1);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(meterField);

		Button meterBtn = new Button(group, SWT.PUSH);
		meterBtn.setText("对接表");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(meterBtn);
		meterBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					CalRelayControlDebugData relay = new CalRelayControlDebugData();
					relay.setDriverIndex(meterField.getSelection() - 1);
					relay.setConnected(true);
					WorkBench.getBoxService().cfgMeterChange(calBox, relay);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "对接表" + meterField.getSelection() + "成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		meterBtn = new Button(group, SWT.PUSH);
		meterBtn.setText("断开表");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(meterBtn);
		meterBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					CalRelayControlDebugData relay = new CalRelayControlDebugData();
					relay.setDriverIndex(meterField.getSelection() - 1);
					relay.setConnected(false);
					WorkBench.getBoxService().cfgMeterChange(calBox, relay);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "断开表" + meterField.getSelection() + "成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		title = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).grab(true, false).applyTo(title);

		title = new Label(group, SWT.NONE);
		title.setText("恒温开关");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		CLabel tempSwitch = new CLabel(group, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(tempSwitch);
		tempSwitch.setImage(Resources.SWITCH_OFF_IMAGE);
		tempSwitch.setData(false);
		tempSwitch.setCursor(handCursor);
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

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		title = new Label(group, SWT.NONE);
		title.setText("恒温温度");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner tempField = new Spinner(group, SWT.BORDER);
		tempField.setValues(42, 0, 100, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(tempField);

		Button btnCfgTemp = new Button(group, SWT.PUSH);
		btnCfgTemp.setText("配置恒温");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(btnCfgTemp);
		btnCfgTemp.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalTempControlDebugData data = new CalTempControlDebugData();
				data.setDriverIndex(driverField.getSelection() - 1);
				data.setOpen(true);
				data.setTemperature(tempField.getSelection());
				try {
					WorkBench.getBoxService().cfgTempControl(calBox, data);
				} catch (Exception e1) {

					e1.printStackTrace();

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

					return;
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置恒温参数成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		Button btnReadTemp = new Button(group, SWT.PUSH);
		btnReadTemp.setText("写入电阻系数");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(btnReadTemp);
		btnReadTemp.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalResistanceDebugData data = new CalResistanceDebugData();
				data.setDriverIndex(driverField.getSelection() - 1);
				data.setWorkPattern(WorkPattern.valueOf(modeCombo.getText()));
				data.setRange(rangeField.getSelection());
				data.setResistance((double) resistanceField.getSelection() / Math.pow(10, 6));
				try {
					WorkBench.getBoxService().cfgResistance(calBox, data);
				} catch (Exception e1) {

					e1.printStackTrace();

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

					return;
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置校准点成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		title = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).grab(true, false).applyTo(title);

		// 配置
		Button btn = new Button(group, SWT.PUSH);
		btn.setText("配置校准");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(btn);
		btn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalCalibrate2DebugData data = new CalCalibrate2DebugData();
				data.setDriverIndex(driverField.getSelection() - 1);
				data.setChnIndex(chnField.getSelection() - 1);
				data.setWorkState((Boolean) calSwitch.getData() ? WorkState.WORK : WorkState.UNWORK);
				data.setWorkMode(CalEnvironment.WorkMode.valueOf(modeCombo.getText()));
				data.setPrecision(rangeField.getSelection());
				data.setPole(poleCombo.getText().equals(POSITIVE) ? PoleData.Pole.NORMAL : PoleData.Pole.REVERSE);
				data.setProgramV(programVField.getSelection());
				data.setProgramI(programIField.getSelection());
				try {
					WorkBench.getBoxService().cfgCalboardCalibrate(calBox, data);
				} catch (Exception e1) {

					e1.printStackTrace();

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

					return;
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置校准点成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		btn = new Button(group, SWT.PUSH);
		btn.setText("配置计量");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(btn);
		btn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalCalculate2DebugData data = new CalCalculate2DebugData();
				data.setDriverIndex(driverField.getSelection() - 1);
				data.setChnIndex(chnField.getSelection() - 1);
				data.setWorkState((Boolean) calSwitch.getData() ? WorkState.WORK : WorkState.UNWORK);
				data.setWorkMode(CalEnvironment.WorkMode.valueOf(modeCombo.getText()));
				data.setPrecision(rangeField.getSelection());
				// data.setPole(PoleData.Pole.values()[poleCombo.getSelectionIndex()]);
				data.setPole(poleCombo.getText().equals(POSITIVE) ? PoleData.Pole.NORMAL : PoleData.Pole.REVERSE);
				System.out.println(dotField.getSelection());
				data.setCalculateDot(dotField.getSelection()/100);
				try {
					WorkBench.getBoxService().cfgCalboardCalculate(calBox, data);
				} catch (Exception e1) {

					e1.printStackTrace();

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

					return;
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置计量点成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		btn = new Button(group, SWT.PUSH);
		btn.setText("电阻系数");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(btn);
		btn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalResistanceDebugData query = new CalResistanceDebugData();
				query.setDriverIndex(driverField.getSelection() - 1);
				query.setWorkPattern(WorkPattern.values()[modeCombo.getSelectionIndex()]);
				query.setRange(rangeField.getSelection());
				try {
					query = WorkBench.getBoxService().readNewResistanceDebug(calBox, query);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());
				}

				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "电阻系数:" + query.getResistance(), false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		Button relay_on = new Button(group, SWT.NONE);
		relay_on.setText("开继电器");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(relay_on);
		relay_on.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				RelaySwitchDebugData query = new RelaySwitchDebugData();
				query.setDriverIndex(driverField.getSelection() - 1);
				query.setChannel((byte) chnField.getSelection());
				try {
					WorkBench.getBoxService().cfgRelaySwitch(calBox, query);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());
				}
				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置成功", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button relay_off = new Button(group, SWT.NONE);
		relay_off.setText("关继电器");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(relay_off);
		relay_off.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelaySwitchDebugData query = new RelaySwitchDebugData();
				query.setDriverIndex(driverField.getSelection() - 1);
				query.setChannel((byte) 0);
				try {
					WorkBench.getBoxService().cfgRelaySwitch(calBox, query);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());
				}
				MyMsgDlg.openInfoDialog(getShell(), "操作成功", "配置成功", false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		return group;
	}

	/**
	 * 创建模片调试窗口
	 * 
	 * @author wavy_zheng 2022年2月7日
	 * @param parent
	 * @return
	 */
	private Composite createModuleDebugPanel(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText("模片调试");

		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(3).margins(10, 5).applyTo(group);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).hint(400, SWT.DEFAULT).grab(false, true)
				.applyTo(group);

		Label title = new Label(group, SWT.NONE);
		title.setText("设备IP");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Text ipField = new Text(group, SWT.BORDER);
		ipField.setText("192.168.1.127");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(120, SWT.DEFAULT).applyTo(ipField);

		moduleConnectBtn = new Button(group, SWT.PUSH);
		moduleConnectBtn.setText("连接");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(80, SWT.DEFAULT).applyTo(moduleConnectBtn);
		moduleConnectBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				final ShowProcessDialog spd = new ShowProcessDialog(Display.getDefault().getActiveShell());
				spd.open("正在连接校准箱,请稍后...");
				final String ip = ipField.getText();
				if (!CommonUtil.checkIP(ip)) {

					MyMsgDlg.openErrorDialog(getShell(), "连接失败", "ip地址" + ip + "不符合规范");
					return;
				}
				final String btnText = moduleConnectBtn.getText();

				new Thread(new Runnable() {

					@Override
					public void run() {

						final StringBuffer errMsg = new StringBuffer();
						try {
							WorkBench.getBoxService().connectDevice(calBox, ip, btnText.equals("连接"));
						} catch (Exception e) {

							errMsg.append(e.getMessage());
							e.printStackTrace();
						}
						Display.getDefault().syncExec(new Runnable() {

							@Override
							public void run() {

								spd.close();
								moduleConnectBtn.setText(btnText.equals("连接") ? "断开" : "连接");
								if (errMsg.length() == 0) {

									MyMsgDlg.openInfoDialog(getShell(), "操作成功", "已" + btnText + "设备主控", false);
								} else {

									MyMsgDlg.openErrorDialog(getShell(), "操作失败", "连接设备主控失败:" + errMsg.toString());
								}

							}

						});

					}

				}).start();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		title = new Label(group, SWT.NONE);
		title.setText("校准开关");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		switchLabel = new CLabel(group, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).span(2, 1)
				.applyTo(switchLabel);
		switchLabel.setImage(Resources.SWITCH_OFF_IMAGE);
		switchLabel.setData(false);
		switchLabel.setCursor(handCursor);
		switchLabel.setToolTipText("打开或关闭校准模式");
		switchLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {

				if (e.button != 1) {

					return;
				}

				try {

					boolean open = (Boolean) switchLabel.getData();
					WorkBench.getBoxService().changeWorkMode(calBox,
							!open ? CalibrateCoreWorkMode.CAL : CalibrateCoreWorkMode.NONE);
					switchLabel.setData(!open);
					switchLabel.setImage(!open ? Resources.SWITCH_ON_IMAGE : Resources.SWITCH_OFF_IMAGE);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

				}

			}

		});

		// 使能开关
		title = new Label(group, SWT.NONE);
		title.setText("使能开关");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		enableLabel = new CLabel(group, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).span(2, 1)
				.applyTo(enableLabel);
		enableLabel.setImage(Resources.SWITCH_OFF_IMAGE);
		enableLabel.setData(false);
		enableLabel.setCursor(handCursor);
		enableLabel.setToolTipText("打开或关闭模片使能");
		enableLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseUp(MouseEvent e) {

				if (e.button != 1) {

					return;
				}

				try {

					boolean open = (Boolean) enableLabel.getData();
					WorkBench.getBoxService().cfgModuleSwitch(calBox, chnField.getSelection() - 1, !open);
					enableLabel.setData(!open);
					enableLabel.setImage(!open ? Resources.SWITCH_ON_IMAGE : Resources.SWITCH_OFF_IMAGE);

				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());

				}

			}

		});

		// 通道
		title = new Label(group, SWT.NONE);
		title.setText("通道:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		chnField = new Spinner(group, SWT.BORDER);
		chnField.setValues(1, 1, 128, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).span(2, 1).applyTo(chnField);

		// 模片
		title = new Label(group, SWT.NONE);
		title.setText("模片:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner moduleField = new Spinner(group, SWT.BORDER);
		moduleField.setValues(1, 0, 20, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(moduleField);

		title = new Label(group, SWT.NONE);
		title.setText("0表示不选中模片");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		// 模式
		title = new Label(group, SWT.NONE);
		title.setText("模式:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Combo modeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (CalMode calMode : CalMode.values()) {
			modeCombo.add(calMode.name());
		}
		modeCombo.setText(CalMode.CC.name());

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).span(2, 1).applyTo(modeCombo);

		// 极性
		title = new Label(group, SWT.NONE);
		title.setText("极性:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Combo poleCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		poleCombo.add(NEGTIVE);
		poleCombo.add(POSITIVE);

		poleCombo.setText(POSITIVE);

		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).span(2, 1).applyTo(poleCombo);

		// 极性
		title = new Label(group, SWT.NONE);
		title.setText("量程:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner rangeField = new Spinner(group, SWT.BORDER);
		rangeField.setValues(0, 0, 4, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(rangeField);

		title = new Label(group, SWT.NONE);
		title.setText("量程越大精度越高");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		// 程控电压
		title = new Label(group, SWT.NONE);
		title.setText("程控电压:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner programVField = new Spinner(group, SWT.BORDER);
		programVField.setValues(60000, 0, 65535, 0, 1, 1000);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(programVField);

		title = new Label(group, SWT.NONE);
		title.setText("0-65535量化值");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		// 程控电压
		title = new Label(group, SWT.NONE);
		title.setText("程控电流:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner programIField = new Spinner(group, SWT.BORDER);
		programIField.setValues(60000, 0, 65535, 0, 1, 1000);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(programIField);

		title = new Label(group, SWT.NONE);
		title.setText("0-65535量化值");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		// 计量值
		title = new Label(group, SWT.NONE);
		title.setText("计量值:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner calculateField = new Spinner(group, SWT.BORDER);
		calculateField.setValues(0, 0, Integer.MAX_VALUE, 2, 100, 1000);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(calculateField);

		title = new Label(group, SWT.NONE);
		title.setText("mV/mA");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		// 程控电压
		title = new Label(group, SWT.NONE);
		title.setText("采样个数");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(title);

		Spinner sampleField = new Spinner(group, SWT.BORDER);
		sampleField.setValues(30, 0, 100, 0, 1, 10);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(sampleField);

		title = new Label(group, SWT.NONE);
		title.setText("单次采样个数");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		// 操控
		title = new Label(group, SWT.NONE);
		title.setText("校准:");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		Button calCfgbtn = new Button(group, SWT.PUSH);
		calCfgbtn.setText("下发");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(calCfgbtn);
		calCfgbtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				
				LogicCalibrate2DebugData data = new LogicCalibrate2DebugData();
				data.setUnitIndex(0);
				data.setChnIndex(chnField.getSelection() - 1);
				data.setModuleIndex(moduleField.getSelection() - 1);
				data.setWorkMode(CalMode.values()[modeCombo.getSelectionIndex()]);
				data.setPole(poleCombo.getText().equals(POSITIVE) ? Pole.POSITIVE : Pole.NEGTIVE);
				data.setPrecision(rangeField.getSelection());
				data.setProgramV(programVField.getSelection());
				data.setProgramI(programIField.getSelection());

				
				
				List<DriverCalibrateData.AdcData> list = new ArrayList<>();
				for (int n = 0; n < sampleField.getSelection(); n++) {

					list.add(new DriverCalibrateData.AdcData());
				}

				data.setAdcs(list);

				try {
					WorkBench.getBoxService().cfgCalibrate(calBox, data);
				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "配置校准点失败:" + e1.getMessage());
					return;

				}

				MyMsgDlg.openInfoDialog(getShell(), "操作失败", "配置校准点成功!", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		Button calReadbtn = new Button(group, SWT.PUSH);
		calReadbtn.setText("读取");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(calReadbtn);
		calReadbtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					LogicCalibrate2DebugData response = WorkBench.getBoxService().readCalibrate(calBox,
							chnField.getSelection() - 1);
					System.out.println(response);
					String info = getCalibrateStableResult(response.getAdcs());
					MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
					box.setMessage(info);
					box.open();

				} catch (Exception e1) {
					e1.printStackTrace();

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "读取校准点失败:" + e1.getMessage());
					return;
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		title = new Label(group, SWT.NONE);
		title.setText("计量:");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(title);

		Button meterCfgbtn = new Button(group, SWT.PUSH);
		meterCfgbtn.setText("下发");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(meterCfgbtn);
		meterCfgbtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Data.setModuleCount(2);
				com.nltecklib.protocol.power.Data.setModuleCount(2);
				LogicCalculate2DebugData data = new LogicCalculate2DebugData();
				data.setUnitIndex(0);
				data.setChnIndex(chnField.getSelection() - 1);
				data.setModuleIndex(moduleField.getSelection() - 1);
				data.setMode(CalMode.values()[modeCombo.getSelectionIndex()]);
				data.setPole(poleCombo.getText().equals(POSITIVE) ? Pole.POSITIVE : Pole.NEGTIVE);
				data.setCalculateDot((double) calculateField.getSelection() / 100);
				data.setProgramDot(modeCombo.getSelectionIndex() != 2 ? programVField.getSelection()
						: programIField.getSelection());
			
				List<ReadonlyAdcData> list = new ArrayList<>();
				for (int n = 0; n < sampleField.getSelection(); n++) {

					list.add(new ReadonlyAdcData());
				}
				data.setAdcDatas(list);

				try {
					WorkBench.getBoxService().cfgCalculate(calBox, data);
				} catch (Exception e1) {

					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "配置计量点失败:" + e1.getMessage());
					return;

				}

				MyMsgDlg.openInfoDialog(getShell(), "操作失败", "配置计量点成功!", false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});


		Button meterReadbtn = new Button(group, SWT.PUSH);
		meterReadbtn.setText("读取");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).hint(100, SWT.DEFAULT).applyTo(meterReadbtn);

		meterReadbtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					LogicCalculate2DebugData response = WorkBench.getBoxService().readCalculate(calBox,
							chnField.getSelection() - 1);
					System.out.println(response);
					String info = getCalculateStableResult(response.getAdcDatas());
					MessageBox box = new MessageBox(getShell(), SWT.OK | SWT.ICON_INFORMATION);
					box.setMessage(info);
					box.open();

				} catch (Exception e1) {
					e1.printStackTrace();

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "读取计量点失败:" + e1.getMessage());
					return;
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		return group;
	}

	@Override
	protected void initializeBounds() {

		Composite composite = (Composite) getButtonBar();

		super.initializeBounds();
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	private String getCalculateStableResult(List<ReadonlyAdcData> adcs) {

		StringBuffer info = new StringBuffer();
		if (adcs.size() < 2) {

			info.append("采样个数太少，无法计算标准差");

		}
		// 总和
		double sum = 0, sumbk1 = 0, sumbk2 = 0;
		for (ReadonlyAdcData adc : adcs) {

			for (int n = 0; n < Data.getModuleCount(); n++) {

				sum += adc.adcList.get(n).finalAdc;

			}

			sumbk1 += adc.finalBackAdc1;
			sumbk2 += adc.finalBackAdc2;
		}
		// 平均数
		double avg = sum / adcs.size();
		double avgbk1 = sumbk1 / adcs.size();
		double avgbk2 = sumbk2 / adcs.size();

		double tmp1 = 0, tmp2 = 0, tmp3 = 0;
		for (ReadonlyAdcData adc : adcs) {

			double finalAdc = 0;
			for (int n = 0; n < Data.getModuleCount(); n++) {

				finalAdc += adc.adcList.get(n).finalAdc;

			}

			tmp1 += Math.pow(finalAdc - avg, 2);
			tmp2 += Math.pow(adc.finalBackAdc1 - avgbk1, 2);
			tmp3 += Math.pow(adc.finalBackAdc2 - avgbk2, 2);
		}
		// 样本方差
		double sigma1 = tmp1 / (adcs.size() - 1);
		double sigma2 = tmp2 / (adcs.size() - 1);
		double sigma3 = tmp3 / (adcs.size() - 1);

		sigma1 = sigma1 >= 0 ? Math.sqrt(sigma1) : 0;
		sigma2 = sigma2 >= 0 ? Math.sqrt(sigma2) : 0;
		sigma3 = sigma3 >= 0 ? Math.sqrt(sigma3) : 0;

		info.append("平均AD采样:[finalAdc = " + avg + ",finalBack1=" + avgbk1 + ",finalBack2=" + avgbk2 + "]\n");
		info.append("AD采样标准差:[finalAdc = " + sigma1 + ",finalBack1=" + sigma2 + ",finalBack2=" + sigma3 + "]\n");
		info.append("详细AD:\n");
		for (int n = 0; n < adcs.size(); n++) {

			info.append(n + 1 + ":" + adcs.get(n) + "\n");
		}

		return info.toString();

	}

	private String getCalibrateStableResult(List<DriverCalibrateData.AdcData> adcs) {

		StringBuffer info = new StringBuffer();
		if (adcs.size() < 2) {

			info.append("采样个数太少，无法计算标准差");

		}

		// 总和
		double sum = 0, sumbk1 = 0, sumbk2 = 0;
		for (DriverCalibrateData.AdcData adc : adcs) {
			sum += adc.mainAdc;
			sumbk1 += adc.backAdc1;
			sumbk2 += adc.backAdc2;
		}
		// 平均数
		double avg = sum / adcs.size();
		double avgbk1 = sumbk1 / adcs.size();
		double avgbk2 = sumbk2 / adcs.size();

		double tmp1 = 0, tmp2 = 0, tmp3 = 0;
		for (DriverCalibrateData.AdcData adc : adcs) {
			tmp1 += Math.pow(adc.mainAdc - avg, 2);
			tmp2 += Math.pow(adc.backAdc1 - avgbk1, 2);
			tmp3 += Math.pow(adc.backAdc2 - avgbk2, 2);
		}
		// 样本方差
		double sigma1 = tmp1 / (adcs.size() - 1);
		double sigma2 = tmp2 / (adcs.size() - 1);
		double sigma3 = tmp3 / (adcs.size() - 1);

		sigma1 = sigma1 >= 0 ? Math.sqrt(sigma1) : 0;
		sigma2 = sigma2 >= 0 ? Math.sqrt(sigma2) : 0;
		sigma3 = sigma3 >= 0 ? Math.sqrt(sigma3) : 0;

		info.append("平均AD采样:[main = " + avg + ",back1=" + avgbk1 + ",back2=" + avgbk2 + "]\n");
		info.append("AD采样标准差:[main = " + sigma1 + ",back1=" + sigma2 + ",back2=" + sigma3 + "]\n");
		info.append("详细AD:\n");
		for (int n = 0; n < adcs.size(); n++) {

			info.append(n + 1 + ":" + adcs.get(n) + "\n");

		}

		return info.toString();

	}

}
