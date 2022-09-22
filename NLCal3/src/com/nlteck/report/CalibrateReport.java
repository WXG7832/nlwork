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
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot;
import com.nltecklib.protocol.li.PCWorkform.UploadTestDot.TestType;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData;

/**
 * @author wavy_zheng
 * @version 创建时间：2022年2月15日 下午3:15:16 校准报表对象模型
 */
public class CalibrateReport {

	private final static String[] headers = new String[] { "通道号", "模片", "类型", "模式", "极性", "校准点/计量点", "ADC", "万用表值",
			"adc偏差", "表值偏差", "档位", "备份ADC1", "回检ADC2", "程控K", "程控B", "adcK", "adcB", "backAdcK1", "backAdcB1",
			"checkAdcK2", "checkAdcB2", "结果" };

	private static java.awt.Color COLOR_GREEN = new java.awt.Color(0, 176, 80);
	private static java.awt.Color COLOR_YELLOW = new java.awt.Color(255, 255, 0);
	private static java.awt.Color COLOR_BLUE = new java.awt.Color(0, 176, 240);

	/** Cell样式库 */
	public static Map<String, XSSFCellStyle> cellStyleMap = new HashMap<String, XSSFCellStyle>();

	/** 常用的边框色 */
	private final static short BORDER_COLOR_BLACK = IndexedColors.BLACK1.getIndex();
	private final static short BORDER_COLOR_RED = IndexedColors.RED.getIndex();
	private final static short BORDER_COLOR_GREEN = IndexedColors.SEA_GREEN.getIndex();

	/** 常用的虚线 */
	private static BorderStyle BORDER_STYLE_DOTTED = BorderStyle.DOTTED;
	private static BorderStyle BORDER_STYLE_THIN = BorderStyle.THIN;
    
	public static  List<UploadTestDot> importCalSheet(XSSFWorkbook wb , int chnIndex ) throws Exception {
		
		XSSFSheet stepSheet = wb.getSheetAt(0);
		if(stepSheet == null) {
			
			throw new Exception("无法读取到sheet1数据");
		}
		List<UploadTestDot> datas = new ArrayList<>();
		for(int n = 1 ; n <= stepSheet.getLastRowNum() ; n++ ) {
			
			XSSFRow row =  stepSheet.getRow(n);
			UploadTestDot dot = new UploadTestDot();
			dot.chnIndex = chnIndex;
			
			int colIndex = 1;
			//模片号
			XSSFCell cell = row.getCell(colIndex++);
			dot.moduleIndex = (int) (cell.getNumericCellValue() - 1);
			//类型
			cell = row.getCell(colIndex++);
			if("校准".equals(cell.getStringCellValue())) {
				
				dot.testType = TestType.Cal;
			} else {
				
				dot.testType = TestType.Measure;
			}
			//模式
			cell = row.getCell(colIndex++);
			dot.mode = CalMode.valueOf(cell.getStringCellValue());
			
			//极性
			cell = row.getCell(colIndex++);
			dot.pole = cell.getStringCellValue().equals("+") ? PoleData.Pole.NORMAL : PoleData.Pole.REVERSE;
			
			//校准计量点
			cell = row.getCell(colIndex++);
			dot.programVal = cell.getNumericCellValue();
			
			//adc
			cell = row.getCell(colIndex++);
			dot.adc = cell.getNumericCellValue();
			
			//meter
			cell = row.getCell(colIndex++);
			dot.meterVal = cell.getNumericCellValue();
			
			colIndex += 2;
			
			//range 档位
			cell = row.getCell(colIndex++);
			dot.precision = (int)cell.getNumericCellValue();
			
			//backAdc1
			cell = row.getCell(colIndex++);
			dot.checkAdc = cell.getNumericCellValue();
			
			//checkAdc2
			cell = row.getCell(colIndex++);
			dot.adc2 = cell.getNumericCellValue();
			
			//programK
			cell = row.getCell(colIndex++);
			dot.programK = cell.getNumericCellValue();
			
			//programB
			cell = row.getCell(colIndex++);
			dot.programB = cell.getNumericCellValue();
			
			//adcK
			cell = row.getCell(colIndex++);
			dot.adcK = cell.getNumericCellValue();
			
			//adcB
			cell = row.getCell(colIndex++);
			dot.adcB = cell.getNumericCellValue();
			
			
			//adcK
			cell = row.getCell(colIndex++);
			dot.checkAdcK = cell.getNumericCellValue();
			
			//adcB
			cell = row.getCell(colIndex++);
			dot.checkAdcB = cell.getNumericCellValue();
			
			
			//adcK
			cell = row.getCell(colIndex++);
			dot.adcK2 = cell.getNumericCellValue();
			
			//adcB
			cell = row.getCell(colIndex++);
			dot.adcB2 = cell.getNumericCellValue();
			
			
			//result
			cell = row.getCell(colIndex++);
			dot.success = cell.getStringCellValue().equalsIgnoreCase("pass");
			
			datas.add(dot);
		}
		
		return datas;
		
	}
	
	
	public static void createCalSheet(XSSFWorkbook wb, String sheetName, List<UploadTestDot> datas) {

		XSSFSheet stepSheet = wb.createSheet(sheetName);
		int rowIndex = 0;
		XSSFCell cell;

		XSSFRow row = stepSheet.createRow(rowIndex++);
		// 创建标题
		for (int n = 0; n < headers.length; n++) {

			cell = row.createCell(n);
			cell.setCellStyle(cellStyleMap.get("styleCommonHeader"));
			cell.setCellValue(headers[n]);
		}

		for (int n = 0; n < datas.size(); n++) {

			row = stepSheet.createRow(rowIndex++);
           
			

			UploadTestDot dot = datas.get(n);

			XSSFCellStyle defaultStyle = cellStyleMap.get("styleCommon");

			short cellCol = 0;
			
			//通道号
			cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.chnIndex + 1);
			
			//模片
             cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.moduleIndex + 1);
			
