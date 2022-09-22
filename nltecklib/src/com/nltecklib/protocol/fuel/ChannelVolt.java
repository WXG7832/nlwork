package com.nltecklib.protocol.fuel;

public class ChannelVolt {

	public ChnState chnState = ChnState.Normal;
	public double volt;

	@Override
	public String toString() {
		return "ChannelVolt [chnState=" + chnState + ", volt=" + volt + "]";
	}

	public enum ChnState {

		Normal("正常"), VoltUpper("超电压"), ReverseBattery("电池反接"), NoBattery("电池未接");

		private String description;

		private ChnState(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}
}
