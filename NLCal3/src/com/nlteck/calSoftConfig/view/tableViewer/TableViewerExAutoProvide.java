package com.nlteck.calSoftConfig.view.tableViewer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.nlteck.swtlib.table.TableViewerEx;

/**
 * @description 无需实现供应者的表格控件
 * @author zemin_zhu
 * @dateTime Jul 2, 2022 2:35:37 PM
 */
public class TableViewerExAutoProvide extends TableViewerEx {

    protected String[] enabledFieldNameArr;
    protected TableExItemProxyFactory tableExItemProxyFactory;

    public TableViewerExAutoProvide(Composite parent, boolean editable, boolean dragable) {
	super(parent, editable, dragable);

	tableExItemProxyFactory = new TableExItemProxyFactory();
    }

    /**
     * @description 传入非TableExItem类型的数据源
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 11:45:16 AM
     */
    public void setInputNotTableExItem(Object input) throws Exception {
	List<TableExItem> tableExItemLst = getTableExItemLst(input);
	setInput(tableExItemLst);
    }

    /**
     * @description 将数据源装饰为TableExItemLst
     * @author zemin_zhu
     * @dateTime Jul 2, 2022 12:29:14 AM
     */
    protected List<TableExItem> getTableExItemLst(Object input) throws Exception {
	List<TableExItem> tableExItemLst = null;
	if (isTableExItemLst(input)) {
	    tableExItemLst = (List<TableExItem>) input;
	} else {
	    List<Object> rawInputLst = (List) input;
	    Object firstRawItem = rawInputLst.get(0);
	    tableExItemProxyFactory.setSuperclass(firstRawItem.getClass());
	    tableExItemProxyFactory.getItemFieldMap(firstRawItem.getClass());
	    tableExItemLst = new ArrayList<TableExItem>();
	    for (Object rawItem : rawInputLst) {
		TableExItem tableExItem = getTableExItem(rawItem);
		tableExItemLst.add(tableExItem);
	    }
	}
	return tableExItemLst;
    }

    /**
     * @description 入参是否为TableExItemLst
     * @author zemin_zhu
     * @dateTime Jul 2, 2022 10:44:06 PM
     */
    protected boolean isTableExItemLst(Object input) throws Exception {
	boolean isTableExItemLst = false;
	if (input instanceof List) {
	    List<Object> rawInputLst = (List) input;
	    if (rawInputLst.size() > 0) {
		Object firstRawItem = rawInputLst.get(0);
		if (firstRawItem instanceof TableExItem) {
		    isTableExItemLst = true;
		}
	    } else {
		throw new Exception("数据源长度为0");
	    }
	}
	return isTableExItemLst;
    }

    /**
     * @description 将行数据源装饰为TableExItem
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 1:04:08 PM
     */
    protected TableExItem getTableExItem(Object rawItem) throws Exception {
	Class<?> clazz = tableExItemProxyFactory.createClass();
	TableExItem tableExItem = (TableExItem) clazz.newInstance();
	copyVal(tableExItem, rawItem);
	return tableExItem;
    }

    /**
     * @describe 将原始数据源的成员变量值, 赋值给自身同名变量
     * @author zemin_zhu
     * @dateTime Mar 26, 2022 10:04:16 PM
     */
    protected void copyVal(Object target, Object source) throws Exception {
	for (Field field : tableExItemProxyFactory.getItemFieldMap().values()) {
	    Object sourceValue = field.get(source);
	    field.set(target, sourceValue);
	}
    }

    /**
     * @description 设置_启用字段名
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 10:16:10 AM
     */
    public void setEnabledFieldNameArr(String[] enableFieldNameArr) {
	this.enabledFieldNameArr = enableFieldNameArr;
	tableExItemProxyFactory.setEnabledFieldNameArr(enableFieldNameArr);
    }

    /**
     * @description 自动设置_启用字段名, *需先赋值数据源
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 3:56:13 PM
     */
    public void setEnabledFieldNameArrAutomatic() throws Exception {
	isInputExist();
	setEnabledFieldNameArr(tableExItemProxyFactory.getItemFieldMap().keySet().toArray(new String[0]));
    }

