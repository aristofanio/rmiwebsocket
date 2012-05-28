package com.lambdazen.websocket.sample;

import javax.servlet.http.HttpServletRequest;

import com.lambdazen.websocket.AbstractRMIWebSocketServlet;
import com.lambdazen.websocket.IRMIWebSocket;
import com.lambdazen.websocket.IRMIWebSocketListener;

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
