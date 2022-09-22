package com.nlteck.parts.uiComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.mysql.fabric.xmlrpc.base.Array;
import com.nlteck.dialog.DebugInfoDlg;
import com.nlteck.dialog.MeasureWindow;
import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.DriverBoard;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.controls.BaseDisplay;
import com.nlteck.swtlib.controls.BaseDisplay.OnClickListener;
import com.nlteck.swtlib.controls.BaseDisplay.OnMouseActionListener;
import com.nlteck.swtlib.controls.Battery;
import com.nlteck.swtlib.controls.ButtonEx;
import com.nlteck.swtlib.controls.ButtonEx.BtnStyle;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.swtlib.tools.UITools;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年1月17日 下午9:28:26 电池界面面板
 */
public class LogicBatteryComposite extends Composite {

	private static final int GROUP_SPACE = 40;
	private static final int LEAD_HOR_SPACE = 40; // 驱动板标签宽度
	private static final int LEAD_VERT_SPACE = 40; // 驱动板标签高度
	private static final int CELL_SPACE = 5; // 电池间距
	
	private static final int MAX_CELL_WIDTH = 160; //最大电池宽度

	private boolean ctrlPress; // ctrl和鼠标同时按下
	private boolean shiftPress; // shift和鼠标同时按下
	private int batSelectStartIndex = -1; // 路径开始

	private LogicBoard logicBoard;

	private List<Battery> batteries = new ArrayList<>();
	private List<ButtonEx> driverFlags = new ArrayList<>();

	private int driverChnCount;

	private List<BatteryClickListener> listeners = new ArrayList<>();

	public interface BatteryClickListener {

		public void onBatteryClick(int index);
	}

