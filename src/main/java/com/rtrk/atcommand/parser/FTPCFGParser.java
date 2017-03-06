package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.DataConnectionMode;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TransferType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TypeOfConfigurableParameters;

public class FTPCFGParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		String commandString = "AT+QFTPCFG=";
		FTPCommand ftpCommand = command.getFtpCommand();
		int type = ftpCommand.getType().getNumber();
		commandString += type;
		if (ftpCommand.hasDataConnectionMode()) {
			commandString += "," + ftpCommand.getDataConnectionMode().getNumber();
		} else if (ftpCommand.hasTransferType()) {
			commandString += "," + ftpCommand.getTransferType().getNumber();
		} else if (ftpCommand.hasResumingPoint()) {
			commandString += "," + ftpCommand.getResumingPoint();
		} else if (ftpCommand.hasLocalPosition()) {
			commandString += "," + ftpCommand.getLocalPosition();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		if (params.length() == 0) {
			throw new XMLParseException("Required parameter missing");
		}
		String typeString = params.split(",")[0].trim();
		int type = Integer.parseInt(typeString);
		FTPCommand.Builder ftpCommandBuilder = (FTPCommand.Builder) commandBuilder;
		ftpCommandBuilder.setType(TypeOfConfigurableParameters.valueOf(type));
		if (params.split(",").length == 2) {
			String valueString = params.split(",")[1].trim();
			if (type == 1) {
				int dataConnectionMode = Integer.parseInt(valueString);
				ftpCommandBuilder.setDataConnectionMode(DataConnectionMode.valueOf(dataConnectionMode));
			} else if (type == 2) {
				int transferType = Integer.parseInt(valueString);
				ftpCommandBuilder.setTransferType(TransferType.valueOf(transferType));
			} else if (type == 3) {
				int resumingPoint = Integer.parseInt(valueString);
				ftpCommandBuilder.setResumingPoint(resumingPoint);
			} else if (type == 4) {
				String localPosition = valueString;
				ftpCommandBuilder.setLocalPosition(localPosition);
			}
		}
	}

}
