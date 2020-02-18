<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<head>
<title>SalesNrich | LiveTracking</title>

<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- Googli map API -->
<script
	src="https://maps.googleapis.com/maps/api/js?v=3.exp&key=AIzaSyDwL42-LQ4wTz5fYvNuBvlTAnWVugNC_h4&signed_in=true&libraries=places"></script>

<spring:url value="/resources/assets/css/style.css" var="style"></spring:url>
<link href="${style}" rel="stylesheet">

<!-- SockJs -->
<spring:url value="/resources/assets/js/websocket/sockjs-0.3.4.js"
	var="sockjs"></spring:url>
<script type="text/javascript" src="${sockjs}"></script>

<!-- StompJS -->
<spring:url value="/resources/assets/js/websocket/stomp.js" var="stomp"></spring:url>
<script type="text/javascript" src="${stomp}"></script>

<spring:url value="/resources/assets/css/preloader.css" var="preloader"></spring:url>
<link href="${preloader}" rel="stylesheet">

<spring:url value="/resources/assets/js/markerwithlabel.js"
	var="markerwithlabel"></spring:url>
<script type="text/javascript" src="${markerwithlabel}"></script>

<!-- jQuery UI css-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<!-- preloader -->
<spring:url value="/resources/assets/js/jquery.isloading.js"
	var="isloading"></spring:url>
<script type="text/javascript" src="${isloading}"></script>

<spring:url value="/resources/assets/js/jquery.isloading.min.js"
	var="isloadingmin"></spring:url>
<script type="text/javascript" src="${isloadingmin}"></script>

<!-- Live Tracking custom js -->
<spring:url value="/resources/app/heat-map.js" var="heatMap"></spring:url>
<script type="text/javascript" src="${heatMap}"></script>

<script type="text/javascript">
	var companyId = "${companyId}";
	var contextPath = "${pageContext.request.contextPath}";
	// initialize google map
	var map;
	var kerala = new google.maps.LatLng(10.7700, 76.6500);
	var mapOptions = {
		zoom : 8,
		center : kerala
	};
	function initialize() {
		console.log("initialize...........");
		map = new google.maps.Map(document.getElementById('map-canvas'),
				mapOptions);
		console.log("initialized");

		loadHeatMapDetails()
		// establish websocket connection
		connect();
	}

	google.maps.event.addDomListener(window, 'load', initialize);
</script>

<style>
.labels {
	color: red;
	background-color: white;
	font-family: "Lucida Grande", "Arial", sans-serif;
	font-size: 10px;
	font-weight: bold;
	text-align: center;
	width: 65px;
	border: 1px solid #19A7BF;
	white-space: nowrap;
}
</style>
<!-- media query style of add route expand collapse -->
<style>
@media screen and (min-width: 50px) and (max-width: 991px) {
	#btn-collapse-table {
		width: 45px;
		float: none;
	}
	.icon-expand-collapse {
		height: 40px;
		border-radius: 4px;
	}
	#icon-left-media {
		margin-top: 0px;
	}
	#div_for_hide {
		border-radius: 0 4px 4px 0;
		border-left: 1px solid #EBEBEB;
	}
	.a-icon-left-media {
		padding: 13px !important;
	}
	.form-padding {
		padding-left: 15px;
	}
}
</style>
<!-- media query style of add route expand collapse -->
</head>
<body class="page-body  page-left-in">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>

		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Heat Map</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="" id="btn-collapse-table">
				<div
					class="panel panel-primary table-for-left-collapse icon-expand-collapse panel-height">
					<div class="panel-heading" style="border-bottom: transparent;">
						<div class="panel-options" style="padding: 0px;">
							<!-- <a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a> -->
							<a href="#" class="a-icon-left-media"><i
								class="entypo-left-open" id="icon-left-media"></i></a>
						</div>
					</div>
				</div>
			</div>
			<!-- the content of slide expand collapse -->

			<div class="row">

				<!-- map starts here -->
				<div class="col-md-11" id="g-map">
					<div class="panel panel-primary" data-collapsed="0">
						<!-- to apply shadow add class "panel-shadow" -->
						<!-- panel head -->
						<div class="panel-heading">
							<div class="panel-title"></div>

							<div class="panel-options">
								<a href="#" data-rel="reload" id="fullscreen"
									data-toggle="tooltip" data-placement="top"
									data-original-title="Full Screen"><i class="entypo-window"></i></a>
								<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
							</div>
						</div>

						<!-- panel body -->
						<div class="panel-body no-padding" style="height: 500px;">
							<div id="map-canvas" style="height: 100%; width: 100%"></div>
						</div>
					</div>
				</div>
			</div>

			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<!-- jQuery UI Js-->
	<spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<!-- custom script here -->
	<script type="text/javascript">
		$(document).ready(function() {
			$(".table-for-left-collapse").show();

			$("#btn-collapse-table").click(function() {
				$("#div_for_hide").toggle("slide");
				$("#g-map").toggleClass("col-md-11");
				$("#g-map").toggleClass("col-md-8");
				$(".entypo-left-open").toggleClass("entypo-right-open");
			});

			$("#fullscreen").click(function() {
				var elem = document.getElementById("map-canvas");
				if (elem.requestFullscreen) {
					elem.requestFullscreen();
				} else if (elem.msRequestFullscreen) {
					elem.msRequestFullscreen();
				} else if (elem.mozRequestFullScreen) {
					elem.mozRequestFullScreen();
				} else if (elem.webkitRequestFullscreen) {
					elem.webkitRequestFullscreen();
				}
			});

		})
	</script>
</body>
</html>