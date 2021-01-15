<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stage Funnel</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="/WEB-INF/jsp/fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="/WEB-INF/jsp/fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<div class="row">
				<div class="col-md-6 col-sm-6 clearfix">
					<h2 id="title">Process Flow Funnel</h2>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="margin-top: 3%;">
				<div class="col-md-12 col-sm-12 clearfix">
					<div id="chartContainer">
						<img src="/resources/assets/images/menu-ajax-loader.gif"
							style="display: block; margin: auto;">
					</div>
				</div>
			</div>

			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/plugin/d3/d3.min.js" var="d3Js"></spring:url>
	<script type="text/javascript" src="${d3Js}"></script>

	<spring:url value="/resources/assets/plugin/d3/d3-funnel.min.js"
		var="d3FunnelJs"></spring:url>
	<script type="text/javascript" src="${d3FunnelJs}"></script>
	
	<spring:url value="/resources/app/process-flow-funnel-chart.js"
		var="funnelChartJs"></spring:url>
	<script type="text/javascript" src="${funnelChartJs}"></script>
</body>
</html>