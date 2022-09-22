package com.nlteck.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.nlteck.resources.Resources;

public class ColorInfoComposite extends Composite {

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public ColorInfoComposite(Composite parent, int style) {
	super(parent, style);
	GridLayout gl_this = new GridLayout(20, true);
	gl_this.verticalSpacing = 0;
	gl_this.marginHeight = 0;
	setLayout(gl_this);

	Label colorNoneLabel = new Label(this, SWT.FILL);
	colorNoneLabel.setAlignment(SWT.CENTER);
	colorNoneLabel.setText("无状态");
	GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
	gd_label.widthHint = 50;
	colorNoneLabel.setLayoutData(gd_label);
	colorNoneLabel.setBackground(Resources.NONE_CLR);

//	Label colorMatchLabel = new Label(this, SWT.FILL);
//	colorMatchLabel.setAlignment(SWT.CENTER);
//	colorMatchLabel.setText("已对接");
//	colorMatchLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
//	colorMatchLabel.setBackground(Resources.COLOR_MATCHED);

	Label colorReadyLabel = new Label(this, SWT.FILL);
	colorReadyLabel.setAlignment(SWT.CENTER);
	colorReadyLabel.setText("准备中");
	colorReadyLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
	colorReadyLabel.setBackground(Resources.READY_CLR);

	Label colorCalibratingLabel = new Label(this, SWT.FILL);
	colorCalibratingLabel.setAlignment(SWT.CENTER);
	colorCalibratingLabel.setText("运行中");
	colorCalibratingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
	colorCalibratingLabel.setBackground(Resources.COLOR_CALIBRATING);

//	Label colorCalculatingLabel = new Label(this, SWT.FILL);
//	colorCalculatingLabel.setAlignment(SWT.CENTER);
//	colorCalculatingLabel.setText("正在计量");
//	colorCalculatingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
//	colorCalculatingLabel.setBackground(Resources.COLOR_CALCULATING);

	Label colorCaliPassLabel = new Label(this, SWT.FILL);
	colorCaliPassLabel.setAlignment(SWT.CENTER);
	colorCaliPassLabel.setText("完成");
	colorCaliPassLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
	colorCaliPassLabel.setBackground(Resources.COLOR_CALIBRATE_PASS);

//	Label colorcCalcPassLabel = new Label(this, SWT.FILL);
//	colorcCalcPassLabel.setAlignment(SWT.CENTER);
//	colorcCalcPassLabel.setText("计量通过");
//	colorcCalcPassLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
//	colorcCalcPassLabel.setBackground(Resources.COLOR_CALCULATE_PASS);

	Label colorcCaliFailLabel = new Label(this, SWT.FILL);
	colorcCaliFailLabel.setAlignment(SWT.CENTER);
	colorcCaliFailLabel.setText("失败");
	colorcCaliFailLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
	colorcCaliFailLabel.setBackground(Resources.COLOR_CALIBRATE_FAIL);

//	Label colorcCalcFailLabel = new Label(this, SWT.FILL);
//	colorcCalcFailLabel.setText("计量失败");
//	colorcCalcFailLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1));
//	colorcCalcFailLabel.setBackground(Resources.COLOR_CALCULATE_FAIL);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);
	new Label(this, SWT.NONE);

    }

}
