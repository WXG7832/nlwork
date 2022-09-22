package com.nlteck.calSoftConfig.viewModel.composite;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nlteck.calSoftConfig.viewModel.ConfigDialogViewModel;
import com.nltecklib.protocol.li.PCWorkform.SteadyCfgData;
import com.nltecklib.utils.XmlUtil;

public class AvgAdcSettingViewModel extends ConfigDialogViewModel {

    protected SteadyCfgData avgAdcSetting;

    public AvgAdcSettingViewModel(ViewModelComposite composite) {
	super(composite);
    }

    @Override
    protected void loadXmlFile(String filePath) throws Exception {
	SteadyCfgData data = avgAdcSetting;
	Document doc = XmlUtil.loadXml(filePath);
	Element rootElement = doc.getRootElement();

	data.setSampleCount(Integer.parseInt(rootElement.attributeValue("sampleCount")));
	data.setMaxSigma(Double.parseDouble(rootElement.attributeValue("maxSigma")));

	String str = rootElement.attributeValue("maxSigmaBackup1");
	if (str == null) {

	    data.setMaxSigmabackup1(data.getMaxSigma());
	} else {

	    data.setMaxSigmabackup1(Double.parseDouble(str));
	}

	str = rootElement.attributeValue("maxSigmaBackup2");
	if (str == null) {

	    data.setMaxSigmabackup2(data.getMaxSigma());
	} else {

	    data.setMaxSigmabackup2(Double.parseDouble(str));
	}

	data.setTrailCount(Integer.parseInt(rootElement.attributeValue("trailCount")));
	data.setAdcReadCount(Integer.parseInt(rootElement.attributeValue("adcReadCount")));
	data.setAdcRetryDelay(Integer.parseInt(rootElement.attributeValue("adcRetryDelay")));
	// 增加CV模式下稳定度读取策略
	data.setAdcReadCountCV(Integer.parseInt(rootElement.attributeValue("adcReadCountCV") == null ? "30"
		: rootElement.attributeValue("adcReadCountCV")));
	data.setAdcRetryDelayCV(Integer.parseInt(rootElement.attributeValue("adcRetryDelayCV") == null ? "2"
		: rootElement.attributeValue("adcRetryDelayCV")));

    }

    @Override
    protected void saveXmlFile(String filePath) throws Exception {
	SteadyCfgData data = avgAdcSetting;
	Document doc = DocumentFactory.getInstance().createDocument();

	Element rootElement = DocumentFactory.getInstance().createElement("steadyCfg");
	doc.setRootElement(rootElement);

	rootElement.addAttribute("sampleCount", data.getSampleCount() + "");
	rootElement.addAttribute("maxSigma", data.getMaxSigma() + "");
	rootElement.addAttribute("trailCount", data.getTrailCount() + "");
	rootElement.addAttribute("adcReadCount", data.getAdcReadCount() + "");
	rootElement.addAttribute("adcRetryDelay", data.getAdcRetryDelay() + "");
	rootElement.addAttribute("adcReadCountCV", data.getAdcReadCount() + "");
	rootElement.addAttribute("adcRetryDelayCV", data.getAdcRetryDelay() + "");

	XmlUtil.saveXml(filePath, doc);

    }

    @Override
    protected String xmlFileDefaultPath() {
	localPath = "temp/steadyCfg.xml";
	remotePath = "~/config/calConfig/steadyCfg.xml";
	return localPath;
    }

    @Override
    protected Object newModel() {
	
	avgAdcSetting = new SteadyCfgData();
	return avgAdcSetting;
    }
}
