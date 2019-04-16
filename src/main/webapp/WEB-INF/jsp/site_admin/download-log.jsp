
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich |Download Log</title>
<style type="text/css">
.error {
	color: red;
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Download Log</h2>
			<div class="row col-xs-12"></div>
			<hr>
			<div class="row">
				<div class="form-group">
					<div class="col-sm-3">
						 Date <input type="date" class="form-control" id="txtDate"
							placeholder="Select Date"
							style="background-color: #fff; width: 139px;" />
					</div>
				</div>
				<div class="col-sm-3">
					<div class="input-group">
							<button class="btn btn-success" type="button" id="btnDownload">Download</button>
					</div>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/download-log.js"
		var="downloadLogJs"></spring:url>
	<script type="text/javascript" src="${downloadLogJs}"></script>
</body>
</html>