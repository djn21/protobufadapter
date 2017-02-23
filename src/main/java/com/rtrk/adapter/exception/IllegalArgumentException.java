package com.rtrk.adapter.exception;

public class IllegalArgumentException extends Exception {

	private static final long serialVersionUID = -1649349605282280725L;

	public IllegalArgumentException() {
		super("Illegal argument");
	}

	public IllegalArgumentException(String msg) {
		super(msg);
	}

}
