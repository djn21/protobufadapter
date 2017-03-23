package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

public class SendSMSMessageParser implements ProtobufParser {

	@Override
	public byte[] encode(Command command) {
		SMSCommand smsCommand = command.getSmsCommand();
		String commandString = "AT+CMGS=";
		if (smsCommand.hasDestinationAddress()) {
			commandString += smsCommand.getDestinationAddress();
		}
		if (smsCommand.hasTypeOfDestinationAddress()) {
			commandString += "," + smsCommand.getTypeOfDestinationAddress();
		}
		if (smsCommand.hasLength()) {
			commandString += smsCommand.getLength();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter missing");
		}
		byte[] mode = ProtobufATCommandAdapter.environmentVariables
				.get("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat");
		SMSCommand.Builder smsCommandBuilder = (SMSCommand.Builder) commandBuilder;
		if (mode != null && "TEXT_MODE".equals(new String(mode))) {
			String destinationAddress = params.split(",")[0].trim();
			smsCommandBuilder.setDestinationAddress(destinationAddress);
			if (params.split(",").length == 2) {
				int typeOfDestinationAddress = Integer.parseInt(params.split(",")[1].trim());
				smsCommandBuilder.setTypeOfDestinationAddress(typeOfDestinationAddress);
			} else if (params.split(",").length > 2) {
				throw new XMLParseException("Wrong number of parameters");
			}
		} else {
			if (params.split(",").length > 1) {
				throw new XMLParseException("Wrong number of parameters");
			}
			int length = Integer.parseInt(params.trim());
			smsCommandBuilder.setLength(length);
		}
	}

}
