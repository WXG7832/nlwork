package com.nltecklib.protocol.lab.main;

import java.io.IOException;
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
import com.nltecklib.protocol.lab.Data;
import com.nltecklib.protocol.lab.Environment.Code;
import com.nltecklib.protocol.lab.main.MainEnvironment.MainCode;
import com.nltecklib.protocol.lab.main.MainEnvironment.WorkMode;
import com.nltecklib.protocol.util.LabProtectionDataAdapter;
import com.nltecklib.protocol.util.ProtocolUtil;
import com.nltecklib.utils.ZipUtil;

public class ProcedureData extends Data implements Configable, Queryable, Responsable, Cloneable {

	private int id; // 用于上位机标识ID
	private int maxId; // 当前最大步次ID记录
	private String name;
	private int stepCount;
	private double standardCapacity; // 流程标准容量
	private int executeStepIndex; // 初始执行步次，流程启动跳步次序号
	private int executeLoopIndex; // 初始循环步次，流程启动步次循环号
	private String info; // 信息
	private String remarks; // 备注
	private String operator; // 操作人
	private List<Step> steps = new ArrayList<Step>();
	private Map<Integer, Data> stepProtections = new HashMap<>(); // 流程步次保护
	private Map<Integer, ChnFirstLevelProtectData> stepGeneralProtections = new HashMap<>(); // 流程步次基础保护
	private ChnFirstLevelProtectData generalProtect; // 全局工步
	private TouchProtectData         touchProtect; //接触保护
	private PoleData                 poleProtect;  //极性保护

	public enum VariableType {

		TimeEnd/* 工步截止时间 */ , VoltageEnd/* 工步截止电压 */ , ACurrentEnd /* 工步截止电流 */, CapacityEnd /* 工步截止容量 */;

	}

	public enum OverStepType {

		TIME, VOLTAGE, CURRENT, CAPACITY, TEMP, INDEX;

		@Override
		public String toString() {

			switch (this) {

			case TIME:
				return "时间";
			case VOLTAGE:
				return "电压";
			case CURRENT:
				return "电流";
			case CAPACITY:
				return "容量";
			case TEMP:
				return "温度";
			case INDEX:
				return "循环数";

			}

			return "";
		}

	}

	/**
	 * 操作符
	 * 
	 * @author wavy_zheng 2020年6月3日
	 *
	 */
	public enum OptType {

		LT, EQ, LTE, GT, GTE;

		@Override
		public String toString() {

			switch (this) {

			case LT:
				return "<";
			case EQ:
				return "=";
			case LTE:
				return "<=";
			case GT:
				return ">";
			case GTE:
				return ">=";

			}

			return "";
		}

	}

	/**
	 * 变量信息
	 * 
	 * @author wavy_zheng 2021年12月24日
	 *
	 */
	public static class VariableInfo implements Cloneable {

		/**
		 * 注,变量名会直接根据变量类型 + index 生成
		 */
		public int index; // 变量序号
		public double val; // 变量值
		public VariableType type;
		public boolean  available ; //是否可以当做变量给其他步次使用

		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		public String getName() {

			if (type != null) {
				return type.name().substring(0, 1).toLowerCase() + index;
			} else {

				return null;
			}
		}

		@Override
		public String toString() {
			return "VariableInfo [index=" + index + ", val=" + val + ", type=" + type + ", available=" + available
					+ "]";
		}
		
		
	}

	/**
	 * 转步信息模型
	 * 
	 * @author wavy_zheng 2020年6月3日
	 *
	 */
	public static class OverStepInfo {

		public OverStepType type = null; // 转步类型
		public double value = 0; // 数值
		public OptType opt = null; // 操作
		public int gotoStep; // -1表示直接结束流程，-2表示暂停，-3表示保护
		public boolean useCRate; // 使用倍率?
		public boolean andCondition; // 是否且转步条件
		public String var; // 结束条件变量

		@Override
		public String toString() {
			return "OverStepInfo [type=" + type + ", value=" + value + ", opt=" + opt + ", gotoStep=" + gotoStep
					+ ", useCRate=" + useCRate + ", andCondition=" + andCondition + ", var=" + var + "]";
		}

	}

	/**
	 * 数据保存策略
	 * 
	 * @author wavy_zheng 2020年6月3日
	 *
	 */
	public static class SaveStepInfo {

		public OverStepType type = null; // 转步类型
		public double value = 0; // 默认45S保存一条数据
		public boolean useCRate; // 使用倍率?

		@Override
		public String toString() {
			return "SaveStepInfo [type=" + type + ", value=" + value + "]";
		}

	}

	/**
	 * 流程步次对象
	 * 
	 * @author wavy_zheng 2021年3月17日
	 *
	 */
	public static class Step implements Cloneable {

