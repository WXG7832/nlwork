package com.nlteck.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.CalculatePlanDotDO;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.table.TableViewerEx;
import com.nlteck.swtlib.table.TableViewerEx.EditCtrlType;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.table.report.CSVReport;
import com.nlteck.utils.EnumUtil;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculatePlanMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月23日 下午2:57:45 计量报表方案
 */
public class CalculateDotDlg extends Dialog {

	private final int dialogWidth = 1300;
	private final int dialogHeight = 900;

	private TableViewerEx tableViewer;

	private final static int calculateDotDecimal = 1;

	private Device device;

	private ToolBar toolbar;

	public CalculateDotDlg(Shell parentShell, Device device) {
		super(parentShell);

		this.device = device;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setImage(Resources.START_CALC_IMAGE);
		newShell.setText("计量方案");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(dialogWidth, dialogHeight);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		return null;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());

		// centerComposite();

		createTopPanel(container);

		Composite composite = new Composite(container, SWT.NONE);

		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 42);
		fd_scrolledComposite.bottom = new FormAttachment(100, 0);
		fd_scrolledComposite.left = new FormAttachment(0, 0);
		fd_scrolledComposite.right = new FormAttachment(100, 0);
		composite.setLayoutData(fd_scrolledComposite);

		composite.setLayout(new FillLayout());

		tableViewer = new TableViewerEx(composite, true, true);
		List<String> headers = new ArrayList<>();
		headers.add("序号");
		headers.add("模式");
		headers.add("极性");
		headers.add("计量点");
		List<Integer> widths = new ArrayList<>();
		widths.add(60);
		widths.add(80);
		widths.add(80);
		widths.add(160);

		tableViewer.setHeaders(headers, widths);

		tableViewer.setColumnEditType(1, EditCtrlType.COMBO);
		tableViewer.setColumnEditType(2, EditCtrlType.COMBO);
		tableViewer.setColumnEditType(3, EditCtrlType.SPINNER);
		tableViewer.setColumnEditDecimal(3, calculateDotDecimal);
		tableViewer.setColumnEditRange(3, 0, Integer.MAX_VALUE);

		tableViewer.setColumnEditContent(1, new String[] { "CC", "CV", "DC", "CV2" });
		tableViewer.setColumnEditContent(2, new String[] { "-", "+" });

		tableViewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener paramILabelProviderListener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isLabelProperty(Object paramObject, String paramString) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener paramILabelProviderListener) {
				// TODO Auto-generated method stub

			}

			@Override
			public Image getColumnImage(Object object, int paramInt) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getColumnText(Object object, int columnIndex) {

				CalculatePlanDotDO p = (CalculatePlanDotDO) object;
				String text = "";
				switch (columnIndex) {

				case 0:
					text = p.getIndex() + "";
					break;
				case 1:
					text = p.getMode() == null ? "" : (p.getMode().name() + "");
					break;
				case 2:
					text = p.getPole() == null ? "" : (p.getPole() == Pole.NORMAL ? "+" : "-");
					break;
				case 3:
					text = p.getCalculateDot() + "";
					break;

				}

				return text;
			}

		});

		// 内存中加载数据显示界面
		show();

		centerComposite(container.getShell());

		return container;
	}

	public void centerComposite(Composite composite) {

		Rectangle measureRect = Display.getDefault().getPrimaryMonitor().getBounds();
		composite.setBounds((measureRect.width - dialogWidth) / 2, (measureRect.height - dialogHeight) / 2, dialogWidth,
				dialogHeight);
	}

	/**
	 * 创建操作栏
	 * 
	 * @author wavy_zheng 2021年1月25日
	 * @param parent
	 * @return
	 */
	private Composite createTopPanel(Composite parent) {

		Composite panel = new Composite(parent, SWT.NONE);
		FormData fd_toolBarcomposite = new FormData();
		fd_toolBarcomposite.top = new FormAttachment(0, 0);
		fd_toolBarcomposite.bottom = new FormAttachment(0, 42);
		fd_toolBarcomposite.left = new FormAttachment(0, 0);
		fd_toolBarcomposite.right = new FormAttachment(100, 0);
		panel.setLayoutData(fd_toolBarcomposite);
		panel.setLayout(new FillLayout());

		toolbar = new ToolBar(panel, SWT.FLAT | SWT.RIGHT);

		ToolItem newToolItem = new ToolItem(toolbar, SWT.NONE);
		newToolItem.setText("新建");
		newToolItem.setImage(Resources.NEW_SCHEMA_IMAGE);

		new ToolItem(toolbar, SWT.SEPARATOR);

		ToolItem addToolItem = new ToolItem(toolbar, SWT.NONE);
		addToolItem.setImage(Resources.ADD_PLAN_ITEM_IMAGE);
		addToolItem.setText("添加");

		ToolItem removeToolItem = new ToolItem(toolbar, SWT.NONE);
		removeToolItem.setImage(Resources.DEL_PLAN_ITEM_IMAGE);
		removeToolItem.setText("删除");

		ToolItem upToolItem = new ToolItem(toolbar, SWT.NONE);
		upToolItem.setImage(Resources.UP_ITEM_IMAGE);
		upToolItem.setText("上移");

		ToolItem downToolItem = new ToolItem(toolbar, SWT.NONE);
		downToolItem.setImage(Resources.DOWN_ITEM_IMAGE);
		downToolItem.setText("下移");

		ToolItem sortToolItem = new ToolItem(toolbar, SWT.NONE);
		sortToolItem.setImage(Resources.SORT_ITEM_IMAGE);
		sortToolItem.setText("排序");

		new ToolItem(toolbar, SWT.SEPARATOR);

		ToolItem importToolItem = new ToolItem(toolbar, SWT.NONE);
		importToolItem.setImage(Resources.IMPORT_SCHEMA_IMAGE);
		importToolItem.setText("导入");

		ToolItem exportToolItem = new ToolItem(toolbar, SWT.NONE);
		exportToolItem.setImage(Resources.IMPORT_SCHEMA_IMAGE);
		exportToolItem.setText("导出");

		new ToolItem(toolbar, SWT.SEPARATOR);

		ToolItem sendToolItem = new ToolItem(toolbar, SWT.NONE);
		sendToolItem.setImage(Resources.SEND_SCHEMA_IMAGE);
		sendToolItem.setText("下发");

		ToolItem queryToolItem = new ToolItem(toolbar, SWT.NONE);
		queryToolItem.setImage(Resources.QUERY_SCHEMA_IMAGE);
		queryToolItem.setText("查询");

		newToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<CalculatePlanDotDO> alculateDotList = new ArrayList<>();
				tableViewer.setInput(alculateDotList);
			}
		});

		addToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();

				if (itemList == null) {
					itemList = new ArrayList<>();
				}

				CalculatePlanDotDO tableCalculateDot = new CalculatePlanDotDO();
				tableCalculateDot.setIndex(itemList.size() + 1);

				CalMode current = CalMode.CC;
				Pole currentPole = Pole.NORMAL;
				double calculateVal = 0d;

				if (itemList.size() > 0) {
					current = itemList.get(itemList.size() - 1).getMode();
					calculateVal = itemList.get(itemList.size() - 1).getCalculateDot();
					currentPole = itemList.get(itemList.size() - 1).getPole();
				}

				tableCalculateDot.setMode(current);
				tableCalculateDot.setPole(currentPole);
				tableCalculateDot.setCalculateDot(calculateVal);
				itemList.add(tableCalculateDot);

				tableViewer.setInput(itemList);
				tableViewer.getTable().setSelection(itemList.size() - 1);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		removeToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CalculatePlanDotDO selectDot = (CalculatePlanDotDO) tableViewer.getStructuredSelection()
						.getFirstElement();
				if (selectDot == null) {

					return;
				}
				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();
				int index = itemList.indexOf(selectDot);
				// 删除
				itemList.remove(selectDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).setIndex(i + 1);
				}
				tableViewer.setInput(itemList);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		upToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				CalculatePlanDotDO selectDot = (CalculatePlanDotDO) tableViewer.getStructuredSelection()
						.getFirstElement();
				if (selectDot == null) {

					return;
				}

				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();

				int index = itemList.indexOf(selectDot);
				if (index > 0) {

					swapItems(itemList, index - 1, index);
				}

				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).setIndex(i + 1);
				}
				tableViewer.setInput(itemList);
			}
		});

		downToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				CalculatePlanDotDO selectDot = (CalculatePlanDotDO) tableViewer.getStructuredSelection()
						.getFirstElement();
				if (selectDot == null) {

					return;
				}

				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();

				int index = itemList.indexOf(selectDot);
				if (index != -1 && index < itemList.size() - 1) {

					swapItems(itemList, index, index + 1);
				}

				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).setIndex(i + 1);
				}
				tableViewer.setInput(itemList);
			}
		});

		sortToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();

				// 排序调整顺序
				sortCalculatePlanDots(itemList);

				tableViewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		importToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.csv" });
				String filePath = fileDialog.open();
				if (filePath == null) {
					return;
				}

				Map<Integer, String[]> map = CSVReport.importCSV(filePath, new String[] { "序号", "模式", "极性", "计量点" });

				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();
				itemList.clear();

				for (Integer key : map.keySet()) {
					itemList.add(new CalculatePlanDotDO(Integer.valueOf(map.get(key)[0]),
							EnumUtil.getEnumByName(CalMode.class, map.get(key)[1]),
							EnumUtil.getEnumByName(Pole.class, map.get(key)[2]), Double.parseDouble(map.get(key)[3])));
				}

				// 排序调整顺序
				sortCalculatePlanDots(itemList);

				tableViewer.refresh();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		exportToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				exportToolItem.setEnabled(false);

				List<CalculatePlanDotDO> itemList = (List<CalculatePlanDotDO>) tableViewer.getInput();

				String[] titles = new String[] { "序号", "模式", "极性", "计量点" };
				String[] propertys = new String[] { "index", "mode", "pole", "calculateDot" };

				try {

					FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					fd.setFilterExtensions(new String[] { "*.csv" });
					String result = fd.open();
					if (result != null && !"".equals(result)) {

						// 排序调整顺序
						sortCalculatePlanDots(itemList);

						CSVReport.exportCsv(result, titles, propertys, itemList);
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "操作成功", "导出成功！！");
					}

				} catch (Exception e1) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "操作失败", "导出失败！" + e1.getMessage());
				} finally {
					exportToolItem.setEnabled(true);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		sendToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					saveCalculatePlan();
					WorkBench.getBoxService().configCalculatePlan(device);
				} catch (Exception e2) {
					MessageDialog.openError(parent.getShell(), "操作失败", e2.getMessage());
					return;
				}

				MessageDialog.openInformation(parent.getShell(), "操作成功", "配置计量点方案成功！");

			}
		});

		queryToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (device.getCalBoxList().isEmpty()) {

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "设备" + device.getName() + "未绑定校准箱");
					return;
				}

				for (CalBox calbox : device.getCalBoxList()) {

					if (!calbox.isConnected()) {

						MyMsgDlg.openErrorDialog(getShell(), "操作失败", "校准箱" + calbox.getName() + "已断开连接");
						return;
					}

					try {
						WorkBench.getBoxService().readCalculatePlan(calbox);
					} catch (Exception e1) {

						e1.printStackTrace();
						MyMsgDlg.openErrorDialog(getShell(), "操作失败", e1.getMessage());
						return;
					}

				}

				CalculatePlanData plan = device.getCalBoxList().get(0).getCalculatePlan();
				if (plan != null) {

					List<CalculatePlanDotDO> list = new ArrayList<>();
					int index = 0;
					for (CalculatePlanMode mode : plan.getModes()) {

						for (Double dot : mode.dots) {
							CalculatePlanDotDO cpdd = new CalculatePlanDotDO();
							cpdd.setCalculateDot(dot);
							cpdd.setMode(mode.mode);
							cpdd.setPole(mode.pole);
							cpdd.setIndex(index++);
							list.add(cpdd);
						}
					}
					tableViewer.setInput(list);
					tableViewer.refresh();
					MyMsgDlg.openInfoDialog(getShell(), "操作成功", "读取计量方案成功!", false);

				} else {

					MyMsgDlg.openErrorDialog(getShell(), "操作失败", "未读取到计量方案");
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

		return panel;

	}

	private void saveCalculatePlan() throws Exception {

		List<CalculatePlanDotDO> calculateDotList = (List<CalculatePlanDotDO>) tableViewer.getInput();
		// 转换表格数据到协议数据
		Map<String, CalculatePlanMode> modeMap = new HashMap<>();

		// 排序调整顺序
		sortCalculatePlanDots(calculateDotList);

		for (CalculatePlanDotDO dot : calculateDotList) {

			System.out.println(dot.toString());
			CalMode calMode = dot.getMode();
			Pole pole = dot.getPole();
			if (calMode == null) {
				throw new Exception("模式不允许为空！");

			}
			if (pole == null) {
				throw new Exception("极性不允许为空！");
			}

			CalculatePlanMode cpm = modeMap.get(calMode.name() + "-" + pole.name());
			if (cpm == null) {

				cpm = new CalculatePlanMode();
				cpm.mode = calMode;
				cpm.pole = pole;
				modeMap.put(calMode.name() + "-" + pole.name(), cpm);
			}
			cpm.dots.add(dot.getCalculateDot());
			
			

		}

		List<CalculatePlanMode> list = new ArrayList<>();
		// 转换
		CalculatePlanMode mode = modeMap.get(CalMode.CC.name() + "-" + Pole.NORMAL.name());
		if (mode != null) {

			list.add(mode);
		}
		mode = modeMap.get(CalMode.CC.name() + "-" + Pole.REVERSE.name());
		if (mode != null) {

			list.add(mode);
		}
		mode = modeMap.get(CalMode.CV.name() + "-" + Pole.NORMAL.name());
		if (mode != null) {

			list.add(mode);
		}
		mode = modeMap.get(CalMode.CV.name() + "-" + Pole.REVERSE.name());
		if (mode != null) {

			list.add(mode);
		}
		mode = modeMap.get(CalMode.DC.name() + "-" + Pole.NORMAL.name());
		if (mode != null) {

			list.add(mode);
		}
		mode = modeMap.get(CalMode.DC.name() + "-" + Pole.REVERSE.name());
		if (mode != null) {

			list.add(mode);
		}

		CalculatePlanData plan = new CalculatePlanData();
		plan.setModes(list);
		device.setCalculatePlan(plan); // 设置到设备

	}

	/**
	 * 展示内存数据到界面
	 */
	private void show() {

		List<CalculatePlanDotDO> tableDotList = new ArrayList<>();
		List<CalculatePlanMode> modeList = WorkBench.calculatePlanData.getModes();
		for (CalculatePlanMode calculatePlanMode : modeList) {
			CalMode calMode = calculatePlanMode.mode;
			Pole pole = calculatePlanMode.pole;
			if (calculatePlanMode.disabled)
				continue;
			for (Double value : calculatePlanMode.dots) {
				CalculatePlanDotDO dot = new CalculatePlanDotDO();
				dot.setMode(calMode);
				dot.setPole(pole);
				dot.setCalculateDot(value);
				tableDotList.add(dot);
			}
		}
		sortCalculatePlanDots(tableDotList);
		tableViewer.setInput(tableDotList);
	}

	/**
	 * 对计量报表进行排序
	 */
	private void sortCalculatePlanDots(List<CalculatePlanDotDO> dotList) {

		Collections.sort(dotList, new Comparator<CalculatePlanDotDO>() {
			@Override
			public int compare(CalculatePlanDotDO o1, CalculatePlanDotDO o2) {

				if (o1.getMode().ordinal() != o2.getMode().ordinal()) {

					return o1.getMode().ordinal() - o2.getMode().ordinal();
				} else {

					if (o1.getPole() != o2.getPole()) {

						return o2.getPole().ordinal() - o1.getPole().ordinal();
					} else {

						double val = o1.getCalculateDot() - o2.getCalculateDot();
						if (val == 0) {

							return 0;
						} else if (val < 0) {

							return -1;
						} else {

							return 1;
						}

					}

				}

			}

		});
		// 更新序号
		for (int i = 0; i < dotList.size(); i++) {
			dotList.get(i).setIndex(i + 1);
		}

	}

	/**
	 * 交换表格中的两项
	 * 
	 * @author wavy_zheng 2021年2月8日
	 * @param items
	 * @param index1
	 * @param index2
	 */
	private void swapItems(List<CalculatePlanDotDO> items, int index1, int index2) {

		CalculatePlanDotDO temp = items.get(index1);
		items.set(index1, items.get(index2));
		items.set(index2, temp);

	}

}
