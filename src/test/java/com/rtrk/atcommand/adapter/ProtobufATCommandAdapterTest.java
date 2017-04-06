package com.rtrk.atcommand.adapter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Action;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.CommandType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.FileCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.GeneralCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.GeneralMessageType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.HTTPCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.HTTPMessageType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MMSCommand;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.MessageFormat;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.ProtocolType;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.SMSCommand;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * Unit test for ProtobufATCommandAdapter
 * 
 * @author djekanovic
 *
 */
public class ProtobufATCommandAdapterTest extends TestCase {

	/**
	 * 
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 * 
	 */
	public ProtobufATCommandAdapterTest(String testName) {
		super(testName);
	}

	/**
	 * 
	 * @return the suite of tests being tested
	 * 
	 */
	public static Test suite() {
		return new TestSuite(ProtobufATCommandAdapterTest.class);
	}

	public void testEncode() {
		// 1. branch (without parameters)
		Command.Builder command = Command.newBuilder();
		command.setCommandType(CommandType.HTTP_COMMAND);

		HTTPCommand.Builder httpCommand = HTTPCommand.newBuilder();
		httpCommand.setMessageType(HTTPMessageType.SET_HTTP_SERVER_URL);
		httpCommand.setAction(Action.TEST);

		command.setHttpCommand(httpCommand.build());
		Command protocommand = command.build();

		byte[] encoded = ProtobufATCommandAdapter.encode(protocommand.toByteArray());
		assertNotNull(encoded);

		String textcommand = new String(encoded);
		assertEquals("AT+QHTTPURL=?", textcommand);

		// 2. branch (with parameters, number of parameters is 0)
		command = Command.newBuilder();
		command.setCommandType(CommandType.GENERAL_COMMAND);

		GeneralCommand.Builder generalCommand = GeneralCommand.newBuilder();
		generalCommand.setMessageType(GeneralMessageType.SET_ALL_PARAMETERS_TO_MANUFACTURER_DEFAULTS);
		generalCommand.setAction(Action.EXECUTION);

		command.setGeneralCommand(generalCommand.build());
		protocommand = command.build();

		encoded = ProtobufATCommandAdapter.encode(protocommand.toByteArray());
		assertNotNull(encoded);

		textcommand = new String(encoded);
		assertEquals("AT&F", textcommand);

		// 3. branch (with parameters, number of parameters is 2)
		command = Command.newBuilder();
		command.setCommandType(CommandType.HTTP_COMMAND);

		httpCommand = HTTPCommand.newBuilder();
		httpCommand.setMessageType(HTTPMessageType.SET_HTTP_SERVER_URL);
		httpCommand.setAction(Action.WRITE);
		httpCommand.setURLLength(100);
		httpCommand.setInputTime(200);

		command.setHttpCommand(httpCommand.build());
		protocommand = command.build();

		encoded = ProtobufATCommandAdapter.encode(protocommand.toByteArray());
		assertNotNull(encoded);

		textcommand = new String(encoded);
		assertEquals("AT+QHTTPURL=100,200", textcommand);
	}

	public void testDecode() {
		try {
			// 1. branch
			String command = "AT+QMMPROXY=1,\"255.255.255.255\",100";

			byte[] protobyte = ProtobufATCommandAdapter.decode(command.getBytes());
			assertNotNull(protobyte);

			Command protocommand = Command.parseFrom(protobyte);
			assertTrue(protocommand.hasMmsCommand());
			assertEquals(CommandType.MMS_COMMAND, protocommand.getCommandType());

			MMSCommand mmsCommand = protocommand.getMmsCommand();
			assertTrue(mmsCommand.hasProtocolType());
			assertTrue(mmsCommand.hasGateway());
			assertTrue(mmsCommand.hasPort());

			ProtocolType protocolType = mmsCommand.getProtocolType();
			String gateway = mmsCommand.getGateway();
			int port = mmsCommand.getPort();

			assertEquals(ProtocolType.HHTP_PROTOCOL, protocolType);
			assertEquals("\"255.255.255.255\"", gateway);
			assertEquals(100, port);

			// 2. branch
			command = "AT+QFMOV=srcfilename,dstfilename,1,1";
			protobyte=ProtobufATCommandAdapter.decode(command.getBytes());
			assertNotNull(protobyte);
			
			protocommand=Command.parseFrom(protobyte);
			assertTrue(protocommand.hasFileCommand());
			assertEquals(CommandType.FILE_COMMAND, protocommand.getCommandType());
			
			FileCommand fileCommand=protocommand.getFileCommand();
			assertTrue(fileCommand.hasSourceFileName());
			assertTrue(fileCommand.hasDestinationFileName());
			assertTrue(fileCommand.hasCopy());
			assertTrue(fileCommand.hasOwerwrite());
			
			String srcfilename=fileCommand.getSourceFileName();
			String dstfilename=fileCommand.getDestinationFileName();
			boolean copy=fileCommand.getCopy();
			boolean overwrite=fileCommand.getOwerwrite();
			
			assertEquals("srcfilename", srcfilename);
			assertEquals("dstfilename", dstfilename);
			assertTrue(copy);
			assertTrue(overwrite);
			
			// 3. branch
			command="AT+CMGF=1";
			protobyte=ProtobufATCommandAdapter.decode(command.getBytes());
			assertNotNull(protocolType);
			
			protocommand=Command.parseFrom(protobyte);
			assertTrue(protocommand.hasSmsCommand());
			assertEquals(CommandType.SMS_COMMAND, protocommand.getCommandType());
			
			SMSCommand smsCommand=protocommand.getSmsCommand();
			assertTrue(smsCommand.hasMessageFormat());
			
			MessageFormat mf=smsCommand.getMessageFormat();
			
			assertEquals(MessageFormat.TEXT_MODE, mf);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

}
