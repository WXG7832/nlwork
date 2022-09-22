package com.nlteck.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.nlteck.model.MeasureDotDO;
import com.nlteck.utils.CommonUtil;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;

/**
  *  计量报表导出
 *  CSV、XLSX
 * @Desc：   
 * @author：LLC   
 * @Date：2021年7月15日 上午10:53:18   
 * @version
 */
public class MeasureReport {

	
	private final static String[] tipHeaderL1 = new String[] {"图示", "判断结果", "判断条件(单位：mA）", "计量员", "确认人"};
	private final static String[] tipHeaderL2 = new String[] {"OK", "-2", "<=Value<=", "2", "", ""};
	private final static String[] tipHeaderL3 = new String[] {"NG", "-2", ">Value>", "2"};
	
	private final static String[] headers = new String[] {"通道号", "设定值", "HP表测量值", "设备回检值", "程控差值", "回检差值", "判断结果"};
	
	private static java.awt.Color COLOR_GREEN = new java.awt.Color(0, 176, 80);
	private static java.awt.Color COLOR_YELLOW = new java.awt.Color(255, 255, 0);
	private static java.awt.Color COLOR_BLUE = new java.awt.Color(0, 176, 240);
	
	/** 常用的边框色 */
	private final static short BORDER_COLOR_BLACK = IndexedColors.BLACK1.getIndex();
	private final static short BORDER_COLOR_RED = IndexedColors.RED.getIndex();
	private final static short BORDER_COLOR_GREEN = IndexedColors.SEA_GREEN.getIndex();
	/** 常用的虚线 */
	private static BorderStyle BORDER_STYLE_DOTTED = BorderStyle.DOTTED;
	private static BorderStyle BORDER_STYLE_THIN = BorderStyle.THIN;
	
	private static CellRangeAddress region  = new CellRangeAddress(0, 0, (short) 2, (short) 4);
	private static CellRangeAddress region2 = new CellRangeAddress(1, 2, (short) 5, (short) 5);
	private static CellRangeAddress region3 = new CellRangeAddress(1, 2, (short) 6, (short) 6);
	

