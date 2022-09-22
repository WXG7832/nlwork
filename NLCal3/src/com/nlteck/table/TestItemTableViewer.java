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

import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestItemDataDO;
import com.nlteck.resources.Resources;
import com.nlteck.utils.CommonUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月27日 下午9:54:44
* 普通类测试表
*/
public class TestItemTableViewer extends TableViewer {
     
	
	private ChannelDO channel;
	
	public TestItemTableViewer(Composite parent, ChannelDO channel) {
		
		super(parent,SWT.BORDER | SWT.FULL_SELECTION);
		this.channel = channel;
		Table table = getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		initCoulumn();
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new TestItemLableProvider());
		
		
	}
	
	private void initCoulumn() {

		Table table = this.getTable();

		TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("序号");
		
		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("通道号");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(120);
		tableColumn.setText("测试项");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(150);
		tableColumn.setText("参数");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("测试值");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("下限值");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("上限值");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("结果");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(80);
		tableColumn.setText("流逝时间");
		
		

	}
	
	
	private class TestItemLableProvider implements ITableLabelProvider, ITableColorProvider {

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
			
			TestItemDataDO  item = (TestItemDataDO)element;
			if(columnIndex == 7) {
				
				if(item.getState().equals("testing")) {
					
					return Resources.TEST_CLR;
				} else if(item.getState().equals("ok")) {
					
					return Resources.DARK_GREEN_CLR;
				} else if(item.getState().equals("ng")) {
					
					return Resources.COLOR_RED;
				} 
				
			}
			
			return null;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			
			
			return null;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			
			TestItemDataDO  item = (TestItemDataDO)element;
			if(columnIndex == 0) {
				
				if(item.getState().equals("testing")) {
					
					return Resources.PLAY_FLAG;
				}
				
			}
			
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			
			TestItemDataDO  item = (TestItemDataDO)element;
			String text = "";
			switch(columnIndex) {
			
			case 0:
				text = item.getIndex() + 1 + "";
				break;
			case 1:
				text = channel.getDeviceChnIndex() + 1 + "";
				break;
			case 2:
				text = item.getName().toString();
				break;
			case 3:
				text = item.getParam() == null ? "" : item.getParam().toString();
				break;
			case 4:
				text = CommonUtil.formatNumber(item.getTestVal() , 1);
				break;
			case 5:
				text = item.getLower() == 0 ? "--" : CommonUtil.formatNumber(item.getLower() , 1);
				break;
			case 6:
				text = item.getUpper() == 0 ? "--" : CommonUtil.formatNumber(item.getUpper() , 1);
				break;
			case 7:
				text = item.getState();
				break;
			case 8:
				text = CommonUtil.formatNumber((double)item.getMilisecs() / 1000 , 3) + "s";
				break;
			
			
			}
			
			return text;
		}
		
		
		
	}
}
