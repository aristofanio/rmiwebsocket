<html>
  <head>
    <title>Farenheit to Celcius converter</title>

    <script src="RMIWebSocket.js"></script>
  </head>

  <body>
    <h2>Farenheit to Celcius converter</h2>

    <form id="ftoc">
      <table>
        <tr><td>Farenheit <td> <input name="fval" onchange="submitf()"></input> </tr></td>
        <tr><td>Celcius <td> <input name="cval" onchange="submitc()"></input> </tr></td>
      </table>
    </form>
   
<script>
fref = document.forms["ftoc"].elements["fval"];
cref = document.forms["ftoc"].elements["cval"];

function unsetErr() {
  fref.style.background = "white";
  cref.style.background = "white";
}
 
callback = { 
  updateF: function(str) { fref.value = str; unsetErr(); },
  updateC: function(str) { cref.value = str; unsetErr(); },
  setErrF: function(str) { fref.style.background = "#FF6600"; },
  setErrC: function(str) { cref.style.background = "#FF6600"; },
  onsocketerror: function(str) { alert("Failed to connect or lost web socket"); },
  onrmierror: function(str) { alert("RMI error: " + str); }
};

// Construct the WebSocket URI
httpUri = location.href;
wsUri = "ws://" + httpUri.substring(httpUri.indexOf("://") + 3, httpUri.lastIndexOf("/")) + "/TempConversion";
alert("Opening a web socket to " + wsUri);

rws = new RMIWebSocket(wsUri, callback);

function submitf() {
  rws.call('onTempChangeF', fref.value);
}

function submitc() {
  rws.call('onTempChangeC', cref.value);
}
</script>
   
</body>
</html>
