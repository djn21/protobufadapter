package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

public class CMGDIndexParser implements Parser{

	@Override
	public byte[] encode(Command command) {
		int index=command.getSmsCommand().getIndex();
		String result=(index/10)+"'"+(index%10)+"\"";
		return result.getBytes();
	}

	@Override
	public void decode(byte[] commandByteArray, Object commandBuilder) {
		String parameterString=new String(commandByteArray);
		String minutes=parameterString.split("'")[0].trim();
		String seconds=parameterString.split("'")[1].split("\"")[0].trim();
		SMSCommand.Builder smsCommandBuilder=(SMSCommand.Builder)commandBuilder;
		smsCommandBuilder.setIndex(Integer.parseInt(minutes+seconds));
	}

}
