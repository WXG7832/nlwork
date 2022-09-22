package com.nlteck.dialog.configDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.nlteck.firmware.CalBox;
import com.nlteck.swtlib.controls.ButtonEx;
import com.nlteck.swtlib.controls.ButtonEx.BtnStyle;
import com.nlteck.swtlib.progress.ProgressBarEx;
import com.nlteck.swtlib.progress.ShowProcessDialog;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.utils.CommonUtil;
import com.nlteck.utils.TelnetOperateUtil;

public class ProductChangeDialog extends Dialog {

	TelnetOperateUtil util = new TelnetOperateUtil();
	private CalBox calBox;
	public String productDir = "product"; //将目标文件放在product目录下
	public int count;
	public Label progressText;
	public ProgressBarEx progressBar;
	public List<String> cfgFiles = new ArrayList<>(); //目标传送文件
	ShowProcessDialog processDialog;

	public ProductChangeDialog(Shell parentShell, CalBox calBox) {
		super(parentShell);
		this.calBox = calBox;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 250);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("切换设备");
	}

	@Override
	protected Control createContents(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite composite = new Composite(sashForm, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		composite.setLayout(formLayout);
		Combo deviceTypeCombo = new Combo(composite, SWT.NONE);

		// 在product文件夹下查找所有类型设备
		List<File> products = new ArrayList<>();
		products.addAll(findProductFile(productDir));
		
		String[] deviceTypes = new String[products.size()];

		for (int i = 0; i < products.size(); i++) {

			deviceTypes[i] = products.get(i).getName();
		}

		deviceTypeCombo.setItems(deviceTypes);
		deviceTypeCombo.select(0);
		FormData formData = new FormData(120, 30);
		formData.top = new FormAttachment(30, 100, 0);
		formData.left = new FormAttachment(10, 75, 0);

		deviceTypeCombo.setLayoutData(formData);
		Font font = new Font(Display.getDefault(), "arial", 13, SWT.BOLD);
		deviceTypeCombo.setFont(font);

		FormData formData2 = new FormData(150, 45);
		formData2.top = new FormAttachment(30, 100, 0);
		formData2.left = new FormAttachment(40, 75, 0);
		Font fontdata = new Font(Display.getDefault(), "arial", 18, SWT.BOLD);
		deviceTypeCombo.setFont(fontdata);

		ButtonEx buttonChange = new ButtonEx(composite, BtnStyle.RAISE);
		Color color = new Color(Display.getDefault(), 150, 100, 255);
		buttonChange.setLayoutData(formData2);
		buttonChange.setText("切换产品");
		buttonChange.setFont(font);
		if(products.size()==0) {
			buttonChange.setEnabled(false);
		}
		Label label = new Label(composite, SWT.NONE);
		FormData formData3 = new FormData(60, 45);
		formData3.top = new FormAttachment(80, 100, 0);
		formData3.left = new FormAttachment(10, 75, 0);
		label.setLayoutData(formData3);
		label.setFont(font);
		label.setText("进度:");

		progressText = new Label(composite, SWT.NONE);
		FormData formData4 = new FormData(250, 45);
		formData4.top = new FormAttachment(80, 100, 0);
		formData4.left = new FormAttachment(25, 75, 0);
		progressText.setLayoutData(formData4);
		progressText.setText("");
		progressText.setFont(font);

		Composite composite2 = new Composite(sashForm, SWT.NONE);
		composite2.setLayout(new GridLayout(1, false));
		progressBar = new ProgressBarEx(composite2, true);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 30;
		progressBar.setLayoutData(gridData);

		
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if(!getShell().isDisposed()) {				
					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							progressBar.redraw();
						}
					});
				}
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);

		sashForm.setWeights(new int[] { 8, 2 });

		buttonChange.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				buttonChange.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
				buttonChange.setEnabled(false);
			}

			@Override
			public void mouseDown(MouseEvent e) {

				progressText.setText("切换产品中···");
				buttonChange.setBackground(color);
				File productFile = new File(productDir + "\\" + deviceTypeCombo.getText());
				String productPath = productFile.getAbsolutePath();
				cfgFiles = getFiles(productPath);
				if(cfgFiles.size()==0) {
					return;
				}
				progressBar.setMaximum(cfgFiles.size());
				ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
				
				scheduledExecutorService.schedule(new Runnable() {

					@Override
					public void run() {
						changeDeviceType(cfgFiles);
					}
					
				},0,TimeUnit.MINUTES);

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		return composite;
	}

	public void changeDeviceType(List<String> configFiles) {

		int max = cfgFiles.size();
		try {
			util.connect(calBox.getIp());
			util.login("root", "");
			for (count = 0; count < max; count++) {
				if(!getShell().isDisposed()) {
					System.out.println("this is " + (count) + " file");
					//最后一个文件传输完成后的操作
					if (count == max - 1) {
						uploadFile(configFiles.get(count), true);
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								File currFile = new File(configFiles.get(count-1));
								progressText.setText("传输"+currFile.getName());
								getShell().dispose();
								processDialog=new ShowProcessDialog(Display.getDefault().getActiveShell());
								processDialog.open("主控重新启动中···");
							}
						});
						//等待主控重启15s
						CommonUtil.sleep(15000);
						try {
							util.connect(calBox.getIp());						
						}catch (Exception e) {
							Display.getDefault().asyncExec(()->{
								processDialog.close();
								MyMsgDlg.openInfoDialog(new Shell(), "", "切换产品失败", false);
							});
							
						}
						Display.getDefault().asyncExec(()->{
							processDialog.close();
							MyMsgDlg.openInfoDialog(new Shell(), "重启成功", "切换产品成功", false);
						});
						//只传输文件
					} else {
						uploadFile(configFiles.get(count), false);
						Display.getDefault().asyncExec(()->{
							File currFile = new File(configFiles.get(count));
							progressText.setText("传输"+currFile.getName());
							progressBar.setSelection(count);
						});
					}
					
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					File failFile = new File(configFiles.get(count));
					MyMsgDlg.openErrorDialog(new Shell(), "", failFile.getName() + "传输失败");
					// getShell().dispose();

				}
			});

		}

	}

	/*
	 * 查找所有产品文件
	 */
	private LinkedList<File> findProductFile(String productDir) {
		File productFile=new File(productDir);
		if(!productFile.isDirectory()) {
			productFile.mkdir();
		}
		LinkedList<File> dirList = new LinkedList<>();
		// dirList存放每个产品目录
		File dir = new File(productDir);
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					dirList.add(file);
				}
			}
		}
		return dirList;
	}

	/**
	 * 
	 * 
	 * @return 目录所有文件
	 */
	protected List<String> getFiles(String fileDir) {
		List<String> filePaths = new ArrayList<>();
		// DirectoryDialog OpenDirDialog=new DirectoryDialog(getShell(),SWT.OPEN);
		// OpenDirDialog.setText("选择上传的目录");
		// String fileDir=OpenDirDialog.open();
		try {
			if (!fileDir.equals("") && fileDir != null) {

				LinkedList<File> Dirlist = new LinkedList<>();

				File dir = new File(fileDir);
				File[] files = dir.listFiles();

				if (files != null) {
					for (File file1 : files) {
						if (file1.isDirectory()) {
							Dirlist.add(file1);

						} else {
							filePaths.add(file1.getAbsolutePath());
						}
					}
				} else {
					filePaths.add(dir.getAbsolutePath());
				}
				File temp;
				while (!Dirlist.isEmpty()) {
					temp = Dirlist.removeFirst();
					if (temp.isDirectory()) {
						files = temp.listFiles();
					}
					if (files == null) {
						continue;
					}
					for (File file2 : files) {
						if (file2.isDirectory()) {
							Dirlist.add(file2);
						} else {
							filePaths.add(file2.getAbsolutePath());
						}
					}
				}
			}
		} catch (Exception e) {

		}
		return filePaths;
	}

	// 上传文件重启系统
	public void uploadFile(String fileDir, boolean reStart) throws Exception {

		File file = new File(fileDir);
		if (file.isFile()) {
			util.upgradeFile(file.getAbsolutePath(), file.getName());
		
			if (file.getName().equals("base.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config");
			} else if (file.getName().equals("chnMap.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config");
			} else if (file.getName().equals("calculatePlan.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config/calConfig");
			} else if (file.getName().equals("calibratePlan.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config/calConfig");
			} else if (file.getName().equals("rangeCurrentPrecision.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config/calConfig");
			} else if (file.getName().equals("steadyCfg.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config/calConfig");
			} else if (file.getName().equals("delay.xml")) {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config/calConfig");
			} else {
				util.cpFile("/var/ftp/pub", file.getName(), " ~/config");
			}
		}
		if (reStart) {
			util.reboot();
			
		}
		Thread.sleep(1000);
	}

	public CalBox getCalBox() {
		return calBox;
	}

	public void setCalBox(CalBox calBox) {
		this.calBox = calBox;
	}

	public void setInfo(String info) {
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				progressText.setText(info);
			}
		});
	}
	
}