		public int id; // ID号，用于识别步次身份
		public int stepIndex; // 当前步次序号
		public int loopIndex; // 当前循环序号
		public int expectedLooIndex; // 期望循环序号，是发给逻辑板预期的循环计数
		public int loopCount; // 循环次数
		public int loopActualIndex; // 循环累计序号
		public WorkMode workMode = null;
		public double constCondition; // 恒定条件
		// public double loopCapacity; //循环容量
		// public boolean useLoopCap; //使用循环容量
		public boolean useCRate; // 恒定条件电流或容量时使用倍率?
		public List<OverStepInfo> overStepInfos = new ArrayList<OverStepInfo>();
		public List<SaveStepInfo> saveStepInfos = new ArrayList<SaveStepInfo>();
		public List<Step> branch = new ArrayList<Step>();
		public List<VariableInfo> variables = new ArrayList<>(); // 变量赋值定义结合
		public transient Step parent; // 父对象,该对象不会进行序列化
		public String conditionVar; // 恒定条件变量
		public Data   protection;   //关联的工步保护

		public VariableInfo findVariableBy(VariableType type) {

			for (VariableInfo var : variables) {

				if (var.type == type) {

					return var;
				}
			}

			return null;

		}

		public List<OverStepInfo> findOverStepInfoBy(OverStepType type, OptType... optTypes) {

			List<OverStepInfo> list = new ArrayList<>();
			for (OverStepInfo osi : overStepInfos) {

				if (osi.type == type) {

					for (OptType ot : optTypes) {

						if (ot == osi.opt) {

							list.add(osi);
							break;
						}
					}

				}
			}

			return list;
		}

		public SaveStepInfo findSaveStepInfoBy(OverStepType type) {

			for (SaveStepInfo osi : saveStepInfos) {

				if (osi.type == type) {

					return osi;
				}
			}

			return null;
		}
  
		
		
		@Override
		public String toString() {
			return "Step [id=" + id + ", stepIndex=" + stepIndex + ", loopIndex=" + loopIndex + ", expectedLooIndex="
					+ expectedLooIndex + ", loopCount=" + loopCount + ", loopActualIndex=" + loopActualIndex
					+ ", workMode=" + workMode + ", constCondition=" + constCondition + ", useCRate=" + useCRate
					+ ", overStepInfos=" + overStepInfos + ", saveStepInfos=" + saveStepInfos + ", branch=" + branch
					+ ", variables=" + variables + ", conditionVar=" + conditionVar + ", protection=" + protection
					+ "]";
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof Step) {

				Step another = (Step) obj;
				if (this.stepIndex == another.stepIndex) {

					if (this.stepIndex == 0) {

						if (this.branch.isEmpty() || another.branch.isEmpty()) {

							return false;
						}
						if (this.branch.size() != another.branch.size()) {

							return false;
						}
						// 是否同一个分支，通过子步次序号确定
						for (int n = 0; n < this.branch.size(); n++) {

							return this.branch.get(n).equals(another.branch.get(n));
						}

					} else {

						return true;
					}

				}

			}

			return false;
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return this.stepIndex;
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			Step cloneStep = (Step) super.clone();
			cloneStep.parent = null;
			cloneStep.overStepInfos = new ArrayList<>();
			for (OverStepInfo osi : this.overStepInfos) {

				OverStepInfo osi2 = new OverStepInfo();
				osi2.gotoStep = osi.gotoStep;
				osi2.opt = osi.opt;
				osi2.type = osi.type;
				osi2.value = osi.value;
				osi2.useCRate = osi.useCRate;
				osi2.andCondition = osi.andCondition;
				osi2.var = osi.var;
				cloneStep.overStepInfos.add(osi2);
			}

			cloneStep.saveStepInfos = new ArrayList<>();
			for (SaveStepInfo ssi : this.saveStepInfos) {

				SaveStepInfo ssi2 = new SaveStepInfo();
				ssi2.type = ssi.type;
				ssi2.value = ssi.value;
				ssi2.useCRate = ssi.useCRate;
				cloneStep.saveStepInfos.add(ssi2);
			}
			// 变量列表赋值
			cloneStep.variables = new ArrayList<>();
			for (VariableInfo var : this.variables) {

				cloneStep.variables.add((VariableInfo) var.clone());
			}
			
			//工步赋值
			if(this.protection != null) {
				
				cloneStep.protection = (Data) this.protection.clone();
			}
			

			cloneStep.branch = new ArrayList<>();
			for (Step step : this.branch) {

				cloneStep.branch.add((Step) step.clone());

			}

