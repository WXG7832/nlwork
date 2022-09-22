package com.nlteck.table;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.resources.Resources;
import com.nlteck.service.CalboxService;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月21日 上午10:07:50 类说明
 */
public class DebugTableViewer extends TableViewer {

	private class DebugLableProvider implements ITableLabelProvider, ITableColorProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			
			UploadTestDot dot = (UploadTestDot) element;
			if(!dot.success) {
				
				return Resources.ALERT_CLR;
			}
			
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {

			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {

			UploadTestDot dot = (UploadTestDot) element;
			String text = "";
			switch (columnIndex) {

			case 0:
				text = channel.getDeviceChnIndex() + 1 + "";
				break;
			case 1:
				text = dot.moduleIndex + 1 + "";
				break;
			case 2:
				text = dot.testType == TestType.Cal ? "校准" : "计量";
				break;
			case 3:
				text = dot.mode == null ? "" : dot.mode.toString();
				break;
			case 4:
				text = dot.pole == Pole.NORMAL ? "+" : "-";
				break;
			case 5:
				text = CommonUtil.formatNumber(dot.programVal, dot.testType == TestType.Cal ? 0 : 1);
				break;
			case 6:
				// adc
				text = CommonUtil.formatNumber(dot.adc, 3);
				break;
			case 7:
				text = CommonUtil.formatNumber(dot.meterVal, 3);
				break;

			case 8:
				if (dot.testType == TestType.Measure) {
					text = CommonUtil.formatNumber(dot.adc - dot.programVal, 3);
				}
				break;
			case 9:
				if (dot.testType == TestType.Measure) {
					text = CommonUtil.formatNumber(dot.meterVal - dot.programVal, 3);
				}
				break;
			case 10:
				text = dot.precision + "";
				break;
			case 11:
				text = CommonUtil.formatNumber(dot.checkAdc, 3);
				break;
			case 12:
				text = CommonUtil.formatNumber(dot.adc2, 3);
				break;
			case 13:
				text = CommonUtil.formatNumber(dot.programK, 7);
				break;
			case 14:
				text = CommonUtil.formatNumber(dot.programB, 7);
				break;
			case 15:
				text = CommonUtil.formatNumber(dot.adcK, 7);
				break;
			case 16:
				text = CommonUtil.formatNumber(dot.adcB, 7);
				break;
			case 17:
				text = CommonUtil.formatNumber(dot.checkAdcK, 7);
				break;
			case 18:
				text = CommonUtil.formatNumber(dot.checkAdcB, 7);
				break;
			case 19:
				text = CommonUtil.formatNumber(dot.adcK2, 7);
				break;
			case 20:
				text = CommonUtil.formatNumber(dot.adcB2, 7);
				break;	
				
			case 21:
				text = dot.success ? "pass" : "fail";
				break;
			case 22:
				text = dot.info == null ? "" : dot.info;
				break;
			}

			return text;
		}

	}

	private ChannelDO channel;

	public DebugTableViewer(Composite parent, ChannelDO channel) {
		super(parent, SWT.BORDER | SWT.FULL_SELECTION);

		this.channel = channel;

		Table table = getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		initCoulumn();

		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new DebugLableProvider());
		
		

	}
	
	

	private void initCoulumn() {

		Table table = this.getTable();

		TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("通道号");
		
		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("模片");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("类型");

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
		tableColumn.setText("档位");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(100);
		tableColumn.setText("备份ADC1");
		
		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(100);
		tableColumn.setText("回检ADC2");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("程控K");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("程控B");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("adcK");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("adcB");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("backAdcK");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("backAdcB");
		
		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("checkAdcK2");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("checkAdcB2");
		

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("结果");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(200);
		tableColumn.setText("信息");

	}

}
