<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Multiple Location Report</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
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
			<h2>Multiple Location Report</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">

							<div id='loader' class="modal fade container">

								<img src='/resources/assets/images/Spinner.gif'>

							</div>
							<div class="col-sm-1">
								<br>
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>
						</div>
<!-- 						<div class="form-group"> -->
<!-- 							<div class="progress"> -->
<!-- 								<div class="progress-bar progress-bar-success myprogress" -->
<!-- 									role="progressbar" style="width: 0%">0%</div> -->
<!-- 							</div> -->
<!-- 							<div class="msg"></div> -->
<!-- 						</div> -->
					</form>
				</div>
			</div>
			<div class="table-responsive">
				<table class="table table-striped table-bordered"
					id="tblLocationHierarchyAccountProfile">
					<thead id="tHeadLocationHierarchyAccountProfile">
					</thead>
					<tbody id="tBodyLocationHierarchyAccountProfile">
					</tbody>
				</table>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/multiple-locations-report.js"
		var="multipleLocationReportJs"></spring:url>
	<script type="text/javascript" src="${multipleLocationReportJs}"></script>


	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/assets/js/custom/jquery.aCollapTable.js"
		var="aCollapTable"></spring:url>
	<script type="text/javascript" src="${aCollapTable}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>

</body>
</html>