package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for RestoreSMSSettingsParser
 * 
 * @author djekanovic
 *
 */
public class RestoreSMSSettingsParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public RestoreSMSSettingsParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(RestoreSMSSettingsParserTest.class);
	}

	/**
	 * 
	 * Testing encode method
	 * 
	 */
	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		SMSCommand.Builder smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.RESTORE_SMS_SETTINGS);
		smsBuilder.setAction(Action.WRITE);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		Parser parser = new RestoreSMSSettingsParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+CRES", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.RESTORE_SMS_SETTINGS);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setProfile(1);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+CRES=1", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params = "";
		SMSCommand.Builder commandBuilder = SMSCommand.newBuilder();

		Parser parser = new RestoreSMSSettingsParser();
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
