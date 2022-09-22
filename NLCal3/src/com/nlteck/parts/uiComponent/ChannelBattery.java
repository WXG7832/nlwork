package com.nlteck.parts.uiComponent;

import org.eclipse.swt.graphics.Rectangle;

/**
 * 封装的通道电池对象
 * 
 * @author caichao_tang
 *
 */
public class ChannelBattery {
    private Rectangle rectangle;

    public ChannelBattery() {
	super();
    }

    public ChannelBattery(Rectangle rectangle) {
	super();
	this.rectangle = rectangle;
    }

    public Rectangle getRectangle() {
	return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
	this.rectangle = rectangle;
    }

}
