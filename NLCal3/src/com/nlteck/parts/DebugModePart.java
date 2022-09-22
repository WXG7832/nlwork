
package com.nlteck.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nltecklib.protocol.li.PCWorkform.LogicCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.LogicCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CalMatchData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalibrateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckCalculateDebugData;
import com.nltecklib.protocol.li.PCWorkform.CheckFlashWriteData;
import com.nltecklib.protocol.li.PCWorkform.LogicFlashWriteData;
import com.nltecklib.protocol.li.check2.Check2Environment.AdcGroup;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.check2.Check2Environment.Work;
import com.nltecklib.protocol.li.logic2.Logic2CalMatchData.AdcData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalculateAdcGroup;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalibrateAdcGroup;
import com.nltecklib.protocol.li.logic2.Logic2FlashWriteData.CalDot;
import com.nltecklib.protocol.li.main.PoleData.Pole;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;

/**
 * 调试模式面板
 * 
 * @author caichao_tang
 *
 */
public class DebugModePart {
	public static final String ID = "nlcal.partdescriptor.debugModePart";
	private final String[] volTypeStrings = new String[] { "Backup,NORMAL", "Power,NORMAL", "Backup,REVERSE",
			"Power,REVERSE" };
	public static Text logText;
	private CalBox calBox;

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(parent, SWT.SMOOTH | SWT.VERTICAL);

		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER | SWT.FLAT | SWT.BOTTOM);
		tabFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		// *************************************************************************************************************
		// *
		// *逻辑板flash调试开始
		// *
		// *************************************************************************************************************

		CTabItem tbtmLogicflash = new CTabItem(tabFolder, SWT.NONE);
		tbtmLogicflash.setText("逻辑板Flash");

		tabFolder.setSelection(tbtmLogicflash);

		Composite logicFlashComposite = new Composite(tabFolder, SWT.NONE);
		tbtmLogicflash.setControl(logicFlashComposite);
		logicFlashComposite.setLayout(new FormLayout());

		TableViewer logicFlashTableViewer = new TableViewer(logicFlashComposite, SWT.FULL_SELECTION);
		logicFlashTableViewer.setContentProvider(new ArrayContentProvider());
		logicFlashTableViewer.setLabelProvider(new LabelProviderForLogic());
		Table logicFlashTable = logicFlashTableViewer.getTable();
		FormData fd_logicFlashTable = new FormData();
		fd_logicFlashTable.bottom = new FormAttachment(100, 0);
		fd_logicFlashTable.right = new FormAttachment(100, -100);
		fd_logicFlashTable.top = new FormAttachment(0, 0);
		fd_logicFlashTable.left = new FormAttachment(0, 0);
		logicFlashTable.setLayoutData(fd_logicFlashTable);
		logicFlashTable.setLinesVisible(true);
		logicFlashTable.setHeaderVisible(true);

		TableColumn tblclmnMode = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnMode.setWidth(100);
		tblclmnMode.setText("模式");

		TableColumn tblclmnPole = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnPole.setWidth(100);
		tblclmnPole.setText("极性");

		TableColumn tblclmnLevel = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnLevel.setWidth(100);
		tblclmnLevel.setText("档位");

		TableColumn tblclmnAdcvalue = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnAdcvalue.setWidth(100);
		tblclmnAdcvalue.setText("adc采样值");

		TableColumn tblclmnNewColumn = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("adc k");

		TableColumn tblclmnAdcB = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnAdcB.setWidth(100);
		tblclmnAdcB.setText("adc b");

		TableColumn tblclmnDa = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnDa.setWidth(100);
		tblclmnDa.setText("da");

		TableColumn tblclmnMeter = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnMeter.setWidth(100);
		tblclmnMeter.setText("万用表值");

		TableColumn tblclmnProgramK = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnProgramK.setWidth(100);
		tblclmnProgramK.setText("程控 k");

		TableColumn tblclmnProgramB = new TableColumn(logicFlashTable, SWT.CENTER);
		tblclmnProgramB.setWidth(100);
		tblclmnProgramB.setText("程控 b");

		// 设置列属性
		logicFlashTableViewer.setColumnProperties(
				new String[] { "mode", "pole", "level", "adc", "adck", "adcb", "da", "meter", "pk", "pb" });

		// 设置cellEditor
		CellEditor[] cellEditors = new CellEditor[10];
		List<String> modeList = new ArrayList<>();
		for (CalMode calMode : CalMode.values())
			modeList.add(calMode.name());
		cellEditors[0] = new StringComboBoxCellEditor(logicFlashTableViewer.getTable(),
				modeList.toArray(new String[] {}), SWT.READ_ONLY | SWT.BORDER);
		List<String> poleList = new ArrayList<>();
		for (Pole calMode : Pole.values())
			poleList.add(calMode.name());
		cellEditors[1] = new StringComboBoxCellEditor(logicFlashTableViewer.getTable(),
				poleList.toArray(new String[] {}), SWT.READ_ONLY | SWT.BORDER);
		cellEditors[2] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[3] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[4] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[5] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[6] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[7] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[8] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		cellEditors[9] = new TextCellEditor(logicFlashTableViewer.getTable(), SWT.BORDER);
		logicFlashTableViewer.setCellEditors(cellEditors);

		// 设置cellModifier
		logicFlashTableViewer.setCellModifier(new ICellModifier() {
			@Override
			public void modify(Object arg0, String arg1, Object arg2) {
				TableItem item = (TableItem) arg0;
				CalDot calDot = (CalDot) item.getData();
				try {
					switch (arg1) {
					case "mode":
						calDot.mode = CalMode.values()[(int) arg2];
						break;
					case "pole":
						calDot.pole = Pole.values()[(int) arg2];
						break;
					case "level":
						calDot.level = Integer.parseInt(arg2.toString());
						break;
					case "adc":
						calDot.adc = Double.parseDouble(arg2.toString());
						break;
					case "adck":
						calDot.adcK = Double.parseDouble(arg2.toString());
						break;
					case "adcb":
						calDot.adcB = Double.parseDouble(arg2.toString());
						break;
					case "da":
						calDot.da = Long.parseLong(arg2.toString());
						break;
					case "meter":
						calDot.meter = Double.parseDouble(arg2.toString());
						break;
					case "pk":
						calDot.programK = Double.parseDouble(arg2.toString());
						break;
					case "pb":
						calDot.programB = Double.parseDouble(arg2.toString());
						break;
					default:
						return;
					}
				} catch (Exception e) {
					if (!(e instanceof NumberFormatException))
						e.printStackTrace();
				}
				logicFlashTableViewer.refresh();
			}

			@Override
			public Object getValue(Object arg0, String arg1) {
				CalDot calDot = (CalDot) arg0;
				switch (arg1) {
				case "mode":
					return calDot.mode.name();
				case "pole":
					return calDot.pole.name();
				case "level":
					return calDot.level + "";
				case "adc":
					return calDot.adc + "";
				case "adck":
					return calDot.adcK + "";
				case "adcb":
					return calDot.adcB + "";
				case "da":
					return calDot.da + "";
				case "meter":
					return calDot.meter + "";
				case "pk":
					return calDot.programK + "";
				case "pb":
					return calDot.programB + "";
				default:
					return null;
				}
			}

			@Override
			public boolean canModify(Object arg0, String arg1) {
				return true;
			}
		});

		Composite logicRightComposite = new Composite(logicFlashComposite, SWT.NONE);
		logicRightComposite.setLayout(new GridLayout(1, false));
		FormData fd_logicRightComposite = new FormData();
		fd_logicRightComposite.top = new FormAttachment(0, 0);
		fd_logicRightComposite.left = new FormAttachment(100, -100);
		fd_logicRightComposite.right = new FormAttachment(100, 0);
		fd_logicRightComposite.bottom = new FormAttachment(100, 0);
		logicRightComposite.setLayoutData(fd_logicRightComposite);

		Label logicNoLabel = new Label(logicRightComposite, SWT.NONE);
		logicNoLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		logicNoLabel.setText("分区号：");
		logicNoLabel.setAlignment(SWT.CENTER);

		Spinner logicNoSpinner = new Spinner(logicRightComposite, SWT.BORDER);
		logicNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label chnNoLabel = new Label(logicRightComposite, SWT.NONE);
		chnNoLabel.setAlignment(SWT.CENTER);
		chnNoLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		chnNoLabel.setText("通道号：");

		Spinner chnNoSpinner = new Spinner(logicRightComposite, SWT.BORDER);
		chnNoSpinner.setMaximum(10000);
		chnNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button logicClearButton = new Button(logicRightComposite, SWT.NONE);
		logicClearButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		logicClearButton.setText("清空");

		Button logicAddButton = new Button(logicRightComposite, SWT.NONE);
		logicAddButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		logicAddButton.setText("添加");

		Button logicQueryButton = new Button(logicRightComposite, SWT.NONE);
		logicQueryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		logicQueryButton.setText("查询 ");

		Button logicConfigButton = new Button(logicRightComposite, SWT.NONE);
		logicConfigButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		logicConfigButton.setText("配置");

		logicClearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logicFlashTableViewer.setInput(null);
			}
		});

		logicAddButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<CalDot> calDotList = (List<CalDot>) logicFlashTableViewer.getInput();
				if (calDotList == null)
					calDotList = new ArrayList<>();
				CalDot calDot = new CalDot();
				calDot.mode = CalMode.CC;
				calDot.pole = Pole.NORMAL;
				calDot.level = 1;
				calDot.adc = 0;
				calDot.adcK = 0;
				calDot.adcB = 0;
				calDot.da = 0;
				calDot.meter = 0;
				calDot.programK = 0;
				calDot.programB = 0;
				calDotList.add(calDot);
				logicFlashTableViewer.setInput(calDotList);
			}
		});

		logicQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// LogicFlashWriteData sendData = new LogicFlashWriteData();
				// sendData.setUnitIndex(logicNoSpinner.getSelection());
				// sendData.setChnIndex(chnNoSpinner.getSelection());
				// StringBuffer info = new StringBuffer();
				// LogicFlashWriteData receiveData = (LogicFlashWriteData)
				// WorkBench.queryCommand(calBox, sendData, 2000, info);
				// if (receiveData == null) {
				// MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
				// return;
				// }
				// logicFlashTableViewer.setInput(receiveData.getDots());
			}
		});

		logicConfigButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogicFlashWriteData sendData = new LogicFlashWriteData();
				sendData.setUnitIndex(logicNoSpinner.getSelection());
				sendData.setChnIndex(chnNoSpinner.getSelection());
				sendData.setDots((List<CalDot>) logicFlashTableViewer.getInput());
				StringBuffer info = new StringBuffer();
				if (WorkBench.configCommand(calBox, sendData, 2000, info))
					MessageDialog.openInformation(parent.getShell(), "操作成功", "已成功发送配置！");
				else
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
			}
		});

		// *************************************************************************************************************
		// *
		// *逻辑板flash调试结束
		// *
		// *************************************************************************************************************

		// *************************************************************************************************************
		// *
		// *回检flash调试开始
		// *
		// *************************************************************************************************************

		CTabItem tbtmRecheckflash = new CTabItem(tabFolder, SWT.NONE);
		tbtmRecheckflash.setText("回检板Flash");

		Composite recheckFlashComposite = new Composite(tabFolder, SWT.NONE);
		tbtmRecheckflash.setControl(recheckFlashComposite);
		recheckFlashComposite.setLayout(new FormLayout());

		TableViewer reCheckTableViewer = new TableViewer(recheckFlashComposite, SWT.FULL_SELECTION);
		reCheckTableViewer.setContentProvider(new ArrayContentProvider());
		reCheckTableViewer.setLabelProvider(new LabelProviderForReCheck());
		Table reCheckTable = reCheckTableViewer.getTable();
		FormData fd_reCheckTable = new FormData();
		fd_reCheckTable.bottom = new FormAttachment(100, 0);
		fd_reCheckTable.right = new FormAttachment(100, -100);
		fd_reCheckTable.top = new FormAttachment(0, 0);
		fd_reCheckTable.left = new FormAttachment(0, 0);
		reCheckTable.setLayoutData(fd_reCheckTable);
		reCheckTable.setLinesVisible(true);
		reCheckTable.setHeaderVisible(true);

		TableColumn calDotMode = new TableColumn(reCheckTable, SWT.CENTER);
		calDotMode.setWidth(160);
		calDotMode.setText("校准点类型");

		TableColumn adcValueColumn = new TableColumn(reCheckTable, SWT.CENTER);
		adcValueColumn.setWidth(160);
		adcValueColumn.setText("adc采样值");

		TableColumn adcKColumn = new TableColumn(reCheckTable, SWT.CENTER);
		adcKColumn.setWidth(160);
		adcKColumn.setText("adc k");

		TableColumn adcBColumn = new TableColumn(reCheckTable, SWT.CENTER);
		adcBColumn.setWidth(160);
		adcBColumn.setText("adc b");

		// 设置列属性
		reCheckTableViewer.setColumnProperties(new String[] { "type", "adc", "adck", "adcb" });

		// 设置cellEditor
		CellEditor[] cellEditorsForReCheck = new CellEditor[4];
		cellEditorsForReCheck[0] = new StringComboBoxCellEditor(reCheckTableViewer.getTable(), volTypeStrings,
				SWT.READ_ONLY | SWT.BORDER);
		cellEditorsForReCheck[1] = new TextCellEditor(reCheckTableViewer.getTable(), SWT.BORDER);
		cellEditorsForReCheck[2] = new TextCellEditor(reCheckTableViewer.getTable(), SWT.BORDER);
		cellEditorsForReCheck[3] = new TextCellEditor(reCheckTableViewer.getTable(), SWT.BORDER);
		reCheckTableViewer.setCellEditors(cellEditorsForReCheck);

		// 设置cellModifier
		reCheckTableViewer.setCellModifier(new ICellModifier() {
			@Override
			public void modify(Object arg0, String arg1, Object arg2) {
				TableItem item = (TableItem) arg0;
				ReCheckFlashData reCheckFlashData = (ReCheckFlashData) item.getData();
				try {
					switch (arg1) {
					case "type":
						reCheckFlashData.voltageType = volTypeStrings[(int) arg2];
						break;
					case "adc":
						reCheckFlashData.adc = Double.parseDouble(arg2.toString());
						break;
					case "adck":
						reCheckFlashData.adck = Double.parseDouble(arg2.toString());
						break;
					case "adcb":
						reCheckFlashData.adcb = Double.parseDouble(arg2.toString());
						break;
					default:
						return;
					}
				} catch (Exception e) {
					if (!(e instanceof NumberFormatException))
						e.printStackTrace();
				}
				reCheckTableViewer.refresh();
			}

			@Override
			public Object getValue(Object arg0, String arg1) {
				ReCheckFlashData reCheckFlashData = (ReCheckFlashData) arg0;
				switch (arg1) {
				case "type":
					return reCheckFlashData.voltageType;
				case "adc":
					return reCheckFlashData.adc + "";
				case "adck":
					return reCheckFlashData.adck + "";
				case "adcb":
					return reCheckFlashData.adcb + "";
				default:
					return null;
				}
			}

			@Override
			public boolean canModify(Object arg0, String arg1) {
				return true;
			}
		});

		Composite recheckRightComposite = new Composite(recheckFlashComposite, SWT.NONE);
		recheckRightComposite.setLayout(new GridLayout(1, false));
		FormData fd_recheckRightComposite = new FormData();
		fd_recheckRightComposite.bottom = new FormAttachment(100, 0);
		fd_recheckRightComposite.top = new FormAttachment(0, 0);
		fd_recheckRightComposite.left = new FormAttachment(100, -100);
		fd_recheckRightComposite.right = new FormAttachment(100, 0);
		recheckRightComposite.setLayoutData(fd_recheckRightComposite);

		Label lblNewLabel = new Label(recheckRightComposite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("分区号：");

		Spinner logicNoSpinner2 = new Spinner(recheckRightComposite, SWT.BORDER);
		logicNoSpinner2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_1 = new Label(recheckRightComposite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("通道号：");

		Spinner chnNoSpinner2 = new Spinner(recheckRightComposite, SWT.BORDER);
		chnNoSpinner2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button reCheckClearButton = new Button(recheckRightComposite, SWT.NONE);
		reCheckClearButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		reCheckClearButton.setText("清空");

		Button reCheckAddButton = new Button(recheckRightComposite, SWT.NONE);
		reCheckAddButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		reCheckAddButton.setText("添加");

		Button reCheckQueryButton = new Button(recheckRightComposite, SWT.NONE);
		reCheckQueryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		reCheckQueryButton.setText("查询");

		Button reCheckConfigButton = new Button(recheckRightComposite, SWT.NONE);
		reCheckConfigButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		reCheckConfigButton.setText("配置");

		reCheckClearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reCheckTableViewer.setInput(null);
			}
		});

		reCheckAddButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<ReCheckFlashData> reCheckFlashDataList = (List<ReCheckFlashData>) reCheckTableViewer.getInput();
				if (reCheckFlashDataList == null)
					reCheckFlashDataList = new ArrayList<>();
				ReCheckFlashData reCheckFlashData = new ReCheckFlashData();
				reCheckFlashData.voltageType = volTypeStrings[0];
				reCheckFlashData.adc = 0;
				reCheckFlashData.adck = 0;
				reCheckFlashData.adcb = 0;
				reCheckFlashDataList.add(reCheckFlashData);
				reCheckTableViewer.setInput(reCheckFlashDataList);
			}
		});

		reCheckQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				CheckFlashWriteData sendData = new CheckFlashWriteData();
