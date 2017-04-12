package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MMSMessageType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.OperateFunction;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.OperateWriteMMS;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WriteMMSMessageParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public WriteMMSMessageParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(WriteMMSMessageParserTest.class);
	}

	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		MMSCommand.Builder mmsBuilder = MMSCommand.newBuilder();
		mmsBuilder.setMessageType(MMSMessageType.WRITE_MMS_MESSAGE);
		mmsBuilder.setAction(Action.WRITE);
		mmsBuilder.setOperateFunction(OperateFunction.OPERATE_TITLE);
		mmsBuilder.setOperateWriteMMS(OperateWriteMMS.WRITE_OPERATE);

		commandBuilder.setCommandType(CommandType.MMS_COMMAND);
		commandBuilder.setMmsCommand(mmsBuilder.build());

		Parser parser = new WriteMMSMessageParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+QMMSW=4,1", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		mmsBuilder = MMSCommand.newBuilder();
		mmsBuilder.setMessageType(MMSMessageType.WRITE_MMS_MESSAGE);
		mmsBuilder.setAction(Action.WRITE);
		mmsBuilder.setOperateFunction(OperateFunction.OPERATE_CC_ADDRESS);
		mmsBuilder.setOperateWriteMMS(OperateWriteMMS.CLEAN_OPERATE);
		mmsBuilder.setOpstring("opstring");

		commandBuilder.setCommandType(CommandType.MMS_COMMAND);
		commandBuilder.setMmsCommand(mmsBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QMMSW=2,0,opstring", commandString);
	}

	public void testDecode() {
		// 1. branch
		String params = "3";

		MMSCommand.Builder commandBuilder = MMSCommand.newBuilder();

		Parser parser = new WriteMMSMessageParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasOperateFunction());
		assertEquals(3, commandBuilder.getOperateFunction().getNumber());

		// 2. branch
		params = "4,1";

		commandBuilder = MMSCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasOperateFunction());
		assertEquals(4, commandBuilder.getOperateFunction().getNumber());

		assertTrue(commandBuilder.hasOperateWriteMMS());
		assertEquals(1, commandBuilder.getOperateWriteMMS().getNumber());

		// 3. branch
		params = "2,0,opstring";

		commandBuilder = MMSCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasOperateFunction());
		assertEquals(2, commandBuilder.getOperateFunction().getNumber());

		assertTrue(commandBuilder.hasOperateWriteMMS());
		assertEquals(0, commandBuilder.getOperateWriteMMS().getNumber());
		
		assertTrue(commandBuilder.hasOpstring());
		assertEquals("opstring", commandBuilder.getOpstring());
	}

}
