package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.BearerType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Rate;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPCommand;

public class SelectCSDOrGPRSParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		TCPIPCommand tcpipCommand = command.getTcpipCommand();
		String commandString = "AT+QICSGP=";
		commandString += tcpipCommand.getBearerType().getNumber();
		if (tcpipCommand.hasAPN()) {
			commandString += "," + tcpipCommand.getAPN();
		}
		if (tcpipCommand.hasUsername()) {
			commandString += "," + tcpipCommand.getUsername();
		}
		if (tcpipCommand.hasPassword()) {
			commandString += "," + tcpipCommand.getPassword();
		}
		if (tcpipCommand.hasRate()) {
			commandString += "," + tcpipCommand.getRate().getNumber();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		TCPIPCommand.Builder tcpipCommandBuilder = (TCPIPCommand.Builder) commandBuilder;
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter bearerType is missing");
		}
		int bearerType = Integer.parseInt(params.split(",")[0].trim());
		tcpipCommandBuilder.setBearerType(BearerType.valueOf(bearerType));
		if (params.split(",").length > 1) {
			if (bearerType == 0) {
				String dialNumber = params.split(",")[1].trim();
				int rate = Integer.parseInt(params.split(",")[4].trim());
				tcpipCommandBuilder.setDialNumber(dialNumber);
				tcpipCommandBuilder.setRate(Rate.valueOf(rate));
			} else {
				String apn = params.split(",")[1].trim();
				tcpipCommandBuilder.setAPN(apn);
			}
			String userName = params.split(",")[2].trim();
			String password = params.split(",")[3].trim();
			tcpipCommandBuilder.setUsername(userName);
			tcpipCommandBuilder.setPassword(password);
		}
	}

}