//				sendData.setUnitIndex(logicNoSpinner2.getSelection());
//				sendData.setChnIndex(chnNoSpinner2.getSelection());
//				StringBuffer info = new StringBuffer();
//				CheckFlashWriteData receiveData = (CheckFlashWriteData) WorkBench.queryCommand(calBox, sendData, 2000,
//						info);
//				List<ReCheckFlashData> reCheckFlashDataList = new ArrayList<>();
//				if (receiveData == null)
//					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
//				else {
//					Map<String, List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot>> string_dotMap = receiveData
//							.getCalDotMap();
//					for (String volType : string_dotMap.keySet()) {
//						List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot> dotList = string_dotMap
//								.get(volType);
//						for (com.nltecklib.protocol.li.check2.Check2Environment.CalDot calDot : dotList) {
//							ReCheckFlashData reCheckFlashData = new ReCheckFlashData();
//							reCheckFlashData.voltageType = volType;
//							reCheckFlashData.adc = calDot.adc;
//							reCheckFlashData.adck = calDot.adcK;
//							reCheckFlashData.adcb = calDot.adcB;
//							reCheckFlashDataList.add(reCheckFlashData);
//						}
//					}
//					// 更新到表格
//					reCheckTableViewer.setInput(reCheckFlashDataList);
//				}
			}
		});

		reCheckConfigButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				CheckFlashWriteData sendData = new CheckFlashWriteData();
				List<ReCheckFlashData> reCheckFlashDataList = (List<ReCheckFlashData>) reCheckTableViewer.getInput();
				sendData.setUnitIndex(logicNoSpinner2.getSelection());
				sendData.setChnIndex(chnNoSpinner2.getSelection());
				List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot> back_normal_calDotList = new ArrayList<>();
				List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot> back_reverse_calDotList = new ArrayList<>();
				List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot> power_normal_calDotList = new ArrayList<>();
				List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot> power_reverse_calDotList = new ArrayList<>();
				for (ReCheckFlashData reCheckFlashData : reCheckFlashDataList) {
					com.nltecklib.protocol.li.check2.Check2Environment.CalDot calDot = new com.nltecklib.protocol.li.check2.Check2Environment.CalDot();
					switch (reCheckFlashData.voltageType) {
					case "Backup,NORMAL":
						calDot.adc = reCheckFlashData.adc;
						calDot.adcK = reCheckFlashData.adck;
						calDot.adcB = reCheckFlashData.adcb;
						back_normal_calDotList.add(calDot);
						break;
					case "Power,NORMAL":
						calDot.adc = reCheckFlashData.adc;
						calDot.adcK = reCheckFlashData.adck;
						calDot.adcB = reCheckFlashData.adcb;
						back_reverse_calDotList.add(calDot);
						break;
					case "Backup,REVERSE":
						calDot.adc = reCheckFlashData.adc;
						calDot.adcK = reCheckFlashData.adck;
						calDot.adcB = reCheckFlashData.adcb;
						power_normal_calDotList.add(calDot);
						break;
					case "Power,REVERSE":
						calDot.adc = reCheckFlashData.adc;
						calDot.adcK = reCheckFlashData.adck;
						calDot.adcB = reCheckFlashData.adcb;
						power_reverse_calDotList.add(calDot);
						break;
					default:
						break;
					}
				}
				Map<String, List<com.nltecklib.protocol.li.check2.Check2Environment.CalDot>> string_dotMap = new HashMap<>();
				string_dotMap.put(volTypeStrings[0], back_normal_calDotList);
				string_dotMap.put(volTypeStrings[1], back_reverse_calDotList);
				string_dotMap.put(volTypeStrings[2], power_normal_calDotList);
				string_dotMap.put(volTypeStrings[3], power_reverse_calDotList);
				sendData.setCalDotMap(string_dotMap);
				StringBuffer info = new StringBuffer();
				if (WorkBench.configCommand(calBox, sendData, 2000, info))
					MessageDialog.openInformation(parent.getShell(), "操作成功", "下发配置成功！");
				else
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
			}
		});

		// *************************************************************************************************************
		// *
		// *回检板flash调试结束
		// *
		// *************************************************************************************************************

		// *************************************************************************************************************
		// *
		// *校准、计量调试开始
		// *
		// *************************************************************************************************************

		CTabItem tbtmCalDebug = new CTabItem(tabFolder, SWT.NONE);
		tbtmCalDebug.setText("校准/计量");

		Composite calDebug = new Composite(tabFolder, SWT.NONE);
		tbtmCalDebug.setControl(calDebug);
		calDebug.setLayout(new FormLayout());

		Group calibrateGroup = new Group(calDebug, SWT.BORDER);
		calibrateGroup.setLayout(new FormLayout());
		calibrateGroup.setText("校准调试");
		FormData fd_calibrateGroup = new FormData();
		fd_calibrateGroup.bottom = new FormAttachment(100, 0);
		fd_calibrateGroup.left = new FormAttachment(0, 0);
		fd_calibrateGroup.right = new FormAttachment(50, 0);
		fd_calibrateGroup.top = new FormAttachment(0, 0);
		calibrateGroup.setLayoutData(fd_calibrateGroup);

		TableViewer calibrateTableViewer = new TableViewer(calibrateGroup, SWT.BORDER | SWT.FULL_SELECTION);
		calibrateTableViewer.setContentProvider(new ArrayContentProvider());
		calibrateTableViewer.setLabelProvider(new LabelProviderForCalibrate());
		Table calibrateTable = calibrateTableViewer.getTable();
		calibrateTable.setLinesVisible(true);
		calibrateTable.setHeaderVisible(true);
		FormData fd_calibrateTable = new FormData();
		fd_calibrateTable.bottom = new FormAttachment(100, 0);
		fd_calibrateTable.right = new FormAttachment(100, -200);
		fd_calibrateTable.top = new FormAttachment(0, 0);
		fd_calibrateTable.left = new FormAttachment(0, 0);
		calibrateTable.setLayoutData(fd_calibrateTable);

		TableColumn tblclmnNewColumn_1 = new TableColumn(calibrateTable, SWT.CENTER);
		tblclmnNewColumn_1.setWidth(180);
		tblclmnNewColumn_1.setText("adc1");

		TableColumn tblclmnNewColumn_12 = new TableColumn(calibrateTable, SWT.CENTER);
		tblclmnNewColumn_12.setWidth(180);
		tblclmnNewColumn_12.setText("adc2");

		Composite calibrateDebugcomposite = new Composite(calibrateGroup, SWT.NONE);
		calibrateDebugcomposite.setLayout(new GridLayout(2, false));
		FormData fd_calibrateDebugcomposite = new FormData();
		fd_calibrateDebugcomposite.bottom = new FormAttachment(100, 0);
		fd_calibrateDebugcomposite.left = new FormAttachment(100, -200);
		fd_calibrateDebugcomposite.top = new FormAttachment(0, 0);
		fd_calibrateDebugcomposite.right = new FormAttachment(100, 0);
		calibrateDebugcomposite.setLayoutData(fd_calibrateDebugcomposite);

		Label lblNewLabel_2 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("分区号：");

		Spinner calibrateLogicNoSpinner = new Spinner(calibrateDebugcomposite, SWT.BORDER);
		calibrateLogicNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_3 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("通道号：");

		Spinner calibrateChnNoSpinner = new Spinner(calibrateDebugcomposite, SWT.BORDER);
		calibrateChnNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_4 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("极性：");

		String[] calPoleStrings = new String[Pole.values().length];
		for (int i = 0; i < calPoleStrings.length; i++) {
			calPoleStrings[i] = Pole.values()[i].name();
		}
		Combo calibratePoleCombo = new Combo(calibrateDebugcomposite, SWT.READ_ONLY);
		calibratePoleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		calibratePoleCombo.setItems(calPoleStrings);
		calibratePoleCombo.select(0);

		Label lblNewLabel_5 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5.setText("工作模式：");

		String[] calModeStrings = new String[CalMode.values().length];
		for (int i = 0; i < calModeStrings.length; i++) {
			calModeStrings[i] = CalMode.values()[i].name();
		}
		Combo calibrateWorkmodeCombo = new Combo(calibrateDebugcomposite, SWT.READ_ONLY);
		calibrateWorkmodeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		calibrateWorkmodeCombo.setItems(calModeStrings);
		calibrateWorkmodeCombo.select(0);

		Label lblNewLabel_6 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_6.setText("精度：");

		Spinner precisionSpinner = new Spinner(calibrateDebugcomposite, SWT.BORDER);
		precisionSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_7 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_7.setText("程控电压：");

		Spinner programVolSpinner = new Spinner(calibrateDebugcomposite, SWT.BORDER);
		programVolSpinner.setMaximum(10000000);
		programVolSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_8 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_8.setText("程控电流：");

		Spinner programCurSpinner = new Spinner(calibrateDebugcomposite, SWT.BORDER);
		programCurSpinner.setMaximum(10000000);
		programCurSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_9 = new Label(calibrateDebugcomposite, SWT.NONE);
		lblNewLabel_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_9.setText("adc采集数：");

		Spinner adcValueCountSpinner = new Spinner(calibrateDebugcomposite, SWT.BORDER);
		adcValueCountSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button calibrateQueryButton = new Button(calibrateDebugcomposite, SWT.NONE);
		calibrateQueryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		calibrateQueryButton.setText("查询");

		Button calibrateConfigButton = new Button(calibrateDebugcomposite, SWT.NONE);
		calibrateConfigButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		calibrateConfigButton.setText("配置");

		calibrateQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				LogicCalibrateDebugData sendData = new LogicCalibrateDebugData();
