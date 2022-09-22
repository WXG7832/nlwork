package com.nlteck.calSoftConfig.view.tableViewer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nlteck.swtlib.table.TableViewerEx.TableExItem;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

/**
 * @description 表格控件的数据源工厂
 * @author zemin_zhu
 * @dateTime Jul 2, 2022 2:36:21 PM
 */
public class TableExItemProxyFactory extends ProxyFactory {

    protected Class<?>[] innerTypeArr = new Class[] { String.class, List.class, Date.class };
    protected Map<String, Field> itemFieldMap;
    protected String[] enabledFieldNameArr;

    public TableExItemProxyFactory() {
	// 设置过滤器，判断哪些方法调用需要被拦截
	setFilter(new MethodFilter() {
	    @Override
	    public boolean isHandled(Method method) {
		if (method.getName().equals("flushItemText")) {
		    return true;
		}
		return false;
	    }
	});
	// 设置拦截处理
	setHandler(new MethodHandler() {
	    @Override
	    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
		if (enabledFieldNameArr == null || enabledFieldNameArr.length == 0) {
		    throw new Exception("启用字段名为空");
		}
		int columnIdx = (int) args[0];
		String content = (String) args[1];
		String fieldName = enabledFieldNameArr[columnIdx];
		Field field = itemFieldMap.get(fieldName);
		setFieldValue(field, self, content);
		return null;
	    }
	});
    }

    /**
     * @description 获得成员变量字段
     * @author zemin_zhu
     * @dateTime Jun 15, 2022 9:59:57 AM
     */
    public Map<String, Field> getItemFieldMap(Class<?> clazz) {
	itemFieldMap = new HashMap<String, Field>();
	Field[] fieldArr = getAllFields(clazz);
	for (Field field : fieldArr) {
	    field.setAccessible(true);
	    itemFieldMap.put(field.getName(), field);
	}
	return itemFieldMap;
    }

    public Map<String, Field> getItemFieldMap() {
	return itemFieldMap;
    }

    /**
     * @description 设置_启用字段名
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 10:16:10 AM
     */
    public void setEnabledFieldNameArr(String[] enableFieldNameArr) {
	this.enabledFieldNameArr = enableFieldNameArr;
    }

    @Override
    public void setSuperclass(Class<?> clazz) {
	if (getSuperclass() != clazz) {
	    if (clazz != TableExItem.class) {
		setInterfaces(new Class[] { TableExItem.class });
		super.setSuperclass(clazz);
	    }
	}
    }

    /**
     * @description 给字段赋值
     * @author zemin_zhu
     * @dateTime Jul 1, 2022 11:14:32 AM
     */
    protected void setFieldValue(Field field, Object object, String content) throws Exception {
	if (field.getType() == int.class) {
	    field.setInt(object, Integer.parseInt(content));
	} else if (field.getType() == double.class) {
	    field.setDouble(object, Double.parseDouble(content));
	} else if (field.getType() == boolean.class) {
	    field.setBoolean(object, Boolean.parseBoolean(content));
	} else if (field.getType() == long.class) {
	    field.setLong(object, Long.parseLong(content));
	} else if (field.getType().isEnum()) {
	    Object targetEnumValue = null;
	    for (Object enumValue : field.getType().getEnumConstants()) {
		if (enumValue.toString().equals(content)) {
		    targetEnumValue = enumValue;
		    break;
		}
	    }
	    field.set(object, targetEnumValue);
	} else {
	    field.set(object, content);
	}
    }

    /**
     * @describe 得到父类的field在内所有filed
     * @author zemin_zhu
     * @dateTime Mar 28, 2022 6:10:50 PM
     */
    protected Field[] getAllFields(Class<?> clazz) {
	List<Field> result = new ArrayList<Field>();

	Class<?> i = clazz;
	while (i != null && i != Object.class) {
	    Collections.addAll(result, i.getDeclaredFields());
	    i = i.getSuperclass();
	}

	return result.toArray(new Field[] {});
    }

}
