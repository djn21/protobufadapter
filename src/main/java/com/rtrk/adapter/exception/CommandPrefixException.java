package com.rtrk.adapter.exception;

public class CommandPrefixException extends Exception {

	private static final long serialVersionUID = 2980759100398339468L;

	public CommandPrefixException() {
		super("Illegal command prefix");
	}

	public CommandPrefixException(String msg) {
		super(msg);
	}

}
