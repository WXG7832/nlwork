package com.nltecklib.protocol.lab.main;

/**
 * 保护参数的包结构
 * @author Administrator
 *
 */
public class ProtectionParamPack implements Cloneable{

	private ChnFirstLevelProtectData general;
	private CCProtectData cc;
	private CCVProtectData ccv;
	private DCProtectData dc;
	private DCVProtectData dcv;
	private CVProtectData cv;
	private DVProtectData dv;
	private CPProtectData cp;
	private DPProtectData dp;
	private CRProtectData cr;
	private DRProtectData dr;
	private TouchProtectData touch;
	private SlpProtectData sleep;
	private PoleData pole;
	private String   name; //保护方案名
	

	public ChnFirstLevelProtectData getGeneral() {
		return general;
	}
	public void setGeneral(ChnFirstLevelProtectData general) {
		this.general = general;
	}
	public CCProtectData getCc() {
		return cc;
	}
	public void setCc(CCProtectData cc) {
		this.cc = cc;
	}
	public DCProtectData getDc() {
		return dc;
	}
	public void setDc(DCProtectData dc) {
		this.dc = dc;
	}
	public CVProtectData getCv() {
		return cv;
	}
	public void setCv(CVProtectData cv) {
		this.cv = cv;
	}
	public DVProtectData getDv() {
		return dv;
	}
	public void setDv(DVProtectData dv) {
		this.dv = dv;
	}
	public CPProtectData getCp() {
		return cp;
	}
	public void setCp(CPProtectData cp) {
		this.cp = cp;
	}
	public DPProtectData getDp() {
		return dp;
	}
	public void setDp(DPProtectData dp) {
		this.dp = dp;
	}
	public CRProtectData getCr() {
		return cr;
	}
	public void setCr(CRProtectData cr) {
		this.cr = cr;
	}
	public DRProtectData getDr() {
		return dr;
	}
	public void setDr(DRProtectData dr) {
		this.dr = dr;
	}
	public TouchProtectData getTouch() {
		return touch;
	}
	public void setTouch(TouchProtectData touch) {
		this.touch = touch;
	}
	public SlpProtectData getSleep() {
		return sleep;
	}
	public void setSleep(SlpProtectData sleep) {
		this.sleep = sleep;
	}
	public PoleData getPole() {
		return pole;
	}
	public void setPole(PoleData pole) {
		this.pole = pole;
	}
	
	public CCVProtectData getCcv() {
		return ccv;
	}
	public void setCcv(CCVProtectData ccv) {
		this.ccv = ccv;
	}
	public DCVProtectData getDcv() {
		return dcv;
	}
	public void setDcv(DCVProtectData dcv) {
		this.dcv = dcv;
	}
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	@Override
	public String toString() {
		return "ProtectionParamPack [general=" + general + ", cc=" + cc + ", ccv=" + ccv + ", dc=" + dc + ", dcv=" + dcv
				+ ", cv=" + cv + ", dv=" + dv + ", cp=" + cp + ", dp=" + dp + ", cr=" + cr + ", dr=" + dr + ", touch="
				+ touch + ", sleep=" + sleep + ", pole=" + pole + ", name=" + name + "]";
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		ProtectionParamPack ppp = (ProtectionParamPack)super.clone();	
		ppp.setCc(cc == null ? null : (CCProtectData)cc.clone());
		ppp.setCcv(ccv == null ? null : (CCVProtectData)ccv.clone());
		ppp.setCv(cv == null ? null : (CVProtectData)cv.clone());
		ppp.setCr(cr == null ? null : (CRProtectData)cr.clone());
		ppp.setCp(cp == null ? null : (CPProtectData)cp.clone());
		ppp.setDc(dc == null ? null : (DCProtectData)dc.clone());
		ppp.setDv(dv == null ? null : (DVProtectData)dv.clone());
		ppp.setDcv(dcv == null ? null : (DCVProtectData)dcv.clone());
		ppp.setDr(dr == null ? null : (DRProtectData)dr.clone());
		ppp.setDp(dp == null ? null : (DPProtectData)dp.clone());
		ppp.setGeneral(general == null ? null : (ChnFirstLevelProtectData)general.clone()); 
		ppp.setSleep(sleep == null ? null : (SlpProtectData)sleep.clone()); 
		ppp.setPole(pole == null ? null : (PoleData)pole.clone());
		ppp.setTouch(touch == null ? null : (TouchProtectData)touch.clone());
		return ppp;
	}

	
}
