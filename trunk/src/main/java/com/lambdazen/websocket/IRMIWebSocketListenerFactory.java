package com.lambdazen.websocket;

public interface IRMIWebSocketListenerFactory {
	IRMIWebSocketListener createListener(IRMIWebSocket rmiWebSocket);
}
