package com.nlteck.utils;
/**
* @author  wavy_zheng
* @version 创建时间：2020年1月20日 上午9:03:41
* 类说明
*/

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DBUtil {

	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String URL = "jdbc:mysql://127.0.0.1:3306/cal2?useUnicode=true&characterEncoding=utf-8&connectTimeout=6000&socketTimeout=6000";
	public static final String USER = "nlteck";
	public static final String PASSWORD = "www.nlteck.com";
	
	
	public static void closeDatabaseConnection(Connection connection) throws SQLException {
		
		connection.close();
	}
	
	/**
	 * 返回插入或更新的ID号
	 * @author  wavy_zheng
	 * 2020年12月23日
	 * @param connection
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static int executeSql(Connection connection , String sql) throws SQLException {
		
		// 3.操作数据库，实现增删改查
		Statement stmt = connection.createStatement();
		//PreparedStatement pstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		stmt.setQueryTimeout(5);
		stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		ResultSet set = stmt.getGeneratedKeys();
		if(set.next()) {
			
			return set.getInt(1);
		}
		stmt.close();
		return -1;
	}
	

	
	//添加的方法
	//String sql = "insert into Student(cid, sname, sex) values(?,?,?)";
	public static void  add(Connection connection , String sql,Object ... params) throws SQLException{
		Connection con = null;
		PreparedStatement ps= null;   //  预编译对象
		
		try {
		    con = connection;
		    ps = con.prepareStatement(sql);
		    //    设置占位符对应的参数
		    for(int num = 1;num <= params.length;num++) {
		    	Object object = params[num-1];
		    	if(object instanceof Integer) {
		    		ps.setInt(num, (int)object);
		    	}else if(object instanceof String){
		    		ps.setString(num, (String)object);
		    	}else if(object instanceof Double){
		    		ps.setDouble(num, (Double)object);
		    	}else {
		    		//System.out.println("===================匹配不到类型==========");
		    		ps.setString(num, (String)object);
		    	}
		    	
		    }
		    ps.executeUpdate();
		}finally {
			ps.close();
			con.close();
		}
	}

	
    public static ResultSet executeQuerySql(Connection connection , String sql) throws SQLException {
		
		// 3.操作数据库，实现增删改查
		Statement stmt = connection.createStatement();
		stmt.setQueryTimeout(5);
		ResultSet result = stmt.executeQuery(sql);
		return result;
	}
	

	/**
	 * 获取公司数据库的链接
	 * 
	 * @author wavy_zheng 2020年1月20日
	 * @return
	 * @throws Exception
	 */
	public static Connection openDatabaseConnection(String url , String user , String password) throws Exception {

		// 2. 获得数据库连接
//		Connection conn = null;
//		Statement stmt = null;
//		try {
            
		    Class.forName(DRIVER);
			return DriverManager.getConnection(url, user, password);

			// 3.操作数据库，实现增删改查
//			stmt = conn.createStatement();
//
//			String sql = "";
//			sql = "CREATE TABLE IF NOT EXISTS log_table(" + "  id int(11) NOT NULL AUTO_INCREMENT,"
//					+ "  eventType int(11) DEFAULT NOT NULL," + "  eventText varchar(255) DEFAULT NULL,"
//					+ "  eventDate timestamp DEFAULT NULL," + "  PRIMARY KEY (id))";
//			System.out.println(sql);
//
//			stmt.execute(sql);
//
//			return conn;
//
//		} finally {
//
//			stmt.close();
//
//		}

	}

//	/**
//	 * 用于测试；插入HK数据库日志
//	 * 
//	 * @author wavy_zheng 2020年1月20日
//	 * @param list
//	 * @throws Exception
//	 * @throws ClassNotFoundException
//	 */
//	public static void insertHKLogs(File dbFile, List<HKEvent> list) throws Exception {
//
//		Connection conn = getHKDatabaseConnection(dbFile);
//		try {
//			String tableName = dbFile.getName();
//			tableName = tableName.split("\\.")[0];
//			StringBuffer sql = new StringBuffer("insert into " + tableName + "(eventType,event,OccurTime) values");
//			for (int n = 0; n < list.size(); n++) {
//
//				HKEvent event = list.get(n);
//
//				sql.append("(" + event.getEventType() + ",'" + event.getEventText() + "','"
//						+ CommonUtil.formatTime(event.getDate(), "yyyy-MM-dd HH:mm:ss") + "')");
//				if (n < list.size() - 1) {
//
//					sql.append(",");
//				}
//			}
//			Statement sm = conn.createStatement();
//
//			sm.execute(sql.toString());
//
//		} finally {
//
//			conn.close();
//		}
//
//	}

	
	/**
	 * 查询日期
	 * 
	 * @author wavy_zheng 2020年1月20日
	 * @param page
	 *            第几页,0表示第1页
	 * @param pageSize
	 *            分页内容条数
	 * @param eventType
	 *            -1查询任意类型的
	 * @param st
	 *            查询起始日期 ,null表示不限制
	 * @param ed
	 *            查询结束日期 ,null表示不限制
	 * @return
	 * @throws Exception
	 */
//	public static List<HKEvent> queryPageLogs(int page, int pageSize, int eventType, Date st, Date ed)
//			throws Exception {
//
//		Connection conn = getH2DatabaseConnection();
//		try {
//			List<HKEvent> list = new ArrayList<HKEvent>();
//
//			Statement sm = conn.createStatement();
//			StringBuffer sql = new StringBuffer("select * from log_table where 0 = 0 ");
//			if (eventType != -1) {
//
//				sql.append("and eventType = " + eventType);
//			}
//			if (st != null) {
//
//				sql.append(" and eventDate >='" + CommonUtil.formatTime(st, "yyyy-MM-dd HH:mm:ss") + "'");
//
//			}
//			if (ed != null) {
//
//				sql.append(" and eventDate <='" + CommonUtil.formatTime(ed, "yyyy-MM-dd HH:mm:ss") + "'");
//
//			}
//			sql.append(" limit " + page * pageSize + "," + pageSize);
//			ResultSet set = sm.executeQuery(sql.toString());
//			int index = 0 ;
//			while (set.next()) {
//
//				HKEvent event = new HKEvent();
//
//				event.setEventType(set.getInt("eventType"));
//				event.setEventText(set.getString("eventText"));
//				event.setDate(set.getTimestamp("eventDate"));
//				event.setId(set.getInt("id"));
//				event.setIndex(page * pageSize + index++);
//				list.add(event);
//
//			}
//			sm.close();
//
//			return list;
//		} finally {
//
//			conn.close();
//		}
//	}

}
