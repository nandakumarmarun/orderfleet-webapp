<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<title>Hello WebSocket</title>
<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="/webjars/jquery/jquery.min.js"></script>
<script src="/webjars/sockjs-client/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/stomp.min.js"></script>
<style type="text/css">
body {
	background-color: #f5f5f5;
}

#main-content {
	max-width: 940px;
	padding: 2em 3em;
	margin: 0 auto 20px;
	background-color: #fff;
	border: 1px solid #e5e5e5;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
}
</style>
</head>
<body>
	<noscript>
		<h2 style="color: #ff0000">Seems your browser doesn't support
			Javascript! Websocket relies on Javascript being enabled. Please
			enable Javascript and reload this page!</h2>
	</noscript>
	<div id="main-content" class="container">
		<div class="row">
			<div class="col-md-6">
				<form class="form-inline">
					<div class="form-group">
						<label for="connect">WebSocket connection:</label>
						<button id="connect" class="btn btn-default" type="submit">Connect</button>
						<button id="disconnect" class="btn btn-default" type="submit"
							disabled="disabled">Disconnect</button>
					</div>
				</form>
			</div>
			<div class="col-md-6">
				<form class="form-inline">
					<div class="form-group">
						<label for="name">What is your name?</label> <input type="text"
							id="name" class="form-control" placeholder="Your name here...">
					</div>
					<button id="send" class="btn btn-default" type="submit">Send</button>
				</form>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<table id="conversation" class="table table-striped">
					<thead>
						<tr>
							<th>Greetings</th>
						</tr>
					</thead>
					<tbody id="greetings">
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		var ws;
		$(document).ready(function() {
			$("form").on('submit', function(e) {
				e.preventDefault();
			});
			$("#connect").click(function() {
				connect();
			});
			$("#disconnect").click(function() {
				disconnect();
			});
			$("#send").click(function() {
				sendName();
			});
		});

		function setConnected(connected) {
			$("#connect").prop("disabled", connected);
			$("#disconnect").prop("disabled", !connected);
			if (connected) {
				$("#conversation").show();
			} else {
				$("#conversation").hide();
			}
			$("#greetings").html("");
		}

		function connect() {
			if (ws == null) {
				ws = new WebSocket("ws://"+ location.host + "/messages");
				setConnected(true);

				ws.onmessage = function(event) {
					console.log("message : " + event.data);
					showGreeting(event.data);
				}

				ws.onopen = function() {
				}

				ws.onclose = function(event) {
					console.log("closed: " + event.reason);
					disconnect();
				}
			}
		}

		function disconnect() {
			if (ws != null) {
				ws.close();
			}
			setConnected(false);
			console.log("Disconnected");
		}

		function sendName() {
			ws.send($("#name").val());
			//sendMessageAjax();
			$("#name").val('');
		}

		function showGreeting(message) {
			$("#greetings").append("<tr><td>" + message + "</td></tr>");
		}

		function sendMessageAjax() {
			var socketContextPath = location.protocol + '//' + location.host
					+ '/web/sockets/sendMessage?msg=' + $("#name").val();
			$.ajax({
				url : socketContextPath,
				method : 'GET',
				success : function(data) {
					console.log(data);
				},
				error : function(xhr, error) {
					console.log(error);
				}
			});
		}
	</script>
</body>
</html>
