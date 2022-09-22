package com.nlteck.calSoftConfig.viewModel;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

import com.nlteck.calSoftConfig.controller.Controller;
import com.nlteck.calSoftConfig.controller.DialogFactory;
import com.nlteck.calSoftConfig.controller.FileTransfer;
import com.nlteck.calSoftConfig.view.ViewModelComposite;
import com.nltecklib.utils.LogUtil;

/**
 * @description 参数窗口的视图模型基类
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:53:44 AM
 */
public abstract class ConfigDialogViewModel extends ViewModel {
    protected String fileName;
    protected String localPath;
    protected String remotePath;
    protected String calibrationDeviceIP;
    protected Logger logger;
    protected FileTransfer fileTransfer;
    protected DialogFactory dialogFactory;
    public CalPlanMenuViewModel calPlanMenuViewModel;

    public ConfigDialogViewModel(ViewModelComposite composite) {
	super(composite);
	calibrationDeviceIP = (String) composite.args[0];
    }

    /**
     * @description 实例化控制器
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 1:43:46 PM
     */
    @Override
    protected void createController() {
	logger = LogUtil.getLogger("CalSoftConfigPlugin");
	loadXmlFileHandleException();
	dialogFactory = DialogFactory.getInstance();
	fileTransfer = FileTransfer.getInstance();
	fileTransfer.addListener(new Listener() {

	    @Override
	    public void onLogEvent(Controller sender, LogEventArgs logEventArgs) {

		if (!logEventArgs.isError) {
		    dialogFactory.waitingDialogUpdate(logEventArgs.msg);
		} else {
		    exceptionReceiver(logEventArgs.msg, logEventArgs.throwable);
		}
	    }

	});

    }

    /**
     * @description 实例化子视图模型
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 1:44:35 PM
     */
    @Override
    public void createSubViewModel() {
//	CTabFolder tabFolder = getParentTabFolder();
	calPlanMenuViewModel = new CalPlanMenuViewModel(composite);
	calPlanMenuViewModel.addListener(new Listener() {

	    @Override
	    public void onLogEvent(Controller sender, LogEventArgs logEventArgs) {
		exceptionReceiver(logEventArgs.throwable);
	    }

	});
    }

    /**
     * @description 下发参数按钮事件
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 2:41:24 PM
     */
    public void sendBtnClickEvent() {
	new Thread(new Runnable() {

	    @Override
	    public void run() {

		try {
		    refreshModel();
		    sendXmlFile();
		    dialogFactory.infoDialog("下发参数已完成");
		} catch (Exception e) {
		    // TODO: handle exception
		    dialogFactory.waitingDialogClose();
		    dialogFactory.errorDialog("下发参数发生异常", e);
		    logger.error("下发参数发生异常", e);
		} finally {
		    fileTransfer.disconnect();
		}
	    }
	}).start();
    }

    /**
     * @description 查询参数按钮事件
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 2:41:35 PM
     */
    public void queryBtnClickEvent() {
	new Thread(new Runnable() {

	    @Override
	    public void run() {

		try {
		    queryXmlFile();
		    refreshControl();
		    dialogFactory.infoDialog("查询参数已完成");
		} catch (Exception e) {
		    // TODO: handle exception
		    dialogFactory.waitingDialogClose();
		    exceptionReceiver("查询参数发生异常", e);
		} finally {
		    fileTransfer.disconnect();
		}
	    }
	}).start();
    }

    /**
     * @description 重启按钮事件
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 2:41:12 PM
     */
    public void rebootBtnClickEvent() {
	if (dialogFactory.confirmDialog("确认重启主板")) {
	    fileTransfer.reboot();
	}
    }

    /**
     * @description 导入文件按钮事件
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 5:07:53 PM
     */
    public void importBtnClickEvent() {
	String filePath = dialogFactory.openFileDialog(getFileName(), new String[] { "*.xml" });

	if (filePath == null) {
	    return;
	}
	loadXmlFileHandleException(filePath);
	refreshControl();
    }

    /**
     * @description 导出文件按钮事件
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 5:10:06 PM
     */
    public void exportBtnClickEvent() {
	String filePath = dialogFactory.saveFileDialog(getFileName(), new String[] { "*.xml" });

	if (filePath == null) {
	    return;
	}
	saveXmlFileHandleException(filePath);
	dialogFactory.infoDialog("导出文件成功: " + filePath);
    }

    @Override
    public void exceptionReceiver(Throwable e) {

	dialogFactory.errorDialog(e);
	logger.error(e);
    }

    /**
     * @description 载入xml文件
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 5:08:01 PM
     */
    protected void loadXmlFileHandleException(String filePath) {
	try {
	    loadXmlFile(filePath);
	} catch (Exception e) {

	    exceptionReceiver(String.format("载入文件发生异常: %s", filePath), e);
	}

    }

