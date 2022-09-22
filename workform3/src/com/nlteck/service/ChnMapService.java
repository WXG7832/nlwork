package com.nlteck.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.nltecklib.utils.LogUtil;
import com.nltecklib.utils.XmlUtil;

import java.util.Optional;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年2月14日 上午11:37:42 通道映射管理器
 */
public class ChnMapService {

	private boolean enable;
	private Map<Integer,Map<Integer, Integer>> map = new HashMap<>();
	private Document document;

	private final static String PATH = "config/chnMap.xml";

	private Logger logger;

	public ChnMapService() {

		try {
			logger = LogUtil.getLogger("chnMapService");

			if (new File(PATH).exists()) {
				document = XmlUtil.loadXml(PATH);
				loadXml();

				logger.info("chn map size = " + map.size());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 从配置文件加载映射关系
	 * 
	 * @author wavy_zheng 2022年2月16日
	 */
	public void loadXml() {

		Element root = document.getRootElement();
		if (root == null) {

			return;
		}

		enable = Boolean.parseBoolean(root.attributeValue("enable"));

		List<Element> mapEles = root.elements("chnMap");

		for (Element mapEle : mapEles) {
			
			int calboardIndex = Integer.parseInt(mapEle.attributeValue("calboardIndex"));
			
			List<Element> chnEles = mapEle.elements("chn");
			for (Element ele : chnEles) {

				int deviceChnIndex = Integer.parseInt(ele.attributeValue("chnIndex"));
				int mapChnIndex = Integer.parseInt(ele.attributeValue("mapChnIndex"));
				saveMapRelation(calboardIndex, deviceChnIndex, mapChnIndex);
			}

		}
	}

	/**
	 * 通过实际通道获取映射通道序号
	 * 
	 * @author wavy_zheng 2022年2月14日
	 * @param deviceChnIndex
	 * @return
	 */
	public int mapChnIndex(int calboardIndex , int deviceChnIndex) {

		Map<Integer,Integer> calMap = map.get(calboardIndex);
		if (calMap == null) {

			return deviceChnIndex;
		}
		if(calMap.containsKey(deviceChnIndex)) {
			
			return calMap.get(deviceChnIndex);
		}
	

		return deviceChnIndex;

	}

	/**
	 * 通过映射通道找到设备实际通道序号
	 * 
	 * @author wavy_zheng 2022年2月14日
	 * @param mapChnIndex
	 * @return
	 */
	public int findDeviceChnIndexByMap(int calboardIndex , int mapChnIndex) {
        
		Map<Integer,Integer> calMap = map.get(calboardIndex);
		if(calMap == null) {
			
			return mapChnIndex;
		}
		
		Optional<Integer> op = calMap.entrySet().stream().filter(x -> x.getValue().equals(mapChnIndex))
				.map(Map.Entry::getKey).findFirst();
		if (op.isPresent()) {

			return op.get();
		}
		// 找不到则直接当成设备通道
		return mapChnIndex;

		// throw new Exception("can not find device chn index by map chnIndex :" +
		// mapChnIndex);
	}

	public void saveMapRelation(int boardIndex , int deviceChnIndex, int mapChnIndex) {

		Map<Integer,Integer> val = map.get(boardIndex);
		if(val == null) {
			
			val = new HashMap<>();
			map.put(boardIndex, val);
		}
		val.put(deviceChnIndex, mapChnIndex);
		
		

	}
	
	
	

	/**
	 * 清除映射关系
	 * 
	 * @author wavy_zheng 2022年2月14日
	 */
	public void clearAllMap() {

		map.clear();
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * 批量映射通道
	 * 
	 * @author wavy_zheng 2022年2月14日
	 * @param mapEntrys
	 *            key 实际通道 value 映射后通道
	 */
	public void mapChnIndexs(int calboardIndex , List<Entry<Integer, Integer>> mapEntrys) {
       
		Map<Integer,Integer> calMap = this.map.get(calboardIndex);
		if(calMap == null) {
			
			calMap = new HashMap<>();
			this.map.put(calboardIndex, calMap);
		}
		
		for (Entry<Integer, Integer> entry : mapEntrys) {
			calMap.put(entry.getKey(), entry.getValue());
		}

	}
	
	/**
	 * 找到校准板上要映射的原通道序号集合
	 * @author  wavy_zheng
	 * 2022年3月4日
	 * @param boardIndex
	 * @return
	 */
	public List<Integer>  findAllCalboardChannels(int boardIndex) {
		
		List<Integer> channels = new ArrayList<>();
		Map<Integer,Integer> calMap = this.map.get(boardIndex);
		if(calMap != null) {
			
			channels.addAll(calMap.keySet());
			
		}

		return channels;
	}

}
