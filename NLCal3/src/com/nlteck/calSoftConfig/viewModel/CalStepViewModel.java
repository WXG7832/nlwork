package com.nlteck.calSoftConfig.viewModel;

import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanMode;

public class CalStepViewModel extends ViewModel {

    protected CalibratePlanMode calStep;

    public CalStepViewModel(CalibratePlanMode calStep) {
	super(calStep);
	this.calStep = calStep;
    }

    @Override
    protected Object newModel() {
	return calStep;
    }

    @Override
    public void exceptionReceiver(Throwable e) {
	notifyErrorLogEvent(e);
    }

}
