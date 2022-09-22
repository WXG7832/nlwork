package com.nlteck.calSoftConfig.viewModel.composite;

import com.nlteck.calSoftConfig.model.DelayConfig;
import com.nlteck.calSoftConfig.model.DelayConfig.DetailConfig;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.ConfigDialogViewModel;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

public class DelayConfigViewModel extends ConfigDialogViewModel {

    protected DelayConfig delayConfig;

    public DelayConfigViewModel(ViewModelComposite composite) {
	super(composite);
    }

    @Override
    protected void loadXmlFile(String filePath) throws Exception {
	delayConfig.loadDelayConfig(filePath);
    }

    @Override
    protected void saveXmlFile(String filePath) throws Exception {
	delayConfig.flush(filePath);
    }

    @Override
    protected String xmlFileDefaultPath() {
	localPath = "temp/delay.xml";
	remotePath = "~/config/calConfig/delay.xml";
	return localPath;
    }

    @Override
    protected Object newModel() {
	
	delayConfig = new DelayConfig();
	return delayConfig;
    }

    /**
     * @description 工步延时参数表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 30, 2022 4:01:43 PM
     */
    public void stepSettingLstAdd() {
	DetailConfig item = new DetailConfig();
	item.mode = CalMode.CC;
	item.pole = Pole.POSITIVE;
	item.precision = 0;
	tableViewerAdd(delayConfig.detailConfigs, item);
    }

    /**
     * @description 工步延时参数表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 30, 2022 4:04:12 PM
     */
    public void stepSettingLstDel(DetailConfig item) {
	tableViewerDel(delayConfig.detailConfigs, item);
    }
}
