package com.rtrk.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws InvalidProtocolBufferException {
		
		byte[] decoded = ProtobufATCommandAdapter.decode("AT+QAUDPLAY= 'SD: picture\\C.wav', 1, 80, 1".getBytes());
		byte[] encoded = ProtobufATCommandAdapter.encode(decoded);
		System.out.println(new String(encoded));
		
	}

}
