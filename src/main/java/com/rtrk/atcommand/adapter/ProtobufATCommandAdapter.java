package com.rtrk.atcommand.adapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.CaseFormat;
import com.google.protobuf.InvalidProtocolBufferException;

import com.rtrk.atcommand.ATCommand;
import com.rtrk.atcommand.Parameter;
import com.rtrk.atcommand.exception.XMLParseException;
import com.rtrk.atcommand.parser.ProtobufParser;
import com.rtrk.atcommand.protobuf.ProtobufATCommand.Command;

/**
 * 
 * Utility class for AT command encoding and decoding. The class conatins static
 * methods encode and decode for converting between AT Command and Protobuf AT
 * Command format.
 * 
 * @author djekanovic
 *
 */

public class ProtobufATCommandAdapter {

	public static Map<String, byte[]> environmentVariables = new HashMap<String, byte[]>();
	public static Map<Class<?>, Class<?>> wrepperTypes = new HashMap<Class<?>, Class<?>>();

	public static Map<String, ATCommand> decodeMap = new HashMap<String, ATCommand>();
	public static Map<String, Map<String, Map<String, ATCommand>>> encodeMap = new HashMap<String, Map<String, Map<String, ATCommand>>>();

	public static String regexp;
	
	static {
		// fill wrepper types map
		wrepperTypes.put(byte.class, Byte.class);
		wrepperTypes.put(short.class, Short.class);
		wrepperTypes.put(int.class, Integer.class);
		wrepperTypes.put(long.class, Long.class);
		wrepperTypes.put(float.class, Float.class);
		wrepperTypes.put(double.class, Double.class);
		wrepperTypes.put(char.class, Character.class);
		wrepperTypes.put(boolean.class, Boolean.class);

		init();
	}

