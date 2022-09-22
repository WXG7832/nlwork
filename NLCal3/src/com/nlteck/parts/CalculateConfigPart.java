package com.nlteck.parts;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ToolItem;
import com.nlteck.dialog.CalculateConfigDialog;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.resources.Resources;
import com.nltecklib.protocol.li.PCWorkform.CalculatePlanData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalculatePlanMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import nlcal.NlteckCalEnvrionment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * 计量方案配置面板
 * 
 * @author caichao_tang
 *
 */
@SuppressWarnings("unchecked")
public class CalculateConfigPart {

	public static final String ID = "nlcal.partdescriptor.calculateConfig";
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private TableViewer tableViewer;
	private CalBox CalBox;

	@Inject
	public CalculateConfigPart() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0, 0);
		fd_toolBar.bottom = new FormAttachment(0, 40);
		fd_toolBar.left = new FormAttachment(0, 0);
		toolBar.setLayoutData(fd_toolBar);

		ToolItem newToolItem = new ToolItem(toolBar, SWT.NONE);
		newToolItem.setText("新建");
		newToolItem.setImage(Resources.NEW_SCHEMA_IMAGE);

		ToolItem saveToolItem = new ToolItem(toolBar, SWT.NONE);
		saveToolItem.setText("保存");
		saveToolItem.setImage(Resources.SAVE_SCHEMA_IMAGE);

		ToolItem importToolItem = new ToolItem(toolBar, SWT.NONE);
		importToolItem.setText("导入");
		importToolItem.setImage(Resources.IMPORT_SCHEMA_IMAGE);

		ToolItem sendToolItem = new ToolItem(toolBar, SWT.NONE);
		sendToolItem.setImage(Resources.SEND_SCHEMA_IMAGE);
		sendToolItem.setText("下发");

		ToolItem queryToolItem = new ToolItem(toolBar, SWT.NONE);
		queryToolItem.setImage(Resources.QUERY_SCHEMA_IMAGE);
		queryToolItem.setText("查询");

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem addToolItem = new ToolItem(toolBar, SWT.NONE);
		addToolItem.setImage(Resources.ADD_ITEM_IMAGE);
		addToolItem.setText("添加");

		ToolItem insertToolItem = new ToolItem(toolBar, SWT.NONE);
		insertToolItem.setImage(Resources.INSERT_ITEM_IMAGE);
		insertToolItem.setText("插入");

		ToolItem delToolItem = new ToolItem(toolBar, SWT.NONE);
		delToolItem.setImage(Resources.DEL_ITEM_IMAGE);
		delToolItem.setText("删除");

		ToolItem upToolItem = new ToolItem(toolBar, SWT.NONE);
		upToolItem.setImage(Resources.UP_ITEM_IMAGE);
		upToolItem.setText("上移");

		ToolItem downToolItem = new ToolItem(toolBar, SWT.NONE);
		downToolItem.setImage(Resources.DOWN_ITEM_IMAGE);
		downToolItem.setText("下移");

		ToolItem sortToolItem = new ToolItem(toolBar, SWT.NONE);
		sortToolItem.setImage(Resources.SORT_ITEM_IMAGE);
		sortToolItem.setText("排序");

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem configToolItem = new ToolItem(toolBar, SWT.NONE);
		configToolItem.setImage(Resources.SCHEMA_CONFIG_IMAGE);
		configToolItem.setText("配置");

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		FormData fd_table = new FormData();
		fd_table.right = new FormAttachment(100, 0);
		fd_table.top = new FormAttachment(0, 40);
		fd_table.bottom = new FormAttachment(100, 0);
		fd_table.left = new FormAttachment(0, 0);
		table.setLayoutData(fd_table);

		TableColumn tblclmnNo = new TableColumn(table, SWT.CENTER);
		tblclmnNo.setWidth(140);
		tblclmnNo.setText("序号");

		TableColumn tblclmnMode = new TableColumn(table, SWT.CENTER);
		tblclmnMode.setWidth(140);
		tblclmnMode.setText("模式");

		TableColumn tblclmnPole = new TableColumn(table, SWT.CENTER);
		tblclmnPole.setWidth(140);
		tblclmnPole.setText("极性");

		TableColumn tblclmnValue = new TableColumn(table, SWT.CENTER);
		tblclmnValue.setWidth(140);
		tblclmnValue.setText("计量点（mV/mA）");

		// 设置列属性
		tableViewer.setColumnProperties(new String[] { "no", "mode", "pole", "value" });

		// 设置cellEditor
		CellEditor[] cellEditors = new CellEditor[4];
		cellEditors[1] = new StringComboBoxCellEditor(tableViewer.getTable(), new String[] { "CC", "CV", "DC", "CV2" },
				SWT.READ_ONLY | SWT.BORDER | SWT.CENTER);
		cellEditors[2] = new StringComboBoxCellEditor(tableViewer.getTable(), new String[] { "REVERSE", "NORMAL" },
				SWT.READ_ONLY | SWT.BORDER | SWT.CENTER);
		cellEditors[3] = new TextCellEditor(tableViewer.getTable(), SWT.BORDER | SWT.CENTER);
		tableViewer.setCellEditors(cellEditors);

		// 设置cellModifier
		tableViewer.setCellModifier(new ICellModifier() {
			@Override
			public void modify(Object arg0, String arg1, Object arg2) {
				TableItem item = (TableItem) arg0;
				TableCalculateDot tableCalculateDot = (TableCalculateDot) item.getData();
				try {
					switch (arg1) {
					case "mode":
						tableCalculateDot.mode = CalMode.values()[(int) arg2 + 1];
						break;
					case "pole":
						tableCalculateDot.pole = Pole.values()[(int) arg2];
						break;
					case "value":
						tableCalculateDot.value = Double.parseDouble(arg2.toString());
						break;
					default:
						return;
					}
				} catch (Exception e) {
					if (!(e instanceof NumberFormatException))
						e.printStackTrace();
				}
				tableViewer.refresh();
			}

			@Override
			public Object getValue(Object arg0, String arg1) {
				TableCalculateDot tableCalculateDot = (TableCalculateDot) arg0;
				switch (arg1) {
				case "mode":
					return tableCalculateDot.mode.name();
				case "pole":
					return tableCalculateDot.pole.name();
				case "value":
					return tableCalculateDot.value + "";
				default:
					return null;
				}
			}

			@Override
			public boolean canModify(Object arg0, String arg1) {
				if (arg1.equals("no"))
					return false;
				return true;
			}
		});

		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem insertMenuItem = new MenuItem(menu, SWT.NONE);
		insertMenuItem.setText("上方插入");

		MenuItem delMenuItem = new MenuItem(menu, SWT.NONE);
		delMenuItem.setText("删除当前");

		MenuItem addMenuItem = new MenuItem(menu, SWT.NONE);
		addMenuItem.setText("末尾添加");

		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem upMenuItem = new MenuItem(menu, SWT.NONE);
		upMenuItem.setText("向上移动");

		MenuItem downMenuItem = new MenuItem(menu, SWT.NONE);
		downMenuItem.setText("向下移动");

		// ***************************************************************************************
		//
		// 监听器开始
		//
		// ***************************************************************************************
		newToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TableCalculateDot> tableCalculateDotList = new ArrayList<>();
				tableCalculateDotList.add(new TableCalculateDot());
				tableViewer.setInput(tableCalculateDotList);
			}
		});

		configToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new CalculateConfigDialog(parent.getShell(), SWT.NONE).open();
			}
		});

		saveToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
				// 打开文件保存窗口
				FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.SAVE);
				fileDialog.setFilterExtensions(new String[] { "*.xml" });
				fileDialog.setFileName("calculatePlan_" + SIMPLE_DATE_FORMAT.format(new Date()));
				String filePath = fileDialog.open();
				if (filePath == null || filePath.isEmpty()) {
					return;
				}
				// 删除同名文件
				new File(filePath).delete();
				StringBuffer info = new StringBuffer();
				if (writeCalculateToXML(filePath, info))
					MessageDialog.openInformation(parent.getShell(), "操作成功", "已保存至文件！");
				else
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
			}
		});

		importToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.xml" });
				String filePath = fileDialog.open();
				if (filePath == null) {
					return;
				}
				StringBuffer info = new StringBuffer();
				if (readXMLToCalculate(filePath, info)) {
					show();
					MessageDialog.openInformation(parent.getShell(), "操作成功", "已导入文件！");
				} else
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
			}
		});

		sendToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
				StringBuffer info = new StringBuffer();
				if (WorkBench.configCommand(CalBox, WorkBench.calculatePlanData, 5000, info)) {
					MessageDialog.openInformation(parent.getShell(), "操作成功", "配置计量点方案成功！");
				} else {
					MessageDialog.openError(parent.getShell(), "操作失败", "配置计量点方案失败！\n" + info.toString());
				}
			}
		});

		queryToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// StringBuffer info = new StringBuffer();
				// CalculatePlanData receiveData = (CalculatePlanData)
				// WorkBench.queryCommand(CalBox, new CalculatePlanData(), 5000, info);
				// if (receiveData == null) {
				// MessageDialog.openError(parent.getShell(), "操作失败", "查询计量点方案失败！\n" +
				// info.toString());
				// return;
				// }
				// WorkBench.calculatePlanData = receiveData;
				// show();
				// MessageDialog.openInformation(parent.getShell(), "操作成功", "查询计量点方案成功！");
			}
		});

		insertToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection().length == 0)
					return;
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				TableCalculateDot tableCalculateDot = new TableCalculateDot();
				tableCalculateDot.mode = currentDot.mode;
				tableCalculateDot.pole = currentDot.pole;
				tableCalculateDot.value = currentDot.value;
				itemList.add(currentIndex - 1, tableCalculateDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
				table.setSelection(currentIndex - 1);
			}
		});

		addToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				if (itemList == null)
					itemList = new ArrayList<>();
				TableCalculateDot tableCalculateDot = new TableCalculateDot();
				tableCalculateDot.index = itemList.size() + 1;
				itemList.add(tableCalculateDot);
				tableViewer.setInput(itemList);
				table.setSelection(itemList.size() - 1);
			}
		});

		delToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection().length == 0)
					return;
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				itemList.remove(currentIndex - 1);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
			}
		});

		upToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection().length == 0)
					return;
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				if (currentDot.index == 1)
					return;
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				TableCalculateDot beforeDot = itemList.get(currentIndex - 2);
				itemList.set(currentIndex - 1, beforeDot);
				itemList.set(currentIndex - 2, currentDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
			}
		});

		downToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelection().length == 0)
					return;
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				if (currentIndex == itemList.size())
					return;
				TableCalculateDot afterDot = itemList.get(currentIndex);
				itemList.set(currentIndex - 1, afterDot);
				itemList.set(currentIndex, currentDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
			}
		});

		sortToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sortTableDotList((List<TableCalculateDot>) tableViewer.getInput());
				tableViewer.refresh();
			}
		});

		insertMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				TableCalculateDot tableCalculateDot = new TableCalculateDot();
				tableCalculateDot.mode = currentDot.mode;
				tableCalculateDot.pole = currentDot.pole;
				tableCalculateDot.value = currentDot.value;
				itemList.add(currentIndex - 1, tableCalculateDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
				table.setSelection(currentIndex - 1);
			}
		});

		delMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				itemList.remove(currentIndex - 1);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
			}
		});

		addMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				TableCalculateDot tableCalculateDot = new TableCalculateDot();
				tableCalculateDot.index = itemList.size() + 1;
				itemList.add(tableCalculateDot);
				tableViewer.setInput(itemList);
				table.setSelection(itemList.size() - 1);
			}
		});

		upMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				TableCalculateDot beforeDot = itemList.get(currentIndex - 2);
				itemList.set(currentIndex - 1, beforeDot);
				itemList.set(currentIndex - 2, currentDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
			}
		});

		downMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableCalculateDot currentDot = ((TableCalculateDot) table.getSelection()[0].getData());
				int currentIndex = currentDot.index;
				List<TableCalculateDot> itemList = (List<TableCalculateDot>) tableViewer.getInput();
				TableCalculateDot afterDot = itemList.get(currentIndex);
				itemList.set(currentIndex - 1, afterDot);
				itemList.set(currentIndex, currentDot);
				// 更新序号
				for (int i = 0; i < itemList.size(); i++) {
					itemList.get(i).index = i + 1;
				}
				tableViewer.setInput(itemList);
			}
		});

		/**
		 * 动态更新表格编辑菜单
		 */
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem[] tableItems = table.getSelection();
				if (tableItems.length == 0) {
					insertMenuItem.setEnabled(false);
					delMenuItem.setEnabled(false);
					addMenuItem.setEnabled(false);
					upMenuItem.setEnabled(false);
					downMenuItem.setEnabled(false);
					return;
				}
				TableCalculateDot selectItem = (TableCalculateDot) table.getSelection()[0].getData();
				if (((List<TableCalculateDot>) tableViewer.getInput()).size() == 1) {
					insertMenuItem.setEnabled(true);
					delMenuItem.setEnabled(true);
					addMenuItem.setEnabled(true);
					upMenuItem.setEnabled(false);
					downMenuItem.setEnabled(false);
				} else if (selectItem.index == 1) {
					insertMenuItem.setEnabled(true);
					delMenuItem.setEnabled(true);
					addMenuItem.setEnabled(true);
					upMenuItem.setEnabled(false);
					downMenuItem.setEnabled(true);
				} else if (selectItem.index == ((List<TableCalculateDot>) tableViewer.getInput()).size()) {
					insertMenuItem.setEnabled(true);
					delMenuItem.setEnabled(true);
					addMenuItem.setEnabled(true);
					upMenuItem.setEnabled(true);
					downMenuItem.setEnabled(false);
				} else {
					insertMenuItem.setEnabled(true);
					delMenuItem.setEnabled(true);
					addMenuItem.setEnabled(true);
					upMenuItem.setEnabled(true);
					downMenuItem.setEnabled(true);
				}
			}
		});

		// ***********************************************************
		//
		// 监听器结束
		//
		// ***********************************************************

		// 初始化销毁
		if (!NlteckCalEnvrionment.isDevelopMode)
			configToolItem.dispose();
	}

	/**
	 * 输出workbench中计量数据到XML文件中
	 * 
	 * @param filePath
	 * @param info
	 * @return
	 */
	public static boolean writeCalculateToXML(String filePath, StringBuffer info) {
		CalculatePlanData calculatePlanData = WorkBench.calculatePlanData;
		List<CalculatePlanMode> calculatePlanModeList = calculatePlanData.getModes();
		Document document = DocumentHelper.createDocument();
		// 根节点
		Element rootElement = document.addElement("calculateSchema");
		// 一级节点
		Element calculatePlanDataElement = rootElement.addElement("calculatePlanData");
		calculatePlanDataElement.addAttribute("maxMeterOffset", calculatePlanData.getMaxMeterOffset() + "");
		calculatePlanDataElement.addAttribute("maxAdcOffset", calculatePlanData.getMaxAdcOffset() + "");
		calculatePlanDataElement.addAttribute("minCalculateCurrent", calculatePlanData.getMinCalculateCurrent() + "");
		calculatePlanDataElement.addAttribute("maxCalculateCurrent", calculatePlanData.getMaxCalculateCurrent() + "");
		calculatePlanDataElement.addAttribute("minCalculateVoltage", calculatePlanData.getMinCalculateVoltage() + "");
		calculatePlanDataElement.addAttribute("maxCalculateVoltage", calculatePlanData.getMaxCalculateVoltage() + "");
		for (int i = 0; i < calculatePlanModeList.size(); i++) {
			CalculatePlanMode calculatePlanMode = calculatePlanModeList.get(i);
			// 二级节点
			Element modeElement = calculatePlanDataElement.addElement("calculatePlanMode");
			modeElement.addAttribute("mode", calculatePlanMode.mode.ordinal() + "");
			modeElement.addAttribute("pole", calculatePlanMode.pole.ordinal() + "");
			modeElement.addAttribute("disabled", calculatePlanMode.disabled + "");
			// 三级节点
			Element dotsElement = modeElement.addElement("dots");
			List<Double> dotList = calculatePlanMode.dots;
			for (int j = 0; j < dotList.size(); j++) {
				// 四级节点
				Element dotElement = dotsElement.addElement("dot");
				dotElement.addAttribute("value", dotList.get(j) + "");
			}
		}
		// 输出格式刷
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		// 设置文件编码
		outputFormat.setEncoding("UTF-8");
		// 设置文件的输出流
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(filePath);
			// 创建xmlWriter
			XMLWriter xmlWriter = new XMLWriter(outputStream, outputFormat);
			// 写入文件到XML
			xmlWriter.write(document);
			// 清空缓存，关闭资源
			xmlWriter.flush();
			xmlWriter.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			info.append(e.getMessage());
			return false;
		}
	}

	/**
	 * 读取计量的XML到workbench
	 * 
	 * @param filePath
	 * @param info
	 * @return
	 */
	public static boolean readXMLToCalculate(String filePath, StringBuffer info) {
		Document document = null;
		try {
			document = new SAXReader().read(new File(filePath));
		} catch (DocumentException e) {
			e.printStackTrace();
			info.append(e.getMessage());
			return false;
		}
		CalculatePlanData calculatePlanData = WorkBench.calculatePlanData;
		List<CalculatePlanMode> calibratePlanModeList = new ArrayList<>();
		// 根节点
		Element rootElement = document.getRootElement();
		// 一级节点
		Element calculatePlanDataElement = rootElement.element("calculatePlanData");
		calculatePlanData
				.setMaxMeterOffset(Double.parseDouble(calculatePlanDataElement.attribute("maxMeterOffset").getValue()));
		calculatePlanData
				.setMaxAdcOffset(Double.parseDouble(calculatePlanDataElement.attribute("maxAdcOffset").getValue()));
		calculatePlanData.setMinCalculateCurrent(
				Double.parseDouble(calculatePlanDataElement.attribute("minCalculateCurrent").getValue()));
		calculatePlanData.setMaxCalculateCurrent(
				Double.parseDouble(calculatePlanDataElement.attribute("maxCalculateCurrent").getValue()));
		calculatePlanData.setMinCalculateVoltage(
				Double.parseDouble(calculatePlanDataElement.attribute("minCalculateVoltage").getValue()));
		calculatePlanData.setMaxCalculateVoltage(
				Double.parseDouble(calculatePlanDataElement.attribute("maxCalculateVoltage").getValue()));
		List<Element> modeElementList = calculatePlanDataElement.elements();
		for (int i = 0; i < modeElementList.size(); i++) {
			// 二级节点
			Element modeElement = modeElementList.get(i);
			CalculatePlanMode calculatePlanMode = new CalculatePlanMode();
			calculatePlanMode.mode = CalMode.values()[Integer.parseInt(modeElement.attribute("mode").getValue())];
			calculatePlanMode.pole = Pole.values()[Integer.parseInt(modeElement.attribute("pole").getValue())];
			calculatePlanMode.disabled = Boolean.parseBoolean(modeElement.attribute("disabled").getValue());
			List<Element> dotElementList = modeElement.element("dots").elements();
			for (int j = 0; j < dotElementList.size(); j++) {
				// 四级节点
				calculatePlanMode.dots.add(Double.parseDouble(dotElementList.get(j).attribute("value").getValue()));
			}
			calibratePlanModeList.add(calculatePlanMode);
		}
		calculatePlanData.setModes(calibratePlanModeList);
		return true;
	}

	private class StringComboBoxCellEditor extends ComboBoxCellEditor {
		public StringComboBoxCellEditor(Composite parent, String[] items, int style) {
			super(parent, items, style);
		}

		@Override
		protected void doSetValue(Object value) {
			if (value instanceof String) {
				((CCombo) getControl()).setText((String) value);
			} else {
				super.doSetValue(value);
			}
		}

		@Override
		protected Object doGetValue() {
			final Object value = super.doGetValue();
			if (value instanceof Integer && (Integer) value == -1) {
				return ((CCombo) getControl()).getText();
			}
			return value;
		}
	}

	/**
	 * 在表格界面中的计量点
	 * 
	 * @author caichao_tang
	 *
	 */
	private class TableCalculateDot {
		public int index = 1;
		public CalMode mode = CalMode.CC;
		public Pole pole = Pole.NORMAL;
		public double value;
	}

	private class LabelProvider implements ITableLabelProvider {
		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			TableCalculateDot row = (TableCalculateDot) element;
			switch (columnIndex) {
			case 0:
				return row.index + "";
			case 1:
				return row.mode.name();
			case 2:
				return row.pole.name();
			case 3:
				return row.value + "";
			default:
				return null;
			}
		}
	}

	/**
	 * 保存界面数据到内存
	 */
	private void save() {
		List<TableCalculateDot> tableCalculateDotList = (List<TableCalculateDot>) tableViewer.getInput();
		// 转换表格数据到协议数据
		List<CalculatePlanMode> modeList = new ArrayList<>();
		for (TableCalculateDot tableDot : tableCalculateDotList) {
			CalMode calMode = tableDot.mode;
			Pole pole = tableDot.pole;
			CalculatePlanMode mode = null;
			for (CalculatePlanMode calculatePlanMode : modeList) {
				if (calculatePlanMode.mode == calMode && calculatePlanMode.pole == pole) {
					mode = calculatePlanMode;
					break;
				}
			}
			if (mode == null) {
				mode = new CalculatePlanMode();
				mode.disabled = false;
				mode.mode = calMode;
				mode.pole = pole;
				modeList.add(mode);
			}
			mode.dots.add(tableDot.value);
		}
		WorkBench.calculatePlanData.setModes(modeList);
	}

	/**
	 * 展示内存数据到界面
	 */
	private void show() {
		List<TableCalculateDot> tableDotList = new ArrayList<>();
		List<CalculatePlanMode> modeList = WorkBench.calculatePlanData.getModes();
		for (CalculatePlanMode calculatePlanMode : modeList) {
			CalMode calMode = calculatePlanMode.mode;
			Pole pole = calculatePlanMode.pole;
			if (calculatePlanMode.disabled)
				continue;
			for (Double value : calculatePlanMode.dots) {
				TableCalculateDot dot = new TableCalculateDot();
				dot.mode = calMode;
				dot.pole = pole;
				dot.value = value;
				tableDotList.add(dot);
			}
		}
		sortTableDotList(tableDotList);
		tableViewer.setInput(tableDotList);
	}

	/**
	 * 表格排序
	 */
	private void sortTableDotList(List<TableCalculateDot> dotList) {
		Collections.sort(dotList, new Comparator<TableCalculateDot>() {
			@Override
			public int compare(TableCalculateDot o1, TableCalculateDot o2) {
				if (o1.mode.ordinal() < o2.mode.ordinal())
					return -1;
				else
					return 1;
			}
		});
		Collections.sort(dotList, new Comparator<TableCalculateDot>() {
			@Override
			public int compare(TableCalculateDot o1, TableCalculateDot o2) {
				if (o1.mode.ordinal() == o2.mode.ordinal() && o1.pole.ordinal() > o2.pole.ordinal())
					return -1;
				else
					return 1;
			}
		});
		Collections.sort(dotList, new Comparator<TableCalculateDot>() {
			@Override
			public int compare(TableCalculateDot o1, TableCalculateDot o2) {
				if (o1.mode.ordinal() == o2.mode.ordinal() && o1.pole.ordinal() == o2.pole.ordinal()
						&& o1.value < o2.value)
					return -1;
				else
					return 1;
			}
		});
		// 更新序号
		for (int i = 0; i < dotList.size(); i++) {
			dotList.get(i).index = i + 1;
		}
	}
}