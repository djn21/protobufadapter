package com.rtrk.adapter;

import java.io.File;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommands.ATCommand.Command;
import com.rtrk.atcommands.ATCommand.GeneralCommand;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InvalidProtocolBufferException {
		byte[] decoded = ProtobufAdapter.decode("ATI".getBytes(), new File("generalCommand.xml"));
		Command cmd=Command.parseFrom(decoded);
		GeneralCommand g=cmd.getGeneralCommand();
		System.out.println(g.getMessageType());
		System.out.println(g.getExecution());
		byte[] encoded = ProtobufAdapter.encode(decoded, new File("generalCommand.xml"));
		System.out.println(new String(encoded));
	}
}
