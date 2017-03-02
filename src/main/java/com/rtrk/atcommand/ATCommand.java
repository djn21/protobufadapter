package com.rtrk.atcommand;

import java.util.Vector;

public class ATCommand {

	private String name;
	private String type;
	private String clazz;
	private String prefix;
	private String sufix;
	private String delimiter;
	private Vector<Parameter> parameters = new Vector<Parameter>();

	public ATCommand() {
		super();
	}

	public ATCommand(String name, String type, String clazz, String prefix, String sufix, String delimiter,
			Vector<Parameter> parameters) {
		super();
		this.name = name;
		this.type = type;
		this.clazz = clazz;
		this.prefix = prefix;
		this.sufix = sufix;
		this.delimiter = delimiter;
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

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Vector<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<Parameter> parameters) {
		this.parameters = parameters;
	}

}
