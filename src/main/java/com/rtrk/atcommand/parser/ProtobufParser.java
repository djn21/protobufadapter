package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;

public interface ProtobufParser {

	byte[] encode(Command command);

	void decode(byte[] commandByteArray, Object commandBuilder);

}