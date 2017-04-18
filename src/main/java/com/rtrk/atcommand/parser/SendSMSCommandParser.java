package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

/**
 * 
 * Class for parsing SEND_SMS_COMMAND command between original and protobuf format
 * 
 * @author djekanovic
 *
 */
public class SendSMSCommandParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		String commandString = "AT+CMGC=";
		SMSCommand smsCommand = command.getSmsCommand();
		if (smsCommand.hasFirstOctet()) {
			commandString += smsCommand.getFirstOctet();
		}
		if (smsCommand.hasCommandType()) {
			commandString += "," + smsCommand.getCommandType();
		}
		if (smsCommand.hasProtocolIdentifier()) {
			commandString += "," + smsCommand.getProtocolIdentifier();
		}
		if (smsCommand.hasMessageNumber()) {
			commandString += "," + smsCommand.getMessageNumber();
		}
		if (smsCommand.hasDestinationAddress()) {
			commandString += "," + smsCommand.getDestinationAddress();
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
		SMSCommand.Builder smsCommandBuilder = (SMSCommand.Builder) commandBuilder;
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter length is missing");
		}
		byte[] mode = ProtobufATCommandAdapter.environmentVariables
				.get("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat");
		if (mode != null && "TEXT_MODE".equals(new String(mode))) {
			int fo = Integer.parseInt(params.split(",")[0]);
			smsCommandBuilder.setFirstOctet(fo);
			if (params.split(",").length >= 2) {
				int ct = Integer.parseInt(params.split(",")[1]);
				smsCommandBuilder.setCommandType(ct);
			}
			if (params.split(",").length >= 3) {
				int pid = Integer.parseInt(params.split(",")[2]);
				smsCommandBuilder.setProtocolIdentifier(pid);
			}
			if (params.split(",").length >= 4) {
				int mn = Integer.parseInt(params.split(",")[3]);
				smsCommandBuilder.setMessageNumber(mn);
			}
			if (params.split(",").length >= 5) {
				String da = params.split(",")[4];
				smsCommandBuilder.setDestinationAddress(da);
			}
			if (params.split(",").length >= 6) {
				int toda = Integer.parseInt(params.split(",")[5]);
				smsCommandBuilder.setTypeOfDestinationAddress(toda);
			}
			if (params.split(",").length > 6) {
				throw new XMLParseException("Wrong number of parameters");
			}
		} else {
			if (params.split(",").length > 1) {
				throw new XMLParseException("Wrong number of parameters");
			} else {
				int length = Integer.parseInt(params.trim());
				smsCommandBuilder.setLength(length);
			}
		}

	}

}
