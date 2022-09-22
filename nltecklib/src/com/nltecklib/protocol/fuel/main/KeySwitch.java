package com.nltecklib.protocol.fuel.main;

import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * ¿ª¹ØÆ÷¼þ
 * 
 * @author caichao_tang
 *
 */
public class KeySwitch {

	public Component component = Component.NONE;
	public boolean state;

	public KeySwitch(Component component, boolean state) {
		this.component = component;
		this.state = state;
	}

	public KeySwitch() {
	}

	@Deprecated
	public boolean isState() {
		return state;
	}

	@Deprecated
	public void setState(boolean state) {
		this.state = state;
	}

	@Deprecated
	public Component getComponent() {
		return component;
	}

	@Deprecated
	public void setComponent(Component component) {
		this.component = component;
	}

	@Override
	public String toString() {
		return "KeySwitch [component=" + component.name() + ", state=" + state + "]";
	}

}