//				sendData.setUnitIndex(calibrateLogicNoSpinner.getSelection());
//				sendData.setChnIndex(calibrateChnNoSpinner.getSelection());
//				StringBuffer info = new StringBuffer();
//				LogicCalibrateDebugData receiveData = (LogicCalibrateDebugData) WorkBench.queryCommand(calBox, sendData,
//						2000, info);
//				if (receiveData == null) {
//					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
//					return;
//				}
//				calibratePoleCombo.select(receiveData.getPole().ordinal());
//				precisionSpinner.setSelection(receiveData.getPrecision());
//				programVolSpinner.setSelection((int) receiveData.getProgramV());
//				programCurSpinner.setSelection((int) receiveData.getProgramI());
//				adcValueCountSpinner.setSelection(receiveData.getAdcs().size());
//				calibrateTableViewer.setInput(receiveData.getAdcs());
			}
		});

		calibrateConfigButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogicCalibrateDebugData sendData = new LogicCalibrateDebugData();
				sendData.setUnitIndex(calibrateLogicNoSpinner.getSelection());
				sendData.setChnIndex(calibrateChnNoSpinner.getSelection());
				sendData.setPole(Pole.values()[calibratePoleCombo.getSelectionIndex()]);
				sendData.setWorkMode(CalMode.values()[calibrateWorkmodeCombo.getSelectionIndex()]);
				sendData.setPrecision(precisionSpinner.getSelection());
				sendData.setProgramV(programVolSpinner.getSelection());
				sendData.setProgramI(programCurSpinner.getSelection());
				List<CalibrateAdcGroup> adcList = new ArrayList<>();
				for (int i = 0; i < adcValueCountSpinner.getSelection(); i++) {
					CalibrateAdcGroup adc = new CalibrateAdcGroup();
					adcList.add(adc);
				}
				sendData.setAdcs(adcList);
				StringBuffer info = new StringBuffer();
				if (WorkBench.configCommand(calBox, sendData, 2000, info)) {
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
					return;
				}
				MessageDialog.openInformation(parent.getShell(), "操作成功", "配置成功！");
			}
		});

		// **********************************************************计量分割线*************************************************************

		Group calculateGroup = new Group(calDebug, SWT.BORDER);
		calculateGroup.setText("计量调试");
		calculateGroup.setLayout(new FormLayout());
		FormData fd_calculateGroup = new FormData();
		fd_calculateGroup.bottom = new FormAttachment(100, 0);
		fd_calculateGroup.top = new FormAttachment(0, 0);
		fd_calculateGroup.right = new FormAttachment(100, 0);
		fd_calculateGroup.left = new FormAttachment(50, 0);
		calculateGroup.setLayoutData(fd_calculateGroup);

		TableViewer calculateTableViewer = new TableViewer(calculateGroup, SWT.BORDER | SWT.FULL_SELECTION);
		calculateTableViewer.setContentProvider(new ArrayContentProvider());
		calculateTableViewer.setLabelProvider(new LabelProviderForCalculate());
		Table calculateTable = calculateTableViewer.getTable();
		calculateTable.setLinesVisible(true);
		calculateTable.setHeaderVisible(true);
		FormData fd_calculateTable = new FormData();
		fd_calculateTable.bottom = new FormAttachment(100, 0);
		fd_calculateTable.top = new FormAttachment(0, 0);
		fd_calculateTable.left = new FormAttachment(0, 0);
		fd_calculateTable.right = new FormAttachment(100, -200);
		calculateTable.setLayoutData(fd_calculateTable);

		TableColumn tblclmnNewColumn_2 = new TableColumn(calculateTable, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("adc1：");

		TableColumn tblclmnNewColumn_21 = new TableColumn(calculateTable, SWT.NONE);
		tblclmnNewColumn_21.setWidth(100);
		tblclmnNewColumn_21.setText("adc2：");

		TableColumn tblclmnNewColumn_3 = new TableColumn(calculateTable, SWT.NONE);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("最终adc：");

		Composite calculateDebugComposite = new Composite(calculateGroup, SWT.NONE);
		calculateDebugComposite.setLayout(new GridLayout(2, false));
		FormData fd_calculateDebugComposite = new FormData();
		fd_calculateDebugComposite.right = new FormAttachment(100, 0);
		fd_calculateDebugComposite.top = new FormAttachment(0, 0);
		fd_calculateDebugComposite.left = new FormAttachment(100, -200);
		fd_calculateDebugComposite.bottom = new FormAttachment(100, 0);
		calculateDebugComposite.setLayoutData(fd_calculateDebugComposite);

		Label lblNewLabel_10 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_10.setText("分区号：");

		Spinner calculateLogicSpinner = new Spinner(calculateDebugComposite, SWT.BORDER);
		calculateLogicSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_11 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_11.setText("通道号：");

		Spinner calculateChnSpinner = new Spinner(calculateDebugComposite, SWT.BORDER);
		calculateChnSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_12 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_12.setText("极性：");

		Combo calculatePoleCombo = new Combo(calculateDebugComposite, SWT.READ_ONLY);
		calculatePoleCombo.setItems(calPoleStrings);
		calculatePoleCombo.select(0);
		calculatePoleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_13 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_13.setText("模式：");

		Combo calculateModeCombo = new Combo(calculateDebugComposite, SWT.READ_ONLY);
		calculateModeCombo.setItems(calModeStrings);
		calculateModeCombo.select(0);
		calculateModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_14 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_14.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_14.setText("计量 点：");

		Spinner calculateDotSpinner = new Spinner(calculateDebugComposite, SWT.BORDER);
		calculateDotSpinner.setMaximum(1000000);
		calculateDotSpinner.setDigits(2);
		calculateDotSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_15 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_15.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_15.setText("程控k：");

		Text programKText = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		programKText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_16 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_16.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_16.setText("程控b：");

		Text programBText = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		programBText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_17 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_17.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_17.setText("程控值：");

		Text programValueText = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		programValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_18 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_18.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_18.setText("adc k 1：");

		Text calculateAdcKText = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		calculateAdcKText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_19 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_19.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_19.setText("adc b 1：");

		Text calculateAdcBText = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		calculateAdcBText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_188 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_188.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_188.setText("adc k 2：");

		Text calculateAdcKText2 = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		calculateAdcKText2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_198 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_198.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_198.setText("adc b 2：");

		Text calculateAdcBText2 = new Text(calculateDebugComposite, SWT.BORDER | SWT.READ_ONLY);
		calculateAdcBText2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_20 = new Label(calculateDebugComposite, SWT.NONE);
		lblNewLabel_20.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_20.setText("采集个数：");

		Spinner calculateQueryNumSpinner = new Spinner(calculateDebugComposite, SWT.BORDER);
		calculateQueryNumSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button calculateQueryButton = new Button(calculateDebugComposite, SWT.NONE);
		calculateQueryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		calculateQueryButton.setText("查询");

		Button calculateConfigButton = new Button(calculateDebugComposite, SWT.NONE);
		calculateConfigButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		calculateConfigButton.setText("配置");

		calculateQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// LogicCalculateDebugData sendData = new LogicCalculateDebugData();
				// sendData.setUnitIndex(calculateLogicSpinner.getSelection());
				// sendData.setChnIndex(chnNoSpinner2.getSelection());
				// StringBuffer info = new StringBuffer();
				// LogicCalculateDebugData receiveData = (LogicCalculateDebugData)
				// WorkBench.queryCommand(calBox, sendData, 2000, info);
				// if (receiveData == null) {
				// MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
				// return;
				// }
				// calculatePoleCombo.select(receiveData.getPole().ordinal());
				// calculateModeCombo.select(receiveData.getWorkMode().ordinal());
				// calculateDotSpinner.setSelection((int) (receiveData.getCalculateDot() *
				// 100));
				// programKText.setText(receiveData.getProgramK() + "");
				// programBText.setText(receiveData.getProgramB() + "");
				// programValueText.setText(receiveData.getProgramVal() + "");
				// calculateAdcKText.setText(receiveData.getAdcK1() + "");
				// calculateAdcBText.setText(receiveData.getAdcB1() + "");
				// calculateAdcKText2.setText(receiveData.getAdcK2() + "");
				// calculateAdcKText2.setText(receiveData.getAdcB2() + "");
				// calculateQueryNumSpinner.setSelection(receiveData.getGroups().size());
				// calculateTableViewer.setInput(receiveData.getGroups());
			}
		});

		calculateConfigButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogicCalculateDebugData sendData = new LogicCalculateDebugData();
				sendData.setUnitIndex(calculateLogicSpinner.getSelection());
				sendData.setChnIndex(chnNoSpinner2.getSelection());
				sendData.setPole(Pole.values()[calculatePoleCombo.getSelectionIndex()]);
				sendData.setWorkMode(CalMode.values()[calculateModeCombo.getSelectionIndex()]);
				sendData.setCalculateDot(calculateDotSpinner.getSelection() / 100.0);
				List<CalculateAdcGroup> adcList = new ArrayList<>();
				for (int i = 0; i < calculateQueryNumSpinner.getSelection(); i++) {
					CalculateAdcGroup adcGroup = new CalculateAdcGroup();
					adcList.add(adcGroup);
				}
				StringBuffer info = new StringBuffer();
				if (WorkBench.configCommand(calBox, sendData, 2000, info)) {
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
					return;
				}
				MessageDialog.openInformation(parent.getShell(), "操作成功", "配置成功！");
			}
		});

		// *************************************************************************************************************
		// *
		// *校准计量调试结束
		// *
		// *************************************************************************************************************

		// *************************************************************************************************************
		// *
		// *校准对接识别开始
		// *
		// *************************************************************************************************************

		CTabItem matchCTabItem = new CTabItem(tabFolder, SWT.NONE);
		matchCTabItem.setText("对接识别");

		Composite matchComposite = new Composite(tabFolder, SWT.NONE);
		matchCTabItem.setControl(matchComposite);
		matchComposite.setLayout(new GridLayout(3, false));

		TableViewer matchTableViewer = new TableViewer(matchComposite, SWT.BORDER | SWT.FULL_SELECTION);
		matchTableViewer.setContentProvider(new ArrayContentProvider());
		matchTableViewer.setLabelProvider(new LabelProviderForMatch());
		Table matchTable = matchTableViewer.getTable();
		matchTable.setLinesVisible(true);
		matchTable.setHeaderVisible(true);
		matchTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		TableColumn tblclmnNewColumn_4 = new TableColumn(matchTable, SWT.CENTER);
		tblclmnNewColumn_4.setWidth(200);
		tblclmnNewColumn_4.setText("通道序号");

		TableColumn tblclmnNewColumn_41 = new TableColumn(matchTable, SWT.CENTER);
		tblclmnNewColumn_41.setWidth(200);
		tblclmnNewColumn_41.setText("对接ADC");

		Label lblNewLabel_21 = new Label(matchComposite, SWT.NONE);
		lblNewLabel_21.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_21.setText("分区号：");

		Spinner matchLogicNoSpinner = new Spinner(matchComposite, SWT.BORDER);
		matchLogicNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_216 = new Label(matchComposite, SWT.NONE);
		lblNewLabel_216.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_216.setText("通道数：");

		Text matchchnNumText = new Text(matchComposite, SWT.BORDER | SWT.READ_ONLY);
		matchchnNumText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button matchQueryButton = new Button(matchComposite, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1);
		gd_btnNewButton.widthHint = 100;
		matchQueryButton.setLayoutData(gd_btnNewButton);
		matchQueryButton.setText("查询");

		matchQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				CalMatchData sendData = new CalMatchData();
