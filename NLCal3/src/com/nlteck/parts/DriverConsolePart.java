
package com.nlteck.parts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import com.nlteck.dialog.ChnInfoDialog;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Channel;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestLog;
import com.nlteck.parts.uiComponent.ChannelBattery;
import com.nlteck.resources.Resources;
import com.nlteck.utils.CommonUtil;
import com.nlteck.utils.UIUtil;
import com.nltecklib.protocol.li.PCWorkform.ChnSelectData;
import com.nltecklib.protocol.li.PCWorkform.TestModeData;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.CalState;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushData;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.PushLog;
import com.nltecklib.protocol.li.PCWorkform.PCWorkformEnvironment.TestMode;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;
import nlcal.NlteckCalEnvrionment;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 单驱动板调试面板
 * 
 * @author caichao_tang
 *
 */
public class DriverConsolePart extends ConsolePart {
	public static final String ID = "nlcal.partdescriptor.driverConsolePart";
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("hh:mm:ss:SSS");
	public static final int CHANNEL_RECTANGLE_WIDTH = 96;
	public static final int CHANNEL_RECTANGLE_HEIGHT = 128;
	public static final int CHANNEL_INFO_MARGIN = 8;
	public static final int BIG_CHN_WIDTH = 150;
	public static final int BIG_CHN_HEIGHT = 200;
	public static DriverConsolePart driverConsolePart;
	public static Map<ChannelDO, ChannelBattery> chn_batteryMap = new HashMap<>();
	public DriverBoard driverBoard;
	//private CalCommandComposite calCommandComposite;
	private TableViewer tableViewer;
	private Text logText;
	private Point startPoint;
	private Point nowPoint;
	private Composite composite;
	private ChnInfoDialog chnInfoDialog;
	private Entry<Channel, ChannelBattery> hoverEntry;

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FormLayout());

		Composite toolBarcomposite = new Composite(parent, SWT.NONE);
		toolBarcomposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_toolBarcomposite = new FormData();
		fd_toolBarcomposite.top = new FormAttachment(0, 0);
		fd_toolBarcomposite.bottom = new FormAttachment(0, 40);
		fd_toolBarcomposite.left = new FormAttachment(0, 0);
		fd_toolBarcomposite.right = new FormAttachment(100, 0);
		toolBarcomposite.setLayoutData(fd_toolBarcomposite);

		//calCommandComposite = new CalCommandComposite(this, toolBarcomposite, SWT.NONE);

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, 40);
		fd_scrolledComposite.bottom = new FormAttachment(0, CHANNEL_RECTANGLE_HEIGHT + 54);
		fd_scrolledComposite.left = new FormAttachment(0, 0);
		fd_scrolledComposite.right = new FormAttachment(100, 0);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		composite = new Composite(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(composite);

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		FormData fd_sashForm = new FormData();
		fd_sashForm.top = new FormAttachment(0, CHANNEL_RECTANGLE_HEIGHT + 64);
		fd_sashForm.bottom = new FormAttachment(100, 0);
		fd_sashForm.left = new FormAttachment(0, 0);
		fd_sashForm.right = new FormAttachment(100, 0);
		sashForm.setLayoutData(fd_sashForm);

		Composite tableComposite = new Composite(sashForm, SWT.NONE);
		tableComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TestDotDataLableProvider());

		new TestDotDataLableProvider().init(tableViewer);

