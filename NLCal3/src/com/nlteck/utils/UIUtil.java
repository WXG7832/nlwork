package com.nlteck.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.nlteck.resources.Resources;

/**
 * UI资源工具包
 * 
 * @author Administrator
 *
 */
public class UIUtil {

    public static final double FACTOR = 0.7;
    private static ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());;

    /**
     * 电池一些常量定义
     */
    public final static int MIN_CELL_WIDTH = 100;
    public final static int MIN_CELL_HEIGHT = 150;
    public final static int MIN_CELL_HEAD_HEIGHT = 8;
    public final static int MIN_CELL_HEAD_WIDTH = 15;
    public final static int CELL_MARGIN_HORZ = 5;
    public final static int CELL_MARGIN_VERT = 10;
    public final static int MARGIN_LEFT = 30;
    public final static int ROW_COUNT_DISPLAY = 4;
    public final static int DRIVER_INDEX_LEFT = 5;

    /**
     * 颜色常量
     */
    public final static Color COLOR_SILVER = SWTResourceManager.getColor(152, 152, 152);
    public final static Color COLOR_WHITE = SWTResourceManager.getColor(255, 255, 255);
    public final static Color COLOR_24K_GOLD = SWTResourceManager.getColor(218, 178, 115);
    public final static Color COLOR_SELECT = SWTResourceManager.getColor(0, 44, 221);

    /**
     * 画圆柱电池
     * 
     * @param gc           GraphcisContext
     * @param rectangle    画电池的矩形区域
     * @param batteryColor 电池的颜色
     * @param isPositive   电池的正负极标识是否颠倒
     */
    public static void drawAwesomeBattery(GC gc, Rectangle rectangle, Color batteryColor, boolean isPositive) {
	Color backgroundColor = gc.getBackground();
	Color foregroundColor = gc.getForeground();

	int x = rectangle.x;
	int y = rectangle.y;
	int l = rectangle.width;
	int h = rectangle.height;

	int margin = l / 10;

	int headl = l / 4;
	int headh = h / 18;
	int headx = x + margin + l / 2 - margin - headl / 2;
	int heady = y + margin;

	int headLeftGraduall = headl / 2 - 4;
	int headLeftGradualh = headh;
	int headLeftGradualx = headx + 4;
	int headLeftGradualy = heady;

	int headRightGraduall = headLeftGraduall;
	int headRightGradualh = headLeftGradualh;
	int headRightGradualx = headLeftGradualx + headLeftGraduall;
	int headRightGradualy = headLeftGradualy;

	int difl = 8;
	int difh = 3;

	int innerx = x + margin + difl;
	int innery = y + margin + headh;
	int innerl = l - 2 * margin - 2 * difl;
	int innerh = h - 2 * margin - headh;

	int innerLeftGradualx = innerx + 2;
	int innerLeftGradualy = innery;
	int innerLeftGraduall = (innerl - 4) / 2;
	int innerLeftGradualh = innerh;

	int innerRightGradualx = innerLeftGradualx + innerLeftGraduall;
	int innerRightGradualy = innery;
	int innerRightGraduall = innerLeftGraduall;
	int innerRightGradualh = innerh;

	int outx = x + margin;
	int outy = y + margin + headh + difh;
	int outl = l - 2 * margin;
	int outh = h - 2 * margin - headh - 2 * difh;

	int outLeftGradualx = outx + difl;
	int outLeftGradualy = outy;
	int outLeftGraduall = outl / 2 - difl;
	int outLeftGradualh = outh;

	int outRightGradualx = outLeftGradualx + outLeftGraduall;
	int outRightGradualy = outy;
	int outRightGraduall = outLeftGraduall;
	int outRightGradualh = outLeftGradualh;

	int pos_xAixsl = outl / 3;
	int pos_xAixsh = outh / 30;
	int pos_xAixsx = outl / 2 - pos_xAixsl / 2 + outx;
	int pos_xAixsy = outy + outh / 10;

	int pos_yAixsl = pos_xAixsh;
	int pos_yAixsh = pos_xAixsl;
	int pos_yAixsx = outx + (outl - pos_yAixsl) / 2;
	int pos_yAixsy = pos_xAixsy - (pos_yAixsh - pos_yAixsl) / 2;

	int neg_xAixsl = pos_xAixsl;
	int neg_xAixsh = pos_xAixsh;
	int neg_xAixsx = pos_xAixsx;
	int neg_xAixsy = outy + outh - outh / 10 - pos_xAixsh;

	int neg_yAixsl = pos_xAixsh;
	int neg_yAixsh = pos_xAixsl;
	int neg_yAixsx = outx + (outl - pos_yAixsl) / 2;
	int neg_yAixsy = neg_xAixsy - (pos_yAixsh - pos_yAixsl) / 2;

	gc.setBackground(COLOR_SILVER);
	gc.fillRoundRectangle(headx, heady, headl, headh + 20, 12, 10);
	// 电池头左渐变
	gc.setAlpha(200);
	gc.setBackground(COLOR_WHITE);
	gc.setForeground(COLOR_SILVER);
	gc.fillGradientRectangle(headLeftGradualx, headLeftGradualy, headLeftGraduall, headLeftGradualh, false);
	// 电池头右渐变
	gc.setBackground(COLOR_SILVER);
	gc.setForeground(COLOR_WHITE);
	gc.fillGradientRectangle(headRightGradualx, headRightGradualy, headRightGraduall, headRightGradualh, false);
	gc.setAlpha(255);

	gc.fillRoundRectangle(innerx, innery, innerl, innerh, 10, 10);
	// 电池内芯左渐变
	gc.setAlpha(200);
	gc.setBackground(COLOR_WHITE);
	gc.setForeground(COLOR_SILVER);
	gc.fillGradientRectangle(innerLeftGradualx, innerLeftGradualy, innerLeftGraduall, innerLeftGradualh, false);
	// 电池内芯右渐变
	gc.setBackground(COLOR_SILVER);
	gc.setForeground(COLOR_WHITE);
	gc.fillGradientRectangle(innerRightGradualx, innerRightGradualy, innerRightGraduall, innerRightGradualh, false);
	gc.setAlpha(255);

	gc.setBackground(batteryColor);
	gc.fillRoundRectangle(outx, outy, outl, outh, 15, 15);

	gc.setBackground(COLOR_24K_GOLD);
	gc.fillRoundRectangle(pos_xAixsx, pos_xAixsy, pos_xAixsl, pos_xAixsh, 5, 5);
	gc.fillRoundRectangle(neg_xAixsx, neg_xAixsy, neg_xAixsl, neg_xAixsh, 5, 5);
	if (isPositive)
	    gc.fillRoundRectangle(pos_yAixsx, pos_yAixsy, pos_yAixsl, pos_yAixsh, 5, 5);
	else
	    gc.fillRoundRectangle(neg_yAixsx, neg_yAixsy, neg_yAixsl, neg_yAixsh, 5, 5);
	// 电池身体左渐变
	gc.setAlpha(150);
	gc.setBackground(COLOR_WHITE);
	gc.setForeground(batteryColor);
	gc.fillGradientRectangle(outLeftGradualx, outLeftGradualy, outLeftGraduall, outLeftGradualh, false);
	// 电池身体右渐变
	gc.setBackground(batteryColor);
	gc.setForeground(COLOR_WHITE);
	gc.fillGradientRectangle(outRightGradualx, outRightGradualy, outRightGraduall, outRightGradualh, false);
	gc.setAlpha(255);

	// 还原颜色
	gc.setBackground(backgroundColor);
	gc.setForeground(foregroundColor);
    }

    /**
     * 画一个普通多边形电池
     * 
     * @param gc           GraphicsContext
     * @param points       多边形点数组
     * @param batteryColor 电池颜色
     * @param isSelect     是否为选中状态
     */
    public static void drawSimpleBattery(GC gc, Rectangle batteryRectangle, Color batteryColor, boolean isSelect) {
	Point[] points = UIUtil.getPointsFromRectangle(batteryRectangle);
	Color backgroundColor = Resources.NONE_CLR;
	Color foregroundColor = Resources.COLOR_BLACK;
	int lineWidth = 3;
	Point point1_1 = new Point(points[1].x - lineWidth, points[1].y + lineWidth);
	Point point2_1 = new Point(points[2].x - lineWidth, points[2].y + lineWidth);
	Point point3_1 = new Point(points[3].x - lineWidth, points[3].y + lineWidth);
	Point point4_1 = new Point(points[4].x - lineWidth, points[4].y - lineWidth);
	Point point5_1 = new Point(points[5].x + lineWidth, points[5].y - lineWidth);
	Point point6_1 = new Point(points[6].x + lineWidth, points[6].y + lineWidth);
	Point point7_1 = new Point(points[7].x + lineWidth, points[7].y + lineWidth);
	Point point0_1 = new Point(points[0].x + lineWidth, points[0].y + lineWidth);
	if (isSelect) {
	    gc.setBackground(Resources.COLOR_SELECT);
	    gc.fillPolygon(UIUtil.getIntsFromPoints(points));
	    gc.setBackground(UIUtil.COLOR_WHITE);
	    gc.fillPolygon(UIUtil.getIntsFromPoints(new Point[] { points[1], points[2], points[3], points[4], points[5], point5_1, point4_1, point3_1, point2_1, point1_1, }));
	    gc.setBackground(UIUtil.COLOR_SILVER);
	    gc.fillPolygon(UIUtil.getIntsFromPoints(new Point[] { points[5], points[6], points[7], points[0], points[1], point1_1, point0_1, point7_1, point6_1, point5_1, }));
	} else {
	    gc.setBackground(batteryColor);
	    gc.fillPolygon(UIUtil.getIntsFromPoints(points));
	    gc.setBackground(UIUtil.COLOR_SILVER);
	    gc.fillPolygon(UIUtil.getIntsFromPoints(new Point[] { points[1], points[2], points[3], points[4], points[5], point5_1, point4_1, point3_1, point2_1, point1_1, }));
	    gc.setBackground(UIUtil.COLOR_WHITE);
	    gc.fillPolygon(UIUtil.getIntsFromPoints(new Point[] { points[5], points[6], points[7], points[0], points[1], point1_1, point0_1, point7_1, point6_1, point5_1, }));
	}
	gc.setBackground(backgroundColor);
	gc.setForeground(foregroundColor);
    }

    /**
     * 从一个矩形区域中获得多边形电池的顶点数组
     * 
     * @param rectangle
     * @return
     */
    public static Point[] getPointsFromRectangle(Rectangle rectangle) {
	int margin = 6;

	int x = rectangle.x;
	int y = rectangle.y;
	int w = rectangle.width;
	int h = rectangle.height;

	int headw = w / 6;
	int headh = headw / 2;

	int bodyw = w - 2 * margin;
	int bodyh = h - 2 * margin - headh;

	int headLeftTopPointx = x + margin + bodyw / 2 - headw / 2;
	int headLeftTopPointy = y + margin;

	int headRightTopPointx = headLeftTopPointx + headw;
	int headRightTopPointy = y + margin;

	int headRightBottomPointx = headRightTopPointx;
	int headRightBottomPointy = headRightTopPointy + headh;

	int bodyRightTopPointx = x + margin + bodyw;
	int bodyRightTopPointy = y + margin + headh;

	int bodyRightBottomPointx = bodyRightTopPointx;
	int bodyRightBottomPointy = bodyRightTopPointy + bodyh;

	int bodyLeftBottomPointx = x + margin;
	int bodyLeftBottomPointy = bodyRightBottomPointy;

	int bodyLeftTopPointx = bodyLeftBottomPointx;
	int bodyLeftTopPointy = y + margin + headh;

	int headLeftBottomPointx = headLeftTopPointx;
	int headLeftBottomPointy = headRightBottomPointy;

	return new Point[] { new Point(headLeftTopPointx, headLeftTopPointy), new Point(headRightTopPointx, headRightTopPointy), new Point(headRightBottomPointx, headRightBottomPointy), new Point(bodyRightTopPointx, bodyRightTopPointy), new Point(bodyRightBottomPointx, bodyRightBottomPointy), new Point(bodyLeftBottomPointx, bodyLeftBottomPointy), new Point(bodyLeftTopPointx, bodyLeftTopPointy), new Point(headLeftBottomPointx, headLeftBottomPointy) };
    }

    /**
     * 电池形状类型
     */
    public enum ShapeType {
	LEFT_TOP, RIGHT_BOTTOM, WHOLE
    }

    /**
     * 电池形状
     */
    public static class BatteryShape {
	public Rectangle rectangle; // 区域
	public int batHeadLeft;
	public int batHeadTop;
	public int batHeadRight;
	public int batHeadBottom;

	@Override
	public String toString() {
	    return "BatteryShape [region=" + rectangle + ", batHeadLeft=" + batHeadLeft + ", batHeadTop=" + batHeadTop + ", batHeadRight=" + batHeadRight + ", batHeadBottom=" + batHeadBottom + "]";
	}
    }

    /**
     * 设置窗口相对屏幕居中
     * 
     * @param shell 要居中的窗口
     */
    public static void setShellAlignCenter(Shell shell) {
	int clientCenterX = Display.getCurrent().getClientArea().width / 2;
	int clientCenterY = Display.getCurrent().getClientArea().height / 2;
	int shellCenterX = shell.getShell().getSize().x / 2;
	int shellCenterY = shell.getShell().getSize().y / 2;
	shell.setLocation(clientCenterX - shellCenterX, clientCenterY - shellCenterY);
    }

    /**
     * 修改面板的字体大小
     * 
     * @param composite
     * @param newSize
     */
    public static Font changeFontSize(Font font, int newSize) {
	FontData fd = font.getFontData()[0];
	fd.setHeight(newSize);
	Font newFont = new Font(font.getDevice(), fd);
	return newFont;
    }

    /**
     * 修改字体粗细
     * 
     * @param font
     * @param bold
     * @return
     */
    public static Font changeFontBold(Font font, boolean bold) {
	FontData fd = font.getFontData()[0];
	fd.setStyle(SWT.BOLD);
	Font newFont = new Font(font.getDevice(), fd);
	return newFont;
    }

    /**
     * eclipse RCP项目通过路径名创建图像标识
     * 
     * @param projectName
     * @param path
     * @return
     */
    public static ImageDescriptor createRCPImageDescriptor(String projectName, String path) {
	URL url = null;
	try {
	    url = new URL("platform:/plugin/" + projectName + "/" + path);
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
	ImageDescriptor temp = ImageDescriptor.createFromURL(url);
	return temp;
    }

    public void disposeResources() {
	resourceManager.dispose();
    }

    public static Image createRCPImage(String projectName, String path) {
	return resourceManager.createImage(createRCPImageDescriptor(projectName, path));
    }

    public static Color createColor(RGB rgb) {
	return resourceManager.createColor(rgb);
    }

    public static void destroyColor(RGB rgb) {
	resourceManager.destroyColor(rgb);
    }

    public static void destroyColor(Color clr) {
	destroyColor(clr.getRGB());
    }

    public static Font createFont(String name, int height, int style) {
	return resourceManager.createFont(FontDescriptor.createFrom(new FontData(name, height, style)));
    }

    public static Color createColor(int red, int green, int blue) {
	return resourceManager.createColor(new RGB(red, green, blue));
    }

    /**
     * 滚动
     * 
     * @param scomp
     * @param xoffset
     * @param yoffset
     */
    public static void scroll(ScrolledComposite scomp, int xoffset, int yoffset) {
	Point origin = scomp.getOrigin();
	Point contentSize = scomp.getContent().getSize();
	int xorigin = origin.x + xoffset;
	int yorigin = origin.y + yoffset;
	xorigin = Math.max(xorigin, 0);
	xorigin = Math.min(xorigin, contentSize.x - 1);
	yorigin = Math.max(yorigin, 0);
	yorigin = Math.min(yorigin, contentSize.y - 1);
	scomp.setOrigin(xorigin, yorigin);
    }

    public static void drawTextCenter(GC gc, Rectangle rect, String text, boolean isBackgroundTransparent) {
	int strWidth = computeStringWidth(gc, text);
	int strHeight = gc.getFontMetrics().getHeight();
	gc.drawText(text, rect.x + (rect.width - strWidth) / 2, rect.y + (rect.height - strHeight) / 2, isBackgroundTransparent);
    }

    /**
     * 水平居中画文字
     * 
     * @param gc
     * @param rect
     * @param text
     * @param isBackgroundTransparent
     */
    public static void drawTextHorCenter(GC gc, Rectangle rect, int y, String text, boolean isBackgroundTransparent) {
	int strWidth = computeStringWidth(gc, text);
	gc.drawString(text, (rect.width - strWidth) / 2 + rect.x, y, isBackgroundTransparent);
    }

    public static int computeStringWidth(GC gc, String content) {
	int width = 0;
	for (int i = 0; i < content.length(); i++) {
	    width += gc.getAdvanceWidth(content.charAt(i));
	}
	return width;
    }

    public static RGB darker(RGB rgb, double factor) {

	return new RGB(Math.max((int) (rgb.red * factor), 0), Math.max((int) (rgb.green * factor), 0), Math.max((int) (rgb.blue * factor), 0));
    }

    public static RGB brighter(RGB rgb, double factor) {

	int r = rgb.red;
	int g = rgb.green;
	int b = rgb.blue;

	/*
	 * From 2D group: 1. black.brighter() should return grey 2. applying brighter to
	 * blue will always return blue, brighter 3. non pure color (non zero rgb) will
	 * eventually return white
	 */
	int i = (int) (1.0 / (1.0 - factor));
	if (r == 0 && g == 0 && b == 0) {
	    return new RGB(i, i, i);
	}
	if (r > 0 && r < i)
	    r = i;
	if (g > 0 && g < i)
	    g = i;
	if (b > 0 && b < i)
	    b = i;

	return new RGB(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255));
    }

    public static Color brighter(Color clr) {

	return brighter(clr, FACTOR);
    }

    public static Color brighter(Color clr, double factor) {

	int r = clr.getRed();
	int g = clr.getGreen();
	int b = clr.getBlue();
	int alpha = clr.getAlpha();

	/*
	 * From 2D group: 1. black.brighter() should return grey 2. applying brighter to
	 * blue will always return blue, brighter 3. non pure color (non zero rgb) will
	 * eventually return white
	 */
	int i = (int) (1.0 / (1.0 - factor));
	if (r == 0 && g == 0 && b == 0) {
	    return UIUtil.createColor(i, i, i);
	}
	if (r > 0 && r < i)
	    r = i;
	if (g > 0 && g < i)
	    g = i;
	if (b > 0 && b < i)
	    b = i;

	return UIUtil.createColor(new RGB(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255)));
	// return new Color(device, Math.min((int) (r / factor), 255), Math.min((int) (g
	// / factor), 255),
	// Math.min((int) (b / factor), 255), alpha);
    }

    public static Color darker(Color clr) {

	return darker(clr, FACTOR);
    }

    public static void centerScreen(Shell shell) {
	Monitor monitor = shell.getDisplay().getPrimaryMonitor();
	Rectangle monitorRect = monitor.getBounds();
	Rectangle shellRect = shell.getBounds();
	int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
	int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
	shell.setLocation(x, y);
    }

    public static Color darker(Color clr, double factor) {

	return UIUtil.createColor(Math.max((int) (clr.getRed() * factor), 0), Math.max((int) (clr.getGreen() * factor), 0), Math.max((int) (clr.getBlue() * factor), 0));

    }

    /**
     * 判断一个点是否在任意多边形内
     * 
     * @param point
     * @param points
     * @param n
     * @return
     */
    public static boolean isInPolygon(Point point, Point[] points) {
	int nCross = 0;
	int n = points.length;
	for (int i = 0; i < n; i++) {
	    Point p1 = points[i];
	    Point p2 = points[(i + 1) % n];
	    if (p1 == null || p2 == null)
		return false;
	    // 求解 y=p.y 与 p1 p2 的交点
	    // p1p2 与 y=p0.y平行
	    if (p1.y == p2.y)
		continue;
	    // 交点在p1p2延长线上
	    if (point.y < Math.min(p1.y, p2.y))
		continue;
	    // 交点在p1p2延长线上
	    if (point.y >= Math.max(p1.y, p2.y))
		continue;
	    // 求交点的 X 坐标
	    double x = (double) (point.y - p1.y) * (double) (p2.x - p1.x) / (double) (p2.y - p1.y) + p1.x;
	    // 只统计单边交点
	    if (x > point.x)
		nCross++;
	}
	return (nCross % 2 == 1);
    }

    public static boolean isInRectangle(Point[] points, Rectangle rect) {

	for (int n = 0; n < points.length; n++) {

	    if (rect.contains(points[n])) {

		return true;
	    }
	}
	return false;
    }

    /**
     * 画3d矩形
     * 
     * @param gc
     * @param rect
     * @param raise
     */
    public static void draw3DRect(GC gc, Rectangle rect, boolean raise, int lineWidth) {

	int left = rect.x + lineWidth;
	int top = rect.y + lineWidth;
	int width = rect.width - 2 * lineWidth;
	int height = rect.height - 2 * lineWidth;

	Color blr = gc.getBackground();
	Color brighter = brighter(blr);
	Color darker = darker(blr);
	gc.setLineWidth(lineWidth);
	gc.setForeground(raise ? brighter : darker);
	gc.drawLine(left, top, left, top + height);
	gc.drawLine(left, top, left + width, top);
	gc.setForeground(raise ? darker : brighter);
	gc.drawLine(left + width - (lineWidth - 1), top, left + width - (lineWidth - 1), top + height);
	gc.drawLine(left, top + height - (lineWidth - 1), left + width, top + height - (lineWidth - 1));

	gc.setLineWidth(1);

	brighter.dispose();
	darker.dispose();

    }

    /**
     * 绘制圆角3D电池，绝对大小
     * 
     * @param gc
     * @param rectangle
     */
    public static void drawRound3DBattery(GC gc, Rectangle rectangle, boolean raise, int lineWidth) {

	int left = rectangle.x + lineWidth;
	int top = rectangle.y + lineWidth;
	int width = rectangle.width - 2 * lineWidth;
	int height = rectangle.height - 2 * lineWidth;

	Color blr = gc.getBackground();
	Color brighter = brighter(blr);
	Color darker = darker(blr);
	gc.setLineWidth(lineWidth);
	gc.setForeground(raise ? brighter : darker);
	gc.drawLine(left, top, left, top + height);
	gc.drawLine(left, top, left + width, top);
	gc.setForeground(raise ? darker : brighter);
	gc.drawLine(left + width - (lineWidth - 1), top, left + width - (lineWidth - 1), top + height);
	gc.drawLine(left, top + height - (lineWidth - 1), left + width, top + height - (lineWidth - 1));

	gc.setLineWidth(1);

	brighter.dispose();
	darker.dispose();

    }

    /**
     * 获取反色
     * 
     * @param color
     * @return
     */
    public static RGB getContrastColor(RGB color) {

	int red = 255 - color.red;
	int green = 255 - color.green;
	int blue = 255 - color.blue;

	return new RGB(red, green, blue);

    }

    public static int getColumnIndex(Table table, TableColumn column) {

	TableColumn[] columns = table.getColumns();
	for (int i = 0; i < columns.length; i++) {
	    if (columns[i].equals(column))
		return i;
	}
	return -1;
    }

    public static String[] getTableItemText(Table table, TableItem item) {

	int count = table.getColumnCount();
	String[] strs = new String[count];
	for (int i = 0; i < count; i++) {
	    strs[i] = item.getText(i);
	}
	return strs;
    }

    /**
     * 给表格列增加排序功能
     * 
     * @param table
     * @param column
     */
    public static void addSorter(final Table table, final TableColumn column) {
	column.addListener(SWT.Selection, new Listener() {
	    boolean isAscend = true;
	    Collator comparator = Collator.getInstance(Locale.getDefault());

	    public void handleEvent(Event e) {
		int columnIndex = getColumnIndex(table, column);
		TableItem[] items = table.getItems();

		for (int i = 1; i < items.length; i++) {
		    String value2 = items[i].getText(columnIndex);
		    for (int j = 0; j < i; j++) {
			String value1 = items[j].getText(columnIndex);
			boolean isLessThan = comparator.compare(value2, value1) < 0;
			if ((isAscend && isLessThan) || (!isAscend && !isLessThan)) {
			    String[] values = getTableItemText(table, items[i]);
			    Object obj = items[i].getData();
			    items[i].dispose();

			    TableItem item = new TableItem(table, SWT.NONE, j);
			    item.setText(values);
			    item.setData(obj);
			    items = table.getItems();
			    break;
			}
		    }
		}

		table.setSortColumn(column);
		table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
		isAscend = !isAscend;
	    }
	});
    }

    /**
     * 画类似APP中未读取消息的圆形标志
     * 
     * @param gc
     * @param number
     */
    public static void drawFlagCircle(GC gc, int number, Rectangle rect) {

	Color oldbgr = gc.getBackground();
	Color oldfgr = gc.getForeground();
	gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
	gc.fillOval(rect.x, rect.y, rect.width, rect.height);
	gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	drawTextCenter(gc, rect, number > 9 ? "9+" : number + "", true);
	gc.setBackground(oldbgr);
	gc.setForeground(oldfgr);

    }

    /**
     * 转化电池图形合适的区域，因电池宽度和高度有1个固定比例
     * 
     * @param region
     * @return
     */
    private static Rectangle calculateBatteryRect(Rectangle region) {
	// 以宽度为基准计算
	int cellHeight = (int) ((double) region.width * MIN_CELL_HEIGHT / MIN_CELL_WIDTH);
	int cellTop = region.y;
	if (cellHeight < region.height) {
	    // 调整位置
	    cellTop = (region.height - cellHeight) / 2 + region.y;
	}
	return new Rectangle(region.x, cellTop, region.width, cellHeight);
    }

    /**
     * 画电池图形
     * 
     * @param st
     * @param region
     * @param color       电池线条颜色
     * @param shrink      电池整体缩进尺寸
     * @param lineWidth   电池轮廓线条宽度
     * @param horzMargion 电池横向缩进尺寸
     * @param vertMargin  电池纵向缩进尺寸
     */
    public static void drawBattery(GC gc, ShapeType st, Rectangle region, Color color, int shrink, int lineWidth, int horzMargion, int vertMargin, BatteryShape shape) {
	Point[] pts = getBatteryShape(st, region, shrink, horzMargion, vertMargin, shape);
	int[] polyData = getIntsFromPoints(pts);
	Color oldClr = gc.getForeground();
	int oldLineWidth = gc.getLineWidth();
	gc.setLineWidth(lineWidth);
	gc.setForeground(color);
	gc.drawPolyline(polyData);
	// 恢复
	gc.setForeground(oldClr);
	gc.setLineWidth(oldLineWidth);
    }

    /**
     * 获取电池形状数据
     * 
     * @param shrink 缩进边框尺度
     * @return
     */
    public static Point[] getBatteryShape(ShapeType st, Rectangle region, int shrink, int horzMargion, int vertMargin, BatteryShape shape) {

	if (region == null)
	    return null;

	int width = region.width;
	int height = region.height;

	int left = horzMargion + region.x + shrink;
	int right = width - horzMargion + region.x - shrink;
	int bottom = height - vertMargin + region.y - shrink;
	int top = vertMargin + region.y + shrink;

	int batHeight = bottom - top;
	int batWidth = right - left;

	double rate = (double) MIN_CELL_HEAD_WIDTH / MIN_CELL_WIDTH;
	int batHeadWidth = (int) (batWidth * rate);
	// if (batHeadWidth < minBatHeadWidth) {
	//
	// batHeadWidth = minBatHeadWidth;
	// }
	int batHeadLeftX = (width - batHeadWidth) / 2 + region.x + shrink;
	int batHeadTopY = horzMargion + region.y + shrink;
	shape.batHeadLeft = batHeadLeftX;
	shape.batHeadTop = batHeadTopY;

	rate = (double) MIN_CELL_HEAD_HEIGHT / MIN_CELL_HEIGHT;
	int batHeadHeight = (int) (batHeight * rate);
	// if (batHeight < minBatHeadHeight) {
	//
	// batHeadHeight = minBatHeadHeight;
	// }
	int batHeadBottomY = batHeadTopY + batHeadHeight - shrink;
	int batHeadRightX = batHeadLeftX + batHeadWidth - shrink;

	shape.batHeadBottom = batHeadBottomY;
	shape.batHeadRight = batHeadRightX;

	List<Point> pts = new ArrayList<Point>();
	// Point[] pts = new Point[9];
	if (st == ShapeType.WHOLE || st == ShapeType.LEFT_TOP) {
	    pts.add(new Point(left, bottom));
	    pts.add(new Point(left, batHeadBottomY));
	    pts.add(new Point(batHeadLeftX, batHeadBottomY));
	    pts.add(new Point(batHeadLeftX, batHeadTopY));
	    pts.add(new Point(batHeadRightX, batHeadTopY));
	}
	if (st == ShapeType.WHOLE || st == ShapeType.RIGHT_BOTTOM) {

	    if (st == ShapeType.RIGHT_BOTTOM) {
		pts.add(new Point(batHeadRightX, batHeadTopY));
	    }
	    pts.add(new Point(batHeadRightX, batHeadBottomY));
	    pts.add(new Point(right, batHeadBottomY));
	    pts.add(new Point(right, bottom));
	    pts.add(new Point(left, bottom));
	}

	return pts.toArray(new Point[0]);

    }

    /**
     * 判断当前点是否处于电池形状内
     * 
     * @param pt
     * @param region
     * @return
     */
    public static boolean isInBattery(Point pt, Rectangle region, int horzMargin, int vertMargin) {

	Point[] shapes = getBatteryShape(ShapeType.WHOLE, region, 0, horzMargin, vertMargin, new BatteryShape());
	if (shapes != null)
	    return isInPolygon(pt, shapes);

	return false;
    }

    /**
     * 从点数组中获得坐标点数组
     * 
     * @param points
     * @return
     */
    public static int[] getIntsFromPoints(Point[] points) {
	int[] array = new int[points.length * 2];
	for (int n = 0; n < points.length; n++) {
	    array[n * 2] = points[n].x;
	    array[n * 2 + 1] = points[n].y;
	}
	return array;
    }

    public static void fillPolyBackgroundColor(GC gc, Rectangle region, int shrink, Color color, int horzMargin, int vertMargin) {

	Point[] pts = UIUtil.getBatteryShape(ShapeType.WHOLE, region, shrink, horzMargin, vertMargin, new BatteryShape());
	Color oldbr = gc.getBackground();
	gc.setBackground(color);
	gc.fillPolygon(getIntsFromPoints(pts));
	gc.setBackground(oldbr);
    }

}
