package com.rtrk.atcommand.parser;

import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.DataConnectionMode;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FTPMessageType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TransferType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.TypeOfConfigurableParameters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SetParametersFTPParserTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public SetParametersFTPParserTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(SetParametersFTPParserTest.class);
	}

	public void testEncode() {
		// 1. branch
		Command.Builder commandBuilder = Command.newBuilder();

		FTPCommand.Builder ftpBuilder = FTPCommand.newBuilder();
		ftpBuilder.setMessageType(FTPMessageType.SET_PARAMETERS);
		ftpBuilder.setAction(Action.WRITE);
		ftpBuilder.setType(TypeOfConfigurableParameters.MODE_OF_DATA_CONNECTION);
		ftpBuilder.setDataConnectionMode(DataConnectionMode.ACTIVE_MODE);

		commandBuilder.setCommandType(CommandType.FTP_COMMAND);
		commandBuilder.setFtpCommand(ftpBuilder.build());

		Parser parser = new SetParametersFTPParser();
		byte[] encoded = parser.encode(commandBuilder.build());

		String commandString = new String(encoded);

		assertEquals("AT+QFTPCFG=1,0", commandString);

		// 2. branch
		commandBuilder = Command.newBuilder();

		ftpBuilder = FTPCommand.newBuilder();
		ftpBuilder.setMessageType(FTPMessageType.SET_PARAMETERS);
		ftpBuilder.setAction(Action.WRITE);
		ftpBuilder.setType(TypeOfConfigurableParameters.TRANSFER_TYPE);
		ftpBuilder.setTransferType(TransferType.ASCII_TRANSFER_TYPE);

		commandBuilder.setCommandType(CommandType.FTP_COMMAND);
		commandBuilder.setFtpCommand(ftpBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QFTPCFG=2,1", commandString);

		// 3. branch
		commandBuilder = Command.newBuilder();

		ftpBuilder = FTPCommand.newBuilder();
		ftpBuilder.setMessageType(FTPMessageType.SET_PARAMETERS);
		ftpBuilder.setAction(Action.WRITE);
		ftpBuilder.setType(TypeOfConfigurableParameters.RESUMING_POINT_TO_RESUME_FILE_TRANSFER);
		ftpBuilder.setResumingPoint(5);

		commandBuilder.setCommandType(CommandType.FTP_COMMAND);
		commandBuilder.setFtpCommand(ftpBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QFTPCFG=3,5", commandString);

		// 4. branch
		commandBuilder = Command.newBuilder();

		ftpBuilder = FTPCommand.newBuilder();
		ftpBuilder.setMessageType(FTPMessageType.SET_PARAMETERS);
		ftpBuilder.setAction(Action.WRITE);
		ftpBuilder.setType(TypeOfConfigurableParameters.LOCAL_POSITION_OF_FILE_TRANSFER);
		ftpBuilder.setLocalPosition("\"COM\"");

		commandBuilder.setCommandType(CommandType.FTP_COMMAND);
		commandBuilder.setFtpCommand(ftpBuilder.build());

		encoded = parser.encode(commandBuilder.build());

		commandString = new String(encoded);

		assertEquals("AT+QFTPCFG=4,\"COM\"", commandString);
	}

	public void testDecode() {
		// 1. branch
		String params = "1,1";

		FTPCommand.Builder commandBuilder = FTPCommand.newBuilder();

		Parser parser = new SetParametersFTPParser();
		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasType());
		assertEquals(1, commandBuilder.getType().getNumber());

		assertTrue(commandBuilder.hasDataConnectionMode());
		assertEquals(1, commandBuilder.getDataConnectionMode().getNumber());

		// 2. branch
		params = "2,1";

		commandBuilder = FTPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasType());
		assertEquals(2, commandBuilder.getType().getNumber());

		assertTrue(commandBuilder.hasTransferType());
		assertEquals(1, commandBuilder.getTransferType().getNumber());

		// 3. branch
		params = "3,10";

		commandBuilder = FTPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasType());
		assertEquals(3, commandBuilder.getType().getNumber());

		assertTrue(commandBuilder.hasResumingPoint());
		assertEquals(10, commandBuilder.getResumingPoint());

		// 4. branch
		params = "4,\"UFS\"";

		commandBuilder = FTPCommand.newBuilder();

		parser.decode(params.getBytes(), commandBuilder);

		assertTrue(commandBuilder.hasType());
		assertEquals(4, commandBuilder.getType().getNumber());

		assertTrue(commandBuilder.hasLocalPosition());
		assertEquals("\"UFS\"", commandBuilder.getLocalPosition());
	}

}
