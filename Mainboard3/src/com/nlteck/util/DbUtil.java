package com.nlteck.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.nlteck.AlertException;
import com.nltecklib.protocol.li.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.li.main.MainEnvironment.ChannelData;
import com.nltecklib.protocol.li.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkMode;

public class DbUtil {

	/**
	 * <property name="driverClassName" value="org.h2.Driver" />
	 * <property name="url" value="jdbc:h2:./db/formation" />
	 * <property name="username" value="sa" />
	 * <property name="password" value="412412" />
	 * 
	 */

	private static final String driverClassName = "org.h2.Driver";
	private static final String URL = "jdbc:h2:./db/offline";
	private static final String USER_NAME = "root";
	private static final String USER_PWD = "root";
	private static Connection conn;

	static {

		try {
			Class.forName(driverClassName);
			conn = DriverManager.getConnection(URL, USER_NAME, USER_PWD);
			System.out.println("connected database success!");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * 取出所有的离线缓存数据
	 * @return
	 */
	public static List<ChannelData> fetchAllOfflineData(){
		
		PreparedStatement stmt = null;
		List<ChannelData> list = new ArrayList<ChannelData>();
		try {
			stmt = conn.prepareStatement("select * from OFFLINE_DATA");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				
				ChannelData chnData = new ChannelData();
				chnData.setUnitIndex(rs.getInt("logicIndex"));
				chnData.setChannelIndex(rs.getInt("channelIndex"));
				chnData.setVoltage(rs.getDouble("voltage"));
				chnData.setCurrent(rs.getDouble("current"));
				chnData.setCapacity(rs.getDouble("capacity"));
				chnData.setStepIndex(rs.getInt("stepIndex"));
				chnData.setLoopIndex(rs.getInt("loopIndex"));
				chnData.setState(ChnState.valueOf(rs.getString("state")));
				chnData.setTimeStepSpend(rs.getInt("timeStepSpend"));
				chnData.setTimeTotalSpend(rs.getInt("timeTotalSpend"));
				chnData.setTemp(rs.getDouble("temp"));
				chnData.setAccumulateCapacity(rs.getDouble("accumulateCapacity"));
				chnData.setAccumulateEnergy(rs.getDouble("accumulateEnergy"));
				try {
					chnData.setDate(CommonUtil.parseTime(rs.getString("date"), "yyyy-MM-dd HH:mm:ss"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chnData.setWorkMode(WorkMode.valueOf(rs.getString("workMode")));
				chnData.setEnergy(rs.getDouble("energy"));
				list.add(chnData);
			}
			rs.close();
			
			Statement sm = conn.createStatement();
			sm.execute("delete from OFFLINE_DATA");
			sm.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
			
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
		
	}
	
	public static void saveOfflineData(List<ChannelData> chnDatas) {
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("insert into OFFLINE_DATA(logicIndex,channelIndex,current,voltage,capacity,stepIndex,loopIndex,state,timeStepSpend,timeTotalSpend,temp,date,workMode,energy,accumulateCapacity,accumulateEnergy)" +
			                      " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			conn.setAutoCommit(false);
			for(ChannelData chnData : chnDatas) {
				 int colIndex = 1;
				 stmt.setInt(colIndex++, chnData.getUnitIndex());
			     stmt.setInt(colIndex++, chnData.getChannelIndex());
			     stmt.setDouble(colIndex++, chnData.getCurrent());
			     stmt.setDouble(colIndex++, chnData.getVoltage());
			     stmt.setDouble(colIndex++, chnData.getCapacity());
			     stmt.setInt(colIndex++, chnData.getStepIndex());
			     stmt.setInt(colIndex++, chnData.getLoopIndex());
			     stmt.setString(colIndex++, chnData.getState().name());
			     stmt.setInt(colIndex++, (int)chnData.getTimeStepSpend());
			     stmt.setInt(colIndex++, (int)chnData.getTimeTotalSpend());
			     stmt.setDouble(colIndex++, chnData.getTemp());
			     stmt.setString(colIndex++, CommonUtil.formatTime(chnData.getDate(),"yyyy-MM-dd HH:mm:ss"));
			     stmt.setString(colIndex++, chnData.getWorkMode().name());
			     stmt.setDouble(colIndex++, chnData.getEnergy());
			     stmt.setDouble(colIndex++, chnData.getAccumulateCapacity());
			     stmt.setDouble(colIndex++, chnData.getAccumulateEnergy());
			     
			     stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();//2,进行手动提交（commit）  
			conn.setAutoCommit(true);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static void initDatabase() throws AlertException {

		Statement stmt = null;

		try {
			stmt = conn.createStatement();

			String sql = "CREATE TABLE IF NOT EXISTS OFFLINE_DATA (" + 
					"       ID BIGINT NOT NULL IDENTITY,\r\n" + 
					"		LOGICINDEX INTEGER NOT NULL,\r\n" + 
					"		CHANNELINDEX INTEGER NOT NULL,\r\n" + 
					"		VOLTAGE DOUBLE,\r\n" + 
					"		CURRENT DOUBLE,\r\n" + 
					"		CAPACITY DOUBLE,\r\n" + 
					"		ENERGY   DOUBLE,\r\n" +
					"		ACCUMULATECAPACITY DOUBLE,\r\n" + 
					"		ACCUMULATEENERGY   DOUBLE,\r\n" +
					"		DEVICEVOLTAGE DOUBLE,\r\n" + 
					"		POWERVOLTAGE DOUBLE,\r\n" + 
					"		STEPINDEX INTEGER,\r\n" + 
					"		LOOPINDEX INTEGER,\r\n" + 
					"		TIMESTEPSPEND INTEGER,\r\n" + 
					"		TIMETOTALSPEND INTEGER,\r\n" + 
					"		STATE VARCHAR(25) NOT NULL,\r\n" + 
					"		TEMP DOUBLE,\r\n" + 
					"		BARCODE VARCHAR(50),\r\n" + 
					"		DATE    TIMESTAMP,\r\n" + 
					"		WORKMODE VARCHAR(8),\r\n" + 
					"		PRESSURE DOUBLE,\r\n" + 
					"		BOARDTEMP DOUBLE,\r\n" + 
					"		PRIMARY KEY (ID) " + 
					"       )";

			stmt.executeUpdate(sql);
			
			System.out.println("init database success");
			
		} catch (SQLException e) {
             
			throw new AlertException(AlertCode.LOGIC,"初始化数据库发生错误!");
		} 
		
		
		
		

	}

}
