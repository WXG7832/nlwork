package com.nlteck.resources;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import com.nlteck.utils.UIUtil;

/**
 * 资源库
 * 
 * @author Administrator
 *
 */
public class Resources {
    public final static String PROJECT_NAME = "NLCal";

    // logo图标
    public final static Image LOGO = UIUtil.createRCPImage(PROJECT_NAME, "icons/logo.png");

    // 树图片
    public final static Image DEVICE_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/tree/equip_16.png");
    public final static Image DRIVER_BOARD_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/driver16.png");
    public final static Image LOGIC_BOARD_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/motherboard16.png");
    public final static Image CAL_BOARD_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/calboard16.png");
    public final static Image MULTI_METER_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/tree/multi_meter_16.png");
    public final static Image CAL_BOX_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/tree/cal_box_16.png");
    public final static Image POWERBOX_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/tree/powerbox16.png");
    
    // 方案配置图片
    public final static Image NEW_SCHEMA_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/new_schema.png");
    public final static Image IMPORT_SCHEMA_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/import_schema.png");
    public final static Image CLEAR_SCHEMA_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/clear_schema.png");
    public final static Image QUERY_SCHEMA_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/query_schema.png");
    public final static Image SAVE_SCHEMA_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/save_schema.png");
  
    public final static Image SCHEMA_CONFIG_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/schema_config.png");
    public final static Image SEND_SCHEMA_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/send_schema.png");
    public final static Image ADD_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/add_32.png");
    public final static Image DEL_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/del_32.png");
    public final static Image INSERT_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/insert_32.png");
    public final static Image UP_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/up_32.png");
    public final static Image DOWN_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/down_32.png");
    public final static Image SORT_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/sort_32.png");
    
    public final static Image ADD_PLAN_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/plan_add_32.png");
    public final static Image DEL_PLAN_ITEM_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/schemaConfig/plan_del_32.png");
    
    // 工具栏图片
    public final static Image SWITCH_ON_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/switch_on_32.png");
    public final static Image SWITCH_OFF_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/switch_off_32.png");
    public final static Image MATCH_MODE_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/match_mode_32.png");
    public final static Image CAL_MODE_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/cali_mode_32.png");
    public final static Image START_CALI_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/start_cali_32.png");
    public final static Image START_CALC_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/start_calc_32.png");
    public final static Image STOP_CAL_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/stop_cal_32.png");
    public final static Image CONNECT_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/connect_32.png");
    public final static Image DISCONNECT_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/disconnect_32.png");
    
    public final static Image STOP_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/stop32.png");
    public final static Image REPORT_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/report32.png");
    public final static Image CONFIG_PLAN_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/config_plan_32.png");
    
    public final static Image CalPass = UIUtil.createRCPImage(PROJECT_NAME, "icons/result/calPass.png");
    public final static Image CalFail = UIUtil.createRCPImage(PROJECT_NAME, "icons/result/calFail.png");
    public final static Image MeterPass = UIUtil.createRCPImage(PROJECT_NAME, "icons/result/meterPass.png");
    public final static Image MeterFail = UIUtil.createRCPImage(PROJECT_NAME, "icons/result/meterFail.png");
    public final static Image Motherboard32 = UIUtil.createRCPImage(PROJECT_NAME, "icons/motherboard32.png");
    public final static Image MotherboardDisable32 = UIUtil.createRCPImage(PROJECT_NAME, "icons/motherboard_disable32.png");
    public final static Image TestPass = UIUtil.createRCPImage(PROJECT_NAME, "icons/result/checkOKEn64.png"); 
    public final static Image TestFail = UIUtil.createRCPImage(PROJECT_NAME, "icons/result/checkFailEn64.png");
    //电池内容图片
    public final static Image MATCHED_IMAGE = UIUtil.createRCPImage(PROJECT_NAME, "icons/battery/matched_18.png");
    //运行标志位
    public final static Image PLAY_FLAG = UIUtil.createRCPImage(PROJECT_NAME, "icons/toolitem/play16.png");
    
    // 颜色定义
    
    //电池背景颜色
    public final static Color UDT_CLR = UIUtil.createColor(new RGB(192, 131, 131)); // 待测颜色
    public final static Color ALERT_CLR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
    public final static Color NONE_CLR = UIUtil.createColor(new RGB(240, 240, 240));
	public final static Color TEST_CLR = UIUtil.createColor(new RGB(136, 208, 132));
	public final static Color READY_CLR = UIUtil.createColor(new RGB(218, 212, 98));
	public final static Color MEA_COMPLETE_CLR = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA);
	public final static Color CAL_COMPLETE_CLR = UIUtil.createColor(new RGB(158, 206, 250));
     // 白色
    // 黄色
    
   
    
    public final static Color BG_CLR = UIUtil.createColor(new RGB(0xCB, 0xDD, 0xF3));
    public final static Color GRAY_CLR = UIUtil.createColor(new RGB(240, 240, 240));
    public final static Color METER_CLR = UIUtil.createColor(new RGB(136, 208, 132));
    public final static Color CAL_CLR = UIUtil.createColor(new RGB(218, 212, 98));
   
    public final static Color WHITE_CLR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
    public final static Color BLUE_CLR = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
    public final static Color GREEN_CLR = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
    public final static Color DARK_GREEN_CLR = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
    public final static Color RED_CLR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
    public final static Color BLACK_CLR = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
  
    public final static Color LIGHT_SKY_BLUE_CLR = UIUtil.createColor(new RGB(135, 206, 250));
    public final static Color PLUM_CLR = UIUtil.createColor(new RGB(221, 160, 221));
    public final static Color GOLD_CLR = UIUtil.createColor(new RGB(255, 215, 0));
    public final static Color LIGHT_GREEN_CLR = UIUtil.createColor(new RGB(144, 238, 144));
    public final static Color LIGHT_GRAY_CLR = UIUtil.createColor(new RGB(211, 211, 211));
    

    public final static Color COLOR_RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
    
    // *************************************************
    // *
    // *电池通道颜色常量
    // *
    // *************************************************
    // 橙色
    public final static Color COLOR_SELECT = SWTResourceManager.getColor(244,164,96);
   
    // 绿色
    public final static Color COLOR_CALIBRATING = SWTResourceManager.getColor(146, 239, 87);
    public final static Color COLOR_CALCULATING = SWTResourceManager.getColor(146, 239, 87);
    // 红色
    public final static Color COLOR_CALIBRATE_FAIL = SWTResourceManager.getColor(255, 0, 0);
    public final static Color COLOR_CALCULATE_FAIL = SWTResourceManager.getColor(255, 0, 0);
    // 紫色
    public final static Color COLOR_CALIBRATE_PASS = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA);// SWTResourceManager.getColor(153, 50, 204);
    public final static Color COLOR_CALCULATE_PASS = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA);// SWTResourceManager.getColor(153, 50, 204);
    // 蓝色
    public final static Color COLOR_MATCHED = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);// SWTResourceManager.getColor(65, 105, 225);
    // 前景色
    public final static Color COLOR_DRIVER_INFO = SWTResourceManager.getColor(150, 205, 205);
    public final static Color COLOR_BLACK = SWTResourceManager.getColor(0, 0, 0);
    // 鼠标
    public final static Cursor normalCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW);
    public final static Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
    public final static Cursor busyCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT);
}
