package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2022年6月6日 下午1:57:47
* 类说明
*/
public class JsonProductCfgData extends Data implements Configable, Queryable, Responsable , Cloneable {
   
	
	public static class BaseCfg {
		
		public String productType;
		public int    driverChnCount;
		public boolean disableDefaultProtection;
		public String  language;

	}
	
	public static class SwitchCfg {
		
		public boolean useVirtualData;
		public boolean useAlert;
		public boolean useReverseChnIndex;
		public boolean useDebug;
		public boolean useStepChangeProtect;
		public boolean useSTM32Time;
		public boolean useDriverTempPick; //驱动板采集通道温度
	}
	
	
	public static class LogRecordCfg {
		
		public boolean printChnLog;
		public boolean printSysLog;
		public boolean printProtocolLog;
	}
	
	public static class RangeCfg {
		
		public boolean  use;
		public double   disableCurrentLine;
		public double   disableVoltageLine;
		public double   voltageFilterRange;
		public double   voltagePrecision;
		public double   voltageStartOffset; //压差保护上限
		public boolean   continueAlertFilter;
		public boolean   useCvCurrentFilter;
		public boolean   useDcVoltageFielter;
		public boolean   useCcVoltageFielter;
		public boolean   useSlpVoltageFielter;
		public List<RangeSection>  sections = new ArrayList<>();
	}
	
	
	public static class RangeSection {
		
		public int        level;
		public double     lower;
		public double     upper;
		public boolean    filter; //是否开启过滤?
		public double     currentFilterRange; //需要过滤的电流偏差范围
		public double     precision; //过滤后的偏差范围
	}
	
	
	public static class ProtocolCfg {
		
		public boolean useChnTempPick; //采集协议增加通道温度
		public boolean useFrameTempPick; //采集协议增加料框温度
		public boolean useAirPressure;  //流程使用气压控制协议
		public boolean useStepRecord; //流程使用步次数据记录协议
		public boolean useProcedureWorkType; //流程下发使用工艺型号
		public int     moduleCount; //当前协议使用的模片数量
	}
	
	
	public static class LimitCfg {
		
		
		public double     maxDeviceVoltage;
		public double     maxDeviceCurrent;
		public double     minDeviceVoltage;
		public double     minDeviceCurrent;
	}
	
	/**
	 * 驱动板配置
	 * @author wavy_zheng
	 * 2022年6月6日
	 *
	 */
	public static class DriversCfg {
		
		public int    index;
		public String  portName;
		public boolean  use;
		public long     communication;
		public long     pickupTime;
		
	}
	
	
	public static class ControlboardCfg {
		
		public boolean  use;
		public String   portName;
		public boolean  heartbeat;
		public long     communicateTimeout;
		
	}
	
	/**
	 * 恒温控制系统
	 * @author wavy_zheng
	 * 2022年6月6日
	 *
	 */
	public static class TempControlCfg {
		
		public boolean  use;
		public String   portName;
		public boolean  monitor; //开启监控?
	}
	/**
	 * 探头管理
	 * @author wavy_zheng
	 * 2022年6月6日
	 *
	 */
    public static class ProbeManagerCfg {
		
		public boolean  use;
		public String   portName;
		public boolean  monitor; //开启监控?
		public int      count;
	}
	/**
	 * 烟雾报警管理配置
	 * @author wavy_zheng
	 * 2022年6月6日
	 *
	 */
    public static class SmogManagerCfg {
		
		public boolean  use;
		public String   portName;
		public boolean  monitor; //开启监控?		
	}
    
    public static class ColorLightManagerCfg {
		
		public boolean  use;
		public String   portName;
		
	}
    
   public static class PoleLightManagerCfg {
		
		public boolean  use;
		public String   portName;
		
	}
   
   public static class BeepAlertManagerCfg {
		
		public boolean  use;
		public String   portName;
		
	}
   
   public static class DoorAlertManagerCfg {
		
		public boolean  use;
		public String   portName;
		
	}
   
   
   public static class ProcedureSupportCfg {
	   
	   public boolean supportDriver;
	   public boolean supportImportantData;
	   
   }
   
   public static class SmartTouchResister {
	   
	   public boolean enable;
	   public double  minCurrent; //最低电流要求
	   public double  protectionRatio; //保护比率
	   public int     sampleMinCount; //单次记录样本值的最小采集数据量
	   public int     trailCount;
	   
	   
   }
   
   /**
    * 智能保护
    * @author wavy_zheng
    * 2022年6月6日
    *
    */
   public static class SmartProtectionsCfg {
	   
	   public boolean enable;
	   public int     interval;
	   public SmartTouchResister  touch = new SmartTouchResister();
	   
   }
   