//				sendData.setUnitIndex(matchLogicNoSpinner.getSelection());
//				StringBuffer info = new StringBuffer();
//				CalMatchData receiveData = (CalMatchData) WorkBench.queryCommand(calBox, sendData, 2000, info);
//				if (receiveData == null) {
//					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
//					return;
//				}
//				matchchnNumText.setText(receiveData.getAdcList().size() + "");
//				matchTableViewer.setInput(receiveData.getAdcList());
			}
		});

		// *************************************************************************************************************
		// *
		// *校准对接识别结束
		// *
		// *************************************************************************************************************

		// *************************************************************************************************************
		// *
		// *回检板调试开始
		// *
		// *************************************************************************************************************

		CTabItem recheckDebugCTabItem = new CTabItem(tabFolder, SWT.NONE);
		recheckDebugCTabItem.setText("回检板调试");

		Composite recheckDebugComposite = new Composite(tabFolder, SWT.NONE);
		recheckDebugCTabItem.setControl(recheckDebugComposite);
		recheckDebugComposite.setLayout(new FormLayout());

		Group recheckCalibrateGroup = new Group(recheckDebugComposite, SWT.NONE);
		recheckCalibrateGroup.setText("校准调试");
		recheckCalibrateGroup.setLayout(new FormLayout());
		FormData fd_recheckCalibrateGroup = new FormData();
		fd_recheckCalibrateGroup.top = new FormAttachment(0, 0);
		fd_recheckCalibrateGroup.left = new FormAttachment(0, 0);
		fd_recheckCalibrateGroup.right = new FormAttachment(50, 0);
		fd_recheckCalibrateGroup.bottom = new FormAttachment(100, 0);
		recheckCalibrateGroup.setLayoutData(fd_recheckCalibrateGroup);

		TableViewer recheckCalibrateTableViewer = new TableViewer(recheckCalibrateGroup,
				SWT.BORDER | SWT.FULL_SELECTION);
		recheckCalibrateTableViewer.setContentProvider(new ArrayContentProvider());
		recheckCalibrateTableViewer.setLabelProvider(new LabelProviderForRecheckCalibrateOrCalculate());
		Table recheckCalibrateTable = recheckCalibrateTableViewer.getTable();
		recheckCalibrateTable.setLinesVisible(true);
		recheckCalibrateTable.setHeaderVisible(true);
		FormData fd_recheckCalibrateTable = new FormData();
		fd_recheckCalibrateTable.right = new FormAttachment(100, -200);
		fd_recheckCalibrateTable.top = new FormAttachment(0, 0);
		fd_recheckCalibrateTable.bottom = new FormAttachment(100, 0);
		fd_recheckCalibrateTable.left = new FormAttachment(0, 0);
		recheckCalibrateTable.setLayoutData(fd_recheckCalibrateTable);

		TableColumn tblclmnAdcorgin = new TableColumn(recheckCalibrateTable, SWT.CENTER);
		tblclmnAdcorgin.setWidth(180);
		tblclmnAdcorgin.setText("adc原始检测值");

		TableColumn tblclmnAdcrecheck = new TableColumn(recheckCalibrateTable, SWT.CENTER);
		tblclmnAdcrecheck.setWidth(180);
		tblclmnAdcrecheck.setText("adc最终检测值");

		Composite recheckCalibrateDoComposite = new Composite(recheckCalibrateGroup, SWT.NONE);
		recheckCalibrateDoComposite.setLayout(new GridLayout(2, false));
		FormData fd_recheckCalibrateDoComposite = new FormData();
		fd_recheckCalibrateDoComposite.top = new FormAttachment(0, 0);
		fd_recheckCalibrateDoComposite.left = new FormAttachment(100, -200);
		fd_recheckCalibrateDoComposite.right = new FormAttachment(100, 0);
		fd_recheckCalibrateDoComposite.bottom = new FormAttachment(100, 0);
		recheckCalibrateDoComposite.setLayoutData(fd_recheckCalibrateDoComposite);

		Label lblNewLabel_22 = new Label(recheckCalibrateDoComposite, SWT.NONE);
		lblNewLabel_22.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_22.setText("分区号：");

		Spinner recheckCaliLogicNoSpinner = new Spinner(recheckCalibrateDoComposite, SWT.BORDER);
		recheckCaliLogicNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_23 = new Label(recheckCalibrateDoComposite, SWT.NONE);
		lblNewLabel_23.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_23.setText("通道号：");

		Spinner recheckCaliChnNoSpinner = new Spinner(recheckCalibrateDoComposite, SWT.BORDER);
		recheckCaliChnNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_24 = new Label(recheckCalibrateDoComposite, SWT.NONE);
		lblNewLabel_24.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_24.setText("极性：");

		String[] recheckCaliPoleStrings = new String[Pole.values().length];
		for (int i = 0; i < recheckCaliPoleStrings.length; i++) {
			recheckCaliPoleStrings[i] = Pole.values()[i].name();
		}
		Combo recheckCaliPoleCombo = new Combo(recheckCalibrateDoComposite, SWT.READ_ONLY);
		recheckCaliPoleCombo.setItems(recheckCaliPoleStrings);
		recheckCaliPoleCombo.select(0);
		recheckCaliPoleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_25 = new Label(recheckCalibrateDoComposite, SWT.NONE);
		lblNewLabel_25.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_25.setText("电压模式：");

		String[] recheckCaliVolModeStrings = new String[VoltMode.values().length];
		for (int i = 0; i < recheckCaliVolModeStrings.length; i++) {
			recheckCaliVolModeStrings[i] = VoltMode.values()[i].name();
		}
		Combo recheckCaliVModeCombo = new Combo(recheckCalibrateDoComposite, SWT.READ_ONLY);
		recheckCaliVModeCombo.setItems(recheckCaliVolModeStrings);
		recheckCaliVModeCombo.select(0);
		recheckCaliVModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_26 = new Label(recheckCalibrateDoComposite, SWT.NONE);
		lblNewLabel_26.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_26.setText("工作方式：");

		String[] recheckCaliWorkModeStrings = new String[Work.values().length];
		for (int i = 0; i < recheckCaliWorkModeStrings.length; i++) {
			recheckCaliWorkModeStrings[i] = Work.values()[i].name();
		}
		Combo recheckCaliWModeCombo = new Combo(recheckCalibrateDoComposite, SWT.READ_ONLY);
		recheckCaliWModeCombo.setItems(recheckCaliWorkModeStrings);
		recheckCaliWModeCombo.select(0);
		recheckCaliWModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_2612 = new Label(recheckCalibrateDoComposite, SWT.NONE);
		lblNewLabel_2612.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2612.setText("adc采集数：");

		Spinner recheckCaliAdcNumSpinner = new Spinner(recheckCalibrateDoComposite, SWT.BORDER);
		recheckCaliAdcNumSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button recheckCaliQueryButton = new Button(recheckCalibrateDoComposite, SWT.NONE);
		recheckCaliQueryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		recheckCaliQueryButton.setText("查询");

		Button recheckCaliConfigButton = new Button(recheckCalibrateDoComposite, SWT.NONE);
		recheckCaliConfigButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		recheckCaliConfigButton.setText("配置");

		recheckCaliQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				CheckCalibrateDebugData sendData = new CheckCalibrateDebugData();
