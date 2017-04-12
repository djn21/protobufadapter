package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SendSMSMessageParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public SendSMSMessageParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(SendSMSMessageParserTest.class);
	}

	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		SMSCommand.Builder smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.SEND_SMS_MESSAGE);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setLength(123);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		Parser parser = new SendSMSMessageParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+CMGS=123", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.SEND_SMS_MESSAGE);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setDestinationAddress("destaddr");
		smsBuilder.setTypeOfDestinationAddress(1);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+CMGS=destaddr,1", commandString);
	}

	public void testDecode() {
		// 1. branch
		String params = "1";

		SMSCommand.Builder commandBuilder = SMSCommand.newBuilder();

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"PDU_MODE".getBytes());

		Parser parser = new SendSMSMessageParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasLength());
		assertEquals(1, commandBuilder.getLength());

		// 2. branch
		params = "destaddr,5";

		commandBuilder = SMSCommand.newBuilder();

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"TEXT_MODE".getBytes());

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasDestinationAddress());
		assertEquals("destaddr", commandBuilder.getDestinationAddress());

		assertTrue(commandBuilder.hasTypeOfDestinationAddress());
		assertEquals(5, commandBuilder.getTypeOfDestinationAddress());
	}

}
