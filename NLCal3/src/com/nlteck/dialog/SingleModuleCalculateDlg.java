package com.nlteck.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nlteck.calModel.base.CalCfgManager;
import com.nlteck.calModel.base.CalculateException;
import com.nlteck.calModel.base.I18N;
import com.nlteck.calModel.base.BaseCfgManager.CalculateValidate;
import com.nlteck.calModel.base.DelayConfig.DetailConfig;
import com.nlteck.calModel.model.ABSCalMainBoard;
import com.nlteck.calModel.model.ABSLogicMianBoard;
import com.nlteck.calModel.model.NetworkCalMianBoard;
import com.nlteck.calModel.model.NetworkLogicMianBoard;
import com.nlteck.calModel.model.VirtureCalMainBoard;
import com.nlteck.calModel.model.VirtureLogicMainBoard;
import com.nlteck.calModel.model.VirtureMeter;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.model.ChannelDO;
import com.nlteck.model.TestDot;
import com.nlteck.model.TestDot.TestResult;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.table.CalculateTableViewer;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.device.Meter;
import com.nltecklib.protocol.li.PCWorkform.CalRelayControlDebugData;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;
import com.nltecklib.protocol.power.driver.DriverEnvironment;

public class SingleModuleCalculateDlg extends Dialog {
	private CalBox calBox;
	public CalculateTableViewer tableViewer;
	public List<TestDot> resultDots = new ArrayList<>();
	public ABSCalMainBoard absCalMainBoard;
	public ABSLogicMianBoard absLogicMainBoard;
	public Meter absMeter;
	
	ToolBar toolBar;
	ToolItem connectItem;
	ToolItem moduleIndexItem;
	ToolItem channelIndexItem;
	ToolItem switchItem;
	Menu menuMoudle;
	Menu menuChn;
	Point point;
	public boolean stop;
	private StyledText logText;
	public CalCfgManager calCfgManager=new CalCfgManager();
//	private static final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	public SingleModuleCalculateDlg(Shell parentShell, CalBox calBox) {
		super(parentShell);
		
		this.calBox = calBox;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("单膜片计量调试");
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MODELESS | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		setBlockOnOpen(false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(1200, 900);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1,false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolBar=new ToolBar(container, SWT.HORIZONTAL);
		
		connectItem = new ToolItem(toolBar, SWT.PUSH);
		connectItem.setText("连接网络");
		channelIndexItem=new ToolItem(toolBar, SWT.DROP_DOWN);
		channelIndexItem.setText("选择通道");
		moduleIndexItem=new ToolItem(toolBar, SWT.DROP_DOWN);
		moduleIndexItem.setText("选择膜片");
		switchItem=new ToolItem(toolBar, SWT.PUSH);
		switchItem.setText("开始计量");
		
		connectItem.setImage(Resources.CONNECT_IMAGE);
		channelIndexItem.setImage(Resources.ADD_ITEM_IMAGE);
		moduleIndexItem.setImage(Resources.ADD_PLAN_ITEM_IMAGE);
		switchItem.setImage(Resources.START_CALC_IMAGE);
		
		tableViewer = new CalculateTableViewer(container);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());
		/*
		 * 通道选择menu
		 */
	
		menuChn=new Menu(getShell(),SWT.PUSH);
		for(int i=0;i<8;i++) {
			MenuItem menuItem=new MenuItem(menuChn, SWT.RADIO);
			menuItem.setText("通道:"+(i+1));
			menuItem.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					channelIndexItem.setText(menuItem.getText());
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
		}
		
		/*
		 * 膜片选择menu
		 */
		menuMoudle=new Menu(getShell(),SWT.PUSH);
		for(int i=0;i<8;i++) {
			MenuItem menuItem=new MenuItem(menuMoudle, SWT.RADIO);
			menuItem.setText("膜片:"+(i+1));
			menuItem.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					moduleIndexItem.setText(menuItem.getText());
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
		}
		
		channelIndexItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.detail==SWT.ARROW) {
					Rectangle bound=channelIndexItem.getBounds();
					point =toolBar.toDisplay(bound.x, bound.y+bound.height);
					menuChn.setLocation(point);
					menuChn.setVisible(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		moduleIndexItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.detail==SWT.ARROW) {
					Rectangle bound=moduleIndexItem.getBounds();
					point =toolBar.toDisplay(bound.x, bound.y+bound.height);
					menuMoudle.setLocation(point);
					menuMoudle.setVisible(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						
						tableViewer.refresh();
					}
				});
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS);

		logText = new StyledText(container, SWT.BORDER | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().span(1, 3).hint(1000, 200).applyTo(logText);

		connectItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				try {
					if(connectItem.getText().equals("连接网络")) {
						connectItem.setText("断开连接");
						WorkBench.getBoxService().connectBoxDebug(calBox);
						WorkBench.getBoxService().connectDevice(calBox, calBox.getDevice().getIp(), true);
						
					}else {
						connectItem.setText("连接网络");
						WorkBench.getBoxService().disconnect(calBox);
						WorkBench.getBoxService().connectDevice(calBox, calBox.getDevice().getIp(), false );
					}
				} catch (Exception e1) {
					appendLog(e1.getMessage());
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	
		/*
		 * 开始计量
		 */
		switchItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String btnText=switchItem.getText();
				 
				if(btnText.equals("开始计量")) {
					switchItem.setText("停止计量");
					stop=false;
					try {
						int chnSelectIndex=Integer.parseInt(channelIndexItem.getText().substring(3));
						int moudleSelectIndex=Integer.parseInt(moduleIndexItem.getText().substring(3));
						test(chnSelectIndex,moudleSelectIndex);				
					}catch (Exception ex) {
						MyMsgDlg.openErrorDialog(getShell(), "操作错误", "请先选择通道和膜片");
					}
				}else {
					stop=true;
					switchItem.setText("开始计量");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		// 关闭资源
		container.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				try {
//					WorkBench.getBoxService().connectDevice(calBox, calBox.getDevice().getIp(), false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				WorkBench.getBoxService().disconnect(calBox);

			}

		});
		
		
		getShell().addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// TODO Auto-generated method stub
				stop=true;
			}
		});
		return container;
	}

	public CalBox getCalBox() {
		return calBox;
	}

	public void setCalBox(CalBox calBox) {
		this.calBox = calBox;
	}

	private void isPass(TestDot dot) throws Exception {
		double adcOffSet = 0;
		double meterOffSet = 0;
		switch (dot.getMode()) {
		case CC:
		case DC:

			if (WorkBench.baseCfgManager.calculateValidates.isEmpty()) {

				// 电流偏差从档位取
				meterOffSet = WorkBench.calCfgManager.rangeCurrentPrecisionData.getRanges().stream()
						.filter(x -> x.level == dot.getPrecision()).findAny().get().maxMeterOffset;
				adcOffSet = WorkBench.calCfgManager.rangeCurrentPrecisionData.getRanges().stream()
						.filter(x -> x.level == dot.getPrecision()).findAny().get().maxAdcOffset;

			} else {

				CalculateValidate validate = WorkBench.baseCfgManager.calculateValidates.stream()
						.filter(x -> x.min <= dot.getProgramVal() && x.max > dot.getProgramVal()).findAny().get();

				meterOffSet = validate.meterOffset;
				adcOffSet = validate.adcOffset;
			}
			break;
		case CV:
			// 电压偏差从计量计划取
			meterOffSet = WorkBench.calCfgManager.calculatePlanData.getMaxMeterOffset();
			adcOffSet = WorkBench.calCfgManager.calculatePlanData.getMaxAdcOffset();
			break;
		default:
			break;
		}

		if (Math.abs(dot.getMeterVal() - dot.getProgramVal()) > meterOffSet) {
			throw new CalculateException(
					I18N.getVal(I18N.MeasureActualValOffsetOver, dot.getMeterVal() - dot.getProgramVal()));
		}

		// 处理adc偏差
		 absCalMainBoard.adjustAdcOffset(true, dot, false);
		if (Math.abs(dot.getAdc() - dot.getProgramVal()) > adcOffSet) {
			throw new Exception(I18N.getVal(I18N.MeasureAdcOffsetOver, dot.getAdc() - dot.getProgramVal()));
		}

		if (dot.getMode() == DriverEnvironment.CalMode.CV) {
			if (WorkBench.baseCfgManager.base.calCheckBoard) {// 回检板启用

				// 处理adc偏差
				absCalMainBoard.adjustAdcOffset(false, dot, false);
				if (Math.abs(dot.getCheckAdc() - dot.getProgramVal()) > WorkBench.calCfgManager.calculatePlanData
						.getMaxAdcOffsetCheck()) {

					throw new Exception(
							I18N.getVal(I18N.CheckBoardAdcOffsetOver, dot.getCheckAdc() - dot.getProgramVal()));
				}

			}
			// CV2
			if (!WorkBench.baseCfgManager.base.ignoreCV2) {

				// 处理adc偏差
				absCalMainBoard.adjustAdcOffset(true, dot, true);
				if (Math.abs(dot.checkAdc2 - dot.getProgramVal()) > WorkBench.calCfgManager.calculatePlanData
						.getMaxAdcOffsetCV2()) {

					throw new Exception(I18N.getVal(I18N.MeasureAdcOffsetOver, dot.checkAdc2 - dot.getProgramVal()));
				}

			}
		}

	}

	
	
	private void appendLog(String content) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (logText.isDisposed()) {
					return;
				}
				int startOffset=logText.getText().length();
