package com.nlteck.dialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.model.TestData;
import com.nlteck.model.TestLog;
import com.nlteck.parts.DriverConsolePart.TestDotColumnHead;
import com.nlteck.resources.Resources;
import com.nlteck.service.CalboxService.CalboxListener;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.check2.Check2Environment.VoltMode;
import com.nltecklib.protocol.li.driver.DriverCalibrationFactorData.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * 计量报表弹框
 * 
 * @author admin
 */
public class MeasureWindow {

	private int MEASURE_WIDTH = 1380;
	private int MEASURE_HEIGHT = 800;

	private ChannelDO channel;

	private TableViewer tableViewer;
	private StyledText logText;

	private Shell shell;
	private static final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	
	private CalboxListener listener;

	public MeasureWindow(Shell shell, ChannelDO channel) {

		final Shell measureWindow = new Shell(shell, SWT.SHELL_TRIM | SWT.PRIMARY_MODAL);
		this.shell = measureWindow;
		this.channel = channel;
		measureWindow.setLayout(new FillLayout(SWT.HORIZONTAL));
		measureWindow.setText((channel == null ? "" : "chn" + (channel.getDeviceChnIndex() + 1)) + "计量报表");
		SashForm form = new SashForm(measureWindow, SWT.NONE | SWT.VERTICAL);

		ScrolledComposite scrolledComposite = new ScrolledComposite(form, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(composite);

		composite.setLayout(new FillLayout(SWT.VERTICAL));

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TestDotDataLableProvider());

		new TestDotDataLableProvider().init(tableViewer);

		logText = new StyledText(form, SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		form.setWeights(new int[] { 2, 1 });
		tableViewer.setInput(channel.getMeasures());
		refreshData();
		refreshLog();
		
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				
				WorkBench.getBoxService().removeListener(listener);
				
			}
		});

		WorkBench.getBoxService().addListener(listener = new CalboxListener() {

			@Override
			public void onRecvLog(CalBox calbox, TestLog log) {

				if (log.getDeviceChnIndex() == channel.getDeviceChnIndex() && !log.getLevel().equals("debug")) {

					if (tableViewer.getTable().isDisposed()) {

						return;
					}
					tableViewer.getTable().getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							appendLog(log);

						}

					});

				}

			}

			@Override
			public void onRecvChnState(CalBox calbox, List<ChannelDO> channels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRecvChnData(CalBox calbox, ChannelDO channel) {

				if (channel == MeasureWindow.this.channel) {

					if (tableViewer.getTable().isDisposed()) {

						return;
					}
					tableViewer.getTable().getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							refreshData();
						}
					});

				}

			}

			@Override
			public void joinVoltage(CalBox calbox, List<ChannelDO> channels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void join(CalBox calbox, boolean operation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void disconnected(CalBox calbox) {
				// TODO Auto-generated method stub

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
			public void onStartTest(CalBox calbox, ChannelDO channel) {
				// TODO Auto-generated method stub
				
			}
		});

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				WorkBench.getBoxService().removeListener(listener);

			}
		});

		centerShell(measureWindow);

		measureWindow.setSize(MEASURE_WIDTH, MEASURE_HEIGHT);// 窗口自定义
		measureWindow.setImage(Resources.START_CALC_IMAGE);
		
		measureWindow.open();

	}

	/**
	 * 通道数据表格内容提供器
	 * 
	 * @author caichao_tang
	 *
	 */
	private class TestDotDataLableProvider implements ITableLabelProvider, ITableColorProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		public void init(TableViewer tableViewer) {
			Table table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(60);
			tableColumn.setText("序号");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(80);
			tableColumn.setText("通道号");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(80);
			tableColumn.setText("模式");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(80);
			tableColumn.setText("极性");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(100);
			tableColumn.setText("计量点");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(100);
			tableColumn.setText("ADC");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(100);
			tableColumn.setText("表值");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(100);
			tableColumn.setText("adc偏差");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(100);
			tableColumn.setText("表偏差");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(80);
			tableColumn.setText("结果");

			tableColumn = new TableColumn(table, SWT.CENTER);
			tableColumn.setWidth(300);
			tableColumn.setText("信息");

			tableViewer.setLabelProvider(this);

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
			MeasureDotDO data = (MeasureDotDO) element;
			String columnString = "";
			switch (columnIndex) {
			case 0:
				columnString = data.getIndex() + 1 + "";
				break;
			case 1:
				columnString = data.getChannel().getDeviceChnIndex() + 1 + "";
				break;
			case 2:
				columnString = data.getMode();
				break;
			case 3:
				columnString = data.getPole();
				break;
			case 4:
				columnString = data.getCalculateDot() + "";
				break;
			case 5:
				columnString = String.format("%.3f", data.getFinalAdc());
				break;
			case 6:
				columnString = String.format("%.3f", data.getMeterVal());
				break;
			case 7:
				columnString = String.format("%.3f", data.getFinalAdc() - data.getCalculateDot());
				break;
			case 8:
				columnString = String.format("%.3f", data.getMeterVal() - data.getCalculateDot());
				break;
			case 9:
				columnString = data.getResult() == null ? "" : data.getResult();
				break;
			case 10:
				columnString = data.getInfo() == null ? "" : data.getInfo();
				break;

			default:
				break;
			}
			System.out.println(columnString);
			return columnString;
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			MeasureDotDO data = (MeasureDotDO) element;
			Color color = null;
			if (data.getResult().equalsIgnoreCase("fail")) {
				color = Resources.COLOR_RED;
			}
			return color;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public void centerShell(Shell composite) {

		Rectangle measureRect = Display.getDefault().getPrimaryMonitor().getBounds();
		composite.setBounds((measureRect.width - MEASURE_WIDTH) / 2, (measureRect.height - MEASURE_HEIGHT) / 2,
				MEASURE_WIDTH, MEASURE_HEIGHT);
	}

	private void appendLog(TestLog log) {

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

	public void refreshData() {

		if (channel != null) {

			if (!tableViewer.getTable().isDisposed()) {
				//

				if (channel.getMeasures().isEmpty()) {

					try {
						List<MeasureDotDO> list = WorkBench.getDatabaseManager().listMeasureDots(channel);
						channel.setMeasures(list);
						tableViewer.setInput(channel.getMeasures());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!channel.getMeasures().isEmpty()) {
					
					tableViewer.getTable().setTopIndex(channel.getMeasures().size() - 1);
					tableViewer.refresh();
				}
			}

		}

	}

	private StyleRange getHighlightStyle(int startOffset, int length, boolean bold, Color color) {

		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.fontStyle = bold ? SWT.BOLD : SWT.NORMAL;
		styleRange.foreground = color;

		return styleRange;
	}
}
