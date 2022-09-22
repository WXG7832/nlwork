package com.nlteck.calSoftConfig.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.nlteck.swtlib.progress.ShowProcessDialog;
import com.nlteck.swtlib.tools.MyMsgDlg;

/**
 * @description 对话窗工厂
 * @author zemin_zhu
 * @dateTime Jun 29, 2022 11:56:06 AM
 */
public class DialogFactory {

    protected String dialogTitleSuc = "操作成功";
    protected String dialogTitleFail = "操作失败";
    protected String dialogTitleConfirm = "操作确认";
    protected String waitingdialogContent = "执行中...";
    private ShowProcessDialog waitingDialog;
    private static String[] defaultAllowExtensionLst = new String[] { "*.*" };
    private static DialogFactory instance;

    public enum DialogEventType {
	ErrorDialog, InfoDialog, ConfirmDialog, WaitingDialogOpen, WaitingDialogClose, OpenFileDialog, SaveFileDialog
    }

    public static DialogFactory getInstance() {
	if (instance == null) {
	    instance = new DialogFactory();
	}
	return instance;
    }

    /**
     * @describe 用于通知监听者
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:57:37 PM
     */
    public static class DialogEventArgs {
	public DialogEventType dialogEventType;
	public Throwable throwable;
	public String msg;
	public String[] allowExtensionLst;

	private DialogEventArgs(DialogEventType dialogEventType, String msg) {
	    this.dialogEventType = dialogEventType;
	    this.msg = msg;
	}

	private DialogEventArgs(Throwable throwable) {
	    this.dialogEventType = DialogEventType.ErrorDialog;
	    this.throwable = throwable;
	}

	private DialogEventArgs(String msg, Throwable throwable) {
	    this.dialogEventType = DialogEventType.ErrorDialog;
	    this.throwable = throwable;
	    this.msg = msg;
	}

	private DialogEventArgs(DialogEventType dialogType, String msg, String[] allowExtensionLst) {
	    this.dialogEventType = dialogType;
	    this.msg = msg;
	    this.allowExtensionLst = allowExtensionLst;
	}

	private DialogEventArgs(DialogEventType dialogType) {
	    this.dialogEventType = dialogType;
	}

	/**
	 * @describe 提示对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs infoDialog(String msg) {
	    return new DialogEventArgs(DialogEventType.InfoDialog, msg);
	}

	/**
	 * @describe 异常提示对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs errorDialog(String msg) {
	    return new DialogEventArgs(DialogEventType.ErrorDialog, msg);
	}

	/**
	 * @describe 异常提示对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs errorDialog(String msg, Throwable throwable) {
	    return new DialogEventArgs(msg, throwable);
	}

	/**
	 * @describe 异常提示对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs errorDialog(Throwable throwable) {
	    return new DialogEventArgs(throwable);
	}

	/**
	 * @describe 等待对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs waitingDialogOpen() {
	    return new DialogEventArgs(DialogEventType.WaitingDialogOpen);
	}

	/**
	 * @describe 等待对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs waitingDialogClose() {
	    return new DialogEventArgs(DialogEventType.WaitingDialogClose);
	}

	/**
	 * @describe 确认对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs confirmDialog(String msg) {
	    return new DialogEventArgs(DialogEventType.ConfirmDialog, msg);
	}

	/**
	 * @describe 打开文件对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs openFileDialog(String msg, String[] allowExtensionLst) {
	    return new DialogEventArgs(DialogEventType.OpenFileDialog, msg, allowExtensionLst);
	}

	/**
	 * @describe 打开文件对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs openFileDialog(String msg) {
	    return openFileDialog(msg, defaultAllowExtensionLst);
	}

	/**
	 * @describe 保存文件对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs saveFileDialog(String msg, String[] allowExtensionLst) {
	    return new DialogEventArgs(DialogEventType.SaveFileDialog, msg, allowExtensionLst);
	}

	/**
	 * @describe 保存文件对话窗的事件参数实例
	 * @author zemin_zhu
	 * @dateTime Mar 25, 2022 9:53:09 AM
	 */
	public static DialogEventArgs saveFileDialog(String msg) {
	    return saveFileDialog(msg, defaultAllowExtensionLst);
	}
    }

    /**
     * @describe 用于处理监听者收到的事件参数
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 3:09:35 PM
     */
    public Object dialogEventArgsHandler(DialogEventArgs dialogEventArgs) {
	Object ret = null;
	DialogEventType dialogEventType = dialogEventArgs.dialogEventType;
	Throwable throwable = dialogEventArgs.throwable;
	String msg = dialogEventArgs.msg;
	String[] allowExtensionLst = dialogEventArgs.allowExtensionLst;
	switch (dialogEventType) {
	case WaitingDialogOpen:
	    waitingDialogOpen();
	    break;
	case WaitingDialogClose:
	    waitingDialogClose();
	    break;
	case ErrorDialog:
	    errorDialog(msg, throwable);
	    break;
	case InfoDialog:
	    infoDialog(msg);
	    break;
	case ConfirmDialog:
	    ret = confirmDialog(msg);
	    break;
	case OpenFileDialog:
	    ret = openFileDialog(msg, allowExtensionLst);
	    break;
	case SaveFileDialog:
	    ret = saveFileDialog(msg, allowExtensionLst);
	    break;
	default:
	    break;
	}
	return ret;
    }

    // 错误对话窗
    public void errorDialog(Throwable throwable) {
	errorDialog(null, throwable);
    }

    public void errorDialog(String msg) {
	errorDialog(msg, null);
    }

