<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dynamic report</title>
<!-- Include the plugin's CSS and JS for multi-select dropdown: -->
<!-- https://github.com/davidstutz/bootstrap-multiselect -->
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">
<style type="text/css">
@media only screen and (max-width: 800px) {
	/* Force table to not be like tables anymore */
	#tblDynamicReport table, #tblDynamicReport thead, #tblDynamicReport tbody,
		#tblDynamicReport th, #tblDynamicReport td, #tblDynamicReport tr {
		display: block;
	}

	/* Hide table headers (but not display: none;, for accessibility) */
	#tblDynamicReport thead tr {
		position: absolute;
		top: -9999px;
		left: -9999px;
	}
	#tblDynamicReport tr {
		border: 1px solid #ccc;
	}
	#tblDynamicReport td {
		/* Behave  like a "row" */
		border: none;
		border-bottom: 1px solid #eee;
		position: relative;
		padding-left: 50%;
		white-space: normal;
		text-align: left;
	}
	#tblDynamicReport td:before {
		/* Now like a table header */
		position: absolute;
		/* Top/left values mimic padding */
		top: 6px;
		left: 6px;
		width: 45%;
		padding-right: 10px;
		white-space: nowrap;
		text-align: left;
		font-weight: bold;
	}

	/*
	Label the data
	*/
	#tblDynamicReport td:before {
		content: attr(data-title);
	}
}
</style>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2 id="tittleName">Dynamic report</h2>
			<div class="clearfix"></div>
			<hr />
			<div class=row>
				<div class="col-md-12">
					<div>
						<select id="dbColumnName" class="form-control">
							<option value="-1">-- Select a column --</option>
							<c:forEach items="${colHeaders}" var="colName">
								<option value="${colName}">${colName}</option>
							</c:forEach>
						</select>
					</div>
					<br />
					<div class="table-wrapper">
						<div class="rt-scrollable">
							<table id="tblDynamicReport"
								class="table table-bordered responsive">
								<thead>
									<tr>
										<c:forEach var="colName" items="${colHeaders}">
											<th class="${colName}" data-initialsortorder="asc">${colName}
												<div style="float: right;">
													<span onclick="DynamicReport.sortDynamicReport(this);"
														class="glyphicon glyphicon-sort" style="font-size: 15px;"></span>
												</div>
											</th>
										</c:forEach>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${dynamicReportDetails}" var="reportDetail"
										varStatus="loopStatus">
										<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
											<c:forEach items="${reportDetail}" var="col"
												varStatus="colLoopStatus">
												<td data-title="${colHeaders[colLoopStatus.index]}">${col}</td>
											</c:forEach>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="modal fade" id="mdlDynamicReport">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">×</button>
							<h4 class="modal-title">
								FilterBy: <b id="mdlTitle"></b>
							</h4>
						</div>
						<div class="modal-body">
							<div class="form-group">
								<select class="form-control" id="sbDynamicReport"
									multiple="multiple"></select>
							</div>
						</div>
						<div class="modal-footer">
							<button id="btnClearFilter" type="button" class="btn btn-default">Clear
								Filter</button>
							<button id="btnApplyFilter" type="button" class="btn btn-info">Apply</button>
						</div>
					</div>
				</div>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>

	<spring:url value="/resources/app/dynamic-report.js"
		var="dynamicReportJs"></spring:url>
	<script type="text/javascript" src="${dynamicReportJs}"></script>
</body>
</html>