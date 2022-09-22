package com.nlteck.dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.nlteck.firmware.Channel;
import com.nlteck.parts.ChannelConsolePart;
import com.nlteck.resources.Resources;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;

public class ChnInfoDialog extends Dialog {

    protected Object result;
    public Shell shell;
    private int width;
    private int height;
    private int x;
    private int y;
    private Channel channel;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public ChnInfoDialog(Shell parent, Channel channel, int x, int y, int width, int height) {
	super(parent, SWT.NONE);
	this.width = width;
	this.height = height;
	this.channel = channel;
	this.x = x;
	this.y = y;
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public Object open() {
	createContents();
	shell.open();
	shell.layout();
	Display display = getParent().getDisplay();
	while (!shell.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	return result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
	shell = new Shell(getParent(), SWT.NO_TRIM);
	shell.setLocation(x, y);
	shell.setSize(width, height);
	shell.setLocation(x, y);
	shell.setBackground(Resources.BLUE_CLR);
	Region region = new Region();
	region.add(UIUtil.getIntsFromPoints(UIUtil.getPointsFromRectangle(new Rectangle(0, 0, width, height))));
	shell.setRegion(region);

	shell.addMouseTrackListener(new MouseTrackAdapter() {
	    @Override
	    public void mouseExit(MouseEvent e) {
		shell.getRegion().dispose();
		shell.dispose();
	    }
	});

	shell.addPaintListener(new PaintListener() {
	    @Override
	    public void paintControl(PaintEvent e) {
		GC gc = e.gc;

		Color baterryColor = Resources.NONE_CLR
				;
		if (channel.getPushData() != null) {
		    baterryColor = ChannelConsolePart.getChnColor(channel.getPushData().calState);
		}
		Rectangle batteryRectangle = new Rectangle(0, 0, width, height);
		UIUtil.drawSimpleBattery(gc, new Rectangle(0, 0, width, height), baterryColor, channel.isSelect());
		// 画电池上的信息
		PushData pushData = channel.getPushData();
		if (pushData != null) {
//		    int textX = batteryRectangle.x + 14;
//		    int texty = batteryRectangle.y + 20;
//		    int textH = 15;
//		    gc.drawString("状态：" + pushData.calState.name(), textX, texty, true);
//		    if (pushData.calState == CalState.CALCULATE || pushData.calState == CalState.CALIBRATE)
//			gc.drawString("模式：" + (pushData.calMode == CalMode.SLEEP ? "SLP" : pushData.calMode), textX, texty += textH, true);
//		    else
//			gc.drawString("模式：SLP", textX, texty += textH, true);
//		    if (pushData.calState == CalState.CALIBRATE || pushData.calState == CalState.CALCULATE && pushData.calMode != CalMode.SLEEP)
//			gc.drawString("极性：+", textX, texty += textH, true);
//		    else
//			gc.drawString("极性：+", textX, texty += textH, true);
//		    if (pushData.calState == CalState.CALIBRATE || pushData.calState == CalState.CALCULATE && pushData.calMode != CalMode.CV && pushData.calMode != CalMode.SLEEP)
//			gc.drawString("精度：0", textX, texty += textH, true);
//		    else
//			gc.drawString("精度：0", textX, texty += textH, true);
//		    if (pushData.seconds != 0)
//			gc.drawString("耗时：88:88:88", textX, texty += textH, true);
//		    else
//			gc.drawString("耗时：", textX, texty += textH, true);
//		    if (pushData.range != 0)
//			gc.drawString("步次：88/88", textX, texty += textH, true);
//		    else
//			gc.drawString("步次：88/88", textX, texty += textH, true);
//		    gc.drawString("当前值：888888", textX, texty += textH, true);
//
//		    gc.drawString("ADC：88/88", textX, texty += textH, true);
//		    gc.drawString("万用表：88/88", textX, texty += textH, true);
		} else {
		    UIUtil.drawTextCenter(gc, batteryRectangle, "暂无通道信息", true);
		}
		// 对接信息
		if (channel.getMatchedCalBoardChannel() != null) {
		    gc.drawImage(Resources.MATCHED_IMAGE, 0, 0, 14, 18, batteryRectangle.x + 10, batteryRectangle.y + batteryRectangle.height - 26, 18, 18);
		    gc.drawString("对接：校准箱1-1-1", batteryRectangle.x + 14, batteryRectangle.y + 155, true);
		} else {
		    gc.drawString("对接：暂无对接", batteryRectangle.x + 14, batteryRectangle.y + 155, true);
		}
		// 通道号
		StringBuilder chnInfo = new StringBuilder();
		chnInfo.append((channel.getDriver().getLogicBoard().getLogicIndex() + 1) + "-");
		chnInfo.append((channel.getDriver().getDriverIndex() + 1) + "-");
		chnInfo.append((channel.getChannelIndexInDriver() + 1));
		UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + batteryRectangle.height - 24, chnInfo.toString(), true);
	    }
	});

    }

    public void close() {
	if (!shell.isDisposed())
	    shell.getRegion().dispose();
	shell.dispose();
    }

}
