package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.OperateFunction;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.OperateWriteMMS;

public class WriteMMSMessageParser implements Parser {

	@Override
	public byte[] encode(Command command) {
		String commandString = "AT+QMMSW=";
		MMSCommand mmsCommand = command.getMmsCommand();
		commandString += mmsCommand.getOperateFunction().getNumber();
		if (mmsCommand.hasOperateWriteMMS()) {
			commandString += "," + mmsCommand.getOperateWriteMMS().getNumber();
		}
		if (mmsCommand.hasOpstring()) {
			commandString += "," + mmsCommand.getOpstring();
		}
		return commandString.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String params = new String(commandByteArray);
		MMSCommand.Builder mmsCommandBuilder = (MMSCommand.Builder) commandBuilder;
		if (!"".contentEquals(params)) {
			int function = Integer.parseInt(params.split(",")[0].trim());
			if (function == 4) {
				if (params.split(",").length == 1) {
					throw new XMLParseException("Required parameter operateFunction is misssing");
				} else {
					int operate = Integer.parseInt(params.split(",")[1].trim());
					mmsCommandBuilder.setOperateWriteMMS(OperateWriteMMS.valueOf(operate));
				}
			} else {
				if (params.split(",").length >= 2) {
					int operate = Integer.parseInt(params.split(",")[1].trim());
					mmsCommandBuilder.setOperateWriteMMS(OperateWriteMMS.valueOf(operate));
				}
				if (params.split(",").length >= 3) {
					String opstring = params.split(",")[2].trim();
					mmsCommandBuilder.setOpstring(opstring);
				}
			}
			mmsCommandBuilder.setOperateFunction(OperateFunction.valueOf(function));
		} else {
			throw new XMLParseException("Required parameter is misssing");
		}
	}

}
