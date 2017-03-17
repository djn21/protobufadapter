package com.rtrk.atcommand.exception;

public class XMLParseException extends RuntimeException {

	private static final long serialVersionUID = -5469058689157208331L;

	public XMLParseException() {
		super("XML parse exception");
	}

	public XMLParseException(String msg) {
		super(msg);
	}

}
