<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="/WEB-INF/jsp/fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Stage report</title>
<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">
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
					<h2 id="title">Stage Report</h2>
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="margin-top: 6%;">
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								Employee
								<!-- <div class=" input-group">
									<span
										class='input-group-addon btn  dropdown-toggle glyphicon glyphicon-filter'
										data-toggle='dropdown' aria-haspopup='true'
										aria-expanded='false' title='filter employee'></span>
									<div class='dropdown-menu dropdown-menu-left'
										style='background-color: #F0F0F0'>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetDashboardEmployees(this,"Dashboard Employee","All Dashboard Employee")'>Dashboard
												Employee</a>
										</div>
										<div>
											<a class='btn btn-default dropdown-item'
												style='width: 100%; text-align: left;'
												onclick='GetAllEmployees(this,"no","All Employee")'>All
												Employee</a>
										</div>
									</div>
									<select id="dbEmployee" name="employeePid"
										class="form-control selectpicker">
										<option value="Dashboard Employee">All Dashboard
											Employees</option>
									</select>
								</div> -->
								<select id="dbEmployee" name="employeePid" class="form-control" >
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-3">
								Phases <br /> <select id="dbStageGroup" name="stageGroupPid"
									class="form-control">
									<option value="-1">All Phases</option>
									<c:forEach items="${stageGroups}" var="stageGroup">
										<option value="${stageGroup.pid}">${stageGroup.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-3">
								Stages <br /> <select id="dbStage" name="stagePid"
									class="form-control" multiple="multiple">
									<c:forEach items="${stages}" var="stage">
										<option value="${stage.pid}">${stage.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="col-sm-3">
								Territory <select id="dbLocation" name="locationPid"
									class="form-control">
									<option value="-1">All Location</option>
									<c:forEach items="${locations}" var="location">
										<option value="${location.pid}">${location.name}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</form>
				</div>
			</div>

			<div class="row">
				<div class="col-sm-3">
					Contacts <select id="dbAccount" name="accountPid"
						class="form-control">
						<option value="-1">All Contacts</option>
						<c:forEach items="${accountProfiles}" var="accountProfile">
							<option value="${accountProfile.pid}">${accountProfile.name}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-3">
					Day <select class="form-control" id="dbDateSearch"
						onchange="StageReport.showDatePicker()">
						<option value="TODAY">Today</option>
						<option value="YESTERDAY">Yesterday</option>
						<option value="WTD">WTD</option>
						<option value="MTD">MTD</option>
						<option value="TILLDATE">TILL DATE</option>
						<option value="CUSTOM">CUSTOM</option>
					</select>
				</div>
				<div id="divDatePickers" style="display: none;">
					<div class="col-sm-2">
						From Date <input type="date" class="form-control" id="txtFromDate"
							placeholder="Select From Date"
							style="background-color: #fff; width: 139px;" />
					</div>
					<div class="col-sm-2">
						To Date <input type="date" class="form-control" id="txtToDate"
							placeholder="Select To Date"
							style="background-color: #fff; width: 139px;" />
					</div>
				</div>
				<div class="col-sm-2">
					<br>
					<button type="button" class="btn btn-info"
						onclick="StageReport.filter()">Apply</button>
						
					<button type="button" class="btn btn-orange" id="btnDownload"
						title="download xlsx">
						<i class="entypo-download"></i> download
					</button>	
				</div>
			</div>

			<div class="clearfix"></div>
			<hr />
			<div class="row" style="font-size: 14px; color: black;">
				<form class="form-inline">
					<div class="form-group col-sm-3">
						<label>Count : </label> <label id="lblCount">0</label>
					</div>
					<!-- <div class="form-group" align="right">
						<label for="srch-term">Search:</label> <input class="form-control" placeholder="Search" name="srch-term" id="srch-term">
					</div> -->
				</form>
			</div>
			<div class="table-responsive">
				<table class="table table-striped table-bordered"
					id="tblStageReport">
					<thead id="tHeadStageReport">
						<tr>
							<th>Date <i onclick="StageReport.sortTable(0)" class="fa fa-sort float-right" aria-hidden="true"></i></th>
							<th>Contact</th>
							<th>Stage</th>
							<th>User</th>
							<th>Remarks</th>
							<th width="18%">Documents</th>
							<th>Amount</th>
							<th>Qtn No</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody id="tBodyStageReport">
					</tbody>
				</table>
			</div>

			<!-- Model Container Dynamic Document-->
			<div class="modal fade custom-width" id="viewDetailsModal"
				style="display: none;">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 96%">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">
								<b>Document Details</b>
							</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<div class="row">
								<div class="col-md-12">
									<ul class="nav nav-tabs" role="tablist">
										<li class="active"><a href="#dd" role="tab"
											data-toggle="tab">Document Details</a></li>
										<!-- <li><a href="#attachments" role="tab" data-toggle="tab">Attachments</a></li> -->
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="dd">
											<table class="table table-striped table-bordered">
												<tr>
													<td>Document Number</td>
													<td id="lbl_documentNumber"></td>
												</tr>
												<tr>
													<td>User</td>
													<td id="lbl_user"></td>
												</tr>
												<tr>
													<td>Account</td>
													<td id="lbl_account"></td>
												</tr>
												<tr>
													<td>Account Ph:</td>
													<td id="lbl_accountph"></td>
												</tr>
												<tr>
													<td>Activity</td>
													<td id="lbl_activity"></td>
												</tr>
												<tr>
													<td>Document</td>
													<td id="lbl_document"></td>
												</tr>
												<tr>
													<td>Date</td>
													<td id="lbl_documentDate"></td>
												</tr>
											</table>
											<div id="divDynamicDocumentDetails"
												style="overflow: auto; height: 300px;"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>

			<div class="modal fade container" id="addNewStageModal">
				<!-- model Dialog -->
				<div class="modal-dialog" style="width: 100%;">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="viewModalLabel">Change Stage</h4>
						</div>
						<div class="modal-body">
							<!-- error message -->
							<div class="alert alert-danger alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
							<div>
								<spring:url value="/web/stage-report" var="urlStageReport"></spring:url>
								<form id="formSaveNewStage" method="post"
									action="${urlStageReport}" enctype="multipart/form-data">
									<input id="hiddenSavedStageHeaderPid" name="id" type="hidden">
									<div class="form-group">
										<label>Current Stage : <span
											style="font-size: 15px; color: black;" id="currentStage"></span></label>
									</div>
									<div class="form-group">
										<label for="stage">Change Stage To</label> <select
											id="dbNewStage" name="stagePid" class="form-control">
											<option value="-1">Select Stage</option>
											<c:forEach items="${stages}" var="stage">
												<option value="${stage.pid}" name="${stage.stageNameType}">${stage.name}</option>
											</c:forEach>
										</select>
									</div>
									 <div class="form-group" id="rca_div">
										<label for="rca">RCA Reasons</label> 
										<select id="dbRca" name="rcaPids"
											class="form-control" multiple="multiple" >
											<c:forEach items="${rcas}" var="rca">
												<option value="${rca.pid}">${rca.name}</option>
											</c:forEach> 
										</select>
									</div> 
									<div class="form-group">
										<label for="quote">Price / Quote</label> <input type="text"
											name="value" class="form-control" id="txtQuote">
									</div>
									<div class="form-group">
										<label for="quote">PO / Quotation No.</label> <input type="text"
											name="quotationNo" class="form-control" id="txtQuoteNo">
									</div>
									<div class="form-group">
										<label for="remarks">Remarks</label>
										<textarea name="remarks" class="form-control" id="txtRemarks"
											rows="5"></textarea>
									</div>
									<div class="form-group" id="upFiles"  >
										<c:forEach items="${fileUploadNames}" var="fileUploadName" varStatus="loop">
											<label id="mfiles${loop.index}">${fileUploadName}</label> 
											<input type="file" name="multipartFiles[${loop.index}]" />
										</c:forEach>
									</div>
									<br />
									<div id="progressbox" style="width: 100%;">
										<div id="progressbar"></div>
										<div id="percent">0%</div>
									</div>
									<div id="message" style="font-weight: bold;"></div>
								</form>
							</div>
						</div>
						<div class="modal-footer">
							<button id="btnSaveNewStage" type="button"
								class="btn btn-default btn-success">Submit</button>
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Cancel</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<hr />

			<!-- Footer -->
			<jsp:include page="/WEB-INF/jsp/fragments/m_footer.jsp"></jsp:include>

		</div>
	</div>
	<jsp:include page="/WEB-INF/jsp/fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>

	<spring:url value="/resources/assets/js/jquery.form.js" var="ajaxForm" />
	<script src="${ajaxForm}"></script>

	<spring:url value="/resources/app/lead-to-sales/stage-report.js" var="stageReportJs"></spring:url>
	<script type="text/javascript" src="${stageReportJs}"></script>
	 
	<spring:url value="/resources/assets/js/table2excel.js"
		var="table2excel"></spring:url>
	<script type="text/javascript" src="${table2excel}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<spring:url value="/resources/app/report-common-js-file.js"
		var="reportcommonjsfileJS"></spring:url>
	<script type="text/javascript" src="${reportcommonjsfileJS}"></script>
</body>
</html>