package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CallRelatedCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CallRelatedMessageType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for MobileOriginatedCallParser
 * 
 * @author djekanovic
 *
 */
public class MobileOriginatedCallParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public MobileOriginatedCallParserTest(String testName) {
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
		Command.Builder commandBuilder = Command.newBuilder();

		CallRelatedCommand.Builder callBuilder = CallRelatedCommand.newBuilder();
		callBuilder.setMessageType(CallRelatedMessageType.MOBILE_ORIGINATED_CALL_TO_DIAL_A_NUMBER);
		callBuilder.setAction(Action.EXECUTION);
		callBuilder.setNumber("+38765123456");

		commandBuilder.setCommandType(CommandType.CALL_RELATED_COMMAND);
		commandBuilder.setCallRelatedCommand(callBuilder.build());

		Parser parser = new MobileOriginatedCallParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);
		assertEquals("ATD+38765123456", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		callBuilder = CallRelatedCommand.newBuilder();
		callBuilder.setMessageType(CallRelatedMessageType.MOBILE_ORIGINATED_CALL_TO_DIAL_A_NUMBER);
		callBuilder.setAction(Action.EXECUTION);
		callBuilder.setNumber("+38765123456");
		callBuilder.setGSMModifier("g");

		commandBuilder.setCommandType(CommandType.CALL_RELATED_COMMAND);
		commandBuilder.setCallRelatedCommand(callBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);
		assertEquals("ATD+38765123456g", commandString);

		// 3. branch
		commandBuilder = Command.newBuilder();

		callBuilder = CallRelatedCommand.newBuilder();
		callBuilder.setMessageType(CallRelatedMessageType.MOBILE_ORIGINATED_CALL_TO_DIAL_A_NUMBER);
		callBuilder.setAction(Action.EXECUTION);
		callBuilder.setNumber("+38765123456");
		callBuilder.setGSMModifier("g");
		callBuilder.setDelimiter(true);

		commandBuilder.setCommandType(CommandType.CALL_RELATED_COMMAND);
		commandBuilder.setCallRelatedCommand(callBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);
		assertEquals("ATD+38765123456g;", commandString);
	}

	
	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params = "+38765123456";
		CallRelatedCommand.Builder commandBuilder = CallRelatedCommand.newBuilder();
		
		Parser parser=new MobileOriginatedCallParser();
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasNumber());
		assertEquals("+38765123456", commandBuilder.getNumber());
		
		// 2. branch
		params = "+38765123456i";
		commandBuilder = CallRelatedCommand.newBuilder();
		
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasNumber());
		assertEquals("+38765123456", commandBuilder.getNumber());
		
		assertTrue(commandBuilder.hasGSMModifier());
		assertEquals("i", commandBuilder.getGSMModifier());
		
		// 3. branch
		params = "+38765123456i;";
		commandBuilder = CallRelatedCommand.newBuilder();
		
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasNumber());
		assertEquals("+38765123456", commandBuilder.getNumber());
		
		assertTrue(commandBuilder.hasGSMModifier());
		assertEquals("i", commandBuilder.getGSMModifier());
		
		assertTrue(commandBuilder.hasDelimiter());
		assertTrue(commandBuilder.getDelimiter());
	}

}
