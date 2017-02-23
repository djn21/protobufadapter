package com.rtrk.adapter;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		byte[] decoded = ProtobufAdapter.decode("AT+QHTTPDL=?".getBytes(), new File("httpCommand.xml"));
		byte[] encoded = ProtobufAdapter.encode(decoded, new File("httpCommand.xml"));
		System.out.println(new String(encoded));
	}
}
