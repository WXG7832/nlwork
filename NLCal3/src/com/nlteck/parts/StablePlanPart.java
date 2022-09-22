
package com.nlteck.parts;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.nlteck.firmware.WorkBench;
import com.nlteck.model.StablePlan;
import com.nlteck.model.StablePlan.StableStep;
import com.nlteck.resources.Resources;
import com.nlteck.swtlib.table.TableViewerEx;
import com.nlteck.swtlib.table.TableViewerEx.EditCtrlType;
import com.nlteck.swtlib.tools.MyMsgDlg;
import com.nlteck.utils.XmlUtil;
import com.nltecklib.protocol.power.driver.DriverEnvironment.CalMode;

public class StablePlanPart {
	
	public static final String ID = "nlcal3.partdescriptor.stableplan";
	private TableViewerEx tableViewer;
	
	public static final String PATH = "calCfg" + File.separator +"stable.xml";

	@PostConstruct
	public void postConstruct(Composite parent) {
		 
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0, 0);
		fd_toolBar.bottom = new FormAttachment(0, 40);
		fd_toolBar.left = new FormAttachment(0, 0);
		toolBar.setLayoutData(fd_toolBar);
		
        
		

		ToolItem addToolItem = new ToolItem(toolBar, SWT.NONE);
		addToolItem.setImage(Resources.ADD_ITEM_IMAGE);
		addToolItem.setText("添加");
		addToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				List<StableStep> list = (List<StableStep>)tableViewer.getInput();
				StableStep step = new StableStep();
				step.index = list.size();
				step.mode = CalMode.CC;
				
