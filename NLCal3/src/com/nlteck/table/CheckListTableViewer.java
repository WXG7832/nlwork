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

import com.nlteck.resources.Resources;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年2月9日 上午9:01:47 自检项目表格
 */
public class CheckListTableViewer extends TableViewer {

	public static class CheckItem {

		public String item;
		public String itemResult;
		public boolean err;

	}

	/**
	 * 专用标签器
	 * 
	 * @author wavy_zheng 2021年2月9日
	 *
	 */
	private static class CheckListLabelProvider implements ITableLabelProvider, ITableColorProvider {

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

			CheckItem ci = (CheckItem) element;
			if (columnIndex == 1) {
				if (ci.err) {

					return Resources.ALERT_CLR;
				} else {
					
					return Resources.DARK_GREEN_CLR;
				}
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			CheckItem ci = (CheckItem) element;
			String text = "";
			switch(columnIndex) {
			
			case 0:
				text = ci.item;
				break;
			case 1:
				text = ci.itemResult;
				break;
			
			
			}
			return text;
		}

	}

	public CheckListTableViewer(Composite parent) {
		super(parent, SWT.BORDER | SWT.FULL_SELECTION);

		Table table = getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		initColumns();
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new CheckListLabelProvider());

	}

	private void initColumns() {

		Table table = this.getTable();

		TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(180);
		tableColumn.setText("自检项目");

		tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(380);
		tableColumn.setText("自检结果");

	}

}
