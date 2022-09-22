package com.nlteck.calSoftConfig.viewModel;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.calSoftConfig.controller.Controller;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.view.tableViewer.TableViewerExAutoProvide;
import com.nlteck.calSoftConfig.viewModel.MappingObject.ControlActionListener;

/**
 * @description 视图模型: 视图与数据模型的绑定关系
 * @author zemin_zhu
 * @dateTime Jun 15, 2022 10:00:31 AM
 */
public abstract class ViewModel extends Controller {
    protected Object model;
    public MappingObject controlMappingObject;
    public MappingObject fieldMappingObject;
    protected ViewModelComposite composite;
    protected Shell shell;

    public ViewModel() {
	init(newModel(), null);
    }

    public ViewModel(Object model) {
	init(model, null);
    }

    public ViewModel(Object model, ViewModelComposite composite) {
	init(model, composite);
    }

    public ViewModel(ViewModelComposite composite) {
	init(newModel(), composite);
    }

    protected void init(Object model, ViewModelComposite composite) {
	this.model = model;
	this.composite = composite;
	if (composite != null) {
	    shell = composite.getShell();
	}
	createMappingObject(model);
	createController();
    }

    /**
     * @description 实例化子视图模型
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 1:44:35 PM
     */
    public void createSubViewModel() {

    }

    /**
     * @description 实例化控制器
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 1:43:46 PM
     */
    protected void createController() {

    }

    protected void createMappingObject(Object model) {
	controlMappingObject = new MappingObject();
	controlMappingObject.addListener(new ControlActionListener() {

	    @Override
	    public void onControlActionEvent(MappingObject sender, MappingElement eventArgs) {
		refreshSpecificModel(eventArgs);
	    }

	    @Override
	    public void onLogEvent(Controller sender, LogEventArgs logEventArgs) {
	    }

	});
	if (model != null) {
	    fieldMappingObject = getAllFields(model.getClass());
	}
    }

    /**
     * @description 传入数据模型实例
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 10:01:33 AM
     */
    protected abstract Object newModel();

    /**
     * @description 刷新控件或数据模型
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 4:51:38 PM
     */
    protected void refreshControlOrModel(MappingObject controlMappingObject, MappingObject fieldMappingObject,
	    Object model, boolean refreshControlOrModel, MappingElement specificConrtolMappingElement) {
	Display.getDefault().asyncExec(new Runnable() {

	    @Override
	    public void run() {
		try {
		    for (Entry<String, MappingElement> pair : controlMappingObject.getEntrySet()) {
			String fieldName = pair.getKey();
			MappingElement controlMappingElement = pair.getValue();
			// 若控件关系模型的值, 也为关系模型, 代表对应的成员变量也为数据模型
			if (controlMappingElement.isMappingObject()) {
			    Object subModel = fieldMappingObject.getAsMappingElement(fieldName).parentField.get(model);
			    refreshControlOrModel(controlMappingElement.getAsMappingObject(),
				    fieldMappingObject.getAsMappingObject(fieldName), subModel, refreshControlOrModel,
				    specificConrtolMappingElement);
			} else {
			    if (specificConrtolMappingElement == null
				    || controlMappingElement == specificConrtolMappingElement) {
				Field field = fieldMappingObject.getAsMappingElement(fieldName).getAsField();
				if (field != null) {
				    // 给控件或数据模型赋值
				    if (refreshControlOrModel) {
					Object value = field.get(model);
					controlMappingElement.setValueToControl(value);
				    } else {
					Object value = controlMappingElement.getValueFromControl();
					if (value != null) {
					    field.set(model, value);
					}
				    }
				}
			    }
			}
		    }
		} catch (Exception e) {
		    String msg = null;
		    if (specificConrtolMappingElement != null) {
			msg = "刷新指定控件发生异常: " + specificConrtolMappingElement.fieldName;
		    } else if (refreshControlOrModel) {
			msg = "刷新控件发生异常";
		    } else {
			msg = "刷新模型发生异常";
		    }
		    Exception exception = new Exception(msg, e);
		    exceptionReceiver(exception);
		}

	    }
	});
    }

    /**
     * @description 刷新控件
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 10:02:39 AM
     */
    public void refreshControl() {
	refreshControlOrModel(controlMappingObject, fieldMappingObject, model, true, null);
    }

    /**
     * @description 刷新数据模型
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 10:02:50 AM
     */
    public void refreshModel() {
	refreshControlOrModel(controlMappingObject, fieldMappingObject, model, false, null);
    }

