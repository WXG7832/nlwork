package com.nlteck.calSoftConfig.viewModel.composite;


import com.nlteck.calSoftConfig.model.BaseConfig;
import com.nlteck.calSoftConfig.model.BaseConfig.AdcAdjust;
import com.nlteck.calSoftConfig.model.BaseConfig.CalBoardParam;
import com.nlteck.calSoftConfig.model.BaseConfig.CalculateValidate;
import com.nlteck.calSoftConfig.model.BaseConfig.CommType;
import com.nlteck.calSoftConfig.model.BaseConfig.IOParam;
import com.nlteck.calSoftConfig.model.BaseConfig.Network;
import com.nlteck.calSoftConfig.model.BaseConfig.Port;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.ConfigDialogViewModel;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

/**
 * @description 基础参数视图模型
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:49:14 AM
 */
public class BaseConfigViewModel extends ConfigDialogViewModel {
    protected BaseConfig baseConfig;

    public BaseConfigViewModel(ViewModelComposite composite) {
	super(composite);
	localPath = "temp/base.xml";
	remotePath = "~/config/base.xml";
    }

    /**
     * @description 校准板参数表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 3:14:11 PM
     */
    public void calBoardLstAdd() {
	CalBoardParam calBoard = new CalBoardParam();
	calBoard.index = baseConfig.calboards.size();
	calBoard.commType = CommType.SERIAL;
	calBoard.commIndex = 0;
	calBoard.meterIndex = 0;
	calBoard.disabled = false;
	tableViewerAdd(baseConfig.calboards, calBoard);
    }

    /**
     * @description 校准板参数表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 3:13:45 PM
     */
    public void calBoardLstDel(CalBoardParam elememt) {
	tableViewerDel(baseConfig.calboards, elememt);
	for (int n = 0; n < baseConfig.calboards.size(); n++) {
	    baseConfig.calboards.get(n).index = n;
	}
	refreshControl();
    }

    /**
     * @description 计量偏差上限表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 3:14:21 PM
     */
    public void calculateValidateLstAdd() {
	tableViewerAdd(baseConfig.calculateValidates, new CalculateValidate());
    }

    /**
     * @description 计量偏差上限表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 16, 2022 3:14:23 PM
     */
    public void calculateValidateLstDel(CalculateValidate elememt) {
	tableViewerDel(baseConfig.calculateValidates, elememt);
    }

    /**
     * @description 最终ADC作假参数表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 17, 2022 11:06:22 AM
     */
    public void adcAdjustLstAdd() {
	AdcAdjust element = new AdcAdjust();
	element.mode = CalMode.CC;
	element.pole = Pole.POSITIVE;
	tableViewerAdd(baseConfig.adjustParam.adcAdjusts, element);
    }

    /**
     * @description 最终ADC作假参数表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 17, 2022 11:06:22 AM
     */
    public void adcAdjustLstDel(AdcAdjust elememt) {
	tableViewerDel(baseConfig.adjustParam.adcAdjusts, elememt);
    }

    /**
     * @description 万用表参数表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 20, 2022 9:19:44 AM
     */
    public void meterLstAdd() {
	IOParam meter = new IOParam();
	meter.index = baseConfig.meters.size();
	meter.commType = CommType.NETWORK;
	meter.commIndex = 0;
	tableViewerAdd(baseConfig.meters, meter);
    }

    /**
     * @description 万用表参数表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 20, 2022 9:22:37 AM
     */
    public void meterLstDel(IOParam elememt) {
	tableViewerDel(baseConfig.meters, elememt);
    }

    /**
     * @description 串口参数表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 4:13:14 PM
     */
    public void serialPortLstAdd() {
	Port element = new Port();
	element.index = baseConfig.ports.size();
	element.name = "/dev/ttyS" + element.index;
	element.baudrate = 38400;
	tableViewerAdd(baseConfig.ports, element);
    }

    /**
     * @description 串口参数表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 4:16:30 PM
     */
    public void serialPortLstDel(Port element) {
	tableViewerDel(baseConfig.ports, element);
	for (int n = 0; n < baseConfig.ports.size(); n++) {
	    baseConfig.ports.get(n).index = n;
	}
	refreshControl();
    }

    /**
     * @description 网口参数表格_添加行
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 4:13:14 PM
     */
    public void ethernetPortLstAdd() {
	Network element = new Network();
	element.index = baseConfig.networks.size();
	element.ip = "192.168.1.1";
	element.async = true;
	tableViewerAdd(baseConfig.networks, element);
    }

    /**
     * @description 网口参数表格_删除行
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 4:16:30 PM
     */
    public void ethernetPortLstDel(Network element) {
	tableViewerDel(baseConfig.networks, element);
	for (int n = 0; n < baseConfig.networks.size(); n++) {
	    baseConfig.networks.get(n).index = n;
	}
	refreshControl();
    }

    @Override
    protected void loadXmlFile(String filePath) throws Exception {
	
	baseConfig.loadDocument(filePath);
    }

    @Override
    protected void saveXmlFile(String filePath) throws Exception {
	
	baseConfig.flush(filePath);
    }

    @Override
    protected Object newModel() {
	
	baseConfig = new BaseConfig();
	return baseConfig;
    }

    @Override
    protected String xmlFileDefaultPath() {
	
	return baseConfig.PATH;
    }

}
