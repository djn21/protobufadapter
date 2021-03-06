package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;

/**
 * 
 * Class for parsing LIST_FILE_NAMES command between original and protobuf format
 * 
 * @author djekanovic
 *
 */
public class ListFileNamesParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		FTPCommand ftpCommand = command.getFtpCommand();
		String commandString = "AT+QFTPNLST";
		if (ftpCommand.hasDirectoryName()) {
			commandString += "=" + ftpCommand.getDirectoryName();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		FTPCommand.Builder ftpCommandBuilder = (FTPCommand.Builder) commandBuilder;
		if (!"".equals(params)) {
			String directoryName = params.trim();
			ftpCommandBuilder.setDirectoryName(directoryName);
		}
	}

}
