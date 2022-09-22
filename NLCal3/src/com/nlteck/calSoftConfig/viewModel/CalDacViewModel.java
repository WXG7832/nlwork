package com.nlteck.calSoftConfig.viewModel;

import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalibratePlanDot;

public class CalDacViewModel extends ViewModel {

    protected CalibratePlanDot calDAC;

    public CalDacViewModel(CalibratePlanDot calDAC) {
	super(calDAC);
	this.calDAC = calDAC;
    }

    @Override
    protected Object newModel() {
	return calDAC;
    }

    @Override
    public void exceptionReceiver(Throwable e) {
	notifyErrorLogEvent(e);
    }

}
