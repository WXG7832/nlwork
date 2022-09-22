package com.nlteck.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.WorkBench.CalType;
import com.nlteck.firmware.WorkBench.DeviceType;
import com.nlteck.model.BaseCfg.RunMode;
import com.nlteck.model.BaseCfg.TestName;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.MeasureDotDO;
import com.nlteck.model.StableDataDO;
import com.nlteck.model.TestItemDataDO;
import com.nlteck.utils.CommonUtil;
import com.nlteck.utils.DBUtil;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment.Pole;

public class Database2Manager {

	protected Connection connection;

	private static Database2Manager databaseManager;

	private static ScheduledExecutorService executor;

	protected Database2Manager() {
	}

	public static Database2Manager getInstance() {

		if (databaseManager != null) {

			if (!databaseManager.isConnect()) {
				databaseManager.connect();
			}
			
			

			return databaseManager;
		}

		databaseManager = new Database2Manager();

		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				if (databaseManager.isConnect()) {

					try {
						databaseManager.pingQuery();
						System.out.println("query ping ok");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}, 1, 5, TimeUnit.MINUTES);

		if (!databaseManager.isConnect()) {
			databaseManager.connect();
		}
		
		try {
			databaseManager.createStableTable();
			databaseManager.createTestItemTable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return databaseManager;

	}

	public void connect() {

		try {
			connection = DBUtil.openDatabaseConnection(DBUtil.URL, DBUtil.USER, DBUtil.PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isConnect() {

		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public void disconnect() throws SQLException {

		if (connection != null) {
			executor.shutdown();
			DBUtil.closeDatabaseConnection(connection);
			connection = null;
			executor = null;
		}
	}

	public void pingQuery() throws SQLException {

		String sql = "select 1";
		DBUtil.executeQuerySql(connection, sql);
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isEmpty(String obj) {

		return obj == null || obj.isEmpty();
	}

	/**
	 * 删除设备
	 * 
	 * @author wavy_zheng 2021年1月18日
	 * @param device
	 * @throws SQLException
	 */
	public void removeDevice(Device device) throws SQLException {

		String sql = String.format("delete from device_table where id = %d", device.getId());
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);
	}

	/**
	 * 更新箱子
	 * 
	 * @author wavy_zheng 2021年1月18日
	 * @param id
	 * @param name
	 * @param ip
	 * @param info
	 * @throws SQLException
	 */
	public void updateBox(CalBox box, String name, String ip,   List<String> meterIps , String screenIp , String info) throws SQLException {

		String sql = String.format("update box_table set ");
		if (name != null) {

			sql += "name = '" + name + "',";
		}
		if (ip != null) {

			sql += "ip = '" + ip + "',";
		}
		if (info != null) {

			sql += "info = '" + info + "',";
		}
		
		if (screenIp != null) {

			sql += "screenIp = '" + screenIp + "',";
		}
		
		if(meterIps != null && !meterIps.isEmpty()) {
			
			for(int n = 0 ; n < meterIps.size() ; n++) {
			    sql += "meterIp" +  (n + 1) + " = '" +  meterIps.get(n) + "',";
			}
			
		}
		
		if (sql.substring(sql.length() - 1, sql.length()).equals(",")) {

			sql = sql.substring(0, sql.length() - 1);
		}

		sql += " where id = " + box.getId();
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);

		if (name != null) {

			box.setName(name);
		}
		if (ip != null) {

			box.setIp(ip);
		}
		if (info != null) {

			box.setInfo(info);
		}
		if(screenIp != null) {
			
			box.setScreenIp(screenIp);
		}

	}

	/**
	 * 更新设备
	 * 
	 * @author wavy_zheng 2021年1月18日
	 * @param id
	 * @param name
	 * @param ip
	 * @param info
	 * @throws SQLException
	 */
	public void updateDevice(Device device, String name, String ip, String info) throws SQLException {

		String sql = String.format("update device_table set ");
		if (name != null) {

			sql += "name = '" + name + "',";
		}
		if (ip != null) {

			sql += "ip = '" + ip + "',";
		}
		if (info != null) {

			sql += "info = '" + info + "',";
		}
		if (sql.substring(sql.length() - 1, sql.length()).equals(",")) {

			sql = sql.substring(0, sql.length() - 1);
		}

		sql += " where id = " + device.getId();
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);

		if (name != null) {

			device.setName(name);
		}
		if (ip != null) {

			device.setIp(ip);
		}
		if (info != null) {

			device.setInfo(info);
		}

	}

	private static String getMeterIpFrom(List<String> meters, int index) {

		if (index < meters.size()) {

			return meters.get(index);
		} else {

			return null;
		}

	}

	/**
	 * 查询校准箱
	 * 
	 * @author wavy_zheng 2021年1月18日
	 * @return
	 * @throws SQLException
	 */
	public List<CalBox> listCalBox(Device device) throws SQLException {

		String sql = "select * from box_table ";
		if (device != null) {

			sql += "where device_id = " + device.getId() + "";
		} else {

			sql += "where device_id is null";
		}
		List<CalBox> boxes = new ArrayList<>();

		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while (result.next()) {

			CalBox box = new CalBox();
			box.setId(result.getInt("id"));
			box.setName(result.getString("name"));
			//box.setCalBoardCount(result.getInt("calCount"));
			box.setDevice(device);
			box.setIp(result.getString("ip"));
			box.setMac(result.getString("mac"));
			box.setScreenIp(result.getString("screenIp"));
			List<String> ips = new ArrayList<>();
			String ip1 = result.getString("meterIp1");
			String ip2 = result.getString("meterIp2");
			if(ip1 != null && CommonUtil.checkIP(ip1)) {
				
				ips.add(ip1);
			}
            if(ip2 != null && CommonUtil.checkIP(ip2)) {
				
				ips.add(ip2);
			}

			boxes.add(box);

		}
		result.close();

		return boxes;

	}

	/**
	 * 通过设备名寻找设备
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param name
	 *            null时搜索所有设备
	 * @return
	 * @throws Exception
	 */
	public List<Device> findDeviceByName(String name) throws Exception {

		String sql = "select * from device_table ";
		if (name != null) {
			sql += "where name = '" + name + "'";
		}

		List<Device> devices = new ArrayList<>();

		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while (result.next()) {

			Device device = new Device();
			device.setId(result.getInt("id"));
			device.setChnNumInDriver(result.getInt("driverChnCount"));
			device.setDriverNumInLogic(result.getInt("logicDriverCount"));
			device.setIp(result.getString("ip"));
			device.setType(DeviceType.valueOf(result.getString("type")));
			device.setLogicNum(result.getInt("logicCount"));
			device.setMac(result.getString("mac"));
			device.setName(result.getString("name"));

			sql = "select * from box_table where device_id = " + device.getId();
			ResultSet boxResult = DBUtil.executeQuerySql(connection, sql);
			while (boxResult.next()) {

				CalBox box = new CalBox();
				box.setId(boxResult.getInt("id"));
				box.setIp(boxResult.getString("ip"));
				box.setMac(boxResult.getString("mac"));
				box.setName(boxResult.getString("name"));
				//box.setCalBoardCount(boxResult.getInt("calCount"));

				List<String> ips = new ArrayList<>();
				ips.add(boxResult.getString("meterIp1"));
				ips.add(boxResult.getString("meterIp2"));

				box.setMeterIp(boxResult.getString("meterIp1"));
				box.setMeterIps(ips);
				box.setScreenIp(boxResult.getString("screenIp"));
				box.setInfo(boxResult.getString("info"));

				box.setDevice(device);
				device.getCalBoxList().add(box);

			}
			boxResult.close();

			// 查询通道
			sql = "select * from channel_table where device_id = " + device.getId();
			ResultSet channelResult = DBUtil.executeQuerySql(connection, sql);
			while (channelResult.next()) {

				ChannelDO channel = new ChannelDO();
				channel.setId(channelResult.getInt("id"));
				channel.setCalType(channelResult.getString("calType") == null ? null
						: CalType.valueOf(channelResult.getString("calType")));
				channel.setChnIndex(channelResult.getInt("chnIndexInDevice"));
				channel.setResult(channelResult.getInt("result"));
				channel.setStartTime(CommonUtil.parseTime(channelResult.getString("startTime"), "yyyy-MM-dd HH:mm:ss"));
				channel.setEndTime(CommonUtil.parseTime(channelResult.getString("endTime"), "yyyy-MM-dd HH:mm:ss"));
				channel.setInfo(channelResult.getString("info"));
				channel.setState(CalState.valueOf(channelResult.getString("state")));
				channel.setSeconds(channelResult.getInt("seconds"));
				if(channel.getResult() == 1) {
					
					channel.setState(CalState.CALIBRATE_FAIL);
				} else if(channel.getResult() == 2) {
					
					channel.setState(CalState.CALIBRATE_PASS);
				}

				device.appendChannel(channel);
				
				
				
			}
			channelResult.close();
			devices.add(device);

		}

		return devices;

	}

	/**
	 * 将校准箱绑定到设备
	 * 
	 * @author wavy_zheng 2021年1月17日
	 * @param device
	 * @param box
	 * @throws Exception
	 */
	public void bindBoxToDevice(Device device, CalBox box) throws Exception {

		String sql = "update box_table set device_id = " + device.getId() + " where id = " + box.getId();
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);
		

	}

	public void unbindBox(CalBox box) throws SQLException {

		if (box.getDevice() != null) {
			String sql = "update box_table set device_id = null where id = " + box.getId();
			System.out.println(sql);
			DBUtil.executeSql(connection, sql);
			
		}
	}

	public void createCalbox(CalBox box) throws SQLException {

		String sql = "insert into box_table(mac,name,ip,calCount,meterCount)value('%s','%s','%s',%d,%d)";
		sql = String.format(sql, box.getMac(), box.getName(), box.getIp(), box.getCalBoardCount(),
				box.getMeterIps().size());

		int id = DBUtil.executeSql(connection, sql);
		box.setId(id);
	}

	public void removeCalbox(CalBox box) throws Exception {

		String sql = "delete from box_table where id = " + box.getId();
		DBUtil.executeSql(connection, sql);
		
		
	}

	private static int getTotalCount(int logicCount, int driverCount, int chnCountInDriver) {

		return driverCount * chnCountInDriver * logicCount;

	}
	
	/**
	 * 保存计量点数据
	 * @author  wavy_zheng
	 * 2021年1月19日
	 * @param mdd
	 * @throws SQLException 
	 */
	public void saveMeasureDot(MeasureDotDO  mdd) throws SQLException {
		
		String sql = String.format("insert into measure_dot_table(chn_id,mode,pole,calculateDot,finalAdc,meterVal,result,info) values(%d,'%s','%s',%.3f,%.3f,%.3f,'%s','%s')",
				    mdd.getChannel().getId(),mdd.getMode(),mdd.getPole(),mdd.getCalculateDot(),mdd.getFinalAdc(),mdd.getMeterVal(),mdd.getResult(),mdd.getInfo() == null ? "" : mdd.getInfo());
	    System.out.println(sql);
		mdd.setId(DBUtil.executeSql(connection, sql));
		
	}
	
	
   public void updateMeasureDot(MeasureDotDO  mdd) throws SQLException {
		
		String sql = String.format("update measure_dot_table set finalAdc = %.3f , meterVal = %.3f , result = '%s', info = '%s' where id = %d",
				     mdd.getFinalAdc(),mdd.getMeterVal(),mdd.getResult(),mdd.getInfo() == null ? "" : mdd.getInfo() , mdd.getId());
	    System.out.println(sql);
		DBUtil.executeSql(connection, sql);
		
	}
	
	
	
	
	/**
	 * 保存稳定度测试数据
	 * @author  wavy_zheng
	 * 2022年3月27日
	 * @param stable
	 * @throws SQLException
	 */
	public void saveStableDot(StableDataDO stable) throws SQLException {
		
		String sql = String.format("insert into stable_table(mode,pole,calculateDot,adc,meter,result,date, chn_id) values('%s','%s',%.3f,%.3f,%.3f,'%s','%s',%d)",
			    stable.getMode(),stable.getPole(),stable.getCalculateDot(),stable.getAdc(),stable.getMeter(),
			    stable.getResult(),CommonUtil.formatTime(stable.getDate(), "yyyy-MM-dd HH:mm:ss"),stable.getChnId());
    System.out.println(sql);
	DBUtil.executeSql(connection, sql);
		
	}
	
	/**
	 * 存储基础测试项
	 * @author  wavy_zheng
	 * 2022年3月27日
	 * @param item
	 * @throws SQLException
	 */
   public void saveTestItemDot(TestItemDataDO item) throws SQLException {
		
		String sql = String.format("insert into testItem_table(name,runMode,calMode,pole,testVal,lower,upper,state,milisecs,info, chn_id) "
				+ "values('%s','%s','%s','%s',%.3f,%.3f,%.3f,'%s',%d,'%s',%d , '%s')", item.getName().toString(),
			    item.getRunMode().name(),item.getCalMode() == null ? "" : item.getCalMode().name()
			    ,item.getPole() == null ? "" : item.getPole().name(),item.getTestVal(),
			    item.getLower(),item.getUpper(),item.getState(),item.getMilisecs(),item.getInfo(),
			    item.getChn_id() , item.getParam() == null ? "" : item.getParam().toString());
    System.out.println(sql);
	DBUtil.executeSql(connection, sql);
		
	}
	
	
	
	/**
	 * 动态创建稳定度测试数据表
	 * @author  wavy_zheng
	 * 2022年3月27日
	 * @throws SQLException
	 */
	public void createStableTable() throws SQLException {
		
		String sql = String.format("create table  IF NOT EXISTS stable_table("
				+ "ID int NOT NULL AUTO_INCREMENT,"
				+ "mode varchar(25) not null,"
				+ "pole varchar(25) ,"
				+ "calculateDot double ,"
				+ "adc double ,"
				+ "meter double,"
				+ "date TIMESTAMP,"
				+ "result varchar(25),"
				+ "chn_id integer ,"
				+ "foreign key (chn_id) references channel_table(ID) on delete cascade on update cascade,"
				+ "PRIMARY KEY (ID))");
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);
		
		//foreign key (chn_id) references channel_table(ID) on delete cascade on update cascade 
	}
	
	
	/**
	 * 动态创建常规测试数据表
	 * @author  wavy_zheng
	 * 2022年3月27日
	 * @throws SQLException
	 */
	public void createTestItemTable() throws SQLException {
		
		String sql = String.format("create table  IF NOT EXISTS testItem_table("
				+ "ID int NOT NULL AUTO_INCREMENT,"
				+ "name varchar(100) not null,"
				+ "runMode varchar(25) not null,"
				+ "calMode varchar(25) ,"
				+ "pole varchar(25) ,"
				+ "testVal double ,"
				+ "lower double ,"
				+ "upper double,"
				+ "info varchar(255),"
				+ "state varchar(25),"
				+ "milisecs int,"
				+ "param  varchar(100),"
				+ "chn_id integer ,"
				+ "index(runMode) ,"
				+ "foreign key (chn_id) references channel_table(ID) on delete cascade on update cascade,"
				+ "PRIMARY KEY (ID))");
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);
		
		//foreign key (chn_id) references channel_table(ID) on delete cascade on update cascade 
	}
	/**
	 * 更新测试项
	 * @author  wavy_zheng
	 * 2022年3月27日
	 * @param item
	 * @throws SQLException 
	 */
	public void updateTestItem(TestItemDataDO  item) throws SQLException {
		
		String sql = String.format("update testitem_table set testVal = %.3f, state = '%s' , milisecs = %d where id = %d",
				           item.getTestVal(),item.getState() , item.getMilisecs() , item.getId());
		
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);		
		
	}
	

	/**
	 * 是否测试过
	 * 
	 * @author wavy_zheng 2021年1月18日
	 * @param device
	 * @return
	 * @throws SQLException
	 */
	public boolean isDeviceTested(Device device) throws SQLException {

		String sql = "select count(*) as total from measure_dot_table m inner join channel_table c on m.chn_id = c.id inner join device_table d on c.device_id = d.id and d.id = "
				+ device.getId();
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		int count = 0;
		if (result.next()) {

			count = result.getInt("total");
		}
		result.close();
		return count > 0;
	}
	
	public void clearMeasureDots(ChannelDO channel) throws SQLException {
		
		String sql  = "delete from measure_dot_table where chn_id = " + channel.getId();
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);

	}
	
    public void clearStableDots(ChannelDO channel) throws SQLException {
		
		String sql  = "delete from stable_table where chn_id = " + channel.getId();
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);

	}
    
    public void clearTestItems(ChannelDO channel , RunMode rm) throws SQLException {
    	
    	String sql  = "delete from testItem_table where chn_id = " + channel.getId();
    	if(rm != null) {
    		
    		sql += " and runMode = '" + rm.name() + "'";
    	}
		System.out.println(sql);
		DBUtil.executeSql(connection, sql);
    }
	
	
	/**
	 * 更新整柜通道状态
	 * @author  wavy_zheng
	 * 2021年1月20日
	 * @param device
	 * @throws SQLException
	 * @throws ParseException 
	 */
	public void  refreshChannels(Device device) throws SQLException, ParseException{
		
		String sql = "select * from channel_table where device_id = " + device.getId();
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while(result.next()) {
			
			int id = result.getInt("id");
			int deviceChnIndex = result.getInt("chnIndexInDevice");
			CalState cs = CalState.valueOf(result.getString("state"));
			String testResult = result.getString("result");
			CalType calType = result.getString("calType") == null ? CalType.CAL : CalType.valueOf(result.getString("calType"));
			Date st = CommonUtil.parseTime(result.getString("startTime"), "yyyy-MM-dd HH:mm:ss");
			Date ed = CommonUtil.parseTime(result.getString("endTime"), "yyyy-MM-dd HH:mm:ss");
			int seconds = result.getInt("seconds");
			String info = result.getString("info");
			
			ChannelDO chn = device.getChannels().get(deviceChnIndex);
			if("2".equals(testResult)) {
				
			   chn.setState(CalState.CALIBRATE_PASS);
			} else if("1".equals(testResult)) {
				
			    chn.setState(calType == CalType.CAL ? CalState.CALIBRATE_FAIL : CalState.CALCULATE_FAIL);
			} else {
				
				chn.setState(cs);
			}
//			if(testResult.equals("pass")) {
//				
//			   chn.setState(calType == CalType.CAL ? CalState.CALIBRATE_PASS : CalState.CALCULATE_PASS);
//			} else if(testResult.equals("fail")) {
//				
//			    chn.setState(calType == CalType.CAL ? CalState.CALIBRATE_FAIL : CalState.CALCULATE_FAIL);
//			} else {
//				
//				chn.setState(cs);
//			}
			chn.setStartTime(st);
			chn.setEndTime(ed);
			chn.setSeconds(seconds);
			chn.setInfo(info);
			chn.setId(id);
			
			
		}
		result.close();
		
	}
	
	
	public void saveChannel(ChannelDO  channel) throws SQLException{

		String sql = "update channel_table set ";
		if(channel.getStartTime() != null) {
			
			sql += "startTime = '" + CommonUtil.formatTime(channel.getStartTime(), "yyyy-MM-dd HH:mm:ss") + "',";
		}
		if(channel.getEndTime() != null) {
			
			sql += "endTime = '" + CommonUtil.formatTime(channel.getEndTime(), "yyyy-MM-dd HH:mm:ss") + "',";
		}
		
		if(channel.getCalType() != null) {
			
			sql += "calType = '" + channel.getCalType() + "',";
		}
		
		sql += "seconds = " + channel.getSeconds() + ",";
		
		
		int result = 0;
		if(channel.getState() == CalState.CALCULATE_PASS || channel.getState() ==CalState.CALIBRATE_PASS) {
			
			result = 2;
		} else if(channel.getState() == CalState.CALCULATE_FAIL || channel.getState() ==CalState.CALIBRATE_FAIL) {
			
			result = 1;
		}
		if(result != -1) {
		    sql += "result = " + result + ",";
		}

		sql = sql.substring(0, sql.length() - 1);
		sql += " where id = " + channel.getId();
		
		System.out.println(sql);
		
		DBUtil.executeSql(connection, sql);
		
		
	}
	
	private ChannelDO  findChannelById(Device device , int chnId) {
		
		int stId = device.getChannels().get(0).getId();
		int edId = device.getChannels().get(device.getChannels().size() - 1).getId();
		if(chnId < stId || chnId > edId) {
			
			return null;
		}
		
		return device.getChannels().get(chnId - stId);
		
	}
	
	/***
	 * 获取计量点
	 * @return
	 * @throws SQLException
	 */
	public List<String> listMeasureNode(Device device) throws SQLException {
		
		//查询设备通道号
		int stId = device.getChannels().get(0).getId();
		int edId = device.getChannels().get(device.getChannels().size() - 1).getId();
		
		List<String> measureNodes = new ArrayList<>();
		
		String sql = "select CONCAT(mode,',',pole,',',calculateDot) as node from measure_dot_table where chn_id = "
				+ "(select chn_id from (select chn_id, count(1) as num from measure_dot_table "
				+ " where chn_id >= " + stId + " and chn_id <=" + edId
				+ " GROUP BY chn_id ORDER BY num desc limit 1)a ) order by id ";
		
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while(result.next()) {
			
			measureNodes.add(result.getString("node"));
		}
		
		result.close();
		
		return measureNodes;
	}
	
	public List<MeasureDotDO> listMeasureDots(Device device) throws SQLException {
		return listMeasureDots(device, null);
	}
	
	public List<MeasureDotDO> listMeasureDots(Device device, Map<String, Object> condition) throws SQLException {
		
		
		//查询设备通道号
		int stId = device.getChannels().get(0).getId();
		int edId = device.getChannels().get(device.getChannels().size() - 1).getId();
		System.out.println("CHN ST:" + stId + ",CHN ED:" + edId);
		
		List<MeasureDotDO> list = new ArrayList<>();
		String sql = "select * from measure_dot_table where chn_id >= " + stId + " and chn_id <=" + edId + " ";
		
		if(condition != null) {
			
			String mode = (String) condition.get("mode");
			if(!CommonUtil.isNullOrEmpty(mode)) {
				sql += String.format(" and mode = '%s' ", mode);
			}
			String pole = (String)condition.get("pole");
			if(!CommonUtil.isNullOrEmpty(pole)) {
				sql += String.format(" and pole = '%s' ",pole);
			}
			double calculateDot = (double)condition.get("calculateDot");
			sql += " and calculateDot = " + calculateDot;
		}
		
		sql += " order by chn_id";
		System.out.println(sql);
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		
		
		
		while(result.next()) {
			
			ChannelDO channel = findChannelById(device, result.getInt("chn_id"));
			
			MeasureDotDO dot = new MeasureDotDO();
			dot.setId(result.getInt("id"));
			dot.setCalculateDot(result.getDouble("calculateDot"));
			dot.setChannel(channel);
			dot.setFinalAdc(result.getDouble("finalAdc"));
			dot.setIndex(list.size());
			dot.setInfo(result.getString("info"));
			dot.setMeterVal(result.getDouble("meterVal"));
			dot.setMode(result.getString("mode"));
			dot.setPole(result.getString("pole"));
			dot.setResult(result.getString("result"));
			
			
			list.add(dot);
		}
		result.close();
		
		return list;
		
	}
	
	public List<TestItemDataDO>  listTestItems(ChannelDO channel , RunMode rm) throws SQLException {
		
		List<TestItemDataDO> list = new ArrayList<>();
		String sql = "select * from testitem_table where chn_id = " + channel.getId();
		if(rm != null) {
			
			sql += " and runMode = '" + rm.name() + "'";
		}
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while(result.next()) {
			
			TestItemDataDO item = new TestItemDataDO();
			item.setId(result.getInt("id"));
			item.setName(result.getString("name") != null ? TestName.parse(result.getString("name")) : null);
			item.setRunMode(RunMode.valueOf(result.getString("runMode")));
			item.setCalMode(CommonUtil.isNullOrEmpty(result.getString("calMode")) ? null : CalMode.valueOf(result.getString("calMode")));
			item.setPole(CommonUtil.isNullOrEmpty(result.getString("pole")) ? null : Pole.valueOf(result.getString("pole")));
			item.setTestVal(result.getDouble("testVal"));
			item.setLower(result.getDouble("lower"));
			item.setUpper(result.getDouble("upper"));
			item.setMilisecs(result.getLong("milisecs"));
			item.setState(result.getString("state"));
			item.setInfo(result.getString("info"));
			item.setChn_id(result.getInt("chn_id"));
			item.setParam(result.getObject("param"));
			list.add(item);
			
		}
		result.close();
		
		
		return list;
		
	}
	
	
	public List<StableDataDO> listStableDatas(ChannelDO channel) throws SQLException {
		
		List<StableDataDO> list = new ArrayList<>();
		
		String sql = "select * from stable_table where chn_id = " + channel.getId();
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while(result.next()) {
			
			StableDataDO stable = new StableDataDO();
			stable.setId(result.getInt("id"));
			stable.setMode(result.getString("mode"));
			stable.setPole(result.getString("pole"));
			stable.setCalculateDot(result.getDouble("calculateDot"));
			stable.setAdc(result.getDouble("adc"));
			stable.setMeter(result.getDouble("meter"));
			stable.setResult(result.getString("result"));
			stable.setChnId(result.getInt("chn_id"));
			try {
				stable.setDate(CommonUtil.parseTime(result.getString("date"),"yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(stable);
			
		}
		result.close();
		
		
		return list;
	}
	
	
	public List<MeasureDotDO> listMeasureDots(ChannelDO channel) throws SQLException {
		
		List<MeasureDotDO> list = new ArrayList<>();
		String sql = "select * from measure_dot_table where chn_id = " + channel.getId();
		ResultSet result = DBUtil.executeQuerySql(connection, sql);
		while(result.next()) {
			
			MeasureDotDO dot = new MeasureDotDO();
			dot.setId(result.getInt("id"));
			dot.setCalculateDot(result.getDouble("calculateDot"));
			dot.setChannel(channel);
			dot.setFinalAdc(result.getDouble("finalAdc"));
			dot.setIndex(list.size());
			dot.setInfo(result.getString("info"));
			dot.setMeterVal(result.getDouble("meterVal"));
			dot.setMode(result.getString("mode"));
			dot.setPole(result.getString("pole"));
			dot.setResult(result.getString("result"));
			
			
			list.add(dot);
		}
		result.close();
		
		return list;
		
	}
	

	/**
	 * 创建一个空白设备
	 * 
	 * @author wavy_zheng 2021年1月13日
	 * @param name
	 * @param ip
	 * @param logicCount
	 * @param driverCount
	 * @param chnCountInDriver
	 * @throws SQLException
	 */
	public Device createDevice(String name, String ip, DeviceType type, int logicCount, int driverCount,
			int chnCountInDriver) throws SQLException {

		String sql = String.format(
				"insert into device_table(name,ip,type , logicCount,logicDriverCount,driverChnCount) values('%s','%s','%s',%d,%d,%d)",
				name, ip, type.name(), logicCount, driverCount, chnCountInDriver);
		connection.setAutoCommit(false);
		int id = DBUtil.executeSql(connection, sql);
		Device device = new Device();
		device.setName(name);
		device.setId(id);
		device.setIp(ip);
		device.setType(type);
		device.setChnNumInDriver(chnCountInDriver);
		device.setDriverNumInLogic(driverCount);
		device.setLogicNum(logicCount);

		// 插入通道
		StringBuffer buff = new StringBuffer("insert into channel_table(chnIndexInDevice,device_id,state) values");
		int total = getTotalCount(logicCount, driverCount, chnCountInDriver);
		for (int n = 0; n < total; n++) {

			buff.append("(" + n + "," + id + ",'" + CalState.NONE.name() + "')");
			if (n < total - 1) {
				buff.append(",");
			}

		}
		int insertId = DBUtil.executeSql(connection, buff.toString());
		// 创建channel
		for (int n = 0; n < total; n++) {

			ChannelDO channel = new ChannelDO();
			channel.setChnIndex(n);
			channel.setDevice(device);
			channel.setId(insertId + n);

			device.appendChannel(channel);

		}

		System.out.println("insertId:" + insertId);
		connection.commit();
		connection.setAutoCommit(true);

		return device;
	}

	public List<Device> listAllDevices() throws Exception {

		return findDeviceByName(null);

	}

	public static void main(String[] args) {
		Database2Manager databaseManager = new Database2Manager();
		try {
			databaseManager.connect();

			// Device device = databaseManager.findDeviceByName("d2");
			// System.out.println(device);

			// Device device = databaseManager.createDevice("d2", "192.168.1.110", 4, 8,
			// 16);

			// CalBox box = new CalBox();
			// box.setCalBoardCount(2);
			// box.setIp("192.168.1.22");
			// box.setName("box1");
			// box.setMeterIps(Arrays.asList(new String[] {"891.12.22.23"}));
			//
			// databaseManager.createCalbox(box);
			//
			// System.out.println("create box ok");

			/*
			 * Device device = new Device(); device.setId(1); device.setDriverChnCount(8);
			 * device.setLogicCount(2); device.setIp("127.0.0.1"); device.setName("431");
			 * databaseManager.addDevice(device);
			 * 
			 * databaseManager.removeDevice(1); Device device2 =
			 * databaseManager.findDeviceBy(3); System.out.println(device2.toString());
			 * 
			 */

			/*
			 * Box box = new Box(); box.setDeviceId(2); box.setCalCount(2);
			 * box.setMac("0:0:0:0"); box.setMeterCount(3); box.setIp("127.0.0.2");
			 * box.setMeterIp("127.0.0.5"); box.setName("烤箱2"); box.setId(3);
			 * databaseManager.updateBox(box);
			 */
			/* databaseManager.addBox(box); */
			// databaseManager.removeBox(2);
			// List<Box> boxs = databaseManager.findBoxBy(new Box());
			/*
			 * boxs.stream().forEach(e->{ System.out.println(e.toString()); });
			 */

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
