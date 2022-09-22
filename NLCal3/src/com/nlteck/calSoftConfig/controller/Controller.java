package com.nlteck.calSoftConfig.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 控制器基类
 * @author zemin_zhu
 * @dateTime Jun 7, 2022 10:39:50 AM
 */
public abstract class Controller {

    protected List<Listener> listeners = new ArrayList<Listener>();

    /**
     * @description 监听者接口
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 12:25:02 AM
     */
    public interface Listener {

	/**
	 * @description 日志事件
	 * @author zemin_zhu
	 * @dateTime Jun 7, 2022 10:35:15 AM
	 */
	public void onLogEvent(Controller sender, LogEventArgs logEventArgs);

    }

    /**
     * @description 添加监听者
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:55:18 AM
     */
    public void addListener(Listener listener) {
	listeners.add(listener);
    }

    /**
     * @description 移除监听者
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:55:28 AM
     */
    public void removeListener(Listener listener) {
	listeners.remove(listener);
    }

    /**
     * @description 清空监听者
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:55:38 AM
     */
    public void clearListners() {
	listeners.clear();
    }

    /**
     * @description 触发日志事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:36:35 AM
     */
    protected void notifyLogEvent(LogEventArgs logEventArgs) {
	for (Listener listener : listeners) {
	    listener.onLogEvent(this, logEventArgs);
	}
    }

    /**
     * @description 触发日志事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:36:35 AM
     */
    protected void notifyLogEvent(String msg, Throwable throwable, boolean isError) {
	LogEventArgs logEventArgs = new LogEventArgs(msg, throwable, isError);
	for (Listener listener : listeners) {
	    listener.onLogEvent(this, logEventArgs);
	}
    }

    /**
     * @description 触发日志事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 2:24:54 PM
     */
    protected void notifyLogEvent(String msg) {
	notifyLogEvent(msg, null, false);
    }

    /**
     * @description 触发异常日志事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:37:23 AM
     */
    protected void notifyErrorLogEvent(Throwable throwable) {
	notifyLogEvent(null, throwable, true);
    }

    /**
     * @description 触发异常日志事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:37:23 AM
     */
    protected void notifyErrorLogEvent(String msg) {
	notifyLogEvent(msg, null, true);
    }

    /**
     * @description 触发异常日志事件
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 10:37:23 AM
     */
    protected void notifyErrorLogEvent(String msg, Throwable throwable) {
	notifyLogEvent(msg, throwable, true);
    }

    /**
     * @description 日志事件参数
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 1:33:21 PM
     */
    public class LogEventArgs implements EventArgs {
	public String msg;
	public Throwable throwable;
	public boolean isError;

	public LogEventArgs(String msg, Throwable throwable, boolean isError) {
	    this.msg = msg;
	    this.throwable = throwable;
	    this.isError = isError;
	}
    }

    /**
     * @description 事件参数接口
     * @author zemin_zhu
     * @dateTime Jun 7, 2022 12:34:05 PM
     */
    public interface EventArgs {

    }

    /**
     * @description 延时
     * @author zemin_zhu
     * @dateTime Jun 13, 2022 6:29:47 PM
     */
    protected void sleep(int delay) {
	notifyLogEvent("延时: " + delay + "ms");
	if (delay == 0) {
	    return;
	}
	try {
	    Thread.sleep(delay);
	} catch (InterruptedException e) {
	   
	    notifyErrorLogEvent("延时发生异常", e);
	}
    }

}