//		Table table = tableViewer.getTable();
//		table.setLinesVisible(true);
//		table.setHeaderVisible(true);
//
//		TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
//		tableColumn.setWidth(60);
//		tableColumn.setText("通道号");
//
//		TableColumn tableColumn1 = new TableColumn(table, SWT.CENTER);
//		tableColumn1.setWidth(70);
//		tableColumn1.setText("测试类型");
//
//		TableColumn tableColumn1Pole = new TableColumn(table, SWT.CENTER);
//		tableColumn1Pole.setWidth(70);
//		tableColumn1Pole.setText("模式");
//
//		TableColumn tableColumnPrecision = new TableColumn(table, SWT.CENTER);
//		tableColumnPrecision.setWidth(70);
//		tableColumnPrecision.setText("电压类型");
//
//		TableColumn tableColumnMode = new TableColumn(table, SWT.CENTER);
//		tableColumnMode.setWidth(70);
//		tableColumnMode.setText("极性");
//
//		TableColumn tableColumn2 = new TableColumn(table, SWT.CENTER);
//		tableColumn2.setWidth(70);
//		tableColumn2.setText("精度");
//
//		TableColumn tableColumn3 = new TableColumn(table, SWT.CENTER);
//		tableColumn3.setWidth(70);
//		tableColumn3.setText("表值");
//
//		TableColumn tableColumn4 = new TableColumn(table, SWT.CENTER);
//		tableColumn4.setWidth(70);
//		tableColumn4.setText("程控值");
//
//		TableColumn tableColumn5 = new TableColumn(table, SWT.CENTER);
//		tableColumn5.setWidth(70);
//		tableColumn5.setText("adc");
//
//		TableColumn tableColumn6 = new TableColumn(table, SWT.CENTER);
//		tableColumn6.setWidth(70);
//		tableColumn6.setText("回检adc");
//
//		TableColumn tableColumn7 = new TableColumn(table, SWT.CENTER);
//		tableColumn7.setWidth(70);
//		tableColumn7.setText("程控k");
//
//		TableColumn tableColumn8 = new TableColumn(table, SWT.CENTER);
//		tableColumn8.setWidth(70);
//		tableColumn8.setText("程控b");
//
//		TableColumn tableColumn9 = new TableColumn(table, SWT.CENTER);
//		tableColumn9.setWidth(70);
//		tableColumn9.setText("adc k");
//
//		TableColumn tableColumn10 = new TableColumn(table, SWT.CENTER);
//		tableColumn10.setWidth(70);
//		tableColumn10.setText("adc b");
//
//		TableColumn tableColumn11 = new TableColumn(table, SWT.CENTER);
//		tableColumn11.setWidth(70);
//		tableColumn11.setText("回检adc k");
//
//		TableColumn tableColumn12 = new TableColumn(table, SWT.CENTER);
//		tableColumn12.setWidth(70);
//		tableColumn12.setText("回检adc b");
//
//		TableColumn tableColumn13 = new TableColumn(table, SWT.CENTER);
//		tableColumn13.setWidth(70);
//		tableColumn13.setText("成功");
//
//		TableColumn tableColumn14 = new TableColumn(table, SWT.CENTER);
//		tableColumn14.setWidth(200);
//		tableColumn14.setText("信息");

		Composite logComposite = new Composite(sashForm, SWT.DOUBLE_BUFFERED);
		logComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		logText = new Text(logComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.READ_ONLY);

		sashForm.setWeights(new int[] { 3, 1 });

		// ************************************************************************
		// *
		// *监听器
		// *
		// ************************************************************************
