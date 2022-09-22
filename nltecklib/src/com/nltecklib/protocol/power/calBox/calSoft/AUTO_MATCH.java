package com.nltecklib.protocol.power.calBox.calSoft;

import java.util.List;

import com.nltecklib.protocol.power.Data;
import com.nltecklib.protocol.power.Environment.Code;
import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.CalSoftCode;
import com.nltecklib.protocol.util.ProtocolUtil;

// 自动对接, 校准板与设备分区配对
public class AUTO_MATCH extends Data {

    private boolean enabled;

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
	// TODO Auto-generated method stub
	data.add((byte) (enabled ? 1 : 0));
    }

    @Override
    public void decode(List<Byte> encodeData) {
	// TODO Auto-generated method stub

	data = encodeData;
	int index = 0;
	enabled = ProtocolUtil.getUnsignedByte(data.get(index++)) == 1;
    }

    @Override
    public Code getCode() {
	// TODO Auto-generated method stub
	return CalSoftCode.AUTO_MATCH;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