				list.add(step);
				refreshTable();
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			
		});

		ToolItem insertToolItem = new ToolItem(toolBar, SWT.NONE);
		insertToolItem.setImage(Resources.INSERT_ITEM_IMAGE);
		insertToolItem.setText("插入");
		insertToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int index = tableViewer.getTable().getSelectionIndex();
				if(index != -1) {
					
					
					List<StableStep> list = (List<StableStep>)tableViewer.getInput();
					StableStep step = new StableStep();
					step.mode = CalMode.CC;
					list.add(index, step);
					
					refreshTable();
				}
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		

		ToolItem delToolItem = new ToolItem(toolBar, SWT.NONE);
		delToolItem.setImage(Resources.DEL_ITEM_IMAGE);
		delToolItem.setText("删除");
		delToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int index = tableViewer.getTable().getSelectionIndex();
				if(index != -1) {
					
					
					List<StableStep> list = (List<StableStep>)tableViewer.getInput();
					list.remove(index);
					refreshTable();
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});

		ToolItem upToolItem = new ToolItem(toolBar, SWT.NONE);
		upToolItem.setImage(Resources.UP_ITEM_IMAGE);
		upToolItem.setText("上移");
		upToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int index = tableViewer.getTable().getSelectionIndex();
				if(index > 0 ) {
					
					
					List<StableStep> list = (List<StableStep>)tableViewer.getInput();
					
					StableStep ss = list.get(index - 1);
					list.set(index - 1, list.get(index));
					list.set(index, ss);
					tableViewer.setSelection(new StructuredSelection(list.get(index - 1)), true);
					refreshTable();
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});

		ToolItem downToolItem = new ToolItem(toolBar, SWT.NONE);
		downToolItem.setImage(Resources.DOWN_ITEM_IMAGE);
		downToolItem.setText("下移");
		downToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int index = tableViewer.getTable().getSelectionIndex();
				List<StableStep> list = (List<StableStep>)tableViewer.getInput();
				if(index <  list.size() - 1) {
					
					StableStep ss = list.get(index + 1);
					list.set(index + 1, list.get(index));
					list.set(index, ss);
					//tableViewer.getTable().setSelection(index + 1);
					tableViewer.setSelection(new StructuredSelection(list.get(index+1)), true);
					refreshTable();
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});


		ToolItem sortToolItem = new ToolItem(toolBar, SWT.NONE);
		sortToolItem.setImage(Resources.SORT_ITEM_IMAGE);
		sortToolItem.setText("排序");
		sortToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				List<StableStep> list = (List<StableStep>)tableViewer.getInput();
				Collections.sort(list);
				refreshTable();
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
			
		});
		
		
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem importToolItem = new ToolItem(toolBar, SWT.NONE);
		importToolItem.setText("导入");
		importToolItem.setImage(Resources.IMPORT_SCHEMA_IMAGE);
		
		ToolItem saveToolItem = new ToolItem(toolBar, SWT.NONE);
		saveToolItem.setText("保存");
		saveToolItem.setImage(Resources.SAVE_SCHEMA_IMAGE);
		saveToolItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				List<StableStep> list = (List<StableStep>)tableViewer.getInput();
				if(list.isEmpty()) {
					
					MyMsgDlg.openErrorDialog(toolBar.getShell(), "保存失败", "空方案无法保存!");
					return;
				}
				try {
					export(PATH);
					MyMsgDlg.openInfoDialog(toolBar.getShell(), "保存失败", "保存稳定度测试方案成功", false);
				} catch (Exception e1) {
					
					e1.printStackTrace();
					MyMsgDlg.openErrorDialog(toolBar.getShell(), "保存失败", e1.getMessage());
				}
				
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		});

		ToolItem otherSaveToolItem = new ToolItem(toolBar, SWT.NONE);
		otherSaveToolItem.setText("另存为");
		otherSaveToolItem.setImage(Resources.SAVE_SCHEMA_IMAGE);
		
		
		
		
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(toolBar,5);
		fd_table.bottom = new FormAttachment(100, -5);
		fd_table.left = new FormAttachment(0, 0);
		fd_table.right = new FormAttachment(100, 0);
		
		
		tableViewer = new TableViewerEx(composite, true, false);
		List<String> headers = new ArrayList<>();
		headers.add("序号");
		headers.add("工作模式");
		headers.add("目标值/mA/mV");
		headers.add("采样点数");
		headers.add("采样间隔/s");
		headers.add("ADC单点偏差");
		headers.add("表单点偏差");
		headers.add("ADC标准偏差");
		headers.add("表标准偏差");
		
		List<Integer> widths = new ArrayList<>();
		widths.add(80);
		widths.add(100);
		widths.add(100);
		widths.add(100);
		widths.add(100);
		widths.add(100);
		widths.add(100);
		widths.add(100);
		widths.add(100);
		tableViewer.setHeaders(headers, widths);
		
		tableViewer.setColumnEditType(1 , EditCtrlType.COMBO);
		tableViewer.setColumnEditType(2 , EditCtrlType.SPINNER);
		tableViewer.setColumnEditType(3 , EditCtrlType.SPINNER);
		tableViewer.setColumnEditType(4 , EditCtrlType.SPINNER);
		
		tableViewer.setColumnEditType(5 , EditCtrlType.SPINNER);
		tableViewer.setColumnEditType(6 , EditCtrlType.SPINNER);
		tableViewer.setColumnEditType(7 , EditCtrlType.SPINNER);
		tableViewer.setColumnEditType(8 , EditCtrlType.SPINNER);
		
		
		List<String> modeArr = new ArrayList<>();
		for(CalMode cm : CalMode.values()) {
			
			modeArr.add(cm.name());
		}
		tableViewer.setColumnEditContent(1, modeArr.toArray(new String[0]));
		tableViewer.setColumnEditDecimal(2, 1);
		tableViewer.setColumnEditDecimal(3, 0);
		tableViewer.setColumnEditDecimal(4, 0);
		
		tableViewer.setColumnEditDecimal(5, 1);
		tableViewer.setColumnEditDecimal(6, 1);
		tableViewer.setColumnEditDecimal(7, 1);
		tableViewer.setColumnEditDecimal(8, 1);
		
		tableViewer.setColumnEditRange(2, 0, Integer.MAX_VALUE);
		tableViewer.setColumnEditRange(3, 0, Integer.MAX_VALUE);
		tableViewer.setColumnEditRange(4, 0, Integer.MAX_VALUE);
		tableViewer.setColumnEditRange(5, 0, Integer.MAX_VALUE);
		tableViewer.setColumnEditRange(6, 0, Integer.MAX_VALUE);
		tableViewer.setColumnEditRange(7, 0, Integer.MAX_VALUE);
		tableViewer.setColumnEditRange(8, 0, Integer.MAX_VALUE);
		
		tableViewer.setInput(new ArrayList<>());
		
		tableViewer.setLabelProvider(new ITableLabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				
				StableStep step = (StableStep)element;
				
				String text = "";
				
				switch(columnIndex) {
				
				case 0:
					text = step.index + 1 + "";
					break;
				case 1:
					text = step.mode.name();
					break;
				case 2:
					text = step.destVal + "";
				    break;
				case 3:
					text = step.pickCount + "";
				    break;
				case 4:
					text = step.pickInterval + "";
				    break;
				case 5:
					text = step.maxAdcOffset + "";
				    break;
				case 6:
					text = step.maxMeterOffset + "";
				    break;
				case 7:
					text = step.maxSigmaAdcOffset + "";
				    break;
				case 8:
					text = step.maxSigmaMeterOffset + "";
				    break;
				
				
				
				}
				
				
				return text;
			}
		});
		
		tableViewer.getTable().setLayoutData(fd_table);
		
		try {
			load(PATH);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	public void refreshTable() {
		
		
		List<StableStep> list = (List<StableStep>)tableViewer.getInput();
		for(int n = 0 ; n < list.size() ; n++) {
			
			list.get(n).index = n;
			
		}
		
		tableViewer.destroyEditControl();
		StableStep ss = (StableStep)tableViewer.getStructuredSelection().getFirstElement();
		if(ss != null && list.indexOf(ss) != -1) {
			

		    tableViewer.setCursorControl(list.indexOf(ss), tableViewer.getCursorColumn());
		   
		}
		tableViewer.refresh();
		
	}
	
	public void load(String path) throws Exception {
		
		String dir = Platform.getInstallLocation().getURL().getPath();
		Document doc = XmlUtil.loadXml(dir + PATH);
		if(doc != null) {
			
			WorkBench.stablePlan.clearSteps();
			Element root = doc.getRootElement();
			
			WorkBench.stablePlan.setOpenModuleDelay(Integer.parseInt(root.attributeValue("openModuleDelay")));
			WorkBench.stablePlan.setCloseModuleDelay(Integer.parseInt(root.attributeValue("closeModuleDelay")));
			List<Element> steps = root.elements("stableStep");
			
			for(Element element : steps) {
				
				StableStep ss = new StableStep();
				for(Attribute attr : element.attributes()) {
					
					Field f = StableStep.class.getField(attr.getName());
					if(f.getType() == double.class) {
						
						f.setDouble(ss, Double.parseDouble(attr.getText()));
						
					} else if(f.getType() == int.class) {
						
						f.setInt(ss, Integer.parseInt(attr.getText()));
						
					} if(f.getType() == CalMode.class) {
						
						f.set(ss, CalMode.valueOf(attr.getText()));
						
					}
				}
				WorkBench.stablePlan.addStep(ss);
				
			}
			
			System.out.println(WorkBench.stablePlan.getSteps());
			
			//更新表格
			tableViewer.setInput(WorkBench.stablePlan.getSteps());
			
		}
		
		
	}
	/**
	 * 验证
	 * @author  wavy_zheng
	 * 2022年3月27日
	 * @throws Exception
	 */
	public void validate() throws Exception{
		
		List<StableStep> list = (List<StableStep>)tableViewer.getInput();
        for(int n = 0 ; n < list.size() ; n++) {
        	
        	StableStep ss = list.get(n);
        	if(ss.destVal == 0) {
        		
        		throw new Exception("第" + (n+1) + "项目标值不能为0");
        	}
        	
        	if(ss.pickCount == 0) {
        		
        		throw new Exception("第" + (n+1) + "项采集次数不能为0");
        	}
        	
            if(ss.pickInterval == 0) {
        		
        		throw new Exception("第" + (n+1) + "项采集间隔不能为0");
        	}
            
            
            if(ss.maxAdcOffset == 0) {
        		
        		throw new Exception("第" + (n+1) + "项采单点ADC偏差不能为0");
        	}
            
             if(ss.maxMeterOffset == 0) {
        		
        		throw new Exception("第" + (n+1) + "项单点表偏差不能为0");
        	}
             
            if(ss.maxSigmaAdcOffset == 0) {
         		
         		throw new Exception("第" + (n+1) + "项ADC标准偏差不能为0");
         	}
            
            if(ss.maxSigmaMeterOffset == 0) {
         		
         		throw new Exception("第" + (n+1) + "项表标准偏差不能为0");
         	}
            
        }
		
	}
	
	
	/**
	 * 导出
	 * @author  wavy_zheng
	 * 2022年3月26日
	 * @param path
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void export(String path) throws Exception {
		
		Document doc = DocumentHelper.createDocument();
		Element plan = doc.addElement("stablePlan");
		
		List<StableStep> list = (List<StableStep>)tableViewer.getInput();
        for(int n = 0 ; n < list.size() ; n++) {
			
        	Element stepElement = plan.addElement("stableStep");
			StableStep ss = list.get(n);
			Field[] fields = StableStep.class.getDeclaredFields();
			for(Field f : fields) {
				
				String name = f.getName();
				if(f.getType() == double.class) {
					
					double val = f.getDouble(ss);
					
					stepElement.addAttribute(name, val + "");
				} else if(f.getType() == int.class) {
					
					stepElement.addAttribute(name, f.getInt(ss) + "");
				} else if(f.getType() == CalMode.class) {
					
					stepElement.addAttribute(name, f.get(ss).toString());
				}
				
			}
			
		}
        String dir = Platform.getInstallLocation().getURL().getPath();
        XmlUtil.saveXml(dir + path, doc);
		
		
	}

}