   /**
    * 液晶屏
    * @author wavy_zheng
    * 2022年6月6日
    *
    */
   public static class ScreenCfg {
	   
	   public boolean  use;
	   public String   portName;
	   public int communicateTimeout;
   }
   
   /**
    * 电源管理
    * @author wavy_zheng
    * 2022年6月6日
    *
    */
   public static class PowerManagerCfg {
	   
	   public boolean  use;
	   public String   portName;
	   public String   product;
	   public boolean  monitor;
	   public int      invertCount; //逆变电源数量
	   public int      auxiliaryCount; //辅助电源数量
	   
   }
   
   
   public static class FanManagerCfg {
	   
	   public boolean  use;
	   public String   portName;
	   public boolean  monitor;
	   public int      coolFanCount;
	   public int      turboFanCount;
   }
   
   public static class PingControllerCfg {
	   
	   public boolean enable;
	   public String  portName;
	   public int     communicateTimeout;
	   
	   
   }
   
   public static class ChnLightControllerCfg {
	   
	   public boolean enable;
	   public String  portName;
	   public int     communicateTimeout;
	   
	   
   }
   
  
   
   
    
   /**
    * 子配置
    */
   
    private BaseCfg       base   =   new BaseCfg();
    private SwitchCfg     switcher = new SwitchCfg();
    private LogRecordCfg  logRecord = new LogRecordCfg();
	private RangeCfg      range = new RangeCfg();
	private ProtocolCfg   protocol = new ProtocolCfg();
	private LimitCfg      limit = new LimitCfg();
	private List<DriversCfg> driverList = new ArrayList<>();
	private ControlboardCfg  controlBoard = new ControlboardCfg();
	private TempControlCfg   tempControl  = new TempControlCfg();
	private ProbeManagerCfg  probeManager = new ProbeManagerCfg();
	private PowerManagerCfg  powerManager = new PowerManagerCfg();
	private FanManagerCfg    fanManager   = new FanManagerCfg();
	private SmartProtectionsCfg  smartProtects = new SmartProtectionsCfg();
	private SmogManagerCfg    smogManager = new SmogManagerCfg();
	private DoorAlertManagerCfg doorManager = new DoorAlertManagerCfg();
	private BeepAlertManagerCfg beepManager = new BeepAlertManagerCfg();
	private ColorLightManagerCfg  colorLightManager = new ColorLightManagerCfg();
	private PoleLightManagerCfg   poleLightManager = new PoleLightManagerCfg();
	private ProcedureSupportCfg   procedureSupport = new ProcedureSupportCfg();
	private PingControllerCfg     pingController   = new PingControllerCfg();
	private ChnLightControllerCfg    chnLightController = new ChnLightControllerCfg();
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonStr = gson.toJson(this);
		System.out.println(jsonStr);
		
		try {
			byte[] arr = jsonStr.getBytes("utf-8");
			data.addAll(Arrays.asList(ProtocolUtil.split((long)arr.length, 2, true)));
			for (int n = 0; n < arr.length; n++) {

				data.add(arr[n]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("流程编码错误:" + e.getMessage());
		}


	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;

		int infoBytes = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		byte[] procBytes = new byte[infoBytes];
		for (int i = index; i < index + infoBytes; i++) {

			procBytes[i - index] = data.get(i);

		}
		index += infoBytes;
		try {
			String jsonStr = new String(procBytes, "utf-8");
			Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
			JsonProductCfgData jp = gson.fromJson(jsonStr, JsonProductCfgData.class);
            this.setBase(jp.getBase());
            this.setBeepManager(jp.getBeepManager());
            this.setColorLightManager(jp.getColorLightManager());
            this.setControlBoard(jp.getControlBoard());
            this.setDoorManager(jp.getDoorManager());
            this.setDriverList(jp.getDriverList());
            this.setFanManager(jp.getFanManager());
            this.setLimit(jp.getLimit());
            this.setLogRecord(jp.getLogRecord());
            this.setPoleLightManager(jp.getPoleLightManager());
            this.setPowerManager(jp.getPowerManager());
            this.setProbeManager(jp.getProbeManager());
            this.setProcedureSupport(jp.getProcedureSupport());
            this.setProtocol(jp.getProtocol());
            this.setRange(jp.getRange());
            this.setSmartProtects(jp.getSmartProtects());
            this.setSmogManager(jp.getSmogManager());
            this.setSwitcher(jp.getSwitcher());
            this.setTempControl(jp.getTempControl());
            this.setPingController(jp.getPingController());
            
            
			

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("流程解码错误:" + e.getMessage());
		}


	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.JsonProductCfgCode;
	}

	public BaseCfg getBase() {
		return base;
	}

	public void setBase(BaseCfg base) {
		this.base = base;
	}

	public SwitchCfg getSwitcher() {
		return switcher;
	}

	public void setSwitcher(SwitchCfg switcher) {
		this.switcher = switcher;
	}

	public LogRecordCfg getLogRecord() {
		return logRecord;
	}

	public void setLogRecord(LogRecordCfg logRecord) {
		this.logRecord = logRecord;
	}

	public RangeCfg getRange() {
		return range;
	}

	public void setRange(RangeCfg range) {
		this.range = range;
	}

	public ProtocolCfg getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolCfg protocol) {
		this.protocol = protocol;
	}

