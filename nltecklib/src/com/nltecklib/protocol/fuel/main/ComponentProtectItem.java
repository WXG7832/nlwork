package com.nltecklib.protocol.fuel.main;

import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * 器件上下限保护
 * 
 * @author guofang_ma
 *
 */
public class ComponentProtectItem {

    public Component component = Component.NONE;

    public double alertMax;
    public double alertMin;
    public double stopMax;
    public double stopMin;

    public Component getComponent() {
	return component;
    }

    public void setComponent(Component component) {
	this.component = component;
    }

    public double getAlertMax() {
	return alertMax;
    }

    public void setAlertMax(double alertMax) {
	this.alertMax = alertMax;
    }

    public double getAlertMin() {
	return alertMin;
    }

    public void setAlertMin(double alertMin) {
	this.alertMin = alertMin;
    }

    public double getStopMax() {
	return stopMax;
    }

    public void setStopMax(double stopMax) {
	this.stopMax = stopMax;
    }

    public double getStopMin() {
	return stopMin;
    }

    public void setStopMin(double stopMin) {
	this.stopMin = stopMin;
    }

    public ComponentProtectItem(Component component, double alertMin, double alertMax, double stopMin, double stopMax) {
	this.component = component;
	this.alertMin = alertMin;
	this.alertMax = alertMax;
	this.stopMin = stopMin;
	this.stopMax = stopMax;
    }

    public ComponentProtectItem() {

    }

    @Override
    public String toString() {
	return "ComponentProtectItem [component=" + component + ", alertMax=" + alertMax + ", alertMin=" + alertMin + ", stopMax=" + stopMax + ", stopMin=" + stopMin + "]";
    }

}
