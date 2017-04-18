package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SaveSMSSettingsParserTest extends TestCase{

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public SaveSMSSettingsParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(SaveSMSSettingsParserTest.class);
	}

	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		SMSCommand.Builder smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.SAVE_SMS_SETTINGS);
		smsBuilder.setAction(Action.WRITE);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		Parser parser = new SaveSMSSettingsParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+CSAS", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.SAVE_SMS_SETTINGS);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setProfile(2);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+CSAS=2", commandString);
	}

	public void testDecode() {
		// 1. branch
		String params = "";
		SMSCommand.Builder commandBuilder = SMSCommand.newBuilder();

		Parser parser = new SaveSMSSettingsParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertFalse(commandBuilder.hasProfile());

		// 2. branch
		params = "1";
		commandBuilder = SMSCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasProfile());
		assertEquals(1, commandBuilder.getProfile());
	}
	
}
