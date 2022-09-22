
package com.nlteck.parts;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

import com.nlteck.firmware.Channel;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.resources.Resources;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

/**
 * 逻辑板调试面板
 * 
 * @author caichao_tang
 *
 */
public class ChannelConsolePart {
    public static final String ID = "nlcal.partdescriptor.channelConsolePart";
    public static ChannelConsolePart channelConsolePart;
    private Device device;
    private Composite composite;

    @Inject
    public ChannelConsolePart() {

    }

    @PostConstruct
    public void postConstruct(Composite parent) {
	parent.setLayout(new FormLayout());

	ColorInfoComposite colorInfoComposite = new ColorInfoComposite(parent, SWT.NONE);
	FormData fd_colorInfoComposite = new FormData();
	fd_colorInfoComposite.top = new FormAttachment(0, 0);
	fd_colorInfoComposite.bottom = new FormAttachment(0, 18);
	fd_colorInfoComposite.left = new FormAttachment(0, 0);
	fd_colorInfoComposite.right = new FormAttachment(100, 0);
	colorInfoComposite.setLayoutData(fd_colorInfoComposite);

	ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	scrolledComposite.setExpandHorizontal(true);
	scrolledComposite.setExpandVertical(true);
	FormData fd_scrolledComposite = new FormData();
	fd_scrolledComposite.top = new FormAttachment(0, 18);
	fd_scrolledComposite.bottom = new FormAttachment(100, 0);
	fd_scrolledComposite.left = new FormAttachment(0, 0);
	fd_scrolledComposite.right = new FormAttachment(100, 0);
	scrolledComposite.setLayoutData(fd_scrolledComposite);

	composite = new Composite(scrolledComposite, SWT.DOUBLE_BUFFERED);

	scrolledComposite.setContent(composite);

	composite.addPaintListener(new PaintListener() {
	    @Override
	    public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		gc.setAntialias(SWT.ON);
		gc.setTextAntialias(SWT.ON);
		int logicWidth = (composite.getBounds().width - 10) / 2;
		Rectangle leftLogicRectangle = new Rectangle(0, 0, logicWidth, 400);
		Rectangle rightLogicRectangle = new Rectangle(logicWidth + 10, 0, logicWidth, 400);
		Rectangle driverInfoRectangle = new Rectangle(0, 0, 0, 0);
		if (device == null)
		    return;
		List<LogicBoard> logicBoardList = device.getLogicBoardList();
		int driverHeight = (int) Math.floor((double) leftLogicRectangle.height / logicBoardList.get(0).getDrivers().size());
		int chnWidth = leftLogicRectangle.width / (logicBoardList.get(0).getDrivers().get(0).getChannels().size() + 1);
		for (int i = 0; i < logicBoardList.size(); i++) {
		    LogicBoard logicBoard = logicBoardList.get(i);
		    // 使用左边逻辑板区域
		    if (i % 2 == 0) {
			if (i > 0)
			    leftLogicRectangle.y = leftLogicRectangle.y + 400 + 10;
			driverInfoRectangle.x = leftLogicRectangle.x;
			driverInfoRectangle.width = leftLogicRectangle.width - (chnWidth * (logicBoard.getDrivers().get(0).getChannels().size()));
			driverInfoRectangle.height = driverHeight;
			// 驱动板信息区域
			for (int j = 0; j < logicBoard.getDrivers().size(); j++) {
			    DriverBoard driverBoard = logicBoard.getDrivers().get(j);
			    driverInfoRectangle.y = leftLogicRectangle.y + j * driverHeight;
			    gc.setForeground(Resources.COLOR_DRIVER_INFO);
			    gc.setBackground(Resources.GRAY_CLR);
			    gc.fillGradientRectangle(driverInfoRectangle.x, driverInfoRectangle.y, driverInfoRectangle.width, driverInfoRectangle.height, false);
			    gc.setForeground(Resources.COLOR_BLACK);
			    UIUtil.drawTextCenter(gc, driverInfoRectangle, driverBoard.getDriverIndexInDevice() + 1 + "", true);
			    // 绘制通道信息
			    for (int k = 0; k < driverBoard.getChannels().size(); k++) {
//				Channel channel = driverBoard.getChannels().get(k);
//				if (channel.getPushData() != null) {
//				    gc.setBackground(getChnColor(channel.getPushData().calState));
//				} else {
//				    gc.setBackground(Resources.GRAY_CLR);
//				}
//				gc.fillRectangle(driverInfoRectangle.x + driverInfoRectangle.width + k * chnWidth, driverInfoRectangle.y, chnWidth, driverHeight);
//				// 对接信息
//				if (channel.getMatchedCalBoardChannel() != null) {
//				    gc.drawImage(Resources.MATCHED_IMAGE, 0, 0, 18, 18, driverInfoRectangle.x + driverInfoRectangle.width + k * chnWidth, driverInfoRectangle.y + 28, 18, 18);
//				}
//				UIUtil.drawTextCenter(gc, new Rectangle(driverInfoRectangle.x + driverInfoRectangle.width + k * chnWidth, driverInfoRectangle.y, chnWidth, driverHeight), k + 1 + "", true);
			    }
			}
			// 逻辑板边界线
			gc.drawRectangle(leftLogicRectangle);
			// 纵向线条
			for (int j = 0; j < logicBoard.getDrivers().get(0).getChannels().size(); j++)
			    gc.drawLine(leftLogicRectangle.x + leftLogicRectangle.width - (j * chnWidth + chnWidth), leftLogicRectangle.y, leftLogicRectangle.x + leftLogicRectangle.width - (j * chnWidth + chnWidth), leftLogicRectangle.y + leftLogicRectangle.height);
			// 横向线条
			for (int j = 0; j < logicBoard.getDrivers().size() - 1; j++)
			    gc.drawLine(leftLogicRectangle.x, leftLogicRectangle.y + j * driverHeight + driverHeight, leftLogicRectangle.x + leftLogicRectangle.width, leftLogicRectangle.y + j * driverHeight + driverHeight);
		    }
		    // 使用右边逻辑板区域
		    else {
			if (i > 1)
			    rightLogicRectangle.y = rightLogicRectangle.y + 400 + 10;
			driverInfoRectangle.x = rightLogicRectangle.x;
			driverInfoRectangle.width = rightLogicRectangle.width - (chnWidth * (logicBoard.getDrivers().get(0).getChannels().size()));
			driverInfoRectangle.height = driverHeight;
			// 驱动板信息区域
			for (int j = 0; j < logicBoard.getDrivers().size(); j++) {
			    DriverBoard driverBoard = logicBoard.getDrivers().get(j);
			    driverInfoRectangle.y = rightLogicRectangle.y + j * driverHeight;
			    gc.setForeground(Resources.COLOR_DRIVER_INFO);
			    gc.setBackground(Resources.GRAY_CLR);
			    gc.fillGradientRectangle(driverInfoRectangle.x, driverInfoRectangle.y, driverInfoRectangle.width, driverInfoRectangle.height, false);
			    gc.setForeground(Resources.COLOR_BLACK);
			    UIUtil.drawTextCenter(gc, driverInfoRectangle, driverBoard.getDriverIndexInDevice() + 1 + "", true);
			    // 绘制通道信息
			    for (int k = 0; k < driverBoard.getChannels().size(); k++) {
//				Channel channel = driverBoard.getChannels().get(k);
//				if (channel.getPushData() != null) {
//				    gc.setBackground(getChnColor(channel.getPushData().calState));
//				} else {
//				    gc.setBackground(Resources.GRAY_CLR);
//				}
//				gc.fillRectangle(driverInfoRectangle.x + driverInfoRectangle.width + k * chnWidth, driverInfoRectangle.y, chnWidth, driverHeight);
//				if (channel.getPushData() != null && channel.getPushData().matched) {
//				    gc.setBackground(Resources.COLOR_MATCHED);
//				    gc.fillArc(driverInfoRectangle.x + driverInfoRectangle.width + k * chnWidth + 2, driverInfoRectangle.y + 2, 16, 16, 0, 360);
//				}
//				UIUtil.drawTextCenter(gc, new Rectangle(driverInfoRectangle.x + driverInfoRectangle.width + k * chnWidth, driverInfoRectangle.y, chnWidth, driverHeight), k + 1 + "", true);
			    }
			}
			gc.drawRectangle(rightLogicRectangle);
			// 纵向线条
			for (int j = 0; j < logicBoard.getDrivers().get(0).getChannels().size(); j++)
			    gc.drawLine(rightLogicRectangle.x + rightLogicRectangle.width - (j * chnWidth + chnWidth), rightLogicRectangle.y, rightLogicRectangle.x + rightLogicRectangle.width - (j * chnWidth + chnWidth), rightLogicRectangle.y + rightLogicRectangle.height);
			// 横向线条
			for (int j = 0; j < logicBoard.getDrivers().size() - 1; j++)
			    gc.drawLine(rightLogicRectangle.x, rightLogicRectangle.y + j * driverHeight + driverHeight, rightLogicRectangle.x + rightLogicRectangle.width, rightLogicRectangle.y + j * driverHeight + driverHeight);
		    }
		}
		int leftNum = (logicBoardList.size() + 1) / 2;
		int leftSpace = 0;
		if (leftNum > 0) {
		    leftSpace = (leftNum - 1) * 10;
		}
		scrolledComposite.setMinSize(0, leftNum * 400 + leftSpace);
	    }
	});
	channelConsolePart = this;
    }

    /**
     * 设置界面绑定
     */
    public void setPartBind(Device device) {
	this.device = device;
    }

    /**
     * 重绘界面
     */
    public void redraw() {
	composite.redraw();
    }

    /**
     * 获取指定状态通道颜色
     * 
     * @param calState
     * @return
     */
    public static Color getChnColor(CalState calState) {
	Color color = Resources.NONE_CLR;
	switch (calState) {
	case NONE:
	    return color;
	case READY:
	    return Resources.READY_CLR;
	case CALIBRATE:
	    return Resources.COLOR_CALIBRATING;
	case CALCULATE:
	    return Resources.COLOR_CALCULATING;
	case CALIBRATE_PASS:
	    return Resources.COLOR_CALIBRATE_PASS;
	case CALCULATE_PASS:
	    return Resources.COLOR_CALCULATE_PASS;
	case CALIBRATE_FAIL:
	    return Resources.COLOR_CALIBRATE_FAIL;
	case CALCULATE_FAIL:
	    return Resources.COLOR_CALCULATE_FAIL;
	default:
	    return color;
	}
    }
}