<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<head>
	<jsp:include page="../fragments/m_head.jsp"></jsp:include>
	<title>SalesNrich | Dashboard</title>

	<spring:url value="/resources/assets/css/flipclock.css"
	var="flipclockCss"></spring:url>
	<link href="${flipclockCss}" rel="stylesheet">

	<style type="text/css">
		.text {
			display: block;
			width: 100px;
			overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
		}
	
		/**THE SAME CSS IS USED IN ALL 3 DEMOS**/
		/**gallery margins**/
		.zoom {
			-webkit-transition: all 0.35s ease-in-out;
			-moz-transition: all 0.35s ease-in-out;
			transition: all 0.35s ease-in-out;
		}
		
		.zoom:hover, .zoom:active, .zoom:focus {
			/**adjust scale to desired size, add browser prefixes**/
			-ms-transform: scale(10);
			-moz-transform: scale(10);
			-webkit-transform: scale(10);
			-o-transform: scale(10);
			transform: scale(10);
			position: relative;
			z-index: 100;
		}
		
		.number-circle {
			border-radius: 50%;
			behavior: url(PIE.htc); /* remove if you don't care about IE8 */
			padding: 5px;
			background: #fff;
			border: 1px solid #666;
			color: #666;
			text-align: center;
			font: 14px Arial, sans-serif;
		}
	</style>
</head>

<!-- styles for the dashboard -->
<spring:url value="/resources/assets/css/dashboard-custom.css"
	var="dashboardCss"></spring:url>
<link href="${dashboardCss}" rel="stylesheet">

<!-- Bar graph -->
<spring:url
	value="/resources/assets/plugin/britecharts/css/common.min.css"
	var="barCommonCss"></spring:url>
<link href="${barCommonCss}" rel="stylesheet">
<spring:url value="/resources/assets/plugin/britecharts/css/bar.min.css"
	var="barMinCss"></spring:url>
<link href="${barMinCss}" rel="stylesheet">

<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			
			<div class="row">
				<div class="col-sm-6">
					<a href="add-mobile-users" class="btn btn-lg btn-info">Add Users</a>
				</div>
			</div>
			
			<!--the inner contents Ends here-->
			<div class="clearfix"></div>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
			<!-- footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	
	
	
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<!-- web socket -->
	<spring:url value="/resources/assets/js/q.js" var="QJs"></spring:url>
	<script type="text/javascript" src="${QJs}"></script>

	<spring:url value="/resources/assets/js/websocket/sockjs-0.3.4.js" var="StockJs"></spring:url>
	<script type="text/javascript" src="${StockJs}"></script>

	<spring:url value="/resources/assets/js/websocket/stomp.js" var="StompJs"></spring:url>
	<script type="text/javascript" src="${StompJs}"></script>

	<spring:url value="/resources/assets/plugin/d3/d3.min.js" var="d3Js"></spring:url>
	<script type="text/javascript" src="${d3Js}"></script>
	
	<spring:url value="/resources/assets/plugin/britecharts/js/bar.min.js" var="barMinJs"></spring:url>
	<script type="text/javascript" src="${barMinJs}"></script>
	
	<spring:url value="/resources/assets/plugin/britecharts/js/miniTooltip.min.js" var="miniTooltipJs"></spring:url>
	<script type="text/javascript" src="${miniTooltipJs}"></script>

	<spring:url value="/resources/assets/plugin/d3/d3-funnel.min.js" var="d3FunnelJs"></spring:url>
	<script type="text/javascript" src="${d3FunnelJs}"></script>
	
	<!-- flipclock js countdown timer-->
	<spring:url value="/resources/assets/js/flipclock.min.js" var="flipclockJs"></spring:url>
	<script type="text/javascript" src="${flipclockJs}"></script>
			

</body>
</html>