package com.nlteck.calSoftConfig.viewModel.composite;

import java.lang.reflect.Field;

import com.nlteck.calSoftConfig.controller.Controller;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.CalDacViewModel;
import com.nlteck.calSoftConfig.viewModel.CalStepViewModel;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;

public class CalPlanTableViewerVerViewModel extends CalibrationPlanViewModel {

    public CalDacViewModel calDacViewModel;
    public CalStepViewModel calStepViewModel;

    public CalPlanTableViewerVerViewModel(ViewModelComposite composite) {
	super(composite);
    }

    @Override
    public void createSubViewModel() {
	Listener viewModelListener = new Listener() {

	    @Override
	    public void onLogEvent(Controller sender, LogEventArgs logEventArgs) {
		exceptionReceiver(logEventArgs.throwable);
	    }
	};
	calDacViewModel = new CalDacViewModel(calibratePlanData.getModes().get(0).dots.get(0));
	calDacViewModel.addListener(viewModelListener);
	calStepViewModel = new CalStepViewModel(calibratePlanData.getModes().get(0));
	calStepViewModel.addListener(viewModelListener);
    }

    @Override
    public void refreshControl() {
	super.refreshControl();
	calDacViewModel.setModelAndRefreshControl(calibratePlanData.getModes().get(0).dots.get(0));
	calStepViewModel.setModelAndRefreshControl(calibratePlanData.getModes().get(0));
    }

    @Override
    protected void saveXmlFile(String filePath) throws Exception {
	for (CalibratePlanMode calStep : calibratePlanData.getModes()) {
	    setCalStepProperty(calStep);
	    for (CalibratePlanDot calDAC : calStep.dots) {
		setCalDacProperty(calDAC);
	    }
	}
	super.saveXmlFile(filePath);
    }

    /**
     * @description 为校准工步的属性赋值
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 11:39:21 AM
     */
    protected void setCalStepProperty(CalibratePlanMode calStep) throws Exception {
	for (String fieldName : calStepViewModel.controlMappingObject.getKeySet()) {
	    Field field = calStepViewModel.getField(fieldName);
	    Object value = calStepViewModel.getModelValue(fieldName);
	    field.set(calStep, value);
	}
    }

    /**
     * @description 为校准DAC的属性赋值
     * @author zemin_zhu
     * @dateTime Jul 3, 2022 11:39:21 AM
     */
    protected void setCalDacProperty(CalibratePlanDot calDAC) throws Exception {
	for (String fieldName : calDacViewModel.controlMappingObject.getKeySet()) {
	    Field field = calDacViewModel.getField(fieldName);
	    Object value = calDacViewModel.getModelValue(fieldName);
	    field.set(calDAC, value);
	}
    }

}
