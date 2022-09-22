package com.nltecklib.protocol.plc.keyence;

/**
 * 基恩士PLC对接
 * TCP scoket 发送 "RD DM0"
 * 
 * 无需握手
 * @author LLC
 * @Date: 20220802
 *
 */
public class KeyenceTool {

	public final static String IP = "192.168.0.1";
	public final static int PORT = 8501; //PLC默认端口8500，对接端口8501
	

	/**多处语法用到空格*/
	private final static String BLANK = " ";
	private final static String CR = "\r\n";
	private final static String RESULT_OK = "OK";
	
	/**
	 * RD、RDS（读）、WR、WRS（写）常用
	 */
	public static enum KeyenceCommand {
		Mn/**更改模式*/, ER/**清除错误*/, WRT/**时间设置*/,
		ST/**强制置位*/, RS/**强制复位*/, STS/**连续强制置位*/, RSS/**连续强制复位*/,
		RD/**读取数据*/, RDS/**读取连续数据*/, RDE/**读取连续数据*/,
		WR/**写入数据*/, WRS/**写入连续数据*/, WRE/**写入连续数据*/,
		WS/**写入设置值*/, WSS/**写入连续设置值*/, MBS,MWS/**监控器登录*/,
		MBR,MWR/**读取监控器*/, RDC/**注释读取*/, BE/**存储体切换*/,
		URD/**读取扩展单元缓冲存储器*/, UWR/**读取扩展单元缓冲存储器*/
	}
	
	public enum Area{
		WR,
		DM/**按字读写*/,
		X/**按位读写，相当于R通道 ，输入继电器*/,
		Y/**按位读写，相当于R通道 ，输出继电器*/;
	}
	
	/**
	 * DM0相当于short 16位
	 *
	 */
	public static enum KeyenceDataType {
		D_UNSIGNED_16BIT(".U")/**16位无符号十进制数 unshort*/, 
		D_SIGNED_16BIT(".S")/**16位有符号十进制数 short*/,
		D_UNSIGNED_32BIT(".D")/**32位无符号十进制数 unint*/,
		D_SIGNED_32BIT(".L")/**32位有符号十进制数 int*/,
		HEX_16BIT(".H")/**16位十六进制数*/,
		NONE("")/**无指定  冒失都支持*/;
	
		private String type;
		
		KeyenceDataType(String type){
			this.type = type;
		}
		
		public  String getType() {
			return type;
		}
	}
	
	/**
	 * 读取PLC连续地址
	 * @param area
	 * @param address
	 * @param length
	 * @param dataType
	 * @return "0001 0000 0000 0000"
	 */
	public static String readBatch(Area area, int address, int length, KeyenceDataType dataType) {
		return KeyenceCommand.RDS + BLANK + area.toString() + address + dataType.getType() + BLANK + length + CR;
	}
	
	/**
	 * 读取PLC单个地址
	 * @param area
	 * @param address
	 * @param dataType
	 * @return "0001"
	 */
	public static String read(Area area, int address, KeyenceDataType dataType) {
		return KeyenceCommand.RD + BLANK + area.toString() + address + dataType.getType() + CR;
	}
	
	
	/**
	   * 单地址写入WR
	   * 案例：修改信号
	 * @param area
	 * @param address
	 * @param data
	 * @param dataType
	 * @return = "OK" or "E1"
	 */
	public static String write(Area area, int address, KeyenceDataType dataType, int data) {
		return KeyenceCommand.WR + BLANK + area.toString() + address + dataType.getType() + BLANK + data + CR;
	}
	
	
	/**
	 * 连续地址写入数据
	 * @param area
	 * @param address
	 * @param length
	 * @param dataType
	 * @param datas
	 * @return = "OK" or "E1"
	 */
	public static String writeBatch(Area area, int address, KeyenceDataType dataType, int... datas) {
		
		String command = KeyenceCommand.WRS + BLANK + area.toString()
		                   + address + dataType.getType() + BLANK + datas.length + CR;
		
		for(int data : datas) {
			command += BLANK + data;
		}
		
		return command;
	}
	
	/***
	 * 写入数据的返回判断
	 * @param result
	 * @return
	 */
	public static boolean writeReponse(String result) {
		
		return result != null && !result.isEmpty() && result.indexOf(RESULT_OK) > -1;
	}
	

