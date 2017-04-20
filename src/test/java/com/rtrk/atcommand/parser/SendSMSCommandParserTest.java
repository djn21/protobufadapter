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

/**
 * 
 * Unit test for SendSMSCommandParser
 * 
 * @author djekanovic
 *
 */
public class SendSMSCommandParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public SendSMSCommandParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(SendDataTCPUDPParserTest.class);
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
		smsBuilder.setMessageType(SMSMessageType.SEND_SMS_COMMAND);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setLength(123);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		Parser parser = new SendSMSCommandParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+CMGC=123", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		smsBuilder = SMSCommand.newBuilder();
		smsBuilder.setMessageType(SMSMessageType.SEND_SMS_COMMAND);
		smsBuilder.setAction(Action.WRITE);
		smsBuilder.setFirstOctet(1);
		smsBuilder.setCommandType(2);
		smsBuilder.setProtocolIdentifier(3);
		smsBuilder.setMessageNumber(4);
		smsBuilder.setDestinationAddress("destaddr");
		smsBuilder.setTypeOfDestinationAddress(5);

		commandBuilder.setCommandType(CommandType.SMS_COMMAND);
		commandBuilder.setSmsCommand(smsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+CMGC=1,2,3,4,destaddr,5", commandString);
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

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"PDU_MODE".getBytes());

		Parser parser = new SendSMSCommandParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasLength());
		assertEquals(1, commandBuilder.getLength());
		
		// 2. branch
		params = "1,2,3,4,destaddr,5";

		commandBuilder = SMSCommand.newBuilder();

		ProtobufATCommandAdapter.environmentVariables.put("smsCommand.SELECT_SMS_MESSAGE_FORMAT.messageFormat",
				"TEXT_MODE".getBytes());

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasFirstOctet());
		assertEquals(1, commandBuilder.getFirstOctet());
		
		assertTrue(commandBuilder.hasCommandType());
		assertEquals(2, commandBuilder.getCommandType());
		
		assertTrue(commandBuilder.hasProtocolIdentifier());
		assertEquals(3, commandBuilder.getProtocolIdentifier());
		
		assertTrue(commandBuilder.hasMessageNumber());
		assertEquals(4, commandBuilder.getMessageNumber());
		
		assertTrue(commandBuilder.hasDestinationAddress());
		assertEquals("destaddr", commandBuilder.getDestinationAddress());
		
		assertTrue(commandBuilder.hasTypeOfDestinationAddress());
		assertEquals(5, commandBuilder.getTypeOfDestinationAddress());
	}

}
