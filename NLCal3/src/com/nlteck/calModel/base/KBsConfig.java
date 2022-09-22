package com.nlteck.calModel.base;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.nltecklib.utils.XmlUtil;

public class KBsConfig {
	public String path="calCfg/kbs.xml";
	public List<Double> programKs=new ArrayList<>();
	public List<Double> programBs=new ArrayList<>();
	public List<Double> adcKs=new ArrayList<>();
	public List<Double> adcBs=new ArrayList<>();

	public KBsConfig() {
		try {
			loadKbs(path);
			System.out.println("over");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadKbs(String path) throws Exception{
		Document doc = XmlUtil.loadXml(path);
		
		Element root=doc.getRootElement();
		Element programKBs=root.element("programKBs");
		Element pksList=programKBs.element("pks");
		List<Element> pks=pksList.elements();
		for(Element pk:pks) {
			programKs.add(Double.parseDouble(pk.getText()));
		}
		
		Element pbsList=programKBs.element("pbs");
		List<Element> pbs=pbsList.elements();
		for(Element pb:pbs) {
			programBs.add(Double.parseDouble(pb.getText()));
		}
		
		Element adcKBs=root.element("adcKbs");
		Element adcksList=adcKBs.element("adcks");
		List<Element> adcks=adcksList.elements();
		for(Element adck:adcks) {
			adcKs.add(Double.parseDouble(adck.getText()));
		}
		
		Element adcbsList=adcKBs.element("adcbs");
		List<Element> adcbs=adcbsList.elements();
		for(Element adcb:adcbs) {
			adcBs.add(Double.parseDouble(adcb.getText()));
		}
	}
	
	
}
