package com.nltecklib.protocol.fuel.main;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.main.MainEnvironment.MainCode;

/**
 * 主控心跳协议数据——0x1B
 * 
 * @author caichao_tang
 *
 */
public class HeartData extends Data implements Configable ,Responsable {

    @Override
    public void encode() {
    }

    @Override
    public void decode(List<Byte> encodeData) {
    }

    @Override
    public Code getCode() {
	return MainCode.HEART;
    }

}
