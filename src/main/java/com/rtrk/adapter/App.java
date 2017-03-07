package com.rtrk.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] decoded = ProtobufATCommandAdapter.decode("AT+QMMSW= 3, 1, \"nesto\"".getBytes());
		byte[] encoded=ProtobufATCommandAdapter.encode(decoded);
		System.out.println(new String(encoded));
		/*byte[] decoded1=ProtobufATCommandAdapter.decode("AT+CMGS= 1, 5".getBytes());
		byte[] encoded1=ProtobufATCommandAdapter.encode(decoded1);
		System.out.println(new String(encoded1));*/
	}

}
