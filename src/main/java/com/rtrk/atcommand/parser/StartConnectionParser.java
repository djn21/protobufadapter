package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPCommand;

public class StartConnectionParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		String commandString = "AT+QIOPEN=";
		TCPIPCommand tcpipCommand = command.getTcpipCommand();
		if (tcpipCommand.hasIndex()) {
			commandString += tcpipCommand.getIndex() + ",";
		}
		commandString += tcpipCommand.getMode();
		commandString += "," + tcpipCommand.getDomainName();
		commandString += "," + tcpipCommand.getPort();
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		TCPIPCommand.Builder tcpipBuilder = (TCPIPCommand.Builder) commandBuilder;
		if (params.split(",").length < 3) {
			throw new XMLParseException("Required parameters is missing");
		}
		if (params.split(",").length > 4) {
			throw new XMLParseException("Number of arguments exception");
		}
		if (params.split(",").length == 3) {
			String mode = params.split(",")[0];
			tcpipBuilder.setMode(mode);
			String domainName = params.split(",")[1];
			tcpipBuilder.setDomainName(domainName);
			int port = Integer.parseInt(params.split(",")[2]);
			tcpipBuilder.setPort(port);
		} else {
			int index = Integer.parseInt(params.split(",")[0]);
			tcpipBuilder.setIndex(index);
			String mode = params.split(",")[1];
			tcpipBuilder.setMode(mode);
			String domainName = params.split(",")[2];
			tcpipBuilder.setDomainName(domainName);
			int port = Integer.parseInt(params.split(",")[3]);
			tcpipBuilder.setPort(port);
		}
	}

}
