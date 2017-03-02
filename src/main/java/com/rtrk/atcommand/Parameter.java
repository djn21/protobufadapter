package com.rtrk.atcommand;

import java.util.HashMap;
import java.util.Map;

public class Parameter {

	private String name;
	private boolean optional;
	private Map<String, Double> boundaries = new HashMap<String, Double>();
	private Map<String, Integer> trueAndFalseValues=new HashMap<String, Integer>();

	public Parameter() {
		super();
	}

	public Parameter(String name, boolean optional, Map<String, Double> boundaries, Map<String, Integer> trueAndFalseValues) {
		super();
		this.name = name;
		this.optional = optional;
		this.boundaries = boundaries;
		this.trueAndFalseValues=trueAndFalseValues;
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
	
	public int getTrueValue(){
		return trueAndFalseValues.get("true");
	}
	
	public void setTrueValue(int trueValue){
		trueAndFalseValues.put("true", trueValue);
	}
	
	public boolean hasTrueValue(){
		return trueAndFalseValues.containsKey("true");
	}
	
	public int getFalseValue(){
		return trueAndFalseValues.get("false");
	}
	
	public void setFalseValue(int falseValue){
		trueAndFalseValues.put("false", falseValue);
	}
	
	public boolean hasFalseValue(){
		return trueAndFalseValues.containsKey("false");
	}

}