	public static void init() {

		File commandDescriptionFile = new File("C:/Users/djekanovic/Documents/EclipseProjects/protobufadapter/resources/commands.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		Document document;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(commandDescriptionFile);
			document.getDocumentElement().normalize();

			// cmds element
			Element cmdsElement = document.getDocumentElement();
			regexp = cmdsElement.getAttribute("regex");

			// cmd node list
			NodeList cmdNodeList = cmdsElement.getElementsByTagName("cmd");
			for (int i = 0; i < cmdNodeList.getLength(); i++) {
				// cmd element
				Element cmdElement = (Element) cmdNodeList.item(i);
				String cmdName = cmdElement.getAttribute("name").trim();
				String cmdPrefix = cmdElement.getAttribute("prefix").trim();
				String cmdSufix = cmdElement.getAttribute("sufix").trim();
				String cmdDelimiter = cmdElement.getAttribute("delimiter").trim();

				// type node list
				NodeList typeNodeList = cmdElement.getElementsByTagName("type");
				Map<String, Map<String, ATCommand>> typeMap = new HashMap<String, Map<String, ATCommand>>();
				for (int j = 0; j < typeNodeList.getLength(); j++) {
					Map<String, ATCommand> classMap = new HashMap<String, ATCommand>();

					// type element
					Element typeElement = (Element) typeNodeList.item(j);
					String typeName = typeElement.getAttribute("name").trim();
					String typePrefix = typeElement.getAttribute("prefix").trim();

					// class node list
					NodeList classNodeList = typeElement.getElementsByTagName("class");
					for (int k = 0; k < classNodeList.getLength(); k++) {
						// class element
						Element classElement = (Element) classNodeList.item(k);
						String className = classElement.getAttribute("name").trim();
						String classPrefix = classElement.getAttribute("prefix").trim();
						String classOptional = classElement.getAttribute("optional").trim();
						String classParser = classElement.getAttribute("parser").trim();

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
									String paramName = paramElement.getAttribute("name").trim();
									String paramOptional = paramElement.getAttribute("optional").trim();
									String paramParser = paramElement.getAttribute("parser").trim();
									String paramEnvironment = paramElement.getAttribute("environment").trim();
									boolean optional = false;
									if (paramOptional.equals("true")) {
										optional = true;
									}
									boolean environment = false;
									if (paramEnvironment.equals("true")) {
										environment = true;
									}

									// set parameter attributes
									parameter.setName(paramName);
									parameter.setOptional(optional);
									parameter.setParser(paramParser);
									parameter.setEnvironment(environment);

									Element trueElement = null;
									Element falseElement = null;
									if (paramElement.hasChildNodes()) {

										// set min and max values
										Element minElement = (Element) paramElement.getElementsByTagName("min").item(0);
										Element maxElement = (Element) paramElement.getElementsByTagName("max").item(0);
										if (minElement != null) {
											double minValue = Double.valueOf(minElement.getTextContent().trim());
											parameter.setMinValue(minValue);
										}
										if (maxElement != null) {
											double maxValue = Double.valueOf(maxElement.getTextContent().trim());
											parameter.setMaxValue(maxValue);
										}

										// set true and false values
										trueElement = (Element) paramElement.getElementsByTagName("true").item(0);
										falseElement = (Element) paramElement.getElementsByTagName("false").item(0);
										if (trueElement != null) {
											int trueValue = Integer.valueOf(trueElement.getTextContent().trim());
											parameter.setTrueValue(trueValue);
										}
										if (falseElement != null) {
											int falseValue = Integer.valueOf(falseElement.getTextContent().trim());
											parameter.setFalseValue(falseValue);
										}

										// set paramter pattern
										Element patternElement = (Element) paramElement.getElementsByTagName("pattern")
												.item(0);
										if (patternElement != null) {
											String pattern = patternElement.getTextContent().trim();
											parameter.setPattern(pattern);
										}
									}

									// set default true and false values
									if (trueElement == null) {
										parameter.setTrueValue(1);
									}
									if (falseElement == null) {
										parameter.setFalseValue(0);
									}

									parameters.add(parameter);
								}
							}
						}

						// set full prefix for command where = is optional
						boolean optional = false;
						if (!"".equals(classOptional)) {
							optional = Boolean.parseBoolean(classOptional);
						}
						if (optional) {
							String fullPrefix = cmdPrefix + typePrefix;
							ATCommand command = new ATCommand(cmdName, typeName, className, fullPrefix, cmdSufix,
									cmdDelimiter, classParser, parameters);
							decodeMap.put(command.getPrefix(), command);
						}

						// set full prefix
						String fullPrefix = cmdPrefix + typePrefix + classPrefix;
						ATCommand command = new ATCommand(cmdName, typeName, className, fullPrefix, cmdSufix,
								cmdDelimiter, classParser, parameters);
						decodeMap.put(command.getPrefix(), command);
						classMap.put(command.getClazz(), command);
					}
					typeMap.put(typeName, classMap);
				}
				encodeMap.put(cmdName, typeMap);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Translate AT Command from Protobuf format using a specific description of
	 * message. The method uses XML file which contains description of specific
	 * AT command.
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
			String commandTypeUpperCamel = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
					command.getCommandType().toString());

			String commandTypeLowerCamel = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,
					command.getCommandType().toString());

			// message and message class
			Class<?> messageClass = commandClass.getMethod("get" + commandTypeUpperCamel).getReturnType();
			Object message = commandClass.getMethod("get" + commandTypeUpperCamel).invoke(command);

			// message type
			String messageType = messageClass.getMethod("getMessageType").invoke(message).toString();

			// message action
			String messageAction = messageClass.getMethod("getAction").invoke(message).toString();
			ATCommand atCommand = encodeMap.get(commandTypeLowerCamel).get(messageType).get(messageAction);

			// set prefix
			commandString += atCommand.getPrefix();

			// parser encode
			if (atCommand.hasParser()) {
				Class<?> parserClass = Class.forName(atCommand.getParser());
				ProtobufParser parser = (ProtobufParser) parserClass.newInstance();
				return parser.encode(command);
			}

			// get parameters
			Vector<Parameter> parameters = atCommand.getParameters();
			for (int i = 0; i < parameters.size(); i++) {
				Parameter parameter = parameters.get(i);
				String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameter.getName());
				boolean hasParemeter = (boolean) messageClass.getMethod("has" + paramNameUpperCamel).invoke(message);
				if (hasParemeter) {
					String paramValue = "";

					// encode with parser if exists
					if (parameter.hasParser()) {
						Class<?> parserClass = Class.forName(parameter.getParser());
						ProtobufParser parser = (ProtobufParser) parserClass.newInstance();
						paramValue = new String(parser.encode(command));
					} else {

						// standard encode
						Class<?> paramClass = messageClass.getMethod("get" + paramNameUpperCamel).getReturnType();
						paramValue = messageClass.getMethod("get" + paramNameUpperCamel).invoke(message).toString();
						if (paramClass.equals(boolean.class)) {
							paramValue = paramValue.equals("true") ? parameter.getTrueValue() + ""
									: parameter.getFalseValue() + "";
						} else if (!paramClass.isPrimitive() && !paramClass.equals(String.class)) {
							Object paramObject = messageClass.getMethod("get" + paramNameUpperCamel).invoke(message);
							paramValue = paramClass.getMethod("getNumber").invoke(paramObject).toString();
						}
					}

					// set parameters
					if (i == 0) {
						commandString += paramValue;
					} else {
						commandString += atCommand.getDelimiter() + paramValue;
					}
				}
			}

			// set sufix
			if (atCommand.hasSufix()) {
				commandString += atCommand.getSufix();
			}
		} catch (InvalidProtocolBufferException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | ClassNotFoundException
				| InstantiationException e) {
			e.printStackTrace();
		}
		return commandString.getBytes();
	}

	/**
	 * 
	 * Translate AT Command to Protobuf format using a specific description of
	 * command. The method uses XML file which contains description of specific
	 * AT command.
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
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(commandString);
		if (matcher.find()) {
			prefix = matcher.group(0);
		}

		// get command with prefix
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

			// sufix check
			if (!commandString.endsWith(atCommand.getSufix())) {
				throw new XMLParseException(
						"Command " + commandString + " must have " + atCommand.getSufix() + " sufix");
			}

			// get params
			String params = commandString.substring(atCommand.getPrefix().length(),
					commandString.length() - atCommand.getSufix().length());

			// number of parameters check
			if (atCommand.hasDelimiter() && !atCommand.hasParser() && params.length() != 0
					&& params.split(atCommand.getDelimiter()).length > atCommand.getParameters().size()) {
				throw new XMLParseException(
						"Number of parameters must be less or equals to " + atCommand.getParameters().size());
			}

			// decode command with parser
			if (atCommand.hasParser()) {
				Class<?> parserClass = Class.forName(atCommand.getParser());
				ProtobufParser parser = (ProtobufParser) parserClass.newInstance();
				parser.decode(params.getBytes(), commandTypeBuilderObject);
				
				// standard command decode
			} else {
				for (int i = 0; i < atCommand.getParameters().size(); i++) {

					// get parameter name
					Parameter parameter = atCommand.getParameters().get(i);
					String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameter.getName());
					String paramValue = "";

					// parameters syntax check
					if (i == 0) {
						if (params.length() == 0) {
							if (!parameter.isOptional()) {
								throw new XMLParseException(
										"Required parameter " + parameter.getName() + " is missing");
							} else {
								break;
							}
						}
					} else {
						if (atCommand.hasDelimiter()) {
							if (params.split(atCommand.getDelimiter()).length == i) {
								if (!parameter.isOptional()) {
									throw new XMLParseException(
											"Required parameter " + parameter.getName() + " is missing");
								} else {
									break;
								}
							}
						}
					}

					// param class
					Class<?> paramClass = commandTypeBuilderClass.getMethod("get" + paramNameUpperCamel)
							.getReturnType();

					// param value
					if (atCommand.hasDelimiter()) {
						paramValue = params.split(atCommand.getDelimiter())[i].trim();
					} else {
						paramValue = params.trim();
					}

					// decode parameter with parser
					if (parameter.hasParser()) {
						Class<?> parserClass = Class.forName(parameter.getParser());
						ProtobufParser parser = (ProtobufParser) parserClass.newInstance();
						parser.decode(paramValue.getBytes(), commandTypeBuilderObject);

						// standard parameter decode
					} else {

						// param is string
						if (paramClass.equals(String.class)) {

							// check param pattern
							if (parameter.hasPattern()) {
								Pattern paramPattern = Pattern.compile(parameter.getPattern());
								Matcher paramMatcher = paramPattern.matcher(paramValue);
								if (!paramMatcher.find()) {
									throw new XMLParseException("Format exception for parameter " + parameter.getName()
											+ ": " + paramValue);
								}
							}

							// set string param
							commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
									.invoke(commandTypeBuilderObject, paramValue);

							// param is primitive
						} else if (paramClass.isPrimitive()) {
							Class<?> wrepperClass = getWrepperClass(paramClass);
							Object paramValueObject = null;

							// param is boolean
							if (boolean.class.equals(paramClass)) {
								boolean paramValueBoolean = false;
								if (parameter.hasTrueValue() && paramValue.equals(parameter.getTrueValue() + "")) {
									paramValueBoolean = true;
								} else if (parameter.hasFalseValue()
										&& paramValue.equals(parameter.getFalseValue() + "")) {
									paramValueBoolean = false;
								} else {
									throw new XMLParseException("Value exception for parameter " + parameter.getName()
											+ ": " + paramValue + "[true=" + parameter.getTrueValue() + " false="
											+ parameter.getFalseValue() + "]");
								}
								paramValueObject = Boolean.valueOf(paramValueBoolean);

								// others primitive types
							} else {
								paramValueObject = wrepperClass.getMethod("valueOf", String.class).invoke(wrepperClass,
										paramValue);
								double paramValueDouble = Double.valueOf(paramValueObject.toString());
								if (parameter.hasMinValue() && paramValueDouble < parameter.getMinValue()) {
									throw new XMLParseException("Value exception for parameter " + parameter.getName()
											+ ":" + paramValueObject + "[must be greater or equals to "
											+ parameter.getMinValue() + "]");
								}
								if (parameter.hasMaxValue() && paramValueDouble > parameter.getMaxValue()) {
									throw new XMLParseException("Value exception for parameter " + parameter.getName()
											+ ":" + paramValueObject + "[must be less or equals to "
											+ parameter.getMaxValue() + "]");
								}
							}
							commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
									.invoke(commandTypeBuilderObject, paramValueObject);

							// param is enumeration
						} else {
							int paramValueInt = Integer.parseInt(paramValue);
							Object paramValueObject = paramClass.getMethod("valueOf", int.class).invoke(paramClass,
									paramValueInt);
							if (paramValueObject != null) {
								commandTypeBuilderClass.getMethod("set" + paramNameUpperCamel, paramClass)
										.invoke(commandTypeBuilderObject, paramValueObject);
							} else {
								throw new XMLParseException("Value exception for parameter " + parameter.getName()
										+ ": " + paramValueObject);
							}
						}
					}
				}
			}

			// set environment
			for (Parameter parameter : atCommand.getParameters()) {
				if (parameter.isEnvironment()) {
					String paramNameUpperCamel = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, parameter.getName());
					String key = atCommand.getName() + "." + atCommand.getType() + "." + parameter.getName();
					byte[] value = commandTypeBuilderClass.getMethod("get" + paramNameUpperCamel)
							.invoke(commandTypeBuilderObject).toString().getBytes();
					environmentVariables.put(key, value);
				}
			}

			// build command type
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
				| InvocationTargetException | SecurityException | ClassNotFoundException | InstantiationException e) {
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
	public static Class<?> getWrepperClass(Class<?> primitiveClass) {
		return wrepperTypes.get(primitiveClass);
	}

}
