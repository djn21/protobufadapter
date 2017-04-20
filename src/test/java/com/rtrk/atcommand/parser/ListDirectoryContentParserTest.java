package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.parser.ListDirectoryContentParser;
import com.rtrk.atcommand.parser.Parser;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for ListDirectoryContentParser
 * 
 * @author djekanovic
 *
 */
public class ListDirectoryContentParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public ListDirectoryContentParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(ListDirectoryContentParserTest.class);
	}

	/**
	 * 
	 * Testing encode method
	 * 
	 */
	public void testEncode() {
		// 1. branch
		Command.Builder commandBulder = Command.newBuilder();

		FTPCommand.Builder ftpBulder = FTPCommand.newBuilder();
		ftpBulder.setAction(Action.WRITE);
		ftpBulder.setMessageType(FTPMessageType.LIST_CONTENTS_OF_DIRECTORY_OR_FILE);
		ftpBulder.setName("\"file.txt\"");

		commandBulder.setCommandType(CommandType.FTP_COMMAND);
		commandBulder.setFtpCommand(ftpBulder.build());

		Parser parser = new ListDirectoryContentParser();
		byte[] encoded = parser.encode(commandBulder.build());

		String commandString = new String(encoded);
		assertEquals("AT+QFTPLIST=\"file.txt\"", commandString);

		// 2. branch
		commandBulder = Command.newBuilder();

		ftpBulder = FTPCommand.newBuilder();
		ftpBulder.setAction(Action.WRITE);
		ftpBulder.setMessageType(FTPMessageType.LIST_CONTENTS_OF_DIRECTORY_OR_FILE);

		commandBulder.setCommandType(CommandType.FTP_COMMAND);
		commandBulder.setFtpCommand(ftpBulder.build());

		parser = new ListDirectoryContentParser();
		encoded = parser.encode(commandBulder.build());

		commandString = new String(encoded);
		assertEquals("AT+QFTPLIST", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params = "\"file.txt\"";
		FTPCommand.Builder commandBuilder = FTPCommand.newBuilder();

		Parser parser = new ListDirectoryContentParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasName());
		assertEquals("\"file.txt\"", commandBuilder.getName());

		// 2. branch
		params = "";
		commandBuilder = FTPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertFalse(commandBuilder.hasName());
	}

}
