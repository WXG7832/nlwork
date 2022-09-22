package com.nlteck.calSoftConfig.viewModel;

import java.lang.reflect.Field;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calSoftConfig.view.tableViewer.TableViewerExAutoProvide;

/**
 * @description 对应关系的值: 暂存成员变量, 控件,或对应关系模型
 * @author zemin_zhu
 * @dateTime Jun 15, 2022 10:48:59 AM
 */
public class MappingElement {
    public String fieldName;
    public MappingObject parent; // 自身所在的关系模型
    public Field parentField; // 成员变量自身所在的类, 作为成员变量在其父类中的字段
    public Object object;

    protected Object getObject() {
	if (object == null) {
	    throw new NullPointerException();
	}
	return object;
    }

    public MappingObject getAsMappingObject() {
	return (MappingObject) getObject();
    }

    public Control getAsControl() {
	return (Control) getObject();
    }

    public TableViewer getAsTableViewer() {
	return (TableViewer) getObject();
    }

    public Field getAsField() {
	return (Field) getObject();
    }

    public boolean isMappingObject() {
	return getObject() instanceof MappingObject;
    }

    public boolean isControl() {
	return getObject() instanceof Control;
    }

    public boolean isTableViewer() {
	return getObject() instanceof TableViewer;
    }

    /**
     * @description 给控件赋值
     * @author zemin_zhu
     * @throws Exception
     * @dateTime Jun 15, 2022 3:09:49 PM
     */
    public void setValueToControl(Object value) throws Exception {
	if (object instanceof CCombo) {
	    ((CCombo) object).setText(value + "");
	} else if (object instanceof Spinner) {
	    ((Spinner) object).setSelection((int) value);
	} else if (object instanceof CLabel) {
	    CLabel cLabel = (CLabel) object;
	    boolean currentValue = (boolean) cLabel.getData();
	    if ((boolean) value != currentValue) {
		cLabel.setData(!(boolean) value);
		Event event = new Event();
		event.button = 1;
		cLabel.notifyListeners(SWT.MouseUp, event);
	    }
	} else if (object instanceof TableViewerExAutoProvide) {
	    ((TableViewerExAutoProvide) object).setInputNotTableExItem(value);
	    ((TableViewerExAutoProvide) object).refresh();
	} else if (object instanceof TableViewer) {
	    ((TableViewer) object).setInput(value);
	    ((TableViewer) object).refresh();
	} else if (object instanceof Text) {
	    ((Text) object).setText(value + "");
	}
    }

    /**
     * @description 从控件取值
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 3:09:41 PM
     */
    public Object getValueFromControl() throws Exception {
	Object value = null;
	if (object instanceof CCombo) {
	    value = ((CCombo) object).getText();
	    // 尝试将值转为整数
	    try {
		value = Integer.parseInt((String) value);
	    } catch (Exception e) {
		// TODO: handle exception
	    }
	} else if (object instanceof Spinner) {
	    value = ((Spinner) object).getSelection();
	} else if (object instanceof CLabel) {
	    value = ((CLabel) object).getData();
	} else if (object instanceof TableViewerExAutoProvide) {
	    value = ((TableViewerExAutoProvide) object).getInputAfterAssignment();
	} else if (object instanceof TableViewer) {
	    value = ((TableViewer) object).getInput();
	} else if (object instanceof Text) {
	    value = ((Text) object).getText();
	    // 尝试将值转为小数
	    try {
		value = Double.parseDouble((String) value);
		value = Long.parseLong((String) value);
	    } catch (Exception e) {
		// TODO: handle exception
	    }
	}
	return value;
    }

}
