package com.nltecklib.process.fuel.unitExtend;

/**
 * 电子负载扩展，用于被 BooleanAndDoUnit继承
 * 
 * @author caichao_tang
 *
 */
public class ElecLoad {
    private ElecLoadMode elecLoadMode;

    public ElecLoadMode getElecLoadMode() {
	return elecLoadMode;
    }

    public void setElecLoadMode(ElecLoadMode elecLoadMode) {
	this.elecLoadMode = elecLoadMode;
    }

    public enum ElecLoadMode {
	NONE(0, "待机模式"), CC(1, "恒流模式"), CV(2, "恒压模式"), CW(3, "恒功率模式"), CR(4, "恒电阻模式");

	private int mark;
	private String describe;

	private ElecLoadMode(int mark, String describe) {
	    this.mark = mark;
	    this.describe = describe;
	}

	public int getMark() {
	    return mark;
	}

	public void setMark(int mark) {
	    this.mark = mark;
	}

	public String getDescribe() {
	    return describe;
	}

	public void setDescribe(String describe) {
	    this.describe = describe;
	}

	/**
	 * 根据负载模式描述获得负载模式对象
	 * 
	 * @param describe
	 * @return
	 */
	public static ElecLoadMode getElecLoadModeFromDescribe(String describe) {
	    for (ElecLoadMode elecLoadMode : ElecLoadMode.values()) {
		if (elecLoadMode.getDescribe().equals(describe)) {
		    return elecLoadMode;
		}
	    }
	    return null;
	}
    }
}
