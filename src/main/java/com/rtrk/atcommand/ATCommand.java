package com.rtrk.atcommand;

import java.util.Vector;

/**
 * 
 * The ATCommand class represents AT Command.
 * 
 * @author djekanovic
 *
 */
public class ATCommand {

	private String name;
	private String type;
	private String clazz;
	private String prefix;
	private String sufix;
	private String delimiter;
	private String parser;
	private String generator;
	private Vector<Parameter> parameters = new Vector<Parameter>();

	public ATCommand() {
		super();
	}

	public ATCommand(String name, String type, String clazz, String prefix, String sufix, String delimiter,
			String parser, String generator, Vector<Parameter> parameters) {
		super();
		this.name = name;
		this.type = type;
		this.clazz = clazz;
		this.prefix = prefix;
		this.sufix = sufix;
		this.delimiter = delimiter;
		this.parser = parser;
		this.generator = generator;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSufix() {
		return sufix;
	}

	public void setSufix(String sufix) {
		this.sufix = sufix;
	}

	public boolean hasSufix() {
		return !"".equals(sufix);
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean hasDelimiter() {
		return !"".equals(delimiter);
	}

	public String getParser() {
		return parser;
	}

	public void setParser(String parser) {
		this.parser = parser;
	}

	public boolean hasParser() {
		return !"".equals(parser);
	}

	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public boolean hasGenerator() {
		return !"".equals(generator);
	}

	public Vector<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<Parameter> parameters) {
		this.parameters = parameters;
	}

}
