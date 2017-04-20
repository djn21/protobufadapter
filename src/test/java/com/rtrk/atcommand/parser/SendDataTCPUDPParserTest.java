package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.adapter.ProtobufATCommandAdapter;
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
 * Unit test for SendDataTCPUDPParser
 * 
 * @author djekanovic
 *
 */
public class SendDataTCPUDPParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public SendDataTCPUDPParserTest(String testName) {
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
		Command.Builder commandBuilder=Command.newBuilder();
		
		TCPIPCommand.Builder tcpipBuilder=TCPIPCommand.newBuilder();
		tcpipBuilder.setMessageType(TCPIPMessageType.SEND_DATA_THROUGH_TCP_OR_UDP_CONNECTION);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setLength(1);
		
		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());
		
		Parser parser=new SendDataTCPUDPParser();
		byte[] encoded=parser.encode(commandBuilder.build());
		
		String commandString=new String(encoded);
		
		assertEquals("AT+QISEND=1", commandString);
		
		// 2. branch
		commandBuilder=Command.newBuilder();
		
		tcpipBuilder=TCPIPCommand.newBuilder();
		tcpipBuilder.setMessageType(TCPIPMessageType.SEND_DATA_THROUGH_TCP_OR_UDP_CONNECTION);
		tcpipBuilder.setAction(Action.WRITE);
		tcpipBuilder.setLength(1);
		tcpipBuilder.setIndex(2);
		
		commandBuilder.setCommandType(CommandType.TCPIP_COMMAND);
		commandBuilder.setTcpipCommand(tcpipBuilder.build());
		
		encoded=parser.encode(commandBuilder.build());
		
		commandString=new String(encoded);
		
		assertEquals("AT+QISEND=2,1", commandString);
	}

	/**
	 * 
	 * Testing decode method
	 * 
	 */
	public void testDecode() {
		// 1. branch
		String params="2";
		TCPIPCommand.Builder commandBuilder=TCPIPCommand.newBuilder();
		
		ProtobufATCommandAdapter.environmentVariables.put("tcpipCommand.ENABLE_MULTIPLE_TCPIP_SESSION.enableMultipleTCPIPSession", "false".getBytes());
		
		Parser parser=new SendDataTCPUDPParser();
		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasLength());
		assertEquals(2, commandBuilder.getLength());
		
		// 2. branch
		params="3";
		commandBuilder=TCPIPCommand.newBuilder();
		
		ProtobufATCommandAdapter.environmentVariables.put("tcpipCommand.ENABLE_MULTIPLE_TCPIP_SESSION.enableMultipleTCPIPSession", "true".getBytes());

		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasIndex());
		assertEquals(3, commandBuilder.getIndex());
		
		// 3. branch
		params="4,5";
		commandBuilder=TCPIPCommand.newBuilder();
		
		ProtobufATCommandAdapter.environmentVariables.put("tcpipCommand.ENABLE_MULTIPLE_TCPIP_SESSION.enableMultipleTCPIPSession", "true".getBytes());

		parser.decode(params.getBytes(), commandBuilder);
		
		assertTrue(commandBuilder.hasIndex());
		assertEquals(4, commandBuilder.getIndex());
		assertTrue(commandBuilder.hasLength());
		assertEquals(5, commandBuilder.getLength());
	}

}
