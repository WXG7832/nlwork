package com.nlteck.table;

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
import org.springframework.test.context.TestExecutionListener;

import com.nlteck.dialog.Test;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.StableDataDO;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nlteck.resources.Resources;
import com.nlteck.utils.CommonUtil;

public class CalculateTableViewer extends TableViewer {
	
	private class CalculateLableProvider implements ITableLabelProvider, ITableColorProvider {

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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			
			TestDot dot = (TestDot) element;
			if(columnIndex == 8) {
				
				return dot.testResult.equals(TestResult.Success) ? Resources.DARK_GREEN_CLR : Resources.COLOR_RED;
			}
			
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			
			TestDot dot = (TestDot) element;
			
			String text = "";
			switch (columnIndex) {
			  
			case 0:
				text = dot.getChnIndex()+1 + "";
				break;
			case 1:
				text = dot.getMode().toString();
				break;
			case 2:
				text = dot.getPole().toString();
				break;
				
			case 3:
				text = CommonUtil.formatNumber(dot.getProgramVal() , 1);
				break;
			case 4:
				text = CommonUtil.formatNumber(dot.getAdc() , 1);
				break;
			case 5:
				text = CommonUtil.formatNumber(dot.getMeterVal(), 1);
				break;
			case 6 :
				text = CommonUtil.formatNumber(dot.getAdc() - dot.getProgramVal(), 1);
				break;
			case 7:
				text = CommonUtil.formatNumber(dot.getMeterVal() - dot.getProgramVal(), 1);
				break;
			case 8:
				text = dot.testResult.equals(TestResult.Success)?"pass":"fail";
				break;
			case 9:
				text = CommonUtil.formatTime(dot.getDate(), "yyyy-MM-dd HH:mm:ss");
				break;
			
				
			
			
			}
			
			return text;
		}
		
		
		
	}
		
	public CalculateTableViewer(Composite parent) {
		
		super(parent, SWT.BORDER | SWT.FULL_SELECTION);
		
		Table table = getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		initCoulumn();
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new CalculateLableProvider());
		
	}
	
	
	private void initCoulumn() {

		Table table = this.getTable();

		TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
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
		tableColumn.setWidth(150);
		tableColumn.setText("时间");

	}
	
}
