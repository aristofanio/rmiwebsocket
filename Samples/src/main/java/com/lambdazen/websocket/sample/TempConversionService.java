package com.lambdazen.websocket.sample;

import java.text.DecimalFormat;

import com.lambdazen.websocket.IRMIWebSocket;
import com.lambdazen.websocket.IRMIWebSocketListener;
import com.lambdazen.websocket.RMIWebSocketException;

public class TempConversionService implements IRMIWebSocketListener {
	IRMIWebSocket rws;
	DecimalFormat formatter;
	
	public TempConversionService(IRMIWebSocket rws) {
		this.rws = rws;
		this.formatter = new DecimalFormat("#.##");
	}
	
	public void onClose() {
		// Nothing to do
	}

	public void onDeserializationError(Exception e, String msg) {
		// Some error reporting
		System.err.println("RMIWebSocket deserialization error " + msg);
		e.printStackTrace();
	}

	public void onError(RMIWebSocketException e) {
		// Some error reporting
		System.err.println("RMIWebSocket error");
		e.printStackTrace();
	}

	// Remote method calls
	public void onTempChangeF(String val) {
		onTempChange(true, val);
	}
	
	public void onTempChangeC(String val) {
		onTempChange(false, val);
	}
	
	private void onTempChange(boolean isFarenheit, String val) {
		try {
			double temp;
			try {
				temp = Double.valueOf(val);
			} catch (NumberFormatException e) {
				// Mark field as erroneous
				rws.call(isFarenheit ? "setErrF" : "setErrC");
				return;
			}

			double otherTemp = isFarenheit ? (temp - 32) * 5 / 9 : temp * 9/5 + 32;
			
			rws.call(isFarenheit ? "updateC" : "updateF", formatter.format(otherTemp));
		} catch (RMIWebSocketException e) {
			// Some error reporting
			System.err.println("RMIWebSocket call error");
			e.printStackTrace();
		}
	}
}
