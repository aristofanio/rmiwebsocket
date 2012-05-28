package com.lambdazen.websocket.sample;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;

import com.lambdazen.websocket.AbstractRMIWebSocketServlet;
import com.lambdazen.websocket.IRMIWebSocket;
import com.lambdazen.websocket.IRMIWebSocketListener;
import com.lambdazen.websocket.IRMIWebSocketListenerFactory;
import com.lambdazen.websocket.RMIWebSocket;

// Skeleton code from http://webtide.intalio.com/2011/08/websocket-example-server-client-and-loadtest/
public class TempConversionServlet extends AbstractRMIWebSocketServlet {
	@Override
	public IRMIWebSocketListener createListener(IRMIWebSocket rws) {
		return new TempConversionService(rws);
	}

	@Override
	public boolean checkOrigin(HttpServletRequest request, String origin) {
		// Allow all
		return true;
	}
}
