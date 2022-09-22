package com.nlteck.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.core.runtime.Platform;

import com.nlteck.utils.XmlUtil;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
* @author  wavy_zheng
* @version 创建时间：2022年3月26日 下午1:46:34
* 测试的基础配置
*/
public class BaseCfg {
   
	public final static String PATH = "calCfg/baseCfg.xml";
	
	public enum  TestMode {
		
		DEBUG/*调试模式*/ , FACTORY/*生产模式*/
		
	}
	
	public static class Protocol {
		
		public int moduleCount = 2;
		
	}
	
	
	/**
	 * 万用表值检测
	 */
	public MeterValueFilter meterValueFilter=new MeterValueFilter();
	public static class MeterValueFilter{
		public boolean use;
		public boolean show;
		public List<MeterFilterParams> meterFilterParams=new ArrayList<>();
	}
	public static class MeterFilterParams{
		public CalMode mode;
		public Pole pole;
		public int level;
		public double threshold; // 万用表
		public double maxRange; //
	}
	
	/**
	 * 测试名
	 * @author wavy_zheng
	 * 2022年3月27日
	 *
	 */
	public enum TestName {
		
		
		
		/**
		 * 通用测试名
		 */
		WRITE_FLASH("写入默认flash") , ENTER_CAL("进入校准模式") , ENTER_NORMAL("进入待测模式") , CALBOARD_DC("校准板切到DC模式") , CALBOARD_CC("校准板切到CC模式") , CALBOARD_CV("校准板切到CV模式") , CALBOARD_VOLT_TEST("校准板切到电压对比模式"), CALBOARD_SLEEP("校准板切到SLEEP模式"),
		CALBOARD_SHORT_POSTIVE("校准板切到正极短路模式") , CALBOARD_SHORT_NEGTIVE("校准板切到负极短路模式"),  SWITCH_METER("切表") , READ_METER("读表"), SLEEP("延时"),
		/**
		 * 自检项
		 */
		DEVICE_SELFCHECK("自检设备") ,SRAM_CHECK("驱动板SRAM自检") , FLASH_CHECK("驱动板FLASH自检") , CAL_CHECK("驱动板校准系数自检"), AD_PICK_CHECK("驱动板AD采集板自检") , TEMP_PICK_CHECK("驱动板温度采集板自检"), BACKUP_CHECK("驱动板备份板自检"),
		/**
		 * 电压对比
		 */
		MAIN_BACK_COMPARAM("主电压vs备份电压"), MAIN_POWER_COMPARAM("主电压vs功率电压"), BACK_POWER_COMPARAM("备份电压vs功率电压"),
		/**
		 * 虚压扫描测试
		 */
		VIRTUAL_VOLTAGE_SCAN("虚电压扫描"),
		/**
		 * 流程启动
		 */
		PROCEDURE("启动流程")
		;
		
		private String desc;
		
		private TestName(String desc) {
			
			this.desc = desc;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.desc;
		}
		
		
		public static TestName parse(String desc) {
			
			for(TestName tn : TestName.values()) {
				
				if(tn.desc.equals(desc)) {
					
					return tn;
				}
				
			}
			
			return null;
			
		}
		
	}
	
	public enum  RunMode {
		
		DefaultFlash/*写入默认系数*/ , Cal/*校准*/ , StableTest/*稳定度测试*/ , VoltTest/*电压比较*/ , 
		VirtualVoltTest/*虚压测试*/,ShortCircuitTest/*短路测试*/ , SelfCheck/*自检*/ ;
		
		
		@Override
		public String toString() {
			
			switch(this) {
			case Cal:
				return "校准计量";
			case DefaultFlash:
				return "写入默认系数";
			case ShortCircuitTest:
				return "短路接地测试";
			case StableTest:
				return "稳定度测试";
			case VirtualVoltTest:
				return "虚压测试";
			case VoltTest:
				return "电压对比测试";
			case SelfCheck:
				return "驱动板自检测试";
			
			}
			
			return super.toString();
		}
		
