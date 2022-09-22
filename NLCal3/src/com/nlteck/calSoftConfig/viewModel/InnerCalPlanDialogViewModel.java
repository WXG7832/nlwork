package com.nlteck.calSoftConfig.viewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nlteck.calSoftConfig.view.ViewModelComposite;

public class InnerCalPlanDialogViewModel extends CalPlanMenuViewModel {

    public Model model;
    protected String tableViewerFiledName = "innerCalPlanLst";

    public InnerCalPlanDialogViewModel(ViewModelComposite composite) {
	super(composite);
	model.innerCalPlanLst = getInnerCalPlanInfoLst(defaultFolderPath);
    }

    @Override
    public void createSubViewModel() {

    }

    @Override
    protected Object newModel() {
	model = new Model();
	return model;
    }

    /**
     * @description 用于绑定控件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 10:56:12 AM
     */
    public class Model {
	public List<InnerCalPlanInfo> innerCalPlanLst;
    }

    /**
     * @description 表格数据源模型
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 10:49:58 AM
     */
    public static class InnerCalPlanInfo {
	public String name;
	public Date modifiedTime;
	public String path;
    }

    /**
     * @description 获得产品型号名称
     * @author zemin_zhu
     * @dateTime Jul 12, 2022 2:32:35 PM
     */
    protected List<InnerCalPlanInfo> getInnerCalPlanInfoLst(String folderPath) {
	List<InnerCalPlanInfo> InnerCalPlanInfoLst = new ArrayList<>();
	File folder = new File(folderPath);
	if (folder.exists()) {
	    for (File file : folder.listFiles()) {
		if (file.isDirectory()) {
		    InnerCalPlanInfo innerCalPlanInfo = new InnerCalPlanInfo();
		    innerCalPlanInfo.name = file.getName();
		    innerCalPlanInfo.path = file.getAbsolutePath();
		    innerCalPlanInfo.modifiedTime = new Date(file.lastModified());
		    InnerCalPlanInfoLst.add(innerCalPlanInfo);
		}
	    }
	}
	return InnerCalPlanInfoLst;
    }

    /**
     * @description 保存按钮事件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:52:18 PM
     */
    public void saveBtnClickEvent() {
	executeCommand(InnerCalPlanCommand.save);
    }

    /**
     * @description 新增按钮事件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:52:18 PM
     */
    public void addBtnClickEvent() {
	executeCommand(InnerCalPlanCommand.add);
    }

    /**
     * @description 删除按钮事件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:52:18 PM
     */
    public void delBtnClickEvent() {
	executeCommand(InnerCalPlanCommand.del);
    }

    /**
     * @description 改名按钮事件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:52:18 PM
     */
    public void renameBtnClickEvent() {
	executeCommand(InnerCalPlanCommand.rename);
    }

    /**
     * @description 导入按钮事件
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:52:18 PM
     */
    public void importBtnClickEvent() {
	executeCommand(InnerCalPlanCommand._import);
    }

    protected InnerCalPlanInfo getSelectedRow() throws Exception {
	return (InnerCalPlanInfo) getSelectedRow(tableViewerFiledName);
    }

    protected enum InnerCalPlanCommand {
	add, del, rename, save, _import;
    }

    protected void executeCommand(InnerCalPlanCommand command) {
	try {
	    InnerCalPlanInfo selectedRow = getSelectedRow();
	    switch (command) {
	    case save:
		if (dialogFactory.confirmDialog("确认将当前参数保存为内置校准方案: " + selectedRow.name)) {
		    executeCommand(selectedRow.path, Command.export);
		}
		break;
	    case del:
		if (dialogFactory.confirmDialog("确认删除当前内置校准方案: " + selectedRow.name)) {
		    tableViewerDel(model.innerCalPlanLst, selectedRow);
		    deleteFolder(new File(selectedRow.path));
		}
		break;
	    case rename:
		renameCalPlan(selectedRow);
		break;
	    case add:
		addCalPlan();
		break;
	    case _import:
		executeCommand(selectedRow.path, Command._import);
		break;
	    default:
		break;
	    }
	} catch (Exception e) {
	    exceptionReceiver(e);
	}
    }

    /**
     * @description 添加内置校准方案
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 2:33:28 PM
     */
    protected void addCalPlan() {
	String newCalPlanName = dialogFactory.inputDialog("校准方案名称", "");
	if (newCalPlanName != null) {
	    InnerCalPlanInfo innerCalPlanInfo = new InnerCalPlanInfo();
	    innerCalPlanInfo.name = newCalPlanName;
	    innerCalPlanInfo.modifiedTime = new Date();
	    innerCalPlanInfo.path = defaultFolderPath + "/" + newCalPlanName;
	    tableViewerAdd(model.innerCalPlanLst, innerCalPlanInfo);
	    new File(innerCalPlanInfo.path).mkdirs();
	}
    }

    /**
     * @description 将内置方案改名
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:56:19 PM
     */
    protected void renameCalPlan(InnerCalPlanInfo innerCalPlanInfo) {
	String newName = dialogFactory.inputDialog("校准方案名称", innerCalPlanInfo.name);
	if (newName != null) {
	    String newPath = renameFile(innerCalPlanInfo.path, newName);
	    innerCalPlanInfo.name = newName;
	    innerCalPlanInfo.path = newPath;
	    refreshControl();
	}

    }
}
