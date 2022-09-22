package com.nlteck.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.nlteck.firmware.Channel;
import com.nlteck.util.CommonUtil;
import com.nlteck.util.XmlUtil;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;

/**
 * 数据管理器
 * @author Administrator
 *
 */
public class DataManager {
	private Document document;
	private String path;

	public DataManager(String path) {

		this.path = path;
		loadXml(path);
	}

	private void loadXml(String path) {

		try {

			int pos = path.lastIndexOf("/");
			File f = new File(path.substring(0, pos));
			f.mkdirs();

			File file = new File(path);
			if (!file.exists()) {

				document = DocumentHelper.createDocument();
			} else {
				document = XmlUtil.loadXml(path);

			}
			if (document.getRootElement() == null) {
				document.addElement("root");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public List<ChannelData> readAllOfflineData(){
		
		Element root = document.getRootElement();
		List<Node> nodes = root.selectNodes("chnData");
		
		List<ChannelData> list = new ArrayList<ChannelData>();
        if(nodes == null) {
			
			return list;
		}
		
		for(int i = 0 ; i < nodes.size() ; i++) {
			
			ChannelData chnData = new ChannelData();
			Element element = (Element)nodes.get(i);
			chnData.setUnitIndex(Integer.parseInt(element.attributeValue("logicIndex")));
			chnData.setChannelIndex(Integer.parseInt(element.attributeValue("channelIndex")));
			chnData.setVoltage(Double.parseDouble(element.attributeValue("voltage")));
			chnData.setCurrent(Double.parseDouble(element.attributeValue("current")));
			chnData.setCapacity(Double.parseDouble(element.attributeValue("capacity")));
			chnData.setEnergy(Double.parseDouble(element.attributeValue("energy")));
			chnData.setAccumulateCapacity(Double.parseDouble(element.attributeValue("accumulateCapacity")));
			chnData.setAccumulateEnergy(Double.parseDouble(element.attributeValue("accumulateEnergy")));
			chnData.setStepIndex(Integer.parseInt(element.attributeValue("stepIndex")));
			chnData.setLoopIndex(Integer.parseInt(element.attributeValue("loopIndex")));
			chnData.setState(ChnState.valueOf(element.attributeValue("state")));
			chnData.setTimeStepSpend(Integer.parseInt(element.attributeValue("timeStepSpend")));
			chnData.setTimeTotalSpend(Integer.parseInt(element.attributeValue("timeTotalSpend")));
			chnData.setTemp(Double.parseDouble(element.attributeValue("temp")));
			try {
				chnData.setDate(CommonUtil.parseTime(element.attributeValue("date"), "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			chnData.setWorkMode(WorkMode.valueOf(element.attributeValue("workMode")));
			list.add(chnData);
			
		}
		
		//删除文件
		new File(path).delete();
		
		return list;
		
	}
	
	/**
	 * 保存所有离线数据到硬盘
	 * @param mb
	 */
	public synchronized void saveChnOfflineData(Channel chn , Queue<ChannelData> dataList) {
		
		Element root = document.getRootElement();
		//添加数据
		while(!dataList.isEmpty()) {
			
			ChannelData chnData = dataList.poll();
			Element dataElement = root.addElement("chnData");
			Field[] fields = ChannelData.class.getDeclaredFields();
			for (Field f : fields) {

				f.setAccessible(true);
				try {
					
					String valStr = f.get(chnData).toString();
					if(f.getType() == Date.class) {
						
						valStr = CommonUtil.formatTime((Date)f.get(chnData), "yyyy-MM-dd HH:mm:ss");
					}
					dataElement.addAttribute(f.getName(), valStr);
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			
		}
	}
	
	/**
	 * 生成数据文件
	 */
	public synchronized void saveFile() {
		
		if(new File(path).exists()) {
			
			new File(path).delete();
		}
		try {
			XmlUtil.saveXml(path, document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
