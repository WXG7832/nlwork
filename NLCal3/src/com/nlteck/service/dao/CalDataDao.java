package com.nlteck.service.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mybatis.spring.SqlSessionTemplate;
import com.nlteck.model.CalData;

public class CalDataDao {
    private static final String MAPPER = "com.nlteck.model.mapper.CalDataMapper";
    private SqlSessionTemplate sqlSessionTemplate;

    public int insert(CalData calData) {
	return sqlSessionTemplate.insert(MAPPER + ".insert", calData);
    }

    public int delete(Integer testId, Integer logicIndex, Integer driverIndex, Integer channelIndex) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testId", testId);
	map.put("logicIndex", logicIndex);
	map.put("driverIndex", driverIndex);
	map.put("channelIndex", channelIndex);
	return sqlSessionTemplate.delete(MAPPER + ".delete", map);
    }

    public List<CalData> select(Integer testId, Integer logicIndex, Integer driverIndex, Integer channelIndex) {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("testId", testId);
	map.put("logicIndex", logicIndex);
	map.put("driverIndex", driverIndex);
	map.put("channelIndex", channelIndex);
	return sqlSessionTemplate.selectList(MAPPER + ".select", map);
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
	return sqlSessionTemplate;
    }

    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
	this.sqlSessionTemplate = sqlSessionTemplate;
    }

}