    /**
     * @description 自动设置_内容供应者, *需先赋值启用字段名
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 2:01:01 PM
     */
    public void setLabelProviderAutomatic() throws Exception {
	isEnabledFieldNameArrExist();
	setLabelProvider(new ITableLabelProvider() {

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

		return getItemVal(element, columnIndex);
	    }

	});

    }

    /**
     * @description 从数据源取值
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 3:03:33 PM
     */
    public String getItemVal(Object tableExItem, int columnIndex) {
	String fieldName = enabledFieldNameArr[columnIndex];
	Field field = tableExItemProxyFactory.getItemFieldMap().get(fieldName);
	Object value = "";
	try {
	    value = field.get(tableExItem);
	} catch (Exception e) {

	    e.printStackTrace();
	}
	return value.toString();
    }

    /**
     * @description 自动设置_可编辑单元格, *需先赋值启用字段名
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 3:56:23 PM
     */
    public void setColumnEditorAutomatic() throws Exception {
	isEnabledFieldNameArrExist();
	int colIdx = 0;
	for (String fieldName : enabledFieldNameArr) {
	    Field field = tableExItemProxyFactory.getItemFieldMap().get(fieldName);
	    if (field.getType() == int.class) {
		setColumnEditType(colIdx, EditCtrlType.SPINNER);
		setColumnEditRange(colIdx, 0, 65535);
	    } else if (field.getType() == String.class || field.getType() == double.class
		    || field.getType() == long.class) {
		setColumnEditType(colIdx, EditCtrlType.TEXT);
	    } else if (field.getType() == boolean.class) {
		setColumnEditType(colIdx, EditCtrlType.COMBO);
		setColumnEditContent(colIdx, "true", "false");
	    } else if (field.getType().isEnum()) {
		List<String> enumValStrLst = new ArrayList<String>();
		for (Object enumValue : field.getType().getEnumConstants()) {
		    enumValStrLst.add(enumValue.toString());
		}
		setColumnEditType(colIdx, EditCtrlType.COMBO);
		setColumnEditContent(colIdx, enumValStrLst.toArray(new String[0]));
	    }
	    colIdx++;
	}
    }

    /**
     * @description 启用字段名是否存在
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 5:49:40 PM
     */
    protected void isEnabledFieldNameArrExist() throws Exception {
	if (enabledFieldNameArr == null || enabledFieldNameArr.length == 0) {
	    throw new Exception("启用字段名为空");
	}
    }

    /**
     * @description 数据源是否存在
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 5:49:54 PM
     */
    protected void isInputExist() throws Exception {
	if (tableExItemProxyFactory.getItemFieldMap() == null
		|| tableExItemProxyFactory.getItemFieldMap().size() == 0) {
	    throw new Exception("数据源为空");
	}
    }

    /**
     * @description 对数据源进行装箱, 适用于数据源为动态生成类的情况
     * @author zemin_zhu
     * @dateTime Jul 2, 2022 10:57:54 PM
     */
    public Object getInputAfterAssignment() throws Exception {
	return getInput();
    }

    /**
     * @description 删除选中行
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 1:32:09 AM
     */
    public void delSelectRow() {
	List<Object> rowItemLst = (List<Object>) getInput();
	rowItemLst.remove(getStructuredSelection().getFirstElement());
	refresh();
    }

    /**
     * @description 添加行
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 9:39:05 AM
     */
    public void addRow() throws Exception {
	List<Object> rowItemLst = (List<Object>) getInput();
	Object rowItem = getStructuredSelection().getFirstElement();
	if (rowItem == null) {
	    rowItem = rowItemLst.get(rowItemLst.size() - 1);
	}
	Class<?> rowItemClass = rowItem.getClass();
	Object newRowItem = rowItemClass.newInstance();
	copyVal(newRowItem, rowItem);
	int newRowItemIdx = rowItemLst.indexOf(rowItem) + 1;
	rowItemLst.add(newRowItemIdx, newRowItem);
	refresh();
    }

}
