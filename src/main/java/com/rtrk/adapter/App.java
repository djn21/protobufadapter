package com.rtrk.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] decoded = ProtobufATCommandAdapter.decode("AT+QHTTPDL= 'file.txt', 50, 65536".getBytes());
		byte[] encoded=ProtobufATCommandAdapter.encode(decoded);
		System.out.println(new String(encoded));
		/*byte[] decoded1=ProtobufATCommandAdapter.decode("AT+QISEND= 1, 1459".getBytes());
		byte[] encoded1=ProtobufATCommandAdapter.encode(decoded1);
		System.out.println(new String(encoded1));*/
	}

}
