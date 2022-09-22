package com.nlteck.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.nltecklib.protocol.li.Data;

public class BaseConfigPart {

	public static final String ID = "nlcal.partdescriptor.baseConfig";
	@Inject
	public BaseConfigPart() {
		
	}
	@PostConstruct
	public void postConstruct(Composite parent) {
		Composite composite=new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2,true));
		
		
		FontData fontData=new FontData("aria",18,SWT.NORMAL);
		Font font=new Font(Display.getDefault(), fontData);
		
		Group group1=creatBaseGroup(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).span(2, 1).grab(true, false).applyTo(group1);
		
		Group group2=creatProtocolGroup(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).span(2, 1).grab(true, false).applyTo(group2);
		
		
		
	}
	
	private Group creatProtocolGroup(Composite composite, int style) {
		Group group=new Group(composite, style);
		group.setLayout(new GridLayout(8, false));
		Button button1=new Button(group, SWT.CHECK);
		button1.setText("通道反序");
		
		return group;
		
	}
	public Group creatBaseGroup(Composite composite, int style) {
		Group group=new Group(composite, style);
		group.setLayout(new GridLayout(8, false));
		group.setText("基础配置");
		Button button1=new Button(group, SWT.CHECK);
		button1.setText("启用回检板");
		button1.setSelection(true);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(button1);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(20, SWT.DEFAULT).applyTo(button1);
		
		
		Button button2=new Button(group, SWT.CHECK);
		button2.setText("忽略cv2");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(20, SWT.DEFAULT).applyTo(button2);
		
		Label label1=new Label(group, SWT.NONE);
		label1.setText("停止模式:");
		label1.setToolTipText("报错后停止模式");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(20, SWT.DEFAULT).applyTo(label1);
		Combo combo1=new Combo(group, SWT.NONE);
		combo1.setItems(new String[] {"关闭通道","停在报错状态"});
		combo1.select(0);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(combo1);
		
		Button button3=new Button(group, SWT.CHECK);
		button3.setText("每个计量点关闭使能");
		button3.setSelection(true);
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(20, SWT.DEFAULT).applyTo(button3);
		
		Button button4=new Button(group, SWT.CHECK);
		button4.setText("复测");
		GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).indent(20, SWT.DEFAULT).applyTo(button4);
		
		return group;
		
	}
	
}
