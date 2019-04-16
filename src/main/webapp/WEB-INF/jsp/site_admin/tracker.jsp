<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Tracker</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Real-time user activities</h2>
			<hr />
			<table
				class="table table-condensed table-striped table-bordered table-responsive">
				<thead>
					<tr>
						<th>User</th>
						<th>IP Address</th>
						<th>Current page</th>
						<th>Time</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<%-- <c:forEach items="${activities}" var="activity"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${activity.userLogin}</td>
							<td>${activity.ipAddress}</td>
							<td>${activity.page}</td>
							<td>${activity.time}</td>
						</tr>
					</c:forEach> --%>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/q.js" var="QJs"></spring:url>
	<spring:url value="/resources/assets/js/websocket/sockjs-0.3.4.js"
		var="StockJs"></spring:url>
	<spring:url value="/resources/assets/js/websocket/stomp.js"
		var="StompJs"></spring:url>
	<script type="text/javascript" src="${QJs}"></script>
	<script type="text/javascript" src="${StockJs}"></script>
	<script type="text/javascript" src="${StompJs}"></script>

	<script type="text/javascript">
		var vm = this;
		this.activities = [];
		
		$(document).ready(function() {
			connect();
			subscribe();
			receive().then(null, null, function(activity) {
	            showActivity(activity);
	        });
		});
		
		function showActivity(activity) {
            var existingActivity = false;
            for (var index = 0; index < vm.activities.length; index++) {
                if(vm.activities[index].sessionId === activity.sessionId) {
                    existingActivity = true;
                    if (activity.page === 'logout') {
                        vm.activities.splice(index, 1);
                    } else {
                        vm.activities[index] = activity;
                    }
                }
            }
            if (!existingActivity && (activity.page !== 'logout')) {
                vm.activities.push(activity);
                $('.table').append('<tr><td>' + activity.userLogin + 
                		'<td>'+ activity.ipAddress +'</td>' +
                		'<td>'+ activity.page +'</td>' +
                		'<td>'+ activity.time + 
                		'</td></tr>' );
            }
        }
		
		//onExit unsubscribe websocket
		window.onbeforeunload = unsubscribe;
		
		var stompClient = null;
		var subscriber = null;
		var listener = Q.defer();
		var connected = Q.defer();
		var alreadyConnectedOnce = false;

		function connect() {
			//building absolute path so that websocket doesnt fail when deploying with a context path
			var loc = window.location;
			//var url = loc.protocol + '//' + loc.host + loc.pathname + '/websocket/tracker';
			var url = loc.protocol + '//' + loc.host + loc.pathname;
			/* var authToken = AuthServerProvider.getToken();
			if (authToken) {
				url += '?access_token=' + authToken;
			} */
			var socket = new SockJS(url);
			stompClient = Stomp.over(socket);
			var stateChangeStart;
			var headers = {};
			stompClient.connect(headers, function() {
				console.log("connected....")
				connected.resolve('success');
				sendActivity();
				if (!alreadyConnectedOnce) {
					/* stateChangeStart = $rootScope.$on('$stateChangeStart',
							function() {
								sendActivity();
							}); */
					alreadyConnectedOnce = true;
				}
			});
		}

		function disconnect() {
			if (stompClient !== null) {
				stompClient.disconnect();
				stompClient = null;
			}
		}

		function receive() {
			return listener.promise;
		}

		function sendActivity() {
			if (stompClient !== null && stompClient.connected) {
				stompClient.send('/topic/activity', {}, JSON.stringify({
					'page' : window.location.href.split('/').pop()
				}));
			}
		}

		function subscribe() {
			connected.promise.then(function() {
				subscriber = stompClient.subscribe('/topic/tracker', function(
						data) {
					listener.notify(JSON.parse(data.body));
				});
			}, null, null);
		}

		function unsubscribe() {
			if (subscriber !== null) {
				subscriber.unsubscribe();
			}
			listener = Q.defer();
		}
	</script>
</body>
</html>