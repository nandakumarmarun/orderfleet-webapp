<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Feedback History</title>

<spring:url value="/resources/assets/css/bootstrap-multiselect.css"
	var="bootstrapMultiselectCss"></spring:url>
<link href="${bootstrapMultiselectCss}" rel="stylesheet">

<!-- jQuery UI-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">

<style type="text/css">
.error {
	color: red;
}
@media only screen and (max-width: 800px) {
	/* Force table to not be like tables anymore */
	#tblfeedbackHistory table, #tblfeedbackHistory thead, #tblfeedbackHistory tbody,
		#tblfeedbackHistory th, #tblfeedbackHistory td, #tblfeedbackHistory tr {
		display: block;
	}

	/* Hide table headers (but not display: none;, for accessibility) */
	#tblfeedbackHistory thead tr {
		position: absolute;
		top: -9999px;
		left: -9999px;
	}
	#tblfeedbackHistory tr {
		border: 1px solid #ccc;
	}
	#tblfeedbackHistory td {
		/* Behave  like a "row" */
		border: none;
		border-bottom: 1px solid #eee;
		position: relative;
		padding-left: 50%;
		white-space: normal;
		text-align: left;
	}
	#tblfeedbackHistory td:before {
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
	#tblfeedbackHistory td:before {
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
			<h2 id="pageHeader">Feedback History</h2>
			<div class="row col-xs-12"></div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<!-- Profile Info and Notifications -->
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-2">
								Employee <select id="dbEmployee" name="employeePid" class="form-control" >
									<option value="no">All Employee</option>
									<c:forEach items="${employees}" var="employee">
										<option value="${employee.pid}">${employee.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Feedback Group <select id="dbFeedbackGroup" class="form-control"
									onchange="FeedbackHistory.statusValues()">
									<c:forEach items="${feedbackGroups}" var="feedbackGroup">
										<option value="${feedbackGroup.pid}">${feedbackGroup.name}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-sm-2">
								Status <select id="dbStatus" class="form-control"><option
										value="no">All Status</option>
								</select>
							</div>
							<div class="col-sm-2">
								Date <select class="form-control" id="dbDateSearch"
									onchange="FeedbackHistory.showDatePicker()">
									<option value="TODAY">Today</option>
									<option value="YESTERDAY">Yesterday</option>
									<option value="WTD">WTD</option>
									<option value="MTD">MTD</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
				
							<div class="col-sm-2 hide custom_date1">
							<br/>
							<div class="input-group">
									<input type="text" class="form-control" id="txtFromDate"
										 placeholder="Select From Date"
										style="background-color: #fff;" readonly="readonly" />

									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
							<br/>
								<div class="input-group">
									<input  type="text"
										class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-1">
							<br/>
								<button type="button" class="btn btn-info"
									onclick="FeedbackHistory.filter()">Apply</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="table-wrapper">
						<div class="rt-scrollable">
			<table class="table table-bordered responsive" id="tblfeedbackHistory">
				<thead>
					<tr>
						<th>Employee</th>
						<th>Account</th>
						<th>Activity</th>
						<th>Date</th>
						<th colspan="2">Action</th>
					</tr>
				</thead>
				<tbody id="tBodyFeedbackHistory">
					<tr>
						<td colspan='5' align='center'>No data available</td>
					</tr>
				</tbody>
			</table>
			</div></div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			<spring:url value="/web/dynamic-documents" var="urlFeedback"></spring:url>
		</div>


		<!-- Model Container-->
		<div class="modal fade container" id="viewModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="viewModalLabel">Document Details</h4>
					</div>
					<div class="modal-body">
						<!-- error message -->
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						
						
							<dl class="dl-horizontal">
									<dt>
										<span>Document Number</span>
									</dt>
									<dd>
										<span id="lbl_documentNumber"></span>
									</dd>
									<hr />
									<dt>
										<span>User</span>
									</dt>
									<dd>
										<span id="lbl_user"></span>
									</dd>
									<hr />
									<dt>
										<span>Account</span>
									</dt>
									<dd>
										<span id="lbl_account"></span>
									</dd>
									<hr />
									<dt>
										<span>Activity</span>
									</dt>
									<dd>
										<span id="lbl_activity"></span>
									</dd>
									<hr />
									<dt>
										<span>Document</span>
									</dt>
									<dd>
										<span id="lbl_document"></span>
									</dd>
									<hr />
									<dt>
										<span>Date</span>
									</dt>
									<dd>
										<span id="lbl_documentDate"></span>
									</dd>
									<hr />
								</dl>
							
						<div id="divFeedbackDetails"
							style="overflow: auto; height: 300px;"></div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>
		</div>


		<!-- Model Container-->
		<div class="modal fade container" id="imagesModal">
			<!-- model Dialog -->
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="viewModalLabel">Document Images</h4>
					</div>
					<div class="modal-body">
						<!-- error message -->
						<div class="alert alert-danger alert-dismissible" role="alert"
							style="display: none;">
							<button type="button" class="close" onclick="$('.alert').hide();"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<p></p>
						</div>
						<div id="divFeedbackImages" style="overflow: auto; height: 500px;"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal-dialog -->
		</div>


	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

<spring:url value="/resources/assets/js/bootstrap-multiselect.js"
		var="bootstrapMultiselectJs"></spring:url>
	<script type="text/javascript" src="${bootstrapMultiselectJs}"></script>

	<spring:url value="/resources/assets/js/jquery-ui-1.11.4.js"
		var="jqueryUI"></spring:url>
	<script type="text/javascript" src="${jqueryUI}"></script>

	<spring:url value="/resources/app/feedback-history.js" var="feedbackHistoryJs"></spring:url>
	<script type="text/javascript" src="${feedbackHistoryJs}"></script>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>
</body>
</html>