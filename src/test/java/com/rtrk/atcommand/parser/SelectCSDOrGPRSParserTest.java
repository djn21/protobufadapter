package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.BearerType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CSDRate;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for SelectCSDOrGPRSParser
 * 
 * @author djekanovic
 *
 */
public class SelectCSDOrGPRSParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public SelectCSDOrGPRSParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(SelectCSDOrGPRSParserTest.class);
	}

	
	/**
	 * 
	 * Testing encode method
	 * 
	 */
	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		TCPIPCommand.Builder tcpipBuilder = TCPIPCommand.newBuilder();
		tcpipBuilder.setMessageType(TCPIPMessageType.SELECT_CSD_OR_GPRS_AS_THE_BEARER);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setBearerType(BearerType.SET_GPRS_AS_BEARER_FOR_TCPIP_CONNECTION);

		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());

		Parser parser = new SelectCSDOrGPRSParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+QICSGP=1", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		tcpipBuilder = TCPIPCommand.newBuilder();
		tcpipBuilder.setMessageType(TCPIPMessageType.SELECT_CSD_OR_GPRS_AS_THE_BEARER);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setBearerType(BearerType.SET_CSD_AS_BEARER_FOR_TCPIP_CONNECTION);
		tcpipBuilder.setDialNumber("+38765123456");
		tcpipBuilder.setUsername("username");
		tcpipBuilder.setPassword("password");
		tcpipBuilder.setRate(CSDRate.CSD_RATE_9600);

		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QICSGP=0,+38765123456,username,password,2", commandString);

		// 3. branch
		commandBuilder = Command.newBuilder();

		tcpipBuilder = TCPIPCommand.newBuilder();
		tcpipBuilder.setMessageType(TCPIPMessageType.SELECT_CSD_OR_GPRS_AS_THE_BEARER);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setBearerType(BearerType.SET_GPRS_AS_BEARER_FOR_TCPIP_CONNECTION);
		tcpipBuilder.setAPN("accesspoint");
		tcpipBuilder.setUsername("username");
		tcpipBuilder.setPassword("password");

		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QICSGP=1,accesspoint,username,password", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params = "1";
		TCPIPCommand.Builder commandBuilder = TCPIPCommand.newBuilder();

		Parser parser = new SelectCSDOrGPRSParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasBearerType());
		assertEquals(1, commandBuilder.getBearerType().getNumber());

		// 2. branch
		params = "0,+38765123456,username,password,2";
		commandBuilder = TCPIPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasBearerType());
		assertEquals(0, commandBuilder.getBearerType().getNumber());

		assertTrue(commandBuilder.hasDialNumber());
		assertEquals("+38765123456", commandBuilder.getDialNumber());

		assertTrue(commandBuilder.hasUsername());
		assertEquals("username", commandBuilder.getUsername());

		assertTrue(commandBuilder.hasPassword());
		assertEquals("password", commandBuilder.getPassword());

		assertTrue(commandBuilder.hasRate());
		assertEquals(2, commandBuilder.getRate().getNumber());

		// 3. branch
		params = "1,accesspoint,username,password,3";
		commandBuilder = TCPIPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasBearerType());
		assertEquals(1, commandBuilder.getBearerType().getNumber());

		assertTrue(commandBuilder.hasAPN());
		assertEquals("accesspoint", commandBuilder.getAPN());

		assertTrue(commandBuilder.hasUsername());
		assertEquals("username", commandBuilder.getUsername());

		assertTrue(commandBuilder.hasPassword());
		assertEquals("password", commandBuilder.getPassword());
	}

}