	public LimitCfg getLimit() {
		return limit;
	}

	public void setLimit(LimitCfg limit) {
		this.limit = limit;
	}

	public List<DriversCfg> getDriverList() {
		return driverList;
	}

	public void setDriverList(List<DriversCfg> driverList) {
		this.driverList = driverList;
	}

	public ControlboardCfg getControlBoard() {
		return controlBoard;
	}

	public void setControlBoard(ControlboardCfg controlBoard) {
		this.controlBoard = controlBoard;
	}

	public TempControlCfg getTempControl() {
		return tempControl;
	}

	public void setTempControl(TempControlCfg tempControl) {
		this.tempControl = tempControl;
	}

	public ProbeManagerCfg getProbeManager() {
		return probeManager;
	}

	public void setProbeManager(ProbeManagerCfg probeManager) {
		this.probeManager = probeManager;
	}

	public PowerManagerCfg getPowerManager() {
		return powerManager;
	}

	public void setPowerManager(PowerManagerCfg powerManager) {
		this.powerManager = powerManager;
	}

	public FanManagerCfg getFanManager() {
		return fanManager;
	}

	public void setFanManager(FanManagerCfg fanManager) {
		this.fanManager = fanManager;
	}

	public SmartProtectionsCfg getSmartProtects() {
		return smartProtects;
	}

	public void setSmartProtects(SmartProtectionsCfg smartProtects) {
		this.smartProtects = smartProtects;
	}

	public SmogManagerCfg getSmogManager() {
		return smogManager;
	}

	public void setSmogManager(SmogManagerCfg smogManager) {
		this.smogManager = smogManager;
	}

	public DoorAlertManagerCfg getDoorManager() {
		return doorManager;
	}

	public void setDoorManager(DoorAlertManagerCfg doorManager) {
		this.doorManager = doorManager;
	}

	public ColorLightManagerCfg getColorLightManager() {
		return colorLightManager;
	}

	public void setColorLightManager(ColorLightManagerCfg colorLightManager) {
		this.colorLightManager = colorLightManager;
	}

	public PoleLightManagerCfg getPoleLightManager() {
		return poleLightManager;
	}

	public void setPoleLightManager(PoleLightManagerCfg poleLightManager) {
		this.poleLightManager = poleLightManager;
	}

	public ProcedureSupportCfg getProcedureSupport() {
		return procedureSupport;
	}

	public void setProcedureSupport(ProcedureSupportCfg procedureSupport) {
		this.procedureSupport = procedureSupport;
	}

	public BeepAlertManagerCfg getBeepManager() {
		return beepManager;
	}

	public void setBeepManager(BeepAlertManagerCfg beepManager) {
		this.beepManager = beepManager;
	}

	public PingControllerCfg getPingController() {
		return pingController;
	}

	public void setPingController(PingControllerCfg pingController) {
		this.pingController = pingController;
	}
	
	
	

	public ChnLightControllerCfg getChnLightController() {
		return chnLightController;
	}

	public void setChnLightController(ChnLightControllerCfg chnLightController) {
		this.chnLightController = chnLightController;
	}

	@Override
	public String toString() {
		return "JsonProductCfgData [base=" + base + ", switcher=" + switcher + ", logRecord=" + logRecord + ", range="
				+ range + ", protocol=" + protocol + ", limit=" + limit + ", driverList=" + driverList
				+ ", controlBoard=" + controlBoard + ", tempControl=" + tempControl + ", probeManager=" + probeManager
				+ ", powerManager=" + powerManager + ", fanManager=" + fanManager + ", smartProtects=" + smartProtects
				+ ", smogManager=" + smogManager + ", doorManager=" + doorManager + ", beepManager=" + beepManager
				+ ", colorLightManager=" + colorLightManager + ", poleLightManager=" + poleLightManager
				+ ", procedureSupport=" + procedureSupport + "]";
	}
	
	
	

}
