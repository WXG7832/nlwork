package com.nlteck.service.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestLog;

public class TestLogDao {
    private static final String MAPPER = "com.nlteck.model.mapper.TestLogMapper";
    private SqlSessionTemplate sqlSessionTemplate;

    public int insert(TestLog testLog) {
	return sqlSessionTemplate.insert(MAPPER + ".insert", testLog);
    }

    public int delete(Integer testId, String level) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testId", testId);
	map.put("level", level);
	return sqlSessionTemplate.delete(MAPPER + ".delete", map);
    }

    public List<TestDot> select(Integer testId, String level) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testId", testId);
	map.put("level", level);
	return sqlSessionTemplate.selectList(MAPPER + ".select", map);
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
	return sqlSessionTemplate;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
	this.sqlSessionTemplate = sqlSessionTemplate;
    }

}
