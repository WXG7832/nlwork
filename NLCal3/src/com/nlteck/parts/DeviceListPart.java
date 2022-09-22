package com.nlteck.parts;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.nlteck.E4LifeCycle;
import com.nlteck.dialog.BoxInfoDlg;
import com.nlteck.dialog.BoxListDlg;
import com.nlteck.dialog.DeviceInfoDlg;
import com.nlteck.dialog.MeasureReportDlg;
import com.nlteck.dialog.SingleModuleCalculateDlg;
import com.nlteck.dialog.UpdateDialog;
import com.nlteck.dialog.configDialog.ConfigDialog;
import com.nlteck.dialog.configDialog.ProductChangeDialog;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Device;
import com.nlteck.firmware.LogicBoard;
import com.nlteck.firmware.WorkBench;
import com.nlteck.parts.uiComponent.TreeNode;
import com.nlteck.parts.uiComponent.TreeNodeDataProvider;
import com.nlteck.parts.uiComponent.TreeNodeLabelProvider;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.swtlib.tools.UITools;
import com.nltecklib.protocol.li.PCWorkform.ModeSwitchData.CalibrateCoreWorkMode;

public class DeviceListPart {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
	public final static String ID = "nlcal.part.devicelist";
	private boolean isConnect;
	private TreeViewer treeViewer;
	public static DeviceListPart deviceListPart;
	private Menu  popMenu; //菜单
	
	

