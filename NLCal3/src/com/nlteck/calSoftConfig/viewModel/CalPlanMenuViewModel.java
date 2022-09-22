package com.nlteck.calSoftConfig.viewModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.nlteck.calSoftConfig.controller.Controller;
import com.nlteck.calSoftConfig.view.InnerCalPlanDialog;
import com.nlteck.calSoftConfig.view.ViewModelComposite;

public class CalPlanMenuViewModel extends ConfigDialogViewModel {

    protected CTabFolder tabFolder;
    protected String defaultFolderPath = System.getProperty("user.dir") + "/product";
    // 本软件尚不支持编辑的.xml文件
    protected String[] unsupportedFileNameArr = new String[] { "calculatePlan.xml", "rangeCurrentPrecision.xml" };
    public InnerCalPlanDialogViewModel innerCalPlanDialogViewModel;

    public CalPlanMenuViewModel(ViewModelComposite composite) {
	super(composite);
	this.tabFolder = (CTabFolder) composite.args[1];
	createSubViewModel();
    }

    @Override
    public void createSubViewModel() {
	innerCalPlanDialogViewModel = new InnerCalPlanDialogViewModel(composite);
	innerCalPlanDialogViewModel.addListener(new Listener() {

	    @Override
	    public void onLogEvent(Controller sender, LogEventArgs logEventArgs) {
		exceptionReceiver(logEventArgs.throwable);

	    }
	});
    }

    @Override
    protected Object newModel() {
	return null;
    }

    @Override
    public void exceptionReceiver(Throwable e) {
	notifyErrorLogEvent(e);
    }

    @Override
    public void importBtnClickEvent() {
	String folderPath = dialogFactory.openFolderDialog(defaultFolderPath);
	if (folderPath == null) {
	    return;
	}
	executeCommand(folderPath, Command._import);
    }

    @Override
    public void exportBtnClickEvent() {
	String folderPath = dialogFactory.saveFolderDialog(defaultFolderPath);
	if (folderPath == null) {
	    return;
	}
	executeCommand(folderPath, Command.export);
    }

    @Override
    public void sendBtnClickEvent() {
	executeCommand(null, Command.send);
    }

    @Override
    public void queryBtnClickEvent() {
	executeCommand(null, Command.query);
    }

    @Override
    protected void loadXmlFile(String filePath) throws Exception {

    }

    @Override
    protected void saveXmlFile(String filePath) throws Exception {

    }

    @Override
    protected String xmlFileDefaultPath() {

	return null;
    }

    /**
     * @description 在指定文件夹中找到指定xml文件的路径
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 4:14:39 PM
     */
    protected String getXmlFilePath(String folderPath, String target) throws Exception {
	String filePath = null;
	List<String> filePathList = Files.walk(Paths.get(folderPath))
		.filter(p -> p.getFileName().toString().equals(target)).map(Path::toString)
		.collect(Collectors.toList());
	if (filePathList.size() == 0) {
	    throw new Exception("文件夹中未找到指定文件: " + target);
	} else {
	    filePath = filePathList.get(0);
	}
	return filePath;
    }

    /**
     * @description 执行指令
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 4:30:52 PM
     */
    protected void executeCommand(String folderPath, Command command) {
	try {
	    for (CTabItem tabItem : tabFolder.getItems()) {
		ConfigDialogViewModel viewModel = (ConfigDialogViewModel) tabItem.getData();
		if (folderPath != null) {
		    String fileName = viewModel.getFileName();
		    String filePath = null;
		    switch (command) {
		    case _import:
			filePath = getXmlFilePath(folderPath, fileName);
			viewModel.loadXmlFileHandleException(filePath);
			break;
		    case export:
			filePath = getRelativePath(folderPath, fileName);
			viewModel.saveXmlFileHandleException(filePath);
			break;
		    default:
			break;
		    }
		} else {
		    switch (command) {
		    case query:
			viewModel.queryXmlFile();
			break;
		    case send:
			viewModel.sendXmlFile();
			break;
		    default:
			break;
		    }
		}
		viewModel.refreshControl();
	    }
	    handleUnsupportedFiles(folderPath, command);
	    dialogFactory.infoDialog(command.description + "文件成功");
	    String calPlanName = null;
	    if (folderPath != null) {
		calPlanName = folderPath;
	    } else {
		calPlanName = calibrationDeviceIP;
	    }
	    shell.setText("校准工装参数: " + calPlanName);
	} catch (Exception e) {
	    notifyErrorLogEvent(command.description + "文件发生异常", e);
	}
    }

    /**
     * @description 操作参数文件的指令
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 5:19:42 PM
     */
    protected enum Command {
	_import("导入"), export("导出"), query("查询"), send("下发");

	public String description;

	private Command(String description) {
	    this.description = description;
	}

    }

    /**
     * @description 下发或查询未支持文件
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 6:43:38 PM
     */
    protected void handleUnsupportedFiles(String folderPath, Command command) throws Exception {
	for (String fileName : unsupportedFileNameArr) {
	    String localPath = "temp/" + fileName;
	    String remotePath = "calConfig/" + fileName;
	    File localFile = new File(localPath);
	    switch (command) {
	    case query:
		queryXmlFile(remotePath);
		break;
	    case send:
		if (localFile.exists()) {
		    sendXmlFile(localPath, remotePath);
		}
		break;
	    case export:
		if (localFile.exists()) {
		    String destination = getRelativePath(folderPath, fileName);
		    Files.copy(localFile.toPath(), new File(destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		break;
	    case _import:
		String innerPlanFilePath = getRelativePath(folderPath, fileName);
		File innerPlanFile = new File(innerPlanFilePath);
		if (innerPlanFile.exists()) {
		    Files.copy(innerPlanFile.toPath(), localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		break;
	    default:
		break;
	    }
	}
    }

    public void innerCalPlanBtnClickEvent() {
	InnerCalPlanDialog innerCalPlanDialog = new InnerCalPlanDialog(shell, innerCalPlanDialogViewModel);
	innerCalPlanDialog.open();
    }

}
