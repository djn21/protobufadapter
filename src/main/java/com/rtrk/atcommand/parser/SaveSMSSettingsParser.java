package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

/**
 * 
 * Class for parsing SAVE_SMS_SETTINGS command between original and protobuf format
 * 
 * @author djekanovic
 *
 */
public class SaveSMSSettingsParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		SMSCommand smsCommand = command.getSmsCommand();
		String commandString = "AT+CSAS";
		if (smsCommand.hasProfile()) {
			commandString += "=" + smsCommand.getProfile();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		SMSCommand.Builder smsCommandBuilder = (SMSCommand.Builder) commandBuilder;
		if (!"".equals(params)) {
				int profile=Integer.parseInt(params);
				smsCommandBuilder.setProfile(profile);
		}
	}

}
