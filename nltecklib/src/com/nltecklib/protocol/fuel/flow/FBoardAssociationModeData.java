package com.nltecklib.protocol.fuel.flow;

import java.util.List;

import com.nltecklib.protocol.Configable;
import com.nltecklib.protocol.Queryable;
import com.nltecklib.protocol.Responsable;
import com.nltecklib.protocol.fuel.Data;
import com.nltecklib.protocol.fuel.Environment.AssociationMode;
import com.nltecklib.protocol.fuel.Environment.Code;
import com.nltecklib.protocol.fuel.flow.FlowEnviroment.FlowCode;

/**
 * 流量板连锁模式
 * 
 * @author caichao_tang
 *
 */
public class FBoardAssociationModeData extends Data implements Configable, Queryable, Responsable {

    /**
     * 关联模式
     */
    private AssociationMode controlMode = AssociationMode.NOT_CHAIN;

    public AssociationMode getControlMode() {
	return controlMode;
    }

    public void setControlMode(AssociationMode controlMode) {
	this.controlMode = controlMode;
    }

    @Override
    public void encode() {
	data.add((byte) controlMode.ordinal());
    }

    @Override
    public void decode(List<Byte> encodeData) {
	data = encodeData;
	int index = 0;
	controlMode = AssociationMode.values()[data.get(index)];
    }

    @Override
    public Code getCode() {
	return FlowCode.ASSOCIATION_CODE;
    }

}
