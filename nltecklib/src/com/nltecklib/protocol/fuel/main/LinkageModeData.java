package com.nltecklib.protocol.fuel.main;

import java.util.ArrayList;
import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.Component;
import com.nltecklib.protocol.fuel.main.MainEnvironment.LinkageMode;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;

/**
 * 控制板泵/电磁阀联动协议数据——0x16
 * 
 * @author caichao_tang
 *
 */
@Deprecated
public class LinkageModeData extends Data implements Configable, Responsable, Queryable {
    /**
     * 联动的5组泵——阀
     */
    public static ArrayList<Component[]> linkageComponentList = createLinkageComponentList();

    private ArrayList<LinkageMode> linkageModeList = new ArrayList<>();

    public ArrayList<LinkageMode> getLinkageModeList() {
	return linkageModeList;
    }

    public void setLinkageModeList(ArrayList<LinkageMode> linkageModeList) {
	this.linkageModeList = linkageModeList;
    }

    @Override
    public void encode() {
	for (LinkageMode linkageMode : linkageModeList) {
	    data.add((byte) linkageMode.ordinal());
	}
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	linkageModeList = new ArrayList<>();
	for (int i = 0; i < data.size(); i++) {
	    linkageModeList.add(LinkageMode.values()[data.get(i)]);
	}
    }

    @Override
    public Code getCode() {
	return MainCode.LINKAGE_MODE_CODE;
    }

    @Override
    public String toString() {
	return "LinkageModeData [linkageModeList=" + linkageModeList + ", data=" + data + ", boardNum=" + boardNum + ", chnNum=" + chnNum + ", componentCode=" + component + ", result=" + result + ", orient=" + orient + "]";
    }

    private static ArrayList<Component[]> createLinkageComponentList() {
	ArrayList<Component[]> componentList = new ArrayList<Component[]>();
	componentList.add(new Component[] { Component.PMP_122, Component.SOV_121 });
	componentList.add(new Component[] { Component.PMP_312, Component.SOV_311 });
	componentList.add(new Component[] { Component.PMP_342, Component.SOV_341 });
	componentList.add(new Component[] { Component.PMP_352, Component.SOV_351 });
	componentList.add(new Component[] { Component.PMP_665, Component.SOV_667 });
	return componentList;
    }

}
