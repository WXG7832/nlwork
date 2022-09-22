package com.nltecklib.test.atlmes;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.nltecklib.atlmes.AtlMes;
import com.nltecklib.protocol.atlmes.MesFactory;
import com.nltecklib.protocol.atlmes.mes.A005;
import com.nltecklib.protocol.atlmes.mes.A034;
import com.nltecklib.protocol.atlmes.mes.A034.ParamItem;
import com.nltecklib.protocol.atlmes.mes.A034.ProductsItem;
import com.nltecklib.protocol.atlmes.mes.A038;
import com.nltecklib.protocol.atlmes.mes.A038.BatteryResult;
import com.nltecklib.protocol.atlmes.mes.A038.ProductInfo;
import com.nltecklib.protocol.atlmes.mes.A082;
import com.nltecklib.protocol.atlmes.mes.A082.CellNameTable;

/**
 * ≤‚ ‘MES2.0
 * 
 * @author guofang_ma
 *
 */
public class TestAtlmes {

	@Test
	public void te1() {
		AtlMes mes = new AtlMes();

		try {
			mes.connectMes();

			while (true) {
				try {

					A005 a005 = new A005();
					mes.send(a005);
					Thread.sleep(10000);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	@Test
	public void createA038() {
		
		A082 a082=new A082();
		int barcodeCount=512;
		int driverChnCount=16;
		for(int i=0;i<barcodeCount;i++) {

			CellNameTable e=new CellNameTable();
			e.MACHINENO="1";
			e.BOARDNO="1";
			e.AREANO=i/driverChnCount+1+"";
			e.CHANNELNO=i%16+1+"";
			e.BATTERYNO=String.format("TESTBARCODE000%03d+A00000000%03d", i+1,i+1);
			e.SCAN_TIME="";
			e.SCANUSER="";
			a082.ResponseInfo.CELLNAMETABLE.add(e);
		}
		
		A038 a038=new A038();
		for (CellNameTable ct : a082.ResponseInfo.CELLNAMETABLE) {
			ProductInfo e=new ProductInfo();
			e.Result=BatteryResult.NotFound;
			e.SerialNo=ct.BATTERYNO;
			e.ResultCode=1;
			a038.ResponseInfo.ProductInfo.add(e);
		}
		System.out.println(MesFactory.encode(a038));
	}
	
	@Test
	public void createA034(){
		
		A082 a082=new A082();
		int barcodeCount=512;
		int driverChnCount=16;
		for(int i=0;i<barcodeCount;i++) {

			CellNameTable e=new CellNameTable();
			e.MACHINENO="1";
			e.BOARDNO="1";
			e.AREANO=i/driverChnCount+1+"";
			e.CHANNELNO=i%16+1+"";
			e.BATTERYNO=String.format("TESTBARCODE000%03d+A00000000%03d", i+1,i+1);
			e.SCAN_TIME="";
			e.SCANUSER="";
			a082.ResponseInfo.CELLNAMETABLE.add(e);
		}
		
		A034 a034=new A034();
		a034.ResponseInfo.Type="Normal";
		for (CellNameTable ct : a082.ResponseInfo.CELLNAMETABLE) {
			ProductsItem e=new ProductsItem();
			e.Param=new ArrayList<>();
			e.Pass="OK";
			e.ResultCode="0";
			e.ProductSN=ct.BATTERYNO;
			ParamItem e1=new ParamItem();
			e1.Installation="";
			e1.ParamID="20581";
			e1.ParamDesc="Step";
			e1.KValue="254";
			e1.Result="OK";
			e.Param.add(e1);
			
			a034.ResponseInfo.Products.add(e);
		}
		System.out.println(MesFactory.encode(a034));
	}
	
	@Test
	public void createA082() {
		A082 a082=new A082();
		int barcodeCount=512;
		int driverChnCount=16;
		for(int i=0;i<barcodeCount;i++) {

			CellNameTable e=new CellNameTable();
			e.MACHINENO="1";
			e.BOARDNO="1";
			e.AREANO=i/driverChnCount+1+"";
			e.CHANNELNO=i%16+1+"";
			e.BATTERYNO=String.format("TESTBARCODE000%03d+A00000000%03d", i+1,i+1);
			e.SCAN_TIME="";
			e.SCANUSER="";
			a082.ResponseInfo.CELLNAMETABLE.add(e);
		}
		System.out.println(MesFactory.encode(a082));
	}

}
