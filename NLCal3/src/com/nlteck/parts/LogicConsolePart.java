package com.nlteck.parts;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nlteck.E4LifeCycle;
import com.nlteck.dialog.CalTempControlDlg;
import com.nlteck.dialog.CalculateDotDlg;
import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.firmware.WorkBench.CalType;
import com.nlteck.listener.ChnInfoShowListener;
import com.nlteck.model.BaseCfg;
import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.model.StableDataDO;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestItemDataDO;
import com.nlteck.model.TestLog;
import com.nlteck.parts.uiComponent.ChannelBattery;
import com.nlteck.parts.uiComponent.LogicBatteryComposite;
import com.nlteck.parts.uiComponent.LogicBatteryComposite.BatteryClickListener;
import com.nlteck.report.MeasureReport;
import com.nlteck.resources.Resources;
import com.nlteck.service.CalboxService.CalboxListener;
import com.nlteck.service.CalboxService.MatchState;
import com.nlteck.swtlib.controls.Battery;
import com.nlteck.swtlib.progress.ShowProcessDialog;
import com.nlteck.swtlib.resource.SwtResources;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.swtlib.tools.UITools;
import com.nlteck.utils.CommonUtil;
import com.nlteck.utils.CvsUtil;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.PCWorkform.CalTempQueryDebugData;
import com.nltecklib.protocol.li.PCWorkform.DeviceSelfCheckData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.TestMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import com.nltecklib.utils.LogUtil;

/**
 * 逻辑板调试面板
 * 
 * @author caichao_tang
 *
 */
public class LogicConsolePart extends ConsolePart {
	public static final String ID = "nlcal.partdescriptor.logicConsole";

	/**
	 * 自动对接
	 */
	public static final boolean AUTO_JOIN = true;

	public static LogicConsolePart logicConsolePart;
	private LogicBoard logicBoard;
	// public static Map<Channel, ChannelBattery> chn_batteryMap = new HashMap<>();
	private LogicBatteryComposite composite;
	// private CalCommandComposite calCommandComposite;

	private Entry<Channel, ChannelBattery> hoverEntry;
	private ScrolledComposite scrolledComposite;

	private ToolBar toolbar;

	private Rectangle region;

	private ToolItem connecToolItem;
	private ToolItem switchToolItem;
	private ToolItem modeToolItem;
	private ToolItem startcaliToolItem;
	private ToolItem startcalcuToolItem;
	private ToolItem stopItem;
	private ToolItem reportItem;
	private ToolItem calculatePlanItem;
	private ToolItem checklistItem; // 自检清单
	private ToolItem startTestItem; // 启动测试按钮

	private ToolItem calboard1Item;
	private ToolItem calboard2Item;

	private ToolItem calPowerItem; // 开始校准上
	private ToolItem calculPowerItem; // 开始校准上
	
	
	private ScheduledExecutorService executor;
	// 图形信息展示变更消息
	private ChnInfoShowListener chnShowInfoListener;

	Logger logger;

	@Inject
	public LogicConsolePart() {

		logger = LogUtil.getLogger("logicConsole");
		logger.error("create part");
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FormLayout());

		logicConsolePart = this;

		toolbar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBarcomposite = new FormData();
		fd_toolBarcomposite.top = new FormAttachment(0, 0);
		fd_toolBarcomposite.bottom = new FormAttachment(0, 40);
		fd_toolBarcomposite.left = new FormAttachment(0, 0);
		fd_toolBarcomposite.right = new FormAttachment(100, 0);
		toolbar.setLayoutData(fd_toolBarcomposite);