//		composite.addPaintListener(new PaintListener() {
//			@Override
//			public void paintControl(PaintEvent e) {
//				GC gc = e.gc;
//				gc.setAntialias(SWT.ON);
//				gc.setTextAntialias(SWT.ON);
//				if (driverBoard == null)
//					return;
//				List<ChannelDO> chnList = driverBoard.getChannels();
//				Rectangle batteryRectangle = new Rectangle(0, 0, CHANNEL_RECTANGLE_WIDTH, CHANNEL_RECTANGLE_HEIGHT);
//				for (int i = 0; i < chnList.size(); i++) {
//					batteryRectangle.x = i * CHANNEL_RECTANGLE_WIDTH;
//					ChannelDO chn = chnList.get(i);
//					ChannelBattery channelBattery = chn_batteryMap.get(chn);
//					if (channelBattery == null)
//						chn_batteryMap.put(chn, new ChannelBattery(new Rectangle(batteryRectangle.x, batteryRectangle.y,
//								batteryRectangle.width, batteryRectangle.height)));
//					else
//						channelBattery.setRectangle(new Rectangle(batteryRectangle.x, batteryRectangle.y,
//								batteryRectangle.width, batteryRectangle.height));
//					channelBattery = chn_batteryMap.get(chn);
//					// ***********************************************画电池***************************************************
//					PushData pushData = chn.getPushData();
//					Color baterryColor = Resources.COLOR_NONE;
//					if (pushData != null) {
//						baterryColor = ChannelConsolePart.getChnColor(pushData.calState);
//					}
//					UIUtil.drawSimpleBattery(gc, batteryRectangle, baterryColor, chn.isSelect());
//					drawBatteryInfo(chn, gc, batteryRectangle);
//				}
//				scrolledComposite.setMinSize(chnList.size() * CHANNEL_RECTANGLE_WIDTH, 0);
//			}
//		});
//
//		composite.addMouseMoveListener(new MouseMoveListener() {
//			@Override
//			public void mouseMove(MouseEvent e) {
//				// 鼠标在电池上时
//				boolean isInChannel = false;
//				for (Entry<Channel, ChannelBattery> channel_battery : chn_batteryMap.entrySet()) {
//					Point hoverPoint = new Point(e.x, e.y);
//					if (UIUtil.isInPolygon(hoverPoint,
//							UIUtil.getPointsFromRectangle(channel_battery.getValue().getRectangle()))
//							&& channel_battery.getKey().getDriver() == driverBoard) {
//						isInChannel = true;
//						hoverEntry = channel_battery;
//						break;
//					}
//				}
//				if (isInChannel) {
//					composite.setCursor(Resources.handCursor);
//					// 计算弹窗坐标点
//					int x = hoverEntry.getValue().getRectangle().x + hoverEntry.getValue().getRectangle().width / 2
//							+ e.display.getCursorLocation().x - e.x - BIG_CHN_WIDTH / 2;
//					int y = hoverEntry.getValue().getRectangle().y + hoverEntry.getValue().getRectangle().height / 2
//							+ e.display.getCursorLocation().y - e.y - BIG_CHN_HEIGHT / 2;
//					int hoverx = e.display.getCursorLocation().x;
//					int hovery = e.display.getCursorLocation().y;
//					new Thread(() -> {
//						try {
//							Thread.sleep(1500);
//						} catch (InterruptedException e1) {
//							e1.printStackTrace();
//						}
//						Display.getDefault().asyncExec(() -> {
//							if (e.display.getCursorLocation().x == hoverx
//									&& e.display.getCursorLocation().y == hovery) {
//								if (composite.getMenu() != null && !composite.getMenu().isDisposed()
//										&& composite.getMenu().getVisible())
//									return;
//								if (chnInfoDialog != null) {
//									chnInfoDialog.close();
//								}
//								chnInfoDialog = new ChnInfoDialog(parent.getShell(), hoverEntry.getKey(), x, y,
//										BIG_CHN_WIDTH, BIG_CHN_HEIGHT);
//								chnInfoDialog.open();
//							}
//						});
//					}).start();
//				} else {
//					composite.setCursor(Resources.normalCursor);
//					return;
//				}
//				if (e.stateMask == 524288 || e.stateMask == 786432) {
//					// 清空当前界面已选
//					if (e.stateMask == 524288)
//						for (Channel channel : chn_batteryMap.keySet()) {
//							if (channel.getDriver() == driverBoard)
//								channel.setSelect(false);
//						}
//					for (Entry<Channel, ChannelBattery> channel_battery : chn_batteryMap.entrySet()) {
//						if (UIUtil.isInPolygon(new Point(e.x, e.y),
//								UIUtil.getPointsFromRectangle(channel_battery.getValue().getRectangle()))
//								&& channel_battery.getKey().getDriver() == driverBoard) {
//							nowPoint = new Point(channel_battery.getValue().getRectangle().x,
//									channel_battery.getValue().getRectangle().y);
//							break;
//						}
//					}
//					for (Entry<Channel, ChannelBattery> channel_battery : chn_batteryMap.entrySet()) {
//						Rectangle chnRectangle = channel_battery.getValue().getRectangle();
//						if (startPoint == null || nowPoint == null)
//							break;
//						if (chnRectangle.x >= startPoint.x && chnRectangle.x <= nowPoint.x
//								&& chnRectangle.y >= startPoint.y && chnRectangle.y <= nowPoint.y)
//							if (channel_battery.getKey().getDriver() == driverBoard)
//								channel_battery.getKey().setSelect(true);
//					}
//					composite.redraw();
//				}
//				// toolBar状态
//				calCommandComposite.setCommandState();
//			}
//		});
//
//		composite.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//				if (e.button == 1 && e.stateMask == 0) {
//					for (Entry<Channel, ChannelBattery> channelBattery : chn_batteryMap.entrySet()) {
//						if (UIUtil.isInPolygon(new Point(e.x, e.y),
//								UIUtil.getPointsFromRectangle(channelBattery.getValue().getRectangle()))
//								&& channelBattery.getKey().getDriver() == driverBoard) {
//							startPoint = new Point(channelBattery.getValue().getRectangle().x,
//									channelBattery.getValue().getRectangle().y);
//							// 判断当前是否已经选中
//							if (channelBattery.getKey().isSelect()) {
//								// 清空当前已选
//								for (Channel channel : chn_batteryMap.keySet()) {
//									if (channel.getDriver() == driverBoard)
//										channel.setSelect(false);
//								}
//							} else {
//								// 清空当前已选
//								for (Channel channel : chn_batteryMap.keySet()) {
//									if (channel.getDriver() == driverBoard)
//										channel.setSelect(false);
//								}
//								// 选中当前按下的
//								if (channelBattery.getKey().getDriver() == driverBoard)
//									channelBattery.getKey().setSelect(true);
//							}
//							break;
//						}
//					}
//					composite.redraw();
//				} else if (e.button == 1 && e.stateMask == 262144) {
//					for (Entry<Channel, ChannelBattery> channelBattery : chn_batteryMap.entrySet()) {
//						if (UIUtil.isInPolygon(new Point(e.x, e.y),
//								UIUtil.getPointsFromRectangle(channelBattery.getValue().getRectangle()))
//								&& channelBattery.getKey().getDriver() == driverBoard) {
//							startPoint = new Point(channelBattery.getValue().getRectangle().x,
//									channelBattery.getValue().getRectangle().y);
//							if (channelBattery.getKey().isSelect()
//									&& channelBattery.getKey().getDriver() == driverBoard) {
//								// 取消当前选中
//								channelBattery.getKey().setSelect(false);
//							} else {
//								// 选中当前按下的
//								if (channelBattery.getKey().getDriver() == driverBoard)
//									channelBattery.getKey().setSelect(true);
//							}
//							break;
//						}
//					}
//					composite.redraw();
//				}
//				// 右击
//				else if (e.button == 3) {
//					for (Entry<Channel, ChannelBattery> channelBattery : chn_batteryMap.entrySet()) {
//						if (UIUtil.isInPolygon(new Point(e.x, e.y),
//								UIUtil.getPointsFromRectangle(channelBattery.getValue().getRectangle()))
//								&& channelBattery.getKey().getDriver() == driverBoard) {
//							// 清空本界面已选
//							for (Channel channel : chn_batteryMap.keySet()) {
//								if (channel.getDriver() == driverBoard)
//									channel.setSelect(false);
//							}
//							// 选中当前按下的
//							channelBattery.getKey().setSelect(true);
//							composite.redraw();
//							createSingleCalMenu(composite, channelBattery.getKey());
//							break;
//						} else {
//							if (composite.getMenu() != null)
//								composite.getMenu().dispose();
//						}
//					}
//				}
//				// toolBar状态
//				calCommandComposite.setCommandState();
//			}
//		});

		driverConsolePart = this;
	}

	public enum TestDotColumnHead {
		ChnIndex("通道号", 60), TestType("测试类型", 70), CalMode("模式", 60), VoltMode("电压类型", 80), Pole("极性", 60),
		Precision("精度", 60), MeterVal("表值", 70), ProgramVal("程控值", 70), Adc("adc", 70), CheckAdc("回检adc", 70),
		ProgramK("程控K", 70), ProgramB("程控B", 70), AdcK("adcK", 70), AdcB("adcB", 70), CheckAdcK("回检adcK", 70),
		CheckAdcB("回检adcB", 70), Result("结果", 60), Info("信息", 250);
		private String text;
		private int length;

		private TestDotColumnHead(String head, int length) {
			this.text = head;
			this.length = length;
		}

		public String getText() {
			return text;
		}

		public int getLength() {
			return length;
		}

	}

	/**
	 * 通道数据表格内容提供器
	 * 
	 * @author caichao_tang
	 *
	 */
	private class TestDotDataLableProvider implements ITableLabelProvider, ITableColorProvider {
		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		public void init(TableViewer tableViewer) {
			Table table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			for (TestDotColumnHead head : TestDotColumnHead.values()) {
				TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
				tableColumn.setWidth(head.getLength());
				tableColumn.setText(head.getText());
			}

			tableViewer.setLabelProvider(this);

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
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			UploadTestDot data = (UploadTestDot) element;
			String columnString = "";
			switch (TestDotColumnHead.values()[columnIndex]) {
			case ChnIndex:
				columnString = data.chnIndex % driverBoard.getChannels().size() + 1 + "";
				break;
			case TestType:
				columnString = data.testType.name();
				break;
			case CalMode:
				columnString = data.mode.name();
				break;
			case VoltMode:
				if (data.voltMode == null)
					columnString = "";
				else
					columnString = data.voltMode.name();
				break;
			case Pole:
				columnString = data.pole == Pole.NORMAL ? "+" : "-";
				break;
			case Precision:
				columnString = data.precision + "";
				break;
			case MeterVal:
				columnString = String.format("%.3f", data.meterVal);
				break;
			case ProgramVal:
				columnString = String.format("%.3f", data.programVal);
				break;
			case Adc:
				columnString = String.format("%.3f", data.adc);
				break;
			case CheckAdc:
				columnString = String.format("%.3f", data.checkAdc);
				break;
			case ProgramK:
				columnString = String.format("%.3f", data.programK);
				break;
			case ProgramB:
				columnString = String.format("%.3f", data.programB);
				break;
			case AdcK:
				columnString = String.format("%.3f", data.adcK);
				break;
			case AdcB:
				columnString = String.format("%.3f", data.adcB);
				break;
			case CheckAdcK:
				columnString = String.format("%.3f", data.checkAdcK);
				break;
			case CheckAdcB:
				columnString = String.format("%.3f", data.checkAdcB);
				break;
			case Result:
				columnString = data.success ? "pass" : "fail";
				break;
			case Info:
				columnString = data.info;
				break;
			default:
				break;
			}
			return columnString;
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			UploadTestDot data = (UploadTestDot) element;
			Color color = null;
			if (!data.success) {
				color = Resources.COLOR_RED;
			}
			return color;
		}

		@Override
		public Color getBackground(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * 展示数据到界面上
	 */
	public void showData(DriverBoard driverBoard) {
		this.driverBoard = driverBoard;
		composite.redraw();
		// 表格
		refreshTable();
		// 日志数据
		if (driverBoard.getPushLogList() != null) {
			if (logText == null)
				return;
			logText.setText("");
			StringBuffer logStringBuffer = new StringBuffer();
			for (PushLog pushLog : driverBoard.getPushLogList()) {
				logStringBuffer
						.append("通道 [ " + (pushLog.chnIndexInLogic % driverBoard.getChannels().size() + 1) + " ] --- ");
				logStringBuffer.append(SIMPLE_DATE_FORMAT.format(pushLog.date) + " ： ");
				logStringBuffer.append(pushLog.log);
				logStringBuffer.append("\n");
			}
			logText.setText(logStringBuffer.toString());
		}
	}

	/**
	 * 设置界面绑定
	 * 
	 * @param driverBoard
	 */
	public void setPartBind(DriverBoard driverBoard) {
		this.driverBoard = driverBoard;
		//this.channelList = driverBoard.getChannels();
	}

	@Override
	public List<CalBox> getBindCalBox() {
		return driverBoard.getLogicBoard().getDevice().getCalBoxList();
	}

	/**
	 * 设置工具栏的状态
	 */
	public void setToolItemState() {
//		calCommandComposite.setConnectState();
//		calCommandComposite.setSwitchState();
//		calCommandComposite.setModeState();
//		calCommandComposite.setCommandState();

	}

	/**
	 * 刷新日志界面
	 */
	public void refreshLog(Channel logOwner, PushLog pushLog) {
		// 日志数据
		if (driverBoard == null)
			return;
		if (logOwner.getDriver() == driverBoard) {
			if (logText == null)
				return;
			logText.append("通道 [ " + (pushLog.chnIndexInLogic % driverBoard.getChannels().size() + 1) + " ] --- ");
			logText.append(SIMPLE_DATE_FORMAT.format(pushLog.date) + " ： ");
			logText.append(pushLog.log + "\n");
		}
	}

	/**
	 * 重绘电池界面
	 */
	public void redraw() {
		composite.redraw();
	}

	/**
	 * 刷新表格数据
	 */
	public void refreshTable() {
		// 表格数据
		List<UploadTestDot> uploadTestDotList = driverBoard.getUploadTestDotList();
		tableViewer.setInput(uploadTestDotList);
		tableViewer.getTable().setTopIndex(uploadTestDotList.size() - 1);
	}

	/**
	 * 创建单点校准菜单
	 * 
	 * @param composite
	 */
	public static void createSingleCalMenu(Composite composite, Channel selectChannel) {
		// 未对接的通道不弹菜单
		if (selectChannel.getMatchedCalBoardChannel() == null) {
			return;
		}
		CalBox calBox = selectChannel.getMatchedCalBoardChannel().getCalBoard().getCalBox();
		// 对接的校准箱非校准模式不弹菜单
		if (calBox.getWorkMode() != CalibrateCoreWorkMode.CAL) {
			return;
		}
		if (composite.getMenu() != null)
			composite.getMenu().dispose();

		Menu compositeMenu = new Menu(composite);
		composite.setMenu(compositeMenu);

		compositeMenu.setVisible(true);
		MenuItem startCalibrateMenuItem = new MenuItem(compositeMenu, SWT.PUSH);
		startCalibrateMenuItem.setText("开始校准");

		MenuItem startCalculateMenuItem = new MenuItem(compositeMenu, SWT.PUSH);
		startCalculateMenuItem.setText("开始计量");

		MenuItem stopOperateMenuItem = new MenuItem(compositeMenu, SWT.PUSH);
		stopOperateMenuItem.setText("停止操作");

		if (selectChannel.getPushData() == null) {
			startCalibrateMenuItem.setEnabled(true);
			startCalculateMenuItem.setEnabled(true);
			stopOperateMenuItem.setEnabled(true);
		} else {
			switch (selectChannel.getPushData().calState) {
			case NONE:
			case CALCULATE_FAIL:
			case CALIBRATE_FAIL:
			case CALCULATE_PASS:
			case CALIBRATE_PASS:
				startCalibrateMenuItem.setEnabled(true);
				startCalculateMenuItem.setEnabled(true);
				stopOperateMenuItem.setEnabled(false);
				break;
			case READY:
			case CALCULATE:
			case CALIBRATE:
				startCalibrateMenuItem.setEnabled(false);
				startCalculateMenuItem.setEnabled(false);
				stopOperateMenuItem.setEnabled(true);
			default:
				break;
			}
		}

		Map<DriverBoard, List<Channel>> driver_chnsMap = new HashMap<>();
		List<Channel> channelList = new ArrayList<>();
		channelList.add(selectChannel);
		driver_chnsMap.put(selectChannel.getDriver(), channelList);

		startCalibrateMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//WorkBench.sendOperateCommand(calBox, driver_chnsMap, TestMode.EnterCalModeAndStartCal);
			}
		});

		startCalculateMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//WorkBench.sendOperateCommand(calBox, driver_chnsMap, TestMode.EnterCalModeAndStartCheck);
			}
		});

		stopOperateMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

