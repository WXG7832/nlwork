package com.nlteck.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.WorkBench;
import com.nlteck.parts.uiComponent.ChannelBattery;
import com.nlteck.resources.Resources;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;

import nlcal.NlteckCalEnvrionment;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

/**
 * 校准板调试面板
 * 
 * @author caichao_tang
 *
 */
public class CalibrateConsolePart {
	public static final String ID = "nlcal.part.calibrateConsole";
	public static final int CHANNEL_RECTANGLE_WIDTH = 96;
	public static final int CHANNEL_RECTANGLE_HEIGHT = 128;
	public static final int CHANNEL_INFO_MARGIN = 8;
	public static CalibrateConsolePart calibrateConsolePart;
	public static Map<Channel, ChannelBattery> chn_batteryMap = new HashMap<>();
	private Rectangle batteryRectangle;
	private Composite composite;

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FillLayout());

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		composite = new Composite(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(composite);

		// ************************************************************************
		// *
		// *监听器
		// *
		// ************************************************************************
		composite.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setAntialias(SWT.ON);
				gc.setTextAntialias(SWT.ON);
				Rectangle calBoardInfoRectangle = new Rectangle(0, -CHANNEL_RECTANGLE_HEIGHT, CHANNEL_RECTANGLE_WIDTH,
						CHANNEL_RECTANGLE_HEIGHT);
				// 绘制的校准箱数量
				int drawBoxCount = 0;
				for (int h = 0; h < WorkBench.calBoxList.size(); h++) {
					CalBox calBox = WorkBench.calBoxList.get(h);
					// 未绑定的校准箱不显示
					if (calBox.getDevice() == null)
						continue;
					// 未连接的校准箱不显示
					if (calBox.getConnector() == null || !calBox.getConnector().isConnected())
						continue;
					drawBoxCount++;
					List<CalBoard> calBoardList = calBox.getCalBoardList();
					// 绘制校准板
					for (int i = 0; i < calBoardList.size(); i++) {
						calBoardInfoRectangle.y = calBoardInfoRectangle.y + CHANNEL_RECTANGLE_HEIGHT;
						CalBoard calBoard = calBoardList.get(i);
						// 通道排序
						List<Channel> matchedDeviceChannels = new ArrayList<>();
						List<Channel> unmatchCalBoardChannels = new ArrayList<>();
						List<Channel> finalDrawChannels = new ArrayList<>();
						for (Channel channel : calBoard.getChannelList()) {
							if (channel.getMatchedDeviceChannel() != null)
								matchedDeviceChannels.add(channel.getMatchedDeviceChannel());
							else
								unmatchCalBoardChannels.add(channel);
						}
						Collections.sort(matchedDeviceChannels, (o1, o2) -> {
							if (o1.getIndexInDevice() < o2.getIndexInDevice())
								return -1;
							else if (o1.getIndexInDevice() > o2.getIndexInDevice())
								return 1;
							else
								return 0;
						});
						finalDrawChannels.addAll(matchedDeviceChannels);
						finalDrawChannels.addAll(unmatchCalBoardChannels);
						// 校准板无对接通道
						if (matchedDeviceChannels.isEmpty()) {
							UIUtil.drawTextHorCenter(gc, calBoardInfoRectangle,
									calBoardInfoRectangle.y + calBoardInfoRectangle.height / 2 - 16,
									calBox.getName() + "-" + (calBoard.getIndex() + 1), true);
							UIUtil.drawTextHorCenter(gc, calBoardInfoRectangle,
									calBoardInfoRectangle.y + calBoardInfoRectangle.height / 2 + 16, "暂无对接通道", true);
						} else {
							// 绘制校准板信息
							if (NlteckCalEnvrionment.isMatchMode)
								UIUtil.drawTextCenter(gc, calBoardInfoRectangle,
										calBox.getName() + "-" + (calBoard.getIndex() + 1), true);
							else {
								UIUtil.drawTextHorCenter(gc, calBoardInfoRectangle,
										calBoardInfoRectangle.y + calBoardInfoRectangle.height / 2 - 16,
										calBox.getName() + "#" + (calBoard.getIndex() + 1), true);
								UIUtil.drawTextHorCenter(gc, calBoardInfoRectangle,
										calBoardInfoRectangle.y + calBoardInfoRectangle.height / 2 + 16,
										"L-" + (matchedDeviceChannels.get(0).getDriver().getLogicBoard().getLogicIndex()
												+ 1) + "D-"
												+ (matchedDeviceChannels.get(0).getDriver().getDriverIndex() + 1),
										true);
							}
						}
						// 绘制通道
						batteryRectangle = new Rectangle(0, calBoardInfoRectangle.y, CHANNEL_RECTANGLE_WIDTH,
								CHANNEL_RECTANGLE_HEIGHT);
						for (int j = 0; j < finalDrawChannels.size(); j++) {
							Channel chn = finalDrawChannels.get(j);
							batteryRectangle.x = j * CHANNEL_RECTANGLE_WIDTH + CHANNEL_RECTANGLE_WIDTH;
							// ***********************************************画电池***************************************************
							if (chn.getDriver() != null) {
								PushData pushData = chn.getPushData();
								Color baterryColor = Resources.NONE_CLR;
								if (pushData != null) {
									baterryColor = ChannelConsolePart.getChnColor(pushData.calState);
								}
								UIUtil.drawSimpleBattery(gc, batteryRectangle, baterryColor, false);
								// 画电池上的信息
								DriverConsolePart.drawBatteryInfo(chn, gc, batteryRectangle);
							} else {
								UIUtil.drawSimpleBattery(gc, batteryRectangle, Resources.NONE_CLR, false);
								UIUtil.drawTextHorCenter(gc, batteryRectangle,
										batteryRectangle.y + CHANNEL_RECTANGLE_HEIGHT - 24,
										chn.getChannelIndexInCal() + "", true);
							}
						}
					}
				}
				if (drawBoxCount == 0) {
					UIUtil.drawTextCenter(e.gc, composite.getBounds(), "暂无已绑定的在线校准箱！", true);
					return;
				}
				scrolledComposite.setMinSize(batteryRectangle.x + CHANNEL_RECTANGLE_WIDTH,
						calBoardInfoRectangle.y + CHANNEL_RECTANGLE_HEIGHT);
			}
		});
		calibrateConsolePart = this;

	}

	/**
	 * 界面重绘
	 */
	public void redraw() {
		composite.redraw();
	}

}