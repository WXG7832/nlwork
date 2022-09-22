 package com.nltecklib.protocol.fuel.main;

import java.util.Date;

import com.nltecklib.protocol.fuel.main.MainEnvironment.AlertCode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.AlertLevel;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * 主控报警协议数据类中的报警信息
 * 
 * @author caichao_tang
 *
 */
public class AlertContent {
    private Component component;
    private Date alertDate;
    private AlertCode alertCode;
    private AlertLevel alertLevel;
    private String info;

    public AlertLevel getAlertLevel() {
	return alertLevel;
    }

    public void setAlertLevel(AlertLevel alertLevel) {
	this.alertLevel = alertLevel;
    }

    public Component getComponent() {
	return component;
    }

    public void setComponent(Component component) {
	this.component = component;
    }

    public Date getAlertDate() {
	return alertDate;
    }

    public void setAlertDate(Date alertDate) {
	this.alertDate = alertDate;
    }

    public AlertCode getAlertCode() {
	return alertCode;
    }

    public void setAlertCode(AlertCode alertCode) {
	this.alertCode = alertCode;
    }

    public String getInfo() {
	return info;
    }

    public void setInfo(String info) {
	this.info = info;
    }

    @Override
    public String toString() {
	return "AlertContent [component=" + component + ", alertDate=" + alertDate + ", alertCode=" + alertCode + ", alertLevel=" + alertLevel + ", info=" + info + "]";
    }

}
