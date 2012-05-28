package com.lambdazen.websocket;

public class RMIWebSocketException extends Exception {
	private static final long serialVersionUID = -855688986456900709L;

	public RMIWebSocketException(String s) {
		super(s);
	}

	public RMIWebSocketException(String s, Throwable t) {
		super(s, t);
	}
}