	public LogicBatteryComposite(Composite parent, int style) {
		super(parent, style);

	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {

		return computeSize(wHint, hHint, batteries.size());

	}

	/**
	 * 计算面板大小
	 * 
	 * @author wavy_zheng 2021年1月12日
	 * @param width
	 * @param height
	 * @return
	 */
	private Point computeSize(int width, int height, int batCount) {

		GridLayout layout = (GridLayout) getLayout();
		int w = 0, h = 0;
		System.out.println("width=" + width + ",height=" + height);
		if (width == 0 || height == 0) {

			return new Point(w, h);
		}
		int cellWidth = 0, cellHeight = 0;
		
		int column = 0;
		int rowCount = 0;
		boolean group = false;
		driverChnCount = logicBoard.getDevice().getChnNumInDriver();
		int driverCount = logicBoard.getDevice().getDriverNumInLogic();
		if(driverChnCount <= 8) {
		   column = driverChnCount * 2 + 2;
		   group = true;
		   rowCount = driverCount % 2 == 0 ? driverCount / 2 : driverCount / 2 + 1 ;
		} else {
			
			column = driverChnCount + 1;
			rowCount = driverCount;
		}
		
		

		w += LEAD_HOR_SPACE * (group ? 2 : 1)  + GROUP_SPACE * (group ? 1: 0);

		cellWidth = (width - LEAD_HOR_SPACE * (group ? 2 : 1) - GROUP_SPACE * (group ? 1: 0)) / (group ? driverChnCount * 2 : driverChnCount)
				- CELL_SPACE;
		if (cellWidth < Battery.minCellWidth) {

			cellWidth = Battery.minCellWidth;
		} else if(cellWidth > MAX_CELL_WIDTH) {
			
			cellWidth = MAX_CELL_WIDTH;
		}
		

		cellHeight = (int) (cellWidth * 1.5);

		System.out.println("cellWidth=" + cellWidth + ",cellHeight=" + cellHeight);

		System.out.println("rowCount = " + rowCount);

		w += (cellWidth + CELL_SPACE) * (group ? driverChnCount * 2 : driverChnCount);
		h += (cellHeight + CELL_SPACE) * rowCount;

		if (w < width) {

			w = width;
		}
		if (h < height) {

			h = height;
		}

		return new Point(w, h);
	}

	public void refreshData(LogicBoard lb) {

		if (this.logicBoard == lb) {

			// 同一个窗口不需要切换
			System.out.println("same window !!");
			return;
		}

		// 驱动板数和通道数一致无需重新创建控件
		if (this.logicBoard == null
				|| (lb.getDevice().getChnNumInDriver() != this.logicBoard.getDevice().getChnNumInDriver())
				|| lb.getDevice().getDriverNumInLogic() != this.logicBoard.getDevice().getDriverNumInLogic()) {

			this.logicBoard = lb;

			System.out.println(logicBoard.getLogicIndex() + " enter part");

			for (Battery bat : batteries) {

				bat.dispose();
			}
			batteries.clear();

			for (ButtonEx flag : driverFlags) {

				flag.dispose();
			}
			driverFlags.clear();

			createBatPanel(this, logicBoard.getDevice().getDriverNumInLogic(),
					logicBoard.getDevice().getChnNumInDriver());

			for (int n = 0; n < driverFlags.size(); n++) {

				final int index = n;
				driverFlags.get(n).addClickListener(new OnClickListener() {

					@Override
					public void onClick(BaseDisplay display) {

						int driverChnCount = logicBoard.getDevice().getChnNumInDriver();
						for (int i = driverChnCount * index; i < driverChnCount * (index + 1); i++) {

							boolean press = batteries.get(i).isMousePress();
							logicBoard.getChannels().get(i).setSelected(!press);
							batteries.get(i).setMousePress(!press);

						}
						triggerListeners(index);

					}

				});

			}

			// 右键菜单
			boolean ok = true;
			if (logicBoard.getDevice().getCalBoxList().isEmpty()) {

				ok = false;
			} else {
				for (CalBox calbox : logicBoard.getDevice().getCalBoxList()) {

					if (calbox.getCalBoardCount() == 0) {

						ok = false;
						break;
					}
				}
			}
			if (ok) {

				createPopMenus(true);
			}

		} else {

			this.logicBoard = lb;
		}

		// 刷新通道序号
		for (int n = 0; n < batteries.size(); n++) {

			Battery bat = batteries.get(n);
			int deviceIndex = this.logicBoard.getChannels().get(n).getDeviceChnIndex();
			bat.setCornerFlag(4, deviceIndex + 1 + "");

		}

		// 刷新板号
		for (int n = 0; n < logicBoard.getDrivers().size(); n++) {

			DriverBoard db = logicBoard.getDrivers().get(n);
			driverFlags.get(n).setText("#" + (db.getDriverIndexInDevice() + 1));

		}

	}

	public void relayoutBatteries() {

		Rectangle region = getClientArea();
		GridLayout layout = (GridLayout) getLayout();

		int count = layout.numColumns == 18 ? 2 : 1;

		int cellWidth = (region.width - LEAD_HOR_SPACE * count) / layout.numColumns;

		if (cellWidth < Battery.minCellWidth) {

			cellWidth = Battery.minCellWidth;
		} else if(cellWidth > MAX_CELL_WIDTH) {
			
			cellWidth = MAX_CELL_WIDTH;
		}
		for (int n = 0; n < batteries.size(); n++) {
			GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).hint(cellWidth, (int) (cellWidth * 1.5))
					.applyTo(batteries.get(n));
		}

	}

