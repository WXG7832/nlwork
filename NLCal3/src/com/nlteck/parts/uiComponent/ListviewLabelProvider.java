package com.nlteck.parts.uiComponent;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.nlteck.firmware.CalBoard;
import com.nlteck.resources.Resources;

public class ListviewLabelProvider implements ILabelProvider{

	@Override
	public void addListener(ILabelProviderListener listener) {
		
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getImage(Object element) {
		
		if(element instanceof CalBoard) {
			
			return showCalboardImage((CalBoard)element);
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		
		if(element instanceof CalBoard) {
			
			return showCalboardText((CalBoard)element);
		}
		return null;
	}
	
	private String showCalboardText(CalBoard cb) {
		
		  return "Ð£×¼°å" + (cb.getIndex() + 1);
	}
	
	private Image showCalboardImage(CalBoard cb) {
		
		  return Resources.CAL_BOARD_IMAGE;
	}

}
