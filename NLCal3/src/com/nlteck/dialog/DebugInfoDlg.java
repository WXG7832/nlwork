package com.nlteck.dialog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.listener.ChnDataChangeLisener;
import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.model.StableDataDO;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestItemDataDO;
import com.nlteck.model.TestLog;
import com.nlteck.parts.uiComponent.TreeNode;
import com.nlteck.parts.uiComponent.TreeNodeDataProvider;
import com.nlteck.parts.uiComponent.TreeNodeLabelProvider;
import com.nlteck.report.CalibrateReport;
import com.nlteck.report.MeasureReport;
import com.nlteck.resources.Resources;
import com.nlteck.service.CalboxService.CalboxListener;
import com.nlteck.swtlib.progress.ShowProcessDialog;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.table.DebugTableViewer;
import com.nlteck.table.StableTableViewer;
import com.nlteck.table.TestItemTableViewer;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWrite2DebugData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDotData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.logic2.Logic2Environment;
import com.nltecklib.protocol.li.main.PoleData;
import com.nltecklib.protocol.power.calBox.calBox_device.MbFlashParamData;
import com.nltecklib.protocol.power.driver.DriverEnvironment;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;
import com.nltecklib.protocol.power.driver.DriverCalParamSaveData.CalParamData;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月20日 下午9:47:19 类说明
 */
public class DebugInfoDlg extends Dialog {

	private LogicBoard logic;
	private ChannelDO channel;

	private Composite calTablePanel;
	private DebugTableViewer calTableViewer;
	private StableTableViewer stableTableViewer;
	private TestItemTableViewer testTableViewer;

	private CalboxListener listener;
	private Composite cardPanel;

	LogicFlashWrite2DebugData response;
	private TreeViewer treeViewer; // 导航树

	private StyledText logText;
	private static final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	private ChnDataChangeLisener dataListener;

