package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageMode;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageStatusList;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

public class ListSMSMessagesParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		SMSCommand smsCommand = command.getSmsCommand();
		String commandString = "AT+CMGL=";
		if (smsCommand.hasMessageStatusListPDU()) {
			commandString += smsCommand.getMessageStatusListPDU().getNumber();
		} else if (smsCommand.hasMessageStatusText()) {
			commandString += smsCommand.getMessageStatusText();
		}
		if (smsCommand.hasMessageMode()) {
			commandString += "," + smsCommand.getMessageMode().getNumber();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		SMSCommand.Builder smsBuilder = (SMSCommand.Builder) commandBuilder;
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter messageStatus is missing");
		}
		String messageFormat;
		if (ProtobufATCommandAdapter.environmentVariables
				.containsKey("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat")) {
			messageFormat = new String(ProtobufATCommandAdapter.environmentVariables
					.get("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat"));
		} else {
			messageFormat = "PDU_MODE";
		}
		if (messageFormat.equals("PDU_MODE")) {
			int messageStatus = Integer.parseInt(params.split(",")[0]);
			smsBuilder.setMessageStatusListPDU(MessageStatusList.valueOf(messageStatus));
		} else if (messageFormat.equals("TEXT_MODE")) {
			String messageStatus = params.split(",")[0];
			smsBuilder.setMessageStatusText(messageStatus);
		}
		if (params.split(",").length >= 2) {
			int mode = Integer.parseInt(params.split(",")[1]);
			smsBuilder.setMessageMode(MessageMode.valueOf(mode));
		}

	}

}
