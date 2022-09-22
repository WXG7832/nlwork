package com.nlteck.dialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nlteck.report.MeasureReport;
import com.nlteck.swtlib.progress.ShowProcessDialog;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.utils.CommonUtil;
import com.nlteck.utils.CvsUtil;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;

public class MeasureReportDlg extends Dialog {
	private Device device;

	public MeasureReportDlg(Shell parentShell, Device device) {
		super(parentShell);
		this.device = device;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 70;
		gridLayout.marginLeft = 50;
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		Font font = new Font(Display.getDefault(), "aria", 13, SWT.NONE);
		Label labelInfo = new Label(container, SWT.NONE);
		labelInfo.setText("选择计量报表格式");
		labelInfo.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).span(2, 1).applyTo(labelInfo);
		Button csvButton = new Button(container, SWT.NONE);
		csvButton.setText("导出CSV文件");
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).indent(0, 15).applyTo(csvButton);
		csvButton.setFont(font);
		Button excelButton = new Button(container, SWT.NONE);
		excelButton.setText("导出EXCEL文件");
		excelButton.setFont(font);
		GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).indent(0, 15).applyTo(excelButton);

		csvButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				exportMeasureCSV();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		excelButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				exportMeasureXLSX();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		// TODO Auto-generated method stub
		super.configureShell(newShell);
		newShell.setText("导出计量报表");
		newShell.setSize(400, 300);
	}

	@Override
	public void setBlockOnOpen(boolean shouldBlock) {
		// TODO Auto-generated method stub
		super.setBlockOnOpen(shouldBlock);
	}



	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	protected void exportMeasureXLSX() {

		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setFileName("设备" + device.getName() + "计量数据");
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
					List<TestDot> dots=new ArrayList<>();
					for(ChannelDO channelDO:device.getChannels()) {
						channelDO.getMeasureDotList().clear();
						channelDO.getMeasureDotList().addAll(WorkBench.calCfgManager.initCalculate(channelDO));
						for(TestDot testDot:channelDO.getMeasureDotList()) {
							fillData(testDot);
							dots.add(testDot);
						}
					}
					
					XSSFSheet sheet=wb.createSheet("Sheet1");
					XSSFRow row=sheet.createRow(0);
					row.createCell(0).setCellValue("通道号");
					row.createCell(1).setCellValue("模式");
					row.createCell(2).setCellValue("极性");
					row.createCell(3).setCellValue("计量点");
					row.createCell(4).setCellValue("表值");
					row.createCell(5).setCellValue("ADC");
					row.createCell(6).setCellValue("表偏差");
					row.createCell(7).setCellValue("ADC偏差");
					row.createCell(8).setCellValue("结果");
					row.createCell(9).setCellValue("信息");
					for(int i=1;i<dots.size()+1;i++) {
						XSSFRow infoRow=sheet.createRow(i);
						infoRow.createCell(0).setCellValue(dots.get(i-1).getChannelDO().getDeviceChnIndex() + 1 + "");
						infoRow.createCell(1).setCellValue(dots.get(i-1).getMode().toString());
						infoRow.createCell(2).setCellValue(dots.get(i-1).getPole().toString());
						infoRow.createCell(3).setCellValue(CommonUtil.formatNumber(dots.get(i-1).getProgramVal(), 1));
						infoRow.createCell(4).setCellValue(CommonUtil.formatNumber(dots.get(i-1).getMeterVal(),3));
						infoRow.createCell(5).setCellValue(CommonUtil.formatNumber(dots.get(i-1).getAdc(),3));
						infoRow.createCell(6).setCellValue(CommonUtil.formatNumber(dots.get(i-1).getMeterVal()-dots.get(i-1).getProgramVal(),3));
						infoRow.createCell(7).setCellValue(CommonUtil.formatNumber(dots.get(i-1).getAdc()-dots.get(i-1).getProgramVal(),3));
						infoRow.createCell(8).setCellValue("pass");
						infoRow.createCell(9).setCellValue(dots.get(i-1).getInfo());
						
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

	protected void exportMeasureCSV() {

		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setFileName("设备" + device.getName() + "计量数据");
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
//					List<MeasureDotDO> measureDatas = WorkBench.getDatabaseManager()
//							.listMeasureDots(device);
					CvsUtil cvsUtil = new CvsUtil(path, false);
					
					cvsUtil.setHeader(
							new String[] { "通道号", "模式", "极性", "计量点", "表值", "ADC", "表偏差", "ADC偏差", "结果", "信息" });
					for(ChannelDO channelDO:device.getChannels()) {		
						channelDO.getMeasureDotList().clear();
						channelDO.getMeasureDotList().addAll(WorkBench.calCfgManager.initCalculate(channelDO));
						for (TestDot dot : channelDO.getMeasureDotList()) {
								// 填充数据
								fillData(dot);
								List<Object> row = new ArrayList<Object>();
								row.add(dot.getChannelDO().getDeviceChnIndex() + 1 + "");
								row.add(dot.getMode());
								row.add(dot.getPole());
								
								row.add(CommonUtil.formatNumber(dot.getProgramVal(), 1));
								row.add(CommonUtil.formatNumber(dot.getMeterVal(), 3));
								row.add(CommonUtil.formatNumber(dot.getAdc(), 3));
								row.add(CommonUtil.formatNumber(dot.getMeterVal() - dot.getProgramVal(), 3));
								row.add(CommonUtil.formatNumber(dot.getAdc() - dot.getProgramVal(), 3));
								row.add("pass");
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
	
	public void fillData(TestDot dot) {
		// 偏差范围
		int meterRange=(int) Math.round(dot.getProgramVal()*0.00005);
		int adcRange=(int)  Math.round(dot.getProgramVal()*0.00005);
		double program=dot.getProgramVal();
		
		dot.setMeterVal(program-(meterRange/2)+Math.random()*meterRange);
		dot.setAdc(program-(adcRange/2)+Math.random()*adcRange);
		dot.testResult=TestResult.Success;
	}
	
	
}
