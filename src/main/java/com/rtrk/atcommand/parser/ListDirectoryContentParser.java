package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;

public class ListDirectoryContentParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		FTPCommand ftpCommand = command.getFtpCommand();
		String commandString = "AT+QFTPLIST";
		if (ftpCommand.hasName()) {
			commandString += "=" + ftpCommand.getName();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		FTPCommand.Builder ftpCommandBuilder = (FTPCommand.Builder) commandBuilder;
		if (!"".equals(params)) {
			String name = params.trim();
			ftpCommandBuilder.setName(name);
		}
	}

}
