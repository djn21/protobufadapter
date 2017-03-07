package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;

public class ListDirectoryContentParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		FTPCommand ftpCommand = command.getFtpCommand();
		String commandString = "AT+QFTPLIST";
		if (ftpCommand.hasName()) {
			commandString += "=" + "\"" + ftpCommand.getName() + "\"";
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		FTPCommand.Builder ftpCommandBuilder = (FTPCommand.Builder) commandBuilder;
		if (!"".equals(params)) {
			if (params.startsWith("\"") && params.endsWith("\"")) {
				String name = params.trim().substring(1, params.trim().length() - 1);
				ftpCommandBuilder.setName(name);
			} else {
				throw new XMLParseException("String format exception for input: " + params);
			}
		}
	}

}