			//类型
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.testType == TestType.Cal ? "校准":"计量");
			
			//模式
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.mode.name());
			
			
			//极性
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.pole == PoleData.Pole.NORMAL ? "+":"-");
			
			//计量点/校准点
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.programVal);
			
			
			//ADC
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.adc);
			
			//表值
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.meterVal);
			
			//adc偏差
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			if(dot.testType == TestType.Measure) {
			   cell.setCellValue(dot.adc - dot.programVal);
			}
			
			//表偏差
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			if(dot.testType == TestType.Measure) {
			   cell.setCellValue(dot.meterVal - dot.programVal);
			}
			
			
			//档位
			
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.precision);
			
			
			//备份ADC1
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.checkAdc);
			
			
			//回检ADC2
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.adc2);
			
			
			//程控K
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.programK);
			
			//程控B
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.programB);
			
			
			//adcK
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.adcK);
			
			
			//adcB
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.adcB);
			
			
			//adcK1
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.checkAdcK);
			
			//adcB1
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.checkAdcB);
			
			
			//adcK2
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.adcK2);
			
			//adcB2
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(defaultStyle);
			
			cell.setCellValue(dot.adcB2);
			
			
			//结果
            cell = row.createCell(cellCol++);
			
			/** 无指定样式，则使用默认样式 */
			cell.setCellStyle(dot.success ? cellStyleMap.get("styleCommonR_GREEN") : cellStyleMap.get("styleCommonR_RED"));
			
			cell.setCellValue(dot.success ? "pass":"fail");
			
			

			
			

		}

	}

	/**
	 * 设置sheet tab 颜色
	 * 
	 * @param stepSheet
	 * @param workMode
	 */
	private static void setTabColor(XSSFSheet stepSheet, String workMode) {

		java.awt.Color defaultColor = COLOR_GREEN;

		if (!CommonUtil.isNullOrEmpty(workMode) && !workMode.toLowerCase().equals(WorkMode.CCC.toString())) {
			if (workMode.toLowerCase().contains(WorkMode.CVC.toString())) {
				defaultColor = COLOR_BLUE;
			} else if (workMode.toLowerCase().contains(WorkMode.CDC.toString())) {
				defaultColor = COLOR_YELLOW;
			}
		}
		XSSFColor color = new XSSFColor(defaultColor);
		stepSheet.setTabColor(color);
	}

	/**
	 * 设置字体
	 * 
	 * @param wb
	 * @param fontName
	 * @param height
	 * @param bold
	 * @return
	 */
	private static XSSFFont setBoldFont(XSSFWorkbook wb, String fontName, int height, boolean bold) {

		XSSFFont boldFont = wb.createFont();
		boldFont.setFontName(fontName);
		boldFont.setBold(bold);
		// font.setColor(HSSFColor.WHITE.index);
		boldFont.setFontHeightInPoints((short) height);

		return boldFont;
	}

	/**
	 * 设置样式库
	 * 
	 * @param wb
	 */
	public static void createCellStyleMap(XSSFWorkbook wb) {

		cellStyleMap.clear();

		XSSFFont boldFont = setBoldFont(wb, "Arial", 10, true);
		XSSFFont commonFont = setBoldFont(wb, "Arial", 10, false);

		XSSFCellStyle styleCommon = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommon", styleCommon);

		XSSFCellStyle styleCommonHeader = createXSSFCellStyle(wb, boldFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommonHeader", styleCommonHeader);

		XSSFCellStyle styleCommonBG_RED = createXSSFCellStyle(wb, BORDER_COLOR_RED, commonFont,
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_THIN, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommonBG_RED", styleCommonBG_RED);

		XSSFCellStyle styleCommonBG_GREEN = createXSSFCellStyle(wb, BORDER_COLOR_GREEN, commonFont,
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_THIN, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommonBG_GREEN", styleCommonBG_GREEN);

		XSSFCellStyle styleCommonR = createXSSFCellStyle(wb, boldFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		cellStyleMap.put("styleCommonR", styleCommonR);

		XSSFCellStyle styleCommonR_GREEN = createXSSFCellStyle(wb, BORDER_COLOR_GREEN, commonFont,
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_THIN);
		cellStyleMap.put("styleCommonR_GREEN", styleCommonR_GREEN);

		XSSFCellStyle styleCommonR_RED = createXSSFCellStyle(wb, BORDER_COLOR_RED, commonFont,
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED,
				BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_THIN);
		cellStyleMap.put("styleCommonR_RED", styleCommonR_RED);

		XSSFCellStyle styleCommonB = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_THIN, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommonB", styleCommonB);

		XSSFCellStyle styleCommonRegionTLR = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, (short) -1, null, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommonRegionTLR", styleCommonRegionTLR);

		XSSFCellStyle styleCommonRegionTR = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, (short) -1, null, (short) -1, null,
				BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		cellStyleMap.put("styleCommonRegionTR", styleCommonRegionTR);

		XSSFCellStyle styleCommonRegionBLR = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, (short) -1, null, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK,
				BORDER_STYLE_THIN, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("styleCommonRegionBLR", styleCommonRegionBLR);

		XSSFCellStyle styleCommonRegionBR = createXSSFCellStyle(wb, commonFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, (short) -1, null, (short) -1, null, BORDER_COLOR_BLACK, BORDER_STYLE_THIN,
				BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		cellStyleMap.put("styleCommonRegionBR", styleCommonRegionBR);

		XSSFCellStyle titleStyle = createXSSFCellStyle(wb, boldFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_THIN, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED);
		cellStyleMap.put("titleStyle", titleStyle);

		XSSFCellStyle titleStyleTR = createXSSFCellStyle(wb, boldFont, HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER, BORDER_COLOR_BLACK, BORDER_STYLE_THIN, BORDER_COLOR_BLACK,
				BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_DOTTED, BORDER_COLOR_BLACK, BORDER_STYLE_THIN);
		cellStyleMap.put("titleStyleTR", titleStyleTR);

	}

	private static XSSFCellStyle createXSSFCellStyle(XSSFWorkbook wb, XSSFFont font, HorizontalAlignment hAlign,
			VerticalAlignment vAlign, short topColor, BorderStyle topBorder, short leftColor, BorderStyle leftBorder,
			short bottomColor, BorderStyle bottomBorder, short rightColor, BorderStyle rightBorder) {
		return createXSSFCellStyle(wb, -1, font, hAlign, vAlign, topColor, topBorder, leftColor, leftBorder,
				bottomColor, bottomBorder, rightColor, rightBorder);
	}

	private static XSSFCellStyle createXSSFCellStyle(XSSFWorkbook wb, int fg, XSSFFont font, HorizontalAlignment hAlign,
			VerticalAlignment vAlign, short topColor, BorderStyle topBorder, short leftColor, BorderStyle leftBorder,
			short bottomColor, BorderStyle bottomBorder, short rightColor, BorderStyle rightBorder) {

		XSSFCellStyle styleCommon = wb.createCellStyle();

		if (fg >= 0) {
			styleCommon.setFillForegroundColor((short) fg);
			styleCommon.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}

		styleCommon.setFont(font);
		styleCommon.setAlignment(hAlign);
		styleCommon.setVerticalAlignment(vAlign);

		if (topColor > -1) {
			styleCommon.setTopBorderColor(topColor);
			styleCommon.setBorderTop(topBorder);
		}
		if (leftColor > -1) {
			styleCommon.setLeftBorderColor(leftColor);
			styleCommon.setBorderLeft(leftBorder);
		}
		if (bottomColor > -1) {
			styleCommon.setBottomBorderColor(bottomColor);
			styleCommon.setBorderBottom(bottomBorder);
		}
		if (rightColor > -1) {
			styleCommon.setRightBorderColor(rightColor);
			styleCommon.setBorderRight(rightBorder);
		}
		return styleCommon;
	}

}
