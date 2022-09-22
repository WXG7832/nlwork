package com.nlteck.service;

import com.nlteck.service.dao.CalDataDao;
import com.nlteck.service.dao.TestDataDao;
import com.nlteck.service.dao.TestDotDao;
import com.nlteck.service.dao.TestLogDao;

/**
 * 数据管理器
 * 
 * @author Administrator
 *
 */
public class DatabaseManager {
    private CalDataDao calDataDao;
    private TestDataDao testDataDao;
    private TestDotDao testDotDao;
    private TestLogDao testLogDao;

    public TestLogDao getTestLogDao() {
	return testLogDao;
    }

    public void setTestLogDao(TestLogDao testLogDao) {
	this.testLogDao = testLogDao;
    }

    public TestDotDao getTestDotDao() {
	return testDotDao;
    }

    public void setTestDotDao(TestDotDao testDotDao) {
	this.testDotDao = testDotDao;
    }

    public TestDataDao getTestDataDao() {
	return testDataDao;
    }

    public void setTestDataDao(TestDataDao testDataDao) {
	this.testDataDao = testDataDao;
    }

    public CalDataDao getCalDataDao() {
	return calDataDao;
    }

    public void setCalDataDao(CalDataDao calDataDao) {
	this.calDataDao = calDataDao;
    }

}
