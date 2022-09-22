package com.nlteck.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.commons.net.telnet.TelnetClient;
import org.aspectj.bridge.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.h2.util.New;

import com.nlteck.firmware.CalBoard;
import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.WorkBench;
import com.nlteck.utils.CommonUtil;
import com.nlteck.utils.UpdateUtil;
import com.nltecklib.protocol.li.PCWorkform.CalBoardUpdateFileData;
import com.nltecklib.protocol.li.PCWorkform.CalBoardUpdateModeData;
import com.nltecklib.protocol.util.ProtocolUtil;

public class UpdateDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected CalBox calBox;

	private Label labelInfo;
	private ProgressBar[] progressBars;
	private CountDownLatch cdl;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public UpdateDialog(Shell parent) {
		super(parent, SWT.TITLE | SWT.CLOSE);
		setText("设备升级");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open(CalBox calBox) {

		this.calBox = calBox;
		createContents();
		setCenterScreen();
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

		shell = new Shell(getParent(), getStyle());
		shell.setSize(800, 600);
		shell.setText(getText());

		GridLayout shellGd = new GridLayout(2, false);
		shellGd.marginWidth = 80;
		shellGd.marginHeight = 80;
		shellGd.horizontalSpacing = 20;
		shellGd.verticalSpacing = 10;
		shell.setLayout(shellGd);

		GridData textGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textGD.widthHint = 400;

		// 主控升级
		Label label = new Label(shell, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		label.setText("主控升级");

		Text workformText = new Text(shell, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		workformText.setLayoutData(textGD);
		workformText.setText("双击选择jar文件");
		workformText.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				String path = dlg.open();
				if (path != null) {
					workformText.setText(path);
				}
			}
		});

		Button button = new Button(shell, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("升级");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {

					if (!workformText.getText().contains("workform")) {
						throw new Exception("错误的主控程序文件");
					}
					if (!CommonUtil.checkIP(calBox.getIp())) {
						throw new Exception("校准箱ip错误");
					}
					UpdateUtil uu = new UpdateUtil();
					uu.connect(calBox.getIp());
					if (!uu.login("root", "")) {
						throw new Exception("登录失败" + calBox.getIp());
					}
					uu.upgradeMainBoard(workformText.getText());

					// TODO 通知主控重启

				} catch (Exception e1) {
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setMessage(e1.getMessage() + "");
					box.open();
				}
			}
		});

		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label label2 = new Label(shell, SWT.NONE);
		label2.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		label2.setText("校准板升级");

		int size = calBox.getCalBoardList().size();

		Composite calComp = new Composite(shell, SWT.NONE);
		calComp.setLayout(new GridLayout(size, true));
		GridData calCompGD = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		calCompGD.heightHint = 80;
		calComp.setLayoutData(calCompGD);

		GridData calBtnGd = new GridData(SWT.CENTER, SWT.CENTER, false, true);
		calBtnGd.widthHint = calCompGD.heightHint - 5;
		calBtnGd.heightHint = calCompGD.heightHint - 5;

		Button[] calBtns = new Button[size];
		for (int i = 0; i < size; i++) {
			calBtns[i] = new Button(calComp, SWT.PUSH);
			calBtns[i].setLayoutData(calBtnGd);
			calBtns[i].setText(i + 1 + "");
			calBtns[i].setData(new Param(i));
			calBtns[i].addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.getSource();
					Param p = (Param) button.getData();
					p.select = !p.select;
					button.setBackground(Display.getDefault()
							.getSystemColor(p.select ? SWT.COLOR_CYAN : SWT.COLOR_WIDGET_LIGHT_SHADOW));
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub

				}
			});
		}

		Text calText = new Text(shell, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		calText.setLayoutData(textGD);
		calText.setText("双击选择bin文件");
		calText.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				String path = dlg.open();
				if (path != null) {
					calText.setText(path);
				}
			}
		});

		Button buttonUpdateCal = new Button(shell, SWT.PUSH);
		buttonUpdateCal.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		buttonUpdateCal.setText("升级");
		buttonUpdateCal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// 防止重复开始
					if (cdl != null && cdl.getCount() > 0) {
						return;
					}

					if (!calBox.getConnector().isConnected()) {
						throw new Exception("校准箱未连接");
					}

					if (!calText.getText().contains(".bin")) {
						throw new Exception("升级文件格式错误");
					}

					String fileName = calText.getText();
					// 文件读取成byte
					File f = new File(fileName);
					int length = (int) f.length();
					byte[] data = new byte[length];
					new FileInputStream(f).read(data);
					List<Byte> dataList = ProtocolUtil.convertArrayToList(data);

					final int singleSize = 1024;

					int fileSize = dataList.size();
					int packCount = fileSize / singleSize + (fileSize % singleSize > 0 ? 1 : 0);
					System.out.println("packCount=" + packCount);

					cdl = new CountDownLatch(Arrays.asList(calBtns).stream().filter(x -> ((Param) x.getData()).select)
							.collect(Collectors.toList()).size());

					for (Button calBtn : calBtns) {
						final Param param = (Param) calBtn.getData();
						if ((param).select) {

							new Thread(() -> {

								try {

									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {
											progressBars[param.index].setState(SWT.NORMAL);
											progressBars[param.index].setMaximum(fileSize);
											progressBars[param.index].setSelection(0);
										}
									});

									CalBoardUpdateModeData calBoardUpdateModeData = new CalBoardUpdateModeData();
									calBoardUpdateModeData.setDriverIndex(param.index);
									calBoardUpdateModeData.setUpdateMode(true);
									StringBuffer info = new StringBuffer();
									if (!WorkBench.configCommand(calBox, calBoardUpdateModeData, 10000, info)) {
										info.append(String.format("校准板%d进入升级模式失败", param.index + 1));
										throw new Exception(info.toString());
									}

									for (int i = 0; i < packCount; i++) {
										int packIndex = i + 1;

										List<Byte> packContent = null;
										if (i < packCount - 1) {
											packContent = dataList.subList(i * singleSize, (i + 1) * singleSize);
										} else {
											packContent = dataList.subList(i * singleSize, fileSize);
										}

										CalBoardUpdateFileData calBoardUpdateFileData = new CalBoardUpdateFileData();
										calBoardUpdateFileData.setDriverIndex(param.index);
										calBoardUpdateFileData.setFileSize(fileSize);
										calBoardUpdateFileData.setPackCount(packCount);
										calBoardUpdateFileData.setPackIndex(packIndex);
										calBoardUpdateFileData.setPackContent(packContent);

										if (!WorkBench.configCommand(calBox, calBoardUpdateFileData, 10000, info)) {
											info.append(String.format("校准板%d升级失败", param.index + 1));
											throw new Exception(info.toString());
										}

										Display.getDefault().asyncExec(new Runnable() {

											@Override
											public void run() {
												progressBars[param.index]
														.setSelection(Math.min(packIndex * singleSize, fileSize));
											}
										});

									}

								} catch (Exception e1) {
									e1.printStackTrace();

									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {
											labelInfo.setText("升级失败:" + e1.getMessage());
											progressBars[param.index].setState(SWT.ERROR);
											progressBars[param.index].setSelection(fileSize);
										}
									});

								} finally {
									cdl.countDown();
								}

							}).start();
						}
					}
				} catch (Exception e1) {
					labelInfo.setText("升级失败:" + e1.getMessage());
					MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
					box.setMessage(e1.getMessage() + "");
					box.open();
				}
			}
		});

		// 进度条
		progressBars = new ProgressBar[size];
		for (int i = 0; i < size; i++) {
			progressBars[i] = new ProgressBar(shell, SWT.NONE);
			progressBars[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}

		labelInfo = new Label(shell, SWT.NONE);
		labelInfo.setLayoutData(textGD);
		labelInfo.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		labelInfo.setText("错误信息");

	}

	private static class Param {
		public int index;
		public boolean select;

		public Param(int index) {
			this.index = index;
		}
	}

	private void setCenterScreen() {
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	}

	public static void main(String[] args) {
		UpdateDialog dialog = new UpdateDialog(new Shell());
		CalBox calBox = new CalBox();
		calBox.setCalBoardList(new ArrayList<CalBoard>());
		
		calBox.setIp("192.168.1.127");
		dialog.open(calBox);
	}
}