//				if(content.contentEquals("error")) {
//					System.out.println("========="+content+"=============");
//					logText.append(CommonUtil.formatTime(new Date(), "yyyy/MM/dd HH:mm:ss") + ":" + content + "\n");
//					getHighlightStyle(startOffset, content.length(), false, RED);
//				}
				logText.append(CommonUtil.formatTime(new Date(), "yyyy/MM/dd HH:mm:ss") + ":" + content + "\n");

				logText.setSelection(logText.getText().length());
			}
		});
	}

	private void test(int chnSelectIndex,int moudleSelectIndex){
		
		logText.setText("");
		boolean useVirture = WorkBench.baseCfgManager.virtual.use;
//		boolean useVirture = false;
		stop=false;
			
		if (useVirture) {
			absCalMainBoard = new VirtureCalMainBoard();
			absLogicMainBoard = new VirtureLogicMainBoard();
			absMeter = new VirtureMeter(0);
		} else {
			absCalMainBoard = new NetworkCalMianBoard(calBox);
			absLogicMainBoard = new NetworkLogicMianBoard();
		}
		try {
			WorkBench.getBoxService().configBaseInfo(calBox);
			appendLog("下发校准箱状态");
			WorkBench.getBoxService().changeWorkMode(calBox, CalibrateCoreWorkMode.CAL);
			appendLog("进入校准");
			calBox.getDevice().getChannels().get(chnSelectIndex-1).setSelected(true);
			WorkBench.getBoxService().selectChns(calBox,calBox.getDevice().getLogicBoardList().get(0), true);
			// 下发选中通道
		}catch (Exception e) {
			e.printStackTrace();
			MyMsgDlg.openErrorDialog(new Shell(), "操作失败", "进入校准失败");
			return;
		}
		
		// 所有膜片的计量点
		List<TestDot> measureDots = new ArrayList<>();
		measureDots.addAll(WorkBench.calCfgManager
				.initCalculateMoudle(calBox.getDevice().getChannels().get(chnSelectIndex-1)));

		// 单膜片的计量点
		List<TestDot> singleMeasureDots=new ArrayList<>();
		for(TestDot dot:measureDots) {
			dot.setChnIndex(chnSelectIndex-1);
			if(dot.moduleIndex==moudleSelectIndex) {
				singleMeasureDots.add(dot);
			}
		}
		
		tableViewer.setInput(resultDots);
		
		
		ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
		
		scheduledExecutorService.schedule(new Runnable() {
			public void run() {

				CalRelayControlDebugData relay = new CalRelayControlDebugData();
				relay.setDriverIndex(0);
				relay.setConnected(true);
				try {
					WorkBench.getBoxService().cfgMeterChange(calBox, relay);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				for (TestDot dot : singleMeasureDots) {
					// 获取延时
					DetailConfig detailConfig = WorkBench.calCfgManager.delayConfig.findDelay(dot);
					try {
						
						absCalMainBoard.sendLogicMeasure(dot);
						appendLog("下发驱动板计量点："+"["+dot.getMode()+","+dot.getPole()+","+dot.getProgramVal()+"]");
						absCalMainBoard.sendCalMeasure(dot);
						appendLog("下发校准板板计量点："+"["+dot.getMode()+","+dot.getPole()+","+dot.getProgramVal()+"]");
						// 打开膜片
						absCalMainBoard.switchModule(calBox.getDevice().getCalBoxList(),
								dot.getChnIndex(), true);
						absCalMainBoard.TestSleep(detailConfig.programSetDelay);
						appendLog("程控设置延时："+detailConfig.programSetDelay);
						
						absCalMainBoard.TestSleep(detailConfig.moduleOpenDelay);
						appendLog("打开膜片延时："+detailConfig.moduleOpenDelay);
						
						// 获取表值
						absCalMainBoard.gatherMeter(dot,detailConfig);
						appendLog("万用表值："+dot.getMeterVal());
						// 读取计量点
						absLogicMainBoard.gatherCalculateADCmoudle(dot);
						appendLog("读取的计量点："+dot.getAdc());
						
						isPass(dot);

					} catch (Exception ex) {
						dot.testResult = TestResult.Fail;
						ex.printStackTrace();
						appendLog(ex.getMessage());
						try {
							absCalMainBoard.switchModule(calBox.getDevice().getCalBoxList(),
									dot.getChnIndex(), false);
						} catch (Exception e) {
							appendLog("关闭膜片失败");
						}
						appendLog("关闭膜片");
						return;
					}

					try {
						absCalMainBoard.switchModule(calBox.getDevice().getCalBoxList(),
								dot.getChnIndex(), false);
						absCalMainBoard.TestSleep(detailConfig.moduleCloseDelay);
						appendLog("关闭膜片延时："+detailConfig.moduleCloseDelay);
					} catch (Exception e1) {
						appendLog("关闭膜片失败");
					}
					dot.testResult = TestResult.Success;
					resultDots.add(dot);
					
					if(stop) {
						break;
					}
				}
				appendLog("计量通过");
				switchItem.setText("开始计量");
				relay.setConnected(true);
				try {
					WorkBench.getBoxService().cfgMeterChange(calBox, relay);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, 0, TimeUnit.MINUTES);
	
	}
	
	private StyleRange getHighlightStyle(int startOffset, int length, boolean bold, Color color) {

		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.fontStyle = bold ? SWT.BOLD : SWT.NORMAL;
		styleRange.foreground = color;

		return styleRange;
	}
	
}
