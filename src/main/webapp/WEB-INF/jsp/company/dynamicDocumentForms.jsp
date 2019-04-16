<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dynamic Document Forms</title>
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<!-- Include the plugin's CSS and JS for multi-select dropdown: -->
<!-- https://github.com/davidstutz/bootstrap-multiselect -->
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2 id="title">Dynamic Document Forms</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-4">
								<select id="dbEmployee" name="employeePid" class="form-control" onchange="DynamicDocumentForm.onChangeUser()">
									<option value="no">Select Employee</option>
									<option value="all">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-4">
								<select id="dbDocument" name="documentPid" class="form-control"
									onchange="DynamicDocumentForm.onChangeDocument()">
									<option value="no">Select Document</option>
								</select>
							</div>
							<div class="col-sm-4">
								<select id="dbForm" name="form" class="form-control">
									<option value="no">Select Form</option>
								</select>
							</div>

						</div>
					</form>
				</div>

				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								<select class="form-control" id="dbDateSearch"
									onchange="DynamicDocumentForm.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							<div class="col-sm-2 hide custom_date1">
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromDate"
										placeholder="Select From Date" style="background-color: #fff;"
										readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
								<div class="input-group">
									<input type="text" class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="checkbox col-sm-2">
								<label><input id="includeHeader" type="checkbox"
									checked="checked" value="">Include Header</label>
							</div>
							<div class="checkbox col-sm-2">
								<label><input id="includeAccount" type="checkbox"
									checked="checked" value="">Include Account Details</label>
							</div>
							<div class="col-sm-2">
								<button id="btnApply" type="button" class="btn btn-info">Apply</button>
							</div>
							<div class="col-sm-2">
								<button id="btnDownload" type="button" class="btn btn-success">Download
									Xls</button>
							</div>

						</div>
					</form>
				</div>
			</div>
			<table id="tblDynamicForm" class="table table-striped table-bordered">
				<thead>
					<tr id="tblHead">
					</tr>
				</thead>
				<tbody id="tblBody">

				</tbody>
			</table>

			<div class="modal fade custom-width" id="mdlDynamicReport">
				<div class="modal-dialog" style="width: 30%; height: 30px;">
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
			<spring:url value="/web/dynamic-document-forms"
				var="urlDynamicDocumentForms"></spring:url>
		</div>

	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>

	<spring:url value="/resources/app/dynamic-document-form.js"
		var="dynamicDocumentFormJs"></spring:url>
	<script type="text/javascript" src="${dynamicDocumentFormJs}"></script>

</body>
</html>