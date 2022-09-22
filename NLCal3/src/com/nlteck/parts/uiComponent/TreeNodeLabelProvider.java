package com.nlteck.parts.uiComponent;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.MultiMeter;
import com.nlteck.firmware.WorkBench.DeviceType;
import com.nlteck.resources.Resources;

public class TreeNodeLabelProvider implements ILabelProvider, IColorProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getImage(Object element) {
		TreeNode node = (TreeNode) element;
		if (node.getContent() instanceof Device) {
			
			Device device = (Device)node.getContent();
			if(device.getType() == DeviceType.CAPACITY) {
				
				return Resources.DEVICE_IMAGE;
			} else if(device.getType() == DeviceType.POWERBOX) {
				
				return Resources.POWERBOX_IMAGE;
			}
			
			
		} else if (node.getContent() instanceof LogicBoard) {
			return Resources.LOGIC_BOARD_IMAGE;
		} else if (node.getContent() instanceof DriverBoard) {
			return Resources.DRIVER_BOARD_IMAGE;
		} else if (node.getContent() instanceof MultiMeter) {
			return Resources.MULTI_METER_IMAGE;
		} else if (node.getContent() instanceof CalBoard) {
			return Resources.CAL_BOARD_IMAGE;
		} else if (node.getContent() instanceof CalBox) {
			return Resources.CAL_BOX_IMAGE;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		TreeNode node = (TreeNode) element;
		
		if (node.getTitle() != null && !node.getTitle().isEmpty()) {
			return node.getTitle();
		}
		
		return "";
	      
		
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

}
