package com.nltecklib.protocol.fuel.main;

import com.nltecklib.protocol.fuel.Environment.ComponentState;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

/**
 * ¿ª¹ØÆ÷¼þ
 * 
 * @author caichao_tang
 *
 */
public class KeyAlert {

	public Component component = Component.NONE;
	public ComponentState state = ComponentState.NORMAL;

	public KeyAlert(Component component, ComponentState state) {
		this.component = component;
		this.state = state;
	}

	public KeyAlert() {
	}

	@Deprecated
	public ComponentState getState() {
		return state;
	}

	@Deprecated
	public void setState(ComponentState state) {
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
		return "KeyAlert [component=" + component.name() + ", state=" + state + "]";
	}

}