//				sendData.setUnitIndex(recheckCaliLogicNoSpinner.getSelection());
//				sendData.setChnIndex(recheckCaliChnNoSpinner.getSelection());
//				StringBuffer info = new StringBuffer();
//				CheckCalibrateDebugData receiveData = (CheckCalibrateDebugData) WorkBench.queryCommand(calBox, sendData,
//						2000, info);
//				if (receiveData == null) {
//					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
//					return;
//				}
//				recheckCaliPoleCombo.select(receiveData.getPole().ordinal());
//				recheckCaliVModeCombo.select(receiveData.getVoltMode().ordinal());
//				recheckCaliWModeCombo.select(receiveData.getWork().ordinal());
//				recheckCaliAdcNumSpinner.setSelection(receiveData.getAdcs().size());
//				recheckCalibrateTableViewer.setInput(receiveData.getAdcs());
			}
		});

		recheckCaliConfigButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CheckCalibrateDebugData sendData = new CheckCalibrateDebugData();
				sendData.setUnitIndex(recheckCaliLogicNoSpinner.getSelection());
				sendData.setChnIndex(recheckCaliChnNoSpinner.getSelection());
				sendData.setPole(Pole.values()[recheckCaliPoleCombo.getSelectionIndex()]);
				sendData.setVoltMode(VoltMode.values()[recheckCaliVModeCombo.getSelectionIndex()]);
				sendData.setWork(Work.values()[recheckCaliWModeCombo.getSelectionIndex()]);
				List<AdcGroup> adcGroupList = new ArrayList<>();
				for (int i = 0; i < recheckCaliAdcNumSpinner.getSelection(); i++) {
					AdcGroup adcGroup = new AdcGroup();
					adcGroupList.add(adcGroup);
				}
				sendData.setAdcs(adcGroupList);
				StringBuffer info = new StringBuffer();
				if (!WorkBench.configCommand(calBox, sendData, 2000, info)) {
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
				} else {
					MessageDialog.openInformation(parent.getShell(), "操作成功", info.toString());
				}
			}
		});

		// **************************************计量调试**********************************************

		Group recheckCalculateGroup = new Group(recheckDebugComposite, SWT.NONE);
		recheckCalculateGroup.setText("计量调试");
		recheckCalculateGroup.setLayout(new FormLayout());
		FormData fd_recheckCalculateGroup = new FormData();
		fd_recheckCalculateGroup.top = new FormAttachment(0, 0);
		fd_recheckCalculateGroup.bottom = new FormAttachment(100, 0);
		fd_recheckCalculateGroup.left = new FormAttachment(50, 0);
		fd_recheckCalculateGroup.right = new FormAttachment(100, 0);
		recheckCalculateGroup.setLayoutData(fd_recheckCalculateGroup);

		TableViewer recheckCalculateTableViewer = new TableViewer(recheckCalculateGroup,
				SWT.BORDER | SWT.FULL_SELECTION);
		recheckCalculateTableViewer.setContentProvider(new ArrayContentProvider());
		recheckCalculateTableViewer.setLabelProvider(new LabelProviderForRecheckCalibrateOrCalculate());
		Table recheckCalculateTable = recheckCalculateTableViewer.getTable();
		recheckCalculateTable.setLinesVisible(true);
		recheckCalculateTable.setHeaderVisible(true);
		FormData fd_recheckCalculateTable = new FormData();
		fd_recheckCalculateTable.top = new FormAttachment(0, 0);
		fd_recheckCalculateTable.bottom = new FormAttachment(100, 0);
		fd_recheckCalculateTable.left = new FormAttachment(0, 0);
		fd_recheckCalculateTable.right = new FormAttachment(100, -200);
		recheckCalculateTable.setLayoutData(fd_recheckCalculateTable);

		TableColumn tblclmnNewColumn_5 = new TableColumn(recheckCalculateTable, SWT.CENTER);
		tblclmnNewColumn_5.setWidth(180);
		tblclmnNewColumn_5.setText("电压检测值");

		TableColumn tblclmnNewColumn_6 = new TableColumn(recheckCalculateTable, SWT.CENTER);
		tblclmnNewColumn_6.setWidth(180);
		tblclmnNewColumn_6.setText("回检电压AD");

		Composite recheckCalculateDoComposite = new Composite(recheckCalculateGroup, SWT.NONE);
		recheckCalculateDoComposite.setLayout(new GridLayout(2, false));
		FormData fd_recheckCalculateDoComposite = new FormData();
		fd_recheckCalculateDoComposite.top = new FormAttachment(0, 0);
		fd_recheckCalculateDoComposite.bottom = new FormAttachment(100, 0);
		fd_recheckCalculateDoComposite.left = new FormAttachment(100, -200);
		fd_recheckCalculateDoComposite.right = new FormAttachment(100, 0);
		recheckCalculateDoComposite.setLayoutData(fd_recheckCalculateDoComposite);

		Label lblNewLabel_22_1 = new Label(recheckCalculateDoComposite, SWT.NONE);
		lblNewLabel_22_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_22_1.setText("分区号：");

		Spinner recheckCalcLogicNoSpinner = new Spinner(recheckCalculateDoComposite, SWT.BORDER);
		recheckCalcLogicNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNewLabel_23_1 = new Label(recheckCalculateDoComposite, SWT.NONE);
		lblNewLabel_23_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_23_1.setText("通道号：");

		Spinner recheckCalcChnNoSpinner = new Spinner(recheckCalculateDoComposite, SWT.BORDER);
		recheckCalcChnNoSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_24_1 = new Label(recheckCalculateDoComposite, SWT.NONE);
		lblNewLabel_24_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_24_1.setText("极性：");

		String[] recheckCalcPoleStrings = new String[Pole.values().length];
		for (int i = 0; i < recheckCalcPoleStrings.length; i++) {
			recheckCalcPoleStrings[i] = Pole.values()[i].name();
		}
		Combo recheckCalcPoleCombo = new Combo(recheckCalculateDoComposite, SWT.READ_ONLY);
		recheckCalcPoleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		recheckCalcPoleCombo.setItems(recheckCalcPoleStrings);
		recheckCalcPoleCombo.select(0);

		Label lblNewLabel_25_1 = new Label(recheckCalculateDoComposite, SWT.NONE);
		lblNewLabel_25_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_25_1.setText("电压模式：");

		String[] recheckCalcVolModeStrings = new String[VoltMode.values().length];
		for (int i = 0; i < recheckCalcVolModeStrings.length; i++) {
			recheckCalcVolModeStrings[i] = VoltMode.values()[i].name();
		}
		Combo recheckCalcVModeCombo = new Combo(recheckCalculateDoComposite, SWT.READ_ONLY);
		recheckCalcVModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		recheckCalcVModeCombo.setItems(recheckCalcVolModeStrings);
		recheckCalcVModeCombo.select(0);

		Label lblNewLabel_26_1 = new Label(recheckCalculateDoComposite, SWT.NONE);
		lblNewLabel_26_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_26_1.setText("工作模式：");

		String[] recheckCalcWorkModeStrings = new String[Work.values().length];
		for (int i = 0; i < recheckCalcWorkModeStrings.length; i++) {
			recheckCalcWorkModeStrings[i] = Work.values()[i].name();
		}
		Combo recheckCalcWModeCombo = new Combo(recheckCalculateDoComposite, SWT.READ_ONLY);
		recheckCalcWModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		recheckCalcWModeCombo.setItems(recheckCalcWorkModeStrings);
		recheckCalcWModeCombo.select(0);

		Label lblNewLabel_2612_1 = new Label(recheckCalculateDoComposite, SWT.NONE);
		lblNewLabel_2612_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2612_1.setText("ADC采集数量：");

		Spinner recheckCalcAdcNumSpinner = new Spinner(recheckCalculateDoComposite, SWT.BORDER);
		recheckCalcAdcNumSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button recheckCalcQueryButton = new Button(recheckCalculateDoComposite, SWT.NONE);
		recheckCalcQueryButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		recheckCalcQueryButton.setText("查询");

		Button recheckCalcConfigButton = new Button(recheckCalculateDoComposite, SWT.NONE);
		recheckCalcConfigButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		recheckCalcConfigButton.setText("配置");

		recheckCalcQueryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				CheckCalculateDebugData sendData = new CheckCalculateDebugData();