	public DebugInfoDlg(Shell parentShell, LogicBoard lb, ChannelDO channel) {
		super(parentShell);

		this.logic = lb;
		this.channel = channel;
		
			
		dataListener = new ChnDataChangeLisener() {

			@Override
			public void onStableDataChange(StableDataDO data) {

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						List<StableDataDO> list = (List<StableDataDO>) stableTableViewer.getInput();
						if (list == null) {

							list = channel.getStableDatas();
							stableTableViewer.setInput(list);
						} else {

							list.add(data);
						}

						stableTableViewer.refresh();

					}

				});

			}

			@Override
			public void onItemChange(RunMode rm, TestItemDataDO item) {

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						TreeNode node = (TreeNode) treeViewer.getStructuredSelection().getFirstElement();
						if (RunMode.parse(node.getTitle()) == rm) {

							List<TestItemDataDO> list = (List<TestItemDataDO>) testTableViewer.getInput();
							if (list == null) {

								list = channel.getTestItemsBy(rm);
								testTableViewer.setInput(list);
							} else {

								list.add(item);
							}

							testTableViewer.refresh();

						}
					}
				});

			}

			@Override
			public void onCalDataChange(UploadTestDotData data) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onRecvLog(TestLog log) {

				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {

						appendLog(log);


					}

				});

			}
		};

	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MODELESS | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		setBlockOnOpen(false);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("通道" + (channel.getDeviceChnIndex() + 1) + "调试信息");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(container);

		SashForm main = new SashForm(container, SWT.NONE | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(main);

		createNavigateTree(main);

		Composite right = new Composite(main, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(right);
		main.setWeights(new int[] { 1, 5 });

		// 加载数据
		try {
			loadData(null);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 创建右边面板

		SashForm form = new SashForm(right, SWT.NONE | SWT.VERTICAL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(form);

		createCardPanel(form);

		logText = new StyledText(form, SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		form.setWeights(new int[] { 2, 1 });
		 refreshData();
		
		//TODO 加载日志
		 refreshLog();

		ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (calTableViewer.getTable().isDisposed()) {

					return;
				}
				calTableViewer.getTable().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						refreshData();
//						refreshLog();
					}
				});

			}
		}, 10, 1000, TimeUnit.MILLISECONDS);
		
		
		
		WorkBench.getBoxService().addListener(listener = new CalboxListener() {

			@Override
			public void connected(CalBox calbox) {
				// TODO Auto-generated method stub

			}

			@Override
			public void disconnected(CalBox calbox) {
				// TODO Auto-generated method stub

			}

			@Override
			public void join(CalBox calbox, boolean operation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void calibration(CalBox calbox, boolean enter) {
				// TODO Auto-generated method stub

			}

			@Override
			public void joinVoltage(CalBox calbox, List<ChannelDO> channels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRecvChnState(CalBox calbox, List<ChannelDO> channels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRecvChnData(CalBox calbox, ChannelDO channel) {

				if (channel == DebugInfoDlg.this.channel) {

					if (calTableViewer.getTable().isDisposed()) {

						return;
					}
					calTableViewer.getTable().getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							refreshData();
						}
					});

				}

			}

			@Override
			public void onRecvLog(CalBox calbox, TestLog log) {

				if (log.getDeviceChnIndex() == channel.getDeviceChnIndex()) {

					if (logText.isDisposed()) {

						return;
					}
					logText.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							appendLog(log);
							
						}

					});

				}

			}

			@Override
			public void onStartTest(CalBox calbox, ChannelDO channel) {
				// TODO Auto-generated method stub

			}

		});

		
		
		
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				WorkBench.getBoxService().removeListener(listener);
				// 删除监听器
				channel.removeListener(dataListener);

			}
		});

		return container;
	}

	private void loadData(RunMode rm) throws SQLException {

		if (rm == null) {

			for (RunMode mode : WorkBench.baseCfg.runModes) {

				loadData(mode);
			}
		} else {

			if (rm != RunMode.Cal) {

				if (rm == RunMode.StableTest && channel.getStableDatas().isEmpty()) {

					channel.getStableDatas().addAll(WorkBench.dataManager.listStableDatas(channel));
				} else {

					if (channel.getTestItemsBy(rm) == null || channel.getTestItemsBy(rm).isEmpty()) {

						List<TestItemDataDO> items = WorkBench.dataManager.listTestItems(channel, rm);
						if (items.isEmpty()) {

							channel.putTestItems(rm, WorkBench.baseCfg.testItemMap.get(rm));
						} else {

							channel.putTestItems(rm, items);
						}
					}
				}

			}

		}

	}

	private void createCardPanel(Composite parent) {

		cardPanel = new Composite(parent, SWT.NONE);
		StackLayout layout = new StackLayout();
		cardPanel.setLayout(layout);

		calTablePanel = new Composite(cardPanel, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(calTablePanel);

		createTopPanel(calTablePanel);

		calTableViewer = new DebugTableViewer(calTablePanel, channel);
		calTableViewer.setInput(channel.getDebugDatas());
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(calTableViewer.getTable());

		stableTableViewer = new StableTableViewer(cardPanel, channel);

		testTableViewer = new TestItemTableViewer(cardPanel, channel);

		RunMode rm = WorkBench.baseCfg.runModes.get(0);

		switch (rm) {

		case Cal:
			layout.topControl = calTablePanel;
			break;
		case StableTest:
			layout.topControl = stableTableViewer.getTable();
			break;
		default:
			layout.topControl = testTableViewer.getTable();
			break;

		}
		List<TreeNode> nodes = (List<TreeNode>) treeViewer.getInput();
		treeViewer.setSelection(new StructuredSelection(nodes.get(0)), true);

		showData(rm);
		cardPanel.layout();

	}

	private List<TestItemDataDO> resetIndex(List<TestItemDataDO> items) {

		if (items == null) {

			return items;
		}
		for (int n = 0; n < items.size(); n++) {

			items.get(n).setIndex(n);

		}

		return items;

	}

	/**
	 * 展示表格数据
	 * 
	 * @author wavy_zheng 2022年3月28日
	 */
	private void showData(RunMode rm) {

		if (rm != RunMode.Cal && rm != RunMode.StableTest) {

			testTableViewer.setInput(resetIndex(channel.getTestItemsBy(rm)));
			testTableViewer.refresh();
		} else {

			stableTableViewer.refresh();
			calTableViewer.refresh();
		}

	}

	/**
	 * 创建左边导航树
	 * 
	 * @author wavy_zheng 2022年3月27日
	 * @param parent
	 */
	private void createNavigateTree(Composite parent) {

		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new TreeNodeDataProvider());
		treeViewer.setLabelProvider(new TreeNodeLabelProvider());
		treeViewer.expandToLevel(1);
		treeViewer.getTree().setCursor(Resources.handCursor);

		List<TreeNode> nodes = new ArrayList<>();

		for (RunMode rm : WorkBench.baseCfg.runModes) {

			TreeNode node = new TreeNode(null, rm.toString(), null);
			nodes.add(node);
		}
		treeViewer.setInput(nodes);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				TreeNode selectNode = (TreeNode) treeViewer.getStructuredSelection().getFirstElement();
				if (selectNode != null) {

					StackLayout layout = (StackLayout) cardPanel.getLayout();
					if (selectNode.getTitle().equals(RunMode.Cal.toString())) {

						layout.topControl = calTablePanel;

					} else if (selectNode.getTitle().equals(RunMode.StableTest.toString())) {

						layout.topControl = stableTableViewer.getTable();
					} else {

						layout.topControl = testTableViewer.getTable();

						showData(RunMode.parse(selectNode.getTitle()));
					}

					cardPanel.layout();

				}

			}

		});

	}

	private void createTopPanel(Composite parent) {

		Composite top = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(10).equalWidth(false).applyTo(top);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(SWT.DEFAULT, SWT.DEFAULT)
				.applyTo(top);
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setText("清除");
		item.setImage(Resources.DEL_ITEM_IMAGE);
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!MyMsgDlg.openConfirmDialog(getShell(), "操作确认", "确定清除当前调试信息?")) {

					return;
				}
				channel.getDebugDatas().clear();
				logText.setText("");
				calTableViewer.refresh();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		item = new ToolItem(bar, SWT.PUSH);
		item.setText("查询系数");
		item.setImage(Resources.QUERY_SCHEMA_IMAGE);
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					readFlash();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		item = new ToolItem(bar, SWT.PUSH);
		item.setText("写入系数");
		item.setImage(Resources.IMPORT_SCHEMA_IMAGE);
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					writeFlash2();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		item = new ToolItem(bar, SWT.PUSH);
		item.setText("导出");
		item.setImage(Resources.REPORT_IMAGE);
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (channel.getDebugDatas().isEmpty()) {

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "表格内无数据可以导出!");
					return;
				}

				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
				fd.setFileName("chn" + (channel.getDeviceChnIndex() + 1) + "调试数据");
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
							/** 初始化样式库 */
							CalibrateReport.createCellStyleMap(wb);

							CalibrateReport.createCalSheet(wb, "调试数据", channel.getDebugDatas());

							// 将输出写入excel文件
							fileOut = new FileOutputStream(path);
							wb.write(fileOut);

							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {

									spd.close();
									MyMsgDlg.openInfoDialog(Display.getDefault().getActiveShell(), "导出成功",
											"导出数据" + path + "成功", false);

								}

							});

						} catch (Exception e) {
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {

									spd.close();
									MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "导出失败",
											e.getMessage());

								}

							});
							e.printStackTrace();
						}

						finally {

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

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		item = new ToolItem(bar, SWT.PUSH);
		item.setText("导入");
		item.setToolTipText("将外部excel格式的调试报表系数导入通道");
		item.setImage(Resources.IMPORT_SCHEMA_IMAGE);
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!channel.getDebugDatas().isEmpty()) {

					if (!MyMsgDlg.openConfirmDialog(getShell(), "操作确认", "当前通道存在调试数据，是否覆盖?")) {

						return;
					}
				}

				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				fd.setFileName("chn" + (channel.getDeviceChnIndex() + 1) + "调试数据");
				fd.setFilterExtensions(new String[] { "*.xlsx" });
				String path = fd.open();

				if (path == null) {
					return;
				}

				// 导出报表
				final ShowProcessDialog spd = new ShowProcessDialog(Display.getDefault().getActiveShell());
				spd.open("正在导入数据，请稍后...");

				new Thread(new Runnable() {

					@Override
					public void run() {

						FileInputStream fileIn = null;
						XSSFWorkbook wb = null;
						try {

							wb = new XSSFWorkbook(new FileInputStream(path));

							List<UploadTestDot> datas = CalibrateReport.importCalSheet(wb, channel.getDeviceChnIndex());

							channel.getDebugDatas().clear();
							channel.getDebugDatas().addAll(datas);

							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {

									spd.close();
									calTableViewer.refresh();

									MyMsgDlg.openInfoDialog(Display.getDefault().getActiveShell(), "导入成功",
											"导入表格" + path + "成功", false);

								}

							});

						} catch (Exception e) {
							Display.getDefault().asyncExec(new Runnable() {

								@Override
								public void run() {

									spd.close();
									MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "导出失败",
											e.getMessage());

								}

							});
							e.printStackTrace();
						}

						finally {

							wb = null;

							if (fileIn != null) {
								try {
									fileIn.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}

					}
				}).start();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		item = new ToolItem(bar, SWT.PUSH);
		item.setText("写入");
		item.setToolTipText("将当前页面的校准系数写入模片flash");
		item.setImage(Resources.DOWN_ITEM_IMAGE);
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// 检查是否有错误
				int calDotCount = 0;
				for (UploadTestDot dot : channel.getDebugDatas()) {

					if (dot.testType == TestType.Cal) {

						calDotCount++;

						if (!dot.success) {
							MyMsgDlg.openErrorDialog(getShell(), "操作失败", "校准点" + dot.programVal + "校验结果fail不能写入flash");
							return;
						}
					}

				}

				if (calDotCount == 0) {

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "当前校准调试数据为空");
					return;
				}

				new Thread(new Runnable() {

					@Override
					public void run() {

						try {
							writeFlash();
						} catch (Exception e1) {

							e1.printStackTrace();

							Display.getDefault().syncExec(new Runnable() {

								@Override
								public void run() {

									MyMsgDlg.openErrorDialog(getShell(), "写入失败", e1.getMessage());

								}

							});

						}

					}

				}).start();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

	}

	/**
	 * 读取flash系数
	 * 
	 * @throws Exception
	 */
	protected void readFlash() throws Exception {

		LogicFlashWrite2DebugData flash = new LogicFlashWrite2DebugData();
		flash.setUnitIndex(0);
		flash.setChnIndex(channel.getChnIndex());
		response = WorkBench.getBoxService().readFlash(logic.getDevice().getCalBoxList().get(0), flash);
		// 写入软件表格
		int moduleIndex=response.getModuleIndex();
		Map<CalMode, List<CalParamData>> kb_dotMap = response.getKb_dotMap();
		int cv1DotCount = response.getCv1DotCount();
		int cv2DotCount = response.getCv2DotCount();

		List<UploadTestDot> datas = new ArrayList<>();
		UploadTestDot uploadTestDot=new UploadTestDot();
		// 存入slp各个校准点
		List<CalParamData> list = kb_dotMap.get(CalMode.SLEEP) == null ? new ArrayList<>()
				: kb_dotMap.get(CalMode.SLEEP);
		
		// 存储cc各个校准点
		list = kb_dotMap.get(CalMode.CC) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.CC);
		if(list!=null&&list.size()!=0) {
			for(CalParamData calParamData:list) {
				uploadTestDot.moduleIndex=moduleIndex;
				uploadTestDot.pole=PoleData.Pole.values()[calParamData.pole.ordinal()];
				uploadTestDot.range=calParamData.range;
				uploadTestDot.adc=calParamData.adc;
				uploadTestDot.adcK=calParamData.adcK;
				uploadTestDot.adcB=calParamData.adcB;
				uploadTestDot.programVal=calParamData.da;
				uploadTestDot.meterVal=calParamData.meter;
				uploadTestDot.programK=calParamData.programK;
				uploadTestDot.programB=calParamData.programB;
				datas.add(uploadTestDot);
			}
		}
		// 存储cv各个校准点
		list = kb_dotMap.get(CalMode.CV) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.CV);
		
		
		
		if(list!=null&&list.size()!=0) {
			
			for(int i=0;i<list.size()-cv1DotCount-cv2DotCount;i++) {
				uploadTestDot.moduleIndex=moduleIndex;
				uploadTestDot.pole=PoleData.Pole.values()[list.get(i).pole.ordinal()];
				uploadTestDot.range=list.get(i).range;
				uploadTestDot.adc=list.get(i).adc;
				uploadTestDot.adcK=list.get(i).adcK;
				uploadTestDot.adcB=list.get(i).adcB;
				uploadTestDot.programVal=list.get(i).da;
				uploadTestDot.meterVal=list.get(i).meter;
				uploadTestDot.programK=list.get(i).programK;
				uploadTestDot.programB=list.get(i).programB;
				if(cv1DotCount>0) {
					CalParamData sameDot=findSameDot(list.get(i),list).get(1);
					uploadTestDot.adc2=sameDot.adc;
					uploadTestDot.adcK2=sameDot.adcK;
					uploadTestDot.adcB2=sameDot.adcB;
				}
				
				if(cv2DotCount>0) {
					CalParamData sameDot=findSameDot(list.get(i),list).get(2);
					uploadTestDot.checkAdc=sameDot.adc;
					uploadTestDot.checkAdcK=sameDot.adcK;
					uploadTestDot.checkAdcB=sameDot.adcB;
				}
				datas.add(uploadTestDot);
			}
			
		}
		
		list = kb_dotMap.get(CalMode.DC) == null ? new ArrayList<>() : kb_dotMap.get(CalMode.DC);
		if(list!=null&&list.size()!=0) {
			for(CalParamData calParamData:list) {
				uploadTestDot.moduleIndex=moduleIndex;
				uploadTestDot.pole=PoleData.Pole.values()[calParamData.pole.ordinal()];
				uploadTestDot.range=calParamData.range;
				uploadTestDot.adc=calParamData.adc;
				uploadTestDot.adcK=calParamData.adcK;
				uploadTestDot.adcB=calParamData.adcB;
				uploadTestDot.programVal=calParamData.da;
				uploadTestDot.meterVal=calParamData.meter;
				uploadTestDot.programK=calParamData.programK;
				uploadTestDot.programB=calParamData.programB;
				datas.add(uploadTestDot);
			}
		}
		

		channel.getDebugDatas().clear();
		channel.getDebugDatas().addAll(datas);
		System.err.println("==================================");
	}

	private List<CalParamData> findSameDot(CalParamData calParamData, List<CalParamData> list) {
		List<CalParamData> sameDots=new ArrayList<>();
		for(CalParamData dot:list) {
			if(dot.da==calParamData.da && dot.pole==calParamData.pole && dot.range==calParamData.range) {
				sameDots.add(dot);
			}
		}
		return sameDots;
	}

	/**
	 * 实时更新线程
	 * 
	 * @author wavy_zheng 2022年3月30日
	 */
	private void createRefreshThread() {

	}

	private void refreshData() {

		if (channel != null) {

			if (!calTableViewer.getTable().isDisposed()) {
				//

				if (!channel.getDebugDatas().isEmpty()) {

					calTableViewer.getTable().setTopIndex(channel.getDebugDatas().size() - 1);
					calTableViewer.refresh();
				}
			}

		}
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	private void refreshLog() {

		if (!logText.isDisposed()) {

			List<TestLog> logs = new ArrayList<>();
			int count = channel.getLogs().size();
			logs.addAll(channel.getLogs().subList(count - 50 < 0 ? 0 : count - 50, count));

			for (TestLog log : logs) {

				appendLog(log);
			}

		}
	}

	public void appendLog(TestLog log) {

		if (logText.isDisposed()) {

			return;
		}
		int st = logText.getText().length();
		logText.append(CommonUtil.formatTime(log.getDate(), "yyyy/MM/dd HH:mm:ss") + ":" + log.getContent() + "\n");
		if (log.getLevel().equals("error")) {

			StyleRange sr = getHighlightStyle(st, logText.getText().length() - st, true, RED);
			logText.setStyleRange(sr);

		}
		logText.setSelection(logText.getText().length());
	}

	private StyleRange getHighlightStyle(int startOffset, int length, boolean bold, Color color) {

		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.fontStyle = bold ? SWT.BOLD : SWT.NORMAL;
		styleRange.foreground = color;

		return styleRange;
	}

	/**
	 * 写入当前调试页面的系数
	 * 
	 * @author wavy_zheng 2022年2月19日
	 * @throws Exception
	 */
	private void writeFlash() throws Exception {

		// 驱动板flash
		Map<DriverEnvironment.CalMode, List<CalParamData>> dotMap = new HashMap<>();

		List<CalParamData> checkList = new ArrayList<>();
		List<CalParamData> check2List = new ArrayList<>();

		int currentModuleIndex = -1;

		for (int n = 0; n < channel.getDebugDatas().size(); n++) {

			UploadTestDot calDot = channel.getDebugDatas().get(n);

			if (calDot.testType != TestType.Cal) {

				continue;
			}

			if (currentModuleIndex == -1) {

				currentModuleIndex = calDot.moduleIndex;
			}
			if (currentModuleIndex != calDot.moduleIndex || n == channel.getDebugDatas().size() - 1) {

				List<CalParamData> cvParams = dotMap.get(DriverEnvironment.CalMode.CV);
				List<CalParamData> dcParams = dotMap.get(DriverEnvironment.CalMode.DC);
				List<CalParamData> ccParams = dotMap.get(DriverEnvironment.CalMode.CC);
				if (cvParams == null) {

					cvParams = new ArrayList<>();
				}
				if (ccParams == null) {

					ccParams = new ArrayList<>();
				}
				if (dcParams == null) {

					dcParams = new ArrayList<>();
				}

				if (!checkList.isEmpty()) {

					cvParams.addAll(checkList);
				}
				if (!check2List.isEmpty()) {

					cvParams.addAll(check2List);
				}
				int cvTotalCount = cvParams.size();

				final TestLog log = new TestLog(channel.getDeviceChnIndex(), "DEBUG",
						"共写入cc" + ccParams.size() + ",cv " + (cvTotalCount - checkList.size() - check2List.size())
								+ ",dc" + dcParams.size() + ",cv1 " + checkList.size() + ",cv2 " + check2List.size(),
						new Date());

				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {

						appendLog(log);

					}

				});

				// 写入或者
				LogicFlashWrite2DebugData flash = new LogicFlashWrite2DebugData();

				flash.setChnIndex(channel.getChnIndex());
				flash.setModuleIndex(currentModuleIndex); // 选择模片写入
				flash.setKb_dotMap(dotMap);

				if (!ccParams.isEmpty()) {
					System.out.println("first cc adc :" + ccParams.get(0).adc);
				}

				flash.setCv1DotCount(checkList.size());
				flash.setCv2DotCount(check2List.size());

				int size = logic.getDevice().getCalBoxList().size();
				if (size == 0) {

					final TestLog msgLog = new TestLog(channel.getDeviceChnIndex(), "error", "该通道未绑定校准箱或者校准箱绑定数>1",
							new Date());
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {

							appendLog(msgLog);

						}

					});
				}
				// 写入
				WorkBench.getBoxService().cfgFlash(logic.getDevice().getCalBoxList().get(0), flash);

				// 清除
				dotMap.clear();

			}

			if (calDot.adcK != 0) {
				CalParamData cd = new CalParamData();
				cd.calMode = CalMode.values()[calDot.mode.ordinal()];
				cd.pole = Pole.values()[calDot.pole.ordinal()];
				cd.meter = calDot.meterVal;
				cd.adc = calDot.adc;
				cd.adcK = calDot.adcK;
				cd.adcB = calDot.adcB;
				cd.da = (int) calDot.programVal;
				cd.programK = calDot.programK;
				cd.programB = calDot.programB;
				cd.range = calDot.precision;

				if (dotMap.containsKey(cd.calMode)) {

					dotMap.get(cd.calMode).add(cd);

				} else {

					List<CalParamData> list = new ArrayList<>();
					list.add(cd);
					dotMap.put(cd.calMode, list);
				}

				if (calDot.mode == Logic2Environment.CalMode.CV) {

					if (calDot.checkAdc != 0) {

						CalParamData back1 = new CalParamData();
						back1.calMode = CalMode.values()[calDot.mode.ordinal()];
						back1.pole = Pole.values()[calDot.pole.ordinal()];
						back1.meter = calDot.meterVal;
						back1.adc = calDot.checkAdc;
						back1.adcK = calDot.checkAdcK;
						back1.adcB = calDot.checkAdcB;
						back1.da = (int) calDot.programVal;
						back1.programK = calDot.programK;
						back1.programB = calDot.programB;
						back1.range = calDot.precision;
						checkList.add(back1);
					}

					if (calDot.adc2 != 0) {

						CalParamData back2 = new CalParamData();
						back2.calMode = CalMode.values()[calDot.mode.ordinal()];
						back2.pole = Pole.values()[calDot.pole.ordinal()];
						back2.meter = calDot.meterVal;
						back2.adc = calDot.adc2;
						back2.adcK = calDot.adcK2;
						back2.adcB = calDot.adcB2;
						back2.da = (int) calDot.programVal;
						back2.programK = calDot.programK;
						back2.programB = calDot.programB;
						back2.range = calDot.precision;
						check2List.add(back2);

					}

				}
			}
		}

	}

	protected void writeFlash2() throws Exception {
		
		response.setChnIndex(0);
		WorkBench.getBoxService().cfgFlash(logic.getDevice().getCalBoxList().get(0), response);
	}

}