			return cloneStep;
		}

	}

	@Override
	public boolean supportMain() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void encode() {

		Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new LabProtectionDataAdapter()).create();
		String jsonStr = gson.toJson(this);
		
		if(Data.isUseStringCompress()) {
			
			try {
				jsonStr = ZipUtil.compress(jsonStr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("压缩流程错误:" + e.getMessage());
			}
		}

		try {
			byte[] arr = jsonStr.getBytes("ISO-8859-1");
			data.addAll(Arrays.asList(ProtocolUtil.split((long) arr.length, 2, true)));
			for (int n = 0; n < arr.length; n++) {

				data.add(arr[n]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("流程编码错误:" + e.getMessage());
		}
		data.addAll(Arrays.asList());

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
			String jsonStr = new String(procBytes,"ISO-8859-1");
			if(Data.isUseStringCompress()) {
				
				try {
					jsonStr = ZipUtil.unCompress(jsonStr);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("解压流程错误:" + e.getMessage());
				}
			}
			
			Gson gson = new GsonBuilder().registerTypeAdapter(Data.class, new LabProtectionDataAdapter()).create();
			ProcedureData p = gson.fromJson(jsonStr, ProcedureData.class);

			this.name = p.getName();
			this.standardCapacity = p.getStandardCapacity();
			this.stepCount = p.getStepCount();
			this.steps = p.getSteps();
			this.maxId = p.getMaxId();
			this.stepGeneralProtections = p.getStepGeneralProtections();
			this.stepProtections = p.getStepProtections();
			this.operator = p.getOperator();
			this.remarks = p.getRemarks();
			this.info = p.getInfo();
			this.generalProtect = p.getGeneralProtect();
			this.poleProtect = p.getPoleProtect();
			this.touchProtect = p.getTouchProtect();

		} 
		catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			throw new RuntimeException("流程解码错误:" + e.getMessage());
		}

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		ProcedureData another = (ProcedureData) super.clone();
		another.steps = new ArrayList<>();

		for (int n = 0; n < this.steps.size(); n++) {

			another.steps.add((Step) this.steps.get(n).clone());
		}

		return another;
	}

	@Override
	public Code getCode() {
		return MainCode.ProcedureCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public double getStandardCapacity() {
		return standardCapacity;
	}

	public void setStandardCapacity(double standardCapacity) {
		this.standardCapacity = standardCapacity;
	}

	@Override
	public boolean supportChannel() {
		return true;
	}

	public int getExecuteStepIndex() {
		return executeStepIndex;
	}

	public void setExecuteStepIndex(int executeStepIndex) {
		this.executeStepIndex = executeStepIndex;
	}

	public int getExecuteLoopIndex() {
		return executeLoopIndex;
	}

	public void setExecuteLoopIndex(int executeLoopIndex) {
		this.executeLoopIndex = executeLoopIndex;
	}

	public int getMaxId() {
		return maxId;
	}

	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * 配置步次一般性保护
	 * 
	 * @author wavy_zheng 2021年8月6日
	 * @param stepIndex
	 * @param protection
	 */
	public void setStepGeneralProtection(int stepIndex, ChnFirstLevelProtectData protection) {

		stepGeneralProtections.put(stepIndex, protection);
	}

	/**
	 * 配置流程步次特殊的保护
	 * 
	 * @author wavy_zheng 2021年8月6日
	 * @param stepIndex
	 * @param protection
	 */
	public void setStepProtection(int stepIndex, Data protection) {

		stepProtections.put(stepIndex, protection);
	}

	public ChnFirstLevelProtectData getStepGeneralProtection(int stepIndex) {

		return stepGeneralProtections.get(stepIndex);
	}

	public Data getStepProtection(int stepIndex) {

		return stepProtections.get(stepIndex);
	}

	/**
	 * 
	 * @author wavy_zheng 2021年8月6日
	 * @param stepIndex
	 *            0表示清除所有步次的保护
	 */
	public void clearStepProtection(int stepIndex) {

		if (stepIndex == 0) {

			stepGeneralProtections.clear();
			stepProtections.clear();
		} else {

			stepGeneralProtections.remove(stepIndex);
			stepProtections.remove(stepIndex);
		}
	}

	public Map<Integer, Data> getStepProtections() {
		return stepProtections;
	}

	public void setStepProtections(Map<Integer, Data> stepProtections) {
		this.stepProtections = stepProtections;
	}

	public Map<Integer, ChnFirstLevelProtectData> getStepGeneralProtections() {
		return stepGeneralProtections;
	}

	public void setStepGeneralProtections(Map<Integer, ChnFirstLevelProtectData> stepGeneralProtections) {
		this.stepGeneralProtections = stepGeneralProtections;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public ChnFirstLevelProtectData getGeneralProtect() {
		return generalProtect;
	}

	public void setGeneralProtect(ChnFirstLevelProtectData generalProtect) {
		this.generalProtect = generalProtect;
	}
	
	
	
	

	public TouchProtectData getTouchProtect() {
		return touchProtect;
	}

	public void setTouchProtect(TouchProtectData touchProtect) {
		this.touchProtect = touchProtect;
	}

	public PoleData getPoleProtect() {
		return poleProtect;
	}

	public void setPoleProtect(PoleData poleProtect) {
		this.poleProtect = poleProtect;
	}

	@Override
	public String toString() {
		return "ProcedureData [id=" + id + ", maxId=" + maxId + ", name=" + name + ", stepCount=" + stepCount
				+ ", standardCapacity=" + standardCapacity + ", executeStepIndex=" + executeStepIndex
				+ ", executeLoopIndex=" + executeLoopIndex + ", info=" + info + ", remarks=" + remarks + ", operator="
				+ operator + ", steps=" + steps + ", stepProtections=" + stepProtections + ", stepGeneralProtections="
				+ stepGeneralProtections + ", generalProtect=" + generalProtect + "]";
	}

}
