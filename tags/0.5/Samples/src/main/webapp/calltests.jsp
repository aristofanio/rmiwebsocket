<html>
  <head>
    <title>RMI WebSocket call tests</title>

    <script src="RMIWebSocket.js"></script>
  </head>

  <body>
    <h2>RMI WebSocket call tests</h2>

This sample page invokes a list of RMI WebSocket calls to a servlet. 
  
<div id="results">
</div>
 
<script>
callback = { 
  append: function(testname, obj) { document.getElementById("results").innerHTML += "<p><b>" + testname + "</b>: " + JSON.stringify(obj); },
  onsocketerror: function(str) { alert("Failed to connect or lost web socket"); },
  onrmierror: function(str) { alert("RMI error: " + str); }
};

// Construct the WebSocket URI
httpUri = location.href;
wsUri = "ws://" + httpUri.substring(httpUri.indexOf("://") + 3, httpUri.lastIndexOf("/")) + "/CallTest";
alert("Opening a web socket to " + wsUri);

rws = new RMIWebSocket(wsUri, callback);

rws.call("testSimpleTypes", "foo", 1, 10, 3.2e-4, 2.345);
rws.call("testUnboxedTypes", 1, 3.2e-5, 23.45);
rws.call("testArrays", ["foo", "bar"], [1, 2], [10, 1000], [3.2e-4, 3.2e-5], [2.345, 23.45]); 
rws.call("testJSONNode", {"foo": "bar", "primes": [2, 3, 5, 7, 11], tree: {"nest": {"eggs": 6, "twigs": 537}}});
rws.call("testMapper", {"id": "myID", "intArray": [1, 2, 3], "mojo": {"level": 8}});
rws.call("testMap", {"foo": "bar", "primes": [2, 3, 5, 7, 11], tree: {"nest": {"eggs": 6, "twigs": 537}}});
</script>
   
</body>
</html>
