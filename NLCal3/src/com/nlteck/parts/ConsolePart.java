package com.nlteck.parts;

import java.util.List;

import com.nlteck.firmware.CalBox;
import com.nlteck.firmware.Channel;
import com.nlteck.model.ChannelDO;

/**
 * 调试面板
 * 
 * @author caichao_tang
 *
 */
public abstract class ConsolePart {

    public List<Channel> channelList;

    /**
     * 设置工具栏状态
     */
    public abstract void setToolItemState();

    /**
     * 活得当前界面设备已绑定的所有校准箱
     * 
     * @return
     */
    public abstract List<CalBox> getBindCalBox();

    /**
     * 刷新通道界面
     */
    public abstract void redraw();
    
    
    /**
     * 
     * @author  wavy_zheng
     * 2021年1月12日
     * @param obj  logic,device,driver,calbox
     */
    public abstract void refreshData(Object obj);
    

}