		public static RunMode parse(String description) {
			
			if(description.equals("校准计量")) {
				
				return RunMode.Cal;
			}
            if(description.equals("写入默认系数")) {
				
				return RunMode.DefaultFlash;
			}
            if(description.equals("短路接地测试")) {
				
				return RunMode.ShortCircuitTest;
			}
            if(description.equals("稳定度测试")) {
				
				return RunMode.StableTest;
			}
            if(description.equals("虚压测试")) {
				
				return RunMode.VirtualVoltTest;
			}
            if(description.equals("电压对比测试")) {
				
				return RunMode.VoltTest;
			}
            
			
			 
			return null;
		}
		
	}
	
	//默认调试模式
	public TestMode   testMode = TestMode.DEBUG;
	
	//默认只校准
	public List<RunMode>    runModes  =  new ArrayList<>();
	
	//测试项编辑
	public Map<RunMode,List<TestItemDataDO>> testItemMap = new HashMap<>();
	
	
	public Protocol   protocol = new Protocol();
	
	
	public BaseCfg() {
		
		runModes.add(RunMode.Cal);
	}
	
	
	/**
	 * 加载配置文件
	 * @author  wavy_zheng
	 * 2022年3月26日
	 * @return
	 * @throws Exception 
	 */
	public static BaseCfg  loadCfgFile() throws Exception {
		
//		String dir = Platform.getInstallLocation().getURL().getPath();
		File file = new File(PATH);
		BaseCfg base = new BaseCfg();
		if(!file.exists()) {
			
			return base;
		}
		
		
	    Document doc = XmlUtil.loadXml(PATH);
	    Element root = doc.getRootElement();
	    base.testMode = TestMode.valueOf(root.attributeValue("testMode"));
		List<Element> runs = root.element("testItems").elements("runMode");
		base.runModes.clear();
		
		for(Element run : runs) {
			
			RunMode rm = RunMode.valueOf(run.attributeValue("mode"));
			base.runModes.add(rm);
			List<Element> items = run.elements("testItem");
			for(Element item : items) {
				
				TestItemDataDO testItem = new TestItemDataDO();
				TestName tn = TestName.valueOf(item.attributeValue("name"));

				testItem.setName(tn);
                if(item.attributeValue("param") != null) {
					
					testItem.setParam(item.attributeValue("param"));
				}
                if(item.attributeValue("lower") != null) {
                	
                	testItem.setLower(Double.parseDouble(item.attributeValue("lower")));
                }
                
                if(item.attributeValue("upper") != null) {
                	
                	testItem.setUpper(Double.parseDouble(item.attributeValue("upper")));
                }
                
                List<TestItemDataDO> list = base.testItemMap.get(rm);
                if(list == null) {
                	
                	list = new ArrayList<>();
                	base.testItemMap.put(rm, list);
                }
                list.add(testItem);
				
			}
			
		}
		//模片数据
		Element protocolEle = root.element("protocol");
		if(protocolEle != null) {
			
			base.protocol.moduleCount = Integer.parseInt(protocolEle.attributeValue("moduleCount"));
			
		}
		
		Element meterAdjustEle=root.element("meterAdjust");
		if(meterAdjustEle!=null) {
			base.meterValueFilter.use=Boolean.parseBoolean(meterAdjustEle.attributeValue("use"));
			base.meterValueFilter.show=Boolean.parseBoolean(meterAdjustEle.attributeValue("show"));
			List <Element> meterjustEles=meterAdjustEle.elements();
			for(Element meterjustEle:meterjustEles) {
				MeterFilterParams meterFilterParams=new MeterFilterParams();
				meterFilterParams.mode=CalMode.valueOf(meterjustEle.attributeValue("mode"));
				meterFilterParams.pole=Pole.valueOf(meterjustEle.attributeValue("pole"));
				meterFilterParams.level=Integer.valueOf(meterjustEle.attributeValue("level"));
				meterFilterParams.threshold=Double.valueOf(meterjustEle.attributeValue("threshold"));
				meterFilterParams.maxRange=Integer.valueOf(meterjustEle.attributeValue("maxRange"));
				base.meterValueFilter.meterFilterParams.add(meterFilterParams);
			}
		}
		
		return base;
		
		
	}


	@Override
	public String toString() {
		return "BaseCfg [testMode=" + testMode + ", runModes=" + runModes + "]";
	}
	
	
	
	
	
}
