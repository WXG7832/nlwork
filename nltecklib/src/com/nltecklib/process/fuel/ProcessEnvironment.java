package com.nltecklib.process.fuel;

/**
 * 燃料电池流程环境
 * 
 * @author caichao_tang
 *
 */
public class ProcessEnvironment {

	/**
	 * 步次类型枚举
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum StepType {
		DO(0, "顺序执行"), DO_WHILE(1, "跳转循环"), IF_ELSE(2, "条件分支"), WAIT_UNTIL(3, "等待跳转");

		private int code;
		private String describe;

		private StepType(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

	}

	/**
	 * 流程类型枚举
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum ProcessType {
		MAIN_PROCESS(0, "主流程"), INSERT_PROCESS(1, "嵌套流程"), START_PROCESS(2, "开机流程"), STOP_PROCESS(3, "停机流程");

		private int code;
		private String describe;

		private ProcessType(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

	}

	/**
	 * 步次的操作执行类型
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum OperationType {
		COMPONENT_ACTION(0, "器件操作"), STEP_JUMP(1, "步次跳转"), PROCESS_JUMP(2, "流程跳转");

		private int code;
		private String describe;

		private OperationType(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

	}

	/**
	 * 流程步次的逻辑运算标志
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum LogicSymbol {
		AND(0, "与"), OR(1, "或"), NOT(2, "非");

		private int code;
		private String describe;

		private LogicSymbol(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

		/**
		 * 根据逻辑符描述获得逻辑符枚举对象
		 * 
		 * @param describe
		 * @return
		 */
		public static LogicSymbol getLogicSymbolFromDescribe(String describe) {
			for (LogicSymbol logicSymbol : LogicSymbol.values()) {
				if (logicSymbol.getDescribe().equals(describe)) {
					return logicSymbol;
				}
			}
			return null;
		}
	}

	/**
	 * 流程步次的条件运算运算符
	 * 
	 * @author caichao_tang
	 *
	 */
	public enum ConditionSymbol {
		EQUAL(0, "="), GREATER(1, ">"), GRATER_EQUAL(2, ">="), LESS(3, "<"), LESS_EQUAL(4, "<=");

		private int code;
		private String describe;

		private ConditionSymbol(int code, String describe) {
			this.code = code;
			this.describe = describe;
		}

		public int getCode() {
			return code;
		}

		public String getDescribe() {
			return describe;
		}

		/**
		 * 根据条件符描述获得条件符枚举对象
		 * 
		 * @param describe
		 * @return
		 */
		public static ConditionSymbol getConditionSymbolFromDescribe(String describe) {
			for (ConditionSymbol conditionSymbol : ConditionSymbol.values()) {
				if (conditionSymbol.getDescribe().equals(describe)) {
					return conditionSymbol;
				}
			}
			return null;
		}

	}

}
