package com.lambdazen.websocket;

public interface IRMIWebSocketListener {
	void onClose();

	void onDeserializationError(Exception e, String msg);

	void onError(RMIWebSocketException e);
}
