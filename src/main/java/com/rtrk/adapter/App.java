package com.rtrk.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] decoded = ProtobufATCommandAdapter.decode("AT+CMGF=0".getBytes());
		byte[] encoded=ProtobufATCommandAdapter.encode(decoded);
		System.out.println(new String(encoded));
		int i=5;
		System.out.println(new String(ProtobufATCommandAdapter.environmentVariables.get("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat")));
	}

}
