package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CallRelatedCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;

public class MobileOriginatedCallParser implements ProtobufParser {

	@Override
	public byte[] encode(Command command) {
		CallRelatedCommand callCommand = command.getCallRelatedCommand();
		String commandString = "ATD";
		commandString += callCommand.getNumber();
		if (callCommand.hasGSMModifier()) {
			commandString += callCommand.getGSMModifier();
		}
		if (callCommand.hasDelimiter()) {
			commandString += ";";
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		CallRelatedCommand.Builder callCommandBuilder = (CallRelatedCommand.Builder) commandBuilder;
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter number is missing");
		}
		String number = params;
		if (params.contains("I")) {
			number = params.split("I")[0];
			if (params.split("I").length == 2 && params.split("I")[1].equals(";")) {
				callCommandBuilder.setDelimiter(true);
			}
			callCommandBuilder.setGSMModifier("I");
		} else if (params.contains("i")) {
			number = params.split("i")[0];
			if (params.split("i").length == 2 && params.split("i")[1].equals(";")) {
				callCommandBuilder.setDelimiter(true);
			}
			callCommandBuilder.setGSMModifier("i");
		} else if (params.contains("G")) {
			number = params.split("G")[0];
			if (params.split("G").length == 2 && params.split("G")[1].equals(";")) {
				callCommandBuilder.setDelimiter(true);
			}
			callCommandBuilder.setGSMModifier("G");
		} else if (params.contains("g")) {
			number = params.split("g")[0];
			if (params.split("g").length == 2 && params.split("g")[1].equals(";")) {
				callCommandBuilder.setDelimiter(true);
			}
			callCommandBuilder.setGSMModifier("g");
		}
		callCommandBuilder.setNumber(number);
	}

}