    /**
     * @description 刷新指定模型
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 5:57:52 PM
     */
    public void refreshSpecificModel(MappingElement specificControlMappingElement) {
	refreshControlOrModel(controlMappingObject, fieldMappingObject, model, false, specificControlMappingElement);
    }

    /**
     * @description 异常信息接收者
     * @author zemin_zhu
     * @dateTime Jun 17, 2022 10:32:29 AM
     */
    public abstract void exceptionReceiver(Throwable e);

    /**
     * @description 异常信息接收者, 接受说明性字符串
     * @author zemin_zhu
     * @dateTime Jun 27, 2022 11:08:54 AM
     */
    public void exceptionReceiver(String msg, Throwable e) {
	Throwable throwable = new Throwable(msg, e);
	exceptionReceiver(throwable);
    }

    public void exceptionReceiver(String msg) {
	Throwable throwable = new Throwable(msg);
	exceptionReceiver(throwable);
    }

    /**
     * @description 获得所有成员变量字段
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 9:59:57 AM
     */
    protected MappingObject getAllFields(Class clazz) {
	MappingObject mappingObject = new MappingObject();
	Object value;
	Field[] fieldArr = clazz.getDeclaredFields();
	for (Field field : fieldArr) {
	    // 若成员变量为自建类, 遍历该类的字段
	    Field parentField = null;
	    if (!field.getType().isPrimitive() && field.getType() != List.class && !field.getType().isEnum()
		    && field.getType() != String.class && field.getModifiers() == Modifier.PUBLIC) {
		value = getAllFields(field.getType());
		parentField = field;
	    } else {
		field.setAccessible(true);
		value = field;
	    }
	    mappingObject.put(field.getName(), value, parentField);
	}
	return mappingObject;
    }

    /**
     * @description 表格控件的数据源_添加行
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 3:29:21 PM
     */
    protected <T> void tableViewerAdd(List<T> dataSource, T elememt) {
	if (dataSource == null) {
	    dataSource = new ArrayList<T>();
	}
	dataSource.add(elememt);
	refreshControl();
    }

    /**
     * @description 表格控件的数据源_删除行
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 3:29:19 PM
     */
    protected <T> void tableViewerDel(List<T> dataSource, T elememt) {
	if (elememt != null) {
	    dataSource.remove(elememt);
	    refreshControl();
	}
    }

    /**
     * @description 获得数据模型的指定字段的值
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 11:32:37 AM
     */
    public Object getModelValue(String fieldName) throws Exception {
	Field field = fieldMappingObject.getAsMappingElement(fieldName).getAsField();
	return field.get(model);
    }

    /**
     * @description 获得数据模型的指定字段
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 11:32:37 AM
     */
    public Field getField(String fieldName) throws Exception {
	return fieldMappingObject.getAsMappingElement(fieldName).getAsField();
    }

    /**
     * @description 更换数据模型, 并刷新控件
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 12:50:07 PM
     */
    public void setModelAndRefreshControl(Object model) {
	this.model = model;
	refreshControl();
    }

    /**
     * @description 获得表格控件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 2:25:19 PM
     */
    protected TableViewer getTableViewer(String tableViewerFieldName) throws Exception {
	MappingElement mappingElement = controlMappingObject.getAsMappingElement(tableViewerFieldName);
	if (mappingElement == null) {
	    throw new Exception("字段名" + tableViewerFieldName + "未绑定表格控件");
	}
	TableViewer tableViewer = mappingElement.getAsTableViewer();
	return tableViewer;
    }

    /**
     * @description 获得表格控件当前选中行
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 11:48:56 AM
     */
    protected Object getSelectedRow(String tableViewerFieldName) throws Exception {
	TableViewer tableViewer = getTableViewer(tableViewerFieldName);
	return tableViewer.getStructuredSelection().getFirstElement();
    }

    /**
     * @description 获得自实行供应者表格控件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 2:29:14 PM
     */
    protected TableViewerExAutoProvide getTableViewerExAutoProvide(String tableViewerFieldName) throws Exception {
	return (TableViewerExAutoProvide) getTableViewer(tableViewerFieldName);
    }

    /**
     * @description 删除文件夹
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 2:11:18 PM
     */
    protected void deleteFolder(File file) {
	File[] contents = file.listFiles();
	if (contents != null) {
	    for (File f : contents) {
		if (!Files.isSymbolicLink(f.toPath())) {
		    deleteFolder(f);
		}
	    }
	}
	file.delete();
    }

    /**
     * @description 重命名文件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 2:19:06 PM
     */
    protected String renameFile(String path, String newName) {
	File src = new File(path);
	File dest = new File(src.getParent() + "/" + newName);
	src.renameTo(dest);
	return dest.getPath();
    }
}
