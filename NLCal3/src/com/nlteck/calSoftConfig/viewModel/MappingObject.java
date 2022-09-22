package com.nlteck.calSoftConfig.viewModel;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calSoftConfig.controller.Controller;
import com.nlteck.calSoftConfig.view.tableViewer.TableViewerExAutoProvide;



//import com.nlteck.controller.Controller;
//import com.nlteck.view.tableViewer.TableViewerExAutoProvide;

/**
 * @description 字段名与成员变量或控件的对应关系模型
 * @author zemin_zhu
 * @dateTime Jun 15, 2022 9:55:39 AM
 */
public class MappingObject extends Controller {
    public String fieldName;
    public MappingObject parent;
    protected Map<String, MappingElement> mappingElementMap = new HashMap<String, MappingElement>();

    /**
     * @description 存入键值对, 存入成员变量的情况, 同时保存_成员变量自身所在的类, 作为成员变量在其父类中的字段
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 4:24:06 PM
     */
    public void put(String fieldName, Object fieldOrControlOrMappingObject, Field parentField) {
	MappingElement element = new MappingElement();
	element.fieldName = fieldName;
	element.parent = this;
	if (parentField != null) {
	    element.parentField = parentField;
	}
	element.object = fieldOrControlOrMappingObject;
	// 添加监听者, 在ViewModel类中, 实现刷新数据模型
	if (fieldOrControlOrMappingObject instanceof Control
		|| fieldOrControlOrMappingObject instanceof TableViewerExAutoProvide) {
	    addListenerToControl(element);
	}
	mappingElementMap.put(fieldName, element);
    }

    /**
     * @description 存入键值对: 字段名 ---- 成员变量, 控件, 或对应关系模型
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 7:29:15 PM
     */
    public void put(String fieldName, Object fieldOrControlOrMappingObject) {
	put(fieldName, fieldOrControlOrMappingObject, null);
    }

    /**
     * @description 取出对应关系模型
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 7:27:35 PM
     */
    public MappingObject getAsMappingObject(String fieldName) {
	MappingObject mappingObject = null;
	if (!mappingElementMap.containsKey(fieldName)) {
	    mappingObject = new MappingObject();
	    mappingObject.fieldName = fieldName;
	    mappingObject.parent = this;
	    put(fieldName, mappingObject, null);
	} else {
	    mappingObject = mappingElementMap.get(fieldName).getAsMappingObject();
	}
	return mappingObject;
    }

    /**
     * @description 取出对应关系值
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 7:26:36 PM
     */
    public MappingElement getAsMappingElement(String fieldName) {
	return mappingElementMap.get(fieldName);
    }

    /**
     * @description 取出键值对
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 7:26:24 PM
     */
    public Set<Entry<String, MappingElement>> getEntrySet() {
	return mappingElementMap.entrySet();
    }

    /**
     * @description 取出键集
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 11:27:33 AM
     */
    public Set<String> getKeySet() {
	return mappingElementMap.keySet();
    }

    /**
     * @description 取出值集
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 9:15:03 AM
     */
    public Collection<MappingElement> getValues() {
	return mappingElementMap.values();
    }

    /**
     * @description 添加监听者, 在ViewModel类中, 实现刷新数据模型
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 8:54:38 AM
     */
    protected void addListenerToControl(MappingElement element) {
	if (element.object instanceof Control) {

	    Control control = (Control) element.object;
	    if (control instanceof CLabel) {
		control.addMouseListener(new MouseListener() {

		    @Override
		    public void mouseUp(MouseEvent e) {
			
			triggerControlActionEvent(element);
		    }

		    @Override
		    public void mouseDown(MouseEvent e) {
			

		    }

		    @Override
		    public void mouseDoubleClick(MouseEvent e) {
			

		    }
		});
	    } else if (control instanceof CCombo) {
		((CCombo) control).addSelectionListener(new SelectionListener() {

		    @Override
		    public void widgetSelected(SelectionEvent e) {
			
			triggerControlActionEvent(element);
		    }

		    @Override
		    public void widgetDefaultSelected(SelectionEvent e) {
			

		    }
		});
	    } else if (control instanceof Spinner) {
		((Spinner) control).addSelectionListener(new SelectionListener() {

		    @Override
		    public void widgetSelected(SelectionEvent e) {
			
			triggerControlActionEvent(element);
		    }

		    @Override
		    public void widgetDefaultSelected(SelectionEvent e) {
			

		    }
		});
	    } else if (control instanceof Text) {
		((Text) control).addModifyListener(new ModifyListener() {

		    @Override
		    public void modifyText(ModifyEvent e) {
			
			triggerControlActionEvent(element);
		    }
		});

	    }
	} else if (element.object instanceof TableViewerExAutoProvide) {
	    TableViewerExAutoProvide tableViewer = (TableViewerExAutoProvide) element.object;
	    tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
		    
		    triggerControlActionEvent(element);
		}
	    });
	}
    }

    /**
     * @description 触发控件动作事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:36:35 AM
     */
    protected void triggerControlActionEvent(MappingElement eventArgs) {
	MappingObject mappingObject = this;
	while (mappingObject.parent != null) {
	    mappingObject = mappingObject.parent;
	}
	for (Listener listener : mappingObject.listeners) {
	    ((ControlActionListener) listener).onControlActionEvent(this, eventArgs);
	}
    }

    /**
     * @description 控件动作监听者
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 10:34:52 AM
     */
    public interface ControlActionListener extends Listener {
	public void onControlActionEvent(MappingObject sender, MappingElement eventArgs);
    }

}
