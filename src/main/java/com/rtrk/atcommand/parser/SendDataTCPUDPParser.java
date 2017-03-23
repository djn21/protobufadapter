package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPCommand;

public class SendDataTCPUDPParser implements ProtobufParser {

	@Override
	public byte[] encode(Command command) {
		TCPIPCommand tcpipCommand = command.getTcpipCommand();
		String commandString = "AT+QISEND=";
		if (tcpipCommand.hasIndex()) {
			commandString += tcpipCommand.getIndex();
			if (tcpipCommand.hasLength()) {
				commandString += "," + tcpipCommand.getLength();
			}
		} else if (tcpipCommand.hasLength()) {
			commandString += tcpipCommand.getLength();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter enableMultipleTCPIPSession missing");
		}
		boolean enableMultipleTCPIPSession = Boolean
				.parseBoolean(new String(ProtobufATCommandAdapter.environmentVariables
						.get("tcpipCommand.ENABLE_MULTIPLE_TCPIP_SESSION.enableMultipleTCPIPSession")));
		TCPIPCommand.Builder tcpipCommandBuilder = (TCPIPCommand.Builder) commandBuilder;
		if (enableMultipleTCPIPSession) {
			int index = Integer.parseInt(params.split(",")[0].trim());
			if (params.split(",").length == 2) {
				int length = Integer.parseInt(params.split(",")[1].trim());
				if (length >= 1460) {
					throw new XMLParseException("Parameter length must be less then 1460");
				}
				tcpipCommandBuilder.setLength(length);
			} else if (params.split(",").length > 2) {
				throw new XMLParseException("Number of parameters must be less then 2");
			}
			tcpipCommandBuilder.setIndex(index);
		} else {
			int length = Integer.parseInt(params.trim());
			tcpipCommandBuilder.setLength(length);
		}
	}

}