	@PostConstruct
	public void postConstruct(Composite parent) {
		
		
		initUI(parent);
		deviceListPart = this;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * 初始化界面
	 * 
	 * @param parent
	 */
	private void initUI(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new TreeNodeDataProvider());
		treeViewer.setLabelProvider(new TreeNodeLabelProvider());
		treeViewer.expandToLevel(1);
		treeViewer.getTree().setCursor(Resources.handCursor);
		
		TreeNode devicesNode = new TreeNode(null, "设备", null);
		TreeNode boxesNode = new TreeNode(null, "校准箱", null);
		createDeviceTreeNodes(devicesNode);
		createCalboxNodes(boxesNode);
		
		List<TreeNode> list = new ArrayList<>();
		list.add(devicesNode);
		list.add(boxesNode);
		treeViewer.setInput(list);
		treeViewer.expandToLevel(2);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				
				
				
			}
			
			
			
		});
		
		initMenu();
		
		treeViewer.getTree().addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
				//双击进入
				StructuredSelection select = (StructuredSelection) treeViewer.getStructuredSelection();
				TreeNode selectEntity = (TreeNode) select.getFirstElement();
				if (selectEntity != null && selectEntity.getContent() != null) {
					
					//进入页面
					if(selectEntity.getContent() instanceof LogicBoard) {
						
						LogicBoard logicboard = (LogicBoard)selectEntity.getContent();
						//进入分区校准页面
						MPart part = E4LifeCycle.showOrCreatePart(LogicConsolePart.ID,
								logicboard.getDevice().getName() + ":" +selectEntity.getTitle());
						((LogicConsolePart)part.getObject()).refreshData(logicboard);
						
						
						
					} else if(selectEntity.getContent() instanceof Device) {
						
						Device  device = (Device)selectEntity.getContent();
						DeviceInfoDlg  dlg = new DeviceInfoDlg(Display.getDefault().getActiveShell(), device);
						dlg.create();
						UITools.centerScreen(dlg.getShell());
						if(Window.OK == dlg.open()) {
							
							treeViewer.refresh();
						}
					} else if(selectEntity.getContent() instanceof CalBox) {
						
						CalBox  calBox = (CalBox)selectEntity.getContent();
						BoxInfoDlg  dlg = new BoxInfoDlg(Display.getDefault().getActiveShell(), calBox);
						dlg.create();
						UITools.centerScreen(dlg.getShell());
						if(Window.OK == dlg.open()) {
							
							treeViewer.refresh();
						}
						
						
					}
					
				}
				
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				
				
				if(e.button == 3) {
					
					
					changePopMenuState();
					
				}
				
			}
			
			
		});

	}
	
	
	public void createCalboxNodes(TreeNode root) {
		
		
		List<TreeNode> treeNodeList = new ArrayList<>();
		for (int n = 0; n < WorkBench.calBoxList.size(); n++) {
			
			TreeNode node = createBoxTreeNode(root , WorkBench.calBoxList.get(n));
			treeNodeList.add(node);
			
		}
	}
	
	public void createDeviceTreeNodes(TreeNode root) {
		
		List<TreeNode> treeNodeList = new ArrayList<>();
		for (int n = 0; n < WorkBench.deviceList.size(); n++) {
			
			TreeNode node = createDeviceTreeNode(root , WorkBench.deviceList.get(n));
			treeNodeList.add(node);
			
		}
		
		
	}
	
	/**
	 * 创建校准箱节点
	 * @author  wavy_zheng
	 * 2021年1月18日
	 * @param parent
	 * @param box
	 * @return
	 */
	private TreeNode createBoxTreeNode(TreeNode parent , CalBox box) {
		
		TreeNode boxNode = new TreeNode(box, box.getName(), parent);
		return boxNode;
	}
	
	
	private TreeNode createDeviceTreeNode(TreeNode parent , Device device) {
		
		   
		  TreeNode deviceNode = new TreeNode(device, device.getName(), parent);
		  List<LogicBoard> boards = new ArrayList<>();
		  for(int n = 0 ; n < device.getLogicNum() ; n++) {
			  
			  
			  LogicBoard  lb = new LogicBoard(device, n);
			  TreeNode node = new TreeNode(lb, "分区" + (n + 1), deviceNode);
			  boards.add(lb);
		  }
		  device.setLogicBoardList(boards);
		  
		  
		  for(CalBox box : device.getCalBoxList()) {
			  
			  TreeNode node = new TreeNode(box, box.getName(), deviceNode);
			  
		  }
		  
		  return deviceNode;
		
		
	}
	


	/**
	 * 点击校准箱时的菜单
	 */
	private void createMenuForCalBox() {
		// 先销毁
		if (treeViewer.getTree().getMenu() != null)
			treeViewer.getTree().getMenu().dispose();

		Menu menu = new Menu(treeViewer.getTree());
		treeViewer.getTree().setMenu(menu);

		menu.setVisible(true);

		CalBox calBox = (CalBox) ((TreeNode) treeViewer.getStructuredSelection().getFirstElement()).getContent();

		MenuItem boxInfoItem = new MenuItem(menu, SWT.PUSH);
		boxInfoItem.setText("校准箱信息");

		MenuItem connectBoxItem = new MenuItem(menu, SWT.PUSH);
		if (calBox.getConnector().isConnected())
			connectBoxItem.setText("断开校准箱");
		else
			connectBoxItem.setText("连接校准箱");

		MenuItem boxUpdateItem = new MenuItem(menu, SWT.PUSH);
		boxUpdateItem.setText("校准箱升级");

		// **************************************************************************************************
		// *
		// *监听器
		// *
		// **************************************************************************************************
		

		connectBoxItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (calBox.getConnector().isConnected()) {
//					calBox.stopHeatBeat();
					calBox.getConnector().disconnect();
					MessageDialog.openInformation(menu.getShell(), "操作成功", calBox.getName() + "网络断开成功！");
				} else {
					treeViewer.getTree().setCursor(Resources.busyCursor);
					menu.getShell().setCursor(Resources.busyCursor);
					if (calBox.connect()) {
						//calBox.heartBeatService();
						treeViewer.getTree().setCursor(Resources.handCursor);
						menu.getShell().setCursor(Resources.normalCursor);
						MessageDialog.openInformation(menu.getShell(), "操作成功", calBox.getName() + "网络连接成功！");
					} else {
						treeViewer.getTree().setCursor(Resources.handCursor);
						menu.getShell().setCursor(Resources.normalCursor);
						MessageDialog.openError(menu.getShell(), "操作失败", calBox.getName() + "网络连接失败！");
					}
				}
				// 通知其它界面按钮更新
				if (LogicConsolePart.logicConsolePart != null)
					LogicConsolePart.logicConsolePart.setToolItemState();
				if (DriverConsolePart.driverConsolePart != null)
					DriverConsolePart.driverConsolePart.setToolItemState();
			}
		});

		boxUpdateItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!calBox.getConnector().isConnected()) {
					MessageDialog.openError(menu.getShell(), "错误", "校准箱未连接");
					return;
				}
				if (calBox.getWorkMode() != CalibrateCoreWorkMode.NONE) {
					MessageDialog.openError(menu.getShell(), "错误", "校准箱不在无工作模式");
					return;
				}
				UpdateDialog dialog = new UpdateDialog(menu.getShell());
				dialog.open(calBox);
			}
		});

	}
	/**
	 * 更新菜单状态
	 * @author  wavy_zheng
	 * 2021年1月21日
	 */
	private void changePopMenuState() {
		
		TreeNode selectObj = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
		
		for(int n = 0 ; n < popMenu.getItemCount() ; n++) {
			
			popMenu.getItem(n).setEnabled(false);
		}
		popMenu.getItem(0).setEnabled(true);
		popMenu.getItem(2).setEnabled(true);
		if(selectObj != null) {
			
			if(selectObj.getContent() instanceof Device) {
				
				popMenu.getItem(0).setEnabled(true);
				popMenu.getItem(1).setEnabled(true);
				
				popMenu.getItem(2).setEnabled(true);
				popMenu.getItem(4).setEnabled(true);
				popMenu.getItem(8).setEnabled(true);
			} else if(selectObj.getContent() instanceof CalBox) {
				
				
				popMenu.getItem(2).setEnabled(true);
				popMenu.getItem(3).setEnabled(true);
				
				if(selectObj.getParent().getContent() instanceof Device) {
				   popMenu.getItem(5).setEnabled(true);
				}
				popMenu.getItem(6).setEnabled(true);
				popMenu.getItem(7).setEnabled(true);
				popMenu.getItem(9).setEnabled(true);
			}
			
		}
		
	}
	
	
	private void initMenu() {
		
		popMenu = new Menu(treeViewer.getTree());
		MenuItem createDeviceItem = new MenuItem(popMenu,SWT.PUSH);
		createDeviceItem.setText("创建设备");
		createDeviceItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DeviceInfoDlg dlg = new DeviceInfoDlg(Display.getCurrent().getActiveShell(),null);
				dlg.create();
				UITools.centerScreen(dlg.getShell());
				if(Window.OK == dlg.open()) {
					
					List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
					createDeviceTreeNode(nodes.get(0),dlg.getDevice());
					treeViewer.refresh();
				}
				
				
				
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
				
			}
			
			
			
		});
		
		MenuItem removeDeviceItem = new MenuItem(popMenu,SWT.PUSH);
		removeDeviceItem.setText("删除设备");
		removeDeviceItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeNode selectNode = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				if(selectNode != null && selectNode.getContent() instanceof Device) {
					
					Device device = (Device)selectNode.getContent();
					boolean yes = MyMsgDlg.openConfirmDialog(Display.getCurrent().getActiveShell(), "操作确认", "确认要删除设备" + device.getName() + "?");
					if(yes) {
						
						//检测设备是否已经测试过?
						try {
							boolean tested = WorkBench.getDatabaseManager().isDeviceTested(device);
							if(tested) {
								
								MyMsgDlg.openErrorDialog(Display.getCurrent().getActiveShell(), "操作错误", "该设备" + device.getName() + "已经产生了计量数据无法删除!");
								return;
							}
							
							//
							WorkBench.getDatabaseManager().removeDevice(device);
							
							selectNode.getParent().getChildren().remove(selectNode);
							
							treeViewer.refresh();
							
							MyMsgDlg.openInfoDialog(Display.getCurrent().getActiveShell(), "操作成功", "删除设备" + device.getName() + "成功!", false);
							
						} catch (SQLException e1) {
							
							e1.printStackTrace();
							MyMsgDlg.openErrorDialog(Display.getCurrent().getActiveShell(), "操作错误", e1.getMessage());
						}
						
						
					}
					
				}
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			
		});
		
		
		MenuItem createBoxItem = new MenuItem(popMenu,SWT.PUSH);
		createBoxItem.setText("创建校准箱");
		createBoxItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				 BoxInfoDlg  bid = new BoxInfoDlg(Display.getDefault().getActiveShell(), null);
				 bid.create();
				 UITools.centerScreen(bid.getShell());
				 if(Window.OK == bid.open()) {
					 
					    List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
					    TreeNode node =  createBoxTreeNode(nodes.get(1),bid.getCalbox());
						treeViewer.refresh();
				 }
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			
		});
		
		MenuItem removeBoxItem = new MenuItem(popMenu,SWT.PUSH);
		removeBoxItem.setText("删除校准箱");
		removeBoxItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeNode selectNode = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				if(selectNode == null) {
					
					return;
				}
				CalBox box = (CalBox)selectNode.getContent();
				if(box.getDevice() != null ) {
					
					MyMsgDlg.openErrorDialog(Display.getCurrent().getActiveShell(), "操作失败", "请先从设备上解绑校准箱");
					return;
				}
				
				if(!MyMsgDlg.openConfirmDialog(Display.getCurrent().getActiveShell(), "删除确认", "确定要删除校准箱" + box.getName() +"吗?")) {
					
					return;
				}
				
				try {
					WorkBench.getDatabaseManager().removeCalbox(box);
					
					selectNode.getParent().getChildren().remove(selectNode);
					
					treeViewer.refresh();
				} catch (Exception e1) {
					
					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(Display.getCurrent().getActiveShell(), "删除失败", e1.getMessage());
					return;
				}
				
				MyMsgDlg.openInfoDialog(Display.getCurrent().getActiveShell(), "删除成功", "删除校准箱" + box.getName() + "成功!", false);
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		
		
		MenuItem bindBoxItem = new MenuItem(popMenu,SWT.PUSH);
		bindBoxItem.setText("绑定校准箱");
		bindBoxItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				TreeNode deviceNode  = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				  if(deviceNode != null) {
					  
					  BoxListDlg  dlg = new BoxListDlg(Display.getDefault().getActiveShell(), (Device)deviceNode.getContent());
					  dlg.create();
					  UITools.centerScreen(dlg.getShell());
					  if(Window.OK == dlg.open()) {
						  
						  //移动节点
						  TreeNode node = findBoxNodeBy(dlg.getBindBox());
						  if(node != null) {
							  
							  node.getParent().removeChild(dlg.getBindBox());
							  deviceNode.addChild(dlg.getBindBox(), dlg.getBindBox().getName());
							  treeViewer.refresh();
							  
						  }
						  
					  }
					  
				  }
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		
		
		MenuItem unbindBoxItem = new MenuItem(popMenu,SWT.PUSH);
		unbindBoxItem.setText("解绑校准箱");
		unbindBoxItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				TreeNode boxNode  = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				if(boxNode != null) {
					
					CalBox box = (CalBox)boxNode.getContent();
					
					try {
						WorkBench.getDatabaseManager().unbindBox(box);
	                    
						boxNode.getParent().removeChild(box);
						box.getDevice().unbindCalbox(box);
						appendUnbindBoxList(box);
						
						WorkBench.calBoxList.add(box);
						
						
						
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "操作失败", e1.getMessage());
						return;
					}
					
					
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
			
			
			
			
		});
		
		
		// xingguo_wang
		MenuItem oneModuleCalculateBoxItem = new MenuItem(popMenu, SWT.PUSH);
		oneModuleCalculateBoxItem.setText("单膜片计量");
		oneModuleCalculateBoxItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeNode boxNode  = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				if(boxNode != null) {
					
					CalBox box = (CalBox)boxNode.getContent();
					if(box.getDevice()==null) {
						MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), "message", "请先绑定校准箱");
						return;
					}
					SingleModuleCalculateDlg dlg = new SingleModuleCalculateDlg(Display.getDefault().getActiveShell(),box);
					dlg.create();
					UITools.centerScreen(dlg.getShell());
					 if(Window.OK == dlg.open()) {
						 
						    List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
						    TreeNode node =  createBoxTreeNode(nodes.get(1),dlg.getCalBox());
							treeViewer.refresh();
					 }
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		MenuItem changeProductBoxItem = new MenuItem(popMenu,SWT.PUSH);
		changeProductBoxItem.setText("切换产品");
		changeProductBoxItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				TreeNode boxNode  = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				if(boxNode != null) {
					
					CalBox box = (CalBox)boxNode.getContent();
					
					ProductChangeDialog changeProductDlg=new ProductChangeDialog(Display.getDefault().getActiveShell(),box);
				
					 changeProductDlg.create();
					 UITools.centerScreen(changeProductDlg.getShell());
					 if(Window.OK == changeProductDlg.open()) {
							treeViewer.refresh();
					 }
					
					
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
			
		});
		/*
		 * 计量报表（measure）
		 */
		if(true) {			
			MenuItem measureBoxItem = new MenuItem(popMenu,SWT.PUSH);
			measureBoxItem.setText("导出计量报表");
			measureBoxItem.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					
					TreeNode boxNode  = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
					if(boxNode != null) {
						
						Device device = (Device)boxNode.getContent();
						
						MeasureReportDlg measureDlg=new MeasureReportDlg(Display.getDefault().getActiveShell(),device);
						
						measureDlg.create();
						UITools.centerScreen(measureDlg.getShell());
						if(Window.OK == measureDlg.open()) {
							
							List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
							
							treeViewer.refresh();
						}
						
						
					}
					
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					
					
				}
				
			});
		}
		// 校准车参数
		MenuItem configPluginItem = new MenuItem(popMenu,SWT.PUSH);
		configPluginItem.setText("校准车参数");
		configPluginItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				TreeNode boxNode  = (TreeNode)treeViewer.getStructuredSelection().getFirstElement();
				if(boxNode != null) {
					CalBox calBox=(CalBox)boxNode.getContent();
					ConfigDialog configDialog=new ConfigDialog(Display.getDefault().getActiveShell(), calBox.getIp(), "zh-CN", "UTF8");
					configDialog.open();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}
			
		});

		treeViewer.getTree().setMenu(popMenu);
		
	}
	
	
	/**
	 * 找匹配的节点
	 * @author  wavy_zheng
	 * 2021年1月18日
	 * @param box
	 * @return
	 */
	private TreeNode findBoxNodeBy(CalBox box) {
		
		List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
		TreeNode node = nodes.get(1);
		
		return node.findChildByContent(box);
		
		
		
	}
	
    private TreeNode findDeviceNodeBy(Device device) {
		
		List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
		TreeNode node = nodes.get(0);
		
		int index = node.getChildren().indexOf(device);
		if(index == -1) {
			
			return null;
		}
		
		return node.getChildren().get(index);
		
		
	}
    
    
    private void appendUnbindBoxList(CalBox box) {
    	
    	
    	List<TreeNode> nodes = (List<TreeNode>)treeViewer.getInput();
		TreeNode node = nodes.get(1);
		node.addChild(box, box.getName());
		treeViewer.refresh();

    }
    
    
    public void refreshTree() {
    	
    	treeViewer.refresh();
    }
	

}