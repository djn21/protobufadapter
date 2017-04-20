package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TCPIPMessageType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for StartConnectionParser
 * 
 * @author djekanovic
 *
 */
public class StartConnectionParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public StartConnectionParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(StartConnectionParserTest.class);
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
		tcpipBuilder.setMessageType(TCPIPMessageType.SET_UP_TCP_OR_UDP_CONNECTION);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setMode("\"TCP\"");
		tcpipBuilder.setDomainName("domainname");
		tcpipBuilder.setPort(80);

		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());

		Parser parser = new StartConnectionParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+QIOPEN=\"TCP\",domainname,80", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		tcpipBuilder = TCPIPCommand.newBuilder();
		tcpipBuilder.setMessageType(TCPIPMessageType.SET_UP_TCP_OR_UDP_CONNECTION);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setIndex(1);
		tcpipBuilder.setMode("\"TCP\"");
		tcpipBuilder.setDomainName("domainname");
		tcpipBuilder.setPort(80);

		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QIOPEN=1,\"TCP\",domainname,80", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params="\"TCP\",domainname,80";
		
		TCPIPCommand.Builder commandBuilder=TCPIPCommand.newBuilder();
		
		Parser parser=new StartConnectionParser();
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasMode());
		assertEquals("\"TCP\"", commandBuilder.getMode());
		
		assertTrue(commandBuilder.hasDomainName());
		assertEquals("domainname", commandBuilder.getDomainName());
		
		assertTrue(commandBuilder.hasPort());
		assertEquals(80, commandBuilder.getPort());
		
		// 2. branch
		params="1,\"TCP\",domainname,80";
		
		commandBuilder=TCPIPCommand.newBuilder();
		
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasIndex());
		assertEquals(1, commandBuilder.getIndex());
		
		assertTrue(commandBuilder.hasMode());
		assertEquals("\"TCP\"", commandBuilder.getMode());
		
		assertTrue(commandBuilder.hasDomainName());
		assertEquals("domainname", commandBuilder.getDomainName());
		
		assertTrue(commandBuilder.hasPort());
		assertEquals(80, commandBuilder.getPort());
	}

}
