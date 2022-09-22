package com.nlteck.service.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import com.nlteck.model.TestDot;

public class TestDotDao {
    private static final String MAPPER = "com.nlteck.model.mapper.TestDotMapper";
    private SqlSessionTemplate sqlSessionTemplate;

    public int insert(TestDot testDot) {
	return sqlSessionTemplate.insert(MAPPER + ".insert", testDot);
    }

    public int delete(Integer testId, Integer unitIndex, Integer chnIndex) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testId", testId);
	map.put("unitIndex", unitIndex);
	map.put("chnIndex", chnIndex);
	return sqlSessionTemplate.delete(MAPPER + ".delete", map);
    }

    public List<TestDot> select(Integer testId, Integer unitIndex, Integer chnIndex) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testId", testId);
	map.put("unitIndex", unitIndex);
	map.put("chnIndex", chnIndex);
	return sqlSessionTemplate.selectList(MAPPER + ".select", map);
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
	return sqlSessionTemplate;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
	this.sqlSessionTemplate = sqlSessionTemplate;
    }

}
