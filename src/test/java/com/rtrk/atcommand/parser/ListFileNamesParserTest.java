package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.parser.ListFileNamesParser;
import com.rtrk.atcommand.parser.Parser;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ListFileNamesParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public ListFileNamesParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(ListFileNamesParserTest.class);
	}

	public void testEncode() {
		// 1. branch
		Command.Builder commandBulder = Command.newBuilder();

		FTPCommand.Builder ftpBulder = FTPCommand.newBuilder();
		ftpBulder.setAction(Action.WRITE);
		ftpBulder.setMessageType(FTPMessageType.LIST_FILE_NAMES);
		ftpBulder.setDirectoryName("\"folder\"");

		commandBulder.setCommandType(CommandType.FTP_COMMAND);
		commandBulder.setFtpCommand(ftpBulder.build());

		Parser parser = new ListFileNamesParser();
		byte[] encoded = parser.encode(commandBulder.build());

		String commandString = new String(encoded);
		assertEquals("AT+QFTPNLST=\"folder\"", commandString);

		// 2. branch
		commandBulder = Command.newBuilder();

		ftpBulder = FTPCommand.newBuilder();
		ftpBulder.setAction(Action.WRITE);
		ftpBulder.setMessageType(FTPMessageType.LIST_FILE_NAMES);

		commandBulder.setCommandType(CommandType.FTP_COMMAND);
		commandBulder.setFtpCommand(ftpBulder.build());

		encoded = parser.encode(commandBulder.build());

		commandString = new String(encoded);
		assertEquals("AT+QFTPNLST", commandString);
	}

	public void testDecode() {
		// 1. branch
		String params = "\"folder\"";
		FTPCommand.Builder commandBuilder = FTPCommand.newBuilder();

		Parser parser = new ListFileNamesParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasDirectoryName());
		assertEquals("\"folder\"", commandBuilder.getDirectoryName());

		// 2. branch
		params = "";
		commandBuilder = FTPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertFalse(commandBuilder.hasName());
	}

}
