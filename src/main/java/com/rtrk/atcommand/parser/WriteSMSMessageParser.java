package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageStatusList;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

/**
 * 
 * Class for parsing WRITE_SMS_MESSAGE_TO_MEMORY command between original and protobuf format
 * 
 * @author djekanovic
 *
 */
public class WriteSMSMessageParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		SMSCommand smsCommand = command.getSmsCommand();
		String commandString = "AT+CMGW";
		if (smsCommand.hasDestinationAddress()) {
			commandString += "=" + smsCommand.getDestinationAddress();
		}
		if (smsCommand.hasTypeOfDestinationAddress()) {
			commandString += "," + smsCommand.getTypeOfDestinationAddress();
		}
		if (smsCommand.hasLength()) {
			commandString += "=" + smsCommand.getLength();
		}
		if (smsCommand.hasMessageStatusListPDU()) {
			commandString += "," + smsCommand.getMessageStatusListPDU().getNumber();
		}
		if (smsCommand.hasMessageStatusText()) {
			commandString += "," + smsCommand.getMessageStatusText();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		SMSCommand.Builder smsCommandBuilder = (SMSCommand.Builder) commandBuilder;
		byte[] mode = ProtobufATCommandAdapter.environmentVariables
				.get("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat");
		if (mode != null && "TEXT_MODE".equals(new String(mode))) {
			if (!params.equals("")) {
				String destinationAddress = params.split(",")[0].trim();
				smsCommandBuilder.setDestinationAddress(destinationAddress);
			}
			if (params.split(",").length >= 2) {
				int typeOfDestinationAddress = Integer.parseInt(params.split(",")[1].trim());
				smsCommandBuilder.setTypeOfDestinationAddress(typeOfDestinationAddress);
			}
			if (params.split(",").length >= 3) {
				String messageStatus = params.split(",")[2].trim();
				smsCommandBuilder.setMessageStatusText(messageStatus);
			}
			if (params.split(",").length > 3) {
				throw new XMLParseException("Wrong number of parameters");
			}
		} else {
			if (params.length() == 0) {
				throw new XMLParseException("Required parameter lenght is missing");
			}
			int length = Integer.parseInt(params.split(",")[0].trim());
			smsCommandBuilder.setLength(length);
			if (params.split(",").length == 2) {
				int messageStatus = Integer.parseInt(params.split(",")[1].trim());
				smsCommandBuilder.setMessageStatusListPDU(MessageStatusList.valueOf(messageStatus));
			}
			if (params.split(",").length > 2) {
				throw new XMLParseException("Wrong number of parameters");
			}
		}
	}

}
