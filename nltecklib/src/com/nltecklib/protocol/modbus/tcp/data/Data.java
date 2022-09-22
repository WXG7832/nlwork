package com.nltecklib.protocol.modbus.tcp.data;

public interface Data {
	
	public enum Fun{
		/**
		 * 读逻辑线圈
		 */
		READ_COILS("01"),
		/**
		 * 读开关输入  读离散输入寄存器
		 */
		READ_SWTICH("02"),
		
		//保持寄存器的值可以读取也可以修改，通常是一些功能控制寄存器或者输出寄存器等
		/**
		 * 读保持型寄存器
		 */
		READ_HOLD_REGISTER("03"),
		
		//所谓输入寄存器，指的是只能读不能写的寄存器，通常是状态寄存器或者是输入结果寄存器等
		/**
		 * 读输入型寄存器
		 */
		READ_INPUT_REGISTER("04"),
		/**
		 * 写单线圈
		 */
		WRITE_SINGLE_COIL("05"),
		/**
		 * 写单个寄存器
		 */
		WRITE_SINGLE_REGISTER("06"),
		/**
		 * 写多个寄存器
		 */
		WRITE_MULTI_REGISTER("10");

		private String code;
		private Fun(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}	
	}
	
	public static final String SLAVE_ID = "01";//从机id
	
	/**
	 * 解码
	 * @param hex 16进制码
	 * @return 
	 */
	public abstract Object decoder(String hex);
	/**
	 * 编码
	 * @return 字节数组
	 */
	public abstract byte[] encoder();
	
}