//				new Thread(() -> {
//					// 下发操作指令
//					TestModeData operateModeData = new TestModeData();
//					operateModeData.setTestMode(TestMode.EnterCalModeAndStartCheck);
//					StringBuffer info = new StringBuffer();
//					if (!WorkBench.configCommand(calBox, operateModeData, 2000, info)) {
//						Display.getDefault().asyncExec(() -> {
//							MessageDialog.openError(Display.getDefault().getActiveShell(), "操作失败",
//									"下发操作指令" + TestMode.EnterCalModeAndStartCheck.name() + "时失败！\n" + info.toString());
//							WorkBench.getDatabaseManager().getTestLogDao()
//									.insert(new TestLog(WorkBench.currenTestData.getId(), "ERROR",
//											TestMode.EnterCalModeAndStartCheck.name() + "时失败", new Date()));
//						});
//						return;
//					}
//				}).start();

//				WorkBench.sendOperateCommand(calBox, driver_chnsMap, TestMode.StopTestAndExitCalMode);
			}
		});

	}

	/**
	 * 在电池中画信息内容
	 * 
	 * @param pushData
	 * @param gc
	 * @param batteryRectangle
	 */
	public static void drawBatteryInfo(Channel chn, GC gc, Rectangle batteryRectangle) {
		// 对接信息
		if (chn.getMatchedCalBoardChannel() != null) {
			gc.drawImage(Resources.MATCHED_IMAGE, 0, 0, 18, 18, batteryRectangle.x + 10,
					batteryRectangle.y + batteryRectangle.height - 26, 18, 18);
		}
		// 通道号
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append((chn.getDriver().getLogicBoard().getLogicIndex() + 1) + "-");
		stringBuilder.append((chn.getDriver().getDriverIndex() + 1) + "-");
		stringBuilder.append((chn.getChannelIndexInDriver() + 1));
		UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + CHANNEL_RECTANGLE_HEIGHT - 24,
				stringBuilder.toString(), true);

		PushData pushData = chn.getPushData();
		UploadTestDot uploadTestDot = chn.getUploadTestDot();

		if (pushData == null || pushData.calState == CalState.NONE || pushData.calState == CalState.READY
				|| pushData.calState == CalState.CALCULATE_FAIL || pushData.calState == CalState.CALIBRATE_FAIL
				|| pushData.calState == CalState.CALCULATE_PASS || pushData.calState == CalState.CALIBRATE_PASS
				|| uploadTestDot == null)
			return;

		stringBuilder = new StringBuilder();
		// 模式
		stringBuilder.append(uploadTestDot.mode == CalMode.SLEEP ? "SLP" : uploadTestDot.mode.name());
		// 极性
		if (pushData.calState == CalState.CALIBRATE
				|| pushData.calState == CalState.CALCULATE && uploadTestDot.mode != CalMode.SLEEP)
			stringBuilder.append(uploadTestDot.pole == Pole.NORMAL ? "+" : "-");
		// 精度
		if (pushData.calState == CalState.CALIBRATE || pushData.calState == CalState.CALCULATE
				&& uploadTestDot.mode != CalMode.CV && uploadTestDot.mode != CalMode.SLEEP)
			stringBuilder.append(uploadTestDot.precision + " ");
		// 耗时
		if (uploadTestDot.seconds != 0)
			stringBuilder.append(CommonUtil.formatTimeExceptHours(uploadTestDot.seconds));
		UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 16, stringBuilder.toString(), true);

		// 校准数值信息
		if (pushData.calState == CalState.CALIBRATE || pushData.calState == CalState.CALIBRATE_PASS
				|| pushData.calState == CalState.CALIBRATE_FAIL) {
			// 开发者模式
			if (NlteckCalEnvrionment.isDevelopMode) {
				if (uploadTestDot.programVal != 0)
					UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 32,
							"D:" + String.format("%.3f", uploadTestDot.programVal), true);
				if (uploadTestDot.adc != 0)
					UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 48,
							"A:" + String.format("%.3f", uploadTestDot.adc), true);
				if (uploadTestDot.meterVal != 0)
					UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 64,
							"M:" + String.format("%.3f", uploadTestDot.meterVal), true);
				if (uploadTestDot.range != 0)
					UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 80,
							uploadTestDot.pos + "/" + uploadTestDot.range, true);
			} else {
				if (uploadTestDot.range != 0)
					UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 56,
							uploadTestDot.pos + "/" + uploadTestDot.range, true);
			}
		}
		// 计量数值信息
		else if (pushData.calState == CalState.CALCULATE || pushData.calState == CalState.CALCULATE_PASS
				|| pushData.calState == CalState.CALCULATE_FAIL) {
			if (uploadTestDot.programVal != 0)
				UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 40,
						"D:" + String.format("%.3f", uploadTestDot.programVal), true);
			if (uploadTestDot.adc != 0)
				UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 56,
						"A:" + String.format("%.3f", uploadTestDot.adc), true);
			if (uploadTestDot.meterVal != 0)
				UIUtil.drawTextHorCenter(gc, batteryRectangle, batteryRectangle.y + 72,
						"M:" + String.format("%.3f", uploadTestDot.meterVal), true);
		}

	}

	@Override
	public void refreshData(Object obj) {
		// TODO Auto-generated method stub
		
	}

}