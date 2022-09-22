package com.nlteck.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 数据库mybatis工具
 * 
 * @author Administrator
 *
 */
public class MybatisUtil {

	private static SqlSessionFactory sqlSessionFactory;

	public static SqlSession getSqlSession() {
		try {
			
			if (sqlSessionFactory == null) {
                 
				InputStream inputStream = Resources.getResourceAsStream("mybatis.xml");
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream); // build()方法会把inputStream关闭掉
				//inputStream.close();
			} 
			return sqlSessionFactory.openSession();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	

}
