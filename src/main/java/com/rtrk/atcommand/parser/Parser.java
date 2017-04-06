package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;

/**
 * 
 * The interface contains methods for encoding and decoding AT Command from text
 * format to protobuf format.
 * 
 * @author djekanovic
 *
 */

public interface Parser {

	/**
	 * 
	 * Translate AT Command from Protobuf format using a specific description of
	 * command. The method uses XML file which contains description of specific
	 * AT command.
	 * 
	 * @param command
	 *            AT command in Protobuf format
	 * 
	 * @return AT command in String format as byte array
	 * 
	 */
	byte[] encode(Command command);

	/**
	 * 
	 * Translate AT Command parameters to Protobuf format using a specific
	 * description of command. The method uses XML file which contains
	 * description of specific AT command.
	 * 
	 * @param params
	 *            Parameters of AT command in String format which receives as
	 *            byte array
	 * 
	 * @param commandBuilder
	 *            Builder of specific AT Command
	 * 
	 */
	void decode(byte[] params, Object commandBuilder);

}