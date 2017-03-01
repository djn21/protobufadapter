package com.rtrk.adapter;

import java.io.File;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommands.ATCommand.Command;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] decoded = ProtobufAdapter.decode("AT+QHTTPURL=20,40".getBytes());
		Command cmd = Command.parseFrom(decoded);
		System.out.println(cmd.getHttpCommand().getMessageType());
	}

}
