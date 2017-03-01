package com.rtrk.adapter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.CaseFormat;
import com.rtrk.adapter.exception.XMLParseException;
import com.rtrk.atcommands.ATCommand.Command;
import com.rtrk.xml.Parameter;

/**
 * 
 * Utility class for AT command encoding and decoding. The class conatins static
 * methods encode and decode for converting between String and Protobuf AT
 * command format.
 * 
 * @author djekanovic
 *
 */

public class ProtobufAdapter {

	private static Map<String, com.rtrk.xml.Command> decodeMap = new HashMap<String, com.rtrk.xml.Command>();
	private static Map<String, Map<String, com.rtrk.xml.Command>> encodeMap = new HashMap<String, Map<String, com.rtrk.xml.Command>>();

	static {
		init();
	}

	public static void init() {
		File commandDescriptionFile = new File("commands.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		Document document;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(commandDescriptionFile);
			document.getDocumentElement().normalize();
			// cmds element
			Element cmdsElement = document.getDocumentElement();
			// cmd node list
			NodeList cmdNodeList = cmdsElement.getElementsByTagName("cmd");
			for (int i = 0; i < cmdNodeList.getLength(); i++) {
				// cmd element
				Element cmdElement = (Element) cmdNodeList.item(i);
				String cmdName = cmdElement.getAttribute("name");
				String cmdPrefix = cmdElement.getAttribute("prefix");
				String cmdSufix = cmdElement.getAttribute("sufix");
				String cmdDelimiter = cmdElement.getAttribute("delimiter");
				// type node list
				NodeList typeNodeList = cmdElement.getElementsByTagName("type");
				for (int j = 0; j < typeNodeList.getLength(); j++) {
					Map<String, com.rtrk.xml.Command> classMap = new HashMap<String, com.rtrk.xml.Command>();
					// type element
					Element typeElement = (Element) typeNodeList.item(j);
					String typeName = typeElement.getAttribute("name");
					String typePrefix = typeElement.getAttribute("prefix");
					// class node list
					NodeList classNodeList = typeElement.getElementsByTagName("class");
					for (int k = 0; k < classNodeList.getLength(); k++) {
						// class element
						Element classElement = (Element) classNodeList.item(k);
						String className = classElement.getAttribute("name");
						String classPrefix = classElement.getAttribute("prefix");
						// order node list
						Vector<Parameter> parameters = new Vector<Parameter>();
						if (classElement.hasChildNodes()) {
							NodeList orderNodeList = classElement.getElementsByTagName("order");
							for (int m = 0; m < orderNodeList.getLength(); m++) {
								// order element
								Element orderElement = (Element) orderNodeList.item(m);
								// param node list
								NodeList paramNodeList = orderElement.getElementsByTagName("param");
								for (int n = 0; n < paramNodeList.getLength(); n++) {
									Parameter parameter = new Parameter();
									// param element
									Element paramElement = (Element) paramNodeList.item(n);
									String paramName = paramElement.getAttribute("name");
									boolean paramOptional = Boolean.valueOf(paramElement.getAttribute("optional"));
									// set parameter attributes
									parameter.setName(paramName);
									parameter.setOptional(paramOptional);
									if (paramElement.hasChildNodes()) {
										Element minElement = (Element) paramElement.getElementsByTagName("min").item(0);
										Element maxElement = (Element) paramElement.getElementsByTagName("max").item(0);
										int min = Integer.valueOf(minElement.getTextContent());
										int max = Integer.valueOf(maxElement.getTextContent());
										// set parameter attributes
										parameter.setMin(min);
										parameter.setMax(max);
									}
									parameters.add(parameter);
								}
							}
						}
						String fullPrefix = cmdPrefix + typePrefix + classPrefix;
						com.rtrk.xml.Command command = new com.rtrk.xml.Command(cmdName, typeName, className,
								fullPrefix, cmdSufix, cmdDelimiter, parameters);
						decodeMap.put(command.getPrefix(), command);
						classMap.put(command.getClazz(), command);
					}
					encodeMap.put(typeName, classMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
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
		/*
		 * String commandString = ""; try { documentBuilder =
		 * documentBuilderFactory.newDocumentBuilder(); document =
		 * documentBuilder.parse(commandDescription);
		 * document.getDocumentElement().normalize(); // command Command command
		 * = Command.parseFrom(commandByteArray); Class<?> commandClass =
		 * command.getClass(); // cmd element Element cmdElement = (Element)
		 * document.getDocumentElement(); String cmdName =
		 * cmdElement.getAttribute("name"); String cmdPrefix =
		 * cmdElement.getAttribute("prefix"); String cmdSufix =
		 * cmdElement.getAttribute("sufix"); String cmdDelimiter =
		 * cmdElement.getAttribute("delimiter"); // type element Element
		 * typeElement = (Element)
		 * cmdElement.getElementsByTagName("type").item(0); String typePrefix =
		 * typeElement.getAttribute("prefix"); // class element Element
		 * classElement = (Element)
		 * typeElement.getElementsByTagName("class").item(0); String classPrefix
		 * = classElement.getAttribute("prefix"); // full prefix String prefix =
		 * cmdPrefix + typePrefix + classPrefix; // upper camel name String
		 * cmdNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL,
		 * cmdName); // command type object and class Object commandTypeObject =
		 * commandClass.getMethod("get" + cmdNameUpperCamel).invoke(command);
		 * Class<?> commandTypeClass = commandTypeObject.getClass(); // order
		 * element Element paramsElement = (Element)
		 * classElement.getElementsByTagName("order").item(0); NodeList params =
		 * paramsElement.getElementsByTagName("param"); commandString = prefix;
		 * for (int i = 0; i < params.getLength(); i++) { // param element
		 * Element param = (Element) params.item(i); // param attribures String
		 * paramName = param.getAttribute("name"); String paramNameUpperCamel =
		 * CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, paramName); // has
		 * parameter boolean hasParam = (Boolean)
		 * commandTypeClass.getMethod("has" + paramNameUpperCamel)
		 * .invoke(commandTypeObject); if (hasParam) { Class<?> paramClass =
		 * commandTypeClass.getMethod("get" +
		 * paramNameUpperCamel).getReturnType(); Object paramObject =
		 * commandTypeClass.getMethod("get" + paramNameUpperCamel)
		 * .invoke(commandTypeObject); String paramValue =
		 * paramObject.toString(); if (paramClass.equals(String.class)) {
		 * paramValue = "\"" + paramObject.toString() + "\""; } else if
		 * (paramClass.equals(boolean.class)) { paramValue = (boolean)
		 * paramObject ? "1" : "0"; } else if (paramClass.isPrimitive()) {
		 * paramValue = paramObject.toString(); } else { paramValue =
		 * paramClass.getMethod("getNumber").invoke(paramObject).toString(); }
		 * if (i == 0) commandString += paramValue; else commandString +=
		 * cmdDelimiter + paramValue; } } commandString += cmdSufix; } catch
		 * (Exception e) { e.printStackTrace(); } return
		 * commandString.getBytes();
		 */
		return null;
	}

	/**
	 * 
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
	public static byte[] decode(byte[] commandByteArray) {
		String commandString = new String(commandByteArray);
		// command builder and class
		Command.Builder commandBuilder = Command.newBuilder();
		Class<?> commandBuilderClass = commandBuilder.getClass();
		// command prefix
		String prefix = "";
		String regex = "AT\\+?[A-Z]*=?\\??";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(commandString);
		if (matcher.find()) {
			prefix = matcher.group(0);
		}
		// command with prefix
		com.rtrk.xml.Command command = decodeMap.get(prefix);
		if (command == null) {
			throw new XMLParseException("Can't find description for command with " + prefix + " prefix");
		}
		// name to upper camel
		String cmdNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, command.getName());
		String classNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, command.getClazz());
		try {
			// command type class, builder and builder class
			Class<?> commandTypeClass = commandBuilderClass.getMethod("get" + cmdNameUpperCamel).getReturnType();
			Object commandTypeBuilderObject = commandTypeClass.getMethod("newBuilder").invoke(commandTypeClass);
			Class<?> commandTypeBuilderClass = commandTypeBuilderObject.getClass();
			// set command type
			Class<?> commandMessageTypeClass = commandTypeBuilderClass.getMethod("getMessageType").getReturnType();
			Object commandMessageTypeObject = commandMessageTypeClass.getMethod("valueOf", String.class)
					.invoke(commandMessageTypeClass, command.getType());
			commandTypeBuilderClass.getMethod("setMessageType", commandMessageTypeClass)
					.invoke(commandTypeBuilderObject, commandMessageTypeObject);
			// set command class
			commandTypeBuilderClass.getMethod("set" + classNameUpperCamel, boolean.class)
					.invoke(commandTypeBuilderObject, true);
			// params length check
			int numOfParams = commandString.split(command.getDelimiter()).length;
			int maxNumOfParams = command.getParameters().size();
			// prefix regex
			String prefixRegex = prefix.replace("+", "\\+");
			if (numOfParams > maxNumOfParams) {
				throw new XMLParseException("Number of arguments must be less or equal to " + maxNumOfParams
						+ " [current " + numOfParams + "]");
			}
			for (int i = 0; i < command.getParameters().size(); i++) {
				Parameter parameter = command.getParameters().get(i);
				String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameter.getName());
				// parameters syntax check
				if (commandString.split(prefixRegex).length == 0 || i == commandString.split(command.getDelimiter()).length) {
					if (parameter.isOptional()) {
						break;
					} else {
						throw new XMLParseException("Required parameter " + parameter.getName() + " is missing");
					}
				}

				// param class
				Class<?> paramClass = commandTypeBuilderClass.getMethod("get" + paramNameUpperCamel).getReturnType();
				// param value
				String paramValue = commandString.split(command.getDelimiter())[i].trim();
				if (i == 0) {
					paramValue = paramValue.split(prefixRegex)[1].trim();
				}
				if (i + 1 == commandString.split(command.getDelimiter()).length && !"".equals(command.getSufix())) {
					paramValue = paramValue.split(command.getSufix())[0].trim();
				}
				if (paramClass.equals(String.class)) {
					paramValue = paramValue.substring(1, paramValue.length() - 1);
					commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
							.invoke(commandTypeBuilderObject, paramValue);
				} else if (paramClass.isPrimitive()) {
					Class<?> wrepperClass = getWrepperClass(paramClass);
					Object paramValueObject = null;
					if (boolean.class.equals(paramClass)) {
						paramValueObject = wrepperClass.getMethod("valueOf", boolean.class).invoke(wrepperClass,
								(paramValue.equals("1") ? true : false));
					} else {
						paramValueObject = wrepperClass.getMethod("valueOf", String.class).invoke(wrepperClass,
								paramValue);
					}
					double paramValueDouble = Double.valueOf(paramValueObject.toString());
					if (parameter.hasMin() && paramValueDouble < parameter.getMin()) {
						throw new XMLParseException("Parameter " + parameter.getName() + " must be greater then "
								+ (int) parameter.getMin());
					}
					if (parameter.hasMax() && paramValueDouble > parameter.getMax()) {
						throw new XMLParseException(
								"Parameter " + parameter.getName() + " must be less then " + (int) parameter.getMax());
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
			// set command type
			Object commandTypeObject = commandTypeBuilderClass.getMethod("build").invoke(commandTypeBuilderObject);
			commandBuilderClass.getMethod("set" + cmdNameUpperCamel, commandTypeClass).invoke(commandBuilder,
					commandTypeObject);
			// set command type enumeration
			Class<?> commandTypeEnumClass = commandBuilderClass.getMethod("getCommandType").getReturnType();
			String commandTypeEnum = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, command.getName());
			Object commandTypeEnumObject = commandTypeEnumClass.getMethod("valueOf", String.class)
					.invoke(commandTypeEnumClass, commandTypeEnum);
			commandBuilderClass.getMethod("setCommandType", commandTypeEnumClass).invoke(commandBuilder,
					commandTypeEnumObject);
		} catch (NoSuchMethodException | IllegalAccessException | java.lang.IllegalArgumentException
				| InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
		return commandBuilder.build().toByteArray();
	}

	/**
	 * 
	 * Get wrepper class from primitive class
	 * 
	 * @param primitiveClass
	 *            primitive class type
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
