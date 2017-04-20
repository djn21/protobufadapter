package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
import com.rtrk.atcommand.parser.DeleteAllSMSParser;
import com.rtrk.atcommand.parser.Parser;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageStatusDelete;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for DeleteAllSMSParser
 * 
 * @author djekanovic
 *
 */
public class DeleteAllSMSParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public DeleteAllSMSParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(DeleteAllSMSParserTest.class);
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
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setMessageType(SMSMessageType.DELETE_ALL_SMS);
		smsBuilder.setMessageStatusDeletePDU(MessageStatusDelete.DELETE_ALL_SMS_MESSAGE);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		Parser parser = new DeleteAllSMSParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+QMGDA=6", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setMessageType(SMSMessageType.DELETE_ALL_SMS);
		smsBuilder.setMessageStatusText("\"DEL ALL\"");

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		parser = new DeleteAllSMSParser();
		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QMGDA=\"DEL ALL\"", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params = "1";
		SMSCommand.Builder commandBuilder = SMSCommand.newBuilder();

		Parser parser = new DeleteAllSMSParser();
		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"PDU_MODE".getBytes());
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasMessageStatusDeletePDU());
		assertEquals(1, commandBuilder.getMessageStatusDeletePDU().getNumber());

		// 2. branch
		params = "\"DEL ALL\"";
		commandBuilder = SMSCommand.newBuilder();

		parser = new DeleteAllSMSParser();
		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"TEXT_MODE".getBytes());
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasMessageStatusText());
		assertEquals(commandBuilder.getMessageStatusText(), "\"DEL ALL\"");
	}

}
