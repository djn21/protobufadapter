package com.rtrk.adapter.exception;

public class ParameterValueOutOfBoundException extends Exception {

	private static final long serialVersionUID = -8710988170995262821L;

	public ParameterValueOutOfBoundException() {
		super("Parameter value out of bound");
	}

	public ParameterValueOutOfBoundException(String msg) {
		super(msg);
	}

}
