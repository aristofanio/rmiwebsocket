// Except otherwise noted, this report is © 2012 Sridhar Ramachandran, under a 
// Creative Commons Attribution license: http://creativecommons.org/licenses/by/3.0/
// Please cite "RMI WebSocket, http://lambdazen.com/rmiwebsocket/"
function RMIWebSocket(url, callback) {
    "use strict";

    // Queue to hold messages before the socket is open
    this.queue = [];

    // Is the socket open?
    this.opened = false;

    // Is the socket closed?
    this.closed = false;

    // Open the web socket
    this.websocket = new WebSocket(url);

    // Maintain a reference to the parent
    this.websocket._rwsparent = this;

    // Define invoke method that takes the method's name and params
    this.call = function() {
        if (arguments.size == 0) {
            throw "RMIWebSocket.call requires 1 or more parameters";
        }

        var method = arguments[0]
	var params = Array.prototype.slice.call(arguments, 1)

        if (this.closed) {
            throw "Connection closed";
        }

        var evt = {method: method, params: params}

        if (this.opened == true) {
            this.websocket.send(JSON.stringify(evt));
        } else {
            this.queue.push(evt);
        }
    }

    Object.preventExtensions(this);

    this.websocket.onopen = function(evt) { 
        for (var i=0; i < this._rwsparent.queue.length; i++) {
            this.send(JSON.stringify(this._rwsparent.queue[i]));
        }

        this._rwsparent.opened = true;
    }

    this.websocket.onclose = function(evt) {
        this.closed = true; 
    }

    this.websocket.onmessage = function(evt) {
        var serverCall = JSON.parse(evt.data);
        if (callback[serverCall.method]) {
            callback[serverCall.method].apply(callback, serverCall.params);
        } else {
            if (callback.onrmierror) {
                callback.onrmierror.call(callback, "Could not find " + serverCall.method);
            } else {
                alert("RMIWebSocket: Could not find " + method + " in callback handler " + callback);
            }
        }
    }

    this.websocket.onerror = function(evt) {
        if (callback.onsocketerror) {
            callback.onsocketerror.apply(callback, evt);
        } else {
            alert("RMIWebSocket: Got onerror from websocket with event: " + evt);
        }
    }
}  
