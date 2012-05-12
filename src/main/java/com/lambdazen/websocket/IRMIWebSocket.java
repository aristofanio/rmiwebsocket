package com.lambdazen.websocket;

import javax.servlet.http.HttpServletRequest;

public interface IRMIWebSocket {
	HttpServletRequest getHttpServletRequest();

	void call(String method, Object... params) throws RMIWebSocketException;

	void close();
}
