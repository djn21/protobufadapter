package com.rtrk.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] decoded = ProtobufATCommandAdapter.decode("AT+QIMUX=1".getBytes());
		byte[] encoded=ProtobufATCommandAdapter.encode(decoded);
		System.out.println(new String(encoded));
		String str=new String(ProtobufATCommandAdapter.environmentVariables.get("tcpipCommand.ENABLE_MULTIPLE_TCPIP_SESSION.enableMultipleTCPIPSession"));
		System.out.println(str);
	}

}
