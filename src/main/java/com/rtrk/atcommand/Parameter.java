package com.rtrk.atcommand;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * The Parameter class represents parameter of AT Command.
 * 
 * @author djekanovic
 *
 */
public class Parameter {

	private String name;
	private boolean optional;
	private String parser;
	private boolean environment;
	private String pattern;
	private Map<String, Double> boundaries = new HashMap<String, Double>();
	private Map<String, Integer> booleanValues = new HashMap<String, Integer>();

	public Parameter() {
		super();
	}

	public Parameter(String name, boolean optional, String parser, boolean environment, Map<String, Double> boundaries,
			Map<String, Integer> trueAndFalseValues) {
		super();
		this.name = name;
		this.optional = optional;
		this.parser = parser;
		this.environment = environment;
		this.boundaries = boundaries;
		this.booleanValues = trueAndFalseValues;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
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

	public boolean isEnvironment() {
		return environment;
	}

	public void setEnvironment(boolean environment) {
		this.environment = environment;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean hasPattern() {
		return pattern != null;
	}

	public double getMinValue() {
		return boundaries.get("min");
	}

	public void setMinValue(double min) {
		boundaries.put("min", min);
	}

	public boolean hasMinValue() {
		return boundaries.containsKey("min");
	}

	public double getMaxValue() {
		return boundaries.get("max");
	}

	public void setMaxValue(double max) {
		boundaries.put("max", max);
	}

	public boolean hasMaxValue() {
		return boundaries.containsKey("max");
	}

	public int getTrueValue() {
		return booleanValues.get("true");
	}

	public void setTrueValue(int trueValue) {
		booleanValues.put("true", trueValue);
	}

	public boolean hasTrueValue() {
		return booleanValues.containsKey("true");
	}

	public int getFalseValue() {
		return booleanValues.get("false");
	}

	public void setFalseValue(int falseValue) {
		booleanValues.put("false", falseValue);
	}

	public boolean hasFalseValue() {
		return booleanValues.containsKey("false");
	}

}
