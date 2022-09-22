package com.nltecklib.protocol.pf9830;

import java.util.Date;

public class PfData {

	private double u, u3, u2, u1, i, i3, i2, i1, 
					p, p3, p2, p1, pf, pf3, pf2, pf1,
					va, va3, va2, va1, var, var3, var2, var1,
					deg, deg3, deg2, deg1, hz, ucf3, ucf2, ucf1,
					icf3, icf2, icf1, kwh;
	private String time;
	private Date date;
	
	public PfData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public double getU() {
		return u;
	}
	public void setU(double u) {
		this.u = u;
	}
	public double getU3() {
		return u3;
	}
	public void setU3(double u3) {
		this.u3 = u3;
	}
	public double getU2() {
		return u2;
	}
	public void setU2(double u2) {
		this.u2 = u2;
	}
	public double getU1() {
		return u1;
	}
	public void setU1(double u1) {
		this.u1 = u1;
	}
	public double getI() {
		return i;
	}
	public void setI(double i) {
		this.i = i;
	}
	public double getI3() {
		return i3;
	}
	public void setI3(double i3) {
		this.i3 = i3;
	}
	public double getI2() {
		return i2;
	}
	public void setI2(double i2) {
		this.i2 = i2;
	}
	public double getI1() {
		return i1;
	}
	public void setI1(double i1) {
		this.i1 = i1;
	}
	public double getP() {
		return p;
	}
	public void setP(double p) {
		this.p = p;
	}
	public double getP3() {
		return p3;
	}
	public void setP3(double p3) {
		this.p3 = p3;
	}
	public double getP2() {
		return p2;
	}
	public void setP2(double p2) {
		this.p2 = p2;
	}
	public double getP1() {
		return p1;
	}
	public void setP1(double p1) {
		this.p1 = p1;
	}
	public double getPf() {
		return pf;
	}
	public void setPf(double pf) {
		this.pf = pf;
	}
	public double getPf3() {
		return pf3;
	}
	public void setPf3(double pf3) {
		this.pf3 = pf3;
	}
	public double getPf2() {
		return pf2;
	}
	public void setPf2(double pf2) {
		this.pf2 = pf2;
	}
	public double getPf1() {
		return pf1;
	}
	public void setPf1(double pf1) {
		this.pf1 = pf1;
	}
	public double getVa() {
		return va;
	}
	public void setVa(double va) {
		this.va = va;
	}
	public double getVa3() {
		return va3;
	}
	public void setVa3(double va3) {
		this.va3 = va3;
	}
	public double getVa2() {
		return va2;
	}
	public void setVa2(double va2) {
		this.va2 = va2;
	}
	public double getVa1() {
		return va1;
	}
	public void setVa1(double va1) {
		this.va1 = va1;
	}
	public double getVar() {
		return var;
	}
	public void setVar(double var) {
		this.var = var;
	}
	public double getVar3() {
		return var3;
	}
	public void setVar3(double var3) {
		this.var3 = var3;
	}
	public double getVar2() {
		return var2;
	}
	public void setVar2(double var2) {
		this.var2 = var2;
	}
	public double getVar1() {
		return var1;
	}
	public void setVar1(double var1) {
		this.var1 = var1;
	}
	public double getDeg() {
		return deg;
	}
	public void setDeg(double deg) {
		this.deg = deg;
	}
	public double getDeg3() {
		return deg3;
	}
	public void setDeg3(double deg3) {
		this.deg3 = deg3;
	}
	public double getDeg2() {
		return deg2;
	}
	public void setDeg2(double deg2) {
		this.deg2 = deg2;
	}
	public double getDeg1() {
		return deg1;
	}
	public void setDeg1(double deg1) {
		this.deg1 = deg1;
	}
	public double getHz() {
		return hz;
	}
	public void setHz(double hz) {
		this.hz = hz;
	}
	public double getUcf3() {
		return ucf3;
	}
	public void setUcf3(double ucf3) {
		this.ucf3 = ucf3;
	}
	public double getUcf2() {
		return ucf2;
	}
	public void setUcf2(double ucf2) {
		this.ucf2 = ucf2;
	}
	public double getUcf1() {
		return ucf1;
	}
	public void setUcf1(double ucf1) {
		this.ucf1 = ucf1;
	}
	public double getIcf3() {
		return icf3;
	}
	public void setIcf3(double icf3) {
		this.icf3 = icf3;
	}
	public double getIcf2() {
		return icf2;
	}
	public void setIcf2(double icf2) {
		this.icf2 = icf2;
	}
	public double getIcf1() {
		return icf1;
	}
	public void setIcf1(double icf1) {
		this.icf1 = icf1;
	}
	public double getKwh() {
		return kwh;
	}
	public void setKwh(double kwh) {
		this.kwh = kwh;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "PfData [u=" + u + ", u3=" + u3 + ", u2=" + u2 + ", u1=" + u1 + ", i=" + i + ", i3=" + i3 + ", i2=" + i2
				+ ", i1=" + i1 + ", p=" + p + ", p3=" + p3 + ", p2=" + p2 + ", p1=" + p1 + ", pf=" + pf + ", pf3=" + pf3
				+ ", pf2=" + pf2 + ", pf1=" + pf1 + ", va=" + va + ", va3=" + va3 + ", va2=" + va2 + ", va1=" + va1
				+ ", var=" + var + ", var3=" + var3 + ", var2=" + var2 + ", var1=" + var1 + ", deg=" + deg + ", deg3="
				+ deg3 + ", deg2=" + deg2 + ", deg1=" + deg1 + ", hz=" + hz + ", ucf3=" + ucf3 + ", ucf2=" + ucf2
				+ ", ucf1=" + ucf1 + ", icf3=" + icf3 + ", icf2=" + icf2 + ", icf1=" + icf1 + ", kwh=" + kwh + ", time="
				+ time + ", date=" + date + "]";
	}
	
	
}
