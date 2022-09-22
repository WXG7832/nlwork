package com.nlteck.calSoftConfig.view.composite;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.nlteck.calModel.base.I18N;
import com.nlteck.calSoftConfig.controller.ControlFactory;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.ViewModel;
import com.nlteck.calSoftConfig.viewModel.composite.AvgAdcSettingViewModel;

//import com.nlteck.controller.ControlFactory;
//import com.nlteck.util.I18N;
//import com.nlteck.view.ViewModelComposite;
//import com.nlteck.viewModel.ViewModel;
//import com.nlteck.viewModel.composite.AvgAdcSettingViewModel;

public class AvgAdcSettingComposite extends ViewModelComposite {

    protected AvgAdcSettingViewModel avgAdcSettingViewModel;

    public AvgAdcSettingComposite(Composite parent, int style, Object[] args) {
	super(parent, style, args);
    }

    @Override
    protected void createControl(Composite parent) {
	
	Group group = ControlFactory.getInstance().newGroup(parent, "平均ADC参数", 8, 150);

	Spinner spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_queryAdcNum));
	avgAdcSettingViewModel.controlMappingObject.put("sampleCount", spinner);

	Text text = ControlFactory.getInstance().newText(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_adcOffsetMax));
	avgAdcSettingViewModel.controlMappingObject.put("maxSigma", text);

	text = ControlFactory.getInstance().newText(group, "备份ADC偏差上限");
	avgAdcSettingViewModel.controlMappingObject.put("maxSigmabackup1", text);

	text = ControlFactory.getInstance().newText(group, "回检芯片ADC偏差上限");
	avgAdcSettingViewModel.controlMappingObject.put("maxSigmabackup2", text);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_avgAdcExcept));
	avgAdcSettingViewModel.controlMappingObject.put("trailCount", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_queryAdcRetryMax));
	avgAdcSettingViewModel.controlMappingObject.put("adcReadCount", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group,
		I18N.getVal(I18N.CalibrateConfigDialog_label_queryAdcRetryDelay));
	avgAdcSettingViewModel.controlMappingObject.put("adcRetryDelay", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group, "CV工步重询次数");
	avgAdcSettingViewModel.controlMappingObject.put("adcReadCountCV", spinner);

	spinner = ControlFactory.getInstance().newSpinner(group, "CV工步重询延时");
	avgAdcSettingViewModel.controlMappingObject.put("adcRetryDelayCV", spinner);
    }

    @Override
    protected ViewModel createViewModel(ViewModelComposite composite) {
	
	avgAdcSettingViewModel = new AvgAdcSettingViewModel(composite);
	return avgAdcSettingViewModel;
    }

}
