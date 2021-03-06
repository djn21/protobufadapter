package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageStatusDelete;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

/**
 * 
 * Class for parsing DELETE_ALL_SMS command between original and protobuf format
 * 
 * @author djekanovic
 *
 */
public class DeleteAllSMSParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		String commandString = "AT+QMGDA=";
		SMSCommand smsCommand = command.getSmsCommand();
		if (smsCommand.hasMessageStatusDeletePDU()) {
			commandString += smsCommand.getMessageStatusDeletePDU().getNumber();
		} else if (smsCommand.hasMessageStatusText()) {
			commandString += smsCommand.getMessageStatusText();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String param = new String(commandByteArray);
		SMSCommand.Builder smsBuilder = (SMSCommand.Builder) commandBuilder;
		if (param.length() == 0) {
			throw new XMLParseException("Required parameter is missing");
		}
		if (param.split(",").length > 1) {
			throw new XMLParseException("Wrong number of parameters");
		}
		byte[] mode = ProtobufATCommandAdapter.environmentVariables
				.get("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat");
		if (mode != null && "TEXT_MODE".equals(new String(mode))) {
			smsBuilder.setMessageStatusText(param.trim());
		} else {
			int messageStatus = Integer.parseInt(param.trim());
			smsBuilder.setMessageStatusDeletePDU(MessageStatusDelete.valueOf(messageStatus));
		}
	}

}
