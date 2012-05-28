package com.lambdazen.websocket.sample;

import javax.servlet.http.HttpServletRequest;

import com.lambdazen.websocket.AbstractRMIWebSocketServlet;
import com.lambdazen.websocket.IRMIWebSocket;
import com.lambdazen.websocket.IRMIWebSocketListener;

public class CallTestServlet extends AbstractRMIWebSocketServlet {

	@Override
	public IRMIWebSocketListener createListener(IRMIWebSocket rws) {
		return new CallTestService(rws);
	}

	@Override
	public boolean checkOrigin(HttpServletRequest request, String origin) {
		return true;
	}

}
