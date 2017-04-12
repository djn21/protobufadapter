package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageStatusList;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WriteSMSMessageParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public WriteSMSMessageParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(WriteSMSMessageParserTest.class);
	}

	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		SMSCommand.Builder smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.WRITE_SMS_MESSAGE_TO_MEMORY);
		smsBuilder.setAction(Action.WRITE);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		Parser parser = new WriteSMSMessageParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+CMGW", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.WRITE_SMS_MESSAGE_TO_MEMORY);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setDestinationAddress("destaddr");
		smsBuilder.setTypeOfDestinationAddress(1);
		smsBuilder.setMessageStatusText("\"ALL\"");

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+CMGW=destaddr,1,\"ALL\"", commandString);

		// 3. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.WRITE_SMS_MESSAGE_TO_MEMORY);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setLength(10);
		smsBuilder.setMessageStatusListPDU(MessageStatusList.STORED_UNSENT_MESSAGES);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+CMGW=10,2", commandString);
	}

	public void testDecode() {
		// 1. branch
		String params = "";

		SMSCommand.Builder commandBuilder = SMSCommand.newBuilder();

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"TEXT_MODE".getBytes());

		Parser parser = new WriteSMSMessageParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertFalse(commandBuilder.hasDestinationAddress());
		assertFalse(commandBuilder.hasLength());

		// 2. branch
		params = "destaddr,3,\"ALL\"";

		commandBuilder = SMSCommand.newBuilder();

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"TEXT_MODE".getBytes());

		parser = new WriteSMSMessageParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasDestinationAddress());
		assertEquals("destaddr", commandBuilder.getDestinationAddress());
		
		assertTrue(commandBuilder.hasTypeOfDestinationAddress());
		assertEquals(3, commandBuilder.getTypeOfDestinationAddress());
		
		assertTrue(commandBuilder.hasMessageStatusText());
		assertEquals("\"ALL\"", commandBuilder.getMessageStatusText());
		
		// 3. branch
		params = "5,3";

		commandBuilder = SMSCommand.newBuilder();

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"PDU_MODE".getBytes());

		parser = new WriteSMSMessageParser();
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasLength());
		assertEquals(5, commandBuilder.getLength());
		
		assertTrue(commandBuilder.hasMessageStatusListPDU());
		assertEquals(3, commandBuilder.getMessageStatusListPDU().getNumber());
	}

}