	/** Cell样式库 */
	public static Map<String, XSSFCellStyle> cellStyleMap = new HashMap<String, XSSFCellStyle>();
	
	

	
	/**
	 * 单个计量SHEET表
	 * Excel xlsx
	 * @param wb
	 * @param sheetName
	 * @param measureDatas
	 * @param defaultColor 
	 */
	public static void createMeasureSheet(XSSFWorkbook wb, String sheetName, 
			List<MeasureDotDO> measureDatas, String workMode) {
		
    	
		
		XSSFSheet stepSheet = wb.createSheet(sheetName);
        /** 设置tab颜色*/
		setTabColor(stepSheet, workMode);
		
		XSSFCell cell;
		int rowIndex = 0; 
		
		stepSheet.addMergedRegion(region);
		stepSheet.addMergedRegion(region2);
		stepSheet.addMergedRegion(region3);
		

		/** 头部 三行 说明区 */
		XSSFRow row = stepSheet.createRow(rowIndex++);
		cell = row.createCell(0);
		cell.setCellStyle(cellStyleMap.get("styleCommonHeader"));
		cell.setCellValue(tipHeaderL1[0]);
		
		cell = row.createCell(1);
		cell.setCellStyle(cellStyleMap.get("styleCommonHeader"));
		cell.setCellValue(tipHeaderL1[1]);
		
		cell = row.createCell(2);
		cell.setCellStyle(cellStyleMap.get("styleCommonHeader"));
		cell.setCellValue(tipHeaderL1[2]);
		
		cell = row.createCell(5);
		cell.setCellStyle(cellStyleMap.get("styleCommonHeader"));
		cell.setCellValue(tipHeaderL1[3]);
		
		cell = row.createCell(6);
		cell.setCellStyle(cellStyleMap.get("styleCommonR"));
		cell.setCellValue(tipHeaderL1[4]);
		/******************/
		row = stepSheet.createRow(rowIndex++);
		
		cell = row.createCell(0);
		cell.setCellStyle(cellStyleMap.get("styleCommonBG_GREEN"));
		
		cell = row.createCell(1);
		cell.setCellStyle(cellStyleMap.get("styleCommon"));
		cell.setCellValue(tipHeaderL2[0]);
		
		cell = row.createCell(2);
		cell.setCellStyle(cellStyleMap.get("styleCommon"));
		cell.setCellValue(tipHeaderL2[1]);
		
		cell = row.createCell(3);
		cell.setCellStyle(cellStyleMap.get("styleCommon"));
		cell.setCellValue(tipHeaderL2[2]);
		
		cell = row.createCell(4);
		cell.setCellStyle(cellStyleMap.get("styleCommon"));
		cell.setCellValue(tipHeaderL2[3]);
		
		cell = row.createCell(5);
		cell.setCellStyle(cellStyleMap.get("styleCommonRegionTLR"));
		cell.setCellValue(tipHeaderL2[4]);
		
		cell = row.createCell(6);
		cell.setCellStyle(cellStyleMap.get("styleCommonRegionTR"));
		cell.setCellValue(tipHeaderL2[5]);
		/******************/
		row = stepSheet.createRow(rowIndex++);
		
		cell = row.createCell(0);
		cell.setCellStyle(cellStyleMap.get("styleCommonBG_RED"));
		
		cell = row.createCell(1);
		cell.setCellStyle(cellStyleMap.get("styleCommonB"));
		cell.setCellValue(tipHeaderL3[0]);
		
		cell = row.createCell(2);
		cell.setCellStyle(cellStyleMap.get("styleCommonB"));
		cell.setCellValue(tipHeaderL3[1]);
		
		cell = row.createCell(3);
		cell.setCellStyle(cellStyleMap.get("styleCommonB"));
		cell.setCellValue(tipHeaderL3[2]);
		
		cell = row.createCell(4);
		cell.setCellStyle(cellStyleMap.get("styleCommonB"));
		cell.setCellValue(tipHeaderL3[3]);
		
		cell = row.createCell(5);
		cell.setCellStyle(cellStyleMap.get("styleCommonRegionBLR"));
		
		cell = row.createCell(6);
		cell.setCellStyle(cellStyleMap.get("styleCommonRegionBR"));
		
		
		
		//空白行
		rowIndex++;
		
		row = stepSheet.createRow(rowIndex++);

		 
		
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				cell = row.createCell(i);
				cell.setCellStyle(headers.length == i + 1 ? cellStyleMap.get("titleStyleTR") : cellStyleMap.get("titleStyle"));
				cell.setCellValue(headers[i]);
 	 			stepSheet.setColumnWidth(i, getAdjustCellWidth(headers[i], 2d));
			}
		}
		
		List<List<Object>> rowDatas = new ArrayList<List<Object>>();
		for (MeasureDotDO dot : measureDatas) {
			List<Object> rowTmp = new ArrayList<Object>();
			rowTmp.add(dot.getChannel().getDeviceChnIndex() + 1 + "");
			rowTmp.add(CommonUtil.formatNumber(dot.getCalculateDot(), 0));
			rowTmp.add(CommonUtil.formatNumber(dot.getMeterVal(), 3));
			rowTmp.add(CommonUtil.formatNumber(dot.getFinalAdc(), 3));
			rowTmp.add(CommonUtil.formatNumber(dot.getMeterVal() - dot.getCalculateDot(), 3));
			rowTmp.add(CommonUtil.formatNumber(dot.getFinalAdc() - dot.getCalculateDot(), 3));
			String result = CommonUtil.isNullOrEmpty(dot.getResult()) 
					|| dot.getResult().toLowerCase().equals("fail") ? "NG" : "OK"; 
			rowTmp.add(result);
			rowDatas.add(rowTmp);
		}
		
	
		
        for(int num = 0;num < rowDatas.size();num++) {
 			
 			row = stepSheet.createRow(rowIndex++);
 			
 			List<Object> data = rowDatas.get(num);
 			
 			for (int i = 0; i < data.size(); i++) {
 	 			
 	 			cell = row.createCell((short) i);

 	 			Object obj = data.get(i);
 	 			
 	 			XSSFCellStyle defaultStyle = cellStyleMap.get("styleCommon");
 	 			
 	 			if(data.size() == i + 1) {
 	 				String result = obj + "";
 	 				defaultStyle =  result.equals("OK") ? 
 	 						cellStyleMap.get("styleCommonR_GREEN") : cellStyleMap.get("styleCommonR_RED");
 	 				
 	 			}
 	 			
                /** 无指定样式，则使用默认样式*/
 	 			cell.setCellStyle(defaultStyle);
 	 			
 	 			if(obj instanceof Double){
                    cell.setCellValue((double)obj);
                }else if(obj instanceof Integer){
                    cell.setCellValue((int)obj);
                }else if(obj instanceof String) {
                	 cell.setCellValue(obj + "");
                }else{
                	
                    cell.setCellValue(obj + "");
                }
 	 			
 	 			defaultStyle = null;
 	 			
 			}
 			
 		}
	}
	
	/**
	 * 设置sheet tab 颜色
	 * @param stepSheet
	 * @param workMode
	 */
	private static void setTabColor(XSSFSheet stepSheet, String workMode) {
		
		java.awt.Color defaultColor = COLOR_GREEN;
		
		if(!CommonUtil.isNullOrEmpty(workMode) && !workMode.toLowerCase().equals(WorkMode.CCC.toString())) {
			if(workMode.toLowerCase().contains(WorkMode.CVC.toString())) {
				defaultColor = COLOR_BLUE;
			}else if(workMode.toLowerCase().contains(WorkMode.CDC.toString())) {
				defaultColor = COLOR_YELLOW;
			}
		}
		XSSFColor color = new XSSFColor(defaultColor);
		stepSheet.setTabColor(color);
	}
	
	/**
	 * 设置样式库
	 * @param wb
	 */
	public static void createCellStyleMap(XSSFWorkbook wb) {
		
		cellStyleMap.clear();
		
		XSSFFont  boldFont = setBoldFont(wb, "Arial", 10, true);
		XSSFFont commonFont = setBoldFont(wb, "Arial", 10, false);
		
		XSSFCellStyle styleCommon = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommon", styleCommon);
		 
		 XSSFCellStyle styleCommonHeader = createXSSFCellStyle(wb, boldFont, HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("styleCommonHeader", styleCommonHeader);
		 
		 XSSFCellStyle styleCommonBG_RED = createXSSFCellStyle(wb, BORDER_COLOR_RED, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("styleCommonBG_RED", styleCommonBG_RED);
		 
		 XSSFCellStyle styleCommonBG_GREEN = createXSSFCellStyle(wb, BORDER_COLOR_GREEN, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("styleCommonBG_GREEN", styleCommonBG_GREEN);
		 
		 XSSFCellStyle styleCommonR = createXSSFCellStyle(wb, boldFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		 cellStyleMap.put("styleCommonR", styleCommonR);
		 
		 XSSFCellStyle styleCommonR_GREEN = createXSSFCellStyle(wb, BORDER_COLOR_GREEN, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		 cellStyleMap.put("styleCommonR_GREEN", styleCommonR_GREEN);
		 
		 XSSFCellStyle styleCommonR_RED = createXSSFCellStyle(wb, BORDER_COLOR_RED, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		 cellStyleMap.put("styleCommonR_RED", styleCommonR_RED);
		 
		 XSSFCellStyle styleCommonB = createXSSFCellStyle(wb, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("styleCommonB", styleCommonB);
		 
		 XSSFCellStyle styleCommonRegionTLR = createXSSFCellStyle(wb, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 (short) -1, null,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("styleCommonRegionTLR", styleCommonRegionTLR);
		 
		 XSSFCellStyle styleCommonRegionTR = createXSSFCellStyle(wb, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 (short) -1, null,
				 (short) -1, null,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		 cellStyleMap.put("styleCommonRegionTR", styleCommonRegionTR);
		 
		 XSSFCellStyle styleCommonRegionBLR = createXSSFCellStyle(wb, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 (short) -1, null,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("styleCommonRegionBLR", styleCommonRegionBLR);
		 
		 XSSFCellStyle styleCommonRegionBR = createXSSFCellStyle(wb, commonFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 (short) -1, null,
				 (short) -1, null,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		 cellStyleMap.put("styleCommonRegionBR", styleCommonRegionBR);
		 
		 XSSFCellStyle titleStyle = createXSSFCellStyle(wb, boldFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		 cellStyleMap.put("titleStyle", titleStyle);
		 
		 XSSFCellStyle titleStyleTR = createXSSFCellStyle(wb, boldFont, 
				 HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				 BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		 cellStyleMap.put("titleStyleTR", titleStyleTR);
		 
		 
	}
	
	private static XSSFCellStyle createXSSFCellStyle(XSSFWorkbook wb, XSSFFont font,
			HorizontalAlignment hAlign, VerticalAlignment vAlign,
			short topColor, BorderStyle topBorder,
			short leftColor, BorderStyle leftBorder,
			short bottomColor, BorderStyle bottomBorder,
			short rightColor, BorderStyle rightBorder) {
		return createXSSFCellStyle(wb, -1, font, hAlign, vAlign,
				topColor, topBorder, leftColor, leftBorder,
				bottomColor, bottomBorder, rightColor, rightBorder);
	}
	
	/**
	 * 设置样式
	 * @param wb
	 * @param font
	 * @param hAlign
	 * @param vAlign
	 * @param topColor
	 * @param topBorder
	 * @param leftColor
	 * @param leftBorder
	 * @param bottomColor
	 * @param bottomBorder
	 * @param rightColor
	 * @param rightBorder
	 * @return
	 */
	private static XSSFCellStyle createXSSFCellStyle(XSSFWorkbook wb, int fg, XSSFFont font,
			HorizontalAlignment hAlign, VerticalAlignment vAlign,
			short topColor, BorderStyle topBorder,
			short leftColor, BorderStyle leftBorder,
			short bottomColor, BorderStyle bottomBorder,
			short rightColor, BorderStyle rightBorder) {
		
		 XSSFCellStyle styleCommon = wb.createCellStyle();
		 
		 if(fg >= 0) {
			 styleCommon.setFillForegroundColor((short)fg);
			 styleCommon.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		 }
		 
		 styleCommon.setFont(font);
		 styleCommon.setAlignment(hAlign);
		 styleCommon.setVerticalAlignment(vAlign);
		 
		 if(topColor > -1) {
		     styleCommon.setTopBorderColor(topColor);
		     styleCommon.setBorderTop(topBorder);
		 }
		 if(leftColor > -1) {
		     styleCommon.setLeftBorderColor(leftColor);
		     styleCommon.setBorderLeft(leftBorder);
		 }
		 if(bottomColor > -1) {
			 styleCommon.setBottomBorderColor(bottomColor);
			 styleCommon.setBorderBottom(bottomBorder);
		 }
		 if(rightColor > -1) {
			 styleCommon.setRightBorderColor(rightColor);
			 styleCommon.setBorderRight(rightBorder);
		 }
		 return styleCommon;
	}
	
	/**
	 * 设置字体
	 * @param wb
	 * @param fontName
	 * @param height
	 * @param bold
	 * @return
	 */
	private static XSSFFont setBoldFont(XSSFWorkbook wb, String fontName, int height, boolean bold) {
		
		XSSFFont  boldFont = wb.createFont();
		boldFont.setFontName(fontName);
		boldFont.setBold(bold);
		//font.setColor(HSSFColor.WHITE.index);
		boldFont.setFontHeightInPoints((short) height);
		
		return boldFont;
	}
	
	/**
	 * 等比例宽度调整
	 * @param value
	 * @param scale
	 * @return
	 */
    public static int getAdjustCellWidth(String value, double scale) {
		
		return (int)(value.getBytes().length * scale * 256 > 3072 ? value.getBytes().length * scale * 256 : 3072);
	}
	    

}
