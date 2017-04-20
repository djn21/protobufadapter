package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.parser.ListSMSMessagesParser;
import com.rtrk.atcommand.parser.Parser;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageMode;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageStatusList;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for ListSMSMessagesParser
 * 
 * @author djekanovic
 *
 */
public class ListSMSMessagesParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public ListSMSMessagesParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(ListSMSMessagesParserTest.class);
	}

	/**
	 * 
	 * Testing encode method
	 * 
	 */
	public void testEncode() {
		// 1. branch
		Command.Builder commandBulder = Command.newBuilder();

		SMSCommand.Builder smsCommand = SMSCommand.newBuilder();
		smsCommand.setAction(Action.WRITE);
		smsCommand.setMessageType(SMSMessageType.LIST_SMS_MESSAGE_FROM_PREFERRED_STORAGE);
		smsCommand.setMessageStatusListPDU(MessageStatusList.ALL_MESSAGES);
		smsCommand.setMessageMode(MessageMode.NORMAL);

		commandBulder.setCommandType(CommandType.SMS_COMMAND);
		commandBulder.setSmsCommand(smsCommand.build());

		Parser parser = new ListSMSMessagesParser();
		byte[] encoded = parser.encode(commandBulder.build());

		String commandString = new String(encoded);
		assertEquals("AT+CMGL=4,0", commandString);

		// 2. branch
		commandBulder = Command.newBuilder();

		smsCommand = SMSCommand.newBuilder();
		smsCommand.setAction(Action.WRITE);
		smsCommand.setMessageType(SMSMessageType.LIST_SMS_MESSAGE_FROM_PREFERRED_STORAGE);
		smsCommand.setMessageStatusText("\"ALL\"");
		smsCommand.setMessageMode(MessageMode.NORMAL);

		commandBulder.setCommandType(CommandType.SMS_COMMAND);
		commandBulder.setSmsCommand(smsCommand.build());

		encoded = parser.encode(commandBulder.build());

		commandString = new String(encoded);
		assertEquals("AT+CMGL=\"ALL\",0", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params = "0";
		SMSCommand.Builder commandBuilder = SMSCommand.newBuilder();

		Parser parser = new ListSMSMessagesParser();
		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"PDU_MODE".getBytes());
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasMessageStatusListPDU());
		assertEquals(0, commandBuilder.getMessageStatusListPDU().getNumber());

		// 2. branch
		params = "\"ALL\"";
		commandBuilder = SMSCommand.newBuilder();

		parser = new ListSMSMessagesParser();
		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"TEXT_MODE".getBytes());
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasMessageStatusText());
		assertEquals("\"ALL\"", commandBuilder.getMessageStatusText());
	}

}
