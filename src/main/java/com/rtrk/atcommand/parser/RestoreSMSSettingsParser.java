package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

public class RestoreSMSSettingsParser implements ProtobufParser {

	@Override
	public byte[] encode(Command command) {
		SMSCommand smsCommand = command.getSmsCommand();
		String commandString = "AT+CRES";
		if (smsCommand.hasProfile()) {
			commandString += "=" + smsCommand.getProfile();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		SMSCommand.Builder smsCommandBuilder = (SMSCommand.Builder) commandBuilder;
		if (!params.equals("")) {
			int profile = Integer.parseInt(params.trim());
			smsCommandBuilder.setProfile(profile);
		}
	}

}