	private void createBatPanel(Composite parent,  int driverCount, int driverChnCount) {

		int column = 0;
		// int leadSpace = 0;
		int rowCount = 0;
		
		//column = 16 + 16 / driverChnCount;
		
		boolean group = false;
		if(driverChnCount <= 8) {
		   column = driverChnCount * 2 + 2;
		   group = true;
		   rowCount = driverCount % 2 == 0 ? driverCount / 2 : driverCount / 2 + 1 ;
		} else {
			
			column = driverChnCount + 1;
			rowCount = driverCount;
		}
		
        
		

		GridLayoutFactory.fillDefaults().numColumns(column).equalWidth(false).spacing(CELL_SPACE, CELL_SPACE)
				.applyTo(parent);

		Map<Integer, List<ButtonEx>> groupFlags = new HashMap<>();
		Map<Integer, List<Battery>> groupBatteries = new HashMap<>();

		for (int g = 0; g < 18 / driverChnCount; g++) {
			groupFlags.put(g, new ArrayList<>());
			groupBatteries.put(g, new ArrayList<>());
		}

		for (int n = 0; n < rowCount; n++) {
			for (int g = 0; g < 2 ; g++) {
                 
				if(g * rowCount + n >= driverCount ) {
					
					break;
				}
				
				ButtonEx label = new ButtonEx(parent, BtnStyle.FLAT);
				label.setForeground(Resources.BLUE_CLR);
				
				DriverBoard db = logicBoard.getDrivers().get(g * rowCount + n);
				System.out.println("#" + (db.getDriverIndexInDevice() + 1));
				label.setText("#" + (db.getDriverIndexInDevice() + 1));
				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).hint(LEAD_HOR_SPACE, LEAD_VERT_SPACE)
						.applyTo(label);
				 groupFlags.get(g).add(label);

				for (int i = 0; i < driverChnCount; i++) {

					Battery bat = new Battery(parent);
					bat.setBackColor(parent.getBackground());
					bat.setBatColor(Resources.NONE_CLR);
					ChannelDO chn = db.getChannels().get(i);
					// bat.setData(db.getChannels().get(i));
					// bat.setCornerFlag(4, chn.getDeviceChnIndex() + 1 + "");
					 groupBatteries.get(g).add(bat);
					GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER)
							.hint(Battery.minCellWidth, Battery.minCellHeight).applyTo(bat);

				}

			}


		}
		 groupFlags.entrySet().stream().forEach(x ->
		 driverFlags.addAll(x.getValue()));
		 groupBatteries.entrySet().stream().forEach(x ->
		 batteries.addAll(x.getValue()));

		 processBatterySelections();

	}

	/**
	 * 处理电池选择事件
	 * 
	 * @author wavy_zheng 2021年1月12日
	 */
	private void processBatterySelections() {

		for (int n = 0; n < batteries.size(); n++) {

			Battery bat = batteries.get(n);

			final int index = n;
			bat.addMouseActionListener(new OnMouseActionListener() {

				@Override
				public void onMouseUp(BaseDisplay display, int button) {

					/**
					 * 注意，当前按钮状态是自动变更的
					 */

					if (button == 1) {
						if (!ctrlPress & !shiftPress) {

							resetAllBatteries(bat);
							batSelectStartIndex = index;

						} else if (shiftPress) {

							if (batSelectStartIndex != -1) {
								System.out.println(batSelectStartIndex + "->" + index);
								// 计算路径
								int min = batSelectStartIndex < index ? batSelectStartIndex : index;
								int max = batSelectStartIndex > index ? batSelectStartIndex : index;

								for (int i = min; i < max; i++) {

									if (batteries.get(i) != bat) {
										batteries.get(i).setMousePress(bat.isMousePress());
										logicBoard.getChannels().get(i).setSelected(bat.isMousePress());
										batteries.get(i).redraw();
									}
								}
							} else {

								batSelectStartIndex = index;

							}

						}

						logicBoard.getChannels().get(index).setSelected(bat.isMousePress());
						ctrlPress = false;
						shiftPress = false;

						triggerListeners(index);

					} else if (button == 3) {

						DebugInfoDlg dlg = new DebugInfoDlg(getShell(), logicBoard , logicBoard.getChannels().get(index));
			
						
						dlg.create();
						
						UITools.centerScreen(dlg.getShell());
						dlg.open();

					}

				}

				@Override
				public void onMouseDown(BaseDisplay display, int button, int stateMask) {

					if (button == 1) {
						if ((stateMask & SWT.CTRL) == SWT.CTRL) {

							ctrlPress = true;

						} else if ((stateMask & SWT.SHIFT) == SWT.SHIFT) {

							shiftPress = true;

						}

					}

				}

				@Override
				public void onMouseMove(BaseDisplay display) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMouseEnter(BaseDisplay display) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMouseLeave(BaseDisplay display) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDoubleClick(BaseDisplay display) {

					MeasureWindow window = new MeasureWindow(display.getShell(), logicBoard.getChannels().get(index));

				}

			});
		}

	}

	/**
	 * 复位所有选中按钮
	 * 
	 * @author wavy_zheng 2021年2月19日
	 * @param batPress
	 *            排除按下的通道,null时表示不排除任何通道
	 */
	public void resetAllBatteries(Battery batPress) {

		for (int n = 0; n < logicBoard.getChannels().size(); n++) {

			Battery bat = batteries.get(n);
			if (batPress == null || bat != batPress) {
				bat.setMousePress(false);
				logicBoard.getChannels().get(n).setSelected(false);

			}
		}
	}

	private int findSelectBat() {

		for (int n = 0; n < batteries.size(); n++) {

			if (batteries.get(n).isMousePress()) {

				return n;
			}
		}

		return -1;
	}

	public LogicBoard getLogicBoard() {
		return logicBoard;
	}

	public List<ButtonEx> getDriverFlags() {
		return driverFlags;
	}

	public List<Battery> getBatteries() {
		return batteries;
	}

	public void redraw() {

		for (Battery bat : batteries) {

			bat.redraw();
		}

	}

	/**
	 * 驱动板标签添加事件
	 * 
	 * @author wavy_zheng 2021年1月19日
	 * @param driverIndex
	 * @param listener
	 */
	public void addDriverFlagListener(int driverIndex, OnClickListener listener) {

		driverFlags.get(driverIndex).addClickListener(listener);

	}

	public void createPopMenus(boolean create) {

		for (int n = 0; n < driverFlags.size(); n++) {

			final int index = n;
			if (create) {

				Menu menu = new Menu(driverFlags.get(n));

				// 动态生成菜单项
				for (int m = 0; m < logicBoard.getDevice().getCalBoxList().size(); m++) {

					CalBox box = logicBoard.getDevice().getCalBoxList().get(m);

					for (int j = 0; j < box.getCalBoardCount(); j++) {

						final int itemIndex = j;
						MenuItem item = new MenuItem(menu, SWT.PUSH);
						item.setEnabled(box.getCalBoardList().get(j).isOpen()); // 是否禁用
						item.setText(box.getName() + "_" + (j + 1));
						item.addSelectionListener(new SelectionListener() {

							@Override
							public void widgetSelected(SelectionEvent e) {

								try {
									WorkBench.getBoxService().bindCalboardToDriver(box, logicBoard.getLogicIndex(),
											index, itemIndex, true);
									System.out.println("bind ok");
									DriverBoard db = logicBoard.getDrivers().get(index);
									CalBoard cb = box.getCalBoardList().get(itemIndex);
									if (cb.getDbBind() != null) {

										if (logicBoard.getDrivers().contains(cb.getDbBind())) {

											// 删除
											ButtonEx flag = driverFlags.get(cb.getDbBind().getDriverIndex());
											flag.setText("#" + (cb.getDbBind().getDriverIndexInDevice() + 1));
										}
									}
									db.bind(cb);
									driverFlags.get(index).setText("#" + (db.getDriverIndexInDevice() + 1) + "\n"
											+ cb.getCalBox().getName() + "_" + (cb.getIndex() + 1));

								} catch (Exception e1) {

									e1.printStackTrace();
									MyMsgDlg.openErrorDialog(getShell(), "操作失败", "绑定失败:" + e1.getMessage());
									return;
								}

							}

							@Override
							public void widgetDefaultSelected(SelectionEvent e) {
								// TODO Auto-generated method stub

							}

						});

					}

				}
				driverFlags.get(n).setMenu(menu);

			} else {

				if (driverFlags.get(n).getMenu() != null) {

					driverFlags.get(n).getMenu().dispose();
				}

			}

		}
	}

	public void addBatteryClickListener(BatteryClickListener listener) {

		listeners.add(listener);
	}

	public void removeBatteryClickListener(BatteryClickListener listener) {

		listeners.remove(listener);
	}

	public void triggerListeners(int n) {

		for (BatteryClickListener listener : listeners) {

			listener.onBatteryClick(n);
		}
	}

}
