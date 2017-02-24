package com.rtrk.adapter;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.CaseFormat;

import com.rtrk.adapter.exception.CommandPrefixException;
import com.rtrk.adapter.exception.IllegalArgumentException;
import com.rtrk.adapter.exception.ParameterValueOutOfBoundException;
import com.rtrk.adapter.exception.RequiredParameterMissingException;

import com.rtrk.atcommands.ATCommand.Command;

/**
 * 
 * @author djekanovic
 * 
 *         Utility class for AT command encoding and decoding. The class
 *         conatins static methods for converting between String and Protobuf AT
 *         command format.
 *
 */

public class ProtobufAdapter {

	/**
	 * Translate from Protobuf format to String format using a specific
	 * description of message. The method uses XML file which contains
	 * description of specific AT command.
	 * 
	 * @param commandByteArray
	 *            AT command in Protobuf format which receives as byte array
	 * 
	 * @param commandDescription
	 *            XML file which contains description of AT command
	 * 
	 * @return AT command in String format as byte array
	 * 
	 */
	public static byte[] encode(byte[] commandByteArray, File commandDescription) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		Document document;
		String commandString = "";
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(commandDescription);
			document.getDocumentElement().normalize();
			// command
			Command command = Command.parseFrom(commandByteArray);
			Class<?> commandClass = command.getClass();
			// cmd element
			Element cmdElement = (Element) document.getDocumentElement();
			String cmdName = cmdElement.getAttribute("name");
			String cmdPrefix = cmdElement.getAttribute("prefix");
			String cmdSufix = cmdElement.getAttribute("sufix");
			String cmdDelimiter = cmdElement.getAttribute("delimiter");
			// type element
			Element typeElement = (Element) cmdElement.getElementsByTagName("type").item(0);
			String typePrefix = typeElement.getAttribute("prefix");
			// class element
			Element classElement = (Element) typeElement.getElementsByTagName("class").item(0);
			String classPrefix = classElement.getAttribute("prefix");
			// full prefix
			String prefix = cmdPrefix + typePrefix + classPrefix;
			// upper camel name
			String cmdNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, cmdName);
			// command type object and class
			Object commandTypeObject = commandClass.getMethod("get" + cmdNameUpperCamel).invoke(command);
			Class<?> commandTypeClass = commandTypeObject.getClass();
			// order element
			Element paramsElement = (Element) classElement.getElementsByTagName("order").item(0);
			NodeList params = paramsElement.getElementsByTagName("param");
			commandString = prefix;
			for (int i = 0; i < params.getLength(); i++) {
				// param element
				Element param = (Element) params.item(i);
				// param attribures
				String paramName = param.getAttribute("name");
				String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, paramName);
				// has parameter
				boolean hasParam = (Boolean) commandTypeClass.getMethod("has" + paramNameUpperCamel)
						.invoke(commandTypeObject);
				if (hasParam) {
					Object paramObject = commandTypeClass.getMethod("get" + paramNameUpperCamel)
							.invoke(commandTypeObject);
					Class<?> paramClass = paramObject.getClass();
					String paramValue = paramObject.toString();
					if (paramClass.equals(String.class)) {
						paramValue = "\"" + paramObject.toString() + "\"";
					} else if (paramClass.isPrimitive()) {
						paramValue = paramObject.toString();
					} else {
						paramValue = paramClass.getMethod("getNumber").invoke(paramObject).toString();
					}
					if (i == 0)
						commandString += paramValue;
					else
						commandString += cmdDelimiter + paramValue;
				}
			}
			commandString += cmdSufix;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return commandString.getBytes();
	}

	/**
	 * Translate from String format to Protobuf format using a specific
	 * description of message. The method uses XML file which contains
	 * description of specific AT command.
	 * 
	 * @param commandByteArray
	 *            AT command in String format which receives as byte array
	 * 
	 * @param commandDescription
	 *            XML file which contains description of AT command
	 * 
	 * @return AT command in Protobuf format as byte array
	 * 
	 */
	public static byte[] decode(byte[] commandByteArray, File commandDescription) {
		String commandString = new String(commandByteArray);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		Document document;
		Command.Builder commandBuilder = Command.newBuilder();
		Class<?> commandBuilderClass = commandBuilder.getClass();
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(commandDescription);
			document.getDocumentElement().normalize();
			// cmd element
			Element cmdElement = (Element) document.getDocumentElement();
			String cmdName = cmdElement.getAttribute("name");
			String cmdPrefix = cmdElement.getAttribute("prefix");
			String cmdSufix = cmdElement.getAttribute("sufix");
			String cmdDelimiter = cmdElement.getAttribute("delimiter");
			// type element
			Element typeElement = (Element) cmdElement.getElementsByTagName("type").item(0);
			String typeName = typeElement.getAttribute("name");
			String typePrefix = typeElement.getAttribute("prefix");
			// class element
			Element classElement = (Element) typeElement.getElementsByTagName("class").item(0);
			String className = classElement.getAttribute("name");
			String classPrefix = classElement.getAttribute("prefix");
			// full prefix
			String prefix = cmdPrefix + typePrefix + classPrefix;
			// regex
			String prefixRegex = prefix.replace("+", "\\+").replace("?", "\\?").replace(".", "\\.").replace("^", "\\^")
					.replace("$", "\\$").replace("|", "\\|").replace("*", "\\*");
			String sufixRegex = cmdSufix.replace("+", "\\+").replace("?", "\\?").replace(".", "\\.").replace("^", "\\^")
					.replace("$", "\\$").replace("|", "\\|").replace("*", "\\*");
			// command prefix check
			if (!commandString.startsWith(prefix)) {
				throw new CommandPrefixException("Command " + commandString + " must have " + prefix + " prefix");
			}
			// name to upper camel
			String cmdNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, cmdName);
			String classNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, className);
			// command type class, builder and builder class
			Class<?> commandTypeClass = commandBuilderClass.getMethod("get" + cmdNameUpperCamel).getReturnType();
			Object commandTypeBuilderObject = commandTypeClass.getMethod("newBuilder").invoke(commandTypeClass);
			Class<?> commandTypeBuilderClass = commandTypeBuilderObject.getClass();
			// set command type
			Class<?> commandMessageTypeClass = commandTypeBuilderClass.getMethod("getMessageType").getReturnType();
			Object commandMessageTypeObject = commandMessageTypeClass.getMethod("valueOf", String.class)
					.invoke(commandMessageTypeClass, typeName);
			commandTypeBuilderClass.getMethod("setMessageType", commandMessageTypeClass)
					.invoke(commandTypeBuilderObject, commandMessageTypeObject);
			// set command class
			commandTypeBuilderClass.getMethod("set" + classNameUpperCamel, boolean.class)
					.invoke(commandTypeBuilderObject, true);
			// order element
			Element orderElement = (Element) classElement.getElementsByTagName("order").item(0);
			NodeList params = orderElement.getElementsByTagName("param");
			// params length check
			if (params.getLength() < commandString.split(cmdDelimiter).length && params.getLength() != 0) {
				throw new IllegalArgumentException("Number of arguments must be less or equal to " + params.getLength()
						+ " [current " + commandString.split(cmdDelimiter).length + "]");
			}
			for (int i = 0; i < params.getLength(); i++) {
				// param element
				Element param = (Element) params.item(i);
				String paramName = param.getAttribute("name");
				String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, paramName);
				boolean paramOptional = Boolean.parseBoolean(param.getAttribute("optional"));
				// parameters syntax check
				if (commandString.split(prefixRegex).length == 0 || commandString.split(cmdDelimiter).length == i) {
					if (paramOptional) {
						break;
					} else {
						throw new RequiredParameterMissingException("Required parameter " + paramName + " is missing");
					}
				}
				// param class
				Class<?> paramClass = commandTypeBuilderClass.getMethod("get" + paramNameUpperCamel).getReturnType();
				// param value
				String paramValue = commandString.split(cmdDelimiter)[i].trim();
				if (commandString.split(cmdDelimiter).length == i + 1 && !"".equals(cmdSufix)) {
					paramValue = paramValue.split(sufixRegex)[0];
				}
				if (i == 0) {
					paramValue = paramValue.split(prefixRegex)[1];
				}
				if (paramClass.equals(String.class)) {
					paramValue = paramValue.substring(1, paramValue.length() - 1);
					commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
							.invoke(commandTypeBuilderObject, paramValue);
				} else if (paramClass.isPrimitive()) {
					Class<?> wrepperClass = getWrepperClass(paramClass);
					Object paramValueObject = wrepperClass.getMethod("valueOf", String.class).invoke(wrepperClass,
							paramValue);
					if (param.hasChildNodes()) {
						Element min = (Element) param.getElementsByTagName("min").item(0);
						Element max = (Element) param.getElementsByTagName("max").item(0);
						double minDouble = Double.valueOf(min.getTextContent());
						double maxDouble = Double.valueOf(max.getTextContent());
						double paramValueDouble = Double.valueOf(paramValueObject.toString());
						if (paramValueDouble < minDouble || maxDouble < paramValueDouble) {
							throw new ParameterValueOutOfBoundException("Parameter " + paramName + " must be between "
									+ min.getTextContent() + " and " + max.getTextContent());
						}
					}
					commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
							.invoke(commandTypeBuilderObject, paramValueObject);
				} else {
					int paramValueInt = Integer.parseInt(paramValue);
					Object paramValueObject = paramClass.getMethod("valueOf", int.class).invoke(paramClass,
							paramValueInt);
					commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
							.invoke(commandTypeBuilderObject, paramValueObject);
				}

			}
			// add command sufix
			commandString += cmdSufix;
			// set command type
			Object commandTypeObject = commandTypeBuilderClass.getMethod("build").invoke(commandTypeBuilderObject);
			commandBuilderClass.getMethod("set" + cmdNameUpperCamel, commandTypeClass).invoke(commandBuilder,
					commandTypeObject);
			// set command type enumeration
			Class<?> commandTypeEnumClass = commandBuilderClass.getMethod("getCommandType").getReturnType();
			String commandTypeEnum = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, cmdName);
			Object commandTypeEnumObject = commandTypeEnumClass.getMethod("valueOf", String.class)
					.invoke(commandTypeEnumClass, commandTypeEnum);
			commandBuilderClass.getMethod("setCommandType", commandTypeEnumClass).invoke(commandBuilder,
					commandTypeEnumObject);
			Command command = commandBuilder.build();
			return command.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	/**
	 * Get wrepper class from primitive class
	 * 
	 * @param primitiveClass
	 *            - primitive class type
	 * 
	 * @return wrepper class type
	 * 
	 */
	private static Class<?> getWrepperClass(Class<?> primitiveClass) {
		HashMap<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
		map.put(byte.class, Byte.class);
		map.put(short.class, Short.class);
		map.put(int.class, Integer.class);
		map.put(long.class, Long.class);
		map.put(float.class, Float.class);
		map.put(double.class, Double.class);
		map.put(char.class, Character.class);
		map.put(boolean.class, Boolean.class);
		return map.get(primitiveClass);
	}

}
