package com.rtrk.xml;

import java.util.HashMap;
import java.util.Map;

public class Parameter {

	private String name;
	private boolean optional;
	private Map<String, Double> boundaries = new HashMap<String, Double>();

	public Parameter() {
		super();
	}

	public Parameter(String name, boolean optional, Map<String, Double> boundaries) {
		super();
		this.name = name;
		this.optional = optional;
		this.boundaries = boundaries;
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

	public double getMin() {
		return boundaries.get("min");
	}

	public void setMin(double min) {
		this.boundaries.put("min", min);
	}

	public boolean hasMin() {
		if (boundaries.get("min") != null)
			return true;
		return false;
	}

	public double getMax() {
		return boundaries.get("max");
	}

	public void setMax(double max) {
		this.boundaries.put("max", max);
	}

	public boolean hasMax() {
		if (boundaries.get("max") != null)
			return true;
		return false;
	}

}