		connecToolItem = new ToolItem(toolbar, SWT.PUSH);
		connecToolItem.setImage(Resources.CONNECT_IMAGE);
		connecToolItem.setText("连接网络");
		connecToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String btnText = connecToolItem.getText();
				if ("连接网络".equals(btnText)) {

					try {

						logger.error("connectDevice");
						connectDevice(true);
						logger.error("create pop menus");

						if (WorkBench.baseCfg.testMode == BaseCfg.TestMode.DEBUG) {
							composite.createPopMenus(true);
						}

					} catch (Exception e1) {

						e1.printStackTrace();

						logger.error(CommonUtil.getThrowableException(e1));
						try {
							connectDevice(false);
							if (WorkBench.baseCfg.testMode == BaseCfg.TestMode.DEBUG) {
								composite.createPopMenus(false);
							}
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						} // 断开所有连接

						MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "连接失败",
								"连接网络失败:" + e1.getMessage());
						return;
					}
					refreshBtns();

				} else {

					//
					try {
						connectDevice(false);
						if (WorkBench.baseCfg.testMode == BaseCfg.TestMode.DEBUG) {
							composite.createPopMenus(false);
						}
						refreshBtns();

					} catch (Exception e1) {

						e1.printStackTrace();
					}

				}

				// 刷新树
				MPart part = E4LifeCycle.findPart(DeviceListPart.ID, null);
				((DeviceListPart) part.getObject()).refreshTree();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		switchToolItem = new ToolItem(toolbar, SWT.PUSH);
		switchToolItem.setImage(Resources.SWITCH_OFF_IMAGE);
		switchToolItem.setText("进入校准");
		switchToolItem.setToolTipText("必须进入校准模式后才能开始校准");
		switchToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					String text = switchToolItem.getText();
					changeWorkmode(text.equals("进入校准") ? CalibrateCoreWorkMode.CAL : CalibrateCoreWorkMode.NONE);
					refreshBtns();
				} catch (Exception e1) {

					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败",
							"切换校准模式失败:" + e1.getMessage());
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		});

		startTestItem = new ToolItem(toolbar, SWT.NONE);
		startTestItem.setText("启动测试");
		startTestItem.setImage(Resources.MATCH_MODE_IMAGE);
		startTestItem.setToolTipText("启动整机测试");
		startTestItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// 将通道加入测试,Device工作线程将按顺序启动测试
				List<ChannelDO> selectChns = logicBoard.listAllSelectChns();
				for (ChannelDO chn : selectChns) {

					chn.setReadyCommonTest(true);
					chn.setSelected(false);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});
		/*
		 * modeToolItem = new ToolItem(toolbar, SWT.NONE); modeToolItem.setText("自动对接");
		 * modeToolItem.setImage(Resources.MATCH_MODE_IMAGE);
		 * modeToolItem.setToolTipText("设备可以自动检测已对接的通道!");
		 * modeToolItem.addSelectionListener(new SelectionListener() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent e) {
		 * 
		 * if (!MyMsgDlg.openConfirmDialog(Display.getDefault().getActiveShell(),
		 * "对接确认", "自动对接可能需要耗费一点时间，确认要进行自动对接吗?")) {
		 * 
		 * return; } try { clearAllChannelMatchFlag();
		 * changeWorkmode(CalibrateCoreWorkMode.MATCH); refreshBtns(); } catch
		 * (Exception e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * }
		 * 
		 * @Override public void widgetDefaultSelected(SelectionEvent e) {
		 * 
		 * }
		 * 
		 * });
		 */

		new ToolItem(toolbar, SWT.SEPARATOR);

		startcaliToolItem = new ToolItem(toolbar, SWT.NONE);
		startcaliToolItem.setImage(Resources.START_CALI_IMAGE);
		startcaliToolItem.setText("开始校准");

		startcaliToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String str = startcaliToolItem.getText();

				try {
					
					execute(TestMode.EnterCalModeAndStartCal, true);
					composite.resetAllBatteries(null);
					composite.redraw();

				} catch (Exception ex) {

					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", "校准失败:" + ex.getMessage());

				} finally {

					refreshBtns();

				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});
		
		
		
		startcalcuToolItem = new ToolItem(toolbar, SWT.NONE);
		startcalcuToolItem.setImage(Resources.START_CALC_IMAGE);
		startcalcuToolItem.setText("开始计量");
		startcalcuToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {

					String str = startcalcuToolItem.getText();
					execute(TestMode.EnterCalModeAndStartCheck, true);
					composite.resetAllBatteries(null);
					composite.redraw();

				} catch (Exception ex) {

					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", "计量失败:" + ex.getMessage());

				} finally {

					refreshBtns();
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});
		
		
		stopItem = new ToolItem(toolbar, SWT.NONE);
		stopItem.setImage(Resources.STOP_IMAGE);
		stopItem.setText("停止校准");
		stopItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (logicBoard.isTesting()) {
					try {
						execute(TestMode.StopTestAndExitCalMode, true);
					} catch (Exception e1) {

						e1.printStackTrace();
						MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败",
								"停止失败:" + e1.getMessage());
					}
				} else if (logicBoard.getDevice().isCommonTesting()) {

					for (ChannelDO chn : logicBoard.getDevice().getChannels()) {

						if (chn.isReadyCommonTest()) {

							chn.setReadyCommonTest(false);
						}

						if (chn.getRunningMode() != null) {

							// 用户停止测试,将当前运行模式置空将强制停止正在测试的任务
							chn.setRunningMode(null);

						}
					}

				}
				refreshBtns();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});
		new ToolItem(toolbar, SWT.SEPARATOR);

		calboard1Item = new ToolItem(toolbar, SWT.NONE);
		calboard1Item.setImage(Resources.MotherboardDisable32);
		calboard1Item.setText("0℃");
		calboard1Item.setData(false);
		calboard1Item.setToolTipText("校准板恒温配置");
		calboard1Item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (logicBoard.getDevice().getCalBoxList().isEmpty()) {

					MyMsgDlg.openErrorDialog(calboard1Item.getDisplay().getActiveShell(), "操作错误", "校准箱1未配置");
					return;
				}

				final CalBox box = logicBoard.getDevice().getCalBoxList().get(0);
				CalTempControlDlg dlg = new CalTempControlDlg(calboard1Item.getDisplay().getActiveShell(), box,
						(Boolean) calboard1Item.getData());
				dlg.create();
				UITools.centerScreen(dlg.getShell());
				int result = dlg.open();

				if (result == Window.OK) {
					calboard1Item.setImage(dlg.isOpen() ? Resources.Motherboard32 : Resources.MotherboardDisable32);
					calboard1Item.setData(dlg.isOpen());
					if (dlg.isOpen()) {

						if (executor == null) {
							executor = Executors.newSingleThreadScheduledExecutor();
							executor.scheduleWithFixedDelay(new Runnable() {

								@Override
								public void run() {

									try {
										CalTempQueryDebugData query = WorkBench.getBoxService().readTempQueryDebug(box,
												0);
										Display.getDefault().syncExec(new Runnable() {

											@Override
											public void run() {

												System.out.println(query);
												calboard1Item.setText(query.getMainTemp() + "℃");

											}

										});

									} catch (Exception e1) {

										e1.printStackTrace();

										return;
									}

								}

							}, 1000, 5000, TimeUnit.MILLISECONDS);
						}
					} else {

						if (executor != null) {
							executor.shutdownNow();
							executor = null;
						}

					}

				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

//		//xingguo_wang
//		new ToolItem(toolbar, SWT.SEPARATOR);
//		
//		calPowerItem = new ToolItem(toolbar, SWT.NONE);
//		calPowerItem.setImage(Resources.START_CALI_IMAGE);
//		calPowerItem.setText("开始校准（上）");
//		
//		calPowerItem.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				String str = calPowerItem.getText();
//				
//				try {
//					
//					execute2(TestMode.EnterCalModeAndStartCal, true);
//					composite.resetAllBatteries(null);
//					composite.redraw();
//					
//				} catch (Exception ex) {
//					ex.printStackTrace();
//					
//					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", "校准失败:" + ex.getMessage());
//					
//				} finally {
//					
//					refreshBtns();
//					
//				}
//				
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		
//		new ToolItem(toolbar, SWT.SEPARATOR);
//		
//		calculPowerItem = new ToolItem(toolbar, SWT.NONE);
//		calculPowerItem.setImage(Resources.START_CALI_IMAGE);
//		calculPowerItem.setText("开始计量（上）");
//		
//		calculPowerItem.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				String str = calculPowerItem.getText();
//				
//				try {
//					
//					execute2(TestMode.EnterCalModeAndStartCheck, true);
//					composite.resetAllBatteries(null);
//					composite.redraw();
//					
//				} catch (Exception ex) {
//					ex.printStackTrace();
//					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", "校准失败:" + ex.getMessage());
//					
//				} finally {
//					
//					refreshBtns();
//					
//				}
//				
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});

		
		new ToolItem(toolbar, SWT.SEPARATOR);

		calculatePlanItem = new ToolItem(toolbar, SWT.NONE);
		calculatePlanItem.setImage(Resources.CONFIG_PLAN_IMAGE);
		calculatePlanItem.setText("计量方案");
		calculatePlanItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalculateDotDlg calculateDotDlg = new CalculateDotDlg(Display.getDefault().getActiveShell(),
						logicBoard.getDevice());
				calculateDotDlg.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		reportItem = new ToolItem(toolbar, SWT.NONE);
		reportItem.setImage(Resources.REPORT_IMAGE);
		reportItem.setText("导出数据");
		reportItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = new Shell(reportItem.getDisplay(), SWT.TITLE | SWT.APPLICATION_MODAL | SWT.CLOSE);
				shell.setSize(370, 280);
				shell.setText("导出报表");
				shell.setLayout(new FillLayout(SWT.VERTICAL));

				Composite composite = new Composite(shell, SWT.NONE);
				GridLayout gridLayout = new GridLayout(2, true);
				gridLayout.marginLeft = 50;
				gridLayout.marginTop = 50;
				composite.setLayout(gridLayout);

				int BUTTON_WIDTH = 120;
				int BUTTON_HIGHT = 40;

				Label label = new Label(composite, SWT.NONE);
				label.setText("请选择类型:");
				GridDataFactory.fillDefaults().span(2, 1).applyTo(label);

				Button csvBtn = new Button(composite, SWT.NONE);
				csvBtn.setText("CSV文件");
				csvBtn.setCursor(Resources.handCursor);
				GridDataFactory.fillDefaults().hint(BUTTON_WIDTH, BUTTON_HIGHT).applyTo(csvBtn);

				Button xlsxBtn = new Button(composite, SWT.NONE);
				xlsxBtn.setText("EXCEL文件");
				xlsxBtn.setCursor(Resources.handCursor);
				GridDataFactory.fillDefaults().hint(BUTTON_WIDTH, BUTTON_HIGHT).applyTo(xlsxBtn);

				xlsxBtn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						exportMeasureXLSX();
					}

				});
				csvBtn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						exportMeasureCSV();
					}

				});

				// 打开窗口，将窗口进行显示
				UITools.centerScreen(shell);
				shell.open();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		checklistItem = new ToolItem(toolbar, SWT.NONE);
		checklistItem.setImage(Resources.QUERY_SCHEMA_IMAGE);
		checklistItem.setText("检测设备");
		checklistItem.setToolTipText("检测待校准的设备异常情况!");
		checklistItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (logicBoard.getDevice().getCalBoxList().isEmpty()) {

					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", "该设备未绑定任何校准箱");
					return;
				}
				if (!logicBoard.getDevice().isConnected()) {

					MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", "该设备未连接网络");
					return;
				}
				final ShowProcessDialog spd = new ShowProcessDialog(Display.getDefault().getActiveShell());
				spd.open("正在检查设备情况，请稍后...");

				new Thread(new Runnable() {

					@Override
					public void run() {

						try {
							final DeviceSelfCheckData pstid = WorkBench.getBoxService()
									.querySelfCheckInfo(logicBoard.getDevice().getCalBoxList().get(0));

							Display.getDefault().syncExec(new Runnable() {

								@Override
								public void run() {

									spd.close();
									// CheckListDlg dlg = new CheckListDlg(Display.getDefault().getActiveShell(),
									// logicBoard.getDevice(), pstid);
									// dlg.create();
									// dlg.setBlockOnOpen(true);
									// UITools.centerScreen(dlg.getShell());
									// dlg.open();

								}

							});
						} catch (Exception e) {

							e.printStackTrace();

							Display.getDefault().syncExec(new Runnable() {

								@Override
								public void run() {

									spd.close();
									MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败",
											"查询错误:" + e.getMessage());

								}

							});

							return;
						}

					}

				}).start();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		//
		// GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
		// true).applyTo(scrolledComposite);
		scrolledComposite.setExpandHorizontal(false);
		scrolledComposite.setExpandVertical(false);

		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 40);
		fd_scrolledComposite.bottom = new FormAttachment(100, 0);
		fd_scrolledComposite.left = new FormAttachment(0, 0);
		fd_scrolledComposite.right = new FormAttachment(100, 0);
		scrolledComposite.setLayoutData(fd_scrolledComposite);

		scrolledComposite.setLayout(new FillLayout());
		composite = new LogicBatteryComposite(scrolledComposite, SWT.DOUBLE_BUFFERED | SWT.BORDER);

		composite.addBatteryClickListener(new BatteryClickListener() {

			@Override
			public void onBatteryClick(int index) {

				refreshBtns();

			}
		});

		scrolledComposite.setContent(composite);
		scrolledComposite.requestLayout();

		scrolledComposite.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void controlResized(ControlEvent e) {

				region = scrolledComposite.getClientArea();
				System.out.println("resize scorll composite");
				relayoutBatteries();

			}

		});

		parent.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {

				ScrollBar verticalBar = scrolledComposite.getVerticalBar();
				verticalBar.setIncrement(verticalBar.getMaximum() / 25);
				// System.out.println(verticalBar.getSelection() + "," + verticalBar.getThumb()
				// + ":" + verticalBar.getMinimum() + "-" + verticalBar.getMaximum());
				if (e.count < 0) {
					// scroll form down

					if (verticalBar != null && verticalBar.isVisible()
							&& verticalBar.getSelection() + verticalBar.getThumb() < verticalBar.getMaximum()) {
						UITools.scroll(scrolledComposite, 0, verticalBar.getIncrement());
					}

				} else {
					// scroll form up
					if (verticalBar != null && verticalBar.isVisible()
							&& verticalBar.getSelection() > verticalBar.getMinimum()) {
						UITools.scroll(scrolledComposite, 0, -verticalBar.getIncrement());
					}

				}

			}

		});

		WorkBench.getBoxService().clearListners();
		// 监听
		WorkBench.getBoxService().addListener(new CalboxListener() {

			@Override
			public void join(CalBox calbox, boolean operation) {

				if (operation) {

					System.out.println("complete connect !");

					calbox.setMatchState(MatchState.MATCHED);

					if (calbox.getDevice().isMatched()) {

						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {

								System.out.println("ok ,exit connect mode");
								calbox.getDevice().setMode(CalibrateCoreWorkMode.NONE);
								refreshBtns();
								resetMatchedBattery();
								MyMsgDlg.openInfoDialog(Display.getDefault().getActiveShell(), "对接完成",
										"已完成自动对接，可进入校准模式开始校准", false);

							}

						});

					}
				}

			}

			@Override
			public void disconnected(CalBox calbox) {

				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {

						try {
							connectDevice(false);
							CommonUtil.sleep(100);
							refreshBtns();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});

			}

			@Override
			public void connected(CalBox calbox) {
				// TODO Auto-generated method stub

			}

			@Override
			public void calibration(CalBox calbox, boolean enter) {
				// TODO Auto-generated method stub

			}

			@Override
			public void joinVoltage(CalBox calbox, List<ChannelDO> channels) {

				for (ChannelDO channel : channels) {

					int index = logicBoard.getChannels().indexOf(channel);
					if (index != -1) {

						Battery bat = composite.getBatteries().get(index);
						refreshBattery(bat, channel);

					}

				}

			}

			@Override
			public void onRecvChnState(CalBox calbox, List<ChannelDO> channels) {

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						for (ChannelDO channel : channels) {

							refreshBtns();
							int index = logicBoard.getChannels().indexOf(channel);
							if (index != -1) {

								Battery bat = composite.getBatteries().get(index);
								refreshBattery(bat, channel);

							}

						}
						refreshBtns();

					}

				});

			}

			@Override
			public void onRecvChnData(CalBox calbox, ChannelDO channel) {

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						// 刷新数据
						int index = logicBoard.getChannels().indexOf(channel);
						if (index != -1) {

							Battery bat = composite.getBatteries().get(index);
							refreshBattery(bat, channel);

						}

					}

				});

			}

			@Override
			public void onRecvLog(CalBox calbox, TestLog log) {

				if (log.getLevel().equals("error") && log.getDeviceChnIndex() == -1) {

					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "设备报错", log.getContent());

						}

					});

				}

			}

			@Override
			public void onStartTest(CalBox calbox, ChannelDO channel) {

				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {

						makeBatteryVisible(channel);

					}

				});

			}

		});

		// 测试状态
		chnShowInfoListener = new ChnInfoShowListener() {

			@Override
			public void onTestItemInfo(ChannelDO channel) {

				if (logicBoard.getDevice() == channel.getDevice()) {

					Battery bat = composite.getBatteries().get(channel.getDeviceChnIndex());
					refreshBattery(bat, channel);
				}

			}

		};

	}

	public void refreshDeviceState() {

	}

	/** 导出设备计量xlsx报表 */
	private void exportMeasureXLSX() {

		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setFileName("设备" + logicBoard.getDevice().getName() + "计量数据");
		fd.setFilterExtensions(new String[] { "*.xlsx" });
		String path = fd.open();

		if (path == null) {
			return;
		}

		// 导出报表
		final ShowProcessDialog spd = new ShowProcessDialog(Display.getDefault().getActiveShell());
		spd.open("正在导出数据，请稍后...");

		new Thread(new Runnable() {

			@Override
			public void run() {

				FileOutputStream fileOut = null;
				XSSFWorkbook wb = null;

				try {

					wb = new XSSFWorkbook();
					List<String> measureNodes = WorkBench.getDatabaseManager().listMeasureNode(logicBoard.getDevice());

					if (measureNodes.size() == 0) {
						return;
					}

					/** 初始化样式库 */
					MeasureReport.createCellStyleMap(wb);

					for (String node : measureNodes) {

						String sheetName = "";
						List<MeasureDotDO> measureDatas = null;
						String mode = "";

						if (!CommonUtil.isNullOrEmpty(node)) {
							sheetName = node.replaceAll(",", "");
							String[] conditions = node.split(",");
							Map<String, Object> conditionMap = new HashMap<String, Object>();
							mode = conditions[0];
							conditionMap.put("mode", mode);
							conditionMap.put("pole", conditions[1]);
							conditionMap.put("calculateDot", Double.parseDouble(conditions[2]));
							measureDatas = WorkBench.getDatabaseManager().listMeasureDots(logicBoard.getDevice(),
									conditionMap);
						}

						MeasureReport.createMeasureSheet(wb, sheetName, measureDatas, mode);
					}

					// 将输出写入excel文件
					fileOut = new FileOutputStream(path);
					wb.write(fileOut);

					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							spd.close();
							MyMsgDlg.openInfoDialog(Display.getDefault().getActiveShell(), "导出成功", "导出数据" + path + "成功",
									false);

						}

					});

				} catch (Exception e) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							spd.close();
							MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "导出失败", e.getMessage());

						}

					});
					e.printStackTrace();
				} finally {

					wb = null;

					if (fileOut != null) {
						try {
							fileOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}

		}).start();

	}

	/** 导出设备计量csv报表 */
	private void exportMeasureCSV() {

		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setFileName("设备" + logicBoard.getDevice().getName() + "计量数据");
		fd.setFilterExtensions(new String[] { "*.csv" });
		String path = fd.open();

		if (path == null) {
			return;
		}

		// 导出报表
		final ShowProcessDialog spd = new ShowProcessDialog(Display.getDefault().getActiveShell());
		spd.open("正在导出数据，请稍后...");

		new Thread(new Runnable() {

			@Override
			public void run() {

				// 查询设备数据
				try {
					List<MeasureDotDO> measureDatas = WorkBench.getDatabaseManager()
							.listMeasureDots(logicBoard.getDevice());
					CvsUtil cvsUtil = new CvsUtil(path, false);

					cvsUtil.setHeader(
							new String[] { "通道号", "模式", "极性", "计量点", "表值", "ADC", "表偏差", "ADC偏差", "结果", "信息" });
					for (MeasureDotDO dot : measureDatas) {

						if (!dot.getMode().equals(CalMode.CV2.name())) {
							List<Object> row = new ArrayList<Object>();
							row.add(dot.getChannel().getDeviceChnIndex() + 1 + "");
							row.add(dot.getMode());
							row.add(dot.getPole());

							row.add(CommonUtil.formatNumber(dot.getCalculateDot(), 1));
							row.add(CommonUtil.formatNumber(dot.getMeterVal(), 3));
							row.add(CommonUtil.formatNumber(dot.getFinalAdc(), 3));
							row.add(CommonUtil.formatNumber(dot.getMeterVal() - dot.getCalculateDot(), 3));
							row.add(CommonUtil.formatNumber(dot.getFinalAdc() - dot.getCalculateDot(), 3));
							row.add(dot.getResult());
							row.add(dot.getInfo());

							cvsUtil.writeRecord(row.toArray());
						}
					}
					cvsUtil.flush();
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							spd.close();
							MyMsgDlg.openInfoDialog(Display.getDefault().getActiveShell(), "导出成功", "导出数据" + path + "成功",
									false);

						}

					});
					// FileNotFoundException exception ;

				} catch (Exception e) {

					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							spd.close();
							MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "导出失败", e.getMessage());

						}

					});

					e.printStackTrace();
				}

			}

		}).start();

	}

	private void refreshBattery(Battery bat, ChannelDO channel) {

		Color back = null;
		CalibrateCoreWorkMode mode = channel.getDevice().getMode();

		bat.clearContent();
		bat.setCornerFlag(0, null);

		if (mode == CalibrateCoreWorkMode.MATCH) {

			// 对接状态中
			if (channel.isConnectCalboard()) {

				back = Resources.UDT_CLR;
			}
			if (channel.getConnectVoltage() > 0) {

				bat.clearContent();
				// 显示对接电压
				bat.appendTextLine(CommonUtil.formatNumber(channel.getConnectVoltage(), 1) + "mV");

			}

		}

		if (channel.getRunningMode() == null) {

			// 常规校准电池图形测试

			if (mode != CalibrateCoreWorkMode.MATCH || !channel.isConnectCalboard()) {
				switch (channel.getState()) {

				case NONE:
					back = Resources.NONE_CLR;
					break;
				case READY:
					back = Resources.READY_CLR;
					break;
				case CALCULATE:
				case CALIBRATE:
					back = Resources.TEST_CLR;
					break;
				case CALCULATE_PASS:
					back = Resources.MEA_COMPLETE_CLR;
					break;
				case CALIBRATE_PASS:
					back = Resources.CAL_COMPLETE_CLR;
					break;
				case CALCULATE_FAIL:
				case CALIBRATE_FAIL:
					back = Resources.ALERT_CLR;
					break;

				}

			}
			// 准备测试
			if (channel.isReadyCommonTest()) {

				back = Resources.READY_CLR;
			}

			if (channel.isConnectCalboard()) {

				bat.setCornerFlag(3, SwtResources.yellowDot);
			} else {

				bat.setCornerFlag(3, null);
			}

			if (channel.getState() == CalState.CALIBRATE || channel.getState() == CalState.CALCULATE) {

				String str = (channel.getMode() == null ? "sleep" : channel.getMode().name().toLowerCase())
						+ (channel.getPole() == null ? "" : (channel.getPole() == Pole.NORMAL ? "+" : "-"));
				bat.setCornerFlag(0, str);

				// 三行内容
				bat.clearContent();

				if (channel.getCalType() == CalType.CAL) {
					// 程控值
					bat.appendTextLine("P:" + CommonUtil.formatNumber(channel.getProgrameVal(), 0));
					// 进度条
					bat.appendTextLine(channel.getPos() + " / " + channel.getRange());
					// 时间
					bat.appendTextLine(CommonUtil.formatMinute(channel.getSeconds()));
				} else {

					// 计量点
					bat.appendTextLine("P:" + CommonUtil.formatNumber(channel.getProgrameVal(), 1));
					// 表值
					bat.appendTextLine("M:" + CommonUtil.formatNumber(channel.getMeterVal(), 1));
					// adc
					bat.appendTextLine("A:" + CommonUtil.formatNumber(channel.getAdc(), 1));
					// 时间
					bat.appendTextLine(CommonUtil.formatMinute(channel.getSeconds()));

				}

			} else if (channel.getState() == CalState.CALIBRATE_PASS || channel.getState() == CalState.CALIBRATE_FAIL
					|| channel.getState() == CalState.CALCULATE_FAIL || channel.getState() == CalState.CALCULATE_PASS) {

				if (mode != CalibrateCoreWorkMode.MATCH) {
					bat.clearContent();

					bat.appendTextLine(CommonUtil.formatMinute(channel.getSeconds()));
				}

			}

		} else {

			// 左上角显示测试目录
			bat.setCornerFlag(0, channel.getRunningMode().toString());

			back = Resources.TEST_CLR; // 背景色

			if (channel.getRunningMode() == RunMode.StableTest) {

				List<StableDataDO> stables = channel.getStableDatas();
				if (!stables.isEmpty()) {

					StableDataDO stable = stables.get(stables.size() - 1);
					// 稳定度测试显示三行
					// 计量值
					bat.appendTextLine("P:" + CommonUtil.formatNumber(stable.getCalculateDot(), 1));
					// 表值
					bat.appendTextLine("M:" + CommonUtil.formatNumber(stable.getMeter(), 1));
					// ADC
					bat.appendTextLine("A:" + CommonUtil.formatNumber(stable.getAdc(), 1));
				}

			} else if (channel.getRunningMode() == RunMode.Cal) {

				// 暂时空缺

			} else {

				// 各种基础测试
				List<TestItemDataDO> items = channel.getTestItemsBy(channel.getRunningMode());
				// 找到正在测试的项目
				for (TestItemDataDO item : items) {

					if (item.getState().equals("testing")) {

						// bat.appendTextLine(item.getIndex() + 1 + "");
						bat.appendTextLine(item.getName().toString());
					}
				}

			}

		}
		bat.setMousePress(channel.isSelected()); // 选中标志
		bat.setBatColor(back);
		bat.redraw();

	}

	/**
	 * 设置界面绑定
	 * 
	 * @param list
	 */
	public void setPartBind(LogicBoard logicBoard) {
		this.logicBoard = logicBoard;
		List<ChannelDO> channelsList = new ArrayList<>();
		for (DriverBoard driverBoard : logicBoard.getDrivers()) {
			channelsList.addAll(driverBoard.getChannels());
		}
		// super.channelList = channelsList;
	}

	@Override
	public List<CalBox> getBindCalBox() {
		return logicBoard.getDevice().getCalBoxList();
	}

	/**
	 * 设置工具栏的状态
	 */
	@Override
	public void setToolItemState() {
		// calCommandComposite.setConnectState();
		// calCommandComposite.setSwitchState();
		// calCommandComposite.setModeState();
		// calCommandComposite.setCommandState();
	}

	/**
	 * 重置电池界面
	 * 
	 * @author wavy_zheng 2021年1月17日
	 */
	private void relayoutBatteries() {

		if (region == null) {

			region = scrolledComposite.getClientArea();
		}
		LogicBatteryComposite composite = (LogicBatteryComposite) scrolledComposite.getContent();
		Point size = composite.computeSize(region.width, region.height, false);
		composite.setSize(size);
		composite.requestLayout();
		composite.relayoutBatteries();
		scrolledComposite.setMinSize(new Point(region.width, region.height));
		UITools.scroll(scrolledComposite, 0, 0);

	}

	@Override
	public void refreshData(Object obj) {

		if (this.logicBoard != obj) {

			if (this.logicBoard != null && chnShowInfoListener != null) {
				// 撤销旧的状态监听器
				for (ChannelDO chn : logicBoard.getChannels()) {
                   
					//从对象中删除消息监听
					chn.removeShowListener(chnShowInfoListener);
				}
			}

			this.logicBoard = (LogicBoard) obj;
			composite.refreshData((LogicBoard) obj);
			relayoutBatteries();
			refreshBtns();
			//
			for (int n = 0; n < logicBoard.getChannels().size(); n++) {

				Battery bat = composite.getBatteries().get(n);
				refreshBattery(bat, logicBoard.getChannels().get(n));
				Date date = logicBoard.getChannels().get(n).getEndTime();
				if (date != null) {
					bat.setToolTipText(CommonUtil.formatTime(date, "yyyy-MM-dd HH:mm:ss"));
				}
				//监听新设备的通道状态变更
				logicBoard.getChannels().get(n).addShowListener(chnShowInfoListener);
				

			}
			composite.createPopMenus(true);
			refreshDrivers();

		}

	}

	/**
	 * 刷新驱动板绑定情况
	 * 
	 * @author wavy_zheng 2021年2月9日
	 */
	private void refreshDrivers() {

		for (int n = 0; n < composite.getDriverFlags().size(); n++) {

			DriverBoard db = logicBoard.getDrivers().get(n);
			CalBoard cb = db.getBindCalBoard();
			composite.getDriverFlags().get(n).setText("#" + (db.getDriverIndexInDevice() + 1)
					+ (cb == null ? "" : "\n" + cb.getCalBox().getName() + "_" + (cb.getIndex() + 1)));

		}

	}

	@Override
	public void redraw() {
		// TODO Auto-generated method stub

	}

	public void execute(TestMode mode, boolean select) throws Exception {

		for (CalBox box : logicBoard.getDevice().getCalBoxList()) {

			if (mode == TestMode.EnterCalModeAndStartCal || mode == TestMode.EnterCalModeAndStartCheck) {
				WorkBench.getBoxService().selectChns(box, logicBoard, true);
				WorkBench.getBoxService().executeTest(box, mode);

			} else if (mode == TestMode.StopTestAndExitCalMode) {

				WorkBench.getBoxService().selectChns(box, logicBoard, !logicBoard.listAllSelectChns().isEmpty());
				WorkBench.getBoxService().executeTest(box, mode);

			}

		}
	}

	/**
	 * 核心在上
	 */
	public void execute2(TestMode mode, boolean select)throws Exception {

		for (CalBox box : logicBoard.getDevice().getCalBoxList()) {

			if (mode == TestMode.EnterCalModeAndStartCal || mode == TestMode.EnterCalModeAndStartCheck) {
				if(mode==TestMode.EnterCalModeAndStartCal) {
					
//					WorkBench.getBoxService().selectChns(box, logicBoard, true);
					List<ChannelDO> selectChannelDOs=new ArrayList<>();
					for(ChannelDO channelDO:logicBoard.getChannels()) {
						if(channelDO.isSelected()) {
							selectChannelDOs.add(channelDO);
						}
					}
					for(ChannelDO testChannelDO:selectChannelDOs) {
						WorkBench.coreService.executeTest(testChannelDO);					
					}
				}else {
					try {
						
						WorkBench.getBoxService().selectChns(box, logicBoard, true);
						List<ChannelDO> selectChannelDOs=new ArrayList<>();
						for(ChannelDO channelDO:logicBoard.getChannels()) {
							if(channelDO.isSelected()) {
								selectChannelDOs.add(channelDO);
							}
						}
						for(ChannelDO testChannelDO:selectChannelDOs) {
							WorkBench.coreService.calculate(testChannelDO);					
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (mode == TestMode.StopTestAndExitCalMode) {

				WorkBench.getBoxService().selectChns(box, logicBoard, !logicBoard.listAllSelectChns().isEmpty());
				WorkBench.getBoxService().executeTest(box, mode);

			}

		}
	}
	
	/**
	 * 清除所有对接标志位
	 * 
	 * @author wavy_zheng 2021年1月20日
	 */
	public void clearAllChannelMatchFlag() {

		List<ChannelDO> channels = logicBoard.getDevice().getChannels();
		for (ChannelDO channel : channels) {

			channel.setConnectCalboard(false);
		}
	}

	public void changeWorkmode(CalibrateCoreWorkMode mode) throws Exception {

		for (CalBox box : logicBoard.getDevice().getCalBoxList()) {

			WorkBench.getBoxService().changeWorkMode(box, mode);
			box.setMatchState(MatchState.MATCHING);
		}
		logicBoard.getDevice().setMode(mode);
	}

	public void connectDevice(boolean connect) throws Exception {

		for (CalBox box : logicBoard.getDevice().getCalBoxList()) {
			if (connect) {

				if (!box.isConnected()) {

					if (!WorkBench.getBoxService().connect(box)) {

						throw new Exception("连接校准箱" + box.getName() + "超时!");
					}

				}

			} else {

				WorkBench.getBoxService().disconnect(box);

			}

		}

		unbindAllCalboard();

		// 额外连接设备,用于性能测试
		if (connect) {
			if (!logicBoard.getDevice().connect()) {

				throw new Exception("连接设备" + logicBoard.getDevice().getName() + "超时!");

			}
		} else {

			logicBoard.getDevice().disconnect();
		}

	}

	/**
	 * 刷新工具栏按钮
	 * 
	 * @author wavy_zheng 2021年1月19日
	 */
	public void refreshBtns() {

		Device device = logicBoard.getDevice();
		boolean connect = device.isConnected();

		boolean connectCalboard = !device.listAllConnectedChannels().isEmpty(); // 有通道完成对接?

		boolean testing = logicBoard.isTesting();
		boolean commonTesting = device.isCommonTesting();

		List<ChannelDO> selectChns = logicBoard.listAllSelectChns();

		connecToolItem.setText(connect ? "断开连接" : "连接网络");
		connecToolItem.setEnabled(!device.getCalBoxList().isEmpty());
		// calboard1Item.setEnabled(connect);
		checklistItem.setEnabled(connect && !device.isTesting());
		calculatePlanItem.setEnabled(connect && !device.isTesting());

		switchToolItem.setEnabled(
				!commonTesting && connect && connectCalboard && device.getMode() != CalibrateCoreWorkMode.MATCH);
		switchToolItem.setText(device.getMode() == CalibrateCoreWorkMode.CAL ? "退出校准" : "进入校准");

		if (testing) {

			boolean allTest = true;
			boolean allReady = true;

			// 在以前标准校准模式下，禁用普通性能测试按钮
			startTestItem.setEnabled(false);

			if (selectChns.isEmpty()) { // 不选中则停止所有通道

				allReady = false;
			} else {

				for (ChannelDO chn : selectChns) {

					if (chn.getState() != CalState.CALIBRATE && chn.getState() != CalState.CALCULATE
							&& chn.getState() != CalState.READY) {

						allTest = false;

					}
					if (chn.getState() == CalState.CALIBRATE || chn.getState() == CalState.CALCULATE) {

						allReady = false;

					}
				}
			}
			if (allTest) {

				startcaliToolItem.setEnabled(false);
				startcalcuToolItem.setEnabled(false);
				stopItem.setEnabled(true);

			} else if (allReady) {

				startcaliToolItem.setEnabled(device.getMode() == CalibrateCoreWorkMode.CAL);
				startcalcuToolItem.setEnabled(device.getMode() == CalibrateCoreWorkMode.CAL);
				stopItem.setEnabled(false);

			} else {

				startcaliToolItem.setEnabled(false);
				startcalcuToolItem.setEnabled(false);
				stopItem.setEnabled(false);
			}

		} else if (commonTesting) {

			boolean allTest = true;
			boolean allReady = true;

			startcaliToolItem.setEnabled(false);
			startcalcuToolItem.setEnabled(false);

			if (selectChns.isEmpty()) { // 不选中则停止所有通道

				allReady = false;
			} else {

				for (ChannelDO chn : selectChns) {

					if (!chn.isReadyCommonTest() && chn.getRunningMode() == null) {

						allTest = false; // 没有选中全部在测试或待测的通道
					}

					if (chn.isReadyCommonTest() || chn.getRunningMode() != null) {

						allReady = false;
					}

				}

				if (allTest) {

					startTestItem.setEnabled(false);
					stopItem.setEnabled(true);

				} else if (allReady) {

					startTestItem.setEnabled(true);
					stopItem.setEnabled(false);

				} else {

					startTestItem.setEnabled(false);
					stopItem.setEnabled(false);
				}

			}

		} else {

			boolean allReady = true;
			if (selectChns.isEmpty()) {

				allReady = false;
			} else {
				for (ChannelDO chn : selectChns) {

					if (chn.getState() == CalState.CALCULATE || chn.getState() == CalState.CALIBRATE
							|| !chn.isConnectCalboard()) {

						allReady = false;
						break;
					}
				}
			}
			if (allReady) {

				startcaliToolItem.setEnabled(device.getMode() == CalibrateCoreWorkMode.CAL);
				startcalcuToolItem.setEnabled(device.getMode() == CalibrateCoreWorkMode.CAL);
				stopItem.setEnabled(false);
			} else {

				startcaliToolItem.setEnabled(false);
				startcalcuToolItem.setEnabled(false);
				stopItem.setEnabled(false);
			}

		}

	}

	/**
	 * 复位对接颜色
	 * 
	 * @author wavy_zheng 2021年1月19日
	 */
	private void resetMatchedBattery() {

		for (int n = 0; n < logicBoard.getChannels().size(); n++) {

			Battery bat = composite.getBatteries().get(n);
			if (logicBoard.getChannels().get(n).isConnectCalboard()) {

				refreshBattery(bat, logicBoard.getChannels().get(n));
			}

		}

	}

	/**
	 * 解绑所有校准板
	 */
	private void unbindAllCalboard() {

		for (LogicBoard lb : logicBoard.getDevice().getLogicBoardList()) {

			for (DriverBoard db : lb.getDrivers()) {

				db.bind(null);
			}

		}
	}

	/**
	 * 让校准的通道可见
	 * 
	 * @author wavy_zheng 2021年2月19日
	 * @param channel
	 */
	private void makeBatteryVisible(ChannelDO channel) {

		ScrollBar verticalBar = scrolledComposite.getVerticalBar();
		ScrollBar horizontalBar = scrolledComposite.getHorizontalBar();
		verticalBar.setIncrement(verticalBar.getMaximum() / 25);
		horizontalBar.setIncrement(horizontalBar.getMaximum() / 25);
		// Rectangle region = scrolledComposite.getClientArea();
		int logicChnIndex = logicBoard.getChannels().indexOf(channel);
		if (logicChnIndex != -1) {

			Battery bat = composite.getBatteries().get(logicChnIndex);
			Rectangle rect = bat.getBounds();
			int offsetX = 0, offsetY = 0;
			if (rect.x + rect.width > horizontalBar.getThumb() + horizontalBar.getSelection()) {

				offsetX = rect.x + rect.width - horizontalBar.getThumb() - horizontalBar.getSelection();
			} else if (horizontalBar.getSelection() - rect.x > 0) {

				offsetX = rect.x - horizontalBar.getSelection();
			}
			if (rect.y + rect.height > verticalBar.getThumb() + verticalBar.getSelection()) {

				offsetY = rect.y + rect.height - verticalBar.getThumb() - verticalBar.getSelection();
			} else if (verticalBar.getSelection() - rect.y > 0) {

				offsetY = rect.y - verticalBar.getSelection();
			}
			UITools.scroll(scrolledComposite, offsetX, offsetY);

		}

	}

}