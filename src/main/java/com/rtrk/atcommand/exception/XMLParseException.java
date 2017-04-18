package com.rtrk.atcommand.exception;

/**
 * 
 * Signals that an error has been reached while generating AT Command which
 * description was defined in .xml file.
 * 
 * @author djekanovic
 *
 */

public class XMLParseException extends RuntimeException {

	private static final long serialVersionUID = -5469058689157208331L;

	public XMLParseException() {
		super("XML parse exception");
	}

	public XMLParseException(String msg) {
		super(msg);
	}

}
