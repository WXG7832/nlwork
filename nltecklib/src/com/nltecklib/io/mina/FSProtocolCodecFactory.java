package com.nltecklib.io.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import com.nltecklib.io.NlteckPackageFactory;

public class FSProtocolCodecFactory implements ProtocolCodecFactory {

    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;
    private boolean client; // 是否用于客户端协议解析

    public FSProtocolCodecFactory(boolean client, NlteckPackageFactory factory) {

	this.client = client;

	encoder = new FSProtocolEncoder(client, factory);
	decoder = new FSProtocolDecoder(client, factory);

    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {

	return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {

	return decoder;
    }

    public void setListener(DebugDataListener listener) {

	((FSProtocolEncoder) encoder).setListener(listener);
	((FSProtocolDecoder) decoder).setListener(listener);
    }

}
