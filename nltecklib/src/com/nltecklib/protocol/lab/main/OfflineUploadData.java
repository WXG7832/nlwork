package com.nltecklib.protocol.lab.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nltecklib.protocol.Alertable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.ChnState;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.SaveFlag;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.ZipUtil;

/**
 * @author wavy_zheng
 * @version 创建时间：2021年7月7日 上午9:51:28 类说明
 */
public class OfflineUploadData extends Data implements Alertable, Responsable {

	private List<ChannelOfflineData> chnDatas = new ArrayList<ChannelOfflineData>();
	private String  testName; //所属测试名

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void encode() {
         
		int index = data.size();
		
		if(Data.isUseOfflineTestname()) {
			
			//离线数据附带测试名
			data.addAll(ProtocolUtil.encodeString(testName, "utf-8", 50));
			
		}
		
		// 通道数据包个数
		data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnDatas.size()), 2, true)));
		// 通道各数据
		for (int i = 0; i < chnDatas.size(); i++) {

			ChannelOfflineData chnData = chnDatas.get(i);
			if (chnData == null) {

				throw new RuntimeException("can not pick up data ");
			}
			data.add((byte) chnData.getChnIndexInMain());
			data.add((byte) chnData.getState().ordinal());
			data.add(chnData.getWorkMode() == null ? (byte) 0xff : (byte) chnData.getWorkMode().ordinal());
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getStepIndex()), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getLoopIndex()), 2, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((long) (chnData.getExecuteIndex()), 4, true)));
			data.add((byte) chnData.getSaveFlag().ordinal());
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getVoltage() * Math.pow(10, Data.getVoltageResolution())), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getCurrent() * Math.pow(10, Data.getCurrentResolution())), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getCapacity() * Math.pow(10, Data.getCapacityResolution())), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getEnergy() * Math.pow(10, Data.getEnergyResolution())), 4, true)));
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getPowerVoltage() * Math.pow(10, Data.getVoltageResolution())), 3, true)));
			data.addAll(Arrays.asList(ProtocolUtil
					.split((long) (chnData.getBackupVoltage() * Math.pow(10, Data.getVoltageResolution())), 3, true)));
			// 温度
			data.addAll(Arrays.asList(ProtocolUtil.splitSpecialMinus((long) (chnData.getTemperature() * 10), 2, true)));
			
			// 采样时间
			data.addAll(Arrays.asList(ProtocolUtil.split((chnData.getPickupTime()), 8, true)));
			data.addAll(Arrays.asList(ProtocolUtil.split((chnData.getRunTime()), 8, true))); //步次累计时间
			if(Data.getGeneration() == Generation.ND2) {
				
				//2代实验室平台增加流程累计时间
				data.addAll(Arrays.asList(ProtocolUtil.split(chnData.getTotalTime(), 8, true))); //步次累计时间
			}
		}
		
		/*if(Data.isUseStringCompress()) {
			
			//压缩			
			byte[] srcDatas = ProtocolUtil.convertListToArray(data.subList(index, data.size()));
			try {
				byte[] compressData = ZipUtil.compress(srcDatas);
				data.subList(index, data.size()).clear();
				data.addAll(ProtocolUtil.convertArrayToList(compressData));
				
			} catch (IOException e) {
				
				e.printStackTrace();
				throw new RuntimeException("compress offline data error :" + e.getMessage());
			}
			
		}*/

	}
    /**
     * 单包50个字节
     */
	@Override
	public void decode(List<Byte> encodeData) {

		data = encodeData;
		int index = 0;
		
		/*if(Data.isUseStringCompress()) {
			
			//解压缩
			try {
				byte[] uncompressData = ZipUtil.unCompress(ProtocolUtil.convertListToArray(encodeData));
				data = ProtocolUtil.convertArrayToList(uncompressData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}*/
		/**
		 * 让离线数据附带测试名，避免离线数据存入错误的测试名
		 */
		if(Data.isUseOfflineTestname()) {
			
			testName = ProtocolUtil.decodeString(data, index, 100, "utf-8");
			index += 100;
		}

		// 通道数据包个数
		int chnDataCount = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		chnDatas.clear();
		for (int i = 0; i < chnDataCount; i++) {
			ChannelOfflineData chnData = new ChannelOfflineData();

			// 通道序号
			chnData.setChnIndexInMain(ProtocolUtil.getUnsignedByte(data.get(index++)));

			// 通道状态
			if (data.get(index) > ChnState.values().length - 1) {

				throw new RuntimeException("未知的通道状态:" + ProtocolUtil.getUnsignedByte(data.get(index)));
			}
			chnData.setState(ChnState.values()[data.get(index++)]);
			// 工作模式
			int mode = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (mode < WorkMode.values().length) {

				chnData.setWorkMode(WorkMode.values()[mode]);
			} else if (chnData.getState() == ChnState.RUN) {

				throw new RuntimeException("in the running mode , the workmode(" + mode + ") must be valid");
			}

			// 步次号
			chnData.setStepIndex((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			// 循环号
			chnData.setLoopIndex((int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true));
			index += 2;
			// 执行号
			chnData.setExecuteIndex(
					(int) ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true));
			index += 4;
			// 关键点标记
			int code = ProtocolUtil.getUnsignedByte(data.get(index++));
			if (code > SaveFlag.values().length - 1) {

				throw new RuntimeException("the data save flag code error : " + code);
			}
			chnData.setSaveFlag(SaveFlag.values()[code]);
			// 电压
			chnData.setVoltage((double) (ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, Data.getVoltageResolution())));
			index += 3;
			// 电流
			chnData.setCurrent((double) (ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, Data.getCurrentResolution())));
			index += 3;
			// 容量
			chnData.setCapacity(
					(double) (ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
							/ Math.pow(10, Data.getCapacityResolution())));
			index += 4;
			// 能量
			chnData.setEnergy((double) (ProtocolUtil.compose(data.subList(index, index + 4).toArray(new Byte[0]), true)
					/ Math.pow(10, Data.getEnergyResolution())));
			index += 4;
			// 功率电压
			chnData.setPowerVoltage((double) (ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, Data.getVoltageResolution())));
			index += 3;
			// 备份电压
			chnData.setBackupVoltage((double) (ProtocolUtil.compose(data.subList(index, index + 3).toArray(new Byte[0]), true)
					/ Math.pow(10, Data.getVoltageResolution())));
			index += 3;
			//通道温度
			chnData.setTemperature((double)  ProtocolUtil.composeSpecialMinus(data.subList(index, index + 2).toArray(new Byte[0]), true) / 10);
			index += 2;
			// 采样时间
			chnData.setPickupTime(ProtocolUtil.compose(data.subList(index, index + 8).toArray(new Byte[0]), true));
			index += 8;
			// 步次累计运行时间
			chnData.setRunTime(ProtocolUtil.compose(data.subList(index, index + 8).toArray(new Byte[0]), true));
			index += 8;
			
			if(Data.getGeneration() == Generation.ND2) {
				//流程累计运行时间
				chnData.setTotalTime(ProtocolUtil.compose(data.subList(index, index + 8).toArray(new Byte[0]), true));
				index += 8;
				
			}
			

			chnDatas.add(chnData);
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.OfflineDataCode;
	}
	
	
	public List<ChannelOfflineData> getChnDatas() {
		return chnDatas;
	}

	public void setChnDatas(List<ChannelOfflineData> chnDatas) {
		this.chnDatas = chnDatas;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	

}