    /**
     * @description 载入xml文件, 从默认路径
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 5:08:01 PM
     */
    protected void loadXmlFileHandleException() {
	String filePath = xmlFileDefaultPath();
	loadXmlFileHandleException(filePath);
    }

    /**
     * @description 保存xml文件
     * @author zemin_zhu
     * @dateTime Jun 22, 2022 5:14:18 PM
     */
    protected void saveXmlFileHandleException(String filePath) {
	try {
	    ensureFolderExist(filePath);
	    saveXmlFile(filePath);
	} catch (Exception e) {

	    exceptionReceiver(String.format("保存文件发生异常: %s", filePath), e);
	}

    }

    protected abstract void loadXmlFile(String filePath) throws Exception;

    protected void loadXmlFile() throws Exception {
	String filePath = xmlFileDefaultPath();
	loadXmlFile(filePath);
    }

    protected abstract void saveXmlFile(String filePath) throws Exception;

    protected void saveXmlFile() throws Exception {
	String filePath = xmlFileDefaultPath();
	saveXmlFile(filePath);
    }

    protected abstract String xmlFileDefaultPath();

    protected String getFileName() {
	if (localPath != null) {
	    String[] pathArr = localPath.split("/");
	    fileName = pathArr[pathArr.length - 1];
	}
	return fileName;
    }

    public DisposeListener getDisposeListener() {
	return fileTransfer.getDisposeListener();
    }

    /**
     * @description 查询.xml文件
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 6:01:49 PM
     */
    protected void queryXmlFile(String remotePath) throws Exception {
	dialogFactory.waitingDialogOpen(fileTransfer.getDisposeListener());
	fileTransfer.telnetRespSymbol = "\n";
	fileTransfer.connectAndLogin(calibrationDeviceIP);
	fileTransfer.download(remotePath, "temp");
	// Thread.sleep(1000);
	loadXmlFile();
	dialogFactory.waitingDialogClose();
    }

    /**
     * @description 下发.xml文件
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 6:02:04 PM
     */
    protected void sendXmlFile(String localPath, String remotePath) throws Exception {
	dialogFactory.waitingDialogOpen(fileTransfer.getDisposeListener());
	saveXmlFile();
	fileTransfer.telnetRespSymbol = ":";
	fileTransfer.connectAndLogin(calibrationDeviceIP);
	fileTransfer.upload(localPath, remotePath);
	dialogFactory.waitingDialogClose();
    }

    /**
     * @description 查询.xml文件, 以默认路径
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 6:30:38 PM
     */
    protected void queryXmlFile() throws Exception {
	queryXmlFile(remotePath);
    }

    /**
     * @description 下发.xml文件, 以默认路径
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 6:31:12 PM
     */
    protected void sendXmlFile() throws Exception {
	sendXmlFile(localPath, remotePath);
    }

    /**
     * @description 以首个已绑定的控件, 找到父控件TabFolder
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 1:30:08 PM
     */
    @Deprecated
    protected CTabFolder getParentTabFolder() {
	CTabFolder tabFolder = null;
	for (MappingElement mappingElement : controlMappingObject.getValues()) {
	    if (mappingElement.isControl()) {
		Control control = mappingElement.getAsControl();
		tabFolder = getParentTabFolder(control);
		break;
	    }
	}
	return tabFolder;
    }

    /**
     * @description 递归找到父控件TabFolder
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 1:31:21 PM
     */
    @Deprecated
    protected CTabFolder getParentTabFolder(Control control) {
	CTabFolder tabFolder = null;
	if (control instanceof CTabFolder) {
	    tabFolder = (CTabFolder) control;
	} else {
	    tabFolder = getParentTabFolder(control.getParent());
	}
	return tabFolder;
    }

    /**
     * @description 确保文件夹存在
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 5:48:07 PM
     */
    protected File ensureFolderExist(String path) {
	File folder = null;
	String[] pathArr = path.split("/");
	String folderPath = "";
	for (int i = 0; i < pathArr.length - 1; i++) {
	    if (i != pathArr.length - 1) {
		folderPath += pathArr[i] + "/";
		folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
		    folder.mkdir();
		}
	    }
	}
	return folder;
    }

    /**
     * @description 导出文件时, 获得相对路径
     * @author zemin_zhu
     * @dateTime Jul 13, 2022 6:16:52 PM
     */
    protected String getRelativePath(String folderPath, String fileName) {
	String filePath = null;
	if (!fileName.equals("base.xml")) {
	    filePath = folderPath + "/calConfig/" + fileName;
	} else {
	    filePath = folderPath + "/" + fileName;
	}
	return filePath;
    }
}
