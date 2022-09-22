package com.nltecklib.protocol.camera;

public class Environment {
     
	public interface Code {

		public int getCode();
	}
	
	public enum CameraCode implements Code {

		IMAGE(0xE1),TEMP_COUNT(0xE2),FUSION_RADIO(0xF1),PALLET(0xF2),EMISSIVITY(0xF3),LED(0xF4),UPLOAD(0xF6),IP(0xF7),
		HEART(0xF8),TEMP_ALERT(0xF9),TEMP_CHANGE_ALERT(0xFA),TEMP(0xFB),TEMP_RETURN(0xE3),HEART_RETURN(0X31);

		private int code;

		private CameraCode(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
		
		public static CameraCode getCode(int code) {
			for (CameraCode type : CameraCode.values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
	}

	public enum State{

		OFF, ON;
	}
	
	public enum FusionRadio implements Code {
		R000(0x01),R025(0x02),R050(0x03),R075(0x04),R100(0x05);
		private int code;

		private FusionRadio(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
		
		public static FusionRadio getCode(int code) {
			for (FusionRadio radio : FusionRadio.values()) {
				if (radio.getCode() == code) {
					return radio;
				}
			}
			return null;
		}
	}
	
	public enum Pallet implements Code {
		GREEN(0x01),BLACK_WHITE(0x02),RAINBOW(0x03),BLUE(0x04),RED(0x05);
		private int code;

		private Pallet(int code) {

			this.code = code;
		}

		public int getCode() {

			return code;
		}
		
		public static Pallet getCode(int code) {
			for (Pallet pallet : Pallet.values()) {
				if (pallet.getCode() == code) {
					return pallet;
				}
			}
			return null;
		}
	}
	
	public enum UploadMode{
		OFF,ON,ONCE;
	}
	
}

