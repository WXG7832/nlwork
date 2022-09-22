package com.nltecklib.protocol.fins;

public class Environment {
	
	public interface Code {

		public int getCode();
	}
	
	/**
	 * 
	* @ClassName: Area  
	* @Description: 区域
	* @author zhang_longyong  
	* @date 2019年12月20日
	 */
	public enum Area{
		CIO,WR,DM,HR,TIM,AR,CNT;
		
		public byte getCode(boolean isBit) {
			if (!isBit){   //字处理            
				switch (this) {
				case CIO:
					return (byte) 0xB0;
				case WR:
					return (byte) 0xB1;
				case DM:
					return (byte) 0x82;
				case HR:
					return (byte) 0xB2;
				case TIM:
					return (byte) 0x89;
				case AR:
					return (byte) 0xB3;
				case CNT:
					return (byte) 0x89;
				}
			}else { //位处理            
				switch (this) {
				case CIO:
					return 0x30;
				case WR:
					return 0x31;
				case DM:
					return 0x02;
				case HR:
					return 0x32;
				case TIM:
					return 0x09;
				case AR:
					return 0x33;
				case CNT:
					return 0x09;
				}
			}
			return 0;
		}
	}
	
	/**
	 * 
	* @ClassName: Orient  
	* @Description: 方向  
	* @author zhang_longyong  
	* @date 2019年12月20日
	 */
	public enum Orient implements Code{
		READ(257),WRITE(258);
		private int code;
		private Orient(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
		
		public static Orient getCode(int code) {
			for (Orient type : Orient.values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
	}
	
	/**
	 * 
	* @ClassName: Error  
	* @Description: 错误码
	* @author zhang_longyong  
	* @date 2019年12月20日
	 */
	public enum Error implements Code{
		NO(0),HEADER_ERROR(1),DATALENGTH_ERROR(2),COMMAND_ERROR(3),ALL_USE_ERROR(0x20),NODE_CONNECT_ERROR(0x21),
		IP_ERROR(0x22),CLIENT_ADDR_ERROR(0x23),SAME_FINSNODE_ERROR(0x24),NODE_USED_ERROR(0x25);
		private int code;

		private Error(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
		
		public static Error getCode(int code) {
			for (Error type : Error.values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			switch (this) {
			case NO:
				return "OK";
			case NODE_CONNECT_ERROR:
				return "The specified node is already connected";
			case ALL_USE_ERROR:
				return "All connections ars is use";
			case CLIENT_ADDR_ERROR:
				return "The client FINS node address is out of range";
			case COMMAND_ERROR:
				return "The command is not supported";
			case DATALENGTH_ERROR:
				return "The data length is too long";
			case HEADER_ERROR:
				return "The header is not FINS";
			case IP_ERROR:
				return "Attempt to access a protected node from an unspecified IP address";
			case NODE_USED_ERROR:
				return "All the node addresses available for allocation have been userd";
			case SAME_FINSNODE_ERROR:
				return "The same FINS node address is being used by the client and server";
			}
			return "";
		}
		
	}
	/**
	 * 数据操作单元
	 * @author wavy_zheng
	 * 2020年7月31日
	 *
	 */
	public enum DataUnit {
		
		BYTE/*按字节操作*/ , BIT/*按位操作*/;
	}
	
	/**
	 *
	* @ClassName: Command  
	* @Description: 命令码
	* @author zhang_longyong  
	* @date 2019年12月20日
	 */
	public enum Command implements Code {
		HANDSHAKE(0),DATA(2);
		private int code;

		private Command(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
		
		public static Command getCode(int code) {
			for (Command type : Command.values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
	}
	
	public enum Result implements Code{
		OK(0X0),SERVICE_CANCELED(0X1),LOCAL_NOT_IN_NETWORK(0X101),TOKEN_TIMEOUT(0X102),RETRIES_FAILED(0X103),
		SEND_FRAME(0X104),RANGE_ERROR(105),ADDR_DUPLICATION(106),DESTINATION_NOT_IN_NETWORK(0X0201),UNIT_MISS(0X202),
		THIRD_MISS(0X203),DESTINATION_BUSY(0X204),RESPONSE_TIMEOUT(0X205),COMMUNICATIONS_ERROR(0X301),CPU_ERROR(0X302),
		CONTROLL_ERROR(0x303),UNIT_NUMBER_ERROR(0X304),UNDEFINED_COMMAND(0X401),NOTSUPPORT_MODEL(0X402),
		DESTINATION_SET_ERROR(0X501),NO_ROUT_TABLE(0X502),ROUT_TABLE_ERROR(0X503),TOO_MANY_RELAYS(0X504),
		COMMAND_LONG(0X1001),COMMAND_SHORT(0X1002),DATA_NOT_MATCH(0X1003),COMMAND_FORMAT_ERROR(0X1004),HEADER_ERROR(0X1005),
		CLASSIFICATION_MISS(0X1101),ACCESS_SIZE_ERROR(0X1102),ADDRESS_RANGE_ERROR(0X1103),ADDRESS_RANGE_EXCEEDED(0X1104),
		PROGRAM_MISS(0X1106),RELATIONAL_ERROR(0X1109),DUPLICATE_DATA_ACCESS(0X110A),REPONSE_LONG(0X110B),PARAMETER_ERROR(0X110C),
		PROTECT(0X2002),TABLE_MISSING(0X2003),DATA_MISSING(0X2004),PROGRAM_MISSING(0X2005),FILE_MISSING(0X2006),DATA_MISMATCH(0X2007),
		READ_ONLY(0X2101),CANNOT_WRITE_DATA(0X2102),CANNOT_REGISTER(0X2103),PROGRAMMISSING(0X2105),FILE_MISS(0X2106),
		FILE_NAME_EXIST(0X2107),CANNOT_CHANGE(0X2108),NO_POSIBLE_EXCUTING(0X2201),NO_POSIBLE_RUNNING(0X2202),WRONG_PLC_MODE1(0X2203),
		WRONG_PLC_MODE2(0X2204),WRONG_PLC_MODE3(0X2205),WRONG_PLC_MODE4(0X2206),NO_POLLING_NODE(0X2207),STEP_CANNOT_EXCUTE(0X2208),
		FILE_DEVICE_MISSING(0X2301),MEMORY_MISSING(0X2302),CLOCK_MISSING(0X2303),TABLE_MISS(0X2401),MEMORY_ERROR(0X2502),
		IO_SET_ERRROR(0X2503),MANY_IO_POINT(0X2504),CPU_ERROR1(0X2505),IO_DUPLICATION(0X2506),CPU_ERROR2(0X2507),SYSMAC_ERROR(0X2509),
		CPU_UNIT_ERROR(0X250A),SYSMAC_NO_DUPLICATION(0X250D),MEMORY_ERROR2(0X250F),SYSMAC_TERMINATOR_MISSING(0X2510),
		NO_PROTECTION(0X2601),INCORRECT_PASSWORD(0X2602),PROTECTED(0X2604),SERVICE_EXCUTING(0X2605),SERVICE_STOPPED(0X2606),
		NO_EXCUTION_RIGHT(0X2607),BEFORE_EXCUTION(0X2608),ITEMS_NOT_SET(0X2609),NUMBER_DEFINED(0X260A),ERROR_NOT_CLEAR(0X260B),
		NO_ACESS_RIGHT(0X3001),SERVICE_ABORTED(0X4001);
		
		
	
		@Override
		public String toString() {
			switch (this) {
			case OK:
				return "success";
			case SERVICE_CANCELED:
				return "service canceled";
			case LOCAL_NOT_IN_NETWORK:
				return "local node not in network";
			case TOKEN_TIMEOUT:
				return "token timeout";
			case RETRIES_FAILED:
				return "retries failed";
			case SEND_FRAME:
				return "too many send frames";
			case RANGE_ERROR:
				return "node address range error";
			case ADDR_DUPLICATION:
				return "node address duplication";
			case DESTINATION_NOT_IN_NETWORK:
				return "destination node not in network";
			case UNIT_MISS:
				return "unit missing";
			case THIRD_MISS:
				return "third node missing";
			case DESTINATION_BUSY:
				return "destination node busy";
			case RESPONSE_TIMEOUT:
				return "response timeout";
			case COMMUNICATIONS_ERROR:
				return "communications controller error";
			case CPU_ERROR:
				return "CPU unit error";
			case CONTROLL_ERROR:
				return "controller error";
			case UNIT_NUMBER_ERROR:
				return "unit number error";
			case UNDEFINED_COMMAND:
				return "undefined command";
			case NOTSUPPORT_MODEL:
				return "not supported by model/version";
			case DESTINATION_SET_ERROR:
				return "destination address setting error";
			case NO_ROUT_TABLE:
				return "no routing tables";
			case ROUT_TABLE_ERROR:
				return "routing table error";
			case TOO_MANY_RELAYS:
				return "too many relays";
			case COMMAND_LONG:
				return "command too long";
			case COMMAND_SHORT:
				return "command too short";
			case DATA_NOT_MATCH:
				return "elements/data don't match";
			case COMMAND_FORMAT_ERROR:
				return "command format error";
			case HEADER_ERROR:
				return "header error";
			case CLASSIFICATION_MISS:
				return "area classification missing";
			case ACCESS_SIZE_ERROR:
				return "access size error";
			case ADDRESS_RANGE_ERROR:
				return "address range error";
			case ADDRESS_RANGE_EXCEEDED:
				return "address range exceeded";
			case PROGRAM_MISS:
				return "program missing";
			case RELATIONAL_ERROR:
				return "relational error";
			case DUPLICATE_DATA_ACCESS:
				return "duplicate data access";
			case REPONSE_LONG:
				return "response too long";
			case PARAMETER_ERROR:
				return "parameter error";
			case PROTECT:
				return "protected";
			case TABLE_MISSING:
				return "table missing";
			case DATA_MISSING:
				return "data missing";
			case PROGRAM_MISSING:
				return "program missing";
			case FILE_MISSING:
				return "file missing";
			case DATA_MISMATCH:
				return "data mismatch";
			case READ_ONLY:
				return "read-only";
			case CANNOT_WRITE_DATA:
				return "protected , cannot write data link table";
			case CANNOT_REGISTER:
				return "cannot register";
			case PROGRAMMISSING:
				return "program missing";
			case FILE_MISS:
				return "file missing";
			case FILE_NAME_EXIST:
				return "file name already exists";
			case CANNOT_CHANGE:
				return "cannot change";
			case NO_POSIBLE_EXCUTING:
				return "not possible during execution";
			case NO_POSIBLE_RUNNING:
				return "not possible while running";
			case WRONG_PLC_MODE1:
				return "wrong PLC mode";
			case WRONG_PLC_MODE2:
				return "wrong PLC mode";
			case WRONG_PLC_MODE3:
				return "wrong PLC mode";
			case WRONG_PLC_MODE4:
				return "wrong PLC mode";
			case NO_POLLING_NODE:
				return "specified node not polling node";
			case STEP_CANNOT_EXCUTE:
				return "step cannot be executed";
			case FILE_DEVICE_MISSING:
				return "file device missing";
			case MEMORY_MISSING:
				return "memory missing";
			case CLOCK_MISSING:
				return "clock missing";
			case TABLE_MISS:
				return "table missing";
			case MEMORY_ERROR:
				return "memory error";
			case IO_SET_ERRROR:
				return "I/O setting error";
			case MANY_IO_POINT:
				return "too many I/O points";
			case CPU_ERROR1:
				return "CPU bus error";
			case IO_DUPLICATION:
				return "I/O duplication";
			case CPU_ERROR2:
				return "CPU bus error";
			case SYSMAC_ERROR:
				return "SYSMAC BUS/2 error";
			case CPU_UNIT_ERROR:
				return "CPU bus unit error";
			case SYSMAC_NO_DUPLICATION:
				return "SYSMAC BUS No duplication";
			case MEMORY_ERROR2:
				return "memory error";
			case SYSMAC_TERMINATOR_MISSING:
				return "SYSMAC BUS terminator missing";
			case NO_PROTECTION:
				return "no protection";
			case INCORRECT_PASSWORD:
				return "incorrect password";
			case PROTECTED :
				return "protected";
			case SERVICE_EXCUTING:
				return "service already executing";
			case SERVICE_STOPPED:
				return "service stopped";
			case NO_EXCUTION_RIGHT:
				return "no execution right";
			case BEFORE_EXCUTION:
				return "settings required before execution";
			case ITEMS_NOT_SET:
				return "necessary items not set";
			case NUMBER_DEFINED:
				return "number already defined";
			case ERROR_NOT_CLEAR:
				return "error will not clear";
			case NO_ACESS_RIGHT:
				return "no access right";
			case SERVICE_ABORTED:
				return "service aborted";
 			}
			return "";
		}
	
		private int code;

		private Result(int code) {
			this.code = code;
		}

		@Override
		public int getCode() {
			return code;
		}

		public static Result getCode(int code) {
			for (Result type : Result.values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
		
	}
	
	public enum Type{
		INT,DINT,BOOL
	}
	
}

