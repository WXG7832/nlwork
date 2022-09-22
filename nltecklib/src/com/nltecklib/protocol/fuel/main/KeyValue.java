package com.nltecklib.protocol.fuel.main;

import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * ÖµÆ÷¼þ
 * 
 * @author caichao_tang
 *
 */
public class KeyValue {

	public Component component = Component.NONE;
	public double value;

	public KeyValue() {
	}

	public KeyValue(Component component, double value) {
		this.component = component;
		this.value = value;
	}

	@Deprecated
	public Component getComponent() {
		return component;
	}

	@Deprecated
	public void setComponent(Component component) {
		this.component = component;
	}

	@Deprecated
	public double getValue() {
		return value;
	}

	@Deprecated
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "KeyValue [component=" + component.name() + ", value=" + value + "]";
	}

}
