package com.rtrk.atcommand.adapter;

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
import com.google.protobuf.InvalidProtocolBufferException;
import com.rtrk.atcommand.ATCommand;
import com.rtrk.atcommand.Parameter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;

/**
 * 
 * Utility class for AT command encoding and decoding. The class conatins static
 * methods encode and decode for converting between String and Protobuf AT
 * command format.
 * 
 * @author djekanovic
 *
 */

public class ProtobufATCommandAdapter {

	private static Map<String, ATCommand> decodeMap = new HashMap<String, ATCommand>();
	private static Map<String, Map<String, ATCommand>> encodeMap = new HashMap<String, Map<String, ATCommand>>();

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
					Map<String, ATCommand> classMap = new HashMap<String, ATCommand>();
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
										Element trueElement = (Element) paramElement.getElementsByTagName("true")
												.item(0);
										Element falseElement = (Element) paramElement.getElementsByTagName("false")
												.item(0);
										if (minElement != null) {
											int minValue = Integer.valueOf(minElement.getTextContent());
											parameter.setMinValue(minValue);
										}
										if (maxElement != null) {
											int maxValue = Integer.valueOf(maxElement.getTextContent());
											parameter.setMaxValue(maxValue);
										}
										if (trueElement != null) {
											int trueValue = Integer.valueOf(trueElement.getTextContent());
											parameter.setTrueValue(trueValue);
										}
										if (falseElement != null) {
											int falseValue = Integer.valueOf(falseElement.getTextContent());
											parameter.setFalseValue(falseValue);
										}
									}
									parameters.add(parameter);
								}
							}
						}
						String fullPrefix = cmdPrefix + typePrefix + classPrefix;
						ATCommand command = new ATCommand(cmdName, typeName, className, fullPrefix, cmdSufix,
								cmdDelimiter, parameters);
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
	 * @return AT command in String format as byte array
	 * 
	 */
	public static byte[] encode(byte[] commandByteArray) {
		String commandString = "";
		try {
			// command and command class
			Command command = Command.parseFrom(commandByteArray);
			Class<?> commandClass = command.getClass();
			String commandTypeUpperCamle = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
					command.getCommandType().toString());
			// message and message class
			Class<?> messageClass = commandClass.getMethod("get" + commandTypeUpperCamle).getReturnType();
			Object message = commandClass.getMethod("get" + commandTypeUpperCamle).invoke(command);
			// message type
			String messageType = messageClass.getMethod("getMessageType").invoke(message).toString();
			// message action
			String messageAction = messageClass.getMethod("getAction").invoke(message).toString();
			ATCommand atCommand = encodeMap.get(messageType).get(messageAction);
			// set prefix
			commandString += atCommand.getPrefix();
			// set parameters
			Vector<Parameter> parameters = atCommand.getParameters();
			for (int i = 0; i < parameters.size(); i++) {
				Parameter parameter = parameters.get(i);
				String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameter.getName());
				boolean hasParemeter = (boolean) messageClass.getMethod("has" + paramNameUpperCamel).invoke(message);
				if (hasParemeter) {
					Class<?> paramClass = messageClass.getMethod("get" + paramNameUpperCamel).getReturnType();
					String paramValue = messageClass.getMethod("get" + paramNameUpperCamel).invoke(message).toString();
					if (paramClass.equals(String.class)) {
						paramValue = "\"" + paramValue + "\"";
					} else if (paramClass.equals(boolean.class)) {
						paramValue = paramValue.equals("true") ? parameter.getTrueValue() + ""
								: parameter.getFalseValue() + "";
					} else if (!paramClass.isPrimitive()) {
						Object paramObject = messageClass.getMethod("get" + paramNameUpperCamel).invoke(message);
						paramValue = paramClass.getMethod("getNumber").invoke(paramObject).toString();
					}
					// set parameter
					if (i == 0) {
						commandString += paramValue;
					} else {
						commandString += atCommand.getDelimiter() + paramValue;
					}
					if (i == parameters.size() - 1) {
						commandString += atCommand.getSufix();
					}
				}
			}
		} catch (InvalidProtocolBufferException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return commandString.getBytes();
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
		ATCommand atCommand = decodeMap.get(prefix);
		if (atCommand == null) {
			throw new XMLParseException("Can't find description for command with " + prefix + " prefix");
		}
		// name to upper camel
		String cmdNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, atCommand.getName());
		try {
			// command type class, builder and builder class
			Class<?> commandTypeClass = commandBuilderClass.getMethod("get" + cmdNameUpperCamel).getReturnType();
			Object commandTypeBuilderObject = commandTypeClass.getMethod("newBuilder").invoke(commandTypeClass);
			Class<?> commandTypeBuilderClass = commandTypeBuilderObject.getClass();
			// set command type
			Class<?> commandMessageTypeClass = commandTypeBuilderClass.getMethod("getMessageType").getReturnType();
			Object commandMessageTypeObject = commandMessageTypeClass.getMethod("valueOf", String.class)
					.invoke(commandMessageTypeClass, atCommand.getType());
			commandTypeBuilderClass.getMethod("setMessageType", commandMessageTypeClass)
					.invoke(commandTypeBuilderObject, commandMessageTypeObject);
			// set command action
			Class<?> actionClass = commandTypeBuilderClass.getMethod("getAction").getReturnType();
			Object actionObject = actionClass.getMethod("valueOf", String.class).invoke(actionClass,
					atCommand.getClazz());
			commandTypeBuilderClass.getMethod("setAction", actionClass).invoke(commandTypeBuilderObject, actionObject);
			// params length check
			int numOfParams = 0;
			if (commandString.contains(atCommand.getDelimiter())) {
				numOfParams = commandString.split(atCommand.getDelimiter()).length;
			}
			int maxNumOfParams = atCommand.getParameters().size();
			// prefix regex
			String prefixRegex = prefix.replace("+", "\\+");
			if (numOfParams > maxNumOfParams) {
				throw new XMLParseException("Number of arguments must be less or equal to " + maxNumOfParams
						+ " [current " + numOfParams + "]");
			}
			for (int i = 0; i < atCommand.getParameters().size(); i++) {
				Parameter parameter = atCommand.getParameters().get(i);
				String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameter.getName());
				// parameters syntax check
				if (commandString.split(prefixRegex).length == 0
						|| i == commandString.split(atCommand.getDelimiter()).length) {
					if (parameter.isOptional()) {
						break;
					} else {
						throw new XMLParseException("Required parameter " + parameter.getName() + " is missing");
					}
				}

				// param class
				Class<?> paramClass = commandTypeBuilderClass.getMethod("get" + paramNameUpperCamel).getReturnType();
				// param value
				String paramValue = commandString.split(atCommand.getDelimiter())[i].trim();
				if (i == 0) {
					paramValue = paramValue.split(prefixRegex)[1].trim();
				}
				if (i + 1 == commandString.split(atCommand.getDelimiter()).length && !"".equals(atCommand.getSufix())) {
					paramValue = paramValue.split(atCommand.getSufix())[0].trim();
				}
				if (paramClass.equals(String.class)) {
					paramValue = paramValue.substring(1, paramValue.length() - 1);
					commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
							.invoke(commandTypeBuilderObject, paramValue);
				} else if (paramClass.isPrimitive()) {
					Class<?> wrepperClass = getWrepperClass(paramClass);
					Object paramValueObject = null;
					if (boolean.class.equals(paramClass)) {
						boolean paramValueBoolean = false;
						if (parameter.hasTrueValue() && paramValue.equals(parameter.getTrueValue() + "")) {
							paramValueBoolean = true;
						} else if (parameter.hasFalseValue() && paramValue.equals(parameter.getFalseValue() + "")) {
							paramValueBoolean = false;
						} else {
							throw new XMLParseException(
									"Incorrect value for " + parameter.getName() + " parameter [expected true="
											+ parameter.getTrueValue() + ", false=" + parameter.getFalseValue() + "]");
						}
						paramValueObject = wrepperClass.getMethod("valueOf", boolean.class).invoke(wrepperClass,
								paramValueBoolean);
					} else {
						paramValueObject = wrepperClass.getMethod("valueOf", String.class).invoke(wrepperClass,
								paramValue);
						double paramValueDouble = Double.valueOf(paramValueObject.toString());
						if (parameter.hasMinValue() && paramValueDouble < parameter.getMinValue()) {
							throw new XMLParseException("Parameter " + parameter.getName() + " must be greater then "
									+ (int) parameter.getMinValue());
						}
						if (parameter.hasMaxValue() && paramValueDouble > parameter.getMaxValue()) {
							throw new XMLParseException("Parameter " + parameter.getName() + " must be less then "
									+ (int) parameter.getMaxValue());
						}
					}
					commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
							.invoke(commandTypeBuilderObject, paramValueObject);
				} else {
					int paramValueInt = Integer.parseInt(paramValue);
					Object paramValueObject = paramClass.getMethod("valueOf", int.class).invoke(paramClass,
							paramValueInt);
					if (paramValueObject != null) {
						commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
								.invoke(commandTypeBuilderObject, paramValueObject);
					} else {
						throw new XMLParseException("Incorrect value ["+paramValueInt+"] for "+parameter.getName());
					}
				}
			}
			// set command type
			Object commandTypeObject = commandTypeBuilderClass.getMethod("build").invoke(commandTypeBuilderObject);
			commandBuilderClass.getMethod("set" + cmdNameUpperCamel, commandTypeClass).invoke(commandBuilder,
					commandTypeObject);
			// set command type enumeration
			Class<?> commandTypeEnumClass = commandBuilderClass.getMethod("getCommandType").getReturnType();
			String commandTypeEnum = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, atCommand.getName());
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
