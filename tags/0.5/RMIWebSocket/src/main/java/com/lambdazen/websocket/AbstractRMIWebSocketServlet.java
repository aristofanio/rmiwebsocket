package com.lambdazen.websocket;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketFactory;


public abstract class AbstractRMIWebSocketServlet extends HttpServlet {
	private static final long serialVersionUID = 8882261079348258206L;
	private WebSocketFactory wsFactory;

	@Override
	public void init() throws ServletException {
		// Create and configure WS factory
		wsFactory = new WebSocketFactory(new WebSocketFactory.Acceptor() {
			public boolean checkOrigin(HttpServletRequest request, String origin) {
				return AbstractRMIWebSocketServlet.this.checkOrigin(request, origin);
			}

			public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
				return new RMIWebSocket(request,
						new IRMIWebSocketListenerFactory() {
							public IRMIWebSocketListener createListener(IRMIWebSocket rmiWebSocket) {
								return AbstractRMIWebSocketServlet.this.createListener(rmiWebSocket);
							}
						});
			}
		});

		wsFactory.setBufferSize(4096);
		wsFactory.setMaxIdleTime(600000);
	}
	

	  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		  if (wsFactory.acceptWebSocket(request,response)) return;

		  response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Websocket only");
	  }

	public abstract IRMIWebSocketListener createListener(IRMIWebSocket rws);
	
	public abstract boolean checkOrigin(HttpServletRequest request, String origin);
}