	/**使用案例*/
	/***********************
	  
	 
	 				try {
					
					if(!CommonUtil.isEmpty(comText.getText())) {
						new Thread(()->{
							Display.getDefault().syncExec(()->{
								
								byte[] result;
								try {
									result = socket.sendSync(comText.getText().getBytes(), 1000, 1000);
									
									if(result != null && result.length > 0) {
										appendLog(">>>>>>>>>>>>>>接收到PLC回复1：" + new String(result,"utf-8"));
									}else {
										appendLog(">>>>>>>>>>>>>>接收到PLC回复NULL");
									}
									
									List<Byte> data = CommonUtil.convertArrayToList(result);
									
									List<Integer> vals = new ArrayList<>();
									for (int i = 0; i < data.size() / 2; i++) {
										vals.add((int) CommonUtil.compose(data.subList(2 * i, 2 * i + 2).toArray(new Byte[0]), true));
									}
									
									appendLog(">>>>>>>>>>>>>>接收到PLC回复2：" + vals);
									
								} catch (Exception e1) {
									e1.printStackTrace();
									appendLog(CommonUtil.getThrowableException(e1));
								}
								
							});
						}).start();
					}
					
//					if(socket == null || !socket.isConnected()) {
//						appendLog("PLC网络通讯异常......兄弟别搞我....");
//						return;
//					}
					//区域"WR", "DM", "D", "X", "Y"
					Area area = Area.DM;
					if(areaComb.getSelectionIndex() == 0) {
						area = Area.WR;
					} else if(areaComb.getSelectionIndex() == 1) {
						area = Area.DM;
					} else if(areaComb.getSelectionIndex() == 2) {
						area = Area.X;
					} else if(areaComb.getSelectionIndex() == 3) {
						area = Area.Y;
					}
					
					//数据类型
					KeyenceDataType dt = KeyenceDataType.NONE;
					if(dataType.getSelectionIndex() == 0) {
						dt = KeyenceDataType.NONE;
					} else if(dataType.getSelectionIndex() == 1) {
						dt = KeyenceDataType.D_UNSIGNED_16BIT;
					} else if(dataType.getSelectionIndex() == 2) {
						dt = KeyenceDataType.D_SIGNED_16BIT;
					} else if(dataType.getSelectionIndex() == 3) {
						dt = KeyenceDataType.D_UNSIGNED_32BIT;
					} else if(dataType.getSelectionIndex() == 4) {
						dt = KeyenceDataType.D_SIGNED_32BIT;
					} else if(dataType.getSelectionIndex() == 5) {
						dt = KeyenceDataType.HEX_16BIT;
					}
					
					String command = "";
					//"读", "连读", "写", "连写"
					if(optComb.getSelectionIndex() == 0) {
						command = protocolData.read(area, startSpinner.getSelection(), dt);
					} else if(optComb.getSelectionIndex() == 1) {
						command = protocolData.readBatch(area, startSpinner.getSelection(), addrLength.getSelection(), dt);
					} else if(optComb.getSelectionIndex() == 2) {
						command = protocolData.write(area, startSpinner.getSelection(), dt, Integer.parseInt(data1Text.getText()));
					} else if(optComb.getSelectionIndex() == 3) {
						command = protocolData.writeBatch(area, startSpinner.getSelection(), dt,
								Integer.parseInt(data1Text.getText()), Integer.parseInt(data2Text.getText()));
					}
					
					if(CommonUtil.isEmpty(command)) {
						appendLog("当前读写类型不识别.");
						return;
					}
					
					final String commandFINAL = command ;
					
					appendLog(">>>>>>>>>>>>>>发送：" + commandFINAL);
					
					new Thread(()->{
						Display.getDefault().syncExec(()->{
							
							byte[] result;
							try {
								result = socket.sendSync(commandFINAL.getBytes("utf-8"), 1000, 1000);
								if(result != null && result.length > 0) {
									appendLog(">>>>>>>>>>>>>>接收到PLC回复1：" + new String(result,"utf-8"));
								}else {
									appendLog(">>>>>>>>>>>>>>接收到PLC回复NULL");
								}
								List<Byte> data = CommonUtil.convertArrayToList(result);
								
								List<Integer> vals = new ArrayList<>();
								for (int i = 0; i < data.size() / 2; i++) {
									vals.add((int) CommonUtil.compose(data.subList(2 * i, 2 * i + 2).toArray(new Byte[0]), true));
								}
								
								appendLog(">>>>>>>>>>>>>>接收到PLC回复2：" + vals);
								
							} catch (Exception e1) {
								e1.printStackTrace();
								appendLog(CommonUtil.getThrowableException(e1));
							}
							
						});
					}).start();
					
				} catch (Exception ex) {
					ex.printStackTrace();
					appendLog(CommonUtil.getThrowableException(ex));
				}
				
			}
		
	参考文献： https://blog.csdn.net/weixin_42288222/article/details/118705024
	***********************/

}
