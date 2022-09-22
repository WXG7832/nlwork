package com.nlteck.calSoftConfig.view.tableViewer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.nlteck.calSoftConfig.controller.ClassFactory;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;

/**
 * @description 用于校准方案的表格控件
 * @author zemin_zhu
 * @dateTime Jul 2, 2022 2:36:58 PM
 */
public class CalPlanTableViewer extends TableViewerExAutoProvide {

    protected Class<?> calStepClass;

    public CalPlanTableViewer(Composite parent, boolean editable, boolean dragable) {
	super(parent, editable, dragable);
	
    }

    @Override
    public void setInputNotTableExItem(Object input) throws Exception {
	List<TableExItem> calStepLst = null;
	if (isTableExItemLst(input)) {
	    calStepLst = (List<TableExItem>) input;
	} else {
	    List<TableExItem> rawCalStepLst = getTableExItemLst(input);
	    calStepLst = getCalStepLst(rawCalStepLst);
	}

	setInput(calStepLst);
    }

    protected Class<?> getCalStepClass() throws Exception {
	if (calStepClass == null) {
	    calStepClass = ClassFactory.getInstance().newClass("CalStep", CalibratePlanMode.class,
		    CalibratePlanDot.class.getDeclaredFields());
	}
	return calStepClass;
    }

    /**
     * @description 将传入数据源, 重构为包含DAC的校准工步
     * @author zemin_zhu
     * @dateTime Jul 2, 2022 8:15:33 PM
     */
    protected List<TableExItem> getCalStepLst(List<TableExItem> rawCalStepLst) throws Exception {
	List<TableExItem> calStepLst = new ArrayList<TableExItem>();
	Field calDacModelField = tableExItemProxyFactory.getItemFieldMap().get("dots");
	calStepClass = getCalStepClass();
	tableExItemProxyFactory.setSuperclass(calStepClass);
	tableExItemProxyFactory.getItemFieldMap(calStepClass);
	for (TableExItem rawCalStep : rawCalStepLst) {
	    List<CalibratePlanDot> calDacLst = (List<CalibratePlanDot>) calDacModelField.get(rawCalStep);
	    for (CalibratePlanDot calDAC : calDacLst) {
		Object calStep = calStepClass.newInstance();
		ClassFactory.getInstance().copyVal(calStep, rawCalStep);
		ClassFactory.getInstance().copyVal(calStep, calDAC);
		TableExItem tableExItem = getTableExItem(calStep);
		calStepLst.add(tableExItem);
	    }
	}
	return calStepLst;
    }

    @Override
    public Object getInputAfterAssignment() throws Exception {
	Field rawCalStepModelField = tableExItemProxyFactory.getItemFieldMap()
		.get(CalibratePlanMode.class.getSimpleName());
	List<Object> calStepLst = (List<Object>) getInput();
	Map<Object, List<CalibratePlanDot>> rawCalStepMap = new LinkedHashMap<Object, List<CalibratePlanDot>>();
	for (Object calStep : calStepLst) {
	    Object rawCalStep = rawCalStepModelField.get(calStep);
	    ClassFactory.getInstance().copyVal(rawCalStep, calStep);
	    List<CalibratePlanDot> calDacLst = rawCalStepMap.get(rawCalStep);
	    if (calDacLst == null) {
		calDacLst = new ArrayList<>();
		rawCalStepMap.put(rawCalStep, calDacLst);
	    }
	    CalibratePlanDot calDAC = new CalibratePlanDot();
	    calDacLst.add(calDAC);
	    ClassFactory.getInstance().copyVal(calDAC, calStep);
	}
	List<CalibratePlanMode> rawCalStepLst = new ArrayList<>();
	for (Map.Entry<Object, List<CalibratePlanDot>> pair : rawCalStepMap.entrySet()) {
	    CalibratePlanMode rawCalStep = (CalibratePlanMode) pair.getKey();
	    List<CalibratePlanDot> calDacLst = pair.getValue();
	    rawCalStep.dots = calDacLst;
	    rawCalStepLst.add(rawCalStep);
	}
	return rawCalStepLst;
    }

}
