package com.nlteck.model;

import com.nlteck.swtlib.table.TableViewerEx.TableExItem;
import com.nlteck.utils.EnumUtil;
import com.nltecklib.protocol.li.logic2.Logic2Environment.CalMode;
import com.nltecklib.protocol.li.main.PoleData.Pole;

/**
* @author  wavy_zheng
* @version 创建时间：2021年1月25日 下午12:07:06
* 计量点方案
*/
public class CalculatePlanDotDO  implements TableExItem  {

	private int      id;
	private int      index;
	private CalMode  mode;
	private Pole     pole;
	private double   calculateDot; //计量点
	
	
	public CalculatePlanDotDO() {}
	
	public CalculatePlanDotDO(int index, CalMode mode, Pole pole, double calculateDot) {
		
		this.index = index;
		this.mode = mode;
		this.pole = pole;
	    this.calculateDot = calculateDot;
	}
	
	
	public int getId() {
		return id;
	}





	public void setId(int id) {
		this.id = id;
	}





	public int getIndex() {
		return index;
	}





	public void setIndex(int index) {
		this.index = index;
	}





	public CalMode getMode() {
		return mode;
	}





	public void setMode(CalMode mode) {
		this.mode = mode;
	}





	public Pole getPole() {
		return pole;
	}





	public void setPole(Pole pole) {
		this.pole = pole;
	}





	public double getCalculateDot() {
		return calculateDot;
	}





	public void setCalculateDot(double calculateDot) {
		this.calculateDot = calculateDot;
	}





	@Override
	public String toString() {
		return "CalculatePlanDotDO [id=" + id + ", index=" + index + ", mode=" + mode + ", pole=" + pole
				+ ", calculateDot=" + calculateDot + "]";
	}

	@Override
	public void flushItemText(int columnIndex, String text) {
		
		System.out.println("flush index:"+columnIndex+" , value :"+text);
		
		switch(columnIndex) {
		
		case 0:
			this.index = Integer.parseInt(text);
			break;
		case 1:
			this.mode = EnumUtil.getEnumByName(CalMode.class,text);
			break;
		case 2:
			this.pole = text.equals("+") ? Pole.NORMAL : Pole.REVERSE;
			break;
		case 3:
			this.calculateDot = Double.parseDouble(text);
			break;
		}
	}
	
	
	
	
} 