//				sendData.setUnitIndex(recheckCaliLogicNoSpinner.getSelection());
//				sendData.setChnIndex(recheckCaliChnNoSpinner.getSelection());
//				StringBuffer info = new StringBuffer();
//				CheckCalibrateDebugData receiveData = (CheckCalibrateDebugData) WorkBench.queryCommand(calBox, sendData,
//						2000, info);
//				if (receiveData == null) {
//					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
//					return;
//				}
//				recheckCalcPoleCombo.select(receiveData.getPole().ordinal());
//				recheckCalcVModeCombo.select(receiveData.getVoltMode().ordinal());
//				recheckCalcWModeCombo.select(receiveData.getWork().ordinal());
//				recheckCalcAdcNumSpinner.setSelection(receiveData.getAdcs().size());
//				recheckCalculateTableViewer.setInput(receiveData.getAdcs());
			}
		});

		recheckCalcConfigButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CheckCalculateDebugData sendData = new CheckCalculateDebugData();
				sendData.setUnitIndex(recheckCalcLogicNoSpinner.getSelection());
				sendData.setChnIndex(recheckCalcChnNoSpinner.getSelection());
				sendData.setPole(Pole.values()[recheckCalcPoleCombo.getSelectionIndex()]);
				sendData.setVoltMode(VoltMode.values()[recheckCalcVModeCombo.getSelectionIndex()]);
				sendData.setWork(Work.values()[recheckCalcWModeCombo.getSelectionIndex()]);
				List<AdcGroup> adcGroupList = new ArrayList<>();
				for (int i = 0; i < recheckCalcAdcNumSpinner.getSelection(); i++) {
					AdcGroup adcGroup = new AdcGroup();
					adcGroupList.add(adcGroup);
				}
				sendData.setAdcs(adcGroupList);
				StringBuffer info = new StringBuffer();
				if (!WorkBench.configCommand(calBox, sendData, 2000, info)) {
					MessageDialog.openError(parent.getShell(), "操作失败", info.toString());
				} else {
					MessageDialog.openInformation(parent.getShell(), "操作成功", info.toString());
				}
			}
		});

		// *************************************************************************************************************
		// *
		// *回检板调试结束
		// *
		// *************************************************************************************************************

		logText = new Text(sashForm, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI);
		sashForm.setWeights(new int[] { 3, 1 });
	}

	private class LabelProviderForLogic implements ITableLabelProvider {
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
			CalDot calDot = (CalDot) element;
			switch (columnIndex) {
			case 0:
				return calDot.mode.name();
			case 1:
				return calDot.pole.name();
			case 2:
				return calDot.level + "";
			case 3:
				return calDot.adc + "";
			case 4:
				return calDot.adcK + "";
			case 5:
				return calDot.adcB + "";
			case 6:
				return calDot.da + "";
			case 7:
				return calDot.meter + "";
			case 8:
				return calDot.programK + "";
			case 9:
				return calDot.programB + "";
			default:
				return "";
			}
		}
	}

	private class LabelProviderForReCheck implements ITableLabelProvider {
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
			ReCheckFlashData reCheckFlashData = (ReCheckFlashData) element;
			switch (columnIndex) {
			case 0:
				return reCheckFlashData.voltageType;
			case 1:
				return reCheckFlashData.adc + "";
			case 2:
				return reCheckFlashData.adck + "";
			case 3:
				return reCheckFlashData.adcb + "";
			default:
				return "";
			}
		}
	}

	public class LabelProviderForCalibrate implements ITableLabelProvider {
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
			CalibrateAdcGroup adcGroup = (CalibrateAdcGroup) element;
			switch (columnIndex) {
			case 0:
				return adcGroup.adc1 + "";
			case 1:
				return adcGroup.adc2 + "";
			default:
				return "";
			}

		}
	}

	public class LabelProviderForCalculate implements ITableLabelProvider {
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
			CalculateAdcGroup data = (CalculateAdcGroup) element;
			switch (columnIndex) {
			case 0:
				return data.adc1 + "";
			case 1:
				return data.adc2 + "";
			case 2:
				return data.finalAdc + "";
			default:
				return "";
			}
		}
	}

	public class LabelProviderForMatch implements ITableLabelProvider {
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
			AdcData data = (AdcData) element;
			switch (columnIndex) {
			case 0:
				return data.chnIndex + "";
			case 1:
				return data.adc + "";
			default:
				return "";
			}
		}
	}

	public class LabelProviderForRecheckCalibrateOrCalculate implements ITableLabelProvider {
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
			AdcGroup data = (AdcGroup) element;
			switch (columnIndex) {
			case 0:
				return data.adc1 + "";
			case 1:
				return data.adc2 + "";
			default:
				return "";
			}
		}
	}

	private class ReCheckFlashData {
		public String voltageType;
		public double adc;
		public double adck;
		public double adcb;
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
}