    public void errorDialog(String msg, Throwable throwable) {
	Display.getDefault().asyncExec(new Runnable() {

	    @Override
	    public void run() {
		String fullMsg = null;
		if (msg != null && throwable != null) {
		    fullMsg = msg + ": " + getStackStrace(throwable);
		} else if (msg != null) {
		    fullMsg = msg;
		} else {
		    fullMsg = getStackStrace(throwable);
		}

		MyMsgDlg.openErrorDialog(Display.getDefault().getActiveShell(), dialogTitleFail, fullMsg);
	    }
	});
    }

    // 提示信息对话窗
    public void infoDialog(String msg) {
	Display.getDefault().asyncExec(new Runnable() {
	    @Override
	    public void run() {

		MyMsgDlg.openInfoDialog(Display.getDefault().getActiveShell(), dialogTitleSuc, msg, false);
	    }
	});

    }

    // 需确认对话窗
    public boolean confirmDialog(String msg) {
	return MyMsgDlg.openConfirmDialog(Display.getDefault().getActiveShell(), dialogTitleConfirm, msg);
    }

    /**
     * @describe 保存/打开文件对话窗
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:28:34 PM
     */
    public String fileDialog(String fileName, String[] allowExtensionLst, int dlgType) {
	FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), dlgType);
//	fileDialog.setFileName(I18N.getVal(I18N.LogicConsolePart_report_singleFile_fileName, device.name));
	fileDialog.setFileName(fileName);
	fileDialog.setFilterExtensions(allowExtensionLst);
	String filePath = fileDialog.open();
	return filePath;
    }

    /**
     * @describe 打开文件对话窗
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:30:24 PM
     */
    public String openFileDialog(String fileName, String[] allowExtensionLst) {
	return fileDialog(fileName, allowExtensionLst, SWT.OPEN);
    }

    public String openFileDialog(String fileName) {
	return openFileDialog(fileName, defaultAllowExtensionLst);
    }

    /**
     * @describe 保存文件对话窗
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:30:24 PM
     */
    public String saveFileDialog(String fileName, String[] allowExtensionLst) {
	return fileDialog(fileName, allowExtensionLst, SWT.SAVE);
    }

    public String saveFileDialog(String fileName) {
	return saveFileDialog(fileName, defaultAllowExtensionLst);
    }

    /**
     * @describe 等待对话窗打开
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:40:25 PM
     */
    public void waitingDialogOpen(DisposeListener disposeListener) {
	if (waitingDialog == null || waitingDialog.getShell().isDisposed()) {
	    Display.getDefault().syncExec(new Runnable() {
		@Override
		public void run() {

		    waitingDialog = new ShowProcessDialog(Display.getDefault().getActiveShell());
		    waitingDialog.open(waitingdialogContent);
		    if (disposeListener != null) {
			waitingDialog.getShell().addDisposeListener(disposeListener);
		    }
		}
	    });
	}
    }

    /**
     * @describe 等待对话窗打开
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:40:25 PM
     */
    public void waitingDialogOpen() {
	waitingDialogOpen(null);
    }

    /**
     * @describe 等待对话窗关闭
     * @author zemin_zhu
     * @dateTime Mar 23, 2022 2:42:10 PM
     */
    public void waitingDialogClose() {
	if (waitingDialog != null && !waitingDialog.getShell().isDisposed()) {
	    Display.getDefault().syncExec(new Runnable() {
		@Override
		public void run() {

		    waitingDialog.close();
		}
	    });
	}
    }

    /**
     * @description 等待对话窗更换文字
     * @author zemin_zhu
     * @dateTime Jun 21, 2022 11:54:58 AM
     */
    public void waitingDialogUpdate(String msg) {
	if (waitingDialog != null && !waitingDialog.getShell().isDisposed()) {
	    Display.getDefault().syncExec(new Runnable() {
		@Override
		public void run() {
		    waitingDialog.updateText(msg);
		}
	    });
	}
    }

    /**
     * @description 获得棧路线
     * @author zemin_zhu
     * @dateTime Jun 13, 2022 6:59:15 PM
     */
    public String getStackStrace(Throwable throwable) {
	throwable.printStackTrace();
	ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
	throwable.printStackTrace(new java.io.PrintWriter(buf, true));
	String stackTrace = buf.toString();
	try {
	    buf.close();
	} catch (IOException e) {

	    e.printStackTrace();
	}
	return stackTrace;
    }

    /**
     * @description 打开文件夹对话窗
     * @author zemin_zhu
     * @dateTime Jul 11, 2022 2:44:16 PM
     */
    public String folderDialog(String defaultPath, int dlgType) {
	DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell(), dlgType);
	dialog.setFilterPath(defaultPath);
	return dialog.open();
    }

    /**
     * @description 选择文件夹对话窗
     * @author zemin_zhu
     * @dateTime Jul 11, 2022 2:50:44 PM
     */
    public String openFolderDialog(String defaultPath) {
	return folderDialog(defaultPath, SWT.OPEN);
    }

    /**
     * @description 保存文件夹对话窗
     * @author zemin_zhu
     * @dateTime Jul 11, 2022 2:50:47 PM
     */
    public String saveFolderDialog(String defaultPath) {
	return folderDialog(defaultPath, SWT.CLOSE);
    }

    /**
     * @description 输入信息对话窗
     * @author zemin_zhu
     * @dateTime Jul 14, 2022 3:04:38 PM
     */
    public String inputDialog(String title, String defaultValue) {
	String result = null;
	InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), title, title, defaultValue, null);
	if (dialog.open() == InputDialog.OK) {
	    result = dialog.getValue();
	}
	return result;
    }
}
