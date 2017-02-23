package com.rtrk.adapter.exception;

public class RequiredParameterMissingException extends Exception{
	
	private static final long serialVersionUID = -7569640005667855065L;

	public RequiredParameterMissingException() {
		super("Required parameter missing");
	}

	public RequiredParameterMissingException(String msg) {
		super(msg);
	}
	
}
