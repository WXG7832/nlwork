package com.nltecklib.protocol.li.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.li.Data;
import com.nltecklib.protocol.li.Environment.Code;
import com.nltecklib.protocol.li.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.li.main.MainEnvironment.WorkType;
import com.nltecklib.protocol.util.ProtocolUtil;

/**
* @author  wavy_zheng
* @version 创建时间：2021年7月8日 下午1:48:16
* 类说明
*/
public class JsonProtectionData extends Data implements Configable, Queryable, Responsable {
   
	public static class ProtectionPlan {
		
		public SaveParamData          pn;
		public CCProtectData          cc;
		public CVProtectData          cv;
		public DCProtectData          dc;
		public SlpProtectData         sleep;
		public CheckVoltProtectData   checkVolt;
		public StartEndCheckData      secd;
		public FirstCCProtectData     fcpd;
	}
	
	public static class PlanKey {
		
		public WorkType workType;
		public boolean  retest; //复测方案
		
		public PlanKey(WorkType workType , boolean retest) {
			
			this.workType = workType;
			this.retest   = retest;
		}

		@Override
		public String toString() {
			return "PlanKey [workType=" + workType + ", retest=" + retest + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (retest ? 1231 : 1237);
			result = prime * result + ((workType == null) ? 0 : workType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PlanKey other = (PlanKey) obj;
			if (retest != other.retest)
				return false;
			if (workType != other.workType)
				return false;
			return true;
		}

		public WorkType getWorkType() {
			return workType;
		}

		public void setWorkType(WorkType workType) {
			this.workType = workType;
		}

		public boolean isRetest() {
			return retest;
		}

		public void setRetest(boolean retest) {
			this.retest = retest;
		}
		
		
		
		
		
	}
	/**
	 * 保护方案集合，下标n为保护分区序号，从0开始；当设备未整柜模式时，0同样表示整柜保护方案
	 */
	private List<Map<PlanKey , ProtectionPlan>>  plans = new ArrayList<>();
	
	
	
	@Override
	public boolean supportUnit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportDriver() {
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
		
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
		String jsonStr = gson.toJson(this);
		System.out.println(jsonStr);
		
		try {
			byte[] arr = jsonStr.getBytes("utf-8");
			data.addAll(Arrays.asList(ProtocolUtil.split((long)arr.length, 2, true)));
			for (int n = 0; n < arr.length; n++) {

				data.add(arr[n]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("流程编码错误:" + e.getMessage());
		}

	}

	@Override
	public void decode(List<Byte> encodeData) {
		
		data = encodeData;
		int index = 0;

		int infoBytes = (int) ProtocolUtil.compose(data.subList(index, index + 2).toArray(new Byte[0]), true);
		index += 2;

		byte[] procBytes = new byte[infoBytes];
		for (int i = index; i < index + infoBytes; i++) {

			procBytes[i - index] = data.get(i);

		}
		index += infoBytes;
		try {
			String jsonStr = new String(procBytes, "utf-8");
			Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().create();
			JsonProtectionData jp = gson.fromJson(jsonStr, JsonProtectionData.class);
            this.setPlans(jp.getPlans());
			

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("流程解码错误:" + e.getMessage());
		}

	}

	@Override
	public Code getCode() {
		// TODO Auto-generated method stub
		return MainCode.JsonProtectionCode;
	}

	@Override
	public String toString() {
		return "JsonProtectionData [plans=" + plans + "]";
	}

	public List<Map<PlanKey,ProtectionPlan>> getPlans() {
		return plans;
	}
	
	
	
	public void setPlans(List<Map<PlanKey, ProtectionPlan>> plans) {
		this.plans = plans;
	}
    
	/**
	 * 添加保护方案
	 * @author  wavy_zheng
	 * 2021年7月13日
	 * @param index  分区序号，也可能是整机（0）
	 * @param plan
	 */
	public void appendPlan(int index , ProtectionPlan plan) {
		
		if(plans.size() < index ) {
			
			throw new RuntimeException("can not append protection plan : must append continualy");
		}
		PlanKey key = new PlanKey(plan.pn.getWorkType(),plan.pn.isDefaultPlan());
		
		Map<PlanKey,ProtectionPlan> planMap = null;
		if(plans.size() == index) {
			
			planMap = new HashMap<>();
			plans.add(planMap);
		} else {
			
			planMap = plans.get(index);
			
		}
		planMap.put(key, plan);
		
		
	}
	
	public void clearPlans() {
		
		plans.clear();
	}

}
