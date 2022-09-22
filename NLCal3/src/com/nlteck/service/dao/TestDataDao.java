package com.nlteck.service.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.nlteck.model.TestData;

public class TestDataDao {
    private static final String MAPPER = "com.nlteck.model.mapper.TestDataMapper";
    private SqlSessionTemplate sqlSessionTemplate;

    public int insert(TestData testData) {
	return sqlSessionTemplate.insert(MAPPER + ".insert", testData);
    }

    public int delete(Integer id, String testName) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("id", id);
	map.put("testName", testName);
	return sqlSessionTemplate.delete(MAPPER + ".delete", map);
    }

    public List<TestData> select(String testName) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testName", testName);
	return sqlSessionTemplate.selectList(MAPPER + ".select", map);
    }

    public List<TestData> update(String testName, Date endTime) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testName", testName);
	map.put("endTime", endTime);
	return sqlSessionTemplate.selectList(MAPPER + ".update", map);
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
	return sqlSessionTemplate;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
	this.sqlSessionTemplate = sqlSessionTemplate;
    }

}
