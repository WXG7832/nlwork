package com.nltecklib.protocol.fuel;

import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;

@Deprecated
public abstract class ComponentData extends Data implements ComponentSupportable{

	protected Component  componentCode;

	public Component getComponent() {
		return componentCode;
	}

	public void setComponent(Component componentCode) {
		this.componentCode = componentCode;
	}
	
	

}
